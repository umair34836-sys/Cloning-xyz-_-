package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import lombok.Generated;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerNaturalDeathEvent extends ZPlayerDeathEventBase {
   private double heartsToLose;
   private boolean shouldDropHearts;
   private String deathMessage;

   public ZPlayerNaturalDeathEvent(PlayerDeathEvent originalEvent, double heartsToLose) {
      super(originalEvent);
      this.heartsToLose = heartsToLose;
      this.shouldDropHearts = false;
      this.deathMessage = originalEvent.getDeathMessage();
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
   public boolean isShouldDropHearts() {
      return this.shouldDropHearts;
   }

   @Generated
   public void setShouldDropHearts(boolean shouldDropHearts) {
      this.shouldDropHearts = shouldDropHearts;
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
