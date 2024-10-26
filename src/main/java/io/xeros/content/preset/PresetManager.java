package io.xeros.content.preset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.cheatprevention.CheatEngineBlock;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.ItemStats;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.items.bank.BankTab;
import io.xeros.util.Misc;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 9/30/19
 * Written to create a preset system, that dynamically allows players to create custom presets.
 * There are also static sets which cost coins that are always available.
 *
 */
public class PresetManager {

	private static volatile PresetManager SINGLETON;

	// Thread safe, anti lock, reflection proof, lazily initialized
	public static PresetManager getSingleton() {
		if (SINGLETON == null) {
			synchronized (PresetManager.class) {
				if (SINGLETON == null) {
					SINGLETON = new PresetManager();
				}
			}
		}

		return SINGLETON;
	}

	public static String getSaveDirectory() {
		return Server.getSaveDirectory() + "/presets/";
	}

	private PresetManager() {
		if (SINGLETON != null) {
			throw new RuntimeException("Use getSingleton() method to get the single instance of this class.");
		}
	}

	/* Variables */
	private static final int INTERFACE_ID = 21553;

	private ArrayList<DefaultPreset> defaultPresets;

	private final Map<String, ArrayList<Preset>> globalPresets = new HashMap<>(); //All users and all their presets, key = playerName

	/**
	 * Initializes JSON data
	 */
	public void init() {
		try {
			Path path = Paths.get(Server.getDataDirectory() + "/cfg/default_presets.json");
			File file = path.toFile();

			JsonParser parser = new JsonParser();
			if (!file.exists()) {
				return;
			}
			Object obj = parser.parse(new FileReader(file));
			JsonArray jsonUpdates = (JsonArray) obj;

			Type listType = new TypeToken<ArrayList<DefaultPreset>>() {
			}.getType();

			defaultPresets = new Gson().fromJson(jsonUpdates, listType);
			
			//Loading all individual players
			File folder = new File(getSaveDirectory());

			if (!folder.exists()) {
				Preconditions.checkState(folder.mkdirs());
			}

	        File[] files = folder.listFiles();
	        
	        listType = new TypeToken<ArrayList<Preset>>() {
			}.getType();
	 
	        for (File f : files)
	        {
	            if (f.exists()) {
	            	obj = parser.parse(new FileReader(f));
	            	JsonArray jsonUpdate = (JsonArray) obj;
	            	ArrayList<Preset> temp = new Gson().fromJson(jsonUpdate, listType);
	            	globalPresets.put(f.getName().replace(".json", ""), temp);
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.out.println("No default presets found!");
			defaultPresets = new ArrayList<>();
		}
	}

	/**
     * Handles opening the interface for the user
     * @param player
     */
    public void open(Player player) {
        if (player == null) {
            return;
        }
		if (Boundary.isIn(player, Boundary.HESPORI)) {
			return;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA)) {
			return;
		}
		if (Boundary.isIn(player, Boundary.RAIDS)) {
			return;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_LOBBY)) {
			return;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
			return;
		}
		if (Boundary.isIn(player, Boundary.FULL_RAIDS)) {
			return;
		}
        updateTabs(player);
        loadOverallPresets(player);
		player.getItems().sendInventoryInterface(3823);
        for(int i = 0; i < 10; i++) {
            player.getPA().sendConfig(903 + i, 0);
        }
        loadPresetView(player, 0, true);
        player.getPA().showInterface(INTERFACE_ID);
        player.viewingPresets = true;
		player.sendMessage("@red@Note: Any changes made after initially saving a preset, will auto-save.");
    }
	/**
	 * Handles updating the tabs on the left side of the interface
	 * @param player
	 */
	private void updateTabs(Player player) {
		for(int i = 0; i < 10; i++) {
			player.getPA().sendFrame126("", 21592 + (i * 2));
		}
		
		if (!globalPresets.containsKey(player.getLoginName().toLowerCase())) {
			player.getPA().sendFrame126("-New Preset-", 21592);
		} else {
			ArrayList<Preset> presets = globalPresets.get(player.getLoginName().toLowerCase());
			if (presets == null) {
				return;
			}
			
			if (presets.isEmpty()) {
				player.getPA().sendFrame126("-New Preset-", 21592);
			} else {
				for(int i = 0; i < presets.size(); i++) {
					player.getPA().sendFrame126(Misc.optimizeText(presets.get(i).getName()), 21592 + (i * 2));
				}
				if (presets.size() != 10) {
					player.getPA().sendFrame126("-New Preset-", 21592 + (presets.size() * 2));
				}
			}
		}
	}

	/**
	 * Loads all the default preset names into their tabs
	 * @param player
	 */
	private void loadOverallPresets(Player player) {
		for (int i = 0; i < 4; i++) {
			if (i < defaultPresets.size()) {
				if (defaultPresets.get(i).getCost()/1000 >= 1000) {
					player.getPA().sendFrame126(defaultPresets.get(i).getName() + " - (" + defaultPresets.get(i).getCost()/1000000 + "m)", 21556 + (i * 2));
				} else {
					player.getPA().sendFrame126(defaultPresets.get(i).getName() + " - (" + defaultPresets.get(i).getCost()/1000 + "k)", 21556 + (i * 2));
				}
			}
		}
	}

