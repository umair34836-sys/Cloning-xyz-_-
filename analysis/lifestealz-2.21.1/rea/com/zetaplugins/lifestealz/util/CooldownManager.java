// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.CooldownManager
class CooldownManager {
    java.util.Map<java.util.UUID, java.lang.Long> lastHeartGain;
    java.util.Map<java.util.UUID, java.lang.Long> lastHeartPickupMessage;
    java.util.Map<java.util.UUID, java.lang.Long> lastHeartUse;

    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 1
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/CooldownManager;
    //     .line 7
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     return
    // .end method

    // rea: m30clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 2
    //     .max locals 0
    // 
    //     .line 8
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putstatic com/zetaplugins/lifestealz/util/CooldownManager lastHeartUse Ljava/util/Map;
    //     .line 9
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putstatic com/zetaplugins/lifestealz/util/CooldownManager lastHeartGain Ljava/util/Map;
    //     .line 10
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putstatic com/zetaplugins/lifestealz/util/CooldownManager lastHeartPickupMessage Ljava/util/Map;
    //     return
    // .end method

}
