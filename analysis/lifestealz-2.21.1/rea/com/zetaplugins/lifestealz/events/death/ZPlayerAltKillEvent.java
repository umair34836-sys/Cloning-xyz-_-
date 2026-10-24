// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events.death;
// rea: class com.zetaplugins.lifestealz.events.death.ZPlayerAltKillEvent
class ZPlayerAltKillEvent {
    java.lang.String sharedIP;
    boolean shouldLogAttempt;
    boolean shouldPreventKill;
    boolean shouldSendMessage;
    org.bukkit.entity.Player suspectedAlt;
    net.kyori.adventure.text.Component warningMessage;

    public ZPlayerAltKillEvent(PlayerDeathEvent originalEvent, Player suspectedAlt, String sharedIP) {
        super(originalEvent);
        this.suspectedAlt = suspectedAlt;
        this.sharedIP = sharedIP;
        this.shouldPreventKill = true;
        this.shouldLogAttempt = true;
        this.shouldSendMessage = true;
        this.warningMessage = MessageUtils.getAndFormatMsg(false, "altKill", "&cPlease don't kill alts! This attempt has been logged!", new MessageUtils.Replaceable[0]);
    }

    @Generated
    public String getSharedIP() {
        return this.sharedIP;
    }

    @Generated
    public Player getSuspectedAlt() {
        return this.suspectedAlt;
    }

    @Generated
    public Component getWarningMessage() {
        return this.warningMessage;
    }

    @Generated
    public boolean isShouldLogAttempt() {
        return this.shouldLogAttempt;
    }

    @Generated
    public boolean isShouldPreventKill() {
        return this.shouldPreventKill;
    }

    @Generated
    public boolean isShouldSendMessage() {
        return this.shouldSendMessage;
    }

    @Generated
    public void setShouldLogAttempt(boolean shouldLogAttempt) {
        this.shouldLogAttempt = shouldLogAttempt;
    }

    @Generated
    public void setShouldPreventKill(boolean shouldPreventKill) {
        this.shouldPreventKill = shouldPreventKill;
    }

    @Generated
    public void setShouldSendMessage(boolean shouldSendMessage) {
        this.shouldSendMessage = shouldSendMessage;
    }

    @Generated
    public void setWarningMessage(Component warningMessage) {
        this.warningMessage = warningMessage;
    }

}
