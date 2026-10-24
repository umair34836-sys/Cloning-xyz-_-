package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.events.death.ZPlayerAltKillEvent;
import com.zetaplugins.lifestealz.events.death.ZPlayerBypassDeathEvent;
import com.zetaplugins.lifestealz.events.death.ZPlayerEliminationEvent;
import com.zetaplugins.lifestealz.events.death.ZPlayerGracePeriodDeathEvent;
import com.zetaplugins.lifestealz.events.death.ZPlayerHeartGainCooldownEvent;
import com.zetaplugins.lifestealz.events.death.ZPlayerMaxHeartsReachedEvent;
import com.zetaplugins.lifestealz.events.death.ZPlayerNaturalDeathEvent;
import com.zetaplugins.lifestealz.events.death.ZPlayerPvPDeathEvent;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.BypassManager;
import com.zetaplugins.lifestealz.util.CooldownManager;
import com.zetaplugins.lifestealz.util.GracePeriodManager;
import com.zetaplugins.lifestealz.util.MaxHeartsManager;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.WebHookManager;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.lifestealz.util.worldguard.WorldGuardManager;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

@AutoRegisterListener
public final class PlayerDeathListener implements Listener {
   private final LifeStealZ plugin;

   public PlayerDeathListener(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      Player player = event.getEntity();
      Player killer = player.getKiller();
      if (!this.plugin.hasWorldGuard() || WorldGuardManager.checkHeartLossFlag(player)) {
         UUID playerUUID = player.getUniqueId();
         if (player.hasMetadata("combat_log_npc")) {
            playerUUID = (UUID)((MetadataValue)player.getMetadata("combat_log_npc").get(0)).value();
         }

         PlayerData playerData = this.plugin.getStorage().load(playerUUID);
         if (playerData == null) {
            this.plugin.getLogger().severe("PlayerData not found for player " + player.getName() + " on death event. This should not happen!");
         } else {
            boolean isDeathByPlayer = killer != null && !killer.getUniqueId().equals(playerUUID);
            if (!this.handleAntiAltLogic(event, player, killer)) {
               boolean looseHeartsToNature = this.plugin.getConfig().getBoolean("looseHeartsToNature") || this.plugin.getConfig().getInt("heartsPerKill") <= 0;
               boolean looseHeartsToPlayer = this.plugin.getConfig().getBoolean("looseHeartsToPlayer")
                  || this.plugin.getConfig().getInt("heartsPerNaturalDeath") <= 0;
               if (!isDeathByPlayer && looseHeartsToNature || isDeathByPlayer && looseHeartsToPlayer) {
                  this.handleHeartLoss(event, player, killer, playerData, isDeathByPlayer);
               }
            }
         }
      }
   }

