# FCMC — Kit Shop files for DeluxeMenus (ready to paste)

Made by the in-game AI on 3 Nov 2026 for the panel session. Owner: "kitshop kulta to hai liken usme to wahi porane wale 3 kits show ho rahay hai and un kits k under kia saman hai unki gui bhi open nhi ho rahay hai".

**Steps (panel):** back up `plugins/DeluxeMenus/gui_menus/kitshop.yml` first. Replace it with `kitshop.yml` below. Add the 8 preview files. Register every new file in `plugins/DeluxeMenus/config.yml` under `gui_menus:` (block at the end). Run `/dm reload`. The Essentials kits `scout archer miner warrior knight tank champion legend` must exist (see FCMC_PANEL_SETUP.md §6f; the in-game AI can create them with `/createkit` if the owner empties his inventory). Prices are in coins (PlayerPoints). In the main menu, left-click buys and right-click opens the preview.

Optional: if your DeluxeMenus version supports armor trims (`trim_material` / `trim_pattern`), add them to the preview armor. Trims: scout redstone/sentry, archer emerald/wild, miner copper/shaper, warrior gold/bolt, knight quartz/ward, tank lapis/rib, champion gold/silence, legend redstone/spire.

## kitshop.yml
```yaml
menu_title: '&8&l✦ KIT SHOP ✦'
open_command: kitshop
size: 45
items:
  'filler':
    material: BLACK_STAINED_GLASS_PANE
    slots: [0-8,9,17,18,26,27,35,36-44]
    display_name: ' '
  'info':
    material: NETHER_STAR
    slot: 4
    display_name: '&6&l✦ FireCraft MC Kits ✦'
    lore:
      - '&7Left-click a kit to &abuy&7 it.'
      - '&7Right-click a kit to &epreview&7 it.'
      - ''
      - '&7Your coins: &f%playerpoints_points%'
  'scout':
    material: IRON_SWORD
    slot: 19
    display_name: '&#FF5555&l🗡 SCOUT KIT'
    hide_attributes: true
    lore:
      - '&#FF5555★☆☆ UNCOMMON'
      - '&8&m                        '
      - '&f&lWhat you get:'
      - '&7• &fScout Hood &b(Protection 1, Unbreaking 2)'
      - '&7• &fScout Tunic &b(Protection 1, Unbreaking 2)'
      - '&7• &fScout Pants &b(Protection 1, Unbreaking 2)'
      - '&7• &fScout Boots &b(Protection 1, Unbreaking 2, Feather Falling 2)'
      - '&7• &fScout Dagger &b(Sharpness 1, Unbreaking 2)'
      - '&7• &f4x Golden Apple'
      - '&7• &f+1 more (right-click to preview)'
      - '&8&m                        '
      - '&ePrice: &f50 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
      - '&e▶ Right-click = Preview'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '50'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
        - '[close]'
    left_click_commands:
      - '[console] points take %player_name% 50'
      - '[console] kit scout %player_name%'
      - '[message] &aYou received the Scout Kit!'
      - '[close]'
    right_click_commands:
      - '[openguimenu] kit_scout_preview'
  'archer':
    material: BOW
    slot: 20
    display_name: '&#7CFF4F&l🏹 ARCHER KIT'
    hide_attributes: true
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8&m                        '
      - '&f&lWhat you get:'
      - '&7• &fArcher Helmet &b(Protection 2, Unbreaking 3)'
      - '&7• &fArcher Chestplate &b(Protection 2, Unbreaking 3)'
      - '&7• &fArcher Leggings &b(Protection 2, Unbreaking 3)'
      - '&7• &fArcher Boots &b(Protection 2, Unbreaking 3, Feather Falling 3)'
      - '&7• &fRanger Bow &b(Power 4, Punch 1, Infinity 1, Unbreaking 3)'
      - '&7• &fArcher Blade &b(Sharpness 3, Unbreaking 3)'
      - '&7• &f+5 more (right-click to preview)'
      - '&8&m                        '
      - '&ePrice: &f100 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
      - '&e▶ Right-click = Preview'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '100'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
        - '[close]'
    left_click_commands:
      - '[console] points take %player_name% 100'
      - '[console] kit archer %player_name%'
      - '[message] &aYou received the Archer Kit!'
      - '[close]'
    right_click_commands:
      - '[openguimenu] kit_archer_preview'
  'miner':
    material: DIAMOND_PICKAXE
    slot: 21
    display_name: '&#FFB84F&l⛏ MINER KIT'
    hide_attributes: true
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8&m                        '
      - '&f&lWhat you get:'
      - '&7• &fMiner Helmet &b(Protection 1, Unbreaking 3)'
      - '&7• &fMiner Chestplate &b(Protection 1, Unbreaking 3)'
      - '&7• &fMiner Leggings &b(Protection 1, Unbreaking 3)'
      - '&7• &fMiner Boots &b(Protection 1, Unbreaking 3)'
      - '&7• &fMiner Pickaxe &b(Efficiency 5, Fortune 3, Unbreaking 3)'
      - '&7• &fMiner Axe &b(Efficiency 4, Unbreaking 3)'
      - '&7• &f+3 more (right-click to preview)'
      - '&8&m                        '
      - '&ePrice: &f150 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
      - '&e▶ Right-click = Preview'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '150'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
        - '[close]'
    left_click_commands:
      - '[console] points take %player_name% 150'
      - '[console] kit miner %player_name%'
      - '[message] &aYou received the Miner Kit!'
      - '[close]'
    right_click_commands:
      - '[openguimenu] kit_miner_preview'
  'warrior':
    material: DIAMOND_SWORD
    slot: 22
    display_name: '&#FFAA00&l⚔ WARRIOR KIT'
    hide_attributes: true
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8&m                        '
      - '&f&lWhat you get:'
      - '&7• &fWarrior Helmet &b(Protection 3, Unbreaking 3)'
      - '&7• &fWarrior Chestplate &b(Protection 3, Unbreaking 3)'
      - '&7• &fWarrior Leggings &b(Protection 3, Unbreaking 3)'
      - '&7• &fWarrior Boots &b(Protection 3, Unbreaking 3)'
      - '&7• &fWarrior Blade &b(Sharpness 3, Fire Aspect 1, Unbreaking 3)'
      - '&7• &fWarrior Shield &b(Unbreaking 3)'
      - '&7• &f+3 more (right-click to preview)'
      - '&8&m                        '
      - '&ePrice: &f250 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
      - '&e▶ Right-click = Preview'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '250'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
        - '[close]'
    left_click_commands:
      - '[console] points take %player_name% 250'
      - '[console] kit warrior %player_name%'
      - '[message] &aYou received the Warrior Kit!'
      - '[close]'
    right_click_commands:
      - '[openguimenu] kit_warrior_preview'
  'knight':
    material: DIAMOND_CHESTPLATE
    slot: 23
    display_name: '&#55FFFF&l🛡 KNIGHT KIT'
    hide_attributes: true
    lore:
      - '&#FFD21A&l⭐ MOST POPULAR ⭐'
      - '&#55FFFF★★★ EPIC'
      - '&8&m                        '
      - '&f&lWhat you get:'
      - '&7• &fKnight Helmet &b(Protection 2, Unbreaking 3)'
      - '&7• &fKnight Chestplate &b(Protection 2, Unbreaking 3)'
      - '&7• &fKnight Leggings &b(Protection 2, Unbreaking 3)'
      - '&7• &fKnight Boots &b(Protection 2, Unbreaking 3)'
      - '&7• &fKnight Sword &b(Sharpness 4, Unbreaking 3)'
      - '&7• &fKnight Shield &b(Unbreaking 3)'
      - '&7• &f+4 more (right-click to preview)'
      - '&8&m                        '
      - '&ePrice: &f400 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
      - '&e▶ Right-click = Preview'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '400'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
        - '[close]'
    left_click_commands:
      - '[console] points take %player_name% 400'
      - '[console] kit knight %player_name%'
      - '[message] &aYou received the Knight Kit!'
      - '[close]'
    right_click_commands:
      - '[openguimenu] kit_knight_preview'
  'tank':
    material: SHIELD
    slot: 24
    display_name: '&#1FA2A2&l🗿 TANK KIT'
    hide_attributes: true
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8&m                        '
      - '&f&lWhat you get:'
      - '&7• &fTitan Helmet &b(Protection 3, Unbreaking 3)'
      - '&7• &fTitan Chestplate &b(Protection 3, Unbreaking 3, Thorns 1)'
      - '&7• &fTitan Leggings &b(Protection 3, Unbreaking 3)'
      - '&7• &fTitan Boots &b(Protection 3, Unbreaking 3)'
      - '&7• &fTitan Sword &b(Sharpness 4, Unbreaking 3)'
      - '&7• &fTitan Axe &b(Sharpness 3, Unbreaking 3)'
      - '&7• &f+4 more (right-click to preview)'
      - '&8&m                        '
      - '&ePrice: &f650 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
      - '&e▶ Right-click = Preview'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '650'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
        - '[close]'
    left_click_commands:
      - '[console] points take %player_name% 650'
      - '[console] kit tank %player_name%'
      - '[message] &aYou received the Tank Kit!'
      - '[close]'
    right_click_commands:
      - '[openguimenu] kit_tank_preview'
  'champion':
    material: GOLDEN_HELMET
    slot: 25
    display_name: '&#FFD700&l👑 CHAMPION KIT'
    hide_attributes: true
    lore:
      - '&#55FF55&l✔ BEST VALUE ✔'
      - '&#FFD700★★★★ LEGENDARY'
      - '&8&m                        '
      - '&f&lWhat you get:'
      - '&7• &fChampion Helmet &b(Protection 4, Unbreaking 3, Mending 1, Respiration 3)'
      - '&7• &fChampion Chestplate &b(Protection 4, Unbreaking 3, Mending 1)'
      - '&7• &fChampion Leggings &b(Protection 4, Unbreaking 3, Mending 1)'
      - '&7• &fChampion Boots &b(Protection 4, Unbreaking 3, Mending 1, Feather Falling 4)'
      - '&7• &fChampion Sword &b(Sharpness 5, Fire Aspect 2, Looting 2, Unbreaking 3, Mending 1)'
      - '&7• &fChampion Pickaxe &b(Efficiency 5, Fortune 3, Unbreaking 3, Mending 1)'
      - '&7• &f+6 more (right-click to preview)'
      - '&8&m                        '
      - '&ePrice: &f900 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
      - '&e▶ Right-click = Preview'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '900'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
        - '[close]'
    left_click_commands:
      - '[console] points take %player_name% 900'
      - '[console] kit champion %player_name%'
      - '[message] &aYou received the Champion Kit!'
      - '[close]'
    right_click_commands:
      - '[openguimenu] kit_champion_preview'
  'legend':
    material: NETHERITE_SWORD
    slot: 31
    display_name: '&#FF3B1F&l🔥 LEGEND KIT'
    hide_attributes: true
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8&m                        '
      - '&f&lWhat you get:'
      - '&7• &fLegend Helmet &b(Protection 4, Unbreaking 3, Mending 1, Respiration 3)'
      - '&7• &fLegend Chestplate &b(Protection 4, Unbreaking 3, Mending 1)'
      - '&7• &fLegend Leggings &b(Protection 4, Unbreaking 3, Mending 1)'
      - '&7• &fLegend Boots &b(Protection 4, Unbreaking 3, Mending 1, Feather Falling 4)'
      - '&7• &fLegend Sword &b(Sharpness 5, Fire Aspect 2, Looting 3, Unbreaking 3, Mending 1)'
      - '&7• &fLegend Pickaxe &b(Efficiency 5, Fortune 3, Unbreaking 3, Mending 1)'
      - '&7• &f+5 more (right-click to preview)'
      - '&8&m                        '
      - '&ePrice: &f2500 Coins'
      - '&7Your coins: &f%playerpoints_points%'
      - ''
      - '&a▶ Left-click = Buy'
      - '&e▶ Right-click = Preview'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '2500'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
        - '[close]'
    left_click_commands:
      - '[console] points take %player_name% 2500'
      - '[console] kit legend %player_name%'
      - '[message] &aYou received the Legend Kit!'
      - '[close]'
    right_click_commands:
      - '[openguimenu] kit_legend_preview'
```

