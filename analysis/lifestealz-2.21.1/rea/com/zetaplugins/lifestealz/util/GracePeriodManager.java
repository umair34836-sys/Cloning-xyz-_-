// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.GracePeriodManager
class GracePeriodManager {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public GracePeriodManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    public void endGracePeriod(Player player) {
        if (isEnabled()) {
            if (getConfig().shouldAnnounce()) {
                Component endMessage = MessageUtils.getAndFormatMsg(true, "gracePeriodEnd", "&7The grace period has ended!", new MessageUtils.Replaceable[0]);
                player.sendMessage(endMessage);
            }
            if (getConfig().shouldPlaySound()) {
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 500.0f, 1.0f);
            }
            for (String command : getConfig().getEndCommands()) {
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
            }
        }
    }

    public GracePeriodConfig getConfig() {
        return new GracePeriodConfig(this.plugin);
    }

    public Optional<Integer> getGracePeriodRemaining(Player player) {
        PlayerData playerData;
        if (isEnabled() && (playerData = this.plugin.getStorage().load(player.getUniqueId())) != null) {
            long firstJoin = playerData.getFirstJoin();
            long now = System.currentTimeMillis();
            long gracePeriodDuration = ((long) getConfig().getDuration()) * 1000;
            long remaining = gracePeriodDuration - (now - firstJoin);
            return remaining < 0 ? Optional.empty() : Optional.of(Integer.valueOf((int) remaining));
        }
        return Optional.empty();
    }

    public boolean isEnabled() {
        return getConfig().isEnabled();
    }

    public boolean isInGracePeriod(OfflinePlayer player) {
        PlayerData playerData;
        if (!isEnabled() || (playerData = this.plugin.getStorage().load(player.getUniqueId())) == null) {
            return false;
        }
        long firstJoin = playerData.getFirstJoin();
        long now = System.currentTimeMillis();
        long gracePeriodDuration = ((long) getConfig().getDuration()) * 1000;
        return now - firstJoin < gracePeriodDuration;
    }

    public boolean resetGracePeriod(OfflinePlayer player) {
        PlayerData playerData;
        if (!isEnabled() || (playerData = this.plugin.getStorage().load(player.getUniqueId())) == null) {
            return false;
        }
        playerData.setFirstJoin(System.currentTimeMillis());
        this.plugin.getStorage().save(playerData);
        for (String command : getConfig().getStartCommands()) {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
        }
        return true;
    }

    public boolean skipGracePeriod(OfflinePlayer player) {
        PlayerData playerData;
        if (!isEnabled() || !isInGracePeriod(player) || (playerData = this.plugin.getStorage().load(player.getUniqueId())) == null) {
            return false;
        }
        playerData.setFirstJoin(System.currentTimeMillis() - (((long) getConfig().getDuration()) * 1000));
        this.plugin.getStorage().save(playerData);
        for (String command : getConfig().getEndCommands()) {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
        }
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v13, types: [com.zetaplugins.lifestealz.util.GracePeriodManager$1] */
    public void startGracePeriod(final Player player) {
        if (isEnabled()) {
            for (String command : getConfig().getStartCommands()) {
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
            }
            long gracePeriodDuration = ((long) getConfig().getDuration()) * 20;
            new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.util.GracePeriodManager.1
                public void run() {
                    GracePeriodManager.this.endGracePeriod(player);
                }
            }.runTaskLater(this.plugin, gracePeriodDuration);
        }
    }

    // rea: class com.zetaplugins.lifestealz.util.GracePeriodManager.AnonymousClass1
    class AnonymousClass1 {
        com.zetaplugins.lifestealz.util.GracePeriodManager this$0;
        org.bukkit.entity.Player val$player;

        // rea: <init>(com.zetaplugins.lifestealz.util.GracePeriodManager, org.bukkit.entity.Player): void -> smali (java_body_unavailable)
        // .method <init>(Lcom/zetaplugins/lifestealz/util/GracePeriodManager;Lorg/bukkit/entity/Player;)V
        //     .param final mandated null
        //     .param final synthetic null
        //     .max stack 2
        //     .max locals 3
        // 
        //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/GracePeriodManager$1;
        //     .local 1 "this$0" Lcom/zetaplugins/lifestealz/util/GracePeriodManager;
        //     .line 84
        //     aload 0
        //     aload 1
        //     putfield com/zetaplugins/lifestealz/util/GracePeriodManager$1 this$0 Lcom/zetaplugins/lifestealz/util/GracePeriodManager;
        //     aload 0
        //     aload 2
        //     putfield com/zetaplugins/lifestealz/util/GracePeriodManager$1 val$player Lorg/bukkit/entity/Player;
        //     aload 0
        //     invokespecial org/bukkit/scheduler/BukkitRunnable <init> ()V
        //     return
        // .end method

        /* JADX WARN: Type inference failed for: r0v13, types: [com.zetaplugins.lifestealz.util.GracePeriodManager$1] */
        public void startGracePeriod(final Player player) {
            if (isEnabled()) {
                for (String command : getConfig().getStartCommands()) {
                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
                }
                long gracePeriodDuration = ((long) getConfig().getDuration()) * 20;
                new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.util.GracePeriodManager.1
                    public void run() {
                        GracePeriodManager.this.endGracePeriod(player);
                    }

    }

    // rea: class com.zetaplugins.lifestealz.util.GracePeriodManager.GracePeriodConfig
    class GracePeriodConfig {
        com.zetaplugins.lifestealz.LifeStealZ plugin;

            public GracePeriodConfig(LifeStealZ plugin) {
                this.plugin = plugin;
            }

            public boolean damageFromPlayers() {
                return this.plugin.getConfig().getBoolean("gracePeriod.damageFromPlayers");
            }

            public boolean damageToPlayers() {
                return this.plugin.getConfig().getBoolean("gracePeriod.damageToPlayers");
            }

            public boolean gainHearts() {
                return this.plugin.getConfig().getBoolean("gracePeriod.gainHearts");
            }

            public int getDuration() {
                return this.plugin.getConfig().getInt("gracePeriod.duration");
            }

            public List<String> getEndCommands() {
                return this.plugin.getConfig().getStringList("gracePeriod.endCommands");
            }

            public List<String> getStartCommands() {
                return this.plugin.getConfig().getStringList("gracePeriod.startCommands");
            }

            public boolean isEnabled() {
                return this.plugin.getConfig().getBoolean("gracePeriod.enabled");
            }

            public boolean looseHearts() {
                return this.plugin.getConfig().getBoolean("gracePeriod.looseHearts");
            }

            public boolean shouldAnnounce() {
                return this.plugin.getConfig().getBoolean("gracePeriod.announce");
            }

            public boolean shouldPlaySound() {
                return this.plugin.getConfig().getBoolean("gracePeriod.playSound");
            }

            public boolean useHearts() {
                return this.plugin.getConfig().getBoolean("gracePeriod.useHearts");
            }

    }

}
