// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.geysermc;
// rea: class com.zetaplugins.lifestealz.util.geysermc.GeyserPlayerFile
class GeyserPlayerFile {
    org.bukkit.configuration.file.FileConfiguration geyserPlayerConfig;
    java.io.File geyserPlayerFile;
    org.bukkit.plugin.java.JavaPlugin plugin;

    public GeyserPlayerFile() {
        createGeyserPlayerFile();
    }

    private void createGeyserPlayerFile() {
        this.geyserPlayerFile = new File(this.plugin.getDataFolder(), "geyser_players.yml");
        if (!this.geyserPlayerFile.exists()) {
            this.geyserPlayerFile.getParentFile().mkdirs();
            this.plugin.saveResource("geyser_players.yml", false);
        }
        this.geyserPlayerConfig = YamlConfiguration.loadConfiguration(this.geyserPlayerFile);
    }

    public String getPlayerName(UUID uuid) {
        return this.geyserPlayerConfig.getString("players." + uuid.toString() + ".name");
    }

    public UUID getPlayerUUID(String name) {
        ConfigurationSection playersSection;
        if (name == null || (playersSection = this.geyserPlayerConfig.getConfigurationSection("players")) == null) {
            return null;
        }
        Set<String> keys = playersSection.getKeys(false);
        for (String uuidString : keys) {
            String storedName = this.geyserPlayerConfig.getString("players." + uuidString + ".name");
            if (storedName != null && storedName.equalsIgnoreCase(name)) {
                return UUID.fromString(uuidString);
            }
        }
        return null;
    }

    public boolean isPlayerStored(UUID uuid) {
        return this.geyserPlayerConfig.contains("players." + uuid.toString());
    }

    public boolean isPlayerStored(String name) {
        return getPlayerUUID(name) != null;
    }

    private void saveConfig() {
        try {
            this.geyserPlayerConfig.save(this.geyserPlayerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(UUID uuid, String name) {
        this.geyserPlayerConfig.set("players." + uuid.toString() + ".name", name);
        saveConfig();
    }

}
