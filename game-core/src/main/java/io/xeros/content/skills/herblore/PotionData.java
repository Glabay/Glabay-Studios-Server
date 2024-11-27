package io.xeros.content.skills.herblore;

import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.items.GameItem;

public class PotionData {

	/**
	 * A set of data involving unfinished herblore potions
	 * @author Matt - https://www.rune-server.org/members/matt%27/
	 *
	 * @date 23 dec. 2016
	 */
	public enum UnfinishedPotions {
		GUAM_POTION(new GameItem(91), new GameItem(249), 1), //Attack potion
		MARRENTILL_POTION(new GameItem(93), new GameItem(251), 5), //Antipoison
		TARROMIN_POTION(new GameItem(95), new GameItem(253), 12), //Strength potion
		HARRALANDER_POTION(new GameItem(97), new GameItem(255), 22), //Restore potion, Energy potion & Combat potion
		RANARR_POTION(new GameItem(99), new GameItem(257), 30), //Prayer potion
		TOADFLAX_POTION(new GameItem(3002), new GameItem(2998), 34), //Agility potion & Saradomin brew
		IRIT_POTION(new GameItem(101), new GameItem(259), 45), //Super attack & Fishing potion
		AVANTOE_POTION(new GameItem(103), new GameItem(261), 50), //Super energy
		KWUARM_POTION(new GameItem(Items.KWUARM_POTION_UNF), new GameItem(263), 55), //Super strength & Weapon poison
		SNAPDRAGON_POTION(new GameItem(3004), new GameItem(3000), 63), //Super restore
		CADANTINE_POTION(new GameItem(107), new GameItem(265), 66), //Super defence
		LANTADYME(new GameItem(2483), new GameItem(2481), 69), //Antifire, Magic potion
		DWARF_WEED_POTION(new GameItem(109), new GameItem(267), 72), //Ranging potion
		TORSTOL_POTION(new GameItem(111), new GameItem(269), 78); //Zamorak brew


		private final GameItem potion, ingredient;
		private final int levelReq;

		UnfinishedPotions(GameItem potion, GameItem ingredient, int levelReq) {
			this.potion = potion;
			this.ingredient = ingredient;
			this.levelReq = levelReq;
		}

		public GameItem getPotion() {
			return potion;
		}

		public GameItem getHerb() {
			return ingredient;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public static UnfinishedPotions forNotedOrUnNotedHerb(int itemId) {
			ItemDef def = ItemDef.forId(itemId);
			return UnfinishedPotions.forHerb(def.getUnNotedIdIfNoted());
		}

		public static UnfinishedPotions forHerb(int i) {
			for(UnfinishedPotions unf : values()) {
				if (unf.getHerb().id() == i) {
					return unf;
				}
			}
			return null;
		}
	}

	/**
	 * If you add new potions check out {@link io.xeros.content.skills.SkillInterfaces} if you need to
	 * ignore new ones (like duplicate 3/4 dose potions).
	 */
	public enum FinishedPotions {
		AGILITY(new GameItem(3032), 34, 80, new GameItem(3002), new GameItem(2152)),


		ANTI_VENOM_4(new GameItem(12905), 87, 120, new GameItem(Items.ANTIDOTEPLUSPLUS4), new GameItem(Items.ZULRAHS_SCALES, 20)),
		ANTI_VENOM_3(new GameItem(12907), 87, 90, new GameItem(Items.ANTIDOTEPLUSPLUS3), new GameItem(Items.ZULRAHS_SCALES, 15)),
		ANTI_VENOM_2(new GameItem(12909), 87, 60, new GameItem(Items.ANTIDOTEPLUSPLUS2), new GameItem(Items.ZULRAHS_SCALES, 10)),
		ANTI_VENOM_1(new GameItem(12911), 87, 30, new GameItem(Items.ANTIDOTEPLUSPLUS1), new GameItem(Items.ZULRAHS_SCALES, 5)),

		ANTI_VENOM_PLUS(new GameItem(12913), 94, 125, new GameItem(12905), new GameItem(Items.TORSTOL)),

