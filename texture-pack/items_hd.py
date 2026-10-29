"""FireCraft Remastered HD — tools, weapons, food aur bundles ke apne designs.

Har tool ek "local frame" me banta hai: A = handle ke saath (neeche-baayein se upar-daayein),
B = handle ke perpendicular (upar-baayein taraf positive). Isse sirf ek jagah shape
badalni padti hai aur sab tools ek jaisi direction/roshni me aate hain.
"""
import math
import numpy as np

import generate as g
import generate_hd as h
from shapes_hd import Cv, GOLD, IRON, DARK, WOOD, dye_of

S = h.S
shade, mix, has = g.shade, g.mix, g.has
D = (1 / math.sqrt(2), -1 / math.sqrt(2))          # handle ki direction (upar-daayein)
HANDLE = np.array((118, 82, 46), float)
GRIP = np.array((70, 48, 34), float)


# ---------------------------------------------------------------- tools -----
def head_color(c):
    b = c.base
    for k in ("wooden", "wood", "stone", "iron", "golden", "gold", "diamond", "netherite", "copper"):
        if b.startswith(k + "_"):
            return np.array(g.MATS[k], float), k
    return np.asarray(c.pal.accent, float), "other"


def head_part(v, m, col, mat, hmap=None):
    if mat in ("wooden", "wood"):
        v.part(m, col, wood=True, hmap=hmap, dome=1.2)
    elif mat == "stone":
        v.part(m, col, noise=0.22, hmap=hmap, dome=1.2, spec=0.05)
    else:
        v.part(m, col, spec=0.85, shin=36, metal=True, hmap=hmap, dome=1.2)


def handle(v, ox, oy, L, w=0.62, grip_to=0.0):
    A, B = v.local(ox, oy, *D)
    m = (A > -0.2) & (A < L) & (np.abs(B) < w)
    hm = np.sqrt(np.clip(1 - (B / w) ** 2, 0, 1))
    v.part(m, HANDLE, wood=True, hmap=hm)
    if grip_to:
        wrap = m & (A < grip_to) & (((A * 1.6) % 1.0) < 0.55)
        v.part(wrap, GRIP, hmap=hm * 0.9 + 0.1)
    v.part(v.circ(ox, oy, w * 0.95), shade(HANDLE, 0.8), dome=0.5)
    return A, B


def sword(c):
    v = Cv(c, 30)
    col, mat = head_color(c)
    ox, oy = 2.0, 14.0
    A, B = v.local(ox, oy, *D)
    # pommel + grip (lipti patti)
    v.part(v.circ(ox, oy, 1.05), shade(col, 0.85) if mat not in ("wooden", "wood", "stone") else GOLD,
           metal=True, spec=0.7, dome=0.7)
    grip = (A > 0.7) & (A < 3.9) & (np.abs(B) < 0.6)
    v.part(grip, GRIP, hmap=np.sqrt(np.clip(1 - (B / 0.6) ** 2, 0, 1)))
    v.tint(grip & (((A * 1.7) % 1.0) < 0.35), 0.7)
    # blade: center ridge, nukeela sira, dhaar par chamak
    hw = np.where(A < 12.4, 1.2, 1.2 * np.clip((15.4 - A) / 3.0, 0, 1))
    blade = (A > 4.6) & (A < 15.4) & (np.abs(B) < hw)
    ridge = np.clip(1 - np.abs(B) / np.maximum(hw, 1e-3), 0, 1)
    head_part(v, blade, col, mat, hmap=ridge * 0.9)
    v.tint(blade & (np.abs(B) < 0.22) & (A < 12.0) & (A > 5.6), 0.8)            # fuller (beech ki nali)
    v.tint(blade & (np.abs(B) > hw - 0.38), 1.18)                                  # tez dhaar
    # crossguard (gol sire)
    guard = (A > 3.8) & (A < 4.9) & (np.abs(B) < 3.0)
    guard |= ((A - 4.35) ** 2 + (np.abs(B) - 3.0) ** 2) < 0.55 ** 2
    gc = GOLD if mat in ("wooden", "wood", "stone") else shade(col, 0.78)
    v.part(guard, gc, metal=mat not in ("wooden", "wood"), spec=0.6, dome=0.6)
    return v.done()


def pickaxe(c):
    v = Cv(c, 31)
    col, mat = head_color(c)
    ox, oy, L = 2.4, 13.8, 11.4
    handle(v, ox, oy, L, grip_to=2.6)
    A, B = v.local(ox, oy, *D)
    head = np.zeros_like(A, bool); hm = np.zeros_like(A)
    for t in np.linspace(-1, 1, 161):
        ac = 10.9 + 1.4 * (1 - t * t) - 0.9 * t * t
        bc = 6.1 * t
        rr = 0.32 + 0.98 * (1 - t * t) ** 0.7
        d = np.hypot(A - ac, B - bc)
        m = d < rr
        head |= m
        hm = np.maximum(hm, np.where(m, np.sqrt(np.clip(1 - (d / rr) ** 2, 0, 1)), 0))
    head_part(v, head, col, mat, hmap=hm)
    v.tint(head & (A > 10.9 + 1.4 * (1 - (B / 6.1) ** 2) - 0.9 * (B / 6.1) ** 2 + 0.25), 1.15)   # upar ki dhaar
    collar = (A > 9.7) & (A < 11.3) & (np.abs(B) < 1.0)
    v.part(collar, shade(col, 0.7) if mat not in ("wooden", "wood") else shade(HANDLE, 0.7),
           metal=mat not in ("wooden", "wood", "stone"), spec=0.5, dome=0.6)
    return v.done()


