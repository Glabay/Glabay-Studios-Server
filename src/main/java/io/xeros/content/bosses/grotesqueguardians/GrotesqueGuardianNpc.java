package io.xeros.content.bosses.grotesqueguardians;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.*;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definition.Npcs;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.projectile.ProjectileBaseBuilder;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GrotesqueGuardianNpc extends NPC {

    private static final Logger logger = LoggerFactory.getLogger(GrotesqueGuardianNpc.class);

    private boolean executingDuskRocks = false;
    public boolean executeDawnEnergy = false;
    public boolean executingDawnStoneBall = false;
    public boolean executingPurpleFlames = false;
    public boolean executingDuskCharge = false;
    public int attackCounter = 0;
    private final GrotesqueInstance instance;
    private GrotesqueGuardianNpc counterpart;

    public GrotesqueGuardianNpc(int id, Position pos, GrotesqueInstance instance) {
        super(id, pos);
        getBehaviour().setAggressive(true);
        getBehaviour().setRespawn(false);
        if (id == Npcs.DUSK_7851) {
            setNpcAutoAttacks(Lists.newArrayList(
                    new DuskMelee().apply(this)
            ));
        } else if (id == Npcs.DAWN_7852) {
            setNpcAutoAttacks(Lists.newArrayList(
                    new DawnMelee().apply(this),
                    new DawnRanged().apply(this)
            ));
        }

        this.instance = instance;
        instance.add(this);
        reset();
    }

    public void setCounterpart(GrotesqueGuardianNpc counterpart) {
        this.counterpart = counterpart;
    }

    private void debug(String message, Object...args) {
        logger.debug("[{} - {}]" + message, getDefinition().getName(), getNpcId(), args);
    }

    public void reset() {
        debug("Reset");
        executingDuskRocks = false;
        executeDawnEnergy = false;
        executingDawnStoneBall = false;
        executingPurpleFlames = false;
        executingDuskCharge = false;
        attackCounter = 0;
        setInvisible(false);
        toggleAttacking(true);
    }

    public boolean hasExecutedSpecial() {
        return executingDuskCharge || executingDuskRocks || executingPurpleFlames;
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
        if (getName().equalsIgnoreCase("dusk")) {
            if (inPhase(1) || inPhase(3)) {
                return false;
            } else if (inPhase(2) || inPhase(4)) {
                if (entity.isPlayer()) {
                    Player p = (Player) entity;
                    return p.usingMelee;
                }
            }
        } else if (getName().equalsIgnoreCase("dawn")) {
            if (entity.isPlayer()) {
                Player p = (Player) entity;
                return !p.usingMelee && !p.usingMagic;
            }
        }
        return !isDead() && !isInvisible();
    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        Player p = null;
        if (entity.isPlayer()) {
            p = (Player) entity;
        }
        if (p == null)
            return false;
        if (getName().equalsIgnoreCase("dusk")) {
            if (inPhase(1) || inPhase(3)) {
                p.sendMessage("Dusk is defending himself with his wing! He absorbs the attack.");
            }
        }
        if (getNpcId() == Npcs.DAWN_7853 || getNpcId() == Npcs.DUSK_7854)
            return false;
        if (getName().equalsIgnoreCase("dawn")) {
            if (instance.dawnFlownAway)
                return false;
            if (p.usingMelee) {
                p.sendMessage("You can't reach her with melee!");
                return false;
            }
        }
        if (isInLightningAttack())
            return false;
        return !isDead() && !isInvisible();
    }

    @Override
    public boolean isAutoRetaliate() {
        if (getName().equalsIgnoreCase("dusk")) {
            if (executingPurpleFlames || executingDuskRocks || executingDuskCharge)
                return false;
        }
        if (isInLightningAttack())
            return false;
        return !isDead() && !isInvisible() && getBehaviour().isAggressive();
    }

    public Player target;

    @Override
    public void process() {
        if (getInstance() == null || getInstance() != instance) {
            debug("Instance is null or not equal to original instance, disposing.");
            unregister();
            instance.dispose();
            return;
        }

        if (instance.getPlayers().size() <= 0) {
            debug("Instance or players is null, unregistering.");
            unregister();
            return;
        }
        if (isInvisible())
            return;
        if (target == null) {
            target = instance.getPlayers().get(0);
            debug("Target is null, resetting target to {}", target);
        }
        if (target == null) {
            debug("No target was found, discarding fight.");
            unregister();
            return;
        }
        if (getName().equalsIgnoreCase("dusk")) {
            if (inPhase(2)) {
                int halfHp = (int) (getHealth().getMaximumHealth() * 0.5);
                if (getHealth().getCurrentHealth() <= halfHp && instance.dawnFlownAway) {
                    debug("Dawn has flown away and half hp, phasing.");
                    target.attacking.reset();
                    instance.phase++;
                    instance.dawnFlownAway = false;
                    counterpart.setInvisible(false);
                    counterpart.teleport(1701, 4573,getHeight());
                    counterpart.startAnimation(7774);
                    counterpart.requestTransform(Npcs.DAWN_7853);
                    //stop dusk
                    requestTransform(Npcs.DUSK_7854);
                    toggleLightning(true);
                    toggleAttacking(false);
                    startAnimation(2851);
                    counterpart.toggleLightning(true);
                    CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (target.isDead) {
                                container.stop();
                                debug("Target is done, stopping cycle event.");
                                return;
                            }
                            if (container.getTotalTicks() >= 3) {
                                if (getPosition().getX() == 1691 && getPosition().getY() == 4573) {
                                    container.stop();
                                    initLightningAttack(target);
                                } else {
                                    moveTowards(1691, 4573,false,false);
                                }
                            }
                        }
                    }, 1);
                }
            }
            //dusk attacks
            if (attackCounter <= 0) {
                if (inPhase(2)) {
                    if (Misc.random(4) == 1) {
                        duskRocksEffect(target);
                    }
                }
                int rand = Misc.random(1,6);
                if (rand == 1)
                    duskTrampleAttack(target);
                else if (rand == 2)
                    initDuskPurpleFlames(target);
            }
            if (attackCounter > 0) {
                attackCounter--;
            }
            if (hasExecutedSpecial()) {
                attackCounter = Misc.random(2,6);
            }
        } else if (getName().equalsIgnoreCase("dawn")) {
            if (inPhase(1)) {
                int halfHp = (int) (getHealth().getMaximumHealth() * 0.5);
                if (getHealth().getCurrentHealth() <= halfHp && !instance.dawnFlownAway) {
                    debug("Flying away, phasing.");
                    instance.dawnFlownAway = true;
                    toggleAttacking(false);
                    startAnimation(7773);
                    instance.phase++;
                    target.attacking.reset();
                    CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (isDead || target.isDead) {
                                container.stop();
                                return;
                            }
                            if (container.getTotalTicks() >= 2) {
                                container.stop();
                                setInvisible(true);
                                target.attacking.reset();
                            }
                        }
                    }, 1);
                }
            } else if (inPhase(3)) {
                if (executeDawnEnergy && !isDead()) {
                    debug("Executing dawn energy, tick {}", instance.dawnEnergyTicks);
                    if (instance.dawnEnergyTicks >= 40) {
                        executeDawnEnergy = false;
                        instance.clearOrbsWithDamageAndHeal(this, target);
                    } else {
                        instance.dawnEnergyTicks++;
                        instance.updateOrbs();
                    }
                }
            }
            if (!isInLightningAttack()) {
                if (attackTimer >= 6 && getAttackType().equals(CombatType.RANGE)) {
                    attack(target, new DawnRanged().apply(this));
                    attackTimer = 6;
                    dawnEnergyBalls(target);
                }
            }
            dawnStoneBallAttack(target);
        }
        processCombat();
    }

    private void processCombat() {
        super.process();
    }

    @Override
    public void onDeath() {
        super.onDeath();
        if (getName().equalsIgnoreCase("dawn") && inPhase(3)) {//4th & final phase when dawn dies
            debug("dead, phasing.");
            instance.phase++;
            counterpart.toggleAttacking(false);
            instance.clearOrbs();
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (container.getTotalTicks() >= 5) {
                        container.stop();
                        counterpart.requestTransform(Npcs.DUSK_7887);
                        counterpart.startAnimation(7796);
                        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                if (container.getTotalTicks() >= 12) {
                                    container.stop();
                                    counterpart.requestTransform(Npcs.DUSK_7888);
                                    counterpart.setNpcAutoAttacks(Lists.newArrayList(
                                            new DuskMelee().apply(counterpart),
                                            new DuskRanged().apply(counterpart)
                                    ));
                                    counterpart.toggleAttacking(true);
                                }
                            }
                        }, 1);
                    }
                }
            }, 1);
        }
    }

    public boolean inPhase(int type) {
        return instance.phase == type;
    }

    public void increasePhase(int a) {
        instance.phase += a;
    }

    public void initLightningAttack(Player target) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalTicks() == 10) {
                    //startAnimation(8758);
                }
                if (container.getTotalTicks() >= 14) {
                    container.stop();
                    toggleLightning(false);
                    counterpart.toggleLightning(false);
                    requestTransform(Npcs.DUSK_7883);
                    toggleAttacking(true);
                    counterpart.requestTransform(Npcs.DAWN_7884);
                    counterpart.toggleAttacking(true);
                }
            }
        }, 1);
        counterpart.attackTimer = 6;
        counterpart.startAnimation(7772);
        startAnimation(7792);
        attackTimer = 6;
        List<Position> dusk_placements = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int randX = Misc.random(1689, 1704);
            int randY = Misc.random(4567, 4582);
            dusk_placements.add(new Position(randX,randY,getHeight()));
        }
        dusk_placements.add(target.getPosition());
        for (Position pos : dusk_placements) {
            Server.playerHandler.sendStillGfx(new StillGraphic(1416, pos), instance);
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                int counter = 14;
                @Override
                public void execute(CycleEventContainer container) {
                    counter--;
                    if (counter <= 0) {
                        container.stop();
                        return;
                    }
                    if (target.getPosition().withinDistance(pos, 1)) {
                        target.appendDamage(Misc.random(5,10), Hitmark.HIT);
                    }
                }
            }, 1);
        }
        //dawn placements
        List<Position> dawn_placements = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int randX = Misc.random(1689, 1704);
            int randY = Misc.random(4567, 4582);
            dawn_placements.add(new Position(randX,randY,getHeight()));
        }
        dawn_placements.add(target.getPosition());
        for (Position pos : dawn_placements) {
            Server.playerHandler.sendStillGfx(new StillGraphic(1424, pos), instance);
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                int counter = 14;
                @Override
                public void execute(CycleEventContainer container) {
                    counter--;
                    if (counter <= 0) {
                        container.stop();
                        return;
                    }
                    if (target.getPosition().withinDistance(pos, 1)) {
                        target.appendDamage(Misc.random(5,10), Hitmark.HIT);
                    }
                }
            }, 1);
        }
    }

    public void duskTrampleAttack(Player target) {
        if (!(inPhase(2) || inPhase(4))) {
            return;
        }
        if (hasExecutedSpecial())
            return;
        if (target.freezeTimer > 0)
            return;
        if (attackTimer > 1)
            return;
        if (!(getDistance(target.getX(), target.getY()) <= 1))
            return;
        executingDuskCharge = true;
        toggleAttacking(false);
        startAnimation(7802);
        final Position targetPos = target.getPosition();
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (target.isDead || getDistance(target.getX(), target.getY()) >= 24) {
                    container.stop();
                    return;
                }
                if (container.getTotalTicks() >= 4) {
                    container.stop();
                    if (getDistance(target.getX(), target.getY()) <= 1) {
                        target.attacking.reset();
                        target.resetWalkingQueue();
                        target.getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS, 600 * 10);
                        target.freezeTimer = 10;
                        target.attackTimer = 10;
                        boolean south = false;
                        if (!target.getRegionProvider().blockedNorth(target.absX,target.absY + 3,getHeight(), false)) {
                            target.setForceMovement(target.absX, target.absY + 4, 0, 60, "NORTH", 368);
                        } else if (!target.getRegionProvider().blockedSouth(target.absX, target.absY - 3,getHeight(), false)) {
                            target.setForceMovement(target.absX, target.absY - 4, 0, 60, "SOUTH", 368);
                            south = true;
                        }
                        target.stopPlayerPacket = true;
                        final boolean movingS = south;
                        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                if (target.isDead || getDistance(target.getX(), target.getY()) >= 24) {
                                    container.stop();
                                    return;
                                }
                                if (container.getTotalTicks() == 3) {
                                    target.resetWalkingQueue();
                                    target.setTeleportToX(target.getX());
                                    target.setTeleportToY(movingS ? target.getY() - 3 : target.getY() + 3);
                                    target.getPA().requestUpdates();
                                    target.startAnimation(785);
                                } else if (container.getTotalTicks() == 5) {
                                    target.appendDamage(asNPC(), Misc.random(30,33), Hitmark.HIT);
                                    target.getPA().sendFrame36(173, 1);
                                    target.playerWalkIndex = 0x333;
                                    target.getPA().requestUpdates();
                                    target.stopPlayerPacket = false;
                                    executingDuskCharge = false;
                                    toggleAttacking(true);
                                    container.stop();
                                }
                            }
                        }, 1);
                    } else {
                        executingDuskCharge = false;
                        toggleAttacking(true);
                    }
                }
            }
        }, 1);
    }

    public static final Position[] FLAME_POSITIONS = {
            new Position(1692, 4570,0),
            new Position(1700, 4571, 0),
            new Position(1700, 4577, 0)
    };

    public void initDuskPurpleFlames(Player target) {
        if (!inPhase(4))
            return;
        if (executingDuskRocks || executingPurpleFlames || executingDuskCharge)
            return;
        if (attackTimer > 1)
            return;
        executingPurpleFlames = true;
        toggleAttacking(false);
        setAttackType(CombatType.SPECIAL);
        attackTimer = 8;
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            int count = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (target.isDead || getDistance(target.getX(), target.getY()) >= 24) {
                    container.stop();
                    return;
                }
                if (getPosition().getX() == 1692 && getPosition().getY() == 4577) {
                    if (count == 0) {
                        count++;
                    } else {
                        container.stop();
                        duskPurpleFlames(target);
                    }
                    return;
                } else {
                    moveTowards(1692, 4577, false, false);
                }
            }
        }, 1);
    }

    public void duskPurpleFlames(Player target) {
        attackTimer = 12;
        setAttackType(CombatType.MAGE);
        startAnimation(7796);
        final Position flameCenter = FLAME_POSITIONS[Misc.random(FLAME_POSITIONS.length - 1)];
        final Position center = new Position(flameCenter.getX(), flameCenter.getY(), getHeight());
        final Position northFlameTile = new Position(center.getX() + 2, center.getY(), center.getHeight());
        final Position playersPos = new Position(northFlameTile.getX(), northFlameTile.getY() + 2, northFlameTile.getHeight());
        Direction dir = Direction.fromDeltas(getPosition(), playersPos);
        if (dir == null) {
            dir = Direction.SOUTH;
        }
        String s = dir.name().replace("_", "-");
        int speedtwo = 30;
        if (target.distance(playersPos) > 3) {
            speedtwo = 40;
        }
        target.resetWalkingQueue();
        target.attacking.reset();
        target.stopPlayerPacket = true;
        target.forcedChat("Arghhh!");
        target.setForceMovement(playersPos.getX(), playersPos.getY(), 0, speedtwo, s, -1);
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (isDead() || target.isDead || getDistance(target.getX(), target.getY()) >= 24) {
                    container.stop();
                    return;
                }
                if (container.getTotalTicks() <= 1) {
                    target.startAnimation(1157);
                } else if (container.getTotalTicks() == 3) {
                    target.resetWalkingQueue();
                    target.stopPlayerPacket = false;
                    target.playerWalkIndex = 0x333;
                    target.setTeleportToX(playersPos.getX());
                    target.setTeleportToY(playersPos.getY());
                    target.getPA().sendFrame36(173, 1);
                    target.getPA().requestUpdates();
                    target.startAnimation(785);
                    //outter tiles
                    for (int x = 0; x < 5; x++) {
                        for (int y = 0; y < 5; y++) {
                            if (x == 0 || x == 4 || y == 0 || y == 4) {
                                Position placement = new Position(center.getX() + x, center.getY() + y, getHeight());
                                if (placement.equals(northFlameTile)) {
                                    continue;
                                }
                                target.getRegionProvider().get(placement.getX(), placement.getY()).addClip(placement.getX(),placement.getY(), getHeight(), 0x200000);
                                Server.playerHandler.sendStillGfx(new StillGraphic(1434, placement), instance);
                                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                                    @Override
                                    public void execute(CycleEventContainer container) {
                                        if (isDead || target.isDead) {
                                            container.stop();
                                            if ((target.getRegionProvider().get(placement.getX(), placement.getY()).getClip(placement.getX(),placement.getY(), getHeight()) & 0x200000) != 0)
                                                target.getRegionProvider().get(placement.getX(), placement.getY()).removeClip(placement.getX(),placement.getY(), getHeight(), 0x200000);
                                            return;
                                        }
                                        if (container.getTotalTicks() >= 8) {
                                            container.stop();
                                            if ((target.getRegionProvider().get(placement.getX(), placement.getY()).getClip(placement.getX(),placement.getY(), getHeight()) & 0x200000) != 0)
                                                target.getRegionProvider().get(placement.getX(), placement.getY()).removeClip(placement.getX(),placement.getY(), getHeight(), 0x200000);
                                        }
                                    }
                                }, 1);
                            }
                        }
                    }
                } else if (container.getTotalTicks() >= 8) {
                    container.stop();
                    //inner tiles
                    for (int x = 0; x < 5; x++) {
                        for (int y = 0; y < 5; y++) {
                            if (!(x == 0 || x == 4 || y == 0 || y == 4)) {
                                Position placement = new Position(center.getX() + x, center.getY() + y, getHeight());
                                Server.playerHandler.sendStillGfx(new StillGraphic(1434, placement), instance);
                                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                                    @Override
                                    public void execute(CycleEventContainer container) {
                                        if (target.isDead) {
                                            container.stop();
                                            return;
                                        }
                                        if (container.getTotalTicks() == 4) {
                                            if (target.getPosition().equals(placement)) {
                                                int damage = Misc.random(55, 65);
                                                target.appendDamage(damage, Hitmark.HIT);
                                                getHealth().increase(damage * 2);
                                            }
                                        } else if (container.getTotalTicks() == 6) {
                                            executingPurpleFlames = false;
                                        } else if (container.getTotalTicks() == 7) {
                                            toggleAttacking(true);
                                            container.stop();
                                        }
                                    }
                                }, 1);
                            }
                        }
                    }
                }
            }
        }, 1);
    }

    public void dawnStoneBallAttack(Player target) {
        if (target.freezeTimer > 0)
            return;
        if (attackTimer > 1)
            return;
        if (isInLightningAttack())
            return;
        if (Misc.trueRand(4) != 1) {
            return;
        }
        attackTimer = 6;
        setAttackType(CombatType.MAGE);
        startAnimation(7775);
        List<Position> spots = new ArrayList<>();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Position center = new Position(target.absX - 2, target.absY - 2);
                    Position placement = new Position(center.getX() + x, center.getY() + y, getHeight());
                    spots.add(placement);
            }
        }
        final Position placement = spots.get(Misc.random(spots.size() - 1));
        new ProjectileBaseBuilder().setProjectileId(1445).setSpeed(120).setStartHeight(60).setEndHeight(40).setSendDelay(1).createProjectileBase()
                .createTargetedProjectile(this, placement).send(instance);
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (target.isDead || isDead()) {
                    container.stop();
                    return;
                }
                if (container.getTotalTicks() >= 5) {
                    container.stop();
                    Server.playerHandler.sendStillGfx(new StillGraphic(160, placement), instance);
                    if (target.getPosition().withinDistance(placement, 1)) {
                        target.gfx100(1312);
                        target.attacking.reset();
                        target.resetWalkingQueue();
                        target.freezeTimer = 10;
                        target.attackTimer = 10;
                        target.getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS, 600 * 10);
                        target.appendDamage(null, Misc.random(1,15), Hitmark.HIT);
                        target.sendMessage("You have been trapped in stone!");
                        executingDawnStoneBall = false;
                    }
                }
            }
        }, 1);
    }
    public void dawnEnergyBalls(Player target) {
        if (!inPhase(3))
            return;
        if (executeDawnEnergy) {
            return;
        }
        if (Misc.trueRand(8) != 1) {
            return;
        }
        resetAttack();
        toggleAttacking(false);
        attackTimer = 6;
        executeDawnEnergy = true;
        startAnimation(7771);
        for (int i = 0; i < 3; i++) {
            final Position placement = new Position(GrotesqueInstance.DAWN_ENERGY_POS[i].getX(),GrotesqueInstance.DAWN_ENERGY_POS[i].getY(), getHeight());
            new ProjectileBaseBuilder().setProjectileId(1437).setSpeed(85).setStartHeight(80).setEndHeight(30).setSendDelay(1).createProjectileBase()
                    .createTargetedProjectile(this, placement).send(instance);
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (isDead || target.isDead) {
                        container.stop();
                        return;
                    }
                    if (container.getTotalTicks() >= 3) {
                        container.stop();
                        toggleAttacking(true);
                        GlobalObject ground_orb = new GlobalObject(31678, placement.getX(), placement.getY(), placement.getHeight(), 0, 10, -1, -1).setInstance(instance);
                        Server.getGlobalObjects().add(ground_orb);
                    }
                }
            }, 1);
        }
    }

    public void duskRocksEffect(Player target) {
        if (!inPhase(2))
            return;
        if (hasExecutedSpecial())
            return;
        if (target.freezeTimer > 0)
            return;
        if (attackTimer > 1)
            return;
        resetAttack();
        toggleAttacking(false);
        executingDuskRocks = true;
        attackTimer = 7;
        //rocks falling
        int randX = 0, randY = 0;
        int airObjectId = 1436;
        int groundObjectId = 1435;
        for (int i = 0; i < 8; i++) {
            randX = Misc.random(1689, 1704);
            randY = Misc.random(4567, 4582);
            final Position placement = new Position(randX, randY, getHeight());
            target.getPA().createProjectile(placement.getX(), placement.getY(),1, 1, 41, 400, 70,
                    140, 1435, 200, 0, 0, 50);
            Server.playerHandler.sendStillGfx(new StillGraphic(1446, placement), instance);
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (isDead || target.isDead) {
                        container.stop();
                        return;
                    }
                    if (container.getTotalTicks() == 5) {
                        Server.playerHandler.sendStillGfx(new StillGraphic(1436, placement), instance);
                        if (target.getPosition().withinDistance(placement, 1)) {
                            target.appendDamage(asNPC(), Misc.random(1,10), Hitmark.HIT);
                        }
                    } else if (container.getTotalTicks() >= 6) {
                        container.stop();
                        executingDuskRocks = false;
                        toggleAttacking(true);
                    }
                }
            }, 1);
        }
    }

    public void toggleAttacking(boolean on) {
        if (!on) {
            resetAttack();
        }
        getBehaviour().setAggressive(on);
    }

    public void toggleLightning(boolean bool) {
        instance.executingLightningAttack = bool;
    }

    public boolean isInLightningAttack() {
        return instance.executingLightningAttack;
    }

    @Override
    public int getDeathAnimation() {
        if (getName().equalsIgnoreCase("dawn")) {
            if (inPhase(3)) {
                requestTransform(Npcs.DAWN_7885);
                return 7777;
            }
            return 7776;
        }
        return 7803;
    }

    @Override
    public int getSize() {
        return NpcDef.forId(getNpcId()).getSize();
    }
}
