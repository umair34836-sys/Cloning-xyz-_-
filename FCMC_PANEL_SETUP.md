# FireCraft MC — Panel / File Setup List

Ye file un saare kaamon ki list hai jo **server console se nahi ho sakte** (plugin config files, panel/hosting, plugin install).
Owner (MrCopper804) ye file kisi doosre AI / helper ko de kar apne PC/panel se kar sakta hai.
Har item me: kya karna hai, kaunsi file, exact text, aur baad me kya command chalani hai.

- Server: Paper 26.2, panel par files `plugins/<Plugin>/...`
- Coins = **PlayerPoints** (website se kharide jayenge), **1 coin = 1 PKR**
- In-game ka kaam Claude (server session) console se karta hai — wo is file me nahi, sirf note kiya hai.
- Status: `[ ]` = baqi, `[x]` = ho gaya

---

## 0. ECONOMY RULE (owner ka faisla — har setup me yaad rakhna)  
Server ka **balance (money), shards aur coins — sab ki value bahut HIGH rakhni hai**, taake koi bhi cheez asaani se na mile.
Shop/sell prices, AFK/vote rewards, crate chances — sab isi hisaab se tight rakhne hain. **Hearts sabse mehngi cheez** hain.
Owner ke alfaaz: "ta k koi server ko easily beat na ker sake — sub se high value, jaise real life me Bitcoin hai waise hi".
Matlab: currency kam milni chahiye (scarce), inflation na ho, sell prices low, buy prices high, free rewards chhote.

---


## 0b. SEPARATION RULE — HUB alag, har mode alag (owner ka faisla)
Owner: "hub se her ek jizz ko alag kro, lifesteal ko alag and baqi incoming ko bhi hub se separate rakhna — playtime, coins, balance, everything".
**Console se ho chuka `[x]`:**
- Multiverse-Inventories groups: `lobby` = HUB, `lifesteal` = lifesteal_lobby + lifesteal + nether + end. Har naya mode = naya group.
- Group-wise alag: inventory, armor, ender chest, XP, health/hearts (max_hit_points), potion effects, **economy (Vault balance)**, **vanilla statistics**, **advancements** (`mvinv toggle economy|game_statistics|advancements`).
- Hourly Common key playtime ab sirf lifesteal side ka time ginta hai (`fc_lpt`), HUB ka nahi.
**Console se nahi ho sakta (plugins global hain) `[ ]`:**
- **Coins (PlayerPoints)**, **Shards (DonutShop)**, **hPlaytime**, **LifeStealZ hearts data**, SlimeCrates virtual keys, AuctionHouse — ye plugins poore server par ek hi data rakhte hain. Abhi sirf lifesteal inhe use karta hai, isliye masla nahi.
- **Naye modes ke liye:** har mode ko alag currency chahiye to (a) BungeeCord/Velocity network par har mode alag backend server (sabse saaf tareeqa), ya (b) har mode ke liye alag per-world currency plugin. Mode banate waqt ye decide karna.
- LifeStealZ ko sirf lifesteal worlds tak limit karo (`plugins/LifeStealZ/config.yml` world setting — config paste karo, exact line bataunga).

---

## 1. AFK zone: har 1 minute = 1 Shard  `[ ]`
**File:** `plugins/AxAFKZone/zones/afk.yml` (zone "afk" lifesteal_lobby AFK pool par ban chuka hai)

`reward-time-seconds`, `roll-amount` aur poora `rewards:` hissa hata kar ye paste karo (`zone:` wala part mat chhedna):
```yaml
reward-time-seconds: 60
roll-amount: 1
rewards:
  - chance: 100.0
    display: "&#55FFFF1x Shard"
    commands:
      - "shard give %player% 1"
```
Phir in-game: `/axafkzone reload`
> Note: DonutShop ka apna AFK system (`/shard setafk` pool par set hai) bhi shard de sakta hai. Agar double shard mile to upar wale `rewards:` ko khali `rewards: []` kar do.

## 2. Scoreboard me Shards (TAB 6.2.1)  `[ ]`
**File:** `plugins/TAB/config.yml` → `scoreboard:` section
- `enabled: false` → `enabled: true`
- `scoreboard-1.20.3+:` ki `lines:` me add:
```yaml
        - "* &bShards&7:||%donutshop_shards%"
```
- `scoreboard:` ki `lines:` me add:
```yaml
        - "* &bShards&7: &f%donutshop_shards%"
```
Phir in-game: `/tab reload` → Claude ko bolo "server shard done" (wo purana vanilla sidebar hata dega).
(`%donutshop_shards%` placeholder test ho chuka hai.)

