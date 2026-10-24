// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.HeartsSubCommand
class HeartsSubCommand {
    boolean $assertionsDisabled;
    org.bukkit.configuration.file.FileConfiguration config;
    com.zetaplugins.lifestealz.LifeStealZ plugin;
    com.zetaplugins.lifestealz.storage.Storage storage;

    public HeartsSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.storage = plugin.getStorage();
    }

    private Component createConfirmMessage(String key, String defaultMessage, Map<String, String> replacements) {
        MessageUtils.Replaceable[] replaceables = (MessageUtils.Replaceable[]) replacements.entrySet().stream().map(entry -> {
            return new MessageUtils.Replaceable((String) entry.getKey(), (String) entry.getValue());
        }).toArray(x$0 -> {
            return new MessageUtils.Replaceable[x$0];
        });
        return MessageUtils.getAndFormatMsg(true, key, defaultMessage, replaceables);
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
        String optionTwo = args[1];
        List<String> possibleOptionTwo = List.of("add", "set", "remove", "get");
        if (optionTwo == null || !possibleOptionTwo.contains(optionTwo)) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        if (optionTwo.equals("get")) {
            OfflinePlayer player = CommandUtils.parseOfflinePlayer(args[2], false, true, this.plugin).get(0);
            if (player == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!", new MessageUtils.Replaceable[0]));
                return false;
            }
            PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
            int hearts = (int) (playerData.getMaxHealth() / 2.0d);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "getHearts", "&c%player% &7currently has &c%amount% &7hearts!", new MessageUtils.Replaceable("%player%", player.getName()), new MessageUtils.Replaceable("%amount%", hearts)));
            return true;
        }
        if (args.length < 4) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        int amount = Integer.parseInt(args[3]);
        if (amount < 0) {
            CommandUtils.throwUsageError(sender, getUsage());
            return false;
        }
        List<OfflinePlayer> targetPlayers = CommandUtils.parseOfflinePlayer(args[2], true, true, this.plugin);
        Iterator<OfflinePlayer> it = targetPlayers.iterator();
        while (it.hasNext()) {
            Player player2 = (OfflinePlayer) it.next();
            if (player2 == null && targetPlayers.size() == 1) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!", new MessageUtils.Replaceable[0]));
                return false;
            }
            if (player2 != null || targetPlayers.size() <= 1) {
                if (!$assertionsDisabled && player2 == null) {
                    throw new AssertionError();
                }
                PlayerData targetPlayerData = this.plugin.getStorage().load(player2.getUniqueId());
                double maxAllowedHearts = this.config.getInt("maxHearts") * 2;
                Player targetOnlinePlayer = player2.getPlayer();
                if (targetOnlinePlayer != null) {
                    maxAllowedHearts = MaxHeartsManager.getMaxHearts(targetOnlinePlayer, this.config);
                }
                switch (optionTwo) {
                    case "add":
                        if (this.config.getBoolean("enforceMaxHeartsOnAdminCommands") && targetPlayerData.getMaxHealth() + ((double) (amount * 2)) > maxAllowedHearts) {
                            sendHeartLimitReachedMessage(sender, maxAllowedHearts);
                            return false;
                        }
                        targetPlayerData.setMaxHealth(targetPlayerData.getMaxHealth() + ((double) (amount * 2)));
                        this.storage.save(targetPlayerData);
                        if (player2 instanceof Player) {
                            LifeStealZ.setMaxHealth(player2, targetPlayerData.getMaxHealth());
                        }
                        break;
                        break;
                    case "set":
                        if (amount == 0) {
                            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "connotSetHeartsBelowOrToZero", "&cYou can't set a player's hearts below or to 0!", new MessageUtils.Replaceable[0]));
                            return false;
                        }
                        if (this.config.getBoolean("enforceMaxHeartsOnAdminCommands") && amount * 2 > maxAllowedHearts) {
                            sendHeartLimitReachedMessage(sender, maxAllowedHearts);
                            return false;
                        }
                        targetPlayerData.setMaxHealth(amount * 2);
                        this.storage.save(targetPlayerData);
                        if (player2 instanceof Player) {
                            LifeStealZ.setMaxHealth(player2, targetPlayerData.getMaxHealth());
                        }
                        break;
                        break;
                    case "remove":
                        if ((targetPlayerData.getMaxHealth() / 2.0d) - ((double) amount) <= 0.0d) {
                            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "connotSetHeartsBelowOrToZero", "&cYou can't set a player's hearts below or to 0!", new MessageUtils.Replaceable[0]));
                            return false;
                        }
                        targetPlayerData.setMaxHealth(targetPlayerData.getMaxHealth() - ((double) (amount * 2)));
                        this.storage.save(targetPlayerData);
                        if (player2 instanceof Player) {
                            LifeStealZ.setMaxHealth(player2, targetPlayerData.getMaxHealth());
                        }
                        break;
                        break;
                }
            }
        }
        sendConfirmMessage(sender, optionTwo, targetPlayers, amount);
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz hearts <add | set | remove> <player> [amount]";
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.setlife");
    }

    // rea: lambda$createConfirmMessage$0(java.util.Map$Entry): com.zetaplugins.lifestealz.util.MessageUtils$Replaceable -> smali (java_body_unavailable)
    // .method private static synthetic lambda$createConfirmMessage$0(Ljava/util/Map$Entry;)Lcom/zetaplugins/lifestealz/util/MessageUtils$Replaceable;
    //     .max stack 4
    //     .max locals 1
    // 
    //     .local 0 "entry" Ljava/util/Map$Entry;
    //     .line 202
    //     new com/zetaplugins/lifestealz/util/MessageUtils$Replaceable
    //     dup
    //     aload 0
    //     invokeinterface java/util/Map$Entry getKey ()Ljava/lang/Object;
    //     checkcast java/lang/String
    //     aload 0
    //     invokeinterface java/util/Map$Entry getValue ()Ljava/lang/Object;
    //     checkcast java/lang/String
    //     invokespecial com/zetaplugins/lifestealz/util/MessageUtils$Replaceable <init> (Ljava/lang/String;Ljava/lang/String;)V
    //     areturn
    // .end method

    // rea: lambda$createConfirmMessage$1(int): com.zetaplugins.lifestealz.util.MessageUtils$Replaceable[] -> smali (java_body_unavailable)
    // .method private static synthetic lambda$createConfirmMessage$1(I)[Lcom/zetaplugins/lifestealz/util/MessageUtils$Replaceable;
    //     .max stack 1
    //     .max locals 1
    // 
    //     .local 0 "x$0" I
    //     .line 203
    //     iload 0
    //     anewarray com/zetaplugins/lifestealz/util/MessageUtils$Replaceable
    //     areturn
    // .end method

    // rea: m25clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 1
    //     .max locals 0
    // 
    //     .line 22
    //     ldc Lcom/zetaplugins/lifestealz/commands/MainCommand/subcommands/HeartsSubCommand;
    //     invokevirtual java/lang/Class desiredAssertionStatus ()Z
    //     ifne :L1
    //     iconst_1
    //     goto :L2
    //   :L1
    //     .stack same
    //     iconst_0
    //   :L2
    //     .stack same1 int
    //     putstatic com/zetaplugins/lifestealz/commands/MainCommand/subcommands/HeartsSubCommand $assertionsDisabled Z
    //     return
    // .end method

    private void sendConfirmMessage(CommandSender sender, String optionTwo, List<OfflinePlayer> targetPlayers, int changedAmount) {
        String messageKey;
        String str;
        String defaultMessage;
        String str2;
        String str3;
        Map<String, String> replacements = new HashMap<>();
        replacements.put("%amount%", String.valueOf(changedAmount));
        if (targetPlayers.size() == 1) {
            replacements.put("%player%", targetPlayers.get(0).getName());
        } else {
            replacements.put("%pamount%", String.valueOf(targetPlayers.size()));
        }
        switch (optionTwo) {
            case "add":
                messageKey = targetPlayers.size() == 1 ? "addHeartsConfirmSingle" : "addHeartsConfirmMultiple";
                if (targetPlayers.size() == 1) {
                    str3 = "&7You successfully added &c%amount% &7hearts to &c%player%!";
                } else {
                    str3 = "&7Successfully added &c%amount% &7hearts to &c%pamount% players";
                }
                defaultMessage = str3;
                break;
            case "set":
                messageKey = targetPlayers.size() == 1 ? "setHeartsConfirmSingle" : "setHeartsConfirmMultiple";
                if (targetPlayers.size() == 1) {
                    str2 = "&7Successfully set &c%player%'s &7hearts to &c%amount% hearts";
                } else {
                    str2 = "&7Successfully set &c%pamount% players' &7hearts to &c%amount% hearts";
                }
                defaultMessage = str2;
                break;
            case "remove":
                messageKey = targetPlayers.size() == 1 ? "removeHeartsConfirmSingle" : "removeHeartsConfirmMultiple";
                if (targetPlayers.size() == 1) {
                    str = "&7Successfully removed &c%amount% &7hearts from &c%player%";
                } else {
                    str = "&7Successfully removed &c%amount% &7hearts from &c%pamount% players";
                }
                defaultMessage = str;
                break;
            default:
                throw new IllegalArgumentException("Invalid option: " + optionTwo);
        }
        Component confirmMessage = createConfirmMessage(messageKey, defaultMessage, replacements);
        sender.sendMessage(confirmMessage);
    }

    private void sendHeartLimitReachedMessage(CommandSender sender, double maxHearts) {
        Component maxHeartsMsg = MessageUtils.getAndFormatMsg(true, "maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new MessageUtils.Replaceable("%limit%", ((int) (maxHearts / 2.0d))));
        sender.sendMessage(maxHeartsMsg);
    }

}
