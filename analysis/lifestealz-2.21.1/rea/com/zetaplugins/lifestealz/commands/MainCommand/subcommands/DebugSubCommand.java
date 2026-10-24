// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;
// rea: class com.zetaplugins.lifestealz.commands.MainCommand.subcommands.DebugSubCommand
class DebugSubCommand {
    java.lang.String MODRINTH_ID;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public DebugSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }
        if (args.length == 1) {
            CommandUtils.throwUsageError(sender, "/lifestealz debug <upload | generate>");
            return true;
        }
        String subCommand = args[1].toLowerCase();
        switch (subCommand) {
            case "upload":
                return handleUpload(sender, args.length > 2 && args[2].equalsIgnoreCase("confirm"));
            case "generate":
                return handleGenerate(sender);
            default:
                CommandUtils.throwUsageError(sender, "/lifestealz debug <upload | generate>");
                return true;
        }
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public String getUsage() {
        return "/lifestealz debug";
    }

    private boolean handleGenerate(CommandSender sender) {
        DebugReport report = ReportDataCollector.collect(MODRINTH_ID, this.plugin, this.plugin.getPluginFile(), this.plugin.getConfigManager().getConfigsMap());
        File reportJson = new File("debug-report.json");
        File reportTxt = new File("debug-report.txt");
        try {
            ReportFileWriter.writeJsonReportToFile(report, reportJson);
            ReportFileWriter.writeTextReportToFile(report, reportTxt);
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "debugFileCreated", "&8 [&a✔&8] &7Saved debug data to the following files:\n<click:COPY_TO_CLIPBOARD:%jsonPath%><#8b73f6>%jsonPath%</click>\n<click:COPY_TO_CLIPBOARD:%txtPath%><#8b73f6>%txtPath%</click>", new MessageUtils.Replaceable("%jsonPath%", reportJson.getAbsolutePath()), new MessageUtils.Replaceable("%txtPath%", reportTxt.getAbsolutePath())));
            return true;
        } catch (IOException e) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "debugFailedToCreateFile", "&cFailed to create debug report file: %error%", new MessageUtils.Replaceable("%error%", e.getMessage())));
            this.plugin.getLogger().log(Level.SEVERE, "Failed to write debug report", (Throwable) e);
            return false;
        }
    }

    private boolean handleUpload(CommandSender sender, boolean confirmed) {
        if (!confirmed) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "debugUploadConfirm", "\n <#8b73f6>&lUploading Debug Report&r\n\n&7 Are you sure you want to upload the debug report? By confirming, you accept our <u><click:OPEN_URL:https://debug.zetaplugins.com/privacy>Privacy Policy</click></u>.\n\n <#8b73f6><click:RUN_COMMAND:%command%>[Click Here]</click> &r&8or run <u>%command%</u>\n", new MessageUtils.Replaceable("%command%", "/lifestealz debug upload confirm")));
            return true;
        }
        DebugReport report = ReportDataCollector.collect(MODRINTH_ID, this.plugin, this.plugin.getPluginFile(), this.plugin.getConfigManager().getConfigsMap());
        String url = ZetaDebugReportUploader.uploadReport(report, this.plugin);
        if (url == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "debugFailedUpload", "&cFailed to upload debug report!", new MessageUtils.Replaceable[0]));
            return false;
        }
        String formattedUrl = url.replaceAll("\\\\", "");
        sender.sendMessage(MessageUtils.getAndFormatMsg(false, "debugUploadSuccess", "&8 [&a✔&8] &7Debug report uploaded successfully! You can view it here:\n <u><#8b73f6><click:OPEN_URL:%url%>%url%</click></u>\n", new MessageUtils.Replaceable("%url%", formattedUrl)));
        return true;
    }

    @Override // com.zetaplugins.lifestealz.commands.SubCommand
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.debug");
    }

}
