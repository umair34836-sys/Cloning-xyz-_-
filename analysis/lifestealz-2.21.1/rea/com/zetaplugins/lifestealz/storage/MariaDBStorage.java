// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage;
// rea: class com.zetaplugins.lifestealz.storage.MariaDBStorage
class MariaDBStorage {
    com.zetaplugins.lifestealz.storage.connectionPool.MariaDBConnectionPool connectionPool;

    public MariaDBStorage(LifeStealZ plugin) {
        super(plugin);
        FileConfiguration config = getPlugin().getConfigManager().getStorageConfig();
        String HOST = config.getString("host");
        String PORT = config.getString("port");
        String DATABASE = config.getString("database");
        String USERNAME = config.getString("username");
        String PASSWORD = config.getString("password");
        this.connectionPool = new MariaDBConnectionPool(HOST, PORT, DATABASE, USERNAME, PASSWORD);
    }

    @Override // com.zetaplugins.lifestealz.storage.SQLStorage
    public ConnectionPool getConnectionPool() {
        return this.connectionPool;
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    protected void migrateDatabase() {
        try {
            Connection connection = getConnection();
            try {
                Statement statement = connection.createStatement();
                try {
                    ResultSet resultSet = statement.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'hearts' AND COLUMN_NAME = 'firstJoin'");
                    try {
                        if (!resultSet.next()) {
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
