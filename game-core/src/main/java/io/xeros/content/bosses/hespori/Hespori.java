package io.xeros.content.bosses.hespori;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.content.skills.Skill;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import static io.xeros.content.combat.Hitmark.HIT;


public class Hespori {

	private static final List<HesporiBonus> bonuses = Lists.newArrayList(new AttasBonus(), new IasorBonus(), new KronosBonus(),
			new BuchuBonus(), new CelastrusBonus(), new GolparBonus(), new KeldaBonus(), new NoxiferBonus(), new ConsecrationBonus());
	public static final int HESPORI_PLANTER_OBJECT = 33983;
	public static final int[] HESPORI_RARE_SEEDS = {
					HesporiBonusPlant.KRONOS.getItemId(),
					HesporiBonusPlant.IASOR.getItemId(),
					HesporiBonusPlant.ATTAS.getItemId(),
					HesporiBonusPlant.KELDA.getItemId(),
					HesporiBonusPlant.NOXIFER.getItemId(),
					HesporiBonusPlant.BUCHU.getItemId(),
					HesporiBonusPlant.CELASTRUS.getItemId(),
					HesporiBonusPlant.GOLPAR.getItemId(),
					HesporiBonusPlant.CONSECRATION.getItemId()
	};
	/**
	 * Variables
	 */
    public static final int[] HESPORI_GROW_PHASE_OBJECTS = {33726, 33727, 33728, 33729};
	public static final int FINAL_OBJECT_ID = 33730;
	public static final int KEY = 22374;

	public static final int NPC_ID = 8583;

	public static final int X = 3046;
	public static final int Y = 3489;
	public static final int SPAWN_ANIMATION = 8221;
	public static final int DEATH_ANIMATION = 8225;

	public static final int RANGE_ANIMATION = 8224;
	public static final int MAGIC_ANIMATION = 8223;

	public static final int RANGE_PROJECTILE = 1639;
	public static final int MAGIC_PROJECTILE = 1640;
	public static final int SPECIAL_PROJECTILE = 1642;

	public static final int SPECIAL_HIT_GFX = 179;

	public static final int ESSENCE_REQUIRED = 200;
	public static final int TOXIC_GEM_EFFECT = 30;

	public static int TOXIC_GEM_AMOUNT = 0;
	public static int HESPORI_DEFENCE = 3000;

	public static int TOTAL_ESSENCE_BURNED = 0;

	public static boolean ENOUGH_BURNED = false;
	public static boolean isWeak = false;


