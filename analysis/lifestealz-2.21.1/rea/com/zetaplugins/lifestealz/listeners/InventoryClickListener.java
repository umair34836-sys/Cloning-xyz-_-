// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.listeners;
// rea: class com.zetaplugins.lifestealz.listeners.InventoryClickListener
class InventoryClickListener {
    com.zetaplugins.lifestealz.LifeStealZ plugin;

    public InventoryClickListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private void applyReviveData(PlayerData data) {
        data.setMaxHealth(this.plugin.getConfig().getInt("reviveHearts") * 2);
        data.setHasBeenRevived(data.getHasBeenRevived() + 1);
        this.plugin.getStorage().save(data);
    }

    /* JADX WARN: Type inference failed for: r0v23, types: [com.zetaplugins.lifestealz.listeners.InventoryClickListener$1] */
    private void beaconRevivePlayer(final Player reviver, final OfflinePlayer target, boolean isBedrock, final Location beaconLocation) {
        final PlayerData data = this.plugin.getStorage().load(target.getUniqueId());
        String customItemId = CustomBlock.REVIVE_BEACON.getCustomItemId(beaconLocation.getBlock());
        try {
            CustomReviveBeaconItemData itemData = new CustomReviveBeaconItemData(customItemId);
            if (canRevivePlayer(reviver, target, data)) {
                final String[] location = {String.valueOf(beaconLocation.getBlockX()), String.valueOf(beaconLocation.getBlockY()), String.valueOf(beaconLocation.getBlockZ())};
                reviver.sendMessage(MessageUtils.getAndFormatMsg(true, "reviveBeaconStart", "&c%player% &7will be revived in &c%seconds% seconds&7! Please wait...", new MessageUtils.Replaceable("%player%", target.getName()), new MessageUtils.Replaceable("%seconds%", String.valueOf(itemData.getReviveTime()))));
                reviver.closeInventory();
                for (String command : this.plugin.getConfig().getStringList("reviveStartCommands")) {
                    String finalCommand = command.replace("&player&", reviver.getName()).replace("&target&", target.getName()).replace("&location&", location[0] + ", " + location[1] + ", " + location[2]).replace("&locationX&", location[0]).replace("&locationY&", location[1]).replace("&locationZ&", location[2]);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                }
                this.plugin.getReviveBeaconEffectManager().startRevivingEffects(beaconLocation, target.getName(), itemData.shouldShowLaser(), itemData.shouldShowParticleRing(), itemData.getParticleColor(), itemData.getInnerLaser(), itemData.getOuterLaser(), itemData.getReviveTime());
                BukkitTask reviveTask = new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.listeners.InventoryClickListener.1
                    public void run() {
                        InventoryClickListener.this.applyReviveData(data);
                        InventoryClickListener.this.executeReviveActions(reviver, target, location);
                        InventoryClickListener.this.plugin.getReviveTaskManager().removeReviveTask(beaconLocation);
                        InventoryClickListener.this.plugin.getReviveBeaconEffectManager().clearAllEffects(beaconLocation);
                        beaconLocation.getBlock().setType(Material.AIR);
                        beaconLocation.getWorld().playSound(beaconLocation, Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f);
                    }
                }.runTaskLater(this.plugin, ((long) itemData.getReviveTime()) * 20);
                this.plugin.getReviveTaskManager().addReviveTask(beaconLocation, new ReviveTask(beaconLocation, reviveTask, reviver.getUniqueId(), target.getUniqueId(), System.currentTimeMillis() / 1000, itemData.getReviveTime()));
            }
        } catch (IllegalArgumentException e) {
        }
    }

    private boolean canRevivePlayer(Player reviver, OfflinePlayer target, PlayerData data) {
        int reviveMaximum = this.plugin.getConfig().getInt("maxRevives");
        int minHearts = this.plugin.getConfig().getInt("minHearts");
        if (reviveMaximum != -1 && data.getHasBeenRevived() >= reviveMaximum) {
            reviver.sendMessage(MessageUtils.getAndFormatMsg(false, "reviveMaxReached", "&cThis player has already been revived %amount% times!", new MessageUtils.Replaceable("%amount%", String.valueOf(data.getHasBeenRevived()))));
            return false;
        }
        if (data.getMaxHealth() > minHearts * 2) {
            reviver.sendMessage(MessageUtils.getAndFormatMsg(false, "onlyReviveElimPlayers", "&cYou can only revive eliminated players!", new MessageUtils.Replaceable[0]));
            return false;
        }
        return true;
    }

    private void executeReviveActions(Player reviver, OfflinePlayer target, String[] location) {
        this.plugin.getEliminatedPlayersCache().removeEliminatedPlayer(target.getName());
        reviver.sendMessage(MessageUtils.getAndFormatMsg(true, "reviveSuccess", "&7You successfully revived &c%player%&7!", new MessageUtils.Replaceable("%player%", target.getName())));
        reviver.playSound(reviver.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f);
        for (String command : this.plugin.getConfig().getStringList("reviveuseCommands")) {
            String finalCommand = command.replace("&player&", reviver.getName()).replace("&target&", target.getName()).replace("&location&", location[0] + ", " + location[1] + ", " + location[2]).replace("&locationX&", location[0]).replace("&locationY&", location[1]).replace("&locationZ&", location[2]);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        }
        this.plugin.getWebHookManager().sendWebhookMessage(WebHookManager.WebHookType.REVIVE, target.getName(), reviver.getName());
    }

    private String getLastLineOfLore(ItemStack item, boolean bedrock) {
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
        List<Component> lore = item.lore();
        if (lore == null) {
            return null;
        }
        if (lore.size() < (bedrock ? 2 : 1)) {
            return null;
        }
        int line = bedrock ? 2 : 1;
        Component lastLore = lore.get(lore.size() - line);
        return plainSerializer.serialize(lastLore);
    }

    private int getPageFromItem(ItemStack item) {
        Integer pageInt = (Integer) item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.REVIVE_PAGE_KEY, PersistentDataType.INTEGER);
        if (pageInt != null) {
            return pageInt.intValue();
        }
        return 1;
    }

    private void handleBeaconReviveClick(ItemStack item, Player player, boolean bedrock, Location beaconLocation) {
        if (!player.hasPermission("lifestealz.revive")) {
            throwPermissionError(player);
            return;
        }
        if (this.plugin.getReviveTaskManager().isReviving(beaconLocation)) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "reviveBeaconAlreadyInUse", "&cThis revive beacon is already in use! Please wait until the current revive is finished.", new MessageUtils.Replaceable[0]));
            return;
        }
        String uuidString = getLastLineOfLore(item, bedrock);
        if (uuidString == null) {
            return;
        }
        UUID uuid = UUID.fromString(uuidString);
        OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(uuid);
        if (target.getName() == null) {
            player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"));
            return;
        }
        boolean targetReviving = this.plugin.getReviveTaskManager().isRevivingTarget(target.getUniqueId());
        if (targetReviving) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "alreadyRevivingPlayer", "&cThis player is already being revived by another beacon! Please wait until the current revive is finished.", new MessageUtils.Replaceable[0]));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.closeInventory();
            return;
        }
        beaconRevivePlayer(player, target, bedrock, beaconLocation);
    }

    private boolean handleBeaconReviveGuiClick(InventoryClickEvent event, Player player, Inventory openInventory) {
        if (!openInventory.equals(GuiManager.REVIVE_BEACON_GUI_MAP.get(player.getUniqueId()))) {
            return false;
        }
        Location beaconLocation = GuiManager.REVIVE_BEACON_INVENTORY_LOCATIONS.get(player.getUniqueId());
        if (beaconLocation == null) {
            player.sendMessage(Component.text("§cAn error occurred while fetching the beacon location! Please try again."));
            return false;
        }
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return true;
        }
        switch (AnonymousClass2.$SwitchMap$org$bukkit$Material[item.getType().ordinal()]) {
            case 1:
                player.closeInventory();
                return true;
            case 2:
                GuiManager.openReviveBeaconGui(player, getPageFromItem(item), this.plugin, beaconLocation);
                return true;
            case 3:
                handleBeaconReviveClick(item, player, false, beaconLocation);
                return true;
            case 4:
                handleBeaconReviveClick(item, player, true, beaconLocation);
                return true;
            default:
                return true;
        }
    }

    private boolean handleRecipeGuiClick(InventoryClickEvent event, Player player, Inventory openInventory) {
        if (!openInventory.equals(GuiManager.RECIPE_GUI_MAP.get(player.getUniqueId()))) {
            return false;
        }
        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType() == Material.BARRIER) {
            player.closeInventory();
            return true;
        }
        return true;
    }

    private void handleReviveClick(ItemStack item, Player player, boolean bedrock) {
        if (!player.hasPermission("lifestealz.revive")) {
            throwPermissionError(player);
            return;
        }
        String uuidString = getLastLineOfLore(item, bedrock);
        if (uuidString == null) {
            return;
        }
        UUID uuid = UUID.fromString(uuidString);
        OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(uuid);
        if (target.getName() == null) {
            player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"));
        } else if (!hasReviveCrystal(player)) {
            this.plugin.getLogger().warning("Player " + player.getName() + " tried to revive " + target.getName() + " without a revive crystal!");
        } else {
            revivePlayer(player, target, bedrock);
        }
    }

    private boolean handleReviveGuiClick(InventoryClickEvent event, Player player, Inventory openInventory) {
        if (!openInventory.equals(GuiManager.REVIVE_GUI_MAP.get(player.getUniqueId()))) {
            return false;
        }
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return true;
        }
        switch (AnonymousClass2.$SwitchMap$org$bukkit$Material[item.getType().ordinal()]) {
            case 1:
                player.closeInventory();
                return true;
            case 2:
                GuiManager.openReviveGui(player, getPageFromItem(item));
                return true;
            case 3:
                handleReviveClick(item, player, false);
                return true;
            case 4:
                handleReviveClick(item, player, true);
                return true;
            default:
                return true;
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory openInventory = player.getOpenInventory().getTopInventory();
        if (!handleRecipeGuiClick(event, player, openInventory) && !handleReviveGuiClick(event, player, openInventory) && !handleBeaconReviveGuiClick(event, player, openInventory) && event.getCurrentItem() != null && CustomItemManager.isForbiddenItem(event.getCurrentItem())) {
            event.setCancelled(true);
            event.setCurrentItem(new ItemStack(Material.AIR));
        }
    }

    private void removeReviveCrystal(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && CustomItemManager.isReviveItem(item)) {
                item.setAmount(item.getAmount() - 1);
                return;
            }
        }
    }

    private void revivePlayer(Player reviver, OfflinePlayer target, boolean isBedrock) {
        PlayerData data = this.plugin.getStorage().load(target.getUniqueId());
        if (canRevivePlayer(reviver, target, data)) {
            String[] locationNull = {"null", "null", "null"};
            applyReviveData(data);
            executeReviveActions(reviver, target, locationNull);
            removeReviveCrystal(reviver);
            reviver.closeInventory();
        }
    }

    private void throwPermissionError(HumanEntity player) {
        CommandUtils.throwPermissionError(player);
    }

    // rea: class com.zetaplugins.lifestealz.listeners.InventoryClickListener.AnonymousClass1
    class AnonymousClass1 {
        com.zetaplugins.lifestealz.listeners.InventoryClickListener this$0;
        org.bukkit.Location val$beaconLocation;
        com.zetaplugins.lifestealz.storage.PlayerData val$data;
        java.lang.String[] val$location;
        org.bukkit.entity.Player val$reviver;
        org.bukkit.OfflinePlayer val$target;

        // rea: <init>(com.zetaplugins.lifestealz.listeners.InventoryClickListener, com.zetaplugins.lifestealz.storage.PlayerData, org.bukkit.entity.Player, org.bukkit.OfflinePlayer, java.lang.String[], org.bukkit.Location): void -> smali (java_body_unavailable)
        // .method <init>(Lcom/zetaplugins/lifestealz/listeners/InventoryClickListener;Lcom/zetaplugins/lifestealz/storage/PlayerData;Lorg/bukkit/entity/Player;Lorg/bukkit/OfflinePlayer;[Ljava/lang/String;Lorg/bukkit/Location;)V
        //     .param final mandated null
        //     .param final synthetic null
        //     .param final synthetic null
        //     .param final synthetic null
        //     .param final synthetic null
        //     .param final synthetic null
        //     .max stack 2
        //     .max locals 7
        // 
        //     .local 0 "this" Lcom/zetaplugins/lifestealz/listeners/InventoryClickListener$1;
        //     .local 1 "this$0" Lcom/zetaplugins/lifestealz/listeners/InventoryClickListener;
        //     .line 356
        //     aload 0
        //     aload 1
        //     putfield com/zetaplugins/lifestealz/listeners/InventoryClickListener$1 this$0 Lcom/zetaplugins/lifestealz/listeners/InventoryClickListener;
        //     aload 0
        //     aload 2
        //     putfield com/zetaplugins/lifestealz/listeners/InventoryClickListener$1 val$data Lcom/zetaplugins/lifestealz/storage/PlayerData;
        //     aload 0
        //     aload 3
        //     putfield com/zetaplugins/lifestealz/listeners/InventoryClickListener$1 val$reviver Lorg/bukkit/entity/Player;
        //     aload 0
        //     aload 4
        //     putfield com/zetaplugins/lifestealz/listeners/InventoryClickListener$1 val$target Lorg/bukkit/OfflinePlayer;
        //     aload 0
        //     aload 5
        //     putfield com/zetaplugins/lifestealz/listeners/InventoryClickListener$1 val$location [Ljava/lang/String;
        //     aload 0
        //     aload 6
        //     putfield com/zetaplugins/lifestealz/listeners/InventoryClickListener$1 val$beaconLocation Lorg/bukkit/Location;
        //     aload 0
        //     invokespecial org/bukkit/scheduler/BukkitRunnable <init> ()V
        //     return
        // .end method

        /* JADX WARN: Type inference failed for: r0v23, types: [com.zetaplugins.lifestealz.listeners.InventoryClickListener$1] */
        private void beaconRevivePlayer(final Player reviver, final OfflinePlayer target, boolean isBedrock, final Location beaconLocation) {
            final PlayerData data = this.plugin.getStorage().load(target.getUniqueId());
            String customItemId = CustomBlock.REVIVE_BEACON.getCustomItemId(beaconLocation.getBlock());
            try {
                CustomReviveBeaconItemData itemData = new CustomReviveBeaconItemData(customItemId);
                if (canRevivePlayer(reviver, target, data)) {
                    final String[] location = {String.valueOf(beaconLocation.getBlockX()), String.valueOf(beaconLocation.getBlockY()), String.valueOf(beaconLocation.getBlockZ())};
                    reviver.sendMessage(MessageUtils.getAndFormatMsg(true, "reviveBeaconStart", "&c%player% &7will be revived in &c%seconds% seconds&7! Please wait...", new MessageUtils.Replaceable("%player%", target.getName()), new MessageUtils.Replaceable("%seconds%", String.valueOf(itemData.getReviveTime()))));
                    reviver.closeInventory();
                    for (String command : this.plugin.getConfig().getStringList("reviveStartCommands")) {
                        String finalCommand = command.replace("&player&", reviver.getName()).replace("&target&", target.getName()).replace("&location&", location[0] + ", " + location[1] + ", " + location[2]).replace("&locationX&", location[0]).replace("&locationY&", location[1]).replace("&locationZ&", location[2]);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                    }
                    this.plugin.getReviveBeaconEffectManager().startRevivingEffects(beaconLocation, target.getName(), itemData.shouldShowLaser(), itemData.shouldShowParticleRing(), itemData.getParticleColor(), itemData.getInnerLaser(), itemData.getOuterLaser(), itemData.getReviveTime());
                    BukkitTask reviveTask = new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.listeners.InventoryClickListener.1
                        public void run() {
                            InventoryClickListener.this.applyReviveData(data);
                            InventoryClickListener.this.executeReviveActions(reviver, target, location);
                            InventoryClickListener.this.plugin.getReviveTaskManager().removeReviveTask(beaconLocation);
                            InventoryClickListener.this.plugin.getReviveBeaconEffectManager().clearAllEffects(beaconLocation);
                            beaconLocation.getBlock().setType(Material.AIR);
                            beaconLocation.getWorld().playSound(beaconLocation, Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f);
                        }

    }

    // rea: class com.zetaplugins.lifestealz.listeners.InventoryClickListener.AnonymousClass2
    class AnonymousClass2 {
        int[] $SwitchMap$org$bukkit$Material;

        // rea: m29clinit(): void -> smali (static_initializer_disassembly)
        // .method static <clinit>()V
        //     .max stack 3
        //     .max locals 1
        // 
        //     .line 88
        //     invokestatic org/bukkit/Material values ()[Lorg/bukkit/Material;
        //     arraylength
        //     newarray 10
        //     putstatic com/zetaplugins/lifestealz/listeners/InventoryClickListener$2 $SwitchMap$org$bukkit$Material [I
        //   :L0
        //     getstatic com/zetaplugins/lifestealz/listeners/InventoryClickListener$2 $SwitchMap$org$bukkit$Material [I
        //     getstatic org/bukkit/Material BARRIER Lorg/bukkit/Material;
        //     invokevirtual org/bukkit/Material ordinal ()I
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
        //     getstatic com/zetaplugins/lifestealz/listeners/InventoryClickListener$2 $SwitchMap$org$bukkit$Material [I
        //     getstatic org/bukkit/Material ARROW Lorg/bukkit/Material;
        //     invokevirtual org/bukkit/Material ordinal ()I
        //     iconst_2
        //     iastore
        //   :L4
        //     .catch java/lang/NoSuchFieldError :L3 .. :L4 goto :L5
        //     goto :L6
        //   :L5
        //     .stack same1 java/lang/NoSuchFieldError
        //     astore 0
        //   :L6
        //     .stack same
        //     getstatic com/zetaplugins/lifestealz/listeners/InventoryClickListener$2 $SwitchMap$org$bukkit$Material [I
        //     getstatic org/bukkit/Material PLAYER_HEAD Lorg/bukkit/Material;
        //     invokevirtual org/bukkit/Material ordinal ()I
        //     iconst_3
        //     iastore
        //   :L7
        //     .catch java/lang/NoSuchFieldError :L6 .. :L7 goto :L8
        //     goto :L9
        //   :L8
        //     .stack same1 java/lang/NoSuchFieldError
        //     astore 0
        //   :L9
        //     .stack same
        //     getstatic com/zetaplugins/lifestealz/listeners/InventoryClickListener$2 $SwitchMap$org$bukkit$Material [I
        //     getstatic org/bukkit/Material SKELETON_SKULL Lorg/bukkit/Material;
        //     invokevirtual org/bukkit/Material ordinal ()I
        //     iconst_4
        //     iastore
        //   :L10
        //     .catch java/lang/NoSuchFieldError :L9 .. :L10 goto :L11
        //     goto :L13
        //   :L11
        //     .stack same1 java/lang/NoSuchFieldError
        //     astore 0
        //   :L13
        //     .stack same
        //     return
        // .end method

    }

}
