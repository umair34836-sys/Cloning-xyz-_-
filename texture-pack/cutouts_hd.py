"""FireCraft Remastered HD — A-Z review ke baad cut-out blocks (paude, fasalein, latakti cheezein,
model ke hisse) ki apni designs. Har function ek HD RGBA lautata hai.
Jin par `own_alpha = True` hai unki transparency hamari design hai (vanilla mask nahi).
"""
import math
import numpy as np

import generate as g
import generate_hd as h
from shapes_hd import Cv
from blocks_hd2 import Blk, door_full, IRONC, GOLDC, DARK

S = h.S
shade, mix, has = g.shade, g.mix, g.has


def own(fn):
    fn.own_alpha = True
    return fn


def leaf(v, x, y, L, w, ang, col):
    h.paint_leaf(v.rgb, np.zeros((v.H, v.W)), v.a, y * S, x * S, L * S, w * S, ang, col, v.H, v.W, wrap=False)


def lit(v):
    """Cut-out sprite par ek saath roshni (dome per pixel nahi, halka)."""
    return v.done(outline=False)


# ---------------------------------------------------------------- crops -----
@own
def beetroot(c):
    v = Cv(c, 80); st = g.stage_of(c.base) or 0
    green = np.array((70, 140, 50.0)); n = 3 + st * 2
    for k in range(n):
        a = -math.pi / 2 + (k - (n - 1) / 2) * 0.42
        L = 2.6 + st * 1.1
        v.part(v.seg(8, 15.6, 8 + math.cos(a) * L, 15.6 + math.sin(a) * L, 0.3), (150, 40, 50), dome=0.3)
        leaf(v, 8 + math.cos(a) * (L + 1.2), 15.6 + math.sin(a) * (L + 1.2), 1.6 + st * 0.5, 0.9 + st * 0.2, a,
             shade(green, v.r.uniform(0.85, 1.1)))
    if st >= 3:
        v.part(v.circ(8, 15.0, 2.2), (150, 30, 50), dome=1.2, spec=0.3)
    return lit(v)


@own
def carrots(c):
    v = Cv(c, 81); st = g.stage_of(c.base) or 0
    green = np.array((80, 160, 60.0))
    for x0 in (3.5, 8, 12.5):
        hh = 4 + st * 2.6
        for k in range(3):
            a = -math.pi / 2 + (k - 1) * 0.35
            x1, y1 = x0 + math.cos(a) * hh, 15.6 + math.sin(a) * hh
            v.part(v.seg(x0, 15.6, x1, y1, 0.25), green, dome=0.2)
            for t in np.linspace(0.35, 1, 5):
                px, py = x0 + (x1 - x0) * t, 15.6 + (y1 - 15.6) * t
                v.part(v.circ(px - 0.6, py, 0.45) | v.circ(px + 0.6, py, 0.45), shade(green, 1.1), dome=0.3)
        if st >= 3:
            v.part(v.ell(x0, 15.2, 1.2, 0.9), (240, 130, 30), dome=0.6)
    return lit(v)


@own
def sweet_berry(c):
    v = Cv(c, 82); st = g.stage_of(c.base) or 0
    rr = 3 + st * 1.4
    for _ in range(int(14 + st * 10)):
        a = v.r.uniform(0, 2 * math.pi); d = v.r.uniform(0, rr)
        leaf(v, 8 + math.cos(a) * d, 15.2 - rr + math.sin(a) * d * 0.9, 1.3, 0.6, v.r.uniform(0, 6.3),
             shade(np.array((60, 110, 50.0)), v.r.uniform(0.8, 1.15)))
    if st >= 2:
        for _ in range(4 if st == 2 else 9):
            a = v.r.uniform(0, 2 * math.pi); d = v.r.uniform(0, rr * 0.8)
            v.part(v.circ(8 + math.cos(a) * d, 15.2 - rr + math.sin(a) * d * 0.9, 0.75),
                   (200, 30, 50) if st == 3 else (230, 140, 160), dome=0.6, spec=0.5)
    return lit(v)


