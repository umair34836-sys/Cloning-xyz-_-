"""FireCraft Remastered HD — A-Z item review ke baad baqi items ki apni vector designs.

harness, lanterns, overlays, redstone parts, light items, signs, banner patterns, raw materials,
mob drops, paude ke items aur crafted cheezein — jo pehle generic "blob" fallback se bante the.
"""
import math, re
import numpy as np

import generate as g
import generate_hd as h
from shapes_hd import Cv, GOLD, IRON, DARK, WOOD, PAPER, dye_of

S = h.S
shade, mix, has = g.shade, g.mix, g.has
LEATHER = np.array((140, 92, 56), float)
BRASS = np.array((214, 170, 70), float)


def lump(v, cx, cy, rr, col, n=8, spec=0.3, salt=0):
    r = v.r
    pts = [(cx + rr * r.uniform(0.75, 1.05) * math.cos(a), cy + rr * r.uniform(0.75, 1.05) * math.sin(a))
           for a in np.linspace(0, 2 * math.pi, n, endpoint=False) + r.uniform(0, 0.5)]
    m = v.poly(pts)
    ang = np.arctan2(v.Y - cy, v.X - cx)
    sec = ((ang + math.pi) / (2 * math.pi) * 6).astype(int) % 6
    tilt = r.uniform(0.35, 1.0, 6)
    hm = np.clip(1 - np.hypot(v.X - cx, v.Y - cy) / rr, 0, 1) * tilt[sec]
    v.part(m, col, hmap=hm, spec=spec, shin=36, noise=0.14)
    return m


# ------------------------------------------------------------ equipment -----
def harness(c):
    v = Cv(c, 200)
    col = dye_of(c.base, LEATHER)
    for x0, x1 in ((4.0, 3.2), (12.0, 12.8)):
        v.part(v.seg(x0, 8.0, x1, 14.2, 0.55), shade(LEATHER, 0.8), dome=0.4)
        v.part(v.rect(x1 - 0.9, 12.6, x1 + 0.9, 13.8) & ~v.rect(x1 - 0.4, 12.9, x1 + 0.4, 13.5), BRASS, metal=True, spec=0.8, dome=0.4)
    cushion = v.ell(8, 7.6, 6.4, 3.0)
    v.part(cushion, col, dome=2.4, noise=0.08, spec=0.15)
    v.tint(cushion & (np.abs(v.X - 8) < 0.25), 0.75)                                  # silai
    v.tint(cushion & (np.abs(((v.X - 8) ** 2 / 30 + (v.Y - 7.6) ** 2 / 6) - 1) < 0.12), 0.8)
    v.part(v.ell(8, 4.4, 2.6, 1.2), (60, 70, 90), spec=0.9, shin=60, dome=0.8)        # goggle lens
    v.part(v.ell(7.2, 4.0, 0.8, 0.35), (220, 240, 255), glow=True)
    return v.done()


def lantern(c):
    v = Cv(c, 201)
    b = c.base
    soul = has(b, "soul")
    metal = c.pal.main if has(b, "copper") else np.array((70, 72, 80.0))
    flame = (110, 230, 240) if soul else (255, 190, 80)
    v.part(v.rect(4.6, 5.6, 11.4, 13.4), shade(np.array(flame, float), 0.55), glow=True)
    core = v.ell(8, 9.6, 2.0, 2.8)
    v.part(core, flame, glow=True)
    v.part(v.ell(8, 9.2, 0.9, 1.4), (255, 255, 230), glow=True)
    for x in (4.6, 7.4, 10.4):
        v.part(v.rect(x, 5.6, x + 1.0, 13.4), metal, metal=True, spec=0.6, dome=0.4)
    v.part(v.rect(3.8, 4.4, 12.2, 5.8), metal, metal=True, spec=0.6, dome=0.5)
    v.part(v.rect(3.8, 13.2, 12.2, 14.8), metal, metal=True, spec=0.6, dome=0.5)
    v.part(v.rect(5.6, 3.4, 10.4, 4.6), shade(metal, 0.9), metal=True, dome=0.4)
    ring = v.circ(8, 2.2, 1.4) & ~v.circ(8, 2.2, 0.7)
    v.part(ring, metal, metal=True, spec=0.6, dome=0.3)
    return v.done()


def wolf_armor(c, overlay=False):
    v = Cv(c, 202)
    plate = v.poly([(2.0, 5.0), (8.0, 3.2), (14.0, 5.0), (13.4, 11.6), (8.0, 13.4), (2.6, 11.6)])
    if overlay:
        for k in range(3):
            band = plate & (np.abs(v.Y - (6 + k * 2.6)) < 0.55)
            v.part(band, (200, 200, 200), dome=0.4)
        return v.done(outline=False)
    v.part(plate, (150, 110, 90), dome=2.0, spec=0.3)
    for k in range(4):
        v.tint(plate & (np.abs(v.Y - (5 + k * 2.4)) < 0.25), 0.7)
    v.part(v.circ(8, 4.6, 0.6), BRASS, metal=True, spec=0.8)
    return v.done()


def horse_overlay(c):
    """leather_horse_armor_overlay: shapes_hd.horse_armor ke kinare par trim + buckle (bina tint)."""
    v = Cv(c, 203)
    pts = [(3.0, 4.6), (8.0, 2.2), (12.6, 3.4), (14.0, 6.2), (12.6, 9.0), (9.6, 13.6), (6.8, 13.8), (6.2, 10.0), (4.0, 7.6)]
    head = v.poly(pts)
    inner = h.dist_inside(head, 4) <= 1.2
    v.part(head & inner, (120, 84, 50), dome=0.4)
    v.part(v.rect(8.4, 8.6, 9.8, 10.0), BRASS, metal=True, spec=0.8, dome=0.4)
    return v.done(outline=False)


# ------------------------------------------------------------- redstone -----
def redstone_item(c):
    v = Cv(c, 204)
    slab = v.poly([(1.6, 9.6), (8.0, 6.4), (14.4, 9.6), (8.0, 12.8)])
    v.part(slab, (168, 168, 170), dome=1.2, noise=0.06)
    v.part(v.poly([(1.6, 9.6), (8.0, 12.8), (8.0, 14.4), (1.6, 11.2)]), (120, 120, 122), dome=0.5)
    v.part(v.poly([(8.0, 12.8), (14.4, 9.6), (14.4, 11.2), (8.0, 14.4)]), (96, 96, 98), dome=0.5)
    v.part(v.seg(4.6, 9.6, 11.4, 9.6, 0.35), (170, 30, 30), dome=0.3)
    pegs = ((5.0, 9.4), (11.0, 9.4)) if has(c.base, "repeater") else ((5.0, 9.4), (11.0, 9.4), (8.0, 7.6))
    for x, y in pegs:
        v.part(v.rect(x - 0.45, y - 3.4, x + 0.45, y), (120, 84, 50), wood=True, dome=0.3)
        v.part(v.circ(x, y - 3.6, 0.75), (255, 70, 50), glow=True)
    return v.done()


