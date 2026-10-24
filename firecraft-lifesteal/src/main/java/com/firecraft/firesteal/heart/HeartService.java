package com.firecraft.firesteal.heart;

import com.firecraft.firesteal.FireSteal;
import com.firecraft.firesteal.storage.PlayerStore;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central heart logic. All mutations happen on the main thread (callers are events/commands),
 * guarded per-player, so simultaneous kills can't race. Values are always clamped to the
 * configured [min, max]. Original implementation.
 */
public final class HeartService {

    private final FireSteal plugin;
    private final PlayerStore store;
    private final Map<UUID, Integer> hearts = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> eliminated = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastKilledBy = new ConcurrentHashMap<>();

    public final NamespacedKey KEY_TOKEN;   // unique per-item token (anti-dupe)
    public final NamespacedKey KEY_SIG;      // signature proving the server minted it (anti-forge)

    public HeartService(FireSteal plugin, PlayerStore store) {
        this.plugin = plugin;
        this.store = store;
        this.KEY_TOKEN = new NamespacedKey(plugin, "heart_token");
        this.KEY_SIG = new NamespacedKey(plugin, "heart_sig");
    }

    public int min() { return plugin.getConfig().getInt("hearts.min", 0); }
    public int max() { return plugin.getConfig().getInt("hearts.max", 20); }
    public int start() { return plugin.getConfig().getInt("hearts.start", 10); }

    private int clamp(int v) { return Math.max(min(), Math.min(max(), v)); }

    /** Load a player at join (cache from DB, or initialise). Safe to call from async. */
    public void loadInto(UUID uuid) {
        try {
            PlayerStore.Record r = store.load(uuid);
            if (r == null) {
                hearts.put(uuid, start());
                eliminated.put(uuid, false);
                store.save(uuid, start(), false, null);
            } else {
                hearts.put(uuid, clamp(r.hearts()));
                eliminated.put(uuid, r.eliminated());
                if (r.lastKilledBy() != null) lastKilledBy.put(uuid, r.lastKilledBy());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Load failed for " + uuid + ": " + e.getMessage());
            hearts.putIfAbsent(uuid, start());
            eliminated.putIfAbsent(uuid, false);
        }
    }

    public int getHearts(UUID uuid) { return hearts.getOrDefault(uuid, start()); }
    public boolean isEliminated(UUID uuid) { return eliminated.getOrDefault(uuid, false); }

    /** Apply a delta, clamp, persist, and sync the player's max-health attribute. Main thread. */
    public synchronized int addHearts(Player player, int delta) {
        UUID id = player.getUniqueId();
        int now = clamp(getHearts(id) + delta);
        hearts.put(id, now);
        applyMaxHealth(player, now);
        boolean elim = now <= min();
        eliminated.put(id, elim);
        store.save(id, now, elim, lastKilledBy.get(id));
        return now;
    }

    public synchronized void setHearts(Player player, int value) {
        UUID id = player.getUniqueId();
        int now = clamp(value);
        hearts.put(id, now);
        applyMaxHealth(player, now);
        boolean elim = now <= min();
        eliminated.put(id, elim);
        store.save(id, now, elim, lastKilledBy.get(id));
    }

    public void setLastKilledBy(UUID victim, UUID killer) {
        if (killer != null) lastKilledBy.put(victim, killer.toString());
    }

    public String getLastKilledBy(UUID victim) { return lastKilledBy.get(victim); }

    public synchronized void revive(Player player) {
        UUID id = player.getUniqueId();
        int v = Math.max(start(), min() + 1);
        hearts.put(id, clamp(v));
        eliminated.put(id, false);
        applyMaxHealth(player, getHearts(id));
        store.save(id, getHearts(id), false, null);
    }

    /** 1 heart = 2 health points. Clamped so the client never gets an insane attribute. */
    public void applyMaxHealth(Player player, int heartCount) {
        double hp = Math.max(2.0, heartCount * 2.0);
        var attr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(hp);
            if (player.getHealth() > hp) player.setHealth(hp);
        }
    }

    // ---- Heart item (signed + unique token) ----

    /** Mint a signed, unique heart item. The signature proves the server created it. */
    public ItemStack mintHeartItem() {
        ItemStack item = new ItemStack(org.bukkit.Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getConfig().getString("withdraw.item-name", "§cHeart"));
        meta.setLore(java.util.List.of(plugin.getConfig().getString("withdraw.item-lore", "§7Right-click to gain a heart.")));
        String token = UUID.randomUUID().toString();
        meta.getPersistentDataContainer().set(KEY_TOKEN, PersistentDataType.STRING, token);
        meta.getPersistentDataContainer().set(KEY_SIG, PersistentDataType.STRING, plugin.sign(token));
        item.setItemMeta(meta);
        return item;
    }

    /** Validate a heart item: correct signature AND token not previously redeemed. */
    public boolean redeemHeartItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        String token = pdc.get(KEY_TOKEN, PersistentDataType.STRING);
        String sig = pdc.get(KEY_SIG, PersistentDataType.STRING);
        if (token == null || sig == null) return false;          // not a real heart item
        if (!plugin.sign(token).equals(sig)) return false;        // forged signature
        return store.redeemToken(token);                          // once-only (anti-dupe), atomic
    }

    public void saveNow(UUID id) {
        store.save(id, getHearts(id), isEliminated(id), lastKilledBy.get(id));
    }

    public void handleElimination(Player player) {
        String mode = plugin.getConfig().getString("elimination.mode", "SPECTATOR").toUpperCase();
        switch (mode) {
            case "BAN" -> Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                    .addBan(player.getName(), plugin.getConfig().getString("elimination.ban-message", "Eliminated"), null, "FireSteal");
            case "KICK" -> player.kickPlayer(plugin.getConfig().getString("elimination.ban-message", "Eliminated"));
            default -> player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        }
    }
}
