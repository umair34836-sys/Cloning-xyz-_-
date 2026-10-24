// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners.revivebeacon;
// rea: class com.zetaplugins.lifestealz.listeners.revivebeacon.ReviveBeaconInteractListener
class ReviveBeaconInteractListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public ReviveBeaconInteractListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onReviveBeaconInteract(PlayerInteractEvent event) {
        Block block;
        Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_BLOCK) && (block = event.getClickedBlock()) != null && CustomBlock.REVIVE_BEACON.is(block)) {
            Player player = event.getPlayer();
            event.setCancelled(true);
            ReviveTask reviveTask = this.plugin.getReviveTaskManager().getReviveTask(block.getLocation());
            if (reviveTask != null) {
                long nowSeconds = System.currentTimeMillis() / 1000;
                int secondsLeft = (int) ((reviveTask.start() + ((long) reviveTask.durationSeconds())) - nowSeconds);
                player.sendMessage(MessageUtils.getAndFormatMsg(true, "beaconInUseInteract", "&7There is already a revive process in progress. Time left: &c%timeLeft% seconds&7.", new MessageUtils.Replaceable("%timeLeft%", String.valueOf(secondsLeft))));
                return;
            }
            GuiManager.openReviveBeaconGui(player, 1, this.plugin, block.getLocation());
        }
    }

}