def axe(c):
    v = Cv(c, 32)
    col, mat = head_color(c)
    ox, oy, L = 2.6, 13.6, 11.6
    handle(v, ox, oy, L, grip_to=2.6)
    A, B = v.local(ox, oy, *D)
    # kulhaade ki dhaar: upar chhota sira, neeche lambi "beard", socket ke paas patla gala
    edge_pts = [(12.6, 4.6)] + [(12.6 - 5.6 * k / 12, 4.6 + 0.9 * math.sin(math.pi * k / 12) + 0.5 * k / 12)
                                for k in range(1, 12)] + [(7.0, 5.1)]
    blade = h.poly(A, B, [(9.6, 0.8), (11.0, 0.8), (11.4, 2.2), (12.2, 3.2)] + edge_pts + [(7.6, 3.6), (9.0, 2.2)])
    edge_dist = np.full(A.shape, 9.0)
    for (a0, b0), (a1, b1) in zip(edge_pts[:-1], edge_pts[1:]):
        da, db = a1 - a0, b1 - b0
        t = np.clip(((A - a0) * da + (B - b0) * db) / (da * da + db * db), 0, 1)
        edge_dist = np.minimum(edge_dist, np.hypot(A - (a0 + t * da), B - (b0 + t * db)))
    hm = np.clip(edge_dist / 3.0, 0, 1) ** 0.7
    head_part(v, blade, col, mat, hmap=hm)
    v.tint(blade & (edge_dist < 0.75), 1.22)                                         # tez dhaar
    poll = (A > 9.0) & (A < 11.0) & (B < -0.7) & (B > -2.3)
    head_part(v, poll, shade(col, 0.9), mat)
    collar = (A > 8.8) & (A < 11.2) & (np.abs(B) < 0.95)
    v.part(collar, shade(col, 0.72) if mat not in ("wooden", "wood") else shade(HANDLE, 0.7),
           metal=mat not in ("wooden", "wood", "stone"), spec=0.5, dome=0.6)
    return v.done()


def shovel(c):
    v = Cv(c, 33)
    col, mat = head_color(c)
    ox, oy, L = 2.2, 14.0, 9.4
    handle(v, ox, oy, L, grip_to=2.4)
    A, B = v.local(ox, oy, *D)
    neck = (A > 8.8) & (A < 10.6) & (np.abs(B) < 0.45 + (A - 8.8) * 0.55)
    tip = 13.1
    blade = (A > 10.4) & (np.abs(B) < 2.45) & ((A < tip) | (((A - tip) / 2.45) ** 2 + (B / 2.45) ** 2 < 1))
    dish = 0.55 + 0.35 * (np.abs(B) / 2.45) ** 2                     # beech me gehra, kinare ubhre
    head_part(v, neck, shade(col, 0.85), mat)
    head_part(v, blade, col, mat, hmap=dish)
    v.tint(blade & (np.abs(B) < 0.22) & (A < 12.6), 0.78)             # beech ki ridge
    rim = blade & ~(((np.abs(B) < 2.0) & ((A < tip) | (((A - tip) / 2.0) ** 2 + (B / 2.0) ** 2 < 1)) & (A > 10.8)))
    v.tint(rim, 1.15)
    return v.done()


def hoe(c):
    v = Cv(c, 34)
    col, mat = head_color(c)
    ox, oy, L = 2.4, 13.8, 11.6
    handle(v, ox, oy, L, grip_to=2.6)
    A, B = v.local(ox, oy, *D)
    collar = (A > 10.2) & (A < 11.9) & (np.abs(B) < 0.95)
    arm = (A > 10.5) & (A < 11.8) & (B > 0.8) & (B < 4.4)
    blade = (A > 7.4) & (A < 11.8) & (B > 3.4) & (B < 5.0)
    hm = np.clip((A - 7.4) / 2.0, 0.2, 1)
    head_part(v, arm, shade(col, 0.92), mat)
    head_part(v, blade, col, mat, hmap=hm)
    v.tint(blade & (A < 8.2), 1.2)                                    # tez sira
    v.part(collar, shade(col, 0.72) if mat not in ("wooden", "wood") else shade(HANDLE, 0.7),
           metal=mat not in ("wooden", "wood", "stone"), spec=0.5, dome=0.6)
    return v.done()


TOOLS = {"sword": sword, "pickaxe": pickaxe, "axe": axe, "shovel": shovel, "hoe": hoe}


def tool(c):
    b = c.base
    for k, fn in TOOLS.items():
        if (b.endswith("_" + k) or b.endswith("_" + k + "_item")) and any(b.startswith(m + "_") for m in g.MATS):
            return fn(c)
    return None


# ----------------------------------------------------------------- food -----
RAW_MEAT, COOKED_MEAT, FAT = (196, 70, 70), (128, 72, 40), (236, 222, 206)


def cooked(b):
    return has(b, "cooked", "baked")