## kit_scout_preview.yml
```yaml
menu_title: '&8🗡 SCOUT KIT &7(Preview)'
size: 36
items:
  'filler':
    material: GRAY_STAINED_GLASS_PANE
    slots: [27-35]
    display_name: ' '
  'i0':
    material: LEATHER_HELMET
    slot: 0
    amount: 1
    display_name: '&#FF5555✦ Scout Hood'
    rgb: '204,0,0'
    enchantments:
      - 'protection;1'
      - 'unbreaking;2'
    lore:
      - '&#FF5555★☆☆ UNCOMMON'
      - '&8✦ FireCraft MC • Scout Kit ✦'
  'i1':
    material: LEATHER_CHESTPLATE
    slot: 1
    amount: 1
    display_name: '&#FF5555✦ Scout Tunic'
    rgb: '204,0,0'
    enchantments:
      - 'protection;1'
      - 'unbreaking;2'
    lore:
      - '&#FF5555★☆☆ UNCOMMON'
      - '&8✦ FireCraft MC • Scout Kit ✦'
  'i2':
    material: LEATHER_LEGGINGS
    slot: 2
    amount: 1
    display_name: '&#FF5555✦ Scout Pants'
    rgb: '204,0,0'
    enchantments:
      - 'protection;1'
      - 'unbreaking;2'
    lore:
      - '&#FF5555★☆☆ UNCOMMON'
      - '&8✦ FireCraft MC • Scout Kit ✦'
  'i3':
    material: LEATHER_BOOTS
    slot: 3
    amount: 1
    display_name: '&#FF5555✦ Scout Boots'
    rgb: '204,0,0'
    enchantments:
      - 'protection;1'
      - 'unbreaking;2'
      - 'feather_falling;2'
    lore:
      - '&#FF5555★☆☆ UNCOMMON'
      - '&8✦ FireCraft MC • Scout Kit ✦'
  'i4':
    material: IRON_SWORD
    slot: 4
    amount: 1
    display_name: '&#FF5555✦ Scout Dagger'
    enchantments:
      - 'sharpness;1'
      - 'unbreaking;2'
    lore:
      - '&#FF5555★☆☆ UNCOMMON'
      - '&8✦ FireCraft MC • Scout Kit ✦'
  'i5':
    material: GOLDEN_APPLE
    slot: 5
    amount: 4
    display_name: '&#FF5555✦ Golden Apple'
    lore:
      - '&#FF5555★☆☆ UNCOMMON'
      - '&8✦ FireCraft MC • Scout Kit ✦'
  'i6':
    material: COOKED_BEEF
    slot: 6
    amount: 16
    display_name: '&#FF5555✦ Steak'
    lore:
      - '&#FF5555★☆☆ UNCOMMON'
      - '&8✦ FireCraft MC • Scout Kit ✦'
  'back':
    material: ARROW
    slot: 31
    display_name: '&e⬅ Back to Kit Shop'
    left_click_commands:
      - '[openguimenu] kitshop'
    right_click_commands:
      - '[openguimenu] kitshop'
  'buy':
    material: EMERALD
    slot: 35
    display_name: '&a&lBuy for 50 Coins'
    lore:
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '50'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
    left_click_commands:
      - '[console] points take %player_name% 50'
      - '[console] kit scout %player_name%'
      - '[message] &aYou received the Scout Kit!'
      - '[close]'
```

