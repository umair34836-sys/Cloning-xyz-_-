# FireCraft MC — Panel / File Setup List

Ye file un saare kaamon ki list hai jo **server console se nahi ho sakte** (plugin config files, panel/hosting, plugin install).
Owner (MrCopper804) ye file kisi doosre AI / helper ko de kar apne PC/panel se kar sakta hai.
Har item me: kya karna hai, kaunsi file, exact text, aur baad me kya command chalani hai.

- Server: Paper 26.2, panel par files `plugins/<Plugin>/...`
- Coins = **PlayerPoints** (website se kharide jayenge), **1 coin = 1 PKR**
- In-game ka kaam Claude (server session) console se karta hai — wo is file me nahi, sirf note kiya hai.
- Status: `[ ]` = baqi, `[x]` = ho gaya

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

## 6. Paid KITS (coins se)  `[ ]` — prices owner approve kare
**Starter kit** ho chuka hai: stone tools + 5 steak, har 24h free (`/kit starter`).

**Proposal (owner confirm kare):**
| Kit | Items (proposal) | Price |
|---|---|---|
| Warrior | full iron armor (Prot I), iron sword (Sharp II), 16 steak, 4 golden apple | 150 coins |
| Archer | full chainmail armor (Prot I), bow (Power III, Punch I), 64 arrows, 16 steak | 150 coins |
| Tank | full diamond armor (Prot II), shield, diamond sword (Sharp I), 8 golden apple, 16 steak | 300 coins |

**Kits banana:** in-game Claude karega (`/createkit` owner ki khali inventory se). Normal players ko `essentials.kits.warrior|archer|tank` permission **mat** dena (warna free mil jayega).

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
      - '&ePrice: &f150 Coins'
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '150'
      deny_commands:
        - '[message] &cCoins kam hain! Website se coins lo.'
    left_click_commands:
      - '[console] points take %player_name% 150'
      - '[console] kit warrior %player_name%'
      - '[message] &aWarrior kit mil gaya!'
      - '[close]'
  archer:
    material: BOW
    slot: 13
    display_name: '&a&lArcher Kit'
    lore:
      - '&7Chainmail + bow + arrows'
      - '&ePrice: &f150 Coins'
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '150'
      deny_commands:
        - '[message] &cCoins kam hain! Website se coins lo.'
    left_click_commands:
      - '[console] points take %player_name% 150'
      - '[console] kit archer %player_name%'
      - '[message] &aArcher kit mil gaya!'
      - '[close]'
  tank:
    material: DIAMOND_CHESTPLATE
    slot: 15
    display_name: '&b&lTank Kit'
    lore:
      - '&7Diamond armor + shield + gapples'
      - '&ePrice: &f300 Coins'
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '300'
      deny_commands:
        - '[message] &cCoins kam hain! Website se coins lo.'
    left_click_commands:
      - '[console] points take %player_name% 300'
      - '[console] kit tank %player_name%'
      - '[message] &aTank kit mil gaya!'
      - '[close]'
```
Phir in-game: `/dm reload`. (Kits area NPC ka click action Claude `kitshop` menu par set kar dega.)
> Note: DeluxeMenus syntax aur PlayerPoints `points take` ek baar test karna — kits ban jane ke baad.

## 7. Website coins → server  `[ ]`
Website/store se coin kharidne par server console command chalni chahiye:
`points give <player> <amount>` (PlayerPoints). Store (Tebex/website) me yahi command set karo.
Crate keys website se: `slimecrates key <player> <rare|epic|legendary|heart> <amount>`.

## 8. Baqi owner decisions / panel kaam
- [ ] Maintenance OFF kab karna hai (launch)
- [ ] Discord invite link (server-ip / welcome me)
- [ ] Backups (panel par automatic backup schedule)
- [ ] LifeStealZ ko sirf lifesteal worlds tak limit (`plugins/LifeStealZ/config.yml` → worlds setting; config paste karo to exact line bataunga)
- [ ] ENCHANTER aur HEART SHOP NPC kya kholenge (koi plugin abhi nahi)
- [ ] Market NPC skins (kaunsi skin)
- [ ] Baqi 4 lobbies (SURVIVAL, PARKOUR, PVP PRACTICE, MINIGAMES, EVENT — abhi "Coming Soon")
- [ ] PvP arena (`ls_pvp_arena`) in-game test
