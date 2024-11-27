package io.xeros.content.minigames.tob.rooms;

import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.item.lootable.impl.TheatreOfBloodChest;
import io.xeros.content.minigames.tob.TobBoss;
import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.content.minigames.tob.TobRoom;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;
import org.apache.commons.lang3.tuple.Pair;

public class RoomSevenTreasure extends TobRoom {

    private static final int CHEST_ID = 32_990;
    private static final int RARE_CHEST_ID = 32_991;
    private static final int CHEST_OPEN_ID = 32_994;

    private static final GlobalObject[] CHESTS = {
            new GlobalObject(CHEST_ID, new Position(3226, 4323), 3, 10),
            new GlobalObject(CHEST_ID, new Position(3226, 4327), 3, 10),
            new GlobalObject(CHEST_ID, new Position(3233, 4330), 0, 10),
            new GlobalObject(CHEST_ID, new Position(3240, 4327), 1, 10),
            new GlobalObject(CHEST_ID, new Position(3240, 4323), 1, 10)
    };

    public static void openChest(Player player, TobInstance tobInstance) {
        if (tobInstance.getChestRewards().contains(player.getLoginName())) {
            player.sendMessage("You've already received your rewards!");
            return;
        }

        tobInstance.getChestRewards().add(player.getLoginName());
        TheatreOfBloodChest.rewardItems(player, tobInstance.getChestRewardItems().get(player.getLoginName()));
    }

    @Override
    public TobBoss spawn(InstancedArea instancedArea) {
        TobInstance tobInstance = (TobInstance) instancedArea;
        Player rareWinner = Misc.random(tobInstance.getPlayers());//tobInstance.getMvpPoints().chooseRareWinner(tobInstance.getPlayers());

        instancedArea.getPlayers().forEach(player -> {
            tobInstance.getChestRewardItems().put(player.getLoginName(),
                    TheatreOfBloodChest.getRandomItems(player.equals(rareWinner), tobInstance.getPartySize()));

            Pair<Integer, Integer> rank = tobInstance.getMvpPoints().getRank(player);
            player.sendMessage("You ranked @pur@#" + rank.getLeft() + "@bla@ with @pur@" + rank.getRight() + " points.");

//            if (rareWinner.equals(player)) {
//                player.sendMessage("@pur@You have been selected as the MVP.");
//            } else {
//                player.sendMessage("@pur@" + rareWinner.getDisplayNameFormatted() + " has been selected as the MVP.");
//            }
        });

        for (int index = 0; index < instancedArea.getPlayers().size(); index++) {
            Player player = instancedArea.getPlayers().get(index);
            GlobalObject chest = CHESTS[index];

            if (TheatreOfBloodChest.containsRare(tobInstance.getChestRewardItems().get(player.getLoginName()))) {
                chest = chest.withId(RARE_CHEST_ID);
            }

            Position position = chest.getPosition();
            GlobalObject chestObject = chest.withPosition(instancedArea.resolve(position)).setInstance(instancedArea);
            Server.getGlobalObjects().add(chestObject);
            instancedArea.getPlayers().get(index).getPA().createObjectHints(position.getX() + 1, position.getY() + 1, 100, 5);
        }
        return null;
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3237, 4307, 0);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        TobInstance tobInstance = (TobInstance) player.getInstance();
        if (worldObject.getId() == TobConstants.TREASURE_ROOM_EXIT_INSTANCE_OBJECT_ID) {
            player.start(new DialogueBuilder(player).option(
                    new DialogueOption("Leave", plr -> {
                        plr.moveTo(TobConstants.FINISHED_TOB_POSITION);
                        plr.getPA().closeAllWindows();
                    }),
                    new DialogueOption("Stay", plr -> plr.getPA().closeAllWindows())));
        } else if (worldObject.getId() == CHEST_ID || worldObject.getId() == RARE_CHEST_ID) {
            if (player.getTobContainer().inTob()) {
                int index = player.getInstance().getPlayers().indexOf(player);

                if (index == -1) {
                    player.sendMessage("@red@There was an issue getting your chest..");
                    return true;
                }

                Position clicked = player.getInstance().resolve(worldObject.getPosition());
                Position actual = player.getInstance().resolve(CHESTS[index].getPosition());
                if (!clicked.equals(actual)) {
                    player.sendMessage("That's not your chest!");
                    return true;
                }

                if (tobInstance.getChestRewards().contains(player.getLoginName())) {
                    player.sendMessage("You've already received your rewards!");
                    return true;
                }

                player.getPA().createObjectHints(worldObject.getX() + 1, worldObject.getY() + 1, 0, 0);
                Server.getGlobalObjects().remove(worldObject.toGlobalObject());
                Server.getGlobalObjects().add(worldObject.toGlobalObject().withId(CHEST_OPEN_ID).setInstance(player.getInstance()));
                openChest(player, tobInstance);
            }
        }

        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {}

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return true;
    }

    @Override
    public Boundary getBoundary() {
        return TobConstants.LOOT_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return getPlayerSpawnPosition();
    }

    @Override
    public Position getFightStartPosition() {
        return null;
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return null;
    }
}
