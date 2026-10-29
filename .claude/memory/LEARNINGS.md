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
- Discord tools (WorldLens) image/attachment bhej ya padh nahi sakte — owner ki image messages text "" dikhti hain.
- Base photos repo me commit nahi ki (reports/worldlens/bases/ gitignored) — privacy.
- Kick reason console log me hota hai; WorldLens command output me nahi aata. AuthMe checks: `authme accounts <name|ip>`, `authme lastlogin`, `authme getip`. LifeStealZ: `lifestealz hearts get <p>`. firecraft MCP: get_user(ign) se website account/application.
