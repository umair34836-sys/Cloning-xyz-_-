# Instructions Log

Yahan user ki di hui saari instructions store hoti hain (jab bhi koi naya kaam ya hidayat milti hai, usay yahan likha jata hai). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — instruction (short, clear)

---

## 2026-10-31

- **FireCraft MC Discord server plan** diya (roles: Owner/Manager(sirf Techshot)/Admin/Moderator/Helper/YouTuber/Booster/Linked/Member/Bots; categories INFORMATION, COMMUNITY, IN-GAME, SUPPORT, CREATORS, STAFF, VOICE; Community ON, verification Medium, AutoMod ON, notifications only @mentions, welcome screen). Faisla: **user khud categories + roles + voice + server settings banayega**, phir Claude FCMC se text channels + permissions banayega. Update: user ne kaha "sab kuch FCMC se banao" → FCMC OPEN mode me tha; Claude ne 9 roles, 22 text channels, permissions, verification Medium, system/rules channel, Owner role (Mrcopper804) + Bots role (bot) set kar diye. Baqi manual: 5 categories, voice channels, emoji names, Community/AutoMod/notifications/welcome screen, Manager→Techshot (abhi server me nahi).
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
