package io.xeros.content.skills.farming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.common.base.Preconditions;
import io.xeros.Server;
import io.xeros.content.skills.Skill;
import io.xeros.model.Animation;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

public class Farming {

	public static final int HARVEST_ANIMATION = 2275;

	/**
	 * Placeholder for Tree harvesting
	 */
	public static final int HARVEST_CHOPPING = -2;

	private static final int[] farmingOutfit = { 13646, 13642, 13640, 13644 };

	private final Player player;
	private Plant[] plants = new Plant[50];
	private GrassyPatch[] patches = new GrassyPatch[50];

	public Farming(Player player) {
		this.player = player;

		for (int i = 0; i < patches.length; i++)
			if (patches[i] == null)
				patches[i] = new GrassyPatch();
	}

	public static int getFarmingPieces(Player player) {
		int pieces = 0;
		for (int aFarmingOutfit : farmingOutfit) {
			if (player.getItems().isWearingItem(aFarmingOutfit)) {
				pieces++;
			}
		}
		return pieces;
	}
	public void handleLogin() {
		doConfig();
		player.addTickable((container, player1) -> sequence());
	}

	public void handleObjectClick(int objectId, int x, int y, int option) {
		player.getFarming().click(player, x, y, option);
	}

	public void handleItemOnObject(int itemId, int objectId, int objectX, int objectY) {
		if (fillWateringCans(itemId, objectId, objectX, objectY))
			return;
		if (plant(itemId, objectX, objectY))
			return;
		if (useItemOnPlant(itemId, objectX, objectY))
			return;
	}

	public void regionChanged() {
		player.getFarming().doConfig();
	}

	public void sequence() {
		for (Plant i : plants) {
			if (i != null) {
				i.process(player);
			}
		}
		for (int i = 0; i < patches.length; i++) {
			if (i >= FarmingPatches.values().length)
				break;
			if ((patches[i] != null) && (!inhabited(FarmingPatches.values()[i].bottomLeft.getX(), FarmingPatches.values()[i].bottomLeft.getY()))) {
				patches[i].process(player, i);
			}
		}
	}

	public int config(FarmingPatches patch) {
		if (inhabited(patch.bottomLeft.getX(), patch.bottomLeft.getY())) {
			for (Plant plant : plants) {
				if (plant != null && plant.getPatch() == patch) {
					return plant.getConfig();
				}
			}
		}
		
		return patches[patch.ordinal()].stage;
	}
	
	public void doConfig() {
		FarmingPatches[] patches = {
				FarmingPatches.FALADOR_HERB,
				FarmingPatches.CATHERBY_HERB,
				FarmingPatches.ARDOUGNE_HERB,
				FarmingPatches.PHAS_HERB

		};

		int x, y;
		if (player.absX == -1) {
			x = player.getTeleportToX();
			y = player.getTeleportToY();
		} else {
			x = player.getX();
			y = player.getY();
		}

		FarmingPatches closest = null;
		int lowest = 0;
		for (FarmingPatches patch : patches) {
			int dist = (int) Misc.distance(x, y, patch.bottomLeft.getX(), patch.bottomLeft.getY());
			if (closest == null || dist < lowest) {
				closest = patch;
				lowest = dist;
			}
		}

		int config = 0;

		switch (closest) {
			case FALADOR_HERB:
				config = (config(FarmingPatches.FALADOR_HERB) << 24)
						+ (config(FarmingPatches.FALADOR_FLOWER) << 16)
						+ (config(FarmingPatches.FALADOR_ALLOTMENT_SOUTH) << 8)
						+ (config(FarmingPatches.FALADOR_ALLOTMENT_NORTH));
				break;
			case CATHERBY_HERB:
				/**
				 * //		529(<<0) = Fruit Tree
				 * //		529(<<24) = Herb
				 * //		529(<<16) = Flower
				 * //		529(<<8) = South Allotment
				 * //		529(<<0) = North Allotment
				 * //		511(<<24) = Compost Bin
				 */
				config = (config(FarmingPatches.CATHERBY_HERB) << 24)
						+ (config(FarmingPatches.CATHERBY_FLOWER) << 16)
						+ (config(FarmingPatches.CATHERBY_ALLOTMENT_SOUTH) << 8)
						+ (config(FarmingPatches.CATHERBY_ALLOTMENT_NORTH));
				break;
			case ARDOUGNE_HERB:
				config = (config(FarmingPatches.ARDOUGNE_HERB) << 24)
						+ (config(FarmingPatches.ARDOUGNE_FLOWER) << 16)
						+ (config(FarmingPatches.ARDOUGNE_ALLOTMENT_SOUTH) << 8)
						+ (config(FarmingPatches.ARDOUGNE_ALLOTMENT_NORTH));
				break;
			case PHAS_HERB:
				config = (config(FarmingPatches.PHAS_HERB) << 24)
						+ (config(FarmingPatches.PHAS_FLOWER) << 16)
						+ (config(FarmingPatches.PHAS_ALLOTMENT_EAST) << 8)
						+ (config(FarmingPatches.PHAS_ALLOTMENT_WEST));
				break;
		}

		player.getPA().sendConfig(529, config);
	}

