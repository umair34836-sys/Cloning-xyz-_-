package com.zetaplugins.lifestealz.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.scheduler.BukkitTask;

public final class AsyncTaskManager {
   private final List<BukkitTask> runningTasks = new ArrayList<>();

   public void addTask(BukkitTask task) {
      this.runningTasks.add(task);
   }

   public void cancelAllTasks() {
      for (BukkitTask task : this.runningTasks) {
         if (!task.isCancelled()) {
            task.cancel();
         }
      }

      this.runningTasks.clear();
   }
}
