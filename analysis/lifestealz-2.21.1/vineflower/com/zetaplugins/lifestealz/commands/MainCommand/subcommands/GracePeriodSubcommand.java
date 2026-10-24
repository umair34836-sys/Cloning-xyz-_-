package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public final class GracePeriodSubcommand implements SubCommand {
   private final LifeStealZ plugin;

   public GracePeriodSubcommand(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean execute(CommandSender sender, String[] args) {
      if (!this.hasPermission(sender)) {
         CommandUtils.throwPermissionError(sender);
         return false;
      } else if (args.length < 3) {
         CommandUtils.throwUsageError(sender, this.getUsage());
         return false;
      } else {
         List<OfflinePlayer> targetPlayers = CommandUtils.parseOfflinePlayer(args[1], true, true, this.plugin);
         if (targetPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
            return false;
         } else {
            String secondArg = args[2].toLowerCase();
            switch (secondArg) {
               case "skip":
                  this.handleGracePeriod(sender, targetPlayers, GracePeriodSubcommand.GracePeriodAction.SKIP);
                  break;
               case "reset":
                  this.handleGracePeriod(sender, targetPlayers, GracePeriodSubcommand.GracePeriodAction.RESET);
                  break;
               default:
                  CommandUtils.throwUsageError(sender, this.getUsage());
                  return false;
            }

            return true;
         }
      }
   }

   private void handleGracePeriod(CommandSender sender, List<OfflinePlayer> targetPlayers, GracePeriodSubcommand.GracePeriodAction gracePeriodAction) {
      int successCount = 0;
      OfflinePlayer lastSucessfulPlayer = null;

      for (OfflinePlayer targetPlayer : targetPlayers) {
         boolean success = gracePeriodAction == GracePeriodSubcommand.GracePeriodAction.SKIP
            ? this.plugin.getGracePeriodManager().skipGracePeriod(targetPlayer)
            : this.plugin.getGracePeriodManager().resetGracePeriod(targetPlayer);
         if (success) {
            successCount++;
            lastSucessfulPlayer = targetPlayer;
         }
      }

      if (successCount != 0 && successCount <= 1) {
         sender.sendMessage(
            gracePeriodAction == GracePeriodSubcommand.GracePeriodAction.SKIP
               ? MessageUtils.getAndFormatMsg(
                  true,
                  "gracePeriodSkipSuccessOnePlayer",
                  "&7Successfully skipped the grace period for &c%player%&7.",
                  new MessageUtils.Replaceable("%player%", lastSucessfulPlayer.getName())
               )
               : MessageUtils.getAndFormatMsg(
                  true,
                  "gracePeriodResetSuccessOnePlayer",
                  "&7Successfully reset the grace period for &c%player%&7.",
                  new MessageUtils.Replaceable("%player%", lastSucessfulPlayer.getName())
               )
         );
      } else {
         sender.sendMessage(
            gracePeriodAction == GracePeriodSubcommand.GracePeriodAction.SKIP
               ? MessageUtils.getAndFormatMsg(
                  true,
                  "gracePeriodSkipSuccess",
                  "&7Successfully skipped the grace period for &c%playerCount% &7players.",
                  new MessageUtils.Replaceable("%playerCount%", String.valueOf(successCount))
               )
               : MessageUtils.getAndFormatMsg(
                  true,
                  "gracePeriodResetSuccess",
                  "&7Successfully reset the grace period for &c%playerCount% &7players.",
                  new MessageUtils.Replaceable("%playerCount%", String.valueOf(successCount))
               )
         );
      }
   }

   @Override
   public String getUsage() {
      return "/lifestealz graceperiod <player> <skip | reset>";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.hasPermission("lifestealz.admin.graceperiod");
   }

   private static enum GracePeriodAction {
      SKIP,
      RESET;
   }
}
