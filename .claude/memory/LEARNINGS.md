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
- **FCMC connector (2026-10-31)** = WorldLens 1.7.0 plugin (Paper MC 26.2) + DiscordSRV bot "FireCraft MC". Read-only snapshot: TPS 20, mspt 0.11, 0/20 players, worlds world/nether/end. Discord server "FireCraft MC" sirf 2 members, 1 channel (#general), sirf bot role; tool groups on = read/messages/channels (moderation/role tools off). Protected: OWNER role, Mrcopper804/MrzCopper804. Session rename: `get_session` (id omit) → `set_session_title`.
- WorldLens top-down/shot sirf loaded chunks dikhata hai — jab koi player online na ho to spawn bhi unloaded (dark grey) aata hai.
- **FCMC Discord tool limits**: `discord_create_channel` sirf TEXT channel banata hai aur sirf EXISTING category me (naam galat/missing → error). Categories, roles, voice channels, server settings (Community/AutoMod/verification) nahi bana sakta. `discord_channel_permissions` staff/admin/bot/protected roles ko target nahi kar sakta → staff channels ke liye category-level permissions + sync use karo.
- **FCMC OPEN mode (2026-10-31)**: ab role_create/edit/delete, channel_edit/delete, discord_server (verification, system/rules channel), purge, dm, nickname bhi hain. Tool list har session/turn me badal sakti hai → kaam se pehle `discord_status` dobara check karo. Phir bhi: category create, voice channel create, Community/AutoMod/notifications/welcome screen NAHI. Text channel names me emoji/`┃` strip ho jate hain (create + rename dono); category names me emoji chal jata hai. Roles top-first banao (naya role neeche lagta hai).
- FCMC `discord_server` sirf verification/system/rules channel badalta hai — server icon/banner/name ka koi tool nahi; `discord_send_message` image sirf WorldLens shots ki leta hai (bahar ki image upload nahi).
- **WorldLens 1.9.0 FCMC**: `discord_create_channel` server-side `type` = text/voice/category accept karta hai (client schema purana dikhe tab bhi `type` bhejo — kaam karta hai). Category/voice names me emoji + space chal jate hain; TEXT channel names se emoji/`┃` ab bhi strip hote hain. `discord_channel_permissions` sirf TEXT channels par chalta hai (voice/category → "no text channel with id"). Owner ka top role (👑 Owner) hard guard 2 ki wajah se move/edit nahi hota.
- FireCraft Discord IDs: categories INFORMATION (purani "Text Channels" category, id unknown), COMMUNITY 1566040283244658849, IN-GAME 1566040350064123904, SUPPORT 1566040355009466498, CREATORS 1566040360113938510, STAFF 1566040365025202219, VOICE 1566040371429908581.
- Discord embed messages ke ids (pinned): rules 1566042308154237020, server-ip 1566042685452853271, link-account 1566042347123515456, creator-info 1566042364445728920, general 1566042381214683222. Text channel emoji names WorldLens 1.9.0 me bhi strip (silent, no error).
- **WorldLens 1.9.1 (2026-10-31)**: text channel emoji┃naam ab save hote hain (jawab 'Renamed text X to Y' aata hai; 1.9.0 me sirf 'Changed text' aur strip hota tha). Saare 22 text channels renamed; #👋┃welcome (1566048619029864469) INFORMATION me position 0. Note: discord_channels list position order nahi, positions-bucket order deti hai.
- `🤖 Bots` role (bot ke paas) ab hard guard 3 me aata hai — edit refuse hota hai. @everyone role `discord_role_edit` se edit ho sakta hai (id = server id).
- FireCraft IGNs: Owner = MrCopper804 (website role player, Discord Mrcopper804), Manager/Techshot = techshotgaming (website role staff, abhisheksaini26aug email). Server offline-mode UUIDs (v3) + AuthMe; LuckPerms me sirf `default` group. 44 plugins incl. Maintenance, Geyser/Floodgate, DiscordSRV, GrimAC, GriefPrevention.
- online-mode check bina server.properties ke: player UUID ko `md5("OfflinePlayer:"+name)` v3 UUID se compare karo — match = cracked/offline mode. online-mode console command se nahi badalta (server.properties + restart chahiye).
- Multiverse 5.8.2: `mv delete X` → `mv confirm <code>` (30s). Default world (`world`) delete nahi hoti, koi error bhi nahi aata — `mv list` se verify karo. Spawn: `mv config first-spawn-location/join-destination <world>`.
- WorldEdit 7.4.6 console: `/world` = 'Only in-game players' → console se WE nahi chalta; vanilla `execute in minecraft:overworld run fill/place feature/setworldspawn/worldborder` use karo (fill limit 32768/command; pehle forceload, baad me `forceload remove all`). MV5 apna spawn alag rakhta hai: `mv setspawn World:x,y,z,yaw,pitch`. WG console: `rg flag -w <world> __global__ <flag> <value>`.