	/**
	 * Loads the current preset the player is looking at
	 * @param player
	 * @param index
	 * @param isDefault
	 */
	private void loadPresetView(Player player, int index, boolean isDefault) {
		player.presetViewingIndex = index;
		if (isDefault) {
			for(int i = 0; i < 4; i++) {
				player.getPA().sendConfig(899 + i, 0);
			}
			player.getPA().sendConfig(899 + index, 1);
			player.presetViewingDefault = true;
			DefaultPreset preset = defaultPresets.get(index);
			if (preset == null) {
				return;
			}

			populateInterface(player, preset);
		} else {
			if (player.amDonated <= 49 && index > 2) {
				player.sendMessage("Only donators+ can have more than 3 presets!");
				return;
			}
			
			for(int i = 0; i < 10; i++) {
				player.getPA().sendConfig(903 + i, 0);
			}
			player.getPA().sendConfig(903 + index, 1);
			
			player.presetViewingDefault = false;
			if (!globalPresets.containsKey(player.getLoginName().toLowerCase())) { //they have no presets saved
				player.presetViewingIndex = 0;
				Preset preset = new Preset(new PresetEquipment(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1), new ArrayList<>(), 0, "", 2);
				populateInterface(player, preset);
				player.currentPreset = preset;
				player.viewingInitialPreset = true;
			} else {
				ArrayList<Preset> presets = globalPresets.get(player.getLoginName().toLowerCase());
				if (index == presets.size()) {
					Preset preset = new Preset(new PresetEquipment(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1), new ArrayList<>(), 0, "", 2);
					populateInterface(player, preset);
					player.currentPreset = preset;
					player.viewingInitialPreset = true;
				} else if (index > presets.size()) {
					player.sendMessage("Please use the -New Preset- preset to make a new preset first!");
					return;
				} else {
					ArrayList<PresetItem> copy = new ArrayList<PresetItem>(presets.get(index).getInventory());
					PresetEquipment equipment = new PresetEquipment(presets.get(index).getEquipment().getWeapon(),
							presets.get(index).getEquipment().getHelmet(),
							presets.get(index).getEquipment().getShield(),
							presets.get(index).getEquipment().getAmmo(),
							presets.get(index).getEquipment().getPlate(),
							presets.get(index).getEquipment().getLegs(),
							presets.get(index).getEquipment().getBoots(),
							presets.get(index).getEquipment().getGloves(),
							presets.get(index).getEquipment().getNecklace(),
							presets.get(index).getEquipment().getCape(),
							presets.get(index).getEquipment().getRing());
					
					Preset preset = new Preset(equipment, copy, presets.get(index).getAmmoAmount(), presets.get(index).getName(), presets.get(index).getSpellBook());
					player.currentPreset = preset;
					populateInterface(player, preset);
					player.viewingInitialPreset = false;
				}
			}
		}
	}
	
	/**
	 * Populates the interface with data based on the current preset
	 * @param player
	 * @param preset
	 */
	private void populateInterface(Player player, Set preset) {
		
		ArrayList<GameItem> tempItems = new ArrayList<>();

		preset.getInventory().forEach(i -> {
			if (i.getItemId() == -1) {
				//System.out.println("Item ID:" + i.toString() + "failed to load because id is null. User:" + player);
			}
			if (i.getAmount() > 1 && player.getItems().isStackable(i.getItemId())) {
				tempItems.add(new GameItem(i.getItemId(), i.getAmount()));
			} else {
				for (int j = 0; j < i.getAmount(); j++) {
					tempItems.add(new GameItem(i.getItemId(), 1));
				}
			}
		});
		
		player.getPA().sendFrame126(preset.getName().equals("") ? "New Preset" : Misc.optimizeText(preset.getName()), 21565);

		PlayerAssistant.sendItems(player, 21578, tempItems, 28);
		
		player.getPA().sendChangeSprite(254, (byte) preset.getSpellBook());

		// playerHat = 0, playerCape = 1, playerAmulet = 2, playerWeapon = 3,
		// playerChest = 4,
		// playerShield = 5, playerLegs = 7, playerHands = 9, playerFeet = 10,
		// playerRing = 12, playerArrows = 13,

		int[] equipmentArray = { preset.getEquipment().getHelmet(), preset.getEquipment().getCape(),
				preset.getEquipment().getNecklace(), preset.getEquipment().getAmmo(),
				preset.getEquipment().getPlate(), preset.getEquipment().getWeapon(),
				preset.getEquipment().getShield(), preset.getEquipment().getLegs(),
				preset.getEquipment().getBoots(), preset.getEquipment().getGloves(),
				preset.getEquipment().getRing() };

		for(int i = 21579; i < 21579 + 11; i++) {
			player.getPA().sendFrame34a(i, equipmentArray[i - 21579], 0, i == 21579 + 3 ? preset.getAmmoAmount() : 1);
		}
		
		for(int i = 0; i < 11; i++) {
			player.getPA().sendConfig(61 + i, equipmentArray[i] == -1 ? 0 : 1);
		}
	}

