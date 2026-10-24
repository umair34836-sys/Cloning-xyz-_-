// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.MainCommandHandler
class MainCommandHandler {
    java.util.Map<java.lang.String, com.zetaplugins.lifestealz.commands.SubCommand> commands;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

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
            sendVersionMessage(sender);
            return true;
        }
        SubCommand subCommand = this.commands.get(args[0]);
        if (subCommand == null) {
            sendVersionMessage(sender);
            return true;
        }
        return subCommand.execute(sender, args);
    }

    private void sendVersionMessage(CommandSender sender) {
        sender.sendMessage(MessageUtils.getAndFormatMsg(false, "newVersionMsg", "\n&c<b><grey>></grey> LifeStealZ</b> <grey>v%version%</grey>\n\n&c <u><click:open_url:'https://wiki.lifestealz.com/'>Documentation</click></u>  &c<u><click:open_url:'https://strassburger.org/discord'>Support Discord</click></u>\n", new MessageUtils.Replaceable("%version%", this.plugin.getDescription().getVersion())));
    }

}
