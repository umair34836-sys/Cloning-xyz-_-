# Instructions Log

Yahan user ki di hui saari instructions store hoti hain (jab bhi koi naya kaam ya hidayat milti hai, usay yahan likha jata hai). Newest sabse upar.

Format:
- **[YYYY-MM-DD]** — instruction (short, clear)

---

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

- 2026-11-02: "Dc per dm kerdo manager ko or bata do k subah 7am ko claude tumhare lie public server me on hoga and 5pm ko off hoga — aisa hi kro ab se, new rule." → DM bheja, cron 7am–5pm PKT kar diya, RULES.md me add.

- 2026-11-27: New session opened from the UMAIR WORKDESK (v3.0.0) main session to start a NEW tool for the same owner. Ask owner first what the tool is (purpose, users, data/files, output) — don't build before he explains. Same working style as WORKDESK: reply in clear English, offline single-file HTML, GUI-editable, free tools, private business data never committed (only memory notes), fake data in tests, deliver as zip (tool + START_HERE.txt + private source zip), build → full tests → check screenshots/PDF → deliver. No AI/authorship mention inside exported business files. If WORKDESK code is to be reused, ask owner to upload `UMAIR_WORKDESK_SOURCE_PRIVATE_v3.0.0.zip`.
- 2026-11-27: Owner: "organizer tool banana hai same WORKDESK ki trah, lekin us me apna local model (AI) connect karna hai jo mere PC par bina internet chalega — full ready-to-use tool bana kar do." → Asking: local runtime (Ollama/LM Studio/llama.cpp), PC specs, reuse WORKDESK source zip or new, what the AI should do.
- 2026-11-27 (answers): Build NEW from scratch (not on WORKDESK code). Model = gpt-oss-20b (~11 GB) downloaded directly; owner does NOT want Ollama / LM Studio (worried they need internet); model already runs on his PC. AI jobs: recognise files, chat with data, clean/organise (suggest only, no invented data), summaries, PLUS a command chat area that can make the tool do anything he asks (agent with tool-calling on app actions).
- 2026-11-27: Model not on this PC yet ("tool bana do, dekhte hain phir"). OS = Windows 10/11. Owner: copy the WORKDESK source and build the new tool from that COPY — never modify the original WORKDESK source. → Need owner to upload UMAIR_WORKDESK_SOURCE_PRIVATE_v3.0.0.zip (not in this container). AI connection must be configurable (URL/port/model name in Settings) + Windows START_AI.bat for llama.cpp.
- 2026-11-27: DELIVERED UMAIR ORGANIZER AI v1.0.0 (copy of WORKDESK 3.0.0 + 🤖 AI Assistant). Kit: Organizer_AI.html + START_AI.bat + AI_Engine (llama.cpp Windows CPU build) + START_HERE.txt; private source zip separate. 788/788 tests; real gpt-oss-20b tested end-to-end on Linux llama-server. Windows exe not run-tested (Wine broken in container) — only dependency-checked. Pending: owner tries on his PC; NVIDIA GPU build if wanted.
- 2026-11-27: Owner: "Is tool ko SQL par bhi bana do — file import me bohat time lagta hai aur page unresponsive ho jata hai. Small kaam par nahi, HARD/heavy kaam par bhi test karo." → Plan: measure first with big fake files, then SQLite (WASM) storage + import off the main thread / in chunks + paged tables; stress-test with heavy data.
- 2026-11-27: DELIVERED UMAIR ORGANIZER AI v1.1.0 — SQL (SQLite/sql.js in a worker, file in IndexedDB per company) + big-file speed fixes. Heavy-tested with fake data up to ~200k records; 809/809 tests. HEAVY_TEST_REPORT.txt in the zip.
