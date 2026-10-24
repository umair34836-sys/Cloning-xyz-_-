// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customblocks;
// rea: class com.zetaplugins.lifestealz.util.customblocks.CustomBlock
class CustomBlock {
    com.zetaplugins.lifestealz.util.customblocks.CustomBlock[] $VALUES;
    org.bukkit.NamespacedKey CUSTOM_BLOCK_KEY;
    org.bukkit.NamespacedKey CUSTOM_ITEM_ID_KEY;
    com.zetaplugins.lifestealz.util.customblocks.CustomBlock REVIVE_BEACON;
    java.lang.String key;

    // rea: $values(): com.zetaplugins.lifestealz.util.customblocks.CustomBlock[] -> smali (java_body_unavailable)
    // .method private static synthetic $values()[Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     .max stack 4
    //     .max locals 0
    // 
    //     .line 7
    //     iconst_1
    //     anewarray com/zetaplugins/lifestealz/util/customblocks/CustomBlock
    //     dup
    //     iconst_0
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/CustomBlock REVIVE_BEACON Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     aastore
    //     areturn
    // .end method

    CustomBlock(String key) {
        this.key = key;
    }

    public static CustomBlock fromKey(String key) {
        for (CustomBlock customBlock : values()) {
            if (customBlock.getKey().equalsIgnoreCase(key)) {
                return customBlock;
            }
        }
        return null;
    }

    public String getCustomItemId(Block block) {
        return (String) BlockTagManager.getBlockTag(block, CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING);
    }

    public String getKey() {
        return this.key;
    }

    public boolean is(Block block) {
        String blockKey = (String) BlockTagManager.getBlockTag(block, CUSTOM_BLOCK_KEY, PersistentDataType.STRING);
        return blockKey != null && blockKey.equalsIgnoreCase(getKey());
    }

    // rea: m35clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 5
    //     .max locals 0
    // 
    //     .line 8
    //     new com/zetaplugins/lifestealz/util/customblocks/CustomBlock
    //     dup
    //     ldc "REVIVE_BEACON"
    //     iconst_0
    //     ldc "revive_beacon"
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/CustomBlock <init> (Ljava/lang/String;ILjava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/CustomBlock REVIVE_BEACON Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     .line 7
    //     invokestatic com/zetaplugins/lifestealz/util/customblocks/CustomBlock $values ()[Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/CustomBlock $VALUES [Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     .line 10
    //     new org/bukkit/NamespacedKey
    //     dup
    //     ldc "lifestealz"
    //     ldc "custom_block"
    //     invokespecial org/bukkit/NamespacedKey <init> (Ljava/lang/String;Ljava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/CustomBlock CUSTOM_BLOCK_KEY Lorg/bukkit/NamespacedKey;
    //     .line 11
    //     new org/bukkit/NamespacedKey
    //     dup
    //     ldc "lifestealz"
    //     ldc "custom_item_id_block"
    //     invokespecial org/bukkit/NamespacedKey <init> (Ljava/lang/String;Ljava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/CustomBlock CUSTOM_ITEM_ID_KEY Lorg/bukkit/NamespacedKey;
    //     return
    // .end method

    public void make(Block block) {
        BlockTagManager.tagBlock(block, CUSTOM_BLOCK_KEY, PersistentDataType.STRING, getKey());
    }

    public void make(Block block, String customItemId) {
        BlockTagManager.tagBlock(block, CUSTOM_BLOCK_KEY, PersistentDataType.STRING, getKey());
        BlockTagManager.tagBlock(block, CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING, customItemId);
    }

    // rea: valueOf(java.lang.String): com.zetaplugins.lifestealz.util.customblocks.CustomBlock -> smali (java_body_unavailable)
    // .method public static valueOf(Ljava/lang/String;)Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     .param mandated null
    //     .max stack 2
    //     .max locals 1
    // 
    //     .local 0 "name" Ljava/lang/String;
    //     .line 7
    //     ldc Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     aload 0
    //     invokestatic java/lang/Enum valueOf (Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
    //     checkcast com/zetaplugins/lifestealz/util/customblocks/CustomBlock
    //     areturn
    // .end method

    // rea: values(): com.zetaplugins.lifestealz.util.customblocks.CustomBlock[] -> smali (java_body_unavailable)
    // .method public static values()[Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     .max stack 1
    //     .max locals 0
    // 
    //     .line 7
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/CustomBlock $VALUES [Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     invokevirtual [Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock; clone ()Ljava/lang/Object;
    //     checkcast [Lcom/zetaplugins/lifestealz/util/customblocks/CustomBlock;
    //     areturn
    // .end method

}