## kit_archer_preview.yml
```yaml
menu_title: '&8🏹 ARCHER KIT &7(Preview)'
size: 36
items:
  'filler':
    material: GRAY_STAINED_GLASS_PANE
    slots: [27-35]
    display_name: ' '
  'i0':
    material: IRON_HELMET
    slot: 0
    amount: 1
    display_name: '&#7CFF4F✦ Archer Helmet'
    enchantments:
      - 'protection;2'
      - 'unbreaking;3'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i1':
    material: IRON_CHESTPLATE
    slot: 1
    amount: 1
    display_name: '&#7CFF4F✦ Archer Chestplate'
    enchantments:
      - 'protection;2'
      - 'unbreaking;3'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i2':
    material: IRON_LEGGINGS
    slot: 2
    amount: 1
    display_name: '&#7CFF4F✦ Archer Leggings'
    enchantments:
      - 'protection;2'
      - 'unbreaking;3'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i3':
    material: IRON_BOOTS
    slot: 3
    amount: 1
    display_name: '&#7CFF4F✦ Archer Boots'
    enchantments:
      - 'protection;2'
      - 'unbreaking;3'
      - 'feather_falling;3'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i4':
    material: BOW
    slot: 4
    amount: 1
    display_name: '&#7CFF4F✦ Ranger Bow'
    enchantments:
      - 'power;4'
      - 'punch;1'
      - 'infinity;1'
      - 'unbreaking;3'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i5':
    material: IRON_SWORD
    slot: 5
    amount: 1
    display_name: '&#7CFF4F✦ Archer Blade'
    enchantments:
      - 'sharpness;3'
      - 'unbreaking;3'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i6':
    material: IRON_PICKAXE
    slot: 6
    amount: 1
    display_name: '&#7CFF4F✦ Archer Pickaxe'
    enchantments:
      - 'efficiency;3'
      - 'unbreaking;3'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i7':
    material: ARROW
    slot: 7
    amount: 1
    display_name: '&#7CFF4F✦ Arrow'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i8':
    material: SPECTRAL_ARROW
    slot: 8
    amount: 16
    display_name: '&#7CFF4F✦ Glow Arrow'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i9':
    material: GOLDEN_APPLE
    slot: 9
    amount: 8
    display_name: '&#7CFF4F✦ Golden Apple'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'i10':
    material: COOKED_BEEF
    slot: 10
    amount: 32
    display_name: '&#7CFF4F✦ Steak'
    lore:
      - '&#55FF55★★☆ RARE'
      - '&8✦ FireCraft MC • Archer Kit ✦'
  'back':
    material: ARROW
    slot: 31
    display_name: '&e⬅ Back to Kit Shop'
    left_click_commands:
      - '[openguimenu] kitshop'
    right_click_commands:
      - '[openguimenu] kitshop'
  'buy':
    material: EMERALD
    slot: 35
    display_name: '&a&lBuy for 100 Coins'
    lore:
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '100'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
    left_click_commands:
      - '[console] points take %player_name% 100'
      - '[console] kit archer %player_name%'
      - '[message] &aYou received the Archer Kit!'
      - '[close]'
```

