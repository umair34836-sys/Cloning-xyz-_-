package com.zetaplugins.lifestealz.util.customitems.customitemdata;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.customitems.CustomItemType;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class CustomItemData {
   private final String itemId;
   private final String name;
   private final List<String> lore;
   private final Material material;
   private final boolean enchanted;
   private final CustomItemType customItemType;
   private final boolean craftable;
   private final boolean requirePermission;
   private final boolean invulnerable;
   private final boolean despawnable;
   private final int customModelID;
   private final List<String> whitelistedWorlds;

   public CustomItemData(String itemId) throws IllegalArgumentException {
      this.itemId = itemId;
      ConfigurationSection section = this.getConfigurationSection();
      if (section == null) {
         throw new IllegalArgumentException("Custom item with id " + itemId + " does not exist!");
      } else {
         this.name = section.getString("name", "&7Fallback Name");
         this.lore = section.getStringList("lore");
         this.material = Material.valueOf(section.getString("material", "STONE"));
         this.enchanted = section.getBoolean("enchanted", false);
         this.customItemType = CustomItemType.fromString(section.getString("customItemType", "none"));
         this.craftable = section.getBoolean("craftable", true);
         this.requirePermission = section.getBoolean("requirePermission", false);
         this.invulnerable = section.getBoolean("invulnerable", false);
         this.despawnable = section.getBoolean("despawnable", true);
         this.whitelistedWorlds = section.getStringList("whitelistedWorlds");
         this.customModelID = section.getInt("customModelId", 0);
      }
   }

   protected ConfigurationSection getConfigurationSection() {
      FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();
      return config.getConfigurationSection(this.itemId);
   }

   public CustomItemData.CustomItemSoundData getSound() {
      return new CustomItemData.CustomItemSoundData(this.itemId);
   }

   public String getItemId() {
      return this.itemId;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public Material getMaterial() {
      return this.material;
   }

   public boolean isEnchanted() {
      return this.enchanted;
   }

   public CustomItemType getCustomItemType() {
      return this.customItemType;
   }

   public boolean isCraftable() {
      return this.craftable;
   }

   public boolean requiresPermission() {
      return this.requirePermission;
   }

   public String getPermission() {
      return "lifestealz.item." + this.itemId;
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public boolean isDespawnable() {
      return this.despawnable;
   }

   public int getCustomModelID() {
      return this.customModelID;
   }

   public List<String> getWhitelistedWorlds() {
      return new ArrayList<>(this.whitelistedWorlds);
   }

   public boolean isAllowedInWorld(String worldName) {
      return this.whitelistedWorlds.isEmpty() || this.whitelistedWorlds.contains(worldName);
   }

   public static class CustomItemSoundData {
      private final boolean enabled;
      private final Sound sound;
      private final double volume;
      private final double pitch;

      private CustomItemSoundData(String itemId) {
         FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();
         this.enabled = config.getBoolean(itemId + ".sound.enabled");
         this.sound = Sound.valueOf(config.getString(itemId + ".sound.sound"));
         this.volume = config.getDouble(itemId + ".sound.volume");
         this.pitch = config.getDouble(itemId + ".sound.pitch");
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public Sound getSound() {
         return this.sound;
      }

      public double getVolume() {
         return this.volume;
      }

      public double getPitch() {
         return this.pitch;
      }
   }
}
