package com.zetaplugins.lifestealz.storage.connectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {
   Connection getConnection() throws SQLException;

   void releaseConnection(Connection var1) throws SQLException;

   void shutdown() throws SQLException;
}
