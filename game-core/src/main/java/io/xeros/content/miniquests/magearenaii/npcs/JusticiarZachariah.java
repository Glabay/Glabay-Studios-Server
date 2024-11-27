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
import io.xeros.model.projectile.ProjectileBaseBuilder;
import io.xeros.util.Misc;

public class JusticiarZachariah extends NPC {

    public JusticiarZachariah(int npcId, Position position, Player spawnedBy) {
        super(npcId, position);
        setAttacks();
        beginSummon(spawnedBy);
    }

    private void beginSummon(Player player) {
        this.spawnedBy = player.getIndex();
        /**
         * Spawning Animation
         */
        this.startAnimation(new Animation(7964));

        player.sendMessage("<col=ff0000>The enchanted symbol is visibly shaking and burns to the touch");
        player.sendMessage("<col=ff0000>and warmer than last time.");

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalExecutions() == 4) {
                    container.stop();
                    finishedSpawn = true;
                    asNPC().attackEntity(player);
                    return;
                }

            }
        }, 1);
    }

    public boolean finishedSpawn;

    public int ticks;

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(7962))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(20)
                        .setHitDelay(2)
                        .setMaxHit(43)
                        .setOnHit(attack -> {
                            if (attack.getCombatHit().missed())
                                return;
                            attack.getVictim().startGraphic(new Graphic(76));
                        })
                        .setAttackDelay(5)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(20)
                        .setHitDelay(7)
                        .setMaxHit(30)
                        .setAttackDelay(8)
                        .setOnAttack(attack -> {
                            attack.getVictim().startGraphic(new Graphic(76));
                            createDragEffect(attack.getVictim().asPlayer(), this);
                        })
                        .setAnimation(new Animation(7962))
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(2) == 1)
                        .setAnimation(new Animation(7853))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setHitDelay(2)
                        .setMaxHit(26)
                        .setAttackDelay(3)
                        .createNPCAutoAttack()

        ));
    }

    private void createDragEffect(Player player, JusticiarZachariah npc) {

        if (isDead())
            return;

        if (npc.getPosition().getDistance(player.getPosition()) < 3) {
            /**
             * FORCE SKIP
             */
            return;
        }

        Position savedPosition = player.getPosition();

        new ProjectileBaseBuilder().setProjectileId(1515).setSpeed(85).setStartHeight(80).setEndHeight(30).setSendDelay(1).createProjectileBase()
                .createTargetedProjectile(this, savedPosition).send(player.getInstance());

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (npc.isDead() || !npc.isRegistered() || container.getTotalExecutions() == 7) {
                    container.stop();
                    return;
                }
                if (container.getTotalExecutions() == 3) {
                    if (player.getPosition().equals(savedPosition)) {
                        npc.freezeTimer = 10;
                        player.forcedChat("Nooo!");
                        player.startAnimation(1157);
                        player.setForceMovement(npc.getX(), npc.getY(), 0, 75, null, 65535);
                        player.faceEntity(npc);
                    }
                }
                if (player.getPosition().equals(npc.getPosition())) {
                    npc.startAnimation(new Animation(7962));
                    container.stop();
                }
            }
        }, 1);

    }

    @Override
    public void process() {
        setAttacks();
        super.process();

    }

    /**
     * 7863 = sword anim
     * 7852 = walk
     *
     * 7963 = special
     *
     * 7854 = death anim
     * 7853 = sword hit after special
     *
     * 7963 = sword
     * 7962 = mage
     *
     *
     *
     *
     * @param entity
     * @return
     */

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
                if (p.oldSpellId != MageArenaBossType.JUSTICIAR_ZACHARIAH.spellRequired) {
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
        killer.activeMageArena2BossId[MageArenaBossType.JUSTICIAR_ZACHARIAH.ordinal()] = 0;
        killer.mageArenaBossKills[MageArenaBossType.JUSTICIAR_ZACHARIAH.ordinal()] = true;
        Server.itemHandler.createGroundItem(killer, new GameItem(MageArenaII.JUSTICIARS_HAND, 1), this.getPosition());
    }
}
