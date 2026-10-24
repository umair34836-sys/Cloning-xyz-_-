// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events.death;
// rea: class com.zetaplugins.lifestealz.events.death.ZPlayerEliminationEvent
class ZPlayerEliminationEvent {
    net.kyori.adventure.text.Component eliminationMessage;
    boolean isNaturalElimination;
    net.kyori.adventure.text.Component kickMessage;
    org.bukkit.entity.Player killer;
    boolean shouldAnnounceElimination;
    boolean shouldBanPlayer;

    public ZPlayerEliminationEvent(PlayerDeathEvent originalEvent, Player killer) {
        super(originalEvent);
        this.killer = killer;
        this.isNaturalElimination = killer == null;
        this.shouldBanPlayer = true;
        this.shouldAnnounceElimination = true;
        this.kickMessage = MessageUtils.getAndFormatMsg(false, "eliminatedJoin", "&cYou don't have any hearts left!", new MessageUtils.Replaceable[0]);
    }

    @Generated
    public Component getEliminationMessage() {
        return this.eliminationMessage;
    }

    @Generated
    public Component getKickMessage() {
        return this.kickMessage;
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
    public boolean isShouldAnnounceElimination() {
        return this.shouldAnnounceElimination;
    }

    @Generated
    public boolean isShouldBanPlayer() {
        return this.shouldBanPlayer;
    }

    @Generated
    public void setEliminationMessage(Component eliminationMessage) {
        this.eliminationMessage = eliminationMessage;
    }

    @Generated
    public void setKickMessage(Component kickMessage) {
        this.kickMessage = kickMessage;
    }

    @Generated
    public void setShouldAnnounceElimination(boolean shouldAnnounceElimination) {
        this.shouldAnnounceElimination = shouldAnnounceElimination;
    }

    @Generated
    public void setShouldBanPlayer(boolean shouldBanPlayer) {
        this.shouldBanPlayer = shouldBanPlayer;
    }

}
