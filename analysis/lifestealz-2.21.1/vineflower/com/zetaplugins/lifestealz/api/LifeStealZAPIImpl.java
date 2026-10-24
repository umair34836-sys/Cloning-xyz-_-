package com.zetaplugins.lifestealz.api;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomItemData;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class LifeStealZAPIImpl implements LifeStealZAPI {
   private final LifeStealZ plugin;

   public LifeStealZAPIImpl(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @Override
   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   @Override
   public PlayerData getPlayerData(UUID uuid) {
      return this.plugin.getStorage().load(uuid);
   }

   @Override
   public void savePlayerData(PlayerData playerData) {
      this.plugin.getStorage().save(playerData);
   }

   @Override
   public boolean isEliminated(UUID uuid) {
      PlayerData playerData = this.plugin.getStorage().load(uuid);
      return playerData == null ? false : playerData.getMaxHealth() <= 0.0;
   }

   @Override
   public boolean eliminate(UUID uuid) {
      PlayerData playerData = this.plugin.getStorage().load(uuid);
      if (playerData == null) {
         return false;
      } else {
         playerData.setMaxHealth(0.0);
         this.plugin.getStorage().save(playerData);
         return true;
      }
   }

   @Override
   public boolean revive(UUID uuid) {
      PlayerData playerData = this.plugin.getStorage().load(uuid);
      if (playerData == null) {
         return false;
      } else {
         playerData.setMaxHealth((double)(this.plugin.getConfig().getInt("reviveHearts") * 2));
         playerData.setHasBeenRevived(playerData.getHasBeenRevived() + 1);
         this.plugin.getStorage().save(playerData);
         return true;
      }
   }

   @Override
   public ItemStack getCustomItem(String customItemID) {
      return CustomItemManager.createCustomItem(customItemID);
   }

   @Override
   public CustomItemData getCustomItemData(String customItemID) {
      return CustomItemManager.getCustomItemData(customItemID);
   }

   @Override
   public Set<String> getCustomItemIDs() {
      return this.plugin.getRecipeManager().getItemIds();
   }

   @Override
   public ItemStack getDefaultHeart() {
      return CustomItemManager.createHeart();
   }

   @Override
   public String getCustomItemID(ItemStack item) {
      return CustomItemManager.getCustomItemId(item);
   }

   @Override
   public boolean isBypassActive(Player player) {
      return this.plugin.getBypassManager().hasBypass(player);
   }
}
