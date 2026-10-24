package com.zetaplugins.lifestealz.util.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.zetaplugins.lifestealz.LifeStealZ;
import org.bukkit.entity.Player;

public final class WorldGuardManager {
   private StateFlag HEARTLOSS_FLAG;

   public WorldGuardManager() {
      this.registerFlags();
   }

   private void registerFlags() {
      StateFlag heartLossFlag = new HeartLossFlag();
      WorldGuard.getInstance().getFlagRegistry().register(heartLossFlag);
      this.HEARTLOSS_FLAG = heartLossFlag;
   }

   public StateFlag getHeartLossFlag() {
      return this.HEARTLOSS_FLAG;
   }

   public static boolean checkHeartLossFlag(Player player) {
      WorldGuardManager worldGuardManager = LifeStealZ.getInstance().getWorldGuardManager();
      if (worldGuardManager == null) {
         return false;
      } else {
         LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
         Location loc = BukkitAdapter.adapt(player.getLocation());
         RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
         RegionQuery query = container.createQuery();
         ApplicableRegionSet set = query.getApplicableRegions(loc);
         return set.testState(localPlayer, new StateFlag[]{worldGuardManager.getHeartLossFlag()});
      }
   }
}
