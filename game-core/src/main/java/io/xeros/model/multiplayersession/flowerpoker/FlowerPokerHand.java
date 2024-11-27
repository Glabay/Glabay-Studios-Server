package io.xeros.model.multiplayersession.flowerpoker;

import com.google.common.collect.Lists;
import io.xeros.Server;

import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.model.cycleevent.*;
import io.xeros.model.entity.player.*;
import io.xeros.model.items.GameItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.FlowerpokerResultLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Ynneh
 */
public class FlowerPokerHand {

    private static final Logger logger = LoggerFactory.getLogger(FlowerPokerHand.class);
    public List<GameItem> prizePool = Lists.newArrayList();
    /**
     * TODO list
     * - Make it so if you're in the flower poker area you can only see people in the area chats public
     * - Fix bug with clicking seeds IF not using AUTOMATIC
     * - DEBUG/Look for other bugs
     */

    private Player player;

    public Player other;

    public int lastPlantTicks;

    public int currentPlants;

    public List<GlobalObject> spawnedFlowers = Lists.newArrayList();

    public FlowerPokerHand(Player player) {
        this.player = player;
    }

    public List<FlowerData> dealtHand = Lists.newArrayList();

    public FlowerHand currentHand = FlowerHand.BUST;

    public String currentForceText = "";

    private Object FLOWER_OBJECT = new Object();

    public Position assignedStart = null;

    private static Set<Integer> lanes = new HashSet<>();

    public static List<Position> positions = Arrays.asList(
            new Position(3110, 3510, 0),
            new Position(3113, 3510, 0),
            new Position(3116, 3510, 0),
            new Position(3119, 3510, 0));

    public static boolean canGamble() {
        return lanes.size() <= 3;
    }

    public void assignLane(Player player) {
        player.interruptActions();
        other.interruptActions();
        player.unfollow();
        other.unfollow();
        player.lastManualSeedPlant = 5000;
        other.lastManualSeedPlant = 5000;
        player.setRunningToggled(true);
        other.setRunningToggled(true);
        /**
         * Index of starting position
         */
        Position lanePosition = null;

        for (int i = 0; i < 4; i++) {
            if (!lanes.contains(i)) {
                lanes.add(i);
                setLane(player, i);
                setLane(other, i);
                lanePosition = positions.get(i);
                logger.debug("Assigned lane {}", i);
                break;
            }
        }

        if (lanePosition == null) {
            logger.error("No lane found for flower poker, player={}", player.getStateDescription());
            player.sendMessage("@red@Error, couldn't find lane - contact support.");
            return;
        }

        assignedStart = lanePosition;
        other.getFlowerPoker().assignedStart = lanePosition;

        /**
         * Host
         */
        pathToLane(false);
    }

    public FlowerPokerHand setOther(Player other) {
        this.other = other;
        return this;
    }

    public FlowerPokerHand beginSession() {
        assignLane(player);
        return this;
    }

    public FlowerPokerHand setPrizePool(List<GameItem> prizePool) {
        this.prizePool = prizePool;
        return this;
    }

    public boolean isAtDestination() {
        Position dest = assignedStart;
        return player.getPosition().equals(new Position(dest.getX(), dest.getY())) && other.getPosition().equals(new Position(dest.getX() + 1, dest.getY()));
    }

