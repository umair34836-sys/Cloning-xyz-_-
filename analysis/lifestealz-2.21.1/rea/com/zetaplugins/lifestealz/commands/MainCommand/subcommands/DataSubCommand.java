// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.DataSubCommand
class DataSubCommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;
    com.zetaplugins.lifestealz.storage.Storage storage;

    public DataSubCommand(LifeStealZ plugin) {
        this.storage = plugin.getStorage();
        this.plugin = plugin;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }
        if (args.length < 3) {
            CommandUtils.throwUsageError(sender, "/lifestealz data <import | export> <file>");
            return false;
        }
        String optionTwo = args[1];
        String fileName = args[2];
        if (optionTwo.equals("export")) {
            return handleExport(sender, fileName);
        }
        if (optionTwo.equals("import")) {
            return handleImport(sender, fileName);
        }
        CommandUtils.throwUsageError(sender, getUsage());
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz data <import | export> <file>";
    }

    private boolean handleExport(CommandSender sender, String fileName) {
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "exportingData", "&7Exporting player data...", new MessageUtils.Replaceable[0]));
        BukkitTask task = this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            String filePath = this.storage.export(fileName);
            if (filePath != null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "exportData", "&7Successfully exported player data to &c%file%", new MessageUtils.Replaceable("%file%", filePath)));
            } else {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "exportDataError", "&cFailed to export data! Check console for details.", new MessageUtils.Replaceable[0]));
            }
        });
        this.plugin.getAsyncTaskManager().addTask(task);
        return true;
    }

    private boolean handleImport(CommandSender sender, String fileName) {
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "importingData", "&7Importing player data...", new MessageUtils.Replaceable[0]));
        BukkitTask task = this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.storage.importData(fileName);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "importData", "&7Successfully imported &c%file%.csv&7!\n&cPlease restart the server, to ensure flawless migration!", new MessageUtils.Replaceable("%file%", fileName)));
        });
        this.plugin.getAsyncTaskManager().addTask(task);
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.managedata");
    }

    // rea: lambda$handleExport$0(java.lang.String, org.bukkit.command.CommandSender): void -> smali (java_body_unavailable)
    // .method private synthetic lambda$handleExport$0(Ljava/lang/String;Lorg/bukkit/command/CommandSender;)V
    //     .max stack 11
    //     .max locals 4
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/DataSubCommand;
    //     .local 1 "fileName" Ljava/lang/String;
    //     .local 2 "sender" Lorg/bukkit/command/CommandSender;
    //     .line 54
    //     aload 0
    //     getfield com/zetaplugins/lifestealz/commands/MainCommand/subcommands/DataSubCommand storage Lcom/zetaplugins/lifestealz/storage/Storage;
    //     aload 1
    //     invokevirtual com/zetaplugins/lifestealz/storage/Storage export (Ljava/lang/String;)Ljava/lang/String;
    //     astore 3
    //     .local 3 "filePath" Ljava/lang/String;
    //     .line 55
    //     aload 3
    //     ifnull :L2
    //     .line 56
    //     aload 2
    //     iconst_1
    //     ldc "exportData"
    //     ldc "&7Successfully exported player data to &c%file%"
    //     iconst_1
    //     anewarray com/zetaplugins/lifestealz/util/MessageUtils$Replaceable
    //     dup
    //     iconst_0
    //     new com/zetaplugins/lifestealz/util/MessageUtils$Replaceable
    //     dup
    //     ldc "%file%"
    //     aload 3
    //     invokespecial com/zetaplugins/lifestealz/util/MessageUtils$Replaceable <init> (Ljava/lang/String;Ljava/lang/String;)V
    //     aastore
    //     invokestatic com/zetaplugins/lifestealz/util/MessageUtils getAndFormatMsg (ZLjava/lang/String;Ljava/lang/String;[Lcom/zetaplugins/lifestealz/util/MessageUtils$Replaceable;)Lnet/kyori/adventure/text/Component;
    //     invokeinterface org/bukkit/command/CommandSender sendMessage (Lnet/kyori/adventure/text/Component;)V
    //     goto :L4
    //   :L2
    //     .line 63
    //     .stack append
    //         java/lang/String
    //     .end stack
    //     aload 2
    //     iconst_0
    //     ldc "exportDataError"
    //     ldc "&cFailed to export data! Check console for details."
    //     iconst_0
    //     anewarray com/zetaplugins/lifestealz/util/MessageUtils$Replaceable
    //     invokestatic com/zetaplugins/lifestealz/util/MessageUtils getAndFormatMsg (ZLjava/lang/String;Ljava/lang/String;[Lcom/zetaplugins/lifestealz/util/MessageUtils$Replaceable;)Lnet/kyori/adventure/text/Component;
    //     invokeinterface org/bukkit/command/CommandSender sendMessage (Lnet/kyori/adventure/text/Component;)V
    //   :L4
    //     .line 69
    //     .stack same
    //     return
    // .end method

    // rea: lambda$handleImport$1(java.lang.String, org.bukkit.command.CommandSender): void -> smali (java_body_unavailable)
    // .method private synthetic lambda$handleImport$1(Ljava/lang/String;Lorg/bukkit/command/CommandSender;)V
    //     .max stack 11
    //     .max locals 3
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/DataSubCommand;
    //     .local 1 "fileName" Ljava/lang/String;
    //     .local 2 "sender" Lorg/bukkit/command/CommandSender;
    //     .line 81
    //     aload 0
    //     getfield com/zetaplugins/lifestealz/commands/MainCommand/subcommands/DataSubCommand storage Lcom/zetaplugins/lifestealz/storage/Storage;
    //     aload 1
    //     invokevirtual com/zetaplugins/lifestealz/storage/Storage importData (Ljava/lang/String;)V
    //     .line 82
    //     aload 2
    //     iconst_1
    //     ldc "importData"
    //     ldc "&7Successfully imported &c%file%.csv&7!\n&cPlease restart the server, to ensure flawless migration!"
    //     iconst_1
    //     anewarray com/zetaplugins/lifestealz/util/MessageUtils$Replaceable
    //     dup
    //     iconst_0
    //     new com/zetaplugins/lifestealz/util/MessageUtils$Replaceable
    //     dup
    //     ldc "%file%"
    //     aload 1
    //     invokespecial com/zetaplugins/lifestealz/util/MessageUtils$Replaceable <init> (Ljava/lang/String;Ljava/lang/String;)V
    //     aastore
    //     invokestatic com/zetaplugins/lifestealz/util/MessageUtils getAndFormatMsg (ZLjava/lang/String;Ljava/lang/String;[Lcom/zetaplugins/lifestealz/util/MessageUtils$Replaceable;)Lnet/kyori/adventure/text/Component;
    //     invokeinterface org/bukkit/command/CommandSender sendMessage (Lnet/kyori/adventure/text/Component;)V
    //     .line 88
    //     return
    // .end method

}
