package com.zetaplugins.lifestealz.util.revive;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

public record ReviveTask(Location location, BukkitTask task, UUID reviver, UUID target, long start, int durationSeconds) {
}
