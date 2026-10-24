// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage.connectionPool;
// rea: class com.zetaplugins.lifestealz.storage.connectionPool.MySQLConnectionPool
class MySQLConnectionPool {
    com.zaxxer.hikari.HikariDataSource dataSource;

    public MySQLConnectionPool(String host, String port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000L);
        config.setIdleTimeout(600000L);
        config.setMaxLifetime(1800000L);
        this.dataSource = new HikariDataSource(config);
    }

    @Override // com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool
    public Connection getConnection() throws SQLException {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Failed to get connection from MySQL connection pool: " + e.getMessage());
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool
    public void releaseConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool
    public void shutdown() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

}
