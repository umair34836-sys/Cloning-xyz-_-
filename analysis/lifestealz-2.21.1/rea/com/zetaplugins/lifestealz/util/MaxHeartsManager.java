// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.MaxHeartsManager
class MaxHeartsManager {
/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/util/MaxHeartsManager.class */
public final class MaxHeartsManager {
    private MaxHeartsManager() {
    }

    public static double getMaxHearts(Player player, FileConfiguration config) {
        double configMaxHearts = config.getInt("maxHearts") * 2;
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
                } catch (NumberFormatException e) {
                }
            }
        }
        return highestFound == -1 ? configMaxHearts : highestFound;
    }

}