   private void handleHeartLoss(PlayerDeathEvent event, Player player, Player killer, PlayerData playerData, boolean isDeathByPlayer) {
      double minHearts = (double)(this.plugin.getConfig().getInt("minHearts") * 2);
      double healthPerKill = (double)(this.plugin.getConfig().getInt("heartsPerKill") * 2);
      double healthPerNaturalDeath = (double)(this.plugin.getConfig().getInt("heartsPerNaturalDeath") * 2);
      double healthToLoose = isDeathByPlayer ? healthPerKill : healthPerNaturalDeath;
      boolean victimHasBypass = this.restrictedHeartLossByBypass(player);
      boolean killerHasBypass = isDeathByPlayer && killer != null && this.restrictedHeartGainByBypass(killer);
      if (victimHasBypass || killerHasBypass) {
         ZPlayerBypassDeathEvent bypassEvent = new ZPlayerBypassDeathEvent(event, killer, victimHasBypass, killerHasBypass);
         Bukkit.getPluginManager().callEvent(bypassEvent);
         if (!bypassEvent.isCancelled()) {
            if (victimHasBypass) {
               player.sendMessage(bypassEvent.getMessageToVictim());
            }

            if (killerHasBypass && killer != null) {
               killer.sendMessage(bypassEvent.getMessageToKiller());
            }

            return;
         }
      }

      boolean victimInGracePeriod = this.restrictedHeartLossByGracePeriod(player);
      boolean killerInGracePeriod = isDeathByPlayer && this.restrictedHeartGainByGracePeriod(killer);
      if (victimInGracePeriod || killerInGracePeriod) {
         ZPlayerGracePeriodDeathEvent graceEvent = new ZPlayerGracePeriodDeathEvent(event, killer, victimInGracePeriod, killerInGracePeriod);
         Bukkit.getPluginManager().callEvent(graceEvent);
         if (!graceEvent.isCancelled()) {
            if (victimInGracePeriod) {
               player.sendMessage(graceEvent.getMessageToVictim());
            }

            if (killerInGracePeriod && killer != null) {
               killer.sendMessage(graceEvent.getMessageToKiller());
            }

            if (victimInGracePeriod) {
               return;
            }
         }
      }

      boolean preventKillerGain = false;
      boolean droppedAtKiller = false;
      if (isDeathByPlayer && !killerInGracePeriod && !killerHasBypass) {
         if (this.handleHeartGainCooldown(event, player, killer, healthToLoose)) {
            preventKillerGain = true;
            if (this.plugin.getConfig().getBoolean("heartGainCooldown.dropOnCooldown")) {
               droppedAtKiller = true;
            }
         }

         if (this.handleMaxHeartsLimit(event, player, killer, healthToLoose)) {
            preventKillerGain = true;
            if (this.plugin.getConfig().getBoolean("dropHeartsIfMax")) {
               droppedAtKiller = true;
            }
         }
      }

      if (playerData.getMaxHealth() - healthToLoose <= minHearts) {
         this.handleElimination(event, player, playerData, killer, isDeathByPlayer, healthToLoose, preventKillerGain, droppedAtKiller);
      } else {
         if (isDeathByPlayer) {
            this.handlePvPDeath(event, player, killer, playerData, healthToLoose, preventKillerGain, droppedAtKiller);
         } else {
            this.handleNaturalDeath(event, player, playerData, healthToLoose);
         }
      }
   }

   private boolean handleHeartGainCooldown(PlayerDeathEvent event, Player player, Player killer, double healthGain) {
      boolean heartGainCooldownEnabled = this.plugin.getConfig().getBoolean("heartGainCooldown.enabled");
      long heartGainCooldown = this.plugin.getConfig().getLong("heartGainCooldown.cooldown");
      if (heartGainCooldownEnabled
         && CooldownManager.lastHeartGain.get(killer.getUniqueId()) != null
         && CooldownManager.lastHeartGain.get(killer.getUniqueId()) + heartGainCooldown > System.currentTimeMillis()) {
         long timeLeft = (CooldownManager.lastHeartGain.get(killer.getUniqueId()) + heartGainCooldown - System.currentTimeMillis()) / 1000L;
         ZPlayerHeartGainCooldownEvent cooldownEvent = new ZPlayerHeartGainCooldownEvent(event, killer, timeLeft);
         cooldownEvent.setShouldDropHeartsInstead(this.plugin.getConfig().getBoolean("heartGainCooldown.dropOnCooldown"));
         Bukkit.getPluginManager().callEvent(cooldownEvent);
         if (!cooldownEvent.isCancelled()) {
            killer.sendMessage(cooldownEvent.getCooldownMessage());
            if (cooldownEvent.isShouldDropHeartsInstead()) {
               this.dropHeartsNaturally(killer.getLocation(), (int)(healthGain / 2.0), CustomItemManager.createHeartGainCooldownHeart());
            }

            return true;
         }
      }

      return false;
   }

