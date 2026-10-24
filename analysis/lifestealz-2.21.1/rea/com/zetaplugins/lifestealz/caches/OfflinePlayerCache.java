// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.caches;
// rea: class com.zetaplugins.lifestealz.caches.OfflinePlayerCache
class OfflinePlayerCache {
/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/caches/OfflinePlayerCache.class */
public final class OfflinePlayerCache extends Cache<String> {
    public OfflinePlayerCache(LifeStealZ plugin) {
        super(plugin);
    }

    @Override // com.zetaplugins.lifestealz.caches.Cache
    public void reloadCache() {
        clearCache();
        addAllItems(new HashSet(getPlugin().getStorage().getPlayerNames()));
    }

}
