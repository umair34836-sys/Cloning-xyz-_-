// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.InteractionListener
class InteractionListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public InteractionListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private void handleHeartItem(ItemStack item, Player player, EquipmentSlot hand, PlayerInteractEvent event) {
        String customItemId = CustomItemManager.getCustomItemId(item);
        try {
            CustomHeartItemData customItemData = new CustomHeartItemData(customItemId);
            event.setCancelled(true);
            String world = player.getWorld().getName();
            if (!customItemData.isAllowedInWorld(world)) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "noItemUseInWorld", "&cYou cannot use this item in this world!", new MessageUtils.Replaceable[0]));
                return;
            }
            if (customItemData.requiresPermission() && !player.hasPermission(customItemData.getPermission()) && !player.isOp() && !player.hasPermission("lifestealz.item.*")) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!", new MessageUtils.Replaceable[0]));
                return;
            }
            if (restrictedHeartUseByBypass(player)) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "noHeartUseWithBypass", "&cYou can't use hearts with bypass permission!", new MessageUtils.Replaceable[0]));
                return;
            }
            if (restrictedHeartByGracePeriod(player)) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "noHeartUseInGracePeriod", "&cYou can't use hearts during the grace period!", new MessageUtils.Replaceable[0]));
                return;
            }
            long heartCooldown = this.plugin.getConfig().getLong("heartCooldown");
            if (CooldownManager.lastHeartUse.get(player.getUniqueId()) != null && CooldownManager.lastHeartUse.get(player.getUniqueId()).longValue() + heartCooldown > System.currentTimeMillis()) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "heartconsumeCooldown", "&cYou have to wait before using another heart!", new MessageUtils.Replaceable[0]));
                return;
            }
            PlayerData playerData = this.plugin.getStorage().load(player.getUniqueId());
            if (playerData == null) {
                this.plugin.getLogger().severe("PlayerData not found for player " + player.getName() + " on death event. This should not happen!");
                return;
            }
            Integer savedHeartAmountInteger = item.getItemMeta().getPersistentDataContainer().has(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER) ? (Integer) item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER) : 1;
            int savedHeartAmount = savedHeartAmountInteger != null ? savedHeartAmountInteger.intValue() : 1;
            double heartsToAdd = savedHeartAmount * 2;
            double newHearts = playerData.getMaxHealth() + heartsToAdd;
            double maxHearts = MaxHeartsManager.getMaxHearts(player, this.plugin.getConfig());
            if (newHearts > maxHearts) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new MessageUtils.Replaceable("%limit%", Integer.toString(((int) maxHearts) / 2))));
                return;
            }
            if (playerData.getMaxHealth() < customItemData.getMinHearts() * 2 && customItemData.getMinHearts() != -1) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "itemMinHearts", "&cYou need at least %amount% hearts to use this item!", new MessageUtils.Replaceable("%amount%", Integer.toString(customItemData.getMinHearts()))));
                return;
            }
            if (playerData.getMaxHealth() >= customItemData.getMaxHearts() * 2 && customItemData.getMaxHearts() != -1) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "itemMaxHearts", "&cYou can't use this item with more than %amount% hearts!", new MessageUtils.Replaceable("%amount%", Integer.toString(customItemData.getMaxHearts()))));
                return;
            }
            if (hand == EquipmentSlot.HAND) {
                updateItemInHand(player, item, player.getInventory().getHeldItemSlot());
            } else if (hand == EquipmentSlot.OFF_HAND) {
                updateItemInHand(player, item, 40);
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
                    player.playSound(player.getLocation(), sound.getSound(), (float) sound.getVolume(), (float) sound.getPitch());
                }
            }
            List<String> heartuseCommands = this.plugin.getConfig().getStringList("heartuseCommands");
            for (String command : heartuseCommands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&player&", player.getName()));
            }
            if (this.plugin.getConfig().getBoolean("playTotemEffect")) {
                playHeartAnimation(player);
            }
            player.sendMessage(MessageUtils.getAndFormatMsg(true, "heartconsume", "&7Consumed a heart and got &c%amount% &7hearts!", new MessageUtils.Replaceable("%amount%", savedHeartAmount)));
            CooldownManager.lastHeartUse.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        } catch (IllegalArgumentException e) {
        }
    }

    private void handleReviveItem(ItemStack item, Player player, EquipmentSlot hand, PlayerInteractEvent event) {
        String customItemId = CustomItemManager.getCustomItemId(item);
        try {
            CustomItemData customItemData = new CustomItemData(customItemId);
            event.setCancelled(true);
            String world = player.getWorld().getName();
            if (!customItemData.isAllowedInWorld(world)) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "noItemUseInWorld", "&cYou cannot use this item in this world!", new MessageUtils.Replaceable[0]));
            } else {
                GuiManager.openReviveGui(player, 1);
            }
        } catch (IllegalArgumentException e) {
        }
    }

    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        if (shouldCancelRespawnAnchorUsage(event) || shouldCancelBedInteraction(event)) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "interactionNotAllowed", "&cYou are not allowed to interact with this!", new MessageUtils.Replaceable[0]));
            event.setCancelled(true);
            return;
        }
        if (event.getAction().isRightClick() && item != null) {
            if (CustomItemManager.isForbiddenItem(item)) {
                event.setCancelled(true);
                if (hand != null) {
                    switch (AnonymousClass2.$SwitchMap$org$bukkit$inventory$EquipmentSlot[hand.ordinal()]) {
                        case 1:
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
                            break;
                        case 2:
                            player.getInventory().setItem(40, new ItemStack(Material.AIR));
                            break;
                    }
                } else {
                    return;
                }
            }
            if (CustomItemType.NONUSABLE.is(item)) {
                event.setCancelled(true);
                return;
            }
            if (CustomItemType.HEART.is(item)) {
                handleHeartItem(item, player, hand, event);
            }
            if (CustomItemType.REVIVE.is(item)) {
                handleReviveItem(item, player, hand, event);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [com.zetaplugins.lifestealz.listeners.InteractionListener$1] */
    private void playHeartAnimation(final Player player) {
        final ItemStack originalOffHandItem = player.getInventory().getItemInOffHand();
        ItemStack fakeTotem = CustomItemManager.createHeartAnimationTotem();
        player.getInventory().setItemInOffHand(fakeTotem);
        new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.listeners.InteractionListener.1
            public void run() {
                player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);
                player.getInventory().setItemInOffHand(originalOffHandItem);
            }
        }.runTaskLater(this.plugin, 3L);
    }

    private boolean restrictedHeartByGracePeriod(Player player) {
        GracePeriodManager gracePeriodManager = this.plugin.getGracePeriodManager();
        return gracePeriodManager.isInGracePeriod(player) && !gracePeriodManager.getConfig().useHearts();
    }

    private boolean restrictedHeartUseByBypass(Player player) {
        return this.plugin.getBypassManager().hasBypass(player) && !this.plugin.getBypassManager().getConfig().useHearts();
    }

    private boolean shouldCancelBedInteraction(PlayerInteractEvent event) {
        List<World.Environment> disabledEnvironments = List.of(World.Environment.NETHER, World.Environment.THE_END);
        List<Material> disabledMaterials = List.of((Object[]) new Material[]{Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED, Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED, Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED, Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED});
        Block block = event.getClickedBlock();
        return block != null && this.plugin.getConfig().getBoolean("preventBeds") && event.getAction() == Action.RIGHT_CLICK_BLOCK && disabledEnvironments.contains(event.getPlayer().getWorld().getEnvironment()) && disabledMaterials.contains(block.getType());
    }

    private boolean shouldCancelRespawnAnchorUsage(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !this.plugin.getConfig().getBoolean("preventRespawnAnchors")) {
            return false;
        }
        Block block = event.getClickedBlock();
        List<World.Environment> disabledEnvironments = List.of(World.Environment.NORMAL, World.Environment.THE_END);
        return block != null && block.getType() == Material.RESPAWN_ANCHOR && disabledEnvironments.contains(event.getPlayer().getWorld().getEnvironment());
    }

    private void updateItemInHand(Player player, ItemStack item, int slot) {
        ItemStack updatedItem = item.clone();
        updatedItem.setAmount(item.getAmount() - 1);
        if (updatedItem.getAmount() > 0) {
            updatedItem.setItemMeta(item.getItemMeta());
        }
        player.getInventory().setItem(slot, updatedItem);
    }

    // rea: class com.zetaplugins.lifestealz.listeners.InteractionListener.AnonymousClass1
    class AnonymousClass1 {
        com.zetaplugins.lifestealz.listeners.InteractionListener this$0;
        org.bukkit.inventory.ItemStack val$originalOffHandItem;
        org.bukkit.entity.Player val$player;

        // rea: <init>(com.zetaplugins.lifestealz.listeners.InteractionListener, org.bukkit.entity.Player, org.bukkit.inventory.ItemStack): void -> smali (java_body_unavailable)
        // .method <init>(Lcom/zetaplugins/lifestealz/listeners/InteractionListener;Lorg/bukkit/entity/Player;Lorg/bukkit/inventory/ItemStack;)V
        //     .param final mandated null
        //     .param final synthetic null
        //     .param final synthetic null
        //     .max stack 2
        //     .max locals 4
        // 
        //     .local 0 "this" Lcom/zetaplugins/lifestealz/listeners/InteractionListener$1;
        //     .local 1 "this$0" Lcom/zetaplugins/lifestealz/listeners/InteractionListener;
        //     .line 249
        //     aload 0
        //     aload 1
        //     putfield com/zetaplugins/lifestealz/listeners/InteractionListener$1 this$0 Lcom/zetaplugins/lifestealz/listeners/InteractionListener;
        //     aload 0
        //     aload 2
        //     putfield com/zetaplugins/lifestealz/listeners/InteractionListener$1 val$player Lorg/bukkit/entity/Player;
        //     aload 0
        //     aload 3
        //     putfield com/zetaplugins/lifestealz/listeners/InteractionListener$1 val$originalOffHandItem Lorg/bukkit/inventory/ItemStack;
        //     aload 0
        //     invokespecial org/bukkit/scheduler/BukkitRunnable <init> ()V
        //     return
        // .end method

        /* JADX WARN: Type inference failed for: r0v6, types: [com.zetaplugins.lifestealz.listeners.InteractionListener$1] */
        private void playHeartAnimation(final Player player) {
            final ItemStack originalOffHandItem = player.getInventory().getItemInOffHand();
            ItemStack fakeTotem = CustomItemManager.createHeartAnimationTotem();
            player.getInventory().setItemInOffHand(fakeTotem);
            new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.listeners.InteractionListener.1
                public void run() {
                    player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);
                    player.getInventory().setItemInOffHand(originalOffHandItem);
                }

    }

    // rea: class com.zetaplugins.lifestealz.listeners.InteractionListener.AnonymousClass2
    class AnonymousClass2 {
        int[] $SwitchMap$org$bukkit$inventory$EquipmentSlot;

        // rea: m28clinit(): void -> smali (static_initializer_disassembly)
        // .method static <clinit>()V
        //     .max stack 3
        //     .max locals 1
        // 
        //     .line 55
        //     invokestatic org/bukkit/inventory/EquipmentSlot values ()[Lorg/bukkit/inventory/EquipmentSlot;
        //     arraylength
        //     newarray 10
        //     putstatic com/zetaplugins/lifestealz/listeners/InteractionListener$2 $SwitchMap$org$bukkit$inventory$EquipmentSlot [I
        //   :L0
        //     getstatic com/zetaplugins/lifestealz/listeners/InteractionListener$2 $SwitchMap$org$bukkit$inventory$EquipmentSlot [I
        //     getstatic org/bukkit/inventory/EquipmentSlot HAND Lorg/bukkit/inventory/EquipmentSlot;
        //     invokevirtual org/bukkit/inventory/EquipmentSlot ordinal ()I
        //     iconst_1
        //     iastore
        //   :L1
        //     .catch java/lang/NoSuchFieldError :L0 .. :L1 goto :L2
        //     goto :L3
        //   :L2
        //     .stack same1 java/lang/NoSuchFieldError
        //     astore 0
        //   :L3
        //     .stack same
        //     getstatic com/zetaplugins/lifestealz/listeners/InteractionListener$2 $SwitchMap$org$bukkit$inventory$EquipmentSlot [I
        //     getstatic org/bukkit/inventory/EquipmentSlot OFF_HAND Lorg/bukkit/inventory/EquipmentSlot;
        //     invokevirtual org/bukkit/inventory/EquipmentSlot ordinal ()I
        //     iconst_2
        //     iastore
        //   :L4
        //     .catch java/lang/NoSuchFieldError :L3 .. :L4 goto :L5
        //     goto :L7
        //   :L5
        //     .stack same1 java/lang/NoSuchFieldError
        //     astore 0
        //   :L7
        //     .stack same
        //     return
        // .end method

    }

}
