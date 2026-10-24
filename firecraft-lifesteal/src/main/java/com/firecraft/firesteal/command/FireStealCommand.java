package com.firecraft.firesteal.command;

import com.firecraft.firesteal.FireSteal;
import com.firecraft.firesteal.heart.HeartService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

/** Handles /firesteal (admin), /withdraw, /hearts. Every action is permission-checked. */
public final class FireStealCommand implements TabExecutor {

    private final FireSteal plugin;
    private final HeartService hearts;

    public FireStealCommand(FireSteal plugin, HeartService hearts) {
        this.plugin = plugin;
        this.hearts = hearts;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "withdraw" -> { return withdraw(sender); }
            case "hearts" -> { return showHearts(sender, args); }
            case "firesteal" -> { return admin(sender, args); }
            default -> { return false; }
        }
    }

    private boolean withdraw(CommandSender sender) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Players only."); return true; }
        if (!p.hasPermission("firesteal.withdraw")) { p.sendMessage(plugin.msg("§cNo permission.")); return true; }
        if (!plugin.getConfig().getBoolean("withdraw.enabled", true)) { p.sendMessage(plugin.msg("§cWithdraw disabled.")); return true; }
        if (hearts.getHearts(p.getUniqueId()) - 1 <= hearts.min()) {
            p.sendMessage(plugin.msg("§cYou can't withdraw — too few hearts."));
            return true;
        }
        hearts.addHearts(p, -1);
        p.getInventory().addItem(hearts.mintHeartItem());
        p.sendMessage(plugin.msg("§aWithdrew 1 heart into an item."));
        return true;
    }

    private boolean showHearts(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            if (!sender.hasPermission("firesteal.hearts.others")) { sender.sendMessage(plugin.msg("§cNo permission.")); return true; }
            OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
            sender.sendMessage(plugin.msg("§e" + args[0] + "§7 has §e" + hearts.getHearts(t.getUniqueId()) + "§7 hearts."));
            return true;
        }
        if (!(sender instanceof Player p)) { sender.sendMessage("Specify a player."); return true; }
        p.sendMessage(plugin.msg("§7You have §e" + hearts.getHearts(p.getUniqueId()) + "§7 hearts."));
        return true;
    }

    private boolean admin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("firesteal.admin")) { sender.sendMessage(plugin.msg("§cNo permission.")); return true; }
        if (args.length == 0) {
            sender.sendMessage(plugin.msg("§7/fs sethearts <player> <n> | give <player> | revive <player> | eliminate <player> | reload"));
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "reload" -> { plugin.reloadConfig(); sender.sendMessage(plugin.msg("§aConfig reloaded.")); }
            case "sethearts" -> {
                if (args.length < 3) { sender.sendMessage(plugin.msg("§cUsage: /fs sethearts <player> <n>")); return true; }
                Player t = Bukkit.getPlayerExact(args[1]);
                if (t == null) { sender.sendMessage(plugin.msg("§cPlayer not online.")); return true; }
                int n;
                try { n = Integer.parseInt(args[2]); } catch (NumberFormatException ex) { sender.sendMessage(plugin.msg("§cNot a number.")); return true; }
                hearts.setHearts(t, n);
                sender.sendMessage(plugin.msg("§aSet §e" + t.getName() + "§a to §e" + hearts.getHearts(t.getUniqueId()) + "§a hearts."));
            }
            case "give" -> {
                if (args.length < 2) { sender.sendMessage(plugin.msg("§cUsage: /fs give <player>")); return true; }
                Player t = Bukkit.getPlayerExact(args[1]);
                if (t == null) { sender.sendMessage(plugin.msg("§cPlayer not online.")); return true; }
                t.getInventory().addItem(hearts.mintHeartItem());
                sender.sendMessage(plugin.msg("§aGave a heart item to §e" + t.getName() + "§a."));
            }
            case "revive" -> {
                if (args.length < 2) { sender.sendMessage(plugin.msg("§cUsage: /fs revive <player>")); return true; }
                Player t = Bukkit.getPlayerExact(args[1]);
                if (t == null) { sender.sendMessage(plugin.msg("§cPlayer not online.")); return true; }
                hearts.revive(t);
                if (t.getGameMode() == org.bukkit.GameMode.SPECTATOR) t.setGameMode(org.bukkit.GameMode.SURVIVAL);
                sender.sendMessage(plugin.msg("§aRevived §e" + t.getName() + "§a."));
            }
            case "eliminate" -> {
                if (args.length < 2) { sender.sendMessage(plugin.msg("§cUsage: /fs eliminate <player>")); return true; }
                Player t = Bukkit.getPlayerExact(args[1]);
                if (t == null) { sender.sendMessage(plugin.msg("§cPlayer not online.")); return true; }
                hearts.setHearts(t, hearts.min());
                hearts.handleElimination(t);
                sender.sendMessage(plugin.msg("§aEliminated §e" + t.getName() + "§a."));
            }
            default -> sender.sendMessage(plugin.msg("§cUnknown subcommand."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("firesteal") && args.length == 1) {
            return List.of("sethearts", "give", "revive", "eliminate", "reload");
        }
        return List.of();
    }
}
