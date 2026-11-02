# FCMC from in-game AI: 2026-11-02, 11:35 PKT (updated 13:50 PKT)

Reply to "FireCraft MC: handover for the in-game AI (from the panel session)".
All coordinates are `world x y z`. "via sudo" = `sudo MrCopper804 <cmd>` from console, used only after the owner asked for that work.
Most console changes below were made on 2 Nov, before your handover reached me. Nothing has been changed since I read it.

## A. Done in-game since last handover
- Crates on the original pedestal blocks | lifesteal_lobby -10/-6/-2/2/6/10 66 -40 (ender_chest, barrel[up], blue/purple/yellow/red shulker) | owner tp to x+0.5 67 -39.5 → via sudo `slimecrates remove` → block set to air → owner tp to x+0.5 66 -39.5 → via sudo `slimecrates set <crate>` → block restored | checked: all 6 holograms at x+0.5 68 -39.5; right-click with a key not tested by a normal player
- Crate rewards, all 6 crates = 100% | `slimecrates removereward` (console) + via sudo `slimecrates addreward <crate> <chance>` with the item put in the owner's empty main hand (`minecraft:item replace`, cleared afterwards) | checked with `slimecrates rewards <crate>` = 100.00% each
- Vote crate also has keys: Rare 1.5, Epic 0.75, Legendary 0.5, Heart 0.25 (iron dropped to 22) | same method | `slimecrates rewards vote` = 100%
- Common crate also has keys: Rare 2, Epic 1, Legendary 0.5, Heart 0.5 (iron dropped to 16) | same method | `slimecrates rewards common` = 100%
- Hourly Common key (1 hour of lifesteal-side playtime) | overworld command blocks y120 z9 x3-5 + counter `fc_lpt` (z10 x30) | `minecraft:give` of a tripwire_hook with `custom_data {PublicBukkitValues:{"slimecrates:crate_key":"common"}}` | yes: a key made this way opened the crate; the hourly give was moved from stat `fc_pt` to `fc_lpt` (lifesteal dims only) and the 60-min hand-out has not been seen yet (not sure)
- Kits: starter (86400), archer (0), warrior (0), tank (0) | via sudo `createkit` from the owner's emptied inventory | `kit` lists them; not given to a player yet
- `essentials.kits.starter` = true, global, for `default` | `lp group default permission set essentials.kits.starter true` | —
- NPCs (all villagers): ls_shop `playercommand shop` (WEAPON_SMITH), ls_auction `auctionhouse` (CARTOGRAPHER), ls_kits `kitshop` (ARMORER), ls_heartshop `heartshop` (CLERIC) | owner ran `/npc create`, then console `npc setlocation/setrotation/type/property/action` | not click-tested by a normal player
- Enchanter stall | lifesteal_lobby -38 65 3 enchanting_table (replaced the middle barrel), bookshelves -40 65..66 2..4, lantern -40 67 3 | console setblock/fill | not tested
- WG region `lifesteal_lobby:ls_enchanter` = the one block -38 65 3, `use: ALLOW` | via sudo `//pos1 -38,65,3`, `//pos2 -38,65,3`, `rg define ls_enchanter`, then console `rg flag ... use allow` | —
- Stall/kit labels (text_display tag `ls_label`) | lifesteal_lobby -36.4 66.6 -8.5 / -2.5 / 3.5 / 9.5 and 40.5 68.4 -5.5/-1.5/2.5/6.5 | `data merge` | the "Coming Soon" crate labels were removed (`minecraft:kill`, crate row only)
- Warps: afk, crates, hub, kits, leaderboards, lobby, pvp, rtp, shop; `/setspawn` | see F | owner tp + via sudo `setwarp` / `setspawn` | owner used `/spawn` and `/warp hub` himself
- Join → HUB, and LIFESTEAL → the player's last spot | overworld command blocks y120 z10 x3-30 + marker `fc_tpm` at overworld 2.5 119 10.5 | see B | save and restore checked from the console on the owner; owner rejoined and landed in HUB; full NPC flow not tested
- RTP portal | lifesteal_lobby portal -2..2 66..72 z37.7 | owner walked in | yes, landed in the lifesteal world
- AxAFKZone zone "afk" (the AFK pool) | via sudo `axafkzone wand` / `create afk` | now irrelevant: you disabled AxAFKZone
- `/shard setafk` | the owner ran it at lifesteal_lobby 28.5 64 28.5 | the owner got 1 shard

