"""FireCraft Remastered HD — A-Z review ke baad blocks ki khaas designs (original).

A-Z QA ne bataya ke jo blocks kisi family me nahi aate wo "pathar par daraarein" ya "frame + heera"
fallback ban jate the. Yahan unki apni designs hain: lakdi ke darwaze/trapdoor/shelf/bamboo, machines
ke chehre, roshni wale blocks, aur naam se zahir hone wala structure.
Har design ek global height map + roshni (generate_hd.light) se banta hai; chamakne wale hisse
(glow) par roshni/saaya nahi lagta.
"""
import math
import numpy as np

import generate as g
import generate_hd as h

S = h.S
shade, mix, has = g.shade, g.mix, g.has
DARK = np.array((28, 24, 22), float)
IRONC = np.array((150, 152, 158), float)
GOLDC = np.array((236, 190, 64), float)


class Blk:
    def __init__(self, c, salt=0):
        self.c = c
        self.H, self.W = c.H * S, c.W * S
        self.alb = np.zeros((self.H, self.W, 3)); self.hgt = np.full((self.H, self.W), 0.5)
        self.glow = np.zeros((self.H, self.W), bool)
        yy, xx = h.grid(self.H, self.W)
        self.X, self.Y = xx / S, yy / S
        self.r = c.rng(700 + salt)

    # shapes (16-unit)
    def rect(self, x0, y0, x1, y1):
        return (self.X >= x0) & (self.X < x1) & (self.Y >= y0) & (self.Y < y1)

    def circ(self, cx, cy, rr):
        return np.hypot(self.X - cx, self.Y - cy) < rr

    def seg(self, x0, y0, x1, y1, w):
        dx, dy = x1 - x0, y1 - y0
        t = np.clip(((self.X - x0) * dx + (self.Y - y0) * dy) / (dx * dx + dy * dy + 1e-9), 0, 1)
        return np.hypot(self.X - (x0 + t * dx), self.Y - (y0 + t * dy)) < w

    def poly(self, pts):
        return h.poly(self.X, self.Y, pts)

    def noise(self, cell=8, oct=3, cx=None):
        return h.fbm(self.H, self.W, self.r, cell, oct, cx)

    def paint(self, m, col, tone=1.0, hgt=None, glow=False):
        col = np.asarray(col, float)
        t = tone if np.ndim(tone) else np.full((self.H, self.W), float(tone))
        self.alb[m] = h.tone_col(col, t)[m]
        if hgt is not None:
            hh = hgt if np.ndim(hgt) else np.full((self.H, self.W), float(hgt))
            self.hgt[m] = hh[m]
        if glow:
            self.glow |= m

    def all(self):
        return np.ones((self.H, self.W), bool)

    # material fills
    def wood(self, m, col, vertical=True, base_h=0.55):
        grain = self.noise(8, 3, 1) if vertical else h.fbm(self.H, self.W, self.r, 1, 3, 8)
        fine = self.noise(1, 1)
        self.paint(m, col, 0.84 + 0.24 * grain + 0.06 * (fine - 0.5), base_h + 0.08 * grain)

    def stone(self, m, col, rough=1.0, base_h=0.5):
        n = self.noise(16, 4); f = self.noise(1, 1)
        self.paint(m, col, 0.88 + 0.16 * n * rough + 0.08 * (f - 0.5) * rough, base_h + 0.15 * n * rough)

    def metal(self, m, col, base_h=0.6):
        b = h.fbm(self.H, self.W, self.r, 1, 2, self.W // 2)
        self.paint(m, col, 0.9 + 0.14 * b, base_h + 0.04 * b)

    def bevel(self, m, depth=0.25, width=1.0):
        d = h.dist_inside(m, int(width * S * 2))
        self.hgt[m] = (self.hgt + depth * np.clip(d / (width * S), 0, 1))[m]

    def recess(self, m, depth=0.4):
        self.hgt[m] -= depth

    def frame(self, col, w=1.4, wood=True):
        fr = ~self.rect(w, w, 16 - w, 16 - w)
        (self.wood if wood else self.stone)(fr, shade(col, 0.82))
        self.bevel(fr, 0.2, 0.8)
        return fr

    def done(self, k=2.6, spec=0.0, shin=24, metal=False, dirt=0.25):
        rgb = h.light(self.alb, self.hgt, k=k, spec=spec, shin=shin, wrap=False, metal=metal, dirt=dirt)
        rgb[self.glow] = np.clip(self.alb[self.glow], 0, 255)
        return h.out_rgba(rgb)


def wood_col(c):
    return c.pal.main


# ---------------------------------------------------------------- wood ------
def door_full(c):
    b = Blk(c, 1); col = wood_col(c)
    top = has(c.base, "top")
    for i in range(4):
        m = b.rect(i * 4, 0, i * 4 + 4, 16)
        b.wood(m, shade(col, 0.94 + 0.06 * (i % 2)))
        b.recess(m & (b.X > i * 4 + 3.75), 0.3)
    b.frame(col, 1.3)
    if top:
        win = b.rect(3.2, 3.0, 12.8, 9.4)
        b.paint(win, (52, 70, 84), b.noise(4, 2) * 0.2 + 0.85, 0.2)
        b.paint(win & ((np.abs(b.X - 8) < 0.45) | (np.abs(b.Y - 6.2) < 0.45)), shade(col, 0.85), 1.0, 0.55)
    else:
        brace = b.seg(2.6, 13.4, 13.4, 2.6, 0.9)
        b.wood(brace, shade(col, 1.06), vertical=False, base_h=0.75)
        b.paint(b.circ(12.0, 8.5 if top else 2.4, 0.6), GOLDC, 1.0, 0.95)
    for y in ((2.4, 12.4) if not top else (4.0,)):
        b.paint(b.rect(1.2, y, 3.4, y + 1.4), (60, 60, 66), 1.0, 0.8)
    return b.done(spec=0.05)


def trapdoor_full(c):
    b = Blk(c, 2); col = wood_col(c)
    for i in range(4):
        m = b.rect(0, i * 4, 16, i * 4 + 4)
        b.wood(m, shade(col, 0.95 + 0.05 * (i % 2)), vertical=False)
        b.recess(m & (b.Y > i * 4 + 3.7), 0.3)
    b.frame(col, 1.6)
    for x, y in ((5, 5), (11, 5), (5, 11), (11, 11)):
        b.paint(b.rect(x - 1.2, y - 1.2, x + 1.2, y + 1.2), (30, 24, 20), 1.0, 0.15)
    for x, y in ((1, 1), (15, 1), (1, 15), (15, 15)):
        b.paint(b.circ(x, y, 0.55), (70, 70, 76), 1.0, 0.95)
    return b.done(spec=0.05)


def shelf(c):
    b = Blk(c, 3); col = wood_col(c)
    b.wood(b.all(), shade(col, 0.55), base_h=0.25)                       # andar ki parchhai
    for y0 in (6.4, 13.6):
        m = b.rect(0, y0, 16, y0 + 2.4)
        b.wood(m, col, vertical=False, base_h=0.75)
        b.bevel(m, 0.15, 0.4)
    b.frame(col, 1.4)
    return b.done()


def bamboo_side(c):
    b = Blk(c, 4); col = c.pal.main
    for i in range(4):
        x0 = i * 4
        m = b.rect(x0, 0, x0 + 4, 16)
        cyl = np.clip(np.cos((b.X - x0 - 2) / 2.0 * (math.pi / 2)), 0, 1) ** 0.6
        b.paint(m, shade(col, 0.95 + 0.06 * (i % 2)), 0.8 + 0.28 * cyl + 0.06 * (b.noise(4, 2, 1) - 0.5), 0.3 + 0.5 * cyl)
        node = b.r.uniform(2, 14)
        for y in (node, (node + 8) % 16):
            nm = m & (np.abs(b.Y - y) < 0.4)
            b.paint(nm, shade(col, 0.72), 1.0, 0.85)
    return b.done(spec=0.12)


def bamboo_top(c):
    b = Blk(c, 5); col = c.pal.main
    b.paint(b.all(), shade(col, 0.4), 1.0, 0.1)
    for cy in (4, 12):
        for cx in (4, 12):
            ring = b.circ(cx, cy, 3.6) & ~b.circ(cx, cy, 2.2)
            b.paint(ring, col, 0.9 + 0.2 * b.noise(2, 2), 0.8)
            b.paint(b.circ(cx, cy, 2.2), (60, 50, 30), 1.0, 0.2)
    return b.done()


def bamboo_mosaic(c):
    b = Blk(c, 6); col = c.pal.main
    for row in range(4):
        for k in range(4):
            x0, y0 = k * 4, row * 4
            m = b.rect(x0, y0, x0 + 4, y0 + 4)
            vert = (row + k) % 2 == 0
            b.wood(m, shade(col, 0.92 + 0.08 * ((row * 3 + k) % 3) / 2), vertical=vert)
            b.recess(m & ((b.X > x0 + 3.7) | (b.Y > y0 + 3.7)), 0.35)
    return b.done()


# -------------------------------------------------------------- machines ----
def machine_base(b, c, metal=False):
    col = c.pal.least_sat_main() if not metal else c.pal.main
    (b.metal if metal else b.stone)(b.all(), col)
    fr = ~b.rect(1.2, 1.2, 14.8, 14.8)
    b.paint(fr, shade(col, 0.85), 1.0, 0.75)
    return col


def furnace(c):
    b = Blk(c, 7)
    on = has(c.base, "_on")
    blast = has(c.base, "blast")
    col = machine_base(b, c, metal=blast)
    if has(c.base, "smoker"):
        b.wood(b.rect(0, 0, 16, 3), (110, 80, 50), vertical=False, base_h=0.7)
    mouth = b.rect(4, 7.6, 12, 14)
    b.paint(mouth, DARK, 1.0, 0.1)
    if on:
        yy = (b.Y - 7.6) / 6.4
        fire = mouth & (b.noise(4, 3) * 0.5 + yy * 0.8 > 0.55)
        b.paint(mouth, (110, 40, 10), 1.0, 0.1, glow=True)
        b.paint(fire, (255, 168, 50), 0.85 + 0.3 * b.noise(2, 2), 0.1, glow=True)
    for x in (5.6, 8, 10.4):
        b.paint(mouth & (np.abs(b.X - x) < 0.35), (70, 70, 74), 1.0, 0.45)
    b.paint(b.rect(3.4, 6.4, 12.6, 7.6), shade(col, 1.1), 1.0, 0.8)          # lintel
    b.paint(b.rect(5, 2.6, 11, 4.6), shade(col, 0.7), 1.0, 0.3)              # vent
    return b.done(spec=0.2 if blast else 0.03, metal=blast)


def dispenser(c):
    b = Blk(c, 8)
    col = machine_base(b, c)
    if has(c.base, "dropper"):
        b.paint(b.rect(5, 5, 11, 11), DARK, 1.0, 0.1)
        b.paint(b.rect(5, 5, 11, 11) & ~b.rect(6, 6, 10, 10), shade(col, 0.7), 1.0, 0.35)
    else:
        b.paint(b.circ(8, 8, 3.6), shade(col, 0.75), 1.0, 0.4)
        b.paint(b.circ(8, 8, 2.6), DARK, 1.0, 0.05)
    return b.done()


def observer(c):
    b = Blk(c, 9)
    col = machine_base(b, c)
    base = c.base
    if has(base, "front"):
        for y in (5.0, 10.0):
            b.paint(b.rect(3, y, 13, y + 1.4), DARK, 1.0, 0.1)
        b.paint(b.rect(6.6, 7.0, 9.4, 9.4), shade(col, 1.15), 1.0, 0.85)
    elif has(base, "back"):
        on = has(base, "_on")
        b.paint(b.circ(8, 8, 2.2), (60, 20, 20), 1.0, 0.2)
        b.paint(b.circ(8, 8, 1.4), (255, 60, 40) if on else (110, 30, 26), 1.0, 0.6, glow=on)
    else:
        b.paint(b.rect(2, 7.2, 14, 8.8), (180, 40, 30), 1.0, 0.3)
    return b.done()


def crafter(c):
    b = Blk(c, 10)
    col = machine_base(b, c)
    lit = has(c.base, "crafting", "triggered")
    for i in range(3):
        for j in range(3):
            m = b.rect(3 + j * 3.6, 3 + i * 3.6, 5.8 + j * 3.6, 5.8 + i * 3.6)
            b.paint(m, (255, 170, 60) if lit and (i + j) % 2 == 0 else DARK, 1.0, 0.1, glow=lit and (i + j) % 2 == 0)
    return b.done()


def table_side(c):
    """crafting/fletching/cartography/smithing/loom/lectern ke sides: lakdi + apne auzaar."""
    b = Blk(c, 11); col = wood_col(c); base = c.base
    smith = has(base, "smithing")
    if smith:
        b.metal(b.all(), (52, 54, 62))
    else:
        for i in range(4):
            m = b.rect(0, i * 4, 16, i * 4 + 4)
            b.wood(m, shade(col, 0.95 + 0.05 * (i % 2)), vertical=False)
            b.recess(m & (b.Y > i * 4 + 3.7), 0.3)
    b.frame(col if not smith else np.array((70, 72, 80.0)), 1.2, wood=not smith)
    if has(base, "crafting_table"):
        b.paint(b.seg(4, 4, 7, 11, 0.5), (190, 190, 196), 1.0, 0.8)              # aari (saw)
        b.paint(b.seg(10, 4, 10, 12, 0.45), (120, 80, 46), 1.0, 0.75)            # hathora
        b.paint(b.rect(8.6, 3.4, 11.4, 5.2), IRONC, 1.0, 0.9)
    elif has(base, "fletching"):
        for k, x in enumerate((5, 8, 11)):
            b.paint(b.seg(x, 12.5, x, 4.5, 0.3), (140, 100, 60), 1.0, 0.7)
            b.paint(b.poly([(x - 1, 5.5), (x, 3.4), (x + 1, 5.5)]), (190, 190, 196), 1.0, 0.85)
            b.paint(b.rect(x - 0.9, 11.2, x + 0.9, 13.2), (236, 236, 232), 1.0, 0.75)
    elif has(base, "cartography"):
        b.paint(b.rect(3.2, 3.6, 10.2, 11.6), (226, 212, 176), 1.0, 0.7)
        b.paint(b.rect(4.4, 5, 9, 10.2), (110, 150, 90), 0.9 + 0.2 * b.noise(2, 2), 0.7)
        b.paint(b.circ(11.8, 11.2, 1.8), (150, 152, 160), 1.0, 0.85)
        b.paint(b.seg(11.8, 11.2, 11.8, 9.8, 0.25), (200, 40, 36), 1.0, 0.9)
    elif smith:
        b.paint(b.seg(4.5, 11.5, 8.5, 5.5, 0.45), (120, 80, 46), 1.0, 0.75)
        b.paint(b.rect(7.4, 3.8, 10.4, 6.2), IRONC, 1.0, 0.9)
        b.paint(b.seg(11, 12, 12.5, 5, 0.3) | b.seg(12.6, 12, 12.0, 5, 0.3), IRONC, 1.0, 0.8)
    elif has(base, "loom"):
        for k, x in enumerate(np.arange(3.5, 13, 1.5)):
            b.paint(b.rect(x, 3, x + 0.6, 13), [(200, 60, 60), (230, 210, 120), (70, 110, 190), (236, 236, 232)][k % 4], 1.0, 0.7)
        b.paint(b.rect(2.6, 7.4, 13.4, 8.6), shade(col, 0.8), 1.0, 0.85)
    elif has(base, "lectern"):
        b.paint(b.rect(5, 4, 11, 12), (120, 60, 40), 1.0, 0.75)
        b.paint(b.rect(7.6, 4, 8.4, 12), (226, 212, 176), 1.0, 0.8)
    return b.done(spec=0.25 if smith else 0.04, metal=smith)


def beehive(c):
    b = Blk(c, 12); col = c.pal.main; base = c.base
    for i in range(8):
        m = b.rect(0, i * 2, 16, i * 2 + 2)
        b.paint(m, shade(col, 0.92 + 0.1 * (i % 2)), 0.85 + 0.25 * h.fbm(b.H, b.W, b.r, 1, 2, 4), 0.5 + 0.2 * np.sin((b.Y - i * 2) / 2 * math.pi))
    if has(base, "front"):
        b.paint(b.circ(8, 10, 2.4), DARK, 1.0, 0.0)
        b.paint(b.circ(8, 10, 3.0) & ~b.circ(8, 10, 2.4), shade(col, 0.75), 1.0, 0.7)
        if has(base, "honey"):
            for x, L in ((5, 4), (10.6, 3), (7.6, 5)):
                drip = b.seg(x, 1.4, x, 1.4 + L, 0.5) | b.circ(x, 1.4 + L, 0.75)
                b.paint(drip, (246, 170, 40), 1.0, 0.85)
            b.paint(b.rect(0, 0, 16, 1.6), (246, 170, 40), 1.0, 0.8)
    return b.done(spec=0.1)


def barrel(c):
    b = Blk(c, 13); col = wood_col(c); base = c.base
    if has(base, "side"):
        for i in range(5):
            m = b.rect(i * 3.2, 0, i * 3.2 + 3.2, 16)
            b.wood(m, shade(col, 0.92 + 0.08 * (i % 2)))
            b.recess(m & (b.X > i * 3.2 + 2.95), 0.35)
        for y in (2.4, 12.4):
            b.metal(b.rect(0, y, 16, y + 1.4), (70, 70, 76), 0.85)
            for x in (1.6, 8, 14.4):
                b.paint(b.circ(x, y + 0.7, 0.35), (130, 130, 136), 1.0, 1.0)
    else:
        rr = np.hypot(b.X - 8, b.Y - 8)
        b.metal(rr >= 7.2, (70, 70, 76), 0.8)
        lid = rr < 7.2
        if has(base, "open"):
            b.paint(lid, DARK, 0.9 + 0.2 * b.noise(4, 2), 0.1)
        else:
            for i in range(4):
                m = lid & (b.X >= 1 + i * 3.5) & (b.X < 1 + (i + 1) * 3.5)
                b.wood(m, shade(col, 0.95 + 0.05 * (i % 2)))
                b.recess(m & (b.X > 1 + i * 3.5 + 3.2), 0.35)
    return b.done(spec=0.06)


def jukebox_note(c):
    b = Blk(c, 14); col = wood_col(c); base = c.base
    for i in range(4):
        m = b.rect(i * 4, 0, i * 4 + 4, 16)
        b.wood(m, shade(col, 0.95 + 0.05 * (i % 2)))
    b.frame(col, 1.4)
    if has(base, "jukebox") and has(base, "top"):
        b.paint(b.rect(3, 7.2, 13, 8.8), DARK, 1.0, 0.05)
    elif has(base, "jukebox"):
        b.paint(b.poly([(8, 4), (12, 8), (8, 12), (4, 8)]), (90, 200, 200), 1.0, 0.8)
    else:   # note block: speaker grille + sur ka nishaan
        for i in range(4):
            for j in range(4):
                b.paint(b.circ(4 + j * 2.7, 4 + i * 2.7, 0.65), DARK, 1.0, 0.1)
        b.paint(b.circ(11.2, 12.4, 1.1) | b.seg(12.2, 12.4, 12.2, 7.6, 0.28), (240, 230, 200), 1.0, 0.9)
    return b.done()


STRUCT_ICON = {"corner": "corner", "data": "data", "load": "load", "save": "save"}


def structure(c):
    b = Blk(c, 15); base = c.base
    col = c.pal.main
    b.stone(b.all(), shade(col, 0.7), rough=0.5)
    b.frame(col, 1.6, wood=False)
    ic = (220, 210, 240)
    if has(base, "corner"):
        for (x, y, sx, sy) in ((4, 4, 1, 1), (12, 4, -1, 1), (4, 12, 1, -1), (12, 12, -1, -1)):
            b.paint(b.seg(x, y, x + 2.4 * sx, y, 0.4) | b.seg(x, y, x, y + 2.4 * sy, 0.4), ic, 1.0, 0.85)
    elif has(base, "data"):
        for y in (5, 7.5, 10):
            b.paint(b.rect(4, y, 12, y + 1), ic, 1.0, 0.85)
    elif has(base, "load"):
        b.paint(b.poly([(8, 12), (4.4, 7.6), (11.6, 7.6)]) | b.rect(7, 3.6, 9, 8), ic, 1.0, 0.85)
    elif has(base, "save"):
        b.paint(b.poly([(8, 4), (4.4, 8.4), (11.6, 8.4)]) | b.rect(7, 8, 9, 12.4), ic, 1.0, 0.85)
    elif has(base, "jigsaw"):
        piece = b.rect(4.6, 4.6, 11.4, 11.4) | b.circ(8, 4.6, 1.3) | b.circ(11.4, 8, 1.3)
        b.paint(piece & ~b.circ(4.6, 8, 1.3), ic, 1.0, 0.85)
    else:
        b.paint(b.rect(4.6, 4.6, 11.4, 11.4) & ~b.rect(5.6, 5.6, 10.4, 10.4), ic, 1.0, 0.85)
    return b.done()


# ---------------------------------------------------------------- lights ----
def lamp(c):
    b = Blk(c, 16)
    on = has(c.base, "_on")
    col = c.pal.main
    b.wood(b.all(), (96, 62, 36), vertical=False)
    b.frame(np.array((96, 62, 36.0)), 1.6)
    glass = b.rect(2.6, 2.6, 13.4, 13.4)
    if on:
        yy, xx = b.Y - 8, b.X - 8
        core = np.clip(1 - np.hypot(yy, xx) / 7.5, 0, 1)
        b.paint(glass, (255, 210, 120), 0.8 + 0.45 * core, 0.4, glow=True)
    else:
        b.paint(glass, (70, 46, 34), 0.9 + 0.2 * b.noise(4, 2), 0.3)
    fil = glass & ((np.abs(b.X - 8) < 0.3) | (np.abs(b.Y - 8) < 0.3) | (np.abs(np.abs(b.X - 8) - np.abs(b.Y - 8)) < 0.3))
    b.paint(fil, (255, 240, 200) if on else (40, 30, 26), 1.0, 0.5, glow=on)
    return b.done()


def froglight(c):
    b = Blk(c, 17)
    col = c.pal.accent if not c.pal.grey else c.pal.main
    rr = np.maximum(np.abs(b.X - 8), np.abs(b.Y - 8))
    inner = rr < 6.4
    b.paint(inner, mix(col, (255, 255, 240), 0.35), 0.9 + 0.2 * b.noise(6, 3), 0.5, glow=True)
    rim = ~inner
    b.paint(rim, shade(col, 0.8), 0.9 + 0.2 * b.noise(2, 2), 0.7)
    if has(c.base, "top"):
        b.paint(b.circ(8, 8, 2.8), mix(col, (255, 255, 255), 0.6), 1.0, 0.5, glow=True)
    return b.done()


def glowstone(c):
    b = Blk(c, 18)
    b.paint(b.all(), (120, 84, 40), 0.85 + 0.25 * b.noise(4, 3), 0.3)
    r = b.r
    for _ in range(26):
        cx, cy = r.uniform(0, 16), r.uniform(0, 16)
        rr = r.uniform(0.8, 1.8)
        d = np.hypot(np.minimum(np.abs(b.X - cx), 16 - np.abs(b.X - cx)), np.minimum(np.abs(b.Y - cy), 16 - np.abs(b.Y - cy)))
        m = d < rr
        b.paint(m, (255, 226, 140), 0.85 + 0.3 * (1 - d / rr), 0.6, glow=True)
    return b.done()


def sea_lantern(c):
    b = Blk(c, 19)
    col = c.pal.light
    b.paint(b.all(), shade(col, 0.7), 0.9 + 0.2 * b.noise(2, 2), 0.7)
    for i in range(3):
        for j in range(3):
            m = b.rect(1.2 + j * 4.8, 1.2 + i * 4.8, 5.2 + j * 4.8, 5.2 + i * 4.8)
            d = np.hypot(b.X - (3.2 + j * 4.8), b.Y - (3.2 + i * 4.8))
            b.paint(m, mix(col, (255, 255, 255), 0.4), 0.85 + 0.3 * np.clip(1 - d / 2.8, 0, 1), 0.4, glow=True)
    return b.done()


def pumpkin_face(c):
    b = Blk(c, 20)
    lit = has(c.base, "jack")
    col = np.array((206, 112, 26.0)) if lit else c.pal.main      # jack ka ref face-glow se peela hota hai
    ribs = 0.9 + 0.14 * np.cos((b.X + 0.5) / 16 * 2 * math.pi * 3)
    b.paint(b.all(), col, ribs * (0.95 + 0.1 * b.noise(4, 2)), 0.5 + 0.2 * ribs)
    inside = (255, 200, 70) if lit else (60, 30, 12)
    face = b.poly([(3.2, 6.6), (5.0, 4.0), (6.8, 6.6)]) | b.poly([(9.2, 6.6), (11.0, 4.0), (12.8, 6.6)])
    face |= b.poly([(3.0, 9.4), (13.0, 9.4), (12.0, 12.6), (10.4, 11.4), (8.8, 12.8), (7.2, 11.4), (5.6, 12.8), (4.0, 11.6)])
    b.paint(face, inside, 1.0, 0.1, glow=lit)
    return b.done(spec=0.1)


def copper_bulb(c):
    b = Blk(c, 21)
    col = c.pal.main
    lit, powered = has(c.base, "lit"), has(c.base, "powered")
    b.metal(b.all(), col)
    b.frame(col, 1.6, wood=False)
    glass = b.circ(8, 8, 4.6)
    if lit:
        core = np.clip(1 - np.hypot(b.X - 8, b.Y - 8) / 4.6, 0, 1)
        b.paint(glass, (255, 220, 150), 0.8 + 0.4 * core, 0.4, glow=True)
    else:
        b.paint(glass, (54, 44, 40), 0.9 + 0.2 * b.noise(2, 2), 0.3)
    b.paint(b.circ(8, 8, 5.4) & ~glass, shade(col, 0.75), 1.0, 0.8)
    if powered:
        b.paint(b.circ(8, 8, 0.9), (255, 50, 40), 1.0, 0.6, glow=True)
    return b.done(spec=0.4, metal=True)


def redstone_plate(c):
    """comparator / repeater: chikna pathar + redstone ki lakeerein + torch ke nuqte."""
    b = Blk(c, 22)
    on = has(c.base, "_on")
    b.stone(b.all(), (168, 168, 168), rough=0.4)
    red = (255, 60, 40) if on else (110, 26, 22)
    b.paint(b.rect(7.4, 2, 8.6, 14) | b.rect(3, 7.4, 13, 8.6), red, 1.0, 0.55, glow=on)
    for x, y in ((8, 3.2), (4.4, 11.6), (11.6, 11.6)) if has(c.base, "comparator") else ((8, 4), (8, 11)):
        b.paint(b.circ(x, y, 1.3), (110, 80, 50), 1.0, 0.9)
        b.paint(b.circ(x, y, 0.7), red, 1.0, 1.0, glow=on)
    return b.done()


def daylight(c):
    b = Blk(c, 23)
    inv = has(c.base, "inverted")
    b.wood(b.all(), (120, 92, 60), vertical=False)
    b.frame(np.array((120, 92, 60.0)), 1.6)
    for i in range(3):
        for j in range(3):
            m = b.rect(2.6 + j * 3.8, 2.6 + i * 3.8, 5.8 + j * 3.8, 5.8 + i * 3.8)
            b.paint(m, (60, 70, 120) if inv else (150, 200, 230), 0.9 + 0.2 * b.noise(2, 2), 0.35)
    return b.done(spec=0.4)


# ------------------------------------------------------------- structure ----
def shulker(c):
    b = Blk(c, 24)
    col = c.pal.main
    b.paint(b.all(), col, 0.9 + 0.12 * b.noise(6, 3) + 0.05 * (b.noise(1, 1) - 0.5), 0.55)
    seam = np.abs(b.Y - 8) < 0.9
    b.paint(seam, shade(col, 0.7), 1.0, 0.2)
    b.paint((np.abs(b.Y - 6.8) < 0.4), shade(col, 1.15), 1.0, 0.7)
    b.bevel(b.rect(0.6, 0.6, 15.4, 15.4), 0.15, 0.8)
    return b.done(spec=0.25)


def honeycomb(c):
    b = Blk(c, 25)
    col = c.pal.main
    sz = 2.6
    yy, xx = b.Y / sz, b.X / (sz * 0.866)
    row = np.floor(yy)
    xo = xx + (row % 2) * 0.5
    fy, fx = yy - row, xo - np.floor(xo)
    d = np.maximum(np.abs(fx - 0.5) * 1.15, np.abs(fy - 0.5))
    wall = d > 0.4
    b.paint(~wall, col, 0.9 + 0.2 * (1 - d * 2), 0.3)
    b.paint(wall, shade(col, 0.72), 1.0, 0.8)
    return b.done(spec=0.4, shin=30)


def sponge(c):
    b = Blk(c, 26)
    wet = has(c.base, "wet")
    col = c.pal.main
    b.paint(b.all(), col, 0.9 + 0.15 * b.noise(4, 3), 0.6)
    for _ in range(46):
        x, y, rr = b.r.uniform(0, 16), b.r.uniform(0, 16), b.r.uniform(0.35, 0.9)
        b.paint(b.circ(x, y, rr), shade(col, 0.55), 1.0, 0.1)
    return b.done(spec=0.45 if wet else 0.05, shin=30)


def raw_block(c):
    b = Blk(c, 27)
    col = c.pal.main
    n = 10
    pts = b.r.random((n, 2)) * [b.H, b.W]
    d1, d2, cell = h.voronoi(b.H, b.W, pts)
    tone = b.r.uniform(0.85, 1.12, n)[cell]
    dome = np.sqrt(np.clip((d2 - d1) / (2.5 * S), 0, 1))
    b.paint(b.all(), col, tone * (0.9 + 0.15 * b.noise(2, 2)), 0.3 + 0.6 * dome)
    b.paint((d2 - d1) < 0.5, shade(col, 0.55), 1.0, 0.0)
    return b.done(k=3.2, spec=0.25)


def tiles(c, n=4, cracked=False):
    b = Blk(c, 28)
    col = c.pal.least_sat_main()
    sz = 16 / n
    for i in range(n):
        for j in range(n):
            m = b.rect(j * sz, i * sz, (j + 1) * sz, (i + 1) * sz)
            b.stone(m, shade(col, b.r.uniform(0.9, 1.08)), rough=0.6, base_h=0.6)
    grout = ((b.X % sz) < 0.45) | ((b.Y % sz) < 0.45)
    b.paint(grout, shade(col, 0.6), 1.0, 0.1)
    if cracked:
        cr = (h.ridged(b.noise(8, 3)) > 0.97)
        b.paint(cr, shade(col, 0.45), 1.0, 0.05)
    return b.done()


def pillar(c):
    b = Blk(c, 29)
    col = c.pal.main
    if has(c.base, "top"):
        rr = np.hypot(b.X - 8, b.Y - 8)
        rings = 0.92 + 0.08 * np.cos(rr * 1.6)
        if has(c.base, "bone"):
            b.paint(b.all(), col, rings, 0.6)
            b.paint(b.circ(8, 8, 3.6), shade(col, 0.82), 0.9 + 0.2 * b.noise(1, 1), 0.3)
        else:
            b.paint(b.all(), col, rings * (0.95 + 0.08 * b.noise(4, 2)), 0.5 + 0.1 * np.cos(rr * 1.6))
        b.bevel(b.rect(0.6, 0.6, 15.4, 15.4), 0.15, 0.6)
    else:
        flute = np.cos((b.X % 2.0) / 2.0 * 2 * math.pi)
        b.paint(b.all(), col, 0.94 + 0.06 * flute + 0.05 * (b.noise(8, 2, 1) - 0.5), 0.5 + 0.2 * flute)
        b.paint(b.rect(0, 0, 16, 1.2) | b.rect(0, 14.8, 16, 16), shade(col, 1.05), 1.0, 0.8)
    return b.done(spec=0.1)


def chiseled_bookshelf(c):
    b = Blk(c, 30)
    col = c.pal.main
    b.wood(b.all(), col)
    base = c.base
    for i in range(2):
        for j in range(3):
            x0, y0 = 1.2 + j * 4.8, 1.2 + i * 7.2
            slot = b.rect(x0, y0, x0 + 4.4, y0 + 6.4)
            if has(base, "occupied") or (has(base, "side") is False and not has(base, "empty") and not has(base, "top")):
                for k in range(3):
                    bc = [(150, 40, 40), (40, 70, 140), (50, 110, 60), (150, 120, 50), (110, 60, 120)][(i * 3 + j + k) % 5]
                    m = slot & b.rect(x0 + k * 1.46, y0 + 0.6, x0 + (k + 1) * 1.46 - 0.15, y0 + 6.4)
                    b.paint(m, bc, 0.9 + 0.15 * b.noise(2, 2, 1), 0.7)
            else:
                b.paint(slot, shade(col, 0.4), 1.0, 0.1)
    return b.done()


def creaking_heart(c):
    b = Blk(c, 31)
    base = c.base
    col = c.pal.main
    rid = h.ridged(h.fbm(b.H, b.W, b.r, b.H // 2, 4, b.W // 6))
    b.paint(b.all(), shade(col, 0.8), 0.6 + 0.6 * rid, 0.3 + 0.6 * rid)
    if has(base, "awake") or (not has(base, "dormant")):
        awake = has(base, "awake")
        core = b.circ(8, 8, 2.6 if awake else 2.0)
        b.paint(core, (255, 140, 40) if awake else (120, 70, 40), 1.0, 0.4, glow=awake)
        b.paint(b.circ(8, 8, 1.0), (255, 230, 150) if awake else (60, 40, 30), 1.0, 0.5, glow=awake)
    return b.done()


def farmland(c):
    b = Blk(c, 32)
    moist = has(c.base, "moist")
    col = c.pal.main
    fur = np.sin(b.Y / 16 * 2 * math.pi * 4 + 0.6 * b.noise(8, 2))
    b.paint(b.all(), shade(col, 0.8 if moist else 1.0), 0.85 + 0.12 * fur + 0.1 * b.noise(1, 1), 0.4 + 0.3 * fur)
    return b.done(spec=0.2 if moist else 0.0)


def roots(c, base_fn):
    img = base_fn(c)
    b = Blk(c, 33)
    rgb = img[..., :3].copy(); hm = np.zeros((b.H, b.W))
    for _ in range(7):
        x, y = b.r.uniform(0, 16), b.r.uniform(0, 16)
        a = b.r.uniform(0, 6.28)
        for k in range(10):
            x2, y2 = x + math.cos(a) * 1.3, y + math.sin(a) * 1.3
            m = b.seg(x, y, x2, y2, 0.35)
            rgb[m] = (150, 112, 70)
            x, y = x2 % 16, y2 % 16
            a += b.r.uniform(-0.6, 0.6)
    img[..., :3] = rgb
    return img


def target(c):
    b = Blk(c, 34)
    rr = np.hypot(b.X - 8, b.Y - 8)
    ring = (np.floor(rr / 1.6) % 2 == 0)
    b.paint(b.all(), (232, 220, 180), 0.92 + 0.1 * b.noise(1, 2, 4), 0.5)
    b.paint(ring & (rr < 7.5), (200, 40, 36), 0.92 + 0.1 * b.noise(2, 2), 0.55)
    return b.done()


def mushroom_block(c):
    b = Blk(c, 35)
    col = c.pal.accent if np.ptp(c.pal.accent) > 40 else c.pal.main
    b.paint(b.all(), col, 0.9 + 0.15 * b.noise(6, 3), 0.5)
    red = col[0] > col[1] * 1.5
    for _ in range(7 if red else 10):
        x, y, rr = b.r.uniform(1, 15), b.r.uniform(1, 15), b.r.uniform(0.9, 1.8) if red else b.r.uniform(0.4, 0.8)
        b.paint(b.circ(x, y, rr), (238, 232, 220) if red else shade(col, 1.25), 1.0, 0.75)
    return b.done(spec=0.1)


def gilded(c):
    img = h.hd_stone(c, c.pal.least_sat_main())
    b = Blk(c, 36)
    rgb = img[..., :3]
    for _ in range(9):
        x, y, rr = b.r.uniform(0, 16), b.r.uniform(0, 16), b.r.uniform(0.5, 1.2)
        m = b.circ(x, y, rr)
        rgb[m] = h.tone_col(GOLDC, 0.85 + 0.35 * np.clip(1 - np.hypot(b.X - x, b.Y - y) / rr, 0, 1))[m]
    return img


def sandstone(c):
    b = Blk(c, 37)
    col = c.pal.main
    base = c.base
    if has(base, "top"):
        ripple = np.sin((b.X * 0.7 + b.Y * 0.3 + 2 * b.noise(16, 2)) * 1.6)
        b.paint(b.all(), col, 0.93 + 0.08 * b.noise(8, 3) + 0.1 * (b.noise(1, 1) - 0.5) + 0.03 * ripple, 0.5 + 0.1 * ripple)
        sp = b.r.random((b.H, b.W))
        b.paint(sp > 0.985, shade(col, 1.15), 1.0, 0.6); b.paint(sp < 0.01, shade(col, 0.8), 1.0, 0.4)
    else:
        strata = np.sin((b.Y / 16 * 6 + 0.4 * b.noise(16, 2)) * 2 * math.pi)
        b.paint(b.all(), col, 0.93 + 0.06 * strata + 0.06 * (b.noise(1, 1) - 0.5), 0.5 + 0.15 * strata)
        if has(base, "bottom"):
            b.paint(b.all(), col, 0.88 + 0.14 * b.noise(2, 3), 0.4 + 0.3 * b.noise(2, 3))
        else:
            b.paint(b.rect(0, 0, 16, 1.6), shade(col, 1.08), 1.0, 0.75)
    return b.done()


def wood_frame_leather(c):
    """item_frame / glow_item_frame block texture: lakdi ka frame + chamda."""
    b = Blk(c, 38)
    glow = has(c.base, "glow")
    b.paint(b.all(), (150, 96, 60) if not glow else (100, 160, 150), 0.9 + 0.15 * b.noise(4, 2), 0.4)
    b.frame(np.array((170, 130, 80.0)), 2.0)
    return b.done()


def campfire_lit_log(c):
    img = h.hd_bark(c)
    b = Blk(c, 39)
    rgb = img[..., :3]
    cr = (h.ridged(h.fbm(b.H, b.W, b.r, b.H // 2, 4, b.W // 6)) > 0.9)
    rgb[cr] = h.tone_col(np.array((255, 130, 40.0)), 0.8 + 0.4 * b.noise(2, 2))[cr]
    return img


def bed_up(c):
    b = Blk(c, 40)
    col = c.pal.main
    yy, xx = h.grid(b.H, b.W)
    k = np.where(xx % (2 * S) < S, (xx // (S // 2) + yy // (S // 2)) % 2, (yy // (S // 2) - xx // (S // 2)) % 2)
    b.paint(b.all(), col, np.where(k == 1, 1.04, 0.94) * (0.95 + 0.08 * b.noise(2, 2)), 0.5)
    if has(c.base, "head"):
        b.paint(b.rect(2, 1.4, 14, 6.4), (238, 236, 230), 0.92 + 0.1 * b.noise(4, 2), 0.8)
        b.bevel(b.rect(2, 1.4, 14, 6.4), 0.3, 1.2)
    return b.done()


def dried_ghast(c):
    b = Blk(c, 41)
    col = np.minimum(c.pal.main, 205)
    wr = np.sin((b.Y + 2 * b.noise(8, 2)) * 1.4)
    b.paint(b.all(), col, 0.9 + 0.06 * wr + 0.08 * b.noise(4, 3), 0.5 + 0.2 * wr)
    return b.done()


def polished_sheen(img):
    rgb = img[..., :3]
    H, W = rgb.shape[:2]
    yy, xx = h.grid(H, W)
    t = (xx + yy) / (H + W)
    band = np.exp(-((t - 0.32) / 0.08) ** 2) * 18
    img[..., :3] = np.clip(rgb + band[..., None], 0, 250)
    return img


# ------------------------------------------------------------- dispatch -----
def override(c, fn, full):
    """make_hd ke liye: block ka naam dekh kar khaas design (ya None)."""
    b = c.base
    W = ("acacia", "bamboo", "birch", "cherry", "crimson", "dark_oak", "jungle", "mangrove", "oak", "pale_oak",
         "spruce", "warped")
    wood = any(b.startswith(w + "_") for w in W)
    if has(b, "campfire_log_lit"):
        return campfire_lit_log
    if not full:
        return None
    if b.startswith("bamboo_block") or b.startswith("stripped_bamboo_block") or b in ("bamboo_fence_particle", "bamboo_fence_gate_particle"):
        return bamboo_top if b.endswith("_top") else bamboo_side
    if b == "bamboo_mosaic":
        return bamboo_mosaic
    if wood and (b.endswith("_door_top") or b.endswith("_door_bottom")):
        return door_full
    if wood and b.endswith("_trapdoor"):
        return trapdoor_full
    if b.endswith("_shelf") or has(b, "_shelf_"):
        return shelf
    if has(b, "furnace", "smoker") and has(b, "front"):
        return furnace
    if has(b, "dispenser", "dropper") and has(b, "front"):
        return dispenser
    if b.startswith("observer"):
        return observer
    if b.startswith("crafter"):
        return crafter
    if (has(b, "crafting_table") and has(b, "front", "side")) or has(b, "fletching_table", "cartography_table",
                                                                       "smithing_table", "loom_front", "loom_side",
                                                                       "lectern_front"):
        return table_side
    if has(b, "bee_nest", "beehive"):
        return beehive
    if b.startswith("barrel"):
        return barrel
    if b.startswith("jukebox") or b == "note_block":
        return jukebox_note
    if b.startswith("structure_block") or b.startswith("jigsaw"):
        return structure
    if b.startswith("redstone_lamp"):
        return lamp
    if has(b, "froglight"):
        return froglight
    if b == "glowstone":
        return glowstone
    if b == "sea_lantern":
        return sea_lantern
    if b in ("jack_o_lantern", "carved_pumpkin"):
        return pumpkin_face
    if has(b, "copper_bulb"):
        return copper_bulb
    if b.startswith("comparator") or b.startswith("repeater"):
        return redstone_plate
    if b.startswith("daylight_detector"):
        return daylight
    if has(b, "shulker_box"):
        return shulker
    if b == "honeycomb_block":
        return honeycomb
    if b in ("sponge", "wet_sponge"):
        return sponge
    if b.startswith("raw_") and b.endswith("_block"):
        return raw_block
    if b in ("deepslate_tiles", "cracked_deepslate_tiles"):
        return lambda cc: tiles(cc, 4, cracked=has(cc.base, "cracked"))
    if b == "purpur_block":
        return lambda cc: tiles(cc, 2)
    if has(b, "pillar") or b.startswith("bone_block") or b.startswith("polished_basalt"):
        return pillar
    if b.startswith("chiseled_bookshelf"):
        return chiseled_bookshelf
    if b.startswith("creaking_heart"):
        return creaking_heart
    if b.startswith("farmland"):
        return farmland
    if b in ("rooted_dirt",) or has(b, "muddy_mangrove_roots"):
        return lambda cc: roots(cc, h.hd_granular)
    if b == "target_side" or b == "target_top":
        return target
    if b in ("brown_mushroom_block", "red_mushroom_block"):
        return mushroom_block
    if b == "gilded_blackstone":
        return gilded
    if b in ("sandstone", "red_sandstone", "sandstone_top", "red_sandstone_top", "sandstone_bottom", "red_sandstone_bottom"):
        return sandstone
    if b in ("item_frame", "glow_item_frame"):
        return wood_frame_leather
    if has(b, "bed_") and b.endswith("_up"):
        return bed_up
    if b.startswith("dried_ghast"):
        return dried_ghast
    if b.startswith("polished_") and fn is not None:
        return None   # sheen post-process in make_hd
    return None
