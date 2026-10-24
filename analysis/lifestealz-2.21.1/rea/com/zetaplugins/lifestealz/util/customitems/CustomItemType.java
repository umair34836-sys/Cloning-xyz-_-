// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems;
// rea: class com.zetaplugins.lifestealz.util.customitems.CustomItemType
class CustomItemType {
    com.zetaplugins.lifestealz.util.customitems.CustomItemType[] $VALUES;
    com.zetaplugins.lifestealz.util.customitems.CustomItemType HEART;
    com.zetaplugins.lifestealz.util.customitems.CustomItemType NONE;
    com.zetaplugins.lifestealz.util.customitems.CustomItemType NONUSABLE;
    com.zetaplugins.lifestealz.util.customitems.CustomItemType REVIVE;
    com.zetaplugins.lifestealz.util.customitems.CustomItemType REVIVE_BEACON;
    java.lang.String type;

    // rea: $values(): com.zetaplugins.lifestealz.util.customitems.CustomItemType[] -> smali (java_body_unavailable)
    // .method private static synthetic $values()[Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     .max stack 4
    //     .max locals 0
    // 
    //     .line 12
    //     iconst_5
    //     anewarray com/zetaplugins/lifestealz/util/customitems/CustomItemType
    //     dup
    //     iconst_0
    //     getstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType HEART Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     aastore
    //     dup
    //     iconst_1
    //     getstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType REVIVE Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     aastore
    //     dup
    //     iconst_2
    //     getstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType REVIVE_BEACON Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     aastore
    //     dup
    //     iconst_3
    //     getstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType NONE Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     aastore
    //     dup
    //     iconst_4
    //     getstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType NONUSABLE Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     aastore
    //     areturn
    // .end method

    CustomItemType(String type) {
        this.type = type;
    }

    public static CustomItemType fromCustomItem(String customItemId, FileConfiguration config) {
        String type = config.getString(customItemId + ".customItemType", "none");
        return fromString(type);
    }

    public static CustomItemType fromString(String type) {
        for (CustomItemType itemType : values()) {
            if (itemType.getType().equalsIgnoreCase(type)) {
                return itemType;
            }
        }
        return NONE;
    }

    public String getType() {
        return this.type;
    }

    public boolean is(ItemStack item) {
        try {
            return item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(CustomItemManager.CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING) && ((String) item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING)).equalsIgnoreCase(getType());
        } catch (NullPointerException e) {
            return false;
        }
    }

    // rea: m38clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 5
    //     .max locals 0
    // 
    //     .line 13
    //     new com/zetaplugins/lifestealz/util/customitems/CustomItemType
    //     dup
    //     ldc "HEART"
    //     iconst_0
    //     ldc "heart"
    //     invokespecial com/zetaplugins/lifestealz/util/customitems/CustomItemType <init> (Ljava/lang/String;ILjava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType HEART Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     .line 14
    //     new com/zetaplugins/lifestealz/util/customitems/CustomItemType
    //     dup
    //     ldc "REVIVE"
    //     iconst_1
    //     ldc "revive"
    //     invokespecial com/zetaplugins/lifestealz/util/customitems/CustomItemType <init> (Ljava/lang/String;ILjava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType REVIVE Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     .line 15
    //     new com/zetaplugins/lifestealz/util/customitems/CustomItemType
    //     dup
    //     ldc "REVIVE_BEACON"
    //     iconst_2
    //     ldc "revivebeacon"
    //     invokespecial com/zetaplugins/lifestealz/util/customitems/CustomItemType <init> (Ljava/lang/String;ILjava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType REVIVE_BEACON Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     .line 16
    //     new com/zetaplugins/lifestealz/util/customitems/CustomItemType
    //     dup
    //     ldc "NONE"
    //     iconst_3
    //     ldc "none"
    //     invokespecial com/zetaplugins/lifestealz/util/customitems/CustomItemType <init> (Ljava/lang/String;ILjava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType NONE Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     .line 17
    //     new com/zetaplugins/lifestealz/util/customitems/CustomItemType
    //     dup
    //     ldc "NONUSABLE"
    //     iconst_4
    //     ldc "non-usable"
    //     invokespecial com/zetaplugins/lifestealz/util/customitems/CustomItemType <init> (Ljava/lang/String;ILjava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType NONUSABLE Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     .line 12
    //     invokestatic com/zetaplugins/lifestealz/util/customitems/CustomItemType $values ()[Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType $VALUES [Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     return
    // .end method

    // rea: valueOf(java.lang.String): com.zetaplugins.lifestealz.util.customitems.CustomItemType -> smali (java_body_unavailable)
    // .method public static valueOf(Ljava/lang/String;)Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     .param mandated null
    //     .max stack 2
    //     .max locals 1
    // 
    //     .local 0 "name" Ljava/lang/String;
    //     .line 12
    //     ldc Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     aload 0
    //     invokestatic java/lang/Enum valueOf (Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
    //     checkcast com/zetaplugins/lifestealz/util/customitems/CustomItemType
    //     areturn
    // .end method

    // rea: values(): com.zetaplugins.lifestealz.util.customitems.CustomItemType[] -> smali (java_body_unavailable)
    // .method public static values()[Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     .max stack 1
    //     .max locals 0
    // 
    //     .line 12
    //     getstatic com/zetaplugins/lifestealz/util/customitems/CustomItemType $VALUES [Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     invokevirtual [Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType; clone ()Ljava/lang/Object;
    //     checkcast [Lcom/zetaplugins/lifestealz/util/customitems/CustomItemType;
    //     areturn
    // .end method

}
