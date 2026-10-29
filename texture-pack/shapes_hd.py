"""FireCraft Remastered HD — khaas shapes (apne designs, 16-unit vector coordinates).

Jin cheezon ki shape fallback se gol dhabba ban jati thi, unke liye yahan apne design hain:
clock/compass (har frame me sui ghoomti hai), boats, buckets, minecarts, books, maps,
pottery sherds, smithing templates, horse/nautilus armor, harness, banner patterns,
aur paude: coral, dripleaf, lily pad, lambe phool, petals, cocoa, sugar cane, vines,
sniffer/turtle egg ka chhilka.
"""
import math, re
import numpy as np

import generate as g
import generate_hd as h

S = h.S
shade, mix, has = g.shade, g.mix, g.has


# ------------------------------------------------------------- canvas -------
class Cv:
    def __init__(self, c, salt=0, H=None, W=None):
        self.c = c
        self.H, self.W = H or c.H * S, W or c.W * S
        self.rgb = np.zeros((self.H, self.W, 3)); self.a = np.zeros((self.H, self.W))
        yy, xx = h.grid(self.H, self.W)
        self.X, self.Y = xx / S, yy / S
        self.r = c.rng(400 + salt)

    # shapes (16-unit coords)
    def circ(self, cx, cy, rr):
        return np.hypot(self.X - cx, self.Y - cy) < rr

    def ell(self, cx, cy, rx, ry, ang=0.0):
        dx, dy = self.X - cx, self.Y - cy
        u = dx * math.cos(ang) + dy * math.sin(ang)
        v = -dx * math.sin(ang) + dy * math.cos(ang)
        return (u / rx) ** 2 + (v / ry) ** 2 < 1

    def rect(self, x0, y0, x1, y1):
        return (self.X >= x0) & (self.X < x1) & (self.Y >= y0) & (self.Y < y1)

    def seg(self, x0, y0, x1, y1, w):
        dx, dy = x1 - x0, y1 - y0
        t = np.clip(((self.X - x0) * dx + (self.Y - y0) * dy) / (dx * dx + dy * dy + 1e-9), 0, 1)
        return np.hypot(self.X - (x0 + t * dx), self.Y - (y0 + t * dy)) < w

    def poly(self, pts):
        return h.poly(self.X, self.Y, pts)

    def local(self, ox, oy, dx, dy):
        """Tool/handle ka apna frame: A = handle ke saath, B = upar-baayein taraf (perpendicular)."""
        n = math.hypot(dx, dy); dx, dy = dx / n, dy / n
        rx, ry = self.X - ox, self.Y - oy
        return rx * dx + ry * dy, -(rx * -dy + ry * dx)

    def part(self, m, col, spec=0.0, shin=24, metal=False, wood=False, noise=0.08, dome=1.6, flat=False, glow=False,
             hmap=None):
        """Ek hissa: apni dome height (ya diya hua hmap) + roshni + material detail, phir upar chipkao."""
        if not m.any():
            return
        col = np.asarray(col, float)
        d = h.dist_inside(m, 3 * S)
        hgt = np.zeros(m.shape) if flat else np.sqrt(np.clip(d / (dome * S), 0, 1))
        if hmap is not None:
            hgt = hmap
        if wood:
            tone = 0.86 + 0.24 * h.fbm(self.H, self.W, self.r, 4, 2, 1)
        else:
            tone = 1 + noise * 2 * (h.fbm(self.H, self.W, self.r, 2 * S, 3) - 0.5)
        alb = h.tone_col(col, tone)
        if glow:
            rgb = alb
        else:
            rgb = h.light(alb, hgt, k=3, spec=spec, shin=shin, wrap=False, metal=metal, dirt=0.12)
        self.rgb[m] = rgb[m]; self.a[m] = 255

    def tint(self, m, f):
        m = m & (self.a > 0)
        self.rgb[m] = np.clip(self.rgb[m] * f, 0, 255)

    def cut(self, m):
        self.a[m] = 0

    def done(self, outline=True):
        rgb, a = self.rgb.copy(), self.a.copy()
        if outline:
            rgb, a = h.outline(rgb, a)
        return h.out_rgba(rgb, a)


def frame_index(base, default_n, c=None):
    if c is not None and c.nf > 1:          # Bedrock atlas strip (compass_atlas, watch_atlas)
        return c.t, c.nf
    m = re.search(r"_(\d+)$", base)
    return (int(m.group(1)), default_n) if m else (0, default_n)


GOLD, IRON, DARK = (240, 196, 70), (196, 198, 204), (44, 38, 40)
DYES = {"white": (236, 236, 236), "light_gray": (160, 160, 156), "gray": (82, 86, 90), "black": (36, 34, 40),
        "brown": (124, 82, 50), "red": (176, 44, 40), "orange": (240, 124, 32), "yellow": (250, 206, 52),
        "lime": (128, 200, 40), "green": (86, 112, 34), "cyan": (22, 150, 156), "light_blue": (64, 172, 218),
        "blue": (52, 62, 166), "purple": (130, 50, 178), "magenta": (194, 74, 186), "pink": (240, 140, 170)}


