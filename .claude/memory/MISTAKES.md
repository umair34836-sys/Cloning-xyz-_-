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

## 2026-10-29 — LP default group par false set karna
- Ghalti: `firecraft.owner false` default group par lagaya → owner group default se inherit karta hai, to mrcopper804 (owner) bhi false ho gaya.
- Fix: owner group par explicit `firecraft.owner true` set kiya.
- Sabaq: LP me default group par koi deny lagane se pehle check karo kaun kaun us se inherit karta hai; change ke BAAD owner + normal + staff teeno users par `permission check` se verify karo (verify ne hi ye pakda).

## 2026-10-30 — Console `msg` me "@" likhna
- Ghalti: `msg MrCopper804 ... @everyone ...` → Minecraft ne `@e` ko entity selector samjha aur saare entities ke naam message me bhar diye (garbled whisper).
- Sabaq: console `msg`/`say`/`tellraw` text me KABHI "@" mat likho (@e/@a/@p/@s/@r selectors ban jaate hain). "everyone-ping" jaisa likho.

## 2026-10-30 — Lamba in-game msg
- Ghalti: ~250+ char wala `msg` "Unhandled exception" de gaya (chat limit 256).
- Sabaq: in-game `msg` 200 characters se chhota rakho; lambi detail #owner-private me do.

## 2026-10-30 — `fill ... outline` on a 1-block-high layer
- **Ghalti:** prison ke liye `fill 589 70 47 611 70 69 polished_blackstone_bricks outline` aur `... 76 ... deepslate_brick_wall outline` chalaya → 1-high layer me outline = poora area fill (529 blocks). Owner andar khada tha (y70) → kuch second block me phansa; turant `fill ... air` se interior clear kiya.
- **Bachao:** 1-block-high ring ke liye kabhi `outline/hollow` mat use karo — 4 alag side fills karo. Building se pehle player ki position check karo aur unke block (feet/head) ko kabhi fill range me na aane do; pehle unhe bahar tp/warn karo ya area exclude karo.

## 2026-10-30 — Scope se zyada replace kiya
- **Ghalti:** owner ne "lamp pillars replace karo" kaha; maine road-end pillars ke saath plaza ke 4 corner lamp posts bhi replace kar diye — owner ne revert karwaya.
- **Bachao:** ambiguous scope me sirf wahi badlo jo clearly kaha gaya (yahan: same type ke pillars). Extra items ke liye pehle in-game short sawal.
