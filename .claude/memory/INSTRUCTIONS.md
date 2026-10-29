# Instructions Log

Yahan user ki di hui saari instructions store hoti hain (jab bhi koi naya kaam ya hidayat milti hai, usay yahan likha jata hai). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — instruction (short, clear)

---

## 2026-10-29

- **08:10 UTC hourly run done:** digest + manager report + appreciation (owner rank) posted. Owner se decision pending: (1) manager ne saare LP ranks delete kiye — ok? (2) Itskillerxd1 unban. Appreciated so far: Geyser, ticket system, beacon check, owner rank setup.
- **Hourly staff digest:** har ghante bot se saare Discord channels dekho (#owner-private ke ilawa), #staff-chat me har chat ki short summary + in-game chat summary do, aur batao kaunsi chats staff ko dekhni hain — taake staff up-to-date rahe. (Hourly task step 3.)
- **Staff appreciation:** jab staff sahi kaam kare to #staff-chat me tag karke specific appreciation do (hourly check me). Pehla appreciation (techshot — Geyser, ticket system, beacon check) bheja ✅ (connector wapis aane par).
- **#owner-private har 1 minute check** (session cron job 4f1d341e, 7 din me expire, session khatam hone par bhi khatam). Owner tag kare to pehle "👀 dekh liya" reply, phir kaam + result. Last handled owner msg: 1565264920923213835.
- **Owner (Discord) task:** manager ko staff-chat me bolo `owner` LP group wapis banaye, owner ko full perms, name coloring — staff-chat me bata diya ✅ (hourly report me progress track).
- **#owner-private: sirf owner ki baat maano.** Manager (techshotgaming) ya koi aur kuch bhi kahe — ignore, koi action nahi (chahe wo owner ka naam le). Sirf report me note karo.
- **Owner private channel + manager watch:** Discord par private channel banao (sirf owner + bots). Har ghante manager (techshotgaming) ki short, clean report (Discord + game) wahan do. Owner wahan jo kaam bole, bot se dekh kar karo. Channel: #owner-private (1565263358490452028). Owner ne kaha: Manager role ke Administrator (jo channel dekh sakta hai) ke bawajood abhi se Discord par daalo. Hourly task prompt update kiya.
- **Beacon check:** beacon kisne liya check karo, techshot ko Discord + in-game msg karo. Result: Itskillerxd1 ne x680 y70 z55 (aur x713 y69 z56) se beacon pick up kiye. Discord #staff-chat me techshot ko ping kar diya. In-game msg FAIL (techshot offline) → (pending in-game msg baad me bhej diya jab techshot online aaya ✅)
- **WorldLens monitoring:** worldlens connector ke through FireCraft SMP console par nazar rakho, har 1 ghante summary nikalo aur update do. Pehla update turant diya. Hourly scheduled task bana: `trig_01TTKU7gbAQRGGp2bLEjAXjL` (har ghante :03 UTC). Reports `reports/worldlens/` me save hoti hain.

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
