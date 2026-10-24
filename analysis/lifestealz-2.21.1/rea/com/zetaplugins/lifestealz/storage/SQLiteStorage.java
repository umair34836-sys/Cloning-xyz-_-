// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage;
// rea: class com.zetaplugins.lifestealz.storage.SQLiteStorage
class SQLiteStorage {
    com.zetaplugins.lifestealz.storage.connectionPool.SQLiteConnectionPool connectionPool;

    public SQLiteStorage(LifeStealZ plugin) {
        super(plugin);
        this.connectionPool = new SQLiteConnectionPool(getPlugin().getDataFolder().getPath() + "/userData.db");
    }

    @Override // com.zetaplugins.lifestealz.storage.SQLStorage
    public ConnectionPool getConnectionPool() {
        return this.connectionPool;
    }

    @Override // com.zetaplugins.lifestealz.storage.SQLStorage
    protected String getInserOrReplaceStatement() {
        return "INSERT OR REPLACE INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    protected void migrateDatabase() {
        try {
            Connection connection = getConnection();
            try {
                Statement statement = connection.createStatement();
                try {
                    ResultSet resultSet = statement.executeQuery("PRAGMA table_info(hearts)");
                    boolean hasFirstJoin = false;
                    while (resultSet.next()) {
                        try {
                            if ("firstJoin".equalsIgnoreCase(resultSet.getString("name"))) {
                                hasFirstJoin = true;
                                break;
                            }
                        } catch (Throwable th) {
                            if (resultSet != null) {
                                try {
                                    resultSet.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    }
                    if (!hasFirstJoin) {
                        getPlugin().getLogger().info("Adding 'firstJoin' column to 'hearts' table.");
                        statement.executeUpdate("ALTER TABLE hearts ADD COLUMN firstJoin INTEGER DEFAULT 0");
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Throwable th3) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Throwable th6) {
                        th5.addSuppressed(th6);
                    }
                }
                throw th5;
            }
        } catch (SQLException e) {
            getPlugin().getLogger().log(Level.SEVERE, "Failed to migrate database: ", (Throwable) e);
        }
    }

}
