// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.PlayerLoginListener
class PlayerLoginListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public PlayerLoginListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private void kickPlayer(PlayerLoginEvent event) {
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        Component kickmsg = MessageUtils.getAndFormatMsg(false, "eliminatedJoin", "&cYou don't have any hearts left!", new MessageUtils.Replaceable[0]);
        event.kickMessage(kickmsg);
    }

    private PlayerData loadOrCreatePlayerData(Player player, Storage storage, int startHearts) {
        PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(player.getName(), player.getUniqueId());
            playerData.setFirstJoin(System.currentTimeMillis());
            playerData.setMaxHealth(((double) startHearts) * 2.0d);
            storage.save(playerData);
            this.plugin.getGracePeriodManager().startGracePeriod(player);
            this.plugin.getOfflinePlayerCache().addItem(player.getName());
        }
        return playerData;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Storage storage = this.plugin.getStorage();
        PlayerData playerData = loadOrCreatePlayerData(player, storage, this.plugin.getConfig().getInt("startHearts", 10));
        if (shouldKickPlayer(playerData)) {
            kickPlayer(event);
        }
    }

    private boolean shouldKickPlayer(PlayerData playerData) {
        boolean disabledBanOnDeath = this.plugin.getConfig().getBoolean("disablePlayerBanOnElimination");
        double minHearts = this.plugin.getConfig().getInt("minHearts") * 2;
        return playerData.getMaxHealth() <= minHearts && !disabledBanOnDeath;
    }

}
