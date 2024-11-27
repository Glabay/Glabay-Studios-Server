package io.xeros.content.skills.agility;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.skills.agility.impl.BarbarianAgility;
import io.xeros.content.skills.agility.impl.GnomeAgility;
import io.xeros.content.skills.agility.impl.Lighthouse;
import io.xeros.content.skills.agility.impl.Shortcuts;
import io.xeros.content.skills.agility.impl.WildernessAgility;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.PlayerMovementStateBuilder;
import io.xeros.util.Misc;

/**
 * AgilityHandler
 *
 * @author Andrew (I'm A Boss on Rune-Server and Mr Extremez on Mopar & Runelocus)
 */

public class AgilityHandler {

    public static final List<Integer> graceful_ids =
            Arrays.asList(
                    11850, 11852, 11854, 11856, 11858, 11860,
                    13579, 13581, 13583, 13585, 13587, 13589,
                    13591, 13593, 13595, 13597, 13599, 13601,
                    13603, 13605, 13607, 13609, 13611, 13613,
                    13615, 13617, 13619, 13621, 13623, 13625,
                    13627, 13629, 13631, 13633, 13635, 13637,
                    13667, 13669, 13671, 13673, 13675, 13677,
                    21061, 21064, 21067, 21070, 21073, 21076);

    public boolean[] agilityProgress = new boolean[12];

    public static final int LOG_EMOTE = 762,
            PIPES_EMOTE = 844,
            CLIMB_UP_EMOTE = 828,
            CLIMB_DOWN_EMOTE = 827,
            CLIMB_UP_MONKEY_EMOTE = 3487,
            WALL_EMOTE = 840,
            JUMP_EMOTE = 3067,
            FAIL_EMOTE = 770,
            CRAWL_EMOTE = 844;

    public int jumping, jumpingTimer, agilityTimer = -1, moveHeight = -1, tropicalTreeUpdate = -1, zipLine = -1;

    private int moveX, moveY, moveH;

    public void resetAgilityProgress() {
        Arrays.fill(agilityProgress, false);
    }

    /**
     * sets a specific emote to walk to point x
     */
    public void walkToEmote(Player c, int id) {
        c.playerWalkIndex = id;
        c.getPA().requestUpdates();
    }

    /**
     * resets the player animation
     */
    public void stopEmote(Player c) {
        c.getPA().requestUpdates();
    }

    public void move(Player c, int EndX, int EndY, int Emote, int endingAnimation) {
        if (c.getItems().isWearingItem(4084)) {
            c.sendMessage("It would seem to be dangerous doing this.. on a sled.. right?");
            return;
        }
        c.setMovementState(new PlayerMovementStateBuilder().setAllowClickToMove(false).setRunningEnabled(false).createPlayerMovementState());
        walkToEmote(c, Emote);
        c.getPA().walkTo2(EndX, EndY);
        destinationReached(c, EndX, EndY, endingAnimation);
    }

    private void resetOnDestinationReached(Player c) {
        c.setMovementState(null);
    }

