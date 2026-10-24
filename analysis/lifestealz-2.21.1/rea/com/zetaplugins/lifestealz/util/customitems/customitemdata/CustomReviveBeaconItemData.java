// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems.customitemdata;
// rea: class com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomReviveBeaconItemData
class CustomReviveBeaconItemData {
    boolean allowBreakingBeaconWhileReviving;
    org.bukkit.Material decoyMaterial;
    org.bukkit.Material innerLaser;
    org.bukkit.Material outerLaser;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor particleColor;
    int reviveTime;
    boolean showEnchantParticles;
    boolean showLaser;
    boolean showParticleRing;

    public CustomReviveBeaconItemData(String itemId) throws IllegalArgumentException {
        super(itemId);
        this.reviveTime = getConfigurationSection().getInt("reviveTime", 30);
        this.allowBreakingBeaconWhileReviving = getConfigurationSection().getBoolean("allowBreakingBeaconWhileReviving", true);
        this.showLaser = getConfigurationSection().getBoolean("showLaser", true);
        this.innerLaser = parseMaterial((String) Objects.requireNonNullElse(getConfigurationSection().getString("innerLaserMaterial"), "RED_GLAZED_TERRACOTTA"), Material.RED_GLAZED_TERRACOTTA);
        this.outerLaser = parseMaterial((String) Objects.requireNonNullElse(getConfigurationSection().getString("outerLaserMaterial"), "RED_STAINED_GLASS"), Material.RED_STAINED_GLASS);
        this.showParticleRing = getConfigurationSection().getBoolean("showParticleRing", true);
        this.particleColor = ParticleColor.fromString(getConfigurationSection().getString("particleColor", "RED"));
        this.showEnchantParticles = getConfigurationSection().getBoolean("showEnchantParticles", true);
        this.decoyMaterial = parseMaterial(getConfigurationSection().getString("decoyMaterial", "RED_STAINED_GLASS"), Material.RED_STAINED_GLASS);
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

    public int getReviveTime() {
        return this.reviveTime;
    }

    public boolean isAllowBreakingBeaconWhileReviving() {
        return this.allowBreakingBeaconWhileReviving;
    }

    private Material parseMaterial(String materialName, Material fallbackMaterial) {
        Material material = Material.getMaterial(materialName.toUpperCase());
        return material != null ? material : fallbackMaterial;
    }

    public boolean shouldShowEnchantParticles() {
        return this.showEnchantParticles;
    }

    public boolean shouldShowLaser() {
        return this.showLaser;
    }

    public boolean shouldShowParticleRing() {
        return this.showParticleRing;
    }

}
