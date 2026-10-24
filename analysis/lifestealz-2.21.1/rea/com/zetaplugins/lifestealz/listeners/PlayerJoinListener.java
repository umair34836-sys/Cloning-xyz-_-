// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.PlayerJoinListener
class PlayerJoinListener {
    com.zetaplugins.lifestealz.util.geysermc.GeyserManager geyserManager;
    com.zetaplugins.lifestealz.util.geysermc.GeyserPlayerFile geyserPlayerFile;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public PlayerJoinListener(LifeStealZ plugin) {
        this.plugin = plugin;
        this.geyserManager = plugin.getGeyserManager();
        this.geyserPlayerFile = plugin.getGeyserPlayerFile();
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

    private void notifyOpAboutUpdate(Player player) {
        if (player.isOp() && this.plugin.getConfig().getBoolean("checkForUpdates") && this.plugin.getVersionChecker().isNewVersionAvailable()) {
            player.sendMessage(MessageUtils.getAndFormatMsg(true, "newVersionAvailable", "&7A new version of LifeStealZ is available!\\n&c<click:OPEN_URL:https://modrinth.com/plugin/lifestealz/versions>https://modrinth.com/plugin/lifestealz/versions</click>", new MessageUtils.Replaceable[0]));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Storage storage = this.plugin.getStorage();
        if (this.plugin.hasGeyser() && this.geyserManager.isBedrockPlayer(player)) {
            this.geyserPlayerFile.savePlayer(player.getUniqueId(), player.getName());
        }
        PlayerData playerData = loadOrCreatePlayerData(player, storage, this.plugin.getConfig().getInt("startHearts", 10));
        LifeStealZ.setMaxHealth(player, playerData.getMaxHealth());
        notifyOpAboutUpdate(player);
    }

}
