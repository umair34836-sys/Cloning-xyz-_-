#!/usr/bin/env python3
"""Bani hui textures ko installable packs me band karta hai.

Usage: package.py <build-dir> <dist-dir> [--hd]
  <build-dir>/java    -> dist/FireCraft-Remastered[-HD]-Java-26.2.zip
  <build-dir>/bedrock -> dist/FireCraft-Remastered[-HD]-Bedrock.mcpack
  <build-dir>/bedrock_subpacks (agar ho) -> Bedrock pack ke andar "Night Vision" ON/OFF subpacks
"""
import json, os, sys, zipfile
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
NAME = "FireCraft Remastered"
DESC = "Original textures for FireCraft SMP — blocks, items & mobs"
# Bedrock ko har version me same UUID chahiye (update pehchanne ke liye)
HEADER_UUID = "4f6c2b1e-8d3a-4c7e-9b52-1a7e3f0d9c61"
MODULE_UUID = "b83e5d27-6f19-4a0c-8e4d-2c9b7a1f5e30"
VERSION = [1, 1, 0]
JAVA_FORMAT = (88, 101)   # 26.2 = resource pack format 88


def zipdir(zf, root, prefix=""):
    for r, _, files in os.walk(root):
        for f in sorted(files):
            p = os.path.join(r, f)
            zf.write(p, os.path.join(prefix, os.path.relpath(p, root)))


def icon(size):
    im = Image.open(os.path.join(HERE, "pack_icon.png")).convert("RGBA")
    return im.resize((size, size), Image.LANCZOS)


def main():
    global NAME, DESC, HEADER_UUID, MODULE_UUID
    build, dist = sys.argv[1:3]
    tag = ""
    if "--hd" in sys.argv[3:]:
        tag = "-HD"
        NAME, DESC = NAME + " HD", "Realistic 64x textures for FireCraft SMP — blocks, items & mobs"
        HEADER_UUID, MODULE_UUID = "9a2d7c4e-31b8-4f6a-a5e2-6c0d8b1f7e94", "e15b3f80-7c2a-4d9e-b6f1-3a8c5d2e0b67"
    os.makedirs(dist, exist_ok=True)

    java = os.path.join(dist, f"FireCraft-Remastered{tag}-Java-26.2.zip")
    with zipfile.ZipFile(java, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as zf:
        zf.writestr("pack.mcmeta", json.dumps({"pack": {
            "description": f"§6{NAME}§r\n§7{DESC}",
            "min_format": JAVA_FORMAT[0], "max_format": JAVA_FORMAT[1]}}, indent=2, ensure_ascii=False))
        icon(256).save(os.path.join(dist, ".pack.png"))
        zf.write(os.path.join(dist, ".pack.png"), "pack.png")
        zipdir(zf, os.path.join(build, "java"))
    os.remove(os.path.join(dist, ".pack.png"))

    bedrock = os.path.join(dist, f"FireCraft-Remastered{tag}-Bedrock.mcpack")
    with zipfile.ZipFile(bedrock, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as zf:
        man = {
            "format_version": 2,
            "header": {"name": NAME, "description": DESC, "uuid": HEADER_UUID,
                       "version": VERSION, "min_engine_version": [1, 21, 0]},
            "modules": [{"type": "resources", "uuid": MODULE_UUID, "version": VERSION, "description": DESC}],
        }
        subs = os.path.join(build, "bedrock_subpacks")       # brightness.py ka banaya Night Vision mode
        if os.path.isdir(subs):
            # aakhri subpack default hota hai -> Night Vision ON; gear icon ke slider se OFF
            man["subpacks"] = [{"folder_name": "off", "name": "Night Vision: OFF", "memory_tier": 0},
                               {"folder_name": "night_vision", "name": "Night Vision: ON", "memory_tier": 0}]
            zipdir(zf, subs, "subpacks")
        zf.writestr("manifest.json", json.dumps(man, indent=2))
        icon(256).save(os.path.join(dist, ".pack_icon.png"))
        zf.write(os.path.join(dist, ".pack_icon.png"), "pack_icon.png")
        zipdir(zf, os.path.join(build, "bedrock"))
    os.remove(os.path.join(dist, ".pack_icon.png"))

    for p in (java, bedrock):
        with zipfile.ZipFile(p) as zf:
            n = sum(1 for i in zf.namelist() if i.endswith((".png", ".tga")))
        print(f"{os.path.basename(p)}: {os.path.getsize(p) / 1e6:.1f} MB, {n} images")


if __name__ == "__main__":
    main()
