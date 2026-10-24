// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.PapiExpansion
class PapiExpansion {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

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

    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        AttributeInstance attribute;
        if (player == null || player.getPlayer() == null) {
            return "PlayerNotFound";
        }
        switch (identifier) {
            case "name":
                return player.getName();
            case "hearts":
                Player onlinePlayer = player.getPlayer();
                if (onlinePlayer != null && (attribute = onlinePlayer.getAttribute(Attribute.MAX_HEALTH)) != null) {
                    return String.valueOf((int) (attribute.getBaseValue() / 2.0d));
                }
                PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
                return String.valueOf((int) (playerData.getMaxHealth() / 2.0d));
            case "revived":
                PlayerData playerData2 = this.plugin.getStorage().load(player.getUniqueId());
                return String.valueOf(playerData2.getHasBeenRevived());
            case "health":
                return String.valueOf((int) (player.getPlayer().getHealth() / 2.0d));
            case "maxhearts":
                Player onlinePlayer2 = player.getPlayer();
                return onlinePlayer2 == null ? String.valueOf(this.plugin.getConfig().getInt("maxHearts")) : String.valueOf((int) (MaxHeartsManager.getMaxHearts(onlinePlayer2, this.plugin.getConfig()) / 2.0d));
            case "maxrevives":
                return String.valueOf(this.plugin.getConfig().getInt("maxRevives"));
            case "craftedhearts":
                PlayerData playerData3 = this.plugin.getStorage().load(player.getUniqueId());
                return String.valueOf(playerData3.getCraftedHearts());
            case "craftedrevives":
                PlayerData playerData4 = this.plugin.getStorage().load(player.getUniqueId());
                return String.valueOf(playerData4.getCraftedRevives());
            case "isInGracePeriod":
                GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
                return !gracePeriodManager.isEnabled() ? "false" : String.valueOf(gracePeriodManager.isInGracePeriod(player.getPlayer()));
            case "gracePeriodRemaining":
                GracePeriodManager gracePeriodManager2 = this.plugin.getGracePeriodManager();
                return !gracePeriodManager2.isEnabled() ? "-1" : TimeFormatter.formatDuration(gracePeriodManager2.getGracePeriodRemaining(player.getPlayer()).orElse(0).intValue());
            case "hasBypass":
                return String.valueOf(this.plugin.getBypassManager().hasBypass(player.getPlayer()));
            case "heartCooldown":
                long heartCooldownTime = this.plugin.getConfig().getLong("heartCooldown");
                long now = System.currentTimeMillis();
                long lastHeartUse = CooldownManager.lastHeartUse.getOrDefault(player.getUniqueId(), 0L).longValue();
                long timeLeft = (lastHeartUse + heartCooldownTime) - now;
                return TimeFormatter.formatDuration(timeLeft);
            default:
                return "InvalidPlaceholder";
        }
    }

    public boolean persist() {
        return true;
    }

}
