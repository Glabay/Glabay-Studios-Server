package io.xeros.content.skills.thieving;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.ArdougneDiaryEntry;
import io.xeros.content.achievement_diary.impl.DesertDiaryEntry;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.content.achievement_diary.impl.WesternDiaryEntry;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.skills.Skill;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;
import org.apache.commons.lang3.RandomUtils;

/**
 * A representation of the thieving skill. Support for both object and npc actions will be supported.
 * 
 * @author Jason MacKeigan
 * @date Feb 15, 2015, 7:12:14 PM
 */
public class Thieving {
	
	private static final int[] rogueOutfit = { 5553, 5554, 5555, 5556, 5557 };

	/**
	 * The managing player of this class
	 */
	private final Player player;

	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	private long lastInteraction;

	/**
	 * The constant delay that is required inbetween interactions
	 */
	private static final long INTERACTION_DELAY = 1_500L;

	/**
	 * The stealing animation
	 */
	private static final int ANIMATION = 881;

	/**
	 * Constructs a new {@link Thieving} object that manages interactions between players and stalls, as well as players and non playable characters.
	 * 
	 * @param player the visible player of this class
	 */
	public Thieving(final Player player) {
		this.player = player;
	}

	/**
	 * A method for stealing from a stall
	 * 
	 * @param stall the stall being stolen from
	 * @param location the location of the stall
	 */
	public void steal(Stall stall, Location3D location) {
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			//player.sendMessage("You must wait a few more seconds before you can steal again.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You need at least one free slot to steal from this.");
			return;
		}

		if (player.playerLevel[Skill.THIEVING.getId()] < stall.level) {
			player.sendMessage("You need a thieving level of " + stall.level + " to steal from this.");
			return;
		}
		if (Misc.random(200) == 0 && player.getInterfaceEvent().isExecutable()) {
			player.getInterfaceEvent().execute();
			return;
		}
		player.getEventCalendar().progress(EventChallenge.THIEVE_X_STALLS);
		switch (stall) {
		case Food:
			player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.TEA_STALL);
			break;
		case Crafting:
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
				player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.STEAL_CAKE);
			}
			break;
		case Magic:
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
				player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.STEAL_GEM_ARD);
			}
			if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
				player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.STEAL_GEM_FAL);
			}
			break;
		case Scimitar:
			break;
		case Fur:
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
				player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.STEAL_FUR);
			}
			break;
		case Gold:
		default:
			break;
		}
		player.facePosition(location.getX(), location.getY());
