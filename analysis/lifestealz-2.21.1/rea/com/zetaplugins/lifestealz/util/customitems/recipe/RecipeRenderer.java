// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems.recipe;
// rea: class com.zetaplugins.lifestealz.util.customitems.recipe.RecipeRenderer
class RecipeRenderer {
    java.util.Map<org.bukkit.inventory.Inventory, java.util.List<java.lang.Integer>> animationMap;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public RecipeRenderer(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private void addAnimation(Inventory inventory, int taskId) {
        if (!this.animationMap.containsKey(inventory)) {
            this.animationMap.put(inventory, new ArrayList(Collections.singletonList(Integer.valueOf(taskId))));
        } else {
            this.animationMap.get(inventory).add(Integer.valueOf(taskId));
        }
    }

    public void cancelAnimations(Inventory inventory) {
        if (this.animationMap.containsKey(inventory)) {
            Iterator<Integer> it = this.animationMap.get(inventory).iterator();
            while (it.hasNext()) {
                int taskId = it.next().intValue();
                Bukkit.getScheduler().cancelTask(taskId);
            }
            this.animationMap.remove(inventory);
        }
    }

    private Set<String> getItemIds() {
        return this.plugin.getConfigManager().getCustomItemConfig().getKeys(false);
    }

    // rea: lambda$startTagAnimation$0(java.util.concurrent.atomic.AtomicReference, org.bukkit.inventory.Inventory, int, java.util.List): void -> smali (java_body_unavailable)
    // .method private static synthetic lambda$startTagAnimation$0(Ljava/util/concurrent/atomic/AtomicReference;Lorg/bukkit/inventory/Inventory;ILjava/util/List;)V
    //     .max stack 6
    //     .max locals 5
    // 
    //     .local 0 "index" Ljava/util/concurrent/atomic/AtomicReference;
    //     .local 1 "inventory" Lorg/bukkit/inventory/Inventory;
    //     .local 2 "slot" I
    //     .local 3 "materialList" Ljava/util/List;
    //     .line 206
    //     aload 0
    //     invokevirtual java/util/concurrent/atomic/AtomicReference get ()Ljava/lang/Object;
    //     checkcast java/lang/Integer
    //     invokevirtual java/lang/Integer intValue ()I
    //     istore 4
    //     .local 4 "currentIndex" I
    //     .line 207
    //     aload 1
    //     iload 2
    //     new com/zetaplugins/lifestealz/util/customitems/CustomItem
    //     dup
    //     aload 3
    //     iload 4
    //     invokeinterface java/util/List get (I)Ljava/lang/Object;
    //     checkcast org/bukkit/Material
    //     invokespecial com/zetaplugins/lifestealz/util/customitems/CustomItem <init> (Lorg/bukkit/Material;)V
    //     invokevirtual com/zetaplugins/lifestealz/util/customitems/CustomItem makeForbidden ()Lcom/zetaplugins/lifestealz/util/customitems/CustomItem;
    //     invokevirtual com/zetaplugins/lifestealz/util/customitems/CustomItem getItemStack ()Lorg/bukkit/inventory/ItemStack;
    //     invokeinterface org/bukkit/inventory/Inventory setItem (ILorg/bukkit/inventory/ItemStack;)V
    //     .line 210
    //     aload 0
    //     iload 4
    //     iconst_1
    //     iadd
    //     aload 3
    //     invokeinterface java/util/List size ()I
    //     irem
    //     invokestatic java/lang/Integer valueOf (I)Ljava/lang/Integer;
    //     invokevirtual java/util/concurrent/atomic/AtomicReference set (Ljava/lang/Object;)V
    //     .line 211
    //     return
    // .end method

    // rea: lambda$startTagAnimation$1(int, org.bukkit.inventory.Inventory, int, java.util.List): void -> smali (java_body_unavailable)
    // .method private static synthetic lambda$startTagAnimation$1(ILorg/bukkit/inventory/Inventory;ILjava/util/List;)V
    //     .max stack 6
    //     .max locals 4
    // 
    //     .local 0 "taskId" I
    //     .local 1 "inventory" Lorg/bukkit/inventory/Inventory;
    //     .local 2 "slot" I
    //     .local 3 "materialList" Ljava/util/List;
    //     .line 221
    //     invokestatic org/bukkit/Bukkit getScheduler ()Lorg/bukkit/scheduler/BukkitScheduler;
    //     iload 0
    //     invokeinterface org/bukkit/scheduler/BukkitScheduler cancelTask (I)V
    //     .line 222
    //     aload 1
    //     ifnull :L2
    //     aload 1
    //     iload 2
    //     new com/zetaplugins/lifestealz/util/customitems/CustomItem
    //     dup
    //     aload 3
    //     iconst_0
    //     invokeinterface java/util/List get (I)Ljava/lang/Object;
    //     checkcast org/bukkit/Material
    //     invokespecial com/zetaplugins/lifestealz/util/customitems/CustomItem <init> (Lorg/bukkit/Material;)V
    //     invokevirtual com/zetaplugins/lifestealz/util/customitems/CustomItem makeForbidden ()Lcom/zetaplugins/lifestealz/util/customitems/CustomItem;
    //     invokevirtual com/zetaplugins/lifestealz/util/customitems/CustomItem getItemStack ()Lorg/bukkit/inventory/ItemStack;
    //     invokeinterface org/bukkit/inventory/Inventory setItem (ILorg/bukkit/inventory/ItemStack;)V
    //   :L2
    //     .line 223
    //     .stack same
    //     return
    // .end method

    private void openRecipeInventory(Player player, String itemId, List<String> rowOne, List<String> rowTwo, List<String> rowThree) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder) null, 45, MessageUtils.getAndFormatMsg(false, "recipeInventoryTitle", "&8Crafting recipe", new MessageUtils.Replaceable[0]));
        inventory.setItem(40, CustomItemManager.createCloseItem());
        ItemStack glass = new CustomItem(Material.GRAY_STAINED_GLASS_PANE).setName("&c ").makeForbidden().getItemStack();
        List<Integer> glassSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 14, 15, 16, 17, 18, 22, 23, 25, 26, 27, 31, 32, 33, 34, 35, 36, 37, 38, 39, 41, 42, 43, 44);
        Iterator<Integer> it = glassSlots.iterator();
        while (it.hasNext()) {
            int slot = it.next().intValue();
            inventory.setItem(slot, glass);
        }
        renderIngredient(inventory, 10, rowOne.get(0));
        renderIngredient(inventory, 11, rowOne.get(1));
        renderIngredient(inventory, 12, rowOne.get(2));
        renderIngredient(inventory, 19, rowTwo.get(0));
        renderIngredient(inventory, 20, rowTwo.get(1));
        renderIngredient(inventory, 21, rowTwo.get(2));
        renderIngredient(inventory, 28, rowThree.get(0));
        renderIngredient(inventory, 29, rowThree.get(1));
        renderIngredient(inventory, 30, rowThree.get(2));
        inventory.setItem(24, new CustomItem(CustomItemManager.createCustomItem(itemId)).makeForbidden().getItemStack());
        GuiManager.RECIPE_GUI_MAP.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }

    private void renderIngredient(Inventory inventory, int slot, String material) {
        if (material == null || material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("empty")) {
            return;
        }
        if (material.startsWith("#") && tagFromString(material.substring(1)) != null) {
            Tag<Material> tag = tagFromString(material.substring(1).toLowerCase());
            Set<Material> materials = tag.getValues();
            startTagAnimation(inventory, slot, materials);
        } else if (getItemIds().contains(material.toLowerCase())) {
            inventory.setItem(slot, new CustomItem(CustomItemManager.createCustomItem(material)).makeForbidden().getItemStack());
        } else {
            if (Material.getMaterial(material.toUpperCase()) != null) {
                inventory.setItem(slot, new CustomItem(new ItemStack(Material.valueOf(material.toUpperCase()), 1)).makeForbidden().getItemStack());
                return;
            }
            throw new IllegalArgumentException("Invalid material: " + material);
        }
    }

    public void renderRecipe(Player player, String itemId) {
        String firstRecipeId;
        FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
        boolean isCraftable = config.getBoolean(itemId + ".craftable");
        if (!isCraftable) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotCraftable", "&cThis item is not craftable!", new MessageUtils.Replaceable[0]));
            return;
        }
        boolean hasOneRecipe = config.isSet(itemId + ".recipe");
        if (hasOneRecipe) {
            List<String> rowOne = config.getStringList(itemId + ".recipe.rowOne");
            List<String> rowTwo = config.getStringList(itemId + ".recipe.rowTwo");
            List<String> rowThree = config.getStringList(itemId + ".recipe.rowThree");
            openRecipeInventory(player, itemId, rowOne, rowTwo, rowThree);
            return;
        }
        ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
        if (recipeSection == null || (firstRecipeId = (String) recipeSection.getKeys(false).stream().findFirst().orElse(null)) == null) {
            return;
        }
        List<String> rowOne2 = recipeSection.getStringList(firstRecipeId + ".rowOne");
        List<String> rowTwo2 = recipeSection.getStringList(firstRecipeId + ".rowTwo");
        List<String> rowThree2 = recipeSection.getStringList(firstRecipeId + ".rowThree");
        openRecipeInventory(player, itemId, rowOne2, rowTwo2, rowThree2);
    }

    public void renderRecipe(Player player, String itemId, String recipeId) {
        FileConfiguration config = this.plugin.getConfigManager().getCustomItemConfig();
        boolean isCraftable = config.getBoolean(itemId + ".craftable");
        if (!isCraftable) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotCraftable", "&cThis item is not craftable!", new MessageUtils.Replaceable[0]));
            return;
        }
        if (!config.isSet(itemId + ".recipes")) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotFound", "&cThis recipe does not exist!", new MessageUtils.Replaceable[0]));
            return;
        }
        if (!config.isSet(itemId + ".recipes." + recipeId)) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "recipeNotFound", "&cThis recipe does not exist!", new MessageUtils.Replaceable[0]));
            return;
        }
        List<String> rowOne = config.getStringList(itemId + ".recipes." + recipeId + ".rowOne");
        List<String> rowTwo = config.getStringList(itemId + ".recipes." + recipeId + ".rowTwo");
        List<String> rowThree = config.getStringList(itemId + ".recipes." + recipeId + ".rowThree");
        openRecipeInventory(player, itemId, rowOne, rowTwo, rowThree);
    }

    private void startTagAnimation(Inventory inventory, int slot, Set<Material> materials) {
        List<Material> materialList = new ArrayList<>(materials);
        AtomicReference<Integer> index = new AtomicReference<>(0);
        if (materialList.isEmpty()) {
            return;
        }
        Runnable runnable = () -> {
            int currentIndex = ((Integer) index.get()).intValue();
            inventory.setItem(slot, new CustomItem((Material) materialList.get(currentIndex)).makeForbidden().getItemStack());
            index.set(Integer.valueOf((currentIndex + 1) % materialList.size()));
        };
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, runnable, 0L, 20L);
        if (taskId == -1) {
            return;
        }
        addAnimation(inventory, taskId);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            Bukkit.getScheduler().cancelTask(taskId);
            if (inventory != null) {
                inventory.setItem(slot, new CustomItem((Material) materialList.get(0)).makeForbidden().getItemStack());
            }
        }, 600L);
    }

    private Tag<Material> tagFromString(String tagName) {
        Tag<Material> blockTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName), Material.class);
        return blockTag != null ? blockTag : Bukkit.getTag("items", NamespacedKey.minecraft(tagName), Material.class);
    }

}
