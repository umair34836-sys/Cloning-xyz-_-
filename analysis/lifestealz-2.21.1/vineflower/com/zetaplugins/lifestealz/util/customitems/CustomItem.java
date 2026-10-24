package com.zetaplugins.lifestealz.util.customitems;

import com.zetaplugins.lifestealz.util.MessageUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.CustomModelData.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class CustomItem {
   private final ItemStack itemStack;

   public CustomItem(ItemStack itemStack) {
      this.itemStack = itemStack;
   }

   public CustomItem(Material material) {
      this.itemStack = new ItemStack(material);
   }

   public CustomItem() {
      this.itemStack = new ItemStack(Material.AIR);
   }

   public CustomItem setMaterial(Material material) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      int amount = this.itemStack.getAmount();
      ItemStack newItemStack = new ItemStack(material, amount);
      newItemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem setAmount(int amount) {
      this.itemStack.setAmount(amount);
      return this;
   }

   public CustomItem setName(String name) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      itemMeta.displayName(MessageUtils.formatMsg(name));
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem setName(Component name) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      itemMeta.displayName(name);
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem setCustomModelID(int customModelID) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      itemMeta.setCustomModelData(customModelID);
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem setUnbreakable(boolean unbreakable) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      itemMeta.setUnbreakable(unbreakable);
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem setEnchanted(boolean enchanted) {
      if (!enchanted) {
         return this;
      } else {
         ItemMeta itemMeta = this.itemStack.getItemMeta();
         itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
         this.itemStack.setItemMeta(itemMeta);
         return this;
      }
   }

   public CustomItem addFlag(ItemFlag itemFlag) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      itemMeta.addItemFlags(new ItemFlag[]{itemFlag});
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem setDespawnable(boolean despawnable) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      itemMeta.getPersistentDataContainer().set(CustomItemManager.DESPAWNABLE_KEY, PersistentDataType.BOOLEAN, despawnable);
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem setInvulnerable(boolean invulnerable) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      itemMeta.getPersistentDataContainer().set(CustomItemManager.INVULNERABLE_KEY, PersistentDataType.BOOLEAN, invulnerable);
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem addEnchantment(Enchantment enchantment, int level) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      itemMeta.addEnchant(enchantment, level, true);
      this.itemStack.setItemMeta(itemMeta);
      return this;
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

   public CustomItem setLore(List<String> lore) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      List<Component> newLore = new ArrayList<>(lore.size());

      for (String s : lore) {
         newLore.add(MessageUtils.formatMsg(s));
      }

      itemMeta.lore(newLore);
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem addLore(String lore) {
      ItemMeta itemMeta = this.itemStack.getItemMeta();
      List<Component> newLore = new ArrayList<>(Objects.requireNonNull(itemMeta.lore()));
      newLore.add(MessageUtils.formatMsg(lore));
      itemMeta.lore(newLore);
      this.itemStack.setItemMeta(itemMeta);
      return this;
   }

   public CustomItem setId(String stringId, int intId) {
      Builder customModelDataBuilder = CustomModelData.customModelData().addString(stringId);
      if (intId != 0) {
         customModelDataBuilder.addFloat((float)intId);
      }

      this.itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData)customModelDataBuilder.build());
      return this;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }
}
