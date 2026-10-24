// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems.customitemdata;
// rea: class com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomItemData
class CustomItemData {
    boolean craftable;
    com.zetaplugins.lifestealz.util.customitems.CustomItemType customItemType;
    int customModelID;
    boolean despawnable;
    boolean enchanted;
    boolean invulnerable;
    java.lang.String itemId;
    java.util.List<java.lang.String> lore;
    org.bukkit.Material material;
    java.lang.String name;
    boolean requirePermission;
    java.util.List<java.lang.String> whitelistedWorlds;

    public CustomItemData(String itemId) throws IllegalArgumentException {
        this.itemId = itemId;
        ConfigurationSection section = getConfigurationSection();
        if (section == null) {
            throw new IllegalArgumentException("Custom item with id " + itemId + " does not exist!");
        }
        this.name = section.getString("name", "&7Fallback Name");
        this.lore = section.getStringList("lore");
        this.material = Material.valueOf(section.getString("material", "STONE"));
        this.enchanted = section.getBoolean("enchanted", false);
        this.customItemType = CustomItemType.fromString(section.getString("customItemType", "none"));
        this.craftable = section.getBoolean("craftable", true);
        this.requirePermission = section.getBoolean("requirePermission", false);
        this.invulnerable = section.getBoolean("invulnerable", false);
        this.despawnable = section.getBoolean("despawnable", true);
        this.whitelistedWorlds = section.getStringList("whitelistedWorlds");
        this.customModelID = section.getInt("customModelId", 0);
    }

    protected ConfigurationSection getConfigurationSection() {
        FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();
        return config.getConfigurationSection(this.itemId);
    }

    public CustomItemType getCustomItemType() {
        return this.customItemType;
    }

    public int getCustomModelID() {
        return this.customModelID;
    }

    public String getItemId() {
        return this.itemId;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return "lifestealz.item." + this.itemId;
    }

    public CustomItemSoundData getSound() {
        return new CustomItemSoundData(this.itemId);
    }

    public List<String> getWhitelistedWorlds() {
        return new ArrayList(this.whitelistedWorlds);
    }

    public boolean isAllowedInWorld(String worldName) {
        return this.whitelistedWorlds.isEmpty() || this.whitelistedWorlds.contains(worldName);
    }

    public boolean isCraftable() {
        return this.craftable;
    }

    public boolean isDespawnable() {
        return this.despawnable;
    }

    public boolean isEnchanted() {
        return this.enchanted;
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean requiresPermission() {
        return this.requirePermission;
    }

    // rea: class com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomItemData.CustomItemSoundData
    class CustomItemSoundData {
        boolean enabled;
        double pitch;
        org.bukkit.Sound sound;
        double volume;

            private CustomItemSoundData(String itemId) {
                FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();
                this.enabled = config.getBoolean(itemId + ".sound.enabled");
                this.sound = Sound.valueOf(config.getString(itemId + ".sound.sound"));
                this.volume = config.getDouble(itemId + ".sound.volume");
                this.pitch = config.getDouble(itemId + ".sound.pitch");
            }

            public double getPitch() {
                return this.pitch;
            }

            public Sound getSound() {
                return this.sound;
            }

            public double getVolume() {
                return this.volume;
            }

            public boolean isEnabled() {
                return this.enabled;
            }

    }

}