## 3. Vote se VOTE KEY  `[ ]`
**a) Votifier port (panel/hosting):**
- Panel → Network/Allocations me naya port add karo.
- `plugins/Votifier/config.yml` (NuVotifier) me `port:` us number par set → restart.
- Vote sites par: IP `play.firecraft.fun`, Votifier port, Public key = `plugins/Votifier/rsa/public.key`.

**b) `plugins/VotingPlugin/VoteSites.yml`:** file ke aakhir me `EverySiteReward: {}` ko replace karo:
```yaml
EverySiteReward:
  Commands:
  - 'slimecrates key %player% vote 1'
  Messages:
    Player: '&aVote ke liye shukriya! +1 Vote Key'
```
Har vote site ke liye `VoteSites:` me site banao (`Enabled: true`, `ServiceSite:` — vote karne par console me naam aata hai, `VoteURL:`).
Phir in-game: `/av reload`

## 4. Items par "$3 / $10k" price display  `[ ]` (pending)
Ye item ke andar save nahi hai, sirf display hai — shayad DonutShop ka worth display.
**Karna:** `plugins/DonutShop/config.yml` ka poora text Discord **#owner-private** me paste karo → Claude exact line batayega jo band/limit karni hai. (Andaze se setting ka naam nahi diya.)

## 5. Leaderboards (Top Hearts / Top Kills / Top Playtime)  `[ ]`
- **Install:** ajLeaderboards (Spigot/Modrinth) → restart. (LifeStealZ me top-player placeholder nahi hai, isliye zaroori.)
- Install ke baad Claude ko batao — wo FancyHolograms se lifesteal_lobby NE boards (x 21/28/35, z -35) par holograms lagwayega.

## 6. Paid KITS (coins se)  `[ ]` — kits ban gaye, sirf sale menu baqi
**Starter kit** `[x]`: stone sword/pickaxe/axe/shovel + 5 steak, har 24h free (`/kit starter`, lifesteal world).

Owner: "pehla sasta, phir price ke saath items behtar" → Claude ne decide kiya (badalna ho to owner bataye):
| Kit | Items (in-game ban chuka `[x]`, delay 0) | Price |
|---|---|---|
| Archer | chainmail armor (Prot I), bow (Power III, Punch I), 64 arrows, 16 steak | 100 coins |
| Warrior | iron armor (Prot I), iron sword (Sharp II), 4 golden apple, 16 steak | 200 coins |
| Tank | diamond armor (Prot II), diamond sword (Sharp I), shield, 8 golden apple, 16 steak | 350 coins |

Paid kits ka delay 0 hai (cooldown hota to coins kat kar kit na milne ka risk). Normal players ko `essentials.kits.warrior|archer|tank` permission **mat** dena (warna free mil jayega).

**Sale menu — `plugins/DeluxeMenus/gui_menus/kitshop.yml`** (naya file) + `plugins/DeluxeMenus/config.yml` me `gui_menus:` ke neeche `kitshop: { file: kitshop.yml }`:
```yaml
menu_title: '&8&lKITS SHOP'
open_command: kitshop
size: 27
items:
  warrior:
    material: IRON_SWORD
    slot: 11
    display_name: '&f&lWarrior Kit'
    lore:
      - '&7Iron armor + sword + food'
      - '&ePrice: &f200 Coins'
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '200'
      deny_commands:
        - '[message] &cCoins kam hain! Website se coins lo.'
    left_click_commands:
      - '[console] points take %player_name% 200'
      - '[console] kit warrior %player_name%'
      - '[message] &aWarrior kit mil gaya!'
      - '[close]'
  archer:
    material: BOW
    slot: 13
    display_name: '&a&lArcher Kit'
    lore:
      - '&7Chainmail + bow + arrows'
      - '&ePrice: &f100 Coins'
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '100'
      deny_commands:
        - '[message] &cCoins kam hain! Website se coins lo.'
    left_click_commands:
      - '[console] points take %player_name% 100'
      - '[console] kit archer %player_name%'
      - '[message] &aArcher kit mil gaya!'
      - '[close]'
  tank:
    material: DIAMOND_CHESTPLATE
    slot: 15
    display_name: '&b&lTank Kit'
    lore:
      - '&7Diamond armor + shield + gapples'
      - '&ePrice: &f350 Coins'
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '350'
      deny_commands:
        - '[message] &cCoins kam hain! Website se coins lo.'
    left_click_commands:
      - '[console] points take %player_name% 350'
      - '[console] kit tank %player_name%'
      - '[message] &aTank kit mil gaya!'
      - '[close]'
```
Phir in-game: `/dm reload`. (Kits area NPC ka click action Claude `kitshop` menu par set kar dega.)
> Note: DeluxeMenus syntax aur PlayerPoints `points take` ek baar test karna — kits ban jane ke baad.


