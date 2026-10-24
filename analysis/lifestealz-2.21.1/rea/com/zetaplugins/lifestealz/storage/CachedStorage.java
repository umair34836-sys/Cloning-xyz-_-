// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage;
// rea: class com.zetaplugins.lifestealz.storage.CachedStorage
class CachedStorage {
    java.util.concurrent.ConcurrentHashMap<java.util.UUID, com.zetaplugins.lifestealz.storage.PlayerData> cache;
    com.zetaplugins.lifestealz.storage.Storage delegate;

    public CachedStorage(LifeStealZ plugin, Storage delegate) {
        super(plugin);
        this.cache = new ConcurrentHashMap<>();
        this.delegate = delegate;
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public void clearDatabase() {
        this.delegate.clearDatabase();
        this.cache.clear();
    }

    public void evict(UUID uuid) {
        PlayerData data = this.cache.remove(uuid);
        if (data != null && data.hasChanges()) {
            this.delegate.save(data);
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public String export(String fileName) {
        return this.delegate.export(fileName);
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public List<String> getEliminatedPlayerNames() {
        return this.delegate.getEliminatedPlayerNames();
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public List<UUID> getEliminatedPlayers() {
        return this.delegate.getEliminatedPlayers();
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public List<String> getPlayerNames() {
        return this.delegate.getPlayerNames();
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public void importData(String fileName) {
        this.delegate.importData(fileName);
        this.cache.clear();
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public void init() {
        this.delegate.init();
    }

    public boolean isCached(UUID uuid) {
        return this.cache.containsKey(uuid);
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public PlayerData load(UUID uuid) {
        PlayerData cached = this.cache.get(uuid);
        if (cached != null) {
            return cached;
        }
        PlayerData data = this.delegate.load(uuid);
        if (data != null) {
            this.cache.put(uuid, data);
        }
        return data;
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public PlayerData load(String uuid) {
        return load(UUID.fromString(uuid));
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    protected void migrateDatabase() {
        this.delegate.migrateDatabase();
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public int reviveAllPlayers(int minHearts, int reviveHearts, int maxRevives, boolean bypassReviveLimit) {
        int affected = this.delegate.reviveAllPlayers(minHearts, reviveHearts, maxRevives, bypassReviveLimit);
        if (affected > 0) {
            this.cache.clear();
        }
        return affected;
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public void save(PlayerData playerData) {
        this.delegate.save(playerData);
        try {
            this.cache.put(UUID.fromString(playerData.getUuid()), playerData);
        } catch (IllegalArgumentException e) {
            getPlugin().getLogger().warning("CachedStorage: skipping cache put for invalid UUID: " + playerData.getUuid());
        }
    }

}
