package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RecipeSubCommand implements SubCommand {
   private final LifeStealZ plugin;

   public RecipeSubCommand(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean execute(CommandSender sender, String[] args) {
      if (!this.hasPermission(sender)) {
         CommandUtils.throwPermissionError(sender);
         return false;
      } else if (!(sender instanceof Player player)) {
         return false;
      } else if (args.length < 2) {
         CommandUtils.throwUsageError(player, this.getUsage());
         return false;
      } else {
         String itemId = args[1];
         if (itemId == null || !this.plugin.getRecipeManager().getItemIds().contains(itemId)) {
            CommandUtils.throwUsageError(player, this.getUsage());
            return false;
         } else if (!this.plugin.getRecipeManager().isCraftable(itemId)) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotCraftable", "&cThis item is not craftable!"));
            return false;
         } else {
            String recipeId = args.length > 2 ? args[2] : null;
            if (recipeId == null) {
               this.plugin.getRecipeManager().renderRecipe(player, itemId);
               return true;
            } else {
               this.plugin.getRecipeManager().renderRecipe(player, itemId, recipeId);
               return true;
            }
         }
      }
   }

   @Override
   public String getUsage() {
      return "/lifestealz recipe <" + String.join(" | ", this.plugin.getRecipeManager().getItemIds()) + "> [<recipeId>]";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.hasPermission("lifestealz.viewrecipes");
   }
}