	public boolean fillWateringCans(int item, int objectId, int x, int y) {
		if (objectId == 15936) {
			int[] cans = {5331, 5333, 5334, 5335, 5336, 5337, 5338, 5339};
			int full = 5340;
			for (int can : cans) {
				if (can == item) {
					player.setTickable((container, player1) -> {
						if (container.getTicks() == 3) {
							for (int i = 0; i < 28; i++) {
								for (int can1 : cans) {
									if (can1 == player.playerItems[i] - 1) {
										player.getItems().deleteItem(can1, 1);
										player.getItems().addItem(full, 1);
										player.startAnimation(new Animation(2295));
										player.facePosition(new Position(x, y));
										player.sendMessage("You fill the can with water.");
										return;
									}
								}
							}
							container.stop();
						}
					});
					return true;
				}
			}
		}
		return false;
	}
	
	public void clear() {
		for (int i = 0; i < plants.length; i++) {
			plants[i] = null;
		}

		for (int i = 0; i < patches.length; i++) {
			patches[i] = new GrassyPatch();
		}
	}

	public void insert(Plant patch) {
		for (int i = 0; i < plants.length; i++)
			if (plants[i] == null) {
				plants[i] = patch;
				break;
			}
	}

	public boolean inhabited(int x, int y) {
		for (int i = 0; i < plants.length; i++) {
			if (plants[i] != null) {
				FarmingPatches patch = plants[i].getPatch();
				if ((x >= patch.bottomLeft.getX()) && (y >= patch.bottomLeft.getY()) && (x <= patch.topLeft.getX()) && (y <= patch.topLeft.getY())) {
					if (isPatchException(x, y, patch)) {
						continue;
					}
					return true;
				}
			}
		}

		return false;
	}

	public int getGrassyPatch(int x, int y) {
		for (int i = 0; i < FarmingPatches.values().length; i++) {
			FarmingPatches patch = FarmingPatches.values()[i];
			if (x >= patch.bottomLeft.getX() && y >= patch.bottomLeft.getY() && x <= patch.topLeft.getX() && y <= patch.topLeft.getY()) {
				if (!isPatchException(x, y, patch)) {
					if (inhabited(x, y) || patches[i] == null)
						break;
					return i;
				}
			}
		}

		return -1;
	}

	public Plant getPlantedPatch(int x, int y) {
		for (int i = 0; i < FarmingPatches.values().length; i++) {
			FarmingPatches patch = FarmingPatches.values()[i];
			if (x >= patch.bottomLeft.getX() && y >= patch.bottomLeft.getY() && x <= patch.topLeft.getX() && y <= patch.topLeft.getY()) {
				if (!isPatchException(x, y, patch)) {
					for (Plant plant : plants) {
						if (plant != null && plant.patch == patch.ordinal()) {
							return plant;
						}
					}
				}
			}
		}

		return null;
	}

