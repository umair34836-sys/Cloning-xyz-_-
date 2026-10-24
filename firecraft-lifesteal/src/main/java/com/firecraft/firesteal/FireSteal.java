package com.firecraft.firesteal;

import com.firecraft.firesteal.command.FireStealCommand;
import com.firecraft.firesteal.heart.HeartService;
import com.firecraft.firesteal.listener.CombatListener;
import com.firecraft.firesteal.placeholder.FireStealExpansion;
import com.firecraft.firesteal.storage.PlayerStore;
import org.bukkit.plugin.java.JavaPlugin;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class FireSteal extends JavaPlugin {

    private PlayerStore store;
    private HeartService hearts;
    private byte[] secret; // per-server HMAC key for signing heart items (anti-forgery)

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSecret();

        store = new PlayerStore();
        try {
            File db = new File(getDataFolder(), getConfig().getString("storage.file", "firesteal.db"));
            store.open(db);
        } catch (Exception e) {
            getLogger().severe("Could not open storage, disabling: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        hearts = new HeartService(this, store);

        getServer().getPluginManager().registerEvents(new CombatListener(this, hearts), this);
        FireStealCommand cmd = new FireStealCommand(this, hearts);
        getCommand("firesteal").setExecutor(cmd);
        getCommand("withdraw").setExecutor(cmd);
        getCommand("hearts").setExecutor(cmd);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FireStealExpansion(this, hearts).register();
            getLogger().info("Hooked into PlaceholderAPI.");
        }

        getLogger().info("FireSteal enabled.");
    }

    @Override
    public void onDisable() {
        if (store != null) store.close();
    }

    /** Load (or generate once) the server-only secret used to sign heart items. */
    private void loadSecret() {
        try {
            File f = new File(getDataFolder(), "secret.key");
            if (f.exists()) {
                secret = HexFormat.of().parseHex(Files.readString(f.toPath()).trim());
            } else {
                secret = new byte[32];
                new SecureRandom().nextBytes(secret);
                getDataFolder().mkdirs();
                Files.writeString(f.toPath(), HexFormat.of().formatHex(secret));
            }
        } catch (Exception e) {
            // Fallback: ephemeral secret (items won't survive a restart, but stays safe).
            secret = new byte[32];
            new SecureRandom().nextBytes(secret);
            getLogger().warning("Using an ephemeral signing secret: " + e.getMessage());
        }
    }

    /** HMAC-SHA256 signature of a token — proves the server minted a heart item. */
    public String sign(String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC failure", e);
        }
    }

    public String msg(String body) {
        return getConfig().getString("messages.prefix", "§6[FireSteal] §r") + body;
    }

    public HeartService hearts() { return hearts; }
}
