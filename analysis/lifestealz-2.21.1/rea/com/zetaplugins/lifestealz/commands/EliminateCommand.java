// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands;
// rea: class com.zetaplugins.lifestealz.commands.EliminateCommand
class EliminateCommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public EliminateCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private void dropPlayerInventory(Player targetPlayer) {
        for (ItemStack item : targetPlayer.getInventory().getContents()) {
            if (item != null) {
                targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), item);
            }
        }
        targetPlayer.getInventory().clear();
    }

    private void eliminatePlayer(CommandSender sender, Player targetPlayer) {
        PlayerData playerData = this.plugin.getStorage().load(targetPlayer.getUniqueId());
        playerData.setMaxHealth(0.0d);
        this.plugin.getStorage().save(playerData);
        dropPlayerInventory(targetPlayer);
        Component kickmsg = MessageUtils.getAndFormatMsg(false, "eliminatedjoin", "&cYou don't have any hearts left!", new MessageUtils.Replaceable[0]);
        targetPlayer.kick(kickmsg, PlayerKickEvent.Cause.BANNED);
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "eliminateSuc", "&7You successfully eliminated &c%player%&7!", new MessageUtils.Replaceable("%player%", targetPlayer.getName())));
        if (this.plugin.getConfig().getBoolean("announceElimination")) {
            Component elimAnnouncementMsg = MessageUtils.getAndFormatMsg(true, "eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", new MessageUtils.Replaceable("%player%", targetPlayer.getName()));
            Bukkit.broadcast(elimAnnouncementMsg);
        }
        this.plugin.getEliminatedPlayersCache().addEliminatedPlayer(targetPlayer.getName());
        this.plugin.getWebHookManager().sendWebhookMessage(WebHookManager.WebHookType.ELIMINATION, targetPlayer.getName(), sender.getName());
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String targetPlayerName = (args == null || args.length <= 0) ? null : args[0];
        if (targetPlayerName == null) {
            throwUsageError(sender);
            return false;
        }
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            throwUsageError(sender);
            return false;
        }
        eliminatePlayer(sender, targetPlayer);
        return true;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }

    private void throwUsageError(CommandSender sender) {
        Component usageMessage = MessageUtils.getAndFormatMsg(false, "usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", "/eliminate <player>"));
        sender.sendMessage(usageMessage);
    }

}