## kit_miner_preview.yml
```yaml
menu_title: '&8⛏ MINER KIT &7(Preview)'
size: 36
items:
  'filler':
    material: GRAY_STAINED_GLASS_PANE
    slots: [27-35]
    display_name: ' '
  'i0':
    material: IRON_HELMET
    slot: 0
    amount: 1
    display_name: '&#FFB84F✦ Miner Helmet'
    enchantments:
      - 'protection;1'
      - 'unbreaking;3'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'i1':
    material: IRON_CHESTPLATE
    slot: 1
    amount: 1
    display_name: '&#FFB84F✦ Miner Chestplate'
    enchantments:
      - 'protection;1'
      - 'unbreaking;3'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'i2':
    material: IRON_LEGGINGS
    slot: 2
    amount: 1
    display_name: '&#FFB84F✦ Miner Leggings'
    enchantments:
      - 'protection;1'
      - 'unbreaking;3'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'i3':
    material: IRON_BOOTS
    slot: 3
    amount: 1
    display_name: '&#FFB84F✦ Miner Boots'
    enchantments:
      - 'protection;1'
      - 'unbreaking;3'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'i4':
    material: DIAMOND_PICKAXE
    slot: 4
    amount: 1
    display_name: '&#FFB84F✦ Miner Pickaxe'
    enchantments:
      - 'efficiency;5'
      - 'fortune;3'
      - 'unbreaking;3'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'i5':
    material: DIAMOND_AXE
    slot: 5
    amount: 1
    display_name: '&#FFB84F✦ Miner Axe'
    enchantments:
      - 'efficiency;4'
      - 'unbreaking;3'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'i6':
    material: DIAMOND_SHOVEL
    slot: 6
    amount: 1
    display_name: '&#FFB84F✦ Miner Shovel'
    enchantments:
      - 'efficiency;4'
      - 'unbreaking;3'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'i7':
    material: GOLDEN_CARROT
    slot: 7
    amount: 16
    display_name: '&#FFB84F✦ Golden Carrot'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'i8':
    material: TORCH
    slot: 8
    amount: 32
    display_name: '&#FFB84F✦ Torch'
    lore:
      - '&#FFB84F★★☆ RARE'
      - '&8✦ FireCraft MC • Miner Kit ✦'
  'back':
    material: ARROW
    slot: 31
    display_name: '&e⬅ Back to Kit Shop'
    left_click_commands:
      - '[openguimenu] kitshop'
    right_click_commands:
      - '[openguimenu] kitshop'
  'buy':
    material: EMERALD
    slot: 35
    display_name: '&a&lBuy for 150 Coins'
    lore:
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '150'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
    left_click_commands:
      - '[console] points take %player_name% 150'
      - '[console] kit miner %player_name%'
      - '[message] &aYou received the Miner Kit!'
      - '[close]'
```