	private boolean isPatchException(int x, int y, FarmingPatches patch) {
		if(x == 3054 && y == 3307 && patch != FarmingPatches.FALADOR_FLOWER)
			return true;
		if (x == 3601 && y == 3525 && patch != FarmingPatches.PHAS_FLOWER)
			return true;
		return false;
	}

	public boolean click(Player player, int x, int y, int option) {
		int grass = getGrassyPatch(x, y);
		if (grass != -1) {
			if (option == 1) {
				patches[grass].click(player, option, grass);
			}
			return true;
		} else {
			Plant plant = getPlantedPatch(x, y);

			if (plant != null) {
				plant.click(player, option);
				return true;
			}
		}

		return false;
	}

	public void remove(Plant plant) {
		for (int i = 0; i < plants.length; i++) {
			if ((plants[i] != null) && (plants[i] == plant)) {
				patches[plants[i].getPatch().ordinal()].setTime();
				plants[i] = null;
				doConfig();
				return;
			}
		}
	}

	public boolean useItemOnPlant(int item, int x, int y) {
		if (item == Items.RAKE) {
			int patch = getGrassyPatch(x, y);
			if (patch != -1) {
				patches[patch].rake(player, patch);
				return true;
			}
		}

		Plant plant = getPlantedPatch(x, y);
		if (plant != null) {
			plant.useItemOnPlant(player, item);
			return true;
		}

		return false;
	}

	public boolean plant(int seed, int x, int y) {
		if (!Plants.isSeed(seed)) {
			return false;
		}

		for (FarmingPatches patch : FarmingPatches.values()) {
			if ((x >= patch.bottomLeft.getX()) && (y >= patch.bottomLeft.getY()) && (x <= patch.topLeft.getX()) && (y <= patch.topLeft.getY())) {
				if (isPatchException(x, y, patch)) {
					continue;
				}
				if (!patches[patch.ordinal()].isRaked()) {
					player.sendMessage("This patch needs to be raked before anything can grow in it.");
					return true;
				}

				for (Plants plant : Plants.values()) {
					if (plant.seed == seed) {
						if (player.getPA().getLevelForXP(player.playerXP[Skill.FARMING.getId()]) >= plant.level) {
							if (inhabited(x, y)) {
								player.sendMessage("There are already seeds planted here.");
								return true;
							}

							if (patch.seedType != plant.type) {
								player.sendMessage("You can't plant this type of seed here.");
								return true;
							}

							if (player.getItems().playerHasItem(patch.planter)) {
								player.startAnimation(new Animation(2291));
								player.sendMessage("You bury the seed in the dirt.");
								player.getItems().deleteItem(seed, 1);
								Plant planted = new Plant(patch.ordinal(), plant.ordinal());
								planted.setTime();
								insert(planted);
								if (player.getItems().playerHasItem(25025)) { // Magic watering can (doesn't exist in osrs)
									planted.watered = -1;
									planted.magicCan = true;
									player.sendMessage("Your magic watering can waters and fertilizes the soil.");
								}
								doConfig();
								player.getPA().addSkillXPMultiplied((int)plant.plantExperience, Skill.FARMING.getId(), true);
							} else {
								String name = ItemDef.forId(patch.planter).getName();
								player.sendMessage("You need " + Misc.anOrA(name) + " " +name+ " to plant seeds.");
							}

						} else {
							player.sendMessage("You need a Farming level of " + plant.level + " to plant this.");
						}

						return true;
					}
				}

				return false;
			}
		}

		return false;
	}

	private String getDirectory() {
		return Server.getSaveDirectory() + "farming/";
	}

	private String getFile() {
		return getDirectory() + player.getLoginName() + ".txt";
	}

