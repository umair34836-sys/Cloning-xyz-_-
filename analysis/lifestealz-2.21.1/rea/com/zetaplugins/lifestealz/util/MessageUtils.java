// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.MessageUtils
class MessageUtils {
    java.util.Map<java.lang.String, java.lang.String> colorMap;

    private MessageUtils() {
    }

    public static Component formatMsg(String msg, Replaceable... replaceables) {
        for (Replaceable replaceable : replaceables) {
            msg = msg.replace(replaceable.getPlaceholder(), replaceable.getValue());
        }
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            msg = msg.replace(entry.getKey(), entry.getValue());
        }
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize("<!i>" + msg);
    }

    public static String formatTime(long seconds) {
        if (seconds < 0) {
            return "0s";
        }
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        StringBuilder result = new StringBuilder();
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0 || (hours > 0 && secs > 0)) {
            result.append(minutes).append("m ");
        }
        if (secs > 0 || seconds == 0) {
            result.append(secs).append("s");
        }
        return result.toString().trim();
    }

    public static Component getAndFormatMsg(boolean addPrefix, String path, String fallback, Replaceable... replaceables) {
        if (path.startsWith("messages.")) {
            path = path.substring("messages.".length());
        }
        MiniMessage mm = MiniMessage.miniMessage();
        String msg = "<!i>" + LifeStealZ.getInstance().getLanguageManager().getString(path, fallback);
        String prefix = LifeStealZ.getInstance().getLanguageManager().getString("prefix", "&8[&cLifeStealZ&8]");
        String msg2 = (prefix.isEmpty() || !addPrefix) ? msg : prefix + " " + msg;
        for (Replaceable replaceable : replaceables) {
            msg2 = msg2.replace(replaceable.getPlaceholder(), replaceable.getValue());
        }
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            msg2 = msg2.replace(entry.getKey(), entry.getValue());
        }
        return mm.deserialize(msg2);
    }

    // rea: m33clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 3
    //     .max locals 0
    // 
    //     .line 16
    //     new java/util/HashMap
    //     dup
    //     invokespecial java/util/HashMap <init> ()V
    //     putstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     .line 17
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&0"
    //     ldc "<black>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 18
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&1"
    //     ldc "<dark_blue>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 19
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&2"
    //     ldc "<dark_green>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 20
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&3"
    //     ldc "<dark_aqua>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 21
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&4"
    //     ldc "<dark_red>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 22
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&5"
    //     ldc "<dark_purple>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 23
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&6"
    //     ldc "<gold>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 24
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&7"
    //     ldc "<gray>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 25
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&8"
    //     ldc "<dark_gray>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 26
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&9"
    //     ldc "<blue>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 27
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&a"
    //     ldc "<green>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 28
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&b"
    //     ldc "<aqua>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 29
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&c"
    //     ldc "<red>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 30
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&d"
    //     ldc "<light_purple>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 31
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&e"
    //     ldc "<yellow>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 32
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&f"
    //     ldc "<white>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 33
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&k"
    //     ldc "<obfuscated>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 34
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&l"
    //     ldc "<bold>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 35
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&m"
    //     ldc "<strikethrough>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 36
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&n"
    //     ldc "<underline>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 37
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&o"
    //     ldc "<italic>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 38
    //     getstatic com/zetaplugins/lifestealz/util/MessageUtils colorMap Ljava/util/Map;
    //     ldc "&r"
    //     ldc "<reset>"
    //     invokeinterface java/util/Map put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //     pop
    //     .line 39
    //     return
    // .end method

    // rea: class com.zetaplugins.lifestealz.util.MessageUtils.Replaceable
    class Replaceable {
        java.lang.String placeholder;
        java.lang.String value;

            public Replaceable(String placeholder, String value) {
                this.placeholder = placeholder;
                this.value = value;
            }

            public String getPlaceholder() {
                return this.placeholder;
            }

            public String getValue() {
                return this.value;
            }

    }

}