## kit_warrior_preview.yml
```yaml
menu_title: '&8⚔ WARRIOR KIT &7(Preview)'
size: 36
items:
  'filler':
    material: GRAY_STAINED_GLASS_PANE
    slots: [27-35]
    display_name: ' '
  'i0':
    material: IRON_HELMET
    slot: 0
    amount: 1
    display_name: '&#FFAA00✦ Warrior Helmet'
    enchantments:
      - 'protection;3'
      - 'unbreaking;3'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'i1':
    material: IRON_CHESTPLATE
    slot: 1
    amount: 1
    display_name: '&#FFAA00✦ Warrior Chestplate'
    enchantments:
      - 'protection;3'
      - 'unbreaking;3'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'i2':
    material: IRON_LEGGINGS
    slot: 2
    amount: 1
    display_name: '&#FFAA00✦ Warrior Leggings'
    enchantments:
      - 'protection;3'
      - 'unbreaking;3'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'i3':
    material: IRON_BOOTS
    slot: 3
    amount: 1
    display_name: '&#FFAA00✦ Warrior Boots'
    enchantments:
      - 'protection;3'
      - 'unbreaking;3'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'i4':
    material: DIAMOND_SWORD
    slot: 4
    amount: 1
    display_name: '&#FFAA00✦ Warrior Blade'
    enchantments:
      - 'sharpness;3'
      - 'fire_aspect;1'
      - 'unbreaking;3'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'i5':
    material: SHIELD
    slot: 5
    amount: 1
    display_name: '&#FFAA00✦ Warrior Shield'
    enchantments:
      - 'unbreaking;3'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'i6':
    material: GOLDEN_APPLE
    slot: 6
    amount: 12
    display_name: '&#FFAA00✦ Golden Apple'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'i7':
    material: ENDER_PEARL
    slot: 7
    amount: 4
    display_name: '&#FFAA00✦ Ender Pearl'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'i8':
    material: COOKED_BEEF
    slot: 8
    amount: 32
    display_name: '&#FFAA00✦ Steak'
    lore:
      - '&#FFAA00★★★ EPIC'
      - '&8✦ FireCraft MC • Warrior Kit ✦'
  'back':
    material: ARROW
    slot: 31
    display_name: '&e⬅ Back to Kit Shop'
    left_click_commands:
      - '[openguimenu] kitshop'
    right_click_commands:
      - '[openguimenu] kitshop'
  'buy':
    material: EMERALD
    slot: 35
    display_name: '&a&lBuy for 250 Coins'
    lore:
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '250'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
    left_click_commands:
      - '[console] points take %player_name% 250'
      - '[console] kit warrior %player_name%'
      - '[message] &aYou received the Warrior Kit!'
      - '[close]'
```

