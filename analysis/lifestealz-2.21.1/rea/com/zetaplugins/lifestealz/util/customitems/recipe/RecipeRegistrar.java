// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems.recipe;
// rea: class com.zetaplugins.lifestealz.util.customitems.recipe.RecipeRegistrar
class RecipeRegistrar {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public RecipeRegistrar(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private Set<String> getItemIds() {
        return this.plugin.getConfigManager().getCustomItemConfig().getKeys(false);
    }

    private void registerCustomItemRecipes(String itemId) {
        boolean craftable = this.plugin.getConfigManager().getCustomItemConfig().getBoolean(itemId + ".craftable");
        if (craftable) {
            FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
            boolean hasOneRecipe = config.isSet(itemId + ".recipe");
            if (hasOneRecipe) {
                List<String> rowOne = config.getStringList(itemId + ".recipe.rowOne");
                List<String> rowTwo = config.getStringList(itemId + ".recipe.rowTwo");
                List<String> rowThree = config.getStringList(itemId + ".recipe.rowThree");
                registerRecipe(itemId, "default", rowOne, rowTwo, rowThree);
                return;
            }
            ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
            if (recipeSection == null) {
                return;
            }
            for (String recipeId : recipeSection.getKeys(false)) {
                List<String> rowOne2 = recipeSection.getStringList(recipeId + ".rowOne");
                List<String> rowTwo2 = recipeSection.getStringList(recipeId + ".rowTwo");
                List<String> rowThree2 = recipeSection.getStringList(recipeId + ".rowThree");
                registerRecipe(itemId, recipeId, rowOne2, rowTwo2, rowThree2);
            }
        }
    }

    private void registerRecipe(String itemId, String recipeId, List<String> rowOne, List<String> rowTwo, List<String> rowThree) {
        NamespacedKey recipeKey = new NamespacedKey(this.plugin, "recipe_" + itemId + "_" + recipeId);
        ItemStack resultItem = CustomItemManager.createCustomItem(itemId);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, resultItem);
        recipe.shape(new String[]{"ABC", "DEF", "GHI"});
        setIngredient(recipe, "A", rowOne.get(0));
        setIngredient(recipe, "B", rowOne.get(1));
        setIngredient(recipe, "C", rowOne.get(2));
        setIngredient(recipe, "D", rowTwo.get(0));
        setIngredient(recipe, "E", rowTwo.get(1));
        setIngredient(recipe, "F", rowTwo.get(2));
        setIngredient(recipe, "G", rowThree.get(0));
        setIngredient(recipe, "H", rowThree.get(1));
        setIngredient(recipe, "I", rowThree.get(2));
        Bukkit.addRecipe(recipe);
    }

    public void registerRecipes() {
        for (String itemId : this.plugin.getConfigManager().getCustomItemConfig().getKeys(false)) {
            removeRecipe(itemId);
            registerCustomItemRecipes(itemId);
        }
    }

    private void removeRecipe(String itemId) {
        FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
        boolean hasOneRecipe = config.isSet(itemId + ".recipe");
        if (hasOneRecipe) {
            NamespacedKey recipeKey = new NamespacedKey(this.plugin, "recipe_" + itemId + "_default");
            Bukkit.removeRecipe(recipeKey);
            return;
        }
        ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
        if (recipeSection == null) {
            return;
        }
        for (String recipeId : recipeSection.getKeys(false)) {
            NamespacedKey recipeKey2 = new NamespacedKey(this.plugin, "recipe_" + itemId + "_" + recipeId);
            Bukkit.removeRecipe(recipeKey2);
        }
    }

    private void setIngredient(ShapedRecipe recipe, String key, String material) {
        if (material == null || material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("empty")) {
            return;
        }
        if (material.startsWith("#") && tagFromString(material.substring(1)) != null) {
            Tag<Material> tag = tagFromString(material.substring(1).toLowerCase());
            recipe.setIngredient(key.charAt(0), new RecipeChoice.MaterialChoice(tag));
        } else if (getItemIds().contains(material.toLowerCase())) {
            recipe.setIngredient(key.charAt(0), CustomItemManager.createCustomItem(material));
        } else {
            if (Material.getMaterial(material.toUpperCase()) != null) {
                recipe.setIngredient(key.charAt(0), Material.valueOf(material.toUpperCase()));
                return;
            }
            throw new IllegalArgumentException("Invalid material or Tag: " + material + " (" + material.toUpperCase() + ")");
        }
    }

    private Tag<Material> tagFromString(String tagName) {
        Tag<Material> blockTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName), Material.class);
        return blockTag != null ? blockTag : Bukkit.getTag("items", NamespacedKey.minecraft(tagName), Material.class);
    }

}
