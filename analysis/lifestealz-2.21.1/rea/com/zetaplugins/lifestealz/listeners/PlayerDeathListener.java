// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.PlayerDeathListener
class PlayerDeathListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public PlayerDeathListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private void dropHeartsNaturally(Location location, int amount, ItemStack itemStack) {
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        boolean shouldHaveUnlimitedLifetime = container.has(CustomItemManager.DESPAWNABLE_KEY) && !Boolean.TRUE.equals(container.get(CustomItemManager.DESPAWNABLE_KEY, PersistentDataType.BOOLEAN));
        boolean shouldBeInvulnerable = container.has(CustomItemManager.INVULNERABLE_KEY) && Boolean.TRUE.equals(container.get(CustomItemManager.INVULNERABLE_KEY, PersistentDataType.BOOLEAN));
        for (int i = 0; i < amount; i++) {
            Item item = location.getWorld().dropItemNaturally(location, itemStack);
            if (shouldHaveUnlimitedLifetime) {
                item.setUnlimitedLifetime(true);
            }
            if (shouldBeInvulnerable) {
                item.setInvulnerable(true);
            }
        }
    }

    private String getPlayerIP(Player player) {
        InetSocketAddress inetSocketAddress = player.getAddress();
        if (inetSocketAddress == null) {
            return null;
        }
        InetAddress address = inetSocketAddress.getAddress();
        return address.getHostAddress();
    }

    private boolean handleAntiAltLogic(PlayerDeathEvent event, Player player, @Nullable Player killer) {
        if (killer == null || player.getUniqueId().equals(killer.getUniqueId())) {
            return false;
        }
        String victimIP = getPlayerIP(player);
        String killerIP = getPlayerIP(killer);
        if (victimIP != null && victimIP.equals(killerIP) && this.plugin.getConfig().getBoolean("antiAlt.enabled")) {
            ZPlayerAltKillEvent altEvent = new ZPlayerAltKillEvent(event, killer, victimIP);
            altEvent.setShouldPreventKill(this.plugin.getConfig().getBoolean("antiAlt.preventKill"));
            altEvent.setShouldLogAttempt(this.plugin.getConfig().getBoolean("antiAlt.logAttempt"));
            altEvent.setShouldSendMessage(this.plugin.getConfig().getBoolean("antiAlt.sendMessage"));
            Bukkit.getPluginManager().callEvent(altEvent);
            if (!altEvent.isCancelled()) {
                if (altEvent.isShouldLogAttempt()) {
                    this.plugin.getLogger().info("[ALT WARNING] Player " + killer.getName() + " tried to kill " + player.getName() + " with the same IP address! (Probably an alt account)");
                }
                if (altEvent.isShouldSendMessage()) {
                    killer.sendMessage(altEvent.getWarningMessage());
                }
                for (String command : this.plugin.getConfig().getStringList("antiAlt.commands")) {
                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", killer.getName()));
                }
                return altEvent.isShouldPreventKill();
            }
            return false;
        }
        return false;
    }

    private void handleElimination(PlayerDeathEvent event, Player player, PlayerData playerData, Player killer, boolean isDeathByPlayer, double healthToLoose, boolean preventKillerGain, boolean droppedAtKiller) {
        ZPlayerEliminationEvent eliminationEvent = new ZPlayerEliminationEvent(event, killer);
        eliminationEvent.setShouldBanPlayer(!this.plugin.getConfig().getBoolean("disablePlayerBanOnElimination"));
        eliminationEvent.setShouldAnnounceElimination(this.plugin.getConfig().getBoolean("announceElimination"));
        String messageKey = isDeathByPlayer ? "eliminationAnnouncement" : "eliminateionAnnouncementNature";
        String defaultMessage = isDeathByPlayer ? "&c%player% &7has been eliminated by &c%killer%&7!" : "&c%player% &7has been eliminated!";
        MessageUtils.Replaceable[] replaceableArr = new MessageUtils.Replaceable[2];
        replaceableArr[0] = new MessageUtils.Replaceable("%player%", player.getName());
        replaceableArr[1] = new MessageUtils.Replaceable("%killer%", killer != null ? killer.getName() : "");
        eliminationEvent.setEliminationMessage(MessageUtils.getAndFormatMsg(false, messageKey, defaultMessage, replaceableArr));
        eliminationEvent.setKickMessage(MessageUtils.getAndFormatMsg(false, "eliminatedJoin", "&cYou don't have any hearts left!", new MessageUtils.Replaceable[0]));
        Bukkit.getPluginManager().callEvent(eliminationEvent);
        if (!eliminationEvent.isCancelled()) {
            List<String> elimCommands = this.plugin.getConfig().getStringList("eliminationCommands");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                Iterator it = elimCommands.iterator();
                while (it.hasNext()) {
                    String command = (String) it.next();
                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
                }
            }, 1L);
            boolean heartRewardOnElimination = this.plugin.getConfig().getBoolean("heartRewardOnElimination", true);
            if (heartRewardOnElimination) {
                if (isDeathByPlayer && killer != null) {
                    boolean dropHeartsPlayer = this.plugin.getConfig().getBoolean("dropHeartsPlayer", true);
                    if (!preventKillerGain && !droppedAtKiller && !dropHeartsPlayer) {
                        boolean preventedByCooldown = handleHeartGainCooldown(event, player, killer, healthToLoose);
                        boolean preventedByMax = false;
                        if (!preventedByCooldown) {
                            preventedByMax = handleMaxHeartsLimit(event, player, killer, healthToLoose);
                        }
                        if (!preventedByCooldown && !preventedByMax) {
                            handleKillerHeartGainDirect(killer, healthToLoose);
                        }
                    } else if (droppedAtKiller || dropHeartsPlayer) {
                        dropHeartsNaturally(killer.getLocation(), (int) (healthToLoose / 2.0d), CustomItemManager.createKillHeart());
                    }
                } else if (!isDeathByPlayer) {
                    boolean dropHeartsNatural = this.plugin.getConfig().getBoolean("dropHeartsNatural", true);
                    if (dropHeartsNatural) {
                        dropHeartsNaturally(player.getLocation(), (int) (healthToLoose / 2.0d), CustomItemManager.createNaturalDeathHeart());
                    }
                }
            }
            if (!eliminationEvent.isShouldBanPlayer()) {
                double respawnHP = this.plugin.getConfig().getInt("reviveHearts") * 2;
                playerData.setMaxHealth(respawnHP);
                this.plugin.getStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, respawnHP);
                return;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                if (player.isOnline()) {
                    player.kick(eliminationEvent.getKickMessage());
                }
            }, 1L);
            if (eliminationEvent.isShouldAnnounceElimination()) {
                Bukkit.broadcast(eliminationEvent.getEliminationMessage());
                event.setDeathMessage((String) null);
            }
            WebHookManager webHookManager = this.plugin.getWebHookManager();
            WebHookManager.WebHookType webHookType = WebHookManager.WebHookType.ELIMINATION;
            String[] strArr = new String[2];
            strArr[0] = player.getName();
            strArr[1] = killer != null ? killer.getName() : "";
            webHookManager.sendWebhookMessage(webHookType, strArr);
            playerData.setMaxHealth(0.0d);
            this.plugin.getStorage().save(playerData);
            this.plugin.getEliminatedPlayersCache().addEliminatedPlayer(player.getName());
        }
    }

    private boolean handleHeartGainCooldown(PlayerDeathEvent event, Player player, Player killer, double healthGain) {
        boolean heartGainCooldownEnabled = this.plugin.getConfig().getBoolean("heartGainCooldown.enabled");
        long heartGainCooldown = this.plugin.getConfig().getLong("heartGainCooldown.cooldown");
        if (heartGainCooldownEnabled && CooldownManager.lastHeartGain.get(killer.getUniqueId()) != null && CooldownManager.lastHeartGain.get(killer.getUniqueId()).longValue() + heartGainCooldown > System.currentTimeMillis()) {
            long timeLeft = ((CooldownManager.lastHeartGain.get(killer.getUniqueId()).longValue() + heartGainCooldown) - System.currentTimeMillis()) / 1000;
            ZPlayerHeartGainCooldownEvent cooldownEvent = new ZPlayerHeartGainCooldownEvent(event, killer, timeLeft);
            cooldownEvent.setShouldDropHeartsInstead(this.plugin.getConfig().getBoolean("heartGainCooldown.dropOnCooldown"));
            Bukkit.getPluginManager().callEvent(cooldownEvent);
            if (!cooldownEvent.isCancelled()) {
                killer.sendMessage(cooldownEvent.getCooldownMessage());
                if (cooldownEvent.isShouldDropHeartsInstead()) {
                    dropHeartsNaturally(killer.getLocation(), (int) (healthGain / 2.0d), CustomItemManager.createHeartGainCooldownHeart());
                    return true;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private void handleHeartLoss(PlayerDeathEvent event, Player player, Player killer, PlayerData playerData, boolean isDeathByPlayer) {
        double minHearts = this.plugin.getConfig().getInt("minHearts") * 2;
        double healthPerKill = this.plugin.getConfig().getInt("heartsPerKill") * 2;
        double healthPerNaturalDeath = this.plugin.getConfig().getInt("heartsPerNaturalDeath") * 2;
        double healthToLoose = isDeathByPlayer ? healthPerKill : healthPerNaturalDeath;
        boolean victimHasBypass = restrictedHeartLossByBypass(player);
        boolean killerHasBypass = isDeathByPlayer && killer != null && restrictedHeartGainByBypass(killer);
        if (victimHasBypass || killerHasBypass) {
            ZPlayerBypassDeathEvent bypassEvent = new ZPlayerBypassDeathEvent(event, killer, victimHasBypass, killerHasBypass);
            Bukkit.getPluginManager().callEvent(bypassEvent);
            if (!bypassEvent.isCancelled()) {
                if (victimHasBypass) {
                    player.sendMessage(bypassEvent.getMessageToVictim());
                }
                if (!killerHasBypass || killer == null) {
                    return;
                }
                killer.sendMessage(bypassEvent.getMessageToKiller());
                return;
            }
        }
        boolean victimInGracePeriod = restrictedHeartLossByGracePeriod(player);
        boolean killerInGracePeriod = isDeathByPlayer && restrictedHeartGainByGracePeriod(killer);
        if (victimInGracePeriod || killerInGracePeriod) {
            ZPlayerGracePeriodDeathEvent graceEvent = new ZPlayerGracePeriodDeathEvent(event, killer, victimInGracePeriod, killerInGracePeriod);
            Bukkit.getPluginManager().callEvent(graceEvent);
            if (!graceEvent.isCancelled()) {
                if (victimInGracePeriod) {
                    player.sendMessage(graceEvent.getMessageToVictim());
                }
                if (killerInGracePeriod && killer != null) {
                    killer.sendMessage(graceEvent.getMessageToKiller());
                }
                if (victimInGracePeriod) {
                    return;
                }
            }
        }
        boolean preventKillerGain = false;
        boolean droppedAtKiller = false;
        if (isDeathByPlayer && !killerInGracePeriod && !killerHasBypass) {
            if (handleHeartGainCooldown(event, player, killer, healthToLoose)) {
                preventKillerGain = true;
                if (this.plugin.getConfig().getBoolean("heartGainCooldown.dropOnCooldown")) {
                    droppedAtKiller = true;
                }
            }
            if (handleMaxHeartsLimit(event, player, killer, healthToLoose)) {
                preventKillerGain = true;
                if (this.plugin.getConfig().getBoolean("dropHeartsIfMax")) {
                    droppedAtKiller = true;
                }
            }
        }
        if (playerData.getMaxHealth() - healthToLoose <= minHearts) {
            handleElimination(event, player, playerData, killer, isDeathByPlayer, healthToLoose, preventKillerGain, droppedAtKiller);
        } else if (isDeathByPlayer) {
            handlePvPDeath(event, player, killer, playerData, healthToLoose, preventKillerGain, droppedAtKiller);
        } else {
            handleNaturalDeath(event, player, playerData, healthToLoose);
        }
    }

    private void handleKillerHeartGainDirect(Player killer, double healthGain) {
        PlayerData killerPlayerData = this.plugin.getStorage().load(killer.getUniqueId());
        killerPlayerData.setMaxHealth(killerPlayerData.getMaxHealth() + healthGain);
        this.plugin.getStorage().save(killerPlayerData);
        LifeStealZ.setMaxHealth(killer, killerPlayerData.getMaxHealth());
        killer.setHealth(Math.min(killer.getHealth() + healthGain, killerPlayerData.getMaxHealth()));
        CooldownManager.lastHeartGain.put(killer.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
    }

    private boolean handleMaxHeartsLimit(PlayerDeathEvent event, Player player, Player killer, double healthGain) {
        double maxHearts = MaxHeartsManager.getMaxHearts(killer, this.plugin.getConfig());
        PlayerData killerPlayerData = this.plugin.getStorage().load(killer.getUniqueId());
        if (killerPlayerData.getMaxHealth() + healthGain > maxHearts) {
            ZPlayerMaxHeartsReachedEvent maxHeartsEvent = new ZPlayerMaxHeartsReachedEvent(event, killer, maxHearts);
            maxHeartsEvent.setShouldDropHeartsInstead(this.plugin.getConfig().getBoolean("dropHeartsIfMax"));
            Bukkit.getPluginManager().callEvent(maxHeartsEvent);
            if (!maxHeartsEvent.isCancelled()) {
                if (maxHeartsEvent.isShouldDropHeartsInstead()) {
                    dropHeartsNaturally(killer.getLocation(), (int) (healthGain / 2.0d), CustomItemManager.createMaxHealthHeart());
                    return true;
                }
                killer.sendMessage(maxHeartsEvent.getMaxHeartsMessage());
                return true;
            }
            return false;
        }
        return false;
    }

    private void handleNaturalDeath(PlayerDeathEvent event, Player player, PlayerData playerData, double healthToLoose) {
        ZPlayerNaturalDeathEvent naturalEvent = new ZPlayerNaturalDeathEvent(event, healthToLoose);
        naturalEvent.setShouldDropHearts(this.plugin.getConfig().getBoolean("dropHeartsNatural"));
        Bukkit.getPluginManager().callEvent(naturalEvent);
        if (!naturalEvent.isCancelled()) {
            if (naturalEvent.getHeartsToLose() > 0.0d) {
                playerData.setMaxHealth(playerData.getMaxHealth() - naturalEvent.getHeartsToLose());
                this.plugin.getStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, playerData.getMaxHealth());
            }
            if (naturalEvent.isShouldDropHearts()) {
                dropHeartsNaturally(player.getLocation(), (int) (naturalEvent.getHeartsToLose() / 2.0d), CustomItemManager.createNaturalDeathHeart());
            }
            if (!naturalEvent.getDeathMessage().equals(event.getDeathMessage())) {
                event.setDeathMessage(naturalEvent.getDeathMessage());
            }
        }
    }

    private void handlePvPDeath(PlayerDeathEvent event, Player player, Player killer, PlayerData playerData, double healthToLoose, boolean preventKillerGain, boolean droppedAtKiller) {
        ZPlayerPvPDeathEvent pvpEvent = new ZPlayerPvPDeathEvent(event, killer, healthToLoose, healthToLoose);
        boolean shouldDropFromPvP = this.plugin.getConfig().getBoolean("dropHeartsPlayer") && !droppedAtKiller;
        pvpEvent.setShouldDropHearts(shouldDropFromPvP);
        pvpEvent.setKillerShouldGainHearts(!preventKillerGain);
        Bukkit.getPluginManager().callEvent(pvpEvent);
        if (!pvpEvent.isCancelled()) {
            if (pvpEvent.getHeartsToLose() > 0.0d) {
                playerData.setMaxHealth(playerData.getMaxHealth() - pvpEvent.getHeartsToLose());
                this.plugin.getStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, playerData.getMaxHealth());
            }
            if (pvpEvent.isShouldDropHearts()) {
                dropHeartsNaturally(player.getLocation(), (int) (pvpEvent.getHeartsToLose() / 2.0d), CustomItemManager.createKillHeart());
            } else if (pvpEvent.isKillerShouldGainHearts() && pvpEvent.getHeartsKillerGains() > 0.0d) {
                handleKillerHeartGainDirect(killer, pvpEvent.getHeartsKillerGains());
            }
            if (pvpEvent.getDeathMessage() != null && !pvpEvent.getDeathMessage().equals(event.getDeathMessage())) {
                event.setDeathMessage(pvpEvent.getDeathMessage());
            }
        }
    }

    // rea: lambda$handleElimination$0(java.util.List, org.bukkit.entity.Player): void -> smali (java_body_unavailable)
    // .method private synthetic lambda$handleElimination$0(Ljava/util/List;Lorg/bukkit/entity/Player;)V
    //     .max stack 5
    //     .max locals 5
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/listeners/PlayerDeathListener;
    //     .local 1 "elimCommands" Ljava/util/List;
    //     .local 2 "player" Lorg/bukkit/entity/Player;
    //     .line 285
    //     aload 1
    //     invokeinterface java/util/List iterator ()Ljava/util/Iterator;
    //     astore 3
    //   :L1
    //     .stack append
    //         java/util/Iterator
    //     .end stack
    //     aload 3
    //     invokeinterface java/util/Iterator hasNext ()Z
    //     ifeq :L2
    //     aload 3
    //     invokeinterface java/util/Iterator next ()Ljava/lang/Object;
    //     checkcast java/lang/String
    //     astore 4
    //     .local 4 "command" Ljava/lang/String;
    //     .line 286
    //     aload 0
    //     getfield com/zetaplugins/lifestealz/listeners/PlayerDeathListener plugin Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     invokevirtual com/zetaplugins/lifestealz/LifeStealZ getServer ()Lorg/bukkit/Server;
    //     aload 0
    //     getfield com/zetaplugins/lifestealz/listeners/PlayerDeathListener plugin Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     .line 287
    //     invokevirtual com/zetaplugins/lifestealz/LifeStealZ getServer ()Lorg/bukkit/Server;
    //     invokeinterface org/bukkit/Server getConsoleSender ()Lorg/bukkit/command/ConsoleCommandSender;
    //     aload 4
    //     ldc "&player&"
    //     aload 2
    //     .line 288
    //     invokeinterface org/bukkit/entity/Player getName ()Ljava/lang/String;
    //     invokevirtual java/lang/String replace (Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //     .line 286
    //     invokeinterface org/bukkit/Server dispatchCommand (Lorg/bukkit/command/CommandSender;Ljava/lang/String;)Z
    //     pop
    //     .line 290
    //     goto :L1
    //     .end local 4 # "command"
    //   :L2
    //     .line 291
    //     .stack chop 1
    //     return
    // .end method

    // rea: lambda$handleElimination$1(org.bukkit.entity.Player, com.zetaplugins.lifestealz.events.death.ZPlayerEliminationEvent): void -> smali (java_body_unavailable)
    // .method private static synthetic lambda$handleElimination$1(Lorg/bukkit/entity/Player;Lcom/zetaplugins/lifestealz/events/death/ZPlayerEliminationEvent;)V
    //     .max stack 2
    //     .max locals 2
    // 
    //     .local 0 "player" Lorg/bukkit/entity/Player;
    //     .local 1 "eliminationEvent" Lcom/zetaplugins/lifestealz/events/death/ZPlayerEliminationEvent;
    //     .line 332
    //     aload 0
    //     invokeinterface org/bukkit/entity/Player isOnline ()Z
    //     ifeq :L1
    //     .line 333
    //     aload 0
    //     aload 1
    //     invokevirtual com/zetaplugins/lifestealz/events/death/ZPlayerEliminationEvent getKickMessage ()Lnet/kyori/adventure/text/Component;
    //     invokeinterface org/bukkit/entity/Player kick (Lnet/kyori/adventure/text/Component;)V
    //   :L1
    //     .line 335
    //     .stack same
    //     return
    // .end method

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (!this.plugin.hasWorldGuard() || WorldGuardManager.checkHeartLossFlag(player)) {
            UUID playerUUID = player.getUniqueId();
            if (player.hasMetadata("combat_log_npc")) {
                playerUUID = (UUID) ((MetadataValue) player.getMetadata("combat_log_npc").get(0)).value();
            }
            PlayerData playerData = this.plugin.getStorage().load(playerUUID);
            if (playerData == null) {
                this.plugin.getLogger().severe("PlayerData not found for player " + player.getName() + " on death event. This should not happen!");
                return;
            }
            boolean isDeathByPlayer = (killer == null || killer.getUniqueId().equals(playerUUID)) ? false : true;
            if (handleAntiAltLogic(event, player, killer)) {
                return;
            }
            boolean looseHeartsToNature = this.plugin.getConfig().getBoolean("looseHeartsToNature") || this.plugin.getConfig().getInt("heartsPerKill") <= 0;
            boolean looseHeartsToPlayer = this.plugin.getConfig().getBoolean("looseHeartsToPlayer") || this.plugin.getConfig().getInt("heartsPerNaturalDeath") <= 0;
            if ((!isDeathByPlayer && looseHeartsToNature) || (isDeathByPlayer && looseHeartsToPlayer)) {
                handleHeartLoss(event, player, killer, playerData, isDeathByPlayer);
            }
        }
    }

    private boolean restrictedHeartGainByBypass(Player player) {
        BypassManager bypassManager = this.plugin.getBypassManager();
        return bypassManager.hasBypass(player) && !bypassManager.getConfig().gainHearts();
    }

    private boolean restrictedHeartGainByGracePeriod(Player player) {
        GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
        return gracePeriodManager.isInGracePeriod(player) && !gracePeriodManager.getConfig().gainHearts();
    }

    private boolean restrictedHeartLossByBypass(Player player) {
        BypassManager bypassManager = this.plugin.getBypassManager();
        return bypassManager.hasBypass(player) && !bypassManager.getConfig().looseHearts();
    }

    private boolean restrictedHeartLossByGracePeriod(Player player) {
        GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
        return gracePeriodManager.isInGracePeriod(player) && !gracePeriodManager.getConfig().looseHearts();
    }

}