## kit_knight_preview.yml
```yaml
menu_title: '&8🛡 KNIGHT KIT &7(Preview)'
size: 36
items:
  'filler':
    material: GRAY_STAINED_GLASS_PANE
    slots: [27-35]
    display_name: ' '
  'i0':
    material: DIAMOND_HELMET
    slot: 0
    amount: 1
    display_name: '&#55FFFF✦ Knight Helmet'
    enchantments:
      - 'protection;2'
      - 'unbreaking;3'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i1':
    material: DIAMOND_CHESTPLATE
    slot: 1
    amount: 1
    display_name: '&#55FFFF✦ Knight Chestplate'
    enchantments:
      - 'protection;2'
      - 'unbreaking;3'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i2':
    material: DIAMOND_LEGGINGS
    slot: 2
    amount: 1
    display_name: '&#55FFFF✦ Knight Leggings'
    enchantments:
      - 'protection;2'
      - 'unbreaking;3'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i3':
    material: DIAMOND_BOOTS
    slot: 3
    amount: 1
    display_name: '&#55FFFF✦ Knight Boots'
    enchantments:
      - 'protection;2'
      - 'unbreaking;3'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i4':
    material: DIAMOND_SWORD
    slot: 4
    amount: 1
    display_name: '&#55FFFF✦ Knight Sword'
    enchantments:
      - 'sharpness;4'
      - 'unbreaking;3'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i5':
    material: SHIELD
    slot: 5
    amount: 1
    display_name: '&#55FFFF✦ Knight Shield'
    enchantments:
      - 'unbreaking;3'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i6':
    material: DIAMOND_PICKAXE
    slot: 6
    amount: 1
    display_name: '&#55FFFF✦ Knight Pickaxe'
    enchantments:
      - 'efficiency;4'
      - 'unbreaking;3'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i7':
    material: GOLDEN_APPLE
    slot: 7
    amount: 16
    display_name: '&#55FFFF✦ Golden Apple'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i8':
    material: ENDER_PEARL
    slot: 8
    amount: 8
    display_name: '&#55FFFF✦ Ender Pearl'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'i9':
    material: COOKED_BEEF
    slot: 9
    amount: 32
    display_name: '&#55FFFF✦ Steak'
    lore:
      - '&#55FFFF★★★ EPIC'
      - '&8✦ FireCraft MC • Knight Kit ✦'
  'back':
    material: ARROW
    slot: 31
    display_name: '&e⬅ Back to Kit Shop'
    left_click_commands:
      - '[openguimenu] kitshop'
    right_click_commands:
      - '[openguimenu] kitshop'
  'buy':
    material: EMERALD
    slot: 35
    display_name: '&a&lBuy for 400 Coins'
    lore:
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '400'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
    left_click_commands:
      - '[console] points take %player_name% 400'
      - '[console] kit knight %player_name%'
      - '[message] &aYou received the Knight Kit!'
      - '[close]'
```

