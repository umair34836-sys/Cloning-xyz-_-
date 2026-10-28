#!/usr/bin/env python3
"""FireCraft Remastered HD — realistic 64x textures (Java 26.2 + Bedrock).

generate.py wali family pehchaan aur palette (sirf idea) use hoti hai; har material yahan
4x resolution par dobara banta hai: height map + roshni (normal shading), ambient
occlusion, specular chamak aur asli-jaisa detail (wood grain, bark, crystals, kankar...).

Usage:
  generate_hd.py java    <java-assets-dir>  <out-dir>
  generate_hd.py bedrock <bedrock-rp-dir>   <out-dir>
"""
import json, math, os, re, shutil, sys
import numpy as np
from PIL import Image

import generate as g

S = 4                 # 16x -> 64x
g.MATS["netherite"] = (104, 100, 106)  # neutral gehra metal (metal albedo >= ~110 rule ke baad gulaabi na lage)
ENT_S = 2             # mobs ki skins 2x (4x par pack 150MB+ ho jata tha)
LUM = g.LUM
shade, mix, has, Ctx = g.shade, g.mix, g.has, g.Ctx


# ------------------------------------------------------------------ core ----
def fbm(h, w, r, cell, octaves=4, cx=None):
    return g.vnoise(h, w, r, max(1, int(cell)), None if cx is None else max(1, int(cx)), octaves)


def nrm(v):
    return g.norm(v)


def ridged(v):
    return 1 - np.abs(2 * v - 1)


def shift(a, k, ax, wrap):
    if wrap:
        return np.roll(a, k, ax)
    pad = [(0, 0)] * a.ndim
    pad[ax] = (abs(k), abs(k))
    p = np.pad(a, pad, mode="edge")
    sl = [slice(None)] * a.ndim
    sl[ax] = slice(abs(k) - k, abs(k) - k + a.shape[ax])
    return p[tuple(sl)]


def blur(a, r=1, wrap=True):
    out = a.astype(float)
    for _ in range(max(0, int(r))):
        for ax in (0, 1):
            out = (shift(out, -1, ax, wrap) + out * 2 + shift(out, 1, ax, wrap)) / 4
    return out


LIGHT = np.array([-0.55, -0.65, 0.52]); LIGHT /= np.linalg.norm(LIGHT)
HALF = LIGHT + np.array([0, 0, 1.0]); HALF /= np.linalg.norm(HALF)


DIRT = np.array([62, 50, 40.0])


def clamp_albedo(alb, metal=False):
    """PBR rule (Substance pbr_channel_guide): non-metal albedo sRGB 30..240, metal >= ~128."""
    lum = alb @ LUM
    lo, hi = (110.0, 250.0) if metal else (30.0, 240.0)
    f = np.where(lum < lo, lo / np.maximum(lum, 1), np.where(lum > hi, hi / np.maximum(lum, 1), 1.0))
    return np.clip(alb * f[..., None], 0, 255)


def light(alb, hgt, k=2.5, spec=0.0, shin=24, amb=0.5, ao=1.6, wrap=True, mask=None,
          metal=False, dirt=0.35, edge=0.12, emissive=None):
    """3D artist layering: albedo clamp -> lambert (key upar-baayein) -> AO (max 35%) ->
    curvature edge highlight -> cavity dirt -> warm key / cool shadow -> specular
    (metal ka highlight base rang se tint, dielectric ka safed; dirt me chamak nahi)."""
    alb = clamp_albedo(alb, metal)
    gx = (shift(hgt, -1, 1, wrap) - shift(hgt, 1, 1, wrap)) / 2
    gy = (shift(hgt, -1, 0, wrap) - shift(hgt, 1, 0, wrap)) / 2
    nx, ny, nz = -gx * k, -gy * k, np.ones_like(hgt)
    ln = np.sqrt(nx * nx + ny * ny + nz * nz)
    nx, ny, nz = nx / ln, ny / ln, nz / ln
    diff = np.clip(nx * LIGHT[0] + ny * LIGHT[1] + nz * LIGHT[2], 0, 1)
    f = amb + (1 - amb) * diff / LIGHT[2]
    occ = np.clip((blur(hgt, 3, wrap) - hgt) * ao, 0, 0.35)
    curv = np.clip((hgt - blur(hgt, 1, wrap)) * 10, 0, 1)
    lit = f * (1 - occ) * (1 + edge * curv)
    col = alb * lit[..., None]
    if dirt:
        dm = np.clip(occ * 2.2, 0, 1)[..., None] * dirt
        col = col * (1 - dm) + (DIRT * lit[..., None]) * dm
    t = np.clip(lit - 1, -0.5, 0.5)[..., None]
    col = col + t * np.array([10.0, 4.0, -8.0])          # roshni garam, saaya thanda
    if spec:
        s = np.clip(nx * HALF[0] + ny * HALF[1] + nz * HALF[2], 0, 1) ** shin * (1 - np.clip(occ * 3, 0, 1))
        tint = alb / 255 if metal else np.ones_like(alb)
        col = col + (s * spec * 255)[..., None] * tint
    if emissive is not None:
        col = np.where(emissive[..., None], alb, col)
    return np.clip(col, 18, 250)


def out_rgba(rgb, alpha=255):
    o = np.zeros(rgb.shape[:2] + (4,)); o[..., :3] = rgb; o[..., 3] = alpha
    return o


def grid(H, W):
    return np.mgrid[0:H, 0:W].astype(float) + 0.5


def wrapd(a, b, n):
    d = np.abs(a - b)
    return np.minimum(d, n - d)


def dist_inside(m, maxd=12):
    """Mask ke andar kinare se faasla (erosion count) -> dome height ke liye."""
    d = np.zeros(m.shape)
    cur = m.copy()
    for i in range(maxd):
        d += cur
        cur = g.erode(cur)
        if not cur.any():
            break
    return d


def voronoi(H, W, pts):
    yy, xx = grid(H, W)
    d = np.stack([np.hypot(wrapd(yy, py, H), wrapd(xx, px, W)) for py, px in pts])
    o = np.argsort(d, 0)
    d1 = np.take_along_axis(d, o[:1], 0)[0]
    d2 = np.take_along_axis(d, o[1:2], 0)[0]
    return d1, d2, o[0]


def tone_col(col, f):
    return shade(col, np.clip(f, 0.2, 2.0))