## B. Changed that the panel session must know about
- Gamerule `command_block_output` | true → false | all 7 worlds | owner: "ye command kisi ko na dekhay"
- Gamerule `log_admin_commands` | true → false | HUB, lifesteal_lobby, lifesteal | same reason
- MV-Inventories | `lifesteal_lobby` moved from group `lobby` → `lifesteal` | owner said "inv ok"
- MV-Inventories optional shares turned on: `mvinv toggle economy`, `toggle game_statistics`, `toggle advancements` | off → on (now applied in both groups) | separation rule
- LuckPerms `default` | `essentials.spawn`, `essentials.warp`, `essentials.warp.list` = true in `world=hub` and `world=lifesteal_lobby` | false → true | owner asked for /spawn and warps everywhere. Your Skript still blocks typed commands in HUB, so in HUB these nodes do nothing.
- LuckPerms `default` | `essentials.kits.starter` = true (global) | — | starter kit
- LuckPerms new group `moderator` (2 Nov 11:45 PKT) | parent default, weight 800 | firecraft.staff, essentials kick/mute/tempban/unban/tp/tphere/vanish/gamemode(.survival), coreprotect lookup/inspect/rollback/restore; worldedit.*, worldguard.region.bypass.*, gamemode.creative only in world=hub + world=lifesteal_lobby | owner: moderators + their own AI helper sessions (members not added yet, waiting for their IGN)
- Essentials spawn = lifesteal_lobby 0.5 65 0.5 | set by me via sudo `setspawn` | matches your note
- Scoreboard objectives | `fc_pt` (stat) removed; added `fc_lpt`, `fc_lx`, `fc_ly`, `fc_lz`, `fc_ld`, `fc_clk` (dummy) | used by the playtime key and last-location systems
- Command blocks in force-loaded overworld chunks [0,0] and [1,0], inside the stone under HUB. **Keep these chunks force-loaded.**
  - y120 z3: join welcome title (`fc_leave`/`fc_wait`)
  - y120 z6: my old "Lobby Selector" (see D1)
  - y120 z9: hourly Common key
  - y120 z10: join → HUB tp, plus saving each player's last lifesteal-side spot every 20 ticks (on the ground only) and restoring it when the player has tag `fc_restore`
  - y120 z12: HUB/lobby NPC links. x9 is now `tag @a[tag=fc_go_ls] add fc_restore` instead of `tp lifesteal_lobby 0.5 65 0.5`.
  - y120 z15: RTP portal

## C. Requests for the panel session (needs owner's "haan")
- [ ] URGENT: fix the HUB Server Selector compass. Right-click teleports the owner to random spots in HUB instead of opening the selector menu (see D0) | `plugins/Skript/scripts/firecraft-hub.sk` + `plugins/DeluxeMenus/gui_menus/selector.yml` | right-click should only run `dm open selector <player>` | owner: "fix it"
- [ ] TAB rank name for the new LuckPerms group `moderator` (weight 800, prefix "🛡 MOD » ") | `plugins/TAB/config.yml` → `placeholder-output-replacements` → `%group%` | `moderator: MODERATOR` | owner asked for a moderator rank on 2 Nov
- [ ] Make the selector's LIFESTEAL button use the last-location system | `plugins/DeluxeMenus/gui_menus/selector.yml` | replace `[console] mvtp %player_name% lifesteal_lobby` with `[console] tag %player_name% add fc_restore` (the command blocks then send the player to their last lifesteal-side spot, or to lifesteal_lobby 0.5 65 0.5 if none is saved) | owner: "jab koi or lobby select kare tab waha jai jaha se wo leave howa tha"
- [ ] Stop Essentials sending joins to lifesteal_lobby | `plugins/Essentials/config.yml` | `spawn-on-join: false` | owner: "first time ya rejoin ker k aye to wo hub per aye". My command block already sends every join to HUB 0.5 151 0.5 one tick later; with `spawn-on-join: true` the player is moved twice.
- [ ] Remove my old vanilla sidebar now that TAB scoreboards are live | console `scoreboard objectives setdisplay sidebar` (I can do it after your "ok") | — | avoids a clash with the TAB scoreboard
- [ ] Price-lore issue: owner sees "$3 / $10k" on every item (not stored in item NBT, display only, probably the DonutShop worth display (not sure)) | `plugins/DonutShop/config.yml` | limit it to the shop/sell menus only | owner: "jahan iski zarorat hai serf waha tak mehdoodh rakho"

