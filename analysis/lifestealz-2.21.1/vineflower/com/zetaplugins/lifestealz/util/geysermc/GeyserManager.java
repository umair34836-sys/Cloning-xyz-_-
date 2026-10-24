package com.zetaplugins.lifestealz.util.geysermc;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class GeyserManager {
   private final FloodgateApi geyserApi = FloodgateApi.getInstance();
   private final GeyserPlayerFile geyserPlayerFile = LifeStealZ.getInstance().getGeyserPlayerFile();

   public boolean isBedrockPlayer(Player player) {
      return this.geyserApi.isFloodgatePlayer(player.getUniqueId());
   }

   public UUID getOfflineBedrockPlayerUniqueId(String playerName) {
      return this.geyserPlayerFile.getPlayerUUID(playerName);
   }

   public String getOfflineBedrockPlayerName(UUID playerUniqueId) {
      return this.geyserPlayerFile.getPlayerName(playerUniqueId);
   }
}