## kit_tank_preview.yml
```yaml
menu_title: '&8🗿 TANK KIT &7(Preview)'
size: 36
items:
  'filler':
    material: GRAY_STAINED_GLASS_PANE
    slots: [27-35]
    display_name: ' '
  'i0':
    material: DIAMOND_HELMET
    slot: 0
    amount: 1
    display_name: '&#1FA2A2✦ Titan Helmet'
    enchantments:
      - 'protection;3'
      - 'unbreaking;3'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i1':
    material: DIAMOND_CHESTPLATE
    slot: 1
    amount: 1
    display_name: '&#1FA2A2✦ Titan Chestplate'
    enchantments:
      - 'protection;3'
      - 'unbreaking;3'
      - 'thorns;1'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i2':
    material: DIAMOND_LEGGINGS
    slot: 2
    amount: 1
    display_name: '&#1FA2A2✦ Titan Leggings'
    enchantments:
      - 'protection;3'
      - 'unbreaking;3'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i3':
    material: DIAMOND_BOOTS
    slot: 3
    amount: 1
    display_name: '&#1FA2A2✦ Titan Boots'
    enchantments:
      - 'protection;3'
      - 'unbreaking;3'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i4':
    material: DIAMOND_SWORD
    slot: 4
    amount: 1
    display_name: '&#1FA2A2✦ Titan Sword'
    enchantments:
      - 'sharpness;4'
      - 'unbreaking;3'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i5':
    material: DIAMOND_AXE
    slot: 5
    amount: 1
    display_name: '&#1FA2A2✦ Titan Axe'
    enchantments:
      - 'sharpness;3'
      - 'unbreaking;3'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i6':
    material: SHIELD
    slot: 6
    amount: 1
    display_name: '&#1FA2A2✦ Titan Shield'
    enchantments:
      - 'unbreaking;3'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i7':
    material: GOLDEN_APPLE
    slot: 7
    amount: 24
    display_name: '&#1FA2A2✦ Golden Apple'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i8':
    material: TOTEM_OF_UNDYING
    slot: 8
    amount: 1
    display_name: '&#1FA2A2✦ Totem of Undying'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'i9':
    material: COOKED_BEEF
    slot: 9
    amount: 64
    display_name: '&#1FA2A2✦ Steak'
    lore:
      - '&#5CE1E6★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Tank Kit ✦'
  'back':
    material: ARROW
    slot: 31
    display_name: '&e⬅ Back to Kit Shop'
    left_click_commands:
      - '[openguimenu] kitshop'
    right_click_commands:
      - '[openguimenu] kitshop'
  'buy':
    material: EMERALD
    slot: 35
    display_name: '&a&lBuy for 650 Coins'
    lore:
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '650'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
    left_click_commands:
      - '[console] points take %player_name% 650'
      - '[console] kit tank %player_name%'
      - '[message] &aYou received the Tank Kit!'
      - '[close]'
```

## kit_champion_preview.yml
```yaml
menu_title: '&8👑 CHAMPION KIT &7(Preview)'
size: 36
items:
  'filler':
    material: GRAY_STAINED_GLASS_PANE
    slots: [27-35]
    display_name: ' '
  'i0':
    material: DIAMOND_HELMET
    slot: 0
    amount: 1
    display_name: '&#FFD700✦ Champion Helmet'
    enchantments:
      - 'protection;4'
      - 'unbreaking;3'
      - 'mending;1'
      - 'respiration;3'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i1':
    material: DIAMOND_CHESTPLATE
    slot: 1
    amount: 1
    display_name: '&#FFD700✦ Champion Chestplate'
    enchantments:
      - 'protection;4'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i2':
    material: DIAMOND_LEGGINGS
    slot: 2
    amount: 1
    display_name: '&#FFD700✦ Champion Leggings'
    enchantments:
      - 'protection;4'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i3':
    material: DIAMOND_BOOTS
    slot: 3
    amount: 1
    display_name: '&#FFD700✦ Champion Boots'
    enchantments:
      - 'protection;4'
      - 'unbreaking;3'
      - 'mending;1'
      - 'feather_falling;4'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i4':
    material: DIAMOND_SWORD
    slot: 4
    amount: 1
    display_name: '&#FFD700✦ Champion Sword'
    enchantments:
      - 'sharpness;5'
      - 'fire_aspect;2'
      - 'looting;2'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i5':
    material: DIAMOND_PICKAXE
    slot: 5
    amount: 1
    display_name: '&#FFD700✦ Champion Pickaxe'
    enchantments:
      - 'efficiency;5'
      - 'fortune;3'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i6':
    material: DIAMOND_AXE
    slot: 6
    amount: 1
    display_name: '&#FFD700✦ Champion Axe'
    enchantments:
      - 'efficiency;5'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i7':
    material: DIAMOND_SHOVEL
    slot: 7
    amount: 1
    display_name: '&#FFD700✦ Champion Shovel'
    enchantments:
      - 'efficiency;5'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i8':
    material: GOLDEN_APPLE
    slot: 8
    amount: 32
    display_name: '&#FFD700✦ Golden Apple'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i9':
    material: ENCHANTED_GOLDEN_APPLE
    slot: 9
    amount: 1
    display_name: '&#FFD700✦ Enchanted Golden Apple'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i10':
    material: TOTEM_OF_UNDYING
    slot: 10
    amount: 1
    display_name: '&#FFD700✦ Totem of Undying'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'i11':
    material: COOKED_BEEF
    slot: 11
    amount: 64
    display_name: '&#FFD700✦ Steak'
    lore:
      - '&#FFD700★★★★ LEGENDARY'
      - '&8✦ FireCraft MC • Champion Kit ✦'
  'back':
    material: ARROW
    slot: 31
    display_name: '&e⬅ Back to Kit Shop'
    left_click_commands:
      - '[openguimenu] kitshop'
    right_click_commands:
      - '[openguimenu] kitshop'
  'buy':
    material: EMERALD
    slot: 35
    display_name: '&a&lBuy for 900 Coins'
    lore:
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '900'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
    left_click_commands:
      - '[console] points take %player_name% 900'
      - '[console] kit champion %player_name%'
      - '[message] &aYou received the Champion Kit!'
      - '[close]'
```