def dye_of(base, default):
    """Java: 'red_bundle', Bedrock: 'bundle_red' / 'bundle_light_blue_open_front' — dono samjho."""
    tok = "_" + base + "_"
    for k in sorted(DYES, key=len, reverse=True):
        if "_" + k + "_" in tok:
            if k in ("blue", "gray") and ("_light_" + k + "_") in tok:
                continue
            return np.array(DYES[k], float)
    return default
WOOD, PAPER = (150, 104, 58), (228, 214, 178)


# --------------------------------------------------------------- items ------
def clock(c):
    v = Cv(c, 1)
    f, n = frame_index(c.base, 64, c)
    v.part(v.circ(8, 8, 7.0), GOLD, spec=0.8, metal=True)
    face = v.circ(8, 8, 5.5)
    v.part(face, (236, 226, 198), dome=4, noise=0.04)
    # din/raat ki khidki (apna design): upar ka aadha hissa sky
    sky = face & (v.Y < 8) & (np.hypot(v.X - 8, v.Y - 8) > 2.6)
    t = f / n
    skyc = mix((92, 160, 230), (24, 30, 70), 0.5 - 0.5 * math.cos(2 * math.pi * t))
    v.part(sky, skyc, flat=True, noise=0.03)
    for k in range(12):
        a = k * math.pi / 6
        v.part(v.circ(8 + 4.7 * math.cos(a), 8 + 4.7 * math.sin(a), 0.62 if k % 3 == 0 else 0.42), (70, 58, 46), flat=True)
    a = 2 * math.pi * t - math.pi / 2
    v.part(v.seg(8, 8, 8 + 4.0 * math.cos(a), 8 + 4.0 * math.sin(a), 0.5), DARK, dome=0.6)
    v.part(v.seg(8, 8, 8 + 2.6 * math.cos(a * 12), 8 + 2.6 * math.sin(a * 12), 0.4), (120, 40, 36), dome=0.6)
    v.part(v.circ(8, 8, 0.9), GOLD, spec=0.9, metal=True)
    return v.done()


def compass(c, recovery=False):
    v = Cv(c, 2)
    f, n = frame_index(c.base, 32, c)
    body = (52, 78, 86) if recovery else ((92, 92, 100) if has(c.base, "lodestone") else (150, 152, 160))
    v.part(v.circ(8, 8, 7.0), body, spec=0.7, metal=True)
    v.part(v.circ(8, 8, 5.3), (24, 44, 50) if recovery else (232, 228, 214), dome=4, noise=0.04)
    for k in range(4):
        a = k * math.pi / 2
        v.part(v.circ(8 + 4.5 * math.cos(a), 8 + 4.5 * math.sin(a), 0.5), (110, 200, 210) if recovery else (90, 80, 70), flat=True)
    a = 2 * math.pi * f / n - math.pi / 2
    ca, sa = math.cos(a), math.sin(a)
    tip, tail = (8 + 4.4 * ca, 8 + 4.4 * sa), (8 - 4.4 * ca, 8 - 4.4 * sa)
    s1, s2 = (8 - 1.1 * sa, 8 + 1.1 * ca), (8 + 1.1 * sa, 8 - 1.1 * ca)
    v.part(v.poly([tip, s1, s2]), (90, 230, 240) if recovery else (214, 44, 40), dome=0.8, spec=0.4)
    v.part(v.poly([tail, s1, s2]), (40, 90, 100) if recovery else (238, 238, 240), dome=0.8, spec=0.4)
    v.part(v.circ(8, 8, 0.7), (60, 60, 64), spec=0.8, metal=True)
    return v.done()


def boat(c, chest=False, raft=False):
    v = Cv(c, 3)
    wood = c.pal.main
    if raft:
        base = v.rect(1.5, 8, 14.5, 11.6)
        for i, x in enumerate(np.arange(1.5, 14.5, 1.6)):
            v.part(base & v.rect(x, 0, x + 1.6, 16), shade(wood, 1.06 if i % 2 else 0.92), dome=0.7)
        v.tint(base & ((np.abs(v.Y - 9.2) < 0.22) | (np.abs(v.Y - 10.6) < 0.22)), 0.72)
    else:
        hull = v.poly([(1.2, 7.6), (14.8, 7.6), (13.2, 12.2), (2.8, 12.2)])
        v.part(hull, wood, wood=True, dome=2.2)
        v.tint(hull & ((np.abs(v.Y - 9.2) < 0.22) | (np.abs(v.Y - 10.7) < 0.22)), 0.7)
        v.part(v.seg(1.2, 7.6, 14.8, 7.6, 0.55), shade(wood, 1.15), wood=True, dome=0.6)
    v.part(v.seg(3.6, 2.8, 6.4, 8.0, 0.42), shade(wood, 0.8), wood=True, dome=0.5)
    v.part(v.ell(3.3, 2.6, 1.1, 1.9, -0.5), shade(wood, 0.92), wood=True)
    if chest:
        box = v.rect(8.6, 3.8, 13.4, 7.7)
        v.part(box, (150, 104, 54), wood=True, dome=1.2)
        v.tint(box & (np.abs(v.Y - 5.3) < 0.2), 0.62)
        v.part(v.rect(10.5, 4.9, 11.5, 6.3), GOLD, spec=0.8, metal=True, dome=0.5)
    return v.done()


