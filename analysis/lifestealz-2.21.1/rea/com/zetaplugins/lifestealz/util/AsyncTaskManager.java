// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.AsyncTaskManager
class AsyncTaskManager {
    java.util.List<org.bukkit.scheduler.BukkitTask> runningTasks;

    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 3
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/AsyncTaskManager;
    //     .line 11
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     .line 12
    //     aload 0
    //     new java/util/ArrayList
    //     dup
    //     invokespecial java/util/ArrayList <init> ()V
    //     putfield com/zetaplugins/lifestealz/util/AsyncTaskManager runningTasks Ljava/util/List;
    //     return
    // .end method

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
