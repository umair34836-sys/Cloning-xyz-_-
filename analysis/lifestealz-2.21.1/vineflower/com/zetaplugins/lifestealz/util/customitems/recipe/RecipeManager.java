package com.zetaplugins.lifestealz.util.customitems.recipe;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class RecipeManager {
   private final LifeStealZ plugin;
   private final RecipeRenderer recipeRenderer;
   private final RecipeRegistrar recipeRegistrar;

   public RecipeManager(LifeStealZ plugin) {
      this.plugin = plugin;
      this.recipeRenderer = new RecipeRenderer(plugin);
      this.recipeRegistrar = new RecipeRegistrar(plugin);
   }

   public Set<String> getItemIds() {
      return this.plugin.getConfigManager().getCustomItemConfig().getKeys(false);
   }

   public Set<String> getRecipeIds(String itemId) {
      Set<String> recipeIds = new HashSet<>();
      FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
      if (!config.isSet(itemId + ".recipes")) {
         return recipeIds;
      } else {
         ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
         if (recipeSection == null) {
            return recipeIds;
         } else {
            recipeIds.addAll(recipeSection.getKeys(false));
            return recipeIds;
         }
      }
   }

   public void registerRecipes() {
      this.recipeRegistrar.registerRecipes();
   }

   public boolean isCraftable(String itemId) {
      return this.plugin.getConfigManager().getCustomItemConfig().getBoolean(itemId + ".craftable");
   }

   public void renderRecipe(Player player, String itemId) {
      this.recipeRenderer.renderRecipe(player, itemId);
   }

   public void renderRecipe(Player player, String itemId, String recipeId) {
      this.recipeRenderer.renderRecipe(player, itemId, recipeId);
   }

   public void cancelAnimations(Inventory inventory) {
      this.recipeRenderer.cancelAnimations(inventory);
   }
}
