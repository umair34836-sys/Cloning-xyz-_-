package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GiveItemSubCommand implements SubCommand {
   private final LifeStealZ plugin;

   public GiveItemSubCommand(LifeStealZ plugin) {
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
         List<Player> targetPlayers = CommandUtils.parsePlayerName(args[1], true, this.plugin);
         if (targetPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
            return false;
         } else {
            String item = args[2];
            if (item == null) {
               CommandUtils.throwUsageError(sender, this.getUsage());
               return false;
            } else {
               Set<String> possibleItems = this.plugin.getRecipeManager().getItemIds();
               if (!possibleItems.contains(item)) {
                  CommandUtils.throwUsageError(sender, this.getUsage());
                  return false;
               } else {
                  int amount = args.length > 3 ? Integer.parseInt(args[3]) : 1;
                  if (amount < 1) {
                     CommandUtils.throwUsageError(sender, this.getUsage());
                     return false;
                  } else {
                     boolean silent = args.length > 4 && args[4].equals("silent");

                     for (Player targetPlayer : targetPlayers) {
                        if (targetPlayer == null && targetPlayers.size() == 1) {
                           sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
                           return false;
                        }

                        if (targetPlayer != null || targetPlayers.size() <= 1) {
                           assert targetPlayer != null;

                           ItemStack itemStack = CustomItemManager.createCustomItem(item, amount);
                           HashMap<Integer, ItemStack> leftover = targetPlayer.getInventory().addItem(new ItemStack[]{itemStack});
                           if (!leftover.isEmpty()) {
                              for (ItemStack dropItem : leftover.values()) {
                                 targetPlayer.getWorld().dropItemNaturally(targetPlayer.getLocation(), dropItem);
                              }
                           }

                           if (!silent) {
                              targetPlayer.sendMessage(
                                 MessageUtils.getAndFormatMsg(
                                    true,
                                    "giveItem",
                                    "&7You received &c%amount% &7%item%!",
                                    new MessageUtils.Replaceable("%amount%", amount + ""),
                                    new MessageUtils.Replaceable("%item%", CustomItemManager.getCustomItemData(item).getName())
                                 )
                              );
                           }
                        }
                     }

                     return true;
                  }
               }
            }
         }
      }
   }

   @Override
   public String getUsage() {
      return "/lifestealz giveItem <player> <item> [amount]";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.hasPermission("lifestealz.admin.giveitem");
   }
}
