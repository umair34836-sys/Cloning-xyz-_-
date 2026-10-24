package com.zetaplugins.lifestealz.util.customblocks;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;

public enum CustomBlock {
   REVIVE_BEACON("revive_beacon");

   private static final NamespacedKey CUSTOM_BLOCK_KEY = new NamespacedKey("lifestealz", "custom_block");
   private static final NamespacedKey CUSTOM_ITEM_ID_KEY = new NamespacedKey("lifestealz", "custom_item_id_block");
   private final String key;

   private CustomBlock(String key) {
      this.key = key;
   }

   public String getKey() {
      return this.key;
   }

   public void make(Block block) {
      BlockTagManager.tagBlock(block, CUSTOM_BLOCK_KEY, PersistentDataType.STRING, this.getKey());
   }

   public void make(Block block, String customItemId) {
      BlockTagManager.tagBlock(block, CUSTOM_BLOCK_KEY, PersistentDataType.STRING, this.getKey());
      BlockTagManager.tagBlock(block, CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING, customItemId);
   }

   public boolean is(Block block) {
      String blockKey = BlockTagManager.getBlockTag(block, CUSTOM_BLOCK_KEY, PersistentDataType.STRING);
      return blockKey != null && blockKey.equalsIgnoreCase(this.getKey());
   }

   public String getCustomItemId(Block block) {
      return BlockTagManager.getBlockTag(block, CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING);
   }

   public static CustomBlock fromKey(String key) {
      for (CustomBlock customBlock : values()) {
         if (customBlock.getKey().equalsIgnoreCase(key)) {
            return customBlock;
         }
      }

      return null;
   }
}
