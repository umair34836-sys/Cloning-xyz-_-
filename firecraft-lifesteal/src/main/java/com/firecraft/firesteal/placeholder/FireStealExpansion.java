package com.firecraft.firesteal.placeholder;

import com.firecraft.firesteal.FireSteal;
import com.firecraft.firesteal.heart.HeartService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/** Soft PlaceholderAPI hook: %firesteal_hearts%, %firesteal_eliminated%. */
public final class FireStealExpansion extends PlaceholderExpansion {

    private final FireSteal plugin;
    private final HeartService hearts;

    public FireStealExpansion(FireSteal plugin, HeartService hearts) {
        this.plugin = plugin;
        this.hearts = hearts;
    }

    @Override public @NotNull String getIdentifier() { return "firesteal"; }
    @Override public @NotNull String getAuthor() { return "FireCraft SMP"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";
        return switch (params.toLowerCase()) {
            case "hearts" -> String.valueOf(hearts.getHearts(player.getUniqueId()));
            case "eliminated" -> hearts.isEliminated(player.getUniqueId()) ? "yes" : "no";
            default -> null;
        };
    }
}
