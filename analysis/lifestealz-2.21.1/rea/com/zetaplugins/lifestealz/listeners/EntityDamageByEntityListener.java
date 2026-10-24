// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.EntityDamageByEntityListener
class EntityDamageByEntityListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public EntityDamageByEntityListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        OfflinePlayer entity = event.getEntity();
        OfflinePlayer damager = event.getDamager();
        boolean preventCrystalPVP = this.plugin.getConfig().getBoolean("preventCrystalPVP");
        if ((entity instanceof Player) && damager.getType() == EntityType.END_CRYSTAL && preventCrystalPVP) {
            event.setCancelled(true);
        }
        if ((entity instanceof Player) && (damager instanceof Player)) {
            OfflinePlayer offlinePlayer = (Player) entity;
            OfflinePlayer offlinePlayer2 = (Player) damager;
            BypassManager bypassManager = this.plugin.getBypassManager();
            if (bypassManager.hasBypass(offlinePlayer) && !bypassManager.getConfig().damageFromPlayers()) {
                event.setCancelled(true);
                offlinePlayer2.sendMessage(MessageUtils.getAndFormatMsg(false, "noDamageWithBypass", "&cYou can't damage players with bypass permission!", new MessageUtils.Replaceable[0]));
                return;
            }
            if (bypassManager.hasBypass(offlinePlayer2) && !bypassManager.getConfig().damageToPlayers()) {
                event.setCancelled(true);
                offlinePlayer2.sendMessage(MessageUtils.getAndFormatMsg(false, "noDamageWithBypass", "&cYou can't damage players with bypass permission!", new MessageUtils.Replaceable[0]));
                return;
            }
            GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
            if (gracePeriodManager.isInGracePeriod(offlinePlayer) && !gracePeriodManager.getConfig().damageFromPlayers()) {
                event.setCancelled(true);
                offlinePlayer2.sendMessage(MessageUtils.getAndFormatMsg(false, "noDamageInGracePeriod", "&cYou can't damage players during the grace period!", new MessageUtils.Replaceable[0]));
            } else if (gracePeriodManager.isInGracePeriod(offlinePlayer2) && !gracePeriodManager.getConfig().damageToPlayers()) {
                event.setCancelled(true);
                offlinePlayer2.sendMessage(MessageUtils.getAndFormatMsg(false, "noDamageInGracePeriod", "&cYou can't damage players during the grace period!", new MessageUtils.Replaceable[0]));
            }
        }
    }

}
