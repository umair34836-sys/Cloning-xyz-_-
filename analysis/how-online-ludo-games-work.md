# Online Ludo / Casual Games — Ye Andar Se Kaise Kaam Karte Hain (Educational)

> Concept-level guide. Is APK ki analysis se jo pattern mila (Cocos Creator 3.7 +
> oops-framework + protobuf/AES backend + ads/attribution SDKs) usi ko base bana kar
> likha gaya — kisi backend ko todne ke liye nahi, **samajhne** ke liye.

---

## 1. Bade level par: Client vs Server

Online real-money/coin game ke do hisse hote hain:

```
 [ APK = CLIENT ]                         [ SERVER = BACKEND ]
 - UI, animations, board, dice            - Game rules ka asli faisla
 - Input (aap ka move)       <--net-->    - RNG (dice/outcome) decide
 - Server ka natija dikhana               - Coins/wallet, matchmaking
 - Ads, sounds, store screens             - Anti-cheat, validation
```

**Sabse ahem baat:** jeet/haar ka faisla **server** karta hai, client nahi. Client
sirf "display terminal" hai — jo server kehta hai wahi dikhata hai.

---

## 2. Game Engine layer (jo is app me mila)

- **Cocos Creator 3.7.x** — JS/TypeScript game engine. `libcocos.so` native engine,
  game ki logic JS bundles (`.jsc`) me.
- **oops-framework** — ek open-source Cocos framework jo code ko modules me baantta hai
  (ECS + MVC style): scenes, UI prefabs, game managers, network layer.
- Content **asset bundles** me hota hai (art, audio, prefabs), aur kuch bundles **remote**
  se download hote hain (live update — app update ke bina naye icons/levels aa jate hain).

> Isi liye size ka 89 MB hissa `assets/` me tha: board, pieces, sounds, localized voice.

---

## 3. Client ↔ Server networking (is app me jo libs mili)

- **protobuf** — messages ko compact binary me encode karta hai (JSON se chhota/tez).
- **AES (crypto-js)** — payload ko encrypt karta hai taake raaste me koi padh/badal na sake.
- **pako (zlib)** — bade messages ko compress karta hai.
- Transport aksar **WebSocket** (real-time, do-tarfa) hota hai turn-based multiplayer ke liye.

Flow: `move/action` → protobuf encode → AES encrypt → (gzip) → server. Server validate
karke → RNG/rules apply → encrypted result wapas → client decode karke animation chalata hai.

> Isi encryption ki wajah se static analysis me plaintext backend URL/outcome nahi milta —
> ye jaan-boojh kar hai (privacy + anti-cheat).

---

## 4. Server-Authoritative RNG — "prediction" kyun mumkin nahi

Fair online games me dice/outcome aise bante hain:

1. Aap "roll" dabate hain → client server ko sirf *request* bhejta hai.
2. **Server** apne secure RNG (CSPRNG) se number nikalta hai — aksar ek **server seed**
   (secret) + round id se.
3. Server result client ko bhejta hai; client sirf **dikhata** hai.

Matlab:
- **Dice ka number client ke andar generate hi nahi hota** → APK decrypt karke bhi aap
  ko aisa koi formula/seed nahi milega jisse agla natija pata chale.
- Agar koi "provably fair" scheme ho (server seed hash pehle, reveal baad me), tab bhi
  aap pehle se predict nahi kar sakte — sirf baad me verify kar sakte hain ke cheating
  nahi hui.
- "Next game kaun jeetega" batane wale apps/tools **100% scam** hote hain — unke paas
  server ka secret seed ho hi nahi sakta.

> Short: jeet ki information client-side **hoti hi nahi**, is liye nikaalne ko kuch nahi.
> Sirf wahi game client-side predict ho sakta hai jo *galat* bana ho (offline/trusting
> client) — aur usay exploit karna cheating/abuse hai.

---

## 5. Matchmaking & Rooms

- Aap "play" dabate hain → server aap ko ek **room** (table) me daalta hai, 2/4 players.
- Har room ka ek state machine server par chalta hai (turn order, timers, moves).
- Client sirf current turn + board state receive/render karta hai.
- Bots: agar asli players na milein to server aksar **bot** daal deta hai (aap ko pata nahi chalta).

---

## 6. Coins / Economy & Monetization (is app me mila)

- **Wallet server par** hota hai (coins/gems) — client sirf number dikhata hai. Client me
  coins "badalna" bekaar hai, server re-check karta hai.
- **Ads mediation**: AdMob + AppLovin MAX + IronSource + AdMost — ek saath, best-paying ad
  dikhane ke liye. Rewarded ads (ad dekho → coins), interstitials (match ke beech).
- **Attribution**: AppsFlyer + Adjust — ye batate hain ke aap kis ad/campaign se aaye
  (install referrer), taake marketing ka ROI pata chale + ad-revenue track ho.
- **Push/re-engagement**: Firebase Messaging + local alarms (notifications) — "wapas aao,
  bonus milega" type reminders.

---

## 7. Data/Analytics — ye apps generally kya collect karte hain

(Privacy awareness ke liye — ye standard casual-game stack hai:)

- Device identifiers (GAID/ad-id), install source, rough usage/events.
- In-game events (matches, purchases, ad views) analytics ke liye.
- Crash reports (Firebase Crashlytics).
- **Is app me koi high-risk permission nahi thi** (SMS/contacts/location/camera nahi) —
  sirf internet, notifications, ad-id, install-referrer.

---

## 8. Anti-cheat basics (server ye isliye karta hai)

- Har move server validate karta hai (legal move? sahi turn? time?).
- Outcome server decide karta hai → client-side memory editing bekaar.
- Rate-limit + anomaly detection (bohot zyada jeet = flag).
- Encrypted + signed messages → man-in-the-middle/replay se bachao.

---

## 9. Agar aap apna aisa game banana chahein (legit)

Ek fair online Ludo khud banane ka stack:

| Layer | Option |
|-------|--------|
| Client/engine | Cocos Creator 3.x (ya Unity) + framework (oops-framework) |
| Realtime server | Node.js + WebSocket (Socket.IO / `ws`) ya Colyseus (game-room framework), ya Go |
| Protocol | protobuf + TLS (encryption transport par) |
| RNG | Server-side CSPRNG; chaho to "provably fair" (seed hash + reveal) |
| State/DB | Redis (live rooms) + Postgres/Mongo (accounts, wallet, history) |
| Economy | Wallet/transactions **sirf server** par, idempotent |
| Monetization | AdMob/AppLovin MAX mediation, IAP (Play Billing) |
| Analytics | Firebase + AppsFlyer/Adjust |
| Anti-cheat | Server-authoritative sab kuch + validation + rate limits |

> Isme asli mehnat **server** me hai (rules + RNG + wallet + anti-cheat), client to
> "skin" hai. Ye sahi tareeqa hai — aur ye bhi samjhata hai ke kisi aur ke server se
> jeetne ka koi client-side shortcut hota hi nahi.

---

## Khulasa

- Jeet/haar **server** decide karta hai → client decrypt karke prediction **namumkin**.
- Client = display + input; asli game server par.
- Ye app ek typical **ad-monetized, server-authoritative online Ludo** hai.
- Apna banana ho to upar wala stack — wahan aap owner hain, sab kuch jayaz.
