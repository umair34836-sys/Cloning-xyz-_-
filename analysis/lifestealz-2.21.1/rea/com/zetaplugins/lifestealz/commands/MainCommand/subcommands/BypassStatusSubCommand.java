// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.BypassStatusSubCommand
class BypassStatusSubCommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public BypassStatusSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }
        if (args.length < 2) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        List<OfflinePlayer> targetPlayers = CommandUtils.parseOfflinePlayer(args[1], true, true, this.plugin);
        if (targetPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!", new MessageUtils.Replaceable[0]));
            return false;
        }
        OfflinePlayer targetPlayer = targetPlayers.get(0);
        boolean hasBypass = this.plugin.getBypassManager().hasBypass(targetPlayer.getPlayer());
        if (hasBypass) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "bypassStatusActive", "&7Bypass permission is &aactive &7for %player%", new MessageUtils.Replaceable("%player%", targetPlayer.getName())));
            return true;
        }
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "bypassStatusInactive", "&7Bypass permission is &cinactive &7for %player%.", new MessageUtils.Replaceable("%player%", targetPlayer.getName())));
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz checkbypass <player>";
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.bypasscheck");
    }

}
