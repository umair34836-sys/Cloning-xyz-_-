// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events.death;
// rea: class com.zetaplugins.lifestealz.events.death.ZPlayerGracePeriodDeathEvent
class ZPlayerGracePeriodDeathEvent {
    boolean isHeartGainBlocked;
    boolean isHeartLossBlocked;
    org.bukkit.entity.Player killer;
    net.kyori.adventure.text.Component messageToKiller;
    net.kyori.adventure.text.Component messageToVictim;

    public ZPlayerGracePeriodDeathEvent(PlayerDeathEvent originalEvent, Player killer, boolean heartLossBlocked, boolean heartGainBlocked) {
        super(originalEvent);
        this.killer = killer;
        this.isHeartLossBlocked = heartLossBlocked;
        this.isHeartGainBlocked = heartGainBlocked;
        this.messageToVictim = MessageUtils.getAndFormatMsg(false, "noHeartLossInGracePeriod", "&cYou can't lose hearts during the grace period!", new MessageUtils.Replaceable[0]);
        this.messageToKiller = killer != null ? MessageUtils.getAndFormatMsg(false, "noHeartGainInGracePeriod", "&cYou can't gain hearts during the grace period!", new MessageUtils.Replaceable[0]) : Component.text("");
    }

    @Generated
    public Player getKiller() {
        return this.killer;
    }

    @Generated
    public Component getMessageToKiller() {
        return this.messageToKiller;
    }

    @Generated
    public Component getMessageToVictim() {
        return this.messageToVictim;
    }

    @Generated
    public boolean isHeartGainBlocked() {
        return this.isHeartGainBlocked;
    }

    @Generated
    public boolean isHeartLossBlocked() {
        return this.isHeartLossBlocked;
    }

    @Generated
    public void setMessageToKiller(Component messageToKiller) {
        this.messageToKiller = messageToKiller;
    }

    @Generated
    public void setMessageToVictim(Component messageToVictim) {
        this.messageToVictim = messageToVictim;
    }

}
