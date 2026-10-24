// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events.death;
// rea: class com.zetaplugins.lifestealz.events.death.ZPlayerPvPDeathEvent
class ZPlayerPvPDeathEvent {
    java.lang.String deathMessage;
    double heartsKillerGains;
    double heartsToLose;
    org.bukkit.entity.Player killer;
    boolean killerShouldGainHearts;
    boolean shouldDropHearts;

    public ZPlayerPvPDeathEvent(PlayerDeathEvent originalEvent, Player killer, double heartsToLose, double heartsKillerGains) {
        super(originalEvent);
        this.killer = killer;
        this.heartsToLose = heartsToLose;
        this.heartsKillerGains = heartsKillerGains;
        this.shouldDropHearts = false;
        this.killerShouldGainHearts = true;
        this.deathMessage = originalEvent.getDeathMessage();
    }

    @Override // com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase
    @Generated
    public String getDeathMessage() {
        return this.deathMessage;
    }

    @Generated
    public double getHeartsKillerGains() {
        return this.heartsKillerGains;
    }

    @Generated
    public double getHeartsToLose() {
        return this.heartsToLose;
    }

    @Generated
    public Player getKiller() {
        return this.killer;
    }

    @Generated
    public boolean isKillerShouldGainHearts() {
        return this.killerShouldGainHearts;
    }

    @Generated
    public boolean isShouldDropHearts() {
        return this.shouldDropHearts;
    }

    @Override // com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase
    @Generated
    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }

    @Generated
    public void setHeartsKillerGains(double heartsKillerGains) {
        this.heartsKillerGains = heartsKillerGains;
    }

    @Generated
    public void setHeartsToLose(double heartsToLose) {
        this.heartsToLose = heartsToLose;
    }

    @Generated
    public void setKillerShouldGainHearts(boolean killerShouldGainHearts) {
        this.killerShouldGainHearts = killerShouldGainHearts;
    }

    @Generated
    public void setShouldDropHearts(boolean shouldDropHearts) {
        this.shouldDropHearts = shouldDropHearts;
    }

}
