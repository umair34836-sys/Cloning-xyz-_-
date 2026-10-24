package com.zetaplugins.lifestealz.storage;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class SQLStorage extends Storage {
   private static final String CSV_SEPARATOR = ",";

   public SQLStorage(LifeStealZ plugin) {
      super(plugin);
   }

   @Override
   public void init() {
      try {
         try (Connection connection = this.getConnection()) {
            if (connection != null) {
               try (Statement statement = connection.createStatement()) {
                  StringBuilder sql = new StringBuilder();
                  sql.append("CREATE TABLE IF NOT EXISTS hearts (")
                     .append("uuid CHAR(36) PRIMARY KEY, ")
                     .append("name VARCHAR(64) NOT NULL, ")
                     .append("maxhp FLOAT NOT NULL DEFAULT 20.0, ")
                     .append("hasbeenRevived SMALLINT NOT NULL DEFAULT 0, ")
                     .append("craftedHearts SMALLINT UNSIGNED NOT NULL DEFAULT 0, ")
                     .append("craftedRevives SMALLINT UNSIGNED NOT NULL DEFAULT 0, ")
                     .append("killedOtherPlayers MEDIUMINT UNSIGNED NOT NULL DEFAULT 0, ")
                     .append("firstJoin BIGINT UNSIGNED NOT NULL")
                     .append(");");
                  statement.executeUpdate(sql.toString());
                  this.migrateDatabase();
               } catch (SQLException var8) {
                  this.getPlugin().getLogger().log(Level.SEVERE, "Failed to initialize SQL database:", (Throwable)var8);
               }

               return;
            }
         }
      } catch (SQLException var10) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to initialize SQL database:", (Throwable)var10);
      }
   }

   public abstract ConnectionPool getConnectionPool();

   public Connection getConnection() throws SQLException {
      return this.getConnectionPool().getConnection();
   }

   @Override
   public PlayerData load(UUID uuid) {
      String sql = "SELECT * FROM hearts WHERE uuid = ?";

      try {
         PlayerData var20;
         try (Connection connection = this.getConnection()) {
            if (connection == null) {
               return null;
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM hearts WHERE uuid = ?")) {
               statement.setString(1, uuid.toString());
               statement.setQueryTimeout(30);

               try (ResultSet resultSet = statement.executeQuery()) {
                  if (!resultSet.next()) {
                     Player player = Bukkit.getPlayer(uuid);
                     if (player == null) {
                        return null;
                     }

                     PlayerData newPlayerData = new PlayerData(player.getName(), uuid);
                     this.save(newPlayerData);
                     return newPlayerData;
                  }

                  var20 = this.mapResultSetToPlayerData(resultSet, uuid);
               } catch (SQLException var13) {
                  this.getPlugin().getLogger().log(Level.SEVERE, "Failed to load player data from SQL database:", (Throwable)var13);
                  return null;
               }
            } catch (SQLException var15) {
               this.getPlugin().getLogger().log(Level.SEVERE, "Failed to load player data from SQL database:", (Throwable)var15);
               return null;
            }
         }

         return var20;
      } catch (SQLException var17) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to load player data from SQL database:", (Throwable)var17);
         return null;
      }
   }

   private PlayerData mapResultSetToPlayerData(ResultSet resultSet, UUID uuid) throws SQLException {
      PlayerData playerData = new PlayerData(resultSet.getString("name"), uuid);
      playerData.setMaxHealth(resultSet.getDouble("maxhp"));
      playerData.setHasBeenRevived(resultSet.getInt("hasbeenRevived"));
      playerData.setCraftedHearts(resultSet.getInt("craftedHearts"));
      playerData.setCraftedRevives(resultSet.getInt("craftedRevives"));
      playerData.setKilledOtherPlayers(resultSet.getInt("killedOtherPlayers"));
      playerData.setFirstJoin(resultSet.getLong("firstJoin"));
      playerData.clearModifiedFields();
      return playerData;
   }

   @Override
   public void save(PlayerData playerData) {
      if (playerData.hasChanges()) {
         try {
            try (Connection connection = this.getConnection()) {
               if (connection != null) {
                  boolean exists = this.checkIfEntryExists(connection, playerData.getUuid());
                  if (exists) {
                     this.updatePlayerData(connection, playerData);
                  } else {
                     this.insertPlayerData(connection, playerData);
                  }

                  return;
               }
            }
         } catch (SQLException var7) {
            this.getPlugin().getLogger().log(Level.SEVERE, "Failed to save player data:", (Throwable)var7);
         }
      }
   }

   private boolean checkIfEntryExists(Connection connection, String uuid) {
      String selectQuery = "SELECT 1 FROM hearts WHERE uuid = ?";

      try {
         boolean var6;
         try (PreparedStatement selectStmt = connection.prepareStatement("SELECT 1 FROM hearts WHERE uuid = ?")) {
            selectStmt.setString(1, uuid);

            try (ResultSet resultSet = selectStmt.executeQuery()) {
               var6 = resultSet.next();
            }
         }

         return var6;
      } catch (SQLException var12) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to check if player entry exists:", (Throwable)var12);
         return false;
      }
   }

   private boolean insertPlayerData(Connection connection, PlayerData playerData) {
      String insertQuery = "INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

      try {
         boolean var5;
         try (PreparedStatement insertStmt = connection.prepareStatement(
               "INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            )) {
            insertStmt.setString(1, playerData.getUuid());
            insertStmt.setString(2, playerData.getName());
            insertStmt.setDouble(3, playerData.getMaxHealth());
            insertStmt.setInt(4, playerData.getHasBeenRevived());
            insertStmt.setInt(5, playerData.getCraftedHearts());
            insertStmt.setInt(6, playerData.getCraftedRevives());
            insertStmt.setInt(7, playerData.getKilledOtherPlayers());
            insertStmt.setLong(8, playerData.getFirstJoin());
            insertStmt.executeUpdate();
            playerData.clearModifiedFields();
            var5 = true;
         }

         return var5;
      } catch (SQLException var9) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to insert player data:", (Throwable)var9);
         return false;
      }
   }

   private boolean updatePlayerData(Connection connection, PlayerData playerData) {
      StringBuilder updateQuery = new StringBuilder("UPDATE hearts SET ");
      List<Object> params = new ArrayList<>();

      for (String field : playerData.getModifiedFields()) {
         updateQuery.append(field).append(" = ?, ");
         switch (field) {
            case "maxhp":
               params.add(playerData.getMaxHealth());
               break;
            case "hasbeenRevived":
               params.add(playerData.getHasBeenRevived());
               break;
            case "craftedHearts":
               params.add(playerData.getCraftedHearts());
               break;
            case "craftedRevives":
               params.add(playerData.getCraftedRevives());
               break;
            case "killedOtherPlayers":
               params.add(playerData.getKilledOtherPlayers());
               break;
            case "firstJoin":
               params.add(playerData.getFirstJoin());
         }
      }

      updateQuery.setLength(updateQuery.length() - 2);
      updateQuery.append(" WHERE uuid = ?");
      params.add(playerData.getUuid());

      try {
         boolean var14;
         try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery.toString())) {
            for (int i = 0; i < params.size(); i++) {
               updateStmt.setObject(i + 1, params.get(i));
            }

            updateStmt.executeUpdate();
            playerData.clearModifiedFields();
            var14 = true;
         }

         return var14;
      } catch (SQLException var11) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to update player data:", (Throwable)var11);
         return false;
      }
   }

   @Override
   public PlayerData load(String uuid) {
      return this.load(UUID.fromString(uuid));
   }

   @Override
   public List<UUID> getEliminatedPlayers() {
      List<UUID> eliminatedPlayers = new ArrayList<>();
      int minHearts = this.getPlugin().getConfig().getInt("minHearts");

      try {
         Object var13;
         try (Connection connection = this.getConnection()) {
            if (connection != null) {
               try (Statement statement = connection.createStatement()) {
                  statement.setQueryTimeout(30);
                  ResultSet resultSet = statement.executeQuery("SELECT uuid FROM hearts WHERE maxhp <= " + minHearts * 2 + ".0");

                  while (resultSet.next()) {
                     eliminatedPlayers.add(UUID.fromString(resultSet.getString("uuid")));
                  }
               } catch (SQLException var10) {
                  this.getPlugin().getLogger().log(Level.SEVERE, "Failed to get eliminated players from SQL database:", (Throwable)var10);
               }

               return eliminatedPlayers;
            }

            var13 = eliminatedPlayers;
         }

         return (List<UUID>)var13;
      } catch (SQLException var12) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to get eliminated players from SQL database:", (Throwable)var12);
         return eliminatedPlayers;
      }
   }

   @Override
   public String export(String fileName) {
      String filePath = this.getPlugin().getDataFolder().getPath() + "/" + fileName + ".csv";

      try {
         try (Connection connection = this.getConnection()) {
            if (connection == null) {
               return null;
            }

            try (Statement statement = connection.createStatement()) {
               ResultSet resultSet = statement.executeQuery("SELECT * FROM hearts");

               try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                  while (resultSet.next()) {
                     String line = resultSet.getString("uuid")
                        + ","
                        + resultSet.getString("name")
                        + ","
                        + resultSet.getDouble("maxhp")
                        + ","
                        + resultSet.getInt("hasbeenRevived")
                        + ","
                        + resultSet.getInt("craftedHearts")
                        + ","
                        + resultSet.getInt("craftedRevives")
                        + ","
                        + resultSet.getInt("killedOtherPlayers")
                        + ","
                        + resultSet.getLong("firstJoin");
                     writer.write(line);
                     writer.newLine();
                  }
               }
            } catch (IOException | SQLException var14) {
               this.getPlugin().getLogger().log(Level.SEVERE, "Failed to export player data to CSV file:", (Throwable)var14);
               return null;
            }
         }

         return filePath;
      } catch (SQLException var16) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to export player data to CSV file:", (Throwable)var16);
         return null;
      }
   }

   protected abstract String getInserOrReplaceStatement();

   @Override
   public void importData(String fileName) {
      String filePath = this.getPlugin().getDataFolder().getPath() + "/" + fileName;
      long startTime = System.currentTimeMillis();

      try {
         try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            Connection connection = this.getConnection();
         ) {
            if (connection != null) {
               connection.setAutoCommit(false);
               String sql = this.getInserOrReplaceStatement();

               try {
                  try (PreparedStatement statement = connection.prepareStatement(sql)) {
                     int batchSize = 0;
                     int totalRows = 0;

                     String line;
                     while ((line = reader.readLine()) != null) {
                        String[] data = line.split(",");
                        if (data.length != 8) {
                           this.getPlugin().getLogger().severe("Invalid CSV format. Expected 8 columns, but got " + data.length);
                        } else {
                           totalRows++;
                           statement.setString(1, data[0]);
                           statement.setString(2, data[1]);
                           statement.setDouble(3, Double.parseDouble(data[2]));
                           statement.setInt(4, Integer.parseInt(data[3]));
                           statement.setInt(5, Integer.parseInt(data[4]));
                           statement.setInt(6, Integer.parseInt(data[5]));
                           statement.setInt(7, Integer.parseInt(data[6]));
                           statement.setLong(8, Long.parseLong(data[7]));
                           statement.addBatch();
                           if (++batchSize % 500 == 0) {
                              this.getPlugin().getLogger().info("Imported " + totalRows + " player data entries. Committing batch...");
                              statement.executeBatch();
                           }
                        }
                     }

                     statement.executeBatch();
                     connection.commit();
                     long endTime = System.currentTimeMillis();
                     this.getPlugin().getLogger().info("Imported " + totalRows + " player data entries in " + (endTime - startTime) + "ms");
                  } catch (SQLException var28) {
                     connection.rollback();
                     this.getPlugin().getLogger().log(Level.SEVERE, "Failed to import player data:", (Throwable)var28);
                  }

                  return;
               } finally {
                  connection.setAutoCommit(true);
               }
            }
         }
      } catch (SQLException | IOException var32) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to read CSV file:", (Throwable)var32);
      }
   }

   @Override
   public int reviveAllPlayers(int minHearts, int reviveHearts, int maxRevives, boolean bypassReviveLimit) {
      int affectedPlayers = 0;
      String sql = "UPDATE hearts SET maxhp = ?, hasbeenRevived = hasbeenRevived + 1 WHERE maxhp <= ? AND (hasbeenRevived < ?)";
      if (bypassReviveLimit || maxRevives < 0) {
         sql = "UPDATE hearts SET maxhp = ?, hasbeenRevived = hasbeenRevived + 1 WHERE maxhp <= ?";
      }

      try {
         int var17;
         try (Connection connection = this.getConnection()) {
            if (connection != null) {
               try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                  pstmt.setDouble(1, (double)(reviveHearts * 2));
                  pstmt.setDouble(2, (double)(minHearts * 2));
                  if (!bypassReviveLimit && maxRevives >= 0) {
                     pstmt.setInt(3, maxRevives);
                  }

                  affectedPlayers = pstmt.executeUpdate();
               } catch (SQLException var14) {
                  this.getPlugin().getLogger().log(Level.SEVERE, "Failed to revive all players in SQL database:", (Throwable)var14);
               }

               return affectedPlayers;
            }

            var17 = affectedPlayers;
         }

         return var17;
      } catch (SQLException var16) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to revive all players in SQL database:", (Throwable)var16);
         return affectedPlayers;
      }
   }

   @Override
   public List<String> getPlayerNames() {
      List<String> playerNames = new ArrayList<>();

      try {
         Object var12;
         try (Connection connection = this.getConnection()) {
            if (connection != null) {
               try (Statement statement = connection.createStatement()) {
                  statement.setQueryTimeout(30);
                  ResultSet resultSet = statement.executeQuery("SELECT name FROM hearts");

                  while (resultSet.next()) {
                     playerNames.add(resultSet.getString("name"));
                  }
               } catch (SQLException var9) {
                  this.getPlugin().getLogger().log(Level.SEVERE, "Failed to load player names from SQL database:", (Throwable)var9);
               }

               return playerNames;
            }

            var12 = playerNames;
         }

         return (List<String>)var12;
      } catch (SQLException var11) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to load player names from SQL database:", (Throwable)var11);
         return playerNames;
      }
   }

   @Override
   public List<String> getEliminatedPlayerNames() {
      List<String> eliminatedPlayerNames = new ArrayList<>();

      try {
         Object var12;
         try (Connection connection = this.getConnection()) {
            if (connection != null) {
               try (Statement statement = connection.createStatement()) {
                  statement.setQueryTimeout(30);
                  ResultSet resultSet = statement.executeQuery("SELECT name FROM hearts WHERE maxhp <= 0.0");

                  while (resultSet.next()) {
                     eliminatedPlayerNames.add(resultSet.getString("name"));
                  }
               } catch (SQLException var9) {
                  this.getPlugin().getLogger().log(Level.SEVERE, "Failed to get eliminated players from SQL database:", (Throwable)var9);
               }

               return eliminatedPlayerNames;
            }

            var12 = eliminatedPlayerNames;
         }

         return (List<String>)var12;
      } catch (SQLException var11) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to get eliminated players from SQL database:", (Throwable)var11);
         return eliminatedPlayerNames;
      }
   }

   @Override
   public void clearDatabase() {
      try {
         try (Connection connection = this.getConnection()) {
            if (connection != null) {
               try (Statement statement = connection.createStatement()) {
                  statement.executeUpdate("DELETE FROM hearts");
               } catch (SQLException var8) {
                  this.getPlugin().getLogger().log(Level.SEVERE, "Failed to clear SQL database:", (Throwable)var8);
               }

               return;
            }
         }
      } catch (SQLException var10) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Failed to clear SQL database:", (Throwable)var10);
      }
   }
}
