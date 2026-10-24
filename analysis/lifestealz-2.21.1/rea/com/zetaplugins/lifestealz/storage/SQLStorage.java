// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage;
// rea: class com.zetaplugins.lifestealz.storage.SQLStorage
class SQLStorage {
    java.lang.String CSV_SEPARATOR;

    public SQLStorage(LifeStealZ plugin) {
        super(plugin);
    }

    private boolean checkIfEntryExists(Connection connection, String uuid) {
        try {
            PreparedStatement selectStmt = connection.prepareStatement("SELECT 1 FROM hearts WHERE uuid = ?");
            try {
                selectStmt.setString(1, uuid);
                ResultSet resultSet = selectStmt.executeQuery();
                try {
                    boolean next = resultSet.next();
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (selectStmt != null) {
                        selectStmt.close();
                    }
                    return next;
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
                if (selectStmt != null) {
                    try {
                        selectStmt.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (SQLException e) {
            getPlugin().getLogger().log(Level.SEVERE, "Failed to check if player entry exists:", (Throwable) e);
            return false;
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public void clearDatabase() {
        try {
            Connection connection = getConnection();
            try {
                if (connection != null) {
                    try {
                        Statement statement = connection.createStatement();
                        try {
                            statement.executeUpdate("DELETE FROM hearts");
                            if (statement != null) {
                                statement.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (Throwable th) {
                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (SQLException e) {
                        getPlugin().getLogger().log(Level.SEVERE, "Failed to clear SQL database:", (Throwable) e);
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
            getPlugin().getLogger().log(Level.SEVERE, "Failed to clear SQL database:", (Throwable) e2);
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public String export(String fileName) {
        String filePath = getPlugin().getDataFolder().getPath() + "/" + fileName + ".csv";
        try {
            Connection connection = getConnection();
            try {
                if (connection == null) {
                    if (connection != null) {
                        connection.close();
                    }
                    return null;
                }
                try {
                    Statement statement = connection.createStatement();
                    try {
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM hearts");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                        while (resultSet.next()) {
                            try {
                                String string = resultSet.getString("uuid");
                                String string2 = resultSet.getString("name");
                                double d = resultSet.getDouble("maxhp");
                                int i = resultSet.getInt("hasbeenRevived");
                                int i2 = resultSet.getInt("craftedHearts");
                                int i3 = resultSet.getInt("craftedRevives");
                                int i4 = resultSet.getInt("killedOtherPlayers");
                                resultSet.getLong("firstJoin");
                                String line = string + "," + string2 + "," + d + "," + string + "," + i + "," + i2 + "," + i3 + "," + i4;
                                writer.write(line);
                                writer.newLine();
                            } catch (Throwable th) {
                                try {
                                    writer.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        }
                        writer.close();
                        if (statement != null) {
                            statement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                        return filePath;
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
                } catch (IOException | SQLException e) {
                    getPlugin().getLogger().log(Level.SEVERE, "Failed to export player data to CSV file:", (Throwable) e);
                    if (connection != null) {
                        connection.close();
                    }
                    return null;
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
        } catch (SQLException e2) {
            getPlugin().getLogger().log(Level.SEVERE, "Failed to export player data to CSV file:", (Throwable) e2);
            return null;
        }
    }

    public Connection getConnection() throws SQLException {
        return getConnectionPool().getConnection();
    }


    // rea: getConnectionPool(): com.zetaplugins.lifestealz.storage.connectionPool.ConnectionPool — no body (not_available; abstract_method)

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public List<String> getEliminatedPlayerNames() {
        List<String> eliminatedPlayerNames = new ArrayList<>();
        try {
            Connection connection = getConnection();
            try {
                if (connection != null) {
                    try {
                        Statement statement = connection.createStatement();
                        try {
                            statement.setQueryTimeout(30);
                            ResultSet resultSet = statement.executeQuery("SELECT name FROM hearts WHERE maxhp <= 0.0");
                            while (resultSet.next()) {
                                eliminatedPlayerNames.add(resultSet.getString("name"));
                            }
                            if (statement != null) {
                                statement.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                            return eliminatedPlayerNames;
                        } catch (Throwable th) {
                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (SQLException e) {
                        getPlugin().getLogger().log(Level.SEVERE, "Failed to get eliminated players from SQL database:", (Throwable) e);
                    }
                } else {
                    if (connection != null) {
                        connection.close();
                    }
                    return eliminatedPlayerNames;
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
            getPlugin().getLogger().log(Level.SEVERE, "Failed to get eliminated players from SQL database:", (Throwable) e2);
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public List<UUID> getEliminatedPlayers() {
        List<UUID> eliminatedPlayers = new ArrayList<>();
        int minHearts = getPlugin().getConfig().getInt("minHearts");
        try {
            Connection connection = getConnection();
            try {
                if (connection != null) {
                    try {
                        Statement statement = connection.createStatement();
                        try {
                            statement.setQueryTimeout(30);
                            ResultSet resultSet = statement.executeQuery("SELECT uuid FROM hearts WHERE maxhp <= " + (minHearts * 2) + ".0");
                            while (resultSet.next()) {
                                eliminatedPlayers.add(UUID.fromString(resultSet.getString("uuid")));
                            }
                            if (statement != null) {
                                statement.close();
                            }
                        } catch (Throwable th) {
                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (SQLException e) {
                        getPlugin().getLogger().log(Level.SEVERE, "Failed to get eliminated players from SQL database:", (Throwable) e);
                    }
                    if (connection != null) {
                        connection.close();
                    }
                    return eliminatedPlayers;
                }
                if (connection != null) {
                    connection.close();
                }
                return eliminatedPlayers;
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
            getPlugin().getLogger().log(Level.SEVERE, "Failed to get eliminated players from SQL database:", (Throwable) e2);
        }
    }


    // rea: getInserOrReplaceStatement(): java.lang.String — no body (not_available; abstract_method)

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public List<String> getPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        try {
            Connection connection = getConnection();
            try {
                if (connection != null) {
                    try {
                        Statement statement = connection.createStatement();
                        try {
                            statement.setQueryTimeout(30);
                            ResultSet resultSet = statement.executeQuery("SELECT name FROM hearts");
                            while (resultSet.next()) {
                                playerNames.add(resultSet.getString("name"));
                            }
                            if (statement != null) {
                                statement.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                            return playerNames;
                        } catch (Throwable th) {
                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (SQLException e) {
                        getPlugin().getLogger().log(Level.SEVERE, "Failed to load player names from SQL database:", (Throwable) e);
                    }
                } else {
                    if (connection != null) {
                        connection.close();
                    }
                    return playerNames;
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
            getPlugin().getLogger().log(Level.SEVERE, "Failed to load player names from SQL database:", (Throwable) e2);
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public void importData(String fileName) {
        String filePath = getPlugin().getDataFolder().getPath() + "/" + fileName;
        long startTime = System.currentTimeMillis();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            try {
                Connection connection = getConnection();
                if (connection == null) {
                    if (connection != null) {
                        connection.close();
                    }
                    reader.close();
                    return;
                }
                try {
                    connection.setAutoCommit(false);
                    String sql = getInserOrReplaceStatement();
                    try {
                        try {
                            PreparedStatement statement = connection.prepareStatement(sql);
                            int batchSize = 0;
                            int totalRows = 0;
                            while (true) {
                                try {
                                    String line = reader.readLine();
                                    if (line == null) {
                                        break;
                                    }
                                    String[] data = line.split(CSV_SEPARATOR);
                                    if (data.length != 8) {
                                        getPlugin().getLogger().severe("Invalid CSV format. Expected 8 columns, but got " + data.length);
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
                                        batchSize++;
                                        if (batchSize % TokenId.BadToken == 0) {
                                            getPlugin().getLogger().info("Imported " + totalRows + " player data entries. Committing batch...");
                                            statement.executeBatch();
                                        }
                                    }
                                } catch (Throwable th) {
                                    if (statement != null) {
                                        try {
                                            statement.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                    }
                                    throw th;
                                }
                            }
                            statement.executeBatch();
                            connection.commit();
                            long endTime = System.currentTimeMillis();
                            getPlugin().getLogger().info("Imported " + totalRows + " player data entries in " + (endTime - startTime) + "ms");
                            if (statement != null) {
                                statement.close();
                            }
                            connection.setAutoCommit(true);
                        } catch (Throwable th3) {
                            connection.setAutoCommit(true);
                            throw th3;
                        }
                    } catch (SQLException e) {
                        connection.rollback();
                        getPlugin().getLogger().log(Level.SEVERE, "Failed to import player data:", (Throwable) e);
                        connection.setAutoCommit(true);
                    }
                    if (connection != null) {
                        connection.close();
                    }
                    reader.close();
                } catch (Throwable th4) {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Throwable th5) {
                            th4.addSuppressed(th5);
                        }
                    }
                    throw th4;
                }
            } catch (Throwable th6) {
                try {
                    reader.close();
                } catch (Throwable th7) {
                    th6.addSuppressed(th7);
                }
                throw th6;
            }
        } catch (IOException | SQLException e2) {
            getPlugin().getLogger().log(Level.SEVERE, "Failed to read CSV file:", (Throwable) e2);
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public void init() {
        try {
            Connection connection = getConnection();
            try {
                if (connection != null) {
                    try {
                        Statement statement = connection.createStatement();
                        try {
                            StringBuilder sql = new StringBuilder();
                            sql.append("CREATE TABLE IF NOT EXISTS hearts (").append("uuid CHAR(36) PRIMARY KEY, ").append("name VARCHAR(64) NOT NULL, ").append("maxhp FLOAT NOT NULL DEFAULT 20.0, ").append("hasbeenRevived SMALLINT NOT NULL DEFAULT 0, ").append("craftedHearts SMALLINT UNSIGNED NOT NULL DEFAULT 0, ").append("craftedRevives SMALLINT UNSIGNED NOT NULL DEFAULT 0, ").append("killedOtherPlayers MEDIUMINT UNSIGNED NOT NULL DEFAULT 0, ").append("firstJoin BIGINT UNSIGNED NOT NULL").append(");");
                            statement.executeUpdate(sql.toString());
                            migrateDatabase();
                            if (statement != null) {
                                statement.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (Throwable th) {
                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (SQLException e) {
                        getPlugin().getLogger().log(Level.SEVERE, "Failed to initialize SQL database:", (Throwable) e);
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
            getPlugin().getLogger().log(Level.SEVERE, "Failed to initialize SQL database:", (Throwable) e2);
        }
    }

    private boolean insertPlayerData(Connection connection, PlayerData playerData) {
        try {
            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            try {
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
                if (insertStmt != null) {
                    insertStmt.close();
                }
                return true;
            } catch (Throwable th) {
                if (insertStmt != null) {
                    try {
                        insertStmt.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (SQLException e) {
            getPlugin().getLogger().log(Level.SEVERE, "Failed to insert player data:", (Throwable) e);
            return false;
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public PlayerData load(UUID uuid) {
        try {
            Connection connection = getConnection();
            try {
                if (connection == null) {
                    if (connection != null) {
                        connection.close();
                    }
                    return null;
                }
                try {
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM hearts WHERE uuid = ?");
                    try {
                        statement.setString(1, uuid.toString());
                        statement.setQueryTimeout(30);
                        try {
                            ResultSet resultSet = statement.executeQuery();
                            try {
                                if (resultSet.next()) {
                                    PlayerData playerDataMapResultSetToPlayerData = mapResultSetToPlayerData(resultSet, uuid);
                                    if (resultSet != null) {
                                        resultSet.close();
                                    }
                                    if (statement != null) {
                                        statement.close();
                                    }
                                    if (connection != null) {
                                        connection.close();
                                    }
                                    return playerDataMapResultSetToPlayerData;
                                }
                                Player player = Bukkit.getPlayer(uuid);
                                if (player == null) {
                                    if (resultSet != null) {
                                        resultSet.close();
                                    }
                                    if (statement != null) {
                                        statement.close();
                                    }
                                    if (connection != null) {
                                        connection.close();
                                    }
                                    return null;
                                }
                                PlayerData newPlayerData = new PlayerData(player.getName(), uuid);
                                save(newPlayerData);
                                if (resultSet != null) {
                                    resultSet.close();
                                }
                                if (statement != null) {
                                    statement.close();
                                }
                                if (connection != null) {
                                    connection.close();
                                }
                                return newPlayerData;
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
                        } catch (SQLException e) {
                            getPlugin().getLogger().log(Level.SEVERE, "Failed to load player data from SQL database:", (Throwable) e);
                            if (statement != null) {
                                statement.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                            return null;
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
                } catch (SQLException e2) {
                    getPlugin().getLogger().log(Level.SEVERE, "Failed to load player data from SQL database:", (Throwable) e2);
                    if (connection != null) {
                        connection.close();
                    }
                    return null;
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
        } catch (SQLException e3) {
            getPlugin().getLogger().log(Level.SEVERE, "Failed to load player data from SQL database:", (Throwable) e3);
            return null;
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public PlayerData load(String uuid) {
        return load(UUID.fromString(uuid));
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

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public int reviveAllPlayers(int minHearts, int reviveHearts, int maxRevives, boolean bypassReviveLimit) {
        int affectedPlayers = 0;
        String sql = "UPDATE hearts SET maxhp = ?, hasbeenRevived = hasbeenRevived + 1 WHERE maxhp <= ? AND (hasbeenRevived < ?)";
        if (bypassReviveLimit || maxRevives < 0) {
            sql = "UPDATE hearts SET maxhp = ?, hasbeenRevived = hasbeenRevived + 1 WHERE maxhp <= ?";
        }
        try {
            Connection connection = getConnection();
            try {
                if (connection != null) {
                    try {
                        PreparedStatement pstmt = connection.prepareStatement(sql);
                        try {
                            pstmt.setDouble(1, reviveHearts * 2);
                            pstmt.setDouble(2, minHearts * 2);
                            if (!bypassReviveLimit && maxRevives >= 0) {
                                pstmt.setInt(3, maxRevives);
                            }
                            affectedPlayers = pstmt.executeUpdate();
                            if (pstmt != null) {
                                pstmt.close();
                            }
                        } catch (Throwable th) {
                            if (pstmt != null) {
                                try {
                                    pstmt.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (SQLException e) {
                        getPlugin().getLogger().log(Level.SEVERE, "Failed to revive all players in SQL database:", (Throwable) e);
                    }
                    if (connection != null) {
                        connection.close();
                    }
                    return affectedPlayers;
                }
                if (connection != null) {
                    connection.close();
                }
                return 0;
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
            getPlugin().getLogger().log(Level.SEVERE, "Failed to revive all players in SQL database:", (Throwable) e2);
        }
    }

    @Override // com.zetaplugins.lifestealz.storage.Storage
    public void save(PlayerData playerData) {
        if (playerData.hasChanges()) {
            try {
                Connection connection = getConnection();
                if (connection != null) {
                    try {
                        boolean exists = checkIfEntryExists(connection, playerData.getUuid());
                        if (exists) {
                            updatePlayerData(connection, playerData);
                        } else {
                            insertPlayerData(connection, playerData);
                        }
                        if (connection != null) {
                            connection.close();
                        }
                        return;
                    } catch (Throwable th) {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                getPlugin().getLogger().log(Level.SEVERE, "Failed to save player data:", (Throwable) e);
            }
        }
    }

    private boolean updatePlayerData(Connection connection, PlayerData playerData) {
        StringBuilder updateQuery = new StringBuilder("UPDATE hearts SET ");
        List<Object> params = new ArrayList<>();
        for (String field : playerData.getModifiedFields()) {
            updateQuery.append(field).append(" = ?, ");
            switch (field) {
                case "maxhp":
                    params.add(Double.valueOf(playerData.getMaxHealth()));
                    break;
                case "hasbeenRevived":
                    params.add(Integer.valueOf(playerData.getHasBeenRevived()));
                    break;
                case "craftedHearts":
                    params.add(Integer.valueOf(playerData.getCraftedHearts()));
                    break;
                case "craftedRevives":
                    params.add(Integer.valueOf(playerData.getCraftedRevives()));
                    break;
                case "killedOtherPlayers":
                    params.add(Integer.valueOf(playerData.getKilledOtherPlayers()));
                    break;
                case "firstJoin":
                    params.add(Long.valueOf(playerData.getFirstJoin()));
                    break;
            }
        }
        updateQuery.setLength(updateQuery.length() - 2);
        updateQuery.append(" WHERE uuid = ?");
        params.add(playerData.getUuid());
        try {
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery.toString());
            for (int i = 0; i < params.size(); i++) {
                try {
                    updateStmt.setObject(i + 1, params.get(i));
                } catch (Throwable th) {
                    if (updateStmt != null) {
                        try {
                            updateStmt.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            updateStmt.executeUpdate();
            playerData.clearModifiedFields();
            if (updateStmt != null) {
                updateStmt.close();
            }
            return true;
        } catch (SQLException e) {
            getPlugin().getLogger().log(Level.SEVERE, "Failed to update player data:", (Throwable) e);
            return false;
        }
    }

}
