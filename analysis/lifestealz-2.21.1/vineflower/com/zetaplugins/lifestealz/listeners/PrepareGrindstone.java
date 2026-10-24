package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

@AutoRegisterListener
public final class PrepareGrindstone implements Listener {
   @EventHandler
   public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
      GrindstoneInventory inventory = event.getInventory();
      ItemStack[] inputs = inventory.getContents();
      if (this.hasCustomItem(inputs)) {
         inventory.setResult(null);
         event.setResult(null);
      }
   }

   private boolean hasCustomItem(ItemStack[] items) {
      for (ItemStack item : items) {
         if (item != null && item.getItemMeta() != null && CustomItemManager.isCustomItem(item)) {
            return true;
         }
      }

      return false;
   }
}
