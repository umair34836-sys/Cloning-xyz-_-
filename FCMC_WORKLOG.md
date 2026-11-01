# FireCraft MC — Work Log (kya, kaise, kab)

Owner **MrCopper804** ki request (2026-11-01): jo bhi kaam ho, yahan likho — kya kiya, kaise kiya (commands/tools), kab kiya — taake owner session aur manager session dono ko baad me yaad rahe.

**Kaun likhta hai:**
- `fcmc owner` session (owner MrCopper804 ke kaam) → tag **[OWNER]**
- `fcmc manager` session (manager techshotgaming ke kaam) → tag **[MANAGER]**

**Kahan:** repo `umair34836-sys/Cloning-xyz-_-`, branch `claude/amazing-pascal-sbsbez`, file `FCMC_WORKLOG.md`.
Likhne se pehle: `git fetch origin claude/amazing-pascal-sbsbez && git checkout claude/amazing-pascal-sbsbez && git pull --rebase origin claude/amazing-pascal-sbsbez` → entry **neeche (newest last)** add karo → commit → `git push origin claude/amazing-pascal-sbsbez` (conflict ho to `git pull --rebase` dobara karke phir push).

**Entry format:**
```
### YYYY-MM-DD HH:MM UTC — [OWNER|MANAGER] Short title
- Kisne kaha: <IGN> — "<original message>"
- Kya kiya: ...
- Kaise: tools/commands (key commands, coordinates, plugin names)
- Result / note: done / failed (exact error) / pending
```

---

## Purana kaam (summary, owner session)

### 2026-10-31 — [OWNER] Discord server setup
- Kya kiya: 9 roles (Owner, Manager, Admin, Moderator, Helper, YouTuber, Linked, Member, Bots), categories, 22 text channels `emoji┃name` style, permissions, verification Medium, pinned info embeds (rules, server-ip, link-account, creator-info, general), slowmode, role colours, mod-alerts channel (STAFF).
- Kaise: FCMC `discord_*` tools (WorldLens 1.9.1).
- Rules: invite link sirf #👋┃welcome se; naye members #welcome me land; DiscordSRV console channel kabhi nahi; owner + bot role kabhi touch nahi.

### 2026-10-31 — [OWNER] Minecraft server basics
- Maintenance ON (`maintenance on`), maintenance whitelist: MrCopper804, techshotgaming.
- Server pehle se offline-mode (cracked) + whitelist off tha — kuch badalna nahi pada.
- Manager ko Discord DM me invite link bheja.

### 2026-10-31 — [OWNER] Worlds → HUB
- Saare purane worlds delete (Multiverse `mv delete` + `mv confirm`), main world = **HUB** (`mv setspawn HUB:0.5,151,0.5,180,0`, join-destination HUB).

### 2026-11-01 — [OWNER] HUB sky island + protection
- ~280x280 sky island (y≈101–150), spawn beacon (0,150,0) Regen II (iron pyramid andar), invisible barrier wall ±63 (y151–319).
- WorldGuard `__global__ -w HUB`: build/pvp/mob-spawning/fall-damage/item-drop/use/chest-access waghera deny, invincible allow, time-lock 6000, feed.
- Kaise: vanilla `fill`/markers (console se WorldEdit nahi chalta), `gamerule minecraft:max_block_modifications` temporarily bada karke.

### 2026-11-01 — [OWNER] Ranks
- LuckPerms `owner` (weight 1000, gradient ⚡ OWNER) → MrCopper804; `manager` (weight 900, ⚙ MANAGER) → techshotgaming.
- Default group: `* false world=HUB` + `authme.player.* true` (hub me normal players ki commands band, owner allow kare tab kholna).

### 2026-11-01 — [OWNER] 6 areas + journey ring
- Area1 SURVIVAL (plains, well, cottage, farm, pond), Area2 Ancient City, Area3 The End, Area4 Water World, Area5 Nether Valley, Area6 PVP PRACTICE (crimson).
- Glass journey ring r=56 + 8-step survival process (①JOIN … ⑧ELYTRA & END CITY), dirt_path trail r=59.

### 2026-11-01 — [OWNER] Master plan + lobby NPCs
- Plan: Join → HUB → 6 lobbies (SURVIVAL, LIFESTEAL, PARKOUR, PVP PRACTICE, MINIGAMES, EVENT). Sab abhi "Coming Soon". Hub/lobbies me no build/break/PvP, RTP + baqi commands band.
- 6 `minecraft:mannequin` NPCs (tag `lobby_npc`), "Coming Soon", centre ki taraf face. (ZNPCsPlus console se `npc create` nahi karta.)

### 2026-11-01 — [OWNER] Manager session
- `fcmc manager` session banaya — techshotgaming ke "server ..." messages har minute handle karta hai.

### 2026-11-01 ~04:50 UTC — [OWNER] HUB centre + paths redesign
- Kisne kaha: MrCopper804 — "center and paths per kaam kerte hai inko wapis se redesign kerdoo with better look"
- Kya kiya: gol plaza (dark ring, gilded ring, andesite, chiseled ring, shroomlight glow ring, dark border); 6 spokes 5-wide (dark edges, stone bricks, beech me glow line); outer ring path symmetric (dark | stone | glow | stone | dark); spoke-ring junctions khule.
- Kaise: markers + `execute at @e[tag] positioned ^ ^ ^R run setblock ...` (yaw doubling 512 markers), phir markers kill + `forceload remove all`.
- Result: done, screenshots se check kiya; owner ko tellraw report.

### 2026-11-01 ~05:05 UTC — [OWNER] Shared work-log
- Kisne kaha: MrCopper804 — "server tum joo bhi kam kerte hoo uski ek file banao ... fcmc manager wale session k sath bhi share kro and usko bolo k wo bhi likhe"
- Kya kiya: yeh file (`FCMC_WORKLOG.md`) banayi + manager session ko file ka link aur likhne ka tareeqa bheja.

---

## Naye entries (dono sessions yahan neeche add karein)

### 2026-11-01 ~05:15 UTC — [OWNER] HUB completion check
- Kisne kaha: MrCopper804 — "server ab i think soo hub kaa kaam complete hai yeah ? check kro if me kuch miss ker raha ho to bata dena ok"
- Kaise check kiya: `mv config join-destination` / `first-spawn-location` = w:HUB; `rg info -w HUB __global__` (build/pvp/use/chest/item-drop/mob-spawning/fall-damage deny, invincible, time/weather lock, feed); 6 `lobby_npc` mannequins; 14 text_display labels (6 area + 8 journey); `maintenance status` = ON.
- Result: owner ko tellraw — ready list + suggestions: (1) spawn welcome hologram (naam/Discord/rules), (2) join welcome title/message, (3) hub hotbar compass lobby selector, (4) scoreboard/TAB header-footer, (5) NPC click actions jab lobby bane, (6) maintenance launch par off. Owner ke jawab ka intezar.
