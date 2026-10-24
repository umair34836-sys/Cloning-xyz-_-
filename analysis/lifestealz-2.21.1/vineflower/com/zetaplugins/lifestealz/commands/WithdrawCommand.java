package com.zetaplugins.lifestealz.commands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.zetacore.annotations.AutoRegisterCommand;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCommand(
   command = "withdrawheart"
)
public final class WithdrawCommand implements CommandExecutor, TabCompleter {
   private final LifeStealZ plugin;

   public WithdrawCommand(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
      if (sender instanceof Player player) {
         boolean isInGracePeriod = this.plugin.getGracePeriodManager().isInGracePeriod(player);
         boolean allowWithdrawInGrace = this.plugin.getConfig().getBoolean("gracePeriod.allowWithdraw", false);
         if (isInGracePeriod && !allowWithdrawInGrace) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "gracePeriodWithdraw", "&cYou cannot withdraw hearts while in the grace period!"));
            return false;
         } else {
            int withdrawHearts;
            try {
               withdrawHearts = args != null && args.length > 0 ? Integer.parseInt(args[0]) : 1;
            } catch (NumberFormatException var23) {
               sender.sendMessage(
                  MessageUtils.getAndFormatMsg(
                     false, "usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", "/withdrawheart <amount> [confirm]")
                  )
               );
               return false;
            }

            String confirmOption = args != null && args.length > 1 ? args[1] : null;
            PlayerData playerdata = this.plugin.getStorage().load(player.getUniqueId());
            int minWithdrawHearts = this.plugin.getConfig().getInt("minWithdrawHearts", 0);
            int playerHearts = (int)(playerdata.getMaxHealth() / 2.0);
            if (playerHearts - withdrawHearts < minWithdrawHearts) {
               sender.sendMessage(
                  MessageUtils.getAndFormatMsg(
                     false,
                     "minWithdrawHearts",
                     "&cInsufficient hearts! You must have at least %min% hearts remaining after withdrawal.",
                     new MessageUtils.Replaceable("%min%", minWithdrawHearts + "")
                  )
               );
               return false;
            } else {
               boolean withdrawtoDeath = this.plugin.getConfig().getBoolean("allowDyingFromWithdraw");
               if (withdrawHearts < 1) {
                  sender.sendMessage(MessageUtils.getAndFormatMsg(false, "withdrawMin", "&cYou can't withdraw less than 1 heart!"));
                  return false;
               } else {
                  int heartsToWithdraw = withdrawHearts;
                  int availableSpace = 0;
                  ItemStack[] contents = player.getInventory().getStorageContents();

                  for (ItemStack item : contents) {
                     if (item == null) {
                        availableSpace += 64;
                     } else if (CustomItemManager.isHeartItem(item)) {
                        availableSpace += 64 - item.getAmount();
                     }

                     if (availableSpace >= heartsToWithdraw) {
                        break;
                     }
                  }

                  if (availableSpace < heartsToWithdraw) {
                     sender.sendMessage(
                        MessageUtils.getAndFormatMsg(false, "noInventorySpace", "&cYou don't have enough inventory space to withdraw that many hearts!")
                     );
                     return false;
                  } else {
                     double resultingHealth = playerdata.getMaxHealth() - (double)withdrawHearts * 2.0;
                     double minHealth = this.plugin.getConfig().getDouble("minHearts", 0.0) * 2.0;
                     if (resultingHealth <= minHealth) {
                        if (confirmOption == null || !confirmOption.equals("confirm")) {
                           sender.sendMessage(MessageUtils.getAndFormatMsg(false, "noWithdraw", "&cYou would be eliminated if you withdraw a heart!"));
                           if (withdrawtoDeath) {
                              sender.sendMessage(
                                 MessageUtils.getAndFormatMsg(
                                    false,
                                    "withdrawConfirmmsg",
                                    "&8&oUse <underlined><click:SUGGEST_COMMAND:/withdrawheart %amount% confirm>/withdrawheart %amount% confirm</click></underlined> if you really want to withdraw a heart",
                                    new MessageUtils.Replaceable("%amount%", withdrawHearts + "")
                                 )
                              );
                           }

                           return false;
                        } else if (!withdrawtoDeath) {
                           sender.sendMessage(MessageUtils.getAndFormatMsg(false, "noWithdraw", "&cYou would be eliminated if you withdraw a heart!"));
                           return false;
                        } else {
                           for (int i = 0; (double)i < playerdata.getMaxHealth() / 2.0; i++) {
                              player.getInventory().addItem(new ItemStack[]{CustomItemManager.createHeart()});
                           }

                           playerdata.setMaxHealth(0.0);
                           this.plugin.getStorage().save(playerdata);
                           Component kickmsg = MessageUtils.getAndFormatMsg(false, "eliminatedjoin", "&cYou don't have any hearts left!");
                           player.kick(kickmsg, Cause.BANNED);
                           if (this.plugin.getConfig().getBoolean("announceElimination")) {
                              Component elimAnnouncementMsg = MessageUtils.getAndFormatMsg(
                                 true,
                                 "eliminateionAnnouncementNature",
                                 "&c%player% &7has been eliminated!",
                                 new MessageUtils.Replaceable("%player%", player.getName())
                              );
                              Bukkit.broadcast(elimAnnouncementMsg);
                           }

                           return false;
                        }
                     } else {
                        playerdata.setMaxHealth(playerdata.getMaxHealth() - (double)withdrawHearts * 2.0);
                        this.plugin.getStorage().save(playerdata);
                        LifeStealZ.setMaxHealth(player, playerdata.getMaxHealth());
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 500.0F, 1.0F);

                        for (int i = 0; i < withdrawHearts; i++) {
                           player.getInventory().addItem(new ItemStack[]{CustomItemManager.createWithdrawHeart()});
                        }

                        return false;
                     }
                  }
               }
            }
         }
      } else {
         sender.sendMessage(MessageUtils.getAndFormatMsg(false, "needToBePlayer", "&cYou need to be a player to execute this command!"));
         return false;
      }
   }

   @Nullable
   public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
      return args.length == 1 ? List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10") : null;
   }
}
