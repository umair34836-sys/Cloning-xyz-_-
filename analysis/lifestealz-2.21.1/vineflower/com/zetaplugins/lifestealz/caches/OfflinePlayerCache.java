package com.zetaplugins.lifestealz.caches;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.HashSet;
import java.util.Set;

public final class OfflinePlayerCache extends Cache<String> {
   public OfflinePlayerCache(LifeStealZ plugin) {
      super(plugin);
   }

   @Override
   public void reloadCache() {
      this.clearCache();
      Set<String> offlinePlayerNames = new HashSet<>(this.getPlugin().getStorage().getPlayerNames());
      this.addAllItems(offlinePlayerNames);
   }
}
