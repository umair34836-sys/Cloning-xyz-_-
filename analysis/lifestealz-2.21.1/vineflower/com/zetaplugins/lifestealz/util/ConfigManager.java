package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.addon.LifeStealZAddon;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class ConfigManager {
   private final LifeStealZ plugin;
   private final Map<String, FileConfiguration> addonConfigs = new HashMap<>();

   public ConfigManager(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public FileConfiguration getStorageConfig() {
      return this.getCustomConfig("storage");
   }

   public FileConfiguration getCustomItemConfig() {
      return this.getCustomConfig("items");
   }

   public FileConfiguration getCustomConfig(String fileName) {
      File configFile = new File(this.plugin.getDataFolder(), fileName + ".yml");
      if (!configFile.exists()) {
         configFile.getParentFile().mkdirs();
         this.plugin.saveResource(fileName + ".yml", false);
      }

      return YamlConfiguration.loadConfiguration(configFile);
   }

   public FileConfiguration getAddonConfig(LifeStealZAddon addon) {
      return this.getAddonConfig(addon.getMetadata().getConfigName(), (Class<? extends LifeStealZAddon>)addon.getClass());
   }

   public Map<String, String> getConfigsMap() {
      Map<String, String> configs = new HashMap<>();
      configs.put("config.yml", this.plugin.getConfig().saveToString());
      configs.put("storage.yml", this.getStorageConfig().saveToString());
      configs.put("items.yml", this.getCustomItemConfig().saveToString());
      return configs;
   }

   public FileConfiguration getAddonConfig(String configName, Class<? extends LifeStealZAddon> addonClass) {
      if (this.addonConfigs.containsKey(configName)) {
         return this.addonConfigs.get(configName);
      } else {
         File addonsDir = new File(this.plugin.getDataFolder(), "addons");
         if (!addonsDir.exists()) {
            addonsDir.mkdirs();
         }

         File configFile = new File(addonsDir, configName + ".yml");
         if (!configFile.exists()) {
            try {
               InputStream defaultConfigStream = addonClass.getResourceAsStream("/config.yml");
               if (defaultConfigStream != null) {
                  YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
                  defaultConfig.save(configFile);
               } else {
                  configFile.createNewFile();
               }
            } catch (Exception var7) {
               this.plugin.getLogger().warning("Could not create config for addon: " + configName);
               var7.printStackTrace();
            }
         }

         FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
         this.addonConfigs.put(configName, config);
         return config;
      }
   }

   public void reloadAddonConfig(String configName) {
      this.addonConfigs.remove(configName);
   }

   public void saveAddonConfig(String configName) {
      FileConfiguration config = this.addonConfigs.get(configName);
      if (config != null) {
         try {
            File configFile = new File(new File(this.plugin.getDataFolder(), "addons"), configName + ".yml");
            config.save(configFile);
         } catch (Exception var4) {
            this.plugin.getLogger().severe("Could not save addon config: " + configName);
            var4.printStackTrace();
         }
      }
   }
}
