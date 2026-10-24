// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customitems;
// rea: class com.zetaplugins.lifestealz.util.customitems.CustomItemManager
class CustomItemManager {
    org.bukkit.NamespacedKey CUSTOM_HEART_VALUE_KEY;
    org.bukkit.NamespacedKey CUSTOM_ITEM_ID_KEY;
    org.bukkit.NamespacedKey CUSTOM_ITEM_TYPE_KEY;
    org.bukkit.NamespacedKey DESPAWNABLE_KEY;
    org.bukkit.NamespacedKey INVULNERABLE_KEY;
    org.bukkit.NamespacedKey REVIVE_PAGE_KEY;

    private CustomItemManager() {
    }

    public static ItemStack createBackItem(int page) {
        CustomItem ci = new CustomItem(Material.ARROW).setName(MessageUtils.getAndFormatMsg(false, "backBtn", "&cBack", new MessageUtils.Replaceable[0])).addFlag(ItemFlag.HIDE_ATTRIBUTES).makeForbidden();
        ci.getItemStack().setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData) CustomModelData.customModelData().addString("lifestealz_back").build());
        ItemMeta itemMeta = ci.getItemStack().getItemMeta();
        itemMeta.getPersistentDataContainer().set(REVIVE_PAGE_KEY, PersistentDataType.INTEGER, Integer.valueOf(page));
        ci.getItemStack().setItemMeta(itemMeta);
        return ci.getItemStack();
    }

    public static ItemStack createCloseItem() {
        CustomItem ci = new CustomItem(Material.BARRIER).setName(MessageUtils.getAndFormatMsg(false, "closeBtn", "&cClose", new MessageUtils.Replaceable[0])).addFlag(ItemFlag.HIDE_ATTRIBUTES).makeForbidden();
        ci.getItemStack().setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData) CustomModelData.customModelData().addString("lifestealz_close").build());
        return ci.getItemStack();
    }

    public static ItemStack createCustomItem(String itemId) {
        try {
            FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();
            String customItemType = config.getString(itemId + ".customItemType", "heart");
            String materialString = config.getString(itemId + ".material", "NETHER_STAR");
            if (customItemType.equalsIgnoreCase("revivebeacon")) {
                materialString = "BEACON";
            }
            Material material = Material.valueOf(materialString);
            CustomItem ci = new CustomItem(material).setName(config.getString(itemId + ".name")).setLore(config.getStringList(itemId + ".lore")).setEnchanted(config.getBoolean(itemId + ".enchanted")).setInvulnerable(config.getBoolean(itemId + ".invulnerable")).setDespawnable(config.getBoolean(itemId + ".despawnable")).setId("lifestealz_" + itemId, config.getInt(itemId + ".customModelId", 0)).addFlag(ItemFlag.HIDE_ATTRIBUTES);
            ItemMeta itemMeta = ci.getItemStack().getItemMeta();
            itemMeta.getPersistentDataContainer().set(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING, itemId);
            itemMeta.getPersistentDataContainer().set(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING, customItemType);
            if (customItemType.equalsIgnoreCase("heart")) {
                itemMeta.getPersistentDataContainer().set(CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER, Integer.valueOf(config.getInt(itemId + ".customHeartValue")));
            }
            ci.getItemStack().setItemMeta(itemMeta);
            return ci.getItemStack();
        } catch (IllegalArgumentException ex) {
            LifeStealZ.getInstance().getLogger().log(Level.SEVERE, "Could not create custom item with id '" + itemId + "': " + ex.getMessage(), (Throwable) ex);
            return new CustomItem(Material.BARRIER).setName("&cInvalid Item").setLore(new ArrayList(List.of("&7This item is invalid or does not exist.", "&cCheck the server console for more information!"))).makeForbidden().getItemStack();
        }
    }

    public static ItemStack createCustomItem(String itemId, int amount) {
        return new CustomItem(createCustomItem(itemId)).setAmount(amount).getItemStack();
    }

    public static ItemStack createHeart() {
        return createCustomItem(LifeStealZ.getInstance().getConfig().getString("heartItem.default", "defaultheart"));
    }

    public static ItemStack createHeartAnimationTotem() {
        CustomItem ci = new CustomItem(Material.TOTEM_OF_UNDYING).setName(MessageUtils.getAndFormatMsg(false, "heartAnimationTotem", "&cHeart Animation", new MessageUtils.Replaceable[0])).setLore(new ArrayList(List.of("&7This item is used for heart animations."))).addFlag(ItemFlag.HIDE_ATTRIBUTES).makeForbidden();
        ci.getItemStack().setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData) CustomModelData.customModelData().addString("lifestealz_totem").build());
        return ci.getItemStack();
    }

    public static ItemStack createHeartGainCooldownHeart() {
        return createCustomItem(LifeStealZ.getInstance().getConfig().getString("heartItem.heartGainCooldown", "defaultheart"));
    }

    public static ItemStack createKillHeart() {
        return createCustomItem(LifeStealZ.getInstance().getConfig().getString("heartItem.kill", "defaultheart"));
    }

    public static ItemStack createMaxHealthHeart() {
        return createCustomItem(LifeStealZ.getInstance().getConfig().getString("heartItem.maxHearts", "defaultheart"));
    }

    public static ItemStack createNaturalDeathHeart() {
        return createCustomItem(LifeStealZ.getInstance().getConfig().getString("heartItem.naturalDeath", "defaultheart"));
    }

    public static ItemStack createNextItem(int page) {
        CustomItem ci = new CustomItem(Material.ARROW).setName(MessageUtils.getAndFormatMsg(false, "nextBtn", "&cNext", new MessageUtils.Replaceable[0])).addFlag(ItemFlag.HIDE_ATTRIBUTES).makeForbidden();
        ci.getItemStack().setData(DataComponentTypes.CUSTOM_MODEL_DATA, (CustomModelData) CustomModelData.customModelData().addString("lifestealz_next").build());
        ItemMeta itemMeta = ci.getItemStack().getItemMeta();
        itemMeta.getPersistentDataContainer().set(REVIVE_PAGE_KEY, PersistentDataType.INTEGER, Integer.valueOf(page));
        ci.getItemStack().setItemMeta(itemMeta);
        return ci.getItemStack();
    }

    public static ItemStack createWithdrawHeart() {
        return createCustomItem(LifeStealZ.getInstance().getConfig().getString("heartItem.withdraw", "defaultheart"));
    }

    public static ItemStack getBedrockPlayerHead(UUID uuid) {
        ItemStack head = new ItemStack(Material.SKELETON_SKULL);
        SkullMeta skullMeta = head.getItemMeta();
        skullMeta.displayName(Component.text("§d" + LifeStealZ.getInstance().getGeyserManager().getOfflineBedrockPlayerName(uuid)));
        List<Component> lines = new ArrayList<>();
        lines.add(MessageUtils.getAndFormatMsg(false, "revivePlayerDesc", "&7Click to revive this player", new MessageUtils.Replaceable[0]));
        lines.add(MessageUtils.formatMsg("<dark_gray>" + String.valueOf(uuid), new MessageUtils.Replaceable[0]));
        lines.add(MessageUtils.formatMsg("<dark_gray><i>This player is using the Bedrock Edition of Minecraft.</i>", new MessageUtils.Replaceable[0]));
        skullMeta.lore(lines);
        head.setItemMeta(skullMeta);
        return head;
    }

    public static CustomItemData getCustomItemData(String itemId) {
        try {
            return new CustomItemData(itemId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    public static String getCustomItemId(ItemStack item) {
        if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING)) {
            return null;
        }
        return (String) item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING);
    }

    public static ItemStack getPlayerHead(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null || offlinePlayer.getName() == null) {
            return new CustomItem(Material.SKELETON_SKULL).setName("&dUnknown").setLore(new ArrayList(List.of("&8" + String.valueOf(UUID.randomUUID())))).getItemStack();
        }
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = head.getItemMeta();
        skullMeta.displayName(Component.text("§d" + offlinePlayer.getName()));
        List<Component> lines = new ArrayList<>();
        lines.add(MessageUtils.getAndFormatMsg(false, "revivePlayerDesc", "&7Click to revive this player", new MessageUtils.Replaceable[0]));
        lines.add(MessageUtils.formatMsg("<dark_gray>" + String.valueOf(offlinePlayer.getUniqueId()), new MessageUtils.Replaceable[0]));
        skullMeta.lore(lines);
        skullMeta.setOwningPlayer(offlinePlayer);
        head.setItemMeta(skullMeta);
        return head;
    }

    public static boolean isCustomItem(ItemStack item) {
        return getCustomItemId(item) != null;
    }

    public static boolean isDespawnable(ItemStack item) {
        return item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(DESPAWNABLE_KEY, PersistentDataType.BOOLEAN) && ((Boolean) item.getItemMeta().getPersistentDataContainer().get(DESPAWNABLE_KEY, PersistentDataType.BOOLEAN)).booleanValue();
    }

    public static boolean isForbiddenItem(ItemStack item) {
        return getCustomItemId(item) != null && getCustomItemId(item).equals("forbidden");
    }

    @Deprecated
    public static boolean isHeartItem(ItemStack item) {
        return CustomItemType.HEART.is(item);
    }

    public static boolean isInvulnerable(ItemStack item) {
        return item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(INVULNERABLE_KEY, PersistentDataType.BOOLEAN) && ((Boolean) item.getItemMeta().getPersistentDataContainer().get(INVULNERABLE_KEY, PersistentDataType.BOOLEAN)).booleanValue();
    }

    @Deprecated
    public static boolean isReviveItem(ItemStack item) {
        return CustomItemType.REVIVE.is(item);
    }

    // rea: m37clinit(): void -> smali (static_initializer_disassembly)
    // .method static <clinit>()V
    //     .max stack 4
    //     .max locals 0
    // 
    //     .line 25
    //     new org/bukkit/NamespacedKey
    //     dup
    //     invokestatic com/zetaplugins/lifestealz/LifeStealZ getInstance ()Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     ldc "customitemid"
    //     invokespecial org/bukkit/NamespacedKey <init> (Lorg/bukkit/plugin/Plugin;Ljava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemManager CUSTOM_ITEM_ID_KEY Lorg/bukkit/NamespacedKey;
    //     .line 26
    //     new org/bukkit/NamespacedKey
    //     dup
    //     invokestatic com/zetaplugins/lifestealz/LifeStealZ getInstance ()Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     ldc "customitemtype"
    //     invokespecial org/bukkit/NamespacedKey <init> (Lorg/bukkit/plugin/Plugin;Ljava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemManager CUSTOM_ITEM_TYPE_KEY Lorg/bukkit/NamespacedKey;
    //     .line 27
    //     new org/bukkit/NamespacedKey
    //     dup
    //     invokestatic com/zetaplugins/lifestealz/LifeStealZ getInstance ()Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     ldc "customheartvalue"
    //     invokespecial org/bukkit/NamespacedKey <init> (Lorg/bukkit/plugin/Plugin;Ljava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemManager CUSTOM_HEART_VALUE_KEY Lorg/bukkit/NamespacedKey;
    //     .line 28
    //     new org/bukkit/NamespacedKey
    //     dup
    //     invokestatic com/zetaplugins/lifestealz/LifeStealZ getInstance ()Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     ldc "revivepage"
    //     invokespecial org/bukkit/NamespacedKey <init> (Lorg/bukkit/plugin/Plugin;Ljava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemManager REVIVE_PAGE_KEY Lorg/bukkit/NamespacedKey;
    //     .line 29
    //     new org/bukkit/NamespacedKey
    //     dup
    //     invokestatic com/zetaplugins/lifestealz/LifeStealZ getInstance ()Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     ldc "despawnable"
    //     invokespecial org/bukkit/NamespacedKey <init> (Lorg/bukkit/plugin/Plugin;Ljava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemManager DESPAWNABLE_KEY Lorg/bukkit/NamespacedKey;
    //     .line 30
    //     new org/bukkit/NamespacedKey
    //     dup
    //     invokestatic com/zetaplugins/lifestealz/LifeStealZ getInstance ()Lcom/zetaplugins/lifestealz/LifeStealZ;
    //     ldc "invulnerable"
    //     invokespecial org/bukkit/NamespacedKey <init> (Lorg/bukkit/plugin/Plugin;Ljava/lang/String;)V
    //     putstatic com/zetaplugins/lifestealz/util/customitems/CustomItemManager INVULNERABLE_KEY Lorg/bukkit/NamespacedKey;
    //     return
    // .end method

}
