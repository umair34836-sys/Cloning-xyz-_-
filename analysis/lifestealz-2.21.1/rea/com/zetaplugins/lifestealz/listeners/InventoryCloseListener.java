// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.InventoryCloseListener
class InventoryCloseListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public InventoryCloseListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        this.plugin.getRecipeManager().cancelAnimations(event.getInventory());
        if (GuiManager.RECIPE_GUI_MAP.get(playerUUID) != null) {
            GuiManager.RECIPE_GUI_MAP.remove(playerUUID);
        }
        if (GuiManager.REVIVE_BEACON_GUI_MAP.get(playerUUID) != null) {
            GuiManager.REVIVE_BEACON_GUI_MAP.remove(playerUUID);
        }
        if (GuiManager.REVIVE_BEACON_INVENTORY_LOCATIONS.get(playerUUID) != null) {
            GuiManager.REVIVE_BEACON_INVENTORY_LOCATIONS.remove(playerUUID);
        }
        if (GuiManager.REVIVE_GUI_MAP.get(playerUUID) != null) {
            GuiManager.REVIVE_GUI_MAP.remove(playerUUID);
        }
    }

}
