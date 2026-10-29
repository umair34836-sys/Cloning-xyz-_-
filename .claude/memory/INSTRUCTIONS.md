# Instructions Log

Yahan user ki di hui saari instructions store hoti hain (jab bhi koi naya kaam ya hidayat milti hai, usay yahan likha jata hai). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — instruction (short, clear)

---

## 2026-10-23

- Install/collect these tools into the project:
  - **Agent Reach** → `Panniantong/Agent-Reach` (AI agent internet reader/search CLI)
  - **Rea / Reverse-engineering-anything** → `morluto/rea` (~371k stars, "Reverse Engineer Anything", confirmed by user, installed)
  - **Ecc** → `affaan-m/ECC` (Everything Claude Code, installed)
  - **Whisper** ("Wishpher") → `openai/whisper` (installed)
  - **Agency Agents** → `msitarzewski/agency-agents` (installed)
  - **GraftV / Graft** → `NanoNets/Graft` (user ne URL diya, installed)
  - **UI/UX Pro Max** ("Uiuxpromax") → `nextlevelbuilder/ui-ux-pro-max-skill` (user ne URL diya, installed)
- Prefer high-star / popular repos where ambiguous. Install method: **git submodules** under `tools/`.
- **Standing rule (memory):** Jab bhi user koi instruction de — usay GitHub par store karo. Rules bhi store karo. Jo cheezein seekhein (learnings) wo bhi. Jo ghaltiyan hon (mistakes) wo bhi — taake same ghalti dobara na ho. Ek memory file rakho aur hamesha up-to-date rakho.

## 2026-10-28

- "worldlens connector connect ker k mere server ka status dekao" → WorldLens MCP (`worldlens_status`) se FireCraft SMP ka status check kiya (TPS 20, MSPT ~8.2, 3/20 players online).
- "all players k ass pass k images nikalker mujhe doo" → Har online player ka top-down map + camera shot WorldLens se liya, `worldlens-shots/2026-10-28/` me save kiya aur user ko bheja.
- "exact player k ass pass dekao jis me player bhi nazar aye" → Har player ke 4-5 blocks paas se close-up shot liya (camera seedha player ki taraf), aur player ki jagah laal marker + naam lagaya. Files: `worldlens-shots/2026-10-28/players-closeup/`.
- "minecraft ke saare blocks/items/mobs ka texture pack banao, gallery Pro editor se, GitHub/web/wiki reference se, exact jitna ho sake lekin khud banao" → Pro editor pixel art ke liye unfit nikla (30 shapes/picture, 100 edits/day), user ne code se pixel art chuna + Java & Bedrock dono.
- Beech me user ne kaha: "ye bohot kharab ho gaye, in se idea le kar NEW designs banao, copy nahi" → poora generator dobara likha: original designs, reference se sirf naam/size/palette. Output: `texture-pack/` (generate.py, package.py, dist/*.zip + *.mcpack, showcase.png). Pack icon gallery Pro editor se (photo id 1fc07fc5-6d21-4f22-925d-8f0961e357c6).
- "inko high quality aur realistic banao" → `generate_hd.py`: 64x blocks/items, 2x mobs, height map + roshni/AO/specular. Output: `texture-pack/dist/FireCraft-Remastered-HD-*`.
- "pehle 3D artist ki saari skills GitHub se install karo, knowledge lo, phir use karo" → `arjun988/blender-skills`, `RobLe3/cc-blender-skill`, `freshtechbro/claudedesignskills` scratchpad me padhi aur rules HD generator me lagaye. `tools/` me submodule install auto-mode ne rok diya (untrusted code) — user ki ijazat chahiye.
- "haan skills tools me install kar do, aur sab ke shapes ek baar dekh lo aur fix kar do" → 3 skill repos `tools/` me submodule (blender-skills, cc-blender-skill, claudedesignskills). Saare 796 items + 575 cut-out blocks ki contact sheets dekh kar `texture-pack/shapes_hd.py` banaya (clock/compass/boats/buckets/minecarts/books/maps/sherds/templates/horse armor/harness/banner patterns + coral/dripleaf/lily pad/tall flowers/petals/cocoa/sugar cane/vines/eggs).
- "food, bundles aur mobs bhi high quality me fix karo, tools (shovel, axe, hoe, pickaxe, sword) ka shape fix karo" → `items_hd.py` (tools handle-frame me, food, bundles), `mobs_hd.py` (Bedrock geometry se har face, 4x).
- "jinme details nahi hai unhe test karo, detail add karo, phir test, phir aur detail, phir test, phir 2 aur testing, phir final dikhao" → `tools_detail_test.py` + `details_hd.py`; 5 test rounds + 3 detail passes, report `texture-pack/DETAIL_TESTS.md`.

## 2026-10-29

- "candles, ender pearl aur flower pot bhi fix karo, aur minecraft ke saare items/mobs/blocks A to Z test karke details add karo aur fix karo — wo hoga final" → candle/ender pearl/flower pot ke apne designs (`items_hd.py`), phir 2 round A-to-Z QA (har category ki poori contact sheet + reviewer agents). Fixes: `blocks_hd2.py`, `cutouts_hd.py`, `items2_hd.py`, `round2_hd.py` (round-2 blocks/cut-outs), `ents_hd.py` (pottery motifs, banner/shield kapda, chest/boat/armor stand lakdi, armor plates, horse markings), `mobs_hd.py` (scales, golem/guardian plates, ghast shooting/happy chehre, panda dhabbe, bee dhaariyan, mooshroom, squid/allay/strider aankhein, gehri khaal par aankh ka ring).
- "texture pack me nightvision bhi lage, alag mod/brightness pack na lagana pare, khaas Bedrock ke liye taake servers par bhi chale, inbuilt brightness ho aur user ek click se on/off kar sake" → `texture-pack/brightness.py`: Bedrock pack me 2 subpacks (`Night Vision: ON` default, `OFF`) — gear icon ka slider. ON = blocks/mobs textures gamma-lift + saari fog halki + Vibrant Visuals lighting tez. User ko hadd bata di (RenderDragon me lightmap nahi badalta).

