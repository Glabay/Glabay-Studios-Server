package io.xeros.content.collection_log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.Vorkath;
import io.xeros.content.item.lootable.impl.RaidsChestRare;
import io.xeros.content.item.lootable.impl.TheatreOfBloodChest;
import io.xeros.content.trails.TreasureTrailsRewardItem;
import io.xeros.content.trails.TreasureTrailsRewards;
import io.xeros.content.trails.RewardLevel;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 10/7/19
 *
 */
public class CollectionLog {

	private static final Logger logger = LoggerFactory.getLogger(CollectionLog.class);

	public static final int PETS_ID = 5;

	/**
	 * Different tabs within interface
	 *
	 */
	public enum CollectionTabType {
		BOSSES, WILDERNESS, RAIDS, MINIGAMES, OTHER
    }
	
	/* Variables */
	private static HashMap<CollectionTabType, ArrayList<Integer>> collectionNPCS;
	private static final int INTERFACE_ID = 23110;

	private boolean groupIronman;
	private String saveName;
	private CollectionLog linked;

	private HashMap<String, ArrayList<GameItem>> collections;

	public CollectionLog() {
		this.collections = new HashMap<>();
	}

	public String getSaveDirectory() {
		if (isGroupIronman()) {
			return Server.getSaveDirectory() + "/gim/collection_log/";
		}
		return Server.getSaveDirectory() + "/collection_log/";
	}

	public HashMap<String, ArrayList<GameItem>> getCollections() {
		return collections;
	}
	
	/**
	 * Initializes the default npcs to be collecting for
	 */
	public static void init() {
		try {
			Path path = Paths.get(Server.getDataDirectory() + "/cfg/collection_npcs.json");
			File file = path.toFile();

			JsonParser parser = new JsonParser();
			if (!file.exists()) {
				return;
			}
			Object obj = parser.parse(new FileReader(file));
			JsonObject jsonUpdates = (JsonObject) obj;

			Type listType = new TypeToken<HashMap<CollectionTabType, ArrayList<Integer>>>() {
			}.getType();

			collectionNPCS = new Gson().fromJson(jsonUpdates, listType);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.out.println("No default NPCs found!");
			collectionNPCS = new HashMap<>();
		}
	}
	
	/**
	 * Opens the interface for a player
	 */
	public void openInterface(Player player) {
		player.setViewingCollectionLog(this);
		resetInterface(player);
		selectTab(player, CollectionTabType.BOSSES);
		//selectCell(0, CollectionTabType.BOSSES);
	}
	
	/**
	 * Clears the interface
	 */
	public void resetInterface(Player player) {
		for(int i = 0; i < 50; i++) {
			player.getPA().sendFrame126("", 23123 + (i * 2));
			player.getPA().sendConfig(520 + i, 0);
		}
		player.getPA().sendConfig(519, 0);
		for(int i = 0; i < 3; i++) {
			player.getPA().sendConfig(571 + i, 0);
		}
	}
	
