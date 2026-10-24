// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz;
// rea: class com.zetaplugins.lifestealz.LifeStealZ
class LifeStealZ {
    com.zetaplugins.lifestealz.libs.faststats.core.ErrorTracker FASTSTATS_ERROR_TRACKER;
    java.lang.String FASTSTATS_TOKEN;
    java.lang.String PACKAGE_PREFIX;
    com.zetaplugins.lifestealz.util.AsyncTaskManager asyncTaskManager;
    com.zetaplugins.lifestealz.util.BypassManager bypassManager;
    com.zetaplugins.lifestealz.util.ConfigManager configManager;
    com.zetaplugins.lifestealz.caches.EliminatedPlayersCache eliminatedPlayersCache;
    com.zetaplugins.lifestealz.util.geysermc.GeyserManager geyserManager;
    com.zetaplugins.lifestealz.util.geysermc.GeyserPlayerFile geyserPlayerFile;
    com.zetaplugins.lifestealz.util.GracePeriodManager gracePeriodManager;
    boolean hasGeyser;
    boolean hasPlaceholderApi;
    boolean hasWorldGuard;
    com.zetaplugins.lifestealz.util.LanguageManager languageManager;
    com.zetaplugins.lifestealz.libs.faststats.core.Metrics metrics;
    com.zetaplugins.lifestealz.caches.OfflinePlayerCache offlinePlayerCache;
    com.zetaplugins.lifestealz.util.customitems.recipe.RecipeManager recipeManager;
    com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager reviveBeaconEffectManager;
    com.zetaplugins.lifestealz.util.revive.ReviveTaskManager reviveTaskManager;
    com.zetaplugins.lifestealz.storage.Storage storage;
    com.zetaplugins.lifestealz.util.VersionChecker versionChecker;
    com.zetaplugins.lifestealz.util.WebHookManager webHookManager;
    com.zetaplugins.lifestealz.util.worldguard.WorldGuardManager worldGuardManager;

    public LifeStealZ() {
        this.hasWorldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
        this.hasPlaceholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        this.hasGeyser = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        this.metrics = BukkitMetrics.factory().token(FASTSTATS_TOKEN).errorTracker(FASTSTATS_ERROR_TRACKER).create((Plugin) this);
    }

    private Storage createBackingStorage() {
        switch (getConfigManager().getStorageConfig().getString("type").toLowerCase()) {
            case "mysql":
                getLogger().info("Using MySQL storage");
                return new MySQLStorage(this);
            case "sqlite":
                getLogger().info("Using SQLite storage");
                return new SQLiteStorage(this);
            case "mariadb":
                getLogger().info("Using MariaDB storage");
                return new MariaDBStorage(this);
            default:
                getLogger().warning("Invalid storage type in config.yml! Using SQLite storage as fallback.");
                return new SQLiteStorage(this);
        }
    }

    private Storage createStorage() {
        boolean useCache = getConfigManager().getStorageConfig().getBoolean("enableCache", true);
        Storage storage = createBackingStorage();
        return useCache ? new CachedStorage(this, storage) : storage;
    }

    public static LifeStealZAPI getAPI() {
        return new LifeStealZAPIImpl(getInstance());
    }

    public AsyncTaskManager getAsyncTaskManager() {
        return this.asyncTaskManager;
    }

