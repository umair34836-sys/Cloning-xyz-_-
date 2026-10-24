// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.PrepareItemCraft
class PrepareItemCraft {
    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 1
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/listeners/PrepareItemCraft;
    //     .line 12
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     return
    // .end method

    private boolean matrixHasCustomItem(ItemStack[] matrix) {
        for (ItemStack item : matrix) {
            if (item != null && item.getItemMeta() != null && CustomItemManager.isCustomItem(item)) {
                return true;
            }
        }
        return false;
    }

/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/listeners/PrepareItemCraft.class */
@AutoRegisterListener
public final class PrepareItemCraft implements Listener {
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();
        ItemStack[] matrix = inventory.getMatrix();
        if (result == null || result.getItemMeta() == null || CustomItemManager.isCustomItem(result) || !matrixHasCustomItem(matrix)) {
            return;
        }
        inventory.setResult((ItemStack) null);
    }

}
