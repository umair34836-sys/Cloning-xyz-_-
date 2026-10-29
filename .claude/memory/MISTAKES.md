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

## 2026-10-28

- **Ghalti:** Pehli dafa player images me player nazar nahi aaya, aur shot se pehle aim calculate karne me der hui to player hil gaya. **Bachao:** WorldLens entities nahi dikhata, is liye pehle hi marker approach use karo aur user ko bata do. Position lete hi foran shot lo (simple fixed offset, script ka wait nahi).
- **Ghalti:** Texture pack me pehle "reference ke 2x2 layout + apna noise" wala tareeqa chalaya — user ko kharab aur copy jaisa laga. **Bachao:** "khud banao / copyright safe" ka matlab hai **original design** (naam se family pehchano, sirf palette lo). Pehle 10-20 textures ka preview user ko dikhao, phir poora pack banao.
- **Ghalti:** `destroy_stage_*` naam me "stage" tha to plant/crop generator chal gaya. **Bachao:** keyword-matching me khaas cases (destroy, overlay, PBR) pehle handle karo, phir random sample sheet dekh kar verify karo.
- **Ghalti:** `git submodule add` se GitHub skills seedha repo me daalne ki koshish — auto-mode ne "Untrusted Code Integration" kehkar roka. **Bachao:** pehle scratchpad me sirf padhne ke liye clone karo; repo me install ke liye user se saaf ijazat lo.
- **Ghalti:** Metal albedo clamp (≥110) ne netherite (purple-grey) ko gulaabi bana diya. **Bachao:** clamp ke baad showcase dekh kar rang check karo; dark metals ke liye neutral grey base do.
- **Ghalti:** Leather armor ka base `pal.main` (gehra grey ~54) rakha — dye multiply hone par item kaala dikhta. **Bachao:** tinted textures (leather, grass, leaves) ka base halka/neutral grey (~200) rakho.
- **Ghalti:** Fallback "inspired" items (boats, buckets, sherds...) gol dhabbe ban gaye aur random sample me pakde nahi gaye. **Bachao:** har category ki POORI contact sheet dekho; aam families ke liye apna vector template banao.
- **Ghalti:** Naam me keyword dhoondhne se galat family: `campfire_log`/`firefly_bush` ("fire") aag ban gaye, `music_disc_lava_chicken` ("chicken") drumstick ban gaya, Bedrock `egg_bat` (spawn egg) khane ka anda ban gaya, Bedrock `bundle_red` me rang nahi mila. **Bachao:** har keyword rule ke saath exclusions likho, aur Java + Bedrock dono naamon par contact sheet dekho.
- **Ghalti:** Preview sheet me bina `block/` prefix ke naam diye to sab textures purane 16x jaise dikhe aur laga ke naye designs kaam nahi kar rahe. **Bachao:** preview script ko hamesha `textures/` root + `block/…`, `item/…`, `entity/…` path do.
- **Ghalti:** `ghast_shooting` NO_FACE list me tha, is liye naya chehra lagta hi nahi tha. **Bachao:** chehra na aaye to pehle NO_FACE / face_style lists check karo.
- **Ghalti:** jack_o_lantern ka rang `pal.main` se liya — ref ka chehra roshan peela hai to poora kaddu peela ho gaya. **Bachao:** jin textures me roshni/overlay rang hawi ho, unka base rang fixed do.

