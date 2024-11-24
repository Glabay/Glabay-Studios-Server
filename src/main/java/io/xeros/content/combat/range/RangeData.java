package io.xeros.content.combat.range;

import java.util.*;

import io.xeros.content.achievement_diary.DifficultyAchievementDiary;
import io.xeros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.DiamondBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.DragonBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.EmeraldBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.JadeBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.OnyxBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.OpalBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.PearlBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.RubyBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.SapphireBoltSpecial;
import io.xeros.content.combat.effects.damageeffect.impl.bolts.TopazBoltSpecial;
import io.xeros.content.combat.weapon.RangedWeaponType;
import io.xeros.content.skills.herblore.PoisonedWeapon;
import io.xeros.model.Bonus;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.ItemStats;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class RangeData {

	public static final int[] MULTI_WEAPONS = {
			10033, // Grey chin
			10034, // Red chin
			11959, // Black chin
	};
    public static final int[] BOWS = { 19481, 19478, 12788, 9185, 11785, 21012, 839, 845, 847, 851, 855, 859, 841, 843, 849,
            853, 857, 12424, 861, 4212, 4214, 4215, 12765, 12766, 12767, 12768, 11235, 4216, 4217, 4218, 4219, 4220,
            4221, 4222, 4223, 4734, 6724, 20997, 21902, 22550 };

    public static final int[] ARROWS = { 9341, 4160, 11959, 10033, 10034, 882, 883, 884, 885, 886, 887, 888, 889, 890, 891,
            892, 893, 4740, 5616, 5617, 5618, 5619, 5620, 5621, 5622, 5623, 5624, 5625, 5626, 5627, 9139, 9140, 9141,
            9142, 9143, 11875, 21316, 21326, 9144, 9145, 9240, 9241, 9242, 9243, 9244, 9245, 9286, 9287, 9288, 9289,
            9290, 9291, 9292, 9293, 9294, 9295, 9296, 9297, 9298, 9299, 9300, 9301, 9302, 9303, 9304, 9305, 9306, 11212,
            11227, 11228, 11229, 9335, 9336, 9337, 9338, 9339, 9340, 9341, 11875, 9340, 21905, 21906, 21316, 21924, 21925,
            21926, 21927, 21928, 21929, 21930, 21931, 21932, 21933, 21934, 21935, 21936, 21937, 21938, 21939, 21940, 21941,
            21942, 21943, 21944, 21945, 21946, 21947, 21948, 21949, 21950, 21951, 21952, 21953, 21954, 21955, 21956, 21957,
            21958, 21959, 21960, 21961, 21962, 21963, 21964, 21965, 21966, 21967, 21968, 21969, 21970, 21971, 21972, 21973,
            21974};
    public static final int[] CRYSTAL_BOWS = { 4212, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223};

    public static final int[] NO_ARROW_DROP = { 11959, 10033, 10034, 4212, 22550, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221,
            4222, 4223, 4734, 4934, 4935, 4936, 4937, 22550 };

    public static final int[] DOUBLE_SHOT_BOWS = {11235, 12765 , 12766, 12767, 12768};

    public static final int[] OTHER_RANGE_WEAPONS = { 11959, 10033, 10034, 800, 801, 802, 803, 804, 805, 20849, 806, 807, 808,
            809, 810, 811, 812, 813, 814, 815, 816, 817, 825, 826, 827, 828, 829, 830, 831, 832, 833, 834, 835, 836,
            863, 864, 865, 866, 867, 868, 869, 870, 871, 872, 873, 874, 875, 876, 4934, 4935, 4936, 4937, 5628, 5629,
            5630, 5632, 5633, 5634, 5635, 5636, 5637, 5639, 5640, 5641, 5642, 5643, 5644, 5645, 5646, 5647, 5648, 5649,
            5650, 5651, 5652, 5653, 5654, 5655, 5656, 5657, 5658, 5659, 5660, 5661, 5662, 5663, 5664, 5665, 5666, 5667,
            6522, 11230, 11231, 11233,11234, 22804, 22634, 21318, 19484 };

	public static final int[] JAVELINS = { 825, 826, 827, 828, 829, 830, 21318, 19484 };

	private static final List<DamageBoostingEffect> BOLT_EFFECTS = Collections
			.unmodifiableList(Arrays.asList(new DragonBoltSpecial(), new OpalBoltSpecial(), new JadeBoltSpecial(), new PearlBoltSpecial(), new TopazBoltSpecial(),
					new EmeraldBoltSpecial(), new SapphireBoltSpecial(), new RubyBoltSpecial(), new DiamondBoltSpecial(), new OnyxBoltSpecial()));

	public static void fireProjectileNpc(Player player, NPC npc, int angle, int speed, int projectile, int startHeight, int endHeight, int time, int slope) {
		int playerX = player.getX();
		int playerY = player.getY();
		int npcX = npc.getX();
		int npcY = npc.getY();
		int xOffset = (playerY - npcY) * -1;
		int yOffset = (playerX - npcX) * -1;
		player.getPA().createPlayersProjectile2(playerX, playerY, xOffset, yOffset, angle, speed, projectile, startHeight, endHeight, npc.getIndex() + 1, time, slope);
	}

	public static void fireProjectilePlayer(Player player, Player target, int angle, int speed, int projectile, int startHeight, int endHeight, int time, int slope) {
		int playerX = player.getX();
		int playerY = player.getY();
		int npcX = target.getX();
		int npcY = target.getY();
		int xOffset = (playerY - npcY) * -1;
		int yOffset = (playerX - npcX) * -1;
		player.getPA().createPlayersProjectile2(playerX, playerY, xOffset, yOffset, angle, speed, projectile, startHeight, endHeight, target.getIndex() + 1, time, slope);
	}

	public static int getRangeStr(Player c, int...excludeEquipmentSlots) {
		int str = 0;
		for (int index = 0; index < c.playerEquipment.length; index++) {
			final int index2 = index;
			if (Arrays.stream(excludeEquipmentSlots).noneMatch(slot -> slot == index2) && c.playerEquipment[index] > 0) {
				str += getRangeStr(c.playerEquipment[index]);
			}
		}
		return str;
	}

	public static int getRangeStr(int i) {
		Optional<Integer> original = PoisonedWeapon.getOriginal(i);
		int item = original.orElse(i);
		if (ItemStats.forId(item) != null && ItemStats.forId(item).getEquipment() != null){
			return ItemStats.forId(item).getEquipment().getBonus(Bonus.RANGED_STRENGTH);
		} else {
			return 0;
		}
	}

	public static int getRangeStartGFX(Player c) {
		int itemId = c.attacking.getRangedWeaponType().noArrows() ? c.playerEquipment[Player.playerWeapon] : c.playerEquipment[Player.playerArrows];
		Optional<Integer> originalItem = PoisonedWeapon.getOriginal(itemId);
		int item = originalItem.orElse(itemId);
		int str = -1;
		
		int[][] data = {
				// KNIFES
				{ 863, 220 }, { 864, 219 }, { 865, 221 }, { 866, 223 }, { 867, 224 }, { 868, 225 }, { 869, 222 }, {22804, -1},

				// DARTS
				{ 806, 232 }, { 807, 233 }, { 808, 234 }, { 809, 235 }, { 810, 236 }, { 811, 237 }, { 11230, 237 },

//				// JAVELINS
//				
				{ 825, 206 }, { 826, 207 }, { 827, 208 }, { 828, 209 }, { 829, 210 }, { 830, 211 },

				// AXES
				{ 800, 36 }, { 801, 35 }, { 802, 37 }, { 803, 38 }, { 804, 39 }, { 805, 48 }, { 20849, 1320}, {22634,1624 },

				// CRYSTAL_BOW
				{ 4212, 250 }, { 4214, 250 },  { 22550, 250},{ 4215, 250 }, { 4216, 250 }, { 4217, 250 }, { 4218, 250 }, { 4219, 250 }, { 4220, 250 }, { 4221, 250 }, { 4222, 250 },
				{ 4223, 250 },

				// ARROWS
				{ 882, 19 }, { 884, 18 }, { 886, 20 }, { 888, 21 }, { 890, 22 }, { 892, 24 }, { 11212, 1120 }, { 21326, 1385 },

		};
		for (int l = 0; l < data.length; l++) {
			if (item == data[l][0]) {
				str = data[l][1];
			}
		}
		if (c.attacking.getRangedWeaponType() == RangedWeaponType.DOUBLE_SHOT) {
			Optional<Integer> originalAmmo = PoisonedWeapon.getOriginal(c.playerEquipment[Player.playerArrows]);
			int ammo = originalAmmo.orElseGet(() -> c.playerEquipment[Player.playerArrows]);
			int[][] moreD = { { 882, 1104 }, { 884, 1105 }, { 886, 1106 }, { 888, 1107 }, { 890, 1108 }, { 892, 1109 }, { 11212, 1111 }, { 21326, 1383 } };
			for (int l = 0; l < moreD.length; l++) {
				if (ammo == moreD[l][0]) {
					str = moreD[l][1];
				}
			}
		}
		return str;
	}

	public static int getRangeProjectileGFX(Player c) {
		int itemId = c.attacking.getRangedWeaponType().noArrows() ? c.playerEquipment[Player.playerWeapon] : c.playerEquipment[Player.playerArrows];
		int equipmentAmmo = c.playerEquipment[Player.playerArrows];
		int weaponId = c.playerEquipment[Player.playerWeapon];
		switch (c.weaponUsedOnAttack) {
		case 861: // Magic shortbow
		case 12788:
			if (c.usingSpecial) {
				return 249;
			}
			break;
		}

		// Blowpipe
		if (c.getItems().isWearingItem(12926)) {
			Optional<Integer> original = PoisonedWeapon.getOriginal(c.getToxicBlowpipeAmmo());
			int ammo = original.orElseGet(c::getToxicBlowpipeAmmo);
			final int[][] DARTS = { { 806, 226 }, { 807, 227 }, { 808, 228 }, { 809, 229 }, { 810, 230 }, { 811, 231 }, { 11230, 231 } };
			for (int index = 0; index < DARTS.length; index++) {
				if (DARTS[index][0] == ammo) {
					return DARTS[index][1];
				}
			}
		}

		if (c.dbowSpec) {
			return Arrow.matchesMaterial(c.arrowUsedOnAttack, Arrow.DRAGON) ? 1099 : 1101;
		}

		// Crossbows
		if (c.playerEquipment[Player.playerWeapon] == 9185 || c.playerEquipment[Player.playerWeapon] == 11785 || c.playerEquipment[Player.playerWeapon] == 21012
				|| c.playerEquipment[Player.playerWeapon] == 21902) {
			return 27;
		}

		int str = -1;
		Optional<Integer> original = PoisonedWeapon.getOriginal(itemId);
		int ammo = original.orElse(itemId);
		if (c.playerEquipment[Player.playerWeapon] == 19481 || c.playerEquipment[Player.playerWeapon] == 19478) {
			ammo = c.playerEquipment[Player.playerArrows];
		}
		int[][] data = {
				// KNIFES
				{ 863, 213 }, { 864, 212 }, { 865, 214 }, { 866, 216 }, { 867, 217 }, { 868, 218 }, { 869, 215 }, {22812, 215},

				// DARTS
				{ 806, 226 }, { 807, 227 }, { 808, 228 }, { 809, 229 }, { 810, 230 }, { 811, 231 }, { 11230, 231 },

				// JAVELINS
				{ 825, 200 }, { 826, 201 }, { 827, 202 }, { 828, 303 }, { 829, 504 }, { 830, 705 }, { 21318, 1000 }, { 19484, 1301 },

				// AXES
				{ 800, 36 }, { 801, 35 }, { 802, 37 }, { 803, 38 }, { 804, 39 }, { 805, 41 }, { 20849, 1319 }, {22634,1623},
				{22804, 28},

				// ARROWS
				{ 882, 19 }, { 884, 18 }, { 886, 20 }, { 888, 21 }, { 890, 22 }, { 892, 24 }, { 11212, 1120 }, { 21326, 1384 },

				// CHINCHOMPA
				{ 10033, 908 }, { 10034, 909 }, { 11959, 909 },

				// OTHERS
				{ 6522, 442 }, { 4740, 27 }, { 4212, 249 } ,{ 4214, 249 },  { 22550, 249},{ 4215, 249 }, { 4216, 249 }, { 4217, 249 }, { 4218, 249 }, { 4219, 249 }, { 4220, 249 }, { 4221, 249 },
				{ 4222, 249 }, { 4223, 249 }, };
		for (int l = 0; l < data.length; l++) {
			if (weaponId == data[l][0] || ammo == data[l][0]) {
				str = data[l][1];
			}
		}
		return str;
	}

	public static int getRangeEndGFX(Player c, boolean weapon) {
		int itemId = weapon ? c.weaponUsedOnAttack : c.arrowUsedOnAttack;
		int str = -1;
		int gfx = 0;
		int[][] data = { { 10033, 157, 100 }, { 10034, 157, 100 }, { 11959, 157, 100 } };
		for (int l = 0; l < data.length; l++) {
			if (itemId == data[l][0]) {
				str = data[l][1];
				gfx = data[l][2];
			}
		}
		if (gfx == 100) {
			c.rangeEndGFXHeight = true;
		}
		return str;
	}

	public static int getProjectileSpeed(Player c) {
		if (c.dbowSpec) {
			return 100;
		}
		switch (c.playerEquipment[3]) {
			case 20849:
				return 35;
			case 22804:
			case 22634:
				return 45;
		case 10033:
		case 10034:
		case 11959:
			return 60;
			
		case 19478:
		case 19481:
			return 100;
		}
		return 70;
	}

	public static int getProjectileShowDelay(Player c, boolean weapon) {
		int itemId = weapon ? c.playerEquipment[Player.playerWeapon] : c.playerEquipment[Player.playerArrows];
		int weaponId = c.playerEquipment[Player.playerWeapon];
		if (weaponId == 22634 || weaponId == 22804)
			return 25;
		if (weaponId == 20849)
			return 20;
		Optional<Integer> original = PoisonedWeapon.getOriginal(itemId);
		int ammo = original.orElse(itemId);
		int[] data = { 806, 806, 808, 809, 810, 811, 10033, 10034, 11959, 11230, };
		int str = 53;
		for (int i = 0; i < data.length; i++) {
			if (ammo == data[i]) {
				str = 32;
			}
		}
		return str;
	}

	public static void createCombatGraphic(Entity entity, int graphic, boolean height100) {
		if (entity instanceof Player) {
			Player target = (Player) entity;
			if (height100 == true) {
				target.gfx100(graphic);
			} else {
				target.gfx0(graphic);
			}
		} else if (entity instanceof NPC) {
			NPC target = (NPC) entity;
			if (height100 == true) {
				target.gfx100(graphic);
			} else {
				target.gfx0(graphic);
			}
		}
	}

	public static Optional<DamageBoostingEffect> getBoltEffect(Player attacker) {
		return BOLT_EFFECTS.stream().filter(e -> e.isExecutable(attacker)).findFirst();
	}

	public static boolean boltSpecialAvailable(Player player, int...bolt) {
		OptionalInt optionalBolt = Arrays.stream(bolt).filter(it -> player.playerEquipment[Player.playerArrows] == it).findFirst();

		if (optionalBolt.isEmpty()) {
			return false;
		}

		double probability;
		switch (optionalBolt.getAsInt()) {
			case Items.OPAL_BOLTS_E:
			case Items.OPAL_DRAGON_BOLTS_E:
			case Items.SAPPHIRE_BOLTS_E:
			case Items.SAPPHIRE_DRAGON_BOLTS_E:
				probability = 0.05;
				break;

			case Items.JADE_BOLTS_E:
			case Items.JADE_DRAGON_BOLTS_E:
			case Items.PEARL_BOLTS_E:
			case Items.PEARL_DRAGON_BOLTS_E:
			case Items.DRAGONSTONE_BOLTS_E:
			case Items.DRAGONSTONE_DRAGON_BOLTS_E:
				probability = 0.06;
				break;

			case Items.TOPAZ_BOLTS_E:
			case Items.TOPAZ_DRAGON_BOLTS_E:
				probability = 0.04;
				break;

			case Items.EMERALD_BOLTS_E:
			case Items.EMERALD_DRAGON_BOLTS_E:
				probability = 0.55;
				break;

			case Items.DIAMOND_BOLTS_E:
			case Items.DIAMOND_DRAGON_BOLTS_E:
				if (player.playerAttackingIndex > 0) {
					probability = 0.1;
				} else
					probability = 0.05;
				break;

			case Items.ONYX_BOLTS_E:
			case Items.ONYX_DRAGON_BOLTS_E:
				probability = 0.1;
				break;

			default:
				probability = 0.05;
				System.err.println("No probability for bolt spec: " + optionalBolt.getAsInt());
				break;
		}

		if (player.getDiaryManager().getKandarinDiary().hasDone(DifficultyAchievementDiary.EntryDifficulty.HARD)) {
			probability += 0.1;
		}

		player.debug("Trying bolt special for {} at: {}%.", ItemDef.forId(optionalBolt.getAsInt()).getName(), probability * 100);
		return Math.random() <= probability;
	}

	public static boolean wearingCrossbow(Player player) {
		return player.playerEquipment[Player.playerWeapon] == 11785 || player.playerEquipment[Player.playerWeapon] == 9185
				|| player.playerEquipment[Player.playerWeapon] == 21012 || player.playerEquipment[Player.playerWeapon] == 21902;
	}

	public static boolean wearingBolt(Player player) {
		int arrows = player.playerEquipment[Player.playerArrows];
		return arrows >= 9236 && arrows <= 9245 || arrows >= 21_932 && arrows <= 21_951;
	}
}