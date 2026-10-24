// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems.recipe;
// rea: class com.zetaplugins.lifestealz.util.customitems.recipe.RecipeManager
class RecipeManager {
    com.zetaplugins.lifestealz.LifeStealZ plugin;
    com.zetaplugins.lifestealz.util.customitems.recipe.RecipeRegistrar recipeRegistrar;
    com.zetaplugins.lifestealz.util.customitems.recipe.RecipeRenderer recipeRenderer;

    public RecipeManager(LifeStealZ plugin) {
        this.plugin = plugin;
        this.recipeRenderer = new RecipeRenderer(plugin);
        this.recipeRegistrar = new RecipeRegistrar(plugin);
    }

    public void cancelAnimations(Inventory inventory) {
        this.recipeRenderer.cancelAnimations(inventory);
    }

    public Set<String> getItemIds() {
        return this.plugin.getConfigManager().getCustomItemConfig().getKeys(false);
    }

    public Set<String> getRecipeIds(String itemId) {
        ConfigurationSection recipeSection;
        Set<String> recipeIds = new HashSet<>();
        FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
        if (config.isSet(itemId + ".recipes") && (recipeSection = config.getConfigurationSection(itemId + ".recipes")) != null) {
            recipeIds.addAll(recipeSection.getKeys(false));
            return recipeIds;
        }
        return recipeIds;
    }

    public boolean isCraftable(String itemId) {
        return this.plugin.getConfigManager().getCustomItemConfig().getBoolean(itemId + ".craftable");
    }

    public void registerRecipes() {
        this.recipeRegistrar.registerRecipes();
    }

    public void renderRecipe(Player player, String itemId) {
        this.recipeRenderer.renderRecipe(player, itemId);
    }

    public void renderRecipe(Player player, String itemId, String recipeId) {
        this.recipeRenderer.renderRecipe(player, itemId, recipeId);
    }

}
