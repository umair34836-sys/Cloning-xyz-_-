// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand
class GracePeriodSubcommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public GracePeriodSubcommand(LifeStealZ plugin) {
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
        List<OfflinePlayer> targetPlayers = CommandUtils.parseOfflinePlayer(args[1], true, true, this.plugin);
        if (targetPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!", new MessageUtils.Replaceable[0]));
            return false;
        }
        String secondArg = args[2].toLowerCase();
        switch (secondArg) {
            case "skip":
                handleGracePeriod(sender, targetPlayers, GracePeriodAction.SKIP);
                return true;
            case "reset":
                handleGracePeriod(sender, targetPlayers, GracePeriodAction.RESET);
                return true;
            default:
                CommandUtils.throwUsageError(sender, getUsage());
                return false;
        }
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz graceperiod <player> <skip | reset>";
    }

    private void handleGracePeriod(CommandSender sender, List<OfflinePlayer> targetPlayers, GracePeriodAction gracePeriodAction) {
        Component andFormatMsg;
        Component andFormatMsg2;
        boolean zResetGracePeriod;
        int successCount = 0;
        OfflinePlayer lastSucessfulPlayer = null;
        for (OfflinePlayer targetPlayer : targetPlayers) {
            if (gracePeriodAction == GracePeriodAction.SKIP) {
                zResetGracePeriod = this.plugin.getGracePeriodManager().skipGracePeriod(targetPlayer);
            } else {
                zResetGracePeriod = this.plugin.getGracePeriodManager().resetGracePeriod(targetPlayer);
            }
            boolean success = zResetGracePeriod;
            if (success) {
                successCount++;
                lastSucessfulPlayer = targetPlayer;
            }
        }
        if (successCount == 0 || successCount > 1) {
            if (gracePeriodAction == GracePeriodAction.SKIP) {
                andFormatMsg = MessageUtils.getAndFormatMsg(true, "gracePeriodSkipSuccess", "&7Successfully skipped the grace period for &c%playerCount% &7players.", new MessageUtils.Replaceable("%playerCount%", String.valueOf(successCount)));
            } else {
                andFormatMsg = MessageUtils.getAndFormatMsg(true, "gracePeriodResetSuccess", "&7Successfully reset the grace period for &c%playerCount% &7players.", new MessageUtils.Replaceable("%playerCount%", String.valueOf(successCount)));
            }
            sender.sendMessage(andFormatMsg);
            return;
        }
        if (gracePeriodAction == GracePeriodAction.SKIP) {
            andFormatMsg2 = MessageUtils.getAndFormatMsg(true, "gracePeriodSkipSuccessOnePlayer", "&7Successfully skipped the grace period for &c%player%&7.", new MessageUtils.Replaceable("%player%", lastSucessfulPlayer.getName()));
        } else {
            andFormatMsg2 = MessageUtils.getAndFormatMsg(true, "gracePeriodResetSuccessOnePlayer", "&7Successfully reset the grace period for &c%player%&7.", new MessageUtils.Replaceable("%player%", lastSucessfulPlayer.getName()));
        }
        sender.sendMessage(andFormatMsg2);
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.graceperiod");
    }

    // rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand.GracePeriodAction
    class GracePeriodAction {
        com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand$GracePeriodAction[] $VALUES;
        com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand$GracePeriodAction RESET;
        com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand$GracePeriodAction SKIP;

        // rea: $values(): com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand$GracePeriodAction[] -> smali (java_body_unavailable)
        // .method private static synthetic $values()[Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     .max stack 4
        //     .max locals 0
        // 
        //     .line 115
        //     iconst_2
        //     anewarray com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction
        //     dup
        //     iconst_0
        //     getstatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction SKIP Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     aastore
        //     dup
        //     iconst_1
        //     getstatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction RESET Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     aastore
        //     areturn
        // .end method

        // rea: <init>(java.lang.String, int): void -> smali (java_body_unavailable)
        // .method private <init>(Ljava/lang/String;I)V
        //     .signature ()V
        //     .param synthetic null
        //     .param synthetic null
        //     .max stack 3
        //     .max locals 3
        // 
        //     .local 0 "this" Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     .line 115
        //     aload 0
        //     aload 1
        //     iload 2
        //     invokespecial java/lang/Enum <init> (Ljava/lang/String;I)V
        //     return
        // .end method

        // rea: m24clinit(): void -> smali (static_initializer_disassembly)
        // .method static <clinit>()V
        //     .max stack 4
        //     .max locals 0
        // 
        //     .line 116
        //     new com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction
        //     dup
        //     ldc "SKIP"
        //     iconst_0
        //     invokespecial com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction <init> (Ljava/lang/String;I)V
        //     putstatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction SKIP Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     .line 117
        //     new com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction
        //     dup
        //     ldc "RESET"
        //     iconst_1
        //     invokespecial com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction <init> (Ljava/lang/String;I)V
        //     putstatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction RESET Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     .line 115
        //     invokestatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction $values ()[Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     putstatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction $VALUES [Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     return
        // .end method

        // rea: valueOf(java.lang.String): com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand$GracePeriodAction -> smali (java_body_unavailable)
        // .method public static valueOf(Ljava/lang/String;)Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     .param mandated null
        //     .max stack 2
        //     .max locals 1
        // 
        //     .local 0 "name" Ljava/lang/String;
        //     .line 115
        //     ldc Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     aload 0
        //     invokestatic java/lang/Enum valueOf (Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
        //     checkcast com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction
        //     areturn
        // .end method

        // rea: values(): com.zetaplugins.lifestealz.commands.MainCommand.subcommands.GracePeriodSubcommand$GracePeriodAction[] -> smali (java_body_unavailable)
        // .method public static values()[Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     .max stack 1
        //     .max locals 0
        // 
        //     .line 115
        //     getstatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction $VALUES [Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     invokevirtual [Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction; clone ()Ljava/lang/Object;
        //     checkcast [Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/GracePeriodSubcommand$GracePeriodAction;
        //     areturn
        // .end method

    }

}
