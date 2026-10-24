package com.zetaplugins.lifestealz.commands.MainCommand;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.BypassStatusSubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.DataSubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.DebugSubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.DevSubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GiveItemSubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.HeartsSubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.HelpSubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.RecipeSubCommand;
import com.zetaplugins.lifestealz.commands.MainCommand.subcommands.ReloadSubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.zetacore.annotations.AutoRegisterCommand;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AutoRegisterCommand(
   command = "lifestealz"
)
public final class MainCommandHandler implements CommandExecutor {
   private final LifeStealZ plugin;
   private final Map<String, SubCommand> commands = new HashMap<>();

   public MainCommandHandler(LifeStealZ plugin) {
      this.plugin = plugin;
      this.commands.put("reload", new ReloadSubCommand(plugin));
      this.commands.put("help", new HelpSubCommand(plugin));
      this.commands.put("recipe", new RecipeSubCommand(plugin));
      this.commands.put("hearts", new HeartsSubCommand(plugin));
      this.commands.put("giveItem", new GiveItemSubCommand(plugin));
      this.commands.put("data", new DataSubCommand(plugin));
      this.commands.put("graceperiod", new GracePeriodSubcommand(plugin));
      this.commands.put("checkbypass", new BypassStatusSubCommand(plugin));
      this.commands.put("dev", new DevSubCommand(plugin));
      this.commands.put("debug", new DebugSubCommand(plugin));
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
      if (args.length == 0) {
         this.sendVersionMessage(sender);
         return true;
      } else {
         SubCommand subCommand = this.commands.get(args[0]);
         if (subCommand == null) {
            this.sendVersionMessage(sender);
            return true;
         } else {
            return subCommand.execute(sender, args);
         }
      }
   }

   private void sendVersionMessage(CommandSender sender) {
      sender.sendMessage(
         MessageUtils.getAndFormatMsg(
            false,
            "newVersionMsg",
            "\n&c<b><grey>></grey> LifeStealZ</b> <grey>v%version%</grey>\n\n&c <u><click:open_url:'https://wiki.lifestealz.com/'>Documentation</click></u>  &c<u><click:open_url:'https://strassburger.org/discord'>Support Discord</click></u>\n",
            new MessageUtils.Replaceable("%version%", this.plugin.getDescription().getVersion())
         )
      );
   }
}
