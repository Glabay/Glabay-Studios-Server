package io.xeros.model.entity.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.util.Misc;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The rights of a player determines their authority. Every right can be viewed with a name and a value. The value is used to separate each right from one another.
 * 
 * @author Jason MacK
 * @since January 22, 2015, 5:23:49 PM
 */

public enum Right implements Comparator<Right> {
	PLAYER(0, "000000"),
	HELPER(11, "004080"),
	MODERATOR(1, "#0000ff", HELPER),
	ADMINISTRATOR(2, "F5FF0F", MODERATOR),
	OWNER(3, "F5FF0F", ADMINISTRATOR),
	UNKNOWN(4, "F5FF0F"),
	REGULAR_DONATOR(5, "1B1ABC"),
	EXTREME_DONOR(7, "118120", REGULAR_DONATOR),
	LEGENDARY_DONATOR(9, "6D0000", EXTREME_DONOR),
	DIAMOND_CLUB(17, "005C6D", LEGENDARY_DONATOR),
	ONYX_CLUB(18, "4a4a4a", DIAMOND_CLUB),

	HITBOX(12, "437100"),
	IRONMAN(13, "3A3A3A"),
	ULTIMATE_IRONMAN(14, "717070"),
	YOUTUBER(15, "FE0018"),
	GAME_DEVELOPER(16, "544FBB"),
	OSRS(23, "437100"),
	MEMBERSHIP(21, "437100"),
	ROGUE(25, "437100"),
	HC_IRONMAN(10, "60201f"),
	ROGUE_IRONMAN(26, "60201f"),
	ROGUE_HARDCORE_IRONMAN(27, "60201f"),
	GROUP_IRONMAN(28, "60201f"),
	EVENT_MAN(29, "60201f"),

	;

	private static final Logger logger = LoggerFactory.getLogger(Right.class);

	/**
	 * Display groups. You can have one displayed right from the first group,
	 * the second group you can have as many until you reach 2 displayed groups.
	 */
	public static final EnumSet<?>[] DISPLAY_GROUPS = {
			EnumSet.of(HELPER, MODERATOR, ADMINISTRATOR, OWNER, UNKNOWN, REGULAR_DONATOR, EXTREME_DONOR,
					 LEGENDARY_DONATOR, DIAMOND_CLUB, ONYX_CLUB, YOUTUBER),
			EnumSet.of(HITBOX, EVENT_MAN, IRONMAN, ULTIMATE_IRONMAN, GAME_DEVELOPER, OSRS, MEMBERSHIP, HC_IRONMAN, ROGUE,
					ROGUE_HARDCORE_IRONMAN, ROGUE_IRONMAN, GROUP_IRONMAN)
	};

	/**
	 * Donator rights.
	 */
	public static final EnumSet<Right> DONATOR_SET = EnumSet.of(REGULAR_DONATOR, EXTREME_DONOR, LEGENDARY_DONATOR, DIAMOND_CLUB, ONYX_CLUB);

	public static final EnumSet<Right> IRONMAN_SET = EnumSet.of(IRONMAN, ROGUE_HARDCORE_IRONMAN, HC_IRONMAN, ROGUE_IRONMAN, ULTIMATE_IRONMAN, GROUP_IRONMAN);

	/**
	 * An array of {@link Right} objects that represent the order in which some rights should be prioritized over others. The index at which a {@link Right} object exists
	 * determines it's priority. The lower the index the less priority that {@link Right} has over another. The list is ordered from lowest priority to highest priority.
	 * <p>
	 * An example of this would be comparing a {@link #MODERATOR} to a {@link #ADMINISTRATOR}. An {@link #ADMINISTRATOR} can be seen as more 'powerful' when compared to a
	 * {@link #MODERATOR} because they have more power within the community.
	 * </p>
	 */
	public static final Right[] PRIORITY = { PLAYER, OSRS, EVENT_MAN, HC_IRONMAN, GROUP_IRONMAN, IRONMAN, ULTIMATE_IRONMAN, MEMBERSHIP, REGULAR_DONATOR, EXTREME_DONOR, LEGENDARY_DONATOR, DIAMOND_CLUB, ONYX_CLUB,YOUTUBER, HITBOX, HELPER,
			GAME_DEVELOPER, MODERATOR, ADMINISTRATOR, OWNER, UNKNOWN};

	/**
	 * The level of rights that define this
	 */
	private final int right;

