package com.zetaplugins.lifestealz.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public final class MaxHeartsManager {
   private MaxHeartsManager() {
   }

   public static double getMaxHearts(Player player, FileConfiguration config) {
      double configMaxHearts = (double)(config.getInt("maxHearts") * 2);
      int highestFound = -1;

      for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
         String perm = permInfo.getPermission();
         if (perm.startsWith("lifestealz.maxhearts.")) {
            try {
               String numberPart = perm.substring("lifestealz.maxhearts.".length());
               int hearts = Integer.parseInt(numberPart) * 2;
               if (hearts > highestFound) {
                  highestFound = hearts;
               }
            } catch (NumberFormatException var10) {
            }
         }
      }

      return highestFound == -1 ? configMaxHearts : (double)highestFound;
   }
}