BUCKET_FILL = (("lava", (240, 112, 28), True), ("milk", (246, 246, 240), False),
               ("powder_snow", (232, 240, 250), False), ("snow", (232, 240, 250), False))
CREATURES = ("cod", "salmon", "puffer", "tropical", "axolotl", "tadpole", "fish")


def bucket(c):
    v = Cv(c, 4)
    b = c.base
    for k in range(25):
        t = math.pi + math.pi * k / 24
        x0, y0 = 8 + 4.6 * math.cos(t), 5.2 + 4.0 * math.sin(t)
        t2 = math.pi + math.pi * (k + 1) / 24
        v.part(v.seg(x0, y0, 8 + 4.6 * math.cos(t2), 5.2 + 4.0 * math.sin(t2), 0.32), (90, 92, 98), dome=0.4)
    body = v.poly([(3.4, 5.2), (12.6, 5.2), (11.4, 14.2), (4.6, 14.2)])
    v.part(body, IRON, spec=0.6, metal=True, dome=2.6)
    v.tint(body & ((np.abs(v.Y - 7.4) < 0.25) | (np.abs(v.Y - 12.6) < 0.25)), 0.72)
    v.part(v.ell(8, 5.2, 4.8, 1.55), shade(IRON, 1.1), spec=0.6, metal=True, dome=0.6)
    inner = v.ell(8, 5.3, 4.0, 1.05)
    fill, glow = None, False
    for k, col, gl in BUCKET_FILL:
        if has(b, k):
            fill, glow = col, gl
            break
    if fill is None and (has(b, "water") or any(w in b for w in CREATURES)):
        fill = (52, 100, 215)
    if fill is None:
        v.part(inner, (58, 58, 64), flat=True)
    else:
        v.part(inner, fill, flat=True, glow=glow, noise=0.1)
    if any(w in b for w in CREATURES):
        acc = c.pal.accent
        fish = v.ell(7.6, 10.2, 2.3, 1.1) | v.poly([(9.6, 10.2), (11.4, 9.0), (11.4, 11.4)])
        v.part(fish, acc, dome=0.8)
        v.part(v.circ(6.4, 10.0, 0.32), DARK, flat=True)
    return v.done()


def minecart(c):
    v = Cv(c, 5)
    b = c.base
    for x in (5, 11):
        v.part(v.circ(x, 12.6, 1.8), (48, 48, 54), spec=0.4, metal=True)
        v.part(v.circ(x, 12.6, 0.6), (150, 150, 156), metal=True, spec=0.6)
    if has(b, "chest"):
        box = v.rect(4.2, 2.0, 11.8, 6.8)
        v.part(box, (150, 104, 54), wood=True, dome=1.4)
        v.tint(box & (np.abs(v.Y - 3.8) < 0.22), 0.62)
        v.part(v.rect(7.4, 3.2, 8.6, 4.8), GOLD, spec=0.8, metal=True, dome=0.4)
    elif has(b, "furnace"):
        v.part(v.rect(4.2, 2.0, 11.8, 6.8), (126, 126, 128), dome=1.4)
        v.part(v.rect(6.0, 3.6, 10.0, 5.8), (250, 150, 50), glow=True)
    elif has(b, "hopper"):
        v.part(v.poly([(3.6, 2.2), (12.4, 2.2), (9.6, 6.8), (6.4, 6.8)]), (66, 66, 72), spec=0.4, metal=True)
        v.part(v.ell(8, 2.6, 4.0, 0.7), (30, 30, 34), flat=True)
    elif has(b, "tnt"):
        box = v.rect(4.2, 2.0, 11.8, 6.8)
        v.part(box, (200, 50, 40), dome=1.4)
        v.part(v.rect(4.2, 3.6, 11.8, 5.2), (232, 228, 218), dome=0.6)
    elif has(b, "command"):
        v.part(v.rect(4.2, 2.0, 11.8, 6.8), (196, 124, 70), dome=1.4, spec=0.3)
        v.part(v.rect(6.6, 3.2, 9.4, 5.6), (90, 60, 44), flat=True)
    elif has(b, "spawner"):
        v.part(v.rect(4.2, 2.0, 11.8, 6.8), (40, 52, 66), dome=1.4)
    body = v.poly([(1.8, 6.4), (14.2, 6.4), (12.8, 11.8), (3.2, 11.8)])
    v.part(body, (124, 128, 134), spec=0.5, metal=True, dome=2.2)
    v.part(v.seg(1.8, 6.5, 14.2, 6.5, 0.5), (170, 172, 178), spec=0.6, metal=True, dome=0.5)
    v.tint(body & (np.abs(v.Y - 9.2) < 0.25), 0.7)
    return v.done()


