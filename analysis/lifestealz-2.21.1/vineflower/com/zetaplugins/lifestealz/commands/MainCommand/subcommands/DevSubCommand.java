package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.GracePeriodManager;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import com.zetaplugins.lifestealz.util.customitems.CustomItem;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class DevSubCommand implements SubCommand {
   private final LifeStealZ plugin;

   public DevSubCommand(LifeStealZ plugin) {
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
         String optionTwo = args[1];
         if (optionTwo.equals("giveForbiddenitem")) {
            if (!(sender instanceof Player player)) {
               return false;
            } else {
               player.getInventory().addItem(new ItemStack[]{new CustomItem(Material.BARRIER).makeForbidden().getItemStack()});
               return true;
            }
         } else {
            if (optionTwo.equals("isInGracePeriod")) {
               if (args.length < 3 && !(sender instanceof Player)) {
                  CommandUtils.throwUsageError(sender, "/lifestealz dev isInGracePeriod [player]");
                  return false;
               }

               Player player = args.length > 2 ? this.plugin.getServer().getPlayer(args[2]) : (Player)sender;
               if (player == null) {
                  CommandUtils.throwUsageError(sender, "/lifestealz dev isInGracePeriod [player]");
                  return false;
               }

               GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
               String gracePeriodColor = gracePeriodManager.isInGracePeriod(player) ? "&a" : "&c";
               String gracePeriodEnabledColor = gracePeriodManager.isEnabled() ? "&a" : "&c";
               sender.sendMessage(
                  MessageUtils.formatMsg(
                     "&7Is &c"
                        + player.getName()
                        + " &7in grace period? "
                        + gracePeriodColor
                        + gracePeriodManager.isInGracePeriod(player)
                        + (
                           gracePeriodManager.isInGracePeriod(player)
                              ? " &7(" + gracePeriodManager.getGracePeriodRemaining(player).orElse(-1) + "remaining)"
                              : ""
                        )
                        + "\n&7Grace period enabled: "
                        + gracePeriodEnabledColor
                        + gracePeriodManager.isEnabled()
                        + "&7"
                  )
               );
            }

            if (optionTwo.equals("setFirstJoinDate")) {
               if (args.length < 3 && !(sender instanceof Player)) {
                  CommandUtils.throwUsageError(sender, "/lifestealz dev setFirstJoinDate [player]");
                  return false;
               }

               Player player = args.length > 2 ? this.plugin.getServer().getPlayer(args[2]) : (Player)sender;
               if (player == null) {
                  CommandUtils.throwUsageError(sender, "/lifestealz dev setFirstJoinDate [player]");
                  return false;
               }

               long newFirstJoin = System.currentTimeMillis();
               PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
               playerData.setFirstJoin(newFirstJoin);
               this.plugin.getStorage().save(playerData);
               this.plugin.getGracePeriodManager().startGracePeriod(player);
            }

            if (optionTwo.equals("refreshCaches")) {
               this.plugin.getEliminatedPlayersCache().reloadCache();
               sender.sendMessage(MessageUtils.formatMsg("&7Caches reloaded!"));
            }

            if (optionTwo.equals("crash")) {
               throw new RuntimeException("Crash test");
            } else {
               if (optionTwo.equals("cleardatabase")) {
                  this.plugin.getStorage().clearDatabase();
                  sender.sendMessage(MessageUtils.formatMsg("&7Database cleared!"));
               }

               if (optionTwo.equals("giveAnimationTotem")) {
                  if (!(sender instanceof Player player)) {
                     return false;
                  } else {
                     player.getInventory().addItem(new ItemStack[]{CustomItemManager.createHeartAnimationTotem()});
                     return true;
                  }
               } else if (optionTwo.equals("getEffectivePerms")) {
                  if (!(sender instanceof Player player)) {
                     return false;
                  } else {
                     String effectivePerms = player.getEffectivePermissions()
                        .stream()
                        .map(permission -> permission.getPermission() + (permission.getValue() ? "" : " (denied)"))
                        .reduce((perm1, perm2) -> perm1 + ", " + perm2)
                        .orElse("No permissions found");
                     sender.sendMessage(MessageUtils.formatMsg("&7Effective permissions for " + player.getName() + ": " + effectivePerms));
                     return true;
                  }
               } else {
                  return false;
               }
            }
         }
      }
   }

   @Override
   public String getUsage() {
      return "/lifestealz dev <giveForbiddenitem | isInGracePeriod | setFirstJoinDate | refreshCaches | crash | cleardatabase | giveAnimationTotem | getEffectivePerms>";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.isOp();
   }
}
