#!/usr/bin/env python3
"""Minecraft skin (64x64) -> 1 pixel = 1 block 3D statue.

Usage:
  python3 skin_statue.py <skin.png> <ox> <oy> <oz> <facing> [out.txt]
    ox,oy,oz = world block of the statue's front-left-bottom reference (see place())
    facing   = direction the statue's FACE looks: south|north|east|west
Writes vanilla `fill` commands (painter's order, merged into cuboids) to out.txt
and a 3-view preview PNG next to it.

Model (local coords, front = +z, character's right = -x):
  legs  y0-11, body y12-23, head y24-31; right arm x0-3, body x4-11, left arm x12-15
  body/arms/legs depth z2-5, head z0-7
  hat (hood) overlay = 1-block shell around the head (x3-12, y23-32, z-1..8);
  jacket/sleeve/pants overlays are flattened onto the base where opaque.
"""
import sys, json
from PIL import Image

PALETTE = {
    "white_concrete": (207, 213, 214), "orange_concrete": (224, 97, 0),
    "magenta_concrete": (169, 48, 159), "light_blue_concrete": (35, 137, 198),
    "yellow_concrete": (240, 175, 21), "lime_concrete": (94, 168, 24),
    "pink_concrete": (213, 101, 142), "gray_concrete": (54, 57, 61),
    "light_gray_concrete": (125, 125, 115), "cyan_concrete": (21, 119, 136),
    "purple_concrete": (100, 31, 156), "blue_concrete": (44, 46, 143),
    "brown_concrete": (96, 59, 31), "green_concrete": (73, 91, 36),
    "red_concrete": (142, 32, 32), "black_concrete": (8, 10, 15),
    "white_wool": (233, 236, 236), "light_gray_wool": (142, 142, 134),
    "gray_wool": (62, 68, 71), "black_wool": (20, 21, 25), "brown_wool": (114, 71, 40),
    "red_wool": (160, 39, 34), "blue_wool": (53, 57, 157), "light_blue_wool": (58, 175, 217),
    "white_terracotta": (209, 178, 161), "terracotta": (152, 94, 67),
    "light_gray_terracotta": (135, 106, 97), "brown_terracotta": (77, 51, 35),
    "black_terracotta": (37, 22, 16), "pink_terracotta": (161, 78, 78),
    "orange_terracotta": (161, 83, 37), "light_blue_terracotta": (113, 108, 137),
    "cyan_terracotta": (87, 91, 91), "blue_terracotta": (74, 60, 91),
    "snow_block": (249, 254, 254), "calcite": (223, 224, 220), "quartz_block": (235, 229, 222),
    "smooth_stone": (158, 158, 158), "stone": (125, 125, 125), "diorite": (188, 188, 188),
    "polished_andesite": (132, 134, 133), "blackstone": (42, 36, 41),
    "polished_blackstone": (53, 48, 56), "coal_block": (16, 15, 15),
    "deepslate_tiles": (54, 54, 55), "polished_deepslate": (72, 72, 73),
    "dark_oak_planks": (66, 43, 20), "spruce_planks": (114, 84, 48),
    "birch_planks": (192, 175, 121), "oak_planks": (162, 130, 78),
    "cherry_planks": (226, 178, 172), "stripped_birch_log": (196, 176, 118),
    "smooth_sandstone": (223, 214, 170), "mushroom_stem": (203, 196, 185),
    "iron_block": (220, 220, 220), "redstone_block": (175, 24, 5), "lapis_block": (31, 67, 140),
}
# skin tone looks better on terracotta than on yellowish birch
PREFER = {"stripped_birch_log": 1.25, "birch_planks": 1.25, "smooth_sandstone": 1.2}


# exact skin colours -> chosen blocks (MrCopper804 skin tone)
OVERRIDE = {(224, 170, 126): "white_terracotta", (193, 147, 112): "stripped_birch_log"}


def nearest(rgb):
    if tuple(rgb) in OVERRIDE:
        return OVERRIDE[tuple(rgb)]
    r1, g1, b1 = rgb
    best, bd = None, 1e18
    for name, (r2, g2, b2) in PALETTE.items():
        rm = (r1 + r2) / 2
        d = ((2 + rm / 256) * (r1 - r2) ** 2 + 4 * (g1 - g2) ** 2 + (2 + (255 - rm) / 256) * (b1 - b2) ** 2)
        d *= PREFER.get(name, 1.0)
        if d < bd:
            best, bd = name, d
    return best