def book(c):
    v = Cv(c, 6)
    b = c.base
    cover = (122, 66, 38)
    if has(b, "enchanted"):
        cover = (104, 48, 140)
    elif has(b, "knowledge"):
        cover = (54, 124, 76)
    v.part(v.rect(11.6, 3.0, 13.6, 13.2), (238, 232, 212), dome=0.6, noise=0.03)
    v.tint(v.rect(11.6, 3.0, 13.6, 13.2) & ((np.floor(v.Y * 2) % 2) == 0), 0.9)
    cv = v.rect(2.6, 2.4, 12.2, 13.8)
    v.part(cv, cover, dome=1.6, noise=0.1)
    v.tint(v.rect(2.6, 2.4, 4.0, 13.8), 0.72)
    if has(b, "written"):
        v.part(v.rect(5.4, 5.0, 10.4, 7.2), GOLD, spec=0.6, metal=True, dome=0.5)
    elif has(b, "writable") or has(b, "writeable"):
        v.part(v.seg(6.0, 12.0, 11.0, 4.0, 0.55), (244, 244, 240), dome=0.5)
        v.part(v.seg(5.6, 12.6, 6.4, 11.4, 0.3), DARK, flat=True)
    elif has(b, "enchanted"):
        for a in range(4):
            ang = a * math.pi / 2
            v.part(v.seg(7.4, 8.0, 7.4 + 2.4 * math.cos(ang), 8.0 + 2.4 * math.sin(ang), 0.4), (250, 160, 250), glow=True)
        v.part(v.circ(7.4, 8.0, 0.8), (255, 220, 255), glow=True)
    elif has(b, "knowledge"):
        v.part(v.circ(7.4, 8.0, 2.0), (120, 210, 140), spec=0.5, dome=0.8)
    else:
        v.part(v.rect(5.4, 7.0, 10.4, 7.6), shade(cover, 0.7), flat=True)
    return v.done()


def map_item(c):
    v = Cv(c, 7)
    paper = v.rect(2.2, 2.2, 13.8, 13.8)
    v.part(paper, PAPER, dome=3, noise=0.05)
    v.tint(paper & ~v.rect(2.8, 2.8, 13.2, 13.2), 0.8)
    inner = v.rect(3.6, 3.6, 12.4, 12.4)
    if not has(c.base, "empty") and c.base != "map":
        n = h.fbm(v.H, v.W, v.r, 8, 3)
        land = inner & (n > 0.45)
        sea = inner & ~land
        v.part(sea, (96, 140, 200), flat=True, noise=0.04)
        v.part(land & (n > 0.62), (104, 150, 72), flat=True, noise=0.06)
        v.part(land & (n <= 0.62), (210, 196, 140), flat=True, noise=0.04)
    else:
        v.tint(inner & ((np.abs((v.X - 3.6) % 2.2) < 0.12) | (np.abs((v.Y - 3.6) % 2.2) < 0.12)), 0.86)
    if c.base.startswith("map_") and not has(c.base, "empty", "filled", "locked"):   # explorer map: apna X nishaan
        mk = c.pal.accent if np.abs(c.pal.accent - PAPER).sum() > 80 else (190, 40, 40)
        v.part(v.seg(7.0, 7.0, 10.0, 10.0, 0.5) | v.seg(10.0, 7.0, 7.0, 10.0, 0.5), mk, dome=0.4)
    if has(c.base, "locked"):
        v.part(v.rect(9.6, 9.6, 13.2, 13.0), (120, 120, 128), metal=True, spec=0.6, dome=0.6)
    return v.done()


def sherd(c):
    v = Cv(c, 8)
    r = v.r
    pts = []
    for k in range(7):
        a = k * 2 * math.pi / 7 + r.uniform(-0.2, 0.2)
        rr = 6.2 + r.uniform(-1.4, 0.6)
        pts.append((8 + rr * math.cos(a), 8 + rr * math.sin(a)))
    body = v.poly(pts)
    col = mix((176, 96, 64), c.pal.main, 0.3)
    v.part(body, col, dome=2.4, noise=0.1)
    for _ in range(int(r.integers(3, 6))):
        x0, y0 = 8 + r.uniform(-3, 3), 8 + r.uniform(-3, 3)
        a = r.uniform(0, 2 * math.pi); L = r.uniform(1.5, 3.5)
        v.tint(body & v.seg(x0, y0, x0 + L * math.cos(a), y0 + L * math.sin(a), 0.42), 0.6)
    v.tint(body & v.circ(8 + r.uniform(-1, 1), 8 + r.uniform(-1, 1), 0.9), 0.6)
    return v.done()


