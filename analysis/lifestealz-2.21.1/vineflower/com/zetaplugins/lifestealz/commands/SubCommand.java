package com.zetaplugins.lifestealz.commands;

import org.bukkit.command.CommandSender;

public interface SubCommand {
   boolean execute(CommandSender var1, String[] var2);

   String getUsage();

   boolean hasPermission(CommandSender var1);
}
