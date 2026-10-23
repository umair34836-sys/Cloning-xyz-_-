# APK Analysis — Ludo game (`game.jthzsrlgl.zegzwtdlly`)

> Static reverse-engineering (REA-style: unzip + DEX/native/JS inspection).
> Source: user-provided APK (~41 MB), app version **3.3.6**.

## TL;DR
Ye ek **online/multiplayer Ludo** (dice board) game hai, jo **Cocos Creator 3.7.x** engine
par **JavaScript/TypeScript** me bana hai (**oops-framework** use karke). Saari game-logic
JS bundles me hai; native `libcocos.so` sirf engine hai. App heavily **monetized + tracked**
hai: multiple ad networks + 2 attribution SDKs + Firebase push + Google Play Games cloud-save.
Backend se baat **encrypted + compressed + protobuf** protocol par hoti hai.

## App ka DNA
| Cheez | Value |
|------|-------|
| Package | `game.jthzsrlgl.zegzwtdlly` (randomized naam → reskin/portfolio style) |
| Engine | Cocos Creator **3.7.x** (`libcocos.so`, 21 MB) |
| Language | JS/TS (SystemJS bundles), framework: **oops-framework** |
| Main Activity | `com.cocos.game.AppActivity` |
| Launch scene | `webMain.scene` |
| Orientation | Landscape (design res 1334×750) |
| ABIs | arm64-v8a, armeabi-v7a |
| Genre (confirmed) | **Ludo** (`Game/Ludo/README` asset) |

## Size kahan gaya (41 MB)
- `assets/` **89 MB** (uncompressed) → game art/audio/scenes (UUID-named bundles). Audio `audio/en/` (localized voice).
- `lib/` 35 MB → `libcocos.so` (engine).
- `classes.dex` 2.6 MB → Java glue + SDKs (game logic yahan nahi).

## Boot flow (kaise chalta hai)
1. `AppActivity` → `libcocos.so` engine load.
2. `assets/main.js` → SystemJS warmup → `src/application.js`.
3. `Application.init()` → Cocos engine init (`src/settings.json`), splash/progress.
4. `cc.game.run()` → `webMain.scene` launch → preload `resources` + `main` bundles.
5. Kuch bundles **remote** hain (`remoteBundles`: `icons_100040…`, `icons_300020…`) → server se on-demand download (live update / seasonal content).

## Networking (client ↔ backend)
Game bundle me ye libs pack hain:
- **protobuf.js** (`reader/writer/rpc/service/roots`) → messages protobuf-encoded.
- **crypto-js** (AES, SHA, MD5, HMAC, RC4) → payload **encryption**.
- **pako** (`oops-framework/libs/network/protocol/pako.min.js`) → zlib **compression**.

→ Matlab: real-time multiplayer ke liye encrypted + compressed + protobuf wire protocol.
(Static strings me koi plain backend host nahi mila — runtime par set/decrypt hota hoga.)

## SDKs / third-party (classes.dex + JS)
| Category | SDK |
|----------|-----|
| Attribution/analytics | **AppsFlyer** (bohot heavy), **Adjust** (`app.adjust.com`) |
| Ads (mediation) | AppsFlyer ad-revenue tracking for **AdMob, AppLovin MAX, IronSource, AdMost** |
| Push | **Firebase Cloud Messaging** (`MyFirebaseMessagingService`) |
| Cloud save / login | **Google Play Games Services** (scopes: `appstate`, `drive.appdata`), Google Ads ID (GAID) |
| Re-engagement | `AlarmReceiver` + exact alarms → local notifications |
| Install referrer | Google Play + **Huawei AppGallery** referrer |
| Social (queries) | Facebook, Instagram (share/deep-link) |

## Permissions (manifest)
`INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `POST_NOTIFICATIONS`,
`SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `AD_ID`,
`ACCESS_ADSERVICES_ATTRIBUTION`, `BIND_GET_INSTALL_REFERRER_SERVICE`,
Huawei `GET_COMMON_DATA`. `usesCleartextTraffic=true`.

> Koi high-risk permission (SMS, contacts, location, camera) nahi — ye standard
> ad-monetized casual game profile hai. Risk points: cleartext traffic allowed,
> aggressive attribution/ad-tracking, exact-alarm re-engagement.

## Verdict
Ek commercially-packaged **online Ludo** game — Cocos Creator + oops-framework par,
ads + attribution se monetized, encrypted protobuf backend ke saath. Behaviour "malicious"
nahi lagta; typical casual-game SDK stack hai.

## Deeper analysis chahiye to
- `jadx` se `classes.dex` → readable Java (SDK init / keys dekhne ke liye).
- JS bundles beautify karke `oops-framework` game logic trace karna.
- Runtime/Frida (REA ka `rea-agents` MCP) se encrypted backend host + protocol capture.
