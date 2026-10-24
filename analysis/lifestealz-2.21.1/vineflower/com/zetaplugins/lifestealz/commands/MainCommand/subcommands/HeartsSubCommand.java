package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.storage.Storage;
import com.zetaplugins.lifestealz.util.MaxHeartsManager;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class HeartsSubCommand implements SubCommand {
   private final LifeStealZ plugin;
   private final FileConfiguration config;
   private final Storage storage;

   public HeartsSubCommand(LifeStealZ plugin) {
      this.plugin = plugin;
      this.config = plugin.getConfig();
      this.storage = plugin.getStorage();
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
         String optionTwo = args[1];
         List<String> possibleOptionTwo = List.of("add", "set", "remove", "get");
         if (optionTwo != null && possibleOptionTwo.contains(optionTwo)) {
            if (optionTwo.equals("get")) {
               OfflinePlayer player = CommandUtils.parseOfflinePlayer(args[2], false, true, this.plugin).get(0);
               if (player == null) {
                  sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
                  return false;
               } else {
                  PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
                  int hearts = (int)(playerData.getMaxHealth() / 2.0);
                  sender.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        true,
                        "getHearts",
                        "&c%player% &7currently has &c%amount% &7hearts!",
                        new MessageUtils.Replaceable("%player%", player.getName()),
                        new MessageUtils.Replaceable("%amount%", hearts + "")
                     )
                  );
                  return true;
               }
            } else if (args.length < 4) {
               CommandUtils.throwUsageError(sender, this.getUsage());
               return false;
            } else {
               int amount = Integer.parseInt(args[3]);
               if (amount < 0) {
                  CommandUtils.throwUsageError(sender, this.getUsage());
                  return false;
               } else {
                  List<OfflinePlayer> targetPlayers = CommandUtils.parseOfflinePlayer(args[2], true, true, this.plugin);

                  for (OfflinePlayer targetPlayer : targetPlayers) {
                     if (targetPlayer == null && targetPlayers.size() == 1) {
                        sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
                        return false;
                     }

                     if (targetPlayer != null || targetPlayers.size() <= 1) {
                        assert targetPlayer != null;

                        PlayerData targetPlayerData = this.plugin.getStorage().load(targetPlayer.getUniqueId());
                        double maxAllowedHearts = (double)(this.config.getInt("maxHearts") * 2);
                        Player targetOnlinePlayer = targetPlayer.getPlayer();
                        if (targetOnlinePlayer != null) {
                           maxAllowedHearts = MaxHeartsManager.getMaxHearts(targetOnlinePlayer, this.config);
                        }

                        switch (optionTwo) {
                           case "add":
                              if (this.config.getBoolean("enforceMaxHeartsOnAdminCommands")
                                 && targetPlayerData.getMaxHealth() + (double)(amount * 2) > maxAllowedHearts) {
                                 this.sendHeartLimitReachedMessage(sender, maxAllowedHearts);
                                 return false;
                              }

                              targetPlayerData.setMaxHealth(targetPlayerData.getMaxHealth() + (double)(amount * 2));
                              this.storage.save(targetPlayerData);
                              if (targetPlayer instanceof Player) {
                                 LifeStealZ.setMaxHealth((Player)targetPlayer, targetPlayerData.getMaxHealth());
                              }
                              break;
                           case "set":
                              if (amount == 0) {
                                 sender.sendMessage(
                                    MessageUtils.getAndFormatMsg(false, "connotSetHeartsBelowOrToZero", "&cYou can't set a player's hearts below or to 0!")
                                 );
                                 return false;
                              }

                              if (this.config.getBoolean("enforceMaxHeartsOnAdminCommands") && (double)(amount * 2) > maxAllowedHearts) {
                                 this.sendHeartLimitReachedMessage(sender, maxAllowedHearts);
                                 return false;
                              }

                              targetPlayerData.setMaxHealth((double)(amount * 2));
                              this.storage.save(targetPlayerData);
                              if (targetPlayer instanceof Player) {
                                 LifeStealZ.setMaxHealth((Player)targetPlayer, targetPlayerData.getMaxHealth());
                              }
                              break;
                           case "remove":
                              if (targetPlayerData.getMaxHealth() / 2.0 - (double)amount <= 0.0) {
                                 sender.sendMessage(
                                    MessageUtils.getAndFormatMsg(false, "connotSetHeartsBelowOrToZero", "&cYou can't set a player's hearts below or to 0!")
                                 );
                                 return false;
                              }

                              targetPlayerData.setMaxHealth(targetPlayerData.getMaxHealth() - (double)(amount * 2));
                              this.storage.save(targetPlayerData);
                              if (targetPlayer instanceof Player) {
                                 LifeStealZ.setMaxHealth((Player)targetPlayer, targetPlayerData.getMaxHealth());
                              }
                        }
                     }
                  }

                  this.sendConfirmMessage(sender, optionTwo, targetPlayers, amount);
                  return true;
               }
            }
         } else {
            CommandUtils.throwUsageError(sender, this.getUsage());
            return false;
         }
      }
   }

   private void sendConfirmMessage(CommandSender sender, String optionTwo, List<OfflinePlayer> targetPlayers, int changedAmount) {
      Map<String, String> replacements = new HashMap<>();
      replacements.put("%amount%", String.valueOf(changedAmount));
      if (targetPlayers.size() == 1) {
         replacements.put("%player%", targetPlayers.get(0).getName());
      } else {
         replacements.put("%pamount%", String.valueOf(targetPlayers.size()));
      }

      String messageKey;
      String defaultMessage;
      switch (optionTwo) {
         case "add":
            messageKey = targetPlayers.size() == 1 ? "addHeartsConfirmSingle" : "addHeartsConfirmMultiple";
            defaultMessage = targetPlayers.size() == 1
               ? "&7You successfully added &c%amount% &7hearts to &c%player%!"
               : "&7Successfully added &c%amount% &7hearts to &c%pamount% players";
            break;
         case "set":
            messageKey = targetPlayers.size() == 1 ? "setHeartsConfirmSingle" : "setHeartsConfirmMultiple";
            defaultMessage = targetPlayers.size() == 1
               ? "&7Successfully set &c%player%'s &7hearts to &c%amount% hearts"
               : "&7Successfully set &c%pamount% players' &7hearts to &c%amount% hearts";
            break;
         case "remove":
            messageKey = targetPlayers.size() == 1 ? "removeHeartsConfirmSingle" : "removeHeartsConfirmMultiple";
            defaultMessage = targetPlayers.size() == 1
               ? "&7Successfully removed &c%amount% &7hearts from &c%player%"
               : "&7Successfully removed &c%amount% &7hearts from &c%pamount% players";
            break;
         default:
            throw new IllegalArgumentException("Invalid option: " + optionTwo);
      }

      Component confirmMessage = this.createConfirmMessage(messageKey, defaultMessage, replacements);
      sender.sendMessage(confirmMessage);
   }

   private Component createConfirmMessage(String key, String defaultMessage, Map<String, String> replacements) {
      MessageUtils.Replaceable[] replaceables = replacements.entrySet()
         .stream()
         .map(entry -> new MessageUtils.Replaceable(entry.getKey(), entry.getValue()))
         .toArray(MessageUtils.Replaceable[]::new);
      return MessageUtils.getAndFormatMsg(true, key, defaultMessage, replaceables);
   }

   private void sendHeartLimitReachedMessage(CommandSender sender, double maxHearts) {
      Component maxHeartsMsg = MessageUtils.getAndFormatMsg(
         true,
         "maxHeartLimitReached",
         "&cYou already reached the limit of %limit% hearts!",
         new MessageUtils.Replaceable("%limit%", (int)(maxHearts / 2.0) + "")
      );
      sender.sendMessage(maxHeartsMsg);
   }

   @Override
   public String getUsage() {
      return "/lifestealz hearts <add | set | remove> <player> [amount]";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.hasPermission("lifestealz.admin.setlife");
   }
}
