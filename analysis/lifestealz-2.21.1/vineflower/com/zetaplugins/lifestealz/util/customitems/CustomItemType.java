package com.zetaplugins.lifestealz.util.customitems;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public enum CustomItemType {
   HEART("heart"),
   REVIVE("revive"),
   REVIVE_BEACON("revivebeacon"),
   NONE("none"),
   NONUSABLE("non-usable");

   private final String type;

   private CustomItemType(String type) {
      this.type = type;
   }

   public String getType() {
      return this.type;
   }

   public boolean is(ItemStack item) {
      try {
         return item.getItemMeta() != null
            && item.getItemMeta().getPersistentDataContainer().has(CustomItemManager.CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING)
            && ((String)item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING))
               .equalsIgnoreCase(this.getType());
      } catch (NullPointerException var3) {
         return false;
      }
   }

   public static CustomItemType fromString(String type) {
      for (CustomItemType itemType : values()) {
         if (itemType.getType().equalsIgnoreCase(type)) {
            return itemType;
         }
      }

      return NONE;
   }

   public static CustomItemType fromCustomItem(String customItemId, FileConfiguration config) {
      String type = config.getString(customItemId + ".customItemType", "none");
      return fromString(type);
   }
}
