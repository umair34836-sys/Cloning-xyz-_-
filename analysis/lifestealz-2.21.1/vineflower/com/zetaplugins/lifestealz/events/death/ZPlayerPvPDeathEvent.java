package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import lombok.Generated;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerPvPDeathEvent extends ZPlayerDeathEventBase {
   private final Player killer;
   private double heartsToLose;
   private double heartsKillerGains;
   private boolean shouldDropHearts;
   private boolean killerShouldGainHearts;
   private String deathMessage;

   public ZPlayerPvPDeathEvent(PlayerDeathEvent originalEvent, Player killer, double heartsToLose, double heartsKillerGains) {
      super(originalEvent);
      this.killer = killer;
      this.heartsToLose = heartsToLose;
      this.heartsKillerGains = heartsKillerGains;
      this.shouldDropHearts = false;
      this.killerShouldGainHearts = true;
      this.deathMessage = originalEvent.getDeathMessage();
   }

   @Generated
   public Player getKiller() {
      return this.killer;
   }

   @Generated
   public double getHeartsToLose() {
      return this.heartsToLose;
   }

   @Generated
   public void setHeartsToLose(double heartsToLose) {
      this.heartsToLose = heartsToLose;
   }

   @Generated
   public double getHeartsKillerGains() {
      return this.heartsKillerGains;
   }

   @Generated
   public void setHeartsKillerGains(double heartsKillerGains) {
      this.heartsKillerGains = heartsKillerGains;
   }

   @Generated
   public boolean isShouldDropHearts() {
      return this.shouldDropHearts;
   }

   @Generated
   public void setShouldDropHearts(boolean shouldDropHearts) {
      this.shouldDropHearts = shouldDropHearts;
   }

   @Generated
   public boolean isKillerShouldGainHearts() {
      return this.killerShouldGainHearts;
   }

   @Generated
   public void setKillerShouldGainHearts(boolean killerShouldGainHearts) {
      this.killerShouldGainHearts = killerShouldGainHearts;
   }

   @Generated
   @Override
   public String getDeathMessage() {
      return this.deathMessage;
   }

   @Generated
   @Override
   public void setDeathMessage(String deathMessage) {
      this.deathMessage = deathMessage;
   }
}
