package com.zetaplugins.lifestealz.util.customitems.customitemdata;

import com.zetaplugins.lifestealz.util.customblocks.ParticleColor;
import java.util.Objects;
import org.bukkit.Material;

public final class CustomReviveBeaconItemData extends CustomItemData {
   private final int reviveTime = this.getConfigurationSection().getInt("reviveTime", 30);
   private final boolean allowBreakingBeaconWhileReviving = this.getConfigurationSection().getBoolean("allowBreakingBeaconWhileReviving", true);
   private final boolean showLaser = this.getConfigurationSection().getBoolean("showLaser", true);
   private final Material innerLaser = this.parseMaterial(
      Objects.requireNonNullElse(this.getConfigurationSection().getString("innerLaserMaterial"), "RED_GLAZED_TERRACOTTA"), Material.RED_GLAZED_TERRACOTTA
   );
   private final Material outerLaser = this.parseMaterial(
      Objects.requireNonNullElse(this.getConfigurationSection().getString("outerLaserMaterial"), "RED_STAINED_GLASS"), Material.RED_STAINED_GLASS
   );
   private final boolean showParticleRing = this.getConfigurationSection().getBoolean("showParticleRing", true);
   private final ParticleColor particleColor = ParticleColor.fromString(this.getConfigurationSection().getString("particleColor", "RED"));
   private final boolean showEnchantParticles = this.getConfigurationSection().getBoolean("showEnchantParticles", true);
   private final Material decoyMaterial = this.parseMaterial(
      this.getConfigurationSection().getString("decoyMaterial", "RED_STAINED_GLASS"), Material.RED_STAINED_GLASS
   );

   public CustomReviveBeaconItemData(String itemId) throws IllegalArgumentException {
      super(itemId);
   }

   public int getReviveTime() {
      return this.reviveTime;
   }

   public boolean isAllowBreakingBeaconWhileReviving() {
      return this.allowBreakingBeaconWhileReviving;
   }

   public boolean shouldShowLaser() {
      return this.showLaser;
   }

   public boolean shouldShowParticleRing() {
      return this.showParticleRing;
   }

   public boolean shouldShowEnchantParticles() {
      return this.showEnchantParticles;
   }

   public Material getDecoyMaterial() {
      return this.decoyMaterial;
   }

   public Material getInnerLaser() {
      return this.innerLaser;
   }

   public Material getOuterLaser() {
      return this.outerLaser;
   }

   public ParticleColor getParticleColor() {
      return this.particleColor;
   }

   private Material parseMaterial(String materialName, Material fallbackMaterial) {
      Material material = Material.getMaterial(materialName.toUpperCase());
      return material != null ? material : fallbackMaterial;
   }
}
