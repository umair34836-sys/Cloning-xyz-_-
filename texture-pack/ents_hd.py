"""FireCraft Remastered HD — flat-layout entity textures ke apne designs (round-2 entity QA).

Pottery motifs, gharay (pot) ki mitti, banner/shield ka kapda, lakdi wale entities (chest, boat,
armor stand), armor plates aur horse markings. Har function HD RGBA (float) lautata hai.
"""
import math
import numpy as np

import generate as g
import generate_hd as h

shade, mix, has = g.shade, g.mix, g.has
TERRA = np.array((176, 98, 64), float)
TERRA_D = np.array((92, 46, 30), float)


# ------------------------------------------------------------ helpers -------
def _grid(c, s):
    H, W = c.H * s, c.W * s
    yy, xx = h.grid(H, W)
    return H, W, xx / s, yy / s


def soft_ref(c, s, rad=None):
    """Ref ke rang ka sirf idea: region ka average, kinare smooth (pixel copy nahi)."""
    mk = (c.ref[..., 3] > 0).repeat(s, 0).repeat(s, 1)
    col = c.ref[..., :3].astype(float).repeat(s, 0).repeat(s, 1)
    rad = rad or s
    sm = h.blur(col * mk[..., None], rad, wrap=False)
    wt = h.blur(mk.astype(float), rad, wrap=False)[..., None]
    return np.where(wt > 0.05, sm / np.maximum(wt, 1e-6), col), mk


def finish(c, s, alb, hgt, spec=0.0, metal=False, k=2.0, alpha=None):
    mk = (c.ref[..., 3] > 0).repeat(s, 0).repeat(s, 1)
    rgb = h.light(alb, hgt, k=k, spec=spec, shin=30, wrap=False, metal=metal, dirt=0.15)
    out = np.zeros(alb.shape[:2] + (4,))
    out[..., :3] = rgb
    out[..., 3] = np.where(mk, 255, 0) if alpha is None else alpha
    return out


def sat_of(col):
    return col.max(-1) - col.min(-1)


