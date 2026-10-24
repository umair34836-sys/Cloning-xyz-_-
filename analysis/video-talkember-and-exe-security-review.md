# Security Review — video.talkember.site + AI Video Automator (.exe)

> Owner-authorized, non-destructive. Date: 2026-10-24. Web: static SPA + `/admin` on
> LiteSpeed. Desktop: **AI Video Automator Setup 1.1.1** (~620 MB), NSIS/electron-builder
> installer → Electron app + bundled Node server + Python/ffmpeg + cloudflared tunnel.

## Verdict
Ye software **security ke lihaaz se behtareen bana hua hai** — zyादातar modern defenses
pehle se maujood hain (neeche "GOOD"). Jo items hain wo mostly **distribution + transport +
ek gateway-verify** ke hain. Sabse bara (HIGH) item: **installer code-signed nahi**.

---

## HIGH

### H1. Installer code-signed NAHI hai
`AI-Video-Automator-Setup-1.1.1.exe` par **koi Authenticode signature nahi** (PE security
directory khaali).
- **Asar**: Windows SmartScreen/AV warnings; user confirm nahi kar sakta ke file asli/
  untampered hai; koi beech me installer ko **trojanize** karke redistribute kar sakta hai.
  Impact zyada hai kyunki ye app user ke **Chrome/Google (YouTube) session** ko automate
  karti hai — compromised build = account takeover.
- **Fix**: EV/OV code-signing certificate se `signtool` ke zariye exe + uninstaller sign
  karein (electron-builder me `win.signtoolOptions`/`certificateFile`). Auto-update ke liye
  **signature verification** on rakhein.

---

## MEDIUM

### M1. Access token URL query (`?token=...`) me
LAN URL / QR code aur `tokenOk()` dono `?token=` accept karte hain
(`url.searchParams.get('token')`). URL me token **server logs, browser history, aur
Referer header** se leak hota hai.
- **Fix**: token sirf `x-app-token` header / cookie se lein; query-string support hata dein
  (ya ek-baar use ke baad invalidate karein).

### M2. LAN par cleartext HTTP
Server LAN par `http://<ip>:7860` (plain HTTP) deta hai (`0.0.0.0` bind). Public/shared
wifi par koi token/session sniff kar sakta hai. (Tunnel path Cloudflare HTTPS use karta
hai — accha.)
- **Fix**: LAN ke liye bhi self-signed/local TLS ya "sirf tunnel (HTTPS)" mode; kam az kam
  docs me warn karein ke untrusted wifi par LAN mode na use karein.

### M3. Gateway `register.php` ki authentication verify karein
Desktop tunnel enable hone par app `POST video.talkember.site/register.php` ko `{url, key}`
bhejta hai, aur gateway visitors ko us tunnel par forward karta hai.
- **Risk**: agar `register.php` **koi bhi `key`** qubool karke mapping overwrite kar de, to
  attacker `video.talkember.site` ko apne tunnel par point karke users ko **phish/hijack**
  kar sakta hai.
- **Fix (server-side, verify)**: sirf owner ka known `gatewayKey` accept ho (constant-time
  compare), unknown keys reject; rate-limit; mapping change par alert/log. Maine isay live
  test nahi kiya (aap ki gateway disrupt na ho) — aap confirm karein.

### M4. `/admin/login` — CSRF token nazar nahi aaya
Admin login form me `csrf`/`_token` field nahi mila (POST par 400 without body). Session
cookie flags login ke baad verify karein.
- **Fix**: admin forms par CSRF token + cookie `SameSite=Strict/Lax` + `HttpOnly` + `Secure`.
  Strong admin password + ideally 2FA. Login par rate-limit confirm karein.

---

## LOW / Hardening

- **L1. Brute-force IP-based (8/15min)** — accha hai, lekin IP `X-Forwarded-For` se aata ho
  to spoofable; distributed attack bypass kar sakti hai. Global throttle + failed-login
  alert add karein; attempt-IP ke liye trusted proxy chain hi use karein.
- **L2. Bundled supply chain** — app Python (numpy/opencv), `@openai/codex`, playwright,
  ffmpeg bundle karti hai. Deps pinned rakhein, regularly update karein (known-CVE scan:
  `npm audit`, `pip-audit`).
- **L3. robots.txt** `/admin`, `/order/`, `/api/` reveal karta hai (order/api abhi 404).
  Security sensitive paths robots se hide karna "security" nahi deta — bas unhe properly
  auth-protect rakhein.

---

## GOOD — ye already theek hai (aise hi rakhein)
**Web (video.talkember.site):**
- Strong **CSP** (`default-src 'self'`, `object-src 'none'`, `frame-ancestors 'none'`), **HSTS**,
  `X-Frame-Options: DENY`, nosniff, referrer-policy, permissions-policy.
- Directory listing off; `.git`/`.env`/`.htaccess` blocked (403); not WordPress; no `X-Powered-By`.

**Desktop app / Node server (bahut acha engineering):**
- **Electron**: har window `contextIsolation: true` + `sandbox: true`, koi `nodeIntegration`
  nahi, UI `loadFile` (local), external links sirf `shell.openExternal` (https). `webSecurity`
  disable nahi.
- **DNS-rebinding defense**: `Host` header server se match zaroori + `Origin` (jab ho) match;
  loopback ko akele trust nahi karta ("any program/web page on PC can reach 127.0.0.1");
  forwarding header aaye to request ko remote maan kar login maangta hai.
- **Auth**: password **scrypt + 16-byte salt**, `timingSafeEqual`, 32-byte session tokens,
  cookie `HttpOnly; SameSite=Lax; Secure(on https)`.
- **No embedded API keys / OAuth secrets** — design hi user ke apne logged-in browser session
  par chalta hai (`package.json`: "No API keys"). Ye bada plus hai.

---

## Jo yahan se test NAHI kar saka (aap karein)
- **TLS config** (proxy ne raw TLS block kiya) → [SSL Labs](https://www.ssllabs.com/ssltest/)
  par `video.talkember.site` (A+ target).
- **`register.php`** gateway auth (M3) — live gateway disrupt na ho, is liye aap verify karein.
- **Authenticated admin testing** (CSRF/session/IDOR on /admin) — real creds maangta hai.
- Maine `.exe` **run nahi kiya** — sirf static analysis (NSIS extract → config.js, auth.js,
  server.js, tunnel.js, Electron main.cjs padhe).

## Priority order
1. **H1** — installer code-signing + signed auto-update.
2. **M3** — gateway `register.php` key verification (gateway-hijack se bachao).
3. **M1 + M2** — token header-only + LAN transport.
4. **M4** — admin CSRF/2FA/rate-limit.
5. **L1–L3** — brute-force hardening, dep updates.
