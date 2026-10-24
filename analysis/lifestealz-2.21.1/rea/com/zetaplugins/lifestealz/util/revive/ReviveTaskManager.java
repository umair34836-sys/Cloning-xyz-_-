// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.revive;
// rea: class com.zetaplugins.lifestealz.util.revive.ReviveTaskManager
class ReviveTaskManager {
    java.util.Map<org.bukkit.Location, com.zetaplugins.lifestealz.util.revive.ReviveTask> reviveTasks;

    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 3
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/revive/ReviveTaskManager;
    //     .line 15
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     .line 16
    //     aload 0
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putfield com/zetaplugins/lifestealz/util/revive/ReviveTaskManager reviveTasks Ljava/util/Map;
    //     .line 17
    //     return
    // .end method

    public void addReviveTask(Location location, ReviveTask reviveTask) {
        this.reviveTasks.put(location, reviveTask);
    }

    public ReviveTask getReviveTask(Location location) {
        return this.reviveTasks.get(location);
    }

    public boolean isReviving(Location location) {
        return this.reviveTasks.containsKey(location);
    }

    public boolean isRevivingTarget(UUID playerId) {
        return this.reviveTasks.values().stream().anyMatch(task -> {
            return task.target().equals(playerId);
        });
    }

    // rea: lambda$isRevivingTarget$0(java.util.UUID, com.zetaplugins.lifestealz.util.revive.ReviveTask): boolean -> smali (java_body_unavailable)
    // .method private static synthetic lambda$isRevivingTarget$0(Ljava/util/UUID;Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;)Z
    //     .max stack 2
    //     .max locals 2
    // 
    //     .local 0 "playerId" Ljava/util/UUID;
    //     .local 1 "task" Lcom/zetaplugins/lifestealz/util/revive/ReviveTask;
    //     .line 62
    //     aload 1
    //     invokevirtual com/zetaplugins/lifestealz/util/revive/ReviveTask target ()Ljava/util/UUID;
    //     aload 0
    //     invokevirtual java/util/UUID equals (Ljava/lang/Object;)Z
    //     ireturn
    // .end method

    public void removeReviveTask(Location location) {
        this.reviveTasks.remove(location);
    }

}
