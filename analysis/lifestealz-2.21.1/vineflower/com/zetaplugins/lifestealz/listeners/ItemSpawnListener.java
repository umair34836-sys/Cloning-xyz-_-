package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@AutoRegisterListener(
   name = "HahahaItemSpawnListenerLol"
)
public class ItemSpawnListener implements Listener {
   @EventHandler
   public void onItemSpawn(ItemSpawnEvent event) {
      Item item = event.getEntity();
      PersistentDataContainer container = item.getItemStack().getItemMeta().getPersistentDataContainer();
      boolean shouldHaveUnlimitedLifetime = container.has(CustomItemManager.DESPAWNABLE_KEY)
         && !Boolean.TRUE.equals(container.get(CustomItemManager.DESPAWNABLE_KEY, PersistentDataType.BOOLEAN));
      boolean shouldBeInvulnerable = container.has(CustomItemManager.INVULNERABLE_KEY)
         && Boolean.TRUE.equals(container.get(CustomItemManager.INVULNERABLE_KEY, PersistentDataType.BOOLEAN));
      if (shouldHaveUnlimitedLifetime) {
         item.setUnlimitedLifetime(true);
      }

      if (shouldBeInvulnerable) {
         item.setInvulnerable(true);
      }
   }
}
