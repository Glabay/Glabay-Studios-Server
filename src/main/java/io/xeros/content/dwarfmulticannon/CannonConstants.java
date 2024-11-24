package io.xeros.content.dwarfmulticannon;

import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.model.Animation;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Boundary;

import static io.xeros.model.entity.player.Boundary.ABYSSAL_SIRE;
import static io.xeros.model.entity.player.Boundary.AL_KHARID_BOUNDARY;
import static io.xeros.model.entity.player.Boundary.ARDOUGNE_BOUNDARY;
import static io.xeros.model.entity.player.Boundary.ARMADYL_GODWARS;
import static io.xeros.model.entity.player.Boundary.BANDOS_GODWARS;
import static io.xeros.model.entity.player.Boundary.CANNON_FREMNIK_DUNGEON;
import static io.xeros.model.entity.player.Boundary.CANNON_JAD;
import static io.xeros.model.entity.player.Boundary.CATACOMBS;
import static io.xeros.model.entity.player.Boundary.CERBERUS_ROOM_EAST;
import static io.xeros.model.entity.player.Boundary.CERBERUS_ROOM_NORTH;
import static io.xeros.model.entity.player.Boundary.CERBERUS_ROOM_WEST;
import static io.xeros.model.entity.player.Boundary.DAGGANOTH_KINGS;
import static io.xeros.model.entity.player.Boundary.DAGGANOTH_MOTHER;
import static io.xeros.model.entity.player.Boundary.DEMONIC_GORILLA;
import static io.xeros.model.entity.player.Boundary.DEMONIC_GORILLAS;
import static io.xeros.model.entity.player.Boundary.DONATOR_ZONE;
import static io.xeros.model.entity.player.Boundary.EDGEVILLE_EXTENDED;
import static io.xeros.model.entity.player.Boundary.GODWARS_MAIN_AREA;
import static io.xeros.model.entity.player.Boundary.HUNLLEF_BOSS_ROOM;
import static io.xeros.model.entity.player.Boundary.HUNTER_AREA;
import static io.xeros.model.entity.player.Boundary.HYDRA_BOSS_ROOM;
import static io.xeros.model.entity.player.Boundary.HYDRA_DUNGEON;
import static io.xeros.model.entity.player.Boundary.HYDRA_DUNGEON2;
import static io.xeros.model.entity.player.Boundary.ICE_DEMON;
import static io.xeros.model.entity.player.Boundary.INFERNO;
import static io.xeros.model.entity.player.Boundary.KALPHITE_QUEEN;
import static io.xeros.model.entity.player.Boundary.KBD;
import static io.xeros.model.entity.player.Boundary.KRAKEN_CAVE;
import static io.xeros.model.entity.player.Boundary.LEGENDARY_ZONE;
import static io.xeros.model.entity.player.Boundary.LIZARDMAN_CANYON;
import static io.xeros.model.entity.player.Boundary.MITHRIL_DRAGONS;
import static io.xeros.model.entity.player.Boundary.OLM;
import static io.xeros.model.entity.player.Boundary.ONYX_ZONE;
import static io.xeros.model.entity.player.Boundary.PEST_CONTROL_AREA;
import static io.xeros.model.entity.player.Boundary.RAIDS;
import static io.xeros.model.entity.player.Boundary.RAIDS_LOBBY;
import static io.xeros.model.entity.player.Boundary.RAIDS_LOBBY_ENTRANCE;
import static io.xeros.model.entity.player.Boundary.RAID_F1;
import static io.xeros.model.entity.player.Boundary.RAID_F2;
import static io.xeros.model.entity.player.Boundary.RAID_F3;
import static io.xeros.model.entity.player.Boundary.RAID_MAIN;
import static io.xeros.model.entity.player.Boundary.REV_CAVE;
import static io.xeros.model.entity.player.Boundary.SARADOMIN_GODWARS;
import static io.xeros.model.entity.player.Boundary.SEERS_BOUNDARY;
import static io.xeros.model.entity.player.Boundary.SKELETAL_MYSTICS;
import static io.xeros.model.entity.player.Boundary.SKOTIZO_BOSSROOM;
import static io.xeros.model.entity.player.Boundary.SLAYER_TOWER_BOUNDARY;
import static io.xeros.model.entity.player.Boundary.SWAMP_AREA;
import static io.xeros.model.entity.player.Boundary.TEKTON;
import static io.xeros.model.entity.player.Boundary.TEKTON_ATTACK_BOUNDARY;
import static io.xeros.model.entity.player.Boundary.THEATRE_LOBBY;
import static io.xeros.model.entity.player.Boundary.THEATRE_LOBBY_ENTRANCE;
import static io.xeros.model.entity.player.Boundary.THERMONUCLEARS;
import static io.xeros.model.entity.player.Boundary.VARROCK_BOUNDARY;
import static io.xeros.model.entity.player.Boundary.WARRIORS_GUILD;
import static io.xeros.model.entity.player.Boundary.WATERBIRTH_DUNGEON;
import static io.xeros.model.entity.player.Boundary.WILDERNESS_GOD_WARS_BOUNDARY;
import static io.xeros.model.entity.player.Boundary.WILDERNESS_UNDERGROUND;
import static io.xeros.model.entity.player.Boundary.XERIC_LOBBY;
import static io.xeros.model.entity.player.Boundary.XERIC_LOBBY_ENTRANCE;
import static io.xeros.model.entity.player.Boundary.ZAMORAK_GODWARS;
import static io.xeros.model.entity.player.Boundary.*;