# --------------------------------------------------------- pottery ----------
def _motif(name, X, Y):
    """Har sherd ka apna nishaan (16-unit), sirf idea se: angler=kaanta, archer=teer-kamaan ..."""
    def seg(x0, y0, x1, y1, w=0.55):
        dx, dy = x1 - x0, y1 - y0
        t = np.clip(((X - x0) * dx + (Y - y0) * dy) / (dx * dx + dy * dy + 1e-9), 0, 1)
        return np.hypot(X - (x0 + t * dx), Y - (y0 + t * dy)) < w

    def circ(cx, cy, r):
        return np.hypot(X - cx, Y - cy) < r

    def ring(cx, cy, r, w=0.5):
        return np.abs(np.hypot(X - cx, Y - cy) - r) < w

    def arc(cx, cy, r, a0, a1, w=0.5):
        th = np.arctan2(Y - cy, X - cx)
        return ring(cx, cy, r, w) & (th >= a0) & (th <= a1)

    def heart(cx, cy, k=1.0):
        return circ(cx - 1.4 * k, cy - 0.8 * k, 1.6 * k) | circ(cx + 1.4 * k, cy - 0.8 * k, 1.6 * k) | \
            h.poly(X, Y, [(cx - 2.9 * k, cy - 0.3 * k), (cx + 2.9 * k, cy - 0.3 * k), (cx, cy + 3.2 * k)])

    n = name
    if n == "angler":
        return seg(8, 3, 8, 10) | arc(6.4, 10, 1.6, 0, math.pi) | seg(4.8, 10, 4.2, 8.6) | seg(8, 3, 11, 2.6, 0.35)
    if n == "archer":
        return arc(11, 8, 5, math.pi / 2, 3 * math.pi / 2) | seg(6, 3.2, 6, 12.8, 0.3) | seg(3, 8, 13, 8) | \
            h.poly(X, Y, [(2, 8), (4, 6.8), (4, 9.2)])
    if n == "arms_up":
        return circ(8, 4.2, 1.6) | seg(8, 5.6, 8, 10.6, 0.8) | seg(8, 7, 4.6, 3.6) | seg(8, 7, 11.4, 3.6) | \
            seg(8, 10.6, 5.8, 13.6) | seg(8, 10.6, 10.2, 13.6)
    if n == "blade":
        return seg(8, 2.6, 8, 10.6, 0.8) | seg(5, 10.8, 11, 10.8) | seg(8, 11, 8, 13.4, 0.45) | circ(8, 13.8, 0.8)
    if n == "brewer":
        return circ(8, 10, 3.2) | seg(8, 4, 8, 7.4, 0.8) | seg(6.8, 3.6, 9.2, 3.6, 0.5)
    if n == "burn":
        return h.poly(X, Y, [(8, 2.6), (11.6, 9), (10.6, 13), (5.4, 13), (4.4, 9)]) & \
            ~h.poly(X, Y, [(8, 7.6), (9.8, 10.6), (8, 12.6), (6.2, 10.6)])
    if n == "danger":
        sq = (np.abs(X - 8) < 5) & (np.abs(Y - 8) < 5)
        holes = ((np.abs(X - 6) < 1) & (np.abs(Y - 6.4) < 1)) | ((np.abs(X - 10) < 1) & (np.abs(Y - 6.4) < 1)) | \
            ((np.abs(X - 8) < 1.6) & (Y > 8) & (Y < 11.6))
        return sq & ~holes & ~((np.abs(X - 8) < 4) & (np.abs(Y - 8) < 4)) | holes
    if n == "explorer":
        return ((np.abs(X - 8) < 5) & (np.abs(Y - 8) < 4.4)) & ~((np.abs(X - 8) < 4.2) & (np.abs(Y - 8) < 3.6)) | \
            seg(5, 10, 7.4, 7.6, 0.35) | seg(7.4, 7.6, 9.6, 9.4, 0.35) | seg(9.6, 9.4, 11.2, 6.4, 0.35) | circ(11.2, 6.4, 0.8)
    if n == "flow":
        r = np.hypot(X - 8, Y - 8); th = np.arctan2(Y - 8, X - 8)
        return (np.abs(((r - th * 1.1) % 3.4) - 1.7) < 0.55) & (r < 6.4)
    if n == "friend":
        return circ(5.6, 6.2, 1.4) | circ(10.4, 6.2, 1.4) | seg(5.6, 7.6, 5.6, 12, 0.8) | seg(10.4, 7.6, 10.4, 12, 0.8) | \
            seg(5.6, 9, 10.4, 9, 0.5)
    if n == "guster":
        return arc(8, 8, 5, -math.pi, 0.6) | arc(8, 8, 3, -0.4, 2.6) | arc(8, 8, 1.2, -math.pi, 1.2)
    if n == "heart":
        return heart(8, 8.4)
    if n == "heartbreak":
        crack = np.abs(X - (8 + 0.9 * np.sign(np.sin((Y - 4) * 1.4)) * np.abs(np.sin((Y - 4) * 1.4)))) < 0.45
        return heart(8, 8.4) & ~crack
    if n == "howl":
        return h.poly(X, Y, [(4, 13), (5, 6), (6.6, 3), (7.6, 6), (9.6, 6.4), (12.6, 8.6), (11, 10.6), (10, 13)]) & \
            ~circ(8.2, 7.6, 0.7)
    if n == "miner":
        return arc(8, 9, 5.4, -2.6, -0.5, 0.75) | seg(8, 3.6, 8, 13.6, 0.55)
    if n == "mourner":
        return circ(8, 6.4, 3.6) & ~circ(6.6, 6, 0.8) & ~circ(9.4, 6, 0.8) | seg(6.4, 10.4, 6.2, 13.4, 0.4) | \
            seg(9.6, 10.4, 9.8, 13.4, 0.4)
    if n == "plenty":
        box = (np.abs(X - 8) < 5) & (Y > 5) & (Y < 12.4)
        return box & ~((np.abs(X - 8) < 4.2) & (Y > 5.8) & (Y < 11.6)) | seg(3, 8, 13, 8, 0.4) | \
            ((np.abs(X - 8) < 0.8) & (np.abs(Y - 8.6) < 1.1))
    if n == "prize":
        return h.poly(X, Y, [(8, 2.6), (13, 7), (8, 13.4), (3, 7)]) & ~h.poly(X, Y, [(8, 4.4), (11, 7), (8, 11), (5, 7)]) | \
            seg(3, 7, 13, 7, 0.35)
    if n == "scrape":
        return seg(5, 13, 10.6, 3.4, 0.55) | h.poly(X, Y, [(9, 3.2), (12.8, 2.6), (13.2, 6.6), (10.4, 6)])
    if n == "sheaf":
        out = seg(8, 13, 8, 4, 0.4) | seg(8, 13, 5, 4.6, 0.4) | seg(8, 13, 11, 4.6, 0.4) | seg(6, 10, 10, 10, 0.5)
        for x, y in ((8, 3.4), (5, 4), (11, 4)):
            out |= ((X - x) / 0.9) ** 2 + ((Y - y) / 1.6) ** 2 < 1
        return out
    if n == "shelter":
        return circ(8, 6, 4) | seg(8, 9, 8, 13.6, 0.75) | seg(4, 13.6, 12, 13.6, 0.4)
    if n == "skull":
        return (circ(8, 7, 4.2) | ((np.abs(X - 8) < 2.6) & (Y > 9) & (Y < 12.6))) & ~circ(6.4, 7.4, 1.1) & \
            ~circ(9.6, 7.4, 1.1) & ~((np.abs(X - 8) < 0.5) & (Y > 9.4) & (Y < 10.6))
    if n == "snort":
        return h.poly(X, Y, [(2.6, 11.6), (3.6, 6.4), (8, 4.6), (12.4, 6), (13.4, 9.6), (11.6, 11.6)]) & ~circ(10.6, 7.6, 0.8) | \
            seg(4, 11.6, 4, 13.6, 0.6) | seg(11, 11.6, 11, 13.6, 0.6)
    # anjaan naya sherd: naam se banne wala chakkar
    k = sum(map(ord, n)) % 5 + 3
    th = np.arctan2(Y - 8, X - 8)
    return np.abs(np.hypot(X - 8, Y - 8) - (3.6 + 1.2 * np.cos(th * k))) < 0.6


