package io.xeros.model.entity.npc;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.bosses.CorporealBeast;
import io.xeros.content.bosses.Scorpia;
import io.xeros.content.bosses.Skotizo;
import io.xeros.content.bosses.Vorkath;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosses.hydra.AlchemicalHydra;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.minigames.inferno.AncestralGlyph;
import io.xeros.content.minigames.inferno.InfernoWaveData;
import io.xeros.content.minigames.rfd.DisposeTypes;
import io.xeros.model.Animation;
import io.xeros.model.Direction;
import io.xeros.model.definition.Npcs;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.AnimationLength;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.actions.NPCHitPlayer;
import io.xeros.model.entity.npc.actions.NpcAggression;
import io.xeros.model.entity.npc.data.RespawnTime;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NPCProcess {

    private static final Logger logger = LoggerFactory.getLogger(NPCProcess.class);

    private final NPCHandler npcHandler;

    NPCProcess(NPCHandler npcHandler) {
        this.npcHandler = npcHandler;
    }

    private int i;
    private NPC npc;
    private int type;
    private AlchemicalHydra hydraInstance;

    public void process(int i) {
        this.i = i;
        if (NPCHandler.npcs[i] == null) {
            logger.debug("Trying to process null npc index: {}", i);
            return;
        }
        npc = NPCHandler.npcs[i];
        type = NPCHandler.npcs[i].getNpcId();
        Optional<AlchemicalHydra> hydraInstance = NPCHandler.getHydraInstance(npc);
        this.hydraInstance = hydraInstance.orElse(null);
        processing();
    }

    private void processing() {
        if (npc.getInstance() != null) {
            npc.getInstance().tick(npc);
            if (npc.getInstance().isDisposed()) {
                logger.debug("NPC instance was disposed, unregistering {}", npc);
                npc.unregister();
                return;
            }
        }

        Player slaveOwner = (PlayerHandler.players[npc.summonedBy]);
        if (slaveOwner == null && npc.summoner) {
            npc.absX = 0;
            npc.absY = 0;
        }
        if (slaveOwner != null && slaveOwner.hasFollower && (!slaveOwner.goodDistance(npc.getX(), npc.getY(), slaveOwner.absX, slaveOwner.absY,
                15) || slaveOwner.heightLevel != npc.heightLevel) && npc.summoner) {
            npc.absX = slaveOwner.absX;
            npc.absY = slaveOwner.absY;
            npc.heightLevel = slaveOwner.heightLevel;

        }
        if (npc.actionTimer > 0) {
            npc.actionTimer--;
        }

        if (npc.freezeTimer > 0) {
            npc.freezeTimer--;
        }
        if (npc.hitDelayTimer > 0) {
            npc.hitDelayTimer--;
        }
        if (npc.hitDelayTimer == 1) {
            npc.hitDelayTimer = 0;
            NPCHitPlayer.applyDamage(npc, npcHandler);
        }
        if (npc.attackTimer > 0) {
            npc.attackTimer--;
        }
        if (npc.getNpcId() == 7553) {
            npc.walkingHome = true;
        }
        if (npc.getNpcId() == 7555) {
            npc.walkingHome = true;
        }
        if (npc.getNpcId() == 1143) {
            if (Misc.random(30) == 3) { //how often he says it
                npc.setUpdateRequired(true); //update to the players view
                npc.forceChat("Sell your PvM items here for a limited time!"); //npc forced text example
            }
        }

        if (npc.getNpcId() == 8583) {
            npc.getBehaviour().setRespawn(false);
        }

        if (npc.getNpcId() == 306) {
            if (Misc.random(50) == 3) {
                npc.forceChat("Speak to me if you wish to learn more about this land!");
            }
        }


        if (npc.getHealth().getCurrentHealth() > 0 && !npc.isDead()) {
            if (npc.getNpcId() == 6611 || npc.getNpcId() == 6612) {
                if (npc.getHealth().getCurrentHealth() < (npc.getHealth().getMaximumHealth() / 2)
                        && !npc.spawnedMinions) {
                    NPC npc1 = NPCSpawning.spawnNpc(Npcs.SKELETON_HELLHOUND, npc.getX() - 1, npc.getY(), 0, 1, 14);
                    NPC npc2 = NPCSpawning.spawnNpc(Npcs.SKELETON_HELLHOUND, npc.getX() + 1, npc.getY(), 0, 1, 14);
                    if (npc1 != null && npc2 != null) {
                        npc1.getBehaviour().setAggressive(true);
                        npc2.getBehaviour().setAggressive(true);
                    }
                    npc.spawnedMinions = true;
                }
            }
        }
        if (npc.getNpcId() == 6600 && !npc.isDead()) {
            NPC runiteGolem = NPCHandler.getNpc(6600);
            if (runiteGolem != null && !runiteGolem.isDead()) {
                npc.setDead(true);
                npc.needRespawn = false;
                npc.actionTimer = 0;
            }
        }

        if (npc.getInstance() == null) { // Only delete summoned npcs when not inside an instance
            if (npc.spawnedBy > 0) { // delete summons npc
                Player spawnedBy = PlayerHandler.players[npc.spawnedBy];
                if (spawnedBy == null || spawnedBy.heightLevel != npc.heightLevel || spawnedBy.respawnTimer > 0
                        || !spawnedBy.goodDistance(npc.getX(), npc.getY(), spawnedBy.getX(),
                        spawnedBy.getY(),
                        NPCHandler.isFightCaveNpc(npc) ? 60 : NPCHandler.isSkotizoNpc(npc) ? 60 : 20)) {
                    npc.unregister();
                }
            }
        }
        if (npc.lastX != npc.getX() || npc.lastY != npc.getY()) {
            npc.lastX = npc.getX();
            npc.lastY = npc.getY();
        }

        if (hydraInstance != null) {
            hydraInstance.onTick();
        }

        // Inferno glyph movement
        if (npc.getNpcId() == InfernoWaveData.ANCESTRAL_GLYPH) {
            if (PlayerHandler.players[npc.spawnedBy] != null) {
                AncestralGlyph.handleMovement(PlayerHandler.players[npc.spawnedBy], npc);
            }
        }

        if (type == 6615) {
            if (npc.walkingHome) {
                npc.getHealth().setCurrentHealth(200);
            }
            Scorpia.spawnHealer();
        }

        if (type == Npcs.CORPOREAL_BEAST) {
            CorporealBeast.checkCore(npc);
            CorporealBeast.healWhenNoPlayers(npc);
        }

        if (type == 8026 || type == 8027) {
            npc.setFacePlayer(false);
        }
        if (type == 8028) {
            npc.setFacePlayer(true);
        }
        if (type >= 2042 && type <= 2044 && npc.getHealth().getCurrentHealth() > 0) {
            Player player = PlayerHandler.players[npc.spawnedBy];
            if (player != null && player.getZulrahEvent().getNpc() != null
                    && npc.equals(player.getZulrahEvent().getNpc())) {
                int stage = player.getZulrahEvent().getStage();
                if (type == 2042) {
                    if (stage == 0 || stage == 1 || stage == 4 || stage == 9 && npc.totalAttacks >= 20
                            || stage == 11 && npc.totalAttacks >= 5) {
                        return;
                    }
                }
                if (type == 2044) {
                    if ((stage == 5 || stage == 8) && npc.totalAttacks >= 5) {
                        return;
                    }
                }
            }
        }

        NpcAggression.doAggression(npc, npcHandler);
        if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
            if (!npc.underAttack && NpcAggression.getCloseRandomPlayer(npc) <= 0) {
                npc.getHealth().reset();
            }
        }
        ///if (npcs[i].killerId <= 0) {
        if (System.currentTimeMillis() - npc.lastDamageTaken > 5000 && !npc.underAttack) {
            npc.underAttackBy = 0;
            npc.randomWalk = true;
        }
        if (System.currentTimeMillis() - npc.lastDamageTaken > 10000) {
            npc.underAttackBy = 0;
            npc.underAttack = false;
            npc.randomWalk = true;
        }
        // }

        if ((npc.getPlayerAttackingIndex() > 0 || npc.underAttack)
                && !npc.walkingHome
                && npcHandler.retaliates(npc.getNpcId())) {
            if (!npc.isDead()) {
                int p = npc.getPlayerAttackingIndex();
                if (PlayerHandler.players[p] != null) {
                    if (!npc.summoner) {
                        Player c = PlayerHandler.players[p];
                        if (c.getInferno() != null && c.getInferno().kill.contains(npc)) {
                            return;
                        }
                        npcHandler.followPlayer(npc, c.getIndex());

                        if (npc.attackTimer == 0) {
                            if (npc.getNpcAutoAttacks().isEmpty()) {
                                npcHandler.attackPlayer(c, npc);
                            } else {
                                npc.selectAutoAttack(c);
                                npc.attack(c, npc.getCurrentAttack());
                            }
                        }

                    } else {
                        Player c = PlayerHandler.players[p];
                        if (c.absX == npc.absX && c.absY == npc.absY) {
                            npcHandler.stepAway(npc);
                            npc.randomWalk = false;
                            if (npc.getNpcId() == InfernoWaveData.JAL_NIB) {
                                return;
                            }
                            npc.facePlayer(c.getIndex());
                        } else {
                            if (c.getInferno() != null && c.getInferno().kill.contains(npc)) {
                                return;
                            }
                            npcHandler.followPlayer(npc, c.getIndex());
                        }
                    }
                } else {
                    npc.setPlayerAttackingIndex(0);
                    npc.underAttack = false;
                    npc.facePlayer(0);
                }
            }
        }

        // Random walking and walking home
        if ((!npc.underAttack) && !NPCHandler.isFightCaveNpc(npc) && npc.randomWalk && !npc.isDead() && npc.getBehaviour().isWalkHome()) {
            npc.facePlayer(0);
            npc.setPlayerAttackingIndex(0);
            // handleClipping(i);
            if (npc.spawnedBy == 0) {
                if ((npc.absX > npc.makeX + Configuration.NPC_RANDOM_WALK_DISTANCE)
                        || (npc.absX < npc.makeX - Configuration.NPC_RANDOM_WALK_DISTANCE)
                        || (npc.absY > npc.makeY + Configuration.NPC_RANDOM_WALK_DISTANCE)
                        || (npc.absY < npc.makeY - Configuration.NPC_RANDOM_WALK_DISTANCE)
                        && npc.getNpcId() != 1635 && npc.getNpcId() != 1636 && npc.getNpcId() != 1637
                        && npc.getNpcId() != 1638 && npc.getNpcId() != 1639 && npc.getNpcId() != 1640
                        && npc.getNpcId() != 1641 && npc.getNpcId() != 1642 && npc.getNpcId() != 1643
                        && npc.getNpcId() != 1654 && npc.getNpcId() != 7302) {
                    npc.walkingHome = true;
                }
            }

            if (npc.walkingType >= 0) {
                switch (npc.walkingType) {
                    case 5:
                        npc.facePosition(npc.absX - 1, npc.absY);
                        break;
                    case 4:
                        npc.facePosition(npc.absX + 1, npc.absY);
                        break;
                    case 3:
                        npc.facePosition(npc.absX, npc.absY - 1);
                        break;
                    case 2:
                        npc.facePosition(npc.absX, npc.absY + 1);
                        break;
                }
            }

            if (npc.walkingType == 1 && (!npc.underAttack) && !npc.walkingHome) {
                if (System.currentTimeMillis() - npc.getLastRandomWalk() > npc.getRandomWalkDelay()) {
                    int direction = Misc.trueRand(8);
                    int movingToX = npc.getX() + NPCClipping.DIR[direction][0];
                    int movingToY = npc.getY() + NPCClipping.DIR[direction][1];
                    if (npc.getNpcId() >= 1635 && npc.getNpcId() <= 1643 || npc.getNpcId() == 1654
                            || npc.getNpcId() == 7302) {
                        NPCDumbPathFinder.walkTowards(npc, npc.getX() - 1 + Misc.random(8),
                                npc.getY() - 1 + Misc.random(8));
                    } else {
                        if (Math.abs(npc.makeX - movingToX) <= 1 && Math.abs(npc.makeY - movingToY) <= 1
                                && NPCDumbPathFinder.canMoveTo(npc, direction)) {
                            NPCDumbPathFinder.walkTowards(npc, movingToX, movingToY);
                        }
                    }
                    npc.setRandomWalkDelay(TimeUnit.SECONDS.toMillis(1 + Misc.random(2)));
                    npc.setLastRandomWalk(System.currentTimeMillis());
                }
            }
        }
        if (npc.walkingHome) {
            if (!npc.isDead()) {
                NPCDumbPathFinder.walkTowards(npc, npc.makeX, npc.makeY);
                if (npc.walkDirection == Direction.NONE) {
                    npc.teleport(npc.makeX, npc.makeY, npc.heightLevel);
                    if (npc.absX == npc.makeX && npc.absY == npc.makeY) {
                        npc.walkingHome = false;
                    }
                    return;
                }
                if (npc.absX == npc.makeX && npc.absY == npc.makeY) {
                    npc.walkingHome = false;
                }
            } else {
                npc.walkingHome = false;
            }
        }

        /*
          Npc death
         */
        if (npc.isDead()) {
            processDeath();
        } else {
            npc.processMovement();
        }
    }

    private void processDeath() {
        if (npc.isDead()) {
            Player playerOwner = PlayerHandler.players[npc.spawnedBy];
            npc.getRegionProvider().removeNpcClipping(npc);

            if (npc.actionTimer == 0 && !npc.applyDead && !npc.needRespawn) {

                // Vorkath
                if (npc.getNpcId() == 8028) {
                    CycleEventHandler.getSingleton().addEvent(playerOwner, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            int dropHead = Misc.random(50);
                            if (dropHead == 1 || playerOwner.getNpcDeathTracker().getKc("vorkath") == 50) {
                                Server.itemHandler.createGroundItem(playerOwner, 2425, Vorkath.lootCoordinates[0],
                                        Vorkath.lootCoordinates[1], playerOwner.heightLevel, 1, playerOwner.getIndex());
                            }
                            Vorkath.spawn(playerOwner);
                            container.stop();
                        }

                    }, 9);
                }

                if (npc.getNpcId() == 9021 || npc.getNpcId() == 9022 || npc.getNpcId() == 9023) {
                    npc.startAnimation(8421);
                    npc.requestTransform(9024);
                    npc.getHealth().reset();
                    npc.setDead(true);
                    npc.forceChat("RAAAAARGHHHHH!");
                    npc.applyDead = true;
                }

                if (npc.getNpcId() == 6618) {
                    npc.forceChat("Ow!");
                }

                if (npc.getNpcId() == 8781) {
                    npc.forceChat("You will pay for this!");
                    npc.startAnimation(-1);
                    npc.gfx0(1005);
                }

                if (npc.getNpcId() == 6611) {
                    npc.requestTransform(6612);
                    npc.getHealth().reset();
                    npc.setDead(false);
                    npc.spawnedMinions = false;
                    npc.forceChat("Do it again!!");
                } else {
                    if (npc.getNpcId() == 6612) {
                        npc.setNpcId(6611);
                        npc.spawnedMinions = false;
                    }

                    if (npc.getNpcId() == 9024) {
                        npc.setNpcId(9021);
                    }

                    if (npc.getNpcId() == 1605) {
                        CycleEventHandler.getSingleton().addEvent(playerOwner, new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                NPCSpawning.spawnNpcOld(playerOwner, 1606, 3106, 3934, 0, 1, 30, 24, 70, 60, true, true);
                                container.stop();
                            }

                        }, 5);
                    }

                    Player killer1 = PlayerHandler.players[npc.spawnedBy];


                    npc.actionTimer = AnimationLength.getFrameLength(npc.getDeathAnimation());
                    if (!"Dusk".equals(npc.getDefinition().getName())) { // Dusk animation length is long and we want it that way
                        if (npc.actionTimer > 20) {      // Fix for death animations being too long
                            npc.actionTimer = 20;
                        }
                    }

                    npc.setUpdateRequired(true);
                    npc.facePlayer(0);
                    Entity killer = npc.calculateKiller();

                    if (killer != null) {
                        npc.killedBy = killer.getIndex();
                    }

                    npc.freezeTimer = 0;
                    npc.applyDead = true;

                    if (npc.getNpcId() == 3118) {
                        NPCSpawning.spawnNpc(3120, npc.absX, npc.absY, playerOwner.heightLevel, 10, 15);
                        NPCSpawning.spawnNpc(3120, npc.absX, npc.absY + 1, playerOwner.heightLevel, 10, 15);
                    }

                    if (npc.getNpcId() == InfernoWaveData.JAL_AK && playerOwner.getInferno() != null) {
                        NPCSpawning.spawnNpc(playerOwner, InfernoWaveData.JAL_AKREK_KET, npc.absX,
                                npc.absY, playerOwner.heightLevel, 1,
                                InfernoWaveData.getMax(InfernoWaveData.JAL_AKREK_KET),
                                true, false);
                        NPCSpawning.spawnNpc(playerOwner, InfernoWaveData.JAL_AKREK_XIL, npc.absX,
                                npc.absY + 1, playerOwner.heightLevel, 1,
                                InfernoWaveData.getMax(InfernoWaveData.JAL_AKREK_XIL),
                                true, false);
                        NPCSpawning.spawnNpc(playerOwner, InfernoWaveData.JAL_AKREK_MEJ,
                                npc.absX + 1, npc.absY + 1, playerOwner.heightLevel, 1,
                                InfernoWaveData.getMax(InfernoWaveData.JAL_AKREK_MEJ),
                                true, false);
                        playerOwner.getInferno().setKillsRemaining(
                                playerOwner.getInferno().getKillsRemaining() + 3);
                    }

                    if (playerOwner != null) {
                        npcHandler.tzhaarDeathHandler(playerOwner, npc);
                        npcHandler.infernoDeathHandler(playerOwner, npc);
                    }

                    if (hydraInstance == null) {
                        npc.startAnimation(npc.getDeathAnimation());
                    }

                    if (npc.getNpcId() == 963) {
                        npc.actionTimer = 0;
                        npc.setDead(false);
                        npc.requestTransform(965);
                        npc.getHealth().reset();
                        npc.applyDead = false;
                        npc.startAnimation(Animation.RESET_ANIMATION);
                    }

                    npcHandler.resetPlayersInCombat(i);
                }
            }
            else if (npc.actionTimer == 0 && npc.applyDead && !npc.needRespawn) {
                int killerIndex = npc.killedBy;
                NPCDeath.dropItems(npc);

                npc.onDeath();

                if (killerIndex <= PlayerHandler.players.length - 1) {
                    Player target = PlayerHandler.players[npc.killedBy];

                    if (target != null) {
                        target.getSlayer().killTaskMonster(npc);
                        target.getBossTimers().death(npc);
                        target.getQuesting().handleNpcKilled(npc);
                    }
                }

                if (npc.getRaidsInstance() != null) {
                    Optional<Player> plrOpt = PlayerHandler.getOptionalPlayerByIndex(npc.killedBy);
                    npc.getRaidsInstance().handleMobDeath(plrOpt.orElse(null), type);
                }

                if (npc.inXeric()) {
                    Player killer = PlayerHandler.players[npc.killedBy];
                    npcHandler.xericDeathHandler(killer, npc);
                }

                npcHandler.appendBossKC(npc);
                npcHandler.handleGodwarsDeath(npc);
                npcHandler.handleDiaryKills(npc);
                npc.getRegionProvider().removeNpcClipping(npc);
                npc.absX = npc.makeX;
                npc.absY = npc.makeY;
                npc.getHealth().reset();
                npc.startAnimation(0x328);

                /*
                  Actions on certain npc deaths
                 */
                Skotizo skotizo = playerOwner != null ? playerOwner.getSkotizo() : null;
                switch (npc.getNpcId()) {
                    case 965:
                        npc.setNpcId(963);
                        break;

                    case 8611:
                        npc.setNpcId(8610);
                        break;
                    case TheUnbearable.NPC_ID:
                        if (playerOwner != null) {
                            PlayerHandler.executeGlobalMessage("@red@[EVENT]@blu@ the wildy boss [@red@Unbearable@blu@] has been defeated!");
                        }
                        TheUnbearable.rewardPlayers();
                        break;
                    case Hespori.NPC_ID:
                        PlayerHandler.executeGlobalMessage("@red@[EVENT]@blu@ the world boss [@gre@Hespori@blu@] has been defeated!");
                        Hespori.rewardPlayers(true);
                        break;
                    case FragmentOfSeren.NPC_ID:
                        if (playerOwner != null) {
                            PlayerHandler.executeGlobalMessage("@red@[EVENT]@blu@ the wildy boss [@red@Seren@blu@] has been defeated!");
                        }
                        FragmentOfSeren.rewardPlayers();
                        FragmentOfSeren.specialAmount = 0;
                        break;
                    case FragmentOfSeren.CRYSTAL_WHIRLWIND:
                        FragmentOfSeren.activePillars.remove(npc);
                        if (FragmentOfSeren.activePillars.isEmpty()) {
                            FragmentOfSeren.isAttackable = true;
                        }
                        npc.unregister();
                        break;
                    case 3127:
                        playerOwner.getFightCave().stop();
                        break;

                    case Skotizo.SKOTIZO_ID:
                        skotizo.end();
                        break;

                    case InfernoWaveData.TZKAL_ZUK:
                        if (playerOwner.getInferno() != null) {
                            playerOwner.getInferno().end(DisposeTypes.COMPLETE);
                        }
                        break;

                    case Skotizo.AWAKENED_ALTAR_NORTH:
                        Server.getGlobalObjects().remove(28923, 1694, 9904, skotizo.getHeight()); // Remove
                        // North
                        // -
                        // Awakened
                        // Altar
                        Server.getGlobalObjects().add(new GlobalObject(28924, 1694, 9904,
                                skotizo.getHeight(), 2, 10, -1, -1)); // North - Empty Altar
                        playerOwner.getPA().sendChangeSprite(29232, (byte) 0);
                        skotizo.altarCount--;
                        skotizo.northAltar = false;
                        skotizo.altarMap.remove(1);
                        break;
                    case Skotizo.AWAKENED_ALTAR_SOUTH:
                        Server.getGlobalObjects().remove(28923, 1696, 9871, skotizo.getHeight()); // Remove
                        // South
                        // -
                        // Awakened
                        // Altar
                        Server.getGlobalObjects().add(new GlobalObject(28924, 1696, 9871,
                                skotizo.getHeight(), 0, 10, -1, -1)); // South - Empty Altar
                        playerOwner.getPA().sendChangeSprite(29233, (byte) 0);
                        skotizo.altarCount--;
                        skotizo.southAltar = false;
                        skotizo.altarMap.remove(2);
                        break;
                    case Skotizo.AWAKENED_ALTAR_WEST:
                        Server.getGlobalObjects().remove(28923, 1678, 9888, skotizo.getHeight()); // Remove
                        // West
                        // -
                        // Awakened
                        // Altar
                        Server.getGlobalObjects().add(new GlobalObject(28924, 1678, 9888,
                                skotizo.getHeight(), 1, 10, -1, -1)); // West - Empty Altar
                        playerOwner.getPA().sendChangeSprite(29234, (byte) 0);
                        skotizo.altarCount--;
                        skotizo.westAltar = false;
                        skotizo.altarMap.remove(3);
                        break;
                    case Skotizo.AWAKENED_ALTAR_EAST:
                        Server.getGlobalObjects().remove(28923, 1714, 9888, skotizo.getHeight()); // Remove
                        // East
                        // -
                        // Awakened
                        // Altar
                        Server.getGlobalObjects().add(new GlobalObject(28924, 1714, 9888,
                                skotizo.getHeight(), 3, 10, -1, -1)); // East - Empty Altar
                        playerOwner.getPA().sendChangeSprite(29235, (byte) 0);
                        skotizo.altarCount--;
                        skotizo.eastAltar = false;
                        skotizo.altarMap.remove(4);
                        break;
                    case Skotizo.DARK_ANKOU:
                        skotizo.ankouSpawned = false;
                        break;

                    case 6615:
                        Scorpia.stage = 0;
                        break;
                    case 6600:
                        NPCSpawning.spawnNpc(6601, npc.absX, npc.absY, 0, 0, 0);
                        break;

                    case 6601:
                        NPCSpawning.spawnNpc(6600, npc.absX, npc.absY, 0, 0, 0);
                        npc.unregister();
                        NPC golem = NPCHandler.getNpc(6600);
                        if (golem != null) {
                            golem.actionTimer = 150;
                        }
                        break;
                    case 5890:
                        NPCHandler.despawn(5916, 0);
                        break;
                    case 6768:
                        npc.unregister();
                        break;
                }

                if (npc.getNpcId() == Npcs.ABYSSAL_SIRE) {
                    NPCHandler.kill(Npcs.SPAWN, npc.getHeight());
                }

                if (npc != null) {
                    if (npc.getBehaviour().isRespawn() && (npc.getInstance() == null || npc.getInstance().getConfiguration().isRespawnNpcs())) {
                        npc.needRespawn = true;
                        npc.actionTimer = RespawnTime.get(npc);
                    } else {
                        npc.unregister();
                    }
                }
            }
            else if (npc.actionTimer == 0) {
                if (npc.getNpcId() == 1739 || npc.getNpcId() == 1740
                        || npc.getNpcId() == 1741 || npc.getNpcId() == 1742
                        || npc.getNpcId() == 8583) {
                    // Don't respawn
                    return;
                }

                if (playerOwner != null && !npc.getBehaviour().isRespawnWhenPlayerOwned()) {
                    npc.unregister();
                } else {
                    if (playerOwner != null && (playerOwner.properLogout || playerOwner.isDisconnected()))
                        return;
                    if (npc.getInstance() != null && npc.getInstance().isDisposed()) {
                        logger.debug("NPC was going to respawn but instance was disposed {}", npc);
                        return;
                    }

                    if (hydraInstance != null) {
                        hydraInstance.respawn();
                        npc.unregister();
                        return;
                    }

                    // Child respawns with parent
                    if (npc.getParent() != null) {
                        return;
                    }

                    // Parent won't respawn until children are dead
                    if (npc.getChildren().stream().allMatch(child -> child.needRespawn)) {
                        npc.getChildren().forEach(child -> npcHandler.respawn(child.getIndex(), playerOwner));
                        npcHandler.respawn(i, playerOwner);
                    }
                }
            }
        }
    }
}
