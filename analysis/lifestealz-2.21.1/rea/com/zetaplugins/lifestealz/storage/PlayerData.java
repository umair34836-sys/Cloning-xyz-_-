// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.storage;
// rea: class com.zetaplugins.lifestealz.storage.PlayerData
class PlayerData {
    int craftedHearts;
    int craftedRevives;
    long firstJoin;
    int hasBeenRevived;
    int killedOtherPlayers;
    double maxHealth;
    java.util.Set<java.lang.String> modifiedFields;
    java.lang.String name;
    java.lang.String uuid;

    public PlayerData(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid.toString();
    }

    public void clearModifiedFields() {
        this.modifiedFields.clear();
    }

    public int getCraftedHearts() {
        return this.craftedHearts;
    }

    public int getCraftedRevives() {
        return this.craftedRevives;
    }

    public long getFirstJoin() {
        return this.firstJoin;
    }

    public int getHasBeenRevived() {
        return this.hasBeenRevived;
    }

    public int getKilledOtherPlayers() {
        return this.killedOtherPlayers;
    }

    public double getMaxHealth() {
        return this.maxHealth;
    }

    public Set<String> getModifiedFields() {
        return new HashSet(this.modifiedFields);
    }

    public String getName() {
        return this.name;
    }

    public String getUuid() {
        return this.uuid;
    }

    public boolean hasChanges() {
        return !this.modifiedFields.isEmpty();
    }

    public void setCraftedHearts(int craftedHearts) {
        if (this.craftedHearts != craftedHearts) {
            this.craftedHearts = craftedHearts;
            this.modifiedFields.add("craftedHearts");
        }
    }

    public void setCraftedRevives(int craftedRevives) {
        if (this.craftedRevives != craftedRevives) {
            this.craftedRevives = craftedRevives;
            this.modifiedFields.add("craftedRevives");
        }
    }

    public void setFirstJoin(long firstJoin) {
        if (this.firstJoin != firstJoin) {
            this.firstJoin = firstJoin;
            this.modifiedFields.add("firstJoin");
        }
    }

    public void setHasBeenRevived(int hasBeenRevived) {
        if (this.hasBeenRevived != hasBeenRevived) {
            this.hasBeenRevived = hasBeenRevived;
            this.modifiedFields.add("hasbeenRevived");
        }
    }

    public void setKilledOtherPlayers(int killedOtherPlayers) {
        if (this.killedOtherPlayers != killedOtherPlayers) {
            this.killedOtherPlayers = killedOtherPlayers;
            this.modifiedFields.add("killedOtherPlayers");
        }
    }

    public void setMaxHealth(double maxHealth) {
        if (this.maxHealth != maxHealth) {
            this.maxHealth = maxHealth;
            this.modifiedFields.add("maxhp");
        }
    }

}
