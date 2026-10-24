// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.caches;
// rea: class com.zetaplugins.lifestealz.caches.EliminatedPlayersCache
class EliminatedPlayersCache {
/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/caches/EliminatedPlayersCache.class */
public final class EliminatedPlayersCache extends Cache<String> {
    public EliminatedPlayersCache(LifeStealZ plugin) {
        super(plugin);
    }

    public void addEliminatedPlayer(String username) {
        addItem(username);
    }

    public Set<String> getEliminatedPlayers() {
        return getCachedData();
    }

    @Override // com.zetaplugins.lifestealz.caches.Cache
    public void reloadCache() {
        clearCache();
        addAllItems(new HashSet(getPlugin().getStorage().getEliminatedPlayerNames()));
    }

    public void removeEliminatedPlayer(String username) {
        removeItem(username);
    }

}