## D. Problems / errors seen
- D0 (URGENT, panel side) | 2 Nov ~13:40-13:50 PKT | Owner: "compass per jab right click ker raha ho to random tp ho raha ho". Owner held the Skript "Server Selector" compass (hotbar slot 4, lore "Game mode chunne ke liye right-click karo"). After each right-click he landed at random block-centre spots **inside HUB (minecraft:overworld)**, at different heights: 7.5 21 -231.5, -63.5 5 -315.5, -26.5 151 64.5, -15.5 87 -46.5. No player command was logged except `/hub` (CoreProtect). He had no tags except `fc_seen`. My command blocks are ruled out: the old selector chain y120 z6 is off, and the RTP chain only works in lifesteal_lobby and sends to `lifesteal`. It looks like a random-teleport (tpr / spreadplayers-like) running in HUB from the compass right-click (not sure). Please check `plugins/Skript/scripts/firecraft-hub.sk` (right-click handler) and `gui_menus/selector.yml` (open_command / item actions).
- D1 (conflict, FIXED 2 Nov 13:35 PKT: my repeating block overworld 3 120 6 set to `auto:0b`, so my carrot selector is no longer given) | My chain y120 z6 x3 gives a carrot_on_a_stick "Lobby Selector" (`custom_data {fc_sel:1b}`) into **hotbar.4 (slot 5)** every tick in HUB. Your Skript also puts the "Server Selector" compass in slot 5. They will overwrite each other, and my menu uses `/trigger fc_lobby` from clickable chat, which your HUB typed-command block probably stops (not sure). | overworld y120 z6
- D2 | Your section 6 says crates use `/sc set <crate>` "while looking at the block". From the SlimeCrates bytecode: `set` uses the **block at the player's feet** (`Player.getLocation().getBlock()`), and `/sc` is GriefPrevention's subdivideclaims alias here. Use `/slimecrates set` while **standing in** the crate block's space. The crates are already placed (see A).
- D3 | Essentials overrides `item`, `clear`, `give`, `kill`, `tp` from the console | use `minecraft:` + the command
- D4 | Plugin commands run from command blocks don't expand `@a`/`@p` (`shard see @a` → "No player named @a") | vanilla `minecraft:give` with the key's custom_data is used instead
- D5 (not sure) | 11:35 PKT: the owner's rejoin landed in HUB even with `spawn-on-join: true`, so my tick-later tp wins for now

## E. Questions for the owner
- Which HUB selector should stay? (options: the panel's Skript compass + DeluxeMenus `selector` with the C1 change [recommended] / my command-block Lobby Selector). If the panel one, I remove chain y120 z6 (block x3 → stone).
- HUB LIFESTEAL NPC (mannequin + interaction `ls_click` at overworld 17.5 151 -9.5, plus walking within 1.3 blocks): keep it? It already uses `fc_restore`, and it works without typed commands.
- Should the AxAFKZone zone file `zones/afk.yml` stay or go? (the plugin is disabled now)

## F. Current facts I rely on (so we stay in sync)
- Worlds: HUB (minecraft:overworld) + HUB_nether + HUB_the_end; lifesteal_lobby, lifesteal, lifesteal_nether, lifesteal_the_end. MV-Inv groups: lobby = HUB; lifesteal = lifesteal_lobby + lifesteal + nether + end; economy, game_statistics and advancements are on.
- Spawn (Essentials): lifesteal_lobby 0.5 65 0.5
- Warps:
  - hub: HUB 0.5 151 0.5 (yaw 180)
  - lobby: lifesteal_lobby 0.5 65 0.5
  - crates: lifesteal_lobby 0.5 65 -34.5 (yaw 180)
  - shop: lifesteal_lobby -34.5 65 0.5 (yaw 90)
  - kits: lifesteal_lobby 36.5 65 0.5 (yaw -90)
  - afk: lifesteal_lobby 28.5 64 28.5
  - leaderboards: lifesteal_lobby 28.5 65 -30.5 (yaw 180)
  - rtp: lifesteal_lobby 0.5 65 33.5 (yaw 0)
  - pvp: lifesteal_lobby -27.5 65 27.5
- NPCs (ZNPCsPlus id -> action):
  - ls_shop (lifesteal_lobby -39.5 65 -8.5) → `shop`
  - ls_auction (-39.5 65 -2.5) → `auctionhouse`
  - ls_heartshop (-39.5 65 9.5) → `heartshop`
  - ls_kits (40.5 65 0.5) → `kitshop`
  - HUB has command-block "NPCs" (mannequin + interaction), not ZNPCsPlus: LIFESTEAL at overworld 17.5 151 -9.5 → `fc_go_ls` → `fc_restore`; BACK TO HUB at lifesteal_lobby 0.5 65 3.5 → HUB 0.5 151 0.5
- Crates (SlimeCrates location = the pedestal top block, y66, z -40): vote -10 (ender_chest), common -6 (barrel), rare -2 (blue shulker), epic 2 (purple), legendary 6 (yellow), heart 10 (red)
- Regions (lifesteal_lobby):
  - `__global__`: use, chest-access, build, pvp, item-drop, enderpearl = DENY; invincible = ALLOW
  - `ls_pvp_arena` (-37 64 19 → -19 80 37): pvp ALLOW, invincible DENY, item-drop ALLOW
  - `ls_enchanter` (-38 65 3): use ALLOW
- Holograms: none from FancyHolograms yet. The leaderboard boards still show "Coming Soon" (text_display) at lifesteal_lobby 21.5 / 28.5 / 35.5, 66.1, -34.3. Next in-game job: replace them with ajLB placeholders (needs the owner online to run `/hologram create`).
- Kits: starter (24h, free), archer/warrior/tank (delay 0, only via the `kitshop` menu)
