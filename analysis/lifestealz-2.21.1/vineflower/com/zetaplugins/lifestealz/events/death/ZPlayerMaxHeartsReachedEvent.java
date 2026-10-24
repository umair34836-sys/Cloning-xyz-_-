package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import com.zetaplugins.lifestealz.util.MessageUtils;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerMaxHeartsReachedEvent extends ZPlayerDeathEventBase {
   private final Player killer;
   private final double maxHeartsLimit;
   private boolean shouldDropHeartsInstead;
   private Component maxHeartsMessage;

   public ZPlayerMaxHeartsReachedEvent(PlayerDeathEvent originalEvent, Player killer, double maxHearts) {
      super(originalEvent);
      this.killer = killer;
      this.maxHeartsLimit = maxHearts;
      this.shouldDropHeartsInstead = false;
      this.maxHeartsMessage = MessageUtils.getAndFormatMsg(
         false,
         "maxHeartLimitReached",
         "&cYou already reached the limit of %limit% hearts!",
         new MessageUtils.Replaceable("%limit%", String.valueOf((int)(maxHearts / 2.0)))
      );
   }

   @Generated
   public Player getKiller() {
      return this.killer;
   }

   @Generated
   public double getMaxHeartsLimit() {
      return this.maxHeartsLimit;
   }

   @Generated
   public boolean isShouldDropHeartsInstead() {
      return this.shouldDropHeartsInstead;
   }

   @Generated
   public void setShouldDropHeartsInstead(boolean shouldDropHeartsInstead) {
      this.shouldDropHeartsInstead = shouldDropHeartsInstead;
   }

   @Generated
   public Component getMaxHeartsMessage() {
      return this.maxHeartsMessage;
   }

   @Generated
   public void setMaxHeartsMessage(Component maxHeartsMessage) {
      this.maxHeartsMessage = maxHeartsMessage;
   }
}
