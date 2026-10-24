package com.zetaplugins.lifestealz.commands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.WebHookManager;
import com.zetaplugins.zetacore.annotations.AutoRegisterCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCommand(
   command = "revive"
)
public final class ReviveCommand implements CommandExecutor, TabCompleter {
   private final LifeStealZ plugin;
   private static final String BYPASS_OPTION = "bypass";

   public ReviveCommand(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
      if (args.length < 1) {
         this.throwUsageError(sender);
         return false;
      } else {
         String targetPlayersArg = args[0];
         String bypassOption = args.length > 1 ? args[1] : null;
         return targetPlayersArg.equals("*") ? this.handleReviveAll(sender, bypassOption) : this.handleReviveOne(sender, targetPlayersArg, bypassOption);
      }
   }

   private boolean handleReviveAll(CommandSender sender, String bypassOption) {
      try {
         int revivedPlayers = this.plugin
            .getStorage()
            .reviveAllPlayers(
               this.plugin.getConfig().getInt("minHearts"),
               this.plugin.getConfig().getInt("reviveHearts"),
               this.plugin.getConfig().getInt("maxRevives"),
               "bypass".equals(bypassOption) && sender.hasPermission("lifestealz.bypassrevivelimit")
            );
         this.plugin.getEliminatedPlayersCache().reloadCache();
         sender.sendMessage(
            MessageUtils.getAndFormatMsg(
               true,
               "massReviveSuccess",
               "&7You successfully revived &c%amount% &7player(s)!",
               new MessageUtils.Replaceable("%amount%", Integer.toString(revivedPlayers))
            )
         );
         return true;
      } catch (Exception var4) {
         sender.sendMessage(
            MessageUtils.getAndFormatMsg(
               false, "massReviveError", "&cAn error occurred while reviving all players: %error%", new MessageUtils.Replaceable("%error%", var4.getMessage())
            )
         );
         this.plugin.getLogger().severe("An error occurred while reviving all players: " + var4.getMessage());
         return false;
      }
   }

   private boolean handleReviveOne(CommandSender sender, String targetPlayerName, String bypassOption) {
      OfflinePlayer targetPlayer = this.plugin.getServer().getOfflinePlayer(targetPlayerName);
      PlayerData playerData = this.plugin.getStorage().load(targetPlayer.getUniqueId());
      if (playerData == null) {
         sender.sendMessage(MessageUtils.getAndFormatMsg(false, "noPlayerData", "&cThis player has not played on this server yet!"));
         return false;
      } else {
         playerData = this.plugin.getStorage().load(targetPlayer.getUniqueId());
         if (!this.canRevive(sender, playerData, bypassOption)) {
            sender.sendMessage(
               MessageUtils.getAndFormatMsg(
                  false,
                  "reviveMaxReached",
                  "&cThis player has already been revived %amount% times!",
                  new MessageUtils.Replaceable("%amount%", Integer.toString(playerData.getHasBeenRevived()))
               )
            );
            return false;
         } else if (!this.isEligibleForRevive(sender, playerData)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "onlyReviveElimPlayers", "&cYou can only revive eliminated players!"));
            return false;
         } else {
            this.revivePlayer(sender, targetPlayer.getName(), playerData);
            sender.sendMessage(
               MessageUtils.getAndFormatMsg(
                  true, "reviveSuccess", "&7You successfully revived &c%player%&7!", new MessageUtils.Replaceable("%player%", targetPlayer.getName())
               )
            );
            return true;
         }
      }
   }

   private boolean canRevive(CommandSender sender, PlayerData playerData, String bypassOption) {
      int maxRevives = this.plugin.getConfig().getInt("maxRevives");
      boolean hasBypassPermission = sender.hasPermission("lifestealz.bypassrevivelimit");
      return maxRevives == -1 || playerData.getHasBeenRevived() < maxRevives || "bypass".equals(bypassOption) && hasBypassPermission;
   }

   private boolean isEligibleForRevive(CommandSender sender, PlayerData playerData) {
      int minHearts = this.plugin.getConfig().getInt("minHearts");
      return !(playerData.getMaxHealth() > (double)(minHearts * 2));
   }

   private void revivePlayer(CommandSender sender, String targetPlayerName, PlayerData playerData) {
      playerData.setMaxHealth(this.plugin.getConfig().getDouble("reviveHearts") * 2.0);
      playerData.setHasBeenRevived(playerData.getHasBeenRevived() + 1);
      this.plugin.getStorage().save(playerData);
      this.plugin.getEliminatedPlayersCache().removeEliminatedPlayer(targetPlayerName);
      this.plugin.getWebHookManager().sendWebhookMessage(WebHookManager.WebHookType.REVIVE, targetPlayerName, sender.getName());
   }

   private void throwUsageError(CommandSender sender) {
      Component usageMessage = MessageUtils.getAndFormatMsg(
         false, "usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", "/revive <player>")
      );
      sender.sendMessage(usageMessage);
   }

   @Nullable
   public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
      if (args.length == 1) {
         Set<String> eliminatedPlayers = this.plugin.getEliminatedPlayersCache().getEliminatedPlayers();
         eliminatedPlayers.add("*");
         return new ArrayList<>(eliminatedPlayers);
      } else {
         return args.length == 2 && sender.hasPermission("lifestealz.bypassrevivelimit") ? List.of("bypass") : null;
      }
   }
}
