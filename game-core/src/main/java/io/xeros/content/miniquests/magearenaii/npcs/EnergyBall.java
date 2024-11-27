package io.xeros.content.miniquests.magearenaii.npcs;

import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;

public class EnergyBall extends NPC {

    public EnergyBall(int npcId, Position position) {
        super(npcId, position);
        this.revokeWalkingPrivilege = true;
    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        if (this.spawnedBy != entity.getIndex()) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                if (p != null)
                    p.sendMessage(this.getName()+" isn't after you.");
            }
            return false;
        }
        return true;
    }

    public int ticks;

    @Override
    public void process() {
        super.process();

    }

    /**
     * 7847 = walk
     * 7850 = death
     * 7849 = normal hit
     *
     * @param entity
     * @return
     */

    @Override
    public boolean canBeDamaged(Entity entity) {
        if (entity instanceof NPC)
            return false;
        return true;
    }

    @Override
    public boolean isFreezable() {
        return false;
    }

    @Override
    public void onDeath() {
        super.onDeath();

        int killerIndex = this.killedBy;

        Player killer = PlayerHandler.getPlayerByIndex(killerIndex);

        if (killer == null)
            return;

        killer.derwens_orbs.remove(this);

    }
}
