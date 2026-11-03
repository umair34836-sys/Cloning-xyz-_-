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
Phir in-game: `/dm reload`. (Kits area NPC `ls_kits` ka click pehle se `kitshop` par set hai `[x]`.)
> Note: DeluxeMenus syntax aur PlayerPoints `points take` ek baar test karna — kits ban jane ke baad.


### 6c. Kitshop me kit ke ANDAR ka saaman dikhao  `[ ]`  (owner, 3 Nov)
Owner: "kitshop per serf kits name or price wala seen hai liken kits k under kia haui wo players kaise dekhingay please fix it"
**File:** `plugins/DeluxeMenus/gui_menus/kitshop.yml` (backup pehle). Har kit ka `lore:` poora is se badlo (baqi keys same), phir `/dm reload`:
```yaml
  warrior:
    lore:
      - '&8&m                        '
      - '&f&lKit me kya milega:'
      - '&7• &fIron Helmet, Chestplate, Leggings, Boots &b(Protection I)'
      - '&7• &fIron Sword &b(Sharpness II)'
      - '&7• &64x Golden Apple'
      - '&7• &f16x Steak'
      - '&8&m                        '
      - '&ePrice: &f200 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
  archer:
    lore:
      - '&8&m                        '
      - '&f&lKit me kya milega:'
      - '&7• &fChainmail Helmet, Chestplate, Leggings, Boots &b(Protection I)'
      - '&7• &fBow &b(Power III, Punch I)'
      - '&7• &f64x Arrow'
      - '&7• &f16x Steak'
      - '&8&m                        '
      - '&ePrice: &f100 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
  tank:
    lore:
      - '&8&m                        '
      - '&f&lKit me kya milega:'
      - '&7• &fDiamond Helmet, Chestplate, Leggings, Boots &b(Protection II)'
      - '&7• &fDiamond Sword &b(Sharpness I)'
      - '&7• &fShield'
      - '&7• &68x Golden Apple'
      - '&7• &f16x Steak'
      - '&8&m                        '
      - '&ePrice: &f350 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
```
Items Essentials kits (archer/warrior/tank, 2 Nov in-game banaye) ke hisaab se hain — agar panel ko `plugins/Essentials/kits.yml` me kuch alag dikhe to lore usi ke mutabiq karo (andaza nahi).
Optional: starter kit ka bhi ek item slot (free, 24h) — `[console] kit starter %player_name%` nahi, sirf info lore, kyunki starter `/kit starter` se milta hai.

