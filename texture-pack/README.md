# FireCraft Remastered — texture pack

Minecraft ke blocks, items aur mobs ke liye **original** textures, Java 26.2 aur Bedrock dono ke liye.

| File | Kis ke liye |
|------|-------------|
| `dist/FireCraft-Remastered-Java-26.2.zip` | Java 26.2 (resource pack format 88) — 2790 textures |
| `dist/FireCraft-Remastered-Bedrock.mcpack` | Bedrock / Geyser players — 3058 textures |
| `showcase.png` | Naye designs ka preview |
| `pack_icon.png` | Pack icon (Future Gallery ke Pro editor se banaya) |

## Install

- **Java:** zip ko `.minecraft/resourcepacks/` me daalo → Options → Resource Packs.
- **Bedrock:** `.mcpack` par double-click (ya Settings → Global Resources).
- **Server (Geyser):** `.mcpack` ko `plugins/Geyser-Spigot/packs/` me daalo; Java zip ko `server.properties` ke `resource-pack=` URL se de sakte ho.

## Design ka tareeqa

Vanilla textures se sirf **idea** liya gaya: ye texture kis cheez ki hai (naam), size, aur rangon ki palette.
Har design `generate.py` me code se khud banaya gaya — koi pixel ya layout copy nahi:

- **Ores:** apne gem shapes (plus, kite, step) saaye aur chamak ke saath
- **Planks:** khadi takhtiyan, jod aur keelein · **Logs:** chaal ki lakeerein, top par chakor rings
- **Bricks:** apni int ki deewar · **Cobblestone:** gol pathar (voronoi)
- **Glazed terracotta:** har rang ka apna 4-fold pinwheel motif
- **Metal blocks:** rivets wali plates · **Bookshelf, TNT, crafting table:** apne designs (TNT apne font me)
- **Tools & armor:** apni pixel shapes, material ke rang se
- **Phool, paude, phaslen:** apne sprites · **Pani, lava, aag:** apna animated noise
- **Mobs:** UV map follow hota hai (warna skin ghalat lagegi), rang 4x4 ke mote idea se, texture aur aankhein apni

Bedrock ke PBR maps (`_mers`, `_normal`, `_heightmap`) vanilla hi rehte hain — wo roshni ka data hain, tasveer nahi.

## Dobara banana

```bash
git clone --depth 1 --branch 26.2-assets https://github.com/misode/mcmeta ref/java-assets
git clone --depth 1 --filter=blob:none --sparse https://github.com/Mojang/bedrock-samples ref/bedrock && (cd ref/bedrock && git sparse-checkout set resource_pack)
python3 generate.py java ref/java-assets build/java
python3 generate.py bedrock ref/bedrock/resource_pack build/bedrock
python3 package.py build dist
```
