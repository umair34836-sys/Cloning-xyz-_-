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

- 2026-11-20: Company kaam ki files me kabhi mention na ho ke kisne banaya (AI/human) → RULES.md me rule.
- 2026-11-21: Offline data-entry tool maanga (format + manual entry + organized final file, data safe/offline) → single HTML tool diya (CSP se internet block, local storage, AES backup, Excel/ZIP export). Business data repo me nahi daala.
- 2026-11-21: Tool me location hierarchy add karwai (Country>Province>District>City/Tehsil>Area/Village>Market/Street): linked dropdowns, Locations tab, bulk set, Excel by location. Karachi district andaze se set nahi kiya.
- 2026-11-21: Tool me 'Import Files' section maanga (PC ka purana kaam upload → samjhe → organize → ZIP). Banaya: xlsx/csv/txt/paste parse, column auto-detect (Roman Urdu), dedupe/merge, preview, photos/PDF ZIP me sorted. Photos/PDF offline parhe nahi ja sakte — user ko bataya.
- 2026-11-21: Tool me alag 'Circulars' page (brand price lists): xlsx/csv/PDF upload, brand/make/date auto, price search + history, price changes, manual entry, Circulars Excel. pdf.js inline (fake worker).
- 2026-11-21: Circular me brand missing → fix (no-space naam, naya brand file-naam se guess, per-row Brand column, Edit, brand yaad). 'Search website' export banaya (kohinoor.firecraft.fun Price Finder jaisa, fuzzy search, password AES). Site khud deploy nahi kiya.
- 2026-11-21: Tool ka full user + hacker test maanga; manual entry 6 saal ke bache jaisa aasaan; custom/adhoora address samjhe; ZIP tarteeb. → Security fixes (XSS, CSV injection, zip-slip, proto, CSP hashes), Quick Add form, parseAddress, ZIP 00-06 layout. 156/156 tests.
- 2026-11-21: Circular me model/comp.code ghalat column (Part No) me → tool me khud-test system: auto-check → fix → re-test → final result; 'Test & fix all' saved; Settings self-test (18 checks). 173/173 tests.
- 2026-11-21: Model/code number se bhi shuru hote hain → 'lakeer ka fakeer' nahi, smart test+fix: har circular ki sahi rows se column profile seekh kar cells rakhna (move/split/swap). 179/179, self-test 21/21.
- 2026-11-23: Tool ko future-ready production project banana (ek HTML par depend na ho), naam KOHINOOR TRADERS, 'Made by Muhammad Umair', ads; baqi sab waisa hi. → project split (byte-identical build verify), brand/credit/ads module, anonymized fixtures, 193 tests.
- 2026-11-23: Live click-by-click user + hacker testing → 261/261. Bugs found+fixed: English browser validation → Roman Urdu; autofocus race could lose fast-typed text; contacts slow with 5000 rows. Final: KOHINOOR_TRADERS_v1.0.0.zip + source zip.
- 2026-11-23 (v1.1.0): (a) Excel me columns fixed/freeze the → chhoti screen par baaki columns nahi milte → fix. (b) Contacts ka search web page (circular jaisa). (c) Field Data Manager (contacts) ke liye bhi circular jaisa smart test + fix + self-test. (d) User ne poocha import me kya "skipped" hota hai → code se wajah batai; "phir bhi import karo" option offer kiya (pending answer).
- 2026-11-23 (v1.2.0): Skipped rows bhi main file me daalo, issue likh kar (thora data bhi miss na ho). → Excel 'Skipped Rows' sheet + ZIP CSV + backup; contacts list saaf rehti hai.
- 2026-11-23 (v1.3.0): Web pages (price + contacts finder) 24 ghante baad khud destroy + nayi file ka tareeqa; web pages me ads (Better Ads Standards ke mutabiq jagah, har jagah alag ad). User ne self-study ke sawal bhi pooche (time/area lock) — wo features abhi nahi chahiye.
