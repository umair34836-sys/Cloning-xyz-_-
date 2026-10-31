# Instructions Log

Yahan user ki di hui saari instructions store hoti hain (jab bhi koi naya kaam ya hidayat milti hai, usay yahan likha jata hai). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — instruction (short, clear)

---

## 2026-10-31

- **FireCraft MC Discord server plan** diya (roles: Owner/Manager(sirf Techshot)/Admin/Moderator/Helper/YouTuber/Booster/Linked/Member/Bots; categories INFORMATION, COMMUNITY, IN-GAME, SUPPORT, CREATORS, STAFF, VOICE; Community ON, verification Medium, AutoMod ON, notifications only @mentions, welcome screen). Faisla: **user khud categories + roles + voice + server settings banayega**, phir Claude FCMC se text channels + permissions banayega. Update: user ne kaha "sab kuch FCMC se banao" → FCMC OPEN mode me tha; Claude ne 9 roles, 22 text channels, permissions, verification Medium, system/rules channel, Owner role (Mrcopper804) + Bots role (bot) set kar diye. Baqi manual: 5 categories, voice channels, emoji names, Community/AutoMod/notifications/welcome screen, Manager→Techshot (abhi server me nahi).
- **Community ON (2026-10-31, user ne khud kiya):** Rules = #📜┃rules, Safety = Discord ka #moderator-only → Claude ne rename `🚨┃mod-alerts` (1566053657793929307), STAFF category me, @everyone view deny, Admin/Mod/Helper view allow. Suggest kiya: Community Updates channel bhi #🚨┃mod-alerts karo (abhi #📰┃updates public hai).
- **Landing (2026-10-31):** naye members #👋┃welcome me land karein, general me nahi. System/join channel → #welcome (1566048619029864469); welcome = INFORMATION position 0, INFORMATION = category 0. Discord invite usi channel me land karata hai jahan se bana — isliye invites sirf #welcome se.
- **Cracked + whitelist off (2026-10-31):** user ne online-mode=false aur whitelist off kehna. Check: server PEHLE se offline-mode tha (maintenance UUIDs = OfflinePlayer v3 UUIDs exact match) aur `whitelist off` → "already turned off". Kuch badalna nahi pada.
- **Maintenance (2026-10-31):** MC server maintenance ON kiya (`maintenance on`, Maintenance plugin). Maintenance whitelist: owner MrCopper804, manager techshotgaming. Band karna ho to `maintenance off`.
- **Invite rule (2026-10-31):** players sirf #👋┃welcome se invite link bana sakein, kisi aur channel se nahi. Kiya: @everyone + Member/Linked/YouTuber/Helper/Moderator/Admin roles se create_invite hataya; #welcome par @everyone allow create_invite. Naye roles/channels banao to create_invite mat do.
- **Decorate (2026-10-31):** style = categories `─── 📢 NAME ───`, text `emoji┃name`, voice `🔊┃General` etc; brand colour #FF7A1A for all embeds; Java IP `join.firecraft.fun` (SRV → paid45.skilloraclouds.com:20050, port likhne ki zaroorat nahi), Bedrock `join.firecraft.fun` port 20072; slowmode general 3s / trading 10s / suggestions 30s; role colours Manager #A855F7, Admin #EF4444, Mod #3B82F6, Helper #22C55E, YouTuber #FF0000 (staff+YT hoist), Linked #14B8A6, Member #9CA3AF; pinned embeds in rules/server-ip/link-account/creator-info/general. Owner + bot role kabhi touch nahi. DiscordSRV console channel kabhi nahi.
- **Retry (WorldLens 1.9.0, OPEN):** user ne poora Discord plan dobara banane ko kaha. Rules: #general ko COMMUNITY me move+rename (dusra na banao), DiscordSRV console channel KABHI na banao, @everyone ping nahi, verification Medium, rules channel = #rules, end me sab ids + exact errors list karo. Done 2026-10-31.
- User ne FireCraft MC logo (fire + grass block, 512x512) Discord server icon lagane ko kaha → FCMC me server icon ka tool NAHI hai; user ko manual tareeqa bataya.
- Session ka naam **"fcmc owner"** rakho, phir FCMC connector se saari details lekar summary do (sirf read-only, kuch change nahi).

## 2026-10-30

- **POSTPONED (2026-10-30, user ne kaha):** FireCraft economy plugin idea abhi hold par hai. Jab tak user dobara na kahe, is par kaam na karo.
- FireCraft SMP economy (planning only, live server par kuch change nahi): user chahta hai **ek hi custom plugin** jisme poora economy ecosystem ho (wallet, jobs, shop, auction, stalls, plot rent, bounty, bail, tax, daily cap, lottery, perks). Vault/VaultUnlocked provider bhi ho taake doosre plugins kaam karein. Bedrock (Geyser/Floodgate) support zaroori.

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
