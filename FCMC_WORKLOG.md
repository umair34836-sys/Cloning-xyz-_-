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

### 2026-11-01 ~05:10–05:25 UTC — [OWNER] HUB extras 1-4 (hologram, welcome, selector, scoreboard)
- Kisne kaha: MrCopper804 — "server 1 se le ker 4 tak sub kerdo one by one"
- Kaise / kya:
  1. **Welcome hologram**: 2 `text_display` (tag `hub_welcome`) at (0.5,155.7,-6.5) title "✦ FIRECRAFT MC ✦" scale 2.6 + (0.5,153.7,-6.5) info (Welcome, paths → lobby, 6 lobbies, rules, www.firecraft.fun) scale 1.1, billboard center.
  2. **Join welcome**: vanilla command blocks (command blocks ON hai) underground y=120, chain z=3 x3..15 (repeating at 3,120,3). Objectives `fc_leave` (leave_game), `fc_wait`. Join/first-join ke 100 ticks baad title + subtitle + levelup sound + chat welcome; first join par `@a` broadcast; tags `fc_new`/`fc_seen`.
  3. **Lobby Selector**: chain z=6 x3..27 (repeating at 3,120,6). HUB me survival/adventure players ko hotbar.4 me carrot_on_a_stick (item_model compass, custom_data fc_sel). Right-click (`fc_sel` used stat) → chat menu, clickable `/trigger fc_lobby set 1..6` → NPC ke samne tp + "Coming Soon". LP: `lp group default permission set minecraft.command.trigger true world=HUB`.
  4. **Scoreboard**: vanilla objective `fc_side` sidebar (numberformat blank, fake entries #l1..#l9). **TAB**: `tab group _DEFAULT_|default header/footer ...` (console output nahi deta → owner se verify).
- Forceload: chunks [0,0] aur [1,0] permanently forceloaded (command blocks ke liye) — hatao mat.
- Limits: worldlens_command max 1000 chars/command; plugin config files (DeluxeMenus GUI, Skript) FCMC se nahi likh sakte.
- Result: done; owner online aaya, mail + tellraw report.
- Fix (05:30): owner join ke waqt chain block x=10 missing tha → `fc_new` atka; `tag MrCopper804 remove fc_new` → welcome chala, ab `fc_seen`. Galti se `execute as MrCopper804 run say check` bhi chal gaya (chat me "check").

### 2026-11-01 ~05:15–05:25 UTC — [OWNER] FireCraft MC logo pixel-art (HUB sky)
- Kisne kaha: owner (Claude chat me image di) — "isko lobby hubs per as a logo build kernaa hai right places per ok apne andaze se kahi per bhi maat laga dena" → poocha: jagah = **spawn ke saamne sky me**, size = **48x48**.
- Kya kiya: logo image → 48x48 pixel art (18 blocks: fire = nether_wart/redstone/terracotta/orange/honeycomb/yellow/gold, grass = lime/green wool+concrete, dirt = brown, MC = quartz/light_gray_wool, outline = black_terracotta), background transparent. Vertical plane **z=-48, x -23..24, y 158..205**, spawn se north me saamne dikhta hai (Survival area ke upar).
- Kaise: "storage printer" — 1518 pixels packed ints (`x*4096+(55-y)*64+id`) 11 `data modify storage fc:logo q append` commands; command-block chain y=118 z=9 x0..32 (repeating at 0,118,9) + marker `fc_cur` har tick ek block lagata hai. Bug: `scoreboard players add -23` invalid → pehli koshish 23 block east me; rok ke `fill 0 185 -48 47 205 -48 air`, `remove 23` fix, data reload.
- Cleanup (build ke baad): printer command blocks + marker hatao, forceload chunks [-2..2,-3] + [2,0] hatao (welcome/selector wale [0,0],[1,0] RAKHO).
- Result (05:23): logo complete (1518 blocks), stray 3 blocks (23..24,158..159) hataye. Printer cleanup done: command blocks y118 z9 → stone, marker kill, `fc_l` objective remove, forceload logo chunks + [2,0] remove. Spawn se seedha upar-saamne dikhta hai; right-bottom par Survival ka ek oak tree thoda overlap karta hai. Owner ko tellraw.

### 2026-11-01 ~05:30 UTC — [OWNER] HUB kami check (verified, andaza nahi)
- Kisne kaha: MrCopper804 — "server now ab check kiaa hub me kis kis jizz ki kammi hai ? apne andaze se maat bolna kuch bhi"
- Kaise check kiya: `mv info HUB --page 1..4`, `lp group default permission info` + `permission check essentials.tpr`, hologram count (2), `gamerule minecraft:show_advancement_messages`, owner gamemode.
- **Bug fix:** default group me exact nodes (essentials.tpr/home/sethome/tpa/spawn/warp/kit/pay/msg/mail..., clickaccept.*, playerpoints.*) global TRUE the → LP exact node wildcard `* false world=hub` ko override karta hai → RTP waghera HUB me chal rahe the. 36 nodes `lp group default permission set <node> false world=hub` (global true rehne diye, future lobbies me chalenge). authme.player.* aur minecraft.command.trigger untouched.
- Baqi kami owner ko batayi (faisla pending): HUB gamemode SURVIVAL (adventure?), advancement messages ON, hologram me Discord link nahi, TAB header/footer verify nahi, maintenance ON, logo pe tree overlap.

### 2026-11-01 ~05:40 UTC — [OWNER] HUB adventure + advancements off
- Kisne kaha: MrCopper804 — "server 1 adventure kerdo 2 advancement off kerdo 3 discord link baad me kringay"
- Kaise: `mv modify HUB set gamemode adventure` (verify: Game Mode ADVENTURE; owner/manager `*` se bypass); `execute in minecraft:overworld run gamerule minecraft:show_advancement_messages false`; `mv modify HUB set allow-advancement-grant false` (verify: Grant Advancements false).
- Pending: Discord link hologram me — owner baad me batayega. Baqi list (TAB verify, maintenance, logo tree overlap) owner ne abhi nahi kaha.
