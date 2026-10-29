#!/usr/bin/env python3
"""Bedrock pack ka built-in "Night Vision" mode (subpack) banata hai — bina mod/shader ke.

Usage: brightness.py <bedrock-build-dir> <vanilla-bedrock-rp> <out-subpacks-dir>

Aaj ke Bedrock (RenderDragon) me resource pack shader/lightmap nahi badal sakta, is liye ye mode:
  * blocks aur mobs ki textures ko gamma-lift deta hai (andhere hisse khul jate hain, roshan hisse
    zyada nahi jalte) — andheri jagah par bhi sab saaf dikhta hai;
  * har biome ki fog halki/door kar deta hai (hawa, barish, paani, lava);
  * Vibrant Visuals wale players ke liye raat/ghaar ki ambient roshni aur chaand ki roshni badhata hai.
Pack settings (gear icon) me "Night Vision: ON / OFF" slider se ek click me badla ja sakta hai.
"""
import json, os, re, sys
import numpy as np
from PIL import Image

GAMMA = 1.6           # 0.10 -> 0.24, 0.30 -> 0.47, 0.50 -> 0.65, 0.80 -> 0.87
LIFT_DIRS = ("textures/blocks", "textures/entity")
AMBIENT = 0.6         # Vibrant Visuals ambient (vanilla overworld 0.02, nether 0.5)
MOON = 2.0            # vanilla 0.4


def lift(path_in, path_out):
    im = Image.open(path_in)
    mode = im.mode
    rgba = np.asarray(im.convert("RGBA")).astype(np.float32)
    rgb = rgba[..., :3] / 255.0
    lum = rgb @ np.array([0.299, 0.587, 0.114], np.float32)
    new = np.power(np.clip(lum, 1e-4, 1), 1 / GAMMA)
    rgb = np.clip(rgb * (new / np.maximum(lum, 1e-4))[..., None], 0, 1)     # rang (hue) wahi, sirf roshni
    rgba[..., :3] = rgb * 255
    out = Image.fromarray(np.round(rgba).astype(np.uint8), "RGBA")
    os.makedirs(os.path.dirname(path_out), exist_ok=True)
    if path_out.endswith(".tga"):
        out.save(path_out, compression="tga_rle")
    elif mode == "P":
        q = out.quantize(256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE)
        if (np.asarray(q.convert("RGBA"))[..., 3] == np.asarray(out)[..., 3]).all():
            out = q
        out.save(path_out, optimize=True)
    else:
        out.save(path_out, optimize=True)


def clear_fog(d):
    dist = d.get("minecraft:fog_settings", {}).get("distance", {})
    for k, v in dist.items():
        if not isinstance(v, dict) or "fog_end" not in v:
            continue
        if v.get("render_distance_type") == "render":
            v["fog_start"] = max(v["fog_start"], 0.9 if k == "air" else 0.6)
            v["fog_end"] = max(v["fog_end"], 1.0)
        else:
            far = {"water": 140.0, "lava": 10.0, "lava_resistance": 16.0}.get(k, 60.0)
            v["fog_end"] = max(v["fog_end"], far)
            v["fog_start"] = min(v.get("fog_start", 0), v["fog_end"] * 0.3)
        tf = v.get("transition_fog")
        if isinstance(tf, dict) and isinstance(tf.get("init_fog"), dict):
            tf["init_fog"]["fog_end"] = max(tf["init_fog"]["fog_end"], 20.0)
    return d


def brighter_lighting(d):
    ls = d.get("minecraft:lighting_settings", {})
    amb = ls.get("ambient")
    if isinstance(amb, dict) and isinstance(amb.get("illuminance"), (int, float)):
        amb["illuminance"] = max(amb["illuminance"], AMBIENT)
    dl = ls.get("directional_lights", {})
    moon = (dl.get("orbital") or dl).get("moon") if isinstance(dl, dict) else None
    if isinstance(moon, dict) and isinstance(moon.get("illuminance"), dict):
        moon["illuminance"] = {k: max(v, MOON) for k, v in moon["illuminance"].items()}
    return d


def load_json(p):
    txt = open(p, encoding="utf-8").read()
    txt = re.sub(r"^\s*//.*$", "", txt, flags=re.M)        # Mojang ki files me kabhi comments hote hain
    return json.loads(txt)


def main():
    build, vanilla, out = sys.argv[1:4]
    nv = os.path.join(out, "night_vision")
    off = os.path.join(out, "off")
    os.makedirs(off, exist_ok=True)
    n = 0
    for sub in LIFT_DIRS:
        root = os.path.join(build, sub)
        for r, _, files in os.walk(root):
            for f in files:
                if f.endswith((".png", ".tga")) and not f.endswith("_mer.png") and "_normal" not in f:
                    src = os.path.join(r, f)
                    lift(src, os.path.join(nv, os.path.relpath(src, build)))
                    n += 1
    for folder, fn in (("fogs", clear_fog), ("lighting", brighter_lighting)):
        for f in sorted(os.listdir(os.path.join(vanilla, folder))):
            if f.endswith(".json"):
                try:
                    d = fn(load_json(os.path.join(vanilla, folder, f)))
                except ValueError:
                    continue
                os.makedirs(os.path.join(nv, folder), exist_ok=True)
                with open(os.path.join(nv, folder, f), "w") as fh:
                    json.dump(d, fh, indent=2)
    # OFF wale subpack me ek chhoti si file (khaali folder zip me gum ho jata hai)
    with open(os.path.join(off, "night_vision_off.txt"), "w") as fh:
        fh.write("FireCraft Remastered: Night Vision OFF (vanilla roshni)\n")
    print(f"night vision: {n} textures, fog + lighting -> {nv}")


if __name__ == "__main__":
    main()
