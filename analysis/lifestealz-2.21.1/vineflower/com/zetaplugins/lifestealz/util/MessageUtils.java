package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class MessageUtils {
   private static final Map<String, String> colorMap = new HashMap<>();

   private MessageUtils() {
   }

   public static Component formatMsg(String msg, MessageUtils.Replaceable... replaceables) {
      for (MessageUtils.Replaceable replaceable : replaceables) {
         msg = msg.replace(replaceable.getPlaceholder(), replaceable.getValue());
      }

      for (Entry<String, String> entry : colorMap.entrySet()) {
         msg = msg.replace(entry.getKey(), entry.getValue());
      }

      MiniMessage mm = MiniMessage.miniMessage();
      return mm.deserialize("<!i>" + msg);
   }

   public static Component getAndFormatMsg(boolean addPrefix, String path, String fallback, MessageUtils.Replaceable... replaceables) {
      if (path.startsWith("messages.")) {
         path = path.substring("messages.".length());
      }

      MiniMessage mm = MiniMessage.miniMessage();
      String msg = "<!i>" + LifeStealZ.getInstance().getLanguageManager().getString(path, fallback);
      String prefix = LifeStealZ.getInstance().getLanguageManager().getString("prefix", "&8[&cLifeStealZ&8]");
      msg = !prefix.isEmpty() && addPrefix ? prefix + " " + msg : msg;

      for (MessageUtils.Replaceable replaceable : replaceables) {
         msg = msg.replace(replaceable.getPlaceholder(), replaceable.getValue());
      }

      for (Entry<String, String> entry : colorMap.entrySet()) {
         msg = msg.replace(entry.getKey(), entry.getValue());
      }

      return mm.deserialize(msg);
   }

   public static String formatTime(long seconds) {
      if (seconds < 0L) {
         return "0s";
      } else {
         long hours = seconds / 3600L;
         long minutes = seconds % 3600L / 60L;
         long secs = seconds % 60L;
         StringBuilder result = new StringBuilder();
         if (hours > 0L) {
            result.append(hours).append("h ");
         }

         if (minutes > 0L || hours > 0L && secs > 0L) {
            result.append(minutes).append("m ");
         }

         if (secs > 0L || seconds == 0L) {
            result.append(secs).append("s");
         }

         return result.toString().trim();
      }
   }

   static {
      colorMap.put("&0", "<black>");
      colorMap.put("&1", "<dark_blue>");
      colorMap.put("&2", "<dark_green>");
      colorMap.put("&3", "<dark_aqua>");
      colorMap.put("&4", "<dark_red>");
      colorMap.put("&5", "<dark_purple>");
      colorMap.put("&6", "<gold>");
      colorMap.put("&7", "<gray>");
      colorMap.put("&8", "<dark_gray>");
      colorMap.put("&9", "<blue>");
      colorMap.put("&a", "<green>");
      colorMap.put("&b", "<aqua>");
      colorMap.put("&c", "<red>");
      colorMap.put("&d", "<light_purple>");
      colorMap.put("&e", "<yellow>");
      colorMap.put("&f", "<white>");
      colorMap.put("&k", "<obfuscated>");
      colorMap.put("&l", "<bold>");
      colorMap.put("&m", "<strikethrough>");
      colorMap.put("&n", "<underline>");
      colorMap.put("&o", "<italic>");
      colorMap.put("&r", "<reset>");
   }

   public static class Replaceable {
      private final String placeholder;
      private final String value;

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
