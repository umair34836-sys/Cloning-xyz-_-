package com.zetaplugins.lifestealz.commands.MainCommand;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import com.zetaplugins.zetacore.annotations.AutoRegisterTabCompleter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterTabCompleter(
   command = "lifestealz"
)
public final class MainTabCompleter implements TabCompleter {
   private final LifeStealZ plugin;

   public MainTabCompleter(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @Nullable
   public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
      if (args.length == 1) {
         return this.getFirstArgOptions(sender, args);
      } else if (args.length == 2) {
         return this.getSecondArgOptions(sender, args);
      } else if (args.length == 3) {
         return this.getThirdArgOptions(sender, args);
      } else if (args.length == 4) {
         return this.getFourthArgOptions(args);
      } else {
         return args.length == 5 ? this.getFifthArgOptions(args) : List.of();
      }
   }

   private List<String> getFirstArgOptions(CommandSender sender, String[] args) {
      String input = args[0].toLowerCase();
      List<String> options = new ArrayList<>();
      if (sender.hasPermission("lifestealz.admin.reload")) {
         options.add("reload");
      }

      if (sender.hasPermission("lifestealz.admin.debug")) {
         options.add("debug");
      }

      if (sender.hasPermission("lifestealz.admin.setlife")) {
         options.add("hearts");
      }

      if (sender.hasPermission("lifestealz.admin.giveitem")) {
         options.add("giveItem");
      }

      if (sender.hasPermission("lifestealz.admin.bypasscheck")) {
         options.add("checkbypass");
      }

      if (sender.hasPermission("lifestealz.viewrecipes")) {
         options.add("recipe");
      }

      if (sender.hasPermission("lifestealz.help")) {
         options.add("help");
      }

      if (sender.hasPermission("lifestealz.managedata")) {
         options.add("data");
      }

      if (sender.hasPermission("lifestealz.graceperiod")) {
         options.add("graceperiod");
      }

      return CommandUtils.getDisplayOptions(options, input);
   }

   private List<String> getSecondArgOptions(CommandSender sender, String[] args) {
      String input = args[1].toLowerCase();
      String var4 = args[0];
      switch (var4) {
         case "hearts":
            return CommandUtils.getDisplayOptions(List.of("add", "set", "remove", "get"), input);
         case "giveItem":
            return CommandUtils.getDisplayOptions(CommandUtils.getPlayersTabCompletion(true, this.plugin), input);
         case "graceperiod":
            return CommandUtils.getDisplayOptions(CommandUtils.getOfflinePlayersTabCompletion(true, true, this.plugin), input);
         case "recipe":
            return CommandUtils.getDisplayOptions(this.plugin.getRecipeManager().getItemIds(), input);
         case "data":
            if (sender.hasPermission("lifestealz.managedata")) {
               return CommandUtils.getDisplayOptions(List.of("import", "export"), input);
            }
         default:
            return List.of();
         case "checkbypass":
            return CommandUtils.getDisplayOptions(CommandUtils.getPlayersTabCompletion(false, this.plugin), input);
         case "debug":
            return CommandUtils.getDisplayOptions(List.of("upload", "generate"), input);
         case "dev":
            return CommandUtils.getDisplayOptions(
               List.of(
                  "giveForbiddenitem",
                  "isInGracePeriod",
                  "setFirstJoinDate",
                  "refreshCaches",
                  "crash",
                  "cleardatabase",
                  "giveAnimationTotem",
                  "getEffectivePerms"
               ),
               input
            );
      }
   }

   private List<String> getThirdArgOptions(CommandSender sender, String[] args) {
      String input = args[2].toLowerCase();
      String var4 = args[0];
      switch (var4) {
         case "hearts":
            if ("get".equals(args[1])) {
               return CommandUtils.getDisplayOptions(CommandUtils.getOfflinePlayersTabCompletion(false, true, this.plugin), input);
            }

            return CommandUtils.getDisplayOptions(CommandUtils.getOfflinePlayersTabCompletion(true, true, this.plugin), input);
         case "graceperiod":
            return CommandUtils.getDisplayOptions(List.of("skip", "reset"), input);
         case "giveItem":
            return CommandUtils.getDisplayOptions(this.plugin.getRecipeManager().getItemIds(), input);
         case "recipe":
            return CommandUtils.getDisplayOptions(this.plugin.getRecipeManager().getRecipeIds(args[1]), input);
         case "data":
            if ("import".equals(args[1]) && sender.hasPermission("lifestealz.managedata")) {
               return CommandUtils.getDisplayOptions(this.getCSVFiles(), input);
            }
         default:
            return List.of();
      }
   }

   private List<String> getFourthArgOptions(String[] args) {
      return !"hearts".equals(args[0]) && !"giveItem".equals(args[0]) ? List.of() : List.of("1", "32", "64");
   }

   private List<String> getFifthArgOptions(String[] args) {
      String input = args[4].toLowerCase();
      return "giveItem".equals(args[0]) ? CommandUtils.getDisplayOptions(List.of("silent"), input) : List.of("");
   }

   private List<String> getCSVFiles() {
      List<String> csvFiles = new ArrayList<>();
      File pluginFolder = this.plugin.getDataFolder();
      File[] files = pluginFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
      if (files != null) {
         for (File file : files) {
            csvFiles.add(file.getName());
         }
      }

      return csvFiles;
   }
}
