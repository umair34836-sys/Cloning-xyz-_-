package com.zetaplugins.lifestealz.util.commands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.MessageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandUtils {
   public static List<Player> parsePlayerName(String playerName, boolean allowPlus, LifeStealZ plugin) {
      List<Player> players = new ArrayList<>();
      if (playerName.equals("+") && allowPlus) {
         players.addAll(plugin.getServer().getOnlinePlayers());
      } else {
         Player player = plugin.getServer().getPlayer(playerName);
         players.add(player);
      }

      return players;
   }

   public static List<Player> parsePlayerName(String playerName, boolean allowPlus) {
      return parsePlayerName(playerName, allowPlus, LifeStealZ.getInstance());
   }

   public static List<String> getPlayersTabCompletion(boolean allowPlus, LifeStealZ plugin) {
      List<String> playerNames = new ArrayList<>();
      if (allowPlus) {
         playerNames.add("+");
      }

      for (Player player : plugin.getServer().getOnlinePlayers()) {
         playerNames.add(player.getName());
      }

      return playerNames;
   }

   public static List<String> getPlayersTabCompletion(boolean allowStar) {
      return getPlayersTabCompletion(allowStar, LifeStealZ.getInstance());
   }

   public static List<OfflinePlayer> parseOfflinePlayer(String playerName, boolean allowStar, boolean allowPlus, LifeStealZ plugin) {
      List<OfflinePlayer> players = new ArrayList<>();
      if (playerName.equals("*") && allowStar) {
         players.addAll(
            plugin.getOfflinePlayerCache().getCachedData().stream().map(name -> plugin.getServer().getOfflinePlayer(name)).collect(Collectors.toList())
         );
      } else if (playerName.equals("+") && allowPlus) {
         players.addAll(plugin.getServer().getOnlinePlayers());
      } else {
         OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
         players.add(player);
      }

      return players;
   }

   public static List<OfflinePlayer> parseOfflinePlayer(String playerName, boolean allowStar, boolean allowPlus) {
      return parseOfflinePlayer(playerName, allowStar, allowPlus, LifeStealZ.getInstance());
   }

   public static List<String> getOfflinePlayersTabCompletion(boolean allowStar, boolean allowPlus, LifeStealZ plugin) {
      List<String> playerNames = new ArrayList<>();
      if (allowStar) {
         playerNames.add("*");
      }

      if (allowPlus) {
         playerNames.add("+");
      }

      playerNames.addAll(plugin.getOfflinePlayerCache().getCachedData());
      return playerNames;
   }

   public static void throwUsageError(CommandSender sender, String usage) {
      Component msg = MessageUtils.getAndFormatMsg(false, "usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", usage));
      sender.sendMessage(msg);
   }

   public static void throwPermissionError(CommandSender sender) {
      Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
      sender.sendMessage(msg);
   }

   public static List<String> getDisplayOptions(List<String> options, String input) {
      return options.stream().filter(option -> startsWithIgnoreCase(option, input)).collect(Collectors.toList());
   }

   public static List<String> getDisplayOptions(Set<String> options, String input) {
      return options.stream().filter(option -> startsWithIgnoreCase(option, input)).collect(Collectors.toList());
   }

   private static boolean startsWithIgnoreCase(String str, String prefix) {
      return str.regionMatches(true, 0, prefix, 0, prefix.length());
   }
}
