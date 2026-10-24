package com.zetaplugins.lifestealz.storage;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CachedStorage extends Storage {
   private final Storage delegate;
   private final ConcurrentHashMap<UUID, PlayerData> cache = new ConcurrentHashMap<>();

   public CachedStorage(LifeStealZ plugin, Storage delegate) {
      super(plugin);
      this.delegate = delegate;
   }

   public void evict(UUID uuid) {
      PlayerData data = this.cache.remove(uuid);
      if (data != null && data.hasChanges()) {
         this.delegate.save(data);
      }
   }

   public boolean isCached(UUID uuid) {
      return this.cache.containsKey(uuid);
   }

   @Override
   public PlayerData load(UUID uuid) {
      PlayerData cached = this.cache.get(uuid);
      if (cached != null) {
         return cached;
      } else {
         PlayerData data = this.delegate.load(uuid);
         if (data != null) {
            this.cache.put(uuid, data);
         }

         return data;
      }
   }

   @Override
   public PlayerData load(String uuid) {
      return this.load(UUID.fromString(uuid));
   }

   @Override
   public void save(PlayerData playerData) {
      this.delegate.save(playerData);

      try {
         this.cache.put(UUID.fromString(playerData.getUuid()), playerData);
      } catch (IllegalArgumentException var3) {
         this.getPlugin().getLogger().warning("CachedStorage: skipping cache put for invalid UUID: " + playerData.getUuid());
      }
   }

   @Override
   public int reviveAllPlayers(int minHearts, int reviveHearts, int maxRevives, boolean bypassReviveLimit) {
      int affected = this.delegate.reviveAllPlayers(minHearts, reviveHearts, maxRevives, bypassReviveLimit);
      if (affected > 0) {
         this.cache.clear();
      }

      return affected;
   }

   @Override
   public void importData(String fileName) {
      this.delegate.importData(fileName);
      this.cache.clear();
   }

   @Override
   public void clearDatabase() {
      this.delegate.clearDatabase();
      this.cache.clear();
   }

   @Override
   public void init() {
      this.delegate.init();
   }

   @Override
   public List<UUID> getEliminatedPlayers() {
      return this.delegate.getEliminatedPlayers();
   }

   @Override
   public String export(String fileName) {
      return this.delegate.export(fileName);
   }

   @Override
   public List<String> getPlayerNames() {
      return this.delegate.getPlayerNames();
   }

   @Override
   public List<String> getEliminatedPlayerNames() {
      return this.delegate.getEliminatedPlayerNames();
   }

   @Override
   protected void migrateDatabase() {
      this.delegate.migrateDatabase();
   }
}
