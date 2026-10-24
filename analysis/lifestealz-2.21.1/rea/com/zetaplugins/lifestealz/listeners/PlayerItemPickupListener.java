// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.PlayerItemPickupListener
class PlayerItemPickupListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public PlayerItemPickupListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem().getItemStack();
        if (CustomItemManager.isForbiddenItem(itemStack)) {
            event.getItem().remove();
            event.setCancelled(true);
            return;
        }
        boolean heartGainCooldownEnabled = this.plugin.getConfig().getBoolean("heartGainCooldown.enabled");
        long heartGainCooldown = this.plugin.getConfig().getLong("heartGainCooldown.cooldown");
        boolean heartGainCooldownPreventPickup = this.plugin.getConfig().getBoolean("heartGainCooldown.preventPickup");
        if (CustomItemManager.isHeartItem(itemStack)) {
            if (heartGainCooldownEnabled && heartGainCooldownPreventPickup && CooldownManager.lastHeartGain.get(player.getUniqueId()) != null && CooldownManager.lastHeartGain.get(player.getUniqueId()).longValue() + heartGainCooldown > System.currentTimeMillis()) {
                event.setCancelled(true);
                if (CooldownManager.lastHeartPickupMessage.get(player.getUniqueId()) == null || CooldownManager.lastHeartPickupMessage.get(player.getUniqueId()).longValue() + 1000 < System.currentTimeMillis()) {
                    long timeLeft = ((CooldownManager.lastHeartGain.get(player.getUniqueId()).longValue() + heartGainCooldown) - System.currentTimeMillis()) / 1000;
                    player.sendMessage(MessageUtils.getAndFormatMsg(false, "heartGainCooldown", "&cYou have to wait before gaining another heart!", new MessageUtils.Replaceable("%time%", MessageUtils.formatTime(timeLeft))));
                    CooldownManager.lastHeartPickupMessage.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
                    return;
                }
                return;
            }
            CooldownManager.lastHeartGain.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        }
    }

}
