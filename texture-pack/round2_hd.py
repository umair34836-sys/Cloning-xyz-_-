"""FireCraft Remastered HD — round-2 A-Z QA ke baad blocks aur cut-outs ke fixes.

Har function HD RGBA lautata hai; `own_alpha` wale apni transparency khud tay karte hain.
Ye module make_hd me sab se pehle dekha jata hai.
"""
import math
import numpy as np

import generate as g
import generate_hd as h
from shapes_hd import Cv
from blocks_hd2 import Blk, IRONC, GOLDC, DARK, table_side
from cutouts_hd import own, leaf, lit

S = h.S
shade, mix, has = g.shade, g.mix, g.has
BONE = np.array((214, 206, 180), float)
SCULK = np.array((14, 34, 42), float)
TEAL = np.array((40, 210, 220), float)


# ----------------------------------------------------------- full blocks ----
def dried_ghast(c):
    b = Blk(c, 300)
    import re
    m = re.search(r"hydration_(\d)", c.base)
    st = int(m.group(1)) if m else 0
    col = mix(np.array((150, 146, 140.0)), np.array((190, 205, 225.0)), st / 3)
    wr = np.sin((b.Y + 2.5 * b.noise(8, 2)) * (1.6 - 0.25 * st))
    b.paint(b.all(), col, 0.88 + (0.1 - 0.025 * st) * wr + 0.07 * b.noise(4, 3), 0.5 + (0.25 - 0.06 * st) * wr)
    if has(c.base, "north"):
        for ex in (5.0, 11.0):
            if st >= 2:
                b.paint(b.circ(ex, 7.0, 1.2), (30, 30, 36), 1.0, 0.2)
            else:
                b.paint(b.rect(ex - 1.4, 6.8, ex + 1.4, 7.4), (60, 56, 56), 1.0, 0.2)
        b.paint(b.rect(6.6, 11.0, 9.4, 11.8), (60, 56, 56), 1.0, 0.2)
    if has(c.base, "bottom"):
        for x in (3, 6.5, 9.5, 13):
            b.paint(b.circ(x, 8, 1.4), shade(col, 0.8), 1.0, 0.8)
    return b.done()


def smithing(c):
    b = Blk(c, 301)
    base = c.base
    if has(base, "top"):
        b.metal(b.all(), (52, 54, 62))
        for y in (5.2, 10.6):
            b.paint(np.abs(b.Y - y) < 0.3, (30, 30, 36), 1.0, 0.2)
        b.paint(b.rect(1.6, 1.6, 14.4, 14.4) & ~b.rect(2.4, 2.4, 13.6, 13.6), (90, 92, 100), 1.0, 0.8)
        return b.done(spec=0.5, metal=True)
    if has(base, "bottom"):
        b.wood(b.all(), (110, 76, 46), vertical=False)
        return b.done()
    if has(base, "front"):
        return table_side(c)
    b.wood(b.all(), (110, 76, 46))
    for y in (3.0, 12.0):
        b.metal(b.rect(0, y, 16, y + 1.4), (70, 72, 80), 0.85)
    return b.done(spec=0.15)


def cartography_top(c):
    b = Blk(c, 302)
    b.wood(b.all(), (120, 92, 60), vertical=False)
    b.paint(b.rect(2, 2, 14, 14), (226, 212, 176), 1.0, 0.65)
    n = b.noise(6, 3)
    land = b.rect(3.4, 3.4, 12.6, 12.6) & (n > 0.5)
    b.paint(b.rect(3.4, 3.4, 12.6, 12.6), (110, 150, 200), 0.95 + 0.05 * n, 0.6)
    b.paint(land, (110, 150, 80), 0.9 + 0.2 * n, 0.62)
    b.paint(b.seg(3, 14, 13, 13.2, 0.35), (60, 50, 40), 1.0, 0.8)
    return b.done()


def plank_face(c, col=None):
    b = Blk(c, 303)
    col = c.pal.main if col is None else col
    for i in range(4):
        m = b.rect(0, i * 4, 16, i * 4 + 4)
        b.wood(m, shade(np.asarray(col, float), 0.95 + 0.05 * (i % 2)), vertical=False)
        b.recess(m & (b.Y > i * 4 + 3.7), 0.3)
    return b.done()