def steak(c, raw_col, fat=True, bone=False):
    v = Cv(c, 40)
    body = v.poly([(2.6, 6.2), (6.4, 3.2), (11.6, 3.6), (14.0, 7.0), (12.6, 11.8), (7.4, 13.4), (3.2, 11.0)])
    col = COOKED_MEAT if cooked(c.base) else raw_col
    v.part(body, col, dome=2.6, noise=0.12, spec=0.25 if not cooked(c.base) else 0.1)
    if fat:
        rim = body & ~v.poly([(3.6, 6.6), (6.8, 4.2), (11.2, 4.6), (13.0, 7.2), (11.8, 11.0), (7.4, 12.4), (4.0, 10.4)])
        v.tint(rim, 1.0)
        v.part(rim, shade(FAT, 0.95) if not cooked(c.base) else (190, 140, 90), dome=0.7)
        for _ in range(4):   # marbling
            x0, y0 = v.r.uniform(5, 11), v.r.uniform(5, 11)
            v.tint(body & v.seg(x0, y0, x0 + v.r.uniform(-2, 2), y0 + v.r.uniform(-2, 2), 0.25), 1.25)
    if cooked(c.base):
        for k in range(3):   # grill ki lakeerein
            v.tint(body & v.seg(4 + k * 3, 4, 2 + k * 3, 13, 0.32), 0.62)
    if bone:
        v.part(v.seg(10.5, 9.5, 14.2, 13.2, 0.75), (236, 232, 218), dome=0.6)
        v.part(v.circ(14.4, 13.4, 1.0), (236, 232, 218), dome=0.6)
    return v.done()


def drumstick(c, raw_col):
    v = Cv(c, 41)
    col = COOKED_MEAT if cooked(c.base) else raw_col
    v.part(v.seg(10.6, 10.6, 14.0, 14.0, 0.8), (236, 232, 218), dome=0.6)
    for dx, dy in ((0.9, -0.6), (-0.6, 0.9)):
        v.part(v.circ(14.2 + dx, 14.2 + dy, 0.9), (236, 232, 218), dome=0.6)
    v.part(v.ell(7.2, 7.2, 5.6, 4.0, math.pi / 4), col, dome=2.6, noise=0.12, spec=0.15)
    if cooked(c.base):
        v.tint(v.ell(6.2, 6.2, 2.4, 1.6, math.pi / 4), 1.2)
    return v.done()


def fish(c, col, stripes=False, round_=False):
    v = Cv(c, 42)
    if cooked(c.base):
        col = (186, 120, 70)
    if round_:   # pufferfish
        body = v.circ(8, 8.4, 5.6)
        v.part(body, col, dome=4, noise=0.1)
        for k in range(12):
            a = k * math.pi / 6
            v.part(v.seg(8 + 5.2 * math.cos(a), 8.4 + 5.2 * math.sin(a), 8 + 6.8 * math.cos(a), 8.4 + 6.8 * math.sin(a), 0.3),
                   shade(col, 0.8), dome=0.3)
        v.part(v.circ(5.6, 7.0, 0.9), (250, 250, 250), dome=0.5); v.part(v.circ(5.4, 7.0, 0.45), DARK, flat=True)
        return v.done()
    body = v.ell(7.0, 8.4, 5.4, 3.0, -0.25)
    tail = v.poly([(11.6, 7.2), (15.2, 4.6), (15.2, 11.6)])
    v.part(tail, shade(col, 0.85), dome=0.8)
    v.part(body, col, dome=2.6, noise=0.08, spec=0.35)
    v.tint(body & (v.Y > 8.8 + (v.X - 7) * 0.25), 1.18)                 # halka pet
    v.part(v.poly([(6.0, 5.6), (8.6, 3.8), (9.4, 5.8)]), shade(col, 0.82), dome=0.5)
    if stripes:
        for x in (5.4, 8.8):
            v.part(body & (np.abs(v.X - x) < 0.6), (250, 250, 246), dome=0.8)
    yy = v.Y - 8.4 - (v.X - 7) * -0.25
    v.tint(body & (np.abs(((v.X * 1.4 + yy * 2) % 1.6) - 0.8) < 0.12), 0.88)   # scales
    if not cooked(c.base):
        v.part(v.circ(3.4, 7.6, 0.85), (250, 250, 250), dome=0.5); v.part(v.circ(3.2, 7.6, 0.42), DARK, flat=True)
    else:
        for k in range(2):
            v.tint(body & v.seg(4 + k * 3, 6, 3 + k * 3, 11, 0.3), 0.65)
    return v.done()


def apple(c, golden=False, enchanted=False):
    v = Cv(c, 43)
    col = GOLD if golden else (206, 32, 36)
    body = v.circ(6.4, 9.4, 4.6) | v.circ(9.6, 9.4, 4.6) | v.circ(8, 11.2, 4.0)
    v.part(body, col, dome=4.5, spec=0.75 if golden else 0.45, metal=golden, shin=30)
    v.tint(body & v.ell(8, 5.4, 1.2, 0.8), 0.6)
    v.part(v.seg(8, 5.6, 8.8, 2.0, 0.42), (96, 64, 38), dome=0.4)
    v.part(v.ell(10.6, 3.0, 2.0, 0.9, -0.5), (90, 160, 60) if not golden else shade(GOLD, 0.85), dome=0.6, metal=golden)
    if enchanted:
        v.tint(body & (np.abs(((v.X + v.Y) % 3.0) - 1.5) < 0.35), 1.25)
    return v.done()


