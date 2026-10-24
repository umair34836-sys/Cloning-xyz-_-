// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.caches;
// rea: class com.zetaplugins.lifestealz.caches.Cache
class Cache {
    java.util.Set<T> cachedData;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public Cache(LifeStealZ plugin) {
        this.plugin = plugin;
        reloadCache();
    }

    public void addAllItems(Set<T> items) {
        this.cachedData.addAll(items);
    }

    public void addItem(T item) {
        this.cachedData.add(item);
    }

    public void clearCache() {
        this.cachedData.clear();
    }

    public Set<T> getCachedData() {
        return new HashSet(this.cachedData);
    }

    protected LifeStealZ getPlugin() {
        return this.plugin;
    }


    // rea: reloadCache(): void — no body (not_available; abstract_method)

    public void removeItem(T item) {
        this.cachedData.remove(item);
    }

}
