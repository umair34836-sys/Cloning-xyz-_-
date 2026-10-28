# Instructions Log

Yahan user ki di hui saari instructions store hoti hain (jab bhi koi naya kaam ya hidayat milti hai, usay yahan likha jata hai). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — instruction (short, clear)

---

## 2026-10-28

- Agent Reach se research: Pakistan me reh kar INR wallet me receive/hold/transfer karne wala app hai? → Result: koi legal app nahi (Indian wallets ko Indian KYC chahiye, UPI intl numbers me Pakistan nahi, NRO/NRE ke liye Pakistani nationals ko RBI approval, Wise/Payoneer INR support nahi, May 2025 se India-Pak curbs). Legal raasta: freelance platform (Upwork/Fiverr) + Payoneer/bank. Crypto/hawala risky — warn kiya.
- Sawal: Pakistan me doosre mulk ki SIM se PayPal chalana real hai ya fake? → Research (Agent Reach): PayPal Pakistan me officially nahi (Sep 2026 tak); foreign SIM/address se account banana user agreement ke khilaf, account limit + 180 din fund hold. Legal: Xoom (PayPal ki service) se bahar wale Pakistan bhej sakte hain; freelance ke liye Payoneer/platform.

## 2026-10-25

- Sawal: Kya Minecraft plugin bana sakte hain jo server ke andar ki jagah ki picture/image console command se nikale (bina server join kiye), taake Claude world ko visually dekh sake? → Jawab diya: haan possible hai (BlueMap/Chunky render, custom Paper raycast plugin, ya Mineflayer bot + prismarine-viewer). Abhi sirf sawal tha, build ka faisla user karega.
- User ne custom plugin (console command se picture) ka idea/spec maanga taake build karwa sake → `docs/worldlens-plugin-idea.md` likha (commands, render modes, async renderer, HTTP API, MCP, moderation, phases).

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
