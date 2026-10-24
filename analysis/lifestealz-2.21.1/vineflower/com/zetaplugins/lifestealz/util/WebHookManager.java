package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class WebHookManager {
   private final LifeStealZ plugin;

   public WebHookManager(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public boolean isEliminationWebhookEnabled() {
      return this.plugin.getConfig().getBoolean("webhook.elimination");
   }

   public boolean isReviveWebhookEnabled() {
      return this.plugin.getConfig().getBoolean("webhook.revive");
   }

   private String getWebhookUrl() {
      return this.plugin.getConfig().getString("webhook.url");
   }

   public void sendWebhookMessage(String title, String message, String colorHex) {
      try {
         URL url = new URL(this.getWebhookUrl());
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setRequestMethod("POST");
         connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
         connection.setDoOutput(true);
         int color = Integer.parseInt(colorHex, 16);
         String jsonPayload = "{\"embeds\": [{\"title\": \"" + title + "\",\"description\": \"" + message + "\",\"color\": " + color + "}]}";

         try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
         }

         int responseCode = connection.getResponseCode();
         if (responseCode != 204) {
            this.plugin.getLogger().severe("Failed to send Discord webhook message. Response code: " + responseCode);
         }

         connection.disconnect();
      } catch (Exception var13) {
         this.plugin.getLogger().severe("Error sending Discord webhook message: " + var13.getMessage());
      }
   }

   public void sendWebhookMessage(WebHookManager.WebHookType type, String... placeholders) {
      switch (type) {
         case ELIMINATION:
            if (!this.isEliminationWebhookEnabled()) {
               return;
            }

            this.sendWebhookMessage(
               this.plugin.getLanguageManager().getString("webhookEliminationTitle", "Player Eliminated"),
               this.plugin
                  .getLanguageManager()
                  .getString("webhookElimination", "**%player%** has been eliminated by **%killer%**!")
                  .replace("%player%", placeholders[0])
                  .replace("%killer%", placeholders[1]),
               this.plugin.getLanguageManager().getString("webhookEliminationColor", "ea3323")
            );
            break;
         case REVIVE:
            if (!this.isReviveWebhookEnabled()) {
               return;
            }

            this.sendWebhookMessage(
               this.plugin.getLanguageManager().getString("webhookReviveTitle", "Player Revived"),
               this.plugin
                  .getLanguageManager()
                  .getString("webhookRevive", "**%player%** has been revived by **%reviver%**!")
                  .replace("%player%", placeholders[0])
                  .replace("%reviver%", placeholders[1]),
               this.plugin.getLanguageManager().getString("webhookReviveColor", "b094ee")
            );
      }
   }

   public static enum WebHookType {
      ELIMINATION,
      REVIVE;
   }
}
