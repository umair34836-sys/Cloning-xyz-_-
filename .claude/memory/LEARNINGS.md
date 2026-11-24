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
- **FCMC manager session (2026-11-01)**: Connector naam badal sakte hain mid-session. `mcp__FCMC__*` = FCMC server (worlds HUB/HUB_nether/HUB_the_end); `mcp__worldlens__*` = FireCraft SMP (world/world_nether). Har bar `worldlens_status` me worlds dekh kar confirm karo. Manager techshotgaming kabhi "Claude"/"Jarvis" se shuru karta hai — rule sirf "server" prefix; unclear ho to owner se poocho.

## FCMC/HUB learnings (2026-11-02)
- Cross-world copy: `clone from minecraft:<src> x1 y1 z1 x2 y2 z2 to minecraft:<dst> x y z` (raise gamerule max_block_modifications first, forceload both areas; `execute if blocks` is hard-capped 32768).
- ZNPCsPlus v2: `npc create` console se nahi chalta (player chahiye). Actions: `npc action add consolecommand <id> ANY_CLICK <cooldown> <delayTicks> <cmd>`; player name ke liye `%player_name%` (PlaceholderAPI) use karo.
- HUB me naye `minecraft:mannequin` summon hote hi gayab (koi plugin/Skript hata raha hai).
- Chorus plant rule: jis block ke side me chorus_plant ho, uske upar ya neeche air chahiye — branches sirf stalk ke top se.
- Multiverse-Portals: `mvp create` in-game (WorldEdit selection); console se `mvp info/remove/modify <p> action e:world:x,y,z`.
- Cross-dimension safe surface tp: `execute as <p> in <dim> run spreadplayers x z 0 1 false @s`.
- Don't use `execute as <player> run say` for testing (broadcasts as that player).
- Offline browser tool: ExcelJS/JSZip npm se le kar HTML me inline karo; CSP connect-src 'none' se internet block. ExcelJS empty creator ko 'Unknown' likhta hai → JSZip se core.xml patch karo. Bash heredoc me lambi JS likhne se safety check atak sakta hai → Write tool se file likho.
- JS: btoa(String.fromCharCode(...bigArray)) bare data par 'Maximum call stack' deta hai → 8KB chunks me convert karo. Locale sort me '~' letters se PEHLE aata hai → blanks last ke liye custom comparator.
- ExcelJS kuch xlsx (openpyxl tables) load karte waqt crash: "reading 'name'" → JSZip + DOMParser ka chhota values-only reader zyada robust. Import column-guess: 'Contact ID(s)', 'Phone Available?', 'S.No', '-' values ko ignore karo warna ghalat match/duplicates.
- pdf.js offline: pdf.worker.min.js ko pehle <script> me daalo → globalThis.pdfjsWorker → main-thread fake worker (CSP me koi worker/fetch nahi). PDF columns: text cells ko left edge, numbers ko right edge se header column me rakho; header column boundary cross ho to cells merge mat karo; footer/terms lines items me na jodo.
- Standalone HTML: CSS display:grid/flex wale element par [hidden] kaam nahi karta → [hidden]{display:none!important}. type=search ka apna × hota hai → ::-webkit-search-cancel-button hide. Hamesha screenshot dekho, tests ye nahi pakadte.
- Security: backup/import data ko whitelist-rebuild karo (cleanData), CSP me 'unsafe-inline' ki jagah build-time sha256 hashes (inline onerror block), CSV me =,+,-,@ ke aage ', ZIP naam safeName. Hamesha khud red-team test (t_attack) chalao — 4 asal bugs mile.
- Circular smart-fix: fixed rules (digits=part) numeric models (2D, 86, 1300 CC) aur numeric codes tor dete hain. Behtar: confident rows (code+model+part bhare) se per-column profile (shape, tokens, prefix, numeric length, part family '12210-'), cell ko best-fit column me; sahi files par 'changed 0' zaroor test karo.
- Live tests: autofocus setTimeout race (focus chheen lena) asli bug tha — stress loop (20-40 baar open→fill→save) se pakda. Playwright timing flakes ko 'flaky' keh kar mat chhoro, reproduce karo. Bash heredoc me lambi JS → Write tool se file.
- Excel freeze panes: xSplit bara ho (11 columns) to zoom/chhoti screen par baaki columns tak pahunch nahi hoti. Sirf 1-2 key columns (≤ ~40 char width) freeze karo; test me saved .xlsx ka <pane xSplit> check karo.
- Auto-fix banane ke baad asli data par DRY-RUN (sirf local, save nahi) karke dekho kya badlega — isi se "0304-657694 → 030-4657694" wala ghalat fix pakra gaya.
- Template me data placeholders single-pass regex se bharo (__TITLE__/__PAYLOAD__/__ADS__), warna data me placeholder likha ho to page toot sakta hai. Contacts template me init() TDZ bug tha (bina password page khaali) — har web page ka no-password version bhi test karo.
- Calendar-shape Excel (Sat..Fri dabbe): tareekh box ki position se nikalo (likhe numbers ka mode offset) — ek typo poora mahina nahi khiskata; spelling ke liye KPK city alias + lev<=1. Asli user file sirf local dry-run, repo/source zip me nahi.
- Test checkers that treat any '#...' cell as an Excel error: never use a bare '#' as a column heading — use 'No.'.
- Tailscale free plan = non-commercial only (tailscale.com/pricing); business Standard = $8/user/month. Domain + Cloudflare Tunnel ~$10/yr is cheapest. Android needs HTTPS for GPS + install. Node 22.5+ has built-in node:sqlite (no install). PWA must open offline with the last logged-in user, else the offline queue never syncs.
- Cloudflare quick tunnels: random URL each restart, no uptime guarantee — not for an installed app. Node fetch ignores a custom Host header; use node:http to test reverse-proxy behaviour. CGNAT (WAN IP 100.64-100.127 or differs from public IP) blocks port forwarding.
