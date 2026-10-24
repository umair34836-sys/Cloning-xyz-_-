package com.zetaplugins.lifestealz.storage;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.List;
import java.util.UUID;

public abstract class Storage {
   private final LifeStealZ plugin;

   public Storage(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   protected LifeStealZ getPlugin() {
      return this.plugin;
   }

   public abstract void init();

   public abstract void save(PlayerData var1);

   public abstract PlayerData load(String var1);

   public abstract PlayerData load(UUID var1);

   public abstract List<UUID> getEliminatedPlayers();

   public abstract String export(String var1);

   public abstract int reviveAllPlayers(int var1, int var2, int var3, boolean var4);

   public abstract void importData(String var1);

   public abstract List<String> getPlayerNames();

   public abstract List<String> getEliminatedPlayerNames();

   public abstract void clearDatabase();

   protected abstract void migrateDatabase();
}
