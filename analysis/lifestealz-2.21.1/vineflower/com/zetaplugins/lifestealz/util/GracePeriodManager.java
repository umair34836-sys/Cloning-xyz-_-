package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class GracePeriodManager {
   private final LifeStealZ plugin;

   public GracePeriodManager(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public GracePeriodManager.GracePeriodConfig getConfig() {
      return new GracePeriodManager.GracePeriodConfig(this.plugin);
   }

   public boolean isEnabled() {
      return this.getConfig().isEnabled();
   }

   public boolean isInGracePeriod(OfflinePlayer player) {
      if (!this.isEnabled()) {
         return false;
      } else {
         PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
         if (playerData == null) {
            return false;
         } else {
            long firstJoin = playerData.getFirstJoin();
            long now = System.currentTimeMillis();
            long gracePeriodDuration = (long)this.getConfig().getDuration() * 1000L;
            return now - firstJoin < gracePeriodDuration;
         }
      }
   }

   public Optional<Integer> getGracePeriodRemaining(Player player) {
      if (!this.isEnabled()) {
         return Optional.empty();
      } else {
         PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
         if (playerData == null) {
            return Optional.empty();
         } else {
            long firstJoin = playerData.getFirstJoin();
            long now = System.currentTimeMillis();
            long gracePeriodDuration = (long)this.getConfig().getDuration() * 1000L;
            long remaining = gracePeriodDuration - (now - firstJoin);
            return remaining < 0L ? Optional.empty() : Optional.of((int)remaining);
         }
      }
   }

   public void startGracePeriod(final Player player) {
      if (this.isEnabled()) {
         for (String command : this.getConfig().getStartCommands()) {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
         }

         long gracePeriodDuration = (long)this.getConfig().getDuration() * 20L;
         (new BukkitRunnable() {
            public void run() {
               GracePeriodManager.this.endGracePeriod(player);
            }
         }).runTaskLater(this.plugin, gracePeriodDuration);
      }
   }

   public void endGracePeriod(Player player) {
      if (this.isEnabled()) {
         if (this.getConfig().shouldAnnounce()) {
            Component endMessage = MessageUtils.getAndFormatMsg(true, "gracePeriodEnd", "&7The grace period has ended!");
            player.sendMessage(endMessage);
         }

         if (this.getConfig().shouldPlaySound()) {
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 500.0F, 1.0F);
         }

         for (String command : this.getConfig().getEndCommands()) {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
         }
      }
   }

   public boolean skipGracePeriod(OfflinePlayer player) {
      if (!this.isEnabled()) {
         return false;
      } else if (!this.isInGracePeriod(player)) {
         return false;
      } else {
         PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
         if (playerData == null) {
            return false;
         } else {
            playerData.setFirstJoin(System.currentTimeMillis() - (long)this.getConfig().getDuration() * 1000L);
            this.plugin.getStorage().save(playerData);

            for (String command : this.getConfig().getEndCommands()) {
               this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
            }

            return true;
         }
      }
   }

   public boolean resetGracePeriod(OfflinePlayer player) {
      if (!this.isEnabled()) {
         return false;
      } else {
         PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
         if (playerData == null) {
            return false;
         } else {
            playerData.setFirstJoin(System.currentTimeMillis());
            this.plugin.getStorage().save(playerData);

            for (String command : this.getConfig().getStartCommands()) {
               this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
            }

            return true;
         }
      }
   }

   public static class GracePeriodConfig {
      private final LifeStealZ plugin;

      public GracePeriodConfig(LifeStealZ plugin) {
         this.plugin = plugin;
      }

      public boolean isEnabled() {
         return this.plugin.getConfig().getBoolean("gracePeriod.enabled");
      }

      public int getDuration() {
         return this.plugin.getConfig().getInt("gracePeriod.duration");
      }

      public boolean shouldAnnounce() {
         return this.plugin.getConfig().getBoolean("gracePeriod.announce");
      }

      public boolean shouldPlaySound() {
         return this.plugin.getConfig().getBoolean("gracePeriod.playSound");
      }

      public boolean damageFromPlayers() {
         return this.plugin.getConfig().getBoolean("gracePeriod.damageFromPlayers");
      }

      public boolean damageToPlayers() {
         return this.plugin.getConfig().getBoolean("gracePeriod.damageToPlayers");
      }

      public boolean useHearts() {
         return this.plugin.getConfig().getBoolean("gracePeriod.useHearts");
      }

      public boolean looseHearts() {
         return this.plugin.getConfig().getBoolean("gracePeriod.looseHearts");
      }

      public boolean gainHearts() {
         return this.plugin.getConfig().getBoolean("gracePeriod.gainHearts");
      }

      public List<String> getStartCommands() {
         return this.plugin.getConfig().getStringList("gracePeriod.startCommands");
      }

      public List<String> getEndCommands() {
         return this.plugin.getConfig().getStringList("gracePeriod.endCommands");
      }
   }
}
