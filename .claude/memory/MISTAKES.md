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
- **2026-10-31**: Pehle FCMC ki tool list ko final maan kar user ko manual guide di; baad me OPEN mode me zyada tools aa gaye. Bachao: connector ki capabilities par faisla dene se pehle `discord_status` + ToolSearch dobara check karo.
- **2026-11-01**: Bina pooche `mail clear MrCopper804` chala diya (owner ka mailbox saaf ho gaya). Bachao: sirf wahi commands chalao jo kaam ke liye zaroori hon; doosre player ka data clear/delete bina pooche nahi.
- **2026-11-01**: Bade box me `replace dirt`/`replace sand` chala diya jo doosre areas ki zameen bhi badal gaya (dirt→sand 7741 underground, Area4 beach sand→nylium). Bachao: replace-fill ka box sirf us area ke andar rakho; underground dirt (grass ke neeche) ko kabhi broad replace mat karo. Respawn anchor overworld me explode karta hai — decor me nahi.
- **2026-11-01**: Testing ke liye `execute as MrCopper804 run say check` chala diya → owner ke naam se public chat me "check" chala gaya. Bachao: kabhi kisi player ke naam se `say`/chat commands mat chalao; check ke liye `tag list` / `scoreboard players list` / `data get` jaise silent commands use karo.
- **2026-11-01**: Command-block chain ko player online hote hue tukdon me lagaya (beech ka block x=10 1000-char error se miss) → owner ka welcome state atak gaya (fc_new tag reh gaya). Bachao: chain poori lagao aur test karo PEHLE, ya repeating block sabse aakhir me lagao (pehle chain blocks, phir repeating).
- **2026-11-01**: Logo printer me `scoreboard players add #x fc_l -23` (negative add invalid) → logo 23 block east me banna shuru ho gaya; rok ke fill air + data reload kiya. Bachao: command-block me daalne se pehle har command console me ek baar run karke validate karo.
- **2026-11-01**: Command lock `* false world=HUB` ko kaafi samjha, verify nahi kiya → default group ke explicit essentials nodes (tpr/home/tpa...) HUB me chal rahe the. Bachao: permission change ke baad `lp ... permission check <node>` se asli nodes verify karo.

## 2026-11-02 — `op list` ne "list" player ko OP bana diya
- Kya hua: OP players dekhne ke liye console me `op list` chalaya. Vanilla me ops list command nahi hai; `op <name>` ne "list" naam ko OP de diya.
- Fix: foran `deop list`.
- Bachao: `op`/`deop`/`ban`/`whitelist add` jaisi commands kabhi "list/check" ke liye mat chalao. Ops dekhne ke liye panel me ops.json dekho ya owner se poocho.
