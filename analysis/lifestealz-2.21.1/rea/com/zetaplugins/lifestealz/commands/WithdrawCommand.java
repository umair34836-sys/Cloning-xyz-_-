// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands;
// rea: class com.zetaplugins.lifestealz.commands.WithdrawCommand
class WithdrawCommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public WithdrawCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0074  */
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        int i;
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "needToBePlayer", "&cYou need to be a player to execute this command!", new MessageUtils.Replaceable[0]));
            return false;
        }
        OfflinePlayer offlinePlayer = (Player) sender;
        boolean isInGracePeriod = this.plugin.getGracePeriodManager().isInGracePeriod(offlinePlayer);
        boolean allowWithdrawInGrace = this.plugin.getConfig().getBoolean("gracePeriod.allowWithdraw", false);
        if (isInGracePeriod && !allowWithdrawInGrace) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "gracePeriodWithdraw", "&cYou cannot withdraw hearts while in the grace period!", new MessageUtils.Replaceable[0]));
            return false;
        }
        if (args != null) {
            try {
                if (args.length > 0) {
                    i = Integer.parseInt(args[0]);
                } else {
                    i = 1;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", "/withdrawheart <amount> [confirm]")));
                return false;
            }
        } else {
            i = 1;
        }
        int withdrawHearts = i;
        String confirmOption = (args == null || args.length <= 1) ? null : args[1];
        PlayerData playerdata = this.plugin.getStorage().load(offlinePlayer.getUniqueId());
        int minWithdrawHearts = this.plugin.getConfig().getInt("minWithdrawHearts", 0);
        int playerHearts = (int) (playerdata.getMaxHealth() / 2.0d);
        if (playerHearts - withdrawHearts < minWithdrawHearts) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "minWithdrawHearts", "&cInsufficient hearts! You must have at least %min% hearts remaining after withdrawal.", new MessageUtils.Replaceable("%min%", minWithdrawHearts)));
            return false;
        }
        boolean withdrawtoDeath = this.plugin.getConfig().getBoolean("allowDyingFromWithdraw");
        if (withdrawHearts < 1) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "withdrawMin", "&cYou can't withdraw less than 1 heart!", new MessageUtils.Replaceable[0]));
            return false;
        }
        int availableSpace = 0;
        ItemStack[] contents = offlinePlayer.getInventory().getStorageContents();
        for (ItemStack item : contents) {
            if (item == null) {
                availableSpace += 64;
            } else if (CustomItemManager.isHeartItem(item)) {
                availableSpace += 64 - item.getAmount();
            }
            if (availableSpace >= withdrawHearts) {
                break;
            }
        }
        if (availableSpace < withdrawHearts) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "noInventorySpace", "&cYou don't have enough inventory space to withdraw that many hearts!", new MessageUtils.Replaceable[0]));
            return false;
        }
        double resultingHealth = playerdata.getMaxHealth() - (((double) withdrawHearts) * 2.0d);
        double minHealth = this.plugin.getConfig().getDouble("minHearts", 0.0d) * 2.0d;
        if (resultingHealth <= minHealth) {
            if (confirmOption == null || !confirmOption.equals("confirm")) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "noWithdraw", "&cYou would be eliminated if you withdraw a heart!", new MessageUtils.Replaceable[0]));
                if (withdrawtoDeath) {
                    sender.sendMessage(MessageUtils.getAndFormatMsg(false, "withdrawConfirmmsg", "&8&oUse <underlined><click:SUGGEST_COMMAND:/withdrawheart %amount% confirm>/withdrawheart %amount% confirm</click></underlined> if you really want to withdraw a heart", new MessageUtils.Replaceable("%amount%", withdrawHearts)));
                    return false;
                }
                return false;
            }
            if (!withdrawtoDeath) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "noWithdraw", "&cYou would be eliminated if you withdraw a heart!", new MessageUtils.Replaceable[0]));
                return false;
            }
            for (int i2 = 0; i2 < playerdata.getMaxHealth() / 2.0d; i2++) {
                offlinePlayer.getInventory().addItem(new ItemStack[]{CustomItemManager.createHeart()});
            }
            playerdata.setMaxHealth(0.0d);
            this.plugin.getStorage().save(playerdata);
            Component kickmsg = MessageUtils.getAndFormatMsg(false, "eliminatedjoin", "&cYou don't have any hearts left!", new MessageUtils.Replaceable[0]);
            offlinePlayer.kick(kickmsg, PlayerKickEvent.Cause.BANNED);
            if (this.plugin.getConfig().getBoolean("announceElimination")) {
                Component elimAnnouncementMsg = MessageUtils.getAndFormatMsg(true, "eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", new MessageUtils.Replaceable("%player%", offlinePlayer.getName()));
                Bukkit.broadcast(elimAnnouncementMsg);
                return false;
            }
            return false;
        }
        playerdata.setMaxHealth(playerdata.getMaxHealth() - (((double) withdrawHearts) * 2.0d));
        this.plugin.getStorage().save(playerdata);
        LifeStealZ.setMaxHealth(offlinePlayer, playerdata.getMaxHealth());
        offlinePlayer.playSound(offlinePlayer.getLocation(), Sound.ENTITY_PLAYER_HURT, 500.0f, 1.0f);
        for (int i3 = 0; i3 < withdrawHearts; i3++) {
            offlinePlayer.getInventory().addItem(new ItemStack[]{CustomItemManager.createWithdrawHeart()});
        }
        return false;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }
        return null;
    }

}