	/**
	 * Handles all buttons within the interface
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public boolean handleActionButtons(Player player, int buttonId) {
		if (buttonId >= 84051 && buttonId <= 84057) {
			int index = (buttonId - 84051) / 2;
			loadPresetView(player, index, true);
		} else if (buttonId >= 197007 && buttonId <= 197184) {
			int offset = (buttonId - 197007) % 22;
			int index = 1;
			if (offset == 1) {
				index += (buttonId - 197008) / 22;
				loadPresetView(player, index, false);
			} else {
				index += (buttonId - 197007) / 22;
				deletePreset(player, index);
			}
		}
		
		switch (buttonId) {
		case 251:
			changeSpellBook(player);
			return true;
		case 196242:
			loadPresetView(player, 0, false);
			return true;
		case 196241:
			deletePreset(player, 0);
			return true;
		case 84062:

			loadPreset(player, false);
			player.getPA().closeAllWindows();
			return true;
		case 84065:

			savePreset(player, true);
			editPresetName(player);
			return true;
		case 84107:
			if (player.inPresets == false && System.currentTimeMillis() - player.clickDelay <= 2200) {
				return false;
			}
			if (player.inPresets == false) {
				CheatEngineBlock.PresetAlert(player);
				return false;
			}
			editPresetName(player);
			return true;
		case 84071:
			if (player.inPresets == false && System.currentTimeMillis() - player.clickDelay <= 2200) {
				return false;
			}
			if (player.inPresets == false) {
				CheatEngineBlock.PresetAlert(player);
				return false;
			}
			player.inPresets = true;
			player.viewingPresets = false;
			player.currentPreset = null;
			player.presetViewingIndex = -1;
			player.getPA().closeAllWindows();
			return true;
		}
		return false;
	}
	public boolean is2handed(String itemName, int itemId) {

		if (itemName.contains("godsword") || itemName.contains("crystal") || itemName.contains("aradomin sword")
				|| itemName.contains("2h") || itemName.contains("spear") || itemName.contains("halberd")
				|| itemName.contains("longbow") || itemName.contains("shortbow") || itemName.contains("ark bow")
				|| itemName.contains("karil") || itemName.contains("verac")
				|| itemName.contains("guthan") || itemName.contains("dharok") || itemName.contains("torag") ||
				itemName.contains("abyssal bludgeon") || itemName.contains("spade") || itemName.contains("casket") ||
				itemName.contains("clueless") || itemName.contains("scythe") || itemName.contains("ballista") || itemName.contains("hunting knife") || itemName.contains("elder maul") || itemName.contains("bulwark") || itemName.contains("claws")) {
			return true;
		}
		switch (itemId) {
			case 12926:
			case 6724:
			case 11838:
			case 12809:
			case 14484:
			case 4153:
			case 12848:
			case 6528:
			case 10887:
			case 12424:
			case 20784:
			case 20997:
			case 22550:
				return true;
		}
		return false;
	}

	/**
     * Loads a preset onto the player
     * @param player
     */
    private void loadPreset(Player player, boolean loadPrevious) {
		if (Configuration.DISABLE_PRESETS) {
			player.sendMessage("Presets have been disabled.");
			return;
		}
		if (System.currentTimeMillis() - player.lastPresetClick < 3000) {
			player.sendMessage("Please wait a few seconds before doing this again.");
			return;
		}
		if ((!loadPrevious && !player.inPresets) && System.currentTimeMillis() - player.clickDelay <= 2200) {
			return;
		}
		if (!loadPrevious && !player.inPresets) {
			CheatEngineBlock.PresetAlert(player);
			return;
		}

		try {
			player.lastPresetClick = System.currentTimeMillis();
			boolean defaultPreset = loadPrevious && player.lastPreset instanceof DefaultPreset || player.presetViewingDefault;
			if (!defaultPreset) {
				for (int slot = 0; slot < player.playerItems.length; slot++) {
					if (player.playerItems[slot] > 0 && player.playerItemsN[slot] > 0) {
						if (!player.getItems().addToBank(player.playerItems[slot] - 1, player.playerItemsN[slot], false, true)) {
							return;
						}
					}
				}

				for (int slot = 0; slot < player.playerEquipment.length; slot++) {
					if (player.playerEquipment[slot] > 0 && player.playerEquipmentN[slot] > 0) {
						if (player.getItems().addEquipmentToBank(player.playerEquipment[slot], slot, player.playerEquipmentN[slot],
								false, true)) {
							player.getItems().equipItem(-1, 0, slot);
						} else {
							player.sendMessage("Your bank is full.");
							return;
						}
					}
				}
				player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
				player.getItems().queueBankContainerUpdate();
				player.getItems().resetTempItems();

				if (globalPresets.get(player.getLoginName().toLowerCase()) == null) {
					player.sendMessage("Preset error occurred, please report this.");
					return;
				}

				if (player.presetViewingIndex >= globalPresets.get(player.getLoginName().toLowerCase()).size()) {
					return;
				}

				Preset preset;

				if (loadPrevious && player.lastPreset != null) {
					preset = (Preset) player.lastPreset;
				} else {
					preset = globalPresets.get(player.getLoginName().toLowerCase()).get(player.presetViewingIndex);
				}

				if (preset == null) {
					return;
				}

				PresetItem[] equipmentItemsFound = new PresetItem[15];

				for(int j = 0; j < preset.getInventory().size(); j++) {
					PresetItem item = new PresetItem(preset.getInventory().get(j).getItemId(), preset.getInventory().get(j).getAmount());

					if (findItemInBank(player, item)) {
						player.getItems().removeFromAnyTabWithoutAdding(item.getItemId(), item.getAmount(), false);
						player.getItems().addItem(item.getItemId(), item.getAmount());
					}
				}

				PresetItem ammo = new PresetItem(preset.getEquipment().getAmmo(), preset.getAmmoAmount());
				PresetItem helmet = new PresetItem(preset.getEquipment().getHelmet(), 1);
				PresetItem legs = new PresetItem(preset.getEquipment().getLegs(), 1);
				PresetItem shield = new PresetItem(preset.getEquipment().getShield(), 1);
				PresetItem weapon = new PresetItem(preset.getEquipment().getWeapon(), 1);
				PresetItem ring = new PresetItem(preset.getEquipment().getRing(), 1);
				PresetItem boots = new PresetItem(preset.getEquipment().getBoots(), 1);
				PresetItem gloves = new PresetItem(preset.getEquipment().getGloves(), 1);
				PresetItem plate = new PresetItem(preset.getEquipment().getPlate(), 1);
				PresetItem cape = new PresetItem(preset.getEquipment().getCape(), 1);
				PresetItem necklace = new PresetItem(preset.getEquipment().getNecklace(), 1);

				/*
				 * This could probably be simplified into a loop instead of using a object to hold the equipment. However this is linear!
				 */
				var ammoItem = findBankItem(player, ammo);
				if (ammoItem != null) {
					var amount = Math.min(ammoItem.amount(), ammo.getAmount());
					if (amount > 0) {
						equipmentItemsFound[Player.playerArrows] = new PresetItem(ammoItem.id() - 1, amount);
					}
				}

				if (preset.getEquipment().getHelmet() != -1 && findItemInBank(player, helmet)) {
					equipmentItemsFound[Player.playerHat] = helmet;
				}

				if (preset.getEquipment().getLegs() != -1 && findItemInBank(player, legs)) {
					equipmentItemsFound[Player.playerLegs] = legs;
				}//

				if (preset.getEquipment().getShield() != -1 && findItemInBank(player, shield)) {
					equipmentItemsFound[Player.playerShield] = shield;
				}//

				if (preset.getEquipment().getWeapon() != -1 && findItemInBank(player, weapon)) {
					equipmentItemsFound[Player.playerWeapon] = weapon;
				}

				if (preset.getEquipment().getRing() != -1 && findItemInBank(player, ring)) {
					equipmentItemsFound[Player.playerRing] = ring;
				}//

				if (preset.getEquipment().getBoots() != -1 && findItemInBank(player, boots)) {
					equipmentItemsFound[Player.playerFeet] = boots;
				}

				if (preset.getEquipment().getGloves() != -1 && findItemInBank(player, gloves)) {
					equipmentItemsFound[Player.playerHands] = gloves;
				}

				if (preset.getEquipment().getPlate() != -1 && findItemInBank(player, plate)) {
					equipmentItemsFound[Player.playerChest] = plate;
				}//

				if (preset.getEquipment().getCape() != -1 && findItemInBank(player, cape)) {
					equipmentItemsFound[Player.playerCape] = cape;
				}//

				if (preset.getEquipment().getNecklace() != -1 && findItemInBank(player, necklace)) {
					equipmentItemsFound[Player.playerAmulet] = necklace;
				}//

				for(int i = 0; i < equipmentItemsFound.length; i++) {
					if (equipmentItemsFound[i] != null) {
						player.getItems().removeFromAnyTabWithoutAdding(equipmentItemsFound[i].getItemId(), equipmentItemsFound[i].getAmount(), false);
						player.getItems().equipItem(equipmentItemsFound[i].getItemId(), equipmentItemsFound[i].getAmount(), i);
						player.getItems().sendInventoryInterface(3823);
					}
				}//

				switch(preset.getSpellBook()) {
				case 2:
					player.setSidebarInterface(6, 938);
					player.sendMessage("You feel a drain on your memory.");
					player.playerMagicBook = 0;
					player.getPA().resetAutocast();
					break;
				case 0:
					player.setSidebarInterface(6, 838);
					player.sendMessage("An ancient wisdomin fills your mind.");
					player.playerMagicBook = 1;
					player.getPA().resetAutocast();
					break;
				case 1:
					player.sendMessage("You switch to the lunar spellbook.");
					player.setSidebarInterface(6, 29999);
					player.playerMagicBook = 2;
					player.getPA().resetAutocast();
					break;
				}

				boolean wearing2h = is2handed(ItemAssistant.getItemName(player.playerEquipment[Player.playerWeapon]).toLowerCase(), player.playerEquipment[Player.playerWeapon]);
				if (wearing2h) {
					boolean wearingShield = player.playerEquipment[Player.playerShield] > 0;
					int currentShield = player.playerEquipment[Player.playerShield];
					if (wearingShield) {
						player.getItems().addEquipmentToBank(currentShield, 5, 1, true, true);
					}
				}
				player.lastPreset = preset;
				player.sendMessage("You have successfully loaded the " + preset.getName() + " set. <col=7a2100>(CTRL + R)");

			} else if (player.presetViewingDefault){
				if (player.getMode().isIronmanType()) {
					player.sendMessage("Ironmen cannot purchase default presets.");
					return;
				}

				int openSlots = 0;
				for(BankTab tab : player.getBank().getBankTab()) {
					openSlots += tab.freeSlots();
				}

				if (openSlots < (14 - player.getItems().freeEquipmentSlots())) {
					player.sendMessage("You don't have enough room in your bank to do this.");
					return;
				}

				for (int slot = 0; slot < player.playerEquipment.length; slot++) {
					if (player.playerEquipment[slot] > 0 && player.playerEquipmentN[slot] > 0) {
						if (player.getItems().addEquipmentToBank(player.playerEquipment[slot], slot, player.playerEquipmentN[slot],
								false, true)) {
							player.getItems().equipItem(-1, 0, slot);
						} else {
							player.sendMessage("Your bank is full.");
							break;
						}
					}
				}
				player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
				player.getItems().queueBankContainerUpdate();
				player.getItems().resetTempItems();

				DefaultPreset preset;
				if (loadPrevious && player.lastPreset != null) {
					preset = (DefaultPreset) player.lastPreset;
				} else {
					preset = defaultPresets.get(player.presetViewingIndex);
				}

				if (preset == null) {
					return;
				}
				for(int i = 0; i < preset.getRequirements().length; i++) {
					if (player.playerLevel[preset.getRequirements()[i].getLevelId()] < preset.getRequirements()[i].getLevel()) {
						player.sendMessage("You must have " + preset.getRequirements()[i].getLevel() + " " + getSkillNameForId(preset.getRequirements()[i].getLevelId()) + " to load this preset.");
						return;
					}
				}
				if (!player.getItems().playerHasItem(995) || !player.getItems().playerHasItem(995, preset.getCost())) {
					player.sendMessage("You need " + preset.getCost()/1000 + "k to load this preset!");
					return;
				}

				if (player.getItems().getItemAmount(995) != preset.getCost()) {
					player.sendMessage("You must have exactly " + preset.getCost()/1000 + "k to load this preset!");
					return;
				}

				if (player.getItems().freeEquipmentSlots() != 14) {
					player.sendMessage("You need to remove your equipment before doing this.");
					return;
				}
				if (player.getItems().freeSlots() != 27) {
					player.sendMessage("You can only have coins in your inventory to do this.");
					return;
				}

				player.getItems().deleteItem(995, preset.getCost());

				player.getItems().equipItem(preset.getEquipment().getWeapon(), 1, Player.playerWeapon);
				player.getItems().equipItem(preset.getEquipment().getBoots(), 1, Player.playerFeet);
				player.getItems().equipItem(preset.getEquipment().getCape(), 1, Player.playerCape);
				player.getItems().equipItem(preset.getEquipment().getNecklace(), 1, Player.playerAmulet);
				player.getItems().equipItem(preset.getEquipment().getGloves(), 1, Player.playerHands);
				player.getItems().equipItem(preset.getEquipment().getLegs(), 1, Player.playerLegs);
				player.getItems().equipItem(preset.getEquipment().getPlate(), 1, Player.playerChest);
				player.getItems().equipItem(preset.getEquipment().getAmmo(), preset.getAmmoAmount(), Player.playerArrows);
				player.getItems().equipItem(preset.getEquipment().getShield(), 1, Player.playerShield);
				player.getItems().equipItem(preset.getEquipment().getHelmet(), 1, Player.playerHat);
				player.getItems().equipItem(preset.getEquipment().getRing(), 1, Player.playerRing);
				   player.getItems().addSpecialBar(player.playerEquipment[3]);

				preset.getInventory().forEach(i -> {
					player.getItems().addItem(i.getItemId(), i.getAmount());
				});

				switch(preset.getSpellBook()) {
				case 2:
					player.setSidebarInterface(6, 938);
					player.sendMessage("You feel a drain on your memory.");
					player.playerMagicBook = 0;
					player.getPA().resetAutocast();
					break;
				case 0:
					player.setSidebarInterface(6, 838);
					player.sendMessage("An ancient wisdomin fills your mind.");
					player.playerMagicBook = 1;
					player.getPA().resetAutocast();
					break;
				case 1:
					player.sendMessage("You switch to the lunar spellbook.");
					player.setSidebarInterface(6, 29999);
					player.playerMagicBook = 2;
					player.getPA().resetAutocast();
					break;
				}
				player.lastPreset = preset;
				player.sendMessage("You have successfully loaded the " + preset.getName() + ".");
			}
		} finally {
			player.getPA().resetAutocast();
			player.usingMagic = false;
			player.usingClickCast = false;
			player.setSpellId(-1);
			player.getItems().sendWeapon(player.getItems().getWeapon());
			MeleeData.setWeaponAnimations(player);
		}
	}
	
