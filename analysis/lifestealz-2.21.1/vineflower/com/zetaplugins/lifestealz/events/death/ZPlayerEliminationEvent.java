package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import com.zetaplugins.lifestealz.util.MessageUtils;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerEliminationEvent extends ZPlayerDeathEventBase {
   private final Player killer;
   private final boolean isNaturalElimination;
   private boolean shouldBanPlayer;
   private boolean shouldAnnounceElimination;
   private Component eliminationMessage;
   private Component kickMessage;

   public ZPlayerEliminationEvent(PlayerDeathEvent originalEvent, Player killer) {
      super(originalEvent);
      this.killer = killer;
      this.isNaturalElimination = killer == null;
      this.shouldBanPlayer = true;
      this.shouldAnnounceElimination = true;
      this.kickMessage = MessageUtils.getAndFormatMsg(false, "eliminatedJoin", "&cYou don't have any hearts left!");
   }

   @Generated
   public Player getKiller() {
      return this.killer;
   }

   @Generated
   public boolean isNaturalElimination() {
      return this.isNaturalElimination;
   }

   @Generated
   public boolean isShouldBanPlayer() {
      return this.shouldBanPlayer;
   }

   @Generated
   public void setShouldBanPlayer(boolean shouldBanPlayer) {
      this.shouldBanPlayer = shouldBanPlayer;
   }

   @Generated
   public boolean isShouldAnnounceElimination() {
      return this.shouldAnnounceElimination;
   }

   @Generated
   public void setShouldAnnounceElimination(boolean shouldAnnounceElimination) {
      this.shouldAnnounceElimination = shouldAnnounceElimination;
   }

   @Generated
   public Component getEliminationMessage() {
      return this.eliminationMessage;
   }

   @Generated
   public void setEliminationMessage(Component eliminationMessage) {
      this.eliminationMessage = eliminationMessage;
   }

   @Generated
   public Component getKickMessage() {
      return this.kickMessage;
   }

   @Generated
   public void setKickMessage(Component kickMessage) {
      this.kickMessage = kickMessage;
   }
}
