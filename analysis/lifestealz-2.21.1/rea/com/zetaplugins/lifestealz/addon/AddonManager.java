// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.addon;
// rea: class com.zetaplugins.lifestealz.addon.AddonManager
class AddonManager {
    com.zetaplugins.lifestealz.LifeStealZ plugin;
    java.util.Map<java.lang.String, com.zetaplugins.lifestealz.addon.LifeStealZAddon> registeredAddons;

    public AddonManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    public void disableAllAddons() {
        for (String name : this.registeredAddons.keySet()) {
            unregisterAddon(name);
        }
    }

    public LifeStealZAddon getAddon(String name) {
        return this.registeredAddons.get(name);
    }

    public Set<String> getRegisteredAddonNames() {
        return this.registeredAddons.keySet();
    }

    public boolean registerAddon(LifeStealZAddon addon) {
        AddonMetadata metadata = addon.getMetadata();
        if (this.registeredAddons.containsKey(metadata.getName())) {
            this.plugin.getLogger().warning("Addon " + metadata.getName() + " is already registered!");
            return false;
        }
        try {
            this.plugin.getConfigManager().getAddonConfig(addon);
            addon.onEnable();
            addon.setEnabled(true);
            this.registeredAddons.put(metadata.getName(), addon);
            this.plugin.getLogger().info("Registered addon: " + metadata.getName() + " v" + metadata.getVersion() + " by " + metadata.getAuthor());
            return true;
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to register addon: " + metadata.getName());
            e.printStackTrace();
            return false;
        }
    }

    public void unregisterAddon(String name) {
        LifeStealZAddon addon = this.registeredAddons.get(name);
        if (addon != null) {
            try {
                addon.onDisable();
                addon.setEnabled(false);
                this.registeredAddons.remove(name);
                this.plugin.getLogger().info("Unregistered addon: " + name);
            } catch (Exception e) {
                this.plugin.getLogger().severe("Error while unregistering addon: " + name);
                e.printStackTrace();
            }
        }
    }

}
