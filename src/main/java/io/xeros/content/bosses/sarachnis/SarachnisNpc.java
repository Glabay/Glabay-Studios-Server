package io.xeros.content.bosses.sarachnis;

import com.google.common.collect.Lists;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;



public class SarachnisNpc extends NPC {

    public static final Position SPAWN_POSITION = new Position(1842, 9902, 0);

    private boolean minionSpawn;
    public int attackCounter;
    public Position nextWalk;

    public SarachnisNpc(int id, Position pos) {
        super(id, pos);
        resetForSpawn();
    }

    public void resetForSpawn() {
        walkingType = 1;
        nextWalk = null;
        attackCounter = 0;
        getBehaviour().setWalkHome(false);
        getBehaviour().setAggressive(true);
        if (getNpcId() == Npcs.SARACHNIS) {
            getBehaviour().setRespawn(true);
        } else {
            getBehaviour().setRespawn(false);
        }
        if (getNpcId() == Npcs.SPAWN_OF_SARACHNIS) {
            setNpcAutoAttacks(Lists.newArrayList(
                    new SarachnisMinionMelee().apply(this)
            ));
        } else if (getNpcId() == Npcs.SPAWN_OF_SARACHNIS_8715) {
            setNpcAutoAttacks(Lists.newArrayList(
                    new SarachnisMinionMage().apply(this)
            ));
        } else {
            setNpcAutoAttacks(Lists.newArrayList(
                    new SarachnisMelee().apply(this),
                    new SarachnisRanged().apply(this)
            ));
        }
    }

    @Override
    public boolean susceptibleTo(HealthStatus status) {
        return false;
    }

    @Override
    public boolean hasBlockAnimation() {
        return false;
    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        return !isDead();
    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        return !isDead();
    }

    @Override
    public boolean isAutoRetaliate() {
        return getBehaviour().isAggressive() && !isDead() && nextWalk == null;
    }

    @Override
    public void process() {
        try {
            if (this.getNpcId() == Npcs.SARACHNIS) {
                if (attackCounter >= 5) {
                    attackCounter = 0;
                    int p = getPlayerAttackingIndex();
                    if (PlayerHandler.players[p] != null) {
                        Player player = PlayerHandler.players[p];
                        this.attack(player, new SarachnisWeb().apply(this));
                    }
                }
                if (this.getHealth().getCurrentHealth() <= (this.getHealth().getMaximumHealth() * 0.66) && !minionSpawn ||
                        this.getHealth().getCurrentHealth() <= (this.getHealth().getMaximumHealth() * 0.33) && !minionSpawn) {
                    minionSpawn = true;
                    spawnMinions();
                }
                if (nextWalk != null) {
                    if (getPosition().equals(nextWalk)) {
                        nextWalk = null;
                        getBehaviour().setRunnable(false);
                        getBehaviour().setAggressive(true);
                    } else {
                        getBehaviour().setRunnable(true);
                        moveTowards(nextWalk.getX(),nextWalk.getY(),false);
                    }
                }
            }
            processCombat();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            unregister();
        }
    }

    private void processCombat() {
        super.process();
    }

    @Override
    public NPC provideRespawnInstance() {
        NPC boss = new SarachnisNpc(Npcs.SARACHNIS, SPAWN_POSITION);
        boss.getBehaviour().setAggressive(true);
        return boss;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        if (!this.getName().equalsIgnoreCase("sarachnis")) {
            if (minionSpawn && minionsDead()) {
                minionSpawn = false;
            }
        } else {
            resetForSpawn();
            minionSpawn = false;
            despawnMinions();
        }
    }

    private void spawnMinions() {
        if (minionSpawn) {
            new SarachnisNpc(Npcs.SPAWN_OF_SARACHNIS, SPAWN_POSITION);
            new SarachnisNpc(Npcs.SPAWN_OF_SARACHNIS_8715, SPAWN_POSITION);
        }
    }

    private void despawnMinions() {
        NPCHandler.despawn(Npcs.SPAWN_OF_SARACHNIS, getHeight());
        NPCHandler.despawn(Npcs.SPAWN_OF_SARACHNIS_8715, getHeight());
    }

    private boolean minionsDead() {
        return NPCHandler.getNpc(Npcs.SPAWN_OF_SARACHNIS, getHeight()) == null || NPCHandler.getNpc(Npcs.SPAWN_OF_SARACHNIS_8715, getHeight()) == null;
    }
}
