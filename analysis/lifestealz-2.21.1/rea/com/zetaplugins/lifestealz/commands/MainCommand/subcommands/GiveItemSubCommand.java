// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GiveItemSubCommand
class GiveItemSubCommand {
    boolean $assertionsDisabled;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public GiveItemSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }
        if (args.length < 3) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        List<Player> targetPlayers = CommandUtils.parsePlayerName(args[1], true, this.plugin);
        if (targetPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!", new MessageUtils.Replaceable[0]));
            return false;
        }
        String item = args[2];
        if (item == null) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        Set<String> possibleItems = this.plugin.getRecipeManager().getItemIds();
        if (!possibleItems.contains(item)) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        int amount = args.length > 3 ? Integer.parseInt(args[3]) : 1;
        if (amount < 1) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        boolean silent = args.length > 4 && args[4].equals("silent");
        for (Player targetPlayer : targetPlayers) {
            if (targetPlayer == null && targetPlayers.size() == 1) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!", new MessageUtils.Replaceable[0]));
                return false;
            }
            if (targetPlayer != null || targetPlayers.size() <= 1) {
                if (!$assertionsDisabled && targetPlayer == null) {
                    throw new AssertionError();
                }
                ItemStack itemStack = CustomItemManager.createCustomItem(item, amount);
                HashMap<Integer, ItemStack> leftover = targetPlayer.getInventory().addItem(new ItemStack[]{itemStack});
                if (!leftover.isEmpty()) {
                    for (ItemStack dropItem : leftover.values()) {
                        targetPlayer.getWorld().dropItemNaturally(targetPlayer.getLocation(), dropItem);
                    }
                }
                if (!silent) {
                    targetPlayer.sendMessage(MessageUtils.getAndFormatMsg(true, "giveItem", "&7You received &c%amount% &7%item%!", new MessageUtils.Replaceable("%amount%", amount), new MessageUtils.Replaceable("%item%", CustomItemManager.getCustomItemData(item).getName())));
                }
            }
        }
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz giveItem <player> <item> [amount]";
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.giveitem");
    }

    // rea: m23clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 1
    //     .max locals 0
    // 
    //     .line 19
    //     ldc Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GiveItemSubCommand;
    //     invokevirtual java/lang/Class desiredAssertionStatus ()Z
    //     ifne :L1
    //     iconst_1
    //     goto :L2
    //   :L1
    //     .stack same
    //     iconst_0
    //   :L2
    //     .stack same1 int
    //     putstatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GiveItemSubCommand $assertionsDisabled Z
    //     return
    // .end method

}
