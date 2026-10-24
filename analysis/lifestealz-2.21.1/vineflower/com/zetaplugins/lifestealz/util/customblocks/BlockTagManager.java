package com.zetaplugins.lifestealz.util.customblocks;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class BlockTagManager {
   private BlockTagManager() {
   }

   public static <P, C> void tagBlock(Block block, NamespacedKey key, PersistentDataType<P, C> type, C value) {
      if (block.getState() instanceof TileState tileState) {
         PersistentDataContainer container = tileState.getPersistentDataContainer();
         container.set(key, type, value);
         tileState.update();
      }
   }

   public static <P, C> C getBlockTag(Block block, NamespacedKey key, PersistentDataType<P, C> type) {
      if (block.getState() instanceof TileState tileState) {
         PersistentDataContainer container = tileState.getPersistentDataContainer();
         return (C)container.get(key, type);
      } else {
         return null;
      }
   }
}
