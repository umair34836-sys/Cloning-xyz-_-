package com.zetaplugins.lifestealz.caches;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.HashSet;
import java.util.Set;

public abstract class Cache<T> {
   private final Set<T> cachedData;
   private final LifeStealZ plugin;

   public Cache(LifeStealZ plugin) {
      this.plugin = plugin;
      this.cachedData = new HashSet<>();
      this.reloadCache();
   }

   public abstract void reloadCache();

   public Set<T> getCachedData() {
      return new HashSet<>(this.cachedData);
   }

   public void addItem(T item) {
      this.cachedData.add(item);
   }

   public void removeItem(T item) {
      this.cachedData.remove(item);
   }

   public void addAllItems(Set<T> items) {
      this.cachedData.addAll(items);
   }

   public void clearCache() {
      this.cachedData.clear();
   }

   protected LifeStealZ getPlugin() {
      return this.plugin;
   }
}
