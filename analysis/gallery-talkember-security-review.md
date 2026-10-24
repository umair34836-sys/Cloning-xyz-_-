# Security Review — gallery.talkember.site (Future Gallery)

> Owner-authorized, **non-destructive** external assessment (safe recon only — koi
> DoS/brute-force/data-damage nahi). Date: 2026-10-24. Stack: React SPA + custom API
> (`/api/v1`) on **LiteSpeed**. App version (leaked): **0.7.0**.

## Overall
Bunyadi hifazat achhi hai: WordPress nahi, `.git`/`.env`/`.htaccess`/backups exposed nahi,
TRACE off, CORS reflect nahi karta, verbose errors nahi, homepage me koi leaked key nahi,
`X-Powered-By` nahi, aur **saare 22 API endpoints auth maangte hain (401)**. Neeche jo
HIGH items hain wo zyadातar *verify-karo* type hain (live user-data ko chhuye baghair main
inhe safely confirm nahi kar sakta) — lekin ek gallery app me yahi sabse bade risk hote hain.

---

## HIGH — inhe foran verify/fix karein

### H1. IDOR — photo ownership check (#1 gallery risk)
Endpoints: `GET /api/v1/photos/{id}`, `/{id}/file`, `/{id}/thumb`, `/{id}/details`.
Auth to lagi hai, lekin sawal: kya server check karta hai ke photo **{id} isi logged-in
user ki hai**? Agar nahi, to koi bhi logged-in user doosre ki `id` daal kar uski private
photo/file khol sakta hai (Insecure Direct Object Reference).
- **Risk**: doosre users ki private photos leak.
- **Safe self-test**: 2 test accounts banayein. Account A se apni photo ka `id` lein.
  Account B ke token se `GET /api/v1/photos/{A_ka_id}/file` call karein → **403/404 aana
  chahiye**. Agar file aa gayi = IDOR, foran fix.
- **Fix**: har photo/album query me `WHERE owner_id = current_user` lagayein (server-side),
  sirf `id` par bharosa na karein. Sequential integer id ki jagah unguessable UUID behtar.

### H2. SSRF — `POST /api/v1/photos/from-url`
Ye endpoint server se ek user-diya **URL fetch** karwata hai → classic **SSRF** surface.
Note: unauthenticated POST par **400** mila (baaki jagah 401) — lagta hai request **auth
se pehle** body/validation tak pohonchti hai. SSRF-prone endpoint par auth sabse pehla
gate hona chahiye.
- **Risk**: attacker server se internal URLs fetch karwa sakta hai:
  `http://169.254.169.254/...` (cloud metadata/credentials), `http://localhost`, internal
  services, file:// — ya internal port-scan.
- **Fix**:
  1. Auth **sabse pehle** (koi bhi processing se pehle 401).
  2. Sirf `http/https` scheme; resolve ki hui IP ko check karein — **private/loopback/
     link-local ranges block** (10/8, 172.16/12, 192.168/16, 127/8, 169.254/16, ::1).
  3. Redirects follow karte waqt dobara IP check (DNS-rebinding/redirect bypass se bachao).
  4. Response size + content-type + timeout limit; sirf image types.

### H3. Temporary download link — `POST /api/v1/photos/{id}/link`
"Another person" ke liye temporary link banata hai.
- **Verify**: token **lamba + random (unguessable)** ho, **expiry** ho, sirf us ek photo
  tak scoped ho, aur revoke ho sake. Agar link me predictable id/short token hai to koi
  bhi doosron ki files guess kar sakta hai.
- **Fix**: 128-bit random token, short TTL, one-file scope, access log.

---

## MEDIUM

### M1. HSTS missing
`Strict-Transport-Security` header nahi. → SSL-strip/downgrade risk.
- **Fix** (LiteSpeed/.htaccess):
  `Header always set Strict-Transport-Security "max-age=31536000; includeSubDomains; preload"`
  Saath hi confirm karein http→https **301 redirect** lagta hai (main proxy ki wajah se
  verify nahi kar saka).

### M2. CSP missing
`Content-Security-Policy` nahi — React SPA me XSS ka sabse bara defense yahi hai.
- **Fix** (start strict, then loosen as needed):
  `Content-Security-Policy: default-src 'self'; img-src 'self' data: blob:; script-src 'self'; style-src 'self' 'unsafe-inline'; object-src 'none'; frame-ancestors 'self'; base-uri 'self'`

