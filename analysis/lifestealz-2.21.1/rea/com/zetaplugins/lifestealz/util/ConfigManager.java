// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.ConfigManager
class ConfigManager {
    java.util.Map<java.lang.String, org.bukkit.configuration.file.FileConfiguration> addonConfigs;
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public ConfigManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FileConfiguration getAddonConfig(LifeStealZAddon addon) {
        return getAddonConfig(addon.getMetadata().getConfigName(), addon.getClass());
    }

    public FileConfiguration getAddonConfig(String configName, Class<? extends LifeStealZAddon> addonClass) {
        if (this.addonConfigs.containsKey(configName)) {
            return this.addonConfigs.get(configName);
        }
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
            } catch (Exception e) {
                this.plugin.getLogger().warning("Could not create config for addon: " + configName);
                e.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        this.addonConfigs.put(configName, config);
        return config;
    }

    public Map<String, String> getConfigsMap() {
        Map<String, String> configs = new HashMap<>();
        configs.put("config.yml", this.plugin.getConfig().saveToString());
        configs.put("storage.yml", getStorageConfig().saveToString());
        configs.put("items.yml", getCustomItemConfig().saveToString());
        return configs;
    }

    public FileConfiguration getCustomConfig(String fileName) {
        File configFile = new File(this.plugin.getDataFolder(), fileName + ".yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.plugin.saveResource(fileName + ".yml", false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getCustomItemConfig() {
        return getCustomConfig("items");
    }

    public FileConfiguration getStorageConfig() {
        return getCustomConfig("storage");
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
            } catch (Exception e) {
                this.plugin.getLogger().severe("Could not save addon config: " + configName);
                e.printStackTrace();
            }
        }
    }

}