	/**
	 * Saves a preset to the players json data
	 * @param player
	 */
	private void savePreset(Player player, boolean viaButton) {
		if (Configuration.DISABLE_PRESETS) {
			player.sendMessage("Presets have been disabled.");
			return;
		}
		if (!player.inPresets && System.currentTimeMillis() - player.clickDelay <= 2200) {
			return;
		}
		if (!player.inPresets) {
			CheatEngineBlock.PresetAlert(player);
			return;
		}
		if (player.presetViewingDefault) {
			player.sendMessage("You can't save a default preset!");
			return;
		}
		
		if (!globalPresets.containsKey(player.getLoginName().toLowerCase())) { //never saved a set before
			ArrayList<Preset> newPresets = new ArrayList<>();
			
			Preset preset = player.currentPreset;
			if (preset.getInventory().isEmpty() && preset.getEquipment().isEmpty()) {
				ArrayList<PresetItem> currentItems = new ArrayList<>();
				for(int i = 0; i < 28; i++) {
					currentItems.add(new PresetItem(player.playerItems[i] - 1, player.playerItemsN[i]));
				}
				
				preset = new Preset(new PresetEquipment(player.playerEquipment[Player.playerWeapon], player.playerEquipment[Player.playerHat],
						player.playerEquipment[Player.playerShield], player.playerEquipment[Player.playerArrows],
						player.playerEquipment[Player.playerChest], player.playerEquipment[Player.playerLegs],
						player.playerEquipment[Player.playerFeet], player.playerEquipment[Player.playerHands],
						player.playerEquipment[Player.playerAmulet], player.playerEquipment[Player.playerCape],
						player.playerEquipment[Player.playerRing]), currentItems, player.playerEquipmentN[Player.playerArrows], "", 2);
			}
			
			newPresets.add(preset);
			populateInterface(player, preset);
			updateTabs(player);
			globalPresets.put(player.getLoginName().toLowerCase(), newPresets);
			saveToJSON(player, newPresets);
		} else {
			ArrayList<Preset> presets = globalPresets.get(player.getLoginName().toLowerCase());
			
			if (player.presetViewingIndex > globalPresets.get(player.getLoginName().toLowerCase()).size()) {
				return;
			}
			
			Preset presetToUpdate = null;
			if (player.presetViewingIndex < presets.size()) {
				presetToUpdate = presets.get(player.presetViewingIndex);
			}
			
			Preset preset = player.currentPreset;
			if (preset.getInventory().isEmpty() && preset.getEquipment().isEmpty() && viaButton) {
				ArrayList<PresetItem> currentItems = new ArrayList<>();
				for(int i = 0; i < 28; i++) {
					if (player.playerItems[i] == 0) {
						continue;
					}
					currentItems.add(new PresetItem(player.playerItems[i] - 1, player.playerItemsN[i]));
				}
				
				preset = new Preset(new PresetEquipment(player.playerEquipment[Player.playerWeapon], player.playerEquipment[Player.playerHat],
						player.playerEquipment[Player.playerShield], player.playerEquipment[Player.playerArrows],
						player.playerEquipment[Player.playerChest], player.playerEquipment[Player.playerLegs],
						player.playerEquipment[Player.playerFeet], player.playerEquipment[Player.playerHands],
						player.playerEquipment[Player.playerAmulet], player.playerEquipment[Player.playerCape],
						player.playerEquipment[Player.playerRing]), currentItems, player.playerEquipmentN[Player.playerArrows], "", 2);
			}
			
			if (presetToUpdate != null) {
				presets.set(player.presetViewingIndex, preset);
			} else {
				presets.add(preset);
			}

			player.viewingInitialPreset = false;
			populateInterface(player, preset);
			globalPresets.put(player.getLoginName().toLowerCase(), presets);
			updateTabs(player);
			saveToJSON(player, presets);
		}
		
		player.sendMessage("Your preset has been saved!");
		Achievements.increase(player, AchievementType.PRESETS, 1);

	}
	
