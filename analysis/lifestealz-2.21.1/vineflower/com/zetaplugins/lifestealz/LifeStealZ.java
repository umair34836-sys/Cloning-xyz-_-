package com.zetaplugins.lifestealz;

import com.zetaplugins.lifestealz.api.LifeStealZAPI;
import com.zetaplugins.lifestealz.api.LifeStealZAPIImpl;
import com.zetaplugins.lifestealz.caches.EliminatedPlayersCache;
import com.zetaplugins.lifestealz.caches.OfflinePlayerCache;
import com.zetaplugins.lifestealz.libs.faststats.bukkit.BukkitMetrics;
import com.zetaplugins.lifestealz.libs.faststats.core.ErrorTracker;
import com.zetaplugins.lifestealz.libs.faststats.core.Metrics;
import com.zetaplugins.lifestealz.storage.CachedStorage;
import com.zetaplugins.lifestealz.storage.MariaDBStorage;
import com.zetaplugins.lifestealz.storage.MySQLStorage;
import com.zetaplugins.lifestealz.storage.SQLiteStorage;
import com.zetaplugins.lifestealz.storage.Storage;
import com.zetaplugins.lifestealz.util.AsyncTaskManager;
import com.zetaplugins.lifestealz.util.BypassManager;
import com.zetaplugins.lifestealz.util.ConfigManager;
import com.zetaplugins.lifestealz.util.GracePeriodManager;
import com.zetaplugins.lifestealz.util.LanguageManager;
import com.zetaplugins.lifestealz.util.PapiExpansion;
import com.zetaplugins.lifestealz.util.VersionChecker;
import com.zetaplugins.lifestealz.util.WebHookManager;
import com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager;
import com.zetaplugins.lifestealz.util.customitems.recipe.RecipeManager;
import com.zetaplugins.lifestealz.util.geysermc.GeyserManager;
import com.zetaplugins.lifestealz.util.geysermc.GeyserPlayerFile;
import com.zetaplugins.lifestealz.util.revive.ReviveTaskManager;
import com.zetaplugins.lifestealz.util.worldguard.WorldGuardManager;
import com.zetaplugins.zetacore.ZetaCorePlugin;
import com.zetaplugins.zetacore.services.commands.AutoCommandRegistrar;
import com.zetaplugins.zetacore.services.events.AutoEventRegistrar;
import java.io.File;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifeStealZ extends ZetaCorePlugin {
   private static final String PACKAGE_PREFIX = "com.zetaplugins.lifestealz";
   private static final String FASTSTATS_TOKEN = "8fb586fadff0ff4cb078cb25d69ab734";
   public static final ErrorTracker FASTSTATS_ERROR_TRACKER = ErrorTracker.contextAware();
   private VersionChecker versionChecker;
   private Storage storage;
   private WorldGuardManager worldGuardManager;
   private LanguageManager languageManager;
   private ConfigManager configManager;
   private RecipeManager recipeManager;
   private GeyserManager geyserManager;
   private GeyserPlayerFile geyserPlayerFile;
   private WebHookManager webHookManager;
   private GracePeriodManager gracePeriodManager;
   private BypassManager bypassManager;
   private EliminatedPlayersCache eliminatedPlayersCache;
   private OfflinePlayerCache offlinePlayerCache;
   private AsyncTaskManager asyncTaskManager;
   private ReviveBeaconEffectManager reviveBeaconEffectManager;
   private ReviveTaskManager reviveTaskManager;
   private final boolean hasWorldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
   private final boolean hasPlaceholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
   private final boolean hasGeyser = Bukkit.getPluginManager().getPlugin("floodgate") != null;
   private final Metrics metrics = BukkitMetrics.factory().token("8fb586fadff0ff4cb078cb25d69ab734").errorTracker(FASTSTATS_ERROR_TRACKER).create((Plugin)this);

   public void onLoad() {
      this.getLogger().info("Loading LifeStealZ...");
      if (Bukkit.getName().toLowerCase().contains("spigot") || Bukkit.getName().toLowerCase().contains("craftbukkit")) {
         this.getLogger().severe("---------------------------------------------------");
         this.getLogger().severe("LifeStealZ does not support Spigot or Bukkit!");
         this.getLogger().severe("Please use Paper or any fork of Paper (like Purpur). If you need further assistance, please join our Discord server:");
         this.getLogger().severe("https://strassburger.org/discord");
         this.getLogger().severe("---------------------------------------------------");
      }

      if (this.hasWorldGuard()) {
         this.getLogger().info("WorldGuard found! Enabling WorldGuard support...");
         this.worldGuardManager = new WorldGuardManager();
         this.getLogger().info("WorldGuard found! Enabled WorldGuard support!");
      }
   }

   public void onEnable() {
      if (this.hasGeyser()) {
         this.getLogger().info("Geyser found, enabling Bedrock player support.");
         this.geyserPlayerFile = new GeyserPlayerFile();
         this.geyserManager = new GeyserManager();
      }

      this.getConfig().options().copyDefaults(true);
      this.saveDefaultConfig();
      this.metrics.ready();
      this.asyncTaskManager = new AsyncTaskManager();
      this.reviveBeaconEffectManager = new ReviveBeaconEffectManager(this);
      this.reviveTaskManager = new ReviveTaskManager();
      this.languageManager = new LanguageManager(this);
      this.configManager = new ConfigManager(this);
      this.storage = this.createStorage();
      this.storage.init();
      this.recipeManager = new RecipeManager(this);
      this.recipeManager.registerRecipes();
      this.versionChecker = new VersionChecker(this, "l8Uv7FzS");
      this.gracePeriodManager = new GracePeriodManager(this);
      this.bypassManager = new BypassManager(this);
      this.webHookManager = new WebHookManager(this);
      this.eliminatedPlayersCache = new EliminatedPlayersCache(this);
      this.offlinePlayerCache = new OfflinePlayerCache(this);
      List<String> registeredCommands = new AutoCommandRegistrar(this, "com.zetaplugins.lifestealz").registerAllCommands();
      this.getLogger().info("Registered " + registeredCommands.size() + " commands");
      List<String> registeredEvents = new AutoEventRegistrar(this, "com.zetaplugins.lifestealz").registerAllListeners();
      this.getLogger().info("Registered " + registeredEvents.size() + " event listeners");
      this.initializeBStats();
      if (this.hasPlaceholderApi()) {
         PapiExpansion papiExpansion = new PapiExpansion(this);
         if (papiExpansion.canRegister()) {
            papiExpansion.register();
            this.getLogger().info("PlaceholderAPI found! Enabled PlaceholderAPI support!");
         }
      }

      this.getLogger().info("LifeStealZ enabled!");
   }

   public void onDisable() {
      this.getLogger().info("Canceling all running tasks...");
      this.asyncTaskManager.cancelAllTasks();
      this.reviveBeaconEffectManager.clearAllEffects();
      this.getLogger().info("Shutting down metrics...");
      this.metrics.shutdown();
      this.getLogger().info("LifeStealZ disabled!");
   }

   public static LifeStealZ getInstance() {
      return (LifeStealZ)JavaPlugin.getPlugin(LifeStealZ.class);
   }

   public static LifeStealZAPI getAPI() {
      return new LifeStealZAPIImpl(getInstance());
   }

   public AsyncTaskManager getAsyncTaskManager() {
      return this.asyncTaskManager;
   }

   public ReviveBeaconEffectManager getReviveBeaconEffectManager() {
      return this.reviveBeaconEffectManager;
   }

   public ReviveTaskManager getReviveTaskManager() {
      return this.reviveTaskManager;
   }

   public VersionChecker getVersionChecker() {
      return this.versionChecker;
   }

   public Storage getStorage() {
      return this.storage;
   }

   public EliminatedPlayersCache getEliminatedPlayersCache() {
      return this.eliminatedPlayersCache;
   }

   public OfflinePlayerCache getOfflinePlayerCache() {
      return this.offlinePlayerCache;
   }

   public WorldGuardManager getWorldGuardManager() {
      return this.worldGuardManager;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public GracePeriodManager getGracePeriodManager() {
      return this.gracePeriodManager;
   }

   public BypassManager getBypassManager() {
      return this.bypassManager;
   }

   public GeyserManager getGeyserManager() {
      return this.geyserManager;
   }

   public GeyserPlayerFile getGeyserPlayerFile() {
      return this.geyserPlayerFile;
   }

   public boolean hasWorldGuard() {
      return this.hasWorldGuard;
   }

   public boolean hasPlaceholderApi() {
      return this.hasPlaceholderApi;
   }

   public boolean hasGeyser() {
      return this.hasGeyser;
   }

   public WebHookManager getWebHookManager() {
      return this.webHookManager;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   private Storage createBackingStorage() {
      String var1 = this.getConfigManager().getStorageConfig().getString("type").toLowerCase();
      switch (var1) {
         case "mysql":
            this.getLogger().info("Using MySQL storage");
            return new MySQLStorage(this);
         case "sqlite":
            this.getLogger().info("Using SQLite storage");
            return new SQLiteStorage(this);
         case "mariadb":
            this.getLogger().info("Using MariaDB storage");
            return new MariaDBStorage(this);
         default:
            this.getLogger().warning("Invalid storage type in config.yml! Using SQLite storage as fallback.");
            return new SQLiteStorage(this);
      }
   }

   private Storage createStorage() {
      boolean useCache = this.getConfigManager().getStorageConfig().getBoolean("enableCache", true);
      Storage storage = this.createBackingStorage();
      return (Storage)(useCache ? new CachedStorage(this, storage) : storage);
   }

   public static void setMaxHealth(Player player, double maxHealth) {
      AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
      if (attribute != null) {
         attribute.setBaseValue(maxHealth);
      }
   }

   private void initializeBStats() {
      int pluginId = 18735;
      com.zetaplugins.zetacore.services.bStats.Metrics metrics = this.createBStatsMetrics(pluginId);
      metrics.addCustomChart(
         new com.zetaplugins.zetacore.services.bStats.Metrics.SimplePie("storage_type", () -> this.getConfigManager().getStorageConfig().getString("type"))
      );
      metrics.addCustomChart(new com.zetaplugins.zetacore.services.bStats.Metrics.SimplePie("language", () -> this.getConfig().getString("lang")));
   }

   @Override
   public File getPluginFile() {
      return this.getFile();
   }
}
