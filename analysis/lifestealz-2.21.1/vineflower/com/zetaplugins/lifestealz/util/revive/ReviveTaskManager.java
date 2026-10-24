package com.zetaplugins.lifestealz.util.revive;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;

public class ReviveTaskManager {
   private final Map<Location, ReviveTask> reviveTasks = new HashMap<>();

   public ReviveTask getReviveTask(Location location) {
      return this.reviveTasks.get(location);
   }

   public void addReviveTask(Location location, ReviveTask reviveTask) {
      this.reviveTasks.put(location, reviveTask);
   }

   public boolean isReviving(Location location) {
      return this.reviveTasks.containsKey(location);
   }

   public void removeReviveTask(Location location) {
      this.reviveTasks.remove(location);
   }

   public boolean isRevivingTarget(UUID playerId) {
      return this.reviveTasks.values().stream().anyMatch(task -> task.target().equals(playerId));
   }
}