	/**
     * Saves the presets of a player to a .json file
     */
    public void saveToJSON(Player player, ArrayList<Preset> presets) {
        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = prettyGson.toJson(presets);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(new File(getSaveDirectory() + player.getLoginName().toLowerCase() + ".json")));
            bw.write(prettyJson);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
	
    
    /**
     * Gets a skill name for its ID
     * @param id
     * @return
     */
	private String getSkillNameForId(int id) {
		switch(id) {
		case 0:
			return "defence";
		case 1:
			return "attack";
		case 4:
			return "ranged";
		case 6:
			return "magic";
		}
		return "";
	}
	
	/**
	 * Adds an item to the presets inventory
	 * @param player
	 * @param itemId
	 * @param itemAmount
	 */
	public void addInventoryItem(Player player, int itemId, int itemAmount) {
		if (player.inPresets == false && System.currentTimeMillis() - player.clickDelay <= 2200) {
			return;
		}
		if (player.inPresets == false) {
			CheatEngineBlock.PresetAlert(player);
			return;
		}
		if (player.presetViewingDefault) {
			player.sendMessage("You can't add items to this preset!");
			return;
		}
		
		if (player.currentPreset == null) {
			return;
		}
		
		if (player.currentPreset.getInventory().size() == 28) {
			player.sendMessage("Your preset inventory is already full.");
			return;
		}
		
		for(int i = 0; i < player.currentPreset.getInventory().size(); i++) {
			if (player.currentPreset.getInventory().get(i).getItemId() == itemId) {
				if (player.getItems().isStackable(itemId)) {
					player.sendMessage("You must remove this item from the current inventory before adding again.");
					return;
				}
			}
		}
		
		player.currentPreset.getInventory().add(new PresetItem(itemId, itemAmount));
		
		ArrayList<GameItem> tempItems = new ArrayList<>();
		player.currentPreset.getInventory().forEach(i -> {
			if (i.getAmount() > 1 && player.getItems().isStackable(i.getItemId())) {
				tempItems.add(new GameItem(i.getItemId(), i.getAmount()));
			} else {
				for (int j = 0; j < i.getAmount(); j++) {
					tempItems.add(new GameItem(i.getItemId(), 1));
				}
			}
		});

		PlayerAssistant.sendItems(player, 21578, tempItems, 28);
		tempItems.clear();

		if (!player.viewingInitialPreset) {
			savePreset(player, false);
		}
	}
	
