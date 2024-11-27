package io.xeros.content.titles;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

import io.xeros.content.achievement.AchievementTier;
import io.xeros.content.achievement.Achievements;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Each element of the enum represents a singular title with an array of qualities.
 * <p>
 * <b> Please note that by default the description is wrapped to fit into the open area so there is no need to use escape characters in the description. </b>
 * </p>
 * 
 * @author Jason MacKeigan
 * @date Jan 22, 2015, 3:44:52 PM
 */
public enum Title implements Comparator<Title> {
	NONE("None", 0, TitleCurrency.NONE, Titles.NO_REQUIREMENT, "No title will be displayed."),

	CUSTOM("Custom", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.LEGENDARY_DONATOR) || player.getRights().isOrInherits(Right.DIAMOND_CLUB) || player.getRights().isOrInherits(Right.ONYX_CLUB) || player.getRights().isOrInherits(Right.MODERATOR);
		}
	}, "Have the option of choosing your own 16-character title. You must be a legendary donator or more to display this title."),
	
	REGULAR_DONATOR("Donator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.REGULAR_DONATOR);
		}
	}, "This title is for Donators. You must be a Donator to purchase and display this title."),


	EXTREME_DONOR("Extreme", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.EXTREME_DONOR);
		}
	}, "This title is for a EXTREME DONOR. You must be a EXTREME DONOR to purchase and display this title."),


	DIAMOND_CLUB("Diamond Club", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.DIAMOND_CLUB);
		}
	}, "This title is for DIAMOND CLUB only. You must be in the ONYX CLUB to purchase and display this title."),
	
	ONYX_CLUB("Onyx Club", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.ONYX_CLUB);
		}
	}, "This title is for ONYX CLUB only. You must be in the ONYX CLUB to purchase and display this title."),


	HELPER("Helper", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.HELPER);
		}
	}, "This title represents the helper rank. You must be a helper to purchase or display this title."),

	MODERATOR("Moderator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.MODERATOR);
		}
	}, "A unique and powerful title that when displayed represents the high level of power the owner has." + "Only moderators on the staff team may display this."),

	ADMINISTRATOR("Administrator", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.ADMINISTRATOR);
		}
	}, "A unique and powerful title that when displayed represents the high level of power the owner has." + "Only administrators on the staff team may display this."),

	EXECUTIVE_OFFICER("Executive Officer", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.ADMINISTRATOR);
		}
	}, "The highest ranking title any person may have. This title can only be worn by those who truly deserve it."),

	JUNIOR_CADET("Junior Cadet", 50, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.killcount >= 15;
		}
	}, "A Junior Cadet is a player that has achieved at least fifteen player kills."),

	SENIOR_CADET("Senior Cadet", 75, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.killcount >= 25;
		}
	}, "A Senior Cadet is a player that has achieved at least twenty five kills."),

	SEGEART("Sergeant", 90, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.killcount >= 50;
		}
	}, "A Sergeant is a player that has achieved at least fifty player kills."),

	COMMANDER("Commander", 115, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.killcount >= 100;
		}
	}, "A Commander is a player that has achieved at least one hundred player kills."),

	MAJOR("Major", 120, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.killcount >= 150;
		}
	}, "A Major is a player that has achieved at least one hundred and fifty player kills."),

	CORPORAL("Corporal", 125, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.killcount >= 200;
		}
	}, "A Corporal is a player that has achieved at least two hundred player kills."),

	INSANE("Insane", 145, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.killcount >= 250;
		}
	}, "Insane is a player that has achieved at least two hundred and fifty player kills."),

	WARCHIEF("War-chief", 200, TitleCurrency.PK_TICKETS, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.killcount >= 500;
		}
	}, "A War-chief is a player that has achieved at least five hundred player kills."),

	GIANT_SLAYER("Giant Slayer", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getNpcDeathTracker().getTotal() >= 1000;
		}
	}, "A Boss Expert is a player that has killed a total of 1,000 non playable characters. This can be tracked using your boss tracker info page."),

	FISHERMAN("Fisherman", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievements.Achievement.EXPERT_FISHER.getId());
		}
	}, "A Fisherman is a player that has fished at least 2,500 fish."),

	LUMBERJACK("Lumberjack", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievements.Achievement.EXPERT_CHOPPER.getId());
		}
	}, "A Lumberjack is a player that has cut down trees and has accumulated at least 2,500 logs."),

	MASTER_SLAYER("Master Slayer", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievements.Achievement.SLAYER_EXPERT.getId());
		}
	}, "To receive access to this title, a player must complete at least 150 slayer tasks."),

	GRAVE_DIGGER("Grave Digger", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievements.Achievement.BARROWS_GOD.getId());
		}
	}, "To receive access to this title, a player must complete the barrows minigame at least 500 times."),

	IRON_MAN("Ironman", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getMode().isIronmanType();
		}
	}, "To receive access to this title, a player must be on the ironman game-mode."),
	
	HC_IRON_MAN("HC Ironman", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.HC_IRONMAN);
		}
	}, "To receive access to this title, a player must be on the hc ironman game-mode."),

	ULTIMATE_IRON_MAN("Ult Ironman", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOrInherits(Right.ULTIMATE_IRONMAN);
		}
	}, "To receive access to this title, a player must be on the ultimate ironman game-mode."),

	PVM_FANATIC("PVM Fanatic", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getNpcDeathTracker().getTotal() > 50_000;
		}
	}, "To receive access to this title, a player must have killed at least 50.000 npcs."),

	COLLECTOR("MR Collector", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return PetHandler.ownsAll(player);
		}
	}, "To receive access to this title, a player must have collected ALL boss pets."),

	SKILLER("#Skiller", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.maxedSkiller(player);
		}
	}, "To receive access to this title, a player must have maxed out all skills while having combat stats at 1."),

	DO_YOU_LIFT("#DOuEvenLift", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.maxedCertain(player, 0, 6);
		}
	}, "To receive access to this title, a player must have maxed out all combat skills."),

	ADVENTURER("Top Adventurer", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.diariesCompleted >= 11;
		}
	}, "To receive access to this title, a player must have completed all achievement diaries."),

	MORPH("#CanUSeeMe", 0, TitleCurrency.NONE, new TitleRequirement() {

		@Override
		public boolean meetsStandard(Player player) {
			return player.getItems().playerHasItem(20005) && player.getItems().playerHasItem(20017);
		}
	}, "To receive access to this title, a player must have the ring of coins and ring of nature in their inventory."),

	MILLIONAIRE("#Millionaire", 100_000_000, TitleCurrency.COINS, Titles.NO_REQUIREMENT, "To receive access to this title, a player must be a millionaire. Simple.");

	/**
	 * The name of the title when displayed
	 */
	private final String name;

	/**
	 * The requirement to display or purchase this title
	 */
	private final TitleRequirement requirement;

	/**
	 * The currency used to purchase the title
	 */
	private final TitleCurrency currency;

	/**
	 * The cost to purchase the title
	 */
	private final int cost;

	/**
	 * The description displayed on the interface
	 */
	private final String description;

	/**
	 * Represents a single title.
	 * 
	 * @param name the name of the title
	 * @param color the color of each character
	 * @param currency the currency used to purchase
	 * @param cost the cost, or amount, of currency required to purchase
	 * @param requirement the requirement to purcahse or display
	 */
    Title(String name, int cost, TitleCurrency currency, TitleRequirement requirement, String description) {
		this.name = name;
		this.cost = cost;
		this.currency = currency;
		this.requirement = requirement;
		this.description = description;
	}

	/**
	 * The name of the title
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The currency used to purchse the title
	 * 
	 * @return the currency
	 */
	public TitleCurrency getCurrency() {
		return currency;
	}

	/**
	 * The cost to purchase the title
	 * 
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * The requirement that must be met before purchasing the title
	 * 
	 * @return the requirement
	 */
	public TitleRequirement getRequirement() {
		return requirement;
	}

	/**
	 * The sequence of words that define the title
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Compares the cost value of both titles and returns -1, 0, or +1. This follows the comparable contract.
	 */
	@Override
	public int compare(Title o1, Title o2) {
		if (o1.cost > o2.cost) {
			return 1;
		} else if (o1.cost < o2.cost) {
			return -1;
		}
		return 0;
	}

	/**
	 * A set of elements from the {@link Title} enum. This set is unmodifiable in any regard. This set serves as a convenience.
	 */
	public static final Set<Title> TITLES = Collections.unmodifiableSet(EnumSet.allOf(Title.class));
}
