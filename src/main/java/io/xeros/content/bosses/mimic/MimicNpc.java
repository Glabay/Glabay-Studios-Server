package io.xeros.content.bosses.mimic;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.Direction;
import io.xeros.model.Npcs;
import io.xeros.model.ProjectileBaseBuilder;
import io.xeros.model.StillGraphic;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCDumbPathFinder;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.List;
import java.util.Objects;

public class MimicNpc extends NPC {

    public static int WARRIOR = 8635;
    public static int RANGER = 8636;
    public static int MAGER = 8637;

    private boolean minionSpawn;
    private boolean movingUnder;
    private Position walkTo;

    public MimicNpc(int id, Position pos) {
        super(id, pos);
        if (id != Npcs.THE_MIMIC_8633) {
            getBehaviour().setAggressive(true);
            if (id == WARRIOR) {
                setNpcAutoAttacks(Lists.newArrayList(
                        new ThirdAgeWarrior().apply(this)
                ));
            } else if (id == RANGER) {
                setNpcAutoAttacks(Lists.newArrayList(
                        new ThirdAgeRanger().apply(this)
                ));
            } else if (id == MAGER) {
                setNpcAutoAttacks(Lists.newArrayList(
                        new ThirdAgeMager().apply(this)
                ));
            }
        } else {
            getBehaviour().setAggressive(false);
            setNpcAutoAttacks(Lists.newArrayList(
                    new MimicMelee().apply(this)
            ));
        }
        getBehaviour().setRespawn(false);
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
        if (getNpcId() != Npcs.THE_MIMIC_8633)
            return true;
        return !movingUnder;
    }

    @Override
    public void process() {
        try {
            if (getNpcId() == Npcs.THE_MIMIC_8633) {
                if (attackTimer <= 1 && freezeTimer <= 0) {
                    if (walkTo != null) {
                        boolean noReach = !NPCDumbPathFinder.canMoveTo(asNPC(), Direction.fromDeltas(getPosition(), walkTo).toInteger());
                        if (walkDirection == Direction.NONE || noReach) {
                            executeStomp();
                            walkTo = null;
                        }
                        return;
                    }
                    if (processCandyAttack())
                        return;
                    if (processStomp())
                        return;
                }
            }
            super.process();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            unregister();
        }
    }

