#!/usr/bin/env python3
"""FireCraft Remastered — original texture pack generator (Java 26.2 + Bedrock).

Vanilla textures se sirf IDEA liya jata hai:
  - ye texture kis cheez ki hai (file ka naam -> family: ore, planks, sword ...)
  - size aur rangon ki palette (k-means se chand rang)
Har design — pattern, shape, shading, gems, takhte, tools ki shapes — yahan code me
khud banaya gaya hai; vanilla ka koi pixel ya layout copy nahi hota.

Functional exceptions (warna game me texture ghalat jagah lagegi):
  - mobs ke UV maps aur cut-out cheezein (rails, doors, panes...) ka alpha mask,
    jin par rang sirf 4x4 ke mote idea se aata hai aur texture/shading apni hoti hai
  - animation .mcmeta / Bedrock texture_set json files

Usage:
  generate.py java    <java-assets-dir>  <out-dir>
  generate.py bedrock <bedrock-rp-dir>   <out-dir>
"""
import json, math, os, re, shutil, sys, zlib
import numpy as np
from PIL import Image

LUM = np.array([0.299, 0.587, 0.114])
JAVA_DIRS = ["block", "item", "entity"]
BEDROCK_DIRS = ["blocks", "items", "entity"]


# ---------------------------------------------------------------- helpers ---
def seed_of(s):
    return zlib.crc32(s.encode())


def shade(c, f):
    """f<1 gehra, f>1 roshan (safed ki taraf). c: (...,3), f: scalar ya array."""
    c = np.asarray(c, float)
    f = np.asarray(f, float)
    if f.ndim and c.ndim == 1:
        c = np.broadcast_to(c, f.shape + (3,))
    f3 = f[..., None] if f.ndim else f
    return np.clip(np.where(f3 <= 1, c * f3, c + (255 - c) * (f3 - 1)), 0, 255)


def mix(a, b, t):
    return np.asarray(a, float) * (1 - t) + np.asarray(b, float) * t