def pottery(c, s):
    H, W, X, Y = _grid(c, s)
    r = c.rng(611)
    n = c.base.replace("_pottery_pattern", "")
    grain = h.fbm(H, W, r, s * 3, 3); fine = h.fbm(H, W, r, 1, 1)
    alb = h.tone_col(TERRA, 0.9 + 0.14 * grain + 0.06 * (fine - 0.5))
    hgt = 0.5 + 0.1 * grain
    m = _motif(n, X * 16 / c.W, Y * 16 / c.H)
    soft = h.blur(m.astype(float), max(1, s // 2), wrap=False)
    alb = np.where(m[..., None], h.tone_col(TERRA_D, 0.92 + 0.14 * grain), alb)
    hgt = hgt - 0.35 * soft                                   # nishaan andar khuda hua
    rim = (X < 0.6) | (X > c.W - 0.6) | (Y < 0.6) | (Y > c.H - 0.6)
    alb[rim] = shade(TERRA, 0.8)
    return finish(c, s, alb, hgt, k=2.4)


def pot_plain(c, s):
    H, W, X, Y = _grid(c, s)
    r = c.rng(612)
    alb, mk = soft_ref(c, s, 2 * s)
    grain = h.fbm(H, W, r, s * 3, 3)
    rings = np.sin(Y * 2 * math.pi / 1.6 + 2 * h.fbm(H, W, r, s * 8, 2))   # chaak (wheel) ke nishaan
    alb = alb * (0.9 + 0.1 * grain + 0.05 * rings)[..., None]
    return finish(c, s, alb, 0.5 + 0.1 * grain + 0.06 * rings, k=2.2)


# ------------------------------------------------------------- cloth --------
def weave(H, W, s, r):
    yy, xx = h.grid(H, W)
    t = max(1, s // 2)
    warp = ((xx // t) % 2 == 0)
    over = ((xx // t + yy // t) % 2 == 0)
    thread = np.where(over, np.cos((yy % t + 0.5) / t * math.pi - math.pi / 2), np.cos((xx % t + 0.5) / t * math.pi - math.pi / 2))
    folds = 0.04 * np.sin(xx / (W + 1) * 2 * math.pi * 3 + 2 * h.fbm(H, W, r, s * 6, 2))
    slub = 0.04 * (h.fbm(H, W, r, s * 2, 1, max(1, s // 2)) - 0.5)
    return 0.93 + 0.07 * thread + folds + slub, 0.4 + 0.2 * thread + folds * 3


def banner(c, s):
    H, W, X, Y = _grid(c, s)
    r = c.rng(613)
    tone, hg = weave(H, W, s, r)
    a = c.ref[..., 3]
    pattern = not has(c.base, "base") and (a < 255).any() and sat_of(c.ref[..., :3].astype(float))[a > 0].max(initial=0) < 12
    if pattern:
        # pattern mask: game rang lagata hai, isliye safed kapda + crisp alpha
        alpha = a.repeat(s, 0).repeat(s, 1).astype(float)
        if not has(c.base, "gradient"):
            alpha = np.where(alpha > 0, 255, 0)
        lum = c.ref[..., :3].astype(float).mean(-1).repeat(s, 0).repeat(s, 1)
        alb = np.repeat((np.clip(lum, 200, 255) * tone)[..., None], 3, -1)
        out = finish(c, s, alb, hg, k=1.2, alpha=alpha)
        out[..., :3] = np.clip(np.maximum(out[..., :3], alb * 0.86), 0, 255)   # mask zyada gehra na ho
        return out
    alb, mk = soft_ref(c, s, max(1, s // 2))
    lum = alb @ h.LUM
    wood = (sat_of(alb) > 30) & (lum < 150) & (alb[..., 0] > alb[..., 2])
    metal = (sat_of(alb) < 18) & (lum < 120)
    grain = h.fbm(H, W, r, s * 4, 3, 1)
    t = np.where(wood, 0.85 + 0.3 * grain, np.where(metal, 0.92 + 0.1 * h.fbm(H, W, r, 1, 2, W // 3), tone))
    hgt = np.where(wood, 0.5 + 0.15 * grain, hg)
    return finish(c, s, alb * t[..., None], hgt, spec=0.15, k=1.6)


# -------------------------------------------------------------- wood --------
def wood_ent(c, s):
    """Chest, boat, chest_boat, armor stand: takhte (planks) + grain; dhaat ke hisse alag."""
    H, W, X, Y = _grid(c, s)
    r = c.rng(614)
    alb, mk = soft_ref(c, s, max(1, s // 2))
    lum = alb @ h.LUM; sat = sat_of(alb)
    gold = (alb[..., 0] > 170) & (alb[..., 1] > 130) & (alb[..., 2] < 110) & (sat > 70)
    metal = ((sat < 22) & (lum > 70) & ~has(c.name, "armorstand")) | gold
    stone = (sat < 22) & has(c.name, "armorstand")
    plank = 4.0
    seam = (Y % plank) < 0.28
    warp = h.fbm(H, W, r, s * 6, 2)
    grain = np.sin((Y + 2.2 * warp) * 2 * math.pi / 1.3 + 0.8 * h.fbm(H, W, r, s * 2, 2, 1))
    streak = h.fbm(H, W, r, 1, 2, s * 8)
    row = np.floor(Y / plank)
    rowtone = 0.94 + 0.12 * ((row * 0.618) % 1)
    knot = np.zeros((H, W), bool)
    for _ in range(max(2, (c.W * c.H) // 700)):
        kx, ky = r.uniform(0, c.W), r.uniform(0, c.H)
        knot |= np.hypot((X - kx) / 1.6, Y - ky) < 0.7
    t = rowtone * (0.88 + 0.06 * grain + 0.12 * (streak - 0.5)) * np.where(seam, 0.68, 1.0) * np.where(knot, 0.8, 1.0)
    hgt = 0.55 + 0.06 * grain - 0.35 * seam
    if has(c.name, "bamboo"):
        node = (X % 5.0) < 0.5
        t = (0.9 + 0.1 * np.cos((Y % plank) / plank * 2 * math.pi)) * np.where(node, 0.75, 1.0) * np.where(seam, 0.7, 1.0)
        hgt = 0.5 + 0.2 * np.cos((Y % plank) / plank * 2 * math.pi) - 0.3 * seam
    mt = 0.92 + 0.14 * h.fbm(H, W, r, 1, 2, max(4, W // 3))
    rough = 0.9 + 0.16 * h.fbm(H, W, r, s * 2, 3)
    t = np.where(metal, mt, np.where(stone, rough, t))
    hgt = np.where(metal, 0.7, np.where(stone, 0.5 + 0.1 * rough, hgt))
    alb = alb * t[..., None]
    out = finish(c, s, alb, hgt, spec=0.0, k=2.0)
    if has(c.name, "armorstand"):
        out[int(56 / 64 * H):, :, 3] = 0             # neeche ki khaali (bina UV) patti saaf
    sh = finish(c, s, alb, hgt, spec=0.6, metal=True, k=2.0)
    out[metal] = sh[metal]
    return out


# ------------------------------------------------------------- armor --------
def armor(c, s):
    H, W, X, Y = _grid(c, s)
    r = c.rng(615)
    alb, mk = soft_ref(c, s, max(1, s // 2))
    n = c.name.lower()
    edge = h.dist_inside(mk, maxd=2 * s) / (2 * s)
    if has(n, "leather", "llama_body", "carpet"):
        tone, hg = weave(H, W, s, r) if has(n, "llama_body") else (None, None)
        if tone is None:
            pores = h.fbm(H, W, r, 1, 1); crease = h.ridged(h.fbm(H, W, r, s * 3, 3))
            tone = 0.9 + 0.08 * pores + 0.08 * (crease - 0.5)
            hg = 0.5 + 0.1 * crease
            stitch = (np.abs(edge - 0.45) < 0.08) & (((X + Y) * 2 % 1.0) < 0.5)
            tone = np.where(stitch, 1.18, tone)
        lum = alb @ h.LUM
        if has(n, "llama_body") and lum.mean() < 70:
            alb = alb + 18                                            # kaala/jamuni kapda bhi dikhe
        return finish(c, s, alb * tone[..., None], hg + 0.3 * edge, k=2.0)
    # dhaat: plates, seams, rivets, brushed lines, kinaron par roshni
    P = 4.0
    seam = ((Y % P) < 0.22)
    rivet = (np.hypot((X % P) - 0.9, (Y % P) - 0.9) < 0.32)
    brush = h.fbm(H, W, r, 1, 2, max(4, W // 4))
    dent = h.fbm(H, W, r, s * 3, 2)
    tone = (0.9 + 0.12 * brush + 0.06 * (dent - 0.5)) * np.where(seam, 0.7, 1.0) * np.where(rivet, 1.2, 1.0)
    if has(n, "diamond"):
        facet = np.abs(((X + Y) % 3.0) - 1.5) / 1.5
        tone = tone * (0.92 + 0.14 * facet)
        alb = np.where(((alb[..., 1] > alb[..., 0] + 20)[..., None]) | (sat_of(alb) < 40)[..., None], alb,
                       mix(alb, np.array((90, 220, 210.0)), 0.6))               # khaki/olive nahi, cyan
    if has(n, "netherite"):
        tone = tone * (0.95 + 0.08 * h.fbm(H, W, r, s, 2))
    hgt = 0.45 + 0.4 * np.clip(edge, 0, 1) - 0.3 * seam + 0.25 * rivet + 0.05 * dent
    return finish(c, s, alb * tone[..., None], hgt, spec=0.7, metal=True, k=2.4)


# ----------------------------------------------------------- markings -------
def markings(c, s):
    H, W, X, Y = _grid(c, s)
    r = c.rng(616)
    a = (c.ref[..., 3] > 0).repeat(s, 0).repeat(s, 1)
    col = c.ref[..., :3].astype(float).repeat(s, 0).repeat(s, 1)
    tuft = h.fbm(H, W, r, max(1, s // 2), 2, max(1, s // 4))
    t = 0.92 + 0.12 * tuft
    alb = col * t[..., None]
    out = finish(c, s, alb, 0.5 + 0.2 * tuft, k=1.4)
    out[..., 3] = np.where(a, 255, 0)
    return out


def override(c, s):
    n = c.name.lower().replace("\\", "/")
    b = c.base
    if "decorated_pot/" in n:
        return pottery(c, s) if b.endswith("_pottery_pattern") else pot_plain(c, s)
    if "/banner/" in n or "entity/banner" in n or ("/shield/" in n or "entity/shield" in n):
        return banner(c, s)
    if "markings" in n:
        return markings(c, s)
    if has(n, "/chest/", "/boat/", "/chest_boat/", "armorstand", "/raft/", "chest_raft") and not has(b, "ender", "copper"):
        return wood_ent(c, s)
    if has(n, "/equipment/", "models/armor/") and not has(n, "saddle", "harness", "wings"):
        return armor(c, s)
    return None
