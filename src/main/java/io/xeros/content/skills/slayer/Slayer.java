package io.xeros.content.skills.slayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.achievement_diary.impl.MorytaniaDiaryEntry;
import io.xeros.content.bosses.Kraken;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.skills.Skill;
import io.xeros.model.CombatType;
import io.xeros.model.Items;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;

public class Slayer {

	/**
	 * The amount to multiply a task amount by when it's extended.
	 */
	public static final double TASK_EXTENSION_MULTIPLIER = 1.5;

	/**
	 * The amount of experience gained after finishing a boss task.
	 */
	private static final int BOSS_TASK_EXPERIENCE = 15_000;

	/**
	 * Represents superior slayer npcs, superior spawned boolean
	 */
	public static int[] SUPERIOR_NPCS = { 7388, 7389, 7390, 7391, 7392, 7393, 7394, 7395, 7396, 7397, 7398, 7399, 7400, 7401, 7402, 7403, 7404, 7405, 7406, 7407, 7409, 7410, 7411 };
	public static int[] SUPERIOR_COUNTERPARTS = { 448, 406, 414, 7272, 421, 419, 435, 417, 446, 484, 7276, 437, 7277, 3209, 6, 7279, 423, 411, 498, 1543, 4005, 415, 11 };
	public boolean superiorSpawned;

	public static final int EASY_TASK_NPC_ID = 401;
	public static final int MEDIUM_TASK_NPC_ID = 402;
	public static final int HARD_TASK_NPC_ID = 405;
	public static final int BOSS_TASK_NPC_ID = 6797;

	/**
	 * The current task for this player
	 */
	private Optional<Task> task = Optional.empty();

	/**
	 * The {@link NPC} id of the master that this player receives tasks from
	 */
	private int master;

	/**
	 * The amount of tasks that the player has completed consecutively from the same slayer master
	 */
	private int consecutiveTasks;

	/**
	 * The amount of slayer points the player has
	 */
	private int points;

	/**
	 * The amount of the task the player has left to slay
	 */
	private int taskAmount;

	/**
	 * The player that will be referenced in slayer related operations
	 */
	private final Player player;

	/**
	 * Determines if this player can obtain larger boss tasks
	 */
	private boolean biggerBossTasks;

	/**
	 * Determines if this player can navigate to cerberus
	 */
	private boolean learnedCerberusRoute;

	/**
	 * Sets the color of which you want to turn your slayer helmet into
	 */
	private String color;

	/**
	 * The task master names that the player has decided to remove
	 */
	private final String[] removed = Misc.nullToEmpty(6);

	/**
	 * Task extensions.
	 */
	private List<TaskExtension> extensions = new ArrayList<>();

	/**
	 * Slayer unlocks.
	 */
	private List<SlayerUnlock> unlocks = new ArrayList<>();

	/**
	 * Creates a new class for managing slayer operations
	 *
	 * @param player the player this is created for
	 */
	public Slayer(Player player) {
		this.player = player;
	}

	public boolean onTask(String name) {
		return getTask().isPresent() && getTask().get().getPrimaryName().toLowerCase().contains(name.toLowerCase());
	}