def template(c):
    v = Cv(c, 9)
    plate = v.rect(2.4, 1.8, 13.6, 14.2)
    v.part(plate, (58, 64, 76), dome=1.8, spec=0.3)
    v.tint(plate & ~v.rect(3.2, 2.6, 12.8, 13.4), 0.75)
    up = has(c.base, "netherite_upgrade")
    acc = (96, 70, 80) if up else c.pal.accent
    em = v.poly([(8, 4.2), (11.4, 8), (8, 11.8), (4.6, 8)])
    v.part(em, acc, spec=0.6, metal=True, dome=1.2)
    if up:
        v.part(v.poly([(8, 5.6), (10, 8), (8, 10.4), (6, 8)]), GOLD, spec=0.8, metal=True, dome=0.8)
    for x, y in ((3.8, 3.2), (12.2, 3.2), (3.8, 12.8), (12.2, 12.8)):
        v.part(v.circ(x, y, 0.55), (150, 150, 160), metal=True, spec=0.6)
    return v.done()


def horse_armor(c):
    v = Cv(c, 10)
    b = c.base
    col = (205, 205, 205) if has(b, "leather") else g.mat_color(b.replace("horse_armor_", "").replace("horse_armor", ""), c.pal)
    for k in g.MATS:
        if has(b, k):
            col = (205, 205, 205) if k == "leather" else g.MATS[k]
            break
    head = v.poly([(3.0, 4.6), (8.0, 2.2), (12.6, 3.4), (14.0, 6.2), (12.6, 9.0), (9.6, 13.6), (6.8, 13.8), (6.2, 10.0), (4.0, 7.6)])
    v.part(head, col, spec=0.15 if has(b, "leather") else 0.8, metal=not has(b, "leather"), dome=2.2)
    v.tint(head & (np.abs((v.X + v.Y * 0.6) % 3.0 - 1.5) < 0.18), 0.78)
    v.cut(v.circ(10.6, 5.6, 1.0))
    v.part(v.seg(4.0, 4.6, 2.6, 2.2, 0.5), shade(col, 0.85), metal=True, spec=0.5)
    return v.done()


def nautilus_armor(c):
    v = Cv(c, 11)
    col = c.pal.main
    for k in g.MATS:
        if c.base.startswith(k + "_"):
            col = g.MATS[k]
    shell = v.circ(8, 8.4, 6.4)
    v.part(shell, col, spec=0.6, metal=True, dome=3)
    rr = np.hypot(v.X - 8, v.Y - 8.4); th = np.arctan2(v.Y - 8.4, v.X - 8)
    v.tint(shell & (np.sin(th + 2.4 * np.log(rr + 0.5)) > 0.75), 0.72)
    v.part(v.ell(10.6, 11.2, 2.2, 1.4, 0.6), (40, 34, 40), flat=True)
    return v.done()


def harness(c):
    v = Cv(c, 12)
    col = dye_of(c.base, c.pal.accent if np.abs(c.pal.accent - c.pal.main).sum() > 40 else c.pal.main)
    for x0, x1 in ((4.2, 3.0), (11.8, 13.0)):
        v.part(v.seg(x0, 7.0, x1, 13.6, 0.55), (96, 64, 40), dome=0.5)
    v.part(v.poly([(2.6, 6.0), (13.4, 6.0), (12.0, 9.4), (4.0, 9.4)]), shade(col, 0.85), dome=1.4)
    v.part(v.ell(8, 5.6, 5.2, 2.4), col, dome=1.6, spec=0.2)
    v.part(v.rect(7.2, 8.6, 8.8, 10.2), IRON, metal=True, spec=0.7, dome=0.5)
    return v.done()


def banner_pattern(c):
    v = Cv(c, 13)
    r = v.r
    v.part(v.rect(3.0, 2.6, 13.0, 13.4), PAPER, dome=3, noise=0.05)
    v.part(v.rect(2.4, 1.8, 13.6, 2.8), shade(PAPER, 0.8), dome=0.5)
    v.part(v.rect(2.4, 13.2, 13.6, 14.2), shade(PAPER, 0.8), dome=0.5)
    acc = c.pal.accent if np.abs(c.pal.accent - PAPER).sum() > 80 else (90, 70, 60)
    for _ in range(4):
        x0, y0 = 8 + r.uniform(-2.5, 2.5), 8 + r.uniform(-3, 3)
        a = r.uniform(0, 2 * math.pi)
        v.part(v.seg(x0, y0, x0 + 2.6 * math.cos(a), y0 + 2.6 * math.sin(a), 0.5), acc, flat=True)
    v.part(v.circ(8, 8, 1.2), acc, flat=True)
    return v.done()


