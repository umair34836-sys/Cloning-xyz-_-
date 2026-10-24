# Instructions Log

Yahan user ki di hui saari instructions store hoti hain (jab bhi koi naya kaam ya hidayat milti hai, usay yahan likha jata hai). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — instruction (short, clear)

---

## 2026-10-24

- LifeStealZ kaam **pause** — pehle "jev" tool install karo. User ne clarify kiya: "Jev AI" (TypeSafe AI, trending). → `tools/jev-skills`, `tools/jev-sdk-python`, `tools/jev-sdk-js` submodules. LifeStealZ modification baad me resume karna hai (user batayega kya badalna hai).

- LifeStealZ ka source code mile to batao — user usay **modify** karna chahta hai. → Upstream tag `2.21.1` `projects/lifestealz/` me rakha.

- `lifestealz-2.21.1.jar` (Minecraft plugin) decompile karo — **rea tool use karna hai** (user ne mid-task kaha). Output: `analysis/lifestealz-2.21.1/`.
- `vineflower/` folder hatao — decompile output me **sirf rea** ka result rakho.

- Same 7 repos ko `tools/` me git submodules (shallow, `--depth 1`) ke tor par install karne ka dobara kaha: agent-reach, rea, ecc, whisper, agency-agents, uiux-pro-max (`ui-ux-pro-max-skill`), graft. Pehle se registered the → verify kiye + `.gitmodules` me `shallow = true` add kiya.

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
