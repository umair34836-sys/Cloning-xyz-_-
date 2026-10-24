package com.zetaplugins.lifestealz.addon;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AddonManager {
   private final LifeStealZ plugin;
   private final Map<String, LifeStealZAddon> registeredAddons = new HashMap<>();

   public AddonManager(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   public boolean registerAddon(LifeStealZAddon addon) {
      AddonMetadata metadata = addon.getMetadata();
      if (this.registeredAddons.containsKey(metadata.getName())) {
         this.plugin.getLogger().warning("Addon " + metadata.getName() + " is already registered!");
         return false;
      } else {
         try {
            this.plugin.getConfigManager().getAddonConfig(addon);
            addon.onEnable();
            addon.setEnabled(true);
            this.registeredAddons.put(metadata.getName(), addon);
            this.plugin.getLogger().info("Registered addon: " + metadata.getName() + " v" + metadata.getVersion() + " by " + metadata.getAuthor());
            return true;
         } catch (Exception var4) {
            this.plugin.getLogger().severe("Failed to register addon: " + metadata.getName());
            var4.printStackTrace();
            return false;
         }
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
         } catch (Exception var4) {
            this.plugin.getLogger().severe("Error while unregistering addon: " + name);
            var4.printStackTrace();
         }
      }
   }

   public LifeStealZAddon getAddon(String name) {
      return this.registeredAddons.get(name);
   }

   public Set<String> getRegisteredAddonNames() {
      return this.registeredAddons.keySet();
   }

   public void disableAllAddons() {
      for (String name : this.registeredAddons.keySet()) {
         this.unregisterAddon(name);
      }
   }
}
