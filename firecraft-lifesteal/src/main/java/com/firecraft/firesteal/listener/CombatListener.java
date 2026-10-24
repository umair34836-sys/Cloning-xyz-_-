package com.firecraft.firesteal.listener;

import com.firecraft.firesteal.FireSteal;
import com.firecraft.firesteal.heart.HeartService;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CombatListener implements Listener {

    private final FireSteal plugin;
    private final HeartService hearts;
    // anti-farm: victim+killer -> last steal time
    private final ConcurrentHashMap<String, Long> lastSteal = new ConcurrentHashMap<>();

    public CombatListener(FireSteal plugin, HeartService hearts) {
        this.plugin = plugin;
        this.hearts = hearts;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        // Load off the main thread, then apply attribute on the main thread.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            hearts.loadInto(id);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                Player p = plugin.getServer().getPlayer(id);
                if (p != null) {
                    hearts.applyMaxHealth(p, hearts.getHearts(id));
                    if (hearts.isEliminated(id)) hearts.handleElimination(p);
                }
            });
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        hearts.saveNow(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = resolveKiller(victim);

        if (killer != null && !killer.equals(victim)) {
            if (allowedSteal(victim, killer)) {
                hearts.setLastKilledBy(victim.getUniqueId(), killer.getUniqueId());
                int loss = plugin.getConfig().getInt("hearts.loss-on-pvp", 1);
                int gain = plugin.getConfig().getInt("hearts.gain-on-kill", 1);
                hearts.addHearts(victim, -loss);
                hearts.addHearts(killer, gain);
            }
        } else {
            int loss = plugin.getConfig().getInt("hearts.loss-on-natural", 0);
            if (loss != 0) hearts.addHearts(victim, -loss);
        }

        // Elimination check happens next tick (victim respawns first).
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (hearts.isEliminated(victim.getUniqueId())) hearts.handleElimination(victim);
        });
    }

    /** Correct attribution: direct player, or the shooter behind a projectile. */
    private Player resolveKiller(Player victim) {
        Player k = victim.getKiller();
        if (k != null) return k;
        var dmg = victim.getLastDamageCause();
        if (dmg instanceof org.bukkit.event.entity.EntityDamageByEntityEvent ede) {
            if (ede.getDamager() instanceof Player p) return p;
            if (ede.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) return p;
        }
        return null;
    }

    /** Anti-farm: same victim->killer cooldown + optional same-IP block. */
    private boolean allowedSteal(Player victim, Player killer) {
        if (plugin.getConfig().getBoolean("anti-farm.block-same-ip", true)) {
            String vIp = victim.getAddress() != null ? victim.getAddress().getAddress().getHostAddress() : null;
            String kIp = killer.getAddress() != null ? killer.getAddress().getAddress().getHostAddress() : null;
            if (vIp != null && vIp.equals(kIp)) return false;
        }
        int cd = plugin.getConfig().getInt("anti-farm.same-victim-cooldown-seconds", 600);
        if (cd > 0) {
            String key = victim.getUniqueId() + "->" + killer.getUniqueId();
            long now = System.currentTimeMillis();
            Long prev = lastSteal.get(key);
            if (prev != null && now - prev < cd * 1000L) return false;
            lastSteal.put(key, now);
        }
        return true;
    }

    /** Consume a signed heart item on right-click (once-only via DB token). */
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        var item = e.getItem();
        if (item == null) return;
        // Quick pre-check: must carry our token, else ignore silently.
        if (item.getItemMeta() == null
                || !item.getItemMeta().getPersistentDataContainer().has(hearts.KEY_TOKEN, org.bukkit.persistence.PersistentDataType.STRING)) {
            return;
        }
        e.setCancelled(true);
        Player p = e.getPlayer();
        if (hearts.getHearts(p.getUniqueId()) >= hearts.max()) {
            p.sendMessage(plugin.msg("§cYou are already at the max hearts."));
            return;
        }
        if (hearts.redeemHeartItem(item)) {
            item.setAmount(item.getAmount() - 1);
            int now = hearts.addHearts(p, 1);
            p.sendMessage(plugin.msg("§aHeart used. You now have §e" + now + "§a hearts."));
        } else {
            p.sendMessage(plugin.msg("§cThis heart is invalid or already used."));
        }
    }
}