@own
def torchflower_crop(c):
    v = Cv(c, 83); st = g.stage_of(c.base) or 0
    v.part(v.seg(8, 16, 8, 7 - st * 2, 0.4), (70, 130, 60), dome=0.3)
    leaf(v, 6.4, 13, 1.8, 0.7, math.pi + 0.5, np.array((80, 150, 60.0)))
    leaf(v, 9.6, 11.4, 1.8, 0.7, -0.5, np.array((80, 150, 60.0)))
    v.part(v.ell(8, 6 - st * 2, 1.1 + st * 0.4, 1.6 + st * 0.5), (90, 150, 60), dome=0.8)
    v.part(v.ell(8, 4.6 - st * 2.2, 0.6 + st * 0.3, 0.8 + st * 0.3), (250, 150, 50), dome=0.5)
    return lit(v)


@own
def pitcher_crop(c):
    v = Cv(c, 84)
    st = g.stage_of(c.base) or (4 if has(c.base, "top") else 2)
    teal = np.array((60, 130, 130.0))
    for k, a in enumerate((-2.4, -1.57, -0.74)):
        L = 2 + st * 1.1
        leaf(v, 8 + math.cos(a) * L * 0.6, 15 + math.sin(a) * L * 0.9, L * 0.7, L * 0.38, a, shade(teal, 0.9 + 0.1 * k))
    if has(c.base, "top") and st >= 3:
        v.part(v.ell(8, 6, 2.4, 3.6), (70, 150, 150), dome=1.4)
        v.part(v.ell(8, 2.8, 2.6, 0.9), (150, 80, 170), dome=0.5)
    return lit(v)


@own
def bamboo_shoot(c):
    v = Cv(c, 85)
    v.part(v.seg(8, 16, 8, 8, 0.7), (110, 160, 60), dome=0.6)
    leaf(v, 6.2, 8.6, 1.8, 0.6, math.pi + 0.6, np.array((90, 160, 60.0)))
    leaf(v, 9.8, 9.6, 1.8, 0.6, -0.6, np.array((90, 160, 60.0)))
    return lit(v)


@own
def nether_wart(c):
    v = Cv(c, 86); st = g.stage_of(c.base) or 0
    for x in (3.5, 8, 12.5):
        hh = 2 + st * 1.6
        v.part(v.seg(x, 16, x, 16 - hh, 0.35), (120, 30, 30), dome=0.3)
        for k in range(1 + st):
            v.part(v.circ(x + v.r.uniform(-0.8, 0.8), 16 - hh - k * 1.1, 0.8 + st * 0.35), (170, 40, 40), dome=0.8, spec=0.3)
    return lit(v)


# ------------------------------------------------------------- hanging ------
@own
def hanging(c, col, bulb=None, tip=True, curl=False, up=False):
    v = Cv(c, 87)
    col = np.asarray(col, float)
    for x0 in (3, 6.4, 9.6, 13):
        ph = v.r.uniform(0, 6.28)
        L = v.r.uniform(10, 16) if tip else 16
        pts = []
        for t in np.linspace(0, 1, 30):
            y = (16 - t * L) if up else t * L
            x = x0 + (1.6 * math.sin(ph + t * 9) if curl else 0.5 * math.sin(ph + t * 4))
            pts.append((x, y))
        for (a, b), (cc, d) in zip(pts[:-1], pts[1:]):
            v.part(v.seg(a, b, cc, d, 0.45), shade(col, v.r.uniform(0.85, 1.1)), dome=0.35)
        if tip and bulb is not None:
            v.part(v.circ(pts[-1][0], pts[-1][1], 0.9), bulb, dome=0.7, spec=0.3)
    return lit(v)


@own
def kelp(c):
    v = Cv(c, 88)
    for x0 in (5, 11):
        ph = v.r.uniform(0, 6.28)
        for y in np.arange(0, 16, 0.25):
            x = x0 + 1.4 * math.sin(ph + y * 0.6)
            v.part(v.ell(x, y, 1.3, 0.35, 0.3 * math.cos(ph + y * 0.6)), (70, 130, 50), dome=0.4, noise=0.1)
        for y in np.arange(v.r.uniform(1, 3), 16, 4):
            v.part(v.circ(x0 + 1.4 * math.sin(ph + y * 0.6) + 1.6, y, 0.8), (150, 170, 60), dome=0.6, spec=0.4)
    return lit(v)


