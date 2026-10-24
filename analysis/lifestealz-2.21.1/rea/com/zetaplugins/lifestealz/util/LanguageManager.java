// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.LanguageManager
class LanguageManager {
    java.util.List<java.lang.String> defaultLangs;
    org.bukkit.configuration.file.FileConfiguration langConfig;
    org.bukkit.plugin.java.JavaPlugin plugin;

    public LanguageManager(LifeStealZ plugin) {
        this.plugin = plugin;
        loadLanguageConfig();
    }

    public String getString(String key) {
        return this.langConfig.getString(key);
    }

    public String getString(String key, String fallback) {
        return this.langConfig.getString(key) != null ? this.langConfig.getString(key) : fallback;
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

    // rea: m32clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 4
    //     .max locals 0
    // 
    //     .line 13
    //     bipush 15
    //     anewarray java/lang/String
    //     dup
    //     iconst_0
    //     ldc "cs-CZ"
    //     aastore
    //     dup
    //     iconst_1
    //     ldc "de-DE"
    //     aastore
    //     dup
    //     iconst_2
    //     ldc "en-US"
    //     aastore
    //     dup
    //     iconst_3
    //     ldc "es-ES"
    //     aastore
    //     dup
    //     iconst_4
    //     ldc "fr-FR"
    //     aastore
    //     dup
    //     iconst_5
    //     ldc "pl-PL"
    //     aastore
    //     dup
    //     bipush 6
    //     ldc "vi-VN"
    //     aastore
    //     dup
    //     bipush 7
    //     ldc "zh-CN"
    //     aastore
    //     dup
    //     bipush 8
    //     ldc "nl-NL"
    //     aastore
    //     dup
    //     bipush 9
    //     ldc "ru-RU"
    //     aastore
    //     dup
    //     bipush 10
    //     ldc "pt-BR"
    //     aastore
    //     dup
    //     bipush 11
    //     ldc "ja-JP"
    //     aastore
    //     dup
    //     bipush 12
    //     ldc "hu-HU"
    //     aastore
    //     dup
    //     bipush 13
    //     ldc "it-IT"
    //     aastore
    //     dup
    //     bipush 14
    //     ldc "tr-TR"
    //     aastore
    //     invokestatic interface java/util/List of ([Ljava/lang/Object;)Ljava/util/List;
    //     putstatic com/zetaplugins/lifestealz/util/LanguageManager defaultLangs Ljava/util/List;
    //     return
    // .end method

    public void reload() {
        loadLanguageConfig();
    }

}
