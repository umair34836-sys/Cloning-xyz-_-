// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.revive;
// rea: class com.zetaplugins.lifestealz.util.revive.ReviveTask
class ReviveTask {
    int durationSeconds;
    org.bukkit.Location location;
    java.util.UUID reviver;
    long start;
    java.util.UUID target;
    org.bukkit.scheduler.BukkitTask task;

    public ReviveTask(Location location, BukkitTask task, UUID reviver, UUID target, long start, int durationSeconds) {
        this.location = location;
        this.task = task;
        this.reviver = reviver;
        this.target = target;
        this.start = start;
        this.durationSeconds = durationSeconds;
    }

    public int durationSeconds() {
        return this.durationSeconds;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ReviveTask.class, Object.class), ReviveTask.class, "location;task;reviver;target;start;durationSeconds", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->location:Lorg/bukkit/Location;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->task:Lorg/bukkit/scheduler/BukkitTask;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->reviver:Ljava/util/UUID;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->target:Ljava/util/UUID;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->start:J", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->durationSeconds:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ReviveTask.class), ReviveTask.class, "location;task;reviver;target;start;durationSeconds", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->location:Lorg/bukkit/Location;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->task:Lorg/bukkit/scheduler/BukkitTask;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->reviver:Ljava/util/UUID;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->target:Ljava/util/UUID;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->start:J", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->durationSeconds:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    public Location location() {
        return this.location;
    }

    public UUID reviver() {
        return this.reviver;
    }

    public long start() {
        return this.start;
    }

    public UUID target() {
        return this.target;
    }

    public BukkitTask task() {
        return this.task;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ReviveTask.class), ReviveTask.class, "location;task;reviver;target;start;durationSeconds", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->location:Lorg/bukkit/Location;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->task:Lorg/bukkit/scheduler/BukkitTask;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->reviver:Ljava/util/UUID;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->target:Ljava/util/UUID;", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->start:J", "FIELD:Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;->durationSeconds:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

}
