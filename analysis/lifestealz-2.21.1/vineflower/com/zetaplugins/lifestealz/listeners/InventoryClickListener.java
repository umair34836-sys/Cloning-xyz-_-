package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.GuiManager;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.WebHookManager;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;
import com.zetaplugins.lifestealz.util.customblocks.CustomBlock;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomReviveBeaconItemData;
import com.zetaplugins.lifestealz.util.revive.ReviveTask;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@AutoRegisterListener
public final class InventoryClickListener implements Listener {
   private final LifeStealZ plugin;

   public InventoryClickListener(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      Inventory openInventory = player.getOpenInventory().getTopInventory();
      if (!this.handleRecipeGuiClick(event, player, openInventory)) {
         if (!this.handleReviveGuiClick(event, player, openInventory)) {
            if (!this.handleBeaconReviveGuiClick(event, player, openInventory)) {
               if (event.getCurrentItem() != null && CustomItemManager.isForbiddenItem(event.getCurrentItem())) {
                  event.setCancelled(true);
                  event.setCurrentItem(new ItemStack(Material.AIR));
               }
            }
         }
      }
   }

   private boolean handleRecipeGuiClick(InventoryClickEvent event, Player player, Inventory openInventory) {
      if (!openInventory.equals(GuiManager.RECIPE_GUI_MAP.get(player.getUniqueId()))) {
         return false;
      } else {
         event.setCancelled(true);
         ItemStack currentItem = event.getCurrentItem();
         if (currentItem != null && currentItem.getType() == Material.BARRIER) {
            player.closeInventory();
         }

         return true;
      }
   }

   private boolean handleReviveGuiClick(InventoryClickEvent event, Player player, Inventory openInventory) {
      if (!openInventory.equals(GuiManager.REVIVE_GUI_MAP.get(player.getUniqueId()))) {
         return false;
      } else {
         event.setCancelled(true);
         ItemStack item = event.getCurrentItem();
         if (item != null && item.getType() != Material.AIR) {
            switch (item.getType()) {
               case BARRIER:
                  player.closeInventory();
                  break;
               case ARROW:
                  GuiManager.openReviveGui(player, this.getPageFromItem(item));
                  break;
               case PLAYER_HEAD:
                  this.handleReviveClick(item, player, false);
                  break;
               case SKELETON_SKULL:
                  this.handleReviveClick(item, player, true);
            }

            return true;
         } else {
            return true;
         }
      }
   }

