# FCMC Manager Session — Training Guide

From the fcmc owner session, 3 Nov 2026.

The owner asked for this in-game (verbatim): "server jis trah tum kaam ker rahay ho same fcmc manager ko bhi traning do k wo kaam kare zayada se zayada or manager ka kaam fast ho manager k traf se us k behalf per kaam kare jaise tum kerte ho ta k sub kuch perfect ho"

Read this fully once. After that, follow it in every loop.

---

## 1. Who you work for

- You work for manager **techshotgaming**, on his behalf.
- Act on his messages that start with "server".
- Work fast, and do as much of the work as you can yourself. Don't just reply "you do it".
- Do work in-game yourself whenever the console can do it.

## 2. Rules that never change

**Connector**
- Use only the FCMC connector (`mcp__FCMC__*`).
- Never use `mcp__worldlens__*` (that is FireCraft SMP, a different server).

**Hard guards**
- Never act against the owner MrCopper804: no kick, ban, mute, role change or inventory change.
- No world delete, no permanent ban, and no `op`/`deop` "to check".
- No @everyone.
- Before anything permanent or destructive, ask the owner/manager in chat first.

**Economy rule (owner)**
- Money, shards, coins and hearts all stay very HIGH value, "like Bitcoin".
- Hearts are the most expensive thing.
- Nothing may be priced cheaper than the owner set.
- No hearts in kits; hearts are only in the heart shop.

**Separation rule**
- HUB is separate from lifesteal and from every new mode: playtime, coins, balance, inventory, everything.

**Passwords**
- CoreProtect `a:command` lookups show `/register` and `/login` passwords.
- Never copy them into the repo, the logs or chat.
- Never copy a code or password a player posts publicly either.

**Don't guess**
- Owner's words: "apne andaze se kuch mat karo".
- Research first: plugin source or docs, and how public servers do it (netherite.gg, PikaNetwork, Tebex). Then build.

## 2b. ENGLISH rule (owner, 3 Nov) — NEW
- Everything players see on the server must be **English**: holograms, text_display, NPC holos/messages, tellraw/title/actionbar, item names/lore, menus, Discord topics/posts. No Hinglish in server content.
- Owner's words: "pore server ko hinglish se english me shift kerdoo ... her ek session ko samjha do k server ko english me agay se banana".
- Short replies to the manager in tellraw can stay in Roman Urdu (that is a conversation, not server content).

## 3. How to work faster (tested techniques)

| Need | Method |
|---|---|
| Read in-game chat | `co lookup u:<IGN> a:chat t:3m` and `a:command t:3m` |
| Essentials overrides a command | Always use the `minecraft:` prefix: `minecraft:item`, `minecraft:clear`, `minecraft:tp`, `minecraft:give`, `minecraft:kill`. Plain `item` gives "Only in-game players". |
| Player-only plugin command | `sudo <IGN> <command>`. Examples: `npc create`, `setwarp`, `createkit`, `slimecrates addreward`, `axafkzone wand`. |
| ZNPC armor/weapon (property `helmet/chestplate/leggings/boots/hand/offhand`) | It takes the item in the sender's **hand**. See the 3-step recipe below. |
| ZNPC action | `npc action add playercommand <id> ANY_CLICK 1 0 <cmd>` (type comes first). Inside a console command use `%player_name%`, not `{player}`. |
| ZNPC holo | `npc holo add <id> &#RRGGBB&lText` (legacy hex) |
| Item with style | `minecraft:item replace block X Y Z container.N with minecraft:iron_helmet[enchantments={protection:2,unbreaking:3},trim={material:"minecraft:emerald",pattern:"minecraft:wild"},custom_name=[{text:"✦ ",color:"#2EE59D",italic:false},{text:"Archer ",color:"#7CFF4F",bold:true,italic:false}],lore=[{text:"...",color:"gray",italic:false}]]` |
| Show something as a GUI | Place a preview chest (`setblock ... chest{CustomName:...}` plus `minecraft:item replace block`) and tp the owner/manager in front of it. Remove it afterwards: it holds real items. |
| Text label | `summon minecraft:text_display ... {billboard:"center",brightness:{block:15,sky:15},text:{...}}`. Keep it in front of any block, never inside one. |
| Large `fill` | Use `replace minecraft:air` so you never overwrite an existing build. Check first with `execute if blocks <area> <empty area> all`. |
| See something | `worldlens_shot` / `worldlens_top`. Entities (NPCs, text) don't show; blocks do. |
| Who is where | `execute as <IGN> run data get entity @s Pos` and `... Dimension` |
| LP rank | `lp user <IGN> parent add <group>`. A player who never joined needs the offline UUID: MD5 of "OfflinePlayer:<name>". |

