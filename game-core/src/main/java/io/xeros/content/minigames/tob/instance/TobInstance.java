package io.xeros.content.minigames.tob.instance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.content.minigames.tob.TobRoom;
import io.xeros.content.minigames.tob.rooms.RoomSevenTreasure;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.content.party.PlayerParty;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.DiedAtTobLog;

public class TobInstance extends InstancedArea {

    public static final String TOB_DEAD_ATTR_KEY = "dead_tob";
    private static final int TREASURE_ROOM_INDEX = 6;

    private final HashSet<String> chestRewards = new HashSet<>();
    private final FoodRewards foodRewards = new FoodRewards();
    private final MvpPoints mvpPoints = new MvpPoints();
    private final HashMap<String, List<GameItem>> chestRewardItems = new HashMap<>();
    private int roomIndex = -1;
    private boolean fightStarted = false;
    private boolean lastRoom;
    private final int size;

    private boolean finalBossComplete;

    public TobInstance(int size) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, TobConstants.ALL_BOUNDARIES);
        this.size = size;
    }

    @Override
    public void remove(Player player) {
        if (roomIndex == TREASURE_ROOM_INDEX) {
            RoomSevenTreasure.openChest(player, (TobInstance) player.getInstance());
        }

        super.remove(player);
        player.getBossTimers().remove(TobConstants.THEATRE_OF_BLOOD);
    }

    public void removeButLeaveInParty(Player player) {
        super.remove(player);
    }

    public void start(List<Player> playerList) {
        if (playerList.isEmpty())
            return;
        initialiseNextRoom(playerList.get(0));
        TobRoom tobRoom = TobConstants.ROOM_LIST.get(0);
        playerList.forEach(plr -> {
            if (plr.getPA().calculateTotalLevel() < plr.getMode().getTotalLevelForTob()) {
                plr.sendStatement("You need " + Misc.insertCommas(plr.getMode().getTotalLevelForTob()) + " total level to compete.");
                return;
            }

            add(plr);
            plr.moveTo(resolve(tobRoom.getPlayerSpawnPosition()));
            plr.sendMessage("Welcome to the Theatre of Blood.");
            plr.getBossTimers().track(TobConstants.THEATRE_OF_BLOOD);
            plr.getPA().closeAllWindows();
        });
    }

    private void initialiseNextRoom(Player player) {
        roomIndex = getPlayerRoomIndex(player) + 1;
        TobRoom tobRoom = TobConstants.ROOM_LIST.get(roomIndex);
        var boss = tobRoom.spawn(this);
        if (boss != null) {
            var modifier = TobConstants.getHealthModifier(size);
            var maxHealth = (int) (boss.getHealth().getMaximumHealth() * modifier);
            boss.getHealth().setCurrentHealth(maxHealth);
            boss.getHealth().setMaximumHealth(maxHealth);
        }
        GlobalObject foodChest = tobRoom.getFoodChestPosition();
        if (foodChest != null) {
            Server.getGlobalObjects().add(foodChest.withHeight(resolveHeight(foodChest.getHeight())).setInstance(this));
        }
        fightStarted = false;
    }

    public void moveToNextRoom(Player player) {
        if (getCurrentRoom().isRoomComplete(this) || getPlayerRoomIndex(player) < roomIndex) {
            int nextRoomIndex = getPlayerRoomIndex(player) + 1;
            if (roomIndex < nextRoomIndex) {
                initialiseNextRoom(player);
            }

            player.healEverything();
            Position playerSpawnPosition = resolve(TobConstants.ROOM_LIST.get(nextRoomIndex).getPlayerSpawnPosition());
            player.moveTo(playerSpawnPosition);
            player.getAttributes().removeBoolean(TOB_DEAD_ATTR_KEY);
        } else {
            player.sendMessage("You haven't completed this room yet!");
        }
    }

    private int getPlayerRoomIndex(Player player) {
        for (int index = 0; index < TobConstants.ALL_BOUNDARIES.length; index++) {
            if (TobConstants.ALL_BOUNDARIES[index].in(player)) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        if (object.getId() == TobConstants.BOSS_GATE_OBJECT_ID || object.getId() == TobConstants.ENTER_FINAL_ROOM_OBJECT_ID) {
            if (getPlayerRoomIndex(player) == roomIndex && !getCurrentRoom().isRoomComplete(this)
                || lastRoom && object.getId() == TobConstants.ENTER_FINAL_ROOM_OBJECT_ID) {// In last unlocked room and fight not completed
                if (!fightStarted || lastRoom) {
                    if (player.equals(player.getInstance().getPlayers().get(0))) {
                        player.start(new DialogueBuilder(player).option(new DialogueOption("Start fight", this::startFight),
                                new DialogueOption("Cancel", plr -> plr.getPA().closeAllWindows())));
                    } else {
                        player.sendMessage("Only the party leader can start a fight.");
                    }
                } else {
                    if (player.getAttributes().getBoolean(TOB_DEAD_ATTR_KEY)) {
                        player.sendMessage("You've been disqualified from the fight for dying, you must wait.");
                    } else {
                        player.sendMessage("The fight has started, there's no turning back now.");
                    }
                }
            } else {                                                                                                          // In room before last unlocked or room complete
                Optional<TobRoom> gateRoomOptional = TobConstants.ROOM_LIST.stream().filter(gateRoom -> gateRoom.getBoundary().in(player)).findFirst();
                gateRoomOptional.ifPresent(tobRoom -> gateRoomOptional.get().handleClickBossGate(player, object));
            }

            return true;
        }

        for (TobRoom room : TobConstants.ROOM_LIST) {
            if (room.handleClickObject(player, object, option)) {
                return true;
            }
        }

        switch (object.getId()) {
            case TobConstants.TREASURE_ROOM_ENTRANCE_OBJECT_ID:
            case TobConstants.ENTER_NEXT_ROOM_OBJECT_ID:
            case TobConstants.ENTER_FINAL_ROOM_OBJECT_ID:
                moveToNextRoom(player);
                return true;
            case TobConstants.FOOD_CHEST_OBJECT_ID:
                foodRewards.openFoodRewards(player);
                return true;
        }

        return false;
    }

    @Override
    public boolean handleDeath(Player player) {
        int roomIndex = getPlayerRoomIndex(player);
        if (roomIndex == -1) {
            player.moveTo(TobConstants.FINISHED_TOB_POSITION);
            player.sendMessage("Could not handle death!");
            return true;
        }

        TobRoom room = TobConstants.ROOM_LIST.get(getPlayerRoomIndex(player));
        player.moveTo(resolve(room.getDeathPosition()));
        player.sendMessage("Oh dear, you have died!");
        player.getAttributes().setBoolean(TOB_DEAD_ATTR_KEY, true);
        Server.getLogging().write(new DiedAtTobLog(player, this));

        if (allDead()) {
            Lists.newArrayList(getPlayers()).forEach(plr -> {
                plr.moveTo(TobConstants.FINISHED_TOB_POSITION);
                removeButLeaveInParty(plr);
                plr.sendMessage("Your performance in the theatre left much to be desired; your team has been defeated.");
            });
        }
        return true;
    }

    private void startFight(Player player) {
        if (getPlayers().stream().allMatch(plr -> getPlayerRoomIndex(plr) == roomIndex)) {
            fightStarted = true;
            TobRoom currentRoom = getCurrentRoom();
            getPlayers().forEach(plr -> {
                if (lastRoom)
                    moveToNextRoom(plr);
                else
                    plr.moveTo(resolve(currentRoom.getFightStartPosition()));
                plr.getPA().closeAllWindows();
            });
            lastRoom = false;
        } else {
            player.sendStatement("All players must be in the room to start the fight.");
        }
    }

    private boolean allDead() {
        return getPlayers().stream().allMatch(plr -> plr.getAttributes().getBoolean(TOB_DEAD_ATTR_KEY));
    }

    @Override
    public void onDispose() { }

    private TobRoom getCurrentRoom() {
        return TobConstants.ROOM_LIST.get(roomIndex);
    }

    public HashSet<String> getChestRewards() {
        return chestRewards;
    }

    public FoodRewards getFoodRewards() {
        return foodRewards;
    }

    public MvpPoints getMvpPoints() {
        return mvpPoints;
    }

    public MvpPoints getChooseRareWinner() {
        return mvpPoints;
    }

    public HashMap<String, List<GameItem>> getChestRewardItems() {
        return chestRewardItems;
    }

    public int getPartySize() {
        return size;
    }

    public void setLastRoom(boolean lastRoom) {
        this.lastRoom = lastRoom;
    }

    public boolean isFinalBossComplete() {
        return finalBossComplete;
    }

    public void setFinalBossComplete(boolean finalBossComplete) {
        this.finalBossComplete = finalBossComplete;
    }

}
