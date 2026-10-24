// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands;
// rea: class com.zetaplugins.lifestealz.commands.ReviveCommand
class ReviveCommand {
    java.lang.String BYPASS_OPTION;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public ReviveCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private boolean canRevive(CommandSender sender, PlayerData playerData, String bypassOption) {
        int maxRevives = this.plugin.getConfig().getInt("maxRevives");
        boolean hasBypassPermission = sender.hasPermission("lifestealz.bypassrevivelimit");
        return maxRevives == -1 || playerData.getHasBeenRevived() < maxRevives || (BYPASS_OPTION.equals(bypassOption) && hasBypassPermission);
    }

    private boolean handleReviveAll(CommandSender sender, String bypassOption) {
        try {
            int revivedPlayers = this.plugin.getStorage().reviveAllPlayers(this.plugin.getConfig().getInt("minHearts"), this.plugin.getConfig().getInt("reviveHearts"), this.plugin.getConfig().getInt("maxRevives"), BYPASS_OPTION.equals(bypassOption) && sender.hasPermission("lifestealz.bypassrevivelimit"));
            this.plugin.getEliminatedPlayersCache().reloadCache();
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "massReviveSuccess", "&7You successfully revived &c%amount% &7player(s)!", new MessageUtils.Replaceable("%amount%", Integer.toString(revivedPlayers))));
            return true;
        } catch (Exception e) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "massReviveError", "&cAn error occurred while reviving all players: %error%", new MessageUtils.Replaceable("%error%", e.getMessage())));
            this.plugin.getLogger().severe("An error occurred while reviving all players: " + e.getMessage());
            return false;
        }
    }

    private boolean handleReviveOne(CommandSender sender, String targetPlayerName, String bypassOption) {
        OfflinePlayer targetPlayer = this.plugin.getServer().getOfflinePlayer(targetPlayerName);
        if (this.plugin.getStorage().load(targetPlayer.getUniqueId()) == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "noPlayerData", "&cThis player has not played on this server yet!", new MessageUtils.Replaceable[0]));
            return false;
        }
        PlayerData playerData = this.plugin.getStorage().load(targetPlayer.getUniqueId());
        if (!canRevive(sender, playerData, bypassOption)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "reviveMaxReached", "&cThis player has already been revived %amount% times!", new MessageUtils.Replaceable("%amount%", Integer.toString(playerData.getHasBeenRevived()))));
            return false;
        }
        if (!isEligibleForRevive(sender, playerData)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "onlyReviveElimPlayers", "&cYou can only revive eliminated players!", new MessageUtils.Replaceable[0]));
            return false;
        }
        revivePlayer(sender, targetPlayer.getName(), playerData);
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "reviveSuccess", "&7You successfully revived &c%player%&7!", new MessageUtils.Replaceable("%player%", targetPlayer.getName())));
        return true;
    }

    private boolean isEligibleForRevive(CommandSender sender, PlayerData playerData) {
        int minHearts = this.plugin.getConfig().getInt("minHearts");
        return playerData.getMaxHealth() <= ((double) (minHearts * 2));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            throwUsageError(sender);
            return false;
        }
        String targetPlayersArg = args[0];
        String bypassOption = args.length > 1 ? args[1] : null;
        if (targetPlayersArg.equals("*")) {
            return handleReviveAll(sender, bypassOption);
        }
        return handleReviveOne(sender, targetPlayersArg, bypassOption);
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            Set<String> eliminatedPlayers = this.plugin.getEliminatedPlayersCache().getEliminatedPlayers();
            eliminatedPlayers.add("*");
            return new ArrayList(eliminatedPlayers);
        }
        if (args.length == 2 && sender.hasPermission("lifestealz.bypassrevivelimit")) {
            return List.of(BYPASS_OPTION);
        }
        return null;
    }

    private void revivePlayer(CommandSender sender, String targetPlayerName, PlayerData playerData) {
        playerData.setMaxHealth(this.plugin.getConfig().getDouble("reviveHearts") * 2.0d);
        playerData.setHasBeenRevived(playerData.getHasBeenRevived() + 1);
        this.plugin.getStorage().save(playerData);
        this.plugin.getEliminatedPlayersCache().removeEliminatedPlayer(targetPlayerName);
        this.plugin.getWebHookManager().sendWebhookMessage(WebHookManager.WebHookType.REVIVE, targetPlayerName, sender.getName());
    }

    private void throwUsageError(CommandSender sender) {
        Component usageMessage = MessageUtils.getAndFormatMsg(false, "usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", "/revive <player>"));
        sender.sendMessage(usageMessage);
    }

}
