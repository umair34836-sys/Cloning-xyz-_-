// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage;
// rea: class com.zetaplugins.lifestealz.storage.Storage
class Storage {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public Storage(LifeStealZ plugin) {
        this.plugin = plugin;
    }


    // rea: clearDatabase(): void — no body (not_available; abstract_method)


    // rea: export(java.lang.String): java.lang.String — no body (not_available; abstract_method)


    // rea: getEliminatedPlayerNames(): java.util.List<java.lang.String> — no body (not_available; abstract_method)


    // rea: getEliminatedPlayers(): java.util.List<java.util.UUID> — no body (not_available; abstract_method)


    // rea: getPlayerNames(): java.util.List<java.lang.String> — no body (not_available; abstract_method)

    protected LifeStealZ getPlugin() {
        return this.plugin;
    }


    // rea: importData(java.lang.String): void — no body (not_available; abstract_method)


    // rea: init(): void — no body (not_available; abstract_method)


    // rea: load(java.lang.String): com.zetaplugins.lifestealz.storage.PlayerData — no body (not_available; abstract_method)


    // rea: load(java.util.UUID): com.zetaplugins.lifestealz.storage.PlayerData — no body (not_available; abstract_method)


    // rea: migrateDatabase(): void — no body (not_available; abstract_method)


    // rea: reviveAllPlayers(int, int, int, boolean): int — no body (not_available; abstract_method)


    // rea: save(com.zetaplugins.lifestealz.storage.PlayerData): void — no body (not_available; abstract_method)

}
