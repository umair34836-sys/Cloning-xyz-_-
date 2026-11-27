# Mistakes

Jo bhi ghalti ho yahan record karo taake dobara na ho. Har entry me: kya hua + aage se kya karna hai.

Format:
- **[YYYY-MM-DD]** — ghalti → bachne ka tareeqa.

---

## 2026-10-23

- Reminder: tool names kabhi blindly guess karke install na karo agar repo clearly match na ho — flag karo.
- **"Rea" ki ghalti** → pehle `zhaoxuya520/reverse-skill` (43k) pick ki thi. Asal me "REA" = **Reverse Engineer Anything** = `morluto/rea` (~371k stars). Sabaq: jab name ek acronym/known tool ho (REA), to usay `stars:>40000` ke saath exact name se search karo, generic keyword se nahi. Ab replace kar di.
- **GraftV resolved** → `NanoNets/Graft` (user ne URL diya). Installed `tools/graft`. (Pehle search se nahi mili thi kyunki "graftv skill" query kuch return nahi karti; asal repo ka naam sirf "Graft" hai.)
- **UI/UX Pro Max resolved** → `nextlevelbuilder/ui-ux-pro-max-skill` (user-confirmed). Pehli best-guess (`prakharbhanu88-star/...`) replace kar di.
- Jab classifier ya tool kisi bare JSON/base64 heavy output par interrupt kare, raw payload ko dubara print karne ki koshish na karo — dosri strategy (fields, chota perPage, grep) use karo.

## 2026-10-24 — boundary (plugin clone)
- User ne baar baar kaha: LifeStealZ (kisi aur ka published plugin) ko REA se decompile karke "uski files use karo / clone banao". Ye **copyright infringement** hai — decline kiya (chahe tool REA ho). Issue tool nahi, ownership hai: LifeStealZ user ka apna nahi (author: KartoffelChipss/ZetaPlugins).
- Legit raasta jo diya: **original** lifesteal plugin (FireSteal) scratch se banaya `firecraft-lifesteal/` me — same mechanics, par original code + hardened (anti-dupe, signed heart items, prepared statements, anti-farm, Electron-style clamps). REA sirf user ke APNE / owner-authorized binary par use hoga.
- REA se decompiled 3rd-party code ko naye plugin me copy karna = NAHI (IP reminder bhi).

## 2026-11-03 — FCMC vote crate holograms: wrong set kept + neighbour stands moved
- Ghalti: Manager ne "baaki crates ke level wala" rakhne ko kaha; maine neeche wala rakha jabki wo upar wala chahte the. Phir `distance=..1.5` selector se tp kiya to pados (z -38.5) ke stray stands bhi move ho gaye.
- Bachne ka tareeqa: (1) Ambiguous ho to pehle har stand ki Pos list karke manager ko options do, phir delete karo. (2) Kill/tp ke liye exact column selector (`x,y,z,dx=0,dy=..,dz=0` ya `distance=..0.1`) use karo; pehle `execute as @e[...] run data get entity @s Pos` se dekh lo kaun match ho raha hai. (3) SlimeCrates har /crate set ya reload par naya hologram banata hai, purane nahi hatata.

## 2026-11-27 — wrong source version uploaded
- Owner uploaded WORKDESK v2.0.0 instead of v3.0.0 (v2 lacks market visit reports, Customize, Print). Caught it by reading src/config/app.json before building. → Always check the version in app.json/CHANGELOG of any uploaded source before starting; ask owner if it's not the expected one. Owner will upload v3.0.0.

## 2026-11-27 — renaming a copied tool
- Renamed output Workdesk.html → Organizer_AI.html but missed the hard-coded download name in src/personal/loader.js (locked-copy test failed). → After a rename, `grep -rn` the OLD name across src/, tests/, tools/ and build scripts, not only tests.
- New AI auto-connect polling to 127.0.0.1 tripped the "no internet requests" test → tests must allow loopback only, never widen to other hosts.

## 2026-11-27 — heavy-test lessons
- Fake-data generator used an LCG with `% n` → low bits repeat → thousands of identical phone numbers; I first thought the tool was wrongly merging rows. → Use a proper PRNG (mulberry32) and check uniqueness of generated data before blaming the tool.
- Added a global `const XT` that clashed with an existing global `XT` regex → whole app failed to start (SyntaxError at load). → grep for the name before adding any global; run a smoke test (no page errors) after every edit.
- Python text-mode open() converted CRLF→LF silently, so replacements with "\r" never matched. → open with newline="" for CRLF files and convert back explicitly.
