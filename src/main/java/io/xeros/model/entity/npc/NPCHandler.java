package io.xeros.model.entity.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.AchievementDiaryKills;
import io.xeros.content.bosses.Cerberus;
import io.xeros.content.bosses.Skotizo;
import io.xeros.content.bosses.Vorkath;
import io.xeros.content.bosses.godwars.GodwarsNPCs;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosses.hydra.AlchemicalHydra;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.bosses.zulrah.Zulrah;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.core.HitDispatcher;
import io.xeros.content.combat.formula.rework.MagicCombatFormula;
import io.xeros.content.combat.formula.rework.RangeCombatFormula;
import io.xeros.content.combat.melee.MeleeExtras;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.barrows.brothers.Brother;
import io.xeros.content.minigames.fight_cave.Wave;
import io.xeros.content.minigames.inferno.InfernoWaveData;
import io.xeros.content.minigames.pest_control.PestControl;
import io.xeros.content.minigames.xeric.XericWave;
import io.xeros.content.questing.hftd.DagannothMother;
import io.xeros.content.skills.hunter.impling.PuroPuro;
import io.xeros.model.*;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definition.Npcs;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.npc.actions.LoadSpell;
import io.xeros.model.entity.npc.combat.CombatMethod;
import io.xeros.model.entity.npc.combat.CommonCombatMethod;
import io.xeros.model.entity.npc.data.AttackAnimation;
import io.xeros.model.entity.player.*;
import io.xeros.model.projectile.ProjectileEntity;
import io.xeros.util.Misc;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.xeros.model.entity.npc.NPCClipping.getDirection;

public class NPCHandler {

    private static final Logger logger = LoggerFactory.getLogger(NPCHandler.class);

    public static NPC getIndexNPC(int npcAttackingIndex) {
        return npcs[npcAttackingIndex];
    }

    public static int maxNPCs = 30000;
    public static int maxListedNPCs = 10000;
    public static NPC[] npcs = new NPC[maxNPCs];
    public static boolean projectileClipping = true;
    /**
     * Tekton variables
     */
    public static String tektonAttack = "MELEE";
    /**
     * Glod variables
     */
    public static String glodAttack = "MELEE";
    /**
     * Queen variables
     */
    public static String queenAttack = "MAGIC";
    @Getter
    private final NPCProcess npcProcess = new NPCProcess(this);

    public NPCHandler() {
    }

    public void init() {
        for (int i = 0; i < maxNPCs; i++) {
            npcs[i] = null;
        }
        startGame();
    }

    public void startGame() {
        for (int i = 0; i < PuroPuro.IMPLINGS.length; i++) {
            newNPC(PuroPuro.IMPLINGS[i][0], PuroPuro.IMPLINGS[i][1], PuroPuro.IMPLINGS[i][2], 0, 1, -1);
        }
        int random_spawn = Misc.random(2);
        int x = 0;
        int y = switch (random_spawn) {
            case 0 -> {
                x = 2620;
                yield 4347;
            }
            case 1 -> {
                x = 2607;
                yield 4321;
            }
            case 2 -> {
                x = 2589;
                yield 4292;
            }
            default -> 0;
        };
        newNPC(7302, x, y, 0, 1, -1);
    }

    /**
     * Random spawns
     */
    public boolean ringOfLife(Player player) {
        boolean defenceCape = SkillcapePerks.DEFENCE.isWearing(player);
        boolean maxCape = SkillcapePerks.isWearingMaxCape(player);
        if (player.getItems().isWearingItem(2570) || defenceCape || (maxCape && player.getRingOfLifeEffect())) {
            if (player.isTeleblocked()) {
                player.sendMessage("The ring of life effect does not work as you are teleblocked.");
                return false;
            }
            if (defenceCape || maxCape) {
                player.sendMessage("Your cape activated the ring of life effect and saved you!");
            } else {
                player.getItems().deleteEquipment(Player.playerRing);
                player.sendMessage("Your ring of life saved you!");
            }
            player.getPA().spellTeleport(3087, 3499, 0, false);
            return true;
        }
        return false;
    }

    public int register(NPC npc) {
        for (int i = 1; i < maxNPCs; i++) {
            if (npcs[i] == null) {
                npcs[i] = npc;
                return i;
            }
        }
        throw new IllegalStateException("Cannot register npc: " + npc);
    }