## 6b. HEART SHOP (sirf hearts, sirf coins se, sabse mehnga)  `[ ]`
Owner: "heart shop me hearts only buy ker sakte ho using coins or hearts cost sub se high". Claude ka decision:
| Item | Price |
|---|---|
| 1 Heart | 500 coins |
| 3 Hearts | 1400 coins |

**File:** `plugins/DeluxeMenus/gui_menus/heartshop.yml` + `config.yml` me `heartshop: { file: heartshop.yml }`
```yaml
menu_title: '&4&lHEART SHOP'
open_command: heartshop
size: 27
items:
  heart1:
    material: NETHER_STAR
    slot: 11
    display_name: '&c&l1 Heart'
    lore:
      - '&ePrice: &f500 Coins'
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '500'
      deny_commands:
        - '[message] &cCoins kam hain! Website se coins lo.'
    left_click_commands:
      - '[console] points take %player_name% 500'
      - '[console] lifestealz giveItem %player_name% defaultheart 1'
      - '[message] &a+1 Heart mil gaya!'
      - '[close]'
  heart3:
    material: NETHER_STAR
    amount: 3
    slot: 15
    display_name: '&c&l3 Hearts'
    lore:
      - '&ePrice: &f1400 Coins'
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '1400'
      deny_commands:
        - '[message] &cCoins kam hain! Website se coins lo.'
    left_click_commands:
      - '[console] points take %player_name% 1400'
      - '[console] lifestealz giveItem %player_name% defaultheart 3'
      - '[message] &a+3 Hearts mil gaye!'
      - '[close]'
```
Phir `/dm reload`. (`lifestealz giveItem <player> defaultheart <n>` console se test ho chuka hai.) HEART SHOP NPC Claude lagayega (click = `heartshop`).

## 7. Website coins → server  `[ ]`
Website/store se coin kharidne par server console command chalni chahiye:
`points give <player> <amount>` (PlayerPoints). Store (Tebex/website) me yahi command set karo.
Crate keys website se: `slimecrates key <player> <rare|epic|legendary|heart> <amount>`.

## 7b. /hub aur /lobby commands (alias)  `[ ]`
Warps console se ban chuke hain (`/warp hub`, `/warp lobby`, crates, shop, kits, afk, pvp, leaderboards, rtp) aur `/spawn` = lifesteal lobby. Essentials me `/hub` command nahi hoti, isliye Bukkit alias banana hai.
**File:** server root `commands.yml` → `aliases:` ke neeche add karo:
```yaml
aliases:
  hub:
  - "warp hub"
  lobby:
  - "warp lobby"
```
Phir server restart (ya console `reload commands` agar available ho). Permission underlying `/warp` wali lagti hai (default players ke liye lobby/hub/lifesteal sab jagah allow kar di gayi hai).

## 8. Baqi owner decisions / panel kaam
- [ ] Maintenance OFF kab karna hai (launch)
- [ ] Discord invite link (server-ip / welcome me)
- [ ] Backups (panel par automatic backup schedule)
- [ ] LifeStealZ ko sirf lifesteal worlds tak limit (`plugins/LifeStealZ/config.yml` → worlds setting; config paste karo to exact line bataunga)
- [x] Market NPCs: villager type (shop=weapon smith, auction=cartographer, kits=armorer) — skin ki zaroorat nahi
- [ ] ENCHANTER stall: enchanting table + bookshelves (Claude banayega)
- [ ] Baqi 4 lobbies (SURVIVAL, PARKOUR, PVP PRACTICE, MINIGAMES, EVENT — abhi "Coming Soon")
- [x] Lobby inventory group: lifesteal_lobby → 'lifesteal' group (console se ho gaya)
- [ ] PvP arena (`ls_pvp_arena`) in-game test