DIG = {"0": ["111", "101", "101", "101", "111"], "1": ["010", "110", "010", "010", "111"],
       "2": ["111", "001", "111", "100", "111"], "3": ["111", "001", "111", "001", "111"],
       "4": ["101", "101", "111", "001", "001"], "5": ["111", "100", "111", "001", "111"],
       "6": ["111", "100", "111", "101", "111"], "7": ["111", "001", "010", "010", "010"],
       "8": ["111", "101", "111", "101", "111"], "9": ["111", "101", "111", "001", "111"]}


def light_item(c):
    v = Cv(c, 205)
    b = c.base
    lvl = int(b.split("_")[-1]) if b.split("_")[-1].isdigit() else 15
    glow = 0.35 + 0.65 * lvl / 15
    bulb = v.circ(9.6, 6.6, 4.2)
    v.part(bulb, mix(np.array((90, 90, 70.0)), np.array((255, 236, 150.0)), glow), glow=True)
    v.part(v.ell(8.6, 5.2, 1.2, 0.8), (255, 255, 240), glow=True)
    v.part(v.rect(7.8, 10.4, 11.4, 13.6), (150, 152, 160), metal=True, spec=0.7, dome=0.6)
    for y in (11.2, 12.4):
        v.tint(v.rect(7.8, y, 11.4, y + 0.35), 0.7)
    txt = str(lvl)
    x0 = 1.0
    v.part(v.rect(0.6, 9.2, 0.6 + 4.2 * len(txt) + 0.4, 15.4), (40, 40, 48), dome=0.3)
    for ch in txt:
        for rr, row in enumerate(DIG[ch]):
            for cc, bit in enumerate(row):
                if bit == "1":
                    v.part(v.rect(x0 + cc * 1.1, 9.8 + rr * 1.1, x0 + (cc + 1) * 1.1, 9.8 + (rr + 1) * 1.1), (255, 230, 120), glow=True)
        x0 += 4.0
    return v.done()


# ---------------------------------------------------------------- signs -----
def sign_item(c):
    v = Cv(c, 206)
    col = c.pal.main
    hanging = has(c.base, "hanging")
    if hanging:
        for x in (4.4, 11.6):
            v.part(v.seg(x, 0.8, x, 4.6, 0.35), (90, 92, 100), metal=True, spec=0.6, dome=0.3)
        v.part(v.rect(2.4, 0.6, 13.6, 1.8), shade(col, 0.8), wood=True, dome=0.5)
        board = v.rect(2.0, 4.4, 14.0, 12.6)
    else:
        v.part(v.rect(7.2, 9.0, 8.8, 15.4), shade(col, 0.8), wood=True, dome=0.6)
        board = v.rect(1.4, 2.4, 14.6, 10.0)
    v.part(board, col, wood=True, dome=1.2)
    y0 = 4.4 if hanging else 2.4
    for y in np.arange(y0 + 2.4, y0 + 7.6, 2.4):
        v.tint(board & (np.abs(v.Y - y) < 0.2), 0.7)
    v.tint(board & ~v.rect(board.nonzero()[1].min() / S + 0.6 if board.any() else 0, y0 + 0.6, 15, 16), 0.82)
    return v.done()


