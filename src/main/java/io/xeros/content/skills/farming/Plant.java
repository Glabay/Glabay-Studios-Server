package io.xeros.content.skills.farming;


import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.skills.Skill;
import io.xeros.model.Animation;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;

public class Plant {

	public int patch;
	public int plant;
	public long time = 0;
	public byte stage = 0;
	public byte disease = -1;
	public byte watered = 0;
	public boolean magicCan = false;

	private boolean dead = false;

	public byte harvested = 0;
	boolean harvesting = false;

	public Plant(int patchId, int plantId) {
		patch = patchId;
		plant = plantId;
	}

	public void water(Player player, int item) {
		if (item == 5332) {
			return;
		}

		if (getPatch().seedType == SeedType.HERB) {
			player.sendMessage("This patch doesn't need watering.");
			return;
		}

		if (isWatered()) {
			player.sendMessage("Your plants have already been watered.");
			return;
		}

		if (item == 5331) {
			player.sendMessage("Your watering can is empty.");
			return;
		}

		player.sendMessage("You water the plant.");
//		if (item != 25025) {
//			player.getItems().deleteItem(item, 1);
//			player.getItems().addItem(item > 5333 ? (item - 1) : (item - 2), 1);
//		}
		player.startAnimation(new Animation(2293));
		watered = -1;
		doConfig(player);
	}

	public void setTime() {
		time = System.currentTimeMillis();
	}

	public void click(Player player, int option) {
		Plants plant = Plants.values()[this.plant];
		if (option == 1) {
			if (stage == plant.stages) {
				harvest(player);
			} else if (plant.type == SeedType.HERB) {
				statusMessage(player, plant);
			}
		} else if ((option == 2)) {
			statusMessage(player, plant);
		}
	}

	private void statusMessage(Player player, Plants plant) {
		if (dead) {
			player.sendMessage("Oh dear, your plants have died!");
		} else if (isDiseased()) {
			player.sendMessage("Your plants are diseased!");
		} else if (stage == plant.stages) {
			player.sendMessage("Your plants are healthy and ready to harvest.");
		} else {
			int stagesLeft = plant.stages - stage;
			String s = "Your plants are healthy";
			if(!isWatered() && getPatch().seedType != SeedType.HERB)
				s += " but need some water to survive.";
			else {
				s += " and are currently growing (about " + (stagesLeft * (plant.getMinutes() / plant.stages)) + " minutes remain).";
			}
			player.sendMessage(s);
		}
	}