# ------------------------------------------------------------- materials ----
def hd_stone(c, col=None, cracks=True, smooth=False):
    r = c.rng(101); H, W = c.H * S, c.W * S
    p = c.pal
    col = p.least_sat_main() if col is None else col
    n1, n2, n3 = fbm(H, W, r, H // 2, 6), fbm(H, W, r, H // 4, 4), fbm(H, W, r, 4, 2)
    hgt = 0.55 * n1 + 0.3 * n2 + 0.15 * n3
    tone = 0.82 + 0.32 * nrm(0.55 * n2 + 0.45 * n3)
    if smooth:
        tone = 0.94 + 0.1 * nrm(n2); hgt = 0.2 * hgt
    alb = tone_col(col, tone)
    if np.abs(p.second - col).sum() > 25 and not smooth:
        m = np.clip((nrm(fbm(H, W, r, H // 2, 3)) - 0.6) * 1.6, 0, 0.45)[..., None]
        alb = alb * (1 - m) + tone_col(p.second, tone) * m
    fl = r.random((H, W))
    alb[fl > 0.988] = shade(col, 1.3); alb[fl < 0.01] = shade(col, 0.55)
    if cracks:
        rd = ridged(fbm(H, W, r, H // 2, 3))
        cr = (rd > 0.988) & (fbm(H, W, r, H // 2, 2) > 0.45)
        hgt = np.where(cr, hgt - 0.4, hgt); alb[cr] *= 0.5
    return out_rgba(light(alb, hgt, k=3.2 if not smooth else 2))


def hd_layered(c, col=None):
    r = c.rng(102); H, W = c.H * S, c.W * S
    col = c.pal.least_sat_main() if col is None else col
    a = fbm(H, W, r, H // 8, 4, W // 2); b = fbm(H, W, r, 2, 2, 16)
    hgt = 0.65 * a + 0.35 * b
    band = np.sin((grid(H, W)[0] / H) * 2 * math.pi * 5 + 3 * a)
    alb = tone_col(col, 0.8 + 0.18 * nrm(a) + 0.08 * band)
    cr = ridged(fbm(H, W, r, H // 8, 3, W // 2)) > 0.96
    alb[cr] *= 0.55; hgt = np.where(cr, hgt - 0.35, hgt)
    return out_rgba(light(alb, hgt, k=3))


def hd_cobble(c):
    r = c.rng(103); H, W = c.H * S, c.W * S
    p = c.pal
    col = p.least_sat_main()
    if has(c.base, "deepslate"):
        col = mix(col, (70, 70, 78), 0.4)
    n = max(8, int(c.H * c.W / 256 * 11))
    pts = r.random((n, 2)) * [H, W]
    d1, d2, cell = voronoi(H, W, pts)
    edge = d2 - d1
    det = fbm(H, W, r, 8, 3)
    hgt = np.sqrt(np.clip(edge / (3.0 * S), 0, 1)) * 0.85 + 0.15 * det
    tones = r.uniform(0.78, 1.15, n)
    alb = tone_col(col, tones[cell] * (0.85 + 0.3 * fbm(H, W, r, 4, 3)))
    mort = edge < 0.55 * S
    alb[mort] = tone_col(col, 0.42 + 0.25 * det)[mort]
    hgt = np.where(mort, 0.0, hgt)
    if has(c.base, "mossy"):
        mm = (nrm(fbm(H, W, r, H // 3, 4)) > 0.55) & ((hgt < 0.45) | (r.random((H, W)) < 0.3))
        alb[mm] = tone_col((86, 118, 50), 0.75 + 0.5 * fbm(H, W, r, 2, 2))[mm]
        hgt = np.where(mm, hgt + 0.08, hgt)
    return out_rgba(light(alb, hgt, k=3.5))


GEM_SHIFT = {"coal": None}


def crystals(c, base, gem, H, W, r, count, matte=False):
    """Pathar me jade hue pehloo-daar crystals (har facet ki apni roshni)."""
    img = base.copy()
    yy, xx = grid(H, W)
    for _ in range(count):
        cy, cx = r.random() * H, r.random() * W
        R = r.uniform(1.4, 2.4) * S * (c.H / 16) ** 0
        nv = int(r.integers(5, 8))
        radii = R * r.uniform(0.65, 1.0, nv)
        tilts = r.uniform(-1, 1, (nv, 2)) * (0.3 if matte else 0.9)
        dy, dx = yy - cy, xx - cx
        dy = (dy + H / 2) % H - H / 2; dx = (dx + W / 2) % W - W / 2
        ang = (np.arctan2(dy, dx) + 2 * math.pi) % (2 * math.pi)
        sec = (ang / (2 * math.pi) * nv).astype(int) % nv
        t = ang / (2 * math.pi) * nv - np.floor(ang / (2 * math.pi) * nv)
        rad = radii[sec] * (1 - t) + radii[(sec + 1) % nv] * t
        rho = np.hypot(dy, dx)
        inside = rho < rad
        rim = (rho < rad + 0.7 * S) & ~inside
        img[rim] *= 0.62
        tx, ty = tilts[sec, 0], tilts[sec, 1]
        ln = np.sqrt(tx * tx + ty * ty + 1)
        dif = np.clip((tx * LIGHT[0] + ty * LIGHT[1] + LIGHT[2]) / ln, 0, 1)
        f = 0.45 + 0.85 * dif / LIGHT[2] * 0.75
        sp = np.clip((tx * HALF[0] + ty * HALF[1] + HALF[2]) / ln, 0, 1) ** (6 if matte else 40)
        core = 1 - np.clip(rho / np.maximum(rad, 1e-6), 0, 1)
        colr = tone_col(gem, f * (0.85 + 0.3 * core))[..., :3] + (sp * (40 if matte else 230))[..., None]
        img[inside] = np.clip(colr, 0, 255)[inside]
    return img


def hd_ore(c):
    p, r = c.pal, c.rng(104)
    H, W = c.H * S, c.W * S
    if has(c.base, "deepslate"):
        base = hd_layered(c, mix(p.least_sat_main(), (70, 70, 78), 0.5))
    elif has(c.base, "nether", "quartz"):
        base = hd_stone(c, mix(p.least_sat_main(), (110, 45, 45), 0.4))
    else:
        base = hd_stone(c, p.least_sat_main())
    coal = has(c.base, "coal")
    gem = shade(p.dark, 0.7) if coal else (p.light if has(c.base, "quartz") else p.accent)
    n = max(4, int(5 * c.H * c.W / 256))
    rgb = crystals(c, base[..., :3], gem, H, W, r, n, matte=coal)
    return out_rgba(rgb)


def hd_planks(c):
    r = c.rng(105); H, W = c.H * S, c.W * S
    col = c.pal.main
    yy, xx = grid(H, W)
    BW = max(4 * S, W // 2)
    warp = fbm(H, W, r, H // 2, 4, 4)
    streak = fbm(H, W, r, 16, 3, 1)
    alb = np.zeros((H, W, 3)); hgt = np.zeros((H, W))
    for b in range(W // BW):
        x0 = b * BW
        sel = (xx >= x0) & (xx < x0 + BW)
        u = xx - x0
        ky, kx = r.uniform(0.2, 0.8) * H, x0 + r.uniform(0.3, 0.7) * BW
        dk = np.hypot((yy - ky) * 0.55, xx - kx)
        knot = np.exp(-(dk / (1.6 * S)) ** 2)
        grain = np.sin(2 * math.pi * (u + 7 * warp + 4 * knot * np.sign(xx - kx)) / (2.4 * S))
        tone = r.uniform(0.9, 1.1) * (0.9 + 0.07 * grain + 0.14 * (streak - 0.5))
        tone = tone * (1 - 0.35 * np.clip(knot * (0.6 + 0.4 * np.sin(dk * 2.2)), 0, 1))
        alb[sel] = tone_col(col, tone)[sel]
        hgt[sel] = (0.55 + 0.05 * grain + 0.1 * streak)[sel]
        edge = np.minimum(u, BW - 1 - u)
        hgt[sel] *= np.clip(edge / (1.2 * S), 0.3, 1)[sel]
        gap = sel & (u >= BW - S * 0.6)
        alb[gap] = shade(col, 0.32); hgt[gap] = 0
        j = r.uniform(0.25, 0.75) * H
        jm = sel & (np.abs(yy - j) < S * 0.45) & ~gap
        alb[jm] = shade(col, 0.42); hgt[jm] = 0
        for nx, ny in ((x0 + 1.6 * S, j - 1.3 * S), (x0 + BW - 2.6 * S, j + 1.3 * S),
                       (x0 + 1.6 * S, j + 1.3 * S), (x0 + BW - 2.6 * S, j - 1.3 * S)):
            dn = np.hypot(wrapd(yy, ny % H, H), xx - nx)
            nm = dn < 0.75 * S
            alb[nm] = (92, 88, 84); hgt[nm] = 0.75 + 0.25 * (1 - dn[nm] / (0.75 * S))
    rgb = light(alb, hgt, k=2.6, spec=0.08, shin=16)
    return out_rgba(rgb)


def hd_bark(c):
    r = c.rng(106); H, W = c.H * S, c.W * S
    p = c.pal
    col = p.main
    if has(c.base, "stripped"):
        warp = fbm(H, W, r, H // 2, 3, 8)
        xx = grid(H, W)[1]
        grain = np.sin(2 * math.pi * (xx + 6 * warp) / (2.0 * S))
        alb = tone_col(col, 0.93 + 0.06 * grain + 0.12 * (fbm(H, W, r, 16, 3, 1) - 0.5))
        return out_rgba(light(alb, 0.5 + 0.05 * grain, k=2))
    rid = ridged(fbm(H, W, r, H // 2, 5, W // 6))
    fine = fbm(H, W, r, 8, 3, 2)
    hgt = 0.65 * rid + 0.35 * fine
    alb = tone_col(col, 0.6 + 0.6 * hgt)
    if (col @ LUM) > 165:  # birch jaisi safed chaal: kaale lenticels
        for _ in range(int(10 * c.W / 16)):
            y, x = r.random() * H, r.random() * W
            L = r.uniform(1.5, 4.5) * S
            yy, xx = grid(H, W)
            m = (wrapd(yy, y, H) < 0.5 * S) & (wrapd(xx, x, W) < L / 2)
            alb[m] = tone_col(p.dark, 0.9 + 0.2 * fine)[m]; hgt = np.where(m, hgt - 0.3, hgt)
        alb = mix(alb, tone_col(col, 1.05), 0.35)
    return out_rgba(light(alb, hgt, k=3.4))


def hd_log_top(c):
    r = c.rng(107); H, W = c.H * S, c.W * S
    p = c.pal
    wood = p.light if not has(c.base, "stem", "hyphae") else p.second
    bark = p.dark
    yy, xx = grid(H, W)
    cy, cx = H / 2 + r.uniform(-1, 1) * S, W / 2 + r.uniform(-1, 1) * S
    d = np.hypot(yy - cy, xx - cx) + 2.2 * S * (fbm(H, W, r, H // 4, 3) - 0.5)
    ring = 0.5 + 0.5 * np.sin(d * 2 * math.pi / (1.6 * S))
    alb = tone_col(wood, 0.82 + 0.16 * ring + 0.06 * (fbm(H, W, r, 4, 2) - 0.5))
    hgt = 0.5 + 0.08 * ring
    ang = np.arctan2(yy - cy, xx - cx)
    for a0 in r.uniform(-math.pi, math.pi, 2):
        crack = (np.abs(((ang - a0 + math.pi) % (2 * math.pi)) - math.pi) < 0.03) & (d < W * 0.38)
        alb[crack] *= 0.45; hgt = np.where(crack, 0.2, hgt)
    pith = d < 0.8 * S
    alb[pith] = shade(wood, 0.65)
    edge = np.minimum.reduce([yy, xx, H - yy, W - xx])
    if not has(c.base, "stripped"):
        bm = edge < 1.6 * S
        bt = ridged(fbm(H, W, r, 8, 3))
        alb[bm] = tone_col(bark, 0.7 + 0.5 * bt)[bm]
        hgt = np.where(bm, 0.7 + 0.2 * bt, hgt)
        hgt = np.where(np.abs(edge - 1.6 * S) < 0.5 * S, 0.3, hgt)
    return out_rgba(light(alb, hgt, k=2.6))


def paint_leaf(alb, hgt, alpha, cy, cx, L, Wd, ang, colr, H, W, wrap=True):
    rad = int(L + 2)
    ys = np.arange(int(cy) - rad, int(cy) + rad + 1)
    xs = np.arange(int(cx) - rad, int(cx) + rad + 1)
    YY, XX = np.meshgrid(ys, xs, indexing="ij")
    dy, dx = YY + 0.5 - cy, XX + 0.5 - cx
    u = dx * math.cos(ang) + dy * math.sin(ang)
    v = -dx * math.sin(ang) + dy * math.cos(ang)
    q = (u / L) ** 2 + (v / (Wd * (1 - 0.35 * (u / L)))) ** 2
    inside = q < 1
    if wrap:
        YY %= H; XX %= W
    else:
        ok = (YY >= 0) & (YY < H) & (XX >= 0) & (XX < W)
        inside &= ok
        YY = np.clip(YY, 0, H - 1); XX = np.clip(XX, 0, W - 1)
    hh = np.sqrt(np.clip(1 - q, 0, 1))
    f = 0.85 + 0.25 * (u / L)
    f = np.where(np.abs(v) < 0.12 * Wd + 0.3, f * 0.82, f)   # beech ki nas (midrib)
    alb[YY[inside], XX[inside]] = tone_col(colr, f)[inside]
    hgt[YY[inside], XX[inside]] = hh[inside] + 0.2
    alpha[YY[inside], XX[inside]] = 255


def hd_leaves(c):
    r = c.rng(108); H, W = c.H * S, c.W * S
    col = c.pal.main
    alb = np.zeros((H, W, 3)); hgt = np.zeros((H, W)); alpha = np.zeros((H, W))
    opaque = bool((c.ref[..., 3] > 0).all())
    if opaque:
        alb[:] = shade(col, 0.4); alpha[:] = 255
    n = int(H * W / (S * S) * 0.42)
    for _ in range(n):
        L = r.uniform(1.8, 3.2) * S
        paint_leaf(alb, hgt, alpha, r.random() * H, r.random() * W, L, L * r.uniform(0.38, 0.5),
                   r.uniform(0, 2 * math.pi), shade(col, r.uniform(0.72, 1.12)), H, W)
    rgb = light(alb, hgt, k=2.2, amb=0.45)
    return out_rgba(rgb, alpha)


BIG_BRICK, DARK_MORTAR = g.BIG_BRICK, g.DARK_MORTAR


def hd_bricks(c):
    r = c.rng(109); H, W = c.H * S, c.W * S
    col = c.pal.main
    big = has(c.base, *BIG_BRICK)
    rh, bl = (8, 16) if big else (4, 8)
    RH, BL = min(rh, c.H) * S, min(bl, c.W) * S
    m = int(S * 0.9)
    mortar = shade(col, 0.55) if has(c.base, *DARK_MORTAR) else mix(col, (196, 190, 180), 0.75)
    yy, xx = np.mgrid[0:H, 0:W]
    row = yy // RH
    off = (row % 2) * (BL // 2)
    lx, ly = (xx + off) % BL, yy % RH
    mor = (ly >= RH - m) | (lx >= BL - m)
    de = np.minimum.reduce([ly, RH - m - 1 - ly, lx, BL - m - 1 - lx]).astype(float)
    bid = row * 97 + (xx + off) // BL
    rr = np.random.default_rng(c.seed + 7)
    rnd = rr.uniform(0.9, 1.1, int(bid.max()) + 1)
    hue = rr.uniform(-0.05, 0.05, (int(bid.max()) + 1, 3)) * np.array([1, 0, -1])
    det = fbm(H, W, r, 8, 4)
    alb = tone_col(col, rnd[bid] * (0.86 + 0.26 * det)) * (1 + hue[bid])
    pits = r.random((H, W)) < 0.012
    alb[pits] *= 0.65
    hgt = np.clip(de / (1.4 * S), 0, 1) ** 0.6 * 0.8 + 0.2 * det
    mg = fbm(H, W, r, 2, 2)
    alb[mor] = tone_col(mortar, 0.8 + 0.35 * mg)[mor]
    hgt = np.where(mor, 0.05 * mg, hgt)
    if has(c.base, "cracked"):
        cr = ridged(fbm(H, W, r, H // 2, 4)) > 0.965
        alb[cr] *= 0.45; hgt = np.where(cr, 0, hgt)
    if has(c.base, "mossy"):
        mm = (nrm(fbm(H, W, r, H // 3, 4)) > 0.58) & ((hgt < 0.5) | (r.random((H, W)) < 0.35))
        alb[mm] = tone_col((86, 118, 50), 0.75 + 0.5 * fbm(H, W, r, 2, 2))[mm]
    return out_rgba(light(alb, hgt, k=3.4))


def hd_granular(c, col=None, H=None, W=None):
    r = c.rng(110)
    H, W = (H or c.H * S), (W or c.W * S)
    col = c.pal.main if col is None else col
    b = c.base
    base = fbm(H, W, r, H // 4, 4)
    fine = fbm(H, W, r, 1, 1)
    alb = tone_col(col, 0.86 + 0.18 * base + 0.14 * (fine - 0.5))
    hgt = 0.25 * base + 0.15 * fine
    gravel, sand = has(b, "gravel"), has(b, "sand", "snow", "concrete_powder") and not has(b, "soul")
    if sand:
        yy, xx = grid(H, W)
        hgt = hgt + 0.12 * np.sin((yy + 3 * S * fbm(H, W, r, H // 2, 2)) * 2 * math.pi / (3 * S))
        sp = r.random((H, W)) > 0.985
        alb[sp] = shade(col, 1.35)
    else:
        n = int((70 if gravel else 34) * c.H * c.W / 256)
        yy, xx = grid(H, W)
        for _ in range(n):
            cy, cx = r.random() * H, r.random() * W
            ry = r.uniform(0.9, 2.4 if gravel else 1.3) * S * 0.6
            rx = ry * r.uniform(0.8, 1.5)
            q = (wrapd(yy, cy, H) / ry) ** 2 + (wrapd(xx, cx, W) / rx) ** 2
            m = q < 1
            pc = mix(col, (128, 124, 118), 0.0 if gravel else 0.45)
            alb[m] = tone_col(pc, r.uniform(0.7, 1.25) * (0.9 + 0.2 * fine))[m]
            hgt = np.where(m, np.maximum(hgt, 0.35 + 0.65 * np.sqrt(np.clip(1 - q, 0, 1))), hgt)
    return out_rgba(light(alb, hgt, k=3.0 if not sand else 1.8))


def hd_capped_side(c):
    r = c.rng(111); H, W = c.H * S, c.W * S
    ref = c.ref[..., :3].astype(float); a = c.ref[..., 3] > 0
    top = ref[:3][a[:3]].mean(0) if a[:3].any() else c.pal.green
    bot = ref[-6:][a[-6:]].mean(0) if a[-6:].any() else c.pal.main
    overlay = has(c.base, "overlay")
    if overlay:
        res = np.zeros((H, W, 4))
    else:
        res = hd_granular(c, bot)
    yy, xx = grid(H, W)
    L = (2.2 + 2.0 * fbm(1, W, r, 1, 1, 6)[0] + 1.4 * (r.random(W) ** 3)) * S
    L = L + 1.2 * S * (r.random(W) < 0.06)
    hang = yy < L[None, :]
    strand = fbm(H, W, r, 4, 2, 1)
    t = yy / L[None, :]
    alb = tone_col(top, (1.08 - 0.3 * t) * (0.85 + 0.3 * strand))
    hgt = 0.7 + 0.2 * strand - 0.3 * t
    lit = light(alb, hgt, k=2.2, wrap=True)
    res[hang, :3] = lit[hang]; res[hang, 3] = 255
    shadow = (~hang) & (yy < L[None, :] + 1.2 * S)
    if not overlay:
        res[shadow, :3] *= 0.65
    return res


def hd_organic(c):
    r = c.rng(112); H, W = c.H * S, c.W * S
    col = c.pal.main
    alb = tone_col(col, 0.82 + 0.25 * fbm(H, W, r, H // 4, 4))
    hgt = 0.3 * fbm(H, W, r, 8, 3)
    for _ in range(int(H * W / (S * S) * 1.6)):
        x, y = r.random() * W, r.random() * H
        ang = r.uniform(-0.9, 0.9) - math.pi / 2
        L = r.uniform(1.0, 2.6) * S
        f = r.uniform(0.75, 1.25)
        for i in range(int(L)):
            px, py = int(x + math.cos(ang) * i) % W, int(y + math.sin(ang) * i) % H
            alb[py, px] = shade(col, f * (0.9 + 0.2 * i / L))
            hgt[py, px] = 0.5 + 0.5 * i / L
    return out_rgba(light(alb, hgt, k=2.4))


def hd_wool(c):
    r = c.rng(113); H, W = c.H * S, c.W * S
    col = c.pal.main
    yy, xx = grid(H, W)
    CW, RH = 2 * S, 2 * S
    colm = (xx // CW).astype(int)
    s = np.where(colm % 2 == 0, 1, -1)
    ph = (xx / CW + s * yy / RH) % 1
    st = np.sin(ph * math.pi)
    fib = fbm(H, W, r, 2, 2, 1)
    hgt = 0.6 * st + 0.25 * fib + 0.15 * fbm(H, W, r, 8, 3)
    alb = tone_col(col, 0.86 + 0.16 * st + 0.1 * (fib - 0.5))
    return out_rgba(light(alb, hgt, k=2.2, amb=0.55))


def hd_concrete(c):
    r = c.rng(114); H, W = c.H * S, c.W * S
    col = c.pal.main
    n = fbm(H, W, r, H // 2, 4)
    alb = tone_col(col, 0.95 + 0.08 * n)
    pores = r.random((H, W)) < 0.006
    alb[pores] *= 0.75
    return out_rgba(light(alb, 0.1 * n - 0.2 * pores, k=2))


def hd_terracotta(c):
    r = c.rng(115); H, W = c.H * S, c.W * S
    col = c.pal.main
    st = fbm(H, W, r, 4, 3, W)
    n = fbm(H, W, r, 8, 3)
    alb = tone_col(col, 0.88 + 0.12 * st + 0.08 * n)
    pores = r.random((H, W)) < 0.008
    alb[pores] *= 0.8
    return out_rgba(light(alb, 0.2 * n + 0.1 * st - 0.2 * pores, k=2.4))


def hd_glazed(c):
    r = c.rng(116); H, W = c.H * S, c.W * S
    low = g.g_glazed(c)[..., :3]
    up = low.repeat(S, 0).repeat(S, 1)
    alb = blur(up, 1)
    lumh = blur((alb @ LUM) / 255, 2)
    d1, d2, _ = voronoi(H, W, r.random((int(9 * c.H * c.W / 256), 2)) * [H, W])
    crack = (d2 - d1) < 0.35
    rgb = light(alb, 0.3 * lumh, k=2, spec=0.55, shin=40, amb=0.6)
    rgb[crack] *= 0.86
    return out_rgba(rgb)


def hd_glass(c):
    H, W = c.H * S, c.W * S
    p = c.pal
    a = c.ref[..., 3]
    semi = ((a > 0) & (a < 250)).mean() > 0.3
    inner_a = float(a[(a > 0) & (a < 250)].mean()) if semi else 0
    col = p.main
    frame = mix(p.light, col, 0.4)
    yy, xx = grid(H, W)
    edge = np.minimum.reduce([yy, xx, H - yy, W - xx])
    out = np.zeros((H, W, 4))
    out[..., :3] = col; out[..., 3] = inner_a
    fm = edge < 1.1 * S
    hgt = np.clip(edge / (1.1 * S), 0, 1)
    fr = light(np.broadcast_to(frame, (H, W, 3)).copy(), np.where(fm, np.sin(hgt * math.pi), 0), k=3, spec=0.5, shin=30)
    out[fm, :3] = fr[fm]; out[fm, 3] = 255
    corner = (np.minimum(yy, H - yy) < S * 0.6) & (np.minimum(xx, W - xx) < S * 0.6)
    out[corner, 3] = 0
    t = (xx + yy) / (H + W)
    streak = ((np.abs(t - 0.28) < 0.012) | (np.abs(t - 0.34) < 0.006)) & ~fm
    out[streak, :3] = shade(frame, 1.35); out[streak, 3] = max(inner_a, 0) + 90 if semi else 120
    if not semi:
        out[streak, 3] = 255 if False else 0
        small = (np.abs(t - 0.3) < 0.02) & (np.minimum(yy, xx) < W * 0.4) & ~fm
        out[small, :3] = shade(frame, 1.3); out[small, 3] = 255
    return out


def hd_metal(c):
    r = c.rng(117); H, W = c.H * S, c.W * S
    col = c.pal.main
    yy, xx = grid(H, W)
    PH, PW = max(4 * S, H // 2), max(4 * S, W // 2)
    py, px = yy % PH, xx % PW
    brushed = fbm(H, W, r, 1, 2, W // 2)
    de = np.minimum.reduce([py, PH - py, px, PW - px])
    hgt = np.clip(de / (1.2 * S), 0, 1) * 0.8 + 0.05 * brushed
    alb = tone_col(col, 0.9 + 0.16 * brushed + 0.06 * fbm(H, W, r, H // 2, 3))
    for ry, rx in ((1.6, 1.6), (1.6, -1.6), (-1.6, 1.6), (-1.6, -1.6)):
        cy = np.where(ry > 0, ry * S, PH + ry * S); cx = np.where(rx > 0, rx * S, PW + rx * S)
        d = np.hypot(py - cy, px - cx)
        m = d < 0.8 * S
        hgt = np.where(m, 0.8 + 0.6 * np.sqrt(np.clip(1 - (d / (0.8 * S)) ** 2, 0, 1)), hgt)
        alb[m] = shade(col, 0.82)
    shiny = has(c.base, "diamond", "emerald", "gold", "lapis", "amethyst", "redstone", "quartz", "iron", "copper", "netherite")
    return out_rgba(light(alb, hgt, k=3, spec=0.75 if shiny else 0.3, shin=40 if shiny else 18, metal=True, dirt=0.2))


def hd_fluid(c):
    r = c.rng(118); H, W = c.H * S, c.W * S
    off = int(c.t * (2 * H) / max(1, c.nf))
    big = fbm(2 * H, W, r, H // 2, 4)
    warp = np.roll(big, off, 0)[:H]
    big2 = fbm(2 * H, W, r, H // 4, 3)
    w2 = np.roll(big2, -off // 2, 0)[:H]
    if has(c.base, "lava", "magma"):
        cols = c.pal.sorted
        yy, xx = grid(H, W)
        pts = np.random.default_rng(c.seed).random((10, 2)) * [2 * H, W]
        d1, d2, _ = voronoi(2 * H, W, pts)
        e = np.roll(d2 - d1, off, 0)[:H] + 2 * S * (warp - 0.5)
        glow = np.clip(1 - e / (2.2 * S), 0, 1) ** 1.5
        v = np.clip(0.25 + 0.75 * glow + 0.2 * (w2 - 0.5), 0, 0.999)
        i = v * (len(cols) - 1)
        lo = np.floor(i).astype(int); t = (i - lo)[..., None]
        rgb = cols[lo] * (1 - t) + cols[np.minimum(lo + 1, len(cols) - 1)] * t
        return out_rgba(np.clip(rgb * (0.85 + 0.3 * glow[..., None]), 0, 255))
    caust = ridged(nrm(0.6 * warp + 0.4 * w2)) ** 6
    rgb = tone_col(c.pal.main, 0.82 + 0.18 * warp + 0.45 * caust)
    return out_rgba(rgb)


def hd_flame(c):
    r = c.rng(119); H, W = c.H * S, c.W * S
    off = int(c.t * (2 * H) / max(1, c.nf))
    big = fbm(2 * H, W, r, H // 4, 4, W // 6)
    v = np.roll(big, -off, 0)[:H]
    yy = grid(H, W)[0] / H
    heat = v * 0.7 + yy * 0.85 - 0.42
    cols = c.pal.sorted
    k = np.clip((heat - 0.22) / 0.75, 0, 0.999) * (len(cols) - 1)
    lo = np.floor(k).astype(int); t = (k - lo)[..., None]
    rgb = cols[lo] * (1 - t) + cols[np.minimum(lo + 1, len(cols) - 1)] * t
    return out_rgba(rgb, np.where(heat > 0.22, 255, 0))


def hd_finish(c, low):
    """Baqi apne low-res designs (panel, bookshelf, TNT...) ko HD karo: smooth upscale +
    asli-jaisa micro detail + embossed roshni."""
    r = c.rng(120)
    H, W = low.shape[0] * S, low.shape[1] * S
    up = low.repeat(S, 0).repeat(S, 1).astype(float)
    a = up[..., 3]
    rgb = blur(up[..., :3], 1, wrap=True) * 0.5 + up[..., :3] * 0.5
    det = fbm(H, W, r, 4, 3)
    rgb = rgb * (0.92 + 0.16 * det)[..., None]
    hgt = blur((up[..., :3] @ LUM) / 255, 2) * 0.8 + 0.2 * det
    rgb = light(rgb, hgt, k=2.4)
    return out_rgba(rgb, a)


def hd_masked(c, s=None, soft=None, fur=0.0):
    """Functional shapes (rails, doors, mob UV): mask exact (nearest), rang smooth,
    detail + kinare ki roshni apni."""
    s = s or S
    r = c.rng(121)
    low = g.g_masked(c, coarse=4, outline=False)
    H, W = low.shape[0] * s, low.shape[1] * s
    m = c.ref[..., 3].repeat(s, 0).repeat(s, 1)
    mk = m > 0
    col = low[..., :3].repeat(s, 0).repeat(s, 1)
    sm = blur(col * mk[..., None], soft or s // 2, wrap=False)
    wt = blur(mk.astype(float), soft or s // 2, wrap=False)[..., None]
    col = np.where(wt > 0.05, sm / np.maximum(wt, 1e-6), col)
    det = fbm(H, W, r, max(2, s), 3)
    fine = fbm(H, W, r, 1, 1)
    strands = fbm(H, W, r, max(2, s // 2), 2, 1) if fur else 0
    col = col * (0.9 + 0.12 * det + 0.08 * (fine - 0.5) + fur * (strands - 0.5))[..., None]
    d = dist_inside(mk, maxd=s)
    hgt = np.clip(d / s, 0, 1) * 0.6 + 0.25 * det + 0.15 * fine + fur * strands
    rgb = light(col, hgt, k=2.0, wrap=False)
    out = np.zeros((H, W, 4)); out[..., :3] = rgb; out[..., 3] = m
    return out


# ---------------------------------------------------------------- plants ----
def capsule(alb, hgt, alpha, pts, width, colr, H, W, tone=None):
    yy, xx = grid(H, W)
    for (x0, y0), (x1, y1), w in zip(pts[:-1], pts[1:], np.linspace(width[0], width[1], len(pts) - 1)):
        dx, dy = x1 - x0, y1 - y0
        L2 = dx * dx + dy * dy + 1e-9
        t = np.clip(((xx - x0) * dx + (yy - y0) * dy) / L2, 0, 1)
        d = np.hypot(xx - (x0 + t * dx), yy - (y0 + t * dy))
        m = d < w
        f = 1.0 if tone is None else tone
        alb[m] = tone_col(colr, f * (0.85 + 0.25 * (1 - d / w)))[m]
        hgt[m] = np.maximum(hgt[m], np.sqrt(np.clip(1 - (d / w) ** 2, 0, 1))[m])
        alpha[m] = 255


def petal(alb, hgt, alpha, cy, cx, L, Wd, ang, colr, H, W):
    paint_leaf(alb, hgt, alpha, cy, cx, L, Wd, ang, colr, H, W, wrap=False)


def hd_plant(c):
    r = c.rng(122); H, W = c.H * S, c.W * S
    p = c.pal
    alb = np.zeros((H, W, 3)); hgt = np.zeros((H, W)); alpha = np.zeros((H, W))
    green = p.main if p.grey else p.green
    bloom = p.accent if np.abs(p.accent - green).sum() > 70 else None
    b = c.base
    st = g.stage_of(b)
    u = S  # 1 purana pixel
    if has(b, "mushroom", "fungus") and not has(b, "block", "stem"):
        cap = bloom if bloom is not None else p.main
        cx = W / 2
        capsule(alb, hgt, alpha, [(cx, H - 2 * u), (cx, H / 2)], (1.3 * u, 1.0 * u), p.light, H, W)
        yy, xx = grid(H, W)
        q = ((xx - cx) / (4.5 * u)) ** 2 + ((yy - (H / 2 - 0.5 * u)) / (3.2 * u)) ** 2
        m = (q < 1) & (yy < H / 2 + 0.6 * u)
        alb[m] = tone_col(cap, 0.8 + 0.4 * (1 - q))[m]; hgt[m] = np.sqrt(np.clip(1 - q, 0, 1))[m] + 0.3; alpha[m] = 255
        for _ in range(4):
            sy, sx = H / 2 - r.uniform(0.5, 2.5) * u, cx + r.uniform(-3, 3) * u
            sm = m & (np.hypot(yy - sy, xx - sx) < 0.6 * u)
            alb[sm] = (238, 236, 228)
    elif st is not None or has(b, "wheat", "carrots", "potatoes", "beetroots", "crop"):
        mx = {"wheat": 7, "carrots": 3, "potatoes": 3, "beetroots": 3}.get(b.split("_")[0], 7)
        s = min(1.0, (st if st is not None else mx) / max(1, mx))
        hgt_px = (3 + s * (c.H - 5)) * u
        for x0 in np.arange(1.5, c.W, 3.5) * u:
            lean = r.uniform(-1, 1) * u
            top = (x0 + lean, H - hgt_px)
            capsule(alb, hgt, alpha, [(x0, H), ((x0 + top[0]) / 2, H - hgt_px / 2), top], (0.6 * u, 0.35 * u), green, H, W)
            for k in range(2):
                yk = H - hgt_px * r.uniform(0.25, 0.7)
                petal(alb, hgt, alpha, yk - 0.8 * u, x0 + (1 if k else -1) * 1.4 * u, 1.8 * u, 0.5 * u,
                      (-0.6 if k else math.pi + 0.6), green, H, W)
            grain = bloom if bloom is not None else (p.light if not p.grey else None)
            if s >= 0.99 and grain is not None:
                for k in range(4):
                    petal(alb, hgt, alpha, top[1] + k * 0.9 * u, top[0] + (0.45 if k % 2 else -0.45) * u,
                          0.75 * u, 0.42 * u, math.pi / 2, shade(grain, 1.05 - 0.05 * k), H, W)
    elif has(b, "grass", "fern", "bush", "roots", "sprout", "seagrass", "kelp", "vine", "litter", "moss", "reeds", "cane"):
        n = 9 if not has(b, "fern") else 5
        for k in range(n):
            x = (0.8 + k * (c.W - 1.6) / max(1, n - 1)) * u + r.uniform(-0.5, 0.5) * u
            hh = r.uniform(0.45, 0.95) * H
            bend = r.uniform(-3, 3) * u
            pts = [(x, H), (x + bend * 0.3, H - hh * 0.5), (x + bend, H - hh)]
            capsule(alb, hgt, alpha, pts, (0.55 * u, 0.12 * u), green, H, W, tone=r.uniform(0.8, 1.15))
            if has(b, "fern"):
                for j in range(1, 6):
                    t = j / 6
                    yx = x + bend * t, H - hh * t
                    for sgn in (-1, 1):
                        petal(alb, hgt, alpha, yx[1], yx[0] + sgn * 0.9 * u, 1.1 * u * (1.2 - t), 0.35 * u,
                              0 if sgn > 0 else math.pi, green, H, W)
    elif has(b, "sapling", "propagule"):
        cx = W / 2
        capsule(alb, hgt, alpha, [(cx, H), (cx, H * 0.45)], (0.7 * u, 0.5 * u), (110, 80, 50), H, W)
        for _ in range(40):
            a = r.uniform(0, 2 * math.pi); d = r.uniform(0, 4) * u
            paint_leaf(alb, hgt, alpha, H * 0.38 + math.sin(a) * d, cx + math.cos(a) * d, 1.4 * u, 0.6 * u,
                       r.uniform(0, 2 * math.pi), shade(green, r.uniform(0.75, 1.15)), H, W, wrap=False)
    else:  # phool
        cx = W / 2 + r.uniform(-0.6, 0.6) * u
        top = r.uniform(3.5, 5.5) * u
        capsule(alb, hgt, alpha, [(cx, H), (cx + r.uniform(-0.6, 0.6) * u, H * 0.6), (cx, top + 1.2 * u)], (0.55 * u, 0.4 * u), green, H, W)
        for ly, sgn in ((H - 3.5 * u, -1), (H - 6 * u, 1)):
            petal(alb, hgt, alpha, ly - 0.8 * u, cx + sgn * 1.6 * u, 1.9 * u, 0.6 * u, -0.5 if sgn > 0 else math.pi + 0.5, green, H, W)
        bc = bloom if bloom is not None else p.light
        kind = g.seed_of(b) % 4
        cy0 = top + 0.5 * u
        if kind == 2:  # tulip cup
            for dx_, a in ((-0.9, -0.25), (0.9, 0.25), (0, 0)):
                petal(alb, hgt, alpha, cy0, cx + dx_ * u, 1.0 * u, 2.0 * u, math.pi / 2 + a, shade(bc, 1.0 - abs(dx_) * 0.1), H, W)
        else:
            npet, L, Wd = {0: (6, 1.9, 1.0), 1: (5, 2.2, 0.9), 3: (8, 2.3, 0.45)}[kind]
            for k in range(npet):
                a = k * 2 * math.pi / npet + r.uniform(-0.15, 0.15)
                petal(alb, hgt, alpha, cy0 + math.sin(a) * L * u * 0.55, cx + math.cos(a) * L * u * 0.55,
                      L * u * 0.6, Wd * u * 0.7, a, shade(bc, r.uniform(0.9, 1.1)), H, W)
            yy, xx = grid(H, W)
            dc = np.hypot(yy - cy0, xx - cx)
            m = dc < 0.9 * u
            cc = (250, 210, 80) if np.abs(bc - (250, 210, 80)).sum() > 120 else shade(bc, 0.6)
            alb[m] = tone_col(cc, 0.9 + 0.2 * (1 - dc / (0.9 * u)))[m]; hgt[m] = 1.2; alpha[m] = 255
    rgb = light(alb, hgt * alpha / 255, k=2.0, wrap=False, amb=0.55)
    return out_rgba(rgb, alpha)


def hd_destroy(c):
    st = g.stage_of(c.base) or 0
    H, W = c.H * S, c.W * S
    r = np.random.default_rng(g.seed_of("destroy-hd"))
    out = np.zeros((H, W, 4))
    for i in range(1 + st * 2):
        x, y = r.random() * W, r.random() * H
        ang = r.uniform(0, 2 * math.pi)
        for _ in range(int(r.integers(3, 7))):
            L = r.uniform(1.5, 3.5) * S
            for k in range(int(L)):
                px, py = int(x) % W, int(y) % H
                out[py, px, :3] = (34, 30, 28); out[py, px, 3] = 210
                out[(py + 1) % H, px, :3] = (90, 86, 80); out[(py + 1) % H, px, 3] = 110
                x += math.cos(ang); y += math.sin(ang)
            ang += r.uniform(-0.9, 0.9)
    return out


# ------------------------------------------------------------------ items ---
def poly(X, Y, pts):
    """Point-in-polygon (ray casting), vectorized."""
    inside = np.zeros(X.shape, bool)
    n = len(pts)
    for i in range(n):
        x0, y0 = pts[i]; x1, y1 = pts[(i + 1) % n]
        cond = ((y0 > Y) != (y1 > Y)) & (X < (x1 - x0) * (Y - y0) / (y1 - y0 + 1e-12) + x0)
        inside ^= cond
    return inside


def tool_mask(kind, H):
    """Tools ki shapes 16-unit coordinates me (geometry, pixel nahi) -> HD masks."""
    s = H / 16
    yy, xx = grid(H, H)
    X, Y = xx / s, yy / s

    def seg(x0, y0, x1, y1, w):
        dx, dy = x1 - x0, y1 - y0
        t = np.clip(((X - x0) * dx + (Y - y0) * dy) / (dx * dx + dy * dy), 0, 1)
        return np.hypot(X - (x0 + t * dx), Y - (y0 + t * dy)) < w

    if kind == "sword":
        handle = seg(2.6, 13.4, 5.2, 10.8, 0.75)
        guard = seg(3.4, 9.4, 6.6, 12.6, 0.8)
        pommel = np.hypot(X - 2.2, Y - 13.8) < 1.0
        d = (X - 5.5) - (10.5 - Y)
        along = ((X - 5.5) + (10.5 - Y)) / 2
        blade = (np.abs(d) < 1.6 * np.clip((14.6 - along * 1.0 - 4.5) / 3, 0.15, 1)) & (along > 0) & (along < 9.3)
        return blade, handle | pommel, guard
    handle = seg(2.2, 14.3, 10.4, 6.1, 0.8)
    head = np.zeros_like(handle)
    if kind == "pickaxe":
        pts = [(4.5, 3.6), (6.5, 2.4), (8.6, 2.2), (10.6, 3.0), (12.4, 4.6), (13.4, 6.6), (14.0, 9.0)]
        for (a, b_), (c_, d_) in zip(pts[:-1], pts[1:]):
            mid = abs((a + c_) / 2 - 9.4) + abs((b_ + d_) / 2 - 4)
            head |= seg(a, b_, c_, d_, 1.25 - 0.08 * mid)
    elif kind == "axe":
        head = poly(X, Y, [(9.0, 3.2), (11.0, 2.4), (12.6, 0.9), (14.6, 1.6), (15.3, 4.2), (14.4, 7.4),
                           (12.4, 8.2), (11.6, 6.6), (9.8, 6.4)])
    elif kind == "shovel":
        u, v = (X - 11.6 + Y - 4.4) / 1.414, (X - 11.6 - (Y - 4.4)) / 1.414
        head = (u / 3.4) ** 2 + (v / 2.5) ** 2 < 1
    elif kind == "hoe":
        head = seg(9.0, 4.0, 13.6, 4.0, 0.95) | seg(13.4, 4.2, 13.8, 6.6, 0.8)
    elif kind == "spear":
        u, v = (X - 12.3 - (Y - 3.7)) / 1.414, (X - 12.3 + (Y - 3.7)) / 1.414
        head = (np.abs(v) < 1.2 * np.clip(1 - u / 3.4, 0, 1)) & (u > -2.2) & (u < 3.4)
    return head, handle, np.zeros_like(handle)


def outline(rgb, alpha, width=None):
    m = alpha > 0
    ring = m.copy()
    for _ in range(width or max(1, S // 2 + 1)):
        ring = g.dilate(ring)
    ring &= ~m
    rgb[ring] = (24, 20, 26); alpha = np.where(ring, 255, alpha)
    return rgb, alpha


def metal_shade(col, mask, H, r, spec=0.7, wood=False):
    d = dist_inside(mask, 3 * S)
    hgt = np.sqrt(np.clip(d / (1.6 * S), 0, 1))
    if wood:
        g1 = fbm(H, H, r, 4, 2, 1)
        alb = tone_col(col, 0.85 + 0.25 * g1)
        return light(alb, hgt, k=2.5, wrap=False, amb=0.5)
    alb = tone_col(col, 0.92 + 0.1 * fbm(H, H, r, 2, 2, 8))
    return light(alb, hgt, k=3.2, spec=spec, shin=28, wrap=False, amb=0.45, metal=spec > 0.5, dirt=0.15)


def hi_tool(c, kind):
    H = 16 * S
    r = c.rng(130)
    headc = g.mat_color(c.base, c.pal)
    head, handle, guard = tool_mask(kind, H)
    rgb = np.zeros((H, H, 3)); alpha = np.zeros((H, H))
    wood = c.base.startswith(("wooden", "wood"))
    stone = c.base.startswith("stone")
    hrgb = metal_shade(np.array(g.HANDLE, float), handle, H, r, wood=True)
    rgb[handle] = hrgb[handle]; alpha[handle] = 255
    if guard.any():
        grgb = metal_shade(shade(headc, 0.75), guard, H, r, spec=0.5)
        rgb[guard] = grgb[guard]; alpha[guard] = 255
    hm = metal_shade(headc, head, H, r, spec=0.15 if (wood or stone) else 0.85, wood=wood)
    if stone:
        hm = hm * (0.85 + 0.3 * fbm(H, H, r, 4, 3))[..., None]
    rgb[head] = hm[head]; alpha[head] = 255
    rgb, alpha = outline(rgb, alpha)
    return out_rgba(np.clip(rgb, 0, 255), alpha)


def upmask(rows, ch_set):
    m = np.array([[ch in ch_set for ch in row] for row in rows], float)
    up = m.repeat(S, 0).repeat(S, 1)
    return blur(up, S // 2, wrap=False) > 0.5


def hi_armor(c, piece, overlay=False):
    H = 16 * S
    r = c.rng(131)
    rows = g.ARMOR[piece]
    leather = c.base.startswith("leather_")
    col = g.mat_color(c.base, c.pal) if any(c.base.startswith(k + "_") for k in g.MATS) else c.pal.main
    if leather:
        col = c.pal.main
    allm = upmask(rows, "HhD")
    dm = upmask(rows, "D") & allm
    if overlay:
        o = np.zeros((H, H, 4))
        trim = metal_shade(np.array((120, 84, 50.0)), dm, H, r, spec=0.1)
        o[dm, :3] = trim[dm]; o[dm, 3] = 255
        return o
    spec = 0.1 if leather else (0.35 if c.base.startswith("chainmail") else 0.8)
    rgb = metal_shade(col, allm, H, r, spec=spec)
    if leather:
        rgb = rgb * (0.9 + 0.15 * fbm(H, H, r, 2, 2))[..., None]
        yy, xx = grid(H, H)
        stitch = g.erode(g.erode(allm)) & ~g.erode(g.erode(g.erode(allm))) & ((xx + yy) % (2 * S) < S)
        rgb[stitch] *= 0.7
    if c.base.startswith("chainmail"):
        yy, xx = grid(H, H)
        ring = np.abs(np.hypot((yy % S) - S / 2, (xx % S) - S / 2) - S * 0.3) < 0.6
        rgb[allm & ~ring] *= 0.55
    trim = rgb.copy(); trim[dm] *= 0.78
    alpha = np.where(allm, 255.0, 0)
    trim, alpha = outline(trim, alpha)
    return out_rgba(np.clip(trim, 0, 255), alpha)


def hi_shape(c, kind):
    H = 16 * S
    r = c.rng(132)
    p = c.pal
    sat = np.ptp(p.colors, 1) / np.maximum(p.colors.max(1), 1)
    vivid = p.colors[(sat * (p.colors @ LUM)).argmax()]
    bright = p.sorted[-2] if len(p.sorted) > 2 else p.light
    col = {"gem": mix(p.accent, p.light, 0.3), "dust": vivid, "nugget": bright, "ingot": bright, "raw": p.main}.get(kind, p.main)
    if kind in ("ingot", "nugget") and any(c.base.startswith(k + "_") for k in g.MATS):
        col = g.mat_color(c.base, p)
    yy, xx = grid(H, H)
    X, Y = xx / S, yy / S
    spec, shin = 0.5, 30
    if kind == "ingot":
        top = (Y >= 5) & (Y < 8) & (X >= 4 + (8 - Y)) & (X <= 11 + (8 - Y))
        front = (Y >= 8) & (Y < 11.5) & (X >= 2.2) & (X < 11.5)
        side = (Y >= 5) & (Y < 11.5) & (X >= 11.5) & (X < 15 - (Y - 5) * 0.0) & (X - 11.5 <= (11.5 - Y) + 0.01) & (X - 11.5 <= 3)
        m = top | front | side
        f = np.where(top, 1.22, np.where(side & ~front, 0.72, 1.0))
        alb = tone_col(col, f * (0.95 + 0.08 * fbm(H, H, r, 2, 2, 8)))
        rgb = light(alb, np.zeros((H, H)) + 0.02 * fbm(H, H, r, 4, 2), k=1, spec=0.4, shin=20, wrap=False)
        rgb[front & (np.abs(Y - 8.2) < 0.25)] = shade(col, 1.35)
    elif kind == "gem":
        cy, cx = 8.0, 8.0
        d = np.abs(X - cx) + np.abs(Y - cy) * 0.9
        m = (d < 6.2) & (Y > 2.5) & (Y < 13.5)
        ang = (np.arctan2(Y - cy, X - cx) + 2 * math.pi) % (2 * math.pi)
        sec = (ang / (2 * math.pi) * 8).astype(int) % 8
        inner = d < 2.6
        tilts = np.random.default_rng(c.seed).uniform(-0.9, 0.9, (8, 2))
        tx, ty = np.where(inner, 0, tilts[sec, 0]), np.where(inner, 0, tilts[sec, 1])
        ln = np.sqrt(tx * tx + ty * ty + 1)
        dif = np.clip((tx * LIGHT[0] + ty * LIGHT[1] + LIGHT[2]) / ln, 0, 1) / LIGHT[2]
        sp = np.clip((tx * HALF[0] + ty * HALF[1] + HALF[2]) / ln, 0, 1) ** 30
        rgb = np.clip(tone_col(col, 0.5 + 0.6 * dif) + (sp * 220)[..., None], 0, 255)
    else:
        if kind == "nugget":
            m = ((X - 8) ** 2 / 9 + (Y - 9) ** 2 / 5) < 1
        elif kind == "raw":
            v = fbm(H, H, r, 4 * S, 3)
            m = ((X - 8) ** 2 + (Y - 8.5) ** 2) < 26 + 18 * (v - 0.5)
            spec = 0.15
        elif kind == "dust":
            m = np.zeros((H, H), bool)
            for _ in range(46):
                x0, y0 = r.normal(8, 2.6), r.normal(10, 1.5)
                m |= np.hypot(X - x0, Y - y0) < r.uniform(0.35, 0.8)
            m &= (Y > 3) & (Y < 15) & (X > 1) & (X < 15)
            spec = 0.6
        elif kind == "egg":
            m = ((X - 8) ** 2 / 20 + (Y - 8.5) ** 2 / 34) < 1
            spec = 0.25
        elif kind == "disc":
            dd = np.hypot(X - 8, Y - 8)
            m = (dd < 7) & (dd > 0.6)
            spec = 0.5
        else:
            m = ((X - 8) ** 2 / 16 + (Y - 10) ** 2 / 16 < 1) | ((np.abs(X - 8) < (Y - 2) * 0.5) & (Y < 10) & (Y > 2))
            spec = 0.5
        d = dist_inside(m, 4 * S)
        hgt = np.sqrt(np.clip(d / (3 * S), 0, 1))
        alb = tone_col(col, 0.92 + 0.12 * fbm(H, H, r, 2 * S, 3))
        if kind == "egg":
            sec_c = p.second if np.abs(p.second - col).sum() > 50 else shade(col, 0.6)
            for _ in range(7):
                sy, sx = r.uniform(3, 14), r.uniform(4, 12)
                sm = (np.hypot((Y - sy) * 1.2, X - sx) < r.uniform(0.6, 1.3)) & m
                alb[sm] = sec_c
        if kind == "disc":
            dd = np.hypot(X - 8, Y - 8)
            alb = tone_col(col, 0.75 + 0.1 * np.sin(dd * 14))
            lab = dd < 2.6
            alb[lab] = p.accent
            hgt = np.where(lab, 0.6, 0.4)
        rgb = light(alb, hgt, k=2.6, spec=spec, shin=shin, wrap=False)
    alpha = np.where(m, 255.0, 0)
    rgb, alpha = outline(rgb, alpha)
    return out_rgba(rgb, alpha)


def hi_inspired(c):
    """Baqi items: shape ka idea (mask ko smooth karke HD), rang smooth, dome roshni."""
    H, W = c.H * S, c.W * S
    r = c.rng(133)
    m0 = (c.ref[..., 3] > 0).astype(float)
    if not m0.any():
        return np.zeros((H, W, 4))
    up = m0.repeat(S, 0).repeat(S, 1)
    m = blur(up, S // 2 + 1, wrap=False) > 0.5
    low = g.g_masked(c, coarse=4, outline=False)
    col = low[..., :3].repeat(S, 0).repeat(S, 1)
    mk = up > 0
    sm = blur(col * mk[..., None], S, wrap=False); wt = blur(mk.astype(float), S, wrap=False)[..., None]
    col = np.where(wt > 0.02, sm / np.maximum(wt, 1e-6), col)
    col = col * (0.92 + 0.14 * fbm(H, W, r, 2 * S, 3))[..., None]
    d = dist_inside(m, 3 * S)
    hgt = np.sqrt(np.clip(d / (2.2 * S), 0, 1))
    rgb = light(col, hgt, k=2.6, spec=0.25, shin=20, wrap=False)
    alpha = np.where(m, 255.0, 0)
    rgb, alpha = outline(rgb, alpha, width=max(1, S // 2))
    return out_rgba(rgb, alpha)


def hi_item(c):
    b = c.base
    fn = g.item_design(c)
    if fn is g.i_inspired:
        return hi_inspired(c)
    for k in ("sword", "pickaxe", "axe", "shovel", "hoe", "spear"):
        if b.endswith("_" + k) and any(b.startswith(m + "_") for m in g.MATS):
            return hi_tool(c, k)
    for k in ("helmet", "chestplate", "leggings", "boots"):
        if b.endswith("_" + k):
            return hi_armor(c, k)
        if b.endswith("_" + k + "_overlay"):
            return hi_armor(c, k, overlay=True)
    for kind, test in (("egg", has(b, "spawn_egg")),
                       ("ingot", b.endswith("_ingot") or b in ("brick", "nether_brick", "netherbrick", "resin_brick")),
                       ("nugget", b.endswith("_nugget")),
                       ("gem", b in ("diamond", "emerald", "quartz", "amethyst_shard", "prismarine_crystals", "echo_shard", "lapis_lazuli")),
                       ("raw", b.startswith("raw_") and not b.endswith("_block")),
                       ("dust", b.endswith("_dust") or b in ("redstone", "gunpowder", "sugar")),
                       ("disc", b.startswith("music_disc") or b.startswith("record_")),
                       ("dye", b.endswith("_dye") or b.startswith("dye_powder"))):
        if test:
            return hi_shape(c, kind)
    return hi_inspired(c)


# --------------------------------------------------------------- entities ---
def hd_mob(c):
    s = ENT_S
    img = hd_masked(c, s, soft=2 * s, fur=0.18)
    m = c.ref[..., 3] > 0
    if any(w in c.name.lower() for w in g.NO_FACE):
        return img
    row0 = np.where(m[0])[0]
    if not len(row0):
        return img
    k = int(row0[0])
    if k < 4 or 2 * k > c.H or 4 * k > c.W or not (m[k:2 * k, k:2 * k].all() and m[0:k, k:3 * k].mean() > 0.9):
        return img
    fs = k * s
    face = img[fs:2 * fs, fs:2 * fs]
    skin = np.median(face[..., :3].reshape(-1, 3), 0)
    light_skin = (skin @ LUM) >= 180
    iris = c.pal.accent if np.abs(c.pal.accent - skin).sum() > 80 else np.array([60, 90, 140.0])
    yy, xx = grid(fs, fs)
    ey = fs * 0.56
    for ex in (fs * 0.28, fs * 0.72):
        q = ((xx - ex) / (fs * 0.13)) ** 2 + ((yy - ey) / (fs * 0.075)) ** 2
        eye = q < 1
        face[eye, :3] = (40, 40, 48) if light_skin else (236, 236, 236)
        ir = np.hypot(xx - ex - (fs * 0.03 if ex < fs / 2 else -fs * 0.03), yy - ey) < fs * 0.065
        face[eye & ir, :3] = iris
        pu = np.hypot(xx - ex - (fs * 0.03 if ex < fs / 2 else -fs * 0.03), yy - ey) < fs * 0.032
        face[eye & pu, :3] = (12, 10, 16)
        hl = np.hypot(xx - ex + fs * 0.01, yy - ey + fs * 0.03) < fs * 0.015
        face[eye & hl, :3] = (255, 255, 255)
        brow = (np.abs(yy - (ey - fs * 0.13)) < fs * 0.03) & (np.abs(xx - ex) < fs * 0.14)
        face[brow, :3] = shade(skin, 0.6)
    mouth = (np.abs(yy - fs * 0.8) < fs * 0.025) & (np.abs(xx - fs / 2) < fs * 0.14)
    face[mouth, :3] = shade(skin, 0.5)
    img[fs:2 * fs, fs:2 * fs] = face
    return img


# ----------------------------------------------------------------- driver ---
LOW2HD = {g.g_stone: hd_stone, g.g_layered: hd_layered, g.g_cobble: hd_cobble, g.g_ore: hd_ore,
          g.g_planks: hd_planks, g.g_log_side: hd_bark, g.g_log_top: hd_log_top, g.g_leaves: hd_leaves,
          g.g_bricks: hd_bricks, g.g_granular: hd_granular, g.g_capped_side: hd_capped_side,
          g.g_organic: hd_organic, g.g_wool: hd_wool, g.g_concrete: hd_concrete, g.g_powder: hd_granular,
          g.g_terracotta: hd_terracotta, g.g_glazed: hd_glazed, g.g_glass: hd_glass, g.g_metal: hd_metal,
          g.g_fluid: hd_fluid, g.g_flame: hd_flame, g.g_plant: hd_plant, g.g_destroy: hd_destroy,
          g.g_masked: hd_masked, g.g_chiseled: None, g.g_polished: None}


def make_hd(ref, name, kind, mcmeta=None, animated_hint=False):
    h, w = ref.shape[:2]
    if not (ref[..., 3] > 0).any():
        sc = ENT_S if kind == "entity" else S
        return np.zeros((h * sc, w * sc, 4), np.uint8)
    fh = g.frame_height(w, h, mcmeta) if (mcmeta or animated_hint) else h
    nf = h // fh
    frames = []
    for t in range(nf):
        fr = ref[t * fh:(t + 1) * fh]
        u = w // 16 if (kind == "block" and w > 16 and w % 16 == 0 and fh % 16 == 0) else 1
        small = fr[::u, ::u]
        c = Ctx(small, name, kind, t, nf)
        full = bool((small[..., 3] == 255).all())
        if kind == "block":
            fn = g.block_family(c, full)
            hd = LOW2HD.get(fn)
            img = hd(c) if hd else hd_finish(c, fn(c))
            if fn not in (g.g_leaves, g.g_glass, g.g_plant, g.g_masked, g.g_flame, g.g_capped_side, g.g_destroy) and not full:
                img[..., 3] = small[..., 3].repeat(S, 0).repeat(S, 1)
        elif kind == "item":
            img = hi_item(c)
        else:
            img = hd_mob(c)
        if kind == "block" and has(c.base, *EMISSIVE):
            img[..., :3] = np.clip(img[..., :3] * 0.6 + blur(img[..., :3], 2) * 0.4 * 1.25, 0, 255)  # khud roshni deta hai
        if kind == "block" and nf == 1 and QUANT:
            img = readable(img)
        frames.append(img)
    return np.clip(np.concatenate(frames, 0), 0, 255).astype(np.uint8)


EMISSIVE = ("glowstone", "shroomlight", "sea_lantern", "magma", "froglight", "lamp_on", "lit", "jack_o_lantern",
            "beacon", "end_rod", "crying_obsidian", "respawn_anchor_top")
QUANT = 32


def readable(img):
    """Voxel/pixel-art skill: chhoti palette (<=32 rang) se noise ka mush khatam, values group."""
    rgb = Image.fromarray(np.clip(img[..., :3], 0, 255).astype(np.uint8), "RGB")
    q = rgb.quantize(colors=QUANT, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE).convert("RGB")
    out = img.copy(); out[..., :3] = np.array(q, float)
    return out


def copy_meta(src, dst, scale):
    if src.endswith(".mcmeta"):
        try:
            d = json.load(open(src))
            an = d.get("animation")
            if isinstance(an, dict):
                for k in ("width", "height"):
                    if k in an:
                        an[k] = an[k] * scale
            json.dump(d, open(dst, "w"), indent=2)
            return
        except Exception:
            pass
    shutil.copyfile(src, dst)


def compact(img, colors):
    """Palette PNG (indexed) — size kam, dikhne me farq nahi (blocks pehle hi <=32 rang)."""
    try:
        if img.mode == "RGB":
            return img.quantize(colors=colors, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE)
        q = img.quantize(colors=colors, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE)
        a0 = np.array(img)[..., 3]
        back = np.array(q.convert("RGBA"))
        if np.array_equal(back[..., 3], a0):
            return q
        back[..., 3] = a0          # transparency bilkul asli rahe (cut-out / UV)
        return Image.fromarray(back, "RGBA")
    except Exception:
        return img


def process_tree(src_root, out_root, dirs, exts, flip=frozenset()):
    count = 0
    for d in dirs:
        for root, _, files in os.walk(os.path.join(src_root, d)):
            for f in sorted(files):
                if g.PBR.search(f):
                    continue
                src = os.path.join(root, f)
                rel = os.path.relpath(src, src_root)
                dst = os.path.join(out_root, rel)
                os.makedirs(os.path.dirname(dst), exist_ok=True)
                kind = g.kind_of(rel)
                if f.endswith(".mcmeta") or f.endswith(".json"):
                    copy_meta(src, dst, ENT_S if kind == "entity" else S)
                    continue
                if not f.lower().endswith(exts):
                    continue
                try:
                    im = Image.open(src)
                    mode = im.mode
                    ref = np.array(im.convert("RGBA"))
                except Exception as e:
                    print("skip", rel, e)
                    continue
                mc = src + ".mcmeta"
                key = os.path.splitext(rel)[0].replace("\\", "/")
                new = make_hd(ref, rel, kind, mc if os.path.exists(mc) else None, key in flip)
                img = Image.fromarray(new, "RGBA")
                if mode in ("RGB", "L", "P") and (new[..., 3] == 255).all():
                    img = img.convert("RGB")
                if dst.endswith(".png"):
                    img = compact(img, 128 if kind == "entity" else 256)
                    img.save(dst, optimize=True)
                else:
                    img.save(dst)
                count += 1
    return count


def main():
    edition, src, out = sys.argv[1:4]
    if edition == "java":
        tex = os.path.join(src, "assets/minecraft/textures")
        n = process_tree(tex, os.path.join(out, "assets/minecraft/textures"), g.JAVA_DIRS, (".png",))
    else:
        flip = set()
        fj = os.path.join(src, "textures/flipbook_textures.json")
        if os.path.exists(fj):
            try:
                for e in json.loads(re.sub(r"//.*", "", open(fj).read())):
                    flip.add(e["flipbook_texture"].replace("textures/", "", 1))
            except Exception as e:
                print("flipbook parse", e)
        tex = os.path.join(src, "textures")
        n = process_tree(tex, os.path.join(out, "textures"), g.BEDROCK_DIRS, (".png", ".tga"), flip)
    print(f"{edition} HD: {n} textures banaye -> {out}")


if __name__ == "__main__":
    main()