/**		if (Misc.random(stall.depletionProbability) == 0) {
			GlobalObject stallObj = Server.getGlobalObjects().get(objectId, location.getX(), location.getY(), location.getZ());
			if (stallObj != null) {
				Server.getGlobalObjects().add(new GlobalObject(4797, location.getX(), location.getY(), location.getZ(), stallObj.getFace(), 10, 8, stallObj.getObjectId()));
			}
		}
 */
		GameItem item = stall.item;
		ItemDef definition = ItemDef.forId(item.id());
		int petRate = player.skillingPetRateScroll ? (int) (stall.petChance * .75) : stall.petChance;
		if (Misc.random(petRate) == 20 && player.getItems().getItemCount(20663, false) == 0 && player.petSummonId != 20663) {
			 PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + "</col> now goes hand in hand with a <col=CC0000>Rocky</col> pet!");
			 player.getItems().addItemUnderAnyCircumstance(20663, 1);
			 player.getCollectionLog().handleDrop(player, 5, 20663, 1);
		 }

		player.startAnimation(ANIMATION);
		player.getItems().addItem(item.id(), item.amount());
		player.getPA().addSkillXPMultiplied((int) (stall.experience * (1 + (getRoguesPieces() * 0.12))), Skill.THIEVING.getId(), true);
		player.sendMessage("You steal a " + definition.getName() + " from the stall.");
		Achievements.increase(player, AchievementType.THIEV, 1);
		lastInteraction = System.currentTimeMillis();
	}

	public int getRoguesPieces() {
		int pieces = 0;
		for (int aRogueOutfit : rogueOutfit) {
			if (player.getItems().isWearingItem(aRogueOutfit)) {
				pieces++;
			}
		}
		return pieces;
	}

	/**
	 * A method for pick pocketing npc's
	 * 
	 * @param pickpocket the pickpocket type
	 * @param npc the npc being pick pocketed
	 */
	public void steal(Pickpocket pickpocket, NPC npc) {
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			//player.sendMessage("You must wait a few more seconds before you can steal again.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You need at least one free slot to steal from this npc.");
			return;
		}
		if (player.playerLevel[Skill.THIEVING.getId()] < pickpocket.level) {
			player.sendMessage("You need a thieving level of " + pickpocket.level + " to steal from this npc.");
			return;
		}
		if (Misc.random(200) == 0 && player.getInterfaceEvent().isExecutable()) {
			player.getInterfaceEvent().execute();
			return;
		}
		/**
		 * Incorporate chance for failure
		 */
		switch (pickpocket) {
		case FARMER:
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
				player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PICKPOCKET_ARD);
			}
			if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
				player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICKPOCKET_MASTER_FARMER_FAL);
			}
			if (Boundary.isIn(player, Boundary.DRAYNOR_BOUNDARY)) {
				player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.PICKPOCKET_FARMER_DRAY);
			}
			break;
		case MAN:
			if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
				player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICKPOCKET_MAN);
			}
			if (Boundary.isIn(player, Boundary.LUMRIDGE_BOUNDARY)) {
				player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.PICKPOCKET_MAN_LUM);
			}
			break;
		case GNOME:
			player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.PICKPOCKET_GNOME);
			break;
		case HERO:
			player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PICKPOCKET_HERO);
			break;
		case MENAPHITE_THUG:
			player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.PICKPOCKET_THUG);
			break;
		default:
			break;
		
		}

		player.facePosition(npc.getX(), npc.getY());
		player.startAnimation(ANIMATION);
		GameItem item = pickpocket.getRandomItem();
		boolean maxCape = SkillcapePerks.THIEVING.isWearing(player) || SkillcapePerks.isWearingMaxCape(player);
		if (item != null) {
			player.getItems().addItem(item.id(), maxCape ? item.amount()*2 : item.amount());
		} else {
			player.sendMessage("You were unable to find anything useful.");
		}
		int petRate = player.skillingPetRateScroll ? (int) (pickpocket.petChance * .75) : pickpocket.petChance;
		if (Misc.random(petRate) == 20 && player.getItems().getItemCount(20663, false) == 0 && player.petSummonId != 20663) {
			 PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + player.getDisplayName() + "</col> now goes hand in hand with a <col=CC0000>Rocky</col> pet!");
			 player.getItems().addItemUnderAnyCircumstance(20663, 1);
			 player.getCollectionLog().handleDrop(player, 5, 20663, 1);
		 }
		Achievements.increase(player, AchievementType.THIEV, 1);
		player.getPA().addSkillXPMultiplied((int) (pickpocket.experience * (1 + (getRoguesPieces() * 0.12))), Skill.THIEVING.getId(), true);
		lastInteraction = System.currentTimeMillis();
	}

	private enum Rarity {
		ALWAYS(0), COMMON(5), UNCOMMON(10), RARE(15), VERY_RARE(25);

		/**
		 * The rarity
		 */
		private final int rarity;

		/**
		 * Creates a new rarity
		 * 
		 * @param rarity the rarity
		 */
		Rarity(int rarity) {
			this.rarity = rarity;
		}
	}

	@SuppressWarnings("serial")
	public enum Pickpocket {
		MAN(1, 8, 20000, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(new GameItem(995, 750), new GameItem(995, 1000), new GameItem(995, 1250)));
			}
		}),

		DRUNKEN_DWARF(75, 90, 20000, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(
						new GameItem(995, 950),
						new GameItem(Items.COAL),
						new GameItem(Items.IRON_ORE),
						new GameItem(Items.BRONZE_BAR),
						new GameItem(Items.SEAWEED),
						new GameItem(Items.BEER),
						new GameItem(Items.BRONZE_PICKAXE),
						new GameItem(Items.RAW_COD)));

				put(Rarity.VERY_RARE, Arrays.asList(
						new GameItem(995, 7850),
						new GameItem(Items.RUNITE_ORE),
						new GameItem(Items.COAL_NOTED, 25),
						new GameItem(Items.RUNITE_BAR),
						new GameItem(Items.RUNE_PICKAXE)));
			}
		}),

		FARMER(60, 80, 20000, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.COMMON, Lists.newArrayList(
						new GameItem(Items.GUAM_SEED, 3), new GameItem(Items.MARRENTILL_SEED, 3),new GameItem(Items.TARROMIN_SEED, 3),
						new GameItem(Items.HARRALANDER_SEED, 3),

						new GameItem(Items.POTATO_SEED, 4),new GameItem(Items.ONION_SEED, 4),new GameItem(Items.CABBAGE_SEED, 4),
						new GameItem(Items.TOMATO_SEED, 4),new GameItem(Items.SWEETCORN_SEED, 4),new GameItem(Items.STRAWBERRY_SEED, 4),
						new GameItem(Items.WATERMELON_SEED, 4),new GameItem(Items.MARIGOLD_SEED, 4),new GameItem(Items.NASTURTIUM_SEED, 4),
						new GameItem(Items.WOAD_SEED, 4),new GameItem(Items.LIMPWURT_SEED, 4)
				));

				put(Rarity.UNCOMMON, Lists.newArrayList(
						new GameItem(Items.RANARR_SEED, 3),new GameItem(Items.TOADFLAX_SEED, 3),
						new GameItem(Items.IRIT_SEED, 3),new GameItem(Items.AVANTOE_SEED, 3),
						new GameItem(Items.KWUARM_SEED, 3),new GameItem(Items.SNAPDRAGON_SEED, 3),
						new GameItem(Items.CADANTINE_SEED, 3),new GameItem(Items.LANTADYME_SEED, 3),
						new GameItem(Items.DWARF_WEED_SEED, 3),new GameItem(Items.TORSTOL_SEED, 3)
				));
			}

		}), MENAPHITE_THUG(65, 75, 18000, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(new GameItem(995, 1000), new GameItem(995, 800), new GameItem(995, 950)));
			} 
		}), GNOME(75, 85, 18000, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(new GameItem(995, 1200), new GameItem(995, 800), new GameItem(995, 1250)));
				put(Rarity.UNCOMMON, Arrays.asList(new GameItem(444), new GameItem(557), new GameItem(13431, 5)));
			}
		}), HERO(80, 100, 17000, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(new GameItem(995, 1500), new GameItem(995, 1800), new GameItem(995, 3500)));
				put(Rarity.UNCOMMON, Arrays.asList(new GameItem(560, 2), new GameItem(565), new GameItem(444), new GameItem(1601)));
			}
		});

		/**
		 * The level required to pickpocket
		 */
		private final int level;

		/**
		 * The experience gained from the pick pocket
		 */
		private final int experience;
		
		/**
		 * The chance of receiving a pet
		 */
		private final int petChance;

		/**
		 * The list of possible items received from the pick pocket
		 */
		private Map<Rarity, List<GameItem>> items = new HashMap<>();

		/**
		 * Creates a new pickpocket level requirement and experience gained
		 * 
		 * @param level the level required to steal from
		 * @param experience the experience gained from stealing
		 */
		Pickpocket(int level, int experience, int petChance, Map<Rarity,List<GameItem>> items) {
			this.level = level;
			this.experience = experience;
			this.petChance = petChance;
			this.items = items;
		}

		GameItem getRandomItem() {
			if (this == DRUNKEN_DWARF && Misc.trueRand(5_000) == 0) {
				return new GameItem(Items.AMMO_MOULD);
			}

			for (Entry<Rarity, List<GameItem>> entry : items.entrySet()) {
				final Rarity rarity = entry.getKey();

				if (rarity == Rarity.ALWAYS) {
					continue;
				}
				final List<GameItem> items = entry.getValue();

				if (items.isEmpty()) {
					continue;
				}

				if (RandomUtils.nextInt(1, rarity.rarity) == 1) {
					return Misc.getItemFromList(items).randomizedAmount();
				}
			}

			List<GameItem> always = items.getOrDefault(Rarity.ALWAYS, Lists.newArrayList());

			if (!always.isEmpty()) {
				return Misc.getItemFromList(always).randomizedAmount();
			}

			return null;
		}
	}

	public enum Stall {
		Crafting(new GameItem(1893), 1, 16, 20, 15000),
		Silk(new GameItem(950), 25, 30, 10, 13000),
		Silver(new GameItem(2961), 50, 54, 10, 12000),
		Fur(new GameItem(6814), 65, 80, 10, 11000),
		Magic(new GameItem(1613), 90, 100, 10, 10000),
		Food(new GameItem(712), 25, 30, 10, 13000),
		General(new GameItem(2961), 50, 54, 10, 12000),
		Scimitar(new GameItem(1993), 90, 100, 10, 10000),
		Spice(new GameItem(2007), 50, 54, 10, 12000),
		Gold(new GameItem(4692), 95, 110, 10, 9000),
		LZ_GOLD(new GameItem(19473), 99, 120, 10, 8500);

		/**
		 * The item received from the stall
		 */
		private final GameItem item;

		/**
		 * The experience gained in thieving from a single stall thieve
		 */
		private final double experience;

		/**
		 * The probability that the stall will deplete
		 */
		private final int depletionProbability;

		/**
		 * The level required to steal from the stall
		 */
		private final int level;
		
		/**
		 * The chance of receiving a pet
		 */
		private final int petChance;

		/**
		 * Constructs a new {@link Stall} object with a single parameter, {@link GameItem} which is the item received when interacted with.
		 * 
		 * @param item the item received upon interaction
		 */
		Stall(GameItem item, int level, int experience, int depletionProbability, int petChance) {
			this.item = item;
			this.level = level;
			this.experience = experience;
			this.depletionProbability = depletionProbability;
			this.petChance = petChance;
		}
	}

}
