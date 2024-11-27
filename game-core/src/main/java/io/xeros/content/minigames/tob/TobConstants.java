package io.xeros.content.minigames.tob;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.content.minigames.tob.rooms.RoomFiveXarpus;
import io.xeros.content.minigames.tob.rooms.RoomFourSotetseg;
import io.xeros.content.minigames.tob.rooms.RoomOneMaiden;
import io.xeros.content.minigames.tob.rooms.RoomSevenTreasure;
import io.xeros.content.minigames.tob.rooms.RoomSixVerzik;
import io.xeros.content.minigames.tob.rooms.RoomThreeNylocas;
import io.xeros.content.minigames.tob.rooms.RoomTwoBloat;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;

public class TobConstants {

    public static final String THEATRE_OF_BLOOD = "Theatre of Blood";
    public static final Position LOBBY_TELEPORT_POSITION = new Position(3650, 3211);
    public static final Position FINISHED_TOB_POSITION = new Position(3671, 3219);

    /**
     * Lobby party overlay interface
     */
    public static final int LOBBY_WALKABLE_INTERFACE = 21_473;
    public static final int LOBBY_WALKABLE_INTERFACE_HEADER = 21_475;
    public static final int LOBBY_WALKABLE_NAME_CONTAINER = 21_476;
    public static final List<String> LOBBY_WALKABLE_EMPTY_NAME_LIST = Collections.unmodifiableList(Lists.newArrayList("-", "-", "-", "-", "-"));

    public static final int ENTER_TOB_OBJECT_ID = 32_653;

    public static final int ENTER_NEXT_ROOM_OBJECT_ID = 33_113;
    public static final int ENTER_FINAL_ROOM_OBJECT_ID = 32_751;
    public static final int BOSS_GATE_OBJECT_ID = 32_755;

    public static final int TREASURE_ROOM_ENTRANCE_OBJECT_ID = 32_738;
    public static final int TREASURE_ROOM_EXIT_INSTANCE_OBJECT_ID = 32_996;

    public static final int FOOD_CHEST_OBJECT_ID = 29_069;

    public static final Boundary TOB_LOBBY = new Boundary(3641, 3202, 3685, 3237);

    public static final Boundary MAIDEN_BOSS_ROOM_BOUNDARY = new Boundary(3138, 4411, 3238, 4471);
    public static final Boundary BLOAT_BOSS_ROOM_BOUNDARY = new Boundary(3265, 4428, 3327, 4465);
    public static final Boundary NYLOCAS_BOSS_ROOM_BOUNDARY = new Boundary(3276, 4231, 3315, 4285);
    public static final Boundary SOTETSEG_BOSS_ROOM_BOUNDARY = new Boundary(3264, 4291, 3295, 4336);
    public static final Boundary XARPUS_BOSS_ROOM_BOUNDARY = new Boundary(3149, 4365, 3193, 4410);
    public static final Boundary VERZIK_BOSS_ROOM_BOUNDARY = new Boundary(3147, 4291, 3192, 4336);
    public static final Boundary LOOT_ROOM_BOUNDARY = new Boundary(3222, 4301, 3253, 4337);

    public static Boundary[] ALL_BOUNDARIES = { MAIDEN_BOSS_ROOM_BOUNDARY, BLOAT_BOSS_ROOM_BOUNDARY,
            NYLOCAS_BOSS_ROOM_BOUNDARY, SOTETSEG_BOSS_ROOM_BOUNDARY, XARPUS_BOSS_ROOM_BOUNDARY,
            VERZIK_BOSS_ROOM_BOUNDARY, LOOT_ROOM_BOUNDARY };

    public static final List<TobRoom> ROOM_LIST = Collections.unmodifiableList(Lists.newArrayList(
            new RoomOneMaiden(), new RoomTwoBloat(), new RoomThreeNylocas(), new RoomFourSotetseg(), new RoomFiveXarpus(), new RoomSixVerzik(), new RoomSevenTreasure()));




    public static double getHealthModifier(final int size) {
        if (size == 5) {
            return 1;
        } else if (size == 4) {
            return 0.875;
        } else {
            return 0.75;
        }
    }
}
