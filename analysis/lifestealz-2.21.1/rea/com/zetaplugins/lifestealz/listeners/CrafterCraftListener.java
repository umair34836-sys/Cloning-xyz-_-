// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.CrafterCraftListener
class CrafterCraftListener {
    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 1
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/listeners/CrafterCraftListener;
    //     .line 13
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     return
    // .end method

    private boolean invHasCustomItem(Inventory inventory) {
        ListIterator it = inventory.iterator();
        while (it.hasNext()) {
            ItemStack item = (ItemStack) it.next();
            if (item != null && item.getItemMeta() != null && CustomItemManager.isCustomItem(item)) {
                return true;
            }
        }
        return false;
    }

/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/listeners/CrafterCraftListener.class */
@AutoRegisterListener
public class CrafterCraftListener implements Listener {
    @EventHandler
    public void onCrafterCraft(CrafterCraftEvent event) {
        Crafter state = event.getBlock().getState(false);
        if (state instanceof Crafter) {
            Crafter crafter = state;
            if (invHasCustomItem(crafter.getInventory())) {
                event.setCancelled(true);
            }
        }
    }

}
