package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PapiExpansion extends PlaceholderExpansion {
   private final LifeStealZ plugin;

   public PapiExpansion(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @NotNull
   public String getAuthor() {
      return "Kartoffelchipss";
   }

   @NotNull
   public String getIdentifier() {
      return "lifestealz";
   }

   @NotNull
   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onRequest(OfflinePlayer player, @NotNull String identifier) {
      if (player != null && player.getPlayer() != null) {
         switch (identifier) {
            case "name":
               return player.getName();
            case "hearts": {
               Player onlinePlayer = player.getPlayer();
               if (onlinePlayer != null) {
                  AttributeInstance attribute = onlinePlayer.getAttribute(Attribute.MAX_HEALTH);
                  if (attribute != null) {
                     return String.valueOf((int)(attribute.getBaseValue() / 2.0));
                  }
               }

               PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
               return String.valueOf((int)(playerData.getMaxHealth() / 2.0));
            }
            case "revived": {
               PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
               return String.valueOf(playerData.getHasBeenRevived());
            }
            case "health":
               return String.valueOf((int)(player.getPlayer().getHealth() / 2.0));
            case "maxhearts":
               Player onlinePlayer = player.getPlayer();
               if (onlinePlayer == null) {
                  return String.valueOf(this.plugin.getConfig().getInt("maxHearts"));
               }

               return String.valueOf((int)(MaxHeartsManager.getMaxHearts(onlinePlayer, this.plugin.getConfig()) / 2.0));
            case "maxrevives":
               return String.valueOf(this.plugin.getConfig().getInt("maxRevives"));
            case "craftedhearts": {
               PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
               return String.valueOf(playerData.getCraftedHearts());
            }
            case "craftedrevives": {
               PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
               return String.valueOf(playerData.getCraftedRevives());
            }
            case "isInGracePeriod":
               GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
               if (!gracePeriodManager.isEnabled()) {
                  return "false";
               }

               return String.valueOf(gracePeriodManager.isInGracePeriod(player.getPlayer()));
            case "gracePeriodRemaining":
               GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
               if (!gracePeriodManager.isEnabled()) {
                  return "-1";
               }

               return TimeFormatter.formatDuration((long)gracePeriodManager.getGracePeriodRemaining(player.getPlayer()).orElse(0).intValue());
            case "hasBypass":
               return String.valueOf(this.plugin.getBypassManager().hasBypass(player.getPlayer()));
            case "heartCooldown":
               long heartCooldownTime = this.plugin.getConfig().getLong("heartCooldown");
               long now = System.currentTimeMillis();
               long lastHeartUse = CooldownManager.lastHeartUse.getOrDefault(player.getUniqueId(), 0L);
               long timeLeft = lastHeartUse + heartCooldownTime - now;
               return TimeFormatter.formatDuration(timeLeft);
            default:
               return "InvalidPlaceholder";
         }
      } else {
         return "PlayerNotFound";
      }
   }
}
