package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.io.File;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class LanguageManager {
   private final JavaPlugin plugin;
   public static final List<String> defaultLangs = List.of(
      "cs-CZ", "de-DE", "en-US", "es-ES", "fr-FR", "pl-PL", "vi-VN", "zh-CN", "nl-NL", "ru-RU", "pt-BR", "ja-JP", "hu-HU", "it-IT", "tr-TR"
   );
   private FileConfiguration langConfig;

   public LanguageManager(LifeStealZ plugin) {
      this.plugin = plugin;
      this.loadLanguageConfig();
   }

   public void reload() {
      this.loadLanguageConfig();
   }

   private void loadLanguageConfig() {
      File languageDirectory = new File(this.plugin.getDataFolder(), "lang/");
      if (!languageDirectory.exists() || !languageDirectory.isDirectory()) {
         languageDirectory.mkdir();
      }

      for (String langString : defaultLangs) {
         File langFile = new File("lang/", langString + ".yml");
         if (!new File(languageDirectory, langString + ".yml").exists()) {
            this.plugin.getLogger().info("Saving file " + langFile.getPath());
            this.plugin.saveResource(langFile.getPath(), false);
         }
      }

      String langOption = this.plugin.getConfig().getString("lang") != null ? this.plugin.getConfig().getString("lang") : "en-US";
      File selectedLangFile = new File(languageDirectory, langOption + ".yml");
      if (!selectedLangFile.exists()) {
         selectedLangFile = new File(languageDirectory, "en-US.yml");
         this.plugin.getLogger().warning("Language file " + langOption + ".yml (" + selectedLangFile.getPath() + ") not found! Using fallback en-US.yml.");
      }

      this.plugin.getLogger().info("Using language file: " + selectedLangFile.getPath());
      this.langConfig = YamlConfiguration.loadConfiguration(selectedLangFile);
   }

   public String getString(String key) {
      return this.langConfig.getString(key);
   }

   public String getString(String key, String fallback) {
      return this.langConfig.getString(key) != null ? this.langConfig.getString(key) : fallback;
   }
}