# part: (box x0,y0,z0, w,h,d), base uv origin, overlay uv origin
# uv origin (U,V) = top-left of the part's net; net layout for size (w,h,d):
#   top (U+d, V) wxd, bottom (U+d+w, V) wxd, right (U, V+d) dxh, front (U+d, V+d) wxh,
#   left (U+d+w, V+d) dxh, back (U+2d+w, V+d) wxh
PARTS = {
    "head": ((4, 24, 0, 8, 8, 8), (0, 0), (32, 0)),
    "body": ((4, 12, 2, 8, 12, 4), (16, 16), (16, 32)),
    "rarm": ((0, 12, 2, 4, 12, 4), (40, 16), (40, 32)),
    "larm": ((12, 12, 2, 4, 12, 4), (32, 48), (48, 48)),
    "rleg": ((4, 0, 2, 4, 12, 4), (0, 16), (0, 32)),
    "lleg": ((8, 0, 2, 4, 12, 4), (16, 48), (0, 48)),
}


def face_uv(lx, ly, lz, w, h, d, faces):
    """Return list of (face, u, v) for a voxel at local part coords on the surface, priority order."""
    out = []
    if lz == d - 1: out.append(("front", lx, h - 1 - ly))
    if lz == 0: out.append(("back", w - 1 - lx, h - 1 - ly))
    if lx == 0: out.append(("right", lz, h - 1 - ly))
    if lx == w - 1: out.append(("left", d - 1 - lz, h - 1 - ly))
    if ly == h - 1: out.append(("top", lx, lz))
    if ly == 0: out.append(("bottom", lx, d - 1 - lz))
    return [f for f in out if f[0] in faces]


def net_xy(face, u, v, w, h, d, U, V):
    if face == "top": return U + d + u, V + v
    if face == "bottom": return U + d + w + u, V + v
    if face == "right": return U + u, V + d + v
    if face == "front": return U + d + u, V + d + v
    if face == "left": return U + d + w + u, V + d + v
    if face == "back": return U + 2 * d + w + u, V + d + v


ALL = ("front", "back", "right", "left", "top", "bottom")


def build_model(img):
    px = lambda x, y: img.getpixel((x, y))
    vox = {}
    for name, ((x0, y0, z0, w, h, d), (U, V), (OU, OV)) in PARTS.items():
        for lx in range(w):
            for ly in range(h):
                for lz in range(d):
                    fs = face_uv(lx, ly, lz, w, h, d, ALL)
                    if not fs:
                        vox[(x0 + lx, y0 + ly, z0 + lz)] = None  # interior, colour decided later
                        continue
                    col = None
                    if name != "head":  # flatten overlay
                        for f, u, v in fs:
                            p = px(*net_xy(f, u, v, w, h, d, OU, OV))
                            if p[3] > 128:
                                col = p; break
                    if col is None:
                        f, u, v = fs[0]
                        col = px(*net_xy(f, u, v, w, h, d, U, V))
                    vox[(x0 + lx, y0 + ly, z0 + lz)] = nearest(col[:3])
    # hood / hat shell: 10x10x10 around head, faces sampled with clamp
    (hx, hy, hz, w, h, d), _, (OU, OV) = PARTS["head"]
    for sx in range(w + 2):
        for sy in range(h + 2):
            for sz in range(d + 2):
                fs = face_uv(sx, sy, sz, w + 2, h + 2, d + 2, ALL)
                if not fs: continue
                pos = (hx - 1 + sx, hy - 1 + sy, hz - 1 + sz)
                if pos in vox: continue
                cl = lambda a, n: max(0, min(n - 1, a - 1))
                for f, u, v in fs:
                    if f in ("front", "back"): uu, vv = cl(u, w), cl(v, h)
                    elif f in ("left", "right"): uu, vv = cl(u, d), cl(v, h)
                    else: uu, vv = cl(u, w), cl(v, d)
                    p = px(*net_xy(f, uu, vv, w, h, d, OU, OV))
                    if p[3] > 128:
                        vox[pos] = nearest(p[:3]); break
    # interior: most common neighbouring colour -> lets cuboids merge
    for pos, b in list(vox.items()):
        if b is None:
            vox[pos] = "white_wool"
    return vox


