// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.GuiManager
class GuiManager {
    java.util.Map<java.util.UUID, org.bukkit.inventory.Inventory> RECIPE_GUI_MAP;
    java.util.Map<java.util.UUID, org.bukkit.inventory.Inventory> REVIVE_BEACON_GUI_MAP;
    java.util.Map<java.util.UUID, org.bukkit.Location> REVIVE_BEACON_INVENTORY_LOCATIONS;
    java.util.Map<java.util.UUID, org.bukkit.inventory.Inventory> REVIVE_GUI_MAP;

    private GuiManager() {
    }

    private static void addNavbar(Inventory inventory, int page, boolean addBackButton, boolean addNextButton) {
        inventory.setItem(49, CustomItemManager.createCloseItem());
        ItemStack glass = new CustomItem(Material.GRAY_STAINED_GLASS_PANE).setName("&r ").makeForbidden().getItemStack();
        int[] glassSlots = {45, 47, 48, 50, 51, 53};
        for (int slot : glassSlots) {
            inventory.setItem(slot, glass);
        }
        if (addBackButton) {
            inventory.setItem(46, CustomItemManager.createBackItem(page - 1));
        } else {
            inventory.setItem(46, glass);
        }
        if (addNextButton) {
            inventory.setItem(52, CustomItemManager.createNextItem(page + 1));
        } else {
            inventory.setItem(52, glass);
        }
    }

    private static Inventory getReviveInventory(List<UUID> eliminatedPlayers, int page, LifeStealZ plugin) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder) null, 54, MessageUtils.getAndFormatMsg(false, "reviveTitle", "&8Revive a player", new MessageUtils.Replaceable[0]));
        if (page < 1) {
            page = 1;
        }
        int startIndex = (page - 1) * 45;
        int endIndex = Math.min(page * 45, eliminatedPlayers.size());
        for (int i = startIndex; i < endIndex; i++) {
            UUID eliminatedPlayerUUID = eliminatedPlayers.get(i);
            if (eliminatedPlayerUUID != null) {
                if (plugin.hasGeyser() && plugin.getGeyserPlayerFile().isPlayerStored(eliminatedPlayerUUID)) {
                    inventory.addItem(new ItemStack[]{new CustomItem(CustomItemManager.getBedrockPlayerHead(eliminatedPlayerUUID)).makeForbidden().getItemStack()});
                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(eliminatedPlayerUUID);
                    inventory.addItem(new ItemStack[]{new CustomItem(CustomItemManager.getPlayerHead(offlinePlayer)).makeForbidden().getItemStack()});
                }
            }
        }
        addNavbar(inventory, page, page > 1, endIndex < eliminatedPlayers.size());
        return inventory;
    }

    // rea: m31clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 2
    //     .max locals 0
    // 
    //     .line 20
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putstatic com/zetaplugins/lifestealz/util/GuiManager REVIVE_GUI_MAP Ljava/util/Map;
    //     .line 21
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putstatic com/zetaplugins/lifestealz/util/GuiManager REVIVE_BEACON_GUI_MAP Ljava/util/Map;
    //     .line 22
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putstatic com/zetaplugins/lifestealz/util/GuiManager RECIPE_GUI_MAP Ljava/util/Map;
    //     .line 23
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putstatic com/zetaplugins/lifestealz/util/GuiManager REVIVE_BEACON_INVENTORY_LOCATIONS Ljava/util/Map;
    //     return
    // .end method

    public static void openReviveBeaconGui(Player player, int page, LifeStealZ plugin, Location beaconLocation) {
        List<UUID> eliminatedPlayers = plugin.getStorage().getEliminatedPlayers();
        Inventory inventory = getReviveInventory(eliminatedPlayers, page, plugin);
        player.openInventory(inventory);
        REVIVE_BEACON_GUI_MAP.put(player.getUniqueId(), inventory);
        REVIVE_BEACON_INVENTORY_LOCATIONS.put(player.getUniqueId(), beaconLocation);
    }

    public static void openReviveGui(Player player, int page) {
        List<UUID> eliminatedPlayers = LifeStealZ.getInstance().getStorage().getEliminatedPlayers();
        Inventory inventory = getReviveInventory(eliminatedPlayers, page, LifeStealZ.getInstance());
        player.openInventory(inventory);
        REVIVE_GUI_MAP.put(player.getUniqueId(), inventory);
    }

}