	/**
	 * Removes an item from the players preset inventory
	 * @param player
	 * @param itemId
	 */
	public void removeInventoryItem(Player player, int itemId) {
		if (player.inPresets == false && System.currentTimeMillis() - player.clickDelay <= 2200) {
			return;
		}
		if (player.inPresets == false) {
			CheatEngineBlock.PresetAlert(player);
			return;
		}
		if (player.presetViewingDefault) {
			player.sendMessage("You can't remove items to this preset!");
			return;
		}
		
		if (player.currentPreset == null) {
			return;
		}
		
		for(int i = 0; i < player.currentPreset.getInventory().size(); i++) {
			if (player.currentPreset.getInventory().get(i).getItemId() == itemId) {
				player.currentPreset.getInventory().remove(i);
				break;
			}
		}
		
		ArrayList<GameItem> tempItems = new ArrayList<>();
		player.currentPreset.getInventory().forEach(i -> {
			if (i.getAmount() > 1 && player.getItems().isStackable(i.getItemId())) {
				tempItems.add(new GameItem(i.getItemId(), i.getAmount()));
			} else {
				for (int j = 0; j < i.getAmount(); j++) {
					tempItems.add(new GameItem(i.getItemId(), 1));
				}
			}
		});

		PlayerAssistant.sendItems(player, 21578, tempItems, 28);
		tempItems.clear();

		if (!player.viewingInitialPreset) {
			savePreset(player, false);
		}
	}
	