# ------------------------------------------------------- banner patterns -----
def glyph(v, kind, col):
    if kind == "creeper":
        m = v.rect(5, 5, 7, 7) | v.rect(9, 5, 11, 7) | v.rect(7, 7, 9, 10) | v.rect(6, 8, 7, 11) | v.rect(9, 8, 10, 11)
    elif kind == "flower":
        m = v.circ(8, 8, 1.0)
        for k in range(5):
            a = k * 2 * math.pi / 5 - math.pi / 2
            m |= v.circ(8 + 2.2 * math.cos(a), 8 + 2.2 * math.sin(a), 1.2)
    elif kind == "skull":
        m = (v.circ(8, 7.4, 3.0) | v.rect(6, 9, 10, 11.4)) & ~v.circ(6.8, 7.4, 0.8) & ~v.circ(9.2, 7.4, 0.8)
        m |= v.seg(4, 12.6, 12, 12.6, 0.3) | v.seg(4, 11.4, 12, 13.8, 0.3)
    elif kind == "globe":
        rr = np.hypot(v.X - 8, v.Y - 8)
        m = (np.abs(rr - 3.6) < 0.4) | ((rr < 3.6) & ((np.abs(v.X - 8) < 0.3) | (np.abs(v.Y - 8) < 0.3) |
                                                      (np.abs(((v.X - 8) / 1.8) ** 2 + ((v.Y - 8) / 3.6) ** 2 - 1) < 0.18)))
    elif kind == "field_masoned":
        m = v.rect(4, 4, 12, 12) & (((v.Y - 4) % 2.0 < 0.35) | (((v.X - 4) + 2 * (((v.Y - 4) // 2) % 2)) % 4.0 < 0.35))
    elif kind == "bordure_indented":
        m = v.rect(4, 4, 12, 12) & ~v.rect(5.2, 5.2, 10.8, 10.8)
        m |= v.rect(5, 5, 11, 11) & (((v.X + v.Y) % 2.0) < 0.5) & ~v.rect(6.2, 6.2, 9.8, 9.8)
    elif kind == "flow":
        rr = np.hypot(v.X - 8, v.Y - 8); th = np.arctan2(v.Y - 8, v.X - 8)
        m = (rr < 4) & (np.abs(np.sin(th + rr * 1.1)) < 0.25)
    elif kind == "guster":
        m = np.zeros_like(v.X, bool)
        for k in range(3):
            m |= v.seg(4, 6 + k * 2, 8, 4 + k * 2, 0.35) | v.seg(8, 4 + k * 2, 12, 6 + k * 2, 0.35)
    elif kind == "piglin":
        m = v.ell(8, 8.6, 3.0, 2.0) & ~v.circ(7, 8.6, 0.6) & ~v.circ(9, 8.6, 0.6)
        m |= v.seg(5, 11.6, 6, 13, 0.4) | v.seg(11, 11.6, 10, 13, 0.4)
    else:  # mojang / baqi: sitara
        m = np.zeros_like(v.X, bool)
        for k in range(5):
            a = k * 2 * math.pi / 5 - math.pi / 2
            m |= v.seg(8, 8, 8 + 3.6 * math.cos(a), 8 + 3.6 * math.sin(a), 0.55)
    v.part(m, col, flat=True)


def banner_pattern(c):
    v = Cv(c, 207)
    v.part(v.rect(3.0, 2.6, 13.0, 13.4), PAPER, dome=3, noise=0.05)
    v.part(v.rect(2.4, 1.8, 13.6, 2.8), shade(PAPER, 0.8), dome=0.5)
    v.part(v.rect(2.4, 13.2, 13.6, 14.2), shade(PAPER, 0.8), dome=0.5)
    kind = c.base.replace("_banner_pattern", "")
    glyph(v, kind, np.array((80, 54, 40.0)))
    return v.done()


# ------------------------------------------------------- raw & mob drops ----
def raw_item(c):
    b = c.base
    v = Cv(c, 208)
    if has(b, "slime_ball", "magma_cream"):
        col = np.array((110, 200, 80.0)) if has(b, "slime") else np.array((230, 120, 40.0))
        m = v.circ(8, 8.6, 5.4)
        v.part(m, col, dome=4, spec=0.8, shin=50)
        for _ in range(4):
            v.part(v.circ(v.r.uniform(5, 11), v.r.uniform(6, 11), v.r.uniform(0.6, 1.1)), shade(col, 1.25), dome=0.6, spec=0.8)
        if has(b, "magma"):
            v.tint(m & (h.ridged(h.fbm(v.H, v.W, v.r, 8, 3)) > 0.9), 0.55)
        return v.done()
    if has(b, "fire_charge"):
        m = lump(v, 8, 8, 5.6, np.array((60, 40, 34.0)), spec=0.1)
        cr = m & (h.ridged(h.fbm(v.H, v.W, v.r, 8, 3)) > 0.86)
        v.part(cr, (255, 150, 40), glow=True)
        v.part(v.circ(8, 8, 1.6), (255, 220, 120), glow=True)
        return v.done()
    col = {"raw_copper": (190, 110, 70), "raw_gold": (230, 180, 60), "raw_iron": (200, 170, 150),
           "netherite_scrap": (90, 70, 66), "clay_ball": (160, 166, 180), "resin_clump": (230, 120, 40)}.get(b, tuple(c.pal.main))
    lump(v, 7.6, 8.6, 5.2, np.array(col, float), spec=0.4 if has(b, "resin") else 0.2)
    lump(v, 11.8, 5.2, 2.2, shade(np.array(col, float), 1.08))
    if b.startswith("raw_"):
        for _ in range(6):
            v.part(v.circ(v.r.uniform(4, 11), v.r.uniform(5, 12), 0.45), shade(np.array(col, float), 1.4), dome=0.3, spec=0.6)
    return v.done()


def mob_drop(c):
    b = c.base
    v = Cv(c, 209)
    if b == "shulker_shell":
        dome_m = v.ell(8, 9.4, 6.4, 4.6) & (v.Y < 11.4)
        v.part(dome_m, (150, 100, 160), dome=3, spec=0.4)
        for k in range(5):
            v.tint(dome_m & (np.abs(np.arctan2(v.Y - 11.4, v.X - 8) + math.pi / 2 - (k - 2) * 0.45) < 0.05), 0.75)
        v.part(v.rect(1.8, 11.2, 14.2, 12.6), (110, 70, 120), dome=0.5)
    elif b == "phantom_membrane":
        wing = v.poly([(1.6, 4.0), (14.4, 3.2), (12.0, 7.0), (14.0, 10.4), (9.4, 9.4), (8.0, 13.4), (5.6, 9.6), (1.6, 10.4), (3.6, 7.0)])
        v.part(wing, (190, 196, 220), dome=1.0, noise=0.1)
        for x1, y1 in ((12, 7), (9.4, 9.4), (5.6, 9.6), (3.6, 7)):
            v.tint(wing & v.seg(8, 3.6, x1, y1, 0.22), 0.72)
    elif b in ("armadillo_scute", "turtle_scute", "scute"):
        col = (180, 120, 110) if has(b, "armadillo") else (80, 160, 80)
        plate = v.poly([(8, 2.4), (13.6, 6.0), (12.4, 12.6), (3.6, 12.6), (2.4, 6.0)])
        v.part(plate, col, dome=2, spec=0.4)
        v.tint(plate & ~v.poly([(8, 4.4), (11.6, 6.8), (10.8, 11.0), (5.2, 11.0), (4.4, 6.8)]), 0.8)
    elif b in ("turtle_egg", "sniffer_egg"):
        col = (236, 232, 200) if has(b, "turtle") else (170, 50, 46)
        egg = v.ell(8, 8.8, 4.6, 5.6)
        v.part(egg, col, dome=4, spec=0.3)
        spot = (80, 140, 80) if has(b, "turtle") else (60, 120, 70)
        for _ in range(9):
            v.part(egg & v.circ(v.r.uniform(4.6, 11.4), v.r.uniform(4, 13.6), v.r.uniform(0.5, 1.1)), spot, dome=0.5)
    elif b == "nautilus_shell":
        shell = v.circ(8, 8.4, 6.2)
        v.part(shell, (236, 222, 200), dome=3.5, spec=0.5)
        rr = np.hypot(v.X - 8, v.Y - 8.4); th = np.arctan2(v.Y - 8.4, v.X - 8)
        v.tint(shell & (np.sin(th + 2.4 * np.log(rr + 0.5)) > 0.7), 0.7)
        v.tint(shell & (np.abs(np.sin(th * 7)) < 0.08), 0.85)
    elif b == "heart_of_the_sea":
        orb = v.circ(8, 8, 5.6)
        v.part(orb, (40, 120, 200), dome=4.5, spec=0.9, shin=60)
        rr = np.hypot(v.X - 8, v.Y - 8); th = np.arctan2(v.Y - 8, v.X - 8)
        v.tint(orb & (np.abs(np.sin(th * 3 + rr)) < 0.12), 1.5)
        v.part(np.abs(rr - 6.0) < 0.5, (90, 200, 220), metal=True, spec=0.7, dome=0.3)
    elif b == "rabbit_foot":
        v.part(v.ell(7.6, 10.2, 3.2, 3.8), (190, 160, 130), dome=2.4, noise=0.15)
        for x in (5.6, 7.6, 9.6):
            v.part(v.ell(x, 6.4, 0.9, 1.4), (200, 170, 140), dome=0.8)
        v.part(v.seg(10, 12.6, 13.6, 14.6, 0.5), (236, 232, 218), dome=0.4)
    elif b in ("ink_sac", "glow_ink_sac"):
        glow = has(b, "glow")
        col = (60, 200, 200) if glow else (40, 40, 56)
        v.part(v.ell(7.6, 8.6, 4.6, 4.0), col, dome=3, spec=0.6, glow=False)
        v.part(v.poly([(10.6, 11.0), (14.0, 14.0), (11.6, 9.6)]), shade(np.array(col, float), 0.8), dome=0.6)
        if glow:
            v.part(v.circ(6.6, 7.4, 1.0), (180, 255, 240), glow=True)
    elif b == "goat_horn":
        for k, t in enumerate(np.linspace(0, 1, 40)):
            x, y = 3 + 9 * t, 13 - 9 * t + 3 * math.sin(t * math.pi)
            v.part(v.circ(x, y, 1.9 - 1.4 * t), shade(np.array((200, 190, 170.0)), 0.9 + 0.12 * (k % 4 == 0)), dome=0.8)
    elif b == "saddle":
        v.part(v.ell(8, 7.4, 6.4, 3.0), LEATHER, dome=2.2, spec=0.15)
        v.part(v.ell(8, 5.8, 3.0, 1.2), shade(LEATHER, 1.15), dome=0.8)
        for x in (4.4, 11.6):
            v.part(v.seg(x, 9.0, x, 13.0, 0.3), (70, 50, 36), dome=0.3)
            v.part(v.rect(x - 1.0, 12.8, x + 1.0, 14.2) & ~v.rect(x - 0.5, 13.2, x + 0.5, 13.8), IRON, metal=True, spec=0.7, dome=0.4)
    elif b in ("leather", "rabbit_hide"):
        col = LEATHER if b == "leather" else np.array((180, 140, 100.0))
        hide = v.poly([(3, 3), (7, 4.4), (13, 2.6), (12, 8), (14, 13), (8, 11.6), (2.6, 13.6), (4, 8)])
        v.part(hide, col, dome=1.4, noise=0.15)
        v.tint(hide & (h.dist_inside(hide, 6) < 2.4) & (((v.X + v.Y) * 2 % 2) < 0.6), 0.75)
    else:
        return None
    return v.done()


# --------------------------------------------------------- plant items ------
def plant_item(c):
    b = c.base
    v = Cv(c, 210)
    green = np.array((80, 150, 60.0))
    if b == "kelp":
        for y in np.arange(1, 15, 0.25):
            v.part(v.ell(8 + 1.6 * math.sin(y * 0.7), y, 1.4, 0.35), (70, 130, 50), dome=0.4)
        for y in (4, 8.6, 12.4):
            v.part(v.circ(8 + 1.6 * math.sin(y * 0.7) + 1.8, y, 0.9), (160, 170, 60), dome=0.6, spec=0.5)
    elif b == "sugar_cane":
        for x in (5, 8.4, 11.6):
            v.part(v.seg(x, 15, x, 2, 0.75), (140, 200, 90), dome=0.8)
            for y in (5, 9, 13):
                v.tint(v.seg(x - 0.8, y, x + 0.8, y, 0.2), 0.7)
    elif b == "wheat":
        for k in range(7):
            x = 5.6 + k * 0.8
            v.part(v.seg(8, 15, x, 4.4, 0.25), (210, 180, 90), dome=0.2)
            for t in range(4):
                v.part(v.ell(x + (8 - x) * t * 0.08, 3.4 + t * 1.0, 0.45, 0.7), (220, 180, 80), dome=0.4)
        v.part(v.rect(6.2, 10.4, 9.8, 11.6), (130, 90, 50), dome=0.4)
    elif b == "pitcher_plant":
        v.part(v.ell(8, 9.6, 3.6, 4.6), (60, 150, 150), dome=2.6, spec=0.3)
        v.part(v.ell(8, 4.8, 3.6, 1.2), (150, 80, 170), dome=0.8)
        v.part(v.ell(8, 4.8, 2.6, 0.7), (60, 30, 70), dome=0.3)
        v.part(v.seg(8, 14, 8, 15.6, 0.5), green, dome=0.3)
    elif b == "pitcher_pod":
        v.part(v.ell(8, 9, 3.2, 4.8), (110, 140, 80), dome=2.6)
        v.part(v.ell(8, 4.4, 1.6, 0.9), (60, 90, 50), dome=0.5)
    elif b == "pointed_dripstone":
        cone = (np.abs(v.X - 8) < 3.2 * np.clip((15 - v.Y) / 13, 0, 1)) & (v.Y > 1.6) & (v.Y < 15)
        v.part(cone, (150, 120, 100), dome=1.4, noise=0.1)
        v.tint(cone & ((v.Y % 2.2) < 0.4), 0.8)
    elif b in ("firefly_bush", "leaf_litter", "torchflower_seeds"):
        if b == "torchflower_seeds":
            for _ in range(5):
                v.part(v.ell(v.r.uniform(4, 12), v.r.uniform(5, 12), 1.0, 0.6, v.r.uniform(0, 3)), (150, 110, 60), dome=0.5)
        else:
            col = (110, 120, 60) if b == "firefly_bush" else (160, 110, 60)
            for _ in range(10):
                a = v.r.uniform(0, 6.3)
                h.paint_leaf(v.rgb, np.zeros((v.H, v.W)), v.a, v.r.uniform(4, 12) * S, v.r.uniform(4, 12) * S,
                             2.0 * S, 0.8 * S, a, shade(np.array(col, float), v.r.uniform(0.85, 1.1)), v.H, v.W, wrap=False)
            if b == "firefly_bush":
                for _ in range(4):
                    v.part(v.circ(v.r.uniform(3, 13), v.r.uniform(3, 13), 0.5), (255, 250, 150), glow=True)
    else:
        return None
    return v.done()


# ---------------------------------------------------------- crafted ---------
def crafted(c):
    b = c.base
    v = Cv(c, 211)
    if b == "name_tag":
        tag = v.poly([(2.4, 6.0), (5.0, 3.4), (14.0, 3.4), (14.0, 12.6), (5.0, 12.6), (2.4, 10.0)])
        v.part(tag, (236, 228, 200), dome=1.2)
        v.cut(v.circ(4.6, 8, 0.8))
        for y in (6.4, 8.4, 10.4):
            v.part(v.rect(7, y, 12.4, y + 0.5), (120, 110, 90), flat=True)
        v.part(v.seg(4.6, 8, 1.0, 13.6, 0.3), (220, 210, 190), dome=0.3)
    elif b == "painting":
        v.part(v.rect(1.6, 2.6, 14.4, 13.4), (140, 96, 56), wood=True, dome=0.8)
        v.part(v.rect(2.8, 3.8, 13.2, 9.6), (110, 170, 220), flat=True, noise=0.05)
        v.part(v.rect(2.8, 9.6, 13.2, 12.2), (90, 150, 60), flat=True, noise=0.1)
        v.part(v.poly([(4, 9.6), (7, 5.6), (10, 9.6)]), (120, 120, 130), flat=True)
        v.part(v.circ(11.4, 5.4, 1.0), (250, 220, 90), glow=True)
    elif b in ("item_frame", "glow_item_frame"):
        glow = has(b, "glow")
        v.part(v.rect(1.6, 1.6, 14.4, 14.4), (170, 130, 80), wood=True, dome=1.0)
        v.part(v.rect(3.4, 3.4, 12.6, 12.6), (110, 170, 160) if glow else LEATHER, dome=0.4, noise=0.15)
    elif b == "totem_of_undying":
        v.part(v.poly([(1.6, 6), (5.6, 5.4), (5.6, 9.4), (2.4, 9.0)]) | v.poly([(14.4, 6), (10.4, 5.4), (10.4, 9.4), (13.6, 9.0)]),
               (90, 170, 90), dome=0.8)
        body = v.rect(5.4, 4.4, 10.6, 14.6) | v.circ(8, 4.0, 2.8)
        v.part(body, GOLD, metal=True, spec=0.9, dome=2)
        for x in (7, 9):
            v.part(v.circ(x, 4.0, 0.55), (40, 140, 80), glow=True)
        v.tint(v.rect(5.4, 9, 10.6, 9.6), 0.7)
    elif b in ("trial_key", "ominous_trial_key"):
        col = (60, 120, 140) if has(b, "ominous") else (200, 140, 70)
        ring = v.circ(5, 5, 3.0) & ~v.circ(5, 5, 1.5)
        v.part(ring, col, metal=True, spec=0.8, dome=0.8)
        v.part(v.seg(7, 7, 13.6, 13.6, 0.75), col, metal=True, spec=0.8, dome=0.6)
        for t in (10.6, 12.4):
            v.part(v.seg(t, t, t + 1.6, t - 1.6, 0.5), col, metal=True, spec=0.8, dome=0.4)
    elif b == "end_crystal":
        for k in range(2):
            m = v.poly([(8, 1.6 + k), (14.4 - k, 8), (8, 14.4 - k), (1.6 + k, 8)])
            v.part(m & ~v.poly([(8, 3.0 + k), (13 - k, 8), (8, 13 - k), (3 + k, 8)]) if k == 0 else m,
                   (210, 200, 230) if k == 0 else (190, 120, 220), spec=0.8, dome=0.6, glow=k == 1)
        v.part(v.circ(8, 8, 2.0), (255, 200, 255), glow=True)
    elif b == "cauldron":
        pot = v.poly([(2.4, 4.0), (13.6, 4.0), (12.6, 13.4), (3.4, 13.4)])
        v.part(pot, (60, 60, 66), metal=True, spec=0.5, dome=2)
        v.part(v.ell(8, 4.0, 5.6, 1.2), (30, 30, 34), dome=0.3)
        for x in (3.6, 12.4):
            v.part(v.rect(x - 0.8, 13.2, x + 0.8, 15.0), (50, 50, 56), metal=True, dome=0.3)
    elif b == "armor_stand":
        v.part(v.rect(2.6, 14.0, 13.4, 15.4), (150, 150, 152), dome=0.5)
        v.part(v.seg(8, 14, 8, 2.6, 0.55), (150, 110, 64), wood=True, dome=0.5)
        v.part(v.seg(3.4, 5.6, 12.6, 5.6, 0.55), (150, 110, 64), wood=True, dome=0.5)
        v.part(v.seg(5.4, 10.4, 10.6, 10.4, 0.5), (150, 110, 64), wood=True, dome=0.5)
        v.part(v.circ(8, 2.2, 1.2), (150, 110, 64), wood=True, dome=0.6)
    elif b in ("campfire", "soul_campfire"):
        soul = has(b, "soul")
        v.part(v.seg(2, 13.6, 14, 10.6, 0.9) | v.seg(2, 10.6, 14, 13.6, 0.9), (110, 76, 46), wood=True, dome=0.6)
        fl = v.poly([(5, 11.4), (6, 6), (7.6, 8), (8, 2.6), (9.4, 7.6), (10.4, 5.4), (11, 11.4)])
        v.part(fl, (90, 220, 230) if soul else (255, 160, 50), glow=True)
        v.part(v.poly([(6.6, 11.4), (8, 6), (9.4, 11.4)]), (220, 255, 255) if soul else (255, 230, 140), glow=True)
    elif b == "hopper":
        v.part(v.poly([(1.6, 3.0), (14.4, 3.0), (10.6, 9.6), (5.4, 9.6)]), (66, 66, 72), metal=True, spec=0.5, dome=1.4)
        v.part(v.rect(6.4, 9.4, 9.6, 13.6), (56, 56, 62), metal=True, spec=0.5, dome=0.6)
        v.part(v.ell(8, 3.4, 5.6, 0.9), (26, 26, 30), dome=0.2)
    elif b == "bell":
        bell = v.poly([(5.4, 3.4), (10.6, 3.4), (12.0, 11.0), (13.4, 12.6), (2.6, 12.6), (4.0, 11.0)])
        v.part(bell, GOLD, metal=True, spec=0.9, dome=2.2)
        v.tint(bell & (np.abs(v.Y - 10.4) < 0.4), 0.75)
        v.part(v.circ(8, 13.6, 1.0), (200, 160, 60), metal=True, spec=0.7)
        v.part(v.rect(7.2, 1.6, 8.8, 3.6), (90, 70, 50), dome=0.4)
    elif b == "brush":
        A = v.seg(2.6, 13.4, 9.0, 7.0, 0.55)
        v.part(A, (150, 110, 64), wood=True, dome=0.5)
        v.part(v.seg(8.8, 7.2, 10.2, 5.8, 0.9), (190, 140, 80), metal=True, spec=0.6, dome=0.5)
        for k in range(6):
            v.part(v.seg(10 + k * 0.3, 6 - k * 0.3, 13.4 + k * 0.2, 2.6 + k * 0.4, 0.28), (230, 214, 180), dome=0.2)
    elif b in ("carrot_on_a_stick", "warped_fungus_on_a_stick"):
        v.part(v.seg(2.4, 13.6, 12.6, 3.4, 0.5), (150, 110, 64), wood=True, dome=0.5)
        v.part(v.seg(12.6, 3.4, 13.4, 9.6, 0.15), (220, 220, 220), dome=0.1)
        if has(b, "carrot"):
            v.part(v.poly([(12.4, 9.6), (14.4, 9.6), (13.4, 14.6)]), (240, 130, 30), dome=0.7)
        else:
            v.part(v.ell(13.4, 11.0, 2.0, 1.4), (30, 150, 140), dome=0.8)
            v.part(v.seg(13.4, 11.0, 13.4, 14.4, 0.4), (200, 120, 60), dome=0.3)
    elif b == "spyglass":
        v.part(v.seg(3.4, 12.6, 12.6, 3.4, 1.1), (200, 140, 80), metal=True, spec=0.8, dome=0.6)
        v.part(v.seg(2.6, 13.4, 5.2, 10.8, 1.3), (130, 90, 50), metal=True, spec=0.6, dome=0.6)
        v.part(v.circ(13.0, 3.0, 1.2), (160, 210, 240), spec=0.9, dome=0.6)
        for t in (7.0, 9.4):
            v.tint(v.seg(t, 16 - t, t + 0.5, 15.5 - t, 1.2), 0.75)
    elif b == "shears":
        for sgn in (-1, 1):
            v.part(v.seg(8, 8, 8 + sgn * 4.8, 2.6, 0.75), IRON, metal=True, spec=0.9, dome=0.6)
            ring = v.circ(8 - sgn * 3.4, 12.4, 2.0) & ~v.circ(8 - sgn * 3.4, 12.4, 1.0)
            v.part(ring | v.seg(8, 8, 8 - sgn * 2.6, 10.8, 0.6), (140, 60, 40), dome=0.6)
        v.part(v.circ(8, 8, 0.6), (80, 80, 86), metal=True, spec=0.8)
    elif b == "string":
        pts = [(2 + t * 12, 8 + 3 * math.sin(t * 9)) for t in np.linspace(0, 1, 40)]
        for (a, bb), (cc, d) in zip(pts[:-1], pts[1:]):
            v.part(v.seg(a, bb, cc, d, 0.35), (236, 236, 236), dome=0.3)
    elif b == "lead":
        rr = np.hypot(v.X - 8, v.Y - 7)
        loop = (np.abs(rr - 4.4) < 0.75)
        v.part(loop, (190, 150, 100), dome=0.6, noise=0.15)
        v.tint(loop & ((np.arctan2(v.Y - 7, v.X - 8) * 4 % 1.0) < 0.3), 0.8)
        v.part(v.seg(10.8, 10.4, 13.6, 14.6, 0.6), (190, 150, 100), dome=0.5)
    elif b == "paper":
        v.part(v.poly([(2.6, 2.6), (13.4, 2.0), (14.0, 13.4), (2.0, 14.0)]), (240, 236, 222), dome=1.0, noise=0.04)
        for y in (5, 7.4, 9.8, 12.2):
            v.part(v.seg(4, y, 12, y - 0.3, 0.18), (150, 170, 200), flat=True)
    elif b == "wind_charge":
        rr = np.hypot(v.X - 8, v.Y - 8); th = np.arctan2(v.Y - 8, v.X - 8)
        sw = (rr < 5.6) & (np.sin(th * 2 + rr * 1.2) > 0.1)
        v.part(sw, (200, 220, 250), dome=1.4, spec=0.5)
        v.part(v.circ(8, 8, 1.4), (240, 250, 255), glow=True)
    elif b == "nether_star":
        star = np.zeros_like(v.X, bool)
        for k in range(4):
            a = k * math.pi / 2
            star |= v.poly([(8, 8), (8 + 6.4 * math.cos(a) - 1.4 * math.sin(a), 8 + 6.4 * math.sin(a) + 1.4 * math.cos(a)),
                            (8 + 6.4 * math.cos(a), 8 + 6.4 * math.sin(a))]) | v.seg(8, 8, 8 + 6.2 * math.cos(a), 8 + 6.2 * math.sin(a), 1.2)
        v.part(star, (236, 240, 250), spec=0.9, dome=1.2)
        v.part(v.circ(8, 8, 1.6), (255, 255, 230), glow=True)
    elif b == "prismarine_shard":
        sh = v.poly([(4, 13), (6, 3), (9, 2), (12.4, 10), (9, 14)])
        v.part(sh, (100, 170, 160), dome=1.4, spec=0.5)
        v.tint(sh & v.seg(6, 3, 9, 14, 0.3), 1.25)
    elif b in ("elytra", "broken_elytra", "elytra_broken"):
        for sgn in (-1, 1):
            wing = v.poly([(8, 2.4), (8 + sgn * 6.6, 3.4), (8 + sgn * 5.4, 9.6), (8 + sgn * 6.2, 14.4), (8 + sgn * 1.0, 11.6)])
            v.part(wing, (130, 130, 160), dome=1.4, noise=0.1)
            for k in range(3):
                v.tint(wing & v.seg(8 + sgn * 0.6, 3, 8 + sgn * (2 + k * 1.6), 12 - k, 0.22), 0.72)
        if has(b, "broken"):
            v.cut(v.seg(10, 4, 13, 12, 0.5) & (v.X > 8))
    else:
        return None
    return v.done()


def special(c):
    if c.H != 16 or c.W != 16:
        return None
    b = c.base
    out = special2(c)
    if out is not None:
        return out
    if b.endswith("_harness") or b == "harness":
        return harness(c)
    if b.endswith("lantern") and not has(b, "jack"):
        return lantern(c)
    if b in ("wolf_armor", "wolf_armor_overlay"):
        return wolf_armor(c, overlay=has(b, "overlay"))
    if b == "leather_horse_armor_overlay":
        return horse_overlay(c)
    if b in ("comparator", "repeater"):
        return redstone_item(c)
    if b == "light" or re.fullmatch(r"light_\d\d", b):
        return light_item(c)
    if b.endswith("_sign") or b.endswith("_hanging_sign") or b in ("sign", "hanging_sign"):
        return sign_item(c)
    if b.endswith("_banner_pattern"):
        return banner_pattern(c)
    if b.startswith("raw_") and not b.endswith("_block") or b in ("netherite_scrap", "clay_ball", "slime_ball",
                                                                  "magma_cream", "fire_charge", "resin_clump"):
        return raw_item(c)
    for fn in (mob_drop, plant_item, crafted):
        out = fn(c)
        if out is not None:
            return out
    return None


# ------------------------------------------------- round-2 QA additions -----
def dust_pile(c):
    """Dusts: chhota dher + bikhre daane + chamak (har material ka apna rang/glint)."""
    b = c.base
    v = Cv(c, 220)
    col, glint, glow = {"gunpowder": ((80, 80, 84), (150, 150, 156), False),
                        "glowstone_dust": ((240, 200, 90), (255, 250, 200), True),
                        "redstone": ((190, 30, 30), (255, 120, 100), False),
                        "sugar": ((244, 244, 246), (255, 255, 255), False),
                        "bone_meal": ((236, 234, 226), (255, 255, 255), False)}.get(b, (200, 200, 200, ))[:3] \
        if b in ("gunpowder", "glowstone_dust", "redstone", "sugar", "bone_meal") else ((200, 200, 200), (255, 255, 255), False)
    pile = (np.abs(v.X - 8) < 6.0 * np.clip((v.Y - 6.2) / 7.4, 0, 1) ** 0.8) & (v.Y > 6.2) & (v.Y < 14.2) | v.ell(8, 13.6, 6.0, 1.2)
    v.part(pile, col, dome=2.6, noise=0.25, spec=0.4 if b == "sugar" else 0.15, glow=glow)
    for _ in range(int(14 if b == "sugar" else 10)):
        x, y = v.r.uniform(3, 13), v.r.uniform(7.6, 13.6)
        m = pile & v.circ(x, y, 0.4)
        v.part(m, glint, flat=True, glow=glow or b in ("sugar", "redstone"))
    for x, y in ((2.6, 13.4), (13.6, 12.8), (12.4, 14.4), (3.8, 11.6)):
        v.part(v.circ(x, y, 0.55), col, dome=0.4, glow=glow)
    if b == "bone_meal":
        v.part(v.seg(9.6, 4.6, 13.2, 2.2, 0.55) | v.circ(9.4, 4.8, 0.8) | v.circ(13.4, 2.0, 0.8), (240, 236, 222), dome=0.5)
    return v.done()


def lump2(c):
    """Har lump ki apni silhouette."""
    b = c.base
    v = Cv(c, 221)
    if b == "charcoal":
        disc = v.ell(8, 8.6, 5.8, 5.0)
        v.part(disc, (60, 46, 36), dome=1.6, noise=0.15)
        rr = np.hypot((v.X - 8) / 5.8, (v.Y - 8.6) / 5.0)
        v.tint(disc & (np.abs(np.sin(rr * 14)) < 0.18), 0.65)
        v.tint(disc & (rr > 0.85), 0.7)
    elif b == "flint":
        fl = v.poly([(8, 1.6), (12.6, 7.0), (11.4, 13.6), (5.4, 14.0), (3.6, 7.6)])
        v.part(fl, (66, 68, 74), dome=1.4, spec=0.6, shin=40)
        for k in range(5):
            v.tint(fl & v.seg(8, 1.6 + k * 2.4, 3.6 + k * 1.6, 7.6 + k * 1.2, 0.25), 1.3)
    elif b == "netherite_scrap":
        sh = v.poly([(2.6, 11.0), (6.0, 3.0), (9.0, 6.0), (13.6, 2.6), (12.4, 9.6), (8.4, 13.6), (6.0, 9.6)])
        v.part(sh, (88, 70, 66), metal=True, spec=0.6, dome=1.2, noise=0.15)
        v.tint(sh & v.seg(6.0, 3.0, 8.4, 13.6, 0.3), 1.35)
    elif b == "clay_ball":
        ball = v.circ(8, 8.6, 5.6)
        v.part(ball, (162, 168, 182), dome=4.5, noise=0.06)
        v.tint(v.ell(9.4, 7.4, 1.8, 1.2, 0.5), 0.8)                          # angoothe ka nishaan
        v.tint(v.ell(9.0, 7.0, 1.2, 0.7, 0.5), 0.9)
    elif b == "resin_clump":
        drip = v.circ(8, 6.6, 4.2) | v.poly([(5.2, 8), (10.8, 8), (8.6, 14.6), (7.4, 14.6)])
        v.part(drip, (230, 120, 40), dome=3, spec=0.8, shin=50)
    elif b == "rotten_flesh":
        st = v.poly([(2.0, 7.6), (5.6, 4.4), (10.4, 5.2), (14.0, 3.6), (13.0, 9.6), (9.0, 11.6), (4.6, 12.6)])
        v.part(st, (120, 110, 64), dome=1.6, noise=0.25)
        for _ in range(4):
            v.tint(st & v.circ(v.r.uniform(4, 12), v.r.uniform(6, 11), v.r.uniform(0.7, 1.2)), 0.65)
        for x in np.arange(4.4, 12.4, 1.4):
            v.part(v.seg(x, 7.8, x + 0.6, 9.2, 0.18), (50, 40, 30), flat=True)
        v.part(v.seg(4, 8.5, 12.4, 8.5, 0.18), (50, 40, 30), flat=True)
    elif b.startswith("raw_"):
        col = np.array({"raw_copper": (150, 110, 90), "raw_gold": (150, 130, 100), "raw_iron": (150, 140, 132)}[b], float)
        vein = np.array({"raw_copper": (220, 120, 70), "raw_gold": (250, 200, 60), "raw_iron": (226, 196, 170)}[b], float)
        rock = v.poly([(3.0, 6.0), (7.0, 2.6), (12.6, 4.0), (14.0, 9.4), (10.4, 13.6), (4.4, 13.0)])
        v.part(rock, col, dome=2.4, noise=0.2)
        vv = h.ridged(h.fbm(v.H, v.W, v.r, 8, 3)) > 0.82
        v.part(rock & vv, vein, metal=True, spec=0.8, dome=0.4)
    else:
        return None
    return v.done()


def gem2(c):
    b = c.base
    v = Cv(c, 222)
    if b == "quartz":
        hexp = v.poly([(8, 1.8), (12.4, 4.4), (12.4, 11.6), (8, 14.2), (3.6, 11.6), (3.6, 4.4)])
        v.part(hexp, (236, 230, 222), dome=1.0, spec=0.7)
        v.tint(hexp & (v.X < 8) & (v.Y > 4.4) & (v.Y < 11.6), 1.08)
        v.tint(hexp & (v.X > 8), 0.86)
        v.tint(hexp & (np.abs(v.X - 8) < 0.2), 0.8)
    elif b == "amethyst_shard":
        for x0, x1, top, col in ((4.0, 7.6, 3.4, (170, 110, 210)), (7.0, 11.0, 1.4, (190, 130, 230)), (10.0, 13.2, 5.0, (160, 100, 200))):
            m = v.poly([(x0, 14), ((x0 + x1) / 2, top), (x1, 14)])
            v.part(m, col, spec=0.8, shin=50, dome=0.8)
            v.tint(m & (v.X < (x0 + x1) / 2), 1.15)
    elif b == "echo_shard":
        sl = v.poly([(3.0, 13.0), (6.0, 6.0), (5.4, 9.0), (10.0, 2.4), (13.0, 3.6), (9.0, 8.6), (11.0, 8.0), (6.4, 14.4)])
        v.part(sl, (20, 70, 80), spec=0.8, dome=0.8)
        v.tint(sl & v.seg(4, 12, 11, 3, 0.4), 1.6)
    elif b == "prismarine_crystals":
        for x, y in ((5.4, 6.0), (10.4, 5.2), (8.0, 10.0), (4.6, 11.6), (11.4, 11.0)):
            v.part(v.circ(x, y, 2.0), (170, 230, 210), spec=0.8, dome=1.4, glow=False)
            v.part(v.circ(x - 0.6, y - 0.6, 0.6), (240, 255, 250), glow=True)
    else:
        return None
    return v.done()


def organics2(c):
    b = c.base
    v = Cv(c, 223)
    if b == "honeycomb":
        blob = v.poly([(3, 4), (13, 3), (14, 10), (9, 14), (2.6, 11)])
        v.part(blob, (240, 170, 40), dome=2, spec=0.5)
        sz = 2.2
        yy, xx = v.Y / sz, v.X / (sz * 0.866)
        row = np.floor(yy); xo = xx + (row % 2) * 0.5
        d = np.maximum(np.abs(xo - np.floor(xo) - 0.5) * 1.15, np.abs(yy - row - 0.5))
        v.tint(blob & (d > 0.4), 0.7)
    elif b == "nether_wart":
        v.part(v.seg(8, 14.6, 8, 8, 0.6), (120, 30, 30), dome=0.5)
        for x, y, rr in ((8, 6, 2.4), (5.2, 8.4, 1.6), (10.8, 8.0, 1.7), (6.6, 4.0, 1.2), (9.8, 4.4, 1.2)):
            v.part(v.circ(x, y, rr), (170, 40, 40), dome=1.2, spec=0.3)
    elif b == "seagrass":
        for k, x in enumerate((4.6, 7.4, 10.0, 12.4)):
            pts = [(x + 1.2 * math.sin(t * 3 + k), 15 - t * 12) for t in np.linspace(0, 1, 24)]
            for (a, bb), (cc, d) in zip(pts[:-1], pts[1:]):
                v.part(v.seg(a, bb, cc, d, 0.6 * (1 - 0.6 * (15 - bb) / 12)), (60, 150, 60), dome=0.4)
    elif b == "pitcher_pod":
        pod = v.ell(8, 8.6, 3.4, 5.4)
        v.part(pod, (120, 150, 80), dome=2.6)
        v.tint(pod & (np.abs(v.X - 8) < 0.25), 0.65)
        v.part(v.seg(8, 3.2, 9.6, 1.4, 0.4), (90, 110, 60), dome=0.3)
    elif b == "sulfur_spike":
        sp = v.poly([(8, 1.2), (10.6, 9.0), (12.6, 14.6), (3.4, 14.6), (5.4, 9.0)])
        v.part(sp, (230, 220, 90), spec=0.7, dome=1.0)
        v.tint(sp & (v.X < 8), 1.12)
    elif b == "dried_kelp":
        roll = v.rect(3, 4, 13, 12)
        v.part(roll, (54, 70, 36), dome=2.0, noise=0.12)
        for x in np.arange(4, 13, 1.6):
            v.tint(roll & (np.abs(v.X - x) < 0.25), 0.7)
        v.part(v.rect(7.4, 3.6, 8.6, 12.4), (150, 130, 80), dome=0.4)
    else:
        return None
    return v.done()


def small2(c):
    b = c.base
    v = Cv(c, 224)
    if b.endswith("_nugget"):
        col = g.mat_color(b.replace("_nugget", "_x"), c.pal) if any(b.startswith(k) for k in ("iron", "gold", "copper")) else c.pal.main
        for x, y, rr in ((7.0, 9.4, 3.0), (10.6, 7.6, 1.8)):
            lump(v, x, y, rr, np.asarray(col, float), spec=0.9)
    elif b.startswith("disc_fragment"):
        wedge = v.poly([(3, 13), (8, 2.6), (13.4, 10.6)])
        v.part(wedge, (40, 40, 46), dome=1.0, spec=0.5)
        rr = np.hypot(v.X - 3, v.Y - 13)
        v.tint(wedge & (np.abs(np.sin(rr * 4)) < 0.2), 1.3)
        x0 = 8.4
        for rr_, row in enumerate(DIG["5"]):
            for cc, bit in enumerate(row):
                if bit == "1":
                    v.part(v.rect(x0 + cc * 0.9, 6.6 + rr_ * 0.9, x0 + (cc + 1) * 0.9, 6.6 + (rr_ + 1) * 0.9), (230, 200, 90), flat=True)
    elif b == "mace":
        v.part(v.seg(2.6, 13.6, 9.0, 7.2, 0.6), (110, 80, 50), wood=True, dome=0.5)
        v.part(v.circ(10.4, 5.8, 3.2), (150, 152, 160), metal=True, spec=0.8, dome=2.4)
        for k in range(8):
            a = k * math.pi / 4
            v.part(v.poly([(10.4 + 3.0 * math.cos(a - 0.3), 5.8 + 3.0 * math.sin(a - 0.3)),
                           (10.4 + 4.8 * math.cos(a), 5.8 + 4.8 * math.sin(a)),
                           (10.4 + 3.0 * math.cos(a + 0.3), 5.8 + 3.0 * math.sin(a + 0.3))]), (190, 192, 200), metal=True, spec=0.8, dome=0.4)
    elif b == "flint_and_steel":
        ring = (np.abs(np.hypot(v.X - 6, v.Y - 9) - 3.6) < 0.8) & ~((v.X > 6) & (np.abs(v.Y - 9) < 1.4))
        v.part(ring, (170, 172, 180), metal=True, spec=0.8, dome=0.6)
        v.part(v.poly([(9.6, 4.4), (13.6, 2.6), (14.0, 7.6), (11.0, 8.4)]), (66, 68, 74), spec=0.5, dome=0.8)
        for k in range(3):
            v.part(v.seg(10.4, 9.0, 12.4 + k, 11.4 - k * 0.6, 0.2), (255, 200, 80), glow=True)
    elif b == "filled_map_markings":
        for x, y in ((5, 5), (11, 6), (8, 11)):
            v.part(v.seg(x - 1, y - 1, x + 1, y + 1, 0.35) | v.seg(x + 1, y - 1, x - 1, y + 1, 0.35), (230, 230, 230), flat=True)
        return v.done(outline=False)
    elif b in ("elytra_broken", "broken_elytra"):
        for sgn in (-1, 1):
            wing = v.poly([(8, 2.4), (8 + sgn * 6.6, 3.4), (8 + sgn * 5.4, 9.6), (8 + sgn * 6.2, 14.4), (8 + sgn * 1.0, 11.6)])
            v.part(wing, (110, 110, 130), dome=1.4, noise=0.15)
        for x, y, rr in ((11.4, 7.6, 1.4), (4.6, 10.6, 1.1), (12.2, 12.0, 0.9)):
            v.cut(v.circ(x, y, rr))
        v.cut(v.poly([(13.6, 9.4), (14.8, 11.6), (12.4, 11.0)]))
    elif b == "lead":
        for k, rr in enumerate((4.6, 3.6, 2.6)):
            m = (np.abs(np.hypot(v.X - 8, v.Y - 7.6) - rr) < 0.55)
            v.part(m, shade(np.array((190, 150, 100.0)), 1 - 0.08 * k), dome=0.5, noise=0.15)
        v.part(v.seg(12.4, 8.6, 14.0, 14.4, 0.55), (190, 150, 100), dome=0.4)
        v.part(v.circ(12.6, 8.4, 1.0), (150, 110, 70), dome=0.6)
    elif b == "wheat":
        for k in range(9):
            a = -math.pi / 2 + (k - 4) * 0.12
            x1, y1 = 8 + 7 * math.cos(a), 9.4 + 7 * math.sin(a)
            v.part(v.seg(8, 9.4, x1, y1, 0.22) | v.seg(8, 9.4, 8 - (x1 - 8) * 0.6, 15.0, 0.22), (210, 180, 90), dome=0.2)
            for t in range(4):
                v.part(v.ell(x1 + (8 - x1) * t * 0.08, y1 + t * 0.9, 0.4, 0.6), (225, 185, 80), dome=0.4)
        v.part(v.rect(6.4, 8.6, 9.6, 10.2), (130, 90, 50), dome=0.4)
    else:
        return None
    return v.done()


def trim_template(c):
    """Har smithing trim template ka apna glyph (naam ke seed se), plate wahi."""
    b = c.base
    v = Cv(c, 225)
    plate = v.rect(2.4, 1.8, 13.6, 14.2)
    v.part(plate, (58, 64, 76), dome=1.8, spec=0.3)
    v.tint(plate & ~v.rect(3.2, 2.6, 12.8, 13.4), 0.75)
    up = has(b, "netherite_upgrade")
    acc = np.array((214, 170, 70.0)) if up else c.pal.accent
    rr = np.random.default_rng(g.seed_of(b))
    kind = int(rr.integers(6))
    m = np.zeros_like(v.X, bool)
    if kind == 0:
        m = (np.abs(np.hypot(v.X - 8, v.Y - 8) - 3.4) < 0.6) | v.circ(8, 8, 1.0)
    elif kind == 1:
        for k in range(3):
            m |= v.seg(4.6, 5 + k * 3, 11.4, 5 + k * 3, 0.45)
    elif kind == 2:
        m = v.poly([(8, 3.6), (12, 11.6), (4, 11.6)]) & ~v.poly([(8, 6.4), (10.2, 10.4), (5.8, 10.4)])
    elif kind == 3:
        for k in range(4):
            a = k * math.pi / 2 + math.pi / 4
            m |= v.seg(8, 8, 8 + 4 * math.cos(a), 8 + 4 * math.sin(a), 0.5)
    elif kind == 4:
        m = v.poly([(8, 3.6), (12.4, 8), (8, 12.4), (3.6, 8)]) & ~v.circ(8, 8, 1.6)
    else:
        for k in range(2):
            m |= (np.abs(np.hypot(v.X - 8, v.Y - 8) - (1.6 + k * 2.4)) < 0.45)
        m |= v.seg(8, 3, 8, 13, 0.4)
    # chhota alag nishaan (har template alag): kono par dots ka pattern
    for k, (x, y) in enumerate(((4, 4), (12, 4), (4, 12), (12, 12))):
        if (g.seed_of(b) >> k) & 1:
            m |= v.circ(x, y, 0.6)
    v.part(m, acc, metal=True, spec=0.7, dome=0.6)
    return v.done()


def special2(c):
    if c.H != 16 or c.W != 16:
        return None
    b = c.base
    if b in ("gunpowder", "glowstone_dust", "redstone", "sugar", "bone_meal"):
        return dust_pile(c)
    for fn in (lump2, gem2, organics2, small2):
        out = fn(c)
        if out is not None:
            return out
    if has(b, "smithing_template"):
        return trim_template(c)
    return None
