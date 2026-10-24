package com.firecraft.firesteal.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SQLite-backed storage. All access via PreparedStatement (no string concat => no SQL
 * injection). Writes run on a single-thread executor (never on the main thread); reads at
 * startup are cached in HeartService. Original implementation.
 */
public final class PlayerStore {

    private final ExecutorService io = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "FireSteal-DB");
        t.setDaemon(true);
        return t;
    });
    private Connection conn;

    public void open(File dbFile) throws SQLException {
        // Shaded org.sqlite driver (relocated at build time).
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        try (PreparedStatement a = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS players (" +
                "uuid TEXT PRIMARY KEY, hearts INTEGER NOT NULL, eliminated INTEGER NOT NULL DEFAULT 0, " +
                "last_killed_by TEXT, updated_at INTEGER NOT NULL)");
             PreparedStatement b = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS used_tokens (token TEXT PRIMARY KEY, redeemed_at INTEGER NOT NULL)")) {
            a.executeUpdate();
            b.executeUpdate();
        }
    }

    public record Record(int hearts, boolean eliminated, String lastKilledBy) {}

    /** Synchronous load (call once, async, at join). */
    public Record load(UUID uuid) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT hearts, eliminated, last_killed_by FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Record(rs.getInt(1), rs.getInt(2) != 0, rs.getString(3));
                }
            }
        }
        return null;
    }

    public CompletableFuture<Void> save(UUID uuid, int hearts, boolean eliminated, String lastKilledBy) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO players (uuid, hearts, eliminated, last_killed_by, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?) ON CONFLICT(uuid) DO UPDATE SET " +
                    "hearts = excluded.hearts, eliminated = excluded.eliminated, " +
                    "last_killed_by = excluded.last_killed_by, updated_at = excluded.updated_at")) {
                ps.setString(1, uuid.toString());
                ps.setInt(2, hearts);
                ps.setInt(3, eliminated ? 1 : 0);
                ps.setString(4, lastKilledBy);
                ps.setLong(5, System.currentTimeMillis());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, io);
    }

    /** Atomically claim a heart-item token; returns true only the first time (anti-dupe). */
    public boolean redeemToken(String token) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO used_tokens (token, redeemed_at) VALUES (?, ?)")) {
            ps.setString(1, token);
            ps.setLong(2, System.currentTimeMillis());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // PRIMARY KEY conflict => already redeemed => duplicate/forged.
            return false;
        }
    }

    public void close() {
        io.shutdown();
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