@own
def mangrove_roots(c):
    v = Cv(c, 89)
    col = np.array((96, 70, 50.0))
    for k in range(5):
        x0 = v.r.uniform(0, 16)
        pts = [(x0 + 5 * math.sin(t * math.pi) * (1 if k % 2 else -1), t * 16) for t in np.linspace(0, 1, 24)]
        for (a, b), (cc, d) in zip(pts[:-1], pts[1:]):
            v.part(v.seg(a % 16, b, cc % 16, d, 0.8), shade(col, v.r.uniform(0.85, 1.1)), dome=0.6)
    return lit(v)


@own
def frogspawn(c):
    v = Cv(c, 90)
    for _ in range(9):
        x, y = v.r.uniform(2, 14), v.r.uniform(2, 14)
        v.part(v.circ(x, y, 1.6), (170, 190, 170), dome=1.2, spec=0.6)
        v.part(v.circ(x, y, 0.55), (30, 30, 34), dome=0.3)
    v.a[v.a > 0] = 200
    return lit(v)


@own
def tall_bottom(c):
    v = Cv(c, 91)
    green = np.array((60, 130, 50.0))
    for x in (5, 8, 11):
        v.part(v.seg(x, 16, x + v.r.uniform(-0.5, 0.5), 0, 0.45), shade(green, 0.85), dome=0.4)
    for i, y in enumerate(np.arange(14, 1, -2.6)):
        for x, sgn in ((5, -1), (11, 1)):
            leaf(v, x + sgn * 1.8, y - (i % 2), 2.4, 1.0, (math.pi + 0.4) if sgn < 0 else -0.4,
                 shade(green, v.r.uniform(0.85, 1.12)))
    return lit(v)


@own
def cherry_sapling(c):
    v = Cv(c, 92)
    v.part(v.seg(8, 16, 8, 8, 0.6), (70, 40, 46), dome=0.5)
    for _ in range(46):
        a = v.r.uniform(0, 6.28); d = v.r.uniform(0, 4.2)
        v.part(v.circ(8 + math.cos(a) * d, 6 + math.sin(a) * d * 0.8, v.r.uniform(0.5, 0.9)),
               shade(np.array((240, 170, 200.0)), v.r.uniform(0.85, 1.1)), dome=0.4)
    return lit(v)


@own
def bamboo_leaves(c):
    v = Cv(c, 93)
    n = 1 if has(c.base, "single") else (5 if has(c.base, "large") else 3)
    for k in range(n):
        a = -0.6 - k * 0.5
        leaf(v, 6 + k * 1.6, 8 + k * 1.2, 4.2, 0.8, a, shade(np.array((90, 160, 60.0)), v.r.uniform(0.85, 1.1)))
    return lit(v)


