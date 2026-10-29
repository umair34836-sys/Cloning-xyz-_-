# FireCraft Remastered — texture pack

Minecraft ke blocks, items aur mobs ke liye **original** textures, Java 26.2 aur Bedrock dono ke liye.

| File | Kis ke liye |
|------|-------------|
| `dist/FireCraft-Remastered-HD-Java-26.2.zip` | **HD (64x) realistic** — Java 26.2, 2790 textures |
| `dist/FireCraft-Remastered-HD-Bedrock.mcpack` | **HD (64x) realistic** — Bedrock / Geyser, 3058 textures |
| `dist/FireCraft-Remastered-Java-26.2.zip` | Java 26.2 (resource pack format 88) — 2790 textures |
| `dist/FireCraft-Remastered-Bedrock.mcpack` | Bedrock / Geyser players — 3058 textures |
| `showcase-hd.png` | HD designs ka preview |
| `showcase-final.png` | Final: naye tools, food, bundles, detail wale blocks |
| `showcase-mobs.png` | Final mob skins (geometry ke hisaab se har chehra) |
| `showcase-shapes.png` | Theek ki gayi shapes (clock, compass, boats, buckets, coral, phool...) |
| `DETAIL_TESTS.md` | 5 test rounds ka report (detail score) |
| `showcase.png` | 16x designs ka preview |
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

## HD (64x) realistic version — `generate_hd.py`

Blocks/items 64x64 (4x), mobs 2x. Har material height map se banta hai, phir 3D-artist
skills (GitHub: `arjun988/blender-skills` texture-workflow/materials/realistic-style/voxel-style,
`freshtechbro/claudedesignskills` substance-3d-texturing, `RobLe3/cc-blender-skill` materials)
ke rules lagte hain:

- Layering: base rang -> bari variation -> beech ki detail -> micro detail -> kinaron ki roshni -> daraaron ki dhool -> roshni
- Albedo PBR range: non-metal sRGB 30–240, metal ≥ ~110; metal ki chamak base rang se tint, pathar/mitti par chamak nahi
- Roshni upar-baayein se (key ~45°), ambient ~0.5, AO max 35%, roshni garam / saaya thanda
- Har brick/takhta ±5% hue, ±10% value alag; mossy blocks par kaai daraaron me
- Readability: blocks ≤32 rang (voxel-style palette rule) taake noise ka mush na bane
- Lava/glowstone jaise emissive blocks par saaya kam

### Khaas shapes — `shapes_hd.py`

Jo items/paude pehle gol dhabba ban jate the, unke apne vector designs (16-unit coordinates, har hissa
apni dome roshni ke saath):

- **Clock / compass / recovery & lodestone compass:** har frame me sui ghoomti hai (Java `_00..` files aur Bedrock `*_atlas` pattiyan dono); clock me din/raat ka aasmaan
- **Boats, chest boats, bamboo raft · buckets** (pani/lava/doodh/powder snow + andar machhli) · **minecarts** (chest, furnace, TNT, hopper, command)
- **Books** (normal, enchanted, writable, written, knowledge) · **maps** (khali, bhari, explorer `X`, locked)
- **Pottery sherds · smithing templates · banner patterns · horse/nautilus armor · harness** (16 dye rang)
- **Paude:** coral (shaakhein) aur coral fan, dripleaf, lily pad, lambe phool (lilac/peony/rose/sunflower upar-neeche), pink petals/wildflowers/leaf litter, cocoa, sugar cane, vines aur chamakte cave-vine ber, sniffer/turtle egg ka chhilka
- Leather armor/horse armor ka base halka grey (205) — dye ka tint sahi aata hai

### Tools, food, bundles, mobs — `items_hd.py`, `mobs_hd.py`, `details_hd.py`

- **Tools:** har tool ek "handle frame" me banta hai — sword (fuller, tez dhaar, lipti grip, gol guard),
  pickaxe (beech se mota, dono taraf nukeela mudha sir), axe (patla gala, beard wali dhaar, peeche poll),
  shovel (dish wala phal, ridge, socket), hoe (L blade)
- **Food:** steak (charbi, grill), drumstick, machhli (scales, aankh, pakki), apple/golden apple, bread,
  cookie, pie, cake, carrot, potato, beetroot, melon, berries, soups (bowl), spider eye, rotten flesh,
  dried kelp, chorus fruit, honey bottle, egg
- **Bundles:** rassi wali thaili + open front/back, 16 dye rang (Java `red_bundle`, Bedrock `bundle_red`)
- **Mobs (4x):** Bedrock geometry (`models/entity`, `entity/*.entity.json`) se har cube ka har chehra;
  chehre ki roshni, material (fur/feather/skin/scales/bone/slime/metal/plant/chitin), aankhein sir ke saamne
  wale chehre par. Java ke liye 4th argument me Bedrock resource_pack ka path do.
- **Detail passes:** `details_hd.py` (concrete, terracotta, panels, obsidian, dragon egg, aag, gehre rang) —
  dekho `DETAIL_TESTS.md`

## Dobara banana

```bash
git clone --depth 1 --branch 26.2-assets https://github.com/misode/mcmeta ref/java-assets
git clone --depth 1 --filter=blob:none --sparse https://github.com/Mojang/bedrock-samples ref/bedrock && (cd ref/bedrock && git sparse-checkout set resource_pack)
python3 generate.py java ref/java-assets build/java
python3 generate.py bedrock ref/bedrock/resource_pack build/bedrock
python3 package.py build dist
# HD:
python3 generate_hd.py java ref/java-assets build_hd/java ref/bedrock/resource_pack
python3 generate_hd.py bedrock ref/bedrock/resource_pack build_hd/bedrock
python3 package.py build_hd dist --hd
```
