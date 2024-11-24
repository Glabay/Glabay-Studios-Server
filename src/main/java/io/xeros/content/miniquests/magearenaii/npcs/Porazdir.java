package io.xeros.content.miniquests.magearenaii.npcs;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.miniquests.magearenaii.MageArenaII;
import io.xeros.content.miniquests.magearenaii.npcs.type.MageArenaBossType;
import io.xeros.model.*;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.projectile.Projectile;
import io.xeros.model.projectile.ProjectileBaseBuilder;
import io.xeros.util.Misc;

public class Porazdir extends NPC {

    public Porazdir(int npcId, Position position, Player spawnedBy) {
        super(npcId, position);
        setAttacks();
        beginSummon(spawnedBy);
    }

    public boolean finishedSpawn;

    private void beginSummon(Player spawnedBy) {
        this.spawnedBy = spawnedBy.getIndex();
        /**
         * Spawning Animation
         */
        this.startAnimation(new Animation(7842));

        spawnedBy.sendMessage("<col=ff0000>The enchanted symbol is visibly shaking and burns to the touch");
        spawnedBy.sendMessage("<col=ff0000>and warmer than last time.");

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalExecutions() == 4) {
                    container.stop();
                    finishedSpawn = true;
                    asNPC().attackEntity(spawnedBy);
                    return;
                }

            }
        }, 1);


    }

    public int ticks;

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(7838))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(15)
                        .setHitDelay(2)
                        .setMaxHit(43)
                        .setOnHit(attack -> {
                            if (attack.getCombatHit().missed())
                                return;
                            attack.getVictim().asPlayer().startGraphic(new Graphic(78));
                        })
                        .setAttackDelay(6)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 1)
                        .setAnimation(new Animation(7838))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(15)
                        .setOnAttack(attack -> {
                            createFireBall(attack.getVictim().asPlayer(), this);
                        })
                        .setOnHit(attack -> {
                            if (attack.getCombatHit().missed())
                                return;
                            attack.getVictim().asPlayer().startGraphic(new Graphic(78));
                        })
                        .setHitDelay(3)
                        .setMaxHit(43)
                        .setAttackDelay(6)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(2) == 1)
                        .setAnimation(new Animation(7840))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setHitDelay(3)
                        .setMaxHit(16)
                        .setAttackDelay(2)
                        .createNPCAutoAttack()

        ));
    }

    private void createFireBall(Player player, NPC porazdir) {

        if (isDead())
            return;

        Projectile.createTargeted(getPosition(), this.getSize(), player, new ProjectileBaseBuilder().setProjectileId(1514).setCurve(0).setSpeed(75).createProjectileBase()).send(null);

        player.sendMessage("<col=ff0000>Porazdir fires a ball of energy directly linked to his power!");

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (porazdir.isDead() || !porazdir.isRegistered() || container.getTotalExecutions() == 10) {
                    container.stop();
                    return;
                }
            }
        }, 1);

    }

    @Override
    public int modifyDamage(Player player, int damage) {
        super.modifyDamage(player, damage);
        if (player.getPosition().getDistance(this.getPosition()) > 8)
            return 0;
        return damage;
    }

    @Override
    public void process() {
        setAttacks(); // For testing
        super.process();
    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        if (!finishedSpawn)
            return false;
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

    /**
     *
     * 7838 = some shield? //common attack with flames of zamorak
     * 7839 = walk
     * 7840 = melee attack
     * 7843 = death
     *
     * @param entity
     * @return
     */

    @Override
    public boolean canBeDamaged(Entity entity) {
        if (entity instanceof NPC)
            return false;
       if (entity instanceof Player) {
            Player p = (Player) entity;
            if (p != null) {
                if (p.attacking.getCombatType() != CombatType.MAGE)
                    return false;
                if (!p.usingGodSpell()) {
                    /**
                     * Requires godspell to deal damage
                     */
                    p.sendMessage("Your spell is ineffective against "+this.getName());
                    return false;
                }
                if (p.oldSpellId != MageArenaBossType.PORAZDIR.spellRequired) {
                    p.sendMessage("This godspell is ineffective against "+this.getName());
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isFreezable() {
        return false;
    }

    @Override
    public void onDeath() {
        int killerIndex = this.killedBy;

        Player killer = PlayerHandler.getPlayerByIndex(killerIndex);

        if (killer == null)
            return;
        killer.activeMageArena2BossId[MageArenaBossType.PORAZDIR.ordinal()] = 0;
        killer.mageArenaBossKills[MageArenaBossType.PORAZDIR.ordinal()] = true;
        Server.itemHandler.createGroundItem(killer, new GameItem(MageArenaII.DEMONS_HEART, 1), this.getPosition());
    }
}
