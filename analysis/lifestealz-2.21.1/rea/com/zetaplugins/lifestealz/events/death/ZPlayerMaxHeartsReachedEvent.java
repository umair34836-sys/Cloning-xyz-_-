// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events.death;
// rea: class com.zetaplugins.lifestealz.events.death.ZPlayerMaxHeartsReachedEvent
class ZPlayerMaxHeartsReachedEvent {
    org.bukkit.entity.Player killer;
    double maxHeartsLimit;
    net.kyori.adventure.text.Component maxHeartsMessage;
    boolean shouldDropHeartsInstead;

    public ZPlayerMaxHeartsReachedEvent(PlayerDeathEvent originalEvent, Player killer, double maxHearts) {
        super(originalEvent);
        this.killer = killer;
        this.maxHeartsLimit = maxHearts;
        this.shouldDropHeartsInstead = false;
        this.maxHeartsMessage = MessageUtils.getAndFormatMsg(false, "maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new MessageUtils.Replaceable("%limit%", String.valueOf((int) (maxHearts / 2.0d))));
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
    public Component getMaxHeartsMessage() {
        return this.maxHeartsMessage;
    }

    @Generated
    public boolean isShouldDropHeartsInstead() {
        return this.shouldDropHeartsInstead;
    }

    @Generated
    public void setMaxHeartsMessage(Component maxHeartsMessage) {
        this.maxHeartsMessage = maxHeartsMessage;
    }

    @Generated
    public void setShouldDropHeartsInstead(boolean shouldDropHeartsInstead) {
        this.shouldDropHeartsInstead = shouldDropHeartsInstead;
    }

}