def bread(c):
    v = Cv(c, 44)
    loaf = v.ell(8, 9, 6.8, 3.8, -0.35)
    v.part(loaf, (200, 140, 64), dome=3.5, noise=0.1)
    v.tint(loaf & (v.Y < 8.6 - (v.X - 8) * 0.35), 1.12)
    for k in range(3):
        x = 5 + k * 3
        v.part(loaf & v.seg(x - 0.8, 7.4 - (x - 8) * 0.35 + 0.8, x + 0.8, 7.4 - (x - 8) * 0.35 - 0.8, 0.35), (240, 214, 160), flat=True)
    return v.done()


def round_food(c, col, kind):
    v = Cv(c, 45)
    r = v.r
    if kind == "cookie":
        body = v.circ(8, 8, 5.6)
        v.part(body, (198, 132, 66), dome=2.5, noise=0.12)
        for _ in range(6):
            v.part(v.circ(r.uniform(4.5, 11.5), r.uniform(4.5, 11.5), 0.7), (70, 40, 26), dome=0.4)
    elif kind == "pie":
        body = v.ell(8, 9.6, 6.6, 3.8)
        v.part(body, (220, 160, 80), dome=2.2)
        v.part(v.ell(8, 9.0, 5.2, 2.6), (206, 110, 40), dome=1.2)
        for k in range(4):
            v.tint(v.ell(8, 9.0, 5.2, 2.6) & v.seg(3.4 + k * 3, 7, 4.4 + k * 3, 11, 0.3), 1.25)
    elif kind == "cake":
        v.part(v.ell(8, 12.2, 7.0, 2.4), (238, 236, 228), dome=1.0)
        side = v.rect(1.6, 6.4, 14.4, 12.2)
        v.part(side, (200, 120, 70), dome=1.0)
        v.part(v.ell(8, 6.4, 6.4, 2.2), (246, 244, 236), dome=1.4)
        v.part(side & (v.Y < 8.2) & (np.sin(v.X * 2.2) > -0.2), (246, 244, 236), dome=0.6)
        for x in (5, 8, 11):
            v.part(v.circ(x, 5.6, 0.75), (210, 30, 40), dome=0.6, spec=0.5)
    return v.done()


def tuber(c, col, eyes=True, golden=False, carrot=False):
    v = Cv(c, 46)
    if carrot:
        body = v.poly([(3.0, 13.6), (4.4, 12.6), (12.0, 4.6), (13.0, 6.0)]) | v.ell(11.8, 5.2, 2.0, 1.6, -0.78)
        v.part(body, col, dome=1.6, metal=golden, spec=0.8 if golden else 0.1)
        for k in range(4):
            v.tint(body & v.seg(5 + k * 1.8, 12.6 - k * 1.8 + 0.6, 6 + k * 1.8, 13.4 - k * 1.8, 0.25), 0.78)
        for a in (-1.9, -1.3, -0.7):
            v.part(v.seg(12.6, 3.8, 12.6 + 3.0 * math.cos(a), 3.8 + 3.0 * math.sin(a), 0.4),
                   shade(GOLD, 0.8) if golden else (70, 150, 50), dome=0.3, metal=golden)
        return v.done()
    body = v.ell(8, 8.6, 5.6, 4.0, -0.4)
    v.part(body, col, dome=3.5, noise=0.14)
    if eyes:
        for _ in range(4):
            v.tint(body & v.circ(v.r.uniform(5, 11), v.r.uniform(6, 11), 0.4), 0.6)
    return v.done()


def bowl_food(c, fill):
    v = Cv(c, 47)
    v.part(v.ell(8, 8.0, 6.8, 2.2), fill if fill is not None else (70, 50, 34), dome=0.8 if fill is not None else 0.3, noise=0.12)
    bowl = v.poly([(1.2, 8.0), (14.8, 8.0), (12.4, 12.6), (3.6, 12.6)]) & ~v.ell(8, 8.0, 6.6, 2.0)
    v.part(bowl, WOOD, wood=True, dome=1.6)
    v.part(v.rect(5.6, 12.4, 10.4, 13.6), shade(WOOD, 0.8), wood=True, dome=0.5)
    if fill is not None:
        for _ in range(3):
            v.part(v.ell(8 + v.r.uniform(-4, 4), 8 + v.r.uniform(-1, 1), 0.9, 0.5), shade(fill, 0.7), dome=0.4)
    return v.done()


def berries(c, col, glow=False):
    v = Cv(c, 48)
    v.part(v.seg(8, 1.6, 8, 6, 0.35) | v.seg(8, 4.4, 4.6, 7.6, 0.3) | v.seg(8, 4.4, 11.6, 7.4, 0.3), (70, 110, 40), dome=0.3)
    for x, y in ((4.6, 9.4), (8.0, 8.6), (11.4, 9.2), (6.2, 12.2), (9.8, 12.0)):
        v.part(v.circ(x, y, 1.8), col, dome=1.6, spec=0.6, glow=glow)
        v.part(v.circ(x - 0.6, y - 0.6, 0.5), shade(col, 1.5), flat=True)
    return v.done()


