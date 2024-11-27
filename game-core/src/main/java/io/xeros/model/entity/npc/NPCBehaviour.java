package io.xeros.model.entity.npc;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NPCBehaviour {
    private boolean walkHome = true;
    private boolean respawn = true;
    private boolean respawnWhenPlayerOwned;
    private boolean aggressive;
    private boolean runnable = false;

    public void copy(NPCBehaviour npcBehaviour) {
        walkHome = npcBehaviour.walkHome;
        respawn = npcBehaviour.respawn;
        respawnWhenPlayerOwned = npcBehaviour.respawnWhenPlayerOwned;
        aggressive = npcBehaviour.aggressive;
        runnable = npcBehaviour.runnable;
    }

    public NPCBehaviour() {
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NPCBehaviour other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.isWalkHome() != other.isWalkHome()) return false;
        if (this.isRespawn() != other.isRespawn()) return false;
        if (this.isRespawnWhenPlayerOwned() != other.isRespawnWhenPlayerOwned()) return false;
        if (this.isAggressive() != other.isAggressive()) return false;
        return this.isRunnable() == other.isRunnable();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NPCBehaviour;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isWalkHome() ? 79 : 97);
        result = result * PRIME + (this.isRespawn() ? 79 : 97);
        result = result * PRIME + (this.isRespawnWhenPlayerOwned() ? 79 : 97);
        result = result * PRIME + (this.isAggressive() ? 79 : 97);
        result = result * PRIME + (this.isRunnable() ? 79 : 97);
        return result;
    }

    @Override
    public String toString() {
        return "NPCBehaviour(walkHome=" + this.isWalkHome() + ", respawn=" + this.isRespawn() + ", respawnWhenPlayerOwned=" + this.isRespawnWhenPlayerOwned() + ", aggressive=" + this.isAggressive() + ", runnable=" + this.isRunnable() + ")";
    }
}
