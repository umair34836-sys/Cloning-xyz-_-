package com.zetaplugins.lifestealz.api;

import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomItemData;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface LifeStealZAPI {
   String getVersion();

   PlayerData getPlayerData(UUID var1);

   void savePlayerData(PlayerData var1);

   boolean isEliminated(UUID var1);

   boolean eliminate(UUID var1);

   boolean revive(UUID var1);

   ItemStack getCustomItem(String var1);

   CustomItemData getCustomItemData(String var1);

   Set<String> getCustomItemIDs();

   ItemStack getDefaultHeart();

   String getCustomItemID(ItemStack var1);

   boolean isBypassActive(Player var1);
}
