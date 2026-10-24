// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events;
// rea: class com.zetaplugins.lifestealz.events.ZEvent
class ZEvent {
    boolean cancelled;
    org.bukkit.event.HandlerList handlers;

    public ZEvent() {
        this.cancelled = false;
    }

    public ZEvent(boolean isAsync) {
        super(isAsync);
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    // rea: m26clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 2
    //     .max locals 0
    // 
    //     .line 8
    //     new org/bukkit/event/HandlerList
    //     dup
    //     invokespecial org/bukkit/event/HandlerList <init> ()V
    //     putstatic com/zetaplugins/lifestealz/events/ZEvent handlers Lorg/bukkit/event/HandlerList;
    //     return
    // .end method

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