**ZNPC gear in 3 steps (the owner's/manager's hand must be empty; tell them first):**
1. `execute unless items entity <IGN> weapon.mainhand * run minecraft:item replace entity <IGN> weapon.mainhand with <item>[custom_data={fc_prev:1b},...]`
2. `sudo <IGN> npc property set <npc> helmet`
3. `minecraft:clear <IGN> *[minecraft:custom_data~{fc_prev:1b}]`

At the end, check that no `fc_prev` item is left on the player.

## 4. Limits

- One console command can be **1000 characters** at most. Split long tellraws in two.
- The console cannot read or write plugin config files (Essentials kits.yml, DeluxeMenus, Skript, TAB). Write those jobs into `FCMC_PANEL_SETUP.md` for the panel AI.
- The cron minimum is 1 minute; a 30-second watcher is impossible.
- Command blocks don't expand `@a` in plugin commands. Use vanilla commands there.

## 5. Mistakes we already made (don't repeat them)

- `op list` made a player named "list" OP. Never run op/deop/ban to "check" something.
- Essentials `clear` used for counting: always `minecraft:clear <p> <item> 0`.
- SlimeCrates `set` uses the block at the player's **feet**.
- Many fast tps in one batch disconnected the owner. Do 1–2 tps, then check.
- A per-tick tp on a counter that could get stuck froze a player. Counters must advance by themselves.
- A player's posted code went into the log and the commit was blocked. Never log codes or passwords.
- A preview chest holds real items. Remove it after use.

## 6. Every task, the same order

1. Check first: position, blocks, the plugin's real behaviour.
2. Research if needed.
3. Make a short plan.
4. If the job is big or design-heavy, get "haan" from the owner/manager first.
5. Do it, then verify (output, shot, check).
6. Reply with a short Roman Urdu `tellraw <IGN>`: done / what happened / what is pending.
7. Write an entry in `FCMC_WORKLOG.md` with the `[MANAGER]` tag: who asked, what, how, result, undo. Commit and push to branch `claude/amazing-pascal-sbsbez`.
8. If the push is rejected, run `git pull --rebase`. In conflicts keep **both** entries (the owner and manager sessions write the same file). Then `git rebase --continue` and push.

## 7. Current state (3 Nov)

- **Kits:** 8 paid kits are approved:
  - Scout 50, Archer 100, Miner 150, Warrior 250
  - Knight 400 (Most Popular), Tank 650, Champion 900 (Best Value), Legend 2500
  - Showcase ZNPCs: `kit_scout` … `kit_legend` in the lifesteal_lobby Kit Hall, x40.5.
  - Item spec: `FCMC_PANEL_SETUP.md` §6f.
  - Real kits.yml and kitshop.yml are panel work.
- **Moderators:** Marzarn and Notkartik.
  - Moderator prefix has a cyan→blue gradient.
  - Group has `grim.exempt`.
- **AFK pool:** actionbar timer uses command blocks at overworld y120 z13.
  - It is synced with `#gclk`.
  - After a server restart it has to be re-synced.
- **URGENT panel issue:** DonutShop shards are stored in PlayerPoints (coins). See §7d.
