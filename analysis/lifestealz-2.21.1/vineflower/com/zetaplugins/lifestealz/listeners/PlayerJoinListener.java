package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.storage.Storage;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.geysermc.GeyserManager;
import com.zetaplugins.lifestealz.util.geysermc.GeyserPlayerFile;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AutoRegisterListener
public final class PlayerJoinListener implements Listener {
   private final LifeStealZ plugin;
   private final GeyserManager geyserManager;
   private final GeyserPlayerFile geyserPlayerFile;

   public PlayerJoinListener(LifeStealZ plugin) {
      this.plugin = plugin;
      this.geyserManager = plugin.getGeyserManager();
      this.geyserPlayerFile = plugin.getGeyserPlayerFile();
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      Storage storage = this.plugin.getStorage();
      if (this.plugin.hasGeyser() && this.geyserManager.isBedrockPlayer(player)) {
         this.geyserPlayerFile.savePlayer(player.getUniqueId(), player.getName());
      }

      PlayerData playerData = this.loadOrCreatePlayerData(player, storage, this.plugin.getConfig().getInt("startHearts", 10));
      LifeStealZ.setMaxHealth(player, playerData.getMaxHealth());
      this.notifyOpAboutUpdate(player);
   }

   private PlayerData loadOrCreatePlayerData(Player player, Storage storage, int startHearts) {
      PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
      if (playerData == null) {
         playerData = new PlayerData(player.getName(), player.getUniqueId());
         playerData.setFirstJoin(System.currentTimeMillis());
         playerData.setMaxHealth((double)startHearts * 2.0);
         storage.save(playerData);
         this.plugin.getGracePeriodManager().startGracePeriod(player);
         this.plugin.getOfflinePlayerCache().addItem(player.getName());
      }

      return playerData;
   }

   private void notifyOpAboutUpdate(Player player) {
      if (player.isOp() && this.plugin.getConfig().getBoolean("checkForUpdates") && this.plugin.getVersionChecker().isNewVersionAvailable()) {
         player.sendMessage(
            MessageUtils.getAndFormatMsg(
               true,
               "newVersionAvailable",
               "&7A new version of LifeStealZ is available!\\n&c<click:OPEN_URL:https://modrinth.com/plugin/lifestealz/versions>https://modrinth.com/plugin/lifestealz/versions</click>"
            )
         );
      }
   }
}
