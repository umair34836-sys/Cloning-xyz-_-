package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import org.bukkit.command.CommandSender;

public final class ReloadSubCommand implements SubCommand {
   private final LifeStealZ plugin;

   public ReloadSubCommand(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean execute(CommandSender sender, String[] args) {
      if (!this.hasPermission(sender)) {
         CommandUtils.throwPermissionError(sender);
         return false;
      } else {
         this.plugin.reloadConfig();
         this.plugin.getLanguageManager().reload();
         this.plugin.getRecipeManager().registerRecipes();
         sender.sendMessage(MessageUtils.getAndFormatMsg(true, "reloadMsg", "&7Successfully reloaded the plugin!"));
         return true;
      }
   }

   @Override
   public String getUsage() {
      return "/lifestealz reload";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.hasPermission("lifestealz.admin.reload");
   }
}
