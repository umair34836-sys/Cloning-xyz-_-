// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customblocks;
// rea: class com.zetaplugins.lifestealz.util.customblocks.ParticleColor
class ParticleColor {
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor[] $VALUES;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor BLUE;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor GRAY;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor GREEN;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor ORANGE;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor PINK;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor PURPLE;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor RED;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor WHITE;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor YELLOW;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor$Bounds blueBounds;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor$Bounds greenBounds;
    com.zetaplugins.lifestealz.util.customblocks.ParticleColor$Bounds redBounds;

    // rea: $values(): com.zetaplugins.lifestealz.util.customblocks.ParticleColor[] -> smali (java_body_unavailable)
    // .method private static synthetic $values()[Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .max stack 4
    //     .max locals 0
    // 
    //     .line 11
    //     bipush 9
    //     anewarray com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     iconst_0
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor WHITE Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     dup
    //     iconst_1
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor GRAY Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     dup
    //     iconst_2
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor RED Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     dup
    //     iconst_3
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor ORANGE Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     dup
    //     iconst_4
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor YELLOW Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     dup
    //     iconst_5
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor GREEN Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     dup
    //     bipush 6
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor BLUE Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     dup
    //     bipush 7
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor PURPLE Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     dup
    //     bipush 8
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor PINK Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aastore
    //     areturn
    // .end method

    ParticleColor(Bounds redBounds, Bounds greenBounds, Bounds blueBounds) {
        this.redBounds = redBounds;
        this.greenBounds = greenBounds;
        this.blueBounds = blueBounds;
    }

    public static ParticleColor fromString(String colorName) {
        try {
            return valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return WHITE;
        }
    }

    public Color getColor() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int red = this.redBounds.random(rand);
        int green = this.greenBounds.random(rand);
        int blue = this.blueBounds.random(rand);
        return Color.fromRGB(red, green, blue);
    }

    // rea: m36clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 10
    //     .max locals 0
    // 
    //     .line 12
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "WHITE"
    //     iconst_0
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 230
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 230
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 230
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor WHITE Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 13
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "GRAY"
    //     iconst_1
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     bipush 100
    //     sipush 155
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     bipush 100
    //     sipush 155
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     bipush 100
    //     sipush 155
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor GRAY Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 14
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "RED"
    //     iconst_2
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 200
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 39
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 39
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor RED Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 15
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "ORANGE"
    //     iconst_3
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 230
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     bipush 100
    //     sipush 150
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 19
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor ORANGE Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 16
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "YELLOW"
    //     iconst_4
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 230
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 230
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 39
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor YELLOW Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 17
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "GREEN"
    //     iconst_5
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 39
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 200
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 39
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor GREEN Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 18
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "BLUE"
    //     bipush 6
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 39
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 39
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 200
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor BLUE Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 19
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "PURPLE"
    //     bipush 7
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 150
    //     sipush 230
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     iconst_0
    //     bipush 39
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 150
    //     sipush 230
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor PURPLE Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 20
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     dup
    //     ldc "PINK"
    //     bipush 8
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 230
    //     sipush 255
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     bipush 100
    //     sipush 150
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     new com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds
    //     dup
    //     sipush 150
    //     sipush 230
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds <init> (II)V
    //     invokespecial com/zetaplugins/lifestealz/util/customblocks/ParticleColor <init> (Ljava/lang/String;ILcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;)V
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor PINK Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .line 11
    //     invokestatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor $values ()[Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     putstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor $VALUES [Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     return
    // .end method

    // rea: valueOf(java.lang.String): com.zetaplugins.lifestealz.util.customblocks.ParticleColor -> smali (java_body_unavailable)
    // .method public static valueOf(Ljava/lang/String;)Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .param mandated null
    //     .max stack 2
    //     .max locals 1
    // 
    //     .local 0 "name" Ljava/lang/String;
    //     .line 11
    //     ldc Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     aload 0
    //     invokestatic java/lang/Enum valueOf (Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
    //     checkcast com/zetaplugins/lifestealz/util/customblocks/ParticleColor
    //     areturn
    // .end method

    // rea: values(): com.zetaplugins.lifestealz.util.customblocks.ParticleColor[] -> smali (java_body_unavailable)
    // .method public static values()[Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     .max stack 1
    //     .max locals 0
    // 
    //     .line 11
    //     getstatic com/zetaplugins/lifestealz/util/customblocks/ParticleColor $VALUES [Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     invokevirtual [Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor; clone ()Ljava/lang/Object;
    //     checkcast [Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor;
    //     areturn
    // .end method

    // rea: class com.zetaplugins.lifestealz.util.customblocks.ParticleColor.Bounds
    class Bounds {
        int max;
        int min;

            private Bounds(int min, int max) {
                if (min >= 0 && max <= 255 && min <= max) {
                    this.min = min;
                    this.max = max;
                    return;
                }
                throw new IllegalArgumentException("Color bounds must be in range 0-255 and min ≤ max");
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Bounds.class, Object.class), Bounds.class, "min;max", "FIELD:Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;->min:I", "FIELD:Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;->max:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Bounds.class), Bounds.class, "min;max", "FIELD:Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;->min:I", "FIELD:Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;->max:I").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            public int max() {
                return this.max;
            }

            public int min() {
                return this.min;
            }

            public int random(ThreadLocalRandom rand) {
                return rand.nextInt(this.min, this.max + 1);
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Bounds.class), Bounds.class, "min;max", "FIELD:Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;->min:I", "FIELD:Lcom/zetaplugins/lifestealz/util/customblocks/ParticleColor$Bounds;->max:I").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

    }

}
