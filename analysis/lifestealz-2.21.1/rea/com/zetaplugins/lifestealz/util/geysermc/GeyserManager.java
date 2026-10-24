// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.geysermc;
// rea: class com.zetaplugins.lifestealz.util.geysermc.GeyserManager
class GeyserManager {
    org.geysermc.floodgate.api.FloodgateApi geyserApi;
    com.zetaplugins.lifestealz.util.geysermc.GeyserPlayerFile geyserPlayerFile;

    // rea: <init>(): void -> smali (java_body_unavailable)
    // .method public <init>()V
    //     .max stack 2
    //     .max locals 1
    // 
    //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/geysermc/GeyserManager;
    //     .line 9
    //     aload 0
    //     invokespecial java/lang/Object <init> ()V
    //     .line 10
    //     aload 0
    //     invokestatic interface org/geysermc/floodgate/api/FloodgateApi getInstance ()Lorg/geysermc/floodgate/api/FloodgateApi;
    //     putfield com/zetaplugins/lifestealz/util/geysermc/GeyserManager geyserApi Lorg/geysermc/floodgate/api/FloodgateApi;
    //     .line 11
    //     aload 0
    //     invokestatic com/zetaplugins/lifestealz/LifeStealZ getInstance ()Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     invokevirtual com/zetaplugins/lifestealz/LifeStealZ getGeyserPlayerFile ()Lcom/zetaplugins/lifestealz/util/geysermc/GeyserPlayerFile;
    //     putfield com/zetaplugins/lifestealz/util/geysermc/GeyserManager geyserPlayerFile Lcom/zetaplugins/lifestealz/util/geysermc/GeyserPlayerFile;
    //     return
    // .end method

    public String getOfflineBedrockPlayerName(UUID playerUniqueId) {
        return this.geyserPlayerFile.getPlayerName(playerUniqueId);
    }

    public UUID getOfflineBedrockPlayerUniqueId(String playerName) {
        return this.geyserPlayerFile.getPlayerUUID(playerName);
    }

    public boolean isBedrockPlayer(Player player) {
        return this.geyserApi.isFloodgatePlayer(player.getUniqueId());
    }

}
