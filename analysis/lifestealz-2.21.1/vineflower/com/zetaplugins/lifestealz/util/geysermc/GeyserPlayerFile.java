package com.zetaplugins.lifestealz.util.geysermc;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class GeyserPlayerFile {
   private final JavaPlugin plugin = LifeStealZ.getInstance();
   private File geyserPlayerFile;
   private FileConfiguration geyserPlayerConfig;

   public GeyserPlayerFile() {
      this.createGeyserPlayerFile();
   }

   private void createGeyserPlayerFile() {
      this.geyserPlayerFile = new File(this.plugin.getDataFolder(), "geyser_players.yml");
      if (!this.geyserPlayerFile.exists()) {
         this.geyserPlayerFile.getParentFile().mkdirs();
         this.plugin.saveResource("geyser_players.yml", false);
      }

      this.geyserPlayerConfig = YamlConfiguration.loadConfiguration(this.geyserPlayerFile);
   }

   public void savePlayer(UUID uuid, String name) {
      this.geyserPlayerConfig.set("players." + uuid.toString() + ".name", name);
      this.saveConfig();
   }

   public String getPlayerName(UUID uuid) {
      return this.geyserPlayerConfig.getString("players." + uuid.toString() + ".name");
   }

   public UUID getPlayerUUID(String name) {
      if (name == null) {
         return null;
      } else {
         ConfigurationSection playersSection = this.geyserPlayerConfig.getConfigurationSection("players");
         if (playersSection == null) {
            return null;
         } else {
            for (String uuidString : playersSection.getKeys(false)) {
               String storedName = this.geyserPlayerConfig.getString("players." + uuidString + ".name");
               if (storedName != null && storedName.equalsIgnoreCase(name)) {
                  return UUID.fromString(uuidString);
               }
            }

            return null;
         }
      }
   }

   private void saveConfig() {
      try {
         this.geyserPlayerConfig.save(this.geyserPlayerFile);
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   public boolean isPlayerStored(UUID uuid) {
      return this.geyserPlayerConfig.contains("players." + uuid.toString());
   }

   public boolean isPlayerStored(String name) {
      return this.getPlayerUUID(name) != null;
   }
}
