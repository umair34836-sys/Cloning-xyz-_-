package com.zetaplugins.lifestealz.storage;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool;
import com.zetaplugins.lifestealz.storage.connectionPool.MariaDBConnectionPool;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;

public final class MariaDBStorage extends MySQLSyntaxStorage {
   private final MariaDBConnectionPool connectionPool;

   public MariaDBStorage(LifeStealZ plugin) {
      super(plugin);
      FileConfiguration config = this.getPlugin().getConfigManager().getStorageConfig();
      String HOST = config.getString("host");
      String PORT = config.getString("port");
      String DATABASE = config.getString("database");
      String USERNAME = config.getString("username");
      String PASSWORD = config.getString("password");
      this.connectionPool = new MariaDBConnectionPool(HOST, PORT, DATABASE, USERNAME, PASSWORD);
   }

   @Override
   public ConnectionPool getConnectionPool() {
      return this.connectionPool;
   }

   @Override
   protected void migrateDatabase() {
      try (
         Connection connection = this.getConnection();
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'hearts' AND COLUMN_NAME = 'firstJoin'"
         );
      ) {
         if (!resultSet.next()) {
            this.getPlugin().getLogger().info("Adding 'firstJoin' column to 'hearts' table.");
            statement.executeUpdate("ALTER TABLE hearts ADD COLUMN firstJoin INTEGER DEFAULT 0");
         }
      } catch (SQLException var12) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to migrate database: ", (Throwable)var12);
      }
   }
}