    private void executeStomp() {
        if (getInstance().getPlayers().size() <= 0)
            return;
        Player target = this.getInstance().getPlayers().get(0);
        if (target == null) {
            return;
        }
        if (walkTo == null)
            return;
        resetAttack();
        attackTimer = 20;
        movingUnder = false;
        final Position walk = walkTo;
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            int counter = 18;
            int dmg = 1;
            @Override
            public void execute(CycleEventContainer container) {
                boolean noReach = !NPCDumbPathFinder.canMoveTo(asNPC(), Direction.fromDeltas(getPosition(), walk).toInteger())
                        && !getPosition().withinDistance(target.getPosition(), 2);
                if (isDead || target.isDead || !getPosition().withinDistance(target.getPosition(), 1) && !noReach) {
                    container.stop();
                    getBehaviour().setAggressive(true);
                    attackTimer = 2;
                    attackEntity(target);
                    walkTo = null;
                    return;
                }
                counter--;
                if (counter <= 0) {
                    container.stop();
                    getBehaviour().setAggressive(true);
                    attackTimer = 0;
                    attackEntity(target);
                    walkTo = null;
                    return;
                }
                target.appendDamage(dmg, Hitmark.HIT);
                if (counter != 0 && (counter % 3) == 0) {
                    dmg++;
                }
            }
        }, 1);
    }

    private boolean processStomp() {
        if (getInstance().getPlayers().size() <= 0)
            return false;
        Player target = getInstance().getPlayers().get(0);
        if (target != null) {
            if (walkTo == null && Misc.random(4) == 1) {
                resetAttack();
                getBehaviour().setAggressive(false);
                movingUnder = true;
                walkTo = target.getPosition();
                moveTowards(walkTo.getX(), walkTo.getY(), true);
                /*Direction dir = Direction.fromDeltas(getPosition(), walkTo);
                if (getRegionProvider().canMove(walkTo.getX(), walkTo.getY(), getHeight(), dir.toInteger())) {
                    moveTowards(walkTo.getX(), walkTo.getY(), true);
                } else {
                    walkTo = target.getAdjacentPosition();
                }*/
                return true;
            }
        }
        return false;
    }

    private boolean processCandyAttack() {
        if (getInstance().getPlayers().size() <= 0)
            return false;
        if (Misc.random(4) == 1) {
            resetAttack();
            freezeTimer = 7;
            attackTimer = 7;
            startAnimation(8309);
            final NPC npc = (this);
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    container.stop();
                    if (getInstance() == null) {
                        System.err.println("Instance was null for mimic.");
                        container.stop();
                        return;
                    }
                    if (getInstance().getPlayers().size() <= 0)
                        return;
                    startAnimation(8309);
                    Player target = getInstance().getPlayers().get(0);
                    if (target != null && !target.isDead) {
                        List<Position> placements = Lists.newArrayList();
                        for (int i = 0; i < 5; i++) {
                            Position pos = new Position(target.getX() + Misc.random(-4, 4), target.getY() + Misc.random(-4, 4), getInstance().getHeight());
                            while (!PathChecker.raycast(npc, target, true)) {
                                pos = new Position(target.getX() + Misc.random(-4, 4), target.getY() + Misc.random(-4, 4), getInstance().getHeight());
                            }
                            placements.add(pos);
                        }
                        placements.add(target.getPosition());
                        int projSpeed = 60;
                        int delayAmount = 2;
                        for (int i = 0; i < 3; i++) {
                            new ProjectileBaseBuilder().setProjectileId(1670).setSpeed(projSpeed).setSendDelay(delayAmount).createProjectileBase()
                                    .createTargetedProjectile(npc, placements.get(i)).send(getInstance());
                        }
                        new ProjectileBaseBuilder().setProjectileId(1673).setSpeed(projSpeed).setSendDelay(delayAmount).createProjectileBase()
                                .createTargetedProjectile(npc, placements.get(3)).send(getInstance());
                        new ProjectileBaseBuilder().setProjectileId(1672).setSpeed(projSpeed).setSendDelay(delayAmount).createProjectileBase()
                                .createTargetedProjectile(npc, placements.get(4)).send(getInstance());
                        new ProjectileBaseBuilder().setProjectileId(1671).setSpeed(projSpeed).setSendDelay(delayAmount).createProjectileBase()
                                .createTargetedProjectile(npc, placements.get(5)).send(getInstance());

                        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                if (isDead) {
                                    container.stop();
                                    return;
                                }
                                if (container.getTotalExecutions() >= 4) {
                                    Server.playerHandler.sendStillGfx(new StillGraphic(1674, placements.get(0)), target.getInstance());
                                    Server.playerHandler.sendStillGfx(new StillGraphic(1674, placements.get(1)), target.getInstance());
                                    Server.playerHandler.sendStillGfx(new StillGraphic(1674, placements.get(2)), target.getInstance());
                                    Server.playerHandler.sendStillGfx(new StillGraphic(1677, placements.get(3)), target.getInstance());
                                    Server.playerHandler.sendStillGfx(new StillGraphic(1676, placements.get(4)), target.getInstance());
                                    Server.playerHandler.sendStillGfx(new StillGraphic(1675, placements.get(5)), target.getInstance());
                                    if (!getInstance().getNpcs().stream().filter(Objects::nonNull).anyMatch(n -> n.getNpcId() == WARRIOR)) {
                                        MimicNpc warrior = new MimicNpc(WARRIOR, placements.get(4));
                                        getInstance().add(warrior);
                                    }
                                    if (!getInstance().getNpcs().stream().filter(Objects::nonNull).anyMatch(n -> n.getNpcId() == MAGER)) {
                                        MimicNpc warrior = new MimicNpc(MAGER, placements.get(3));
                                        getInstance().add(warrior);
                                    }
                                    if (!getInstance().getNpcs().stream().filter(Objects::nonNull).anyMatch(n -> n.getNpcId() == RANGER)) {
                                        MimicNpc warrior = new MimicNpc(RANGER, placements.get(5));
                                        getInstance().add(warrior);
                                    }
                                    getInstance().getPlayers().forEach(plr -> {
                                        if (placements.stream().anyMatch(pos -> plr.getPosition().equals(pos))) {
                                            plr.appendDamage(10, Hitmark.HIT);
                                        }
                                    });
                                    container.stop();
                                }
                            }
                        }, 1);
                    }
                }
            }, 3);
            return true;
        }
        return false;
    }

    @Override
    public void onDeath() {
        if (this.getName().equalsIgnoreCase("the mimic")) {
            despawnMinions();
        }
        super.onDeath();
    }

    private void despawnMinions() {
        NPCHandler.despawn(WARRIOR, getHeight());
        NPCHandler.despawn(RANGER, getHeight());
        NPCHandler.despawn(MAGER, getHeight());
    }
}
