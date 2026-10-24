// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.InteractionEntityEventListener
class InteractionEntityEventListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public InteractionEntityEventListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractionEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            item = event.getPlayer().getInventory().getItemInOffHand();
        }
        if (item.getType() == Material.AIR) {
            return;
        }
        Entity targetEntity = event.getRightClicked();
        boolean preventItemFrames = this.plugin.getConfig().getBoolean("preventCustomItemsInItemFrames");
        if (preventItemFrames) {
            if ((CustomItemManager.isHeartItem(item) || CustomItemManager.isReviveItem(item)) && targetEntity.getType().equals(EntityType.ITEM_FRAME)) {
                event.setCancelled(true);
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "itemFramesDisabled", "&cYou cannot put custom items in itemframes!", new MessageUtils.Replaceable[0]));
            }
        }
    }

}
