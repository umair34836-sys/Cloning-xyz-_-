// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.MainTabCompleter
class MainTabCompleter {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public MainTabCompleter(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private List<String> getCSVFiles() {
        List<String> csvFiles = new ArrayList<>();
        File pluginFolder = this.plugin.getDataFolder();
        File[] files = pluginFolder.listFiles((dir, name) -> {
            return name.toLowerCase().endsWith(".csv");
        });
        if (files != null) {
            for (File file : files) {
                csvFiles.add(file.getName());
            }
        }
        return csvFiles;
    }

    private List<String> getFifthArgOptions(String[] args) {
        String input = args[4].toLowerCase();
        if ("giveItem".equals(args[0])) {
            return CommandUtils.getDisplayOptions((List<String>) List.of("silent"), input);
        }
        return List.of("");
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

    private List<String> getFourthArgOptions(String[] args) {
        if ("hearts".equals(args[0]) || "giveItem".equals(args[0])) {
            return List.of("1", "32", "64");
        }
        return List.of();
    }

    private List<String> getSecondArgOptions(CommandSender sender, String[] args) {
        String input = args[1].toLowerCase();
        switch (args[0]) {
            case "hearts":
                return CommandUtils.getDisplayOptions((List<String>) List.of("add", "set", "remove", "get"), input);
            case "giveItem":
                return CommandUtils.getDisplayOptions(CommandUtils.getPlayersTabCompletion(true, this.plugin), input);
            case "graceperiod":
                return CommandUtils.getDisplayOptions(CommandUtils.getOfflinePlayersTabCompletion(true, true, this.plugin), input);
            case "recipe":
                return CommandUtils.getDisplayOptions(this.plugin.getRecipeManager().getItemIds(), input);
            case "data":
                if (sender.hasPermission("lifestealz.managedata")) {
                    return CommandUtils.getDisplayOptions((List<String>) List.of("import", "export"), input);
                }
                break;
            case "checkbypass":
                return CommandUtils.getDisplayOptions(CommandUtils.getPlayersTabCompletion(false, this.plugin), input);
            case "debug":
                return CommandUtils.getDisplayOptions((List<String>) List.of("upload", "generate"), input);
            case "dev":
                return CommandUtils.getDisplayOptions((List<String>) List.of("giveForbiddenitem", "isInGracePeriod", "setFirstJoinDate", "refreshCaches", "crash", "cleardatabase", "giveAnimationTotem", "getEffectivePerms"), input);
        }
        return List.of();
    }

    private List<String> getThirdArgOptions(CommandSender sender, String[] args) {
        String input = args[2].toLowerCase();
        switch (args[0]) {
            case "hearts":
                return "get".equals(args[1]) ? CommandUtils.getDisplayOptions(CommandUtils.getOfflinePlayersTabCompletion(false, true, this.plugin), input) : CommandUtils.getDisplayOptions(CommandUtils.getOfflinePlayersTabCompletion(true, true, this.plugin), input);
            case "graceperiod":
                return CommandUtils.getDisplayOptions((List<String>) List.of("skip", "reset"), input);
            case "giveItem":
                return CommandUtils.getDisplayOptions(this.plugin.getRecipeManager().getItemIds(), input);
            case "recipe":
                return CommandUtils.getDisplayOptions(this.plugin.getRecipeManager().getRecipeIds(args[1]), input);
            case "data":
                if ("import".equals(args[1]) && sender.hasPermission("lifestealz.managedata")) {
                    return CommandUtils.getDisplayOptions(getCSVFiles(), input);
                }
                break;
        }
        return List.of();
    }

    // rea: lambda$getCSVFiles$0(java.io.File, java.lang.String): boolean -> smali (java_body_unavailable)
    // .method private static synthetic lambda$getCSVFiles$0(Ljava/io/File;Ljava/lang/String;)Z
    //     .max stack 2
    //     .max locals 2
    // 
    //     .local 0 "dir" Ljava/io/File;
    //     .local 1 "name" Ljava/lang/String;
    //     .line 116
    //     aload 1
    //     invokevirtual java/lang/String toLowerCase ()Ljava/lang/String;
    //     ldc ".csv"
    //     invokevirtual java/lang/String endsWith (Ljava/lang/String;)Z
    //     ireturn
    // .end method

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return getFirstArgOptions(sender, args);
        }
        if (args.length == 2) {
            return getSecondArgOptions(sender, args);
        }
        if (args.length == 3) {
            return getThirdArgOptions(sender, args);
        }
        if (args.length == 4) {
            return getFourthArgOptions(args);
        }
        return args.length == 5 ? getFifthArgOptions(args) : List.of();
    }

}
