// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.worldguard;
// rea: class com.zetaplugins.lifestealz.util.worldguard.WorldGuardManager
class WorldGuardManager {
    com.sk89q.worldguard.protection.flags.StateFlag HEARTLOSS_FLAG;

    public WorldGuardManager() {
        registerFlags();
    }

    public static boolean checkHeartLossFlag(Player player) {
        WorldGuardManager worldGuardManager = LifeStealZ.getInstance().getWorldGuardManager();
        if (worldGuardManager == null) {
            return false;
        }
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Location loc = BukkitAdapter.adapt(player.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        return set.testState(localPlayer, new StateFlag[]{worldGuardManager.getHeartLossFlag()});
    }

    public StateFlag getHeartLossFlag() {
        return this.HEARTLOSS_FLAG;
    }

    private void registerFlags() {
        StateFlag heartLossFlag = new HeartLossFlag();
        WorldGuard.getInstance().getFlagRegistry().register(heartLossFlag);
        this.HEARTLOSS_FLAG = heartLossFlag;
    }

}
