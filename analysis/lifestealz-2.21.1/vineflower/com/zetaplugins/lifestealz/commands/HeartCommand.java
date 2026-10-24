package com.zetaplugins.lifestealz.commands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.zetacore.annotations.AutoRegisterCommand;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCommand(
   command = "hearts"
)
public final class HeartCommand implements CommandExecutor, TabCompleter {
   private final LifeStealZ plugin;

   public HeartCommand(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
      String targetName = args != null && args.length > 0 ? args[0] : null;
      return targetName == null ? this.handleSelfHeartCheck(sender) : this.handleOtherHeartCheck(sender, targetName);
   }

   private boolean handleSelfHeartCheck(CommandSender sender) {
      if (!(sender instanceof Player player)) {
         sender.sendMessage(MessageUtils.getAndFormatMsg(false, "specifyPlayerOrBePlayer", "&cYou need to either specify a player or be a player yourself!"));
         return false;
      } else {
         PlayerData playerdata = this.plugin.getStorage().load(player.getUniqueId());
         int heartCount = (int)Math.floor(playerdata.getMaxHealth() / 2.0);
         sender.sendMessage(
            MessageUtils.getAndFormatMsg(
               true, "viewheartsYou", "&7You have &c%amount% &7hearts!", new MessageUtils.Replaceable("%amount%", Integer.toString(heartCount))
            )
         );
         return true;
      }
   }

   private boolean handleOtherHeartCheck(CommandSender sender, String targetName) {
      PlayerData playerdata;
      if (this.plugin.hasGeyser() && this.plugin.getGeyserPlayerFile().isPlayerStored(targetName)) {
         playerdata = this.plugin.getStorage().load(this.plugin.getGeyserManager().getOfflineBedrockPlayerUniqueId(targetName));
      } else {
         OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
         if (target.getName() == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
            return false;
         }

         playerdata = this.plugin.getStorage().load(target.getUniqueId());
      }

      if (playerdata == null) {
         sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
         return false;
      } else {
         int heartCount = (int)Math.floor(playerdata.getMaxHealth() / 2.0);
         sender.sendMessage(
            MessageUtils.getAndFormatMsg(
               true,
               "viewheartsOther",
               "&c%player% &7currently has &c%amount% &7hearts!",
               new MessageUtils.Replaceable("%amount%", Integer.toString(heartCount)),
               new MessageUtils.Replaceable("%player%", targetName)
            )
         );
         return true;
      }
   }

   @Nullable
   public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
      return null;
   }
}