def item_special(c):
    b = c.base
    if c.H != 16 or c.W != 16:
        return None
    if b.startswith("egg_") or b.startswith("spawn_egg"):      # Bedrock spawn eggs
        return h.hi_shape(c, "egg")
    import items_hd
    out = items_hd.special(c)            # tools, bundles, food
    if out is not None:
        return out
    if b.startswith("clock") or b.startswith("watch"):
        return clock(c)
    if b.startswith("recovery_compass"):
        return compass(c, recovery=True)
    if (b.startswith("compass") or b.startswith("lodestonecompass") or b.startswith("lodestone_compass")) \
            and not has(b, "overlay"):
        return compass(c)
    if b.endswith("_raft") or has(b, "bamboo_raft") or has(b, "raft"):
        return boat(c, chest=has(b, "chest"), raft=True)
    if b == "boat" or b.endswith("_boat") or b.startswith("boat_") or b.startswith("chest_boat") or has(b, "_chest_boat"):
        return boat(c, chest=has(b, "chest"))
    if (b.endswith("_bucket") or b.startswith("bucket")) and not has(b, "overlay"):
        return bucket(c)
    if has(b, "minecart"):
        return minecart(c)
    if b in ("book", "written_book", "writable_book", "enchanted_book", "knowledge_book") or b.startswith("book_"):
        return book(c)
    if b in ("map", "filled_map") or (b.startswith("map_") and not has(b, "marker", "overlay")):
        return map_item(c)
    if has(b, "pottery_sherd"):
        return sherd(c)
    if has(b, "smithing_template"):
        return template(c)
    if has(b, "horse_armor") and not has(b, "overlay"):
        return horse_armor(c)
    if has(b, "nautilus_armor"):
        return nautilus_armor(c)
    if b.endswith("_harness") or b == "harness":
        return harness(c)
    if b.endswith("_banner_pattern") or b.startswith("banner_pattern"):
        return banner_pattern(c)
    return None


# -------------------------------------------------------------- plants ------
def coral(c):
    v = Cv(c, 20)
    p = c.pal
    col = p.main if has(c.base, "dead") else (p.accent if np.abs(p.accent - p.main).sum() > 30 else p.main)
    r = v.r
    if has(c.base, "fan"):
        hole = h.fbm(v.H, v.W, r, 4, 2) > 0.72
        ang = np.arctan2(v.Y - 15.5, v.X - 8); rr = np.hypot(v.X - 8, v.Y - 15.5)
        web = (rr < 10.5 - 1.2 * np.abs(np.sin(ang * 4))) & (ang < -0.45) & (ang > -2.7) & ~hole
        v.part(web, shade(col, 0.88), dome=1.2, noise=0.1)
        for k in range(7):
            a = -2.6 + k * (2.1 / 6)
            v.part(v.seg(8, 15.5, 8 + 9.6 * math.cos(a), 15.5 + 9.6 * math.sin(a), 0.42), shade(col, 1.12), dome=0.5)
        return v.done(outline=False)

    def branch(x, y, a, L, w, d):
        x1, y1 = x + L * math.cos(a), y + L * math.sin(a)
        v.part(v.seg(x, y, x1, y1, w), col, dome=0.7, noise=0.1)
        if d == 0 or L < 1.2:
            v.part(v.circ(x1, y1, w * 1.35), shade(col, 1.18), dome=0.6)
            return
        for s in (-1, 1):
            branch(x1, y1, a + s * r.uniform(0.35, 0.75), L * r.uniform(0.62, 0.8), w * 0.78, d - 1)
    branch(8, 16, -math.pi / 2 + r.uniform(-0.15, 0.15), 4.6, 0.95, 3)
    return v.done(outline=False)


def dripleaf(c):
    v = Cv(c, 21)
    b = c.base
    green = c.pal.green
    if has(b, "stem"):
        for x in ((8,) if has(b, "big") else (5.5, 10.5)):
            v.part(v.seg(x, 0, x, 16, 0.7), green, dome=0.6)
        return v.done(outline=False)
    if has(b, "top") and has(b, "big"):
        leaf = v.ell(8, 8, 7.4, 6.6)
        v.part(leaf, green, dome=4, noise=0.08)
        th = np.arctan2(v.Y - 8, v.X - 8)
        v.tint(leaf & (np.abs(np.sin(th * 5)) < 0.08), 0.78)
        v.tint(leaf & (np.hypot(v.X - 8, v.Y - 8) < 0.8), 0.7)
        return v.done(outline=False)
    if has(b, "side") or has(b, "tip"):
        v.part(v.ell(8, 2.2, 7.6, 1.6), green, dome=0.8)
        return v.done(outline=False)
    for x0, a in ((6, -2.2), (10, -0.9)):   # small dripleaf
        v.part(v.seg(8, 16, x0, 7, 0.45), shade(green, 0.85), dome=0.4)
        v.part(v.ell(x0 + 1.6 * math.cos(a), 6 + 1.6 * math.sin(a), 3.2, 1.6, a), green, dome=1.0)
    return v.done(outline=False)