# ---------------------------------------------------------- model parts -----
def ice(c):
    b = Blk(c, 94)
    st = g.stage_of(c.base.replace("frosted_ice_", "stage_")) or 0
    col = c.pal.main
    b.paint(b.all(), col, 0.92 + 0.12 * b.noise(8, 3), 0.5)
    for _ in range(14):
        b.paint(b.circ(b.r.uniform(0, 16), b.r.uniform(0, 16), b.r.uniform(0.2, 0.5)), (240, 250, 255), 1.0, 0.7)
    streak = (h.fbm(b.H, b.W, b.r, 2, 2, b.W // 2) > 0.75)
    b.paint(streak, shade(col, 1.15), 1.0, 0.6)
    if has(c.base, "frosted"):
        cr = h.ridged(b.noise(8, 3)) > (0.995 - st * 0.012)
        b.paint(cr, (240, 248, 255), 1.0, 0.2)
    return b.done(spec=0.6, shin=50)


def anvil_top(c):
    b = Blk(c, 95)
    b.metal(b.all(), (70, 72, 78))
    ham = h.fbm(b.H, b.W, b.r, 2, 2)
    b.paint(b.all(), (70, 72, 78), 0.88 + 0.22 * ham, 0.5 + 0.3 * ham)
    b.bevel(b.rect(1, 1, 15, 15), 0.25, 1.0)
    n = 0 if c.base.startswith("anvil") else (6 if has(c.base, "chipped") else 14)
    for _ in range(n):
        x, y = b.r.uniform(2, 14), b.r.uniform(2, 14); a = b.r.uniform(0, 6.28)
        for k in range(4):
            x2, y2 = x + math.cos(a) * 1.2, y + math.sin(a) * 1.2
            b.paint(b.seg(x, y, x2, y2, 0.2), (30, 30, 34), 1.0, 0.1)
            x, y, a = x2, y2, a + b.r.uniform(-0.7, 0.7)
    return b.done(spec=0.5, metal=True)


def bell(c):
    b = Blk(c, 96)
    b.metal(b.all(), GOLDC)
    yy = b.Y / 16
    ham = h.fbm(b.H, b.W, b.r, 2, 2)                                     # hathode ki chot (hammered)
    b.paint(b.all(), GOLDC, 0.82 + 0.28 * np.cos((b.X - 5) / 16 * math.pi) ** 2 + 0.12 * (ham - 0.5), 0.5 + 0.25 * ham)
    b.paint(np.abs(b.Y - 11) < 0.7, shade(GOLDC, 0.75), 1.0, 0.8)
    return b.done(spec=0.9, shin=40, metal=True)


def grindstone(c):
    b = Blk(c, 97)
    if has(c.base, "pivot"):
        b.wood(b.all(), (120, 84, 50))
    else:
        b.stone(b.all(), (150, 148, 144), rough=1.6)
        if has(c.base, "round", "side"):
            rr = np.hypot(b.X - 8, b.Y - 8)
            b.paint(np.abs(rr - 6.5) < 0.6, (110, 108, 104), 1.0, 0.8)
            b.paint(b.circ(8, 8, 1.4), (90, 70, 50), 1.0, 0.7)
    return b.done()


def sniffer_egg(c):
    b = Blk(c, 98)
    base = (160, 40, 40)
    b.paint(b.all(), base, 0.9 + 0.15 * b.noise(6, 3), 0.5)
    mot = (h.nrm(b.noise(4, 3)) > 0.68) | (b.r.random((b.H, b.W)) > 0.985)   # zyada laal, hare dhabbe + chhite
    b.paint(mot, (60, 120, 70), 0.9 + 0.2 * b.noise(2, 2), 0.55)
    n = 0 if has(c.base, "not_cracked") else (0.988 if has(c.base, "slightly") else 0.97)
    if n:
        cr = h.ridged(b.noise(8, 3)) > n
        b.paint(cr, (30, 20, 20), 1.0, 0.1)
    return b.done(spec=0.15)


def sculk_side(c):
    b = Blk(c, 99)
    b.paint(b.all(), (16, 36, 44), 0.8 + 0.3 * b.noise(4, 3), 0.4)
    vein = h.ridged(b.noise(8, 3)) > 0.93
    b.paint(vein, (40, 200, 210), 0.8 + 0.3 * b.noise(2, 2), 0.6, glow=True)
    return b.done()


def slime_block(c):
    b = Blk(c, 100)
    col = c.pal.main
    b.paint(b.all(), col, 0.95 + 0.08 * b.noise(6, 3), 0.5)
    b.paint(b.rect(3, 3, 13, 13), shade(col, 0.8), 0.95 + 0.08 * b.noise(4, 2), 0.35)
    b.paint(~b.rect(1, 1, 15, 15), shade(col, 1.15), 1.0, 0.7)
    return b.done(spec=0.6, shin=40)


def saw(c):
    b = Blk(c, 101)
    b.metal(b.all(), (180, 182, 188))
    teeth = ((b.X % 2.0) < (b.Y % 2.0)) & (b.Y < 3)
    b.paint(teeth, (120, 122, 128), 1.0, 0.3)
    return b.done(spec=0.8, metal=True)


def cake(c):
    b = Blk(c, 102)
    if has(c.base, "top"):
        b.paint(b.all(), (246, 244, 236), 0.95 + 0.06 * b.noise(4, 2), 0.6)
        for x, y in ((5, 5), (11, 5), (8, 8), (5, 11), (11, 11)):
            b.paint(b.circ(x, y, 0.9), (210, 30, 40), 1.0, 0.9)
    else:
        b.paint(b.all(), (200, 130, 70), 0.85 + 0.25 * b.noise(2, 2), 0.4)
        rows = np.where((c.ref[..., 3] > 0).any(1))[0]
        y0 = rows[0] if len(rows) else 0                      # model ka upar wala kinara
        b.paint(b.rect(0, y0, 16, y0 + 2.2), (246, 244, 236), 1.0, 0.7)
        drip = b.rect(0, y0 + 2.2, 16, y0 + 3.6) & (np.sin(b.X * 1.8) > 0.2)
        b.paint(drip, (246, 244, 236), 1.0, 0.7)
    return b.done(spec=0.1)


@own
def brewing_stand(c):
    v = Cv(c, 103)
    v.part(v.rect(1, 13.6, 15, 16), (120, 120, 126), metal=True, spec=0.5, dome=0.5)
    v.part(v.seg(8, 14, 8, 2, 0.6), (190, 150, 60), metal=True, spec=0.7, dome=0.5)
    for a in (-2.6, -0.5, 1.57):
        v.part(v.seg(8, 7, 8 + 5 * math.cos(a), 7 - 2 * math.sin(a) + 3, 0.35), (150, 120, 60), metal=True, dome=0.3)
    v.part(v.circ(8, 2, 1.0), (230, 200, 90), metal=True, spec=0.8)
    return lit(v)


@own
def rail_corner(c):
    v = Cv(c, 104)
    rr = np.hypot(v.X - 16, v.Y - 16)
    th = np.arctan2(v.Y - 16, v.X - 16)
    for k in range(7):
        a = -math.pi + (k + 0.5) * (math.pi / 2) / 7
        tie = v.seg(16 + 4.5 * math.cos(a), 16 + 4.5 * math.sin(a), 16 + 13.5 * math.cos(a), 16 + 13.5 * math.sin(a), 0.9)
        v.part(tie, (120, 84, 50), wood=True, dome=0.6)
    for R in (6.5, 11.5):
        v.part((np.abs(rr - R) < 0.6) & (th < -math.pi / 2), (170, 172, 178), metal=True, spec=0.7, dome=0.5)
    return lit(v)


@own
def bars(c):
    v = Cv(c, 105)
    col = c.pal.main if not has(c.base, "iron") else IRONC
    for x in (1.5, 5.5, 9.5, 13.5):
        v.part(v.rect(x, 0, x + 1.2, 16), col, metal=True, spec=0.6, dome=0.5)
    for y in (3, 12):
        v.part(v.rect(0, y, 16, y + 1.0), shade(col, 0.9), metal=True, spec=0.6, dome=0.5)
        for x in (2.1, 6.1, 10.1, 14.1):
            v.part(v.circ(x, y + 0.5, 0.55), shade(col, 1.15), metal=True, spec=0.8, dome=0.4)
    return lit(v)


@own
def door_cutout(c):
    img = door_full(c)
    yy, xx = h.grid(img.shape[0], img.shape[1])
    if has(c.base, "top"):
        win = (xx / S > 3.2) & (xx / S < 12.8) & (yy / S > 3.0) & (yy / S < 9.4)
        bar = (np.abs(xx / S - 8) < 0.45) | (np.abs(yy / S - 6.2) < 0.45)
        img[win & ~bar, 3] = 0
    return img


@own
def rail_straight(c):
    """Seedhi rails: lakdi ke sleepers + do patri; powered = sone ki patri + redstone, detector = plate,
    activator = laal torch line. _on par roshni."""
    v = Cv(c, 106)
    b = c.base
    on = has(b, "_on")
    for y in np.arange(1.0, 16, 3.2):
        v.part(v.rect(1.2, y, 14.8, y + 1.8), (120, 84, 50), wood=True, dome=0.6)
    rail = GOLDC if has(b, "powered") else IRONC
    for x in (3.0, 11.6):
        v.part(v.rect(x, 0, x + 1.4, 16), rail, metal=True, spec=0.8, dome=0.5)
    if has(b, "powered", "activator"):
        red = (255, 60, 40) if on else (110, 26, 22)
        v.part(v.rect(7.4, 0, 8.6, 16), red, dome=0.3, glow=on)
    if has(b, "detector"):
        v.part(v.rect(6, 6, 10, 10), (150, 150, 156) if not on else (255, 80, 60), metal=not on, spec=0.6, dome=0.5, glow=on)
    return lit(v)


def rail_glow(img, c):
    if not has(c.base, "_on"):
        return img
    rgb = img[..., :3]
    red = (rgb[..., 0] > rgb[..., 1] * 1.3) & (img[..., 3] > 0)
    rgb[red] = np.clip(rgb[red] * 1.3 + np.array([40, 0, 0]), 0, 255)
    return img


def campfire_log(c):
    img = h.hd_bark(c)
    H, W = img.shape[:2]
    yy, xx = h.grid(H, W)
    return img


def override(c, fn):
    b = c.base
    if b.startswith("beetroots_stage"):
        return beetroot
    if b.startswith("carrots_stage"):
        return carrots
    if b.startswith("sweet_berry_bush"):
        return sweet_berry
    if b.startswith("torchflower_crop"):
        return torchflower_crop
    if b.startswith("pitcher_crop"):
        return pitcher_crop
    if b == "bamboo_stage0":
        return bamboo_shoot
    if b.startswith("nether_wart_stage"):
        return nether_wart
    if b.startswith("weeping_vines"):
        return own(lambda cc: hanging(cc, (150, 30, 36), bulb=(200, 50, 50), tip=not has(cc.base, "plant")))
    if b.startswith("twisting_vines"):
        return own(lambda cc: hanging(cc, (30, 150, 140), bulb=(60, 200, 180), tip=not has(cc.base, "plant"), curl=True, up=True))
    if b.startswith("kelp"):
        return kelp
    if b == "hanging_roots":
        return own(lambda cc: hanging(cc, (150, 110, 80), bulb=(170, 130, 90)))
    if b.startswith("pale_hanging_moss"):
        return own(lambda cc: hanging(cc, (140, 150, 140), bulb=None, tip=has(cc.base, "tip")))
    if b.startswith("mangrove_roots"):
        return mangrove_roots
    if b == "frogspawn":
        return frogspawn
    if b in ("lilac_bottom", "peony_bottom", "rose_bush_bottom", "sunflower_bottom"):
        return tall_bottom
    if b == "cherry_sapling":
        return cherry_sapling
    if b.startswith("bamboo_") and has(b, "leaves", "singleleaf"):
        return bamboo_leaves
    if b == "ice" or b.startswith("frosted_ice"):
        return ice
    if b.endswith("anvil_top"):
        return anvil_top
    if b.startswith("bell_"):
        return bell
    if b.startswith("grindstone"):
        return grindstone
    if b.startswith("sniffer_egg"):
        return sniffer_egg
    if has(b, "sculk_sensor_side", "sculk_shrieker_side", "input_side"):
        return sculk_side
    if b == "slime_block":
        return slime_block
    if b == "stonecutter_saw":
        return saw
    if b.startswith("cake_"):
        return cake
    if b == "brewing_stand":
        return brewing_stand
    if b == "rail_corner":
        return rail_corner
    if b in ("rail", "powered_rail", "powered_rail_on", "detector_rail", "detector_rail_on", "activator_rail",
             "activator_rail_on"):
        return rail_straight
    if b.endswith("_bars") or b == "iron_bars":
        return bars
    if b.endswith("_door_top") or b.endswith("_door_bottom"):
        if not has(b, "iron", "copper"):
            return door_cutout
    if b == "campfire_log":
        return campfire_log
    return None