	/**
	 * Adds an item to the preset's equipment
	 * @param player
	 * @param itemId
	 * @param amount
	 */
	public void addItemToEquipment(Player player, int itemId, int amount) {
		player.getItems().sendInventoryInterface(3823);
		if (player.inPresets == false && System.currentTimeMillis() - player.clickDelay <= 2200) {
			return;
		}
		if (player.inPresets == false) {
			CheatEngineBlock.PresetAlert(player);
			return;
		}
		if (player.presetViewingDefault) {
			player.sendMessage("You can't add items from this preset!");
			return;
		}
		
		if (player.currentPreset == null) {
			return;
		}

		ItemDef item = ItemDef.forId(itemId);
		
		if (item != null) {
			int targetSlot = ItemStats.forId(item.getId()).getEquipment().getSlot();
			if (targetSlot == -1) {
				player.sendMessage(item.getName() + " can't be equipped.");
				return;
			}

			switch(targetSlot) {
			case 1:
				player.currentPreset.getEquipment().setCape(itemId);
				player.getPA().sendFrame34a(21579 + 1, itemId, 0, 1);
				player.getPA().sendConfig(61 + 1, 1);
				break;
			case 0:
				player.currentPreset.getEquipment().setHelmet(itemId);
				player.getPA().sendFrame34a(21579, itemId, 0, 1);
				player.getPA().sendConfig(61, 1);
				break;
			case 2:
				player.currentPreset.getEquipment().setNecklace(itemId);
				player.getPA().sendFrame34a(21579 + 2, itemId, 0, 1);
				player.getPA().sendConfig(61 + 2, 1);
				break;
			case 13:
				player.currentPreset.getEquipment().setAmmo(itemId);
				player.currentPreset.setAmmoAmount(amount);
				player.getPA().sendFrame34a(21579 + 3, itemId, 0, amount);
				player.getPA().sendConfig(61 + 3, 1);
				break;
			case 4:
				player.currentPreset.getEquipment().setPlate(itemId);
				player.getPA().sendFrame34a(21579 + 4, itemId, 0, 1);
				player.getPA().sendConfig(61 + 4, 1);
				break;
			case 3:
				player.currentPreset.getEquipment().setWeapon(itemId);
				player.getPA().sendFrame34a(21579 + 5, itemId, 0, 1);
				player.getPA().sendConfig(61 + 5, 1);
				break;
			case 5:
				player.currentPreset.getEquipment().setShield(itemId);
				player.getPA().sendFrame34a(21579 + 6, itemId, 0, 1);
				player.getPA().sendConfig(61 + 6, 1);
				break;
			case 7:
				player.currentPreset.getEquipment().setLegs(itemId);
				player.getPA().sendFrame34a(21579 + 7, itemId, 0, 1);
				player.getPA().sendConfig(61 + 7, 1);
				break;
			case 10:
				player.currentPreset.getEquipment().setBoots(itemId);
				player.getPA().sendFrame34a(21579 + 8, itemId, 0, 1);
				player.getPA().sendConfig(61 + 8, 1);
				break;
			case 9:
				player.currentPreset.getEquipment().setGloves(itemId);
				player.getPA().sendFrame34a(21579 + 9, itemId, 0, 1);
				player.getPA().sendConfig(61 + 9, 1);
				break;
			case 12:
				player.currentPreset.getEquipment().setRing(itemId);
				player.getPA().sendFrame34a(21579 + 10, itemId, 0, 1);
				player.getPA().sendConfig(61 + 10, 1);
				break;
			}

			if (!player.viewingInitialPreset) {
				savePreset(player, false);
			}
		}
	}

	/**
	 * Removes an item from the preset equipment
	 * @param player
	 * @param interfaceId
	 */
	public void removeEquipmentItem(Player player, int interfaceId) {
		if (player.inPresets == false && System.currentTimeMillis() - player.clickDelay <= 2200) {
			return;
		}
		if (player.inPresets == false) {
			CheatEngineBlock.PresetAlert(player);
			return;
		}
		if (player.presetViewingDefault) {
			player.sendMessage("You can't remove items from this preset!");
			return;
		}
		
		if (player.currentPreset == null) {
			return;
		}
		
		switch(interfaceId) {
		case 21580:
			player.currentPreset.getEquipment().setCape(-1);
			player.getPA().sendFrame34a(21579 + 1, -1, 0, 1);
			player.getPA().sendConfig(61 + 1, 0);
			break;
		case 21579:
			player.currentPreset.getEquipment().setHelmet(-1);
			player.getPA().sendFrame34a(21579, -1, 0, 1);
			player.getPA().sendConfig(61, 0);
			break;
		case 21581:
			player.currentPreset.getEquipment().setNecklace(-1);
			player.getPA().sendFrame34a(21579 + 2, -1, 0, 1);
			player.getPA().sendConfig(61 + 2, 0);
			break;
		case 21582:
			player.currentPreset.getEquipment().setAmmo(-1);
			player.currentPreset.setAmmoAmount(-1);
			player.getPA().sendFrame34a(21579 + 3, -1, 0, 1);
			player.getPA().sendConfig(61 + 3, 0);
			break;
		case 21583:
			player.currentPreset.getEquipment().setPlate(-1);
			player.getPA().sendFrame34a(21579 + 4, -1, 0, 1);
			player.getPA().sendConfig(61 + 4, 0);
			break;
		case 21584:
			player.currentPreset.getEquipment().setWeapon(-1);
			player.getPA().sendFrame34a(21579 + 5, -1, 0, 1);
			player.getPA().sendConfig(61 + 5, 0);
			break;
		case 21585:
			player.currentPreset.getEquipment().setShield(-1);
			player.getPA().sendFrame34a(21579 + 6, -1, 0, 1);
			player.getPA().sendConfig(61 + 6, 0);
			break;
		case 21586:
			player.currentPreset.getEquipment().setLegs(-1);
			player.getPA().sendFrame34a(21579 + 7, -1, 0, 1);
			player.getPA().sendConfig(61 + 7, 0);
			break;
		case 21587:
			player.currentPreset.getEquipment().setBoots(-1);
			player.getPA().sendFrame34a(21579 + 8, -1, 0, 1);
			player.getPA().sendConfig(61 + 8, 0);
			break;
		case 21588:
			player.currentPreset.getEquipment().setGloves(-1);
			player.getPA().sendFrame34a(21579 + 9, -1, 0, 1);
			player.getPA().sendConfig(61 + 9, 0);
			break;
		case 21589:
			player.currentPreset.getEquipment().setRing(-1);
			player.getPA().sendFrame34a(21579 + 10, -1, 0, 1);
			player.getPA().sendConfig(61 + 10, 0);
			break;
		}

		if (!player.viewingInitialPreset) {
			savePreset(player, false);
		}
	}
	