### 6d. SPECIAL paid kits (owner, 3 Nov) — v2 RESEARCH-BASED, owner "haan" ka intezaar  `[ ]`
Owner: "kits wale area ko re design kroo ... her ek kit ko special bnao kio k players humay pay ker rahay hai" + "kits ko apne andaze se maat banao dosre public servers ka dekh ker unse idea loo"
**Research (3 Nov):**
- netherite.gg Lifesteal (https://netherite.gg/wiki/lifesteal/store): har paid rank kit = full Netherite set Prot IV + Unbreaking III + Mending (helmet Respiration III/Aqua Affinity, boots Feather Falling IV/Depth Strider III); sword Sharp V, Looting III, Fire Aspect II, Sweeping Edge III, Unbr III, Mending; pickaxe/axe/shovel/hoe Eff V, Fortune III, Unbr III, Mending; 16→64 golden apple, 0→3 enchanted golden apple, 32 steak, 4→9 hearts; cooldown 16h→2h.
- PikaNetwork OP Lifesteal (pika-network.net/threads/op-lifesteal-guide.543357): kit vouchers — Prot III gear + Sharp IV weapons + golden apples (Legend), Prot V/Sharp V + god apples (Ultimate).
- JartexNetwork KitPvP: Warrior kit = all Prot I + Sharp II (sabse neeche wala).
**Pattern:** paid kit = poora set (armor + sword + TOOLS), Unbreaking/Mending, khana + gapples; tier badhne par enchant level + gapples badhte hain. Hamare rule ke hisaab se: **hearts kit me NAHI** (hearts sirf heart shop), netherite nahi (endgame ke liye).
| Kit (price) | Saaman (har item kit ke rang ka naam + lore "✦ FireCraft MC • <Kit> Kit ✦") |
|---|---|
| 🏹 ARCHER (100) | iron armor Prot II, Unbr III; bow Power IV, Punch I, Infinity, Unbr III + 1 arrow + 16 spectral arrow; iron sword Sharp III, Unbr III; iron pickaxe Eff III, Unbr III; 8 golden apple; 32 steak |
| ⚔ WARRIOR (200) | diamond armor Prot III, Unbr III; diamond sword Sharp IV, Fire Aspect I, Unbr III; diamond pickaxe Eff IV, Fortune II, Unbr III; diamond axe Eff IV, Unbr III; shield Unbr III; 16 golden apple; 8 ender pearl; 32 steak |
| 🛡 TANK (350) | diamond armor Prot IV, Unbr III, Mending (helmet Respiration III, boots Feather Falling IV); diamond sword Sharp V, Looting II, Fire Aspect II, Unbr III, Mending; diamond pickaxe/axe/shovel Eff V, Fortune III (pick), Unbr III, Mending; shield Unbr III; 32 golden apple; 1 enchanted golden apple; 1 Totem of Undying; 64 steak |
**Price note (economy rule):** in kits ki value badh gayi hai (Tank ≈ netherite.gg ke sabse neeche wale rank kit se thoda kam) — owner chahe to price badha sakta hai (suggestion: Archer 150, Warrior 400, Tank 800 coins). Owner decide karega.
Kits area (lifesteal_lobby E pad x29..45 z-11..11): 4 booths (Starter free + 3 paid), har booth apne rang ka pedestal + canopy + lanterns, armor stand kit pehne, upar ghoomta weapon (item_display), text sign (naam, price, top items, "Right-click Kits NPC / /kitshop"), beech me bada "✦ KITS ✦" title, ls_kits NPC beech me.
Owner "haan" ke baad: Essentials kits.yml (archer/warrior/tank) — panel; area build — Claude (console); kitshop.yml lore (§6c) naye items ke saath.

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
Phir `/dm reload`. (`lifestealz giveItem <player> defaultheart <n>` console se test ho chuka hai.) HEART SHOP NPC `ls_heartshop` lag chuka hai, click = `heartshop` `[x]`.

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

### 7b-UPDATE (owner, 3 Nov): warps hub/lobby hatao, /hub aur /spawn rahe  `[ ]`
Owner: "server warp lobby remove ker k /spawn rakhdo lobby k lie and warp hub remove ker k /hub hona chaye"
- **Claude ne kar diya:** `delwarp rtp`, `delwarp lobby`. Ab warps: afk, crates, hub, kits, leaderboards, pvp, shop. `/spawn` (lifesteal_lobby 0.5 65 0.5) har jagah allowed hai.
- **`warp hub` abhi RAKHA hai** kyunki `/hub` alias usi par chalta hai. Panel kaam (`commands.yml`, backup pehle):
```yaml
aliases:
  hub:
  - "mvtp HUB"
  lobby:
  - "spawn"
```
  HUB ka Multiverse spawn = 0.5 151 0.5 yaw 180 (warp hub jaisa). Players ko mvtp ki permission chahiye (Multiverse 5 ka exact node verify karo, e.g. `multiverse.teleport.self.w`) — bata dena, main LP me de dunga (sirf HUB tak, agar node allow kare).
- Restart ke baad `/hub` test → phir Claude `delwarp hub`.

## 7c. HUB me free "Tools" kit (first-join) band karo  `[ ]`  (owner, 3 Nov)
Owner: "hub me kio players ko free wala kit mil raha hai ? hub ko her ek lobby or world se alag rakhoo"
**Wajah:** EssentialsX pehli dafa join par `newbies.kit` deta hai (default kit = `tools`: stone sword/shovel/pickaxe/axe). Join HUB me hota hai, isliye kit HUB inventory (MV-Inv group `lobby`) me aa jata hai. Marzarn ke HUB inventory me yahi 4 stone tools mile (3 Nov).
**File:** `plugins/Essentials/config.yml` → `newbies:` section (backup pehle)
```yaml
newbies:
  kit: ''
```
Phir console `essentials reload` (ya restart batch me). Starter kit lifesteal me `/kit starter` (24h) se milta rahega.
**Console side (Claude ne kar diya, 3 Nov):** `essentials.kits.starter` default group me global se hata kar sirf `world=lifesteal_lobby` (jahan `essentials.kit` bhi hai). Marzarn ke HUB inventory se stone tools hataye.

## 7d. URGENT: DonutShop SHARDS = PlayerPoints COINS (alag karo)  `[ ]`  (owner, 3 Nov)
Owner: "shard and coins dono mil rahay hai mujhe jab k coins alag chizz hai or shards alag"
**Saboot (3 Nov):** `papi parse` → MrCopper804 `%donutshop_shards%`=8 aur `%playerpoints_points%`=8 (phir dono 9); Marzarn 2 = 2. `points lead` = shard balances. Yaani DonutShop (1.5.6-SNAPSHOT, byebye_gg) shards ko **PlayerPoints me store** kar raha hai → AFK pool se FREE coins (1 coin = 1 PKR) ban rahe hain. Economy rule ke khilaaf.
**File:** `plugins/DonutShop/config.yml` (aur agar alag ho to shards/AFK wali yml) — backup pehle. Koi setting jaise `shards.storage` / `use-playerpoints` / `economy: PLAYERPOINTS` / `currency` dhoondo aur shards ko DonutShop ki apni storage (internal/sqlite/yml) par karo. Exact key file dekh kar hi — andaza nahi.
**Phir:** `/shard reload` (ya restart batch). Check: `papi parse <player> %donutshop_shards% %playerpoints_points%` alag values dikhaye.
**Saaf-safai (owner decide):** abhi tak ke AFK se bane points coins me jud gaye (MrCopper804 9, Marzarn 2). Separate hone ke baad PlayerPoints reset karna hai ya nahi — owner ka faisla.

## 8. Baqi owner decisions / panel kaam
- [ ] Maintenance OFF kab karna hai (launch)
- [ ] Discord invite link (server-ip / welcome me)
- [ ] Backups (panel par automatic backup schedule)
- [ ] LifeStealZ ko sirf lifesteal worlds tak limit (`plugins/LifeStealZ/config.yml` → worlds setting; config paste karo to exact line bataunga)
- [x] Market NPCs: villager type (shop=weapon smith, auction=cartographer, kits=armorer) — skin ki zaroorat nahi
- [x] ENCHANTER stall: enchanting table + 6 bookshelves + WG region ls_enchanter (use allow)
- [ ] Baqi lobbies (SURVIVAL, PARKOUR, PVP PRACTICE, MINIGAMES, EVENT — abhi "Coming Soon")
- [x] Join/rejoin → HUB; LIFESTEAL select → player ki last jagah (command blocks)
- [x] RTP portal test OK; /spawn + 9 warps
- [x] Lobby inventory group: lifesteal_lobby → 'lifesteal' group (console se ho gaya)
- [ ] PvP arena (`ls_pvp_arena`) in-game test
