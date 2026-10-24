// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage;
// rea: class com.zetaplugins.lifestealz.storage.MySQLSyntaxStorage
class MySQLSyntaxStorage {
/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/storage/MySQLSyntaxStorage.class */
public abstract class MySQLSyntaxStorage extends SQLStorage {
    public MySQLSyntaxStorage(LifeStealZ plugin) {
        super(plugin);
    }

    @Override // com.zetaplugins.lifestealz.storage.SQLStorage
    protected String getInserOrReplaceStatement() {
        return "INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), maxhp = VALUES(maxhp), hasbeenRevived = VALUES(hasbeenRevived), craftedHearts = VALUES(craftedHearts), craftedRevives = VALUES(craftedRevives), killedOtherPlayers = VALUES(killedOtherPlayers), firstJoin = VALUES(firstJoin)";
    }

    @Override // com.zetaplugins.lifestealz.storage.SQLStorage, com.zetaplugins.lifestealz.storage.Storage
    public void save(PlayerData playerData) {
        try {
            Connection connection = getConnection();
            try {
                if (connection != null) {
                    try {
                        PreparedStatement stmt = connection.prepareStatement("INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), maxhp = VALUES(maxhp), hasbeenRevived = VALUES(hasbeenRevived), craftedHearts = VALUES(craftedHearts), craftedRevives = VALUES(craftedRevives), killedOtherPlayers = VALUES(killedOtherPlayers), firstJoin = VALUES(firstJoin)");
                        try {
                            stmt.setString(1, playerData.getUuid());
                            stmt.setString(2, playerData.getName());
                            stmt.setDouble(3, playerData.getMaxHealth());
                            stmt.setInt(4, playerData.getHasBeenRevived());
                            stmt.setInt(5, playerData.getCraftedHearts());
                            stmt.setInt(6, playerData.getCraftedRevives());
                            stmt.setInt(7, playerData.getKilledOtherPlayers());
                            stmt.setLong(8, playerData.getFirstJoin());
                            stmt.executeUpdate();
                            playerData.clearModifiedFields();
                            if (stmt != null) {
                                stmt.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (Throwable th) {
                            if (stmt != null) {
                                try {
                                    stmt.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (SQLException e) {
                        getPlugin().getLogger().severe("Failed to save player data to database: " + e.getMessage());
                    }
                } else if (connection != null) {
                    connection.close();
                }
            } catch (Throwable th3) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (SQLException e2) {
            getPlugin().getLogger().severe("Failed to save player data to database: " + e2.getMessage());
        }
    }

}
