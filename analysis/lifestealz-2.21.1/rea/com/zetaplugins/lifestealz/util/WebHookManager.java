// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.WebHookManager
class WebHookManager {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public WebHookManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private String getWebhookUrl() {
        return this.plugin.getConfig().getString("webhook.url");
    }

    public boolean isEliminationWebhookEnabled() {
        return this.plugin.getConfig().getBoolean("webhook.elimination");
    }

    public boolean isReviveWebhookEnabled() {
        return this.plugin.getConfig().getBoolean("webhook.revive");
    }

    public void sendWebhookMessage(String title, String message, String colorHex) {
        try {
            URL url = new URL(getWebhookUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            int color = Integer.parseInt(colorHex, 16);
            String jsonPayload = "{\"embeds\": [{\"title\": \"" + title + "\",\"description\": \"" + message + "\",\"color\": " + color + "}]}";
            OutputStream os = connection.getOutputStream();
            try {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                if (os != null) {
                    os.close();
                }
                int responseCode = connection.getResponseCode();
                if (responseCode != 204) {
                    this.plugin.getLogger().severe("Failed to send Discord webhook message. Response code: " + responseCode);
                }
                connection.disconnect();
            } catch (Throwable th) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Exception e) {
            this.plugin.getLogger().severe("Error sending Discord webhook message: " + e.getMessage());
        }
    }

    public void sendWebhookMessage(WebHookType type, String... placeholders) {
        switch (type) {
            case ELIMINATION:
                if (isEliminationWebhookEnabled()) {
                    sendWebhookMessage(this.plugin.getLanguageManager().getString("webhookEliminationTitle", "Player Eliminated"), this.plugin.getLanguageManager().getString("webhookElimination", "**%player%** has been eliminated by **%killer%**!").replace("%player%", placeholders[0]).replace("%killer%", placeholders[1]), this.plugin.getLanguageManager().getString("webhookEliminationColor", "ea3323"));
                    break;
                }
                break;
            case REVIVE:
                if (isReviveWebhookEnabled()) {
                    sendWebhookMessage(this.plugin.getLanguageManager().getString("webhookReviveTitle", "Player Revived"), this.plugin.getLanguageManager().getString("webhookRevive", "**%player%** has been revived by **%reviver%**!").replace("%player%", placeholders[0]).replace("%reviver%", placeholders[1]), this.plugin.getLanguageManager().getString("webhookReviveColor", "b094ee"));
                    break;
                }
                break;
        }
    }

    // rea: class com.zetaplugins.lifestealz.util.WebHookManager.WebHookType
    class WebHookType {
        com.zetaplugins.lifestealz.util.WebHookManager$WebHookType[] $VALUES;
        com.zetaplugins.lifestealz.util.WebHookManager$WebHookType ELIMINATION;
        com.zetaplugins.lifestealz.util.WebHookManager$WebHookType REVIVE;

        // rea: $values(): com.zetaplugins.lifestealz.util.WebHookManager$WebHookType[] -> smali (java_body_unavailable)
        // .method private static synthetic $values()[Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     .max stack 4
        //     .max locals 0
        // 
        //     .line 92
        //     iconst_2
        //     anewarray com/zetaplugins/lifestealz/util/WebHookManager$WebHookType
        //     dup
        //     iconst_0
        //     getstatic com/zetaplugins/lifestealz/util/WebHookManager$WebHookType ELIMINATION Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     aastore
        //     dup
        //     iconst_1
        //     getstatic com/zetaplugins/lifestealz/util/WebHookManager$WebHookType REVIVE Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     aastore
        //     areturn
        // .end method

        // rea: <init>(java.lang.String, int): void -> smali (java_body_unavailable)
        // .method private <init>(Ljava/lang/String;I)V
        //     .signature ()V
        //     .param synthetic null
        //     .param synthetic null
        //     .max stack 3
        //     .max locals 3
        // 
        //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     .line 92
        //     aload 0
        //     aload 1
        //     iload 2
        //     invokespecial java/lang/Enum <init> (Ljava/lang/String;I)V
        //     return
        // .end method

        // rea: m34clinit(): void -> smali (static_initializer_disassembly)
        // .method static <clinit>()V
        //     .max stack 4
        //     .max locals 0
        // 
        //     .line 93
        //     new com/zetaplugins/lifestealz/util/WebHookManager$WebHookType
        //     dup
        //     ldc "ELIMINATION"
        //     iconst_0
        //     invokespecial com/zetaplugins/lifestealz/util/WebHookManager$WebHookType <init> (Ljava/lang/String;I)V
        //     putstatic com/zetaplugins/lifestealz/util/WebHookManager$WebHookType ELIMINATION Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     .line 94
        //     new com/zetaplugins/lifestealz/util/WebHookManager$WebHookType
        //     dup
        //     ldc "REVIVE"
        //     iconst_1
        //     invokespecial com/zetaplugins/lifestealz/util/WebHookManager$WebHookType <init> (Ljava/lang/String;I)V
        //     putstatic com/zetaplugins/lifestealz/util/WebHookManager$WebHookType REVIVE Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     .line 92
        //     invokestatic com/zetaplugins/lifestealz/util/WebHookManager$WebHookType $values ()[Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     putstatic com/zetaplugins/lifestealz/util/WebHookManager$WebHookType $VALUES [Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     return
        // .end method

        // rea: valueOf(java.lang.String): com.zetaplugins.lifestealz.util.WebHookManager$WebHookType -> smali (java_body_unavailable)
        // .method public static valueOf(Ljava/lang/String;)Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     .param mandated null
        //     .max stack 2
        //     .max locals 1
        // 
        //     .local 0 "name" Ljava/lang/String;
        //     .line 92
        //     ldc Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     aload 0
        //     invokestatic java/lang/Enum valueOf (Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
        //     checkcast com/zetaplugins/lifestealz/util/WebHookManager$WebHookType
        //     areturn
        // .end method

        // rea: values(): com.zetaplugins.lifestealz.util.WebHookManager$WebHookType[] -> smali (java_body_unavailable)
        // .method public static values()[Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     .max stack 1
        //     .max locals 0
        // 
        //     .line 92
        //     getstatic com/zetaplugins/lifestealz/util/WebHookManager$WebHookType $VALUES [Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     invokevirtual [Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType; clone ()Ljava/lang/Object;
        //     checkcast [Lcom/zetaplugins/lifestealz/util/WebHookManager$WebHookType;
        //     areturn
        // .end method

    }

}