def rotate(vox, facing):
    """local front=+z; return dict with world-relative offsets (dx,dy,dz)."""
    xs = [p[0] for p in vox]; zs = [p[2] for p in vox]
    cx = (min(xs) + max(xs)) / 2; cz = (min(zs) + max(zs)) / 2
    out = {}
    for (x, y, z), b in vox.items():
        rx, rz = x - cx, z - cz
        if facing == "south": nx, nz = rx, rz
        elif facing == "north": nx, nz = -rx, -rz
        elif facing == "east": nx, nz = rz, -rx
        elif facing == "west": nx, nz = -rz, rx
        out[(int(round(nx + 0.01)), y, int(round(nz + 0.01)))] = b
    return out


def cuboids(vox):
    """painter's algorithm: most common block first, boxes may cover voxels painted later."""
    from collections import Counter
    order = [b for b, _ in Counter(vox.values()).most_common()]
    rank = {b: i for i, b in enumerate(order)}
    cmds = []
    for b in order:
        allowed = {p for p, bb in vox.items() if rank[bb] >= rank[b]}
        need = sorted(p for p, bb in vox.items() if bb == b)
        done = set()
        for p in need:
            if p in done: continue
            x0, y0, z0 = p; x1, y1, z1 = p
            ok = lambda X0, X1, Y0, Y1, Z0, Z1: all((x, y, z) in allowed for x in range(X0, X1 + 1) for y in range(Y0, Y1 + 1) for z in range(Z0, Z1 + 1))
            while ok(x1 + 1, x1 + 1, y0, y1, z0, z1): x1 += 1
            while ok(x0, x1, y1 + 1, y1 + 1, z0, z1): y1 += 1
            while ok(x0, x1, y0, y1, z1 + 1, z1 + 1): z1 += 1
            for x in range(x0, x1 + 1):
                for y in range(y0, y1 + 1):
                    for z in range(z0, z1 + 1):
                        if vox[(x, y, z)] == b: done.add((x, y, z))
            cmds.append((b, x0, y0, z0, x1, y1, z1))
    return cmds


def preview(vox, path):
    from PIL import Image as I
    S = 8
    xs = [p[0] for p in vox]; ys = [p[1] for p in vox]; zs = [p[2] for p in vox]
    W = max(xs) - min(xs) + 1; H = max(ys) - min(ys) + 1; D = max(zs) - min(zs) + 1
    canvas = I.new("RGB", ((W + D + W + 4) * S, (H + 2) * S), (40, 40, 40))
    def draw(ox, getcol, width):
        for a in range(width):
            for y in range(H):
                c = getcol(a, max(ys) - y)
                if c:
                    for i in range(S):
                        for j in range(S):
                            canvas.putpixel(((ox + a) * S + i, (y + 1) * S + j), PALETTE[c])
    def front(a, y):  # looking from +z
        x = min(xs) + a
        col = [(z, b) for (xx, yy, z), b in vox.items() if xx == x and yy == y]
        return max(col)[1] if col else None
    def side(a, y):  # looking from -x (character's right)
        z = max(zs) - a
        col = [(-x, b) for (x, yy, zz), b in vox.items() if zz == z and yy == y]
        return max(col)[1] if col else None
    def back(a, y):
        x = max(xs) - a
        col = [(-z, b) for (xx, yy, z), b in vox.items() if xx == x and yy == y]
        return max(col)[1] if col else None
    draw(1, front, W); draw(W + 2, side, D); draw(W + D + 3, back, W)
    canvas.save(path)


if __name__ == "__main__":
    skin, ox, oy, oz, facing = sys.argv[1], int(sys.argv[2]), int(sys.argv[3]), int(sys.argv[4]), sys.argv[5]
    out = sys.argv[6] if len(sys.argv) > 6 else "statue_cmds.txt"
    img = Image.open(skin).convert("RGBA")
    model = build_model(img)
    preview(model, out.rsplit(".", 1)[0] + "_preview.png")
    world = rotate(model, facing)
    cmds = cuboids(world)
    with open(out, "w") as f:
        for b, x0, y0, z0, x1, y1, z1 in cmds:
            f.write(f"fill {ox+x0} {oy+y0} {oz+z0} {ox+x1} {oy+y1} {oz+z1} minecraft:{b}\n")
    xs = [p[0] for p in world]; zs = [p[2] for p in world]
    meta = {"voxels": len(world), "commands": len(cmds),
            "bbox": [ox + min(xs), oy, oz + min(zs), ox + max(xs), oy + max(p[1] for p in world), oz + max(zs)],
            "blocks": sorted(set(world.values()))}
    print(json.dumps(meta))