def vnoise(h, w, rng, cy=4, cx=None, octaves=2):
    """Tileable smooth value noise [0,1]."""
    cx = cy if cx is None else cx
    out = np.zeros((h, w)); amp, tot = 1.0, 0.0
    for o in range(octaves):
        sy, sx = max(1, cy >> o), max(1, cx >> o)
        gh, gw = max(1, -(-h // sy)), max(1, -(-w // sx))
        g = rng.random((gh, gw))
        ys, xs = np.arange(h) / sy, np.arange(w) / sx
        y0, x0 = np.floor(ys).astype(int), np.floor(xs).astype(int)
        fy, fx = ys - y0, xs - x0
        fy, fx = fy * fy * (3 - 2 * fy), fx * fx * (3 - 2 * fx)
        y0 %= gh; x0 %= gw; y1 = (y0 + 1) % gh; x1 = (x0 + 1) % gw
        a = g[y0][:, x0] * (1 - fx) + g[y0][:, x1] * fx
        b = g[y1][:, x0] * (1 - fx) + g[y1][:, x1] * fx
        out += amp * (a * (1 - fy[:, None]) + b * fy[:, None]); tot += amp; amp *= 0.5
    return out / tot


def norm(v):
    return (v - v.min()) / (np.ptp(v) + 1e-9)


def levels(v, base, factors):
    """v [0,1] -> len(factors) bands -> shaded colors (pixel-art quantize)."""
    i = np.clip((norm(v) * len(factors)).astype(int), 0, len(factors) - 1)
    return shade(base, np.asarray(factors)[i])


def kmeans(cols, k, seed=0):
    uniq = np.unique(cols, axis=0)
    if len(uniq) <= k:
        cnt = np.array([(np.abs(cols - u).sum(1) < 1).sum() for u in uniq])
        return uniq.astype(float), cnt
    rng = np.random.default_rng(seed)
    order = np.argsort(cols @ LUM)
    c = cols[order[np.linspace(0, len(cols) - 1, k).astype(int)]].astype(float)
    for _ in range(10):
        lab = ((cols[:, None] - c[None]) ** 2).sum(-1).argmin(1)
        for i in range(k):
            m = lab == i
            c[i] = cols[m].mean(0) if m.any() else cols[rng.integers(len(cols))]
    lab = ((cols[:, None] - c[None]) ** 2).sum(-1).argmin(1)
    return c, np.bincount(lab, minlength=k)


class Pal:
    """Reference ke rang (sirf idea): main, accent, light, dark, green..."""

    def __init__(self, ref, seed):
        a = ref[..., 3] > 0
        cols = ref[..., :3][a].astype(float) if a.any() else np.array([[128, 128, 128.0]])
        self.colors, self.counts = kmeans(cols, 5, seed)
        share = self.counts / max(1, self.counts.sum())
        self.share = share
        order = np.argsort(self.colors @ LUM)
        self.sorted = self.colors[order]
        self.main = self.colors[share.argmax()]
        sat = np.ptp(self.colors, 1) / np.maximum(self.colors.max(1), 1)
        ok = share >= 0.03
        self.accent = self.colors[np.where(ok, sat, -1).argmax()]
        self.dark = self.sorted[0]
        self.light = self.sorted[-1]
        g = self.colors[:, 1] - (self.colors[:, 0] + self.colors[:, 2]) / 2
        self.green = self.colors[np.where(ok, g, -999).argmax()]
        self.mean = cols.mean(0)
        self.grey = bool(np.ptp(self.colors, 1).max() < 20)
        o2 = np.argsort(-share)
        self.second = self.colors[o2[1]] if len(o2) > 1 else self.main

    def least_sat_main(self):
        sat = np.ptp(self.colors, 1) / np.maximum(self.colors.max(1), 1)
        return self.colors[(self.share - sat).argmax()]


def rgba(rgb, alpha=255):
    h, w = rgb.shape[:2]
    out = np.zeros((h, w, 4)); out[..., :3] = rgb
    out[..., 3] = alpha
    return out


def stamp(img, pts, x, y, color, W, H, alpha=None):
    for dx, dy in pts:
        xx, yy = (x + dx) % W, (y + dy) % H
        img[yy, xx, :3] = color
        if img.shape[-1] == 4:
            img[yy, xx, 3] = 255 if alpha is None else alpha


def walk(rng, W, H, n):
    x, y = int(rng.integers(W)), int(rng.integers(H))
    pts = []
    for _ in range(n):
        pts.append((x % W, y % H))
        dx, dy = [(1, 0), (1, 1), (0, 1), (-1, 1)][rng.integers(4)]
        x += dx; y += dy
    return pts


def erode(m):
    p = np.pad(m, 1)
    return m & p[:-2, 1:-1] & p[2:, 1:-1] & p[1:-1, :-2] & p[1:-1, 2:]


def dilate(m):
    p = np.pad(m, 1)
    return m | p[:-2, 1:-1] | p[2:, 1:-1] | p[1:-1, :-2] | p[1:-1, 2:]


def has(base, *words):
    return any(w in base for w in words)


# ------------------------------------------------------------ block designs --
class Ctx:
    def __init__(self, ref, name, kind, t=0, nf=1):
        self.ref, self.name, self.kind, self.t, self.nf = ref, name, kind, t, nf
        self.base = os.path.basename(name).rsplit(".", 1)[0]
        self.H, self.W = ref.shape[:2]
        self.seed = seed_of(name)
        self.pal = Pal(ref, self.seed)

    def rng(self, salt=0):
        return np.random.default_rng(self.seed + salt)


def g_stone(c, col=None, cracks=2, factors=(0.8, 0.9, 1.0, 1.08)):
    r = c.rng(1)
    col = c.pal.least_sat_main() if col is None else col
    v = vnoise(c.H, c.W, r, 4, octaves=3) * 0.75 + vnoise(c.H, c.W, r, 1, 1, 1) * 0.25
    img = levels(v, col, factors)
    for _ in range(cracks):
        for x, y in walk(r, c.W, c.H, int(r.integers(3, 6))):
            img[y, x] = shade(col, 0.68)
    return rgba(img)


def g_cobble(c):
    """Gol patharon ki deewar (apna voronoi design), gehri darzon ke saath."""
    r, p = c.rng(26), c.pal
    col = p.least_sat_main()
    if has(c.base, "deepslate"):
        col = mix(col, (70, 70, 78), 0.4)
    n = max(5, (c.W * c.H) // 36)
    pts = r.random((n, 2)) * [c.H, c.W]
    shades = r.uniform(0.86, 1.1, n)
    yy, xx = np.mgrid[0:c.H, 0:c.W] + 0.5
    d = []
    for py, px in pts:
        dy = np.abs(yy - py); dy = np.minimum(dy, c.H - dy)
        dx = np.abs(xx - px); dx = np.minimum(dx, c.W - dx)
        d.append(np.hypot(dy, dx))
    d = np.stack(d)
    o = np.argsort(d, 0)
    d1 = np.take_along_axis(d, o[:1], 0)[0]; d2 = np.take_along_axis(d, o[1:2], 0)[0]
    cell = o[0]
    cy, cx = pts[cell, 0], pts[cell, 1]
    rel = ((yy - cy + c.H / 2) % c.H - c.H / 2) + ((xx - cx + c.W / 2) % c.W - c.W / 2)
    f = shades[cell] * np.where(rel < -1.5, 1.1, np.where(rel > 1.5, 0.9, 1.0))
    f = np.where(d2 - d1 < 0.9, 0.6, f)
    img = rgba(shade(col, f))
    if has(c.base, "mossy"):
        m = vnoise(c.H, c.W, r, 4) > 0.6
        img[m, :3] = levels(vnoise(c.H, c.W, r, 1), (88, 118, 52), (0.85, 1.0, 1.12))[m]
    return img


def g_layered(c, col=None):
    """Deepslate / basalt jaisi: leti (horizontal) parten."""
    r = c.rng(2)
    col = c.pal.least_sat_main() if col is None else col
    v = 0.7 * vnoise(c.H, c.W, r, 2, 8) + 0.3 * vnoise(c.H, c.W, r, 1, 4, 1)
    img = levels(v, col, (0.74, 0.86, 0.97, 1.08))
    for y in range(1, c.H, 5):
        x0 = int(r.integers(c.W))
        for i in range(int(r.integers(3, 7))):
            img[(y + (i // 4)) % c.H, (x0 + i) % c.W] = shade(col, 0.62)
    return rgba(img)


GEMS = [[(1, 0), (0, 1), (1, 1), (2, 1), (1, 2)],          # plus
        [(0, 0), (1, 0), (0, 1), (1, 1)],                  # square
        [(1, 0), (0, 1), (1, 1), (2, 1), (2, 2)],          # kite
        [(0, 0), (1, 0), (1, 1), (2, 1)]]                  # step


def g_ore(c):
    p, r = c.pal, c.rng(3)
    if has(c.base, "deepslate"):
        img = g_layered(c, mix(p.least_sat_main(), (70, 70, 78), 0.5))
    elif has(c.base, "nether", "quartz"):
        img = g_stone(c, mix(p.least_sat_main(), (110, 45, 45), 0.4), cracks=1)
    else:
        img = g_stone(c, p.least_sat_main(), cracks=1)
    if has(c.base, "coal"):
        gem = shade(p.dark, 0.8)
    elif has(c.base, "quartz"):
        gem = p.light
    else:
        gem = p.accent
    base = img[..., :3].copy()
    for i in range(4):
        gx, gy = (i % 2) * (c.W // 2), (i // 2) * (c.H // 2)
        x, y = gx + int(r.integers(1, max(2, c.W // 2 - 3))), gy + int(r.integers(1, max(2, c.H // 2 - 3)))
        shape = GEMS[int(r.integers(len(GEMS)))]
        for dx, dy in shape:  # saaya
            yy, xx = (y + dy + 1) % c.H, (x + dx + 1) % c.W
            img[yy, xx, :3] = shade(base[yy, xx], 0.7)
        stamp(img, shape, x, y, gem, c.W, c.H)
        stamp(img, shape[:1], x, y, shade(gem, 1.35), c.W, c.H)
        stamp(img, shape[-1:], x, y, shade(gem, 0.72), c.W, c.H)
    return img


def g_planks(c):
    r, col = c.rng(4), c.pal.main
    v = vnoise(c.H, c.W, r, 8, 1)
    img = np.zeros((c.H, c.W, 3))
    bw = max(4, c.W // 2)
    for b in range(c.W // bw):
        xs = slice(b * bw, (b + 1) * bw)
        f = 0.94 + 0.1 * r.random()
        img[:, xs] = levels(v[:, xs], shade(col, f), (0.88, 0.95, 1.0, 1.06))
        img[:, (b + 1) * bw - 1] = shade(col, 0.68)
        j = int(r.integers(3, c.H - 3))
        img[j, b * bw:(b + 1) * bw - 1] = shade(col, 0.74)
        for nx, ny in ((b * bw + 1, j - 1), (b * bw + bw - 3, j + 1), (b * bw + 1, j + 1), (b * bw + bw - 3, j - 1)):
            img[ny % c.H, nx % c.W] = shade(col, 0.58)
    return rgba(img)


def g_log_side(c):
    r, p = c.rng(5), c.pal
    stripped = has(c.base, "stripped")
    col = p.main
    v = vnoise(c.H, c.W, r, 8, 1)
    img = levels(v, col, (0.92, 1.0, 1.08) if stripped else (0.8, 0.9, 1.0, 1.1))
    for _ in range(2 if stripped else 4):
        x = int(r.integers(c.W)); y = int(r.integers(c.H))
        for i in range(int(r.integers(c.H // 2, c.H))):
            img[(y + i) % c.H, x % c.W] = shade(col, 0.82 if stripped else 0.6)
            if r.random() < 0.18:
                x += int(r.choice([-1, 1]))
    return rgba(img)


def g_log_top(c):
    r, p = c.rng(6), c.pal
    wood = p.light if not has(c.base, "stem", "hyphae") else p.second
    bark = p.dark
    yy, xx = np.mgrid[0:c.H, 0:c.W] + 0.5
    cy, cx = c.H / 2, c.W / 2
    d = 0.6 * np.maximum(abs(yy - cy), abs(xx - cx)) + 0.4 * np.hypot(yy - cy, xx - cx)
    d += (vnoise(c.H, c.W, r, 4) - 0.5) * 0.8
    ring = (np.floor(d / 1.5) % 2).astype(float)
    img = shade(wood, 0.94 + 0.1 * ring)
    img[d < 0.9] = shade(wood, 0.8)
    edge = (np.minimum.reduce([yy, xx, c.H - yy, c.W - xx]) < 1.2)
    if not has(c.base, "stripped"):
        img[edge] = shade(bark, np.where((xx + yy).astype(int) % 3 == 0, 0.8, 1.0))[edge]
    return rgba(img)


LEAF = [(1, 0), (0, 1), (1, 1), (2, 1), (1, 2), (2, 2)]


def g_leaves(c):
    r, col = c.rng(7), c.pal.main
    opaque = bool((c.ref[..., 3] > 0).all())
    img = np.zeros((c.H, c.W, 4))
    if opaque:
        img[..., :3] = shade(col, 0.55); img[..., 3] = 255
    for gy in range(0, c.H, 3):
        for gx in range(0, c.W, 3):
            if r.random() < 0.12:
                continue
            x, y = gx + int(r.integers(0, 2)), gy + int(r.integers(0, 2))
            f = float(r.choice([0.76, 0.88, 1.0, 1.1]))
            stamp(img, LEAF, x, y, shade(col, f), c.W, c.H)
            stamp(img, LEAF[:1], x, y, shade(col, f * 1.12), c.W, c.H)
            stamp(img, LEAF[-1:], x, y, shade(col, f * 0.78), c.W, c.H)
    return img


BIG_BRICK = ("stone", "deepslate", "tuff", "end", "blackstone", "quartz", "prismarine", "purpur", "sandstone")
DARK_MORTAR = ("stone", "deepslate", "nether", "end", "blackstone", "tuff", "polished", "mud", "prismarine", "purpur", "resin")


def g_bricks(c):
    r, p = c.rng(8), c.pal
    col = p.main
    big = has(c.base, *BIG_BRICK)
    rh, bl = (8, 16) if big else (4, 8)
    rh, bl = min(rh, c.H), min(bl, c.W)
    mortar = shade(col, 0.6) if has(c.base, *DARK_MORTAR) else mix(col, (196, 190, 180), 0.75)
    img = np.zeros((c.H, c.W, 3))
    nz = vnoise(c.H, c.W, r, 2)
    for y in range(c.H):
        row = y // rh
        off = (row % 2) * (bl // 2)
        for x in range(c.W):
            if y % rh == rh - 1 or (x + off) % bl == bl - 1:
                img[y, x] = mortar; continue
            bid = (row, (x + off) // bl)
            f = 0.92 + 0.12 * ((seed_of(f"{c.name}{bid}") % 100) / 100)
            if y % rh == 0:
                f *= 1.08
            elif y % rh == rh - 2:
                f *= 0.9
            f *= 0.96 + 0.08 * (nz[y, x] > 0.6)
            img[y, x] = shade(col, f)
    out = rgba(img)
    if has(c.base, "cracked"):
        for _ in range(3):
            for x, y in walk(r, c.W, c.H, 5):
                out[y, x, :3] = shade(col, 0.55)
    if has(c.base, "mossy"):
        m = vnoise(c.H, c.W, r, 4) > 0.62
        out[m, :3] = levels(vnoise(c.H, c.W, r, 1), (88, 118, 52), (0.85, 1.0, 1.12))[m]
    return out


def g_wool(c):
    col = c.pal.main
    yy, xx = np.mgrid[0:c.H, 0:c.W]
    k = np.where(xx % 4 < 2, (xx // 2 + yy) % 2, (yy - xx // 2) % 2)
    r = c.rng(9)
    f = np.where(k == 1, 1.05, 0.93) * (0.97 + 0.06 * (vnoise(c.H, c.W, r, 1, 1, 1) > 0.5))
    return rgba(shade(col, f))


def g_concrete(c):
    v = vnoise(c.H, c.W, c.rng(10), 1, 1, 1)
    f = np.where(v > 0.86, 1.04, np.where(v < 0.12, 0.96, 1.0))
    return rgba(shade(c.pal.main, f))


def g_powder(c):
    r = c.rng(11)
    return rgba(levels(vnoise(c.H, c.W, r, 1, 1, 1) + 0.3 * vnoise(c.H, c.W, r, 2), c.pal.main, (0.86, 0.94, 1.0, 1.07)))


def g_terracotta(c):
    r = c.rng(12)
    v = 0.6 * vnoise(c.H, c.W, r, 2, 16) + 0.4 * vnoise(c.H, c.W, r, 1, 1, 1)
    return rgba(levels(v, c.pal.main, (0.9, 0.96, 1.0, 1.05)))


def g_glazed(c):
    """Apna 4-fold pinwheel motif (har rang ke liye alag, seed se)."""
    r, cols = c.rng(13), c.pal.sorted
    n = len(cols)
    q = r.integers(0, n, (c.H // 2, c.W // 2))
    for _ in range(2):  # majority smoothing -> shapes
        pad = np.pad(q, 1, mode="wrap")
        nb = np.stack([pad[1 + dy:1 + dy + q.shape[0], 1 + dx:1 + dx + q.shape[1]]
                       for dy in (-1, 0, 1) for dx in (-1, 0, 1)])
        q = np.apply_along_axis(lambda a: np.bincount(a, minlength=n).argmax(), 0, nb)
    q[0, :] = 0; q[:, 0] = 0          # frame line (gehra)
    q[1, 1] = n - 1                   # kone par roshan nuqta
    for i in range(2, min(q.shape) - 1):
        q[i, i] = n - 2 if n > 2 else 0   # diagonal line
    t = np.zeros((c.H, c.W), int)
    hh, hw = c.H // 2, c.W // 2
    t[:hh, :hw] = q
    t[:hh, hw:] = np.rot90(q, -1)
    t[hh:, hw:] = np.rot90(q, 2)
    t[hh:, :hw] = np.rot90(q, 1)
    return rgba(cols[t])


def g_glass(c):
    p = c.pal
    a = c.ref[..., 3]
    semi = ((a > 0) & (a < 250)).mean() > 0.3
    inner_a = float(a[(a > 0) & (a < 250)].mean()) if semi else 0
    col = p.main
    frame = mix(p.light, col, 0.4)
    img = np.zeros((c.H, c.W, 4))
    img[..., :3] = col; img[..., 3] = inner_a
    e = np.zeros((c.H, c.W), bool)
    e[0, :] = e[-1, :] = e[:, 0] = e[:, -1] = True
    img[e, :3] = frame; img[e, 3] = 255
    for y, x in ((0, 0), (0, c.W - 1), (c.H - 1, 0), (c.H - 1, c.W - 1)):
        img[y, x, 3] = 0
    for x, y in ((2, 4), (3, 3), (4, 2), (2, 5), (5, 2)):
        if x < c.W and y < c.H:
            img[y, x, :3] = shade(frame, 1.25); img[y, x, 3] = 255
    for x, y in ((c.W - 4, c.H - 3), (c.W - 3, c.H - 4)):
        img[y, x, :3] = shade(frame, 1.15); img[y, x, 3] = 255
    return img


def g_metal(c):
    r, p = c.rng(14), c.pal
    col = p.main
    img = np.zeros((c.H, c.W, 3))
    ph, pw = max(4, c.H // 2), max(4, c.W // 2)
    nz = vnoise(c.H, c.W, r, 2)
    shiny = has(c.base, "diamond", "emerald", "gold", "lapis", "amethyst", "redstone", "quartz")
    for y in range(c.H):
        for x in range(c.W):
            py, px = y % ph, x % pw
            f = 1.0 + (0.03 if nz[y, x] > 0.6 else 0)
            if py == 0 or px == 0:
                f = 1.14
            if py == ph - 1 or px == pw - 1:
                f = 0.8
            if (px, py) in ((1, 1), (pw - 2, 1), (1, ph - 2), (pw - 2, ph - 2)):
                f = 0.7
            if shiny and (px + py) in (4, 5) and 2 <= px <= 4:
                f = 1.3
            img[y, x] = shade(col, f)
    return rgba(img)


def g_granular(c, col=None):
    r = c.rng(15)
    col = c.pal.main if col is None else col
    img = levels(vnoise(c.H, c.W, r, 1, 1, 1) * 0.7 + vnoise(c.H, c.W, r, 4) * 0.3, col, (0.84, 0.92, 1.0, 1.07))
    for _ in range(6 if has(c.base, "gravel") else 4):
        x, y = int(r.integers(c.W)), int(r.integers(c.H))
        dark = r.random() < 0.6
        for dx, dy in ([(0, 0), (1, 0)] if r.random() < 0.5 else [(0, 0), (0, 1), (1, 0), (1, 1)]):
            img[(y + dy) % c.H, (x + dx) % c.W] = shade(col, 0.72 if dark else 1.16)
    return rgba(img)


def g_capped_side(c):
    """Grass/mycelium/podzol side: neeche mitti, upar apni lehrdaar parat."""
    r, ref = c.rng(16), c.ref[..., :3].astype(float)
    a = c.ref[..., 3] > 0
    top = ref[:3][a[:3]].mean(0) if a[:3].any() else c.pal.green
    bot = ref[-6:][a[-6:]].mean(0) if a[-6:].any() else c.pal.main
    overlay = has(c.base, "overlay")
    img = g_granular(c, bot) if not overlay else np.zeros((c.H, c.W, 4))
    hgt = 2 + np.round(2.2 * vnoise(1, c.W, r, 1, 4)[0]).astype(int)
    for x in range(c.W):
        hh = hgt[x] + (3 if r.random() < 0.18 else 0)
        for y in range(min(hh, c.H)):
            f = 1.08 if y == 0 else (0.86 if y == hh - 1 else 1.0)
            img[y, x, :3] = shade(top, f * (0.96 + 0.08 * r.random())); img[y, x, 3] = 255
    return img


def g_organic(c):
    """Grass top, moss, nylium...: noise + chhoti pattiyon ki lakeerein."""
    r, col = c.rng(17), c.pal.main
    img = levels(vnoise(c.H, c.W, r, 2) * 0.6 + vnoise(c.H, c.W, r, 1, 1, 1) * 0.4, col, (0.82, 0.92, 1.0, 1.1))
    for _ in range(c.W // 2):
        x, y = int(r.integers(c.W)), int(r.integers(c.H))
        img[y, x] = shade(col, 1.18); img[(y + 1) % c.H, x] = shade(col, 1.06)
    return rgba(img)


def g_fluid(c):
    """Pani/lava: scrolling noise, frame-wise loop."""
    r = c.rng(18)
    big = vnoise(c.H * 2, c.W, r, 4, octaves=3)
    v = np.roll(big, int(c.t * (2 * c.H) / max(1, c.nf)), 0)[:c.H]
    if has(c.base, "lava", "magma"):
        cols = c.pal.sorted
        i = np.clip((norm(v) * len(cols)).astype(int), 0, len(cols) - 1)
        return rgba(cols[i])
    return rgba(levels(v, c.pal.main, (0.82, 0.9, 1.0, 1.1, 1.22)))


def g_flame(c):
    """Aag / soul fire: apna flame generator (animated)."""
    r = c.rng(19)
    big = vnoise(c.H * 2, c.W, r, 3, 2, octaves=2)
    v = np.roll(big, -int(c.t * (2 * c.H) / max(1, c.nf)), 0)[:c.H]
    yy = np.mgrid[0:c.H, 0:c.W][0] / c.H
    heat = v * 0.6 + yy * 0.8 - 0.35
    img = np.zeros((c.H, c.W, 4))
    cols = c.pal.sorted
    i = np.clip(((heat - 0.25) / 0.8 * len(cols)).astype(int), 0, len(cols) - 1)
    img[..., :3] = cols[i]; img[..., 3] = np.where(heat > 0.25, 255, 0)
    return img


def g_panel(c):
    """Machines/misc ke liye: apna frame-panel design + accent emblem (front par)."""
    r, p = c.rng(20), c.pal
    col = p.main
    img = levels(vnoise(c.H, c.W, r, 2), col, (0.9, 0.97, 1.03))
    img[0, :] = img[-1, :] = shade(col, 0.72); img[:, 0] = img[:, -1] = shade(col, 0.72)
    img[1, 1:-1] = shade(col, 1.12); img[1:-1, 1] = shade(col, 1.12)
    img[-2, 1:-1] = shade(col, 0.86); img[1:-1, -2] = shade(col, 0.86)
    if has(c.base, "front", "face"):
        acc = p.accent if np.abs(p.accent - col).sum() > 60 else shade(col, 0.6)
        cy, cx = c.H // 2, c.W // 2
        for d in range(-3, 4):
            for e in (abs(d) - 3, 3 - abs(d)):
                img[(cy + e) % c.H, (cx + d) % c.W] = acc
        img[cy, cx] = shade(acc, 1.3)
    return rgba(img)


def g_bookshelf(c):
    r = c.rng(21)
    wood = c.pal.main
    img = levels(vnoise(c.H, c.W, r, 8, 1), wood, (0.9, 1.0, 1.06))
    half = c.H // 2
    for shelf in range(2):
        y_floor = (shelf + 1) * half - 1
        x = 1
        while x < c.W - 1:
            bw = int(r.integers(1, 3))
            hgt = int(r.integers(3, half - 1))
            colr = np.array([[150, 40, 40], [40, 70, 140], [50, 110, 60], [150, 120, 50], [110, 60, 120]][r.integers(5)], float)
            for xx in range(x, min(x + bw, c.W - 1)):
                for yy in range(y_floor - hgt, y_floor):
                    img[yy, xx] = shade(colr, 1.08 if xx == x else 0.94)
                img[y_floor - hgt, xx] = shade(colr, 1.25)
            x += bw + (1 if r.random() < 0.3 else 0)
        img[y_floor, :] = shade(wood, 0.7)
    img[0, :] = shade(wood, 0.75)
    return rgba(img)


FONT = {"T": ["111", "010", "010", "010", "010"], "N": ["1001", "1101", "1011", "1001", "1001"]}


def g_tnt(c):
    red = c.pal.accent
    yy, xx = np.mgrid[0:c.H, 0:c.W]
    img = shade(red, np.where(xx % 4 == 0, 0.8, 1.0))
    if c.base.endswith("side"):
        y0 = c.H // 2 - 4
        img[y0:y0 + 8] = (232, 228, 220)
        img[y0] = img[y0 + 7] = (190, 186, 178)
        x = 2
        for ch in "TNT":
            g = FONT[ch]
            for rr, row in enumerate(g):
                for cc, bit in enumerate(row):
                    if bit == "1":
                        img[y0 + 2 + rr - 1, x + cc] = (30, 30, 30)
            x += len(g[0]) + 1
    else:
        cy, cx = c.H // 2, c.W // 2
        img[cy - 2:cy + 2, cx - 2:cx + 2] = (60, 60, 60)
        img[cy - 1:cy + 1, cx - 1:cx + 1] = (30, 30, 30)
    return rgba(img)


def g_crafting_top(c):
    img = g_planks(c)
    col = c.pal.main
    for i in (c.W // 3, 2 * c.W // 3):
        img[:, i, :3] = shade(col, 0.66); img[i, :, :3] = shade(col, 0.66)
    img[0, :, :3] = img[-1, :, :3] = shade(col, 0.6)
    img[:, 0, :3] = img[:, -1, :3] = shade(col, 0.6)
    return img


def g_ribbed(c):
    """Pumpkin / melon side: apni khadi dhaariyan."""
    r, col = c.rng(22), c.pal.main
    yy, xx = np.mgrid[0:c.H, 0:c.W]
    f = 0.9 + 0.14 * np.cos((xx + 0.5) / c.W * 2 * math.pi * 3) + 0.04 * (vnoise(c.H, c.W, r, 4, 1) - 0.5)
    img = levels(f, col, (0.82, 0.92, 1.0, 1.1))
    img[0, :] = img[-1, :] = shade(col, 0.75)
    return rgba(img)


def g_hay(c):
    r, col = c.rng(23), c.pal.main
    img = levels(vnoise(c.H, c.W, r, 1, 8), col, (0.85, 0.95, 1.05, 1.12))
    if c.base.endswith("side"):
        for y in (3, c.H - 4):
            img[y] = c.pal.dark
    return rgba(img)


def g_chiseled(c):
    img = g_stone(c, c.pal.main, cracks=0, factors=(0.94, 1.0, 1.05))
    col = c.pal.main
    for d, f in ((1, 0.75), (3, 1.12), (5, 0.8)):
        lo, hi = d, c.W - 1 - d
        if lo >= hi:
            break
        img[lo, lo:hi + 1, :3] = shade(col, f); img[hi, lo:hi + 1, :3] = shade(col, f)
        img[lo:hi + 1, lo, :3] = shade(col, f); img[lo:hi + 1, hi, :3] = shade(col, f)
    return img


def g_cut(c):
    img = g_stone(c, c.pal.main, cracks=0, factors=(0.95, 1.0, 1.05))
    col = c.pal.main
    hh, hw = max(1, c.H // 2), max(1, c.W // 2)
    for y in range(c.H):
        for x in range(c.W):
            if y % hh == hh - 1 or x % hw == hw - 1:
                img[y, x, :3] = shade(col, 0.74)
            elif y % hh == 0 or x % hw == 0:
                img[y, x, :3] = shade(col, 1.1)
    return img


def g_polished(c):
    img = g_stone(c, c.pal.main, cracks=0, factors=(0.96, 1.0, 1.04))
    col = c.pal.main
    img[0, :, :3] = shade(col, 1.12); img[:, 0, :3] = shade(col, 1.12)
    img[-1, :, :3] = shade(col, 0.8); img[:, -1, :3] = shade(col, 0.8)
    return img


# --- plants (apne sprites) ---------------------------------------------------
PLANT_WORDS = ("sapling", "flower", "tulip", "orchid", "allium", "bluet", "daisy", "poppy", "dandelion",
               "cornflower", "lily", "rose", "fern", "short_grass", "tall_grass", "bush", "sprout", "roots",
               "mushroom", "fungus", "_stage", "stage_", "crop", "vine", "kelp", "seagrass", "coral", "berr",
               "petals", "pitcher", "torchflower", "wildflower", "lilac", "peony", "sunflower", "propagule",
               "dripleaf", "hanging_moss", "pale_moss_carpet", "cactus_flower", "firefly", "leaf_litter",
               "grass", "dead_bush", "sugar_cane", "reeds", "bamboo_stage", "nether_wart", "eyeblossom",
               "deadbush", "tallgrass", "double_plant", "waterlily", "sweet_berry", "cane")


def stage_of(base):
    m = re.search(r"stage_?(\d+)", base)
    return int(m.group(1)) if m else None


def g_plant(c):
    r, p = c.rng(24), c.pal
    W, H = c.W, c.H
    img = np.zeros((H, W, 4))
    green = p.main if p.grey else p.green
    bloom = p.accent if np.abs(p.accent - green).sum() > 70 else None
    b = c.base

    def px(x, y, col, f=1.0):
        if 0 <= x < W and 0 <= y < H:
            img[y, x, :3] = shade(col, f); img[y, x, 3] = 255

    st = stage_of(b)
    if has(b, "mushroom", "fungus") and not has(b, "block", "stem"):
        cap = bloom if bloom is not None else p.main
        cx, top = W // 2, H // 2 - 2
        for y in range(top + 3, H - 2):
            px(cx, y, p.light, 0.95); px(cx - 1, y, p.light, 0.85)
        for y, half in ((top, 2), (top + 1, 3), (top + 2, 4)):
            for x in range(cx - half, cx + half):
                px(x, y, cap, 1.12 if y == top else (0.85 if y == top + 2 else 1.0))
        px(cx - 2, top + 1, (240, 240, 230)); px(cx + 1, top, (240, 240, 230))
        return img
    if st is not None or has(b, "wheat", "carrots", "potatoes", "beetroots", "crop"):
        mx = {"wheat": 7, "carrots": 3, "potatoes": 3, "beetroots": 3}.get(b.split("_")[0], 7)
        s = min(1.0, (st if st is not None else mx) / max(1, mx))
        hgt = int(3 + s * (H - 5))
        for x0 in range(1, W, 4):
            lean = int(r.integers(-1, 2))
            for i in range(hgt):
                px(x0 + (lean if i > hgt // 2 else 0), H - 1 - i, green, 0.85 + 0.25 * i / hgt)
            grain = bloom if bloom is not None else (p.light if not p.grey else None)
            if s >= 0.99 and grain is not None and bloom is None:
                for dy in range(3):
                    px(x0 + lean, H - hgt + dy, grain, 1.1 - 0.1 * dy); px(x0 + lean + 1, H - hgt + dy + 1, grain, 0.9)
            if s >= 0.99 and bloom is not None:
                for dy in range(3):
                    px(x0 + lean, H - hgt + dy, bloom, 1.1 - 0.1 * dy); px(x0 + lean + 1, H - hgt + dy + 1, bloom, 0.9)
        return img
    if has(b, "grass", "fern", "bush", "roots", "sprout", "seagrass", "kelp", "vine", "litter", "moss", "reeds", "cane"):
        n = 4 if has(b, "fern") else 6
        for k in range(n):
            x = int(1 + k * (W - 2) / n + r.integers(0, 2))
            hgt = int(r.integers(H // 2, H - 1))
            lean = float(r.uniform(-0.25, 0.25))
            for i in range(hgt):
                xx = int(round(x + lean * i))
                px(xx, H - 1 - i, green, 0.8 + 0.35 * i / hgt)
                if has(b, "fern") and i % 3 == 1:
                    px(xx - 1, H - 1 - i, green, 0.9); px(xx + 1, H - 1 - i, green, 0.9)
        return img
    if has(b, "sapling", "propagule"):
        cx = W // 2
        for y in range(H - 6, H):
            px(cx, y, (110, 80, 50))
        for y in range(3, H - 5):
            half = 4 - abs(y - (H // 2 - 1)) // 2
            for x in range(cx - half, cx + half + 1):
                if r.random() < 0.85:
                    px(x, y, green, float(r.choice([0.8, 0.95, 1.1])))
        return img
    # phool: dandi + patte + apni bloom shapes
    cx = W // 2 + int(r.integers(-1, 1))
    top = int(r.integers(3, 6))
    for y in range(top + 2, H):
        px(cx, y, green, 0.9)
    for ly, d in ((H - 4, -1), (H - 6, 1)):
        px(cx + d, ly, green, 1.05); px(cx + 2 * d, ly - 1, green, 1.15)
    bc = bloom if bloom is not None else p.light
    shapes = {0: [(0, -2), (-2, 0), (2, 0), (0, 2), (-1, -1), (1, -1), (-1, 1), (1, 1)],
              1: [(-1, -2), (0, -2), (1, -2), (-2, -1), (2, -1), (-2, 0), (2, 0), (-1, 1), (0, 1), (1, 1)],
              2: [(0, -2), (-1, -1), (0, -1), (1, -1), (-2, 0), (-1, 0), (1, 0), (2, 0), (0, 1)],
              3: [(-1, -1), (1, -1), (0, 0), (-1, 1), (1, 1), (0, -2), (0, 2)]}[seed_of(b) % 4]
    for dx, dy in shapes:
        px(cx + dx, top + 1 + dy, bc, 1.1 if dy < 0 else 0.92)
    px(cx, top + 1, (250, 220, 90) if np.abs(bc - (250, 220, 90)).sum() > 120 else shade(bc, 0.7))
    return img


def g_masked(c, coarse=4, outline=True):
    """Functional cut-out shapes (rails, doors, panes, mobs ke UV): mask follow,
    rang 4x4 ke mote idea se, shading/texture apni."""
    r, ref = c.rng(25), c.ref
    a = ref[..., 3]
    m = a > 0
    out = np.zeros((c.H, c.W, 4))
    if not m.any():
        return out
    pal = c.pal.colors
    idx = ((ref[..., :3][:, :, None, :].astype(float) - pal[None, None]) ** 2).sum(-1).argmin(-1)
    H2, W2 = -(-c.H // coarse) * coarse, -(-c.W // coarse) * coarse
    ip = np.full((H2, W2), -1); ip[:c.H, :c.W] = np.where(m, idx, -1)
    cells = ip.reshape(H2 // coarse, coarse, W2 // coarse, coarse).transpose(0, 2, 1, 3).reshape(H2 // coarse, W2 // coarse, -1)
    cnt = np.stack([(cells == i).sum(-1) for i in range(len(pal))], -1) + r.uniform(0, 0.3, cells.shape[:2] + (len(pal),))
    base = pal[cnt.argmax(-1)].repeat(coarse, 0).repeat(coarse, 1)[:c.H, :c.W]
    v = vnoise(c.H, c.W, r, 2) * 0.5 + vnoise(c.H, c.W, r, 1, 1, 1) * 0.5
    f = np.where(v > 0.66, 1.06, np.where(v < 0.33, 0.94, 1.0))
    if outline:
        inner = erode(m)
        p = np.pad(inner, 1)
        f = np.where(inner & ~p[:-2, :-2], f * 1.1, f)
        f = np.where(inner & ~p[2:, 2:], f * 0.88, f)
        f = np.where(m & ~inner & dilate(inner), f * 0.72, f)
    out[..., :3] = shade(base, f); out[..., 3] = a
    return out


# ------------------------------------------------------------ item designs ---
MATS = {"wooden": (150, 112, 62), "wood": (150, 112, 62), "stone": (140, 140, 140), "iron": (222, 222, 222),
        "golden": (246, 205, 72), "gold": (246, 205, 72), "diamond": (80, 226, 216), "netherite": (82, 70, 78),
        "copper": (214, 124, 78), "leather": (160, 101, 58), "chainmail": (160, 166, 172), "turtle": (80, 170, 80)}
HANDLE = (112, 78, 44)


def mat_color(base, pal):
    for k, v in MATS.items():
        if base.startswith(k + "_"):
            return np.array(v, float)
    return pal.accent


def line(img, x0, y0, x1, y1, col, thick=1):
    n = int(max(abs(x1 - x0), abs(y1 - y0))) + 1
    for i in range(n):
        t = i / max(1, n - 1)
        x, y = int(round(x0 + (x1 - x0) * t)), int(round(y0 + (y1 - y0) * t))
        for k in range(thick):
            if 0 <= x + k < 16 and 0 <= y < 16:
                img[y, x + k, :3] = col; img[y, x + k, 3] = 255


def finish_item(img, outline_col=None):
    """Apna outline (shape ke bahar 1px) + upar/baayein kinare par roshni."""
    m = img[..., 3] > 0
    out = img.copy()
    p = np.pad(m, 1)
    hl = m & (~p[:-2, 1:-1] | ~p[1:-1, :-2])
    out[hl, :3] = shade(img[hl, :3], 1.12)
    ring = dilate(m) & ~m
    out[ring, :3] = (28, 24, 30) if outline_col is None else outline_col
    out[ring, 3] = 255
    return out


def i_tool(c, kind):
    img = np.zeros((16, 16, 4))
    head = mat_color(c.base, c.pal)
    hd, hl = shade(head, 0.78), shade(head, 1.2)
    hcol = np.array(HANDLE, float)
    if kind == "sword":
        line(img, 3, 12, 5, 10, hcol)
        line(img, 2, 13, 2, 13, shade(head, 0.9))
        line(img, 3, 9, 6, 12, hd)                 # crossguard
        line(img, 6, 9, 13, 2, head, 2)            # blade
        line(img, 7, 8, 13, 2, hl)                 # fuller shine
        line(img, 14, 1, 14, 1, hl)
    else:
        line(img, 2, 14, 10, 6, hcol)
        line(img, 3, 14, 10, 7, shade(hcol, 0.8))
        if kind == "pickaxe":
            pts = [(5, 3), (6, 2), (7, 2), (8, 2), (9, 2), (10, 3), (11, 4), (12, 5), (13, 6), (13, 7), (13, 8), (14, 9)]
            for x, y in pts:
                line(img, x, y, x, y, head); line(img, x, y + 1, x, y + 1, hd)
            line(img, 5, 2, 5, 2, hl); line(img, 14, 10, 14, 10, hl)
        elif kind == "axe":
            for y in range(2, 9):
                for x in range(9, 15):
                    if (x - 9) + (8 - y) <= 7 and (x - 9) - (y - 2) >= -1 and not (x == 9 and y > 6):
                        img[y, x, :3] = head if x < 13 else hl; img[y, x, 3] = 255
            line(img, 9, 3, 9, 7, hd)
        elif kind == "shovel":
            for y in range(1, 9):
                for x in range(7, 16):
                    if ((x - 11.5) ** 2 + (y - 4.5) ** 2) < 10 and (x - 11.5) + (y - 4.5) < 3.5:
                        img[y, x, :3] = head; img[y, x, 3] = 255
            line(img, 10, 2, 13, 2, hl)
            line(img, 9, 7, 10, 6, hd)
        elif kind == "hoe":
            line(img, 9, 4, 13, 4, head); line(img, 9, 5, 12, 5, hd)
            line(img, 13, 5, 13, 6, head); line(img, 9, 4, 9, 4, hl)
        elif kind == "spear":
            line(img, 10, 6, 13, 3, head, 2); line(img, 14, 2, 14, 2, hl)
    return finish_item(img)


ARMOR = {
    "helmet": ["................", "................", "................", ".....HHHHHH.....",
               "....HhhhhhHH....", "...HhHHHHHHHH...", "...HhHHHHHHHH...", "...HDDDDDDDDH...",
               "...HD......DH...", "...HH......HH...", "...HD......DH...", "................",
               "................", "................", "................", "................"],
    "chestplate": ["................", "..HHH......HHH..", ".HhhHH....HHhhH.", ".HhHHHHHHHHHHHH.",
                   "..HhHHHHHHHHHD..", "...HhHHHHHHHD...", "...HhHHDHHHHD...", "...HhHHDHHHHD...",
                   "...HhHHDHHHHD...", "...HhHHDHHHHD...", "...HHHHDHHHHD...", "...HDDDDDDDDD...",
                   "................", "................", "................", "................"],
    "leggings": ["................", "................", "...HhhhhhhhhH...", "...HDDDDDDDDH...",
                 "...HhHHHHHHHD...", "...HhHH..HHHD...", "...HhHH..HHHD...", "...HhHH..HHHD...",
                 "...HhHH..HHHD...", "...HhHH..HHHD...", "...HhHH..HHHD...", "...HDDD..DDDD...",
                 "................", "................", "................", "................"],
    "boots": ["................", "................", "................", "................",
              "................", "................", "...HhH....HhH...", "...HhH....HhH...",
              "...HhH....HhH...", "...HhH....HhH...", "..HhHH...HhHH...", ".HhHHD..HhHHD...",
              ".HDDDD..HDDDD...", "................", "................", "................"],
}


def i_armor(c, piece, overlay=False):
    img = np.zeros((16, 16, 4))
    col = mat_color(c.base, c.pal) if any(c.base.startswith(k + "_") for k in MATS) else c.pal.main
    if c.base.startswith("leather_"):
        col = c.pal.main if not overlay else np.array((120, 84, 50.0))   # base grey = dye se tint hota hai
    if overlay:  # leather: sirf trim (D) pixels, bina tint ke
        for y, row in enumerate(ARMOR[piece]):
            for x, ch in enumerate(row):
                if ch == "D":
                    img[y, x, :3] = col; img[y, x, 3] = 255
        return img
    for y, row in enumerate(ARMOR[piece]):
        for x, ch in enumerate(row):
            if ch != ".":
                img[y, x, :3] = shade(col, {"H": 1.0, "h": 1.22, "D": 0.76}[ch]); img[y, x, 3] = 255
                if c.base.startswith("chainmail") and (x + y) % 2 == 0 and ch == "H":
                    img[y, x, :3] = shade(col, 0.7)
    return finish_item(img)


def i_shape(c, kind):
    img = np.zeros((16, 16, 4))
    p = c.pal
    bright = p.sorted[-2] if len(p.sorted) > 2 else p.light
    sat = np.ptp(p.colors, 1) / np.maximum(p.colors.max(1), 1)
    vivid = p.colors[(sat * (p.colors @ LUM)).argmax()]
    col = {"gem": mix(p.accent, p.light, 0.3), "dust": vivid, "nugget": bright, "ingot": bright,
           "raw": p.main}.get(kind, p.main)
    if kind in ("ingot", "nugget") and any(c.base.startswith(k + "_") for k in MATS):
        col = mat_color(c.base, p)
    r = c.rng(30)
    yy, xx = np.mgrid[0:16, 0:16] + 0.5
    if kind == "ingot":
        # isometric bar: upar ka chehra (roshan parallelogram) + saamne ka chehra
        top = (yy >= 5) & (yy < 8) & (xx >= 4 + (8 - yy)) & (xx <= 12 + (8 - yy) - 1)
        front = (yy >= 8) & (yy < 12) & (xx >= 2) & (xx < 12)
        side = (yy >= 5) & (yy < 12) & (xx >= 12) & (xx < 15) & (xx - 12 < 12 - yy + 3) & (yy - 5 >= xx - 12 - 0)
        m = top | front | side
        f = np.where(top, 1.25, np.where(side & ~front, 0.75, 1.0))
        f = np.where(front & (yy == 8.5), 1.08, f)
    elif kind == "nugget":
        m = ((xx - 8) ** 2 / 9 + (yy - 9) ** 2 / 5) < 1
        f = np.where(yy < 8.5, 1.18, 0.92)
    elif kind == "gem":
        d = np.abs(xx - 8) + np.abs(yy - 8) * 0.9
        m = (d < 6.2) & (yy > 2.5) & (yy < 13.5)
        f = np.where(xx < 8, np.where(yy < 8, 1.25, 1.0), np.where(yy < 8, 1.05, 0.78))
    elif kind == "raw":
        v = vnoise(16, 16, r, 4)
        m = ((xx - 8) ** 2 + (yy - 8.5) ** 2) < 26 + 18 * (v - 0.5)
        f = np.where(v > 0.6, 1.15, np.where(v < 0.35, 0.82, 1.0))
    elif kind == "dust":
        m = np.zeros((16, 16), bool)
        for _ in range(26):
            x, y = int(r.normal(8, 2.6)), int(r.normal(10, 1.6))
            if 1 <= x < 15 and 3 <= y < 15:
                m[y, x] = True
        f = np.where(r.random((16, 16)) > 0.5, 1.15, 0.9)
    elif kind == "egg":
        m = ((xx - 8) ** 2 / 20 + (yy - 8.5) ** 2 / 34) < 1
        f = np.where(xx + yy < 13, 1.15, np.where(xx + yy > 20, 0.85, 1.0))
    elif kind == "disc":
        d = np.hypot(xx - 8, yy - 8)
        m = d < 7
        f = np.where(d < 2.6, 1.0, np.where((d.astype(int)) % 2 == 0, 0.85, 0.95))
    else:  # dye / drop
        m = ((xx - 8) ** 2 / 16 + (yy - 10) ** 2 / 16 < 1) | ((np.abs(xx - 8) < (yy - 2) * 0.5) & (yy < 10) & (yy > 2))
        f = np.where(xx < 7, 1.15, 0.92)
    img[m, :3] = shade(col, f)[m]; img[m, 3] = 255
    if kind == "egg":
        sec = p.second if np.abs(p.second - col).sum() > 50 else shade(col, 0.6)
        for _ in range(6):
            x, y = int(r.integers(4, 12)), int(r.integers(3, 14))
            for dx, dy in ((0, 0), (1, 0), (0, 1)):
                if m[y + dy, x + dx]:
                    img[y + dy, x + dx, :3] = sec
    if kind == "disc":
        img[(np.hypot(xx - 8, yy - 8) < 2.6) & m, :3] = p.accent
        img[7:9, 7:9, 3] = 0
        img[2, 5, :3] = shade(col, 1.4); img[3, 4, :3] = shade(col, 1.4)
    return finish_item(img)


def i_inspired(c):
    """Baqi items: shape ka idea (mask 3x3 majority se naram), rang 4x4 ke mote
    idea se, roshni + andar ki outline apni."""
    ref = c.ref
    m = ref[..., 3] > 0
    if not m.any():
        return np.zeros(ref.shape, float)
    p = np.pad(m, 1).astype(int)
    votes = sum(p[1 + dy:1 + dy + m.shape[0], 1 + dx:1 + dx + m.shape[1]] for dy in (-1, 0, 1) for dx in (-1, 0, 1))
    ms = (votes >= 5) | (m & erode(m))
    if ms.sum() < 4:
        ms = m
    tmp = c.ref.copy(); tmp[..., 3] = np.where(ms, 255, 0)
    c2 = Ctx(tmp, c.name, c.kind); c2.pal = c.pal
    img = g_masked(c2, coarse=4, outline=False)
    mm = img[..., 3] > 0
    edge = mm & ~erode(mm)
    img[edge, :3] = shade(img[edge, :3], 0.62)
    pp = np.pad(erode(mm), 1)
    hl = erode(mm) & ~pp[:-2, :-2]
    img[hl, :3] = shade(img[hl, :3], 1.15)
    return img


# --------------------------------------------------------------- entities ----
NO_FACE = ("banner", "bed", "chest", "boat", "shield", "sign", "decorated_pot", "equipment", "conduit", "beacon",
           "bell", "enchant", "end_portal", "experience", "fishing", "lead", "minecart", "projectile", "trident",
           "shulker", "end_crystal", "armorstand", "armor_stand", "_overlay", "outer", "armor", "eyes", "saddle",
           "collar", "emissive", "glow", "crack", "marking", "pattern", "spark", "wind_charge", "breeze_wind",
           "heart", "hat", "harness", "leash", "ghast_shooting", "map", "painting")


def e_mob(c):
    img = g_masked(c, coarse=4, outline=False)
    m = c.ref[..., 3] > 0
    if any(w in c.name.lower() for w in NO_FACE):
        return img
    row0 = np.where(m[0])[0]
    if not len(row0):
        return img
    s = int(row0[0])
    if s < 4 or 2 * s > c.H or 4 * s > c.W:
        return img
    if not (m[s:2 * s, s:2 * s].all() and m[0:s, s:3 * s].mean() > 0.9):
        return img
    face = img[s:2 * s, s:2 * s]
    skin = np.median(face[..., :3].reshape(-1, 3), 0)
    w = max(1, s // 8)
    ey = s + s // 2
    light_skin = (skin @ LUM) >= 180
    white = np.array([40, 40, 48.0]) if light_skin else np.array([238, 238, 238.0])
    pupil = np.array([250, 250, 250.0]) if light_skin else np.array([24, 20, 32.0])
    for x, inner_right in ((s + w, True), (2 * s - 3 * w, False)):
        img[ey:ey + w, x:x + 2 * w, :3] = white
        px = x + w if inner_right else x
        img[ey:ey + w, px:px + w, :3] = pupil
        img[ey - w:ey, x:x + 2 * w, :3] = shade(skin, 0.75)       # bhaven (brows)
    my = min(2 * s - 2 * w, ey + 2 * w)
    img[my:my + w, s + s // 2 - w:s + s // 2 + w, :3] = shade(skin, 0.6)   # munh
    return img


# --------------------------------------------------------------- classify ----
MACHINE = ("furnace", "smoker", "dispenser", "dropper", "observer", "piston", "crafter", "command", "structure",
           "jigsaw", "loom", "lectern", "jukebox", "note", "barrel", "beehive", "bee_nest", "smithing", "fletching",
           "cartography", "target", "lodestone", "respawn", "spawner", "vault", "trial", "chiseled_bookshelf",
           "stonecutter", "grindstone", "enchanting", "anvil", "cauldron", "hopper", "composter", "daylight",
           "redstone_lamp", "beacon", "conduit", "crafting_table", "chest", "sculk_sensor", "sculk_shrieker",
           "test_block", "test_instance", "dispenser", "furnace")


def g_destroy(c):
    """Block tootne ki apni daraarein: stage ke saath barhti hain."""
    st = stage_of(c.base) or 0
    r = np.random.default_rng(seed_of("destroy"))
    img = np.zeros((c.H, c.W, 4))
    lines = [walk(r, c.W, c.H, int(r.integers(4, 9))) for _ in range(12)]
    for i in range(min(len(lines), 1 + st)):
        for x, y in lines[i]:
            img[y, x, :3] = (40, 36, 34); img[y, x, 3] = 200
            if r.random() < 0.3:
                img[y, (x + 1) % c.W, :3] = (70, 66, 62); img[y, (x + 1) % c.W, 3] = 150
    return img


def block_family(c, full):
    b = c.base
    if b.startswith("destroy_stage"):
        return g_destroy
    if has(b, "fire") and not has(b, "fire_coral", "fire_charge"):
        return g_flame
    if has(b, "water", "lava") and not has(b, "cauldron"):
        return g_fluid
    if has(b, "leaves", "leaf") and not has(b, "litter", "dripleaf"):
        return g_leaves
    if has(b, "glass") and not has(b, "pane"):
        return g_glass
    if not full:
        if any(w in b for w in PLANT_WORDS) and not has(b, "block", "potted", "pot", "slab", "stairs", "door"):
            return g_plant
        return g_masked
    if b.endswith("_overlay") or has(b, "grass_side", "grass_block_snow") or \
            (b.endswith("_side") and has(b, "grass", "mycelium", "podzol", "path", "nylium", "snow")):
        return g_capped_side
    if b.startswith("tnt_") or b == "tnt":
        return g_tnt
    if has(b, "bookshelf") and not has(b, "chiseled"):
        return g_bookshelf
    if b == "crafting_table_top":
        return g_crafting_top
    if has(b, "pumpkin", "melon") and has(b, "side"):
        return g_ribbed
    if has(b, "hay"):
        return g_hay
    if has(b, "_ore", "ore_") or b.endswith("ore"):
        return g_ore
    if has(b, "cobble"):
        return g_cobble
    if has(b, "glazed"):
        return g_glazed
    if has(b, "concrete_powder"):
        return g_powder
    if has(b, "concrete"):
        return g_concrete
    if has(b, "wool", "carpet"):
        return g_wool
    if has(b, "terracotta", "hardened_clay", "stained_clay"):
        return g_terracotta
    if has(b, "planks"):
        return g_planks
    if has(b, "log", "stem", "hyphae", "_wood") and not has(b, "mushroom_stem", "pumpkin", "melon", "attached"):
        return g_log_top if has(b, "top") else g_log_side
    if has(b, "brick"):
        return g_bricks
    if has(b, "chiseled"):
        return g_chiseled
    if b.startswith("cut_") or has(b, "_cut_"):
        return g_cut
    if b.startswith("polished") or b.startswith("smooth"):
        return g_polished
    if (b.endswith("_block") and has(b, "iron", "gold", "diamond", "emerald", "lapis", "redstone", "netherite",
                                     "coal", "copper", "amethyst", "quartz", "raw_")) or has(b, "copper_block"):
        return g_metal
    if has(b, "dirt", "sand", "gravel", "soul_s", "farmland", "podzol", "mud", "clay", "suspicious", "snow"):
        return g_granular
    if has(b, "grass", "moss", "nylium", "mycelium", "wart_block", "lichen", "sculk"):
        return g_organic
    if has(b, "deepslate", "basalt") and not has(b, "brick", "tile"):
        return g_layered
    if any(w in b for w in MACHINE) or b.endswith(("_front", "_front_on", "_bottom", "_inner")):
        return g_panel
    return g_stone


def item_design(c):
    b = c.base
    for k in ("sword", "pickaxe", "axe", "shovel", "hoe", "spear"):
        if b.endswith("_" + k) and any(b.startswith(m + "_") for m in MATS):
            return lambda cc, k=k: i_tool(cc, k)
    for k in ("helmet", "chestplate", "leggings", "boots"):
        if b.endswith("_" + k):
            return lambda cc, k=k: i_armor(cc, k)
        if b.endswith("_" + k + "_overlay"):
            return lambda cc, k=k: i_armor(cc, k, overlay=True)
    if has(b, "spawn_egg"):
        return lambda cc: i_shape(cc, "egg")
    if b.endswith("_ingot") or b in ("brick", "nether_brick", "netherbrick", "resin_brick"):
        return lambda cc: i_shape(cc, "ingot")
    if b.endswith("_nugget"):
        return lambda cc: i_shape(cc, "nugget")
    if b in ("diamond", "emerald", "quartz", "amethyst_shard", "prismarine_crystals", "echo_shard", "lapis_lazuli"):
        return lambda cc: i_shape(cc, "gem")
    if b.startswith("raw_") and not b.endswith("_block"):
        return lambda cc: i_shape(cc, "raw")
    if b.endswith("_dust") or b in ("redstone", "gunpowder", "sugar"):
        return lambda cc: i_shape(cc, "dust")
    if b.startswith("music_disc") or b.startswith("record_"):
        return lambda cc: i_shape(cc, "disc")
    if b.endswith("_dye") or b.startswith("dye_powder"):
        return lambda cc: i_shape(cc, "dye")
    return i_inspired


# ------------------------------------------------------------------ driver ---
def frame_height(w, h, mcmeta):
    if h > w and h % w == 0:
        if mcmeta:
            try:
                fh = json.load(open(mcmeta)).get("animation", {}).get("height")
                if fh and h % fh == 0:
                    return fh
            except Exception:
                pass
        return w
    return h


def make(ref, name, kind, mcmeta=None, animated_hint=False):
    """ref RGBA uint8 -> naya RGBA uint8, same size."""
    h, w = ref.shape[:2]
    if not (ref[..., 3] > 0).any():
        return ref.copy()
    fh = frame_height(w, h, mcmeta) if (mcmeta or animated_hint) else h
    nf = h // fh
    out = np.zeros((h, w, 4))
    for t in range(nf):
        fr = ref[t * fh:(t + 1) * fh]
        u = w // 16 if (kind == "block" and w > 16 and w % 16 == 0 and fh % 16 == 0) else 1
        small = fr[::u, ::u]
        c = Ctx(small, name, kind, t, nf)
        full = bool((small[..., 3] == 255).all())
        if kind == "block":
            fn = block_family(c, full)
            img = fn(c)
            if fn not in (g_leaves, g_glass, g_plant, g_masked, g_flame, g_capped_side, g_destroy) and not full:
                img[..., 3] = small[..., 3]          # Bedrock tint-mask / functional alpha
        elif kind == "item":
            img = item_design(c)(c) if small.shape[:2] == (16, 16) else i_inspired(c)
        else:
            img = e_mob(c)
        if u > 1:
            img = img.repeat(u, 0).repeat(u, 1)
        out[t * fh:(t + 1) * fh] = img[:fh, :w]
    return np.clip(out, 0, 255).astype(np.uint8)


def kind_of(rel):
    top = rel.replace("\\", "/").split("/")[0]
    return {"items": "item", "blocks": "block"}.get(top, top)


PBR = re.compile(r"(_mers?|_normal|_heightmap)\.(png|tga)$|\.texture_set\.json$")


def process_tree(src_root, out_root, dirs, exts, flip=frozenset()):
    count = 0
    for d in dirs:
        for root, _, files in os.walk(os.path.join(src_root, d)):
            for f in sorted(files):
                if PBR.search(f):
                    continue  # Bedrock PBR maps (roshni ka data, tasveer nahi) -> vanilla hi rehne do
                src = os.path.join(root, f)
                rel = os.path.relpath(src, src_root)
                dst = os.path.join(out_root, rel)
                os.makedirs(os.path.dirname(dst), exist_ok=True)
                if f.endswith(".mcmeta") or f.endswith(".json"):
                    shutil.copyfile(src, dst)
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
                new = make(ref, rel, kind_of(rel), mc if os.path.exists(mc) else None, key in flip)
                img = Image.fromarray(new, "RGBA")
                if mode in ("RGB", "L", "P") and (new[..., 3] == 255).all():
                    img = img.convert("RGB")
                img.save(dst)
                count += 1
    return count


def main():
    edition, src, out = sys.argv[1:4]
    if edition == "java":
        tex = os.path.join(src, "assets/minecraft/textures")
        n = process_tree(tex, os.path.join(out, "assets/minecraft/textures"), JAVA_DIRS, (".png",))
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
        n = process_tree(tex, os.path.join(out, "textures"), BEDROCK_DIRS, (".png", ".tga"), flip)
    print(f"{edition}: {n} textures banaye -> {out}")


if __name__ == "__main__":
    main()
