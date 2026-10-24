package com.zetaplugins.lifestealz.listeners.revivebeacon;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.GuiManager;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.customblocks.CustomBlock;
import com.zetaplugins.lifestealz.util.revive.ReviveTask;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@AutoRegisterListener
public final class ReviveBeaconInteractListener implements Listener {
   private final LifeStealZ plugin;

   public ReviveBeaconInteractListener(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onReviveBeaconInteract(PlayerInteractEvent event) {
      Action action = event.getAction();
      if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
         Block block = event.getClickedBlock();
         if (block != null) {
            if (CustomBlock.REVIVE_BEACON.is(block)) {
               Player player = event.getPlayer();
               event.setCancelled(true);
               ReviveTask reviveTask = this.plugin.getReviveTaskManager().getReviveTask(block.getLocation());
               if (reviveTask != null) {
                  long nowSeconds = System.currentTimeMillis() / 1000L;
                  int secondsLeft = (int)(reviveTask.start() + (long)reviveTask.durationSeconds() - nowSeconds);
                  player.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        true,
                        "beaconInUseInteract",
                        "&7There is already a revive process in progress. Time left: &c%timeLeft% seconds&7.",
                        new MessageUtils.Replaceable("%timeLeft%", String.valueOf(secondsLeft))
                     )
                  );
               } else {
                  GuiManager.openReviveBeaconGui(player, 1, this.plugin, block.getLocation());
               }
            }
         }
      }
   }
}
