// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems.customitemdata;
// rea: class com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomHeartItemData
class CustomHeartItemData {
    int customHeartValue;
    int maxHearts;
    int minHearts;

    public CustomHeartItemData(String itemId) throws IllegalArgumentException {
        super(itemId);
        ConfigurationSection section = getConfigurationSection();
        this.customHeartValue = section.getInt("customHeartValue", 1);
        this.minHearts = section.getInt("minHearts", 0);
        this.maxHearts = section.getInt("maxHearts", -1);
    }

    public int getCustomHeartValue() {
        return this.customHeartValue;
    }

    public int getMaxHearts() {
        return this.maxHearts;
    }

    public int getMinHearts() {
        return this.minHearts;
    }

}
