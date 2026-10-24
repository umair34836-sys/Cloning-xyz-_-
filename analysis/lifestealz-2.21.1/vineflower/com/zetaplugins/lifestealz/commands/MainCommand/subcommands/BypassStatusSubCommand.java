package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public final class BypassStatusSubCommand implements SubCommand {
   private final LifeStealZ plugin;

   public BypassStatusSubCommand(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean execute(CommandSender sender, String[] args) {
      if (!this.hasPermission(sender)) {
         CommandUtils.throwPermissionError(sender);
         return false;
      } else if (args.length < 2) {
         CommandUtils.throwUsageError(sender, this.getUsage());
         return false;
      } else {
         List<OfflinePlayer> targetPlayers = CommandUtils.parseOfflinePlayer(args[1], true, true, this.plugin);
         if (targetPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
            return false;
         } else {
            OfflinePlayer targetPlayer = targetPlayers.get(0);
            boolean hasBypass = this.plugin.getBypassManager().hasBypass(targetPlayer.getPlayer());
            if (hasBypass) {
               sender.sendMessage(
                  MessageUtils.getAndFormatMsg(
                     true,
                     "bypassStatusActive",
                     "&7Bypass permission is &aactive &7for %player%",
                     new MessageUtils.Replaceable("%player%", targetPlayer.getName())
                  )
               );
            } else {
               sender.sendMessage(
                  MessageUtils.getAndFormatMsg(
                     true,
                     "bypassStatusInactive",
                     "&7Bypass permission is &cinactive &7for %player%.",
                     new MessageUtils.Replaceable("%player%", targetPlayer.getName())
                  )
               );
            }

            return true;
         }
      }
   }

   @Override
   public String getUsage() {
      return "/lifestealz checkbypass <player>";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.hasPermission("lifestealz.admin.bypasscheck");
   }
}
