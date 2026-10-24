package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import com.zetaplugins.lifestealz.util.MessageUtils;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerAltKillEvent extends ZPlayerDeathEventBase {
   private final Player suspectedAlt;
   private final String sharedIP;
   private boolean shouldPreventKill;
   private boolean shouldLogAttempt;
   private boolean shouldSendMessage;
   private Component warningMessage;

   public ZPlayerAltKillEvent(PlayerDeathEvent originalEvent, Player suspectedAlt, String sharedIP) {
      super(originalEvent);
      this.suspectedAlt = suspectedAlt;
      this.sharedIP = sharedIP;
      this.shouldPreventKill = true;
      this.shouldLogAttempt = true;
      this.shouldSendMessage = true;
      this.warningMessage = MessageUtils.getAndFormatMsg(false, "altKill", "&cPlease don't kill alts! This attempt has been logged!");
   }

   @Generated
   public Player getSuspectedAlt() {
      return this.suspectedAlt;
   }

   @Generated
   public String getSharedIP() {
      return this.sharedIP;
   }

   @Generated
   public boolean isShouldPreventKill() {
      return this.shouldPreventKill;
   }

   @Generated
   public void setShouldPreventKill(boolean shouldPreventKill) {
      this.shouldPreventKill = shouldPreventKill;
   }

   @Generated
   public boolean isShouldLogAttempt() {
      return this.shouldLogAttempt;
   }

   @Generated
   public void setShouldLogAttempt(boolean shouldLogAttempt) {
      this.shouldLogAttempt = shouldLogAttempt;
   }

   @Generated
   public boolean isShouldSendMessage() {
      return this.shouldSendMessage;
   }

   @Generated
   public void setShouldSendMessage(boolean shouldSendMessage) {
      this.shouldSendMessage = shouldSendMessage;
   }

   @Generated
   public Component getWarningMessage() {
      return this.warningMessage;
   }

   @Generated
   public void setWarningMessage(Component warningMessage) {
      this.warningMessage = warningMessage;
   }
}
