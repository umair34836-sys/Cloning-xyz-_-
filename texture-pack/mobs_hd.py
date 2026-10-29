"""FireCraft Remastered HD — mobs ki skins, Bedrock geometry (cube UV faces) ki madad se.

Bedrock ke `models/entity/*.geo.json` me har mob ke cubes aur unka UV layout hota hai, aur
`entity/*.entity.json` batata hai kaunsi texture kis geometry par lagti hai. Isse har pixel ka
pata chalta hai ke wo kis cube ke kis chehre (upar/neeche/saamne/side/peeche) ka hissa hai.
Har chehra phir apni roshni, kinaron ka AO aur mob ke material (fur/skin/scales/haddi/...)
ke saath banta hai; aankhein sirf sir ke saamne wale chehre par.
Rang ka idea har chehre ke mote 3x3 grid se (smooth) — pixel copy nahi.
"""
import glob, json, math, os, re
import numpy as np

import generate as g
import generate_hd as h

shade, mix, has = g.shade, g.mix, g.has
_INDEX = None


def _load(path):
    try:
        t = open(path, encoding="utf-8").read()
    except Exception:
        return None
    t = re.sub(r"^\s*//.*$", "", t, flags=re.M)
    try:
        return json.loads(t)
    except Exception:
        return None


def _geometries(models_dir):
    geos, parents = {}, {}
    for f in glob.glob(os.path.join(models_dir, "**", "*.json"), recursive=True):
        d = _load(f)
        if not isinstance(d, dict):
            continue
        for key, val in d.items():
            if key.startswith("geometry.") and isinstance(val, dict):
                name, _, parent = key.partition(":")
                geos[name] = {"tw": val.get("texturewidth", 64), "th": val.get("textureheight", 64),
                              "bones": val.get("bones") or []}
                if parent:
                    parents[name] = parent
        for gd in d.get("minecraft:geometry", []) if isinstance(d.get("minecraft:geometry"), list) else []:
            desc = gd.get("description", {})
            name = desc.get("identifier")
            if name:
                geos[name] = {"tw": desc.get("texture_width", 64), "th": desc.get("texture_height", 64),
                              "bones": gd.get("bones") or []}
    for name, parent in parents.items():   # inheritance: parent ki bones + child override
        pb = {b.get("name"): b for b in geos.get(parent, {}).get("bones", [])}
        for b in geos[name]["bones"]:
            pb[b.get("name")] = b
        geos[name]["bones"] = list(pb.values())
        if parent in geos and not geos[name].get("tw"):
            geos[name]["tw"], geos[name]["th"] = geos[parent]["tw"], geos[parent]["th"]
    return geos


def index(rp_dir):
    """texture path ('entity/creeper/creeper') -> geometry dict."""
    global _INDEX
    if _INDEX is not None:
        return _INDEX
    _INDEX = {}
    if not rp_dir or not os.path.isdir(rp_dir):
        return _INDEX
    geos = _geometries(os.path.join(rp_dir, "models"))
    for f in glob.glob(os.path.join(rp_dir, "entity", "*.json")):
        d = _load(f)
        try:
            desc = d["minecraft:client_entity"]["description"]
        except Exception:
            continue
        tex, geo = desc.get("textures", {}), desc.get("geometry", {})
        if not tex or not geo:
            continue
        for k, path in tex.items():
            gid = geo.get(k) or geo.get("default") or next(iter(geo.values()))
            if gid in geos:
                _INDEX.setdefault(path.replace("textures/", "", 1), geos[gid])
    by_base = {}
    for k in list(_INDEX):
        by_base.setdefault(os.path.basename(k), []).append(k)
    for b, ks in by_base.items():
        if len(ks) == 1:
            _INDEX.setdefault("@" + b, _INDEX[ks[0]])
    return _INDEX


