// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners.revivebeacon;
// rea: class com.zetaplugins.lifestealz.listeners.revivebeacon.ReviveBeaconPlaceListener
class ReviveBeaconPlaceListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public ReviveBeaconPlaceListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private boolean canPlace(Player player, Location loc, Material type) {
        Block block = loc.getBlock();
        Block placedAgainst = block.getRelative(BlockFace.DOWN);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, block.getState(), placedAgainst, new ItemStack(type), player, true, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(placeEvent);
        return !placeEvent.isCancelled();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.BEACON)) {
            ItemStack itemInHand = event.getItemInHand();
            if (CustomItemType.REVIVE_BEACON.is(itemInHand)) {
                Player player = event.getPlayer();
                if (!canPlace(player, block.getLocation(), Material.BEACON)) {
                    event.setCancelled(true);
                    return;
                }
                String customItemId = CustomItemManager.getCustomItemId(itemInHand);
                try {
                    CustomReviveBeaconItemData itemData = new CustomReviveBeaconItemData(customItemId);
                    String world = block.getWorld().getName();
                    if (!itemData.isAllowedInWorld(world)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(MessageUtils.getAndFormatMsg(false, "noItemUseInWorld", "&cYou cannot use this item in this world!", new MessageUtils.Replaceable[0]));
                    } else {
                        CustomBlock.REVIVE_BEACON.make(block, customItemId);
                        this.plugin.getReviveBeaconEffectManager().startIdleEffects(block.getLocation(), itemData.shouldShowEnchantParticles(), itemData.getDecoyMaterial());
                    }
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

}
