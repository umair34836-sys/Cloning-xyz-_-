// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.EntityResurrectListener
class EntityResurrectListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public EntityResurrectListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityRessurect(EntityResurrectEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = event.getEntity();
            ItemStack totem = null;
            if (player.getInventory().getItemInOffHand().getType().name().contains("TOTEM")) {
                totem = player.getInventory().getItemInOffHand();
            } else if (player.getInventory().getItemInMainHand().getType().name().contains("TOTEM")) {
                totem = player.getInventory().getItemInMainHand();
            }
            if (this.plugin.getConfig().getBoolean("preventTotems") || (totem != null && CustomItemManager.isForbiddenItem(totem))) {
                event.setCancelled(true);
            }
        }
    }

}