    /**
	 * Hespori rewards player
	 * @param eventCompleted
	 */
	public static void rewardPlayers(boolean eventCompleted) {
		TOTAL_ESSENCE_BURNED = 0;
		TOXIC_GEM_AMOUNT = 0;
		HESPORI_DEFENCE = 3000;
		ENOUGH_BURNED = false;
		isWeak = false;
		HesporiSpawner.despawn();
		Server.getGlobalObjects().add(new GlobalObject(FINAL_OBJECT_ID, X, Y,
				0, 1, 10, -1, -1)); // West - Empty Altar
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.HESPORI))
		.forEach(p -> {
			if (!eventCompleted) {
				p.sendMessage("@blu@Hespori event was ended before she was killed!");
				p.canLeaveHespori = true;
				p.getPA().startTeleport2(3087, 3491, 0);
				p.setHesporiDamageCounter(0);
				deleteEventItems(p);
			} else {
				if (p.getHesporiDamageCounter() >= 200) {
					p.sendMessage("@blu@Hespori has been killed!");
					p.sendMessage("@blu@Harvest Hespori with an axe to receive your reward!");
					p.canLeaveHespori = true;
					p.canHarvestHespori = true;
					p.setHesporiDamageCounter(0);
					p.getEventCalendar().progress(EventChallenge.OBTAIN_X_HESPORI_EVENT_KEYS);
					LeaderboardUtils.addCount(LeaderboardType.HESPORI, p, 1);
					Achievements.increase(p, AchievementType.HESPORI, 1);
					deleteEventItems(p);
				} else {
					p.sendMessage("@blu@You were not active enough to receive a reward.");
					p.canLeaveHespori = true;
					p.getPA().startTeleport2(3087, 3491, 0);
					p.setHesporiDamageCounter(0);
					deleteEventItems(p);
				}
			}
		});
	}
	/**
	 * Hespori Boss Event Mechanics
	 */


	public static void useBurningRune(Player c) {//damages Hespori
		int amount = c.getItems().getItemAmount(9699);
		int setBurnAmount = Misc.random(TOXIC_GEM_AMOUNT * amount); //player max hit is equal to amount of used toxic gems
		NPC npc = NPCHandler.npcs[c.npcClickIndex];
		if (npc != null && npc.getNpcId() == NPC_ID) {
			if (ENOUGH_BURNED) {
				if (TOXIC_GEM_AMOUNT < 80) {
					c.sendMessage("@blu@Hesporis defence is still very high!");
					c.appendDamage(npc, Misc.random(15), HIT);
				} else {
					c.appendDamage(npc, setBurnAmount, HIT);
					c.sendMessage("@blu@Your burning runes vanish as they damage Hespori.");
				}
			}
		}
	}

	public static void useToxicGem(Player c) {//lowers Hespori defence
		int amount = c.getItems().getItemAmount(23783);
		int lowerDefBy = TOXIC_GEM_EFFECT * TOXIC_GEM_AMOUNT;
		int setNewDefence = HESPORI_DEFENCE - lowerDefBy;
		NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
		c.setHesporiDamageCounter(c.getHesporiDamageCounter() + (5 * amount));
		if (npc != null && npc.getNpcId() == NPC_ID) {
			TOXIC_GEM_AMOUNT += amount;
			c.getItems().deleteItem2(23783, amount);
			if (!c.getMode().isOsrs() && !c.getMode().is5x()) {
				c.getPA().addSkillXP(3000 * amount, 12, true);
				c.getPA().addSkillXP(1000 * amount, 14, true);
			} else {
				c.getPA().addSkillXP(110 * amount, 12, true);//crafting
				c.getPA().addSkillXP(20 * amount, 14, true);//mining
			}
			npc.setNpcStats(NpcStats.builder().setDefenceLevel(setNewDefence).createNpcStats());
			PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.HESPORI))
					.forEach(p -> {

						if (TOXIC_GEM_AMOUNT < 100) {
							p.sendMessage("@blu@Hespori's defence has been lowered but is still too high!");
						} else if (!isWeak) {
							p.sendMessage("@red@Hespori is now weak and vulnerable to attacks but can still be weakened.");
							isWeak = true;
						} else if (TOXIC_GEM_AMOUNT > 200) {
							p.sendMessage("@red@Hespori is now extremely weak!");
						}
					});
		}
	}

	public static void burnEssence(Player c) {
		int amount = c.getItems().getItemAmount(9017);

		if (!HesporiSpawner.isSpawned()) {
			c.sendMessage("@red@You cannot do this right now.");
			return;
		}
		if (c.getItems().playerHasItem(9017, 1)) {
			c.getItems().deleteItem2(9017, amount);
			TOTAL_ESSENCE_BURNED += amount;
			int ESSENCE_LEFT = ESSENCE_REQUIRED - TOTAL_ESSENCE_BURNED;
			if (ESSENCE_LEFT <= 0 && ENOUGH_BURNED != true) {
				ENOUGH_BURNED = true;
				c.setHesporiDamageCounter(c.getHesporiDamageCounter() + (5 * amount));
				if (!c.getMode().isOsrs() && !c.getMode().is5x()) {
					c.getPA().addSkillXP(2500 * amount, 11, true);
				} else {
					c.getPA().addSkillXP(120 * amount, 11, true);
				}

				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@]@red@Enough essence has burned! @gre@Hespori @red@can now be attacked!");

			} else if (ESSENCE_LEFT >= 1) {
				c.sendMessage("@red@" + ESSENCE_LEFT + " essence are still required to be burned.");
				c.setHesporiDamageCounter(c.getHesporiDamageCounter() + (5 * amount));
				if (!c.getMode().isOsrs() && !c.getMode().is5x()) {
					c.getPA().addSkillXP(2500 * amount, 11, true);
				} else {
					c.getPA().addSkillXP(120 * amount, 11, true);
				}
			}
		} else if (!c.getItems().playerHasItem(9698, 1)) {
			c.sendMessage("You have no essence to burn.");
		} else {
			c.sendMessage("You have no essence to burn. Right-click the fire to burn your runes.");
		}
	}
	public static void burnRunes(Player c) {
		int amount = c.getItems().getItemAmount(9698);
		if (!HesporiSpawner.isSpawned()) {
			c.sendMessage("@red@You cannot do this right now.");
			return;
		}
		if (c.getLevelForXP(c.playerXP[20]) < 50 || c.getLevelForXP(c.playerXP[13]) < 50) {
			c.sendMessage("You need a Smithing and Runecrafting level of 50 to burn these.");
			return;
		}
		if (c.getItems().playerHasItem(9698, 1)) {
			c.getItems().deleteItem2(9698, amount);
			c.getItems().addItem(9699, amount);
			c.sendMessage("@red@Your runes become hot to the touch.");
			c.setHesporiDamageCounter(c.getHesporiDamageCounter() + (5 * amount));
		} else {
			c.sendMessage("@red@You have no runes to burn.");
		}
	}

	public static boolean clickObject(Player player, int objectId) {
		if (objectId == HESPORI_PLANTER_OBJECT) {
			List<HesporiBonus> list = bonuses.stream().filter(bonus -> player.getItems().playerHasItem(bonus.getPlant().getItemId())).collect(Collectors.toList());

			if (list.isEmpty()) {
				player.sendMessage("You need a Hespori seed to plant here.");
			} else if (list.size() > 1) {
				player.sendMessage("You have too many @gre@Hespori seeds@bla@ please bring only 1 seed type.");
			} else {
				plant(player, list.get(0));
			}
		}

		return false;
	}

	private static void plant(Player player, HesporiBonus hesporiBonus) {
		if (!player.getItems().playerHasItem(hesporiBonus.getPlant().getItemId())) {
			player.sendMessage("You don't have the seed.");
			player.getPA().removeAllWindows();
			return;
		}

		 if (hesporiBonus.canPlant(player)) {
			player.getItems().deleteItem(hesporiBonus.getPlant().getItemId(), 1);
			hesporiBonus.activate(player);
			hesporiBonus.updateObject(true);
			if (!player.getMode().isOsrs() && !player.getMode().is5x()) {
				player.getPA().addSkillXP(250000 , Skill.FARMING.getId(), true);
			} else {
				player.getPA().addSkillXP(50000 , Skill.FARMING.getId(), true);
			}
			player.getItems().addItemUnderAnyCircumstance(Items.HESPORI_KEY, 5);
			player.getItems().addItemUnderAnyCircumstance(21046, 5);
		}
	}

	public static void deleteEventItems (Player c) {
		if (c.getItems().playerHasItem(9698)
				|| c.getItems().playerHasItem(9699)
				|| c.getItems().playerHasItem(23778)
				|| c.getItems().playerHasItem(23783)
				|| c.getItems().playerHasItem(9017)) {
			c.getItems().deleteItem2(9698, 28);
			c.getItems().deleteItem2(9699, 28);
			c.getItems().deleteItem2(23778, 28);
			c.getItems().deleteItem2(923783, 28);
			c.getItems().deleteItem2(9017, 28);
		}
	}

	/**
	 * Hespori Seeds Bonus Time Handling
	 */

	public static long ATTAS_TIMER, KRONOS_TIMER, IASOR_TIMER, GOLPAR_TIMER, BUCHU_TIMER, NOXIFER_TIMER, KELDA_TIMER, CELASTRUS_TIMER, CONSECRATION_TIMER;
	public static boolean activeAttasSeed = false;
	public static boolean activeKronosSeed = false;
	public static boolean activeIasorSeed = false;
	public static boolean activeBuchuSeed = false;
	public static boolean activeNoxiferSeed = false;
	public static boolean activeGolparSeed = false;
	public static boolean activeKeldaSeed = false;
	public static boolean activeCelastrusSeed = false;
	public static boolean activeConsecrationSeed = false;
	public static int chosenSkillid;

	public static String getSaveFile() {
		return Server.getSaveDirectory() + "hespori_seed_bonuses.txt";
	}

	public static void init() {
		try {
			File f = new File(getSaveFile());
			if (!f.exists()) {
				Preconditions.checkState(f.createNewFile());
			}
			Scanner sc = new Scanner(f);
			int i = 0;
			while(sc.hasNextLine()){
				i++;
				String line = sc.nextLine();
				String[] details = line.split("=");
				String amount = details[1];

				switch (i) {
					case 1:
						ATTAS_TIMER = (int) Long.parseLong(amount);
						break;
					case 2:
						KRONOS_TIMER = (int) Long.parseLong(amount);
						break;
					case 3:
						IASOR_TIMER = (int) Long.parseLong(amount);
						break;
					case 4:
						KELDA_TIMER = (int) Long.parseLong(amount);
						break;
					case 5:
						CELASTRUS_TIMER = (int) Long.parseLong(amount);
						break;
					case 6:
						NOXIFER_TIMER = (int) Long.parseLong(amount);
						break;
					case 7:
						BUCHU_TIMER = (int) Long.parseLong(amount);
						break;
					case 8:
						GOLPAR_TIMER = (int) Long.parseLong(amount);
						break;
					case 9:
						CONSECRATION_TIMER = (int) Long.parseLong(amount);
						break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public static String activeAnimaBonus() {
		if (Hespori.ATTAS_TIMER > 0) {
			return "Anima: @gre@Attas [Bonus XP]";
		}
		if (Hespori.KRONOS_TIMER > 0) {
			return "Anima: @gre@Kronos [x2 Raids 1 Keys]";
		}
		if (Hespori.IASOR_TIMER > 0) {
			return "Anima: @gre@Iasor [+10% DR]";
		}
		return "Anima: @red@None";
	}

}

