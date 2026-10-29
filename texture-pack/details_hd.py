"""FireCraft Remastered HD — detail passes (test -> detail -> test cycle).

detail_test.py ne jo textures sab se kam detail wale nikale, unke liye yahan behtar generators:
concrete, terracotta, machine panels, obsidian, aur ek generic "micro detail" layer.
"""
import math
import numpy as np

import generate as g
import generate_hd as h

S = h.S
shade, mix, has = g.shade, g.mix, g.has


def concrete(c):
    """Pass 1: smooth concrete lekin zinda — bara mottling, bajri ke daane, trowel ki halki lakeerein,
    chhote hawa ke surakh (dhanse hue, kaale nuqte nahi)."""
    r = c.rng(601); H, W = c.H * S, c.W * S
    col = c.pal.main
    mott = h.fbm(H, W, r, H // 2, 4)
    trowel = h.fbm(H, W, r, 2, 3, W // 2)
    grain = h.fbm(H, W, r, 1, 1)
    tone = 0.95 + 0.07 * mott + 0.03 * (trowel - 0.5) + 0.06 * (grain - 0.5)
    alb = h.tone_col(col, tone)
    agg = r.random((H, W))
    alb[agg > 0.985] = shade(col, 1.12)
    alb[agg < 0.012] = shade(col, 0.9)
    hgt = 0.08 * mott + 0.04 * grain
    yy, xx = h.grid(H, W)
    for _ in range(int(6 * c.H * c.W / 256)):               # hawa ke surakh
        cy, cx, rr = r.random() * H, r.random() * W, r.uniform(0.4, 0.9) * S
        d = np.hypot(h.wrapd(yy, cy, H), h.wrapd(xx, cx, W))
        m = d < rr
        hgt = np.where(m, hgt - 0.3 * (1 - d / rr), hgt)
    return h.out_rgba(h.light(alb, hgt, k=2.4, spec=0.04, shin=10, dirt=0.25))


def terracotta(c):
    """Pass 1: pakki mitti — saaf tehein (strata), mitti ke daane, chamakte mineral, baal jaisi daraarein."""
    r = c.rng(602); H, W = c.H * S, c.W * S
    col = c.pal.main
    yy, xx = h.grid(H, W)
    warp = h.fbm(H, W, r, H // 2, 3)
    strata = np.sin((yy / H * 7 + 1.2 * warp) * 2 * math.pi)
    grain = h.fbm(H, W, r, 2, 2)
    tone = 0.92 + 0.07 * strata + 0.1 * (grain - 0.5) + 0.05 * (h.fbm(H, W, r, H // 4, 3) - 0.5)
    alb = h.tone_col(col, tone)
    sp = r.random((H, W))
    alb[sp > 0.992] = shade(col, 1.3)
    alb[sp < 0.006] = shade(col, 0.7)
    hgt = 0.12 * strata + 0.15 * grain
    cr = (h.ridged(h.fbm(H, W, r, H // 2, 3)) > 0.993) & (h.fbm(H, W, r, H // 3, 2) > 0.62)
    alb[cr] *= 0.72; hgt = np.where(cr, hgt - 0.3, hgt)
    return h.out_rgba(h.light(alb, hgt, k=2.6, dirt=0.3))


def panel(c, low):
    """Pass 1: machines/tech blocks — material (metal ya pathar) + bevel frame + khudi hui
    lakeerein + kono par bolt + front par emblem. Rang apne low-res design se."""
    r = c.rng(603); H, W = c.H * S, c.W * S
    p = c.pal
    col = p.main
    sat = np.ptp(col) / max(1.0, col.max())
    metal = sat < 0.18 and 60 < (col @ h.LUM) < 200
    yy, xx = h.grid(H, W)
    base = h.fbm(H, W, r, 1, 2, W // 2) if metal else h.fbm(H, W, r, H // 4, 4)
    up = low[..., :3].repeat(S, 0).repeat(S, 1).astype(float)
    alb = h.blur(up, 2) * (0.92 + 0.14 * base)[..., None]
    edge = np.minimum.reduce([yy, xx, H - yy, W - xx])
    hgt = np.clip(edge / (1.4 * S), 0, 1) * 0.6 + 0.1 * base
    inset = (np.abs(edge - 3.0 * S) < 0.35 * S)                     # khudi hui andar ki lakeer
    hgt = np.where(inset, hgt - 0.35, hgt)
    for cy, cx in ((1.6, 1.6), (1.6, W / S - 1.6), (H / S - 1.6, 1.6), (H / S - 1.6, W / S - 1.6)):
        d = np.hypot(yy - cy * S, xx - cx * S)
        m = d < 0.75 * S
        hgt = np.where(m, 0.9 + 0.4 * np.sqrt(np.clip(1 - (d / (0.75 * S)) ** 2, 0, 1)), hgt)
        alb[m] = shade(col, 0.85)
    if has(c.base, "front", "face", "_top") and not has(c.base, "bottom"):
        acc = p.accent if np.abs(p.accent - col).sum() > 60 else shade(col, 1.35)
        d = np.abs(xx - W / 2) + np.abs(yy - H / 2)
        ring = (np.abs(d - 3.2 * S) < 0.45 * S)
        alb[ring] = acc; hgt = np.where(ring, hgt + 0.2, hgt)
    scratches = (h.fbm(H, W, r, 1, 1, W // 3) > 0.93) & metal
    alb[scratches] = shade(col, 1.15)
    return h.out_rgba(h.light(alb, hgt, k=3, spec=0.45 if metal else 0.05, shin=30, metal=metal, dirt=0.3))


def obsidian(c):
    """Pass 1: sheeshe jaisa jwalamukhi pathar — tooti hui satah (har tukde ki apni chamak),
    crying obsidian par chamakte jamni aansu."""
    r = c.rng(604); H, W = c.H * S, c.W * S
    col = c.pal.main
    n = int(34 * c.H * c.W / 256)
    pts = r.random((n, 2)) * [H, W]
    d1, d2, cell = h.voronoi(H, W, pts)
    tilt = r.uniform(-1, 1, (n, 2))
    tx, ty = tilt[cell, 0], tilt[cell, 1]
    ln = np.sqrt(tx * tx + ty * ty + 1)
    dif = np.clip((tx * h.LIGHT[0] + ty * h.LIGHT[1] + h.LIGHT[2]) / ln, 0, 1) / h.LIGHT[2]
    spec = np.clip((tx * h.HALF[0] + ty * h.HALF[1] + h.HALF[2]) / ln, 0, 1) ** 40
    base = mix(col, (22, 14, 34), 0.55)
    fine = h.fbm(H, W, r, 2, 2)
    rgb = h.tone_col(base, 0.6 + 0.32 * dif + 0.12 * (fine - 0.5)) + (spec * 90)[..., None]
    rgb[(d2 - d1) < 0.5] *= 0.6
    if has(c.base, "crying"):
        glow = (h.fbm(H, W, r, 4, 2) > 0.7)
        rgb[glow] = mix(rgb[glow], np.array([150, 60, 230.0]), 0.75)
    return h.out_rgba(np.clip(rgb, 18, 250))


def dark_boost(img, amount=34):
    """Pass 3: gehre rangon me detail dabti hai — high-frequency detail ko additive taur par ubhaaro."""
    rgb = img[..., :3]
    lum = rgb @ h.LUM
    hf = lum - h.blur(lum, 2)
    k = np.clip(1 - lum / 110, 0, 1)[..., None]
    img = img.copy()
    img[..., :3] = np.clip(rgb + hf[..., None] * 1.6 * k + 0 * amount, 18, 250)
    return img


def dragon_egg(c):
    r = c.rng(605); H, W = c.H * S, c.W * S
    base = (24, 14, 32)
    n = h.fbm(H, W, r, H // 4, 4)
    alb = h.tone_col(base, 0.8 + 0.5 * n)
    sp = r.random((H, W))
    alb[sp > 0.975] = (190, 110, 240)
    alb[(sp > 0.96) & (sp <= 0.975)] = (110, 60, 150)
    return h.out_rgba(h.light(alb, 0.4 * n, k=2.2, spec=0.4, shin=30))


def flame(c, base_fn):
    """Pass 3: aag — andar ka roshan core aur chingariyan (embers)."""
    img = base_fn(c)
    r = c.rng(606 + c.t)
    H, W = img.shape[:2]
    a = img[..., 3] > 0
    core = a & (h.blur(a.astype(float), 3, wrap=False) > 0.92)
    img[core, :3] = np.clip(img[core, :3] * 1.18 + 20, 0, 255)
    em = (r.random((H, W)) > 0.994) & ~a
    yy = h.grid(H, W)[0]
    em &= yy < H * 0.6
    img[em, :3] = (255, 220, 120); img[em, 3] = 255
    return img


def override(c, fn, hd):
    """make_hd ke liye: (fn, hd) -> behtar generator (ya wahi)."""
    b = c.base
    if b == "dragon_egg":
        return dragon_egg
    if fn is g.g_flame and hd is not None:
        return lambda cc: flame(cc, hd)
    if has(b, "obsidian"):
        return obsidian
    if fn is g.g_concrete:
        return lambda cc: dark_boost(concrete(cc))
    if fn is g.g_terracotta:
        return lambda cc: dark_boost(terracotta(cc))
    if fn is g.g_powder and hd is not None:
        return lambda cc: dark_boost(hd(cc))
    if fn is g.g_panel:
        return lambda cc: panel(cc, fn(cc))
    return hd


def entity_detail(alb, base_col, tone_d):
    """Pass 1 (mobs): gehre rang me bhi detail dikhe — multiplicative ke saath additive detail."""
    lum = (base_col @ h.LUM)[..., None]
    return np.clip(alb + (tone_d[..., None] - 1) * 70 * (1 - lum / 255), 0, 255)
