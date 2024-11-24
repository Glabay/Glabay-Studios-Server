package io.xeros.content.miniquests.magearenaii.npcs;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
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

public class Derwen extends NPC {

    public boolean finishedSpawn;

    private Player spawner;

    public Derwen(int npcId, Position position, Player spawner) {
        super(npcId, position);
        setAttacks();
        beginSummon(spawner);
    }

    private void beginSummon(Player spawnedBy) {
        this.spawnedBy = spawnedBy.getIndex();
        this.spawner = spawnedBy;
        /**
         * Spawning Animation
         */
        this.startAnimation(new Animation(7844));

        spawnedBy.sendMessage("<col=ff0000>The enchanted symbol is visibly shaking and burns to the touch");
        spawnedBy.sendMessage("<col=ff0000>and warmer than last time.");

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalExecutions() == 5) {
                    container.stop();
                    finishedSpawn = true;
                    asNPC().attackEntity(spawnedBy);
                    return;
                }

            }
        }, 1);
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(7849))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(15)
                        .setHitDelay(2)
                        .setMaxHit(43)
                        .setAttackDelay(6)
                        .setOnHit(attack -> {
                            if (attack.getCombatHit().missed())
                                return;
                            attack.getVictim().startGraphic(new Graphic(1511, 100));
                        })
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setAnimation(new Animation(7849))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(10)
                        .setOnAttack(attack -> {
                            createHealingEffect(attack.getVictim().asPlayer(), this);
                        })
                        .setHitDelay(2)
                        .setMaxHit(43)
                        .setAttackDelay(6)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 2)
                        .setAnimation(new Animation(7849))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setHitDelay(3)
                        .setMaxHit(16)
                        .setAttackDelay(2)
                        .createNPCAutoAttack()

        ));
    }

    private void createHealingEffect(Player player, NPC npc) {

        if (isDead())
            return;

        Position pos = new Position(Misc.random(player.getX() - 2, player.getX() + 2), Misc.random(player.getY() - 2, player.getY() + 2), 0);

        Projectile.createTargeted(npc, pos, new ProjectileBaseBuilder().setProjectileId(1513).setCurve(0).setSpeed(30).createProjectileBase()).send(null);

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (npc.isDead() || !npc.isRegistered() || container.getTotalExecutions() == 10) {
                        container.stop();
                        return;
                    }

                    if (container.getTotalExecutions() == 3) {
                        NPC npc = new EnergyBall(7514, pos);
                        npc.spawnedBy = player.getIndex();
                        npc.parentIndex = npc.getIndex();
                        player.derwens_orbs.add(npc);
                    }
                }
            }, 1);
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

    public int ticks;

    @Override
    public void process() {
        setAttacks(); // For testing
        super.process();

        if (this.isDead())
            return;
        /**
         * Healing process
         */
        if (++ticks == 10) {
            ticks = 0;
            if (this.spawner.derwens_orbs.size() > 0) {
                int totalHeal = this.spawner.derwens_orbs.size() * 5;
                this.appendHeal(totalHeal, Hitmark.NPC_HEAL);
            }
        }
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
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (p != null) {
                //System.err.println(p.attacking.getCombatType()+" usingMagic="+p.usingMagic+" spellId="+p.getSpellId());
                if (!p.usingMagic) {
                    p.sendMessage("Your attack style is ineffective against "+this.getName());
                    return false;
                }
                if (!p.usingGodSpell()) {
                    /**
                     * Requires godspell to deal damage
                     */
                    p.sendMessage("Your spell is ineffective against "+this.getName());
                    return false;
                }
                if (p.oldSpellId != MageArenaBossType.DERWEN.spellRequired) {
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
        killer.activeMageArena2BossId[MageArenaBossType.DERWEN.ordinal()] = 0;
        killer.mageArenaBossKills[MageArenaBossType.DERWEN.ordinal()] = true;
        Server.itemHandler.createGroundItem(killer, new GameItem(MageArenaII.ENTS_ROOT, 1), this.getPosition());
        killer.clearDerwensOrbs();
    }
}
