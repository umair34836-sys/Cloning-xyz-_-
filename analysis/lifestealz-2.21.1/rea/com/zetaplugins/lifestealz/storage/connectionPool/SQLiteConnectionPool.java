// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage.connectionPool;
// rea: class com.zetaplugins.lifestealz.storage.connectionPool.SQLiteConnectionPool
class SQLiteConnectionPool {
    java.sql.Connection connection;
    java.lang.String connectionUrl;

    public SQLiteConnectionPool(String path) {
        this.connectionUrl = "jdbc:sqlite:" + path;
    }

    @Override // com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool
    public synchronized Connection getConnection() throws SQLException {
        if (isConnectionDead()) {
            this.connection = DriverManager.getConnection(this.connectionUrl);
        }
        return this.connection;
    }

    private boolean isConnectionDead() {
        try {
            return this.connection == null || this.connection.isClosed() || !this.connection.isValid(1);
        } catch (SQLException e) {
            return true;
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool
    public void releaseConnection(Connection connection) {
    }

    @Override // com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool
    public synchronized void shutdown() {
        if (!isConnectionDead()) {
            try {
                this.connection.close();
            } catch (SQLException e) {
            } finally {
                this.connection = null;
            }
        }
    }

}
