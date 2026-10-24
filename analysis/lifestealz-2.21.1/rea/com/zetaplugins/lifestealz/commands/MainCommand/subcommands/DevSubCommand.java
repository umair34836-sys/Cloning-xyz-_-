// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.DevSubCommand
class DevSubCommand {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public DevSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }
        if (args.length < 2) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        String optionTwo = args[1];
        if (optionTwo.equals("giveForbiddenitem")) {
            if (!(sender instanceof Player)) {
                return false;
            }
            ((Player) sender).getInventory().addItem(new ItemStack[]{new CustomItem(Material.BARRIER).makeForbidden().getItemStack()});
            return true;
        }
        if (optionTwo.equals("isInGracePeriod")) {
            if (args.length < 3 && !(sender instanceof Player)) {
                CommandUtils.throwUsageError(sender, "/lifestealz dev isInGracePeriod [player]");
                return false;
            }
            Player player = args.length > 2 ? this.plugin.getServer().getPlayer(args[2]) : (Player) sender;
            if (player == null) {
                CommandUtils.throwUsageError(sender, "/lifestealz dev isInGracePeriod [player]");
                return false;
            }
            GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
            String gracePeriodColor = gracePeriodManager.isInGracePeriod(player) ? "&a" : "&c";
            String gracePeriodEnabledColor = gracePeriodManager.isEnabled() ? "&a" : "&c";
            sender.sendMessage(MessageUtils.formatMsg("&7Is &c" + player.getName() + " &7in grace period? " + gracePeriodColor + gracePeriodManager.isInGracePeriod(player) + (gracePeriodManager.isInGracePeriod(player) ? " &7(" + String.valueOf(gracePeriodManager.getGracePeriodRemaining(player).orElse(-1)) + "remaining)" : "") + "\n&7Grace period enabled: " + gracePeriodEnabledColor + gracePeriodManager.isEnabled() + "&7", new MessageUtils.Replaceable[0]));
        }
        if (optionTwo.equals("setFirstJoinDate")) {
            if (args.length < 3 && !(sender instanceof Player)) {
                CommandUtils.throwUsageError(sender, "/lifestealz dev setFirstJoinDate [player]");
                return false;
            }
            Player player2 = args.length > 2 ? this.plugin.getServer().getPlayer(args[2]) : (Player) sender;
            if (player2 == null) {
                CommandUtils.throwUsageError(sender, "/lifestealz dev setFirstJoinDate [player]");
                return false;
            }
            long newFirstJoin = System.currentTimeMillis();
            PlayerData playerData = this.plugin.getStorage().load(player2.getUniqueId());
            playerData.setFirstJoin(newFirstJoin);
            this.plugin.getStorage().save(playerData);
            this.plugin.getGracePeriodManager().startGracePeriod(player2);
        }
        if (optionTwo.equals("refreshCaches")) {
            this.plugin.getEliminatedPlayersCache().reloadCache();
            sender.sendMessage(MessageUtils.formatMsg("&7Caches reloaded!", new MessageUtils.Replaceable[0]));
        }
        if (optionTwo.equals("crash")) {
            throw new RuntimeException("Crash test");
        }
        if (optionTwo.equals("cleardatabase")) {
            this.plugin.getStorage().clearDatabase();
            sender.sendMessage(MessageUtils.formatMsg("&7Database cleared!", new MessageUtils.Replaceable[0]));
        }
        if (optionTwo.equals("giveAnimationTotem")) {
            if (!(sender instanceof Player)) {
                return false;
            }
            ((Player) sender).getInventory().addItem(new ItemStack[]{CustomItemManager.createHeartAnimationTotem()});
            return true;
        }
        if (!optionTwo.equals("getEffectivePerms") || !(sender instanceof Player)) {
            return false;
        }
        Player player3 = (Player) sender;
        String effectivePerms = (String) player3.getEffectivePermissions().stream().map(permission -> {
            return permission.getPermission() + (permission.getValue() ? "" : " (denied)");
        }).reduce((perm1, perm2) -> {
            return perm1 + ", " + perm2;
        }).orElse("No permissions found");
        sender.sendMessage(MessageUtils.formatMsg("&7Effective permissions for " + player3.getName() + ": " + effectivePerms, new MessageUtils.Replaceable[0]));
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz dev <giveForbiddenitem | isInGracePeriod | setFirstJoinDate | refreshCaches | crash | cleardatabase | giveAnimationTotem | getEffectivePerms>";
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.isOp();
    }

    // rea: lambda$execute$0(org.bukkit.permissions.PermissionAttachmentInfo): java.lang.String -> smali (java_body_unavailable)
    // .method private static synthetic lambda$execute$0(Lorg/bukkit/permissions/PermissionAttachmentInfo;)Ljava/lang/String;
    //     .max stack 2
    //     .max locals 1
    // 
    //     .local 0 "permission" Lorg/bukkit/permissions/PermissionAttachmentInfo;
    //     .line 124
    //     aload 0
    //     invokevirtual org/bukkit/permissions/PermissionAttachmentInfo getPermission ()Ljava/lang/String;
    //     aload 0
    //     invokevirtual org/bukkit/permissions/PermissionAttachmentInfo getValue ()Z
    //     ifeq :L1
    //     ldc ""
    //     goto :L2
    //   :L1
    //     .stack same1 java/lang/String
    //     ldc " (denied)"
    //   :L2
    //     .stack full
    //         stack 0 java/lang/String
    //         stack 1 java/lang/String
    //         local 0 org/bukkit/permissions/PermissionAttachmentInfo
    //     .end stack
    //     invokedynamic makeConcatWithConstants (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //         .handle invoke-static java/lang/invoke/StringConcatFactory makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;
    //         .arg 0 "\u0001\u0001"
    //     .end invokedynamic
    //     areturn
    // .end method

    // rea: lambda$execute$1(java.lang.String, java.lang.String): java.lang.String -> smali (java_body_unavailable)
    // .method private static synthetic lambda$execute$1(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //     .max stack 2
    //     .max locals 2
    // 
    //     .local 0 "perm1" Ljava/lang/String;
    //     .local 1 "perm2" Ljava/lang/String;
    //     .line 125
    //     aload 0
    //     aload 1
    //     invokedynamic makeConcatWithConstants (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //         .handle invoke-static java/lang/invoke/StringConcatFactory makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;
    //         .arg 0 "\u0001, \u0001"
    //     .end invokedynamic
    //     areturn
    // .end method

}
