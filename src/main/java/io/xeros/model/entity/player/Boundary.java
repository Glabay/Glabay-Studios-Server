package io.xeros.model.entity.player;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;
import io.xeros.model.collisionmap.doors.Location;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Mar 2, 2014
 */
public class Boundary {


    /**
	 * Calculates the lowest/highest position based on the positions provided.
	 *
	 * @return {@link Boundary}
	 */
	public static Boundary calculateBoundary(Position a, Position b, int height) {
		return calculateBoundary(a.getX(), a.getY(), b.getX(), b.getY(), height);
	}

	/**
	 * Calculates the lowest/highest position based on the positions provided.
	 *
	 * @return {@link Boundary}
	 */
	public static Boundary calculateBoundary(int x1, int y1, int x2, int y2, int height) {
		return new Boundary(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), height);
	}

	public static int getWildernessLevel(int x, int y) {
		int modY = x > 6400 ? y - 6400 : y;
		return (((modY - 3520) / 8) + 1);
	}

	int minX, minY, highX, highY;
	int height;

	public Boundary(Position low, Position high) {
		this(low.getX(), low.getY(), high.getX(), high.getY());
	}

	/**
	 * @param minX  The south-west x coordinate
	 * @param minY  The south-west y coordinate
	 * @param highX The north-east x coordinate
	 * @param highY The north-east y coordinate
	 */
	public Boundary(int minX, int minY, int highX, int highY) {
		this.minX = minX;
		this.minY = minY;
		this.highX = highX;
		this.highY = highY;
		height = -1;
	}

	public Boundary(Position low, Position high, int height) {
		this(low.getX(), low.getY(), high.getX(), high.getY(), height);
	}

	/**
	 * @param minX   The south-west x coordinate
	 * @param minY   The south-west y coordinate
	 * @param highX  The north-east x coordinate
	 * @param highY  The north-east y coordinate
	 * @param height The height of the boundary
	 */
	public Boundary(int minX, int minY, int highX, int highY, int height) {
		this.minX = minX;
		this.minY = minY;
		this.highX = highX;
		this.highY = highY;
		this.height = height;
	}

	/**
	 * Check if this boundary intersects with another boundary.
	 */
	public boolean intersects(Boundary boundary) {
		int x1 = getMinimumX();
		int y1 = getMinimumY();
		int x2 = getMaximumX();
		int y2 = getMaximumY();
		int x3 = boundary.getMinimumX();
		int y3 = boundary.getMinimumY();
		int x4 = boundary.getMaximumX();
		int y4 = boundary.getMaximumY();
		return (x1 < x4) && (x3 < x2) && (y1 < y4) && (y3 < y2);
	}

	public int getMinimumX() {
		return minX;
	}

	public int getMinimumY() {
		return minY;
	}

	public int getMaximumX() {
		return highX;
	}

	public int getMaximumY() {
		return highY;
	}

	public boolean in(Entity entity) {
		return isIn(entity, this);
	}

	/**
	 * @param entity     The entity object
	 * @param boundaries The array of Boundary objects
	 * @return
	 */
	public static boolean isIn(Entity entity, Boundary... boundaries) {
		Preconditions.checkState(boundaries.length > 0, "No boundaries specified.");
		return isIn(entity.getPosition(), boundaries);
	}

	public static boolean isIn(Position position, Boundary... boundaries) {
		for (Boundary b : boundaries) {
			if (b.height >= 0) {
				if (position.getHeight() != b.height) {
					continue;
				}
			}
			if (position.getX() >= b.minX && position.getX() <= b.highX && position.getY() >= b.minY && position.getY() <= b.highY) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param player     The player object
	 * @param boundaries The boundary object
	 * @return
	 */
	public static boolean isIn(Player player, Boundary boundaries) {
		if (boundaries.height >= 0) {
			if (player.heightLevel != boundaries.height) {
				return false;
			}
		}
		return player.absX >= boundaries.minX && player.absX <= boundaries.highX && player.absY >= boundaries.minY && player.absY <= boundaries.highY;
	}

	/**
	 * @param npc        The npc object
	 * @param boundaries The boundary object
	 * @return
	 */
	public static boolean isIn(NPC npc, Boundary boundaries) {
		if (boundaries.height >= 0) {
			if (npc.heightLevel != boundaries.height) {
				return false;
			}
		}
		return npc.absX >= boundaries.minX && npc.absX <= boundaries.highX && npc.absY >= boundaries.minY && npc.absY <= boundaries.highY;
	}

	public static boolean isIn(NPC npc, Boundary... boundaries) {
		for (Boundary boundary : boundaries) {
			if (boundary.height >= 0) {
				if (npc.heightLevel != boundary.height) {
					return false;
				}
			}
			if (npc.absX >= boundary.minX && npc.absX <= boundary.highX && npc.absY >= boundary.minY && npc.absY <= boundary.highY) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInSameBoundary(Player player1, Player player2, Boundary[] boundaries) {
		Optional<Boundary> boundary1 = Arrays.asList(boundaries).stream().filter(b -> isIn(player1, b)).findFirst();
		Optional<Boundary> boundary2 = Arrays.asList(boundaries).stream().filter(b -> isIn(player2, b)).findFirst();
		if (!boundary1.isPresent() || !boundary2.isPresent()) {
			return false;
		}
		return Objects.equals(boundary1.get(), boundary2.get());
	}

	public static int getPlayersInBoundary(Boundary boundary) {
		int i = 0;
		for (Player player : PlayerHandler.players)
			if (player != null)
				if (isIn(player, boundary))
					i++;
		return i;
	}

	/**
	 * Returns the centre point of a boundary as a {@link Coordinate}
	 *
	 * @param boundary The boundary of which we want the centre.
	 * @return The centre point of the boundary, represented as a {@link Coordinate}.
	 */
	public static Coordinate centre(Boundary boundary) {
		int x = (boundary.minX + boundary.highX) / 2;
		int y = (boundary.minY + boundary.highY) / 2;
		if (boundary.height >= 0) {
			return new Coordinate(x, y, boundary.height);
		} else {
			return new Coordinate(x, y, 0);
		}
	}

	/**
	 * Diary locations
	 */
	public static final Boundary VARROCK_BOUNDARY = new Boundary(3136, 3349, 3326, 3519);
	public static final Boundary ARDOUGNE_BOUNDARY = new Boundary(2432, 3259, 2690, 3380);
	public static final Boundary ARDOUGNE_ZOO_BRIDGE_BOUNDARY = new Boundary(2611, 3270, 2614, 3280);
	public static final Boundary FALADOR_BOUNDARY = new Boundary(2935, 3310, 3066, 3394);
	public static final Boundary CRAFTING_GUILD_BOUNDARY = new Boundary(2925, 3274, 2944, 3292);
	public static final Boundary TAVERLY_BOUNDARY = new Boundary(2866, 3388, 2938, 3517);
	public static final Boundary LUMRIDGE_BOUNDARY = new Boundary(3142, 3139, 3265, 3306);
	public static final Boundary DRAYNOR_DUNGEON_BOUNDARY = new Boundary(3084, 9623, 3132, 9700);
	public static final Boundary AL_KHARID_BOUNDARY = new Boundary(3263, 3136, 3388, 3328);
	public static final Boundary DRAYNOR_MANOR_BOUNDARY = new Boundary(3074, 3311, 3131, 3388);
	public static final Boundary DRAYNOR_BOUNDARY = new Boundary(3065, 3216, 3136, 3292);
	public static final Boundary KARAMJA_BOUNDARY = new Boundary(2816, 3139, 2965, 3205);
	public static final Boundary BRIMHAVEN_BOUNDARY = new Boundary(2683, 3138, 2815, 3248);
	public static final Boundary BRIMHAVEN_DUNGEON_BOUNDARY = new Boundary(2624, 9402, 2752, 9604);
	public static final Boundary TZHAAR_CITY_BOUNDARY = new Boundary(2368, 5056, 2495, 5183);
	public static final Boundary FOUNTAIN_OF_RUNE_BOUNDARY = new Boundary(3367, 3888, 3380, 3899);
	public static final Boundary DEMONIC_RUINS_BOUNDARY = new Boundary(3279, 3879, 3294, 3893);
	public static final Boundary WILDERNESS_GOD_WARS_BOUNDARY = new Boundary(3008, 10112, 3071, 10175);
	public static final Boundary RESOURCE_AREA_BOUNDARY = new Boundary(3173, 3923, 3197, 3945);
	public static final Boundary CANIFIS_BOUNDARY = new Boundary(3471, 3462, 3516, 3514);
	public static final Boundary CATHERBY_BOUNDARY = new Boundary(2767, 3392, 2864, 3521);
	public static final Boundary SEERS_BOUNDARY = new Boundary(2574, 3393, 2766, 3517);
	public static final Boundary RELLEKKA_BOUNDARY = new Boundary(2590, 3597, 2815, 3837);
	public static final Boundary GNOME_STRONGHOLD_BOUNDARY = new Boundary(2369, 3398, 2503, 3550);
	public static final Boundary LLETYA_BOUNDARY = new Boundary(2314, 3153, 2358, 3195);
	public static final Boundary BANDIT_CAMP_BOUNDARY = new Boundary(3156, 2965, 3189, 2993);
	public static final Boundary DESERT_BOUNDARY = new Boundary(3136, 2880, 3517, 3122);
	public static final Boundary SLAYER_TOWER_BOUNDARY = new Boundary(3399, 3527, 3454, 3581);
	public static final Boundary APE_ATOLL_BOUNDARY = new Boundary(2691, 2692, 2815, 2812);
	public static final Boundary FELDIP_HILLS_BOUNDARY = new Boundary(2474, 2880, 2672, 3010);
	public static final Boundary YANILLE_BOUNDARY = new Boundary(2531, 3072, 2624, 3126);
	public static final Boundary ZEAH_BOUNDARY = new Boundary(1402, 3446, 1920, 3972);
	public static final Boundary LUNAR_ISLE_BOUNDARY = new Boundary(2049, 3844, 2187, 3959);
	public static final Boundary FREMENNIK_ISLES_BOUNDARY = new Boundary(2300, 3776, 2436, 3902);
	public static final Boundary WATERBIRTH_ISLAND_BOUNDARY = new Boundary(2495, 3711, 2559, 3772);
	public static final Boundary MISCELLANIA_BOUNDARY = new Boundary(2493, 3835, 2628, 3921);
	public static final Boundary WOODCUTTING_GUILD_BOUNDARY = new Boundary(1608, 3479, 1657, 3516);
	public static final Boundary HUNLLEF_BOSS_ROOM = new Boundary(1156, 9922, 1186, 9948);
	public static final Boundary LUMBRIDGE_OUTLAST_LOBBY = new Boundary(3399, 4807, 3448, 4848);
	public static final Boundary LUMBRIDGE_OUTLAST_AREA = new Boundary(3136, 4928, 3199, 4991);
	public static final Boundary OUTLAST_AREA = new Boundary(3263, 4927, 3330, 4992);
	public static final Boundary OUTLAST = new Boundary(3263, 4927, 3330, 4992);
	public static final Boundary OUTLAST_LOBBY = new Boundary(3321, 4939, 3325, 4979);
	public static final Boundary OUTLAST_HUT = new Boundary(3077, 3506, 3085, 3514);
	public static final Boundary TOURNAMENT_LOBBIES_AND_AREAS = new Boundary(3134, 4673, 3638, 5121);
	public static final Boundary DEMONIC_GORILLAS = new Boundary(2071, 5613, 2192, 5702);
	public static final Boundary CRYSTAL_CAVE_STAIRS = new Boundary(3222, 12441, 3229, 12448);
	public static final Boundary CRYSTAL_CAVE_ENTRANCE = new Boundary(3268, 6050, 3278, 6056);
	public static final Boundary CRYSTAL_CAVE_AREA = new Boundary(3131, 12346, 3265, 12482);

	public static final Boundary FLOWER_POKER_AREA = new Boundary(3109, 3504, 3121, 3515);
	public static final Boundary FP_LANE_1 = new Boundary(3110, 3510, 3111, 3514);
	public static final Boundary FP_LANE_2 = new Boundary(3113, 3510, 3114, 3514);
	public static final Boundary FP_LANE_3 = new Boundary(3116, 3510, 3117, 3514);
	public static final Boundary FP_LANE_4 = new Boundary(3119, 3510, 3120, 3514);
	public static final Boundary[] FP_LANES = {FP_LANE_1, FP_LANE_2, FP_LANE_3, FP_LANE_4};
	public static final Boundary MAGE_ARENA = new Boundary(3092, 3912, 3117, 3954);
	/**
	 * 3118 3923
	 * 3128 3942
	 *
	 * 3082 3922
	 * 3095 3940
	 */
//3082 3921
	//3217 3942
	public static final boolean isInMageArena(Entity e) {
		return e.getX() >= 3095 && e.getX() <= 3117 && e.getY() >= 3912 && e.getY() <= 3954//center
		|| e.getX() >= 3082 && e.getX() <= 3217 && e.getY() >= 3921 && e.getY() <= 3942//west


				;
	}

	public static final Boundary GRAND_EXCHANGE = new Boundary(3095, 3493, 3098, 3496);
	/**
	 * Konar Locations
	 */
	public static final Boundary TAVERLY_DUNGEON = new Boundary(2802, 9715, 2959, 9858);

	public static final Boundary OSRS_GRAND_EXCHANGE = new Boundary(3142, 3468, 3189, 3516);


	public static final Boundary SARACHNIS_LAIR = new Boundary(1830,9893,1850,9911);
	public static final Boundary MIMIC_LAIR = new Boundary(2708,4309,2731,4328);
	public static final Boundary GROTESQUE_LAIR = new Boundary(1689, 4567,1704, 4582);
	/**
	 * Halloween
	 */
	public static final Boundary HALLOWEEN_ORDER_MINIGAME = new Boundary(2591, 4764, 2617, 4786);

	/**
	 * skilling island
	 */
	public static final Boundary SKILLING_ISLAND = new Boundary(3778, 3521, 3832, 3577);
	public static final Boundary SKILLING_ISLAND_BANK = new Boundary(3802, 3546, 3814, 3577);


	/*
	 *
	 */

	public static final Boundary SLAYER_REV_CAVE_1 = new Boundary(3224, 10192, 3251, 10218);
	public static final Boundary SLAYER_REV_CAVE_2 = new Boundary(3146, 10128, 3227, 10232);
	public static final Boundary SLAYER_REV_CAVE_3 = new Boundary(3126, 10041, 3240, 10150);
	public static final Boundary REV_CAVE = new Boundary(3121, 10038, 3276, 10245);
	/**
	 * Hunter
	 */
	public static final Boundary HUNTER_JUNGLE = new Boundary(1486, 3392, 1685, 3520);
	public static final Boundary HUNTER_LOVAK = new Boundary(1468, 3840, 1511, 3890);
	public static final Boundary HUNTER_DONATOR = new Boundary(2124, 4917, 2157, 4946);
	public static final Boundary HUNTER_LZ_DONATOR = new Boundary(2856, 5069, 2868, 5081);
	public static final Boundary HUNTER_WILDERNESS = new Boundary(3128, 3755, 3172, 3796);
	public static final Boundary PURO_PURO = new Boundary(2561, 4289, 2623, 4351);
	public static final Boundary[] HUNTER_BOUNDARIES = {HUNTER_JUNGLE, HUNTER_WILDERNESS, HUNTER_LOVAK, HUNTER_DONATOR, HUNTER_LZ_DONATOR};

	public static final Boundary LAVA_DRAGON_ISLE = new Boundary(3174, 3801, 3233, 3855);

	public static final Boundary ABYSSAL_SIRE = new Boundary(2942, 4735, 3136, 4863);

	public static final Boundary COMBAT_DUMMY = new Boundary(2846, 2960, 2848, 2962);

	public static final Boundary SAFEPKSAFE = new Boundary(3068, 3516, 3109, 3536);
	/**
	 * Raids bosses
	 */

	public static final Boundary DZ_BOSS = new Boundary(3722, 2817, 3798, 2833);


	public static final Boundary ALTAR = new Boundary(3223, 3603, 3255, 3633);
	public static final Boundary FORTRESS = new Boundary(2993, 3615, 3024, 3648);
	public static final Boundary DEMONIC = new Boundary(3236, 3852, 3275, 3884);
	public static final Boundary ROGUES = new Boundary(3293, 3919, 3320, 3950);
	public static final Boundary DRAGONS = new Boundary(3293, 3655, 3320, 3682);
	public static final Boundary[] PURSUIT_AREAS = {ALTAR, FORTRESS, DEMONIC, ROGUES, DRAGONS};

	/**
	 * Kalphite Queen
	 */
	public static final Boundary KALPHITE_QUEEN = new Boundary(3457, 9472, 3514, 9527);

	public static final Boundary CATACOMBS = new Boundary(1590, 9980, 1731, 10115);


	public static final Boundary CLAN_WARS = new Boundary(3272, 4759, 3380, 4852);
	public static final Boundary CLAN_WARS_SAFE = new Boundary(3263, 4735, 3390, 4761);
	/**
	 * Cerberus spawns
	 */
	public static final Boundary CERBERUS_ROOM_WEST = new Boundary(1224, 1225, 1259, 1274);
	public static final Boundary CERBERUS_ROOM_NORTH = new Boundary(1290, 1288, 1323, 1338);
	public static final Boundary CERBERUS_ROOM_EAST = new Boundary(1354, 1224, 1387, 1274);
	public static final Boundary CERB_BOUNDARY2 = new Boundary(1234, 1246, 1246, 1256);
	public static final Boundary[] CERBERUS_BOSSROOMS = {CERBERUS_ROOM_NORTH, CERBERUS_ROOM_WEST, CERBERUS_ROOM_EAST};


	public static final Boundary SKOTIZO_BOSSROOM = new Boundary(1678, 9870, 1714, 9905);

	public static final Boundary GODWARS_MAIN = new Boundary(2847, 5346, 2912, 5280);
	public static final Boundary GODWARS_AREA = new Boundary(2815, 5245, 2954, 5377);

	public static final Boundary GODWARS_ARMADYL_ROOM = new Boundary(2847, 5346, 2912, 5280);
	public static final Boundary GODWARS_BANDOS_ROOM = new Boundary(2819, 5372, 2860, 5311);
	public static final Boundary GODWARS_SARA_ROOM = new Boundary(2910, 5308, 2944, 5252);
	public static final Boundary GODWARS_ZAMMY_ROOM = new Boundary(2880, 5371, 2940, 5335);

	public static final Boundary[] GODWARS_OTHER_ROOMS = {GODWARS_MAIN, GODWARS_ARMADYL_ROOM, GODWARS_BANDOS_ROOM, GODWARS_SARA_ROOM, GODWARS_ZAMMY_ROOM};

	public static final Boundary BANDOS_GODWARS = new Boundary(2864, 5351, 2876, 5369);
	public static final Boundary ARMADYL_GODWARS = new Boundary(2824, 5296, 2842, 5308);
	public static final Boundary ZAMORAK_GODWARS = new Boundary(2918, 5318, 2936, 5331);
	public static final Boundary SARADOMIN_GODWARS = new Boundary(2889, 5258, 2907, 5276);
	public static final Boundary[] GODWARS_BOSSROOMS = {BANDOS_GODWARS, ARMADYL_GODWARS, ZAMORAK_GODWARS, SARADOMIN_GODWARS};

	public static final Boundary JORMUNGANDS_PRISON = new Boundary(2383, 10360, 2506, 10477);

	public static final Boundary KARUULM_DUNGEON = new Boundary(1217, 10126, 1415, 10301);

	public static final Boundary DEATH_PLATEAU = new Boundary(2841, 3576, 2886, 3606);

	public static final Boundary BRINE_RAT_CAVERN = new Boundary(2685, 10111, 2748, 10153);


	public static final Boundary SMOKE_DEVIL_BOUNDARY = new Boundary(2338, 9409, 2433, 9472);
	public static final Boundary LITHKREN_VAULT = new Boundary(1530, 5050, 1608, 5124);
	public static final Boundary ANCIENT_CAVERN = new Boundary(1595, 5311, 1798, 5380);
	public static final Boundary STRONGHOLD_CAVE = new Boundary(2373, 9760, 2501, 9842);

	public static final Boundary OBOR_AREA = new Boundary(3072, 9792, 3110, 9818);

	public static final Boundary BRYOPHYTA_ROOM = new Boundary(3198, 9918, 3239, 9945);

	public static final Boundary WATERBIRTH_CAVES_1 = new Boundary(2424, 10097, 2569, 10185);
	public static final Boundary WATERBIRTH_CAVES_2 = new Boundary(1778, 4316, 2047, 4420);
	public static final Boundary WATERBIRTH_BOSS_CAVE = new Boundary(2876, 4354, 2942, 4475);
	public static final Boundary[] WATERBIRTH_CAVES = {WATERBIRTH_CAVES_1, WATERBIRTH_CAVES_2, WATERBIRTH_BOSS_CAVE };


	public static final Boundary FIGHT_ROOM = new Boundary(1671, 4690, 1695, 4715);
	public static final Boundary[] SAFE_ZONE_BLACK_KNIGHTS_FORTRESS = {
			new Boundary(3002, 3535, 3026, 3539),
			new Boundary(3005, 3540, 3025, 3542),
			new Boundary(3006, 3543, 3023, 3543),
			new Boundary(2997, 3525, 3032, 3528),
			new Boundary(2994, 3529, 3028, 3534)
	};
	public static final Boundary WILDERNESS = new Boundary(2941, 3525, 3392, 3968);
	public static final Boundary WILDERNESS_UNDERGROUND = new Boundary(2941, 9918, 3392, 10366);
	public static final Boundary[] WILDERNESS_PARAMETERS = {WILDERNESS, WILDERNESS_UNDERGROUND, WILDERNESS_GOD_WARS_BOUNDARY, REV_CAVE};
	public static final Boundary[] DEEP_WILDY_CAVES = {WILDERNESS_UNDERGROUND, WILDERNESS_GOD_WARS_BOUNDARY, REV_CAVE};


	//public static final Boundary WILDERNESS = new Boundary(2941, 3525, 3392, 3968); // (2941, 3525, 3392, 3968);
//public static final Boundary WILDERNESS = new Boundary(1486, 1444, 3767, 3730);
	//public static final Boundary ZEAH_WILDERNESS = new Boundary(1420, 3730, 1600, 4060);
	//public static final Boundary WILDERNESS_UNDERGROUND = new Boundary(2941, 9918, 3392, 10366);
	//public static final Boundary[] WILDERNESS_PARAMETERS = { WILDERNESS, ZEAH_WILDERNESS, WILDERNESS_UNDERGROUND, WILDERNESS_GOD_WARS_BOUNDARY };

	/*
	 * Hydra Dungeon
	 */
	public static final Boundary HYDRA_DUNGEON = new Boundary(1297, 10215, 1397, 10283);
	public static final Boundary HYDRA_DUNGEON2 = new Boundary(1248, 10144, 1302, 10210);
	public static final Boundary HYDRA_BOSS_ROOM = new Boundary(1355, 10253, 1380, 10281);
	public static final Boundary[] HYDRA_ROOMS = {HYDRA_DUNGEON, HYDRA_DUNGEON2, HYDRA_BOSS_ROOM};

	public static final Boundary HESPORI = new Boundary(3025, 3475, 3068, 3508);
	public static final Boundary HESPORI_EXIT = new Boundary(3066, 3497, 3067, 3501);
	public static final Boundary HESPORI_ENTRANCE = new Boundary(3069, 3497,3072, 3501);
	public static final Boundary INFERNO = new Boundary(2256, 5328, 2286, 5359);




	public static final Boundary EDGE_BANK = new Boundary(3091, 3488, 3098, 3499);
	public static final Boundary EDGE_TRADING_AREA = new Boundary(3085, 3478, 3109, 3504);
	public static final Boundary EDGE_NORTH_BUILDING = new Boundary(3088, 3502, 3108, 3520);
	public static final Boundary EDGE_DUNG_LADDER = new Boundary(3083, 9966, 3098, 9978);
	public static final Boundary EDGE_DUNG_ENTRANCE_LADDER = new Boundary(3092, 9863, 3102, 9870);
	public static final Boundary FOE_DUNGEON = new Boundary(2545, 9947, 2556, 9956);

	public static final Boundary PRESET_SKILLING = new Boundary(3807, 3551, 3108, 3520);
	public static final Boundary PRESET_HOME_MAIN_BANK = new Boundary(3088, 3481, 3097, 3484);
	public static final Boundary PRESET_HOME_TRADING_POST = new Boundary(3093, 3491, 3100, 3498);
	public static final Boundary PRESET_GAMBLING_BANK = new Boundary(3118, 3504, 3121, 3509);

	public static final Boundary[] ALLOWED_PRESET_LOAD_AREAS = {EDGE_NORTH_BUILDING, PRESET_SKILLING, PRESET_HOME_MAIN_BANK,PRESET_HOME_TRADING_POST,PRESET_GAMBLING_BANK};

	public static final Boundary WILDY_CHAOS_HUT = new Boundary(2942, 3804, 2964, 3831);
	public static final Boundary WILDY_CHAOS_INSIDE_HUT= new Boundary(2948, 3819, 2957, 3824);
	public static final Boundary WILDY_CHAOS_INSIDE_HUT2= new Boundary(2949, 3817, 2952, 3824);
	public static final Boundary ICE_PATH = new Boundary(2837, 3786, 2870, 3821);
	public static final Boundary ICE_PATH_TOP = new Boundary(2822, 3806, 2837, 3813);
	public static final Boundary SEERS = new Boundary(2689, 3455, 2734, 3500);
	public static final Boundary VARROCK = new Boundary(3178, 3390, 3243, 3423);
	public static final Boundary ARDOUGNE = new Boundary(2644, 3288, 2678, 3323);
	public static final Boundary[] ROOFTOP_COURSES = {SEERS_BOUNDARY, VARROCK_BOUNDARY, ARDOUGNE_BOUNDARY};
	public static final Boundary ONYX_ZONE = new Boundary(2778, 4842, 2788, 4851);
	public static final Boundary LEGENDARY_ZONE = new Boundary(2842, 5091, 2853, 5103);
	public static final Boundary DONATOR_ZONE = new Boundary(3805, 2839, 3822, 2849);
	public static final Boundary DONATOR_ZONE_BOSS = new Boundary(3781, 2820, 3793, 2831);
	public static final Boundary REGULAR_DZ_HUNTER = new Boundary(3821, 2847, 3837, 2867);
	public static final Boundary SKILLING_ISLAND_HUNTER = new Boundary(3814, 3543, 3829, 3555);
	public static final Boundary HUNTER_AREA = new Boundary(3531, 3974, 3598, 4036);

	public static final Boundary CORPOREAL_BEAST_LAIR = new Boundary(2972, 4370, 2999, 4397);
	public static final Boundary DAGANNOTH_KINGS = new Boundary(2891, 4428, 2934, 4469);
	public static final Boundary SCORPIA_LAIR = new Boundary(3216, 10329, 3248, 10354);
	public static final Boundary KRAKEN_CAVE = new Boundary(2240, 9984, 2303, 10047);
	public static final Boundary KRAKEN_BOSS_ROOM = new Boundary(2268, 10022, 2294, 10046);
	public static final Boundary DAGANNOTH_MOTHER_HFTD = new Boundary(2501, 4630, 2553, 4678);
	public static final Boundary MONKEY_MADNESS_DEMON = new Boundary(2643, 4546, 2687, 4605);
	public static final Boundary RFD = new Boundary(1888, 5344, 1911, 5367);
	public static final Boundary RESOURCE_AREA = new Boundary(3174, 3924, 3196, 3944);
	public static final Boundary KBD_AREA = new Boundary(2251, 4675, 2296, 4719);
	public static final Boundary PEST_CONTROL_AREA = new Boundary(2622, 2554, 2683, 2675);
	public static final Boundary FIGHT_CAVE = new Boundary(2365, 5052, 2429, 5119);
	public static final Boundary HUNLLEF_CAVE = new Boundary(1161, 9924, 1183, 9946);
	public static final Boundary EDGEVILLE_PERIMETER = new Boundary(3072, 3451, 3138, 3520);
	public static final Boundary WILDY_BONE_ALTAR = new Boundary(2945, 3813, 2960, 3827);
	public static final Boundary[] DUEL_ARENA = {new Boundary(3332, 3244, 3359, 3259), new Boundary(3364, 3244, 3389, 3259)};
	public static final Boundary EMPTY = new Boundary(0, 0, 0, 0);

	/*
	 * Minigame Lobbys
	 */
	public static final Boundary LOBBY = new Boundary(3010, 9921, 3070, 9982);
	//South Side
	public static final Boundary RAIDS_LOBBY = new Boundary(3020, 6035, 3049, 6062);
	public static final Boundary RAIDS_LOBBY_ENTRANCE = new Boundary(3027, 6065, 3040, 6072);
	public static final Boundary XERIC_LOBBY = new Boundary(3032, 9924, 3047, 9942);
	public static final Boundary XERIC_LOBBY_ENTRANCE = new Boundary(3038, 9944, 3041, 9951);

	//north side
	public static final Boundary THEATRE_LOBBY = new Boundary(3052, 9961, 3067, 9979);
	public static final Boundary THEATRE_LOBBY_ENTRANCE = new Boundary(3058, 9952, 3061, 9960);
	public static final Boundary TOURNY_LOBBY = new Boundary(3032, 9961, 3047, 9979);
	public static final Boundary TOB_VERZIK = new Boundary(3150, 4293, 3184, 4329);

	/**
	 * Raids bosses
	 */
	public static final Boundary RAID_MAIN = new Boundary(3295, 5152, 3359, 5407, 0);
	public static final Boundary RAID_F1 = new Boundary(3295, 5152, 3359, 5407, 1);
	public static final Boundary RAID_F2 = new Boundary(3295, 5152, 3359, 5407, 2);
	public static final Boundary RAID_F3 = new Boundary(3295, 5152, 3359, 5407, 3);

	public static final Boundary OLM = new Boundary(3197, 5708, 3270, 5780);
	public static final Boundary RAIDS = new Boundary(3212, 5119, 3367, 5763);
	public static final Boundary TEKTON = new Boundary(3296, 5281, 3327, 5310);
	public static final Boundary TEKTON_ATTACK_BOUNDARY = new Boundary(3299, 5285, 3321, 5301);
	public static final Boundary SHAMAN_BOUNDARY = new Boundary(3305, 5257, 3320, 5269);
	public static final Boundary SKELETAL_MYSTICS = new Boundary(3298, 5249, 3325, 5275);
	public static final Boundary ICE_DEMON = new Boundary(3297, 5343, 3325, 5374);
	public static final Boundary[] RAIDROOMS = {OLM, RAIDS, RAID_MAIN, RAID_F1, RAID_F2, RAID_F3, TEKTON, TEKTON_ATTACK_BOUNDARY, SKELETAL_MYSTICS, ICE_DEMON};
	public static final Boundary FULL_RAIDS = new Boundary(3204, 5118, 3379, 5766);
	public static final Boundary XERIC = new Boundary(2685, 5440, 2743, 5495);

	public static final Boundary EDGEVILLE_EXTENDED = new Boundary(2944, 3436, 3207, 3549);
	public static final Boundary GODWARS_MAIN_AREA = new Boundary(2811, 5247, 2950, 5347);
	public static final Boundary SWAMP_AREA = new Boundary(3395, 3163, 3853, 3595);
	public static final Boundary WARRIORS_GUILD = new Boundary(2833, 3531, 2878, 3558);
	public static final Boundary ZULRAH = new Boundary(2248, 3059, 2283, 3084);
	public static final Boundary DEMONIC_GORILLA = new Boundary(2124, 5660, 2174, 5696);
	public static final Boundary THERMONUCLEARS = new Boundary(2399, 9434, 2439, 9474);
	public static final Boundary KBD = new Boundary(2271, 4680, 2296, 4718);
	public static final Boundary WATERBIRTH_DUNGEON = new Boundary(2442, 10147, 2562, 10181);
	public static final Boundary MITHRIL_DRAGONS = new Boundary(1603, 5309, 1803, 5381);
	public static final Boundary DAGGANOTH_MOTHER = new Boundary(2515, 4632, 2545, 4667);
	public static final Boundary DAGGANOTH_KINGS = new Boundary(2882, 4357, 2946, 4476);
	public static final Boundary CANNON_JAD = new Boundary(2365, 5053, 2558, 5187);
	public static final Boundary LZ_CAVE = new Boundary(2363, 10237, 2429, 10303);
	public static final Boundary CANNON_FREMNIK_DUNGEON = new Boundary(2685, 9945, 2815, 10048);
	public static final Boundary LIZARDMAN_CANYON = new Boundary(1468, 3674, 1567, 3709);
	public static final Boundary VORKATH = new Boundary(2257, 4052, 2288, 4079);
	public static final Boundary FORTHOS_DUNGEON = new Boundary(1781, 9875, 1866, 9994);

	public static final Boundary OURIANA_ALTAR = new Boundary(2995, 5559, 3092, 5645);
	public static final Boundary OURIANA_ALTAR_BANK = new Boundary(3010, 5618, 3026, 5630);

	public static final Boundary GROUP_IRONMAN_FORMING = new Boundary(new Position(3059, 3032, 0), new Position(3179, 3134, 0));

	/**
	 * Guilds and Specialty
	 */

	public static final Boundary GRAND_TREE_1 = new Boundary(new Position(2461, 3491, 1), new Position(2470, 3500, 1));
	public static final Boundary GRAND_TREE_2 = new Boundary(new Position(2461, 3491, 2), new Position(2470, 3500, 2));


	public static Location centerAsLocation(Boundary boundary) {
		int x = (boundary.minX + boundary.highX) / 2;
		int y = (boundary.minY + boundary.highY) / 2;
		if (boundary.height >= 0) {
			return new Location(x, y, boundary.height);
		} else {
			return new Location(x, y, 0);
		}
	}

	public Location getMinLocation() {
		return new Location(minX, minY);
	}
}