def melon_slice(c, glister=False):
    v = Cv(c, 49)
    th = np.arctan2(v.Y - 3.0, v.X - 8.0); rr = np.hypot(v.X - 8, v.Y - 3.0)
    wedge = (rr < 11.4) & (th > 0.55) & (th < 2.6)
    v.part(wedge & (rr > 10.0), GOLD if glister else (60, 140, 50), dome=0.6, metal=glister, spec=0.8 if glister else 0.1)
    v.part(wedge & (rr > 9.2) & (rr <= 10.0), (220, 230, 170), flat=True)
    flesh = wedge & (rr <= 9.2)
    v.part(flesh, (232, 60, 66), dome=2.0, noise=0.08)
    for k in range(5):
        a = 0.8 + k * 0.4
        v.part(v.ell(8 + 6.0 * math.cos(a), 3 + 6.0 * math.sin(a), 0.5, 0.28, a), DARK, flat=True)
    return v.done()


def misc_food(c):
    v = Cv(c, 50)
    b = c.base
    if has(b, "spider_eye"):
        fer = has(b, "fermented")
        v.part(v.circ(8, 9, 5.0), (150, 30, 40) if not fer else (120, 70, 50), dome=4, spec=0.6)
        v.part(v.circ(7.0, 8.0, 1.8), (240, 220, 210), dome=1.2)
        v.part(v.circ(7.0, 8.0, 0.8), DARK, flat=True)
        if fer:
            v.part(v.ell(10.4, 5.0, 3.0, 1.4), (200, 60, 50), dome=1.0)
        return v.done()
    if has(b, "rotten_flesh"):
        body = v.poly([(3, 6), (7, 3), (12, 4), (14, 9), (10, 13), (4, 12)])
        v.part(body, (130, 110, 70), dome=2.5, noise=0.2)
        for _ in range(4):
            v.tint(body & v.circ(v.r.uniform(5, 11), v.r.uniform(5, 11), 1.0), 0.65)
        v.part(v.seg(5, 9, 11, 7, 0.25), (60, 50, 40), flat=True)
        return v.done()
    if has(b, "dried_kelp"):
        roll = v.ell(8, 8, 6.2, 3.0, -0.6)
        v.part(roll, (54, 70, 36), dome=2.2, noise=0.12)
        for k in range(4):
            v.tint(roll & v.seg(4 + k * 2.4, 12 - k * 2.4, 6.4 + k * 2.4, 13 - k * 2.4, 0.25), 0.7)
        return v.done()
    if has(b, "chorus"):
        popped = has(b, "popped")
        col = (196, 160, 210) if popped else (140, 90, 150)
        for x, y, rr in ((8, 8, 4.4), (5.6, 6.0, 2.0), (10.6, 6.0, 2.0), (6.0, 11.4, 1.8), (11, 11, 2.0)):
            v.part(v.circ(x, y, rr), shade(col, v.r.uniform(0.9, 1.1)), dome=2.0, noise=0.1)
        return v.done()
    if has(b, "honey_bottle"):
        v.part(v.ell(8, 10.4, 4.8, 4.2), (240, 160, 40), dome=3, spec=0.6)
        v.part(v.rect(6.6, 2.6, 9.4, 6.6), (220, 230, 236), dome=0.8, spec=0.7)
        v.part(v.rect(6.2, 1.6, 9.8, 2.8), (160, 110, 60), wood=True, dome=0.5)
        v.tint(v.ell(6.4, 9.0, 1.2, 2.0), 1.35)
        return v.done()
    return None


