// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util;
// rea: class com.zetaplugins.lifestealz.util.BypassManager
class BypassManager {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public BypassManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    public BypassConfig getConfig() {
        return new BypassConfig(this.plugin);
    }

    public boolean hasBypass(Player player) {
        if (!isEnabled() || player == null) {
            return false;
        }
        return player.hasPermission("lifestealz.bypass");
    }

    public boolean isEnabled() {
        return getConfig().isEnabled();
    }

    // rea: class com.zetaplugins.lifestealz.util.BypassManager.BypassConfig
    class BypassConfig {
        com.zetaplugins.lifestealz.LifeStealZ plugin;

            public BypassConfig(LifeStealZ plugin) {
                this.plugin = plugin;
            }

            public boolean damageFromPlayers() {
                return this.plugin.getConfig().getBoolean("bypassPermission.damageFromPlayers", false);
            }

            public boolean damageToPlayers() {
                return this.plugin.getConfig().getBoolean("bypassPermission.damageToPlayers", false);
            }

            public boolean gainHearts() {
                return this.plugin.getConfig().getBoolean("bypassPermission.gainHearts", false);
            }

            public boolean isEnabled() {
                return this.plugin.getConfig().getBoolean("bypassPermission.enabled", false);
            }

            public boolean looseHearts() {
                return this.plugin.getConfig().getBoolean("bypassPermission.looseHearts", false);
            }

            public boolean useHearts() {
                return this.plugin.getConfig().getBoolean("bypassPermission.useHearts", false);
            }

    }

}
