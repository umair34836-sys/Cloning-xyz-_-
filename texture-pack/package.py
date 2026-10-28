#!/usr/bin/env python3
"""Bani hui textures ko installable packs me band karta hai.

Usage: package.py <build-dir> <dist-dir>
  <build-dir>/java    -> dist/FireCraft-Remastered-Java-26.2.zip
  <build-dir>/bedrock -> dist/FireCraft-Remastered-Bedrock.mcpack
"""
import json, os, sys, zipfile
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
NAME = "FireCraft Remastered"
DESC = "Original textures for FireCraft SMP — blocks, items & mobs"
# Bedrock ko har version me same UUID chahiye (update pehchanne ke liye)
HEADER_UUID = "4f6c2b1e-8d3a-4c7e-9b52-1a7e3f0d9c61"
MODULE_UUID = "b83e5d27-6f19-4a0c-8e4d-2c9b7a1f5e30"
VERSION = [1, 0, 0]
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
    build, dist = sys.argv[1:3]
    os.makedirs(dist, exist_ok=True)

    java = os.path.join(dist, "FireCraft-Remastered-Java-26.2.zip")
    with zipfile.ZipFile(java, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as zf:
        zf.writestr("pack.mcmeta", json.dumps({"pack": {
            "description": f"§6{NAME}§r\n§7{DESC}",
            "min_format": JAVA_FORMAT[0], "max_format": JAVA_FORMAT[1]}}, indent=2, ensure_ascii=False))
        icon(256).save(os.path.join(dist, ".pack.png"))
        zf.write(os.path.join(dist, ".pack.png"), "pack.png")
        zipdir(zf, os.path.join(build, "java"))
    os.remove(os.path.join(dist, ".pack.png"))

    bedrock = os.path.join(dist, "FireCraft-Remastered-Bedrock.mcpack")
    with zipfile.ZipFile(bedrock, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as zf:
        zf.writestr("manifest.json", json.dumps({
            "format_version": 2,
            "header": {"name": NAME, "description": DESC, "uuid": HEADER_UUID,
                       "version": VERSION, "min_engine_version": [1, 21, 0]},
            "modules": [{"type": "resources", "uuid": MODULE_UUID, "version": VERSION, "description": DESC}],
        }, indent=2))
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