    /**
     * when a player reaches he's point the stopEmote() method gets called this method calculates when the player reached he's point
     */
    public void destinationReached(final Player c, int x2, int y2, final int endingEmote) {
        if (x2 >= 0 && y2 >= 0 && x2 != y2) {
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (c.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    if (moveHeight >= 0) {
                        c.getPA().movePlayer(c.getX(), c.getY(), moveHeight);
                        moveHeight = -1;
                    }
                    stopEmote(c);
                    c.startAnimation(endingEmote);
                    container.stop();
                }

                @Override
                public void onStopped() {
                    if (c != null && !c.isDisconnected()) {
                        resetOnDestinationReached(c);
                        if (c.playerEquipment[Player.playerWeapon] == -1) {
                            c.playerStandIndex = 0x328;
                            c.playerTurnIndex = 0x337;
                            c.playerWalkIndex = 0x333;
                            c.playerTurn180Index = 0x334;
                            c.playerTurn90CWIndex = 0x335;
                            c.playerTurn90CCWIndex = 0x336;
                            c.playerRunIndex = 0x338;
                        } else {
                            MeleeData.setWeaponAnimations(c);
                        }
                    }
                }
            }, x2 + y2);
        } else if (x2 == y2) {
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (c.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    if (moveHeight >= 0) {
                        c.getPA().movePlayer(c.getX(), c.getY(), moveHeight);
                        moveHeight = -1;
                    }
                    stopEmote(c);
                    c.startAnimation(endingEmote);
                    container.stop();
                }

                @Override
                public void onStopped() {
                    if (c != null && !c.isDisconnected()) {
                        resetOnDestinationReached(c);
                        if (c.playerEquipment[Player.playerWeapon] == -1) {
                            c.playerStandIndex = 0x328;
                            c.playerTurnIndex = 0x337;
                            c.playerWalkIndex = 0x333;
                            c.playerTurn180Index = 0x334;
                            c.playerTurn90CWIndex = 0x335;
                            c.playerTurn90CCWIndex = 0x336;
                            c.playerRunIndex = 0x338;
                        } else {
                            MeleeData.setWeaponAnimations(c);
                        }
                    }
                }
            }, x2);
        } else if (x2 < 0) {
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (c.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    if (moveHeight >= 0) {
                        c.getPA().movePlayer(c.getX(), c.getY(), moveHeight);
                        moveHeight = -1;

                    }
                    stopEmote(c);
                    c.startAnimation(endingEmote);
                    container.stop();
                }

                @Override
                public void onStopped() {
                    if (c != null && !c.isDisconnected()) {
                        resetOnDestinationReached(c);
                        if (c.playerEquipment[Player.playerWeapon] == -1) {
                            c.playerStandIndex = 0x328;
                            c.playerTurnIndex = 0x337;
                            c.playerWalkIndex = 0x333;
                            c.playerTurn180Index = 0x334;
                            c.playerTurn90CWIndex = 0x335;
                            c.playerTurn90CCWIndex = 0x336;
                            c.playerRunIndex = 0x338;
                        } else {
                            MeleeData.setWeaponAnimations(c);
                        }
                    }
                }
            }, -x2 + y2);
        } else if (y2 < 0) {
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (c.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    if (moveHeight >= 0) {
                        c.getPA().movePlayer(c.getX(), c.getY(), moveHeight);
                        moveHeight = -1;

                    }
                    stopEmote(c);
                    c.startAnimation(endingEmote);
                    container.stop();
                }

                @Override
                public void onStopped() {
                    if (c != null && !c.isDisconnected()) {
                        resetOnDestinationReached(c);
                        if (c.playerEquipment[Player.playerWeapon] == -1) {
                            c.playerStandIndex = 0x328;
                            c.playerTurnIndex = 0x337;
                            c.playerWalkIndex = 0x333;
                            c.playerTurn180Index = 0x334;
                            c.playerTurn90CWIndex = 0x335;
                            c.playerTurn90CCWIndex = 0x336;
                            c.playerRunIndex = 0x338;
                        } else {
                            MeleeData.setWeaponAnimations(c);
                        }
                    }
                }
            }, x2 - y2);
        }
    }

    /**
     * @param objectId : the objectId to know how much exp a player receives
     */

    public double getXp(int objectId) {
        switch (objectId) {
            case GnomeAgility.TREE_OBJECT:
            case GnomeAgility.TREE_BRANCH_OBJECT:
                return 0;
            case GnomeAgility.LOG_OBJECT:
            case GnomeAgility.PIPES1_OBJECT:
            case GnomeAgility.PIPES2_OBJECT:
            case GnomeAgility.NET2_OBJECT:
            case GnomeAgility.NET1_OBJECT:
            case GnomeAgility.ROPE_OBJECT:
                return 0;

            case BarbarianAgility.BARBARIAN_SWING_ROPE_OBJECT:
            case BarbarianAgility.BARBARIAN_LOG_BALANCE_OBJECT:
            case BarbarianAgility.BARBARIAN_NET_OBJECT:
            case BarbarianAgility.BARBARIAN_LEDGE_OBJECT:
            case BarbarianAgility.BARBARIAN_LADDER_OBJECT:
            case BarbarianAgility.BARBARIAN_WALL_OBJECT:
                return 0;

            case WildernessAgility.WILDERNESS_PIPE_OBJECT:
                return 0;
            case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
            case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT:
            case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT:
                return 0;
            case WildernessAgility.WILDERNESS_ROCKS_OBJECT:
                return 0;
        }
        return -1;
    }

    /**
     * @param objectId : the objectId to fit with the right agility level required
     */

    private int getLevelRequired(int objectId) {
        switch (objectId) {

            case WildernessAgility.WILDERNESS_PIPE_OBJECT:
            case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
            case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT:
            case WildernessAgility.WILDERNESS_ROCKS_OBJECT:
            case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT:
                return 52;

            case Lighthouse.BASALT_ROCK:
                return 40;

            case BarbarianAgility.BARBARIAN_SWING_ROPE_OBJECT:
            case BarbarianAgility.BARBARIAN_LOG_BALANCE_OBJECT:
            case BarbarianAgility.BARBARIAN_NET_OBJECT:
            case BarbarianAgility.BARBARIAN_LEDGE_OBJECT:
            case BarbarianAgility.BARBARIAN_LADDER_OBJECT:
            case BarbarianAgility.BARBARIAN_WALL_OBJECT:
                return 35;

		/*case RooftopSeers.WALL:
			return 60;

		case RooftopVarrock.ROUGH_WALL:
			return 30;
			
		case RooftopArdougne.WOODEN_BEAMS:
			return 90;*/

            case Shortcuts.SLAYER_TOWER_CHAIN_UP:
                return 61;

            case Shortcuts.RELLEKKA_STRANGE_FLOOR:
                return 81;

            case Shortcuts.RELLEKKA_CREVICE:
                return 62;

            case Shortcuts.STEPPING_STONE:
                return 90;
        }
        return -1;
    }

    /**
     * @param objectId : the objectId to fit with the right animation played
     */

    public int getAnimation(int objectId) {
        switch (objectId) {
            case GnomeAgility.LOG_OBJECT:
            case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT:
            case BarbarianAgility.BARBARIAN_LOG_BALANCE_OBJECT:
            case GnomeAgility.ROPE_OBJECT:
            case 2332:
                return LOG_EMOTE;
            case 154:
            case 4084:
            case 9330:
            case 9228:
            case 5100:
            case WildernessAgility.WILDERNESS_PIPE_OBJECT:
                return PIPES_EMOTE;
            case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
            case BarbarianAgility.BARBARIAN_SWING_ROPE_OBJECT:
                return 3067;
            case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT:
                return 1604; // 2588
            case WildernessAgility.WILDERNESS_ROCKS_OBJECT:
                return 1148;
            case BarbarianAgility.BARBARIAN_LEDGE_OBJECT:
                return 756;
            case BarbarianAgility.BARBARIAN_WALL_OBJECT:
                return 839;
        }
        return -1;
    }

    public static void delayFade(final Player c, String emote, final int moveX, final int moveY, final int moveH, String message, String endMessage, int time) {
        delayFade(c, emote, moveX, moveY, moveH, message, endMessage, time, null);
    }

    public static void delayFade(final Player c, String emote, final int moveX, final int moveY, final int moveH, String message,
                                 String endMessage, int time, Consumer<Player> finishConsumer) {
        if (emote == "CLIMB_DOWN")
            c.startAnimation(CLIMB_DOWN_EMOTE);
        if (emote == "CLIMB_UP")
            c.startAnimation(CLIMB_UP_EMOTE);
        if (emote == "JUMP")
            c.startAnimation(JUMP_EMOTE);
        if (emote == "FAIL")
            c.startAnimation(FAIL_EMOTE);
        if (emote == "CRAWL")
            c.startAnimation(CRAWL_EMOTE);
        if (emote == "NONE") {

        }

        c.getPA().sendScreenFade(message, 1, time);
        c.sendMessage(message);
        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (c.isDisconnected()) {
                    onStopped();
                    return;
                }
                c.getPA().movePlayer(moveX, moveY, moveH);
                c.sendMessage("..." + endMessage);
                container.stop();
                if (finishConsumer != null) {
                    finishConsumer.accept(c);
                }
            }

            @Override
            public void onStopped() {
            }
        }, time + 2);
    }

    public static boolean failObstacle(final Player c, int x, int y, int z) {
        c.lastObstacleFail = System.currentTimeMillis();
        int chance = 25 + c.playerLevel[Player.playerAgility] / 13;
        if (Misc.random(chance) == 1) {
            delayEmote(c, "FAIL", x, y, z, 2);
            c.appendDamage(null, 5, Hitmark.HIT);
            c.sendMessage("You slipped and hurt yourself.");
            c.getAgilityHandler().resetAgilityProgress();
            return true;
        }
        return false;
    }

    /**
     * climbDown a ladder or anything. small delay before getting teleported to destination
     */

    public static void delayEmote(final Player c, String emote, final int moveX, final int moveY, final int moveH, int time) {
        switch (emote) {
            case "CLIMB_DOWN":
                c.startAnimation(CLIMB_DOWN_EMOTE);
                break;
            case "CLIMB_UP":
                c.startAnimation(CLIMB_UP_EMOTE);
                break;
            case "FAIL":
                c.startAnimation(FAIL_EMOTE);
                break;
            case "JUMP":
                c.startAnimation(JUMP_EMOTE);
                break;
            case "JUMP_GRAB":
                c.startAnimation(5039);
                break;
            case "BALANCE":
                c.startAnimation(756);
                break;
            case "HANG":
                c.startAnimation(3060);
                break;
            case "JUMP_DOWN":
                c.startAnimation(2586);
                break;
            case "CLIMB_UP_WALL":
                c.getPA().movePlayer(2729, 3491, 3);
                break;
            case "HANG_ON_POST":
                c.getPA().movePlayer(2729, 3491, 3);
                break;
            case "CRAWL":
                c.startAnimation(CRAWL_EMOTE);
                break;
        }

        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (c.isDisconnected()) {
                    onStopped();
                    return;
                }
                c.getPA().movePlayer(moveX, moveY, moveH);
                c.getAgilityHandler().stopEmote(c);
                c.startAnimation(-1);
                container.stop();
            }

            @Override
            public void onStopped() {
                if (c != null && !c.isDisconnected()) {
                    if (c.playerEquipment[Player.playerWeapon] == -1) {
                        c.playerStandIndex = 0x328;
                        c.playerTurnIndex = 0x337;
                        c.playerWalkIndex = 0x333;
                        c.playerTurn180Index = 0x334;
                        c.playerTurn90CWIndex = 0x335;
                        c.playerTurn90CCWIndex = 0x336;
                        c.playerRunIndex = 0x338;
                    } else {
                        MeleeData.setWeaponAnimations(c);
                    }
                }
            }
        }, time);
    }

    /**
     * climbDown a ladder or anything. small delay before getting teleported to destination
     */

    public static void delayEmote(final Player c, int emote, final int moveX, final int moveY, final int moveH, int time) {
        c.startAnimation(emote);
        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (c.isDisconnected()) {
                    onStopped();
                    return;
                }
                c.getPA().movePlayer(moveX, moveY, moveH);
                c.getAgilityHandler().stopEmote(c);
                c.startAnimation(-1);
                container.stop();
            }

            @Override
            public void onStopped() {
                if (c != null && !c.isDisconnected()) {
                    if (c.playerEquipment[Player.playerWeapon] == -1) {
                        c.playerStandIndex = 0x328;
                        c.playerTurnIndex = 0x337;
                        c.playerWalkIndex = 0x333;
                        c.playerTurn180Index = 0x334;
                        c.playerTurn90CWIndex = 0x335;
                        c.playerTurn90CCWIndex = 0x336;
                        c.playerRunIndex = 0x338;
                    } else {
                        MeleeData.setWeaponAnimations(c);
                    }
                }
            }
        }, time);
    }

    /**
     * a specific position the player has to stand on before the action is set to true
     */

    public boolean hotSpot(Player c, int hotX, int hotY) {
        return c.getX() == hotX && c.getY() == hotY;
    }

    public void lapProgress(Player c, int progress, int obj) {
        if (agilityProgress[progress]) {
            double exp = getXp(obj) * 5;
            c.getPlayerAssistant().addSkillXP((int) exp, 16, true);
        }
    }

    public boolean isUsingFullGraceful(Player player) {
        int count = 0;
        for (int i : graceful_ids) {
            if (player.getItems().isWearingItem(i)) {
                count++;
            }
        }
        return count >= 6;
    }

    public int getGracefulEquipmentCount(Player player) {
        int count = 0;
        for (int i : graceful_ids) {
            if (player.getItems().isWearingItem(i)) {
                count++;
            }
        }
        return count;
    }

    public int assignXP(Player player, int base) {
        int gracefulPiecesWearing = getGracefulEquipmentCount(player);

        /**
         * Capped at 6 (should never be > 6)
         */
        if (gracefulPiecesWearing > 5)
            gracefulPiecesWearing = 6;
        /**
         * When not wearing any
         */
        if (gracefulPiecesWearing == 0)
            return base;

        /**
         * Base for multiplication (2.5% extra per piece)
         */
        double multiplier = 1 + ((2.5 * gracefulPiecesWearing) / 100);

        /**
         * Ending multiplier
         */
        return base *= multiplier;
    }

    public void lapFinished(Player c, int progress, int experience, int petChance) {
        if (agilityProgress[progress]) {
            resetAgilityProgress();
            experience = assignXP(c, experience);
            c.sendMessage("You received some XP for completing the track!");
            c.getPlayerAssistant().addSkillXPMultiplied(experience, 16, true);
            Achievements.increase(c, AchievementType.AGIL, 1);
            if (Misc.random(petChance) == 20 && c.getItems().getItemCount(20659, false) == 0 && c.petSummonId != 20659) {
                PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + c.getDisplayName() + "</col> is apperantly agile like a <col=CC0000>Squirrel</col> pet!");
                c.getItems().addItemUnderAnyCircumstance(20659, 1);
                c.getCollectionLog().handleDrop(c, 5, 20659, 1);
            }
        } else {
            c.sendMessage("You must complete the full course to gain experience.");
            return;
        }
    }

    public void roofTopFinished(Player c, int progress, int experience, int petChance) {
        if (agilityProgress[progress]) {
            resetAgilityProgress();
            c.sendMessage("You received some XP for completing the track!");
            experience = assignXP(c, experience);
            c.getPlayerAssistant().addSkillXPMultiplied(experience, 16, true);
            Achievements.increase(c, AchievementType.ROOFTOP, 1);
            if (Misc.random(petChance) == 20 && c.getItems().getItemCount(20659, false) == 0 && c.petSummonId != 20659) {
                PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + c.getDisplayName() + "</col> is apperantly agile like a <col=CC0000>Squirrel</col> pet!");
                c.getItems().addItemUnderAnyCircumstance(20659, 1);
                c.getCollectionLog().handleDrop(c, 5, 20659, 1);
            }
        } else {
            c.sendMessage("You must complete the full course to gain experience.");
            return;
        }
    }

    /**
     * 600 ms process for some agility actions
     */

    public void agilityProcess(Player c) {
        if (jumping > 0 && jumpingTimer == 0) {
            move(c, -1, 0, getAnimation(WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT), -1);
            jumping--;
            jumpingTimer = 2;
        }

        if (jumpingTimer > 0) {
            jumpingTimer--;
        }

//		if (hotSpot(c, 3190, 3414) || hotSpot(c, 3190, 3413) || hotSpot(c, 3190, 3412) || hotSpot(c, 3190, 3411)) {
//			move(c, 0, -1, 1122, -1);
//		}

//		if (hotSpot(c, 3190, 3410)) {
//			c.getPA().movePlayer(3190, 3409, 1);
//		}
//
//		if (hotSpot(c, 3190, 3410) || hotSpot(c, 3190, 3409) || hotSpot(c, 3190, 3408) || hotSpot(c, 3190, 3407) || hotSpot(c, 3190, 3406)) {
//			move(c, 0, -1, 756, -1);
//		}

//		if (hotSpot(c, 3190, 3405)) {
//			delayEmote(c, "JUMP", 3192, 3405, 3, 2);
//		}

        if (hotSpot(c, 3215, 3399)) {
            delayEmote(c, "JUMP", 3218, 3399, 3, 2);
        }

        if (hotSpot(c, 3253, 3180)) {
            delayEmote(c, "JUMP", 3259, 3179, 0, 2);
        }

        if (agilityTimer > 0) {
            agilityTimer--;
        }

        if (agilityTimer == 0) {
            c.getPA().movePlayer(moveX, moveY, moveH);
            moveX = -1;
            moveY = -1;
            moveH = 0;
            agilityTimer = -1;
        }

    }

    public boolean checkLevel(Player c, int objectId) {
        if (getLevelRequired(objectId) > c.playerLevel[Player.playerAgility]) {
            c.sendMessage("You need an agility level of at least " + getLevelRequired(objectId) + " to do this.");
            return true;
        }
        return false;
    }
}