## kit_legend_preview.yml
```yaml
menu_title: '&8🔥 LEGEND KIT &7(Preview)'
size: 36
items:
  'filler':
    material: GRAY_STAINED_GLASS_PANE
    slots: [27-35]
    display_name: ' '
  'i0':
    material: NETHERITE_HELMET
    slot: 0
    amount: 1
    display_name: '&#FF3B1F✦ Legend Helmet'
    enchantments:
      - 'protection;4'
      - 'unbreaking;3'
      - 'mending;1'
      - 'respiration;3'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i1':
    material: NETHERITE_CHESTPLATE
    slot: 1
    amount: 1
    display_name: '&#FF3B1F✦ Legend Chestplate'
    enchantments:
      - 'protection;4'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i2':
    material: NETHERITE_LEGGINGS
    slot: 2
    amount: 1
    display_name: '&#FF3B1F✦ Legend Leggings'
    enchantments:
      - 'protection;4'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i3':
    material: NETHERITE_BOOTS
    slot: 3
    amount: 1
    display_name: '&#FF3B1F✦ Legend Boots'
    enchantments:
      - 'protection;4'
      - 'unbreaking;3'
      - 'mending;1'
      - 'feather_falling;4'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i4':
    material: NETHERITE_SWORD
    slot: 4
    amount: 1
    display_name: '&#FF3B1F✦ Legend Sword'
    enchantments:
      - 'sharpness;5'
      - 'fire_aspect;2'
      - 'looting;3'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i5':
    material: NETHERITE_PICKAXE
    slot: 5
    amount: 1
    display_name: '&#FF3B1F✦ Legend Pickaxe'
    enchantments:
      - 'efficiency;5'
      - 'fortune;3'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i6':
    material: NETHERITE_AXE
    slot: 6
    amount: 1
    display_name: '&#FF3B1F✦ Legend Axe'
    enchantments:
      - 'efficiency;5'
      - 'unbreaking;3'
      - 'mending;1'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i7':
    material: GOLDEN_APPLE
    slot: 7
    amount: 64
    display_name: '&#FF3B1F✦ Golden Apple'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i8':
    material: ENCHANTED_GOLDEN_APPLE
    slot: 8
    amount: 2
    display_name: '&#FF3B1F✦ Enchanted Golden Apple'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i9':
    material: TOTEM_OF_UNDYING
    slot: 9
    amount: 2
    display_name: '&#FF3B1F✦ Totem of Undying'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'i10':
    material: COOKED_BEEF
    slot: 10
    amount: 64
    display_name: '&#FF3B1F✦ Steak'
    lore:
      - '&#FF9A3C★★★★★ MYTHIC'
      - '&8✦ FireCraft MC • Legend Kit ✦'
  'back':
    material: ARROW
    slot: 31
    display_name: '&e⬅ Back to Kit Shop'
    left_click_commands:
      - '[openguimenu] kitshop'
    right_click_commands:
      - '[openguimenu] kitshop'
  'buy':
    material: EMERALD
    slot: 35
    display_name: '&a&lBuy for 2500 Coins'
    lore:
      - '&7Your coins: &f%playerpoints_points%'
    left_click_requirement:
      requirements:
        coins:
          type: '>='
          input: '%playerpoints_points%'
          output: '2500'
      deny_commands:
        - '[message] &cNot enough coins! Buy coins on the website.'
    left_click_commands:
      - '[console] points take %player_name% 2500'
      - '[console] kit legend %player_name%'
      - '[message] &aYou received the Legend Kit!'
      - '[close]'
```

## config.yml (DeluxeMenus) — `gui_menus:` registration
```yaml
gui_menus:
  kitshop:
    file: kitshop.yml
  kit_scout_preview:
    file: kit_scout_preview.yml
  kit_archer_preview:
    file: kit_archer_preview.yml
  kit_miner_preview:
    file: kit_miner_preview.yml
  kit_warrior_preview:
    file: kit_warrior_preview.yml
  kit_knight_preview:
    file: kit_knight_preview.yml
  kit_tank_preview:
    file: kit_tank_preview.yml
  kit_champion_preview:
    file: kit_champion_preview.yml
  kit_legend_preview:
    file: kit_legend_preview.yml
```
