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

- 2026-11-20: Private business data (phone numbers, photos) PUBLIC repo me push kar diya bina visibility check kiye. Bachao: push se pehle `list_repos` se visibility dekho; private data repo me nahi.
- 2026-11-20: Excel me text cell "=" se shuru kiya to formula ban gaya (#VALUE!); header '#' bhi confusing. Text '=' se shuru na karo.
- Phone fixer ne adhoora mobile (10 digit "0304-...") ko landline samajh kar format kar diya tha. Bachao: sirf wahi number badlo jo normalize hone ke baad Mobile/Landline bane; adhoora/unclear user ke liye chhodo. Self-test me case add hai.
- Calendar sheet (row me >=5 din ke naam) Import me kabhi contacts na bane — pehle November sheet se bogus dukanein ban rahi thin.
- Import preview must not change lists/DB (it ran recordsOf → added list values → lost recheck mark). Forms must always show a record's current multi values even if not in the list (data loss bug).

## 2026-11-25 — Visit-report detector caught a contacts sheet
- Galti: header detector (≥5 matching columns) ne "Lucas Visits" (S.No, City, Area, Visit Date, Remarks + Shop Name) ko visit report samjha → contacts test fail.
- Bachao: naye file-type detector me (1) sirf us type ke KHAAS columns ko "strong" maano (mechanics/khiradi/order/complaint/suggestion), aam columns (date/city/area/remarks) ko nahi; (2) shop-name column ho to contacts hi rahe; (3) hamesha poora test suite chalao.
- Also: `$$` wale python heredoc ko safety check parse nahi kar saka → script file (Write) se chalao.
