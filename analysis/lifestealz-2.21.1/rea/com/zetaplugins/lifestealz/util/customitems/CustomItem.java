// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems;
// rea: class com.zetaplugins.lifestealz.util.customitems.CustomItem
class CustomItem {
    org.bukkit.inventory.ItemStack itemStack;

    public CustomItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public CustomItem(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public CustomItem() {
        this.itemStack = new ItemStack(Material.AIR);
    }

    public CustomItem addEnchantment(Enchantment enchantment, int level) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem addFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(new ItemFlag[]{itemFlag});
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem addLore(String lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        List<Component> newLore = new ArrayList<>((Collection<? extends Component>) Objects.requireNonNull(itemMeta.lore()));
        newLore.add(MessageUtils.formatMsg(lore, new MessageUtils.Replaceable[0]));
        itemMeta.lore(newLore);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public PersistentDataContainer getPersistentDataContainer() {
        return this.itemStack.getItemMeta().getPersistentDataContainer();
    }

    public CustomItem makeForbidden() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(CustomItemManager.CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING, "forbidden");
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public CustomItem setCustomModelID(int customModelID) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setCustomModelData(Integer.valueOf(customModelID));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setDespawnable(boolean despawnable) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(CustomItemManager.DESPAWNABLE_KEY, PersistentDataType.BOOLEAN, Boolean.valueOf(despawnable));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setEnchanted(boolean enchanted) {
        if (!enchanted) {
            return this;
        }
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
        itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setId(String stringId, int intId) {
        CustomModelData.Builder customModelDataBuilder = CustomModelData.customModelData().addString(stringId);
        if (intId != 0) {
            customModelDataBuilder.addFloat(intId);
        }
        this.itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData) customModelDataBuilder.build());
        return this;
    }

    public CustomItem setInvulnerable(boolean invulnerable) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(CustomItemManager.INVULNERABLE_KEY, PersistentDataType.BOOLEAN, Boolean.valueOf(invulnerable));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setLore(List<String> lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        List<Component> newLore = new ArrayList<>(lore.size());
        for (String s : lore) {
            newLore.add(MessageUtils.formatMsg(s, new MessageUtils.Replaceable[0]));
        }
        itemMeta.lore(newLore);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setMaterial(Material material) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        int amount = this.itemStack.getAmount();
        ItemStack newItemStack = new ItemStack(material, amount);
        newItemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setName(String name) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(MessageUtils.formatMsg(name, new MessageUtils.Replaceable[0]));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setName(Component name) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(name);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setUnbreakable(boolean unbreakable) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

}
