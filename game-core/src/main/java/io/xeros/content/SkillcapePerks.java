package io.xeros.content;

import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public enum SkillcapePerks {
	ATTACK( 9747, 9748, 33033),
	DEFENCE(9753, 9754, 33034),
	STRENGTH( 9750, 9751, 33035),
	HITPOINTS( 9768, 9769, 33036),
	RANGING( 9756, 9757, 33037),
	PRAYER( 9759, 9760, 33038),
	MAGIC( 9762, 9763, 33039),
	COOKING( 9801, 9802, 33040),
	WOODCUTTING( 9807, 9808, 33041),
	FLETCHING( 9783, 9784, 33042),
	FISHING( 9798, 9799, 33043),
	FIREMAKING( 9804, 9805, 33044),
	CRAFTING( 9780, 9781, 33045),
	SMITHING( 9795, 9796, 33046),
	MINING( 9792, 9793, 33047),
	HERBLORE( 9774, 9775, 33048),
	AGILITY( 9771, 9772, 33049),
	THIEVING( 9777, 9778, 33050),
	SLAYER( 9786, 9787, 33051),
	FARMING( 9810, 9811, 33052),
	RUNECRAFTING( 9765, 9766, 33053),
	HUNTER(9948, 9949, 330354),
	MAX_CAPE( 13280),
	ARDOUGNE_MAX_CAPE( 20760),
	FIRE_MAX_CAPE( 13329),
	AVAS_MAX_CAPE( 13337),
	SARADOMIN_MAX_CAPE( 13331),
	ZAMORAK_MAX_CAPE( 13333),
	GUTHIX_MAX_CAPE( 13335),
	IMBUED_SARADOMIN_MAX_CAPE( 21776),
	IMBUED_ZAMORAK_MAX_CAPE( 21780),
	IMBUED_GUTHIX_MAX_CAPE( 21784),
	ASSEMBLER_MAX_CAPE(21898),
	INFERNAL_MAX_CAPE(21285),

	;

	public static final int MAX_CAPE_ID = 13280;
	public static final int MAX_CAPE_HOOD = 13281;

	private final int[] skillcapes;

	public static final EnumSet<SkillcapePerks> SKILL_CAPES = EnumSet.of(
			ATTACK, DEFENCE, STRENGTH, HITPOINTS, RANGING, PRAYER, MAGIC, COOKING, WOODCUTTING,
			FLETCHING, FISHING, FIREMAKING, CRAFTING, SMITHING, MINING, HERBLORE, AGILITY, THIEVING,
			SLAYER, FARMING, RUNECRAFTING, HUNTER
	);

	SkillcapePerks(int... skillcapes) {
		this.skillcapes = skillcapes;
	}

	/**
	 * Allows us to check wether or not a player is wearing one of the capes
     */
	public boolean isWearing(Player player) {
		return Arrays.stream(skillcapes).anyMatch(it -> player.getItems().isWearingItem(it, Player.playerCape));
	}

	/**
	 * Contains skill capes only.
	 */
	public static List<Integer> getAllSkillCapeIds() {
		List<Integer> list = new ArrayList<>();
		SKILL_CAPES.forEach(it -> Arrays.stream(it.skillcapes).filter(item -> item < 30_000).forEach(list::add));
		return list;
	}

	/**
	 * Contains 200m capes only.
	 */
	public static List<Integer> getAll200mCapeIds() {
		List<Integer> list = new ArrayList<>();
		SKILL_CAPES.forEach(it -> Arrays.stream(it.skillcapes).filter(item -> item > 30_000).forEach(list::add));
		return list;
	}

	public static boolean isWearingMaxCape(Player player) {
		return player.getItems().isWearingAnyItem(Items.COMPLETIONIST_CAPE) ||
				MAX_CAPE.isWearing(player) ||
				FIRE_MAX_CAPE.isWearing(player) ||
				AVAS_MAX_CAPE.isWearing(player) ||
				SARADOMIN_MAX_CAPE.isWearing(player) ||
				ZAMORAK_MAX_CAPE.isWearing(player) ||
				ARDOUGNE_MAX_CAPE.isWearing(player) ||
				GUTHIX_MAX_CAPE.isWearing(player) ||
				INFERNAL_MAX_CAPE.isWearing(player) ||
				ASSEMBLER_MAX_CAPE.isWearing(player);
	}

}
