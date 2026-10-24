// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.ItemSpawnListener
class ItemSpawnListener {
    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 1
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/listeners/ItemSpawnListener;
    //     .line 15
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     return
    // .end method

/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/listeners/ItemSpawnListener.class */
@AutoRegisterListener(name = "HahahaItemSpawnListenerLol")
public class ItemSpawnListener implements Listener {
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        PersistentDataContainer container = item.getItemStack().getItemMeta().getPersistentDataContainer();
        boolean shouldHaveUnlimitedLifetime = container.has(CustomItemManager.DESPAWNABLE_KEY) && !Boolean.TRUE.equals(container.get(CustomItemManager.DESPAWNABLE_KEY, PersistentDataType.BOOLEAN));
        boolean shouldBeInvulnerable = container.has(CustomItemManager.INVULNERABLE_KEY) && Boolean.TRUE.equals(container.get(CustomItemManager.INVULNERABLE_KEY, PersistentDataType.BOOLEAN));
        if (shouldHaveUnlimitedLifetime) {
            item.setUnlimitedLifetime(true);
        }
        if (shouldBeInvulnerable) {
            item.setInvulnerable(true);
        }
    }

}
