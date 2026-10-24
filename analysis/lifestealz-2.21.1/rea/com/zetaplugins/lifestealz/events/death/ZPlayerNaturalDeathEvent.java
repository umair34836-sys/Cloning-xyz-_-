// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.events.death;
// rea: class com.zetaplugins.lifestealz.events.death.ZPlayerNaturalDeathEvent
class ZPlayerNaturalDeathEvent {
    java.lang.String deathMessage;
    double heartsToLose;
    boolean shouldDropHearts;

    public ZPlayerNaturalDeathEvent(PlayerDeathEvent originalEvent, double heartsToLose) {
        super(originalEvent);
        this.heartsToLose = heartsToLose;
        this.shouldDropHearts = false;
        this.deathMessage = originalEvent.getDeathMessage();
    }

    @Override // com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase
    @Generated
    public String getDeathMessage() {
        return this.deathMessage;
    }

    @Generated
    public double getHeartsToLose() {
        return this.heartsToLose;
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
    public void setHeartsToLose(double heartsToLose) {
        this.heartsToLose = heartsToLose;
    }

    @Generated
    public void setShouldDropHearts(boolean shouldDropHearts) {
        this.shouldDropHearts = shouldDropHearts;
    }

}
