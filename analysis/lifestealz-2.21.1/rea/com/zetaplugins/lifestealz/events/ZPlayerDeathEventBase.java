// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events;
// rea: class com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase
class ZPlayerDeathEventBase {
    org.bukkit.event.entity.PlayerDeathEvent originalEvent;

    public ZPlayerDeathEventBase(PlayerDeathEvent originalEvent) {
        this.originalEvent = originalEvent;
    }

    @Generated
    public void deathMessage(Component deathMessage) {
        getOriginalEvent().deathMessage(deathMessage);
    }

    @Generated
    public Component deathMessage() {
        return getOriginalEvent().deathMessage();
    }

    @Generated
    public DamageSource getDamageSource() {
        return getOriginalEvent().getDamageSource();
    }

    @Generated
    @Deprecated
    public String getDeathMessage() {
        return getOriginalEvent().getDeathMessage();
    }

    @Generated
    public Sound getDeathSound() {
        return getOriginalEvent().getDeathSound();
    }

    @Generated
    public SoundCategory getDeathSoundCategory() {
        return getOriginalEvent().getDeathSoundCategory();
    }

    @Generated
    public float getDeathSoundPitch() {
        return getOriginalEvent().getDeathSoundPitch();
    }

    @Generated
    public float getDeathSoundVolume() {
        return getOriginalEvent().getDeathSoundVolume();
    }

    @Generated
    public int getDroppedExp() {
        return getOriginalEvent().getDroppedExp();
    }

    @Generated
    public List<ItemStack> getDrops() {
        return getOriginalEvent().getDrops();
    }

    @Generated
    public Player getEntity() {
        return getOriginalEvent().getEntity();
    }

    @Generated
    public EntityType getEntityType() {
        return getOriginalEvent().getEntityType();
    }

    @Generated
    public List<ItemStack> getItemsToKeep() {
        return getOriginalEvent().getItemsToKeep();
    }

    @Generated
    public boolean getKeepInventory() {
        return getOriginalEvent().getKeepInventory();
    }

    @Generated
    public boolean getKeepLevel() {
        return getOriginalEvent().getKeepLevel();
    }

    @Generated
    public int getNewExp() {
        return getOriginalEvent().getNewExp();
    }

    @Generated
    public int getNewLevel() {
        return getOriginalEvent().getNewLevel();
    }

    @Generated
    public int getNewTotalExp() {
        return getOriginalEvent().getNewTotalExp();
    }

    @Generated
    public PlayerDeathEvent getOriginalEvent() {
        return this.originalEvent;
    }

    @Generated
    public Player getPlayer() {
        return getOriginalEvent().getPlayer();
    }

    @Generated
    public double getReviveHealth() {
        return getOriginalEvent().getReviveHealth();
    }

    @Generated
    @Deprecated
    public void setDeathMessage(String deathMessage) {
        getOriginalEvent().setDeathMessage(deathMessage);
    }

    @Generated
    public void setDeathSound(Sound sound) {
        getOriginalEvent().setDeathSound(sound);
    }

    @Generated
    public void setDeathSoundCategory(SoundCategory soundCategory) {
        getOriginalEvent().setDeathSoundCategory(soundCategory);
    }

    @Generated
    public void setDeathSoundPitch(float pitch) {
        getOriginalEvent().setDeathSoundPitch(pitch);
    }

    @Generated
    public void setDeathSoundVolume(float volume) {
        getOriginalEvent().setDeathSoundVolume(volume);
    }

    @Generated
    public void setDroppedExp(int exp) {
        getOriginalEvent().setDroppedExp(exp);
    }

    @Generated
    public void setKeepInventory(boolean keepInventory) {
        getOriginalEvent().setKeepInventory(keepInventory);
    }

    @Generated
    public void setKeepLevel(boolean keepLevel) {
        getOriginalEvent().setKeepLevel(keepLevel);
    }

    @Generated
    public void setNewExp(int exp) {
        getOriginalEvent().setNewExp(exp);
    }

    @Generated
    public void setNewLevel(int level) {
        getOriginalEvent().setNewLevel(level);
    }

    @Generated
    public void setNewTotalExp(int totalExp) {
        getOriginalEvent().setNewTotalExp(totalExp);
    }

    @Generated
    public void setReviveHealth(double reviveHealth) throws IllegalArgumentException {
        getOriginalEvent().setReviveHealth(reviveHealth);
    }

    @Generated
    public void setShouldDropExperience(boolean doExpDrop) {
        getOriginalEvent().setShouldDropExperience(doExpDrop);
    }

    @Generated
    public void setShouldPlayDeathSound(boolean playDeathSound) {
        getOriginalEvent().setShouldPlayDeathSound(playDeathSound);
    }

    @Generated
    public boolean shouldDropExperience() {
        return getOriginalEvent().shouldDropExperience();
    }

    @Generated
    public boolean shouldPlayDeathSound() {
        return getOriginalEvent().shouldPlayDeathSound();
    }

}
