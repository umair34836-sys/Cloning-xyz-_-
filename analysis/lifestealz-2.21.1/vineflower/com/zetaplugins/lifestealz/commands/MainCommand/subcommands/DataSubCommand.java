package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.storage.Storage;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public final class DataSubCommand implements SubCommand {
   private final Storage storage;
   private final LifeStealZ plugin;

   public DataSubCommand(LifeStealZ plugin) {
      this.storage = plugin.getStorage();
      this.plugin = plugin;
   }

   @Override
   public boolean execute(CommandSender sender, String[] args) {
      if (!this.hasPermission(sender)) {
         CommandUtils.throwPermissionError(sender);
         return false;
      } else if (args.length < 3) {
         CommandUtils.throwUsageError(sender, "/lifestealz data <import | export> <file>");
         return false;
      } else {
         String optionTwo = args[1];
         String fileName = args[2];
         if (optionTwo.equals("export")) {
            return this.handleExport(sender, fileName);
         } else if (optionTwo.equals("import")) {
            return this.handleImport(sender, fileName);
         } else {
            CommandUtils.throwUsageError(sender, this.getUsage());
            return true;
         }
      }
   }

   private boolean handleExport(CommandSender sender, String fileName) {
      sender.sendMessage(MessageUtils.getAndFormatMsg(true, "exportingData", "&7Exporting player data..."));
      BukkitTask task = this.plugin
         .getServer()
         .getScheduler()
         .runTaskAsynchronously(
            this.plugin,
            () -> {
               String filePath = this.storage.export(fileName);
               if (filePath != null) {
                  sender.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        true, "exportData", "&7Successfully exported player data to &c%file%", new MessageUtils.Replaceable("%file%", filePath)
                     )
                  );
               } else {
                  sender.sendMessage(MessageUtils.getAndFormatMsg(false, "exportDataError", "&cFailed to export data! Check console for details."));
               }
            }
         );
      this.plugin.getAsyncTaskManager().addTask(task);
      return true;
   }

   private boolean handleImport(CommandSender sender, String fileName) {
      sender.sendMessage(MessageUtils.getAndFormatMsg(true, "importingData", "&7Importing player data..."));
      BukkitTask task = this.plugin
         .getServer()
         .getScheduler()
         .runTaskAsynchronously(
            this.plugin,
            () -> {
               this.storage.importData(fileName);
               sender.sendMessage(
                  MessageUtils.getAndFormatMsg(
                     true,
                     "importData",
                     "&7Successfully imported &c%file%.csv&7!\n&cPlease restart the server, to ensure flawless migration!",
                     new MessageUtils.Replaceable("%file%", fileName)
                  )
               );
            }
         );
      this.plugin.getAsyncTaskManager().addTask(task);
      return true;
   }

   @Override
   public String getUsage() {
      return "/lifestealz data <import | export> <file>";
   }

   @Override
   public boolean hasPermission(CommandSender sender) {
      return sender.hasPermission("lifestealz.managedata");
   }
}
