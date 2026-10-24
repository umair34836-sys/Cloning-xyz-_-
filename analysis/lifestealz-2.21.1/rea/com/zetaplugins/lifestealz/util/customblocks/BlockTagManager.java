// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customblocks;
// rea: class com.zetaplugins.lifestealz.util.customblocks.BlockTagManager
class BlockTagManager {
/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/util/customblocks/BlockTagManager.class */
public final class BlockTagManager {
    private BlockTagManager() {
    }

    public static <P, C> C getBlockTag(Block block, NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType) {
        TileState state = block.getState();
        if (state instanceof TileState) {
            return (C) state.getPersistentDataContainer().get(namespacedKey, persistentDataType);
        }
        return null;
    }

    public static <P, C> void tagBlock(Block block, NamespacedKey key, PersistentDataType<P, C> type, C value) {
        TileState state = block.getState();
        if (state instanceof TileState) {
            TileState tileState = state;
            PersistentDataContainer container = tileState.getPersistentDataContainer();
            container.set(key, type, value);
            tileState.update();
        }
    }

}