		ANTIDOTE_PLUS(new GameItem(5943), 68, 155, new GameItem(3002), new GameItem(Items.YEW_ROOTS)),
		ANTIDOTE_PLUS_PLUS(new GameItem(5952), 79, 177, new GameItem(101), new GameItem(Items.MAGIC_ROOTS)),
		STAMINA(new GameItem(12625), 77, 152, new GameItem(3016), new GameItem(Items.AMYLASE_CRYSTAL, 3)),
		ANTIFIRE(new GameItem(2452), 69, 157, new GameItem(2483), new GameItem(Items.DRAGON_SCALE_DUST)),
		SUPER_ANTIFIRE(new GameItem(21978), 92, 130, new GameItem(2452), new GameItem(Items.CRUSHED_SUPERIOR_DRAGON_BONES)),

		EXTENDED_ANTIFIRE(new GameItem(11951), 84, 110, new GameItem(2452), new GameItem(11994, 4)),
		EXTENDED_SUPER_ANTIFIRE(new GameItem(22209), 98, 160, new GameItem(21978), new GameItem(11994, 4)),

		ANTIPOISON(new GameItem(2446), 5, 37, new GameItem(93), new GameItem(Items.UNICORN_HORN_DUST)),
		ATTACK(new GameItem(2428), 3, 25, new GameItem(91), new GameItem(Items.EYE_OF_NEWT)),
		COMBAT(new GameItem(9739), 36, 84, new GameItem(97), new GameItem(Items.GOAT_HORN_DUST)),
		DEFENCE(new GameItem(2432), 30, 75, new GameItem(99), new GameItem(Items.WHITE_BERRIES)),
		ENERGY(new GameItem(3008), 26, 67, new GameItem(97), new GameItem(Items.CHOCOLATE_DUST)),
		FISHING(new GameItem(2438), 50, 112, new GameItem(103), new GameItem(Items.SNAPE_GRASS)),
		GUTHIX_BALANCE(new GameItem(7660), 22, 62, new GameItem(99), new GameItem(Items.RED_SPIDERS_EGGS), new GameItem(1550), new GameItem(7650)),
		MAGIC(new GameItem(3040), 76, 172, new GameItem(2483), new GameItem(Items.POTATO_CACTUS)),
		PRAYER(new GameItem(2434), 38, 87, new GameItem(99), new GameItem(Items.SNAPE_GRASS)),
		RANGING(new GameItem(2444), 72, 162, new GameItem(109), new GameItem(Items.WINE_OF_ZAMORAK)),
		RESTORE(new GameItem(2430), 22, 62, new GameItem(97), new GameItem(Items.RED_SPIDERS_EGGS)),
		SARADOMIN_BREW(new GameItem(6685), 81, 180, new GameItem(3002), new GameItem(Items.CRUSHED_NEST)),
		STRENGTH(new GameItem(113), 12, 50, new GameItem(95), new GameItem(Items.LIMPWURT_ROOT)),
		SUPER_ANTIPOISON(new GameItem(2448), 48, 106, new GameItem(101), new GameItem(Items.UNICORN_HORN_DUST)),
		SUPER_ATTACK(new GameItem(2436), 45, 100, new GameItem(101), new GameItem(Items.EYE_OF_NEWT)),

		SUPER_COMBAT_3(new GameItem(12697), 90, 190, new GameItem(Items.TORSTOL_POTION_UNF), new GameItem(145), new GameItem(157), new GameItem(163)),
		SUPER_COMBAT_4(new GameItem(12695), 90, 190, new GameItem(Items.TORSTOL_POTION_UNF), new GameItem(2436), new GameItem(2440), new GameItem(2442)),