	/**
	 * Finds an item in the bank
	 * @param player
	 * @param item
	 * @return
	 */
	private boolean findItemInBank(Player player, PresetItem item) {
		if (player.getItems().bankContains(item.getItemId(), item.getAmount())) {
			return true;
		} else {
			ItemDef itemList = ItemDef.forId(item.getItemId());
			if (item.getItemId() != -1) {
				player.sendMessage("You do not currently have a " + itemList.getName() + ".");
				return false;
			}
			return false;
		}
	}

	private GameItem findBankItem(Player player, PresetItem item) {
		for (GameItem bankItem : getBankItems(player)) {
			if (bankItem.id() == item.getItemId() + 1) {
				return bankItem;
			}
		}
		ItemDef itemList = ItemDef.forId(item.getItemId());
		if (item.getItemId() != -1) {
			player.sendMessage("You do not currently have a " + itemList.getName() + ".");
		}
		return null;
	}

	private List<GameItem> getBankItems(Player player) {
		List<GameItem> bankItems = new ArrayList<>();
		for (BankTab tab : player.getBank().getBankTab())
			bankItems.addAll(tab.getItems());
		return bankItems;
	}

	/**
	 * Allos users to edit their preset name
	 * @param player
	 */
	private void editPresetName(Player player) {
		if (player.inPresets == false && System.currentTimeMillis() - player.clickDelay <= 2200) {
			return;
		}
		if (player.inPresets == false) {
			CheatEngineBlock.PresetAlert(player);
			return;
		}
		if (player.presetViewingDefault) {
			player.sendMessage("You can't edit this preset name.");
			return;
		}

		player.getPA().sendEnterString("Name your preset");
	}
	
	/**
	 * Actual updating of the name in the global data
	 * @param player
	 * @param name
	 */
	public void updateName(Player player, String name) {
		if (player.inPresets == false && System.currentTimeMillis() - player.clickDelay <= 2200) {
			return;
		}
		if (player.inPresets == false) {
			CheatEngineBlock.PresetAlert(player);
			return;
		}
		ArrayList<Preset> presets = globalPresets.get(player.getLoginName().toLowerCase());
		if (presets != null) {
			if (player.presetViewingIndex < presets.size()) {
				Preset preset = presets.get(player.presetViewingIndex);
				if (preset != null) {
					preset.setName(name);
					globalPresets.put(player.getLoginName().toLowerCase(), presets);
					populateInterface(player, preset);
					updateTabs(player);
					saveToJSON(player, presets);
				}
			} else {
				player.sendMessage("Save a preset first before settings its name!");
			}
		} else {
			player.sendMessage("Save a preset first before settings its name!");
		}
	}
	
	/**
	 * Handles deleting a preset
	 * @param player
	 * @param index
	 */
	public void deletePreset(Player player, int index) {
		ArrayList<Preset> presets = globalPresets.get(player.getLoginName().toLowerCase());
		if (presets != null) {
			if (index >= presets.size()) {
				return;
			}
			Preset preset = presets.get(index);
			if (preset != null) {
				player.sendMessage("You have deleted preset: " + preset.getName());
				presets.remove(index);
				//updateTabs(player);
				globalPresets.put(player.getLoginName().toLowerCase(), presets);
				saveToJSON(player, presets);
				open(player);
			} else {
				return;
			}
		}
	}
	
	public void changeSpellBook(Player player) {
		if (player.presetViewingDefault) {
			player.sendMessage("You can't change this spellbook.");
			return;
		}
		
		if (player.currentPreset == null) {
			return;
		}
        
        int currentSpellBook = player.currentPreset.getSpellBook();
        currentSpellBook++;
        if (currentSpellBook == 3) {
        	currentSpellBook = 0;
        }
		
        player.currentPreset.setSpellBook(currentSpellBook);
        player.getPA().sendChangeSprite(254, (byte) currentSpellBook);
	}

	public void loadLastPreset(Player player) {
		if (player.lastPreset == null) {
			player.sendMessage("You need to have used a preset recently!", 255);
			return;
		}
		if (Boundary.isIn(player, Boundary.HESPORI)) {
			player.sendMessage("You cannot do that in this area.");
			return;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA)) {
			player.sendMessage("You cannot do that in this area.");
			return;
		}
		if (Boundary.isIn(player, Boundary.RAIDS)) {
			player.sendMessage("You cannot do that in this area.");
			return;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_LOBBY)) {
			player.sendMessage("You cannot do that in this area.");
			return;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
			player.sendMessage("You cannot do that in this area.");
			return;
		}
		if (Boundary.isIn(player, Boundary.FULL_RAIDS)) {
			player.sendMessage("You cannot do that in this area.");
			return;
		}
		if (player.isBusy() && !player.isBanking) {
			player.sendMessage("You are busy, please finish what you are doing.");
			return;
		}
		if (player.isInTradingPost() || player.isShopping || player.inTrade || player.isFping() || player.inDuel) {
			player.sendMessage("Please finish what you are doing.");
			return;
		}
		if (!Boundary.isIn(player, Boundary.ALLOWED_PRESET_LOAD_AREAS) && !player.isBanking) {
			player.sendMessage("You need to be near or have open a bank to use this keyboard action!", 255);
			return;
		}
		if (player.isBanking && Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
			player.sendMessage("Please leave the outlast area to do this.", 255);
			return;
		}
		player.getPA().stopSkilling();
		loadPreset(player, true);
	}
}