	public void save() {
		try {
			if (!new File(getDirectory()).exists()) {
				Preconditions.checkState(new File(getDirectory()).mkdirs());
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(getFile()));
			for (int i = 0; i < patches.length; i++) {
				if (i >= FarmingPatches.values().length)
					break;
				if (patches[i] != null) {
					writer.write("[PATCH]");
					writer.newLine();
					writer.write("patch: "+i);
					writer.newLine();
					writer.write("stage: "+patches[i].stage);
					writer.newLine();
					writer.write("time: "+patches[i].time);
					writer.newLine();
					writer.write("END PATCH");
					writer.newLine();
					writer.newLine();
				}
			}
			for (int i = 0; i < plants.length; i++) {
				if (plants[i] != null) {
					writer.write("[PLANT]");
					writer.newLine();
					writer.write("patch: "+plants[i].patch);
					writer.newLine();
					writer.write("plant: "+plants[i].plant);
					writer.newLine();
					writer.write("stage: "+plants[i].stage);
					writer.newLine();
					writer.write("watered: "+plants[i].watered);
					writer.newLine();
					writer.write("harvested: "+plants[i].harvested);
					writer.newLine();
					writer.write("magicCan: "+plants[i].magicCan);
					writer.newLine();
					writer.write("time: "+plants[i].time);
					writer.newLine();
					writer.write("END PLANT");
					writer.newLine();
					writer.newLine();
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public void load() {
		try {
			if (!new File(getFile()).exists())
				return;
			BufferedReader r = new BufferedReader(new FileReader(getFile()));
			int stage = -1, patch = -1, plant = -1, watered = -1, harvested = -1;
			boolean magicCan = false;
			long time = -1;
			while(true) {
				String line = r.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				if(line.startsWith("patch"))
					patch = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("stage"))
					stage = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("plant"))
					plant = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("watered"))
					watered = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("harvested"))
					harvested = Integer.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("magicCan"))
					magicCan = Boolean.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.startsWith("time"))
					time = Long.valueOf(line.substring(line.indexOf(":")+2));
				else if(line.equals("END PATCH") && patch >= 0) {
					patches[patch].stage = (byte)stage;
					patches[patch].time = time;
					patch = -1;
				}
				else if(line.equals("END PLANT") && patch >= 0) {
					plants[patch] = new Plant(patch, plant);
					plants[patch].watered = (byte) watered;
					plants[patch].stage = (byte) stage;
					plants[patch].harvested = (byte) harvested;
					plants[patch].time = time;
					plants[patch].magicCan = magicCan;
					patch = -1;
				}
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}


//Catherby
//		529(<<0) = Fruit Tree
//		529(<<24) = Herb
//		529(<<16) = Flower
//		529(<<8) = South Allotment
//		529(<<0) = North Allotment
//		511(<<24) = Compost Bin
//		Falador
//		529(<<0) = Tree
//		529(<<24) = Herb
//		529(<<16) = Flower
//		529(<<8) = South Allotment
//		529(<<0) = North Allotment
//		511(<<0) = Compost Bin
//		Ardougne
//		529(<<24) = Herb
//		529(<<16) = Flower
//		529(<<8) = South Allotment
//		529(<<0) = North Allotment
//		529(<<24) = Compost Bin
//		529(<<0) = Bush
//		Port Phasmatys
//		529(<<24) = Herb
//		529(<<16) = Flower
//		529(<<8) = South Allotment
//		529(<<0) = North Allotment
//		511(<<16) = Compost Bin
//		Lumbridge
//		529(<<0) = Tree
//		Taverley
//		529(<<0) = Tree
//		Varrock
//		529(<<0) = Tree
//		529(<<0) = Bush
//		Gnome Stronghold
//		529(<<0) = Tree
//		529(<<8) = Fruit Tree
//		Tree Gnome Village
//		529(<<0) = Fruit Tree
//		Brimhaven
//		529(<<0) = Fruit Tree
//		Rimmington
//		529(<<0) = Bush
//		Etceteria
//		529(<<0) = Bush
//		Al Kharid
//		529(<<0) = Cactus