### M3. Version / state disclosure — `/api/health`
`{"ok":true,"version":"0.7.0","editing":true}` publicly. Version attacker ko known-bug
target karne deta hai; `editing` internal flag leak.
- **Fix**: health ko internal/monitoring tak mehdood karein, ya sirf `{"ok":true}` do.

### M4. Public OpenAPI spec maps full attack surface
`/api/openapi.json` (22 endpoints) + `/api/v1` + `/developers` sab public. Agar dev-API
public-by-design hai to theek, warna poora map dena attacker ki recon aasaan karta hai.
- **Option**: spec ko auth ya dev-portal ke peeche rakhein.

### M5. Rate limiting (verify)
Main brute-force test nahi karunga, lekin confirm karein:
- Sign-in / token endpoints par **rate limit + lockout** (credential stuffing se bachao).
- Per-key API throttle (aap ne 5000/day daily cap mention ki thi — accha; sign-in par
  bhi per-IP limit hona chahiye).

---

## GOOD (ye already theek hai — aise hi rakhein)
- Not WordPress; `.git`/`.env`/`.htaccess`/`wp-config.bak` exposed nahi (403/404).
- `X-Content-Type-Options: nosniff`, `X-Frame-Options: SAMEORIGIN`,
  `Referrer-Policy: strict-origin-when-cross-origin`, `Permissions-Policy` set.
- TRACE disabled (501). CORS arbitrary origin reflect nahi karta.
- Malformed JSON par verbose stack-trace nahi. Homepage me koi API key/secret nahi.
- `X-Powered-By`/LiteSpeed **version** hidden. Sab sensitive endpoints **401** (auth enforced).

---

## Jo main yahan se test NAHI kar saka (aap bahar se karein)
- **TLS config** (protocols/ciphers/cert chain): proxy ne raw TLS probe block kiya.
  → [SSL Labs](https://www.ssllabs.com/ssltest/) par `gallery.talkember.site` scan karein
  (A/A+ target: TLS1.2+1.3 only, no weak ciphers, HSTS).
- **http→https redirect** (http proxy se reachable nahi tha) — khud confirm karein.
- **Authenticated** testing (IDOR/session cookie flags/CSRF) — ye real accounts maangta
  hai; live user-data ko chhue baghair main nahi karta. H1/H3 ke self-tests upar diye hain.

## Priority order
1. **H2 (SSRF on from-url)** — auth ordering + internal-IP block. (Sabse khatarnak.)
2. **H1 (IDOR)** — ownership checks + UUID ids.
3. **H3** — link token hardening.
4. **M1/M2** — HSTS + CSP headers.
5. **M3/M4/M5** — version hide, spec gating, rate limits.

---

## RE-TEST (2026-10-24) — fixes verify

| Pehla finding | Ab status |
|---------------|-----------|
| M1 HSTS missing | ✅ **FIXED** — `Strict-Transport-Security: max-age=31536000; includeSubDomains` |
| M2 CSP missing | ✅ **FIXED** — strong CSP (`object-src 'none'`, script-src me `unsafe-inline` nahi, hash-based, `frame-ancestors 'self'`, `base-uri 'self'`) |
| M3 /api/health version leak | ✅ **FIXED** — ab sirf `{"ok":true}` |
| H2 SSRF auth-ordering (from-url 400 unauth) | ✅ **FIXED** — ab unauth GET/POST dono **401** (auth sabse pehle; metadata-URL probe process se pehle block) |

Naye safe checks (sab clean):
- CORS arbitrary origin reflect nahi karta; TRACE 501 (off); bad method 400.
- Har malformed/injection/path-traversal probe (`limit=abc'`, giant id, `..%2f..%2fetc%2fpasswd`) **auth-first 401** — processing se pehle hi ruk jata hai.
- OpenAPI: global security `key`, **koi explicit no-auth endpoint nahi** (22/22 auth).

### Abhi bhi (external se test nahi ho sakta — authenticated chahiye)
- **H1 IDOR** (`/photos/{id}`/`/file`) aur **H3 temp-link token strength** — ye **real/test account** ke baghair safely verify nahi ho sakte (live user-data). 2-account self-test report me upar diya hai.
- **M4 openapi/developers public** (200) — agar dev-API by-design hai to theek.

**Natija:** external attack surface ab **solid** hai. Baaki sirf authenticated-logic (IDOR/link) reh gaya.
