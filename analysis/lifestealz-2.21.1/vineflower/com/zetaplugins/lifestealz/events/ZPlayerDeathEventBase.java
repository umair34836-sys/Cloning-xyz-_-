package com.zetaplugins.lifestealz.events;

import java.util.List;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public abstract class ZPlayerDeathEventBase extends ZEvent {
   protected final PlayerDeathEvent originalEvent;

   public ZPlayerDeathEventBase(PlayerDeathEvent originalEvent) {
      this.originalEvent = originalEvent;
   }

   @Generated
   public PlayerDeathEvent getOriginalEvent() {
      return this.originalEvent;
   }

   @Generated
   public List<ItemStack> getItemsToKeep() {
      return this.getOriginalEvent().getItemsToKeep();
   }

   @Generated
   public boolean shouldDropExperience() {
      return this.getOriginalEvent().shouldDropExperience();
   }

   @Generated
   public void setShouldDropExperience(boolean doExpDrop) {
      this.getOriginalEvent().setShouldDropExperience(doExpDrop);
   }

   @Generated
   public Player getEntity() {
      return this.getOriginalEvent().getEntity();
   }

   @Generated
   public Player getPlayer() {
      return this.getOriginalEvent().getPlayer();
   }

   @Generated
   public void deathMessage(Component deathMessage) {
      this.getOriginalEvent().deathMessage(deathMessage);
   }

   @Generated
   public Component deathMessage() {
      return this.getOriginalEvent().deathMessage();
   }

   @Deprecated
   @Generated
   public void setDeathMessage(String deathMessage) {
      this.getOriginalEvent().setDeathMessage(deathMessage);
   }

   @Deprecated
   @Generated
   public String getDeathMessage() {
      return this.getOriginalEvent().getDeathMessage();
   }

   @Generated
   public int getNewExp() {
      return this.getOriginalEvent().getNewExp();
   }

   @Generated
   public void setNewExp(int exp) {
      this.getOriginalEvent().setNewExp(exp);
   }

   @Generated
   public int getNewLevel() {
      return this.getOriginalEvent().getNewLevel();
   }

   @Generated
   public void setNewLevel(int level) {
      this.getOriginalEvent().setNewLevel(level);
   }

   @Generated
   public int getNewTotalExp() {
      return this.getOriginalEvent().getNewTotalExp();
   }

   @Generated
   public void setNewTotalExp(int totalExp) {
      this.getOriginalEvent().setNewTotalExp(totalExp);
   }

   @Generated
   public boolean getKeepLevel() {
      return this.getOriginalEvent().getKeepLevel();
   }

   @Generated
   public void setKeepLevel(boolean keepLevel) {
      this.getOriginalEvent().setKeepLevel(keepLevel);
   }

   @Generated
   public void setKeepInventory(boolean keepInventory) {
      this.getOriginalEvent().setKeepInventory(keepInventory);
   }

   @Generated
   public boolean getKeepInventory() {
      return this.getOriginalEvent().getKeepInventory();
   }

   @Generated
   public DamageSource getDamageSource() {
      return this.getOriginalEvent().getDamageSource();
   }

   @Generated
   public int getDroppedExp() {
      return this.getOriginalEvent().getDroppedExp();
   }

   @Generated
   public void setDroppedExp(int exp) {
      this.getOriginalEvent().setDroppedExp(exp);
   }

   @Generated
   public List<ItemStack> getDrops() {
      return this.getOriginalEvent().getDrops();
   }

   @Generated
   public double getReviveHealth() {
      return this.getOriginalEvent().getReviveHealth();
   }

   @Generated
   public void setReviveHealth(double reviveHealth) throws IllegalArgumentException {
      this.getOriginalEvent().setReviveHealth(reviveHealth);
   }

   @Generated
   public boolean shouldPlayDeathSound() {
      return this.getOriginalEvent().shouldPlayDeathSound();
   }

   @Generated
   public void setShouldPlayDeathSound(boolean playDeathSound) {
      this.getOriginalEvent().setShouldPlayDeathSound(playDeathSound);
   }

   @Generated
   public Sound getDeathSound() {
      return this.getOriginalEvent().getDeathSound();
   }

   @Generated
   public void setDeathSound(Sound sound) {
      this.getOriginalEvent().setDeathSound(sound);
   }

   @Generated
   public SoundCategory getDeathSoundCategory() {
      return this.getOriginalEvent().getDeathSoundCategory();
   }

   @Generated
   public void setDeathSoundCategory(SoundCategory soundCategory) {
      this.getOriginalEvent().setDeathSoundCategory(soundCategory);
   }

   @Generated
   public float getDeathSoundVolume() {
      return this.getOriginalEvent().getDeathSoundVolume();
   }

   @Generated
   public void setDeathSoundVolume(float volume) {
      this.getOriginalEvent().setDeathSoundVolume(volume);
   }

   @Generated
   public float getDeathSoundPitch() {
      return this.getOriginalEvent().getDeathSoundPitch();
   }

   @Generated
   public void setDeathSoundPitch(float pitch) {
      this.getOriginalEvent().setDeathSoundPitch(pitch);
   }

   @Generated
   public EntityType getEntityType() {
      return this.getOriginalEvent().getEntityType();
   }
}
