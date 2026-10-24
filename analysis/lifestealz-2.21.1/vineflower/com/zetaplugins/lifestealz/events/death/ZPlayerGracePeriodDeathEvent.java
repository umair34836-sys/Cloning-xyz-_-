package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import com.zetaplugins.lifestealz.util.MessageUtils;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerGracePeriodDeathEvent extends ZPlayerDeathEventBase {
   private final Player killer;
   private final boolean isHeartLossBlocked;
   private final boolean isHeartGainBlocked;
   private Component messageToVictim;
   private Component messageToKiller;

   public ZPlayerGracePeriodDeathEvent(PlayerDeathEvent originalEvent, Player killer, boolean heartLossBlocked, boolean heartGainBlocked) {
      super(originalEvent);
      this.killer = killer;
      this.isHeartLossBlocked = heartLossBlocked;
      this.isHeartGainBlocked = heartGainBlocked;
      this.messageToVictim = MessageUtils.getAndFormatMsg(false, "noHeartLossInGracePeriod", "&cYou can't lose hearts during the grace period!");
      this.messageToKiller = (Component)(killer != null
         ? MessageUtils.getAndFormatMsg(false, "noHeartGainInGracePeriod", "&cYou can't gain hearts during the grace period!")
         : Component.text(""));
   }

   @Generated
   public Player getKiller() {
      return this.killer;
   }

   @Generated
   public boolean isHeartLossBlocked() {
      return this.isHeartLossBlocked;
   }

   @Generated
   public boolean isHeartGainBlocked() {
      return this.isHeartGainBlocked;
   }

   @Generated
   public Component getMessageToVictim() {
      return this.messageToVictim;
   }

   @Generated
   public void setMessageToVictim(Component messageToVictim) {
      this.messageToVictim = messageToVictim;
   }

   @Generated
   public Component getMessageToKiller() {
      return this.messageToKiller;
   }

   @Generated
   public void setMessageToKiller(Component messageToKiller) {
      this.messageToKiller = messageToKiller;
   }
}
