package com.zetaplugins.lifestealz.util.customitems.recipe;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

final class RecipeRegistrar {
   private final LifeStealZ plugin;

   public RecipeRegistrar(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public void registerRecipes() {
      for (String itemId : this.plugin.getConfigManager().getCustomItemConfig().getKeys(false)) {
         this.removeRecipe(itemId);
         this.registerCustomItemRecipes(itemId);
      }
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
            this.registerRecipe(itemId, "default", rowOne, rowTwo, rowThree);
         } else {
            ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
            if (recipeSection == null) {
               return;
            }

            for (String recipeId : recipeSection.getKeys(false)) {
               List<String> rowOne = recipeSection.getStringList(recipeId + ".rowOne");
               List<String> rowTwo = recipeSection.getStringList(recipeId + ".rowTwo");
               List<String> rowThree = recipeSection.getStringList(recipeId + ".rowThree");
               this.registerRecipe(itemId, recipeId, rowOne, rowTwo, rowThree);
            }
         }
      }
   }

   private void registerRecipe(String itemId, String recipeId, List<String> rowOne, List<String> rowTwo, List<String> rowThree) {
      NamespacedKey recipeKey = new NamespacedKey(this.plugin, "recipe_" + itemId + "_" + recipeId);
      ItemStack resultItem = CustomItemManager.createCustomItem(itemId);
      ShapedRecipe recipe = new ShapedRecipe(recipeKey, resultItem);
      recipe.shape(new String[]{"ABC", "DEF", "GHI"});
      this.setIngredient(recipe, "A", rowOne.get(0));
      this.setIngredient(recipe, "B", rowOne.get(1));
      this.setIngredient(recipe, "C", rowOne.get(2));
      this.setIngredient(recipe, "D", rowTwo.get(0));
      this.setIngredient(recipe, "E", rowTwo.get(1));
      this.setIngredient(recipe, "F", rowTwo.get(2));
      this.setIngredient(recipe, "G", rowThree.get(0));
      this.setIngredient(recipe, "H", rowThree.get(1));
      this.setIngredient(recipe, "I", rowThree.get(2));
      Bukkit.addRecipe(recipe);
   }

   private void removeRecipe(String itemId) {
      FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
      boolean hasOneRecipe = config.isSet(itemId + ".recipe");
      if (hasOneRecipe) {
         NamespacedKey recipeKey = new NamespacedKey(this.plugin, "recipe_" + itemId + "_default");
         Bukkit.removeRecipe(recipeKey);
      } else {
         ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
         if (recipeSection == null) {
            return;
         }

         for (String recipeId : recipeSection.getKeys(false)) {
            NamespacedKey recipeKey = new NamespacedKey(this.plugin, "recipe_" + itemId + "_" + recipeId);
            Bukkit.removeRecipe(recipeKey);
         }
      }
   }

   private void setIngredient(ShapedRecipe recipe, String key, String material) {
      if (material != null && !material.equalsIgnoreCase("AIR") && !material.equalsIgnoreCase("empty")) {
         if (material.startsWith("#") && this.tagFromString(material.substring(1)) != null) {
            Tag<Material> tag = this.tagFromString(material.substring(1).toLowerCase());
            recipe.setIngredient(key.charAt(0), new MaterialChoice(tag));
         } else if (this.getItemIds().contains(material.toLowerCase())) {
            recipe.setIngredient(key.charAt(0), CustomItemManager.createCustomItem(material));
         } else if (Material.getMaterial(material.toUpperCase()) != null) {
            recipe.setIngredient(key.charAt(0), Material.valueOf(material.toUpperCase()));
         } else {
            throw new IllegalArgumentException("Invalid material or Tag: " + material + " (" + material.toUpperCase() + ")");
         }
      }
   }

   private Tag<Material> tagFromString(String tagName) {
      Tag<Material> blockTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName), Material.class);
      return blockTag != null ? blockTag : Bukkit.getTag("items", NamespacedKey.minecraft(tagName), Material.class);
   }

   private Set<String> getItemIds() {
      return this.plugin.getConfigManager().getCustomItemConfig().getKeys(false);
   }
}