   private boolean handleBeaconReviveGuiClick(InventoryClickEvent event, Player player, Inventory openInventory) {
      if (!openInventory.equals(GuiManager.REVIVE_BEACON_GUI_MAP.get(player.getUniqueId()))) {
         return false;
      } else {
         Location beaconLocation = GuiManager.REVIVE_BEACON_INVENTORY_LOCATIONS.get(player.getUniqueId());
         if (beaconLocation == null) {
            player.sendMessage(Component.text("§cAn error occurred while fetching the beacon location! Please try again."));
            return false;
         } else {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() != Material.AIR) {
               switch (item.getType()) {
                  case BARRIER:
                     player.closeInventory();
                     break;
                  case ARROW:
                     GuiManager.openReviveBeaconGui(player, this.getPageFromItem(item), this.plugin, beaconLocation);
                     break;
                  case PLAYER_HEAD:
                     this.handleBeaconReviveClick(item, player, false, beaconLocation);
                     break;
                  case SKELETON_SKULL:
                     this.handleBeaconReviveClick(item, player, true, beaconLocation);
               }

               return true;
            } else {
               return true;
            }
         }
      }
   }

   private void handleReviveClick(ItemStack item, Player player, boolean bedrock) {
      if (!player.hasPermission("lifestealz.revive")) {
         this.throwPermissionError(player);
      } else {
         String uuidString = this.getLastLineOfLore(item, bedrock);
         if (uuidString != null) {
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(uuid);
            if (target.getName() == null) {
               player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"));
            } else if (!this.hasReviveCrystal(player)) {
               this.plugin.getLogger().warning("Player " + player.getName() + " tried to revive " + target.getName() + " without a revive crystal!");
            } else {
               this.revivePlayer(player, target, bedrock);
            }
         }
      }
   }

   private void handleBeaconReviveClick(ItemStack item, Player player, boolean bedrock, Location beaconLocation) {
      if (!player.hasPermission("lifestealz.revive")) {
         this.throwPermissionError(player);
      } else if (this.plugin.getReviveTaskManager().isReviving(beaconLocation)) {
         player.sendMessage(
            MessageUtils.getAndFormatMsg(
               false, "reviveBeaconAlreadyInUse", "&cThis revive beacon is already in use! Please wait until the current revive is finished."
            )
         );
      } else {
         String uuidString = this.getLastLineOfLore(item, bedrock);
         if (uuidString != null) {
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(uuid);
            if (target.getName() == null) {
               player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"));
            } else {
               boolean targetReviving = this.plugin.getReviveTaskManager().isRevivingTarget(target.getUniqueId());
               if (targetReviving) {
                  player.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        false,
                        "alreadyRevivingPlayer",
                        "&cThis player is already being revived by another beacon! Please wait until the current revive is finished."
                     )
                  );
                  player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  player.closeInventory();
               } else {
                  this.beaconRevivePlayer(player, target, bedrock, beaconLocation);
               }
            }
         }
      }
   }

   private boolean canRevivePlayer(Player reviver, OfflinePlayer target, PlayerData data) {
      int reviveMaximum = this.plugin.getConfig().getInt("maxRevives");
      int minHearts = this.plugin.getConfig().getInt("minHearts");
      if (reviveMaximum != -1 && data.getHasBeenRevived() >= reviveMaximum) {
         reviver.sendMessage(
            MessageUtils.getAndFormatMsg(
               false,
               "reviveMaxReached",
               "&cThis player has already been revived %amount% times!",
               new MessageUtils.Replaceable("%amount%", String.valueOf(data.getHasBeenRevived()))
            )
         );
         return false;
      } else if (data.getMaxHealth() > (double)(minHearts * 2)) {
         reviver.sendMessage(MessageUtils.getAndFormatMsg(false, "onlyReviveElimPlayers", "&cYou can only revive eliminated players!"));
         return false;
      } else {
         return true;
      }
   }

   private void applyReviveData(PlayerData data) {
      data.setMaxHealth((double)(this.plugin.getConfig().getInt("reviveHearts") * 2));
      data.setHasBeenRevived(data.getHasBeenRevived() + 1);
      this.plugin.getStorage().save(data);
   }

   private void executeReviveActions(Player reviver, OfflinePlayer target, String[] location) {
      this.plugin.getEliminatedPlayersCache().removeEliminatedPlayer(target.getName());
      reviver.sendMessage(
         MessageUtils.getAndFormatMsg(
            true, "reviveSuccess", "&7You successfully revived &c%player%&7!", new MessageUtils.Replaceable("%player%", target.getName())
         )
      );
      reviver.playSound(reviver.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500.0F, 1.0F);

      for (String command : this.plugin.getConfig().getStringList("reviveuseCommands")) {
         String finalCommand = command.replace("&player&", reviver.getName())
            .replace("&target&", target.getName())
            .replace("&location&", location[0] + ", " + location[1] + ", " + location[2])
            .replace("&locationX&", location[0])
            .replace("&locationY&", location[1])
            .replace("&locationZ&", location[2]);
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
      }

      this.plugin.getWebHookManager().sendWebhookMessage(WebHookManager.WebHookType.REVIVE, target.getName(), reviver.getName());
   }

   private void revivePlayer(Player reviver, OfflinePlayer target, boolean isBedrock) {
      PlayerData data = this.plugin.getStorage().load(target.getUniqueId());
      if (this.canRevivePlayer(reviver, target, data)) {
         String[] locationNull = new String[]{"null", "null", "null"};
         this.applyReviveData(data);
         this.executeReviveActions(reviver, target, locationNull);
         this.removeReviveCrystal(reviver);
         reviver.closeInventory();
      }
   }

   private void beaconRevivePlayer(final Player reviver, final OfflinePlayer target, boolean isBedrock, final Location beaconLocation) {
      final PlayerData data = this.plugin.getStorage().load(target.getUniqueId());
      String customItemId = CustomBlock.REVIVE_BEACON.getCustomItemId(beaconLocation.getBlock());

      CustomReviveBeaconItemData itemData;
      try {
         itemData = new CustomReviveBeaconItemData(customItemId);
      } catch (IllegalArgumentException var12) {
         return;
      }

      if (this.canRevivePlayer(reviver, target, data)) {
         final String[] location = new String[]{
            String.valueOf(beaconLocation.getBlockX()), String.valueOf(beaconLocation.getBlockY()), String.valueOf(beaconLocation.getBlockZ())
         };
         reviver.sendMessage(
            MessageUtils.getAndFormatMsg(
               true,
               "reviveBeaconStart",
               "&c%player% &7will be revived in &c%seconds% seconds&7! Please wait...",
               new MessageUtils.Replaceable("%player%", target.getName()),
               new MessageUtils.Replaceable("%seconds%", String.valueOf(itemData.getReviveTime()))
            )
         );
         reviver.closeInventory();

         for (String command : this.plugin.getConfig().getStringList("reviveStartCommands")) {
            String finalCommand = command.replace("&player&", reviver.getName())
               .replace("&target&", target.getName())
               .replace("&location&", location[0] + ", " + location[1] + ", " + location[2])
               .replace("&locationX&", location[0])
               .replace("&locationY&", location[1])
               .replace("&locationZ&", location[2]);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
         }

         this.plugin
            .getReviveBeaconEffectManager()
            .startRevivingEffects(
               beaconLocation,
               target.getName(),
               itemData.shouldShowLaser(),
               itemData.shouldShowParticleRing(),
               itemData.getParticleColor(),
               itemData.getInnerLaser(),
               itemData.getOuterLaser(),
               itemData.getReviveTime()
            );
         BukkitTask reviveTask = (new BukkitRunnable() {
            public void run() {
               InventoryClickListener.this.applyReviveData(data);
               InventoryClickListener.this.executeReviveActions(reviver, target, location);
               InventoryClickListener.this.plugin.getReviveTaskManager().removeReviveTask(beaconLocation);
               InventoryClickListener.this.plugin.getReviveBeaconEffectManager().clearAllEffects(beaconLocation);
               beaconLocation.getBlock().setType(Material.AIR);
               beaconLocation.getWorld().playSound(beaconLocation, Sound.ENTITY_PLAYER_LEVELUP, 500.0F, 1.0F);
            }
         }).runTaskLater(this.plugin, (long)itemData.getReviveTime() * 20L);
         this.plugin
            .getReviveTaskManager()
            .addReviveTask(
               beaconLocation,
               new ReviveTask(
                  beaconLocation, reviveTask, reviver.getUniqueId(), target.getUniqueId(), System.currentTimeMillis() / 1000L, itemData.getReviveTime()
               )
            );
      }
   }

   private int getPageFromItem(ItemStack item) {
      Integer pageInt = (Integer)item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.REVIVE_PAGE_KEY, PersistentDataType.INTEGER);
      return pageInt != null ? pageInt : 1;
   }

   private void throwPermissionError(HumanEntity player) {
      CommandUtils.throwPermissionError(player);
   }

   private String getLastLineOfLore(ItemStack item, boolean bedrock) {
      PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
      List<Component> lore = item.lore();
      if (lore != null && lore.size() >= (bedrock ? 2 : 1)) {
         int line = bedrock ? 2 : 1;
         Component lastLore = lore.get(lore.size() - line);
         return plainSerializer.serialize(lastLore);
      } else {
         return null;
      }
   }

   private boolean hasReviveCrystal(Player player) {
      for (ItemStack item : player.getInventory().getContents()) {
         if (item != null && CustomItemManager.isReviveItem(item)) {
            return true;
         }
      }

      return false;
   }

   private void removeReviveCrystal(Player player) {
      for (ItemStack item : player.getInventory().getContents()) {
         if (item != null && CustomItemManager.isReviveItem(item)) {
            item.setAmount(item.getAmount() - 1);
            return;
         }
      }
   }
}