def food(c):
    b = c.base
    if has(b, "spawn_egg") or has(b, "seeds") or has(b, "bucket") or b.startswith(("music_disc", "record_")):
        return None
    if has(b, "beef") or b in ("steak",):
        return steak(c, RAW_MEAT)
    if has(b, "porkchop"):
        return steak(c, (232, 140, 140))
    if has(b, "mutton"):
        return steak(c, (196, 60, 64), bone=True)
    if has(b, "chicken") and not has(b, "spawn"):
        return drumstick(c, (238, 190, 170))
    if has(b, "rabbit") and not has(b, "stew", "foot", "hide", "spawn"):
        return drumstick(c, (210, 130, 120))
    if has(b, "pufferfish"):
        return fish(c, (230, 190, 40), round_=True)
    if has(b, "tropical_fish") or has(b, "clownfish"):
        return fish(c, (240, 120, 30), stripes=True)
    if has(b, "salmon"):
        return fish(c, (190, 70, 60))
    if b in ("cod", "cooked_cod", "fish_raw", "fish_cooked") or has(b, "fish_raw", "fish_cooked"):
        return fish(c, (190, 168, 130))
    if has(b, "golden_apple") or has(b, "apple_golden"):
        return apple(c, golden=True, enchanted=has(b, "enchanted"))
    if b == "apple":
        return apple(c)
    if b == "bread":
        return bread(c)
    if b == "cookie":
        return round_food(c, None, "cookie")
    if has(b, "pumpkin_pie"):
        return round_food(c, None, "pie")
    if b == "cake":
        return round_food(c, None, "cake")
    if has(b, "golden_carrot") or has(b, "carrot_golden"):
        return tuber(c, GOLD, golden=True, carrot=True)
    if b == "carrot":
        return tuber(c, (240, 130, 30), carrot=True)
    if has(b, "poisonous_potato") or has(b, "potato_poisonous"):
        return tuber(c, (170, 190, 90))
    if has(b, "baked_potato") or has(b, "potato_baked"):
        return tuber(c, (214, 160, 70), eyes=False)
    if b == "potato":
        return tuber(c, (200, 160, 96))
    if b == "beetroot":
        v = Cv(c, 51)
        v.part(v.circ(8, 10, 4.6) | v.poly([(6.6, 13.6), (9.4, 13.6), (8, 15.8)]), (150, 30, 50), dome=3.5, spec=0.3)
        for a in (-2.0, -1.57, -1.1):
            v.part(v.seg(8, 6, 8 + 4 * math.cos(a), 6 + 4 * math.sin(a), 0.5), (80, 140, 50), dome=0.4)
        return v.done()
    if has(b, "glistering_melon") or has(b, "melon_speckled"):
        return melon_slice(c, glister=True)
    if has(b, "melon_slice") or b == "melon":
        return melon_slice(c)
    if has(b, "sweet_berries"):
        return berries(c, (200, 30, 50))
    if has(b, "glow_berries"):
        return berries(c, (255, 176, 50), glow=True)
    if has(b, "mushroom_stew"):
        return bowl_food(c, (200, 160, 120))
    if has(b, "rabbit_stew"):
        return bowl_food(c, (150, 96, 50))
    if has(b, "beetroot_soup"):
        return bowl_food(c, (170, 30, 40))
    if has(b, "suspicious_stew"):
        return bowl_food(c, (140, 150, 90))
    if b == "bowl":
        return bowl_food(c, None)
    if b in ("egg", "blue_egg", "brown_egg"):
        v = Cv(c, 52)
        col = {"blue_egg": (150, 196, 220), "brown_egg": (180, 130, 90)}.get(b, (240, 232, 214))
        v.part(v.ell(8, 8.6, 4.4, 5.6), col, dome=4, spec=0.3, noise=0.05)
        return v.done()
    return misc_food(c)


# --------------------------------------------------------------- bundles ----
def bundle(c):
    b = c.base
    if not has(b, "bundle"):
        return None
    v = Cv(c, 60)
    col = dye_of(b, np.array((160, 110, 64), float))
    if has(b, "open_back"):
        v.part(v.ell(8, 7.0, 6.0, 2.4), shade(col, 0.6), dome=0.8)
        v.part(v.ell(8, 7.0, 6.0, 2.4) & ~v.ell(8, 7.4, 5.0, 1.6), col, dome=0.6)
        return v.done()
    if has(b, "open_front"):
        sack = v.poly([(2.0, 8.0), (14.0, 8.0), (13.2, 13.6), (2.8, 13.6)])
        v.part(sack, col, dome=2.2, noise=0.12)
        v.part(v.seg(2.0, 8.0, 14.0, 8.0, 0.55), shade(col, 1.15), dome=0.5)
        return v.done()
    sack = v.circ(8, 10.0, 5.2) | v.poly([(4.6, 7.2), (11.4, 7.2), (9.6, 4.4), (6.4, 4.4)])
    v.part(sack, col, dome=3.6, noise=0.12)
    v.tint(sack & (np.abs(np.sin(np.arctan2(v.Y - 4.6, v.X - 8) * 6)) < 0.12) & (v.Y > 6), 0.82)   # silwatein
    v.part(v.rect(5.6, 3.0, 10.4, 4.6), shade(col, 1.1), dome=0.8)
    v.part(v.seg(5.8, 6.2, 10.2, 6.2, 0.38), (230, 210, 170), dome=0.4)                        # rassi
    v.part(v.circ(10.6, 6.6, 0.7), (230, 210, 170), dome=0.4)
    return v.done()


def coal(c):
    """Pass 2: koyle ki dheliyan — har dheli ke pehloo (facets) aur halki chamak."""
    v = Cv(c, 61)
    col = {"coal": (46, 44, 48), "charcoal": (58, 44, 34), "flint": (70, 72, 78)}[c.base]
    r = v.r
    for x, y, rr in ((8.0, 8.4, 4.6), (4.8, 11.2, 2.4), (11.6, 11.4, 2.2), (11.2, 5.0, 2.0)):
        pts = [(x + rr * r.uniform(0.75, 1.05) * math.cos(a), y + rr * r.uniform(0.75, 1.05) * math.sin(a))
               for a in np.linspace(0, 2 * math.pi, 8, endpoint=False) + r.uniform(0, 0.6)]
        m = v.poly(pts)
        ang = np.arctan2(v.Y - y, v.X - x)
        sec = ((ang + math.pi) / (2 * math.pi) * 6).astype(int) % 6
        tilt = r.uniform(0.3, 1.0, 6)
        hm = np.clip(1 - np.hypot(v.X - x, v.Y - y) / rr, 0, 1) * tilt[sec]
        v.part(m, shade(col, r.uniform(0.9, 1.15)), hmap=hm, spec=0.35, shin=40, noise=0.15)
    return v.done()


