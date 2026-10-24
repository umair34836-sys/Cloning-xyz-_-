// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events.death;
// rea: class com.zetaplugins.lifestealz.events.death.ZPlayerHeartGainCooldownEvent
class ZPlayerHeartGainCooldownEvent {
    net.kyori.adventure.text.Component cooldownMessage;
    org.bukkit.entity.Player killer;
    boolean shouldDropHeartsInstead;
    long timeLeftOnCooldown;

    public ZPlayerHeartGainCooldownEvent(PlayerDeathEvent originalEvent, Player killer, long timeLeft) {
        super(originalEvent);
        this.killer = killer;
        this.timeLeftOnCooldown = timeLeft;
        this.shouldDropHeartsInstead = false;
        this.cooldownMessage = MessageUtils.getAndFormatMsg(false, "heartGainCooldown", "You have to wait before gaining another heart!", new MessageUtils.Replaceable("%time%", MessageUtils.formatTime(timeLeft)));
    }

    @Generated
    public Component getCooldownMessage() {
        return this.cooldownMessage;
    }

    @Generated
    public Player getKiller() {
        return this.killer;
    }

    @Generated
    public long getTimeLeftOnCooldown() {
        return this.timeLeftOnCooldown;
    }

    @Generated
    public boolean isShouldDropHeartsInstead() {
        return this.shouldDropHeartsInstead;
    }

    @Generated
    public void setCooldownMessage(Component cooldownMessage) {
        this.cooldownMessage = cooldownMessage;
    }

    @Generated
    public void setShouldDropHeartsInstead(boolean shouldDropHeartsInstead) {
        this.shouldDropHeartsInstead = shouldDropHeartsInstead;
    }

}
