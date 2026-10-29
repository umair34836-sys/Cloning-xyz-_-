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
    ("fur", ("cow", "mooshroom", "sheep", "wolf", "fox", "cat", "ocelot", "horse", "donkey", "mule", "llama",
             "bear", "rabbit", "goat", "panda", "camel", "sniffer", "hoglin", "zoglin", "bat", "bee", "trader_llama")),
    ("feather", ("chicken", "parrot", "phantom", "allay", "vex")),
    ("bone", ("skeleton", "stray", "bogged", "wither")),
    ("slime", ("slime", "magma")),
    ("metal", ("iron_golem", "copper_golem", "golem")),
    ("plant", ("creeper", "creaking")),
    ("chitin", ("spider", "endermite", "silverfish", "shulker")),
    ("scales", ("cod", "salmon", "tropical", "pufferfish", "fish", "guardian", "turtle", "frog", "tadpole", "nautilus")),
    ("skin", ("pig", "villager", "witch", "illager", "pillager", "vindicator", "evoker", "zombie", "husk", "drowned",
              "player", "piglin", "steve", "alex", "wandering_trader", "enderman", "blaze", "ghast", "dolphin",
              "axolotl", "warden", "ravager", "strider", "squid", "breeze", "armadillo")),
]


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
        strands = h.fbm(H, W, r, s * 3, 3, 1)
        fine = h.fbm(H, W, r, s, 2, 1)
        tufts = h.fbm(H, W, r, s * 2, 2)
        return 0.82 + 0.22 * strands + 0.1 * fine + 0.06 * tufts, 0.5 * strands + 0.3 * tufts + 0.2 * fine, 0.0
    if mat == "feather":
        sz = 1.5 * s
        arc = np.abs(((xx / sz + (np.floor(yy / sz) % 2) * 0.5) % 1) - 0.5) + ((yy / sz) % 1) * 0.4
        return 0.92 + 0.14 * (1 - arc), 0.4 * (1 - arc), 0.05
    if mat == "scales":
        sz = 1.2 * s
        cy = (yy / sz) % 1
        cx = (xx / sz + (np.floor(yy / sz) % 2) * 0.5) % 1
        rim = np.hypot(cx - 0.5, cy - 0.1)
        return 0.9 + 0.16 * np.clip(1 - rim, 0, 1), 0.5 * np.clip(1 - rim, 0, 1), 0.35
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
        a_patch = alb[Y0:Y1, X0:X1]
        import details_hd
        a_patch[sel] = details_hd.entity_detail(np.clip(col * tone[..., None], 0, 255), col, tone_d[Y0:Y1, X0:X1])[sel]
        h_patch = hgt[Y0:Y1, X0:X1]
        h_patch[sel] = (0.5 + hgt_d[Y0:Y1, X0:X1] * 0.5)[sel]
        if f["face"] == "north" and "head" in f["bone"] and f["cube"] == 0:
            eye_faces.append((X0, Y0, X1, Y1))
    lit = h.light(alb, hgt, k=1.6, spec=spec, shin=24, wrap=False, dirt=0.15)
    rgb[mask] = lit[mask]
    if not any(w in c.name.lower() for w in g.NO_FACE):
        for X0, Y0, X1, Y1 in eye_faces[:1]:
            draw_eyes(rgb, X0, Y0, X1, Y1, c)
    out[..., :3] = rgb
    return out


def draw_eyes(rgb, X0, Y0, X1, Y1, c):
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
