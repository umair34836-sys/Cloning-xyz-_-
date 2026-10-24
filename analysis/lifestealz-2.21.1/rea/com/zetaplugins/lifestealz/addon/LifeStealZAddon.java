// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.addon;
// rea: class com.zetaplugins.lifestealz.addon.LifeStealZAddon
class LifeStealZAddon {
    boolean enabled;
    org.bukkit.plugin.java.JavaPlugin mainPlugin;
    com.zetaplugins.lifestealz.addon.AddonMetadata metadata;

    public LifeStealZAddon(AddonMetadata metadata, JavaPlugin mainPlugin) {
        this.metadata = metadata;
        this.mainPlugin = mainPlugin;
    }

    public JavaPlugin getMainPlugin() {
        return this.mainPlugin;
    }

    public AddonMetadata getMetadata() {
        return this.metadata;
    }

    public boolean isEnabled() {
        return this.enabled;
    }


    // rea: onDisable(): void — no body (not_available; abstract_method)


    // rea: onEnable(): void — no body (not_available; abstract_method)

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
