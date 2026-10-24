package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.CooldownManager;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

@AutoRegisterListener
public final class PlayerItemPickupListener implements Listener {
   private final LifeStealZ plugin;

   public PlayerItemPickupListener(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onItemPickup(PlayerAttemptPickupItemEvent event) {
      Player player = event.getPlayer();
      ItemStack itemStack = event.getItem().getItemStack();
      if (CustomItemManager.isForbiddenItem(itemStack)) {
         event.getItem().remove();
         event.setCancelled(true);
      } else {
         boolean heartGainCooldownEnabled = this.plugin.getConfig().getBoolean("heartGainCooldown.enabled");
         long heartGainCooldown = this.plugin.getConfig().getLong("heartGainCooldown.cooldown");
         boolean heartGainCooldownPreventPickup = this.plugin.getConfig().getBoolean("heartGainCooldown.preventPickup");
         if (CustomItemManager.isHeartItem(itemStack)) {
            if (heartGainCooldownEnabled
               && heartGainCooldownPreventPickup
               && CooldownManager.lastHeartGain.get(player.getUniqueId()) != null
               && CooldownManager.lastHeartGain.get(player.getUniqueId()) + heartGainCooldown > System.currentTimeMillis()) {
               event.setCancelled(true);
               if (CooldownManager.lastHeartPickupMessage.get(player.getUniqueId()) == null
                  || CooldownManager.lastHeartPickupMessage.get(player.getUniqueId()) + 1000L < System.currentTimeMillis()) {
                  long timeLeft = (CooldownManager.lastHeartGain.get(player.getUniqueId()) + heartGainCooldown - System.currentTimeMillis()) / 1000L;
                  player.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        false,
                        "heartGainCooldown",
                        "&cYou have to wait before gaining another heart!",
                        new MessageUtils.Replaceable("%time%", MessageUtils.formatTime(timeLeft))
                     )
                  );
                  CooldownManager.lastHeartPickupMessage.put(player.getUniqueId(), System.currentTimeMillis());
               }
            } else {
               CooldownManager.lastHeartGain.put(player.getUniqueId(), System.currentTimeMillis());
            }
         }
      }
   }
}