def door(c):
    """Pass 3: darwaze ka item — takhtiyan, khidki, qabze aur handle (iron/copper par rivets)."""
    b = c.base
    if not (b.endswith("_door") or b.startswith("door_")) or has(b, "trapdoor"):
        return None
    v = Cv(c, 62)
    metal = has(b, "iron", "copper")
    col = c.pal.main
    body = v.rect(3.6, 0.8, 12.4, 15.2)
    if metal:
        v.part(body, col, metal=True, spec=0.6, dome=1.2, noise=0.1)
    else:
        v.part(body, col, wood=True, dome=1.0)
        for x in (5.8, 8.0, 10.2):
            v.tint(body & (np.abs(v.X - x) < 0.16), 0.7)
    v.tint(body & ~v.rect(4.2, 1.4, 11.8, 14.6), 0.8)
    win = v.rect(5.2, 2.6, 10.8, 6.8)
    v.part(win, (150, 190, 220), spec=0.5, dome=0.5)
    v.part(win & ((np.abs(v.X - 8) < 0.25) | (np.abs(v.Y - 4.7) < 0.25)), shade(col, 0.8), dome=0.3)
    for y in (3.0, 12.4):
        v.part(v.rect(3.6, y, 5.2, y + 1.2), (70, 70, 76), metal=True, spec=0.6, dome=0.4)
    v.part(v.circ(10.6, 9.0, 0.7), GOLD if not metal else (80, 80, 86), metal=True, spec=0.8, dome=0.5)
    if metal:
        for y in (8.0, 10.0):
            for x in (5.0, 11.0):
                v.part(v.circ(x, y, 0.4), shade(col, 0.8), metal=True, spec=0.6, dome=0.4)
    return v.done()


def candle(c):
    """Mom ka belan: cylinder shading, batti, behte qatre; 16 dye rang (Java red_candle / Bedrock candle_red)."""
    v = Cv(c, 63)
    col = dye_of(c.base, np.array((232, 224, 200), float))
    body = v.rect(5.6, 4.6, 10.4, 14.6) | v.ell(8, 4.6, 2.4, 0.9) | v.ell(8, 14.6, 2.4, 0.9)
    cyl = np.clip(1 - ((v.X - 7.4) / 2.6) ** 2, 0, 1) ** 0.5             # gol belan ki roshni
    v.part(body, col, hmap=cyl, spec=0.35, shin=30, noise=0.04)
    v.part(v.ell(8, 4.6, 2.2, 0.7), shade(col, 1.12), dome=0.4, spec=0.3)
    for x, L in ((6.2, 3.0), (9.6, 4.4), (8.0, 2.0)):                    # behte qatre
        v.part(v.seg(x, 4.8, x, 4.8 + L, 0.42) | v.circ(x, 4.8 + L, 0.55), shade(col, 1.05), dome=0.5, spec=0.4)
    v.part(v.seg(8, 4.4, 8.3, 2.2, 0.28), (40, 34, 30), dome=0.3)        # batti
    return v.done()


def ender_pearl(c):
    """Gehra teal gola, andar bhanwar (swirl) aur chamak."""
    v = Cv(c, 64)
    eye = has(c.base, "eye")
    col = (40, 110, 70) if eye else (24, 92, 92)
    ball = v.circ(8, 8, 5.8)
    v.part(ball, col, dome=5, spec=0.8, shin=50)
    rr = np.hypot(v.X - 8, v.Y - 8); th = np.arctan2(v.Y - 8, v.X - 8)
    swirl = ball & (np.sin(th * 2 + rr * 1.3) > 0.55) & (rr < 4.8)
    v.tint(swirl, 1.45)
    if eye:
        v.part(v.ell(8, 8, 1.2, 3.0), (20, 24, 18), dome=0.6)
    else:
        v.part(v.circ(8, 8, 1.6), (120, 230, 210), glow=True)
    v.part(v.ell(6.2, 5.6, 1.4, 0.8, -0.6), (230, 250, 245), glow=True)
    return v.done()


def flower_pot(c):
    """Terracotta gamla: kinara, mitti, pakki mitti ka rang."""
    v = Cv(c, 65)
    clay = (176, 92, 60)
    v.part(v.poly([(4.2, 7.0), (11.8, 7.0), (10.8, 14.6), (5.2, 14.6)]), clay, dome=2.4, noise=0.1)
    v.tint(v.rect(4, 12.4, 12, 13.0), 0.8)
    v.part(v.rect(3.2, 4.8, 12.8, 7.2), shade(clay, 1.08), dome=1.0, noise=0.1)
    v.part(v.ell(8, 5.2, 4.4, 0.9), (70, 46, 30), dome=0.5, noise=0.2)
    return v.done()


GLASS = np.array((196, 226, 238), float)


def bottle_shape(v, kind):
    """Teen tarah ki sheeshi: drink (gol), splash (chapti + grip), lingering (lambi gol)."""
    if kind == "splash":
        body = v.ell(8, 10.6, 5.2, 4.0); neck = v.rect(6.6, 3.8, 9.4, 7.2)
    elif kind == "lingering":
        body = v.ell(8, 10.0, 4.4, 4.8); neck = v.rect(6.8, 3.4, 9.2, 6.0)
    else:
        body = v.circ(8, 10.4, 4.6); neck = v.rect(6.8, 3.6, 9.2, 6.6)
    return body, neck


