// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.api;
// rea: class com.zetaplugins.lifestealz.api.LifeStealZAPIImpl
class LifeStealZAPIImpl {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public LifeStealZAPIImpl(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public boolean eliminate(UUID uuid) {
        PlayerData playerData = this.plugin.getStorage().load(uuid);
        if (playerData == null) {
            return false;
        }
        playerData.setMaxHealth(0.0d);
        this.plugin.getStorage().save(playerData);
        return true;
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public ItemStack getCustomItem(String customItemID) {
        return CustomItemManager.createCustomItem(customItemID);
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public CustomItemData getCustomItemData(String customItemID) {
        return CustomItemManager.getCustomItemData(customItemID);
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public String getCustomItemID(ItemStack item) {
        return CustomItemManager.getCustomItemId(item);
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public Set<String> getCustomItemIDs() {
        return this.plugin.getRecipeManager().getItemIds();
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public ItemStack getDefaultHeart() {
        return CustomItemManager.createHeart();
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public PlayerData getPlayerData(UUID uuid) {
        return this.plugin.getStorage().load(uuid);
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public boolean isBypassActive(Player player) {
        return this.plugin.getBypassManager().hasBypass(player);
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public boolean isEliminated(UUID uuid) {
        PlayerData playerData = this.plugin.getStorage().load(uuid);
        return playerData != null && playerData.getMaxHealth() <= 0.0d;
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public boolean revive(UUID uuid) {
        PlayerData playerData = this.plugin.getStorage().load(uuid);
        if (playerData == null) {
            return false;
        }
        playerData.setMaxHealth(this.plugin.getConfig().getInt("reviveHearts") * 2);
        playerData.setHasBeenRevived(playerData.getHasBeenRevived() + 1);
        this.plugin.getStorage().save(playerData);
        return true;
    }

    @Override // com.zetaplugins.lifestealz.api.LifeStealZAPI
    public void savePlayerData(PlayerData playerData) {
        this.plugin.getStorage().save(playerData);
    }

}
