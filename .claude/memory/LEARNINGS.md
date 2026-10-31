# Learnings

Jo bhi naya seekha jaye yahan likho (project conventions, repo facts, tool usage). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — kya seekha + kyun useful hai.

---

## 2026-10-23

- **Google Drive download (cloud env)**: file ko "Anyone with the link" share karna zaroori hai, aur network policy me `drive.google.com` + `drive.usercontent.google.com` allow hone chahiye. Bari files ka asal download: `https://drive.usercontent.google.com/download?id=<ID>&export=download&confirm=t`. gdown proxy ke peeche aksar fail hota hai → direct curl use karo.
- **APK analysis (native RE tools nahi)**: env me sirf `unzip` + `python3` hain (jadx/apktool/aapt nahi). APK = zip → unzip; binary `AndroidManifest.xml` ko `strings -e l` (UTF-16) se parho; SDKs classes.dex me `strings | grep` se; Cocos games ki logic `assets/src` + `assets/assets` JS bundles me hoti hai.
- **Ludo APK (`game.jthzsrlgl.zegzwtdlly`)**: Cocos Creator 3.7.x + oops-framework online Ludo; AppsFlyer+Adjust, AdMob/AppLovin/IronSource ads, Firebase push, Play Games cloud-save, protobuf+AES+pako backend. Full report: `analysis/ludo-apk-analysis.md`.

- Repo `umair34836-sys/Cloning-xyz-_-` fresh tha (no commits). Pehla kaam: memory/rules system set up karna.
- Designated dev branch: `ccr-7349d8d1-89knkk`.
- Tool name mapping jo confirm hui:
  - "Agent reach" → `Panniantong/Agent-Reach` (~101k stars).
  - "Wishpher" → `openai/whisper` (~111k stars).
  - "Agency agents" → `msitarzewski/agency-agents` (~163k stars).
- Ambiguous names resolve ki (best-guess high-star, user ki marzi se):
  - "Rea / reverse engineering anything" → `morluto/rea` (~371k) — "Reverse Engineer Anything" (user-confirmed; "REA" ek known tool/acronym hai).
  - "Ecc" → `affaan-m/ECC` (~287k) — "Everything Claude Code" agent harness.
  - "UI/UX Pro Max" → `nextlevelbuilder/ui-ux-pro-max-skill` (user-confirmed).
  - "GraftV / Graft" → `NanoNets/Graft` (user-confirmed). Repo ka actual naam sirf "Graft" hai.
- Poori list ka theme: zyada tar **Claude Code skills / agent packs** hain (ECC, agency-agents, reverse-skill, uiux-pro-max, agent-reach) + whisper (speech model).
- Install method: user ne **git submodules** choose kiya. Shallow add: `git -c protocol.file.allow=always submodule add --depth 1 <url> tools/<name>`.
- Disk ~30G free; sab submodules mila kar ~120M. ECC sabse bara (~76M).
- **jadx on dex**: `jadx -d out --no-res classes.dex` (Java 22 OK; `unset JAVA_TOOL_OPTIONS` noise se bachne ke liye). Cocos app ka custom Java `com.game.*` (SDK bridge) hota hai.
- **Cocos `.jsc` = XXTEA-encrypted JS**; key `libcocos.so` (native) me, `cc.js` me nahi. Decrypt ke liye disassembler (Ghidra/IDA) ya device-par Frida runtime chahiye.
- **Container limits for RE**: radare2 apt se install nahi hota (no package/sudo); koi Android device/emulator nahi (headless x86) → live Frida yahan impossible. REA MCP khud Ghidra/Hopper/IDA ya adb-device maangta hai.
- **Security testing (owner-authorized)**: sirf non-destructive recon (headers, TLS, cookies, exposed paths, CORS, auth-enforcement, info-disclosure) — no DoS/brute-force/data-damage. Live user-data ko chhuye baghair IDOR/SSRF ko "verify + self-test instructions" ke tor par report karo.
- **gallery.talkember.site** = Future Gallery (React SPA + /api/v1 on LiteSpeed, v0.7.0). Review: `analysis/gallery-talkember-security-review.md`. Main finds: verify IDOR on /photos/{id}, SSRF on /photos/from-url (unauth POST→400 = auth ordering issue), temp-link token strength; HSTS+CSP missing; /api/health leaks version.
- Network: `gallery.talkember.site` ko bhi allowlist karna pada (Drive ki tarah).
- **video.talkember.site** = "AI Video Automator" marketing site (LiteSpeed, strong CSP+HSTS). EXE: `/files/AI-Video-Automator-Setup-1.1.1.exe` (~620MB, NSIS/electron-builder). Admin panel `/admin/login`. Review: `analysis/video-talkember-and-exe-security-review.md`.
- **NSIS/electron extraction (no 7z installed)**: `npm i 7zip-bin @electron/asar` → `7zip-bin` ka bundled `7za` (chmod +x) NSIS ko 7z ki tarah extract karta hai; phir `@electron/asar` se app.asar unpack. PE Authenticode signing python se check (security data-dir size==0 → unsigned).
- Static analysis se EXE run kiye baghair: config.js/auth.js/server.js/tunnel.js + Electron main.cjs padh kar auth model samjha. App well-secured tha (DNS-rebind defense, Electron sandbox, scrypt). Main gap: installer unsigned + token-in-URL + gateway register.php verify.
- `video.talkember.site` ko bhi network allowlist karna pada.
- **gallery re-test (2026-10-24)**: HSTS+CSP added, /api/health version removed, from-url ab auth-first 401 (SSRF ordering fixed). Sab injection/path-traversal probes 401 (auth-first). Baaki IDOR + temp-link = authenticated-only, test account ke baghair verify nahi.