	/**
	 * The rights inherited by this right
	 */
	private final List<Right> inherited;

	/**
	 * The color associated with the right
     * -- GETTER --
     *  The color associated with the right
     *

     */
	@Getter
    private final String color;

	/**
	 * Creates a new right with a value to differentiate it between the others
	 * 
	 * @param right the right required
	 * @param color a color thats used to represent the players name when displayed
	 * @param inherited the right or rights inherited with this level of right
	 */
	Right(int right, String color, Right... inherited) {
		this.right = right;
		this.inherited = Arrays.asList(inherited);
		this.color = color;
	}

	public String getFormattedName() {
		return Misc.capitalizeEveryWord(name().toLowerCase().replace("_", " "));
	}

	public Mode getMode() {
        return switch (this) {
            case IRONMAN -> Mode.forType(ModeType.IRON_MAN);
            case ULTIMATE_IRONMAN -> Mode.forType(ModeType.ULTIMATE_IRON_MAN);
            case HC_IRONMAN -> Mode.forType(ModeType.HC_IRON_MAN);
            case ROGUE_HARDCORE_IRONMAN -> Mode.forType(ModeType.ROGUE_HARDCORE_IRONMAN);
            case ROGUE_IRONMAN -> Mode.forType(ModeType.ROGUE_IRONMAN);
            case OSRS -> Mode.forType(ModeType.OSRS);
            case ROGUE -> Mode.forType(ModeType.ROGUE);
            case GROUP_IRONMAN -> Mode.forType(ModeType.GROUP_IRONMAN);
            default -> Mode.forType(ModeType.STANDARD);
        };

    }

	/**
	 * The rights of this enumeration
	 * 
	 * @return the rights
	 */
	public int getValue() {
		return right;
	}

	/**
	 * Returns a {@link Right} object for the value.
	 * 
	 * @param value the right level
	 * @return the rights object
	 */
	public static Right get(int value) {
		return RIGHTS.stream().filter(element -> element.right == value).findFirst().orElse(PLAYER);
	}

	/**
	 * A {@link Set} of all {@link Right} elements that cannot be directly modified.
	 */
	private static final Set<Right> RIGHTS = Collections.unmodifiableSet(EnumSet.allOf(Right.class));

    /**
	 * Determines if this level of rights inherited another level of rights
	 * 
	 * @param right the level of rights we're looking to determine is inherited
	 * @return {@code true} if the rights are inherited, otherwise {@code false}
	 */
	public boolean isOrInherits(Right right) {
		/*if (this == right) 
			return true;
		for (int i = 0; i < inherited.size(); i++) {
			if (this == inherited.get(i))
				return true;
		}
		System.out.println("inherited.size: "+inherited.size());
		return false;*/
		return this == right || !inherited.isEmpty() && inherited.stream().anyMatch(r -> r.isOrInherits(right));
	}
	
	/**
	 * Determines if the players rights equal that of {@link Right#MODERATOR}
	 * @return	true if they are of type moderator
	 */
	public boolean isModerator() {
		return equals(MODERATOR);
	}
	
	/**
	 * Determines if the players rights equal that of {@link Right#HELPER}
	 * @return	true if they are of type moderator
	 */
	public boolean isHelper() {
		return equals(HELPER);
	}
	
	/**
	 * Determines if the players rights equal that of {@link Right#ADMINISTRATOR}
	 * @return	true if they are of type administrator
	 */
	public boolean isAdministrator() {
		return equals(ADMINISTRATOR);
	}
	
	/**
	 * Determines if the players rights equal that of {@link Right#OWNER}
	 * @return	true if they are of type owner
	 */
	public boolean isOwner() {
		return equals(OWNER);
	}

	/**
	 * Determines if the players right equal that of {@link Right#MODERATOR}, {@link Right#ADMINISTRATOR},
	 * and {@link Right#OWNER}
	 * @return	true if they are any of the predefined types
	 */
	public boolean isStaff() {
		return isHelper() || isModerator() || isAdministrator() || isOwner();
	}

	public boolean isDonator() {
		return false;
	}


	public boolean isManagment() {
		return isAdministrator() || isOwner();
	}

	@Override
	public String toString() {
		return Misc.capitalizeJustFirst(name().replaceAll("_", " "));
	}

	@Override
	public int compare(Right arg0, Right arg1) {
		return 0;
	}

}