def faces_for(rel, W, H, rp_dir):
    idx = index(rp_dir)
    key = os.path.splitext(rel.replace("\\", "/"))[0]
    geo = idx.get(key) or idx.get("@" + os.path.basename(key))
    if not geo and "baby" not in key:
        # naye Java naam (pig_temperate, cow_cold...): usi folder ka koi model jiska texture size mile
        folder = os.path.dirname(key) + "/"
        for k, gg in idx.items():
            if k.startswith(folder) and "baby" not in k and gg["tw"] / max(1, gg["th"]) == W / max(1, H):
                geo = gg
                break
    if not geo:
        return []
    sx = sy = W / max(1, geo["tw"])      # ek hi scale (Java 64x64 skin par Bedrock 64x32 layout upar wale hisse me same hai)
    out = []
    for bone in geo["bones"]:
        bname = (bone.get("name") or "").lower()
        for ci, cube in enumerate(bone.get("cubes") or []):
            size = cube.get("size") or [0, 0, 0]
            w, hh, d = (max(0, int(math.floor(float(x) + 1e-6))) for x in size)
            uv = cube.get("uv")
            if isinstance(uv, list) and len(uv) == 2:
                u, v = uv
                rects = {"up": (u + d, v, w, d), "down": (u + d + w, v, w, d), "east": (u, v + d, d, hh),
                         "north": (u + d, v + d, w, hh), "west": (u + d + w, v + d, d, hh),
                         "south": (u + 2 * d + w, v + d, w, hh)}
            elif isinstance(uv, dict):
                rects = {}
                for fn, fd in uv.items():
                    if isinstance(fd, dict) and "uv" in fd:
                        x0, y0 = fd["uv"]; fw, fh = fd.get("uv_size", [0, 0])
                        rects[fn] = (min(x0, x0 + fw), min(y0, y0 + fh), abs(fw), abs(fh))
            else:
                continue
            for fn, (x, y, fw, fh) in rects.items():
                if fw <= 0 or fh <= 0:
                    continue
                out.append(dict(face=fn, bone=bname, cube=ci, x=x * sx, y=y * sy, w=fw * sx, h=fh * sy))
    return out


MATERIAL = [
    ("golem", ("iron_golem", "copper_golem")),
    ("wet", ("frog", "axolotl", "tadpole", "dolphin", "squid")),
    ("glowskin", ("allay", "vex")),
    ("fur", ("cow", "mooshroom", "sheep", "wolf", "fox", "cat", "ocelot", "horse", "donkey", "mule", "llama",
             "bear", "rabbit", "goat", "panda", "camel", "sniffer", "hoglin", "zoglin", "bat", "bee", "trader_llama")),
    ("feather", ("chicken", "parrot", "phantom")),
    ("bone", ("skeleton", "stray", "bogged", "wither")),
    ("slime", ("slime", "magma")),
    ("plant", ("creeper", "creaking")),
    ("chitin", ("spider", "endermite", "silverfish", "shulker")),
    ("scales", ("cod", "salmon", "tropical", "pufferfish", "fish", "guardian", "turtle", "nautilus", "dragon")),
    ("skin", ("pig", "villager", "witch", "illager", "pillager", "vindicator", "evoker", "zombie", "husk", "drowned",
              "player", "piglin", "steve", "alex", "wandering_trader", "enderman", "blaze", "ghast",
              "warden", "ravager", "strider", "breeze", "armadillo")),
]
CLOTH = ("villager", "illager", "pillager", "vindicator", "evoker", "witch", "wandering_trader", "zombie_villager")


def material_of(name):
    n = name.lower()
    for mat, words in MATERIAL:
        if any(w in n for w in words):
            return mat
    return "skin"


