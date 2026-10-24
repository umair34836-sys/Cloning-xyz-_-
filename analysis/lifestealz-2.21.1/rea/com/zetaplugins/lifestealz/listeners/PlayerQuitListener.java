// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.PlayerQuitListener
class PlayerQuitListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public PlayerQuitListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Storage storage = this.plugin.getStorage();
        if (storage instanceof CachedStorage) {
            CachedStorage cachedStorage = (CachedStorage) storage;
            cachedStorage.evict(event.getPlayer().getUniqueId());
        }
    }

}
