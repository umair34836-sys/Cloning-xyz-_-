package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import com.zetaplugins.zetacore.debug.ReportDataCollector;
import com.zetaplugins.zetacore.debug.ReportFileWriter;
import com.zetaplugins.zetacore.debug.data.DebugReport;
import com.zetaplugins.zetacore.debug.uploader.ZetaDebugReportUploader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;

public final class DebugSubCommand implements SubCommand {
   private static final String MODRINTH_ID = "l8Uv7FzS";
   private final LifeStealZ plugin;

   public DebugSubCommand(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean execute(CommandSender sender, String[] args) {
      if (!this.hasPermission(sender)) {
         CommandUtils.throwPermissionError(sender);
         return false;
      } else if (args.length == 1) {
         CommandUtils.throwUsageError(sender, "/lifestealz debug <upload | generate>");
         return true;
      } else {
         String subCommand = args[1].toLowerCase();

         return switch (subCommand) {
            case "upload" -> this.handleUpload(sender, args.length > 2 && args[2].equalsIgnoreCase("confirm"));
            case "generate" -> this.handleGenerate(sender);
            default -> {
               CommandUtils.throwUsageError(sender, "/lifestealz debug <upload | generate>");
               yield true;
            }
         };
      }
   }

   private boolean handleUpload(CommandSender sender, boolean confirmed) {
      if (!confirmed) {
         sender.sendMessage(
            MessageUtils.getAndFormatMsg(
               false,
               "debugUploadConfirm",
               "\n <#8b73f6>&lUploading Debug Report&r\n\n&7 Are you sure you want to upload the debug report? By confirming, you accept our <u><click:OPEN_URL:https://debug.zetaplugins.com/privacy>Privacy Policy</click></u>.\n\n <#8b73f6><click:RUN_COMMAND:%command%>[Click Here]</click> &r&8or run <u>%command%</u>\n",
               new MessageUtils.Replaceable("%command%", "/lifestealz debug upload confirm")
            )
         );
         return true;
      } else {
         DebugReport report = ReportDataCollector.collect("l8Uv7FzS", this.plugin, this.plugin.getPluginFile(), this.plugin.getConfigManager().getConfigsMap());
         String url = ZetaDebugReportUploader.uploadReport(report, this.plugin);
         if (url == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "debugFailedUpload", "&cFailed to upload debug report!"));
            return false;
         } else {
            String formattedUrl = url.replaceAll("\\\\", "");
            sender.sendMessage(
               MessageUtils.getAndFormatMsg(
                  false,
                  "debugUploadSuccess",
                  "&8 [&a✔&8] &7Debug report uploaded successfully! You can view it here:\n <u><#8b73f6><click:OPEN_URL:%url%>%url%</click></u>\n",
                  new MessageUtils.Replaceable("%url%", formattedUrl)
               )
            );
            return true;
         }
      }
   }

   private boolean handleGenerate(CommandSender sender) {
      DebugReport report = ReportDataCollector.collect("l8Uv7FzS", this.plugin, this.plugin.getPluginFile(), this.plugin.getConfigManager().getConfigsMap());
      File reportJson = new File("debug-report.json");
      File reportTxt = new File("debug-report.txt");

      try {
         ReportFileWriter.writeJsonReportToFile(report, reportJson);
         ReportFileWriter.writeTextReportToFile(report, reportTxt);
      } catch (IOException var6) {
         sender.sendMessage(
            MessageUtils.getAndFormatMsg(
               false, "debugFailedToCreateFile", "&cFailed to create debug report file: %error%", new MessageUtils.Replaceable("%error%", var6.getMessage())
            )
         );
         this.plugin.getLogger().log(Level.SEVERE, "Failed to write debug report", (Throwable)var6);
         return false;
      }

      sender.sendMessage(
         MessageUtils.getAndFormatMsg(
            false,
            "debugFileCreated",
            "&8 [&a✔&8] &7Saved debug data to the following files:\n<click:COPY_TO_CLIPBOARD:%jsonPath%><#8b73f6>%jsonPath%</click>\n<click:COPY_TO_CLIPBOARD:%txtPath%><#8b73f6>%txtPath%</click>",
            new MessageUtils.Replaceable("%jsonPath%", reportJson.getAbsolutePath()),
            new MessageUtils.Replaceable("%txtPath%", reportTxt.getAbsolutePath())
         )
      );
      return true;
   }

   @Override
   public String getUsage() {
      return "/lifestealz debug";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.hasPermission("lifestealz.admin.debug");
   }
}