def farmland(c):
    b = Blk(c, 304)
    moist = has(c.base, "moist")
    col = np.array((110, 74, 46.0)) if not moist else np.array((70, 46, 30.0))
    ridge = np.sin(b.Y / 16 * 2 * math.pi * 4)
    b.paint(b.all(), col, 0.86 + 0.12 * b.noise(1, 1) + 0.06 * ridge, 0.4 + 0.3 * ridge)
    for _ in range(40):
        x, y = b.r.uniform(0, 16), b.r.uniform(0, 16)
        rr = b.r.uniform(0.3, 0.8)
        b.paint(b.circ(x, y, rr), shade(col, b.r.uniform(0.75, 1.2)), 1.0, 0.8)
    return b.done(spec=0.25 if moist else 0.0)


def jack(c):
    from blocks_hd2 import pumpkin_face
    img = pumpkin_face(c)
    if has(c.base, "jack"):
        rgb = img[..., :3]
        hot = (rgb[..., 0] > 235) & (rgb[..., 1] > 215)
        rgb[hot] = np.clip(rgb[hot] * 1.05 + np.array([10, 10, 30]), 0, 255)
    return img


def polished(c):
    b = Blk(c, 305)
    col = c.pal.least_sat_main()
    b.stone(b.all(), col, rough=0.5)
    sp = b.r.random((b.H, b.W))
    b.paint(sp > 0.98, shade(col, 1.25), 1.0, 0.55); b.paint(sp < 0.015, shade(col, 0.75), 1.0, 0.45)
    t = (b.X + b.Y) / 32
    sheen = np.exp(-((t - 0.35) / 0.07) ** 2) * 0.18
    b.paint(b.all(), col, 1.0 + sheen + 0.04 * (b.noise(4, 2) - 0.5), b.hgt)
    b.bevel(b.rect(0.8, 0.8, 15.2, 15.2), 0.3, 0.8)
    return b.done(spec=0.35, shin=40)


def basalt(c):
    b = Blk(c, 306)
    col = c.pal.least_sat_main()
    if has(c.base, "top"):
        pts = b.r.random((9, 2)) * [b.H, b.W]
        d1, d2, cell = h.voronoi(b.H, b.W, pts)
        b.paint(b.all(), col, b.r.uniform(0.85, 1.1, 9)[cell] * (0.95 + 0.08 * b.noise(2, 2)), 0.6)
        b.paint((d2 - d1) < 0.6 * S, shade(col, 0.55), 1.0, 0.1)
    else:
        colm = np.floor(b.X / 3.2)
        t = 0.88 + 0.1 * np.cos((b.X % 3.2) / 3.2 * 2 * math.pi) + 0.06 * b.noise(8, 2, 1)
        b.paint(b.all(), col, t, 0.5 + 0.2 * np.cos((b.X % 3.2) / 3.2 * 2 * math.pi))
        b.paint((b.X % 3.2) < 0.35, shade(col, 0.6), 1.0, 0.1)
    return b.done()


def barrel_open(c):
    b = Blk(c, 307)
    rr = np.hypot(b.X - 8, b.Y - 8)
    b.metal(rr >= 7.2, (70, 70, 76), 0.8)
    inner = rr < 7.2
    for i in range(5):
        m = inner & (b.X >= 1 + i * 2.9) & (b.X < 1 + (i + 1) * 2.9)
        b.wood(m, shade(np.array((110, 76, 46.0)), 0.6 + 0.05 * (i % 2)), base_h=0.2)
    b.paint(inner, (60, 40, 26), np.clip(rr / 7.2, 0.3, 1) * (0.9 + 0.2 * b.noise(2, 2, 1)), 0.2)
    return b.done()


