# Instructions Log

Yahan user ki di hui saari instructions store hoti hain (jab bhi koi naya kaam ya hidayat milti hai, usay yahan likha jata hai). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — instruction (short, clear)

---

## 2026-11-02

- **Staff activity schedule:** worldlens (FireCraft SMP) + FCMC (FireCraft MC) dono ke liye har roz **12 AM aur 12 PM (PKT)** check: staff/manager ne pichle 12 ghante me kitne ghante khela + kya kaam kiya (CoreProtect `co lookup`, hPlaytime, website `playerSessions`, Discord staff/support channels, tickets/applications/reports). Real report firecraft website MCP me likho → `staffActivityReports/<YYYY-MM-DD>_<AM|PM>` + `staff/<uid>` par sirf `lastActivityReport` / `lastActivityCheckedAt` merge. Scheduled task id: `trig_01YAnJoeRXpKhqni8vy4zNhN` (fresh session har fire, push notification on). Sab console/Discord calls READ-ONLY.

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
