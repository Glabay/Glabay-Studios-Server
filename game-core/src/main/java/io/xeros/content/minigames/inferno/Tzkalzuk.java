package io.xeros.content.minigames.inferno;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.formula.MagicMaxHit;
import io.xeros.content.combat.formula.RangeMaxHit;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public abstract class Tzkalzuk extends LegacySoloPlayerInstance {

    /**
     * 280 ticks = 3 minutes and 30 seconds
     */
    private static int SPAWN_TIME = 280;

    /**
     * 168 ticks = 1 minute 45 seconds
     */
    private static int EXTRA_SPAWN_TIMER = 168;

    /**
     * Npc variables, start coordinates.
     */
    static final Position EXIT = new Position(2497, 5116);
    private static final int START_X = 2271, START_Y = 5358;
    private static final int SPAWN_X = 2268, SPAWN_Y = 5365;
    private static final int GLYPH_SPAWN_X = 2270, GLYPH_SPAWN_Y = 5363;
    static final int TOKKUL = 6529;
    private static final int GLYPH_HITPOINTS = 600;
    private static final Position[] HEALER_SPAWNS = {
            new Position(2281, 5363),
            new Position(2275, 5363),
            new Position(2267, 5363),
            new Position(2261, 5363),
    };


    int glyphCurrentX;
    int glyphCurrentY;
    boolean zukDead;
    boolean glyphCanMove;
    public boolean started;
    public boolean cutsceneWalkBlock;
    boolean spawnedJad;
    boolean spawnedHealers;

    /**
     * A flag that pauses the ranger/mage spawn timer until jad is spawned
     *
     * 0 - Waiting for 600 hp
     * 1 - Waiting for jad spawn
     * 2 - completed
     */
    private int pauseState;

    private final int glyphHitpoints;

    private final List<NPC> healers = Lists.newArrayList();
    NPC glyph;
    NPC tzkal;
    NPC ranger;
    NPC mager;

    public List<NPC> kill = new ArrayList<>();

    public Tzkalzuk(Player player, Boundary boundary) {
        super(player, boundary);
        glyphHitpoints = Server.isDebug() ? Integer.MAX_VALUE : GLYPH_HITPOINTS;
    }

    private void spawnNpcs() {
        tzkal = NPCSpawning.spawnNpcOld(player, InfernoWaveData.TZKAL_ZUK, SPAWN_X, SPAWN_Y, getHeight(), 0, 1200, 251, 1000, 1000, false, false);
    }

    public boolean isSpawningPaused() { return pauseState == 1; }

    public void tzkalzukSpecials() {
        Tzkalzuk ctx = this;
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int rangerRespawnTick;
            int magerRespawnTick;
            @Override
            public void execute(CycleEventContainer container) {
                if (tzkal.isDead()) {
                    container.stop();
                    return;
                }
                if (player == null) {
                    container.stop();
                    return;
                }
                if (!Boundary.isIn(player, Boundary.INFERNO)) {
                    Inferno.reset(player);
                    container.stop();
                    return;
                }

                if (ranger == null) {
                    if (!player.getInferno().isSpawningPaused()) {
                        if (container.getTotalTicks() == Tzkalzuk.SPAWN_TIME) {
                            RangersAndMagers.spawnRanger(player, ctx);
                        }
                    }
                } else if (ranger.isDead()) {
                    if (!player.getInferno().isSpawningPaused()) {
                        if (rangerRespawnTick == 0)
                            rangerRespawnTick = Tzkalzuk.SPAWN_TIME;
                        if (--rangerRespawnTick == 0) {
                            RangersAndMagers.spawnRanger(player, ctx);
                        }
                    }
                }

                if (mager == null) {
                    if (!player.getInferno().isSpawningPaused()) {
                        if (container.getTotalTicks() == Tzkalzuk.SPAWN_TIME) {
                            RangersAndMagers.spawnMager(player, ctx);
                        }
                    }
                } else if (mager.isDead()) {
                    if (!player.getInferno().isSpawningPaused()) {
                        if (magerRespawnTick == 0)
                            magerRespawnTick = Tzkalzuk.SPAWN_TIME;
                        if (--magerRespawnTick == 0) {
                            RangersAndMagers.spawnMager(player, ctx);
                        }
                    }
                }

                // Pauses the spawn timer for rangers / magers
                if (tzkal.getHealth().getCurrentHealth() <= 600 && pauseState == 0)
                    pauseState++;

                if (tzkal.getHealth().getCurrentHealth() <= 480 && !player.getInferno().spawnedJad) {
                    pauseState++;

                    magerRespawnTick += EXTRA_SPAWN_TIMER;
                    rangerRespawnTick += EXTRA_SPAWN_TIMER;

                    player.getInferno().spawnedJad = true;
                    spawnJad();
                }
                if (tzkal.getHealth().getCurrentHealth() <= 240 && !player.getInferno().spawnedHealers) {
                    player.getInferno().spawnedHealers = true;
                    spawnHealers();
                }
                if (container.getTotalTicks() % 8 == 0) {
                    if (!Boundary.isIn(player, Boundary.INFERNO)) {
                        Inferno.reset(player);
                        container.stop();
                        return;
                    }
                    if (tzkal.isDead()) {
                        container.stop();
                        return;
                    }
                    if (player == null) {
                        container.stop();
                        return;
                    }
                    tzkal.startAnimation(7566);
                    if (player.debugMessage) {
                        player.sendMessage("Glyph: " + glyph.getX() + ", player: " + player.getX());
                    }
                    if (!(player.getX() >= glyph.getX() - 3 && player.getX() <= glyph.getX() + 5) || player.getInferno().glyph.isDead()) {
                        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                            int ticks;
                            final int amount = Misc.random(0, 255);

                            @Override
                            public void execute(CycleEventContainer container) {
                                if (tzkal.isDead()) {
                                    container.stop();
                                    return;
                                }
                                if (player == null) {
                                    container.stop();
                                    return;
                                }
                                if (player.isDead()) {
                                    container.stop();
                                    return;
                                }
                                if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                    Inferno.reset(player);
                                    container.stop();
                                    return;
                                }
                                if (ticks == 0) {
                                    if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                        Inferno.reset(player);
                                        container.stop();
                                        return;
                                    }
                                    if (tzkal.isDead() || player == null || player.isDead()) {
                                        container.stop();
                                        return;
                                    }

                                    int nX = tzkal.getX();
                                    int nY = tzkal.getY();
                                    int pX = player.getX();
                                    int pY = player.getY();
                                    int offX = (nX - pX) * -1;
                                    int offY = (nY - pY) * -1;
                                    int centerX = nX + tzkal.getSize() / 2;
                                    int centerY = nY + tzkal.getSize() / 2;

                                    int speed = 130;
                                    int startHeight = 110;
                                    int endHeight = 31;
                                    int delay = 0;
                                    player.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, speed, 1375, startHeight, endHeight, -player.getIndex() - 1, 65, delay);
                                }
                                if (ticks == 3) {
                                    if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                        Inferno.reset(player);
                                        container.stop();
                                        return;
                                    }
                                    if (tzkal.isDead()) {
                                        container.stop();
                                        return;
                                    }
                                    if (player == null) {
                                        container.stop();
                                        return;
                                    }
                                    if (player.isDead()) {
                                        container.stop();
                                        return;
                                    }
                                    if (Misc.random(500) + 200 > Misc.random(MagicMaxHit.mageDefence(player))) {
                                        if (amount == 0) {
                                            player.appendDamage(tzkal, amount, Hitmark.MISS);
                                        } else {
                                            player.appendDamage(tzkal, amount, Hitmark.HIT);
                                        }
                                    } else {
                                        player.appendDamage(tzkal, 0, Hitmark.MISS);
                                    }
                                    container.stop();
                                }
                                ticks++;
                            }
                        }, 1);
                    } else {
                        if (tzkal.isDead()) {
                            container.stop();
                            return;
                        }
                        if (player.getInferno().glyph.isDead()) {
                            container.stop();
                            return;
                        }
                        if (!Boundary.isIn(player, Boundary.INFERNO)) {
                            Inferno.reset(player);
                            container.stop();
                            return;
                        }
                        if (player == null) {
                            container.stop();
                            return;
                        }
                        if (player.isDead()) {
                            container.stop();
                            return;
                        }
                        int nX = tzkal.getX();
                        int nY = tzkal.getY();
                        int pX = player.getInferno().glyph.getX();
                        int pY = player.getInferno().glyph.getY();
                        int offX = (nX - pX) * -1;
                        int offY = (nY - pY) * -1;
                        int centerX = nX + tzkal.getSize() / 2;
                        int centerY = nY + tzkal.getSize() / 2;

                        int speed = 130;
                        int startHeight = 110;
                        int endHeight = 31;
                        int delay = 0;
                        player.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, speed, 1375, startHeight, endHeight, player.getInferno().glyph.getIndex() + 1,
                                65, delay);
                    }
                }
            }
        }, 1);
    }

    private void spawnHealers() {
        InstancedArea ctx = this;
        Preconditions.checkState(healers.isEmpty());
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int tick;

            @Override
            public void execute(CycleEventContainer container) {
                if (tzkal.isDead() || player.isDead() || !Boundary.isIn(player, Boundary.INFERNO)
                        || !healers.isEmpty() && healers.stream().allMatch(healer -> healer.isDead())) {
                    container.stop();
                    return;
                }

                if (tick == 0) {
                    for (Position healerSpawn : HEALER_SPAWNS) {
                        healers.add(NPCSpawning.spawnNpcOld(player, InfernoWaveData.JAL_MEJJAK, healerSpawn.getX(), healerSpawn.getY(), getHeight(), 0,
                                InfernoWaveData.getHp(InfernoWaveData.JAL_MEJJAK), InfernoWaveData.getMax(InfernoWaveData.JAL_MEJJAK), InfernoWaveData.getAtk(InfernoWaveData.JAL_MEJJAK),
                                InfernoWaveData.getDef(InfernoWaveData.JAL_MEJJAK), false, false));
                        healers.forEach(ctx::add);
                    }
                    player.getInferno().kill.addAll(healers);
                }

                for (NPC healer : healers) {
                    if (healer.lastDamageTaken > 0) {
                        healer.facePosition(player.getX(), player.getY());
                    } else {
                        healer.facePosition(tzkal.getX(), tzkal.getY());
                    }
                }

                if (tick % 6 == 0) {
                    for (NPC healer : healers) {
                        healOrAttack(healer);
                    }
                }

                tick++;
            }

            private void healOrAttack(NPC npc) {
                boolean attack = npc.lastDamageTaken > 0;
                npc.startAnimation(2868);
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    int tick;

                    @Override
                    public void execute(CycleEventContainer container) {
                        if (npc.isDead() || tzkal.isDead() || player.isDead) {
                            container.stop();
                            return;
                        }
                        if (tick == 0) {
                            int nX = npc.getX();
                            int nY = npc.getY();
                            int pX = attack ? glyph.getX() : player.getX();
                            int pY = attack ? glyph.getY() : player.getY();
                            int offX = (nX - pX) * -1;
                            int offY = (nY - pY) * -1;
                            int centerX = nX + npc.getSize() / 2;
                            int centerY = nY + npc.getSize() / 2;

                            int speed = 85;
                            int startHeight = 43;
                            int endHeight = 31;
                            int delay = 0;
                            player.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, speed, 660, startHeight, endHeight, attack ? -player.getIndex() - 1 : glyph.getIndex() + 1,
                                    65, delay);
                        }

                        if (tick == 2) {
                            if (attack) {
                                player.appendDamage(npc, 1 + Misc.random(0, 2), Hitmark.HIT);
                            } else {
                                if (tzkal.getHealth().getCurrentHealth() < tzkal.getHealth().getMaximumHealth()) {
                                    tzkal.getHealth().increase(25);
                                }
                            }
                            container.stop();
                        }
                        tick++;
                    }
                }, 1);
            }
        }, 1);
    }

    private void spawnJad() {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            NPC jad;
            int tick;

            @Override
            public void execute(CycleEventContainer container) {
                if (tick == 0) {
                    jad = NPCSpawning.spawnNpcOld(player, InfernoWaveData.JALTOK_JAD, 2270, 5348, getHeight(), 0, InfernoWaveData.getHp(InfernoWaveData.JALTOK_JAD), InfernoWaveData.getMax(InfernoWaveData.JALTOK_JAD), InfernoWaveData.getAtk(InfernoWaveData.JALTOK_JAD), InfernoWaveData.getDef(InfernoWaveData.JALTOK_JAD), false, false);
                    player.getInferno().kill.add(jad);
                }
                if (player == null || player.getInferno() == null) {
                    container.stop();
                    return;
                }
                if (player.getInferno().zukDead) {
                    container.stop();
                    return;
                }
                if (player.isDead()) {
                    container.stop();
                    return;
                }
                if (jad != null) {
                    if (!jad.isDead()) {
                        if (jad.lastDamageTaken > 0 || player.getInferno().glyph.isDead()) {
                            if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                Inferno.reset(player);
                                container.stop();
                                return;
                            }
                            if (player == null) {
                                container.stop();
                                return;
                            }
                            if (player.isDead()) {
                                container.stop();
                                return;
                            }
                            if (player.getInferno().zukDead) {
                                container.stop();
                                return;
                            }
                            jad.facePlayer(player.getIndex());
                        } else {
                            jad.facePosition(player.getInferno().glyph.getX(), player.getInferno().glyph.getY());
                        }
                    }
                }
                final int type = Misc.random(0, 1);
                if (tick % 10 == 0 && tick != 0) {
                    if (jad != null) {
                        if (!jad.isDead()) {
                            if (type == 0) {
                                jad.startAnimation(7593);
                            } else {
                                jad.startAnimation(7592);
                            }
                        }
                    }
                    CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                        int tick;
                        int amount = Misc.random(0, 113);

                        @Override
                        public void execute(CycleEventContainer container) {
                            if (tick == 0) {
                                if (jad != null) {
                                    if (!jad.isDead()) {
                                        if (jad.lastDamageTaken > 0 || player.getInferno().glyph.isDead()) {
                                            if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                                Inferno.reset(player);
                                                container.stop();
                                                return;
                                            }
                                            if (player == null) {
                                                container.stop();
                                                return;
                                            }
                                            if (player.isDead()) {
                                                container.stop();
                                                return;
                                            }
                                            if (player.getInferno().zukDead) {
                                                container.stop();
                                                return;
                                            }
                                            int nX = jad.getX();
                                            int nY = jad.getY();
                                            int pX = player.getX();
                                            int pY = player.getY();
                                            int offX = (nX - pX) * -1;
                                            int offY = (nY - pY) * -1;
                                            int centerX = nX + jad.getSize() / 2;
                                            int centerY = nY + jad.getSize() / 2;

                                            int speed = 130;
                                            int startHeight = 110;
                                            int endHeight = 31;
                                            int delay = 0;
                                            if (type == 0) {
                                                player.gfx100(451);
                                                if (player.protectingRange()) {
                                                    amount = 0;
                                                }
                                            } else {
                                                if (player.protectingMagic()) {
                                                    amount = 0;
                                                }
                                                player.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, speed, 448, startHeight, endHeight, -player.getIndex() - 1, 65, delay);
                                            }
                                        } else {
                                            if (player.getInferno().glyph == null) {
                                                container.stop();
                                                return;
                                            }
                                            if (player.getInferno().glyph.isDead()) {
                                                container.stop();
                                                return;
                                            }
                                            if (player.getInferno().zukDead) {
                                                container.stop();
                                                return;
                                            }
                                            int nX = jad.getX();
                                            int nY = jad.getY();
                                            int pX = player.getInferno().glyph.getX();
                                            int pY = player.getInferno().glyph.getY();
                                            int offX = (nX - pX) * -1;
                                            int offY = (nY - pY) * -1;
                                            int centerX = nX + jad.getSize() / 2;
                                            int centerY = nY + jad.getSize() / 2;

                                            int speed = 130;
                                            int startHeight = 110;
                                            int endHeight = 31;
                                            int delay = 0;
                                            if (type == 0) {
                                                player.getInferno().glyph.gfx100(451);
                                            } else {
                                                player.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, speed, 448, startHeight, endHeight, player.getInferno().glyph.getIndex() + 1,
                                                        65, delay);
                                            }

                                        }
                                    }
                                }
                            }

                            if (tick == 1 || tick == 2) {
                                if (type == 0) {
                                    if (player.protectingRange()) {
                                        amount = 0;
                                    }
                                } else {
                                    if (player.protectingMagic()) {
                                        amount = 0;
                                    }
                                }
                            }

                            if (tick == 3) {
                                if (jad != null) {
                                    if (jad.isDead()) {
                                        container.stop();
                                        return;
                                    }
                                }
                                if (jad == null) {
                                    container.stop();
                                    return;
                                }

                                if (player == null) {
                                    container.stop();
                                    return;
                                }
                                if (player.getInferno().zukDead) {
                                    container.stop();
                                    return;
                                }
                                if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                    Inferno.reset(player);
                                    container.stop();
                                    return;
                                }
                                if (jad != null) {
                                    if (!jad.isDead()) {
                                        if (jad.lastDamageTaken > 0 || player.getInferno().glyph.isDead()) {
                                            if (player == null) {
                                                container.stop();
                                                return;
                                            }
                                            if (player.isDead()) {
                                                container.stop();
                                                return;
                                            }
                                            if (player.getInferno().zukDead) {
                                                container.stop();
                                                return;
                                            }
                                            if (type == 0) {
                                                if (player.protectingRange()) {
                                                    amount = 0;
                                                }
                                                if (Misc.random(500) + 200 > Misc.random(RangeMaxHit.calculateRangeDefence(player))) {
                                                    if (amount == 0) {
                                                        player.appendDamage(jad, amount, Hitmark.MISS);
                                                    } else {
                                                        player.appendDamage(jad, amount, Hitmark.HIT);
                                                    }
                                                } else {
                                                    player.appendDamage(jad, 0, Hitmark.MISS);
                                                }
                                            } else {
                                                if (player.protectingMagic()) {
                                                    amount = 0;
                                                }
                                                if (Misc.random(500) + 200 > Misc.random(MagicMaxHit.mageDefence(player))) {
                                                    if (amount == 0) {
                                                        player.appendDamage(jad, amount, Hitmark.MISS);
                                                    } else {
                                                        player.appendDamage(jad, amount, Hitmark.HIT);
                                                    }
                                                } else {
                                                    player.appendDamage(jad, 0, Hitmark.MISS);
                                                }

                                            }
                                        } else {
                                            if (player.getInferno().glyph == null) {
                                                container.stop();
                                                return;
                                            }
                                            if (player.getInferno().glyph.isDead()) {
                                                container.stop();
                                                return;
                                            }
                                            if (player.getInferno().zukDead) {
                                                container.stop();
                                                return;
                                            }
                                            amount = Misc.random(0, 113);
                                            if (amount == 0) {
                                                player.getInferno().glyph.appendDamage(jad, amount, Hitmark.MISS);
                                            } else {
                                                player.getInferno().glyph.appendDamage(jad, amount, Hitmark.HIT);
                                            }
                                        }
                                    }
                                }
                                container.stop();
                            }

                            if (jad != null) {
                                if (jad.isDead()) {
                                    container.stop();
                                    return;
                                }
                            }
                            if (jad == null) {
                                container.stop();
                                return;
                            }

                            if (player == null) {
                                container.stop();
                                return;
                            }

                            if (player.isDead()) {
                                container.stop();
                                return;
                            }
                            if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                Inferno.reset(player);
                                container.stop();
                                return;
                            }

                            if (player.getInferno().zukDead) {
                                container.stop();
                                return;
                            }

                            tick++;
                        }
                    }, 1);
                }
                tick++;
            }
        }, 1);
    }



    public void initiateTzkalzuk() {
        player.getInferno().started = true;
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (player == null) {
                    container.stop();
                    return;
                }
                if (!Boundary.isIn(player, Boundary.INFERNO)) {
                    Inferno.reset(player);
                    container.stop();
                    return;
                }
                int cycle = container.getTotalTicks();
                if (cycle == 1) {//TODO FIX CAMERA ANGLE SOMTIMES? (MAYBE)
                    player.getInferno().cutsceneWalkBlock = true;
                    player.getPA().sendScreenFade("TzKal-Zuk instance loading...", -1, 5);
                    player.getPA().movePlayer(START_X, START_Y, getHeight() + 1);
                } else if (cycle == 2) {
                    player.facePosition(START_X, SPAWN_Y);
                } else if (cycle == 3) {
                    Server.getGlobalObjects().add(new GlobalObject(-1, 2267, 5368, (getHeight() + 1), 0, 10, -1, -1).setInstance(player.getInstance()));  // Delete ceiling
                    player.getPA().movePlayer(START_X, START_Y, getHeight());
                    player.getInferno().glyph = NPCSpawning.spawnNpcOld(player, InfernoWaveData.ANCESTRAL_GLYPH, GLYPH_SPAWN_X, GLYPH_SPAWN_Y,
                            getHeight(), 0, glyphHitpoints, 38, 500, 700, false, false);
                } else if (cycle == 4) {
                    Server.getGlobalObjects().add(new GlobalObject(30342, 2267, 5366, getHeight(), 1, 10, -1, -1).setInstance(player.getInstance()));  // West Wall
                    Server.getGlobalObjects().add(new GlobalObject(30341, 2275, 5366, getHeight(), 3, 10, -1, -1).setInstance(player.getInstance()));  // East Wall
                    Server.getGlobalObjects().add(new GlobalObject(30340, 2267, 5364, getHeight(), 1, 10, -1, -1).setInstance(player.getInstance()));  // West Corner
                    Server.getGlobalObjects().add(new GlobalObject(30339, 2275, 5364, getHeight(), 3, 10, -1, -1).setInstance(player.getInstance()));  // East Corner
                    Server.getGlobalObjects().add(new GlobalObject(30344, 2268, 5364, getHeight(), 3, 10, -1, -1).setInstance(player.getInstance())); // Set falling rocks - west
                    Server.getGlobalObjects().add(new GlobalObject(30343, 2273, 5364, getHeight(), 3, 10, -1, -1).setInstance(player.getInstance())); // Set falling rocks - east
                    Server.getGlobalObjects().add(new GlobalObject(-1, 2270, 5363, getHeight(), 3, 10, -1, -1).setInstance(player.getInstance())); // Delete ancestral glyph
                    player.getPA().stillCamera(2271, 5365, 2, 0, 10);
                    deleteGlyph();
                } else if (cycle == 10) {
                    player.getPA().sendPlayerObjectAnimation(2268, 5364, 7560, 10, 3); // Set falling rocks animation - west
                    player.getPA().sendPlayerObjectAnimation(2273, 5364, 7559, 10, 3); // Set falling rocks animation - east
                    //player.getPA().sendPlayerObjectAnimation(2268, 5364, 7560, 10, 3); // Set falling rocks animation - west
                    //player.getPA().sendPlayerObjectAnimation(2273, 5364, 7559, 10, 3); // Set falling rocks animation - east
                    //player.getPA().sendPlayerObjectAnimation(player, 2270, 5363, 7560, 10, 3, height); // Set falling rocks animation - middle
                    spawnNpcs();
                } else if (cycle >= 14) {
                    player.getPA().resetCamera();
                    player.getInferno().cutsceneWalkBlock = false;
                    player.getInferno().glyphCanMove = true;
                    tzkalzukSpecials();
                    container.stop();
                }
            }
        }, 1);
    }

    private void deleteGlyph() {
        Server.getGlobalObjects().add(new GlobalObject(-1, 2270, 5363, getHeight(), 3, 10, -1, -1)); // Delete ancestral glyph
    }
}