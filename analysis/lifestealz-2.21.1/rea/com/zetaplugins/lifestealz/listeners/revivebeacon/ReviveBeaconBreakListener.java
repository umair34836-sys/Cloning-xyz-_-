// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners.revivebeacon;
// rea: class com.zetaplugins.lifestealz.listeners.revivebeacon.ReviveBeaconBreakListener
class ReviveBeaconBreakListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public ReviveBeaconBreakListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        List listBlockList = event.blockList();
        CustomBlock customBlock = CustomBlock.REVIVE_BEACON;
        Objects.requireNonNull(customBlock);
        listBlockList.removeIf(customBlock::is);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (CustomBlock.REVIVE_BEACON.is(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List listBlockList = event.blockList();
        CustomBlock customBlock = CustomBlock.REVIVE_BEACON;
        Objects.requireNonNull(customBlock);
        listBlockList.removeIf(customBlock::is);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (CustomBlock.REVIVE_BEACON.is(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (CustomBlock.REVIVE_BEACON.is(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onReviveBeaconBreak(BlockBreakEvent event) {
        String customID;
        Block block = event.getBlock();
        if (CustomBlock.REVIVE_BEACON.is(block)) {
            Player player = event.getPlayer();
            Location location = block.getLocation();
            ReviveTask reviveTask = this.plugin.getReviveTaskManager().getReviveTask(location);
            if (reviveTask != null) {
                CustomReviveBeaconItemData itemData = new CustomReviveBeaconItemData(CustomBlock.REVIVE_BEACON.getCustomItemId(block));
                if (!itemData.isAllowBreakingBeaconWhileReviving()) {
                    event.setCancelled(true);
                    player.sendMessage(MessageUtils.getAndFormatMsg(false, "noReviveBeaconBreak", "&cYou cannot break a revive beacon while it is in use!", new MessageUtils.Replaceable[0]));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return;
                }
                if (!reviveTask.task().isCancelled()) {
                    reviveTask.task().cancel();
                }
                this.plugin.getReviveTaskManager().removeReviveTask(location);
                location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
                Player reviver = Bukkit.getPlayer(reviveTask.reviver());
                if (reviver != null && reviver.isOnline()) {
                    reviver.sendMessage(MessageUtils.getAndFormatMsg(true, "reviveBeaconBreak", "&7Your revive beacon has been broken by &c%breaker%&7, the revive process has been cancelled.", new MessageUtils.Replaceable("%breaker%", player.getName())));
                }
            }
            this.plugin.getReviveBeaconEffectManager().clearAllEffects(location);
            event.setDropItems(false);
            if (player.getGameMode().equals(GameMode.SURVIVAL) && (customID = CustomBlock.REVIVE_BEACON.getCustomItemId(block)) != null) {
                ItemStack item = CustomItemManager.createCustomItem(customID);
                block.getWorld().dropItemNaturally(location.add(0.5d, 0.5d, 0.5d), item);
            }
        }
    }

}
