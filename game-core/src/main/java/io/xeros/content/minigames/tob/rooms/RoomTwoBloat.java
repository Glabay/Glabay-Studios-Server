package io.xeros.content.minigames.tob.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.content.minigames.tob.TobRoom;
import io.xeros.content.minigames.tob.bosses.MaidenOfSugadinti;
import io.xeros.content.minigames.tob.bosses.PestilentBloat;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class RoomTwoBloat extends TobRoom {

    @Override
    public PestilentBloat spawn(InstancedArea instancedArea) {
        return new PestilentBloat(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3322, 4448);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {
        if (player.getX() > 3300) {         // West gate
            if (player.getX() <= 3303) {
                player.getPA().movePlayer(3305, player.getY(), player.getHeight());
            } else {
                player.getPA().movePlayer(3303, player.getY(), player.getHeight());
            }
        } else {                            // East gate
            if (player.getX() <= 3286) {
                player.getPA().movePlayer(3288, player.getY(), player.getHeight());
            } else {
                player.getPA().movePlayer(3286, player.getY(), player.getHeight());
            }
        }
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return TobConstants.BLOAT_BOSS_ROOM_BOUNDARY;
    }


    @Override
    public Position getDeathPosition() {
        return new Position(3305, 4447);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(3303, 4447, 0);
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return getFoodChest(new Position(3269, 4449, 0), 1);
    }
}