		SUPER_DEFENCE(new GameItem(2442), 66, 150, new GameItem(107), new GameItem(Items.WHITE_BERRIES)),
		SUPER_ENERGY(new GameItem(3016), 52, 117, new GameItem(103), new GameItem(Items.MORT_MYRE_FUNGUS)),
		SUPER_RESTORE(new GameItem(3024), 63, 142, new GameItem(3004), new GameItem(Items.RED_SPIDERS_EGGS)),
		SUPER_STRENGTH(new GameItem(2440), 55, 125, new GameItem(Items.KWUARM_POTION_UNF), new GameItem(Items.LIMPWURT_ROOT)),
		WEAPON_POISON(new GameItem(187), 60, 137, new GameItem(Items.KWUARM_POTION_UNF), new GameItem(Items.DRAGON_SCALE_DUST)),
		WEAPON_POISON_PLUS(new GameItem(5937), 73, 165, new GameItem(6124), new GameItem(6016), new GameItem(Items.RED_SPIDERS_EGGS)),
		WEAPON_POISON_PLUS_PLUS(new GameItem(5940), 82, 190, new GameItem(5935), new GameItem(2398), new GameItem(6018)),
		ZAMORAK_BREW(new GameItem(2450), 78, 175, new GameItem(111), new GameItem(247)),

		SANFEW_4(new GameItem(10925), 65, 25, new GameItem(Items.SUPER_RESTORE4), new GameItem(Items.UNICORN_HORN_DUST)),
		SANFEW_3(new GameItem(10927), 65, 25, new GameItem(Items.SUPER_RESTORE3), new GameItem(Items.UNICORN_HORN_DUST)),

		DIVINE_RANGING_POTION_3(new GameItem(Items.DIVINE_RANGING_POTION3), 74, 3, new GameItem(Items.RANGING_POTION3), new GameItem(Items.CRYSTAL_DUST, 3)),
		DIVINE_RANGING_POTION_4(new GameItem(Items.DIVINE_RANGING_POTION4), 74, 4, new GameItem(Items.RANGING_POTION4), new GameItem(Items.CRYSTAL_DUST, 4)),

		DIVINE_SUPER_COMBAT_3(new GameItem(Items.DIVINE_SUPER_COMBAT_POTION3), 97, 3, new GameItem(Items.SUPER_COMBAT_POTION3), new GameItem(Items.CRYSTAL_DUST, 3)),
		DIVINE_SUPER_COMBAT_4(new GameItem(Items.DIVINE_SUPER_COMBAT_POTION4), 97, 4, new GameItem(Items.SUPER_COMBAT_POTION4), new GameItem(Items.CRYSTAL_DUST, 4)),

		DIVINE_MAGIC_POTION_3(new GameItem(Items.DIVINE_MAGIC_POTION3), 78, 3, new GameItem(Items.MAGIC_POTION3), new GameItem(Items.CRYSTAL_DUST, 3)),
		DIVINE_MAGIC_POTION_4(new GameItem(Items.DIVINE_MAGIC_POTION4), 78, 4, new GameItem(Items.MAGIC_POTION4), new GameItem(Items.CRYSTAL_DUST, 4)),

		;

		/**
		 * The primary ingredient required
		 */
		private final GameItem primary;

		/**
		 * An array of {@link GameItem} objects that represent the ingredients
		 */
		private final GameItem[] ingredients;

		/**
		 * The item received from combining the ingredients
		 */
		private final GameItem result;

		/**
		 * The level required to make this potion
		 */
		private final int level;

		/**
		 * The experience gained from making this potion
		 */
		private final int experience;

		/**
		 * Creates a new in-game potion that will be used in herblore
		 *
		 * @param result the result from combining ingredients
		 * @param level the level required
		 * @param experience the experience
		 * @param ingredients the ingredients to make the result
		 */
		FinishedPotions(GameItem result, int level, int experience, GameItem primary, GameItem... ingredients) {
			this.result = result;
			this.level = level;
			this.experience = experience;
			this.primary = primary;
			this.ingredients = ingredients;
		}

		/**
		 * The result from combining the ingredients
		 *
		 * @return the result
		 */
		public GameItem getResult() {
			return result;
		}

		/**
		 * The level required to combine the ingredients
		 *
		 * @return the level required
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * The total amount of experience gained in the herblore skill
		 *
		 * @return the experience gained
		 */
		public int getExperience() {
			return experience;
		}

		/**
		 * An array of {@link GameItem} objects that represent the ingredients required to create this potion.
		 *
		 * @return the ingredients required
		 */
		public GameItem[] getIngredients() {
			return ingredients;
		}

		/**
		 * The primary ingredient required for the potion
		 *
		 * @return the primary ingredient
		 */
		public GameItem getPrimary() {
			return primary;
		}
	}

}