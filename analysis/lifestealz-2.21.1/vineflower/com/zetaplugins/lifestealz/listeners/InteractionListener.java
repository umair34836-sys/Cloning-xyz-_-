package com.zetaplugins.lifestealz.listeners;

import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.storage.PlayerData;
import com.zetaplugins.lifestealz.util.CooldownManager;
import com.zetaplugins.lifestealz.util.GracePeriodManager;
import com.zetaplugins.lifestealz.util.GuiManager;
import com.zetaplugins.lifestealz.util.MaxHeartsManager;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;
import com.zetaplugins.lifestealz.util.customitems.CustomItemType;
import com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomHeartItemData;
import com.zetaplugins.lifestealz.util.customitems.customitemdata.CustomItemData;
import com.zetaplugins.zetacore.annotations.AutoRegisterListener;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

@AutoRegisterListener
public final class InteractionListener implements Listener {
   private final LifeStealZ plugin;

   public InteractionListener(LifeStealZ plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerInteraction(PlayerInteractEvent event) {
      ItemStack item = event.getItem();
      Player player = event.getPlayer();
      EquipmentSlot hand = event.getHand();
      if (!this.shouldCancelRespawnAnchorUsage(event) && !this.shouldCancelBedInteraction(event)) {
         if (event.getAction().isRightClick() && item != null) {
            if (CustomItemManager.isForbiddenItem(item)) {
               event.setCancelled(true);
               if (hand == null) {
                  return;
               }

               switch (hand) {
                  case HAND:
                     player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
                     break;
                  case OFF_HAND:
                     player.getInventory().setItem(40, new ItemStack(Material.AIR));
               }
            }

            if (CustomItemType.NONUSABLE.is(item)) {
               event.setCancelled(true);
               return;
            }

            if (CustomItemType.HEART.is(item)) {
               this.handleHeartItem(item, player, hand, event);
            }

            if (CustomItemType.REVIVE.is(item)) {
               this.handleReviveItem(item, player, hand, event);
            }
         }
      } else {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "interactionNotAllowed", "&cYou are not allowed to interact with this!"));
         event.setCancelled(true);
      }
   }

   private boolean shouldCancelRespawnAnchorUsage(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.plugin.getConfig().getBoolean("preventRespawnAnchors")) {
         Block block = event.getClickedBlock();
         List<Environment> disabledEnvironments = List.of(Environment.NORMAL, Environment.THE_END);
         return block != null && block.getType() == Material.RESPAWN_ANCHOR && disabledEnvironments.contains(event.getPlayer().getWorld().getEnvironment());
      } else {
         return false;
      }
   }

   private boolean shouldCancelBedInteraction(PlayerInteractEvent event) {
      List<Environment> disabledEnvironments = List.of(Environment.NETHER, Environment.THE_END);
      List<Material> disabledMaterials = List.of(
         Material.BLACK_BED,
         Material.BLUE_BED,
         Material.BROWN_BED,
         Material.CYAN_BED,
         Material.GRAY_BED,
         Material.GREEN_BED,
         Material.LIGHT_BLUE_BED,
         Material.LIGHT_GRAY_BED,
         Material.LIME_BED,
         Material.MAGENTA_BED,
         Material.ORANGE_BED,
         Material.PINK_BED,
         Material.PURPLE_BED,
         Material.RED_BED,
         Material.WHITE_BED,
         Material.YELLOW_BED
      );
      Block block = event.getClickedBlock();
      return block != null && this.plugin.getConfig().getBoolean("preventBeds")
         ? event.getAction() == Action.RIGHT_CLICK_BLOCK
            && disabledEnvironments.contains(event.getPlayer().getWorld().getEnvironment())
            && disabledMaterials.contains(block.getType())
         : false;
   }

   private void handleHeartItem(ItemStack item, Player player, EquipmentSlot hand, PlayerInteractEvent event) {
      String customItemId = CustomItemManager.getCustomItemId(item);

      CustomHeartItemData customItemData;
      try {
         customItemData = new CustomHeartItemData(customItemId);
      } catch (IllegalArgumentException var23) {
         return;
      }

      event.setCancelled(true);
      String world = player.getWorld().getName();
      if (!customItemData.isAllowedInWorld(world)) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "noItemUseInWorld", "&cYou cannot use this item in this world!"));
      } else if (customItemData.requiresPermission()
         && !player.hasPermission(customItemData.getPermission())
         && !player.isOp()
         && !player.hasPermission("lifestealz.item.*")) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!"));
      } else if (this.restrictedHeartUseByBypass(player)) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "noHeartUseWithBypass", "&cYou can't use hearts with bypass permission!"));
      } else if (this.restrictedHeartByGracePeriod(player)) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "noHeartUseInGracePeriod", "&cYou can't use hearts during the grace period!"));
      } else {
         long heartCooldown = this.plugin.getConfig().getLong("heartCooldown");
         if (CooldownManager.lastHeartUse.get(player.getUniqueId()) != null
            && CooldownManager.lastHeartUse.get(player.getUniqueId()) + heartCooldown > System.currentTimeMillis()) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "heartconsumeCooldown", "&cYou have to wait before using another heart!"));
         } else {
            PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
            if (playerData == null) {
               this.plugin.getLogger().severe("PlayerData not found for player " + player.getName() + " on death event. This should not happen!");
            } else {
               Integer savedHeartAmountInteger = item.getItemMeta()
                     .getPersistentDataContainer()
                     .has(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER)
                  ? (Integer)item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER)
                  : 1;
               int savedHeartAmount = savedHeartAmountInteger != null ? savedHeartAmountInteger : 1;
               double heartsToAdd = (double)(savedHeartAmount * 2);
               double newHearts = playerData.getMaxHealth() + heartsToAdd;
               double maxHearts = MaxHeartsManager.getMaxHearts(player, this.plugin.getConfig());
               if (newHearts > maxHearts) {
                  player.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        false,
                        "maxHeartLimitReached",
                        "&cYou already reached the limit of %limit% hearts!",
                        new MessageUtils.Replaceable("%limit%", Integer.toString((int)maxHearts / 2))
                     )
                  );
               } else if (playerData.getMaxHealth() < (double)(customItemData.getMinHearts() * 2) && customItemData.getMinHearts() != -1) {
                  player.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        false,
                        "itemMinHearts",
                        "&cYou need at least %amount% hearts to use this item!",
                        new MessageUtils.Replaceable("%amount%", Integer.toString(customItemData.getMinHearts()))
                     )
                  );
               } else if (playerData.getMaxHealth() >= (double)(customItemData.getMaxHearts() * 2) && customItemData.getMaxHearts() != -1) {
                  player.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        false,
                        "itemMaxHearts",
                        "&cYou can't use this item with more than %amount% hearts!",
                        new MessageUtils.Replaceable("%amount%", Integer.toString(customItemData.getMaxHearts()))
                     )
                  );
               } else {
                  if (hand == EquipmentSlot.HAND) {
                     this.updateItemInHand(player, item, player.getInventory().getHeldItemSlot());
                  } else if (hand == EquipmentSlot.OFF_HAND) {
                     this.updateItemInHand(player, item, 40);
                  }

                  playerData.setMaxHealth(newHearts);
                  this.plugin.getStorage().save(playerData);
                  LifeStealZ.setMaxHealth(player, newHearts);
                  if (this.plugin.getConfig().getBoolean("healOnHeartUse")) {
                     player.setHealth(Math.min(player.getHealth() + heartsToAdd, newHearts));
                  }

                  String customItemID = CustomItemManager.getCustomItemId(item);
                  if (customItemID != null) {
                     CustomItemData.CustomItemSoundData sound = CustomItemManager.getCustomItemData(customItemID).getSound();
                     if (sound.isEnabled()) {
                        player.playSound(player.getLocation(), sound.getSound(), (float)sound.getVolume(), (float)sound.getPitch());
                     }
                  }

                  for (String command : this.plugin.getConfig().getStringList("heartuseCommands")) {
                     Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&player&", player.getName()));
                  }

                  if (this.plugin.getConfig().getBoolean("playTotemEffect")) {
                     this.playHeartAnimation(player);
                  }

                  player.sendMessage(
                     MessageUtils.getAndFormatMsg(
                        true,
                        "heartconsume",
                        "&7Consumed a heart and got &c%amount% &7hearts!",
                        new MessageUtils.Replaceable("%amount%", savedHeartAmount + "")
                     )
                  );
                  CooldownManager.lastHeartUse.put(player.getUniqueId(), System.currentTimeMillis());
               }
            }
         }
      }
   }

   private void handleReviveItem(ItemStack item, Player player, EquipmentSlot hand, PlayerInteractEvent event) {
      String customItemId = CustomItemManager.getCustomItemId(item);

      CustomItemData customItemData;
      try {
         customItemData = new CustomItemData(customItemId);
      } catch (IllegalArgumentException var8) {
         return;
      }

      event.setCancelled(true);
      String world = player.getWorld().getName();
      if (!customItemData.isAllowedInWorld(world)) {
         player.sendMessage(MessageUtils.getAndFormatMsg(false, "noItemUseInWorld", "&cYou cannot use this item in this world!"));
      } else {
         GuiManager.openReviveGui(player, 1);
      }
   }

   private void playHeartAnimation(final Player player) {
      final ItemStack originalOffHandItem = player.getInventory().getItemInOffHand();
      ItemStack fakeTotem = CustomItemManager.createHeartAnimationTotem();
      player.getInventory().setItemInOffHand(fakeTotem);
      (new BukkitRunnable() {
         public void run() {
            player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);
            player.getInventory().setItemInOffHand(originalOffHandItem);
         }
      }).runTaskLater(this.plugin, 3L);
   }

   private void updateItemInHand(Player player, ItemStack item, int slot) {
      ItemStack updatedItem = item.clone();
      updatedItem.setAmount(item.getAmount() - 1);
      if (updatedItem.getAmount() > 0) {
         updatedItem.setItemMeta(item.getItemMeta());
      }

      player.getInventory().setItem(slot, updatedItem);
   }

   private boolean restrictedHeartByGracePeriod(Player player) {
      GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
      return gracePeriodManager.isInGracePeriod(player) && !gracePeriodManager.getConfig().useHearts();
   }

   private boolean restrictedHeartUseByBypass(Player player) {
      return this.plugin.getBypassManager().hasBypass(player) && !this.plugin.getBypassManager().getConfig().useHearts();
   }
}