   private boolean handleMaxHeartsLimit(PlayerDeathEvent event, Player player, Player killer, double healthGain) {
      double maxHearts = MaxHeartsManager.getMaxHearts(killer, this.plugin.getConfig());
      PlayerData killerPlayerData = this.plugin.getStorage().load(killer.getUniqueId());
      if (killerPlayerData.getMaxHealth() + healthGain > maxHearts) {
         ZPlayerMaxHeartsReachedEvent maxHeartsEvent = new ZPlayerMaxHeartsReachedEvent(event, killer, maxHearts);
         maxHeartsEvent.setShouldDropHeartsInstead(this.plugin.getConfig().getBoolean("dropHeartsIfMax"));
         Bukkit.getPluginManager().callEvent(maxHeartsEvent);
         if (!maxHeartsEvent.isCancelled()) {
            if (maxHeartsEvent.isShouldDropHeartsInstead()) {
               this.dropHeartsNaturally(killer.getLocation(), (int)(healthGain / 2.0), CustomItemManager.createMaxHealthHeart());
            } else {
               killer.sendMessage(maxHeartsEvent.getMaxHeartsMessage());
            }

            return true;
         }
      }

      return false;
   }

   private void handlePvPDeath(
      PlayerDeathEvent event, Player player, Player killer, PlayerData playerData, double healthToLoose, boolean preventKillerGain, boolean droppedAtKiller
   ) {
      ZPlayerPvPDeathEvent pvpEvent = new ZPlayerPvPDeathEvent(event, killer, healthToLoose, healthToLoose);
      boolean shouldDropFromPvP = this.plugin.getConfig().getBoolean("dropHeartsPlayer") && !droppedAtKiller;
      pvpEvent.setShouldDropHearts(shouldDropFromPvP);
      pvpEvent.setKillerShouldGainHearts(!preventKillerGain);
      Bukkit.getPluginManager().callEvent(pvpEvent);
      if (!pvpEvent.isCancelled()) {
         if (pvpEvent.getHeartsToLose() > 0.0) {
            playerData.setMaxHealth(playerData.getMaxHealth() - pvpEvent.getHeartsToLose());
            this.plugin.getStorage().save(playerData);
            LifeStealZ.setMaxHealth(player, playerData.getMaxHealth());
         }

         if (pvpEvent.isShouldDropHearts()) {
            this.dropHeartsNaturally(player.getLocation(), (int)(pvpEvent.getHeartsToLose() / 2.0), CustomItemManager.createKillHeart());
         } else if (pvpEvent.isKillerShouldGainHearts() && pvpEvent.getHeartsKillerGains() > 0.0) {
            this.handleKillerHeartGainDirect(killer, pvpEvent.getHeartsKillerGains());
         }

         if (pvpEvent.getDeathMessage() != null && !pvpEvent.getDeathMessage().equals(event.getDeathMessage())) {
            event.setDeathMessage(pvpEvent.getDeathMessage());
         }
      }
   }

   private void handleNaturalDeath(PlayerDeathEvent event, Player player, PlayerData playerData, double healthToLoose) {
      ZPlayerNaturalDeathEvent naturalEvent = new ZPlayerNaturalDeathEvent(event, healthToLoose);
      naturalEvent.setShouldDropHearts(this.plugin.getConfig().getBoolean("dropHeartsNatural"));
      Bukkit.getPluginManager().callEvent(naturalEvent);
      if (!naturalEvent.isCancelled()) {
         if (naturalEvent.getHeartsToLose() > 0.0) {
            playerData.setMaxHealth(playerData.getMaxHealth() - naturalEvent.getHeartsToLose());
            this.plugin.getStorage().save(playerData);
            LifeStealZ.setMaxHealth(player, playerData.getMaxHealth());
         }

         if (naturalEvent.isShouldDropHearts()) {
            this.dropHeartsNaturally(player.getLocation(), (int)(naturalEvent.getHeartsToLose() / 2.0), CustomItemManager.createNaturalDeathHeart());
         }

         if (!naturalEvent.getDeathMessage().equals(event.getDeathMessage())) {
            event.setDeathMessage(naturalEvent.getDeathMessage());
         }
      }
   }