def detail(mat, H, W, r, s):
    """Material ka apna surface: tone multiplier + height."""
    yy, xx = h.grid(H, W)
    if mat == "fur":
        # chhote guchhe (clumps) + chhote resho ke strokes, lambi lakdi jaisi dhaariyan nahi
        clump = h.fbm(H, W, r, s * 2, 3)
        short = h.fbm(H, W, r, max(1, s // 2), 2, max(1, s // 4)) if s >= 2 else h.fbm(H, W, r, 1, 1)
        stroke = np.sin((xx * 0.9 + yy * 0.5 + 6 * clump) * 1.6 / max(1, s / 2))
        t = 0.84 + 0.18 * clump + 0.08 * short + 0.05 * stroke
        return t, 0.45 * clump + 0.25 * short + 0.1 * stroke, 0.0
    if mat == "wet":
        n = h.fbm(H, W, r, s * 4, 3)
        spots = h.nrm(h.fbm(H, W, r, s * 2, 2)) > 0.78
        return np.where(spots, 0.8, 0.95 + 0.08 * n), 0.2 * n, 0.6
    if mat == "glowskin":
        n = h.fbm(H, W, r, s * 4, 3)
        return 1.0 + 0.1 * n, 0.2 * n, 0.4
    if mat == "golem":
        P = 4 * s
        py, px = yy % P, xx % P
        edge = np.minimum.reduce([py, px, P - py, P - px])
        rivet = (np.hypot(py - s, px - s) < s * 0.45)
        rust = h.nrm(h.fbm(H, W, r, s * 3, 3)) > 0.72
        t = np.where(edge < s * 0.4, 0.8, 0.96 + 0.06 * h.fbm(H, W, r, 1, 2, s * 4))
        t = np.where(rivet, 1.15, np.where(rust, 0.78, t))
        return t, 0.4 * np.clip(edge / s, 0, 1) + 0.3 * rivet, 0.5
    if mat == "feather":
        sz = 1.5 * s
        arc = np.abs(((xx / sz + (np.floor(yy / sz) % 2) * 0.5) % 1) - 0.5) + ((yy / sz) % 1) * 0.4
        return 0.92 + 0.14 * (1 - arc), 0.4 * (1 - arc), 0.05
    if mat == "scales":
        # ek doosre par chadhe hue gol chhilke (har chhilka neeche se gol, upar wala uske upar)
        sz = 1.4 * s
        row = np.floor(yy / (sz * 0.6))
        cx = xx / sz + (row % 2) * 0.5
        fx = cx - np.floor(cx) - 0.5
        fy = (yy / (sz * 0.6)) - row
        d = np.hypot(fx, (fy - 0.0) * 0.8)
        t = 0.92 + 0.12 * (1 - np.clip(d / 0.7, 0, 1)) - 0.05 * ((d > 0.5) & (d < 0.58))
        return t, 0.5 * (1 - np.clip(d / 0.7, 0, 1)), 0.4
    if mat == "bone":
        n = h.fbm(H, W, r, s * 4, 3)
        cr = h.ridged(h.fbm(H, W, r, s * 6, 3)) > 0.975
        return np.where(cr, 0.7, 0.95 + 0.08 * n), 0.2 * n - 0.3 * cr, 0.15
    if mat == "slime":
        n = h.fbm(H, W, r, s * 4, 3)
        return 0.92 + 0.1 * n, 0.3 * n, 0.55
    if mat == "metal":
        n = h.fbm(H, W, r, 1, 2, s * 6)
        return 0.9 + 0.14 * n, 0.1 * n, 0.6
    if mat == "plant":
        blot = h.fbm(H, W, r, s * 3, 3)
        return np.where(blot > 0.58, 0.8, np.where(blot < 0.38, 1.14, 1.0)) * (0.95 + 0.08 * h.fbm(H, W, r, 1, 1)), 0.4 * blot, 0.0
    if mat == "chitin":
        n = h.fbm(H, W, r, s * 3, 3)
        return 0.9 + 0.12 * n, 0.3 * n, 0.5
    n = h.fbm(H, W, r, s * 4, 3); pores = r.random((H, W)) < 0.01
    blot = h.fbm(H, W, r, s * 2, 2)
    freck = (h.fbm(H, W, r, max(1, s // 2), 1) > 0.8)
    t = 0.92 + 0.12 * n + 0.06 * (blot - 0.5)
    return np.where(pores | freck, t * 0.88, t), 0.2 * n + 0.1 * blot, 0.1


FACE_TONE = {"up": 1.12, "down": 0.74}


def render(c, s, rp_dir, base_img):
    """base_img: hd_masked ka result (jo pixels kisi face me na aayein unke liye)."""
    ref = c.ref
    Hs, Ws = ref.shape[:2]
    faces = faces_for(c.name, Ws, Hs, rp_dir)
    if not faces:
        return None
    H, W = Hs * s, Ws * s
    r = c.rng(500)
    mask = (ref[..., 3] > 0).repeat(s, 0).repeat(s, 1)
    mat = material_of(c.name)
    cloth = any(w in c.name.lower() for w in CLOTH)
    tone_d, hgt_d, spec = detail(mat, H, W, r, s)
    out = base_img.copy()
    rgb = out[..., :3].copy()
    alb = rgb.copy(); hgt = np.full((H, W), 0.5)
    yy, xx = h.grid(H, W)
    refc = ref[..., :3].astype(float)
    eye_faces = []
    for f in faces:
        x0, y0 = int(round(f["x"])), int(round(f["y"]))
        fw, fh = int(round(f["w"])), int(round(f["h"]))
        x1, y1 = min(Ws, x0 + fw), min(Hs, y0 + fh)
        x0, y0 = max(0, x0), max(0, y0)
        if x1 <= x0 or y1 <= y0:
            continue
        fm = ref[y0:y1, x0:x1, 3] > 0
        if not fm.any():
            continue
        # rang ka idea: face ka 3x3 grid (har cell ka average), phir smooth
        gy, gx = min(3, y1 - y0), min(3, x1 - x0)
        grid = np.zeros((gy, gx, 3))
        ys = np.linspace(y0, y1, gy + 1).astype(int); xs = np.linspace(x0, x1, gx + 1).astype(int)
        mean_all = refc[y0:y1, x0:x1][fm].mean(0)
        for i in range(gy):
            for j in range(gx):
                cell = ref[ys[i]:max(ys[i + 1], ys[i] + 1), xs[j]:max(xs[j + 1], xs[j] + 1)]
                cm = cell[..., 3] > 0
                grid[i, j] = cell[..., :3][cm].mean(0) if cm.any() else mean_all
        Y0, Y1, X0, X1 = y0 * s, y1 * s, x0 * s, x1 * s
        fy = (np.arange(Y1 - Y0) + 0.5) / (Y1 - Y0) * gy - 0.5
        fx = (np.arange(X1 - X0) + 0.5) / (X1 - X0) * gx - 0.5
        iy0 = np.clip(np.floor(fy).astype(int), 0, gy - 1); iy1 = np.clip(iy0 + 1, 0, gy - 1)
        ix0 = np.clip(np.floor(fx).astype(int), 0, gx - 1); ix1 = np.clip(ix0 + 1, 0, gx - 1)
        ty = np.clip(fy - np.floor(fy), 0, 1)[:, None, None]; tx = np.clip(fx - np.floor(fx), 0, 1)[None, :, None]
        col = (grid[iy0][:, ix0] * (1 - tx) + grid[iy0][:, ix1] * tx) * (1 - ty) + \
              (grid[iy1][:, ix0] * (1 - tx) + grid[iy1][:, ix1] * tx) * ty
        # chehre ki roshni: upar roshan, neeche gehra, side par upar->neeche gradient, kinaron par AO
        ft = FACE_TONE.get(f["face"])
        if ft is None:
            ft = 1.07 - 0.18 * ((np.arange(Y1 - Y0) + 0.5) / (Y1 - Y0))[:, None]
        ry = np.arange(Y1 - Y0)[:, None] + np.zeros((1, X1 - X0))
        rx = np.arange(X1 - X0)[None, :] + np.zeros((Y1 - Y0, 1))
        edge = np.minimum(np.minimum(ry, (Y1 - Y0 - 1) - ry), np.minimum(rx, (X1 - X0 - 1) - rx))
        # cube ke ubhre kinare: upar wala kinara roshni pakadta hai, neeche wala halka gehra
        ao = np.ones_like(ry, float)
        if f["face"] not in ("up", "down"):
            ao = np.where(ry < 0.7 * s, 1.07, np.where((Y1 - Y0 - 1) - ry < 0.7 * s, 0.9, 1.0))
        sel = mask[Y0:Y1, X0:X1]
        tone = ft * ao * tone_d[Y0:Y1, X0:X1]
        if cloth and "head" not in f["bone"] and "hat" not in f["bone"]:
            yy_, xx_ = h.grid(Y1 - Y0, X1 - X0)
            weave = np.where(((xx_ // max(1, s // 2)) + (yy_ // max(1, s // 2))) % 2 == 0, 1.04, 0.96)
            folds = 1 + 0.06 * np.sin(xx_ / (Y1 - Y0 + 1) * 6.0 * math.pi)
            tone = ft * ao * weave * folds * (0.95 + 0.05 * tone_d[Y0:Y1, X0:X1])
        a_patch = alb[Y0:Y1, X0:X1]
        import details_hd
        a_patch[sel] = details_hd.entity_detail(np.clip(col * tone[..., None], 0, 255), col, tone_d[Y0:Y1, X0:X1])[sel]
        h_patch = hgt[Y0:Y1, X0:X1]
        h_patch[sel] = (0.5 + hgt_d[Y0:Y1, X0:X1] * 0.5)[sel]
        if f["face"] == "north" and f["cube"] == 0 and ("head" in f["bone"] or not eye_faces and
                                                       any(w in c.name.lower() for w in ("ghast", "slime", "magma", "guardian", "blaze"))):
            if "head" in f["bone"]:
                eye_faces.insert(0, (X0, Y0, X1, Y1))
            else:
                eye_faces.append((X0, Y0, X1, Y1))
    lit = h.light(alb, hgt, k=1.6, spec=spec, shin=24, wrap=False, dirt=0.15)
    rgb[mask] = lit[mask]
    if not any(w in c.name.lower() for w in g.NO_FACE):
        for X0, Y0, X1, Y1 in eye_faces[:1]:
            draw_eyes(rgb, X0, Y0, X1, Y1, c)
    out[..., :3] = rgb
    return out


FACE_STYLE = [
    ("none", ("warden", "creaking", "shulker", "endermite", "silverfish", "breeze", "armor")),
    ("creeper", ("creeper",)),
    ("spider", ("spider",)),
    ("skull", ("skeleton", "stray", "bogged", "wither")),
    ("ender", ("enderman",)),
    ("single", ("guardian",)),
    ("ghast", ("ghast",)),
    ("golem", ("golem",)),
    ("human", ("villager", "illager", "pillager", "vindicator", "evoker", "witch", "zombie", "husk", "drowned",
               "player", "steve", "alex", "wandering_trader", "piglin", "ravager")),
]


def face_style(name):
    n = name.lower()
    for st, words in FACE_STYLE:
        if any(w in n for w in words):
            return st
    return "animal"


def ellipse(face, cx, cy, rx, ry, col):
    hh, ww = face.shape[:2]
    yy, xx = h.grid(hh, ww)
    m = ((xx - cx) / max(rx, 0.5)) ** 2 + ((yy - cy) / max(ry, 0.5)) ** 2 < 1
    face[m] = col
    return m


def draw_eyes(rgb, X0, Y0, X1, Y1, c):
    style = face_style(c.name)
    if style == "none":
        return
    fw, fh = X1 - X0, Y1 - Y0
    if fw < 8 or fh < 6:
        return
    face = rgb[Y0:Y1, X0:X1]
    skin = np.median(face.reshape(-1, 3), 0)
    dark = np.array((20, 18, 24.0))
    if style == "creeper":
        for ex in (0.25, 0.625):
            face[int(fh * 0.25):int(fh * 0.5), int(fw * ex):int(fw * (ex + 0.25))] = dark
        face[int(fh * 0.5):int(fh * 0.75), int(fw * 0.375):int(fw * 0.625)] = dark
        face[int(fh * 0.62):int(fh * 0.9), int(fw * 0.25):int(fw * 0.375)] = dark
        face[int(fh * 0.62):int(fh * 0.9), int(fw * 0.625):int(fw * 0.75)] = dark
        return
    if style == "spider":
        for ex, ey, r_ in ((0.3, 0.45, 0.09), (0.7, 0.45, 0.09), (0.18, 0.3, 0.05), (0.82, 0.3, 0.05),
                           (0.4, 0.28, 0.045), (0.6, 0.28, 0.045), (0.12, 0.55, 0.04), (0.88, 0.55, 0.04)):
            ellipse(face, fw * ex, fh * ey, fw * r_, fw * r_, (230, 30, 30))
            ellipse(face, fw * ex - fw * r_ * 0.3, fh * ey - fw * r_ * 0.3, fw * r_ * 0.3, fw * r_ * 0.3, (255, 160, 150))
        return
    if style == "skull":
        for ex in (0.3, 0.7):
            ellipse(face, fw * ex, fh * 0.48, fw * 0.12, fh * 0.11, dark)
        ellipse(face, fw * 0.5, fh * 0.66, fw * 0.05, fh * 0.06, dark)
        face[int(fh * 0.8):int(fh * 0.84), int(fw * 0.28):int(fw * 0.72)] = shade(skin, 0.5)
        for k in range(5):
            x = int(fw * (0.3 + k * 0.1))
            face[int(fh * 0.76):int(fh * 0.88), x:x + max(1, fw // 64)] = shade(skin, 0.5)
        return
    if style == "ender":
        for ex in (0.28, 0.72):
            ellipse(face, fw * ex, fh * 0.55, fw * 0.16, fh * 0.05, (230, 120, 255))
        return
    if style == "single":
        ellipse(face, fw * 0.5, fh * 0.5, fw * 0.22, fh * 0.22, (236, 230, 220))
        ellipse(face, fw * 0.5, fh * 0.5, fw * 0.12, fh * 0.12, (230, 150, 40))
        ellipse(face, fw * 0.5, fh * 0.5, fw * 0.05, fh * 0.12, dark)
        return
    if style == "ghast":
        for ex in (0.28, 0.72):
            face[int(fh * 0.4):int(fh * 0.45), int(fw * (ex - 0.12)):int(fw * (ex + 0.12))] = dark
            face[int(fh * 0.45):int(fh * 0.7), int(fw * (ex - 0.02)):int(fw * (ex + 0.02))] = (170, 190, 210)
        ellipse(face, fw * 0.5, fh * 0.75, fw * 0.12, fh * 0.06, dark)
        return
    if style == "golem":
        for ex in (0.32, 0.68):
            ellipse(face, fw * ex, fh * 0.45, fw * 0.09, fh * 0.06, (60, 20, 20))
            ellipse(face, fw * ex, fh * 0.45, fw * 0.04, fh * 0.03, (240, 60, 40))
        face[int(fh * 0.5):int(fh * 0.8), int(fw * 0.44):int(fw * 0.56)] = shade(skin, 0.8)
        return
    if style == "animal":
        for ex in (0.2, 0.8):
            ellipse(face, fw * ex, fh * 0.42, fw * 0.075, fh * 0.09, dark)
            ellipse(face, fw * ex - fw * 0.02, fh * 0.39, fw * 0.025, fh * 0.03, (250, 250, 250))
        if "panda" in c.name.lower():
            for ex in (0.2, 0.8):
                m = np.zeros(face.shape[:2], bool)
                yy, xx = h.grid(fh, fw)
                m = (((xx - fw * ex) / (fw * 0.15)) ** 2 + ((yy - fh * 0.45) / (fh * 0.2)) ** 2 < 1)
                face[m] = (30, 30, 34)
                ellipse(face, fw * ex, fh * 0.42, fw * 0.05, fh * 0.06, (240, 240, 240))
        return
    _human_eyes(rgb, X0, Y0, X1, Y1, c)


def _human_eyes(rgb, X0, Y0, X1, Y1, c):
    fw, fh = X1 - X0, Y1 - Y0
    if fw < 8 or fh < 6:
        return
    face = rgb[Y0:Y1, X0:X1]
    skin = np.median(face.reshape(-1, 3), 0)
    light_skin = (skin @ h.LUM) >= 170
    iris = c.pal.accent if np.abs(c.pal.accent - skin).sum() > 80 else np.array([70, 100, 150.0])
    yy, xx = h.grid(fh, fw)
    ey = fh * 0.55
    for ex, sgn in ((fw * 0.28, 1), (fw * 0.72, -1)):
        q = ((xx - ex) / (fw * 0.13)) ** 2 + ((yy - ey) / (fh * 0.085)) ** 2
        eye = q < 1
        face[eye] = (40, 38, 46) if light_skin else (236, 234, 230)
        cx = ex + sgn * fw * 0.03
        face[eye & (np.hypot(xx - cx, yy - ey) < fw * 0.065)] = iris
        face[eye & (np.hypot(xx - cx, yy - ey) < fw * 0.032)] = (12, 10, 16)
        face[eye & (np.hypot(xx - cx + fw * 0.02, yy - ey + fh * 0.03) < fw * 0.016)] = (255, 255, 255)
        face[(q >= 1) & (q < 1.5) & (yy < ey)] = shade(skin, 0.7)               # palak / brow saaya
    mouth = (np.abs(yy - fh * 0.82) < max(1, fh * 0.025)) & (np.abs(xx - fw / 2) < fw * 0.14)
    face[mouth] = shade(skin, 0.5)
