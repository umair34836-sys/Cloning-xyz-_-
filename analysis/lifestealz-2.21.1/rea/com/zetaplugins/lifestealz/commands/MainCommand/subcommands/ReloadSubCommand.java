// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.ReloadSubCommand
class ReloadSubCommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public ReloadSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }
        this.plugin.reloadConfig();
        this.plugin.getLanguageManager().reload();
        this.plugin.getRecipeManager().registerRecipes();
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "reloadMsg", "&7Successfully reloaded the plugin!", new MessageUtils.Replaceable[0]));
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz reload";
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.reload");
    }

}
