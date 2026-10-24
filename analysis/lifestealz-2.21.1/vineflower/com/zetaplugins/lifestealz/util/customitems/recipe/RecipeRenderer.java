package com.zetaplugins.lifestealz.util.customitems.recipe;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.GuiManager;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.customitems.CustomItem;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

final class RecipeRenderer {
   private final LifeStealZ plugin;
   private final Map<Inventory, List<Integer>> animationMap = new HashMap<>();

   public RecipeRenderer(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   private void addAnimation(Inventory inventory, int taskId) {
      if (this.animationMap.containsKey(inventory)) {
         this.animationMap.get(inventory).add(taskId);
      } else {
         this.animationMap.put(inventory, new ArrayList<>(Collections.singletonList(taskId)));
      }
   }

   public void cancelAnimations(Inventory inventory) {
      if (this.animationMap.containsKey(inventory)) {
         for (int taskId : this.animationMap.get(inventory)) {
            Bukkit.getScheduler().cancelTask(taskId);
         }

         this.animationMap.remove(inventory);
      }
   }

   public void renderRecipe(Player player, String itemId) {
      FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
      boolean isCraftable = config.getBoolean(itemId + ".craftable");
      if (!isCraftable) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotCraftable", "&cThis item is not craftable!"));
      } else {
         boolean hasOneRecipe = config.isSet(itemId + ".recipe");
         if (hasOneRecipe) {
            List<String> rowOne = config.getStringList(itemId + ".recipe.rowOne");
            List<String> rowTwo = config.getStringList(itemId + ".recipe.rowTwo");
            List<String> rowThree = config.getStringList(itemId + ".recipe.rowThree");
            this.openRecipeInventory(player, itemId, rowOne, rowTwo, rowThree);
         } else {
            ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
            if (recipeSection == null) {
               return;
            }

            String firstRecipeId = (String)recipeSection.getKeys(false).stream().findFirst().orElse(null);
            if (firstRecipeId == null) {
               return;
            }

            List<String> rowOne = recipeSection.getStringList(firstRecipeId + ".rowOne");
            List<String> rowTwo = recipeSection.getStringList(firstRecipeId + ".rowTwo");
            List<String> rowThree = recipeSection.getStringList(firstRecipeId + ".rowThree");
            this.openRecipeInventory(player, itemId, rowOne, rowTwo, rowThree);
         }
      }
   }

   public void renderRecipe(Player player, String itemId, String recipeId) {
      FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
      boolean isCraftable = config.getBoolean(itemId + ".craftable");
      if (!isCraftable) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotCraftable", "&cThis item is not craftable!"));
      } else if (!config.isSet(itemId + ".recipes")) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotFound", "&cThis recipe does not exist!"));
      } else if (!config.isSet(itemId + ".recipes." + recipeId)) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotFound", "&cThis recipe does not exist!"));
      } else {
         List<String> rowOne = config.getStringList(itemId + ".recipes." + recipeId + ".rowOne");
         List<String> rowTwo = config.getStringList(itemId + ".recipes." + recipeId + ".rowTwo");
         List<String> rowThree = config.getStringList(itemId + ".recipes." + recipeId + ".rowThree");
         this.openRecipeInventory(player, itemId, rowOne, rowTwo, rowThree);
      }
   }

   private void openRecipeInventory(Player player, String itemId, List<String> rowOne, List<String> rowTwo, List<String> rowThree) {
      Inventory inventory = Bukkit.createInventory(null, 45, MessageUtils.getAndFormatMsg(false, "recipeInventoryTitle", "&8Crafting recipe"));
      inventory.setItem(40, CustomItemManager.createCloseItem());
      ItemStack glass = new CustomItem(Material.GRAY_STAINED_GLASS_PANE).setName("&c ").makeForbidden().getItemStack();

      for (int slot : Arrays.asList(
         0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 14, 15, 16, 17, 18, 22, 23, 25, 26, 27, 31, 32, 33, 34, 35, 36, 37, 38, 39, 41, 42, 43, 44
      )) {
         inventory.setItem(slot, glass);
      }

      this.renderIngredient(inventory, 10, rowOne.get(0));
      this.renderIngredient(inventory, 11, rowOne.get(1));
      this.renderIngredient(inventory, 12, rowOne.get(2));
      this.renderIngredient(inventory, 19, rowTwo.get(0));
      this.renderIngredient(inventory, 20, rowTwo.get(1));
      this.renderIngredient(inventory, 21, rowTwo.get(2));
      this.renderIngredient(inventory, 28, rowThree.get(0));
      this.renderIngredient(inventory, 29, rowThree.get(1));
      this.renderIngredient(inventory, 30, rowThree.get(2));
      inventory.setItem(24, new CustomItem(CustomItemManager.createCustomItem(itemId)).makeForbidden().getItemStack());
      GuiManager.RECIPE_GUI_MAP.put(player.getUniqueId(), inventory);
      player.openInventory(inventory);
   }

   private void renderIngredient(Inventory inventory, int slot, String material) {
      if (material != null && !material.equalsIgnoreCase("AIR") && !material.equalsIgnoreCase("empty")) {
         if (material.startsWith("#") && this.tagFromString(material.substring(1)) != null) {
            Tag<Material> tag = this.tagFromString(material.substring(1).toLowerCase());
            Set<Material> materials = tag.getValues();
            this.startTagAnimation(inventory, slot, materials);
         } else if (this.getItemIds().contains(material.toLowerCase())) {
            inventory.setItem(slot, new CustomItem(CustomItemManager.createCustomItem(material)).makeForbidden().getItemStack());
         } else if (Material.getMaterial(material.toUpperCase()) != null) {
            inventory.setItem(slot, new CustomItem(new ItemStack(Material.valueOf(material.toUpperCase()), 1)).makeForbidden().getItemStack());
         } else {
            throw new IllegalArgumentException("Invalid material: " + material);
         }
      }
   }

   private Tag<Material> tagFromString(String tagName) {
      Tag<Material> blockTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName), Material.class);
      return blockTag != null ? blockTag : Bukkit.getTag("items", NamespacedKey.minecraft(tagName), Material.class);
   }

   private void startTagAnimation(Inventory inventory, int slot, Set<Material> materials) {
      List<Material> materialList = new ArrayList<>(materials);
      AtomicReference<Integer> index = new AtomicReference<>(0);
      if (!materialList.isEmpty()) {
         Runnable runnable = () -> {
            int currentIndex = index.get();
            inventory.setItem(slot, new CustomItem(materialList.get(currentIndex)).makeForbidden().getItemStack());
            index.set((currentIndex + 1) % materialList.size());
         };
         int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, runnable, 0L, 20L);
         if (taskId != -1) {
            this.addAnimation(inventory, taskId);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
               Bukkit.getScheduler().cancelTask(taskId);
               if (inventory != null) {
                  inventory.setItem(slot, new CustomItem(materialList.get(0)).makeForbidden().getItemStack());
               }
            }, 600L);
         }
      }
   }

   private Set<String> getItemIds() {
      return this.plugin.getConfigManager().getCustomItemConfig().getKeys(false);
   }
}
