# Detail test report — FireCraft Remastered HD

`tools_detail_test.py` har HD texture ka **detail score** nikalta hai: high-frequency energy
(roshni minus uska blur) × (1 + log(alag rangon ki ginti)/6). Har category ke sab se kam
detail wale textures ki list aur contact sheet banti hai; unhe dekh kar agla detail pass hota hai.

```bash
python3 tools_detail_test.py build_hd/java/assets/minecraft/textures dt test1 60
```

## Rounds (Java, median / p10 — zyada = zyada detail)

| Round | Kya kiya | Blocks | Cut-out | Items | Mobs |
|---|---|---|---|---|---|
| Test 1 | Shuruaati naap | 10.69 / 4.39 | 17.85 / 9.51 | 28.82 / 14.74 | 21.12 / 8.30 |
| Pass 1 | Concrete (bajri, trowel, surakh), terracotta (strata, minerals), machine panels (frame, bolts, emblem), obsidian (sheeshe ke tukde), gehre mobs ke liye additive detail | | | | |
| Test 2 | | 13.35 / 5.85 | 17.85 / 9.51 | 28.82 / 14.74 | 21.23 / 8.72 |
| Pass 2 | Spawn eggs (grain, dhabbe, chamak), coal/charcoal (facets), cut-out me lakdi/metal pehchaan (grain, brushed + specular), sniffer egg, mob fur/skin zyada mazboot + freckles | | | | |
| Test 3 | | 13.35 / 5.85 | 21.94 / 11.77 | 29.49 / 15.34 | 23.02 / 9.77 |
| Pass 3 | Music discs (grooves + vinyl sheen), dye (sheeshe ka qatra), flint, gehre blocks par additive detail, aag me core + chingariyan, dragon egg, door items | | | | |
| Test 4 | | 13.55 / 6.26 | 21.99 / 11.78 | 29.51 / 15.66 | 23.02 / 9.77 |
| Test 5 | Visual test se bug pakda: `campfire_log` aur `firefly_bush` aag ban rahe the (naam me "fire") — theek kiya. Java + Bedrock dono ka full validation | 13.53 / 6.26 | 22.06 / 12.00 | 29.51 / 15.66 | 23.02 / 9.77 |

Test 5 validation: Java 2790/2790 aur Bedrock 3058/3058 textures — sahi size, mobs ka UV mask exact,
koi file missing nahi. Bedrock (test 5): blocks 12.79 / 5.55, cut-out 22.18 / 11.79, items 30.10 / 15.44, mobs 19.38 / 8.16.

Ab bhi sab se neeche wale zyada tar woh hain jo asal me gehre ya smooth hote hain (kaala concrete,
pani, obsidian, kaale mobs) — un par aur detail dalne se wo shor (noise) lagne lagte.

## Final A-to-Z pass (2026-10-29)

- Round-2 reviewers (blocks, cut-outs, items, entities) ki saari reports par fixes: `round2_hd.py`, `ents_hd.py`, `mobs_hd.py`.
- Full rebuild: Java 2790 + Bedrock 3058 textures, 0 errors; size check 5848 files me sirf 3 aise (vanilla me pehle se 64px) jo scale nahi hue.
- Night Vision subpack (`brightness.py`): 2143 block/mob textures + 80 fog + 15 lighting files; preview `showcase-nightvision.png`.
- Final preview: `showcase-final-az.png`.
