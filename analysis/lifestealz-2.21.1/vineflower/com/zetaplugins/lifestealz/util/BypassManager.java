package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;
import org.bukkit.entity.Player;

public final class BypassManager {
   private final LifeStealZ plugin;

   public BypassManager(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public BypassManager.BypassConfig getConfig() {
      return new BypassManager.BypassConfig(this.plugin);
   }

   public boolean isEnabled() {
      return this.getConfig().isEnabled();
   }

   public boolean hasBypass(Player player) {
      return this.isEnabled() && player != null ? player.hasPermission("lifestealz.bypass") : false;
   }

   public static class BypassConfig {
      private final LifeStealZ plugin;

      public BypassConfig(LifeStealZ plugin) {
         this.plugin = plugin;
      }

      public boolean isEnabled() {
         return this.plugin.getConfig().getBoolean("bypassPermission.enabled", false);
      }

      public boolean damageFromPlayers() {
         return this.plugin.getConfig().getBoolean("bypassPermission.damageFromPlayers", false);
      }

      public boolean damageToPlayers() {
         return this.plugin.getConfig().getBoolean("bypassPermission.damageToPlayers", false);
      }

      public boolean useHearts() {
         return this.plugin.getConfig().getBoolean("bypassPermission.useHearts", false);
      }

      public boolean looseHearts() {
         return this.plugin.getConfig().getBoolean("bypassPermission.looseHearts", false);
      }

      public boolean gainHearts() {
         return this.plugin.getConfig().getBoolean("bypassPermission.gainHearts", false);
      }
   }
}
