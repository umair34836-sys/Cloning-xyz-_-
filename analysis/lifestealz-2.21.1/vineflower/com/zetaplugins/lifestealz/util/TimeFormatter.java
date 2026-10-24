package com.zetaplugins.lifestealz.util;

public final class TimeFormatter {
   private TimeFormatter() {
   }

   public static String formatDuration(long duration) {
      if (duration <= 0L) {
         return "0s";
      } else {
         long SECOND_MS = 1000L;
         long MINUTE_MS = 60000L;
         long HOUR_MS = 3600000L;
         long DAY_MS = 86400000L;
         long days = duration / 86400000L;
         duration %= 86400000L;
         long hours = duration / 3600000L;
         duration %= 3600000L;
         long minutes = duration / 60000L;
         duration %= 60000L;
         long seconds = duration / 1000L;
         StringBuilder result = new StringBuilder();
         int addedSymbols = 0;
         if (days > 0L) {
            result.append(days).append("d");
            addedSymbols++;
         }

         if (hours > 0L && addedSymbols < 2) {
            if (result.length() > 0) {
               result.append(" ");
            }

            result.append(hours).append("h");
            addedSymbols++;
         }

         if (minutes > 0L && addedSymbols < 2) {
            if (result.length() > 0) {
               result.append(" ");
            }

            result.append(minutes).append("m");
            addedSymbols++;
         }

         if (seconds > 0L && addedSymbols < 2) {
            if (result.length() > 0) {
               result.append(" ");
            }

            result.append(seconds).append("s");
            addedSymbols++;
         }

         return result.length() > 0 ? result.toString() : "0s";
      }
   }
}