## 2026-10-29 — WorldLens (FireCraft SMP console)
- `worldlens_command` se koi bhi console command chalti hai; monitoring ke liye sirf read-only: `list`, `tps`, `mspt`, `whitelist list`, `banlist`, `plugins`, `data get entity <p> Pos`.
- Activity history CoreProtect se: `co lookup t:1h a:session` / `a:chat`. Paging: `co page N` (lookup ke baad). `page:N` param lookup me kaam nahi karta. `a:kill` ke liye user ya radius chahiye.
- Server console log ka direct tail tool nahi hai — CoreProtect hi history ka source hai.
- Discord (WorldLens bot): DMs off; staff members "off_limits" (moderation nahi), lekin channel me <@id> mention chal jata hai. techshotgaming Discord id 1376918464379359346. Staff baatein #🛡️┃staff-chat (1563030676968837171) me.
- `msg <player>` offline player ko nahi jata ("No player was found") — mail plugin nahi hai; player online aane par bhejo.
- CoreProtect tricks: `r:#global` zaroori jab user na ho; time range `t:1.8d-1.65d` kaam karta hai; item pickup `a:+item`, inventory `a:inventory` (u: zaroori). Tunnel Drill jaisi custom items se tootay blocks log nahi hote — item pickup se trace karo.
- Discord Administrator role wale har private channel dekh sakte hain — channel overrides se nahi rukta. ⚙️ Manager + MrzCopper804 roles ke paas Admin hai. Protected/staff/bot roles channel_permissions ka target nahi ban sakte.
- Manager audit: `co lookup u:<p> a:command` (commands), `lp log recent`, `lp log grouphistory <g>`, `lp user <p> permission check <perm>`, `data get entity <p> playerGameType` (1 = creative).
- LP trialstaff group: `*` true, lekin firecraft.owner & coreprotect.purge false (console ne set kiye).
- Bad Gateway (07:28–07:40 UTC 29 Oct) = manager ne server /stop (maintenance) + WorldLens 1.5→1.6 update. Connector restart ke baad khud wapis aata hai.
- Hourly trigger is session me notification ke roop me aata hai (ReadNotifications); per-minute cron ke saath queue ho kar thoda late (~2 min) fire hota hai.
- `lp log recent 2` = page 2; `lp group <g> listmembers`.
- WorldLens shot sirf loaded chunks dikhata hai → door ke base ke liye `forceload add x1 z1 x2 z2` (≤256 chunks), shots ke baad `forceload remove ...` aur `forceload query` se verify.
- Shot cooldown 10s. Underground camera ke liye `execute if block x y z minecraft:air` se khaali jagah dhoondo.
- Bases dhoondhne ka tareeqa: `co lookup t:30d r:#global a:+block b:chest,barrel` → coordinates cluster karo.
- Discord tools (WorldLens): incoming attachments padh nahi sakte (owner ki image messages text "" dikhti hain). **Correction (30 Oct):** `discord_send_message` ka `image` param saved WorldLens shot (file name, e.g. 20261030-070216_shot_...png) embed me post KAR sakta hai. Embed ke liye `embed_title`/`embed_description`/`embed_color` params (embed object nahi).
- Base photos repo me commit nahi ki (reports/worldlens/bases/ gitignored) — privacy.
- Kick reason console log me hota hai; WorldLens command output me nahi aata. AuthMe checks: `authme accounts <name|ip>`, `authme lastlogin`, `authme getip`. LifeStealZ: `lifestealz hearts get <p>`. firecraft MCP: get_user(ign) se website account/application.
- "lost connection: Disconnected" = client ne connection toda (server kick hota to "kicked" + reason). GrimAC 2.3.74 ka shaded packetevents 2.13.1 MC 26.2 par "Unknown entity metadata type id: 105" deta hai → affected player desync/disconnect. Fix: Grim update; temp: grim.exempt.
- 30 Oct: LP ranks kisi aur session ne (Console) dobara banaye: owner/manager/youtuber/builder/loyal/member/exposed/default. Bot ke naam se staff-chat me posts doosre sessions se bhi aa sakti hain (by_this_bot=true matlab sirf same bot, zaroori nahi ye session).
- MC 26.2 item syntax works: `item replace entity <p> armor.head with minecraft:netherite_helmet[enchantments={protection:4,...},trim={pattern:"minecraft:silence",material:"minecraft:gold"},custom_name={text:"..",color:"gold",bold:true,italic:false},unbreakable={}]`. Equip se pehle `data get entity <p> equipment` se check karo slot khaali hai (replace purana item mita deta hai).
- Beacon NBT: `setblock x y z minecraft:beacon{primary_effect:"minecraft:regeneration",secondary_effect:"minecraft:regeneration"}` + 4-level iron pyramid → Regen II.
- WorldLens top PNG (tool-results me saved) ko Python PIL se per-block sample karke ASCII map banao (scale 4 → block = 4px, origin = x-r, z-r) — exact coords bina 100 probes ke.
- `place feature minecraft:cherry X Y Z` / `flower_cherry` / `flower_meadow` = ek command me full tree/flowers. Pehle `execute if block X Y-1 Z grass_block` check.
- `execute if ... run <cmd>` ka blank output = condition fail / run ka output nahi dikhta → result `data get`/`execute if block` se verify karo.
- WorldEdit console se: `worldedit:/world world` → `worldedit:/pos1 x,y,z` / `pos2` → `worldedit:/count <mask>`, `worldedit:/replace "<mask>" <pattern>`; khatam hone par `worldedit:/world` (override hatao). Mask `~water` = 6 sides (upar/neeche bhi) me adjacent.
- WorldGuard region console se: WE selection (`worldedit:/world world`, `worldedit:/pos1/pos2`) phir `rg define -w world <name>` — kaam karta hai. Flags `rg flag -w world <name> <flag> <value>`. build deny ke saath use/interact ALLOW alag se do warna doors/buttons band.
- Same design multiple jagah: `clone` (WE selection/count se pehle exact bounding box nikaalo, `worldedit:/count !air`).
- Sign readability: dark oak + glowing colored text = blurry. Birch/light sign + no glow + dark hex colors (`color:"#B00000"`) = saaf. Invisible light: `light[level=15]` block (ground lanterns ko `worldedit:/replace "lantern >grass_block" light[level=15]` se hidden lights banaya).
- WE mask `>X` = block jo X ke upar hai. `##dirt` tag mask kaam nahi kiya (0) — exact block names use karo.
- Mob farm (no spawner): mobs ko 22 block girao (zombie 20hp → 1hp, 1-hit + XP); 23+ = fall se mar jaate (sirf drops). Mob landing cell me water mat rakho (water fall damage cancel kar deta hai). Spawn ke liye player se >24 block door. Water source 7 blocks flow karta hai — arm length 8 (source + 7) hole tak.
- Farm/mechanism test: `forceload add x1 z1 x2 z2` (chunks entity-tick karte hain bina player) + `summon zombie ... {PersistenceRequired:1b,CustomName:"FarmTest"}` → `data get entity @e[name=FarmTest,limit=1] Pos/Health` se track; test ke baad `forceload remove`.
- `execute if entity @e[x,y,z,dx,dy,dz,type=..]` → "Test passed. Count: N" (mobs gin-ne ka tareeqa). 26.2 me gamerule names snake_case (e.g. `gamerule spawn_mobs`).
- Circular layout via WE console: `worldedit:/pos1 cx,y,cz` then `worldedit:/cyl <block> r` (filled disk, layer by layer bade se chhote radius) aur `worldedit:/hcyl <block> r [height]` (ring/hedge). Plots ko ring roads ke beech fit karne ke liye har corner ka radius calculate karo (inner road r < min, outer road r > max). Hedge gaps: `fill ... air replace azalea_leaves`.
- WG plots: parent region (build deny, use/interact allow, prio 5) + child plot regions (build allow, prio 10) — child me flag set na ho to parent ka deny lagta hai, isliye plot pe explicit `build allow` zaroori.
- Dropped items bachana: `execute as @e[type=item,...] run data merge entity @s {Age:-32768,Invulnerable:1b}` (kabhi despawn nahi). Chest me daalna: chest ke upar `hopper[facing=down]` + items ko hopper ke upar tp (PickupDelay:0s) → apne aap double chest me (verify `data get block ... Items`, phir hopper hata do). Double chest setblock: facing=south ke liye east half type=left, west half type=right.
- Civilization fast: `place structure minecraft:village_desert x y z` (jigsaw village + villagers + golem, terrain adapt). Pehle WE `/count chest,planks,torch...` se area me player builds check karo (cobblestone+2 chest underground = natural dungeon).
- Surface road on uneven terrain: WE mask `"sand,stone,... <air"` (block jiske upar air ho = surface) → `/replace` smooth_sandstone. Surface pe lamp: `execute positioned X 0 Z positioned over motion_blocking_no_leaves run fill ~ ~ ~ ~ ~2 ~ dark_oak_fence`.
- ClickVillagers plugin: picked-up villagers player_head items ban jaate hain (custom_data clickvillagers:nbt) — ye bhi player ka saman hai.
- Negative z/x ke block ka centre = coord + 0.5 (block -62 ka centre -61.5, NOT -62.5). Summon/tp me iska khayal rakho warna entity wall me.
- Trading hall pods: villager 1x1x2 cell, workstation peeche wall me, front pe fence (villager nikal nahi sakta, player upar se trade kar sakta). `summon villager ... {VillagerData:{profession:"minecraft:librarian",level:1,type:"minecraft:desert"},PersistenceRequired:1b}`. Carpet floor ke UPAR (y+1) lagao, floor y pe nahi.
- Hosting panel: sahi domain `panel.skilloraclouds.com` (user ne "skilloaraclouds" likha tha = NXDOMAIN). Network full hai, lekin panel + /api/client "Checking your browser | SkilloraClouds" challenge (403) ke peeche hai. Headless browser se challenge paar karna = bot-check circumvention → auto-mode ne deny kiya, dobara try MAT karo. Raasta: host se API whitelist / user khud panel use kare / WorldLens console.
- Session worker restart hone par CronCreate wale session-only jobs gayab ho jaate hain (CronList empty). Restart notice ke baad CronList check karo aur zaroori jobs (jaise build_event bossbar) same prompt se recreate karo.
- DiscordSRV linking: player in-game `/discord link` → 4-digit code → Discord pe "Firecraft smp" bot ko DM me code. Console check: `discord linked <player>` (agar "Interpreted as Discord name" aaye to woh Discord user hai, MC player nahi). Owner ka "@Firecraft smp PM 3716" (chill-chat 30 Oct) = galat jagah link code.
- (31 Oct) WorldEdit console masks: quoted intersection works, e.g. `worldedit:/replace "air >deepslate_bricks" deepslate_tiles`. To avoid upward cascade, each layer must use a DIFFERENT block than the mask (bricks -> tiles -> wall -> lantern). Wall-top upgrade recipe: tile band + brick_wall parapet + 12% lanterns + 15% cracked texture.
- (31 Oct) MC 26.x: `doFireTick` gamerule removed → use `gamerule minecraft:fire_spread_radius_around_player <n>` (NOTE: 0 did NOT stop spread — use 1 = only within 1 block of a player; default 128).
- (31 Oct) WE console keeps undo history: `worldedit:/undo N` (with `worldedit:/world world` set) reverts the last N console edits. `^block` pattern keeps states (walls/stairs). `~mask` = adjacent-to mask works; `[min][max]` count syntax does NOT parse.
