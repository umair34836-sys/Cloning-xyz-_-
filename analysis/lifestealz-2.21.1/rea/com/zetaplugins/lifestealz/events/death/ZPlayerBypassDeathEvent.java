// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events.death;
// rea: class com.zetaplugins.lifestealz.events.death.ZPlayerBypassDeathEvent
class ZPlayerBypassDeathEvent {
    boolean isHeartGainBlocked;
    boolean isHeartLossBlocked;
    org.bukkit.entity.Player killer;
    net.kyori.adventure.text.Component messageToKiller;
    net.kyori.adventure.text.Component messageToVictim;

    public ZPlayerBypassDeathEvent(PlayerDeathEvent originalEvent, Player killer, boolean heartLossBlocked, boolean heartGainBlocked) {
        Component componentText;
        super(originalEvent);
        this.killer = killer;
        this.isHeartLossBlocked = heartLossBlocked;
        this.isHeartGainBlocked = heartGainBlocked;
        this.messageToVictim = MessageUtils.getAndFormatMsg(false, "bypassVictim", "&aYour hearts are protected; you didn't lose any on death.", new MessageUtils.Replaceable[0]);
        if (killer != null) {
            componentText = MessageUtils.getAndFormatMsg(false, "bypassKiller", "&e%player% had bypass active; you didn't gain a heart.", new MessageUtils.Replaceable("%player%", originalEvent.getEntity().getName()));
        } else {
            componentText = Component.text("");
        }
        this.messageToKiller = componentText;
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