def lily_pad(c):
    v = Cv(c, 22)
    col = c.pal.main
    pad = v.circ(8, 8, 7.2)
    th = np.arctan2(v.Y - 8, v.X - 8)
    notch = (np.abs(th - 0.9) < 0.22)
    pad &= ~notch
    v.part(pad, col, dome=5, noise=0.08)
    v.tint(pad & (np.abs(np.sin(th * 4.5)) < 0.06), 0.82)
    return v.done(outline=False)


def bloom_cluster(v, cx, cy, col, n, rad, r):
    for _ in range(n):
        a = r.uniform(0, 2 * math.pi); d = r.uniform(0, rad)
        x, y = cx + d * math.cos(a), cy + d * math.sin(a)
        v.part(v.circ(x, y, r.uniform(0.7, 1.1)), shade(col, r.uniform(0.85, 1.15)), dome=0.5)


def tall_flower(c):
    v = Cv(c, 23)
    b = c.base
    p = c.pal
    green = p.green
    bloom = p.accent if np.abs(p.accent - green).sum() > 70 else p.light
    r = v.r
    if has(b, "sunflower_front") or has(b, "sunflower_back"):
        front = has(b, "front")
        for k in range(14):
            a = k * 2 * math.pi / 14
            v.part(v.ell(8 + 5.0 * math.cos(a), 8 + 5.0 * math.sin(a), 2.0, 0.95, a),
                   (246, 206, 50) if front else shade(green, 0.9), dome=0.8)
        disc = v.circ(8, 8, 3.6)
        v.part(disc, (96, 60, 30) if front else green, dome=2)
        if front:
            v.tint(disc & ((np.floor(v.X * 1.5) + np.floor(v.Y * 1.5)) % 2 == 0), 0.78)
        return v.done(outline=False)
    stems = (5, 8, 11)
    if has(b, "bottom"):
        for x in stems:
            v.part(v.seg(x, 16, x + r.uniform(-0.6, 0.6), 0, 0.5), shade(green, 0.9), dome=0.4)
        for _ in range(6):
            x, y = r.uniform(3, 13), r.uniform(4, 14)
            h.paint_leaf(v.rgb, np.zeros((v.H, v.W)), v.a, y * S, x * S, 2.2 * S, 0.8 * S, r.uniform(0, 6.28),
                         shade(green, r.uniform(0.85, 1.1)), v.H, v.W, wrap=False)
        return v.done(outline=False)
    # top: dandiyan upar tak + phoolon ka guchha
    top = 5.0
    for x in stems:
        v.part(v.seg(x, 16, x + r.uniform(-0.6, 0.6), top + 2, 0.45), shade(green, 0.9), dome=0.4)
    if has(b, "sunflower"):
        return v.done(outline=False)
    if has(b, "rose"):
        for x in stems:
            bloom_cluster(v, x, top + 1, (200, 36, 46), 4, 1.2, r)
    else:
        bloom_cluster(v, 8, top + 1.5, bloom, 16, 4.0, r)
    return v.done(outline=False)


def ground_flowers(c):
    v = Cv(c, 24)
    b = c.base
    r = v.r
    p = c.pal
    if has(b, "litter"):
        for _ in range(16):
            h.paint_leaf(v.rgb, np.zeros((v.H, v.W)), v.a, r.uniform(1, 15) * S, r.uniform(1, 15) * S, 1.6 * S, 0.7 * S,
                         r.uniform(0, 6.28), shade(p.main, r.uniform(0.75, 1.15)), v.H, v.W, wrap=False)
        return v.done(outline=False)
    if has(b, "stem"):
        for _ in range(10):
            x, y = r.uniform(2, 14), r.uniform(2, 14)
            v.part(v.seg(x, y, x + r.uniform(-1.5, 1.5), y + r.uniform(-1.5, 1.5), 0.3), p.green, dome=0.3)
        return v.done(outline=False)
    col = p.accent if np.abs(p.accent - p.green).sum() > 60 else p.light
    for _ in range(8):
        x, y = r.uniform(2.5, 13.5), r.uniform(2.5, 13.5)
        for k in range(5):
            a = k * 2 * math.pi / 5 + r.uniform(0, 1)
            v.part(v.circ(x + 0.9 * math.cos(a), y + 0.9 * math.sin(a), 0.62), shade(col, r.uniform(0.9, 1.15)), dome=0.4)
        v.part(v.circ(x, y, 0.45), (250, 214, 80), flat=True)
    return v.done(outline=False)


