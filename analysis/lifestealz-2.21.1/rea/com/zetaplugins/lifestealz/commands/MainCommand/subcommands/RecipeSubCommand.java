// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.RecipeSubCommand
class RecipeSubCommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public RecipeSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            CommandUtils.throwUsageError(player, getUsage());
            return false;
        }
        String itemId = args[1];
        if (itemId == null || !this.plugin.getRecipeManager().getItemIds().contains(itemId)) {
            CommandUtils.throwUsageError(player, getUsage());
            return false;
        }
        if (!this.plugin.getRecipeManager().isCraftable(itemId)) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotCraftable", "&cThis item is not craftable!", new MessageUtils.Replaceable[0]));
            return false;
        }
        String recipeId = args.length > 2 ? args[2] : null;
        if (recipeId == null) {
            this.plugin.getRecipeManager().renderRecipe(player, itemId);
            return true;
        }
        this.plugin.getRecipeManager().renderRecipe(player, itemId, recipeId);
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz recipe <" + String.join(" | ", this.plugin.getRecipeManager().getItemIds()) + "> [<recipeId>]";
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.viewrecipes");
    }

}