	/**
	 * Creates a new random slayer task for the player by grabbing a random task from the slayer master that the player is capable of completing.
	 *
	 * @param masterId the id of the master
	 */
	//@SuppressWarnings("unlikely-arg-type")
	public void createNewTask(int masterId, boolean override) {
		SlayerMaster.get(masterId).ifPresent(m -> {
			if (player.calculateCombatLevel() < m.getLevel() && !override) {
				player.getDH().sendNpcChat("You need a combat level of " + m.getLevel() + " to receive tasks from me.", "Please come back when you have this combat level.");
				return;
			}
			if (masterId == 401 && master != 401 && consecutiveTasks > 0 && taskAmount > 0) {
				consecutiveTasks = 0;
				player.sendMessage("Your consecutive tasks have been reset as you have switched to an easy task.");
			}

			switch (masterId) {
				case 6797:
				case 8605:
					if (player.playerLevel[18] < 95) {
						player.start(new DialogueBuilder(player).setNpcId(Npcs.NIEVE).npc("You need a Slayer level of 95 to receive boss tasks."));
						return;
					}
					break;
				case 405:
					player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.DURADEL);
					break;
			}

			Task[] available = retainObtainable(m.getAvailable());

			int exclusiveLength = available.length;

			if (exclusiveLength == 0) {
				player.getPA().closeAllWindows();
				String[] msg = { "There was a problem getting your slayer task!", "Please report this to the staff." };
				player.start(new DialogueBuilder(player).npc(masterId, DialogueExpression.SAD, msg[0], msg[1]));
				player.sendMessage(msg[0]+" "+msg[1]);
				return;
			}

			task = Optional.of(Misc.randomSearch(available, 0, exclusiveLength));
//			if (Server.isDebug()) {
//				while (!task.get().getPrimaryName().equalsIgnoreCase("blue dragon")) {
//					task = Optional.of(Misc.randomSearch(available, 0, available.length - 1));
//				}
//			}

			int minimum = task.get().getMinimum();
			int maximum = task.get().getMaximum();
			if (extensions.stream().anyMatch(ext -> Arrays.stream(ext.getNames()).anyMatch(name -> task.get().getPrimaryName().equals(name)))) {
				minimum *= TASK_EXTENSION_MULTIPLIER;
				maximum *= TASK_EXTENSION_MULTIPLIER;
			}

			taskAmount = Misc.random(Range.between(minimum, maximum));
			master = m.getId();

			if (masterId == 8623) {
				player.start(new KonarDialogue(player, task.get()));
			} else if (masterId == BOSS_TASK_NPC_ID) {
				player.start(new SelectTaskLengthDialogue(player, task.get()));
			} else {
				player.talkingNpc = 6797;
				player.lastTask = task.get().getFormattedName();
				player.getDH().sendNpcChat("You have been assigned " + taskAmount + " " + task.get().getFormattedName() + ".", "It costs 30 points to cancel task in the Rewards tab!", "Or choose an easier task and lose your streak.");
				player.nextChat = -1;
			}
		});
	}

	/**
	 * Set amount to slay, used for boss task length selection.
	 */
	protected void setAmountToSlay(int amount) {
		if (task.isPresent() && master == BOSS_TASK_NPC_ID) {
			taskAmount = amount;
			player.lastTask = task.get().getFormattedName();
			player.start(new DialogueBuilder(player).setNpcId(Npcs.NIEVE).npc("You've been assigned x" + amount + " " + task.get().getFormattedName() + "."));

		}
	}

	public boolean hasSlayerHelmBoost(NPC npc, CombatType combatType) {
		if (npc != null && (isTaskNpc(npc) || npc.getNpcId() == Npcs.MAX_DUMMY)) {
			if ((player.getItems().isWearingItem(Items.SALVE_AMULET) && combatType == CombatType.MELEE
					|| player.getItems().isWearingItem(Items.SALVE_AMULET_E) || player.getItems().isWearingItem(Items.SALVE_AMULETI))
					&& Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
				return false; // Salve boost doesn't stack
			}

			IntPredicate hasHelmet = id -> player.getItems().isWearingItem(id, Player.playerHat);
			boolean regularHelm = IntStream.of(player.SLAYER_HELMETS).anyMatch(hasHelmet) || IntStream.of(player.BLACK_MASKS).anyMatch(hasHelmet);
			boolean imbuedHelm = IntStream.of(player.IMBUED_SLAYER_HELMETS).anyMatch(hasHelmet);

			if (combatType == CombatType.MELEE && regularHelm || imbuedHelm) {
				return true;
			}
		}

		return false;
	}

	public boolean isTaskNpc(NPC npc) {
		String name = npc.getDefinition().getName().toLowerCase().replaceAll("_", "");
		return task.isPresent() && name.equals(task.get().getPrimaryName()) || task.isPresent() && ArrayUtils.contains(task.get().getNames(), name);
	}

	public boolean hasBoSInventory(Player player) {
		if (player.getItems().playerHasItem(21183) && player.slaughterCharge > 0) {
			return true;
		}
		return false;
	}
	public boolean hasBoSEquipment(Player player) {
		if (player.getItems().isWearingItem(21183) && player.slaughterCharge > 0) {
			return true;
		}
		return false;
	}

	public void reduceTaskAmount(Player player) {
		if (hasBoSEquipment(player) && Misc.random(100) < 30) {
			player.sendMessage("Your bracelet shines.");
			player.decreaseSlaughterCharge(1);
			if (player.slaughterCharge == 0) {
				player.sendMessage("Your bracelet has ran out of charges.");
			}

		} else if (hasBoSInventory(player) && Misc.random(100) < 20) {
			player.sendMessage("Your bracelet shines.");
			player.decreaseSlaughterCharge(1);
			if (player.slaughterCharge == 0) {
				player.sendMessage("Your bracelet has ran out of charges.");
			}
		} else {
			taskAmount--;
		}
	}
	/**
	 * A function referenced when a monster is killed. We manage
	 * cancelling the task and appending additional experience from
	 * this function.
	 *
	 * @param npc
	 * 			the non-playable character being killing.
	 */
	public void killTaskMonster(NPC npc) {
		if (npc == null || taskAmount == 0) {
			return;
		}
		if (player == null) {
			return;
		}
		if (npc.getNpcId() >= 7388 && npc.getNpcId() <= 7411) {
			handleSuperiorExp(npc);
		}
		task.ifPresent(task -> {
			if (isTaskNpc(npc)) {
				Optional<SlayerMaster> master = SlayerMaster.get(this.master);

				master.ifPresent(m -> {
					switch (m.getId()) {
						case 8623:
							if (KonarSlayer.checkLocation(player)) {
								reduceTaskAmount(player);
								int npcCombatLevel = npc.getDefinition().getCombatLevel();
								int chance = 1;
								int rewardAmount = 1;
								if (npcCombatLevel >= 100) {
									chance = ((200 - (npcCombatLevel/5))/3);
								} else {
									chance = ((100 +(((npcCombatLevel - 100)^2)/5))/2);
								}
								if (Hespori.activeCelastrusSeed) {
									rewardAmount = 2;
								}
								if (Misc.random(chance) == 1) {
									if (npc.getNpcId() == Kraken.KRAKEN_ID || npc.getNpcId() == 492) {
										Server.itemHandler.createGroundItem(player, Items.BRIMSTONE_KEY, player.getX(), player.getY(), player.heightLevel, rewardAmount, player.getIndex());
									} else {
										Server.itemHandler.createGroundItem(player, Items.BRIMSTONE_KEY, npc.getX(), npc.getY(), player.heightLevel, rewardAmount, player.getIndex());
									}
									//PlayerHandler.executeGlobalMessage("@cr21@ @pur@" + player.playerName + " received a drop: Brimstone's key from Konar slayer.");
								}
								if (npc.getNpcId() == 494) {
									player.getPA().addSkillXPMultiplied(255, Skill.SLAYER.getId(), true);
								} else {
									player.getPA().addSkillXPMultiplied(player.getRechargeItems().hasAnyItem(13113, 13114, 13115) && Boundary.isIn(player, Boundary.SLAYER_TOWER_BOUNDARY) ? (int) (task.getExperience() * 1.10) : task.getExperience(),
											Skill.SLAYER.getId(), true);
								}
							} else {

							}
							break;
						case 401:
						case 402:
						case 405:
						case 6797:
						case 603:
						case 8605:
						case 8761:
						case 5870:
							reduceTaskAmount(player);
							if (npc.getNpcId() == 494) {
								player.getPA().addSkillXPMultiplied(255, Skill.SLAYER.getId(), true);
							} else {
								player.getPA().addSkillXPMultiplied(player.getRechargeItems().hasAnyItem(13113, 13114, 13115) && Boundary.isIn(player, Boundary.SLAYER_TOWER_BOUNDARY) ? (int) (task.getExperience() * 1.10) : task.getExperience(),
										Skill.SLAYER.getId(), true);
							}
							break;
						case 7663:
							if (npc.getPosition().inWild()) {
								LarrensKey.roll(player, npc);
								reduceTaskAmount(player);
								player.getPA().addSkillXPMultiplied(player.getRechargeItems().hasAnyItem(13113, 13114, 13115) && Boundary.isIn(player, Boundary.SLAYER_TOWER_BOUNDARY) ? (int) (task.getExperience() * 1.10) : task.getExperience(),
										Skill.SLAYER.getId(), true);
								break;
							}
					}

					if (getUnlocks().contains(SlayerUnlock.BIGGER_AND_BADDER)) {
						handleSuperiorSpawn();
					}
					if (taskAmount == 0) {
						if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
						player.getEventCalendar().progress(EventChallenge.COMPLETE_X_HARD_SLAYER_ASSIGNMENTS);
						}
						int multiplier = 1;
						boolean scrollDoublePoints = player.getItems().playerHasItem(7629);
						if (scrollDoublePoints) {
							player.getItems().deleteItem(7629, 1);
							multiplier = 2;
							player.sendMessage("Your scroll is consumed and your slayer point reward is @red@doubled!");
						}
						if (Hespori.activeNoxiferSeed) {
							multiplier = 2;
						}
						int consecutive = consecutiveTasks + 1;
						this.consecutiveTasks++;
						this.points += (m.getPointReward(0) * multiplier);
						this.task = Optional.empty();
						player.sendMessage("<col=9a35ff>You have completed your slayer task, talk to a slayer master to receive another.");
						player.sendMessage("You have completed " + consecutiveTasks + " in a row!");
						player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.TEN_CONSECUTIVE);


						if (consecutiveTasks == 10) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (80 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@80@blu@ additional points.");
							}
						} else if (consecutiveTasks == 20) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (130 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@130@blu@ additional points.");
							}
						} else if (consecutiveTasks == 30) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (180 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@180@blu@ additional points.");
							}
						} else if (consecutiveTasks == 40) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (230 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@230@blu@ additional points.");
							}
						} else if (consecutiveTasks == 50) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (280 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@280@blu@ additional points.");
							}
						} else if (consecutiveTasks == 60) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (310 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@310@blu@ additional points.");
							}
						} else if (consecutiveTasks == 70) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (340 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@340@blu@ additional points.");
							}
						} else if (consecutiveTasks == 80) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (370 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@370@blu@ additional points.");
							}
						} else if (consecutiveTasks == 90) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (400 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@400@blu@ additional points.");
							}
						} else if (consecutiveTasks == 100) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (450 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@450@blu@ additional points.");
							}
						} else if (consecutiveTasks == 150) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (500 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@500@blu@ additional points.");
							}
						} else if (consecutiveTasks == 200) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (800 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@800@blu@ additional points.");
							}
						} else if (consecutiveTasks == 250) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (900 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@800@blu@ additional points.");
							}
						} else if (consecutiveTasks == 300) {
							if (player.getSlayer().getMaster() == 401 || player.getSlayer().getMaster() == 402) {
								points += (40 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@40@blu@ additional points.");
							} else if (!(player.getSlayer().getMaster() == 401 && !(player.getSlayer().getMaster() == 402))) {
								points += (1000 * multiplier);
								player.sendMessage("@blu@You have completed " + consecutive + " tasks in a row and receive @red@1000@blu@ additional points.");
							}
						}
							if (player.amDonated < 50) {
								points += 0;

							} else if (player.amDonated >= 50 && player.amDonated < 100) {
								points += 3;
								player.sendMessage("You have received an additonal@red@ 3@bla@ points for your donator rank.");
							} else if (player.amDonated >= 100 && player.amDonated < 250) {
								points += 5;
								player.sendMessage("You have received an additonal@red@ 5@bla@ points for your donator rank.");
							} else if (player.amDonated >= 250 && player.amDonated < 500) {
								points += 8;
								player.sendMessage("You have received an additonal@red@ 8@bla@ points for your donator rank.");
							} else if (player.amDonated >= 500 && player.amDonated < 1000) {
								points += 10;
								player.sendMessage("You have received an additonal@red@ 10@bla@ points for your donator rank.");
							} else if (player.amDonated >= 1000) {
								points += 12;
								player.sendMessage("You have received an additonal@red@ 12@bla@ points for your donator rank.");
							}
						player.getQuestTab().updateInformationTab();
						player.getQuestTab().updateInformationTab();
						if (consecutive == 10) {
							player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.TEN_CONSECUTIVE);
						}
						switch (m.getId()) {
							case 402:
								player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.MAZCHNA);
								break;

							case 6797:
								player.getPA().addSkillXP(BOSS_TASK_EXPERIENCE, Skill.SLAYER.getId(), true);
								player.sendMessage("You have completed a boss task and have gained an additional "
										+ Misc.insertCommas(Integer.toString(BOSS_TASK_EXPERIENCE)) + " experience.", 255);
								break;
						}
						Achievements.increase(player, AchievementType.SLAY, 1);
					}
				});
			}
		});
	}


	public void handleSuperiorSpawn() {
		task.ifPresent(task -> {
			int chance = Misc.random(75);
			if (chance == 0 || Server.isDebug()) {
				if (superiorSpawned){
					return;
				}
				if (!isSuperiorCounter()){
					return;
				}
				switch(task.getPrimaryName()){
					case "crawling hand":
						NPCSpawning.spawnNpc(player, 7388, player.getX(), player.getY(), player.getHeight(), 1, 5, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "cave crawler":
						NPCSpawning.spawnNpc(player, 7389, player.getX(), player.getY(), player.getHeight(), 1, 7, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "banshee":
						NPCSpawning.spawnNpc(player, 7390, player.getX(), player.getY(), player.getHeight(), 1, 7, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "twisted banshee":
						NPCSpawning.spawnNpc(player, 7391, player.getX(), player.getY(), player.getHeight(), 1, 15, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "rockslug":
						NPCSpawning.spawnNpc(player, 7392, player.getX(), player.getY(), player.getHeight(), 1, 9, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "cockatrice":
						NPCSpawning.spawnNpc(player, 7393, player.getX(), player.getY(), player.getHeight(), 1, 9, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "pyrefiend":
						NPCSpawning.spawnNpc(player, 7394, player.getX(), player.getY(), player.getHeight(), 1, 7, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "basilisk":
						NPCSpawning.spawnNpc(player, 7395, player.getX(), player.getY(), player.getHeight(), 1, 8, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "infernal mage":
						NPCSpawning.spawnNpc(player, 7396, player.getX(), player.getY(), player.getHeight(), 1, 20, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "bloodveld":
						NPCSpawning.spawnNpc(player, 7397, player.getX(), player.getY(), player.getHeight(), 1, 15, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "mutated bloodveld": //Add slayer task
						NPCSpawning.spawnNpc(player, 7398, player.getX(), player.getY(), player.getHeight(), 1, 20, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "jelly":
						NPCSpawning.spawnNpc(player, 7399, player.getX(), player.getY(), player.getHeight(), 1, 7, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "warped jelly": //Add slayer task
						NPCSpawning.spawnNpc(player, 7400, player.getX(), player.getY(), player.getHeight(), 1, 13, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "cave horror":
						NPCSpawning.spawnNpc(player, 7401, player.getX(), player.getY(), player.getHeight(), 1, 24, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					/*case "aberrant spectre":
					Server.npcHandler.spawnNpc(player, 7402, player.getX(), player.getY(), player.getHeight(), 1, 275, , npc.attack, npc.defence, true, false);
					break;
					case "deviant spectre":
					Server.npcHandler.spawnNpc(player, 7403, player.getX(), player.getY(), player.getHeight(), 1, 390, , npc.attack, npc.defence, true, false);
					break;*/
					case "dust devil":
						NPCSpawning.spawnNpc(player, 7404, player.getX(), player.getY(), player.getHeight(), 1, 24, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "kurask":
						NPCSpawning.spawnNpc(player, 7405, player.getX(), player.getY(), player.getHeight(), 1, 33, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "smoke devil":
						NPCSpawning.spawnNpc(player, 7406, player.getX(), player.getY(), player.getHeight(), 1, 21, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "gargoyle":
						NPCSpawning.spawnNpc(player, 7407, player.getX(), player.getY(), player.getHeight(), 1, 38, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "dark beast":
						NPCSpawning.spawnNpc(player, 7409, player.getX(), player.getY(), player.getHeight(), 1, 39, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "abyssal demon":
						NPCSpawning.spawnNpc(player, 7410, player.getX(), player.getY(), player.getHeight(), 1, 31, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
					case "nechryael":
						NPCSpawning.spawnNpc(player, 7411, player.getX(), player.getY(), player.getHeight(), 1, 27, true, false);
						superiorSpawned = true;
						player.sendMessage("@red@A superior foe has appeared...");
						break;
				}
				//superiorSpawned = true;
				//player.sendMessage("@red@A superior foe has appeared...");
			}
		});
	}


	public void handleSuperiorExp(NPC npc) {
		task.ifPresent(task -> {
			String name = npc.getDefinition().getName().toLowerCase().replaceAll("_", " ");
			if (!name.equals(task.getPrimaryName())) {
				if (!isSuperiorNpc()){
					return;
				}
				if (!superiorSpawned){
					return;
				}
				if (isSuperiorNpc()) {
					player.getPA().addSkillXPMultiplied(player.getRechargeItems().hasAnyItem(13113, 13114, 13115) && Boundary.isIn(player, Boundary.SLAYER_TOWER_BOUNDARY) ? (int) (task.getExperience() * 1.10)
									: task.getExperience() * 10, Skill.SLAYER.getId(), true);
					superiorSpawned = false;
					player.sendMessage("You receive bonus xp for killing a superior slayer npc!");
				}
			}
		});
	}

	public boolean isSuperiorNpc() {

		for (int SUPERIOR_NPC : SUPERIOR_NPCS) {
			return true;
		}
		return false;
	}

	public boolean isSuperiorCounter() {

		for (int SUPERIOR_COUNTERPART : SUPERIOR_COUNTERPARTS) {
			return true;
		}
		return false;
	}


	/**
	 * Retains an array of {@link Task} objects that the player can operate with the required slayer level.
	 *
	 * @param tasks the original array of tasks
	 * @return the retained array of obtainable tasks
	 */
	private Task[] retainObtainable(Task[] tasks) {
		List<Task> retainable = new ArrayList<>();
		List<String> blocked = new ArrayList<>(Arrays.asList(removed));
		for (Task task1 : tasks) {
			if (task1.getLevel()<=player.playerLevel[Skill.SLAYER.getId()]&&!blocked.contains(task1.getPrimaryName())||(Objects.equals(task1.getPrimaryName(), "cerberus")&&learnedCerberusRoute)) {
					//!task1.getFormattedName().equals(player.lastTask) ||
				retainable.add(task1);
			}

		}

		return retainable.toArray(new Task[retainable.size()]);
	}

	public static boolean hasRequiredLevel(Player player, int levelRequired) {
		int playerSlayerLevel = player.playerLevel[18];
		if (playerSlayerLevel < levelRequired) {
			player.getDH().sendStatement("You need a Slayer level of " + levelRequired + " to kill this monster.");
			player.attacking.reset();
			return false;
		}
		return true;
	}

	public void cancelTask() {
		int cost = getCancelTaskCost();
		if (!task.isPresent()) {
			player.sendMessage("You must have a task to cancel first.");
			return;
		}

		if (task.get().getPrimaryName().equals("cerberus") && !learnedCerberusRoute) {
			player.sendMessage("You have cancelled your current task of " + taskAmount + " " + task.get().getFormattedName() + " for free.");
			task = Optional.empty();
			taskAmount = 0;
			return;
		}

		if (points < cost) {
			player.sendMessage(String.format("You need %d Slayer points to cancel a task.", cost));
		} else {
			player.sendMessage("You spent " + cost + " points to cancel your current task of " + taskAmount + " " + task.get().getFormattedName() + ".");
			task = Optional.empty();
			taskAmount = 0;
			points -= cost;
		}
	}
	public void cancelTaskWithGp() {
		int cost = 1_500_000;
		if (!task.isPresent()) {
			player.getDH().sendNpcChat1("You must have a task to cancel first.", player.talkingNpc, "Nieve");
			return;
		}

		if (!player.getItems().playerHasItem(995, cost)) {
			player.getDH().sendNpcChat1("You need 1.5m GP to cancel a task this way.", player.talkingNpc, "Nieve");
			return;
		} else {
			player.getDH().sendNpcChat("You spent 1.5m GP to cancel your current task of", " " + taskAmount + " " + task.get().getFormattedName() + ".");
			task = Optional.empty();
			taskAmount = 0;
			player.getItems().deleteItem2(995, cost);
		}
	}

	public int getCancelTaskCost() {
			if (player.amDonated >= 2500) {
			return 10;
		} else if (player.amDonated >= 500) {
			return 15;
		} else if (player.amDonated >= 100) {
			return 20;
		} else if (player.amDonated >= 10) {
			return 25;
		} else {
			return 30;
		}
	}

	public void removeTask() {
		int rankPoints = getBlockTaskCost();

		if (!task.isPresent()) {
			player.sendMessage("You must have a task to block first.");
			return;
		}
		if (points < rankPoints) {
			player.sendMessage("You need " + rankPoints + " slayer points to block a task.");
			return;
		}
		for (int index = 0; index < removed.length; index++) {
			if (index > 2 && !player.getRights().isOrInherits(Right.REGULAR_DONATOR)) {
				player.sendMessage("You must be a donator to block more tasks.");
				return;
			} else if (removed[index].isEmpty()) {
				player.sendMessage(String.format("You spend %d to block %s tasks.", rankPoints, task.get().getFormattedName()));
				removed[index] = task.get().getPrimaryName();
				points -= rankPoints;
				task = Optional.empty();
				taskAmount = 0;
				updateCurrentlyRemoved();
				updatePoints();
				return;
			}
		}

		player.sendMessage("You don't have any open slots left to block tasks.");
	}

	public void removeCurrentTask() {
		task = Optional.empty();
		taskAmount = 0;
	}

	/**
	 * Gets the cost to block a task.
	 */
	public int getBlockTaskCost() {
		if (player.amDonated >= 5 && player.amDonated <= 49) {
			return 80;
		} else if (player.amDonated >= 50 && player.amDonated <= 99) {
			return 80;
		} else if (player.amDonated >= 100 && player.amDonated <= 199) {
			return 70;
		} else if (player.amDonated >= 200 && player.amDonated <= 299) {
			return 70;
		} else if (player.amDonated >= 300 && player.amDonated <= 499) {
			return 60;
		} else if (player.amDonated >= 500 && player.amDonated <= 999) {
			return 50;
		} else if (player.amDonated >= 1000) {
			return 40;
		}
		return 100;
	}

	/**
	 * Attempt to unlock a {@link SlayerUnlock}
	 * @param unlock the unlock
	 * @return <code>true</code> if unlocked
	 */
	public boolean unlock(SlayerUnlock unlock, int cost) {
		if (unlocks.contains(unlock)) {
			return false;
		} else if (points >= cost) {


			switch (unlock) {
				case IMBUE_HELMET:
					int slayerHelmet = 11864;
					int slayerHelmetI = 11865;
					if (!player.getItems().playerHasItem(11864) &&
							!player.getItems().playerHasItem(19639) &&
							!player.getItems().playerHasItem(24370) &&
							!player.getItems().playerHasItem(19643) &&
							!player.getItems().playerHasItem(19647) &&
							!player.getItems().playerHasItem(23073) &&
							!player.getItems().playerHasItem(21264) &&
							!player.getItems().playerHasItem(21888) ) {
						player.sendMessage("You need a slayer helmet in your inventory to do this.");
						return false;
					}
					if (player.getItems().playerHasItem(11864)) {
						slayerHelmet = 11864;
						slayerHelmetI = 11865;
					}
					if (player.getItems().playerHasItem(21888)) {
						slayerHelmet = 21888;
						slayerHelmetI = 21890;
					}
					if (player.getItems().playerHasItem(21264)) {
						slayerHelmet = 21264;
						slayerHelmetI = 21266;
					}
					if (player.getItems().playerHasItem(23073)) {
						slayerHelmet = 23073;
						slayerHelmetI = 23075;
					}
					if (player.getItems().playerHasItem(24370)) {
						slayerHelmet = 24370;
						slayerHelmetI = 24444;
					}
					if (player.getItems().playerHasItem(19639)) {
						slayerHelmet = 19639;
						slayerHelmetI = 19641;
					}
					if (player.getItems().playerHasItem(19643)) {
						slayerHelmet = 19643;
						slayerHelmetI = 19645;
					}
					if (player.getItems().playerHasItem(19647)) {
						slayerHelmet = 19647;
						slayerHelmetI = 19649;
					}
					points -= cost;
					player.getItems().deleteItem2(slayerHelmet, 1);
					player.getItems().addItem(slayerHelmetI, 1);
					player.buySlayerTimer = System.currentTimeMillis();
					player.sendMessage("You imbue the slayer helmet and create an imbued slayer helmet.");
					return true;
				default:
					points -= cost;
					unlocks.add(unlock);
					return true;
			}

		} else {
			player.sendMessage(String.format("You need %d Slayer points to unlock this.", cost));
			return false;
		}
	}

	/**
	 * Attempt to unlock a {@link TaskExtension}
	 * @param extension the extension
	 * @return <code>true</code> if extended
	 */
	public boolean extend(TaskExtension extension, int cost) {
		if (extensions.contains(extension)) {
			return false;
		} else if (points >= cost) {
			points -= cost;
			extensions.add(extension);
			player.sendMessage(String.format("%s tasks will now be extended.", Misc.formatPlayerName(extension.name()).replaceAll("_", " ")));
			return true;
		} else {
			player.sendMessage(String.format("You need %d Slayer points to extend this task.", cost));
			return false;
		}
	}

	public void updatePoints() {
		player.getPA().sendFrame126("Slayer Points: " + points, 41011);
		player.getPA().sendFrame126("Slayer Points: " + points, 41511);
		player.getPA().sendFrame126("Slayer Points: " + points, 42011);
		player.getPA().sendFrame126("@red@Slayer Points: @or2@" + points, 7336);
	}

	public void updateCurrentlyRemoved() {
		for (int index = 0; index < removed.length; index++) {
			if (removed[index].isEmpty()) {
				player.getPA().sendFrame126("", 42014 + index);
			} else {
				player.getPA().sendFrame126(removed[index], 42014 + index);
			}
		}
	}


	public boolean onActionButton(int actionId) {
		switch (actionId) {

			case 160052:
				int amount = 60_000;
				if (System.currentTimeMillis() - player.buySlayerTimer < 500) {
					return true;
				}
				if (points < 50) {
					player.sendMessage("You need at least 50 slayer points to gain " + amount + " Experience.");
					return true;
				}
				if (!player.getMode().canBuyExperienceRewards()) {
					player.sendMessage("Non-Normies are currently unable to purchase xp rewards from slayer shop.");
					return true;
				}
				player.buySlayerTimer = System.currentTimeMillis();
				points -= 50;
				player.getPA().addSkillXP(amount, 18, true);
				player.sendMessage("You spend 50 slayer points and gain " + amount + " experience in slayer.");
				updatePoints();
				return true;

			case 160054:
				if (System.currentTimeMillis() - player.buySlayerTimer < 500) {
					return true;
				}
				if (points < 35) {
					player.sendMessage("You need at least 35 slayer points to buy Slayer darts.");
					return true;
				}
				if (player.getItems().freeSlots() < 2 && !player.getItems().playerHasItem(560) && !player.getItems().playerHasItem(558)) {
					player.sendMessage("You need at least 2 free slots to purchase this.");
					return true;
				}
				player.buySlayerTimer = System.currentTimeMillis();
				points -= 35;
				player.sendMessage("You spend 35 slayer points and acquire 250 casts of Slayer darts.");
				player.getItems().addItem(558, 1000);
				player.getItems().addItem(560, 250);
				updatePoints();
				return true;

			case 160055:
				if (System.currentTimeMillis() - player.buySlayerTimer < 500) {
					return true;
				}
				if (points < 25) {
					player.sendMessage("You need at least 25 slayer points to buy Broad arrows.");
					return true;
				}
				if (player.getItems().freeSlots() < 1 && !player.getItems().playerHasItem(4160)) {
					player.sendMessage("You need at least 1 free slot to purchase this.");
					return true;
				}
				player.buySlayerTimer = System.currentTimeMillis();
				points -= 25;
				player.sendMessage("You spend 25 slayer points and acquire 250 Broad arrows.");
				player.getItems().addItem(4160, 250);
				updatePoints();
				return true;

			case 160053:
//			if (System.currentTimeMillis() - player.buySlayerTimer < 1000) {
//				return true;
//			}
//			if (points < 25) {
//				player.sendMessage("You need at least 25 slayer points to buy Slayer's respite.");
//				return true;
//			}
//			if (player.getItems().freeSlots() < 1) {
//				player.sendMessage("You need at least 1 free slot to purchase this.");
//				return true;
//			}
//			player.buySlayerTimer = System.currentTimeMillis();
//			points -= 25;
//			player.sendMessage("You spend 25 slayer points and acquire a useful Slayer's respite.");
//			player.getItems().addItem(5759, 1);
//			updatePoints();
				player.sendMessage("You cannot purchase this at the moment.");
				return true;

			case 160057:
				if (System.currentTimeMillis() - player.buySlayerTimer < 3000) {
					return true;
				}
				if (biggerBossTasks) {
					player.getDH().sendDialogues(75, 6797);
					return true;
				}
				if (points < 100) {
					player.sendMessage("You need 100 slayer points to extend boss tasks.");
					return true;
				}
				points -= 100;
				biggerBossTasks = true;
				player.buySlayerTimer = System.currentTimeMillis();
				player.sendMessage("You will now get extended boss tasks.");
				updatePoints();
				return true;

			case 162042:
				if (learnedCerberusRoute) {
					player.sendMessage("You already know this.");
					return false;
				}
				if (points < 1250) {
					player.sendMessage("You need 1250 slayer points to learn this.");
					return true;
				}
				points -= 1250;
				learnedCerberusRoute = true;
				player.buySlayerTimer = System.currentTimeMillis();
				player.sendMessage("You've successfully learned the route to cerberus.");
				updatePoints();
				return true;

			case 40132:
				setColor("black");
				player.sendMessage("Color chosen: Black");
				return true;

			case 40133:
				setColor("green");
				player.sendMessage("Color chosen: Green");
				return true;

			case 25155:
				setColor("red");
				player.sendMessage("Color chosen: Red");
				return true;

			case 25160:
				setColor("revert");
				player.sendMessage("Color chosen: Revert");
				return true;

			case 160058:
				//player.getPA().showInterface(10294);
				player.sendMessage("@red@You can make a recolored helmet by using the head on your slayer helmet.");
				return true;

			case 40122:
				if (getColor() == null) {
					player.sendMessage("Please choose a color.");
					return false;
				}
				if (!player.getItems().playerHasItem(SLAYER_HELMETS.REVERT.getRegular()) && !player.getItems().playerHasItem(SLAYER_HELMETS.REVERT.getImbued())) {
					player.sendMessage("You must have a slayer helmet to color.");
					return false;
				}
				if (Objects.equals(getColor(), "revert")) {
					player.sendMessage("Currently you must do this by right clicking the item in question.");
					return false;
				}
				if (getPoints() < 500) {
					player.sendMessage("You do not have enough slayer points to do this.");
					return false;
				}
				if (player.getItems().playerHasItem(SLAYER_HELMETS.REVERT.getRegular())) {
					switch (getColor()) {
						case "black":
							if (player.getItems().playerHasItem(SLAYER_HELMETS.BLACK.getHead(), 1)) {
								player.getItems().deleteItem(SLAYER_HELMETS.REVERT.getRegular(), 1);
								player.getItems().deleteItem(SLAYER_HELMETS.BLACK.getHead(), 1);
								player.getItems().addItem(SLAYER_HELMETS.BLACK.getRegular(), 1);
								points -= 500;
							} else {
								player.sendMessage("You need a KBD Head to do this.");
								return false;
							}
							break;
						case "green":
							if (player.getItems().playerHasItem(SLAYER_HELMETS.GREEN.getHead(), 1)) {
								player.getItems().deleteItem(SLAYER_HELMETS.REVERT.getRegular(), 1);
								player.getItems().deleteItem(SLAYER_HELMETS.GREEN.getHead(), 1);
								player.getItems().addItem(SLAYER_HELMETS.GREEN.getRegular(), 1);
								points -= 500;
							} else {
								player.sendMessage("You need a KQ Head to do this.");
								return false;
							}
							break;
						case "red":
							if (player.getItems().playerHasItem(SLAYER_HELMETS.RED.getHead(), 1)) {
								player.getItems().deleteItem(SLAYER_HELMETS.REVERT.getRegular(), 1);
								player.getItems().deleteItem(SLAYER_HELMETS.RED.getHead(), 1);
								player.getItems().addItem(SLAYER_HELMETS.RED.getRegular(), 1);
								points -= 500;
							} else {
								player.sendMessage("You need an Abyssal Head to do this.");
								return false;
							}
							break;
					}
				} else if (player.getItems().playerHasItem(SLAYER_HELMETS.REVERT.getImbued())) {
					switch (getColor()) {
						case "black":
							if (player.getItems().playerHasItem(SLAYER_HELMETS.BLACK.getHead(), 1)) {
								player.getItems().deleteItem(SLAYER_HELMETS.REVERT.getImbued(), 1);
								player.getItems().deleteItem(SLAYER_HELMETS.BLACK.getHead(), 1);
								player.getItems().addItem(SLAYER_HELMETS.BLACK.getImbued(), 1);
								points -= 500;
							} else {
								player.sendMessage("You need a KBD Head to do this.");
								return false;
							}
							break;
						case "green":
							if (player.getItems().playerHasItem(SLAYER_HELMETS.GREEN.getHead(), 1)) {
								player.getItems().deleteItem(SLAYER_HELMETS.REVERT.getImbued(), 1);
								player.getItems().deleteItem(SLAYER_HELMETS.GREEN.getHead(), 1);
								player.getItems().addItem(SLAYER_HELMETS.GREEN.getImbued(), 1);
								points -= 500;
							} else {
								player.sendMessage("You need a KQ Head to do this.");
								return false;
							}
							break;
						case "red":
							if (player.getItems().playerHasItem(SLAYER_HELMETS.RED.getHead(), 1)) {
								player.getItems().deleteItem(SLAYER_HELMETS.REVERT.getImbued(), 1);
								player.getItems().deleteItem(SLAYER_HELMETS.RED.getHead(), 1);
								player.getItems().addItem(SLAYER_HELMETS.RED.getImbued(), 1);
								points -= 500;
							} else {
								player.sendMessage("You need an Abyssal Head to do this.");
								return false;
							}
							break;
					}
				}
				return true;
		}
		return false;
	}

	public void revertHelmet(int helmet) {
		if (ItemAssistant.getItemName(helmet).contains("(i)")) {
			player.getItems().deleteItem(helmet, 1);
			player.getItems().addItem(SLAYER_HELMETS.REVERT.getImbued(), 1);
		} else {
			player.getItems().deleteItem(helmet, 1);
			player.getItems().addItem(SLAYER_HELMETS.REVERT.getRegular(), 1);
		}
		player.sendMessage("You successfully reverted your slayer helmet to normal.");
	}

	private enum SLAYER_HELMETS {
		BLACK(19639, 19641, 7980),
		GREEN(19643, 19645, 7981),
		RED(19647, 19649, 7979),
		PURPLE(21264, 21266, 21275),
		REVERT(11864, 11865, -1),
		HYDRA(23073, 23075, 23077),
		TWISTED(24370, 24444, 24466);

		private final int regular;
		private final int imbued;
		private final int head;
		public int getRegular() {
			return regular;
		}
		public int getImbued() {
			return imbued;
		}
		public int getHead() {
			return head;
		}
		SLAYER_HELMETS(int regular, int imbued, int head) {
			this.regular = regular;
			this.imbued = imbued;
			this.head = head;
		}
	}

	/**
	 * Modifies the current amount of slayer points the player has
	 *
	 * @param points the amount of points
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * The amount of points the player has in slayer
	 *
	 * @return the amount of points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * The amount of the slayer task the player has remaining
	 *
	 * @return the amount of the task
	 */
	public int getTaskAmount() {
		return taskAmount;
	}

	/**
	 * Modifies the amount of a slayer task the player has
	 *
	 * @param taskAmount the new task amount
	 */
	public void setTaskAmount(int taskAmount) {
		this.taskAmount = taskAmount;
	}

	/**
	 * The identification value of the slayer master this player goes to
	 *
	 * @return the identification value of the slayer master
	 */
	public int getMaster() {
		return master;
	}

	/**
	 * Modifies the variable that represents the master the player goes to
	 *
	 * @param master the new slayer master
	 */
	public void setMaster(int master) {
		this.master = master;
	}

	/**
	 * The amount of tasks that a player has completed from the same master consecutively
	 *
	 * @return int number of tasks
	 */
	public int getConsecutiveTasks() {
		return consecutiveTasks;
	}

	/**
	 * Modifies the current amount of consecutive tasks completed.
	 *
	 * @param consecutiveTasks the amount of consecutive tasks completed
	 */
	public void setConsecutiveTasks(int consecutiveTasks) {
		this.consecutiveTasks = consecutiveTasks;
	}

	/**
	 * Modifies the currently removed tasks
	 *
	 * @param removed the new removed tasks
	 */
	public void setRemoved(String[] removed) {
		Preconditions.checkArgument(removed.length <= this.removed.length, "Removed length is too high!");
		for (int index = 0; index < removed.length; index++) {
			this.removed[index] = removed[index];
		}
	}

	/**
	 * The array of task names that are removed
	 *
	 * @return the removed tasks
	 */
	public String[] getRemoved() {
		return removed;
	}

	/**
	 * The slayer task that this player currently has assigned
	 *
	 * @return the task the player has assigned
	 */
	public Optional<Task> getTask() {
		return task;
	}

	/**
	 * Sets the current task to that of the parameter. The current task is the task the player has received from their slayer master.
	 *
	 * @param task the new slayer task
	 */
	public void setTask(Optional<Task> task) {
		this.task = task;
	}

	public boolean isBiggerBossTasks() {
		return biggerBossTasks;
	}
	public void setBiggerBossTasks(boolean biggerBossTasks) {
		this.biggerBossTasks = biggerBossTasks;
	}

	public boolean isCerberusRoute() {
		return learnedCerberusRoute;
	}
	public void setCerberusRoute(boolean learnedCerberusRoute) {
		this.learnedCerberusRoute = learnedCerberusRoute;
	}

	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

	public List<TaskExtension> getExtensions() {
		return extensions;
	}

	public List<SlayerUnlock> getUnlocks() {
		return unlocks;
	}
}