def bed_foot(c):
    b = Blk(c, 308)
    col = c.pal.main
    yy, xx = h.grid(b.H, b.W)
    k = np.where(xx % (2 * S) < S, (xx // (S // 2) + yy // (S // 2)) % 2, (yy // (S // 2) - xx // (S // 2)) % 2)
    fold = 0.06 * np.sin(b.Y / 16 * 2 * math.pi * 1.5 + 0.8 * b.noise(8, 2))
    b.paint(b.all(), col, np.where(k == 1, 1.04, 0.95) * (0.96 + fold), 0.5 + fold * 3)
    b.paint(b.rect(0, 13.4, 16, 16), shade(col, 0.82), 1.0, 0.7)
    b.paint(np.abs(b.Y - 13.2) < 0.3, (236, 232, 220), 1.0, 0.8)
    return b.done()


def reinforced(c):
    b = Blk(c, 309)
    b.stone(b.all(), (52, 54, 58), rough=0.8)
    fr = ~b.rect(2.2, 2.2, 13.8, 13.8)
    b.paint(fr, BONE, 0.9 + 0.12 * b.noise(2, 2), 0.8)
    b.bevel(fr, 0.2, 0.8)
    for x, y in ((2.2, 2.2), (13.8, 2.2), (2.2, 13.8), (13.8, 13.8)):
        b.paint(b.rect(x - 1.8, y - 1.8, x + 1.8, y + 1.8), (40, 42, 46), 1.0, 0.95)
    return b.done()


def sculk_full(c):
    b = Blk(c, 310)
    base = c.base
    b.paint(b.all(), SCULK, 0.85 + 0.25 * b.noise(8, 3), 0.4)
    spots = h.nrm(b.noise(3, 2)) > 0.84
    b.paint(spots, TEAL, 0.8 + 0.3 * b.noise(2, 2), 0.6, glow=True)
    vein = h.ridged(b.noise(8, 3)) > 0.95
    b.paint(vein, shade(TEAL, 0.8), 1.0, 0.55, glow=True)
    if has(base, "catalyst") and has(base, "side", "bottom"):
        fr = ~b.rect(2, 2, 14, 14)
        b.paint(fr, BONE, 0.9 + 0.12 * b.noise(2, 2), 0.8)
    if has(base, "catalyst") and has(base, "top"):
        b.paint(b.circ(8, 8, 3.2), BONE, 0.95, 0.8)
        b.paint(b.circ(8, 8, 1.8), TEAL, 1.0, 0.6, glow=True)
    return b.done()


def organic_top(c):
    b = Blk(c, 311)
    col = c.pal.main
    clump = b.noise(16, 3)
    b.paint(b.all(), col, 0.85 + 0.2 * clump + 0.05 * (b.noise(2, 2) - 0.5), 0.4 + 0.3 * clump)
    for _ in range(120):
        x, y = b.r.uniform(0, 16), b.r.uniform(0, 16)
        a = b.r.uniform(-2.6, -0.5); L = b.r.uniform(0.6, 1.4)
        m = b.seg(x, y, x + math.cos(a) * L, y + math.sin(a) * L, 0.16)
        b.paint(m, shade(col, b.r.uniform(1.03, 1.14)), 1.0, 0.56)
    return b.done()


def debris(c):
    b = Blk(c, 312)
    col = np.array((96, 66, 56.0))
    if has(c.base, "top"):
        rr = np.hypot(b.X - 8, b.Y - 8)
        b.paint(b.all(), col, 0.85 + 0.12 * np.sin(rr * 2.2 + 2 * b.noise(8, 2)) + 0.06 * b.noise(2, 2), 0.5)
    else:
        b.paint(b.all(), col, 0.85 + 0.12 * np.sin(b.Y * 1.8 + 2 * b.noise(8, 2)) + 0.06 * b.noise(2, 2), 0.5)
    sp = b.r.random((b.H, b.W)) > 0.99
    b.paint(sp, (190, 150, 130), 1.0, 0.7)
    return b.done(spec=0.2)


def portal_frame_top(c):
    b = Blk(c, 313)
    col = np.array((200, 210, 160.0))
    b.stone(b.all(), shade(col, 0.85), rough=0.6)
    rr = np.hypot(b.X - 8, b.Y - 8)
    b.paint(np.abs(rr - 4.6) < 1.0, (60, 110, 90), 1.0, 0.8)
    b.paint(rr < 3.6, (14, 40, 34), 1.0, 0.1)
    b.bevel(b.rect(0.8, 0.8, 15.2, 15.2), 0.2, 0.8)
    return b.done()


def gourd_top(c):
    b = Blk(c, 314)
    col = c.pal.main
    th = np.arctan2(b.Y - 8, b.X - 8); rr = np.hypot(b.X - 8, b.Y - 8)
    rib = np.cos(th * 8)
    b.paint(b.all(), col, 0.9 + 0.1 * rib * np.clip(rr / 8, 0, 1) + 0.05 * b.noise(4, 2), 0.5 + 0.2 * rib)
    b.paint(b.circ(8, 8, 1.8), (90, 120, 50), 1.0, 0.9)
    b.paint(b.circ(8, 8, 1.0), (70, 90, 40), 1.0, 1.0)
    return b.done()


def speckled_stone(c):
    img = h.hd_stone(c, c.pal.least_sat_main(), cracks=c.base != "prismarine")
    b = Blk(c, 315)
    rgb = img[..., :3]
    col = c.pal.least_sat_main()
    sp = b.r.random((b.H, b.W))
    for lo, hi_, f in ((0.0, 0.03, 0.6), (0.97, 1.0, 1.35)):
        m = (sp >= lo) & (sp < hi_)
        m = m | np.roll(m, 1, 1)
        rgb[m] = shade(col, f)
    return img


def stripped_log(c):
    b = Blk(c, 316)
    col = c.pal.main
    warp = b.noise(16, 3, 2)
    grain = np.sin((b.X + 6 * warp) * 2.6)
    b.paint(b.all(), col, 0.9 + 0.07 * grain + 0.1 * (b.noise(8, 3, 1) - 0.5), 0.5 + 0.08 * grain)
    for _ in range(2):
        kx, ky = b.r.uniform(3, 13), b.r.uniform(3, 13)
        rr = np.hypot((b.X - kx) * 1.6, b.Y - ky)
        b.paint(rr < 1.6, shade(col, 0.7), 0.9 + 0.2 * np.sin(rr * 4), 0.4)
    for _ in range(5):
        x = b.r.uniform(0, 16)
        b.paint(b.seg(x, b.r.uniform(0, 8), x + b.r.uniform(-0.4, 0.4), b.r.uniform(8, 16), 0.18), shade(col, 1.15), 1.0, 0.6)
    return b.done()


def coal_ore(c):
    img = h.hd_stone(c, c.pal.least_sat_main())
    v = Cv(c, 317)
    from items2_hd import lump
    for x, y, rr in ((4.4, 4.6, 2.0), (11.6, 5.4, 1.7), (6.0, 11.4, 1.8), (12.4, 12.0, 1.5)):
        lump(v, x, y, rr, np.array((34, 32, 36.0)), spec=0.5)
    m = v.a > 0
    img[m, :3] = v.rgb[m]
    return img


def bamboo_stalk(c):
    b = Blk(c, 318)
    col = c.pal.main
    cyl = np.clip(np.cos((b.X - 8) / 8 * (math.pi / 2)), 0, 1) ** 0.5
    b.paint(b.all(), col, 0.8 + 0.3 * cyl, 0.3 + 0.5 * cyl)
    for y in (3.0, 11.0):
        b.paint(np.abs(b.Y - y) < 0.45, shade(col, 0.72), 1.0, 0.85)
    return b.done(spec=0.15)


def state_mark(img, c):
    """on/triggered/charge states ko saaf alag dikhana."""
    base = c.base
    H, W = img.shape[:2]
    yy, xx = h.grid(H, W)
    if base.startswith("observer_back_on"):
        m = np.hypot(xx - W / 2, yy - H / 2) < W * 0.16
        img[m, :3] = (255, 70, 50)
    if base.startswith("crafter") and has(base, "triggered"):
        img[..., :3] = np.clip(img[..., :3] * 0.85 + np.array([40, 10, 0]), 0, 255)
    import re
    m = re.match(r"respawn_anchor_side(\d)", base)
    if m:
        k = int(m.group(1))
        for i in range(4):
            cell = (np.abs(xx - W * (0.2 + i * 0.2)) < W * 0.06) & (np.abs(yy - H * 0.86) < H * 0.05)
            img[cell, :3] = (255, 140, 255) if i < k else (40, 20, 50)
    return img


# --------------------------------------------------------------- cutouts ----
@own
def saw(c):
    v = Cv(c, 330)
    a = c.ref[..., 3] > 0
    rows = np.where(a.any(1))[0]
    y0 = rows[0] if len(rows) else 8
    off = (c.t % 4) * 0.5
    body = v.rect(0, y0 + 0.8, 16, 16) & (a.repeat(S, 0).repeat(S, 1))
    v.part(body, (180, 182, 190), metal=True, spec=0.8, dome=0.6)
    teeth = v.rect(0, y0, 16, y0 + 1.2) & (((v.X + off) % 1.2) < (1.2 - (v.Y - y0)))
    v.part(teeth, (210, 212, 220), metal=True, spec=0.9, dome=0.3)
    return lit(v)


@own
def tall_bottom2(c):
    v = Cv(c, 331)
    b = c.base
    sp = {"lilac": ((60, 130, 50), 2.6, 1.0), "peony": ((70, 140, 60), 2.0, 1.3),
          "rose_bush": ((50, 110, 40), 1.6, 0.7), "sunflower": ((80, 150, 50), 3.0, 1.4)}
    key = next((k for k in sp if b.startswith(k)), "lilac")
    col, L, w = sp[key]
    green = np.array(col, float)
    xs = (8,) if key == "sunflower" else (5, 8, 11)
    for x in xs:
        v.part(v.seg(x, 16, x + v.r.uniform(-0.5, 0.5), 0, 0.55 if key == "sunflower" else 0.42), shade(green, 0.85), dome=0.4)
    for i, y in enumerate(np.arange(14, 1, -2.4 if key != "rose_bush" else -1.8)):
        for x0, sgn in ((xs[0], -1), (xs[-1], 1)):
            leaf(v, x0 + sgn * L * 0.7, y - (i % 2), L, w, (math.pi + 0.5) if sgn < 0 else -0.5,
                 shade(green, v.r.uniform(0.85, 1.12)))
    if key == "rose_bush":
        for _ in range(6):
            v.part(v.circ(v.r.uniform(3, 13), v.r.uniform(2, 14), 0.3), (60, 40, 30), dome=0.2)   # kaante
    return lit(v)


@own
def pale_moss(c):
    v = Cv(c, 332)
    tip = has(c.base, "tip")
    col = np.array((150, 160, 150.0))
    for x0 in (3, 6.4, 9.6, 13):
        L = v.r.uniform(6, 11) if tip else 16
        for y in np.arange(0, L, 0.6):
            x = x0 + 0.6 * math.sin(y * 0.8 + x0)
            v.part(v.circ(x, y, 0.55 + 0.25 * math.sin(y * 2)), shade(col, v.r.uniform(0.85, 1.1)), dome=0.4)
        if tip:
            v.part(v.ell(x0, L, 1.3, 1.8), shade(col, 1.08), dome=0.8)
    return lit(v)


def bell2(c):
    b = Blk(c, 333)
    ham = h.fbm(b.H, b.W, b.r, 2, 2)
    b.paint(b.all(), GOLDC, 0.82 + 0.28 * np.cos((b.X - 5) / 16 * math.pi) ** 2 + 0.12 * (ham - 0.5), 0.5 + 0.25 * ham)
    a = c.ref[..., 3] > 0
    rows = np.where(a.any(1))[0]
    if len(rows):
        y0, y1 = rows[0], rows[-1] + 1
        b.paint(np.abs(b.Y - (y0 + 0.6)) < 0.5, shade(GOLDC, 1.3), 1.0, 0.9)
        b.paint(np.abs(b.Y - (y1 - 0.8)) < 0.6, shade(GOLDC, 1.15), 1.0, 0.9)
        b.paint(np.abs(b.Y - (y0 + y1) / 2) < 0.45, shade(GOLDC, 0.7), 1.0, 0.3)
    return b.done(spec=0.9, shin=40, metal=True)


def anvil_plain(c):
    b = Blk(c, 334)
    ham = h.fbm(b.H, b.W, b.r, 2, 2)
    b.paint(b.all(), (70, 72, 78), 0.88 + 0.22 * ham, 0.5 + 0.3 * ham)
    edge = ~b.rect(1.2, 1.2, 14.8, 14.8)
    b.paint(edge, (110, 112, 120), 1.0, 0.9)
    b.paint(b.rect(3, 6.6, 13, 9.4), (56, 58, 64), 1.0, 0.3)
    return b.done(spec=0.6, metal=True)


def cake_inner(c):
    b = Blk(c, 335)
    for k, y in enumerate(np.arange(0, 16, 2.6)):
        m = b.rect(0, y, 16, y + 2.6)
        b.paint(m, (205, 140, 80) if k % 2 == 0 else (246, 236, 220), 0.85 + 0.25 * b.noise(1, 1), 0.5)
    return b.done()


def metal_door(c):
    b = Blk(c, 336)
    col = c.pal.main
    b.metal(b.all(), col)
    b.frame(col, 1.3, wood=False)
    if has(c.base, "top"):
        b.paint(b.rect(3.2, 3.0, 12.8, 9.4), (40, 46, 54), 1.0, 0.2)
        for x in (5.6, 8, 10.4):
            b.paint(b.rect(x - 0.3, 3.0, x + 0.3, 9.4), shade(col, 0.9), 1.0, 0.7)
    for y in (4.0, 12.0):
        for x in (2.4, 13.6):
            b.paint(b.circ(x, y, 0.5), shade(col, 1.2), 1.0, 0.95)
    return b.done(spec=0.6, metal=True)


@own
def fungus(c):
    v = Cv(c, 337)
    warped = has(c.base, "warped")
    cap = np.array((30, 150, 140.0)) if warped else np.array((170, 40, 40.0))
    v.part(v.seg(8, 15.6, 8, 8, 0.7), (230, 150, 90) if warped else (200, 120, 90), dome=0.5)
    capm = v.ell(8, 7.4, 5.2, 3.0) & (v.Y < 8.6)
    v.part(capm, cap, dome=1.8, noise=0.15)
    for x in (4.2, 6.6, 9.6, 11.8):
        v.part(v.seg(x, 8.4, x, 8.4 + v.r.uniform(1.0, 2.4), 0.35), shade(cap, 0.85), dome=0.3)
    v.tint(capm & (np.abs(np.hypot(v.X - 8, (v.Y - 7.4) * 1.7) - 3.0) < 0.3), 1.3)
    return lit(v)


@own
def amethyst(c):
    v = Cv(c, 338)
    b = c.base
    size = {"small": 0.4, "medium": 0.6, "large": 0.8}.get(next((k for k in ("small", "medium", "large") if has(b, k)), ""), 1.0)
    col = np.array((170, 110, 210.0))
    for k, (x, ang, L) in enumerate(((8, 0, 12), (5.0, -0.35, 9), (11.0, 0.35, 9), (3.4, -0.7, 6), (12.6, 0.7, 6))):
        L *= size
        if L < 2.5:
            continue
        tip = (x + math.sin(ang) * L, 16 - math.cos(ang) * L)
        wdt = 1.4 * size + 0.4
        pl = (x - wdt * math.cos(ang), 16 - wdt * math.sin(ang) * 0.2)
        pr = (x + wdt * math.cos(ang), 16 + wdt * math.sin(ang) * 0.2)
        m = v.poly([pl, tip, pr])
        v.part(m, shade(col, 0.95 + 0.05 * k), spec=0.8, shin=50, dome=0.6)
        half = v.poly([pl, tip, (x, 16)])
        v.tint(m & half, 1.18)
    return lit(v)


def cactus(c):
    b = Blk(c, 339)
    col = np.array((70, 140, 60.0))
    if has(c.base, "top") or has(c.base, "bottom"):
        th = np.arctan2(b.Y - 8, b.X - 8)
        rib = np.cos(th * 8)
        b.paint(b.all(), col, 0.9 + 0.1 * rib + 0.04 * b.noise(2, 2), 0.5 + 0.2 * rib)
        b.paint(b.circ(8, 8, 1.4), shade(col, 1.2), 1.0, 0.8)
    else:
        rib = np.cos((b.X % 4) / 4 * 2 * math.pi)
        b.paint(b.all(), col, 0.86 + 0.14 * rib + 0.04 * b.noise(4, 2, 1), 0.5 + 0.3 * rib)
        for x in np.arange(2, 16, 4):
            for y in np.arange(1.5, 16, 3):
                b.paint(b.circ(x, y + (x % 8) * 0.2, 0.32), (236, 230, 200), 1.0, 0.95)
    return b.done(spec=0.15)


def honey(c):
    b = Blk(c, 340)
    col = np.array((240, 170, 40.0))
    b.paint(b.all(), col, 0.9 + 0.12 * b.noise(6, 3), 0.5)
    b.paint(b.rect(2.4, 2.4, 13.6, 13.6), shade(col, 0.85), 0.92 + 0.12 * b.noise(4, 2), 0.35)
    for _ in range(8):
        x, y, rr = b.r.uniform(3.5, 12.5), b.r.uniform(3.5, 12.5), b.r.uniform(0.4, 0.9)
        b.paint(b.circ(x, y, rr), shade(col, 1.2), 1.0, 0.7)
    return b.done(spec=0.7, shin=50)


def portal_frame_side(c):
    b = Blk(c, 341)
    col = np.array((200, 210, 160.0))
    b.stone(b.all(), shade(col, 0.85), rough=0.6)
    b.paint(b.rect(0, 0, 16, 4), (60, 110, 90), 0.9 + 0.15 * b.noise(2, 2), 0.7)
    b.paint(np.abs(b.Y - 4) < 0.35, shade(col, 0.6), 1.0, 0.2)
    return b.done()


def portal_eye(c):
    b = Blk(c, 342)
    b.paint(b.all(), (40, 110, 90), 0.9 + 0.1 * b.noise(2, 2), 0.5)
    b.paint(b.circ(8, 8, 3.4), (60, 200, 120), 1.0, 0.7)
    b.paint(b.circ(8, 8, 2.0), (20, 60, 40), 1.0, 0.4)
    b.paint(((b.X - 8) / 0.7) ** 2 + ((b.Y - 8) / 1.8) ** 2 < 1, (10, 14, 10), 1.0, 0.3)
    return b.done(spec=0.6)


def enchant_side(c):
    b = Blk(c, 343)
    b.paint(b.all(), (30, 20, 44), 0.9 + 0.15 * b.noise(4, 3), 0.4)
    cloth = b.rect(0, 0, 16, 7)
    fold = np.sin(b.X * 1.4) * 0.5 + 0.5
    b.paint(cloth, (170, 40, 50), 0.85 + 0.2 * fold, 0.5 + 0.3 * fold)
    b.paint(b.rect(0, 7, 16, 7.8), (230, 190, 70), 1.0, 0.7)
    return b.done(spec=0.1)


@own
def sculk_vein(c):
    v = Cv(c, 344)
    for _ in range(7):
        x, y, a = v.r.uniform(0, 16), v.r.uniform(0, 16), v.r.uniform(0, 6.28)
        for k in range(8):
            x2, y2 = x + math.cos(a) * 1.4, y + math.sin(a) * 1.4
            v.part(v.seg(x, y, x2, y2, 0.3), SCULK if k % 3 else TEAL, dome=0.3, glow=k % 3 == 0)
            x, y, a = x2, y2, a + v.r.uniform(-0.8, 0.8)
    return lit(v)


@own
def cobweb(c):
    v = Cv(c, 345)
    col = (236, 236, 240)
    for k in range(8):
        a = k * math.pi / 4
        v.part(v.seg(8, 8, 8 + 9 * math.cos(a), 8 + 9 * math.sin(a), 0.22), col, dome=0.2)
    for R in (2.2, 4.4, 6.6):
        pts = []
        for k in range(9):
            a = k * math.pi / 4
            pts.append((8 + R * 0.92 * math.cos(a), 8 + R * 0.92 * math.sin(a)))
        for (x0, y0), (x1, y1) in zip(pts[:-1], pts[1:]):
            mx, my = (x0 + x1) / 2 * 0.95 + 8 * 0.05, (y0 + y1) / 2 * 0.95 + 8 * 0.05
            v.part(v.seg(x0, y0, mx, my, 0.18) | v.seg(mx, my, x1, y1, 0.18), col, dome=0.2)
    return lit(v)


@own
def lantern_block(c):
    v = Cv(c, 346)
    soul = has(c.base, "soul")
    a = (c.ref[..., 3] > 0).repeat(S, 0).repeat(S, 1)
    metal = c.pal.least_sat_main() if has(c.base, "copper") else np.array((70, 72, 80.0))
    v.part(a, (110, 230, 240) if soul else (255, 190, 80), glow=True)
    bars = a & (((v.X % 2.0) < 0.5) | ((v.Y % 4.0) < 0.6))
    v.part(bars, metal, metal=True, spec=0.6, dome=0.3)
    return lit(v)


@own
def copper_torch(c):
    v = Cv(c, 347)
    a = (c.ref[..., 3] > 0).repeat(S, 0).repeat(S, 1)
    rows = np.where(a.any(1))[0]
    y0 = rows[0] / S if len(rows) else 6
    v.part(a, (140, 100, 60), wood=True, dome=0.3)
    v.part(a & (v.Y < y0 + 2.2), (120, 255, 150), glow=True)
    v.part(a & (v.Y < y0 + 1.0), (220, 255, 230), glow=True)
    return lit(v)


@own
def fern_bush(c):
    v = Cv(c, 348)
    green = c.pal.main if c.pal.grey else np.array((70, 140, 60.0))
    for k in range(5):
        a = -math.pi / 2 + (k - 2) * 0.36
        L = 12.5 + v.r.uniform(-1, 1.5)
        x1, y1 = 8 + math.cos(a) * L, 15.6 + math.sin(a) * L
        v.part(v.seg(8, 15.6, x1, y1, 0.3), shade(green, 0.85), dome=0.3)
        for t in np.linspace(0.25, 0.95, 6):
            px, py = 8 + (x1 - 8) * t, 15.6 + (y1 - 15.6) * t
            for sgn in (-1, 1):
                leaf(v, px + sgn * math.cos(a + math.pi / 2) * 0.9, py + sgn * math.sin(a + math.pi / 2) * 0.9,
                     2.8 * (1.15 - t), 0.85, a + sgn * 1.0, shade(green, v.r.uniform(0.85, 1.12)))
    return lit(v)


@own
def spore_blossom(c):
    v = Cv(c, 349)
    for k in range(6):
        a = k * math.pi / 3
        leaf(v, 8 + math.cos(a) * 3.6, 8 + math.sin(a) * 3.6, 3.4, 1.4, a, shade(np.array((236, 120, 190.0)), 0.95 + 0.05 * (k % 2)))
    v.part(v.circ(8, 8, 1.8), (120, 170, 60), dome=1.0)
    return lit(v)


@own
def resin_clump(c):
    v = Cv(c, 350)
    for _ in range(5):
        x, y = v.r.uniform(3, 13), v.r.uniform(3, 11)
        m = v.circ(x, y, 1.4) | v.poly([(x - 1.0, y + 0.4), (x + 1.0, y + 0.4), (x, y + 3.2)])
        v.part(m, (230, 120, 40), dome=1.2, spec=0.8, shin=50)
    return lit(v)


def dripstone(c):
    b = Blk(c, 351)
    col = np.array((150, 120, 100.0))
    rib = np.cos((b.X % 2.4) / 2.4 * 2 * math.pi)
    b.paint(b.all(), col, 0.88 + 0.1 * rib + 0.05 * b.noise(4, 2, 1), 0.5 + 0.2 * rib)
    for y in np.arange(2, 16, 3.2):
        b.paint(np.abs(b.Y - y) < 0.3, shade(col, 0.8), 1.0, 0.3)
    return b.done()


@own
def redstone_line(c):
    v = Cv(c, 352)
    a = (c.ref[..., 3] > 0)
    horiz = a.any(1).sum() < a.any(0).sum()
    m = (np.abs(v.Y - 8) < 1.0) if horiz else (np.abs(v.X - 8) < 1.0)
    v.part(m, (200, 200, 200), flat=True, noise=0.1)
    return lit(v)


def override(c, full):
    b = c.base
    if b.startswith("dried_ghast"):
        return dried_ghast
    if b.startswith("smithing_table"):
        return smithing
    if b == "cartography_table_top":
        return cartography_top
    if b in ("chiseled_bookshelf_side", "chiseled_bookshelf_top", "daylight_detector_side"):
        return plank_face
    if b.startswith("farmland"):
        return farmland
    if b == "jack_o_lantern":
        return jack
    if b.startswith("polished_") and full and not has(b, "brick"):
        return polished
    if b in ("basalt_side", "basalt_top"):
        return basalt
    if b == "barrel_top_open":
        return barrel_open
    if has(b, "_bed_") and b.endswith("foot_up"):
        return bed_foot
    if b.startswith("reinforced_deepslate"):
        return reinforced
    if (b == "sculk" or b.startswith("sculk_catalyst") or b in ("sculk_shrieker_bottom", "sculk_shrieker_inner_top",
                                                                "sculk_sensor_bottom")) and full:
        return sculk_full
    if b in ("grass_block_top", "mycelium_top", "pale_moss_block", "moss_block", "warped_wart_block",
             "crimson_nylium", "warped_nylium"):
        return organic_top
    if b.startswith("ancient_debris"):
        return debris
    if b == "end_portal_frame_top":
        return portal_frame_top
    if b in ("pumpkin_top", "melon_top"):
        return gourd_top
    if b in ("diorite", "granite", "netherrack", "andesite", "prismarine"):
        return speckled_stone
    if b.startswith("stripped_") and b.endswith(("_log", "_stem", "_wood", "_hyphae")):
        return stripped_log
    if b == "coal_ore":
        return coal_ore
    if b == "bamboo_stalk":
        return bamboo_stalk
    # cut-outs / model parts
    if b == "stonecutter_saw":
        return saw
    if b in ("lilac_bottom", "peony_bottom", "rose_bush_bottom", "sunflower_bottom"):
        return tall_bottom2
    if b.startswith("pale_hanging_moss"):
        return pale_moss
    if b.startswith("bell_"):
        return bell2
    if b == "anvil_top":
        return anvil_plain
    if b == "cake_inner":
        return cake_inner
    if b in ("iron_door_top", "iron_door_bottom") or (has(b, "copper_door") and b.endswith(("_top", "_bottom"))):
        return metal_door
    if b in ("warped_fungus", "crimson_fungus"):
        return fungus
    if has(b, "amethyst_cluster", "amethyst_bud"):
        return amethyst
    if b.startswith("cactus") and not has(b, "flower"):
        return cactus
    if b.startswith("honey_block"):
        return honey
    if b == "end_portal_frame_side":
        return portal_frame_side
    if b == "end_portal_frame_eye":
        return portal_eye
    if b == "enchanting_table_side":
        return enchant_side
    if b == "sculk_vein":
        return sculk_vein
    if b == "cobweb":
        return cobweb
    if b in ("lantern", "soul_lantern") or (has(b, "copper_lantern")):
        return lantern_block
    if b == "copper_torch":
        return copper_torch
    if b in ("bush", "fern", "large_fern_top", "large_fern_bottom"):
        return fern_bush
    if b == "spore_blossom":
        return spore_blossom
    if b == "resin_clump":
        return resin_clump
    if b.startswith("pointed_dripstone"):
        return dripstone
    if b == "redstone_dust_line0" or b == "redstone_dust_line1" or b.startswith("redstone_dust_line"):
        return redstone_line
    return None