   private void handleElimination(
      PlayerDeathEvent event,
      Player player,
      PlayerData playerData,
      Player killer,
      boolean isDeathByPlayer,
      double healthToLoose,
      boolean preventKillerGain,
      boolean droppedAtKiller
   ) {
      ZPlayerEliminationEvent eliminationEvent = new ZPlayerEliminationEvent(event, killer);
      eliminationEvent.setShouldBanPlayer(!this.plugin.getConfig().getBoolean("disablePlayerBanOnElimination"));
      eliminationEvent.setShouldAnnounceElimination(this.plugin.getConfig().getBoolean("announceElimination"));
      String messageKey = isDeathByPlayer ? "eliminationAnnouncement" : "eliminateionAnnouncementNature";
      String defaultMessage = isDeathByPlayer ? "&c%player% &7has been eliminated by &c%killer%&7!" : "&c%player% &7has been eliminated!";
      eliminationEvent.setEliminationMessage(
         MessageUtils.getAndFormatMsg(
            false,
            messageKey,
            defaultMessage,
            new MessageUtils.Replaceable("%player%", player.getName()),
            new MessageUtils.Replaceable("%killer%", killer != null ? killer.getName() : "")
         )
      );
      eliminationEvent.setKickMessage(MessageUtils.getAndFormatMsg(false, "eliminatedJoin", "&cYou don't have any hearts left!"));
      Bukkit.getPluginManager().callEvent(eliminationEvent);
      if (!eliminationEvent.isCancelled()) {
         List<String> elimCommands = this.plugin.getConfig().getStringList("eliminationCommands");
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            for (String command : elimCommands) {
               this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", player.getName()));
            }
         }, 1L);
         boolean heartRewardOnElimination = this.plugin.getConfig().getBoolean("heartRewardOnElimination", true);
         if (heartRewardOnElimination) {
            if (isDeathByPlayer && killer != null) {
               boolean dropHeartsPlayer = this.plugin.getConfig().getBoolean("dropHeartsPlayer", true);
               if (!preventKillerGain && !droppedAtKiller && !dropHeartsPlayer) {
                  boolean preventedByCooldown = this.handleHeartGainCooldown(event, player, killer, healthToLoose);
                  boolean preventedByMax = false;
                  if (!preventedByCooldown) {
                     preventedByMax = this.handleMaxHeartsLimit(event, player, killer, healthToLoose);
                  }

                  if (!preventedByCooldown && !preventedByMax) {
                     this.handleKillerHeartGainDirect(killer, healthToLoose);
                  }
               } else if (droppedAtKiller || dropHeartsPlayer) {
                  this.dropHeartsNaturally(killer.getLocation(), (int)(healthToLoose / 2.0), CustomItemManager.createKillHeart());
               }
            } else if (!isDeathByPlayer) {
               boolean dropHeartsNatural = this.plugin.getConfig().getBoolean("dropHeartsNatural", true);
               if (dropHeartsNatural) {
                  this.dropHeartsNaturally(player.getLocation(), (int)(healthToLoose / 2.0), CustomItemManager.createNaturalDeathHeart());
               }
            }
         }

