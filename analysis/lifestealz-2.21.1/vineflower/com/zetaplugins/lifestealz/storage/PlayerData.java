package com.zetaplugins.lifestealz.storage;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
   private final String name;
   private final String uuid;
   private double maxHealth = 20.0;
   private int craftedHearts;
   private int craftedRevives;
   private int hasBeenRevived;
   private int killedOtherPlayers;
   private long firstJoin;
   private final Set<String> modifiedFields = new HashSet<>();

   public PlayerData(String name, UUID uuid) {
      this.name = name;
      this.uuid = uuid.toString();
   }

   public String getName() {
      return this.name;
   }

   public String getUuid() {
      return this.uuid;
   }

   public double getMaxHealth() {
      return this.maxHealth;
   }

   public void setMaxHealth(double maxHealth) {
      if (this.maxHealth != maxHealth) {
         this.maxHealth = maxHealth;
         this.modifiedFields.add("maxhp");
      }
   }

   public int getCraftedHearts() {
      return this.craftedHearts;
   }

   public void setCraftedHearts(int craftedHearts) {
      if (this.craftedHearts != craftedHearts) {
         this.craftedHearts = craftedHearts;
         this.modifiedFields.add("craftedHearts");
      }
   }

   public int getCraftedRevives() {
      return this.craftedRevives;
   }

   public void setCraftedRevives(int craftedRevives) {
      if (this.craftedRevives != craftedRevives) {
         this.craftedRevives = craftedRevives;
         this.modifiedFields.add("craftedRevives");
      }
   }

   public int getHasBeenRevived() {
      return this.hasBeenRevived;
   }

   public void setHasBeenRevived(int hasBeenRevived) {
      if (this.hasBeenRevived != hasBeenRevived) {
         this.hasBeenRevived = hasBeenRevived;
         this.modifiedFields.add("hasbeenRevived");
      }
   }

   public int getKilledOtherPlayers() {
      return this.killedOtherPlayers;
   }

   public void setKilledOtherPlayers(int killedOtherPlayers) {
      if (this.killedOtherPlayers != killedOtherPlayers) {
         this.killedOtherPlayers = killedOtherPlayers;
         this.modifiedFields.add("killedOtherPlayers");
      }
   }

   public long getFirstJoin() {
      return this.firstJoin;
   }

   public void setFirstJoin(long firstJoin) {
      if (this.firstJoin != firstJoin) {
         this.firstJoin = firstJoin;
         this.modifiedFields.add("firstJoin");
      }
   }

   public boolean hasChanges() {
      return !this.modifiedFields.isEmpty();
   }

   public Set<String> getModifiedFields() {
      return new HashSet<>(this.modifiedFields);
   }

   public void clearModifiedFields() {
      this.modifiedFields.clear();
   }
}
