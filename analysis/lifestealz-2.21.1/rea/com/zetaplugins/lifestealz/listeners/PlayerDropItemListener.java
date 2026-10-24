// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.PlayerDropItemListener
class PlayerDropItemListener {
    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 1
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/listeners/PlayerDropItemListener;
    //     .line 11
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     return
    // .end method

/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/listeners/PlayerDropItemListener.class */
@AutoRegisterListener
public class PlayerDropItemListener implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        if (CustomItemManager.isForbiddenItem(item.getItemStack())) {
            item.remove();
        }
    }

}
