package com.zetaplugins.lifestealz.storage.connectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLiteConnectionPool implements ConnectionPool {
   private final String connectionUrl;
   private Connection connection;

   public SQLiteConnectionPool(String path) {
      this.connectionUrl = "jdbc:sqlite:" + path;
   }

   @Override
   public synchronized Connection getConnection() throws SQLException {
      if (this.isConnectionDead()) {
         this.connection = DriverManager.getConnection(this.connectionUrl);
      }

      return this.connection;
   }

   @Override
   public void releaseConnection(Connection connection) {
   }

   @Override
   public synchronized void shutdown() {
      if (!this.isConnectionDead()) {
         try {
            this.connection.close();
         } catch (SQLException var5) {
         } finally {
            this.connection = null;
         }
      }
   }

   private boolean isConnectionDead() {
      try {
         return this.connection == null || this.connection.isClosed() || !this.connection.isValid(1);
      } catch (SQLException var2) {
         return true;
      }
   }
}
