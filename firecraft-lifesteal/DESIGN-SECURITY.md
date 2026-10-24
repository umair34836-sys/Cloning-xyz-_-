# FireSteal — Design & Security (FireCraft SMP)

> Ek **original** LifeSteal plugin, scratch se likha gaya FireCraft SMP ke liye.
> Kisi plugin ko decompile/copy **nahi** kiya gaya — ye mechanics (hearts, steal,
> withdraw, eliminate, revive) LifeSteal genre me standard hain; implementation poori
> original hai. Neeche wo common weak-points jo lifesteal plugins me aate hain, aur
> FireSteal me unka strong solution.

## Common weak points → FireSteal me strong

| # | Common weakness (lifesteal plugins) | FireSteal ka solution |
|---|--------------------------------------|------------------------|
| 1 | **Heart item dupe** (drop+pickup, shift-click, crash, /dupe) | Heart items par **PersistentDataContainer (PDC) me server-secret + unique UUID token**. Consume par token ko store me **once-only redeem** check (used-token set). Forged/duplicated item redeem nahi hota. |
| 2 | **Forged heart items** (creative/other plugin se banaya item) | Item valid tabhi jab PDC me **sahi secret signature** ho (HMAC-style key jo sirf server ke paas). Bina signature ke heart item bekaar. |
| 3 | **Race condition** (do simultaneous kills / double event) | Saari heart mutations **main thread** par + per-player **lock**; death event me **idempotency guard** (ek death ek hi bar process). |
| 4 | **SQL injection** (player name/args direct query me) | **Sirf PreparedStatement (parameterized)** — kahin string-concat nahi. UUID-keyed. |
| 5 | **Data loss on crash** (flatfile, no flush) | **SQLite** (ACID) + async writes + **save-on-quit** + periodic flush + in-memory cache. |
| 6 | **Kill farming** (alt account bar bar maar kar hearts) | Config: **same victim→killer cooldown**, optional **same-IP block**, aur per-steal daily cap. |
| 7 | **Permission bypass** (admin commands bina check) | Har command/action par explicit **permission check**; defaults op-only. |
| 8 | **Client crash / insane values** (maxHealth bohot bara set) | Hearts **clamp** (min/max config), maxHealth attribute safely set; invalid config par safe defaults. |
| 9 | **Wrong kill attribution** (projectile/pet/tnt) | Last-damage + **projectile shooter resolve**; player-vs-player hi steal count. |
| 10 | **Revive abuse** | Revive sirf permission/revive-item se; eliminated state DB me, revive par atomic reset + log. |
| 11 | **Thread-unsafe Bukkit API calls from async** | DB async, lekin **Bukkit API calls sirf main thread** (scheduler se marshal). |

## Mechanics (config-driven)
- Start hearts, min (elimination), max (cap).
- PvP kill: victim -1 heart, killer +1 (cap tak). Natural death: config loss.
- Eliminate on min: ban / spectator / event (config).
- `/firesteal` admin: sethearts, give, revive, eliminate, reload (+ aliases).
- `withdraw`: ek heart → signed item; item use → +1 heart (once-only).
- PlaceholderAPI (soft): `%firesteal_hearts%`.

## Storage
SQLite (`firesteal.db`) — table `players(uuid PRIMARY KEY, hearts INT, eliminated INT, last_killed_by TEXT, updated_at BIGINT)` + `used_tokens(token PRIMARY KEY, redeemed_at BIGINT)`. Sab prepared statements, async executor, graceful close.