    public void stepAway(NPC npc) {
        int[][] points = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] k : points) {
            int dir = getDirection(k[0], k[1]);
            if (NPCDumbPathFinder.canMoveTo(npc, dir)) {
                NPCDumbPathFinder.walkTowards(npc, npc.getX() + NPCClipping.DIR[dir][0], npc.getY() + NPCClipping.DIR[dir][1]);
                break;
            }
        }
    }

    public void multiAttackGfx(NPC npc) {
        if (npc.projectileId < 0) return;
        for (int j = 0; j < PlayerHandler.players.length; j++) {
            if (PlayerHandler.players[j] != null) {
                Player c = PlayerHandler.players[j];
                if (c.heightLevel != npc.heightLevel) continue;
                if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS)) {
                    continue;
                }
                if (PlayerHandler.players[j].goodDistance(c.absX, c.absY, npc.absX, npc.absY, 15)) {
                    int nX = npc.getX() + offset(npc);
                    int nY = npc.getY() + offset(npc);
                    int pX = c.getX();
                    int pY = c.getY();
                    int offX = (nX - pX) * -1;
                    int offY = (nY - pY) * -1;
                    int centerX = nX + npc.getSize() / 2;
                    int centerY = nY + npc.getSize() / 2;
                    c.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, getProjectileSpeed(npc), npc.projectileId, getProjectileStartHeight(npc.getNpcId(), npc.projectileId), getProjectileEndHeight(npc.getNpcId(), npc.projectileId), -c.getIndex() - 1, 65);
                    if (npc.getNpcId() == 7554) {
                        c.getPA().sendPlayerObjectAnimation(3220, 5738, 7371, 10, 3);
                    }
                }
            }
        }
    }

    public boolean switchesAttackers(NPC npc) {
        return switch (npc.getNpcId()) {
            case 963, 965, 3129, 2208, 239, 6611, 6612, 494, 319, 7554, 320, 5535, 2551, 6609, 2552, 2553, 2559, 2560, 2561, 2563, 2564, 2565, 2892,
                 2894, 1046, 6615, 6616, 7604, 7605, 7606, 7544, 5129, FragmentOfSeren.NPC_ID, 8781 -> true;
            default -> false;
        };
    }

    public static boolean isSpawnedBy(Player player, NPC npc) {
        if (player != null && npc != null)
            return npc.spawnedBy == player.getIndex() || npc.getPlayerAttackingIndex() == player.getIndex();
        return false;
    }

    public static boolean isFightCaveNpc(NPC npc) {
        if (npc == null) return false;
        return switch (npc.getNpcId()) {
            case Wave.TZ_KEK_SPAWN, Wave.TZ_KIH, Wave.TZ_KEK, Wave.TOK_XIL, Wave.YT_MEJKOT, Wave.KET_ZEK, Wave.TZTOK_JAD -> true;
            default -> false;
        };
    }

    public static boolean isInfernoNpc(NPC npc) {
        if (npc == null) return false;
        return switch (npc.getNpcId()) {
            case InfernoWaveData.JAL_NIB, InfernoWaveData.JAL_MEJRAH, InfernoWaveData.JAL_AK, InfernoWaveData.JAL_AKREK_MEJ,
                 InfernoWaveData.JAL_AKREK_XIL, InfernoWaveData.JAL_AKREK_KET, InfernoWaveData.JAL_IMKOT, InfernoWaveData.JAL_XIL,
                 InfernoWaveData.JAL_ZEK, InfernoWaveData.JALTOK_JAD, InfernoWaveData.YT_HURKOT, InfernoWaveData.TZKAL_ZUK,
                 InfernoWaveData.ANCESTRAL_GLYPH, InfernoWaveData.JAL_MEJJAK -> true;
            default -> false;
        };
    }

    public static boolean isXericNpc(NPC npc) {
        if (npc == null) return false;
        return switch (npc.getNpcId()) {
            case XericWave.RUNT, XericWave.BEAST, XericWave.RANGER, XericWave.MAGE, XericWave.SHAMAN, XericWave.LIZARD, XericWave.VESPINE,
                 XericWave.AIR_CRAB, XericWave.FIRE_CRAB, XericWave.EARTH_CRAB, XericWave.WATER_CRAB, XericWave.ICE_FIEND, XericWave.VANGUARD,
                 XericWave.VESPULA, XericWave.TEKTON, XericWave.MUTTADILE, XericWave.VASA, XericWave.ICE_DEMON -> true;
            default -> false;
        };
    }

    public static boolean isSkotizoNpc(NPC npc) {
        if (npc == null) return false;
        return switch (npc.getNpcId()) {
            case Skotizo.SKOTIZO_ID, Skotizo.AWAKENED_ALTAR_NORTH, Skotizo.AWAKENED_ALTAR_SOUTH, Skotizo.AWAKENED_ALTAR_WEST,
                 Skotizo.AWAKENED_ALTAR_EAST, Skotizo.REANIMATED_DEMON, Skotizo.DARK_ANKOU -> true;
            default -> false;
        };
    }

    /**
     * Attack animations
     *
     * @return the animation to be performed.
     */
    public static int getAttackEmote(NPC npc) {
        return AttackAnimation.handleEmote(npc);
    }

    /**
     * Attack delay
     *
     * @return the delay were setting
     */
    public int getNpcDelay(NPC npc) {
        return switch (npc.getNpcId()) {
            case InfernoWaveData.JAL_NIB, Brother.KARIL, 2205, 499, InfernoWaveData.JAL_AKREK_XIL, InfernoWaveData.JAL_AKREK_MEJ,
                 InfernoWaveData.JAL_AKREK_KET, InfernoWaveData.JAL_AK -> 4;
            case InfernoWaveData.JAL_MEJRAH, 3162, Brother.DHAROK, 2025, 2028, 963, 965, 498, InfernoWaveData.JAL_MEJJAK -> 7;
            case InfernoWaveData.JAL_IMKOT, 3167, 2558, 2559, 2560, 2561, 2215, Brother.AHRIM, InfernoWaveData.YT_HURKOT, InfernoWaveData.JAL_ZEK,
                 InfernoWaveData.JAL_XIL -> 6;
            case InfernoWaveData.JALTOK_JAD -> 9;
            case 6611, 6612 -> npc.getAttackType() == CombatType.MAGE ? 6 : 5;
            case 319 -> npc.getAttackType() == CombatType.MAGE ? 7 : 6;
            case 7554 -> npc.getAttackType() == CombatType.MAGE ? 4 : 6;
            case 3127 -> 8;
            // saradomin gw boss
            case 2562, 7597, 7547 -> 2;
            default -> 5;
        };
    }

    /**
     * Projectile start height
     *
     * @param npcType      the npc to perform the projectile
     * @param projectileId the projectile to be performed
     */
    private int getProjectileStartHeight(int npcType, int projectileId) {
        switch (npcType) {
            case 1377:
                return 120;
            case 2044:
            case 1443:
                return 60;
            case 3162:
                return 0;
            case 3127:
            case 3163:
            case InfernoWaveData.JALTOK_JAD:
            case 3164:
            case 3167:
            case 3174:
                return 110;
            case 6610:
                if (projectileId == 165) {
                    return 20;
                }
                break;
        }
        return 43;
    }

    /**
     * Projectile end height
     *
     * @param npcType      the npc to perform the projectile
     * @param projectileId the projectile to be performed
     */
    private int getProjectileEndHeight(int npcType, int projectileId) {
        switch (npcType) {
            case 1605:
                return 0;
            case 3162:
                return 15;
            case Npcs.HESPORI:
                return 20;
            case 6610:
                switch (projectileId) {
                    case 165:
                        return 30;
                    case 1996:
                        return 0;
                }
                break;
        }
        return 31;
    }

    /**
     * Hit delay
     *
     * @return the delay were setting
     */
    public int getHitDelay(NPC npc) {
        switch (npc.getNpcId()) {
            case 7706:
                return 7;
            case 1605:
            case 1606:
            case 1607:
            case 1608:
            case 1609:
            case 499, 498:
                return 4;
            case 6611:
            case 6612:
                return npc.getAttackType() == CombatType.MAGE ? 3 : 2;
            case 1672:
            case 1675:
            case 1046:
            case 1049:
            case 6610:
            case 2265:
            case 2266:
            case 2054:
            case 2892:
            case 2894:
            case 3125:
            case 3121:
            case 2167:
            case 2558:
            case 2559:
            case 2560:
            case 2209:
            case 2211:
            case 2218:
            case 2242:
            case 2244:
            case 3160:
            case 3163:
            case 3167:
            case 3174:
            case 2028:
            case 8781:
                return 3;
            case 2212:
            case 2217:
            case 3161:
            case 3162:
            case 3164:
            case 3168:
            case 6914:
                // Lizardman, Lizardman brute
            case 6915:
            case 6916:
            case 6917:
            case 6918:
            case 6919:
            case 2025:
                return 4;
            case 3127:
            case InfernoWaveData.JALTOK_JAD:
                if (npc.getAttackType() == CombatType.RANGE || npc.getAttackType() == CombatType.MAGE) {
                    return 5;
                } else {
                    return 2;
                }
            default:
                return 2;
        }
    }

    public static NPC newNPC(int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit) {
        // first, search for a free slot
        int slot = -1;
        for (int i = 1; i < maxNPCs; i++) {
            if (npcs[i] == null) {
                slot = i;
                break;
            }
        }
        if (slot == -1) {
            throw new IllegalStateException();
        }
        return NPCSpawning.newNPC(slot, npcType, x, y, heightLevel, WalkingType, maxHit);
    }

    public void resetUpdateFlags() {
        for (int i = 0; i < maxNPCs; i++) {
            if (npcs[i] == null) continue;
            npcs[i].clearUpdateFlags();
        }
    }

    public long getNpcCount() {
        return Arrays.stream(npcs).filter(Objects::nonNull).count();
    }

    /**
     * Handles processes for NPCHandler every 600ms
     */
    public void process() {
        for (int i = 0; i < maxNPCs; i++) {
            NPC npc = npcs[i];
            if (npc != null) {
                try {
                    // Don't need to process an unregistered npc
                    if (npc.processDeregistration()) {
                        return;
                    }

                    npc.process();

                    // They might have been unregistered during the processing
                    npc.processDeregistration();
                } catch (Exception e) {
                    logger.error("Error occurred while processing npc {}", npc.toString());
                    e.printStackTrace(System.err);
                    npc.unregister();
                }
            }
        }
    }

    void respawn(int i, Player owner) {
        NPC newNpc = null;
        try {
            Preconditions.checkArgument(npcs[i].needRespawn);
            int newType = npcs[i].getNpcId();
            int newX = npcs[i].makeX;
            int newY = npcs[i].makeY;
            int newH = npcs[i].heightLevel;
            int newWalkingType = npcs[i].walkingType;
            int newMaxHit = npcs[i].maxHit;
            int parent = npcs[i].parentNpc;
            NpcStats npcStats = npcs[i].getDefaultNpcStats();
            InstancedArea instance = npcs[i].getInstance();
            List<Integer> children = npcs[i].children;
            NPCBehaviour behaviour = npcs[i].getBehaviour();
            //Preconditions.checkState(npcs[i].getClass().equals(NPC.class), "Extended npc trying to respawn as base class: " + npcs[i]);
            //Preconditions.checkState(instance == null || !instance.isDisposed(), "Instance is disposed.");
            //Preconditions.checkState(owner == null || !owner.isDisconnected(), "Owner disconnected.");
            NPC oldInstance = npcs[i];
            npcs[i].unregisterInstant();
            if (owner != null && owner.isDisconnected() || instance != null && instance.isDisposed()) {
                return;
            }
            newNpc = NPCFactory.create(oldInstance, i, newType, newX, newY, newH, newWalkingType, newMaxHit);
            newNpc.parentNpc = parent;
            newNpc.children = children;
            if (instance != null) {
                instance.add(newNpc);
            }
            newNpc.spawnedBy = owner == null ? 0 : owner.getIndex();
            newNpc.getBehaviour().copy(behaviour);
            newNpc.setDefaultNpcStats(npcStats);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            if (npcs[i] != null) {
                npcs[i].unregister();
            }
            if (newNpc != null) {
                newNpc.unregister();
            }

            logger.error("Error occurred while respawning npc {}", (npcs[i] != null ? npcs[i] : newNpc != null ? newNpc : "Both npcs were null"), e);
        }
    }

    /**
     * Poison damage
     *
     * @param npc the npc whom can be poisonous
     * @return the amount of damage the poison will begin on
     */
    public int getPoisonDamage(NPC npc) {
        return switch (npc.getNpcId()) {
            case 3129 -> 16;
            case 3021 -> 5;
            case 957 -> 4;
            case 959 -> 6;
            case 6615 -> 10;
            default -> 0;
        };
    }

    public static Optional<AlchemicalHydra> getHydraInstance(NPC npc) {
        if (npc.getInstance() != null && npc.getInstance() instanceof AlchemicalHydra)
            return Optional.of((AlchemicalHydra) npc.getInstance());
        return Optional.empty();
    }

    /**
     * Multi attacks from a distance
     *
     * @param npc the npc whom can pefrom multiattacks from a distance
     * @return the distance that the npc can reach from
     */
    private int multiAttackDistance(NPC npc) {
        if (npc == null) {
            return 0;
        }
        return switch (npc.getNpcId()) {
            case 319, 239, 8031, 8030 -> 35;
            case 7554 -> 30;
            case TheUnbearable.NPC_ID, FragmentOfSeren.NPC_ID, Hespori.NPC_ID -> 25;
            default -> 15;
        };
    }

    /**
     * Multi attack damage
     */
    public void multiAttackDamage(NPC npc) {
        int damage;
        Hitmark hitmark = Hitmark.MISS;
        for (int j = 0; j < PlayerHandler.players.length; j++) {
            if (PlayerHandler.players[j] != null) {
                Player c = PlayerHandler.players[j];
                damage = Misc.random(getMaxHit(c, npc));
                if (c.isDead || c.heightLevel != npc.heightLevel) continue;
                if (PlayerHandler.players[j].isInvisible()) {
                    continue;
                }
                if (PlayerHandler.players[j].goodDistance(c.absX, c.absY, npc.absX, npc.absY, multiAttackDistance(npc))) {
                    if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS)) {
                        continue;
                    }
                    if (npc.getAttackType() == CombatType.SPECIAL) {
                        if (npc.getNpcId() == 5862) {
                            if (cerberusGroundCoordinates.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
                                continue;
                            }
                        }
                        if (npc.getNpcId() == 6618) {
                            if (archSpellCoordinates.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
                                continue;
                            }
                        }
                        if (npc.getNpcId() == 8609) {
                            if (hydraPoisonCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
                                continue;
                            }
                        }
                        if (npc.getNpcId() == 7566) {
                            if (vasaRockCoordinates.parallelStream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
                                continue;
                            }
                        }
                        if (npc.getNpcId() == 8030) {
                            if (DragonGroundCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
                                continue;
                            }
                        }
                        if (npc.getNpcId() == 6766) {
                            if (explosiveSpawnCoordinates.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
                                continue;
                            }
                        }
                        if (Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR)) {
                            if (!Boundary.isIn(c, Boundary.CORPOREAL_BEAST_LAIR)) {
                                return;
                            }
                        }
                        if (npc.getNpcId() == 6619) {
                            if (fanaticSpellCoordinates.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
                                continue;
                            }
                        }
                        if (npc.getNpcId() == 6611 || npc.getNpcId() == 6612) {
                            if (!(c.absX > npc.absX - 5 && c.absX < npc.absX + 5 && c.absY > npc.absY - 5 && c.absY < npc.absY + 5)) {
                                continue;
                            }
                            c.sendMessage("Vet'ion pummels the ground sending a shattering earthquake shockwave through you.");
                            createVetionEarthquake(c);
                        }
                        if (npc.getNpcId() == 319) {
                            if (corpSpellCoordinates.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
                                continue;
                            }
                        }
                        c.appendDamage(npc, damage, hitmark);
                    } else if (npc.getAttackType() == CombatType.DRAGON_FIRE) {
                        int resistance = c.getItems().isWearingItem(1540) || c.getItems().isWearingItem(11283) || c.getItems().isWearingItem(11284) || (npc.getNpcId() == 465 && c.getItems().isWearingItem(2890)) ? 1 : 0;
                        if ((System.currentTimeMillis() - c.lastAntifirePotion) < c.antifireDelay) {
                            resistance = resistance + 1;
                        }
                        if (resistance == 0) {
                            damage = Misc.random(getMaxHit(c, npc));
                            c.sendMessage("You are badly burnt by the dragon fire!");
                        } else if (resistance == 1)
                            damage = Misc.random(15);
                        else if (resistance == 2 || (resistance == 1 && c.prayerActive[16]))
                            damage = 0;
                        if (c.getHealth().getCurrentHealth() - damage < 0)
                            damage = c.getHealth().getCurrentHealth();
                        c.gfx100(npc.endGfx);
                        c.appendDamage(npc, damage, hitmark);
                    } else if (npc.getAttackType() == CombatType.MAGE) {
                        if (npc.getNpcId() == 6611 || npc.getNpcId() == 6612) {
                            if (vetionSpellCoordinates.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
                                continue;
                            }
                        }
                        if (!c.protectingMagic()) {
                            double accuracy = MagicCombatFormula.STANDARD.getAccuracy(npc, c);
                            boolean isAccurate = accuracy >= HitDispatcher.rand.nextDouble();

                            if (isAccurate) {
                                c.appendDamage(npc, damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
                            } else {
                                c.appendDamage(npc, 0, Hitmark.MISS);
                            }
                        } else {
                            switch (npc.getNpcId()) {
                                case 1046:
                                case 3162:
                                case 6610:
                                case 6611:
                                case 6612:
                                case TheUnbearable.NPC_ID:
                                    //Magic 60%,
                                case FragmentOfSeren.NPC_ID:
                                case Hespori.NPC_ID:
                                    damage *= 0.6;
                                    break;
                                case 7554, 319:
                                    if (c.protectingMagic()) damage /= 2;
                                    break;
                                default:
                                    damage = 0;
                                    break;
                            }
                            c.appendDamage(npc, damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
                        }
                    } else if (npc.getAttackType() == CombatType.RANGE) {
                        if (!c.protectingRange()) {
                            double accuracy = RangeCombatFormula.STANDARD.getAccuracy(npc, c);
                            boolean isAccurate = accuracy >= HitDispatcher.rand.nextDouble();

                            if (isAccurate) {
                                c.appendDamage(npc, damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
                            } else {
                                c.appendDamage(npc, 0, Hitmark.MISS);
                            }
                        } else {
                            damage = 0;
                            c.appendDamage(npc, damage, Hitmark.MISS);
                        }
                    }
                    if (npc.endGfx > 0) {
                        c.gfx0(npc.endGfx);
                    }
                    MeleeExtras.appendVengeanceNPC(c, damage, npc);
                }
            }
        }
    }

    /**
     * Multi attacks
     *
     * @return true if it can, false otherwise
     */
    public boolean multiAttacks(NPC npc) {
        switch (npc.getNpcId()) {
            case 7554:
                return npc.getAttackType() == CombatType.SPECIAL || npc.getAttackType() == CombatType.MAGE || npc.getAttackType() == CombatType.RANGE;
            case FragmentOfSeren.NPC_ID, 2558, Hespori.NPC_ID:
                return true;
            case 6611:
            case 6612:
            case 6618:
            case 6619:
            case 319:
            case 6766:
            case 6768:
            case 7617:
                return npc.getAttackType() == CombatType.SPECIAL || npc.getAttackType() == CombatType.MAGE;
            case 8609, 7604, 7605, 7606, 8781:
                return npc.getAttackType() == CombatType.SPECIAL;
            case TheUnbearable.NPC_ID, 3162, 6610:
                return npc.getAttackType() == CombatType.MAGE;
            case 1046:
                return npc.getAttackType() == CombatType.MAGE || npc.getAttackType() == CombatType.SPECIAL && Misc.random(3) == 0;
            case 2562:
                if (npc.getAttackType() == CombatType.MAGE) return true;
            case 2215:
                return npc.getAttackType() == CombatType.RANGE;
            case 963:
            case 965:
                return npc.getAttackType() == CombatType.MAGE || npc.getAttackType() == CombatType.RANGE;
            default:
                return false;
        }
    }

    /**
     * Godwars kill
     *
     * @param npc the godwars npc whom been killed
     */
    void handleGodwarsDeath(NPC npc) {
        Player player = PlayerHandler.players[npc.killedBy];
        if (!GodwarsNPCs.NPCS.containsKey(npc.getNpcId())) {
            return;
        }
        if (player != null && player.getGodwars() != null) {
            player.getGodwars().increaseKillcount(GodwarsNPCs.NPCS.get(npc.getNpcId()));
        }
        /*
         * if (Misc.random(60 + 10 * player.getItems().getItemCount(Godwars.KEY_ID,
         * true)) == 1) { /** Key will not drop if player owns more than 3 keys already
         *
         * int key_amount =
         * player.getDiaryManager().getWildernessDiary().hasCompleted("ELITE") ? 6 : 3;
         * if (player.getItems().getItemCount(Godwars.KEY_ID, true) > key_amount) {
         * return; } Server.itemHandler.createGroundItem(player, Godwars.KEY_ID,
         * npc.getX(), npc.getY(), player.heightLevel, 1, player.getIndex()); }
         */
    }

    /**
     * Handles kills towards the achievement diaries
     *
     * @param npc The npc killed.
     */
    void handleDiaryKills(NPC npc) {
        Player player = PlayerHandler.players[npc.killedBy];
        if (player != null) {
            AchievementDiaryKills.kills(player, npc.getNpcId());
        }
    }

    /**
     * Tzhaar kill
     *
     * @param player the player who killed a tzhaar
     */
    void tzhaarDeathHandler(Player player, NPC npc) {
        // hold a vit plz
        if (npc != null) {
            if (player != null) {
                if (player.getFightCave() != null) {
                    if (isFightCaveNpc(npc)) killedTzhaar(player);
                }
            }
        }
    }

    void infernoDeathHandler(Player player, NPC npc) {
        // hold a vit plz
        if (npc != null) {
            if (player != null) {
                if (player.getInferno() != null) {
                    if (isInfernoNpc(npc)) killedInferno(player);
                }
            }
        }
    }

    void xericDeathHandler(Player player, NPC npc) {
        // hold a vit plz
        if (player != null) {
            if (player.getXeric() != null) {
                if (isXericNpc(npc)) killedXeric(player, npc);
            }
        }
    }

    private void killedXeric(Player player, NPC npc) {
        if (Boundary.isIn(player, Boundary.XERIC)) {
            if (player.getXeric() != null) {
                player.getXeric().setKillsRemaining(player.getXeric().getKillsRemaining() - 1);
                player.getXeric().getSpawns().remove(npc);
                if (player.getXeric().getKillsRemaining() == 0) {
                    player.getXeric().incWaveID();
                    player.getXeric().spawn();
                }
            }
        }
    }

    private void killedTzhaar(Player player) {
        if (player.getFightCave() != null) {
            player.getFightCave().setKillsRemaining(player.getFightCave().getKillsRemaining() - 1);
            if (player.getFightCave().getKillsRemaining() <= 0) {
                player.waveId++;
                player.getFightCave().spawn();
            }
        }
    }

    private void killedInferno(Player player) {
        if (player.getInferno() != null) {
            player.getInferno().setKillsRemaining(player.getInferno().getKillsRemaining() - 1);
            if (player.getInferno().getKillsRemaining() == 0) {
                player.getInferno().setInfernoWaveId(player.getInferno().getInfernoWaveId() + 1);
                player.getInferno().spawn();
            }
        }
    }

    public void appendBossKC(NPC npc) {
        if (npc == null || npc.killedBy < 0) return;
        Player player = PlayerHandler.players[npc.killedBy];
        if (player == null) {
            return;
        }
        if (NpcDef.forId(npc.getNpcId()).getCombatLevel() >= 170) {
            Achievements.increase(player, AchievementType.SLAY_BOSSES, 1);
        }
    }

    /**
     * Resets players in combat
     */
    public static NPC getNpc(int npcType) {
        for (NPC npc : npcs) if (npc != null && npc.getNpcId() == npcType) return npc;
        return null;
    }

    public static Optional<NPC> getNpcAtIndex(int index) {
        if (index < 0 || index >= npcs.length || npcs[index] == null) {
            return Optional.empty();
        } else {
            return Optional.of(npcs[index]);
        }
    }

    public void resetPlayersInCombat(int i) {
        for (int j = 0; j < PlayerHandler.players.length; j++) {
            if (PlayerHandler.players[j] != null)
                if (PlayerHandler.players[j].underAttackByNpc == i) PlayerHandler.players[j].underAttackByNpc = 0;
        }
    }

    public static NPC getNpc(int npcType, int x, int y) {
        for (NPC npc : npcs) if (npc != null && npc.getNpcId() == npcType && npc.absX == x && npc.absY == y) return npc;
        return null;
    }

    public static NPC getNpc(int npcType, int x, int y, int height) {
        for (NPC npc : npcs) {
            if (npc != null && npc.getNpcId() == npcType && npc.absX == x && npc.absY == y && npc.heightLevel == height) {
                return npc;
            }
        }
        return null;
    }

    public static NPC getNpc(int npcType, int height) {
        for (NPC npc : npcs) {
            if (npc != null && npc.getNpcId() == npcType && npc.heightLevel == height) {
                return npc;
            }
        }
        return null;
    }

    public static NPC getNpcDist(int npcType, Position pos, int dist) {
        for (NPC npc : npcs) {
            if (npc != null && npc.getPosition().withinDistance(pos, dist)) {
                return npc;
            }
        }
        return null;
    }

    /**
     * Npc Follow Player
     */
    public int GetMove(int Place1, int Place2) {
        if ((Place1 - Place2) == 0) {
            return 0;
        } else if ((Place1 - Place2) < 0) {
            return 1;
        } else if ((Place1 - Place2) > 0) {
            return -1;
        }
        return 0;
    }

    public void followPlayer(NPC npc, int playerId) {
        if (PlayerHandler.players[playerId] == null) {
            return;
        }
        Player player = PlayerHandler.players[playerId];
        if (PlayerHandler.players[playerId].respawnTimer > 0 || npc.heightLevel != player.heightLevel) {
            npc.facePlayer(0);
            npc.randomWalk = true;
            npc.underAttack = false;
            return;
        }
        // Vorkath doesn't follow player
        if (Arrays.stream(Vorkath.NPC_IDS).anyMatch(id -> npc.getNpcId() == id)) {
            return;
        }
        if (Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR)) {
            if (!Boundary.isIn(player, Boundary.CORPOREAL_BEAST_LAIR)) {
                npc.setPlayerAttackingIndex(0);
                return;
            }
        }
        if (Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR)) {
            if (!Boundary.isIn(player, Boundary.CORPOREAL_BEAST_LAIR)) {
                npc.setPlayerAttackingIndex(0);
                return;
            }
        }
        if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
            if (!Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
                npc.setPlayerAttackingIndex(0);
                return;
            }
        }
        if (Boundary.isIn(npc, Boundary.ZULRAH) && (npc.getNpcId() >= 2042 && npc.getNpcId() <= 2044 || npc.getNpcId() == 6720)) {
            return;
        }
        if (npc.getNpcId() == InfernoWaveData.JAL_NIB) {
            npc.facePlayer(playerId);
            return;
        }
        if (npc.getNpcId() == FragmentOfSeren.CRYSTAL_WHIRLWIND) {
            return;
        }
        npc.facePlayer(playerId);
        if (npc.getNpcId() >= 1739 && npc.getNpcId() <= 1742 || npc.getNpcId() == 7413 || npc.getNpcId() >= 7288 && npc.getNpcId() <= 7294) {
            return;
        }
        int playerX = PlayerHandler.players[playerId].absX;
        int playerY = PlayerHandler.players[playerId].absY;
        npc.randomWalk = false;
        int followDistance = followDistance(npc);
        double distance = ((double) distanceRequired(npc)) + (npc.getSize() > 1 ? 0.5 : 0.0);
        followTick(npc, player, distance, followDistance, false);
        if (npc.getBehaviour().isRunnable()) {
            followTick(npc, player, distance, followDistance, true);
        }
    }

    public boolean fixUnder(NPC npc, Player player) {
        if (npc.insideOf(player.absX, player.absY)) {
            npc.randomWalk = false;
            npc.facePlayer(player.getIndex());
            NPCDumbPathFinder.generateMovement(npc);
            return true;
        }
        return false;
    }

    private void followTick(NPC npc, Player player, double distance, int followDistance, boolean run) {
        if (fixUnder(npc, player))
            return;
        Position source = run ? npc.getPosition().translate(npc.walkDirection) : npc.getPosition();
        if (npc.getDistance(source, player.absX, player.absY) <= distance) return;
        if (npc.spawnedBy > 0 || !npc.getBehaviour().isWalkHome() || (npc.absX < npc.makeX + followDistance) && (npc.absX > npc.makeX - followDistance) && (npc.absY < npc.makeY + followDistance) && (npc.absY > npc.makeY - followDistance)) {
            if (!run || npc.walkDirection != Direction.NONE) {
                NPCDumbPathFinder.follow(npc, player, run);
            }
        } else {
            npc.facePlayer(0);
            npc.randomWalk = true;
            npc.underAttack = false;
            npc.walkingHome = true;
            npc.setPlayerAttackingIndex(0);
        }
    }

    /**
     * Distanced required to attack
     */
    public int distanceRequired(NPC npc) {
        if (npc.getCurrentAttack() != null) {
            return npc.getCurrentAttack().getDistanceRequiredForAttack();
        }
        switch (npc.getNpcId()) {
            case Npcs.ABYSSAL_SIRE, 1443:
                return npc.getAttackType() == CombatType.MELEE ? 3 : 14;
            case InfernoWaveData.TZKAL_ZUK, 5867, 5868, 5869, 7617:
                return 30;
            case InfernoWaveData.JALTOK_JAD:
            case InfernoWaveData.JAL_XIL:
            case InfernoWaveData.JAL_ZEK, 1046, 6610, 3163, 2208, InfernoWaveData.JAL_AK:
                return 8;
            case InfernoWaveData.JAL_IMKOT:
                return 1;
            case InfernoWaveData.JAL_AKREK_XIL:
            case InfernoWaveData.JAL_AKREK_MEJ:
            case InfernoWaveData.JAL_MEJRAH, 1610, 1611, 1612, 2209, 2211, 2212, 2242, 2244, 3160, 3161, 3174:
                return 4;
            case TheUnbearable.NPC_ID, 3131, 3132, 494, 492, 6609, 5535, 6370, 7559, 7560, 8028:
                return 10;
            case Hespori.NPC_ID:
                return 18;
            case Skotizo.SKOTIZO_ID:
                return npc.getAttackType() == CombatType.MAGE ? 25 : 3;
            /* Hydra */
            case 8609:
                return 26;
            /*xeric monsters*/
            case 7576:
                //crabs
            case 7577:
            case 7578:
            case 7579:
                return npc.getAttackType() == CombatType.MELEE ? 1 : 2;
            case 7586:
                //ice fiend
                return npc.getAttackType() == CombatType.MELEE ? 1 : 2;
            case 7585:
                //ice demon
                return npc.getAttackType() == CombatType.MELEE ? 1 : 2;
            case 5862:
            case 6766:
            case 6768:
            case 7144:
            case 7145:
            case 7146:
            case 9293:
                return npc.getAttackType() == CombatType.MELEE ? 1 : 7;
            case 9021:
                //melee
            case 9022:
                //range
            case 9023:
                //mage
            case 9024:
                //death
                return npc.getAttackType() == CombatType.MELEE ? 2 : 7;
            case 7604:
                // Skeletal mystic
            case 7605:
                // Skeletal mystic
            case 7606:
                // Skeletal mystic
            case FragmentOfSeren.NPC_ID:
                return 8;
            case 319:
                if (npc.getAttackType() == CombatType.MELEE) return 4;
                //case 5890:
            case 7544:
            case 5129:
                return npc.getAttackType() == CombatType.MELEE ? 3 : 7;
            case 6914:
                // Lizardman, Lizardman brute
            case 6915:
            case 6916:
            case 6917:
            case 6918:
            case 6919:
                return npc.getAttackType() == CombatType.MAGE ? 4 : 1;
            case 6618:
                return npc.getAttackType() == CombatType.RANGE || npc.getAttackType() == CombatType.SPECIAL ? 4 : 1;
            case 465:
                return npc.getAttackType() == CombatType.RANGE || npc.getAttackType() == CombatType.SPECIAL ? 6 : 2;
            case 6615:
                // Scorpia
            case 6619:
                // Chaos fanatic
                return 4;
            case 6367:
            case 6368:
            case 6369:
            case 6371:
            case 6372:
            case 6373:
            case 6374:
            case 6375:
            case 6376:
            case 6377:
            case 6378:
                if (npc.getAttackType() == CombatType.MAGE || npc.getAttackType() == CombatType.RANGE) return 8;
                else return 4;
            case 498:
            case 499, 8781, 2025, 2028, 2218:
                return 6;
            case 1672:
                // Ahrim
            case 1675:
                // Karil
            case 983:
                // Dagannoth mother
            case 984:
            case 985:
            case 987:
                return 8;
            case 986:
            case 988:
                return 3;
            case 3162:
            case 3167:
            case 3168, 2552, 2553, 2556, 2557, 2558, 2559, 2560, 2564, 2565, 2217:
                return 9;
            case 2205:
                return npc.getAttackType() == CombatType.MAGE ? 4 : 1;
            case Zulrah.SNAKELING, 3130, 2206, 2207, 2267, 2562:
                return 2;
            case 2215:
                return npc.getAttackType() == CombatType.MELEE ? 3 : 12;
            case 7936:
                //start of revs
            case 7940:
            case 7931:
            case 7932:
            case 7937:
            case 7939:
            case 7935:
            case 7934:
            case 7938:
            case 7933:
                return npc.getAttackType() == CombatType.MELEE ? 2 : 8;
            case 2042:
            case 2043:
            case 2044:
            case 7554:
                return 25;
            case 3164:
            case 1049, 3129:
                return 5;
            case 6611:
            case 6612:
                return npc.getAttackType() == CombatType.SPECIAL || npc.getAttackType() == CombatType.MAGE ? 12 : 3;
            case 2054:
                // chaos ele
            case 3125:
            case 3121:
            case 2167:
            case 3127:
                return npc.getAttackType() == CombatType.MELEE ? 2 : 8;
            case 2265:
                // dag kings
            case 2266:
                return 4;
            case 239:
                return npc.getAttackType() == CombatType.DRAGON_FIRE ? 18 : 4;
            case 8030:
            case 8031:
                return npc.getAttackType() == CombatType.DRAGON_FIRE ? 20 : 3;
            // things around dags
            case 2892:
            case 2894:
                return 10;
            default:
                return 1;
        }
    }

    /**
     * Cerberus
     */
    public int followDistance(NPC npc) {
        if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) || Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR) || Boundary.isIn(npc, Boundary.CERBERUS_BOSSROOMS)) {
            return 20;
        }
        if (Boundary.isIn(npc, Boundary.XERIC)) {
            return 128;
        }
        return switch (npc.getNpcId()) {
            case 8622, 8621, 8620, 8619, 8615, 239, 8031, 8030 -> 40;
            case 2045 -> 20;
            case 6615 -> 30;
            /* Hydra */
            case 8609 -> 30;
            case 1739, 1740, 1741, 1742, 7413, 7288, 7290, 7292, 7294, Npcs.HESPORI -> -1;
            // Barrows tunnel NPCs
            // Lizardman, Lizardman brute
            case 1678, 1679, 1680, 1683, 1684, 1685, 484, 7276, 135, 6914, 6915, 6916, 6917, 6918, 6919 -> 4;
            //start of revs
            case 2209, 2211, 2212, 2233, 2234, 2235, 2237, 2241, 2242, 2243, 2244, 2245, 3133, 3134, 3135, 3137, 3138, 3139, 3140, 3141, 3159, 3160,
                 3161, 3166, 3167, 3168, 7936, 7940, 7931, 7932, 7937, 7939, 7935, 7934, 7938, 7933 -> 4;
            case 2205, 2206, 2207, 2208, 2215, 2216, 2217, 2218, 3129, 3130, 3131, 3132, 3162, 3163, 3164, 3165 -> 22;
            case 6611, 6612, 963, 965, 7544 -> 15;
            case 5129, 319 -> 9;
            case 2551, 2562, 2563, 2265, 2266, 2267 -> 8;
            case 8781 -> 3;
            default -> 10;
        };
    }

    public int getProjectileSpeed(NPC npc) {
        return switch (npc.getNpcId()) {
            case 498 -> 120;
            case 499 -> 105;
            case 8781 -> 75;
            case 3127, InfernoWaveData.JALTOK_JAD, 7617 -> 130;
            case 1672, 239, 8030, 8031 -> 90;
            case 8609 ->
                //Hydra
                110;
            case 6607 -> 70;
            case 2028 -> 80;
            case 3162 -> 100;
            default -> 85;
        };
    }

    /**
     * Npcs who ignores projectile clipping to ensure no safespots
     *
     * @return true is the npc is using range, mage or special
     */
    public static boolean ignoresProjectile(NPC npc) {
        if (npc == null) return false;
        return switch (npc.getNpcId()) {
            //melee
            //range
            //mage
            //death
            case 6611, 6612, 319, 6618, 6766, 6768, 5862, 963, 965, 7706, 7144, 8028, 7145, 7146, 9021, 9022, 9023, 9024, 5890, 8609, 7566, 7563,
                 7585, 7544, 7554, 8781, Hespori.NPC_ID, TheUnbearable.NPC_ID, FragmentOfSeren.NPC_ID, 8031, 8030 -> true;
            default -> false;
        };
    }

    /**
     * NPC Attacking Player
     */
    public static final int[] ignoreAttack = new int[]{7706, 7413, 7553, 7555, 8921, 7700, 7691, 8026, 8027, 8028};
    private CombatMethod method = null;

    public void attackPlayer(Player c, NPC npc) {
        if (npc != null && npc.getInstance() != c.getInstance()) {
            npc.resetAttack();
            return;
        }

        if (npc != null) {

            if (npc.getCombatMethod() != null && npc.getNpcStats().scripts.combat_ != null) {
                method = npc.getCombatMethod();
            }

            if (npc.lastX != npc.getX() || npc.lastY != npc.getY()) {
                return;
            }

            if (ArrayUtils.contains(ignoreAttack, npc.getNpcId())) return;

            if (c.getInferno() != null && c.getInferno().kill.contains(npc)) {
                return;
            }

            if (c.isInvisible()) {
                return;
            }

            if (c.getBankPin().requiresUnlock()) {
                c.getBankPin().open(2);
                return;
            }

            if (npc.isDead()) return;

            if (Arrays.stream(PestControl.PORTAL_DATA).anyMatch(data -> data[0] == npc.getNpcId())) {
                return;
            }
            if (!npc.getPosition().inMulti() && npc.underAttackBy > 0 && npc.underAttackBy != c.getIndex()) {
                npc.setPlayerAttackingIndex(0);
                npc.facePlayer(0);
                npc.underAttack = false;
                npc.randomWalk = true;
                return;
            }
            if (!npc.getPosition().inMulti() && ((c.underAttackByPlayer > 0 && c.underAttackByNpc != npc.getIndex()) || (c.underAttackByNpc > 0 && c.underAttackByNpc != npc.getIndex()))) {
                npc.setPlayerAttackingIndex(0);
                npc.facePlayer(0);
                npc.underAttack = false;
                npc.randomWalk = true;
                return;
            }
            if (npc.heightLevel != c.heightLevel) {
                npc.setPlayerAttackingIndex(0);
                npc.facePlayer(0);
                npc.underAttack = false;
                npc.randomWalk = true;
                return;
            }
            switch (npc.getNpcId()) {
                case 8062:
                    // vorkath crab when gets close and attacks player insta kills player
                    npc.gfx100(427);
                    c.appendDamage(npc, 150, Hitmark.HIT);
                    npc.setDead(true);
                    break;
                case 1739:
                case 7413:
                case 1740:
                case 1741:
                case 1742:
                case 6600:
                case 7288:
                case 7290:
                case 7292:
                case 7294:
                case 6602:
                    npc.setPlayerAttackingIndex(0);
                    npc.facePlayer(0);
                    npc.underAttack = false;
                    npc.randomWalk = true;
                    break;
                case Npcs.ABYSSAL_SIRE:
                    boolean hasDistance = npc.getDistance(c.getX(), c.getY()) <= ((double) distanceRequired(npc)) + (npc.getSize() > 1 ? 0.5 : 0.0);
                    if (!hasDistance) {
                        npc.setAttackType(CombatType.RANGE);
                    }
                    break;
            }
            if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) ^ Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS)) {
                npc.setPlayerAttackingIndex(0);
                npc.facePlayer(0);
                npc.underAttack = false;
                npc.randomWalk = true;
                return;
            }
            if (Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR) ^ Boundary.isIn(c, Boundary.CORPOREAL_BEAST_LAIR)) {
                npc.setPlayerAttackingIndex(0);
                npc.facePlayer(0);
                npc.underAttack = false;
                npc.randomWalk = true;
                return;
            }
            npc.facePlayer(c.getIndex());
            int distance = c.distanceToPoint(npc.absX, npc.absY);
            boolean hasDistance = npc.getDistance(c.getX(), c.getY()) <= ((double) distanceRequired(npc)) + (npc.getSize() > 1 ? 0.5 : 0.0);
            if (ignoresProjectile(npc)) {
                if (distance < 10) {
                    c.getPA().removeAllWindows();
                    npc.oldIndex = c.getIndex();
                    c.underAttackByNpc = npc.getIndex();
                    c.singleCombatDelay2 = System.currentTimeMillis();
                    npc.attackTimer = getNpcDelay(npc);
                    npc.hitDelayTimer = getHitDelay(npc);
                    LoadSpell.loadSpell(c, npc);
                    startAnimationHighPriority(getAttackEmote(npc), npc);
                }
            }

            if (hasDistance) {
                if (projectileClipping) {
                    if (npc.getAttackType() == null || npc.getAttackType() == CombatType.MAGE || npc.getAttackType() == CombatType.RANGE) {
                        if (!PathChecker.raycast(npc, c, true) && !PathChecker.raycast(c, npc, true)) return;
                    }
                }

                if (c.respawnTimer <= 0) {

                    Optional<AlchemicalHydra> hydraInstance = getHydraInstance(npc);
                    if (hydraInstance.isPresent()) {
                        hydraInstance.get().doAttack();
                        return;
                    }

                    npc.attackTimer = getNpcDelay(npc);

                    if (method instanceof CommonCombatMethod ccm) {
                        if (npc.getCombatMethod() != null) {
                            ccm.set(npc, c);
                            ccm.prepareAttack(npc, c);
                            return;
                        }
                    }


                    if (npc.getNpcId() == Npcs.CERBERUS && Cerberus.cerberusSpecials(npc, c)) {
                        return;
                    }
                    npc.hitDelayTimer = getHitDelay(npc);
                    if (npc.getAttackType() == null) {
                        npc.setAttackType(CombatType.MELEE);
                    }
                    LoadSpell.loadSpell(c, npc);
                    npc.oldIndex = c.getIndex();
                    c.underAttackByNpc = npc.getIndex();
                    c.singleCombatDelay2 = System.currentTimeMillis();
                    startAnimationHighPriority(getAttackEmote(npc), npc);
                    if (c.getOpenInterface() != 39000) {
                        c.getPA().removeAllWindows();
                    }
                    if (npc.getAttackType() == CombatType.DRAGON_FIRE) {
                        npc.hitDelayTimer += 2;
                        c.getCombatItems().absorbDragonfireDamage();
                    }
                    if (multiAttacks(npc)) {
                        startAnimationHighPriority(getAttackEmote(npc), npc);
                        multiAttackGfx(npc);
                        npc.oldIndex = c.getIndex();
                        return;
                    }

                    PlayerHandler.processQueuedActions();

                    if (npc.projectileId > 0) {
                        if (npc.getNpcId() == 7706) {
                            NPC glyph = getNpc(7707, c.getHeight());
                            if (glyph == null) {
                                return;
                            }
                        }
                        ProjectileEntity projectile = new ProjectileEntity(npc, c, npc.projectileId, 50, getProjectileSpeed(npc), getProjectileStartHeight(npc.getNpcId(), npc.projectileId), getProjectileEndHeight(npc.getNpcId(), npc.projectileId), 16, 1, 5, true);
                        projectile.sendProjectile();

                        if (c.teleporting) {
                            c.startAnimation(65535);
                            c.teleporting = false;
                            c.gfx0(-1);
                            c.startAnimation(-1);
                        }

                    }
                } else {
                    if (npc.getNpcId() == InfernoWaveData.JAL_IMKOT && npc.getDistance(c.getX(), c.getY()) > 2 && Misc.random(0, 3) == 0) {
                        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                            int ticks;

                            @Override
                            public void execute(CycleEventContainer container) {
                                if (ticks == 0) {
                                    npc.startAnimation(7600);
                                }
                                if (ticks == 2) {
                                    npc.teleport(c.getX(), c.getY(), c.heightLevel);
                                }
                                if (ticks == 4) {
                                    npc.startAnimation(7601);
                                }
                                ticks++;
                            }
                        }, 1);
                    }
                }
            }
        }
    }

    public int offset(NPC npc) {
        return switch (npc.getNpcId()) {
            case 6611, 6612 -> 3;
            case 6610, 239, 8031, 8030 -> 2;
            case 2265, 2266, 3127, 3125, InfernoWaveData.JALTOK_JAD -> 1;
            default -> 0;
        };
    }

    public boolean retaliates(int npcType) {
        return npcType < 3777 || npcType > 3780;
    }

    public boolean prayerProtectionIgnored(NPC npc) {
        return switch (npc.getNpcId()) {
            case 1610, 1611, 1612, 8028, 3129 -> true;
            case 6611, 6612, 6609 -> npc.getAttackType() == CombatType.MAGE || npc.getAttackType() == CombatType.SPECIAL;
            case 465 -> npc.getAttackType() == CombatType.DRAGON_FIRE;
            default -> false;
        };
    }

    public void handleSpecialEffects(Player c, NPC npc, int damage) {
        if (npc.getNpcId() == 2892 || npc.getNpcId() == 2894) {
            if (damage > 0) {
                if (c != null) {
                    if (c.playerLevel[5] > 0) {
                        c.playerLevel[5]--;
                        c.getPA().refreshSkill(5);
                    }
                }
            }
        }
    }

    public static void startAnimation(int animId, NPC npc) {
        npc.startAnimation(animId);
    }

    public static void startAnimationHighPriority(int animId, NPC npc) {
        npc.startAnimation(new Animation(animId, 0, AnimationPriority.HIGH));
    }

    public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
        return ((objectX - playerX <= distance && objectX - playerX >= -distance) && (objectY - playerY <= distance && objectY - playerY >= -distance));
    }

    public int getMaxHit(Player c, NPC npc) {
        if (npc == null) {
            return 0;
        }
        if (Boundary.isIn(npc, Boundary.XERIC)) {
            return XericWave.getMax(npc.getNpcId());
        }
        if (Arrays.stream(DagannothMother.DAGANNOTH_MOTHER_TRANSFORMS).anyMatch(dagId -> dagId == npc.getNpcId())) {
            if (npc.getAttackType() == CombatType.MELEE) {
                return 9;
            } else {
                return 12;
            }
        }
        if (npc.getNpcId() == FragmentOfSeren.NPC_ID || npc.getNpcId() == TheUnbearable.NPC_ID) {
            if (c.hasFollower && (c.petSummonId == 23939)) {
                if (Misc.random(1) == 1) {
                    return 0;
                }
            }
            if (c.hasFollower && (c.petSummonId == 30123)) {
                if (Misc.random(100) < 85) {
                    return 0;
                }
            }
        }
        switch (npc.getNpcId()) {
            case TheUnbearable.NPC_ID:
                return npc.getAttackType() == CombatType.MELEE ? 55 : 25;
            case 22545:
                return 22;
            case 7706:
                return 120;
            case 3021:
                // KBD Spiders
                return 7;
            case Skotizo.SKOTIZO_ID:
                return 38;
            case Skotizo.AWAKENED_ALTAR_NORTH:
            case Skotizo.AWAKENED_ALTAR_SOUTH:
            case Skotizo.AWAKENED_ALTAR_WEST:
            case Skotizo.AWAKENED_ALTAR_EAST:
                return 15;
            case Skotizo.REANIMATED_DEMON:
            case Skotizo.DARK_ANKOU:
                return 8;
            case 8028:
                int resistance = c.getItems().isWearingItem(1540) || c.getItems().isWearingItem(11283) || c.getItems().isWearingItem(11284) || (npc.getNpcId() == 465 && c.getItems().isWearingItem(2890)) ? 1 : 0;
                if ((System.currentTimeMillis() - c.lastAntifirePotion) < c.antifireDelay) {
                    resistance = resistance + 1;
                }
                if (resistance == 2) {
                    return 30;
                }
                if (resistance == 1) {
                    return 40;
                }
                return 80;
            case 6914:
                // Lizardman, Lizardman brute
            case 6915:
            case 6916:
            case 6917:
                return 7;
            case 7617, 6610, 7144, 7145, 7146, 5867, 5868, 5869:
                return 30;
            case 6918:
            case 6919:
                return 11;
            case 8781:
                return npc.getAttackType() == CombatType.MAGE ? 25 : 55;
            case 9293:
                return 20;
            case 1443:
                return 32;
            case 2042:
            case 2043:
            case 2044:
                return 41;
            case 5862:
                return 23;
            case 499:
                return 21;
            case 498:
                return 12;
            case 273:
                return npc.getAttackType() == CombatType.MELEE ? 20 : 18;
            case 239:
                return npc.getAttackType() == CombatType.DRAGON_FIRE ? 50 : 22;
            case 465:
                return npc.getAttackType() == CombatType.DRAGON_FIRE ? 55 : 13;
            case 8031:
            case 8030:
                return npc.getAttackType() == CombatType.DRAGON_FIRE ? 55 : 38;
            case 2208:
            case 2207:
            case 2206:
                return 16;
            /*Hydra*/
            case 8609:
                return 22;
            case 319:
                return npc.getAttackType() == CombatType.MELEE ? 55 : npc.getAttackType() == CombatType.SPECIAL ? 35 : 49;
            case 320:
                return 10;
            case 3129:
                return npc.getAttackType() == CombatType.MELEE ? 47 : npc.getAttackType() == CombatType.SPECIAL ? 49 : 30;
            case 6611:
            case 6612:
                return npc.getAttackType() == CombatType.MELEE ? 30 : npc.getAttackType() == CombatType.MAGE ? 34 : 46;
            case 1046:
                return npc.getAttackType() == CombatType.MAGE ? 40 : 50;
            case 9021:
                //melee
            case 9022:
                //range
            case 9023:
                //mage
            case 9024:
                //death
                return npc.getAttackType() == CombatType.MELEE ? 30 : 20;
            case 6609:
                return npc.getAttackType() == CombatType.SPECIAL ? 3 : npc.getAttackType() == CombatType.MAGE ? 60 : 40;
            case 6618:
                return npc.getAttackType() == CombatType.SPECIAL ? 23 : 15;
            case 6619:
                return npc.getAttackType() == CombatType.SPECIAL ? 31 : 25;
            case 2558:
                return npc.getAttackType() == CombatType.MAGE ? 38 : 68;
            case 2562:
                return 31;
            case 2215:
                return npc.getAttackType() == CombatType.MELEE ? 55 : 31;
            case 3162:
                return npc.getAttackType() == CombatType.RANGE ? 71 : npc.getAttackType() == CombatType.MAGE ? 21 : 15;
            case 963:
                return npc.getAttackType() == CombatType.MAGE ? 30 : 21;
            case 965:
                return npc.getAttackType() == CombatType.MAGE || npc.getAttackType() == CombatType.RANGE ? 30 : 21;
        }
        return npc.maxHit == 0 ? 1 : npc.maxHit;
    }

    private final ArrayList<int[]> vetionSpellCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> archSpellCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> hydraPoisonCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> fanaticSpellCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> corpSpellCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> olmSpellCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> explosiveSpawnCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> cerberusGroundCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> DragonGroundCoordinates = new ArrayList<>(3);
    private final ArrayList<int[]> vasaRockCoordinates = new ArrayList<>(1);

    public void groundSpell(NPC npc, Player player, int startGfx, int endGfx, String coords, int time) {
        if (player == null) {
            return;
        }
        switch (coords) {
            case "hydra":
                player.coordinates = hydraPoisonCoordinates;
                break;
            case "vasa":
                player.coordinates = vasaRockCoordinates;
                break;
            case "vetion":
                player.coordinates = vetionSpellCoordinates;
                break;
            case "Dragon":
                player.coordinates = DragonGroundCoordinates;
                break;
            case "archaeologist":
                player.coordinates = archSpellCoordinates;
                break;
            case "fanatic":
                player.coordinates = fanaticSpellCoordinates;
                break;
            case "corp":
                player.coordinates = corpSpellCoordinates;
                break;
            case "olm":
                player.coordinates = olmSpellCoordinates;
                break;
            case "spawns":
                player.coordinates = explosiveSpawnCoordinates;
                List<NPC> exploader = Arrays.asList(npcs);
                if (exploader.stream().filter(Objects::nonNull).anyMatch(n -> n.getNpcId() == 6768 && !n.isDead())) {
                    return;
                }
                break;
            case "cerberus":
                player.coordinates = cerberusGroundCoordinates;
                break;
        }
        int x = player.getX();
        int y = player.getY();
        player.coordinates.add(new int[]{x, y});
        for (int i = 0; i < 2; i++) {
            player.coordinates.add(new int[]{(x - 1) + Misc.random(3), (y - 1) + Misc.random(3)});
        }
        for (int[] point : player.coordinates) {
            int nX = npc.absX + 2;
            int nY = npc.absY + 2;
            int x1 = point[0] + 1;
            int y1 = point[1] + 2;
            int offY = (nX - x1) * -1;
            int offX = (nY - y1) * -1;
            if (startGfx > 0) {
                player.getPA().createPlayersProjectile(nX, nY, offX, offY, 40, getProjectileSpeed(npc), startGfx, 31, 0, -1, 5);
            }
            if (Objects.equals(coords, "spawns")) {
                NPCSpawning.spawnNpc(6768, point[0], point[1], 0, 0, -1);
            }
        }
        if (Objects.equals(coords, "spawns")) {
            CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    kill(6768, 0);
                    container.stop();
                }
            }, 7);
        }
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for (int[] point : player.coordinates) {
                    int x2 = point[0];
                    int y2 = point[1];
                    if (endGfx > 0) {
                        player.getPA().createPlayersStillGfx(endGfx, x2, y2, player.heightLevel, 5);
                    }
                    if (Objects.equals(coords, "cerberus")) {
                        player.getPA().createPlayersStillGfx(1247, x2, y2, player.heightLevel, 5);
                    }
                }
                player.coordinates.clear();
                container.stop();
            }
        }, time);
    }

    public ArrayList<int[]> gorillaBoulder = new ArrayList<>(1);

    public void groundAttack(NPC npc, Player player, int startGfx, int endGfx, int explosionGfx, int time) {
        if (player == null) {
            return;
        }
        player.totalMissedGorillaHits = 3;
        player.coordinates = gorillaBoulder;
        int x = player.getX();
        int y = player.getY();
        player.coordinates.add(new int[]{x, y});
        for (int[] point : player.coordinates) {
            int nX = npc.absX + 2;
            int nY = npc.absY + 2;
            int x1 = point[0] + 1;
            int y1 = point[1] + 2;
            int offY = (nX - x1) * -1;
            int offX = (nY - y1) * -1;
            if (startGfx > 0)
                player.getPA().createPlayersProjectile(nX, nY, offX, offY, 40, getProjectileSpeed(npc), startGfx, 31, 0, -1, 5); // 304
        }
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for (int[] point : player.coordinates) {
                    int x2 = point[0];
                    int y2 = point[1];
                    if (endGfx > 0) player.getPA().createPlayersStillGfx(endGfx, x2, y2, player.heightLevel, 5); // 303
                }
                container.stop();
            }
        }, 3);
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for (int[] point : player.coordinates) {
                    int x2 = point[0];
                    int y2 = point[1];
                    if (explosionGfx > 0)
                        player.getPA().createPlayersStillGfx(explosionGfx, x2, y2, player.heightLevel, 5); // 305
                }
                npc.setAttackType(CombatType.getRandom(CombatType.MELEE, CombatType.RANGE, CombatType.MAGE));
                player.coordinates.clear();
                container.stop();
            }
        }, time);
    }

    public static void kill(int minHeight, int maxHeight, int... npcType) {
        nonNullStream().filter(n -> IntStream.of(npcType).anyMatch(type -> type == n.getNpcId()) && n.getHeight() >= minHeight && n.getHeight() <= maxHeight).forEach(npc -> npc.setDead(true));
    }

    public static void despawn(int npcType, int height) {
        List<NPC> npcs = Arrays.stream(NPCHandler.npcs).filter(Objects::nonNull).filter(n -> n.getNpcId() == npcType && n.heightLevel == height).toList();
        npcs.forEach(NPC::unregister);
    }

    public static void kill(int npcType, int height) {
        Arrays.stream(npcs).filter(Objects::nonNull).filter(n -> n.getNpcId() == npcType && n.heightLevel == height).forEach(npc -> npc.setDead(true));
    }

    private void createVetionEarthquake(Player player) {
        player.getPA().shakeScreen(3, 2, 3, 2);
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                player.getPA().resetScreenShake();
                container.stop();
            }
        }, 4);
    }

    public static Stream<NPC> nonNullStream() {
        return Stream.of(npcs).filter(Objects::nonNull);
    }

    /**
     * Handles transforming npcs when clicked on to attack
     *
     * @author Grant_ | www.rune-server.ee/members/grant_ | 10/18/19
     */
    public static boolean transformOnAttack(NPC npc) {
        if (npc == null) {
            return true;
        }
        switch (npc.getNpcId()) {
            case FragmentOfSeren.FRAGMENT_ID:
                npc.requestTransform(FragmentOfSeren.NPC_ID);
                npc.startAnimation(FragmentOfSeren.TOUCHED_ANIMATED);
                FragmentOfSeren.isAttackable = false;
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        FragmentOfSeren.isAttackable = true;
                        npc.forceChat("How dare you awaken me!");
                        npc.startAnimation(65535);
                        container.stop();
                    }
                }, 13);
                //npc.gfx100(1582);
                return true;
            case 8610:
                npc.attackTimer = 3;
                npc.requestTransform(8611);
                npc.startAnimation(8268);
                npc.gfx100(1582);
                return true;
            default:
                return false;
        }
    }
}