    public void pathToLane(boolean tieReplant) {
        player.lock(new FlowerPokerLock());
        other.lock(new FlowerPokerLock());
        player.lastManualSeedPlant = System.currentTimeMillis() + 5000;
        other.lastManualSeedPlant = System.currentTimeMillis() + 5000;
        Position dest = assignedStart;
        CycleEventHandler.getSingleton().addEvent(FLOWER_OBJECT, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer exe) {
                PathFinder.getPathFinder().findRoute(player, dest.getX(), dest.getY(), true, 1, 1, true);
                PathFinder.getPathFinder().findRoute(other, dest.getX() + 1, dest.getY(), true, 1, 1, true);

                if (isAtDestination()) {
                    currentForceText = "";
                    other.getFlowerPoker().currentForceText = "";
                    exe.stop();
                    if (!tieReplant) {
                        player.lastManualSeedPlant = 0;
                        other.lastManualSeedPlant = 0;
                    }

                    handleRoll();
                    return;
                }
            }
        }, 1);
    }

    public static boolean isLane(int destX, int destY) {
        /**
         * Check for IF destination is IN the lane.
         */
        List<Boundary> bound = Lists.newArrayList();
        for (Position pos : positions) {
            if (pos != null) {
                bound.add(new Boundary(pos.getX(), pos.getY(), pos.getX() + 1, pos.getY() + 5));
            }
        }
        return bound.stream().anyMatch(b -> destX >= b.getMinimumX() && destX <= b.getMaximumX() && destY >= b.getMinimumY() && destY <= b.getMaximumY());
    }

    public boolean finishedPlanting() {
        return currentPlants >= 5 && other.getFlowerPoker().currentPlants >= 5;
    }

    public void handleRoll() {
        updateFaceDirection();
        CycleEventHandler.getSingleton().addEvent(FLOWER_OBJECT, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer exe) {

                if (finishedPlanting()) {
                    exe.stop();
                    return;
                }
                /**
                 * Ticks for afk planting
                 */
                lastPlantTicks++;

                other.getFlowerPoker().lastPlantTicks++;

                /**
                 * Force Planting
                 */

                int forcePlantTicks = Server.isDebug() ? 3 : 15;

                if (lastPlantTicks >= forcePlantTicks) {
                    player.lastManualSeedPlant = System.currentTimeMillis() + 1000;
                    plantSeed(player, false, true);
                }

                if (other.getFlowerPoker().lastPlantTicks >= forcePlantTicks) {
                    other.lastManualSeedPlant = System.currentTimeMillis() + 1000;
                    plantSeed(other, false, true);
                }
                /**
                 * Force talking
                 */
                if (currentForceText.length() > 5 && !currentForceText.isEmpty()) {
                    player.forcedChat(currentForceText);
                }
                if (other.getFlowerPoker().currentForceText.length() > 5 & !other.getFlowerPoker().currentForceText.isEmpty()) {
                    other.forcedChat(other.getFlowerPoker().currentForceText);
                }

                if (exe.getTotalExecutions() == 250) {
                    exe.stop();
                    return;
                }
            }
        }, 1);
    }

    private void updateFaceDirection() {
        player.getPA().resetFollow();
        other.getPA().resetFollow();
        player.facePosition(new Position(player.getX(), player.getY() - 1));
        other.facePosition(new Position(other.getX(), other.getY() - 1));
    }

    public void removeLaneFlowers(boolean removeLane) {
        spawnedFlowers.stream().forEach(s -> Server.getGlobalObjects().remove(s));
        other.getFlowerPoker().spawnedFlowers.stream().forEach(f -> Server.getGlobalObjects().remove(f));
        spawnedFlowers = Lists.newArrayList();
        other.getFlowerPoker().spawnedFlowers = Lists.newArrayList();
        if (removeLane) {
            currentForceText = "";
            other.getFlowerPoker().currentForceText = "";

            int lane1 = getLane(player);
            int lane2 = getLane(other);
            setLane(player, -1);
            setLane(other, -1);
            lanes.remove(lane1);
            lanes.remove(lane2);

            logger.debug("Removed lane a={} b={}", lane1, lane2);

            other.getFlowerPoker().other = null;
            other.flowerPokerHand = null;
            other = null;
            player.flowerPokerHand = null;

        }
    }

    public boolean isAutoReplant(FlowerHand hand) {
        /** White/Black Flower **/
        return hand.ordinal() >= 7 && hand.ordinal() <= 8;
    }

    private void displayWinner() {
        player.setRunningToggled(true);
        other.setRunningToggled(true);
        FlowerHand hostHand = currentHand;
        int hostHandOrdinal = hostHand.ordinal();

        FlowerHand otherHand = other.getFlowerPoker().currentHand;
        int otherHandOrdinal = otherHand.ordinal();

        String handToString = currentHand.name().replaceAll("_", " ");
        String otherHandToString = other.getFlowerPoker().currentHand.name().replaceAll("_", " ");

        logger.debug("B/W="+isAutoReplant(otherHand)+" - B/WP="+isAutoReplant(player.getFlowerPoker().currentHand));

        // Replant check
        if (otherHandOrdinal == hostHandOrdinal || isAutoReplant(other.getFlowerPoker().currentHand) || isAutoReplant(player.getFlowerPoker().currentHand)) {
            player.lastManualSeedPlant = System.currentTimeMillis() + 7_000;
            other.lastManualSeedPlant = System.currentTimeMillis() + 7_000;

            CycleEventHandler.getSingleton().addEvent(FLOWER_OBJECT, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer exe) {
                    if (exe.getTotalExecutions() == 2) {
                        other.forcedChat(handToString + " - A TIE - REPLANT!");
                        player.forcedChat(handToString + " - A TIE - REPLANT!");
                    }

                    if (exe.getTotalExecutions() == 5) {
                        other.getFlowerPoker().lastPlantTicks = 0;
                        other.getFlowerPoker().dealtHand = Lists.newArrayList();
                        removeLaneFlowers(false);
                        lastPlantTicks = 0;
                        dealtHand = Lists.newArrayList();
                        other.getFlowerPoker().currentHand = FlowerHand.BUST;
                        currentHand = FlowerHand.BUST;
                        currentPlants = 0;
                        other.getFlowerPoker().currentPlants = 0;
                        player.getItems().addItem(299, 5);
                        other.getItems().addItem(299, 5);
                        pathToLane(true);
                        exe.stop();
                    }
                }
            }, 1);

            return;
        }

        String totalPotMessage = calculateTotalPotMessage();
        FlowerHand winner = getWinner(otherHand, hostHand);
        Server.getLogging().write(new FlowerpokerResultLog(player, other.getLoginName(), otherHandToString, handToString));
        player.unlock();
        other.unlock();

        if (winner == otherHand) {
            other.forcedChat(otherHandToString + totalPotMessage);
            player.forcedChat(handToString + " - I LOSE");
            giveItems(other);
        } else {
            player.forcedChat(handToString + totalPotMessage);
            other.forcedChat(otherHandToString + " - I LOSE");
            giveItems(player);
        }

        // Removes partner/lane
        CycleEventHandler.getSingleton().addEvent(FLOWER_OBJECT, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer exe) {
                if (exe.getTotalExecutions() == 5) {
                    exe.stop();
                    removeLaneFlowers(true);
                }
            }
        }, 1);
    }

    public static FlowerHand getWinner(FlowerHand a, FlowerHand b) {
        int aOrdinal = a.ordinal();
        int bOrdinal = b.ordinal();
        if (aOrdinal == bOrdinal)
            throw new IllegalStateException("Ties don't happen here.");
        return aOrdinal > bOrdinal ? a : b;
    }

    private void giveItems(Player player) {
        for (GameItem item : prizePool) {
            if (item != null) {
                player.getItems().addItemUnderAnyCircumstance(item.id(), item.amount());
            }
        }
        prizePool = Lists.newArrayList();
    }

    public long calculateGpPot() {
        long saved = 0;
        for (GameItem item : prizePool) {
            if (item != null) {
                if (item.id() == 995) {
                    saved += item.getDef().getRawShopValue() * item.amount();
                }
            }
        }
        return saved;
    }
    public long calculateFoePot() {
        long saved = 0;
        for (GameItem item : prizePool) {
            if (item != null) {
                    int foeBurnRate = FireOfExchangeBurnPrice.getBurnPrice(null, item.id(), false);
                if (foeBurnRate > 0) {
                    saved += (long) foeBurnRate * item.amount();
                }
            }
        }
        return saved;
    }
    public String calculateTotalPotMessage() {
        String message = " - I WIN";
        int messageCount = 0;
        long gpPot = calculateGpPot();
        long foePot = calculateFoePot();
        if ( gpPot > 0 ) {
            message += " [" + Misc.formatCoins(gpPot) + " GP]";
            messageCount += 1;
        }
        if ( foePot > 0 ){
            if (messageCount >= 1) {
                message += " &";
            }
            message += " [" + Misc.formatCoins(foePot) + " EP]";
        }
        return message;
    }
    /**
     * For auto flowers in the future
     */
    public void plantSeed(Player person, boolean manualClick, boolean forced) {
        if (person.getFlowerPoker().currentPlants >= 5) {
            return;
        }

        if (!Boundary.isIn(person, Boundary.FP_LANES)) {
            person.sendMessage("Please wait until you are at start before planting.");
            return;
        }
        if (person.lastManualSeedPlant > System.currentTimeMillis() && manualClick) {
            //person.sendMessage("Please wait before planting.");
            return;
        }

        if (person.lastManualSeedPlant > System.currentTimeMillis() && manualClick && System.currentTimeMillis() - person.lastForcedSeedPlant < 2_000) {
            return;
        }

        if (!manualClick) {
            person.sendMessage("<col=ff0000>You failed to plant the seed within 10 seconds, it has been auto planted for you.");
        }

        person.facePosition(new Position(person.getX(), person.getY() - 1));
        FlowerData chosenFlower = FlowerData.getRandomFlower();

        person.getItems().deleteItem(299, 1);
        person.getFlowerPoker().lastPlantTicks = 0;
        person.getFlowerPoker().currentPlants++;
        if (forced)
        person.lastForcedSeedPlant = System.currentTimeMillis();
        person.getFlowerPoker().dealtHand.add(chosenFlower);
        if (manualClick)
        person.lastManualSeedPlant = System.currentTimeMillis() + 1000;

        logger.debug("last plant" + person.lastManualSeedPlant+"  "+person.lastForcedSeedPlant+" ");
        displayResult(person);
        GlobalObject flower = new GlobalObject(chosenFlower.objectId, person.getX(), person.getY(), person.getHeight(), 3, 10, 100, -1);
        spawnedFlowers.add(flower);
        Server.getGlobalObjects().add(flower);
        PathFinder.getPathFinder().findRoute(person, person.getX(), person.getY() + 1, false, 1, 1, true);
        person.facePosition(person.absX, person.absY - 1);
        if (finishedPlanting()) {

            CycleEventHandler.getSingleton().addEvent(FLOWER_OBJECT, new CycleEvent() {

                @Override
                public void execute(CycleEventContainer exe) {

                    if (exe.getTotalExecutions() == 3) {
                        displayWinner();
                    }
                }
            }, 1);
        }
        return;
    }

    public String getForceTextForHand(Player person) {
        FlowerHand hand = person.getFlowerPoker().currentHand;
        boolean goodHand = hand != null;
        if (hand == FlowerHand.BUST) {
            if (person.getFlowerPoker().currentPlants == 5) {
                return "BUST";
            }
            return "";
        }
        return goodHand ? hand.name().replaceAll("_", " ") : "";
    }

    public int getCount(Player person, FlowerData data) {
        int count = 0;
        for (FlowerData flower : person.getFlowerPoker().dealtHand) {
            if (data == flower)
                count++;
        }
        return count;
    }

    public FlowerHand getResult(Player p) {
        List<FlowerData> result = p.getFlowerPoker().dealtHand;

        Map<Integer, Integer> pairs = checkPairs(result);

        for (FlowerData f : result) {
            if (f != null) {
                if (f == FlowerData.BLACK || f == FlowerData.WHITE) {
                    return f == FlowerData.BLACK ? FlowerHand.BLACK_FLOWER : FlowerHand.WHITE_FLOWER;
                }
            }
        }

        int totalPairs = pairs.size();

        /**
         * Rare pairs
         */
        if (totalPairs == 2) {
            if (pairs.get(0) == 3 && pairs.get(1) == 2 ||
                    pairs.get(1) == 3 && pairs.get(0) == 2) {
                return FlowerHand.FULL_HOUSE;
            }
            return FlowerHand.TWO_PAIRS;
        }

        for (FlowerData flower : result) {
            if (flower.objectId == 2987 || flower.objectId == 2988)
                return flower.objectId == 2987 ? FlowerHand.WHITE_FLOWER : FlowerHand.BLACK_FLOWER;
            else if (getCount(p, flower) == 5)
                return FlowerHand.FIVE_OF_A_KIND;
            else if (getCount(p, flower) == 4)
                return FlowerHand.FOUR_OF_A_KIND;
            else if (getCount(p, flower) == 3)
                return FlowerHand.THREE_OF_A_KIND;
            else if (getCount(p, flower) == 2)
                return FlowerHand.ONE_PAIR;
        }
        return null;
    }

    public void displayResult(Player p) {
        FlowerHand result = getResult(p);
        if (result == null) {
            if (p.getFlowerPoker().currentPlants == 5) {
                p.getFlowerPoker().currentHand = FlowerHand.BUST;
                p.getFlowerPoker().currentForceText = getForceTextForHand(p);
            }
            return;
        }
        if (!isAutoReplant(p.getFlowerPoker().currentHand))
            p.getFlowerPoker().currentHand = result;
        p.getFlowerPoker().currentForceText = getForceTextForHand(p);
    }

    private static Map<Integer, Integer> checkPairs(List<FlowerData> list) {
        Map<Integer, Integer> finalPairs = new HashMap<>();
        int[] pairs = new int[FlowerData.values().length];
        for (FlowerData flower : list) {
            if (flower != null) {
                pairs[flower.ordinal()]++;
            }
        }
        int slotId = 0;
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] >= 2) {
                finalPairs.put(slotId, pairs[i]);
                slotId++;
            }
        }
        return finalPairs;
    }

    private static int getLane(Player player) {
        return player.getAttributes().getInt("fp_lane", -1);
    }

    private static void setLane(Player player, int lane) {
        if (lane == -1) {
            player.getAttributes().removeInt("fp_lane");
            return;
        }
        player.getAttributes().setInt("fp_lane", lane);
    }
}
