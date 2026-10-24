package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.GuiManager;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

@AutoRegisterListener
public final class InventoryCloseListener implements Listener {
   private final LifeStealZ plugin;

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
