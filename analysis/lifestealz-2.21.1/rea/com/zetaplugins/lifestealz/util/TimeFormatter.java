// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.TimeFormatter
class TimeFormatter {
/* JADX INFO: loaded from: target.apk:com/zetaplugins/lifestealz/util/TimeFormatter.class */
public final class TimeFormatter {
    private TimeFormatter() {
    }

    public static String formatDuration(long duration) {
        if (duration <= 0) {
            return "0s";
        }
        long days = duration / 86400000;
        long duration2 = duration % 86400000;
        long hours = duration2 / 3600000;
        long duration3 = duration2 % 3600000;
        long minutes = duration3 / 60000;
        long seconds = (duration3 % 60000) / 1000;
        StringBuilder result = new StringBuilder();
        int addedSymbols = 0;
        if (days > 0) {
            result.append(days).append("d");
            addedSymbols = 0 + 1;
        }
        if (hours > 0 && addedSymbols < 2) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(hours).append("h");
            addedSymbols++;
        }
        if (minutes > 0 && addedSymbols < 2) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(minutes).append("m");
            addedSymbols++;
        }
        if (seconds > 0 && addedSymbols < 2) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(seconds).append("s");
            int i = addedSymbols + 1;
        }
        return result.length() > 0 ? result.toString() : "0s";
    }

}
