package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

@AutoRegisterListener
public final class PrepareItemCraft implements Listener {
   @EventHandler
   public void onPrepareItemCraft(PrepareItemCraftEvent event) {
      CraftingInventory inventory = event.getInventory();
      ItemStack result = inventory.getResult();
      ItemStack[] matrix = inventory.getMatrix();
      if (result != null) {
         if (result.getItemMeta() != null) {
            if (!CustomItemManager.isCustomItem(result)) {
               if (this.matrixHasCustomItem(matrix)) {
                  inventory.setResult(null);
               }
            }
         }
      }
   }

   private boolean matrixHasCustomItem(ItemStack[] matrix) {
      for (ItemStack item : matrix) {
         if (item != null && item.getItemMeta() != null && CustomItemManager.isCustomItem(item)) {
            return true;
         }
      }

      return false;
   }
}