def potion(c):
    """Java: potion/splash/lingering = khali sheeshi (andar transparent), potion_overlay = tint hone wala
    maya. Bedrock: potion_bottle_<effect> = rangeen maya wali poori sheeshi."""
    b = c.base
    kind = "splash" if has(b, "splash") else ("lingering" if has(b, "lingering") else "drink")
    v = Cv(c, 66)
    body, neck = bottle_shape(v, kind)
    if b == "potion_overlay":                                   # sirf maya (grey -> potion rang se tint)
        liquid = body & (v.Y > 8.0)
        v.part(liquid, (200, 200, 200), dome=2.2, spec=0.5, noise=0.05)
        v.tint(liquid & v.ell(6.6, 10.4, 1.0, 1.8), 1.25)
        return v.done(outline=False)
    liquid_col = None
    if b.startswith("potion_bottle") and not has(b, "empty"):
        liquid_col = c.pal.accent if not c.pal.grey else np.array((200, 200, 200.0))
    if b in ("experience_bottle",):
        liquid_col = np.array((150, 230, 60.0))
    if b in ("dragon_breath", "dragons_breath"):
        liquid_col = np.array((236, 120, 200.0))
    if b == "ominous_bottle":
        liquid_col = np.array((40, 50, 60.0))
    shell = (body & ~v.ell(8, body_center(kind), *inner_r(kind))) | neck
    v.part(shell, GLASS, spec=0.8, shin=60, dome=0.6, noise=0.03)
    if liquid_col is not None:
        liquid = body & ~shell & (v.Y > 8.0)
        v.part(liquid, liquid_col, dome=2.0, spec=0.6, noise=0.06,
               glow=b in ("experience_bottle",))
        if b == "experience_bottle":
            for _ in range(6):
                v.part(v.circ(v.r.uniform(5, 11), v.r.uniform(9, 13.5), 0.45), (250, 255, 200), glow=True)
    v.part(v.rect(6.4, 3.0, 9.6, 4.0), shade(GLASS, 0.9), spec=0.6, dome=0.4)                   # lip
    v.part(v.rect(6.9, 1.2, 9.1, 3.2), (150, 104, 60), wood=True, dome=0.5)                    # cork
    if kind == "splash":
        v.part(v.seg(4.2, 8.8, 11.8, 8.8, 0.35), shade(GLASS, 0.8), dome=0.3)
    v.part(v.ell(5.8, 9.6, 0.7, 1.6, 0.3), (250, 252, 255), glow=True)                        # chamak
    return v.done()


def body_center(kind):
    return {"splash": 10.6, "lingering": 10.0}.get(kind, 10.4)


def inner_r(kind):
    return {"splash": (4.4, 3.2), "lingering": (3.6, 4.0)}.get(kind, (3.8, 3.8))


def arrow(c):
    """Teer: lakdi ki dandi, steel ka phal, pankh. tipped_arrow_head = sirf phal (grey, tint hota hai)."""
    b = c.base
    v = Cv(c, 67)
    A, B = v.local(2.6, 13.4, *D)
    head = (A > 11.0) & (A < 14.6) & (np.abs(B) < 1.5 * np.clip((14.6 - A) / 2.6, 0, 1))
    head &= ~((A < 11.8) & (np.abs(B) > 0.5))
    if b == "tipped_arrow_head":
        v.part(head, (200, 200, 200), metal=True, spec=0.6, dome=0.6)
        return v.done(outline=False)
    shaft = (A > 0.4) & (A < 11.2) & (np.abs(B) < 0.42)
    v.part(shaft, (150, 110, 64), wood=True, hmap=np.sqrt(np.clip(1 - (B / 0.42) ** 2, 0, 1)))
    fl = (A > 0.2) & (A < 3.6) & (np.abs(B) < 0.5 + (3.6 - A) * 0.45) & (np.abs(B) > 0.35)
    v.part(fl, (236, 236, 232), dome=0.6, noise=0.1)
    v.tint(fl & (((A * 2.5) % 1.0) < 0.3), 0.85)
    if b != "tipped_arrow_base":
        hc = GOLD if has(b, "spectral") else np.array((176, 180, 188.0))
        v.part(head, hc, metal=True, spec=0.8, dome=0.7, glow=False)
        if has(b, "spectral"):
            v.tint(head, 1.15)
    return v.done()


def special(c):
    if c.H == 16 and c.W == 16:
        b = c.base
        if b in ("potion", "splash_potion", "lingering_potion", "potion_overlay", "glass_bottle", "experience_bottle",
                 "dragon_breath", "dragons_breath", "ominous_bottle") or b.startswith("potion_bottle"):
            return potion(c)
        if b in ("arrow", "spectral_arrow", "tipped_arrow_base", "tipped_arrow_head") or \
                (b.startswith("tipped_arrow") and not has(b, "base", "head")):
            return arrow(c)
        if has(b, "candle") and not has(b, "cake"):
            return candle(c)
        if b in ("ender_pearl", "ender_eye", "ender_eye_item"):
            return ender_pearl(c)
        if b in ("flower_pot", "flower_pot_item"):
            return flower_pot(c)
        d = door(c)
        if d is not None:
            return d
    if c.H == 16 and c.W == 16 and c.base in ("coal", "charcoal", "flint"):
        return coal(c)
    if c.H != 16 or c.W != 16:
        return None
    for fn in (tool, bundle, food):
        out = fn(c)
        if out is not None:
            return out
    return None
