package com.zetaplugins.lifestealz.storage.connectionPool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class MariaDBConnectionPool implements ConnectionPool {
   private final HikariDataSource dataSource;

   public MariaDBConnectionPool(String host, String port, String database, String username, String password) {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
      config.setUsername(username);
      config.setPassword(password);
      config.setMaximumPoolSize(10);
      config.setConnectionTimeout(30000L);
      config.setIdleTimeout(600000L);
      config.setMaxLifetime(1800000L);
      this.dataSource = new HikariDataSource(config);
   }

   @Override
   public Connection getConnection() throws SQLException {
      try {
         return this.dataSource.getConnection();
      } catch (SQLException var2) {
         throw new SQLException("Failed to get connection from MariaDB connection pool: " + var2.getMessage());
      }
   }

   @Override
   public void releaseConnection(Connection connection) throws SQLException {
      if (connection != null) {
         connection.close();
      }
   }

   @Override
   public void shutdown() {
      if (this.dataSource != null) {
         this.dataSource.close();
      }
   }
}
