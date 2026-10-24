package com.zetaplugins.lifestealz.caches;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.HashSet;
import java.util.Set;

public final class EliminatedPlayersCache extends Cache<String> {
   public EliminatedPlayersCache(LifeStealZ plugin) {
      super(plugin);
   }

   @Override
   public void reloadCache() {
      this.clearCache();
      Set<String> eliminatedPlayerNames = new HashSet<>(this.getPlugin().getStorage().getEliminatedPlayerNames());
      this.addAllItems(eliminatedPlayerNames);
   }

   public Set<String> getEliminatedPlayers() {
      return this.getCachedData();
   }

   public void addEliminatedPlayer(String username) {
      this.addItem(username);
   }

   public void removeEliminatedPlayer(String username) {
      this.removeItem(username);
   }
}
