// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands;
// rea: class com.zetaplugins.lifestealz.commands.HeartCommand
class HeartCommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public HeartCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private boolean handleOtherHeartCheck(CommandSender sender, String targetName) {
        PlayerData playerdata;
        if (this.plugin.hasGeyser() && this.plugin.getGeyserPlayerFile().isPlayerStored(targetName)) {
            playerdata = this.plugin.getStorage().load(this.plugin.getGeyserManager().getOfflineBedrockPlayerUniqueId(targetName));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (target.getName() == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!", new MessageUtils.Replaceable[0]));
                return false;
            }
            playerdata = this.plugin.getStorage().load(target.getUniqueId());
        }
        if (playerdata == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!", new MessageUtils.Replaceable[0]));
            return false;
        }
        int heartCount = (int) Math.floor(playerdata.getMaxHealth() / 2.0d);
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "viewheartsOther", "&c%player% &7currently has &c%amount% &7hearts!", new MessageUtils.Replaceable("%amount%", Integer.toString(heartCount)), new MessageUtils.Replaceable("%player%", targetName)));
        return true;
    }

    private boolean handleSelfHeartCheck(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "specifyPlayerOrBePlayer", "&cYou need to either specify a player or be a player yourself!", new MessageUtils.Replaceable[0]));
            return false;
        }
        Player player = (Player) sender;
        PlayerData playerdata = this.plugin.getStorage().load(player.getUniqueId());
        int heartCount = (int) Math.floor(playerdata.getMaxHealth() / 2.0d);
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "viewheartsYou", "&7You have &c%amount% &7hearts!", new MessageUtils.Replaceable("%amount%", Integer.toString(heartCount))));
        return true;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String targetName = (args == null || args.length <= 0) ? null : args[0];
        if (targetName == null) {
            return handleSelfHeartCheck(sender);
        }
        return handleOtherHeartCheck(sender, targetName);
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }

}