    public BypassManager getBypassManager() {
        return this.bypassManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public EliminatedPlayersCache getEliminatedPlayersCache() {
        return this.eliminatedPlayersCache;
    }

    public GeyserManager getGeyserManager() {
        return this.geyserManager;
    }

    public GeyserPlayerFile getGeyserPlayerFile() {
        return this.geyserPlayerFile;
    }

    public GracePeriodManager getGracePeriodManager() {
        return this.gracePeriodManager;
    }

    public static LifeStealZ getInstance() {
        return (LifeStealZ) JavaPlugin.getPlugin(LifeStealZ.class);
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public OfflinePlayerCache getOfflinePlayerCache() {
        return this.offlinePlayerCache;
    }

    @Override // com.zetaplugins.zetacore.ZetaCorePlugin
    public File getPluginFile() {
        return getFile();
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }

    public ReviveBeaconEffectManager getReviveBeaconEffectManager() {
        return this.reviveBeaconEffectManager;
    }

    public ReviveTaskManager getReviveTaskManager() {
        return this.reviveTaskManager;
    }

    public Storage getStorage() {
        return this.storage;
    }

    public VersionChecker getVersionChecker() {
        return this.versionChecker;
    }

    public WebHookManager getWebHookManager() {
        return this.webHookManager;
    }

    public WorldGuardManager getWorldGuardManager() {
        return this.worldGuardManager;
    }

    public boolean hasGeyser() {
        return this.hasGeyser;
    }

    public boolean hasPlaceholderApi() {
        return this.hasPlaceholderApi;
    }

    public boolean hasWorldGuard() {
        return this.hasWorldGuard;
    }

    private void initializeBStats() {
        com.zetaplugins.zetacore.services.bStats.Metrics metrics = createBStatsMetrics(18735);
        metrics.addCustomChart(new com.zetaplugins.zetacore.services.bStats.Metrics.SimplePie("storage_type", () -> {
            return getConfigManager().getStorageConfig().getString("type");
        }));
        metrics.addCustomChart(new com.zetaplugins.zetacore.services.bStats.Metrics.SimplePie("language", () -> {
            return getConfig().getString("lang");
        }));
    }

    // rea: lambda$initializeBStats$0(): java.lang.String -> smali (java_body_unavailable)
    // .method private synthetic lambda$initializeBStats$0()Ljava/lang/String;
    //     .throw java/lang/Exception
    //     .max stack 2
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     .line 260
    //     aload 0
    //     invokevirtual com/zetaplugins/lifestealz/LifeStealZ getConfigManager ()Lcom/zetaplugins/lifestealz/util/ConfigManager;
    //     invokevirtual com/zetaplugins/lifestealz/util/ConfigManager getStorageConfig ()Lorg/bukkit/configuration/file/FileConfiguration;
    //     ldc "type"
    //     invokevirtual org/bukkit/configuration/file/FileConfiguration getString (Ljava/lang/String;)Ljava/lang/String;
    //     areturn
    // .end method

    // rea: lambda$initializeBStats$1(): java.lang.String -> smali (java_body_unavailable)
    // .method private synthetic lambda$initializeBStats$1()Ljava/lang/String;
    //     .throw java/lang/Exception
    //     .max stack 2
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     .line 261
    //     aload 0
    //     invokevirtual com/zetaplugins/lifestealz/LifeStealZ getConfig ()Lorg/bukkit/configuration/file/FileConfiguration;
    //     ldc "lang"
    //     invokevirtual org/bukkit/configuration/file/FileConfiguration getString (Ljava/lang/String;)Ljava/lang/String;
    //     areturn
    // .end method

    // rea: m22clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 1
    //     .max locals 0
    // 
    //     .line 33
    //     invokestatic interface com/zetaplugins/lifestealz/libs/faststats/core/ErrorTracker contextAware ()Lcom/zetaplugins/lifestealz/libs/faststats/core/ErrorTracker;
    //     putstatic com/zetaplugins/lifestealz/LifeStealZ FASTSTATS_ERROR_TRACKER Lcom/zetaplugins/lifestealz/libs/faststats/core/ErrorTracker;
    //     return
    // .end method

    public void onDisable() {
        getLogger().info("Canceling all running tasks...");
        this.asyncTaskManager.cancelAllTasks();
        this.reviveBeaconEffectManager.clearAllEffects();
        getLogger().info("Shutting down metrics...");
        this.metrics.shutdown();
        getLogger().info("LifeStealZ disabled!");
    }

    public void onEnable() {
        if (hasGeyser()) {
            getLogger().info("Geyser found, enabling Bedrock player support.");
            this.geyserPlayerFile = new GeyserPlayerFile();
            this.geyserManager = new GeyserManager();
        }
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        this.metrics.ready();
        this.asyncTaskManager = new AsyncTaskManager();
        this.reviveBeaconEffectManager = new ReviveBeaconEffectManager(this);
        this.reviveTaskManager = new ReviveTaskManager();
        this.languageManager = new LanguageManager(this);
        this.configManager = new ConfigManager(this);
        this.storage = createStorage();
        this.storage.init();
        this.recipeManager = new RecipeManager(this);
        this.recipeManager.registerRecipes();
        this.versionChecker = new VersionChecker(this, "l8Uv7FzS");
        this.gracePeriodManager = new GracePeriodManager(this);
        this.bypassManager = new BypassManager(this);
        this.webHookManager = new WebHookManager(this);
        this.eliminatedPlayersCache = new EliminatedPlayersCache(this);
        this.offlinePlayerCache = new OfflinePlayerCache(this);
        List<String> registeredCommands = new AutoCommandRegistrar(this, PACKAGE_PREFIX).registerAllCommands();
        getLogger().info("Registered " + registeredCommands.size() + " commands");
        List<String> registeredEvents = new AutoEventRegistrar(this, PACKAGE_PREFIX).registerAllListeners();
        getLogger().info("Registered " + registeredEvents.size() + " event listeners");
        initializeBStats();
        if (hasPlaceholderApi()) {
            PapiExpansion papiExpansion = new PapiExpansion(this);
            if (papiExpansion.canRegister()) {
                papiExpansion.register();
                getLogger().info("PlaceholderAPI found! Enabled PlaceholderAPI support!");
            }
        }
        getLogger().info("LifeStealZ enabled!");
    }

    public void onLoad() {
        getLogger().info("Loading LifeStealZ...");
        if (Bukkit.getName().toLowerCase().contains("spigot") || Bukkit.getName().toLowerCase().contains("craftbukkit")) {
            getLogger().severe("---------------------------------------------------");
            getLogger().severe("LifeStealZ does not support Spigot or Bukkit!");
            getLogger().severe("Please use Paper or any fork of Paper (like Purpur). If you need further assistance, please join our Discord server:");
            getLogger().severe("https://strassburger.org/discord");
            getLogger().severe("---------------------------------------------------");
        }
        if (hasWorldGuard()) {
            getLogger().info("WorldGuard found! Enabling WorldGuard support...");
            this.worldGuardManager = new WorldGuardManager();
            getLogger().info("WorldGuard found! Enabled WorldGuard support!");
        }
    }

    public static void setMaxHealth(Player player, double maxHealth) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(maxHealth);
        }
    }

}