         if (!eliminationEvent.isShouldBanPlayer()) {
            double respawnHP = (double)(this.plugin.getConfig().getInt("reviveHearts") * 2);
            playerData.setMaxHealth(respawnHP);
            this.plugin.getStorage().save(playerData);
            LifeStealZ.setMaxHealth(player, respawnHP);
            return;
         }

         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            if (player.isOnline()) {
               player.kick(eliminationEvent.getKickMessage());
            }
         }, 1L);
         if (eliminationEvent.isShouldAnnounceElimination()) {
            Bukkit.broadcast(eliminationEvent.getEliminationMessage());
            event.setDeathMessage(null);
         }

         this.plugin.getWebHookManager().sendWebhookMessage(WebHookManager.WebHookType.ELIMINATION, player.getName(), killer != null ? killer.getName() : "");
         playerData.setMaxHealth(0.0);
         this.plugin.getStorage().save(playerData);
         this.plugin.getEliminatedPlayersCache().addEliminatedPlayer(player.getName());
      }
   }

   private void handleKillerHeartGainDirect(Player killer, double healthGain) {
      PlayerData killerPlayerData = this.plugin.getStorage().load(killer.getUniqueId());
      killerPlayerData.setMaxHealth(killerPlayerData.getMaxHealth() + healthGain);
      this.plugin.getStorage().save(killerPlayerData);
      LifeStealZ.setMaxHealth(killer, killerPlayerData.getMaxHealth());
      killer.setHealth(Math.min(killer.getHealth() + healthGain, killerPlayerData.getMaxHealth()));
      CooldownManager.lastHeartGain.put(killer.getUniqueId(), System.currentTimeMillis());
   }

   private boolean handleAntiAltLogic(PlayerDeathEvent event, Player player, @Nullable Player killer) {
      if (killer != null && !player.getUniqueId().equals(killer.getUniqueId())) {
         String victimIP = this.getPlayerIP(player);
         String killerIP = this.getPlayerIP(killer);
         if (victimIP != null && victimIP.equals(killerIP) && this.plugin.getConfig().getBoolean("antiAlt.enabled")) {
            ZPlayerAltKillEvent altEvent = new ZPlayerAltKillEvent(event, killer, victimIP);
            altEvent.setShouldPreventKill(this.plugin.getConfig().getBoolean("antiAlt.preventKill"));
            altEvent.setShouldLogAttempt(this.plugin.getConfig().getBoolean("antiAlt.logAttempt"));
            altEvent.setShouldSendMessage(this.plugin.getConfig().getBoolean("antiAlt.sendMessage"));
            Bukkit.getPluginManager().callEvent(altEvent);
            if (!altEvent.isCancelled()) {
               if (altEvent.isShouldLogAttempt()) {
                  this.plugin
                     .getLogger()
                     .info(
                        "[ALT WARNING] Player "
                           + killer.getName()
                           + " tried to kill "
                           + player.getName()
                           + " with the same IP address! (Probably an alt account)"
                     );
               }

               if (altEvent.isShouldSendMessage()) {
                  killer.sendMessage(altEvent.getWarningMessage());
               }

               for (String command : this.plugin.getConfig().getStringList("antiAlt.commands")) {
                  this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command.replace("&player&", killer.getName()));
               }

               return altEvent.isShouldPreventKill();
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private String getPlayerIP(Player player) {
      InetSocketAddress inetSocketAddress = player.getAddress();
      if (inetSocketAddress == null) {
         return null;
      } else {
         InetAddress address = inetSocketAddress.getAddress();
         return address.getHostAddress();
      }
   }

   private void dropHeartsNaturally(Location location, int amount, ItemStack itemStack) {
      PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
      boolean shouldHaveUnlimitedLifetime = container.has(CustomItemManager.DESPAWNABLE_KEY)
         && !Boolean.TRUE.equals(container.get(CustomItemManager.DESPAWNABLE_KEY, PersistentDataType.BOOLEAN));
      boolean shouldBeInvulnerable = container.has(CustomItemManager.INVULNERABLE_KEY)
         && Boolean.TRUE.equals(container.get(CustomItemManager.INVULNERABLE_KEY, PersistentDataType.BOOLEAN));

      for (int i = 0; i < amount; i++) {
         Item item = location.getWorld().dropItemNaturally(location, itemStack);
         if (shouldHaveUnlimitedLifetime) {
            item.setUnlimitedLifetime(true);
         }

         if (shouldBeInvulnerable) {
            item.setInvulnerable(true);
         }
      }
   }

   private boolean restrictedHeartLossByGracePeriod(Player player) {
      GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
      return gracePeriodManager.isInGracePeriod(player) && !gracePeriodManager.getConfig().looseHearts();
   }

   private boolean restrictedHeartGainByGracePeriod(Player player) {
      GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
      return gracePeriodManager.isInGracePeriod(player) && !gracePeriodManager.getConfig().gainHearts();
   }

   private boolean restrictedHeartLossByBypass(Player player) {
      BypassManager bypassManager = this.plugin.getBypassManager();
      return bypassManager.hasBypass(player) && !bypassManager.getConfig().looseHearts();
   }

   private boolean restrictedHeartGainByBypass(Player player) {
      BypassManager bypassManager = this.plugin.getBypassManager();
      return bypassManager.hasBypass(player) && !bypassManager.getConfig().gainHearts();
   }
}