def cocoa(c):
    v = Cv(c, 25)
    st = g.stage_of(c.base) or 0
    s = [0.62, 0.8, 1.0][min(st, 2)]
    col = [(128, 146, 62), (176, 124, 58), (146, 82, 40)][min(st, 2)]
    v.part(v.seg(8, 0, 8, 2.4, 0.4), (110, 90, 50), dome=0.3)
    pod = v.ell(8, 2.4 + 3.6 * s, 2.4 * s, 3.6 * s)
    v.part(pod, col, dome=2.2 * s, spec=0.15)
    v.tint(pod & (np.abs(np.sin((v.X - 8) * 2.6 / s)) < 0.18), 0.78)
    return v.done(outline=False)


def sugar_cane(c):
    v = Cv(c, 26)
    green = c.pal.main if c.pal.grey else c.pal.green
    r = v.r
    for x in (4.0, 8.4, 12.4):
        v.part(v.seg(x, 0, x, 16, 0.85), green, dome=0.9, noise=0.06)
        off = r.uniform(0, 4)
        for y in np.arange(off, 16, 4):
            v.tint(v.seg(x - 0.9, y, x + 0.9, y, 0.22), 0.7)
            if r.random() < 0.5:
                v.part(v.seg(x, y, x + r.choice([-2.4, 2.4]), y - 1.6, 0.3), shade(green, 1.1), dome=0.3)
    return v.done(outline=False)


def vines(c):
    v = Cv(c, 27)
    b = c.base
    green = c.pal.main if c.pal.grey else c.pal.green
    r = v.r
    lit = has(b, "lit") or has(b, "berr")
    for x0 in (3.5, 8.0, 12.5):
        ph = r.uniform(0, 6.28)
        pts = [(x0 + 1.0 * math.sin(ph + y * 0.6), y) for y in np.arange(0, 16.5, 0.5)]
        for (a, b_), (c_, d_) in zip(pts[:-1], pts[1:]):
            v.part(v.seg(a, b_, c_, d_, 0.3), shade(green, 0.8), dome=0.3)
        for y in np.arange(r.uniform(0, 2), 16, 2.2):
            x = x0 + 1.0 * math.sin(ph + y * 0.6)
            h.paint_leaf(v.rgb, np.zeros((v.H, v.W)), v.a, y * S, (x + r.choice([-1.1, 1.1])) * S, 1.3 * S, 0.6 * S,
                         r.uniform(0, 6.28), shade(green, r.uniform(0.85, 1.15)), v.H, v.W, wrap=False)
            if lit and r.random() < 0.4:
                v.part(v.circ(x + r.uniform(-1, 1), y + 0.8, 0.75), (255, 178, 58), glow=True)
    return v.done(outline=False)


def egg_shell(c):
    """Sniffer / turtle egg: chhilke ka apna speckled design (mask functional)."""
    H, W = c.H * S, c.W * S
    r = c.rng(28)
    m = c.ref[..., 3].repeat(S, 0).repeat(S, 1)
    p = c.pal
    base, spot = p.main, p.second if np.abs(p.second - p.main).sum() > 40 else shade(p.main, 0.7)
    n = h.fbm(H, W, r, 8, 3)
    grain = h.fbm(H, W, r, 1, 1)
    alb = h.tone_col(base, 0.88 + 0.2 * n + 0.1 * (grain - 0.5))
    spots = (h.fbm(H, W, r, 4, 2) > 0.6) | (h.fbm(H, W, r, 2, 1) > 0.78) | (r.random((H, W)) > 0.97)
    alb[spots] = h.tone_col(spot, 0.9 + 0.2 * n)[spots]
    if has(c.base, "crack") or has(c.base, "slight") or has(c.base, "very"):
        cr = h.ridged(h.fbm(H, W, r, H // 2, 3)) > (0.97 if has(c.base, "slight") else 0.94)
        alb[cr] *= 0.4
    rgb = h.light(alb, 0.3 * n, k=2.2)
    return h.out_rgba(rgb, m)


def plant_special(c):
    b = c.base
    if has(b, "coral") and not has(b, "block"):
        return coral(c)
    if has(b, "dripleaf"):
        return dripleaf(c)
    if has(b, "lily_pad") or has(b, "waterlily"):
        return lily_pad(c)
    if has(b, "lilac", "peony", "rose_bush", "sunflower", "syringa", "paeonia", "double_plant_rose") and \
            not has(b, "grass", "fern"):
        return tall_flower(c)
    if has(b, "pink_petals", "wildflowers", "leaf_litter"):
        return ground_flowers(c)
    if has(b, "cocoa"):
        return cocoa(c)
    if has(b, "sugar_cane", "reeds"):
        return sugar_cane(c)
    if (b == "vine" or b.startswith("vine_") or has(b, "cave_vines")) and not has(b, "twisting", "weeping"):
        return vines(c)
    return None