	public void harvest(final Player player) {
		if(harvesting)
			return;
		List<Integer> harvestItemNeeded = Lists.newArrayList();
		harvestItemNeeded.add(getPatch().harvestItem);
		if (getPatch().harvestItem == Items.SECATEURS) {
			harvestItemNeeded.add(Items.MAGIC_SECATEURS);
			harvestItemNeeded.add(Items.MAGIC_SECATEURS);
		}

		if (harvestItemNeeded.stream().anyMatch(item -> player.getItems().playerHasItem(item) || player.getItems().isWearingItem(item))) {
			final Plant instance = this;
			player.setTickable((container, player1) -> {
				if (container.getTicks() % 3 == 0) {
					if (!player.isWalkingQueueEmpty()) {
						container.stop();
						return;
					}
					if (player.getInventory().freeInventorySlots() == 0) {
						player.sendMessage("Your inventory is full.");
						container.stop();
						return;
					}
					player.startAnimation(new Animation(2282));
					GameItem add = null;
					int id = Plants.values()[plant].harvest;
					add = ItemDef.forId(id).isNoted() ? new GameItem(ItemDef.forId(id).getNoteId(), 1) : new GameItem(id, 1);
					player.getItems().addItem(add.id(), add.amount());

					int petRate = player.skillingPetRateScroll ? (int) (10000 * .75) : 10000;
					if (Misc.random(petRate) == 2 && player.getItems().getItemCount(20661, false) == 0 && player.petSummonId != 7352) {
						PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + player.getDisplayName() + "</col> found a <col=CC0000>Tangleroot</col> pet!");
						player.getItems().addItemUnderAnyCircumstance(20661, 1);
						player.getCollectionLog().handleDrop(player, 5, 20661, 1);
					}

					String name = ItemDef.forId(Plants.values()[plant].harvest).getName();
					if (name.endsWith("s"))
						name = name.substring(0, name.length() - 1);
					player.sendMessage("You harvest " + Misc.anOrA(name) + " " + name + ".");
					player.getPA().addSkillXPMultiplied((int) Plants.values()[plant].harvestExperience * (1 + (Farming.getFarmingPieces(player) * 0.12)), Skill.FARMING.getId(), true);

					harvested++;

					if (add.id() == Items.GRIMY_TORSTOL && getPatch() == FarmingPatches.FALADOR_HERB) {
						player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.HARVEST_TORSTOL);
					}

					int min = 7;
					if (magicCan)
						min += 7;
					if (player.getItems().isWearingItem(Items.MAGIC_SECATEURS))
						min += 4;

					Achievements.increase(player, AchievementType.FARM, 1);
					if (id == 225) {
						player.getInventory().addAnywhere(new ImmutableItem(225, 2));
						player.getFarming().remove(instance);
						container.stop();
						return;
					}
					if (getPatch().seedType == SeedType.FLOWER || harvested >= min && Misc.trueRand(4) <= 1) {
						player.getFarming().remove(instance);
						container.stop();
						return;
					}
				}
			});

		} else {
			String name = ItemDef.forId(FarmingPatches.values()[patch].harvestItem).getName();
			player.sendMessage("You need " + Misc.anOrA(name) + " " + name + " to harvest these plants.");
		}
	}

	public boolean useItemOnPlant(final Player player, int item) {
		if (item == 952) {
			player.startAnimation(new Animation(830));
			player.getFarming().remove(this);
			player.setTickable((container, player1) -> {
				if (container.getTicks() == 2) {
					player.sendMessage("You remove your plants from the plot.");
					player.startAnimation(new Animation(65535));
					container.stop();
				}
			});
			return true;
		}
		if (item == 6036) {
			if (dead) {
				player.sendMessage("Your plant is dead!");
			} else if (isDiseased()) {
				player.sendMessage("You cure the plant.");
				player.startAnimation(new Animation(2288));
				player.getItems().deleteItem(6036, 1);
				disease = -1;
				doConfig(player);
			} else {
				player.sendMessage("Your plant does not need this.");
			}

			return true;
		}
		if ((item >= 5331) && (item <= 5340) || item == 25025) {
			water(player, item);
			return true;
		}

		return false;
	}

	public void process(Player player) {
		if (dead || stage >= Plants.values()[plant].stages) {
			return;
		}

		long elapsed = (System.currentTimeMillis() - time) / 60_000;

		Plants plant = Plants.values()[this.plant];
		int grow = plant.getMinutes() / plant.stages;
		if (grow == 0)
			grow = 1;


//		if (Server.isDebug()) {
//			elapsed = plant.minutes * plant.stages;
//		}
		if (elapsed >= grow) {
			for (int i = 0; i < elapsed / grow; i++) {
				if(isWatered() || Server.isDebug() || getPatch().seedType == SeedType.HERB) {
					stage++;
					player.getFarming().doConfig();
					if (stage >= plant.stages) {
						player.sendMessage("<img=10> @blu@A seed you planted has finished growing!");
						return;
					}
				}

			}
			setTime();
		}
	}

	public void doConfig(Player player) {
		player.getFarming().doConfig();
	}

	public int getConfig() {
		Plants plants = Plants.values()[plant];
		return (plants.healthy + stage + (isWatered() && stage == 0 ? 64 : 0));
	}

	public FarmingPatches getPatch() {
		return FarmingPatches.values()[patch];
	}

	public boolean isDiseased() {
		return disease > -1;
	}

	public boolean isWatered() {
		return watered == -1;
	}
}
