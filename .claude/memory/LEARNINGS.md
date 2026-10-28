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

## 2026-10-28

- **WorldLens connector** (`mcp__worldlens__*`) = FireCraft SMP (Paper) ka camera + console. Server status ke liye `worldlens_status` (TPS, MSPT, players, worlds). Tools deferred hain — pehle ToolSearch se load karo. Spawn ≈ x 675, y 70, z 57 ('world').
- Player positions: `worldlens_command` → `execute as @a run data get entity @s Pos` (aur `... Dimension`). Bedrock names (`.Name`) bhi isi se aa jaate hain.
- WorldLens `worldlens_top`/`worldlens_shot` par **10 s cooldown** hai aur parallel calls fail hoti hain — ek ek karke chalao (beech me `timeout 11 tail -f /dev/null` se wait).
- Underground player (y < 0) ke liye top map sirf surface dikhata hai — unki exact eye position se `worldlens_shot` lo. Surface players ke liye ~30 blocks peeche, y ~95, pitch ~35 se aerial shot achha aata hai.
- **WorldLens players/mobs render NAHI karta** — sirf blocks. Player stationary ho tab bhi shot me nazar nahi aata. Workaround: camera ko player ke chest par aim karo (player image ke centre me aata hai), phir PIL se centre par player-size box + naam ka marker lagao (height px ≈ (H/2)/tan(fov/2) / distance per block).
- Fast aim formula: camera = player se 5 blocks south (z+5), y = feet+3, yaw 180, pitch 21.8°. Pos query ke foran baad shot lo — players (khaas kar Bedrock) tez move karte hain.
- **Gallery `design_image` (Pro editor):** max 30 layers (shapes/text/photos), min 64x64, Premium par 100 edits/day. Pixel-art textures ke liye unfit — sirf icons/posters ke liye. `get_download_link` se file curl karke repo me lao.
- **Minecraft references:** `misode/mcmeta` tags (`26.2-assets`, `26.2-summary`...) git clone se milte hain (api.github.com 403 hai, git/raw chalta hai). Bedrock: `Mojang/bedrock-samples` sparse `resource_pack`. 26.2 = resource pack format 88; pack.mcmeta me `min_format`/`max_format`.
- Bedrock textures me ~3000 PBR maps (`_mers`, `_normal`, `_heightmap` + `.texture_set.json`) hote hain — inhein redesign mat karo.
- Tinted textures (leaves, grass, leather armor) grey hoti hain; leather armor ka base grey hi rehna chahiye aur `_overlay` naye shape se match hona chahiye.