	/**
	 * Selects a tab within the interface
	 * @param type
	 */
	public void selectTab(Player player, CollectionTabType type) {
		if (collectionNPCS == null || collectionNPCS.isEmpty()) {
			return;
		}
		
		ArrayList<Integer> npcs = collectionNPCS.get(type);
		if (npcs != null) {
			resetInterface(player);
			player.collectionLogTab = type;
			player.previousSelectedCell = 0;
			player.getPA().sendConfig(player.previousSelectedTab == 0 ? 519 : 570 + player.previousSelectedTab, 0);
			player.previousSelectedTab = type.ordinal();
			player.getPA().sendConfig(type.ordinal() == 0 ? 519 : 570 + type.ordinal(), 1);
			for(int i = 0; i < npcs.size(); i++) {
				boolean found = false;
				if (getCollections().containsKey(npcs.get(i) + "")) {
					ArrayList<GameItem> itemsObtained = getCollections().get(npcs.get(i) + "");
					if (itemsObtained != null) {
						List<GameItem> drops = Server.getDropManager().getNPCdrops(npcs.get(i));
						if (npcs.get(i) == 8028) {
							drops = Vorkath.getVeryRareDrops();
						}
						if (npcs.get(i) == 7554) {
							drops = RaidsChestRare.getRareDrops();
						} else if (npcs.get(i) >= 1 && npcs.get(i) <= 4) {
							drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(npcs.get(i)));
						} else if (npcs.get(i) == PETS_ID) {
							drops = PetHandler.getPetIds(true);
						} else if (npcs.get(i) == Npcs.THE_MAIDEN_OF_SUGADINTI) {
							drops = TheatreOfBloodChest.getRareDrops();
						}

						if (drops != null && drops.size() == itemsObtained.size()) {
							found = true;
							String name = (type == CollectionTabType.OTHER ? Misc.optimizeText(RewardLevel.VALUES.get(npcs.get(i)).name().toLowerCase()) : Misc.optimizeText(NpcDef.forId(npcs.get(i)).getName()));
							if (npcs.get(i) == Npcs.THE_MAIDEN_OF_SUGADINTI) {
								name = "Theatre of Blood";
							} else if (npcs.get(i) == Npcs.DUSK_7888) {
								name = "Grotesque Guardians";
							}
							player.getPA().sendFrame126("@gre@" + name, 23123 + (i * 2));
						}
					}
				}
				if (!found) {
					if (npcs.get(i) == PETS_ID) {
						player.getPA().sendFrame126("Pets", 23123 + (i * 2));
					} else {
						String name = type == CollectionTabType.OTHER ? RewardLevel.VALUES.get(npcs.get(i)).getFormattedName() + " clue scroll"
								: Misc.optimizeText(NpcDef.forId(npcs.get(i)).getName());
						if (npcs.get(i) == Npcs.THE_MAIDEN_OF_SUGADINTI) {
							name = "Theatre of Blood";
						} else if (npcs.get(i) == Npcs.DUSK_7888) {
							name = "Grotesque Guardians";
						}
						player.getPA().sendFrame126(name, 23123 + (i * 2));
					}
				}
			}
			selectCell(player, 0, type);
		} else {
			player.sendMessage("There are no collection logs for this type yet.");
		}
	}
	
	/**
	 * Selects a cell from a tab type
	 * @param index
	 * @param type
	 */
	public void selectCell(Player player, int index, CollectionTabType type) {
		if (collectionNPCS == null || collectionNPCS.isEmpty()) {
			return;
		}
		
		ArrayList<Integer> npcs = collectionNPCS.get(type);
		if (npcs != null) {
			if (index >= npcs.size()) {
				return;
			}
			
			player.getPA().sendConfig(520 + player.previousSelectedCell, 0);
			player.previousSelectedCell = index;
			player.getPA().sendConfig(520 + index , 1);

			if (npcs.get(index) == PETS_ID) {
				List<GameItem> pets = PetHandler.getPetIds(false);
				for(GameItem petItem : pets) {
					if (player.getItems().getItemCount(petItem.id(), false) > 0 || (player.hasFollower && player.petSummonId == petItem.id())) {
						PetHandler.Pets petForItem = PetHandler.forItem(petItem.id());
						if (petForItem != null) {
							PetHandler.Pets pet = PetHandler.getPetForParentId(petForItem);
							ArrayList<GameItem> petList = getCollections().get("" + 5);
							if (petList == null || petList.stream().noneMatch(item -> item.id() == pet.getItemId())) {
								player.getCollectionLog().handleDrop(player, 5, pet.getItemId(), 1);
								player.sendMessage("@red@Added missing " + ItemDef.forId(pet.getItemId()).getName() + " to collection log.");
							}
						}
					}
				}
			}
			populateInterface(player, npcs.get(index));
		}
	}
	
	/**
	 * Populates the interface with data
	 * @param npcId
	 */
	public void populateInterface(Player player, int npcId) {
		if (!getCollections().containsKey("" + npcId)) { //If they've never looked at that NPC before, initialize a blank arraylist
			getCollections().put("" + npcId, new ArrayList<>());
			saveToJSON();
		}
		
		String npcName = NpcDef.forId(npcId).getName();
		if (npcId >= 1 && npcId <= 4) {
			npcName = Misc.optimizeText(RewardLevel.VALUES.get(npcId).name().toLowerCase());
		}
		if (npcId == PETS_ID) {
			npcName = "Pets";
		}
		if (npcId == Npcs.THE_MAIDEN_OF_SUGADINTI) {
			npcName = "Theatre of Blood";
		}
		if (npcId == Npcs.DUSK_7888) {
			npcName = "Grotesque Guardians";
		}

		player.getPA().sendFrame126(getSaveName() + "'s Collection Log", 23112);
		player.getPA().sendFrame126(Misc.optimizeText(npcName), 23118);
		player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.getNpcDeathTracker().getKc(npcName), 23120);
		
		//Clear items
		for(int i = 0; i < 198; i++) {
			player.getPA().sendItemToSlotWithOpacity(23231, -1, i, 0, false);
		}
		
		ArrayList<GameItem> items = getCollections().get(npcId + "");
		Server.getDropManager().getDrops(player, npcId);
		if (npcId == 8028) {
			player.dropItems = Vorkath.getVeryRareDrops();
		}
		if (npcId == 7554) {
			player.dropItems = RaidsChestRare.getRareDrops();
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.raidCount, 23120);
		}
		if (npcId >= 1 && npcId <= 4) {
			player.dropItems = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(npcId));
		}
		if (npcId == PETS_ID) {
			player.dropItems = PetHandler.getPetIds(true);
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + items.size(), 23120);
		}
		if (npcId == Npcs.THE_MAIDEN_OF_SUGADINTI) {
			player.dropItems = TheatreOfBloodChest.getRareDrops();
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.tobCompletions, 23120);
		}

		int foundCount = 0;
		for(int i = 0; i < player.dropItems.size(); i++) {
			boolean found = false;
			for(int j = 0; j < items.size(); j++) {
				if (items.get(j).id() == player.dropItems.get(i).id()) {
					player.getPA().sendItemToSlotWithOpacity(23231, items.get(j).id(), i, items.get(j).amount(), false);
					foundCount++;
					found = true;
					break;
				}
			}
			if (!found) {
				player.getPA().sendItemToSlotWithOpacity(23231, player.dropItems.get(i).id(), i, 0, true);
			}
		}
		player.getPA().sendFrame126("Obtained: " + (foundCount == player.dropItems.size() ? "@gre@" : "@red@") + foundCount + "/" + player.dropItems.size(), 23119);
		player.getPA().showInterface(INTERFACE_ID);
	}

	public void handleDrop(Player player, int npcId, int dropId, int dropAmount) {
		handleDrop(player, npcId, dropId, dropAmount, true);
	}

	/**
	 * Handles and NPC dropping an item
	 * @param npcId
	 * @param dropId
	 * @param dropAmount
	 */
	public void handleDrop(Player player, int npcId, int dropId, int dropAmount, boolean message) {
		if (linked != null) {
			linked.handleDrop(player, npcId, dropId, dropAmount, false);
		}

		if (npcId == 2043 || npcId == 2044) { //All zulrahs
			npcId = 2042;
		}
		if (npcId == 965) {
			npcId = 963;
		}
		if (npcId == 963) {
			npcId = 965;
		}
		if (npcId == 7144  || npcId == 7146) {
			npcId = 7145;
		}
		
		if (npcId == 8615 || npcId == 8619 || npcId == 8620 || npcId == 8622) {
			npcId = 8621;
		}

		//Pets
		if (npcId == PETS_ID) {
		    dropId = PetHandler.getPetForParentId(PetHandler.forItem(dropId)).getItemId();
        }
		
		if (!isCollectionNPC(npcId)) {
			return;
		}

		ArrayList<GameItem> currentItems = getCollections().get("" + npcId);
		if (currentItems == null) {
			currentItems = new ArrayList<>();
			currentItems.add(new GameItem(dropId, dropAmount));
			if (message)
				player.sendMessage("You have unlocked another item in your collection log!");
			Achievements.increase(player, AchievementType.COLLECTOR, 1);

		} else {
			boolean found = false;
			for(int i = 0; i < currentItems.size(); i++) {
				if (currentItems.get(i).id() == dropId) {
					currentItems.get(i).setAmount(currentItems.get(i).amount() + dropAmount);
					found = true;
					break;
				}
			}
			
			if (!found) {
				currentItems.add(new GameItem(dropId, dropAmount));
				if (message)
					player.sendMessage("You have unlocked another item in your collection log!");
			}
		}
		getCollections().put("" + npcId, currentItems);
		//As soon as it gets a drop it saves Kraken has been getting the most complaints
		saveToJSON();
	}
	
	/**
	 * Checks if an NPC is in fact a collection NPC
	 * @param npcId
	 * @return
	 */
	public boolean isCollectionNPC(int npcId) {
		for (Map.Entry<CollectionTabType, ArrayList<Integer>> entry : collectionNPCS.entrySet()) {
		    for(int i = 0; i < entry.getValue().size(); i++) {
		    	if (entry.getValue().get(i) == npcId) {
		    		return true;
		    	}
		    }
		}
		return false;
	}

	public ArrayList<GameItem> getUnlocked(int npcId) {
		return collections.getOrDefault(String.valueOf(npcId), Lists.newArrayList());
	}

	/**
	 * Gets the amount of unique items unlocked.
	 * Doesn't count item amounts or repeat items in different collection log tabs/categories.
	 */
	public int getUniquesUnlocked() {
		HashSet<Integer> uniques = new HashSet<>();

		for (List<GameItem> items : collections.values()) {
			for (GameItem item : items) {
				uniques.add(item.id());
			}
		}

		return uniques.size();
	}

	/**
	 * Handles all buttons on the interface
	 * @param buttonId
	 * @return
	 */
	public boolean handleActionButtons(Player player, int buttonId) {
		if (buttonId >= 90082 && buttonId <= 90180) {
			int index = (buttonId - 90082) / 2;
			player.getViewingCollectionLog().selectCell(player, index, player.collectionLogTab);
			return true;
		}
        switch (buttonId) {
            case 90076:
                player.getViewingCollectionLog().selectTab(player, CollectionTabType.BOSSES);
                return true;
            case 90182:
                player.getViewingCollectionLog().selectTab(player, CollectionTabType.WILDERNESS);
                return true;
            case 90184:
                player.getViewingCollectionLog().selectTab(player, CollectionTabType.RAIDS);
                return true;
            case 90186:
                player.getViewingCollectionLog().selectTab(player, CollectionTabType.MINIGAMES);
                return true;
            case 90188:
                player.getViewingCollectionLog().selectTab(player, CollectionTabType.OTHER);
                return true;
            case 90073:
                player.getPA().closeAllWindows();
                return true;
            default:
                return false;
        }
    }

	/**
	 * Saves users collection to a JSON file
	 */
	public void saveToJSON() {
		if (getSaveName() == null) {
			logger.error("No name set for collection log to save.");
			return;
		}
		Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = prettyGson.toJson(getCollections());
		BufferedWriter bw;
		try {
			if (!new File(getSaveDirectory()).exists()) {
				Preconditions.checkState(new File(getSaveDirectory()).mkdirs());
			}
			bw = new BufferedWriter(new FileWriter(new File(getSaveDirectory() + getSaveName().toLowerCase() + ".json")));
			bw.write(prettyJson);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public void loadForPlayer(Player player) {
		setGroupIronman(false);
		setSaveName(player.getLoginNameLower());
		loadCollections(); // Load collection for non-group ironman players
	}

	public void loadForGroupIronman(GroupIronmanGroup group) {
		setGroupIronman(true);
		setSaveName(group.getName().toLowerCase());
		loadCollections();
	}

	/**
	 * Group ironman was released without a group collection log, and therefore people were filling
	 * up their collection logs individually. We needed to combine team members collection logs together
	 * for the release.
	 * TODO delete on re-release
	 */
	public static void combineForGroupIronman(Player player, GroupIronmanGroup group) {
		if (group.getMergedCollectionLogs().contains(player.getLoginNameLower())) {
			return;
		}

		group.getMergedCollectionLogs().add(player.getLoginNameLower());

		HashMap<String, ArrayList<GameItem>> groupEntries = group.getCollectionLog().getCollections();
		HashMap<String, ArrayList<GameItem>> playerEntries = player.getCollectionLog().getCollections();

		if (!playerEntries.isEmpty()) {
			for (Map.Entry<String, ArrayList<GameItem>> entry : playerEntries.entrySet()) {
				ArrayList<GameItem> groupItems = groupEntries.get(entry.getKey());

				if (groupItems == null) {
					groupEntries.put(entry.getKey(), entry.getValue());
					logger.debug("Putting full entry onto group collection log because it doesn't exist in group collection log {}", entry);
					continue;
				}

				main: for (GameItem playerItem : entry.getValue()) {
					for (GameItem groupItem : groupItems) {
						if (playerItem.id() == groupItem.id()) {
							groupItem.setAmount(playerItem.amount() + groupItem.amount());
							logger.debug("Combined player and group item to create new amount {}, originalGroupItem={}, originalPlayerItem={}", groupItem, groupItem, playerItem);
							continue main;
						}
					}

					groupItems.add(playerItem);
					logger.debug("Added new group item from player collection log {}", playerItem);
				}
			}

			group.getCollectionLog().saveToJSON();
		}
	}

	private Path getPlayerSaveFilePath() {
		return Paths.get(getSaveDirectory() + getSaveName().toLowerCase() + ".json");
	}

	/**
	 * Loads a users collection data
	 */
	public void loadCollections() {
		try {
			File file = getPlayerSaveFilePath().toFile();

			JsonParser parser = new JsonParser();
			if (!file.exists()) {
				return;
			}
			Object obj = parser.parse(new FileReader(file));
			JsonObject jsonUpdates = (JsonObject) obj;

			Type listType = new TypeToken<HashMap<String, ArrayList<GameItem>>>() {
			}.getType();

			collections = new Gson().fromJson(jsonUpdates, listType);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.out.println("No collections found!");
			collections = new HashMap<>();
		}
	}

	public boolean isGroupIronman() {
		return groupIronman;
	}

	public void setGroupIronman(boolean groupIronman) {
		this.groupIronman = groupIronman;
	}

	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	public CollectionLog getLinked() {
		return linked;
	}

	public void setLinked(CollectionLog linked) {
		this.linked = linked;
	}
}
