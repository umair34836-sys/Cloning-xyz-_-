// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.commands;
// rea: class com.zetaplugins.lifestealz.util.commands.CommandUtils
class CommandUtils {
    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 1
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/commands/CommandUtils;
    //     .line 15
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     return
    // .end method

    public static List<String> getDisplayOptions(List<String> options, String input) {
        return (List) options.stream().filter(option -> {
            return startsWithIgnoreCase(option, input);
        }).collect(Collectors.toList());
    }

    public static List<String> getDisplayOptions(Set<String> options, String input) {
        return (List) options.stream().filter(option -> {
            return startsWithIgnoreCase(option, input);
        }).collect(Collectors.toList());
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

    // rea: lambda$getDisplayOptions$1(java.lang.String, java.lang.String): boolean -> smali (java_body_unavailable)
    // .method private static synthetic lambda$getDisplayOptions$1(Ljava/lang/String;Ljava/lang/String;)Z
    //     .max stack 2
    //     .max locals 2
    // 
    //     .local 0 "input" Ljava/lang/String;
    //     .local 1 "option" Ljava/lang/String;
    //     .line 120
    //     aload 1
    //     aload 0
    //     invokestatic com/zetaplugins/lifestealz/util/commands/CommandUtils startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;)Z
    //     ireturn
    // .end method

    // rea: lambda$getDisplayOptions$2(java.lang.String, java.lang.String): boolean -> smali (java_body_unavailable)
    // .method private static synthetic lambda$getDisplayOptions$2(Ljava/lang/String;Ljava/lang/String;)Z
    //     .max stack 2
    //     .max locals 2
    // 
    //     .local 0 "input" Ljava/lang/String;
    //     .local 1 "option" Ljava/lang/String;
    //     .line 132
    //     aload 1
    //     aload 0
    //     invokestatic com/zetaplugins/lifestealz/util/commands/CommandUtils startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;)Z
    //     ireturn
    // .end method

    // rea: lambda$parseOfflinePlayer$0(com.zetaplugins.lifestealz.LifeStealZ, java.lang.String): org.bukkit.OfflinePlayer -> smali (java_body_unavailable)
    // .method private static synthetic lambda$parseOfflinePlayer$0(Lcom/zetaplugins/lifestealz/LifeStealZ;Ljava/lang/String;)Lorg/bukkit/OfflinePlayer;
    //     .max stack 2
    //     .max locals 2
    // 
    //     .local 0 "plugin" Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     .local 1 "name" Ljava/lang/String;
    //     .line 76
    //     aload 0
    //     invokevirtual com/zetaplugins/lifestealz/LifeStealZ getServer ()Lorg/bukkit/Server;
    //     aload 1
    //     invokeinterface org/bukkit/Server getOfflinePlayer (Ljava/lang/String;)Lorg/bukkit/OfflinePlayer;
    //     areturn
    // .end method

    public static List<OfflinePlayer> parseOfflinePlayer(String playerName, boolean allowStar, boolean allowPlus, LifeStealZ plugin) {
        List<OfflinePlayer> players = new ArrayList<>();
        if (playerName.equals("*") && allowStar) {
            players.addAll((Collection) plugin.getOfflinePlayerCache().getCachedData().stream().map(name -> {
                return plugin.getServer().getOfflinePlayer(name);
            }).collect(Collectors.toList()));
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

/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/util/commands/CommandUtils.class */
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

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static void throwPermissionError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!", new MessageUtils.Replaceable[0]);
        sender.sendMessage(msg);
    }

    public static void throwUsageError(CommandSender sender, String usage) {
        Component msg = MessageUtils.getAndFormatMsg(false, "usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", usage));
        sender.sendMessage(msg);
    }

}
