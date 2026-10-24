// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.PrepareGrindstone
class PrepareGrindstone {
    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 1
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/listeners/PrepareGrindstone;
    //     .line 12
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     return
    // .end method

    private boolean hasCustomItem(ItemStack[] items) {
        for (ItemStack item : items) {
            if (item != null && item.getItemMeta() != null && CustomItemManager.isCustomItem(item)) {
                return true;
            }
        }
        return false;
    }

/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/listeners/PrepareGrindstone.class */
@AutoRegisterListener
public final class PrepareGrindstone implements Listener {
    @EventHandler
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        GrindstoneInventory inventory = event.getInventory();
        ItemStack[] inputs = inventory.getContents();
        if (hasCustomItem(inputs)) {
            inventory.setResult((ItemStack) null);
            event.setResult((ItemStack) null);
        }
    }

}