public class CannonConstants {

    static final int COMPLETE_CANNON_OBJECT_ID = 6;

    /**
     * The damage range the cannon may deal (between 0 and this number). This
     * can (and should) be edited to deal damage based on your skill levels.
     */
    static final int MAX_DAMAGE = 30;

    static final int MAX_GRANITE_DAMAGE = 35;

    public static final int CANNON_SIZE = 3;

    static final int PROJECTILE_ID = 53;

    /**
     * Animation when placing the cannon.
     */
    static final Animation PLACING_ANIMATION = new Animation(827);

    /**
     * Cannon pieces.
     */
    static final int[] CANNON_PIECES = {Items.CANNON_BASE, Items.CANNON_BARRELS, Items.CANNON_FURNACE, Items.CANNON_STAND};

    public static final Boundary[] ALLOWED_REV_AREAS = {
            SLAYER_REV_CAVE_1, SLAYER_REV_CAVE_2, SLAYER_REV_CAVE_3
    };

    public static final Boundary[] PROHIBITED_CANNON_AREAS = {
            DEMONIC_GORILLAS,
            //Agility Courses/City
            SEERS_BOUNDARY, VARROCK_BOUNDARY, ARDOUGNE_BOUNDARY, AL_KHARID_BOUNDARY, EDGEVILLE_EXTENDED, SWAMP_AREA,
            //TZHAAR
            CANNON_JAD,
            //INFERNO
            INFERNO,
            //CERBERUS
            CERBERUS_ROOM_NORTH, CERBERUS_ROOM_WEST, CERBERUS_ROOM_EAST,
            //CATACOMBS
            CATACOMBS, SKOTIZO_BOSSROOM,
            //GODWARS
            BANDOS_GODWARS, ARMADYL_GODWARS, ZAMORAK_GODWARS, SARADOMIN_GODWARS, GODWARS_MAIN_AREA,
            //WILDERNESS
            WILDERNESS_UNDERGROUND, WILDERNESS_GOD_WARS_BOUNDARY, REV_CAVE,
            //SLAYER AREAS
            SLAYER_TOWER_BOUNDARY, KALPHITE_QUEEN, KRAKEN_CAVE, LIZARDMAN_CANYON, THERMONUCLEARS, WATERBIRTH_DUNGEON, MITHRIL_DRAGONS,
            //HYDRA
            HYDRA_DUNGEON, HYDRA_DUNGEON2, HYDRA_BOSS_ROOM ,
            //DONATOR ZONES
            ONYX_ZONE, LEGENDARY_ZONE, DONATOR_ZONE, LEGENDARY_ZONE, ONYX_ZONE, LZ_CAVE,
            //MINIGAMES
            PEST_CONTROL_AREA, WARRIORS_GUILD,
            //SKILLING
            HUNTER_AREA,
            //RAIDS/TOB
            RAIDS_LOBBY, RAIDS_LOBBY_ENTRANCE, XERIC_LOBBY, XERIC_LOBBY_ENTRANCE, THEATRE_LOBBY, THEATRE_LOBBY_ENTRANCE,
            OLM, RAIDS, RAID_MAIN, RAID_F1, RAID_F2, RAID_F3, TEKTON, TEKTON_ATTACK_BOUNDARY, SKELETAL_MYSTICS, ICE_DEMON,
            CANNON_FREMNIK_DUNGEON,
            //OTHER BOSSES
            HUNLLEF_BOSS_ROOM, DEMONIC_GORILLA, ZULRAH, KBD, DAGGANOTH_KINGS, DAGGANOTH_MOTHER, ABYSSAL_SIRE,

            // Theatre of blood
            TobConstants.MAIDEN_BOSS_ROOM_BOUNDARY,

    };

}
