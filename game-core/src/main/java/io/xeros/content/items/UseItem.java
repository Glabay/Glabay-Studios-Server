package io.xeros.content.items;

import java.util.Optional;

import dev.openrune.cache.CacheManager;
import io.xeros.Server;
import io.xeros.content.PetCollector;
import io.xeros.content.PlatinumTokens;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.ArdougneDiaryEntry;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.content.bosses.bryophyta.BryophytaNPC;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.dialogue.impl.FireOfDestructionDialogue;
import io.xeros.content.item.lootable.impl.CrystalChest;
import io.xeros.content.item.lootable.impl.FoeMysteryBox;
import io.xeros.content.items.item_combinations.Godswords;
import io.xeros.content.items.pouch.RunePouch;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.minigames.warriors_guild.AnimatedArmour;
import io.xeros.content.miniquests.magearenaii.MageArenaII;
import io.xeros.content.skills.Cooking;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.crafting.*;
import io.xeros.content.skills.firemake.Firemaking;
import io.xeros.content.skills.herblore.*;
import io.xeros.content.skills.prayer.Bone;
import io.xeros.content.skills.prayer.Prayer;
import io.xeros.content.skills.slayer.SlayerUnlock;
import io.xeros.content.trails.MasterClue;
import io.xeros.content.wogw.Wogw;
import io.xeros.model.Animation;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.net.packets.ClickObject;
import io.xeros.net.packets.objectoptions.impl.DarkAltar;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.ItemOnItemLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sanity
 * @author Ryan
 * @author Lmctruck30 Revised by Shawn Notes by Shawn
 */

public class UseItem {

	private static final Logger logger = LoggerFactory.getLogger(UseItem.class);

	public static void unNoteItems(Player c, int itemId, int amount) {
		String name = CacheManager.INSTANCE.getItem(itemId).getName();
		int counterpartId = Server.itemHandler.getCounterpart(itemId);
		
		/**
		 * If a player enters an amount which is greater than the amount of the item they have it will set it to the amount
		 * they currently have.
		 */
		int amountOfNotes = c.getItems().getItemAmount(itemId);
		if (amount > amountOfNotes) {
			amount = amountOfNotes;
		}
		
		/**
		 * Stops if you are trying to unnote an unnotable item
		 */
		
		if (counterpartId < 0) { 
			//c.sendMessage("You can only use unnotable items on this bank to un-note them.");
			return;
		}
		/**
		 * Stops if you do not have the item you are trying to unnote
		 */
		if (!c.getItems().playerHasItem(itemId, 1)) {
			return;
		}
		/**
		 * Stops if inside zammy hut
		 */
		if (Boundary.isIn(c, Boundary.WILDY_CHAOS_INSIDE_HUT) || Boundary.isIn(c, Boundary.WILDY_CHAOS_INSIDE_HUT2)) {
			c.sendMessage("You cannot reach the monk from inside here.");
			return;
		}
		/**
		 * Preventing from unnoting more items that you have space available
		 */
		if (amount > c.getItems().freeSlots()) {
			amount = c.getItems().freeSlots();
		}
		
		/**
		 * Stops if you do not have any space available
		 */
		if (amount <= 0) {
			c.sendMessage("You need at least one free slot to do this.");
			return;
		}

		//Wildy chaos hut monk, un-notes bones
		if (Boundary.isIn(c, Boundary.WILDY_CHAOS_HUT)) {
			if (!c.getItems().playerHasItem(995, 5000)) {
				c.sendMessage("You do not have enough coins to un-note your bones.");
				return;
			}
			if (c.getItems().getItemAmount(995) < 5000 * amount) {
				int affordBones = c.getItems().getItemAmount(995) / 5000;
				c.getItems().deleteItem(995, 5000 * affordBones);
				c.getItems().deleteItem2(itemId, affordBones);
				c.sendMessage("You do not have enough coins to un-note all your bones.");
				c.getItems().addItem(counterpartId, affordBones);
				c.getDH().sendStatement("You unnote x"+affordBones+" of " + name + ".");
				c.settingUnnoteAmount = false;
				c.unNoteItemId = 0;
				return;
			}
			c.getItems().deleteItem(995, 5000 * amount);
			c.getItems().deleteItem2(itemId, amount);
			c.getItems().addItem(counterpartId, amount);
			c.getDH().sendStatement("You unnote x" + amount + " of " + name + ".");
			c.settingUnnoteAmount = false;
			c.unNoteItemId = 0;
			return;
		}

			/**
			 * Deletes the noted item and adds the amount of unnoted items
			 */
			c.getItems().deleteItem2(itemId, amount);
			c.getItems().addItem(counterpartId, amount);
			c.getDH().sendStatement("You unnote x" + amount + " of " + name + ".");
			c.settingUnnoteAmount = false;
			c.sendMessage("You do not have enough coins to un-note your bones.44");
			c.unNoteItemId = 0;
			return;
		}


	/**
	 * Using items on an object.
	 * 
	 * @param c
	 * @param objectID
	 * @param objectX
	 * @param objectY
	 * @param itemId
	 */
	public static void ItemonObject(Player c, int objectID, int objectX, int objectY, int itemId) {
		if (!c.getItems().playerHasItem(itemId, 1)) return;
		c.clickObjectType = 0;
		var def = CacheManager.INSTANCE.getObject(objectID);
		var object = ClickObject.getObject(c, objectID, objectX, objectY);

        if (def.getName().toLowerCase().contains("bank")) {
            if (ItemDef.forId(itemId).isStackable()) {
                c.getPA().sendEnterAmount(0);
                c.unNoteItemId = itemId;
                c.settingUnnoteAmount = true;
            }
            else
                PlayerAssistant.noteItems(c, itemId);
        }

        var itemActionManager = Server.getItemOptionActionManager();
		if (itemActionManager.findHandlerById(itemId).isPresent()) {
			var itemAction = itemActionManager.findHandlerById(itemId).get();
			if (itemAction.performActionOnObject(c, object, itemId))
				return;
		}
		switch (objectID) {
			case 33311:
				c.objectYOffset = 5;
				c.objectXOffset = 5;
				c.objectDistance = 5;

				switch (itemId) {
					case 9698:
						c.getItems().deleteItem(9698, 1);
						c.getItems().addItem(9699, 1);
						break;

					case 9017:
						c.getItems().deleteItem(9017, 1);
						break;
				}

				break;
			case 6097:
				Wogw.donateItem(c, itemId);
				break;
		case 26782: 
			c.objectYOffset = 5;
			c.objectXOffset = 5;
			c.objectDistance = 5;
		    switch (itemId) {
		    case 1704:
	    		c.getItems().deleteItem(1704, 1);
		    	c.getItems().addItem(1712, 1);
		    	break;
		    case 1706:
		    	c.getItems().deleteItem(1706, 1);
		    	c.getItems().addItem(1712, 1);
		    	break;
		    case 1708:
		    	c.getItems().deleteItem(1708, 1);
		    	c.getItems().addItem(1712, 1);
		    	break;
		    case 1710:
		    	c.getItems().deleteItem(1710, 1);
		    	c.getItems().addItem(1712, 1);
		    	break;
		    }
		    c.sendMessage("You need at least 1 inventory space to do this.");
			break;
		case 172:
		case 170:
			c.objectDistance = 3;
			new CrystalChest().roll(c);
			break;
		case 33320:
			c.objectDistance = 3;
			//FireOfExchange.getExchangePrice(c, itemId);
		break;
		
		case 16469:
		case 2030: //Allows for ores to be used on the furnace instead of going though the interface.
				c.getSmithing().sendSmelting();
				break;
			case 33318:
				c.start(new FireOfDestructionDialogue(c, itemId));
				break;
		case 28900:
			switch (itemId) {
			case 19675:
				DarkAltar.handleRechargeArcLight(c);
				break;
			case 6746:
				DarkAltar.handleDarklightTransaction(c);
			}
			break;
		case 7813:
			if (itemId == 6055) {
				c.getItems().deleteItem(6055, 28);
			}
			break;
		
		case 9380:
		case 9385:
		case 9344:
		case 9345:
		case 9348:
			if (itemId == 6713) {
				Server.getGlobalObjects().remove(objectID, objectX, objectY, c.heightLevel);
				c.sendMessage("@cr10@Attempting to remove object..");
			}
			break;
		
		case 27029:
			if (itemId == 13273) {
				if (c.getItems().playerHasItem(13273)) {
					c.facePosition(3039, 4774);
					c.getDH().sendDialogues(700, -1);
				}
			}
			break;
		
		case 8927:
			switch (itemId) {
			case 1925:
			case 3727:
				c.getItems().deleteItem(1925, 1);
				c.getItems().addItem(1929, 1);
				c.sendMessage("You fill the bucket with water.");
				c.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.FILL_BUCKET);
				break;
			}
		
		case 3043:
		case 7143:
			if (itemId == 229) {
				if (Boundary.isIn(c, Boundary.VARROCK_BOUNDARY)) {
					c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.FILL_VIAL);
					c.getItems().deleteItem(229, 1);
					c.getItems().addItem(227, 1);
				}
			}
			break;
			
		case 11744:
			if (c.getMode().isUltimateIronman()) {

			}
			break;
			
		case 14888:
			if (itemId == 19529) {
				if (c.getItems().playerHasItem(6571)) {
					c.getItems().deleteItem(19529, 1);
					c.getItems().deleteItem(6571, 1);
					c.getItems().addItem(19496, 1);
					c.sendMessage("You successfully bind the two parts together into an uncut zenyte.");
				} else {
					c.sendMessage("You need an uncut onyx to do this.");
					return;
				}
			} else {
				BraceletMaking.craftBraceletDialogue(c);
			}
			break;

		case 25824:
			c.facePosition(objectX, objectY);
			SpinMaterial.getInstance().spin(c, itemId);
			break;

		case 23955:
			AnimatedArmour.itemOnAnimator(c, itemId);
			break;
		case 878:
			if (c.getPoints().useItem(itemId)) {
				c.getPoints().sendConfirmation(itemId);
			}
			break;
		case 2783:
		case 6150:
		case 2097:
			c.getSmithingInt().showSmithInterface(itemId);
			
			switch (itemId) {
			
			case 11286:
			case 1540:
					if (c.playerLevel[Player.playerSmithing] >= 90) {
						if (!c.getItems().playerHasItem(1540) || !c.getItems().playerHasItem(11286) || !c.getItems().playerHasItem(2347)) {
							c.sendMessage("You must have a draconic visage, dragonfire shield and a hammer in order to do this.");
							return;
						}
						c.startAnimation(898);
						c.getItems().deleteItem(1540, c.getItems().getInventoryItemSlot(1540), 1);
						c.getItems().deleteItem(11286, c.getItems().getInventoryItemSlot(11286), 1);
						c.getItems().addItem(11284, 1);
						c.getDH().sendItemStatement("You combine the two materials to create a dragonfire shield.", 11284);
						c.getPA().addSkillXPMultiplied(5, Player.playerSmithing, true);
					} else {
						c.sendMessage("You need a smithing level of 90 to create a dragonfire shield.");
					}
				break;
			}
			break;

		case 12269:
		case 2732:
		case 3039:
		case 114:
		case 5249:
		case 2728:
		case 26185:
		case 4488:
		case 27724:
		case 7183:
		case 26181:
			c.facePosition(objectX, objectY);
			Cooking.cookThisFood(c, itemId, objectID);
			break;

		case 409:
			Optional<Bone> bone = Prayer.isOperableBone(itemId);
			if (bone.isPresent()) {
				c.getPrayer().setAltarBone(bone);
				c.getPrayer().alter(c.getItems().getItemAmount(itemId), objectX, objectY);
				c.settingUnnoteAmount = false;
				c.boneOnAltar = true;
				return;
			}
			break;
		case 411:
			if (Boundary.isIn(c, Boundary.WILDY_BONE_ALTAR)) {
			Optional<Bone> WildyBone = Prayer.isOperableBone(itemId);
			if (WildyBone.isPresent()) {
				c.getPrayer().setAltarBone(WildyBone);
				c.getPrayer().alter(c.getItems().getItemAmount(itemId), objectX, objectY);
				c.settingUnnoteAmount = false;
				c.boneOnAltar = true;
				return;
			}
		}
			break;
		/*
		 * case 2728: case 12269: c.getCooking().itemOnObject(itemId); break;
		 */
		default:
			if (c.debugMessage)
				c.sendMessage("Player At Object id: " + objectID + " with Item id: " + itemId);
			break;
		}
	}

	/**
	 * Using items on items.
	 * 
	 * @param c
	 * @param itemUsed
	 * @param useWith
	 */
	public static void ItemonItem(final Player c, final int itemUsed, final int useWith, final int itemUsedSlot, final int usedWithSlot) {
		GameItem gameItemUsed = new GameItem(itemUsed, c.playerItemsN[itemUsedSlot], itemUsedSlot);
		GameItem gameItemUsedWith = new GameItem(useWith, c.playerItemsN[itemUsedSlot], usedWithSlot);
		Server.getLogging().write(new ItemOnItemLog(c, gameItemUsed, gameItemUsedWith, c.getPosition()));
		c.getPA().resetVariables();
		var itemCombinations = ItemCombinations.getCombinations(new GameItem(itemUsed), new GameItem(useWith));
		if (!itemCombinations.isEmpty()) {
			for (ItemCombinations combinations : itemCombinations) {
				var combination = combinations.getItemCombination();
				if (combination.isCombinable(c)) {
					c.setCurrentCombination(Optional.of(combination));
					c.dialogueAction = -1;
					c.nextChat = -1;
					combination.showDialogue(c);
					return;
				}
				else if (itemCombinations.size() == 1) {
					c.getDH().sendStatement("You don't have all of the items required for this combination.");
					return;
				}
			}
		}

		var itemActionManager = Server.getItemOptionActionManager();
		if (itemActionManager.findHandlerById(gameItemUsed.id()).isPresent()) {
			var itemAction = itemActionManager.findHandlerById(gameItemUsed.id()).get();
			if (itemAction.performActionOnItem(c, gameItemUsed, gameItemUsedWith))
				return;
		}

		// Handles Pvp weapon item on item interaction
		if (PvpWeapons.handleItemOnItem(c, itemUsed, useWith))
			return;

		if (BryophytaStaff.handleItemOnItem(c, itemUsed, useWith))
			return;
		if (SanguinestiStaff.useItem(c, itemUsed, useWith)) {
			return;
		}
		if (itemUsed == RunePouch.RUNE_POUCH_ID || useWith == RunePouch.RUNE_POUCH_ID) {
			c.getRunePouch().addRunesFromInventory(itemUsed == RunePouch.RUNE_POUCH_ID ? useWith : itemUsed, Integer.MAX_VALUE);
			return;
		}

		if (MaxCapeCombinations.mix(c, itemUsed, useWith))
			return;
		if (OrnamentedItem.ornament(c, itemUsed, useWith))
			return;

		if (itemUsed == 2 && useWith == 21726 || itemUsed == 21726 && useWith == 2) {
			if (c.getItems().playerHasItem(2) && c.getItems().playerHasItem(21726)) {
				int count = c.getItems().getInventoryCount(2);
				int count2 = c.getItems().getInventoryCount(21726);
				int lowestAmount = count;
				if (count > count2)
					lowestAmount = count2;
				c.getItems().deleteItem(2, lowestAmount);
				c.getItems().deleteItem(21726, lowestAmount);
				c.getItems().addItem(21728, lowestAmount);
			}
		}
		if (itemUsed == 21730 && useWith == 11836 || itemUsed == 11836 && useWith == 21730) {
			if (c.getItems().playerHasItem(21730) && c.getItems().playerHasItem(11836)) {
				c.getItems().deleteItem(21730, 1);
				c.getItems().deleteItem(11836, 1);
				c.getItems().addItem(21733, 1);
				c.sendMessage("The second you hold your tourmaline core by the Bandos boots, the core");
				c.sendMessage("Is absorbed rapidly. Perhaps there is some link between the metal in Bandos");
				c.sendMessage("and tourmaline...");
			}
		}
		if (itemUsed == 23804 && useWith == 21739 || itemUsed == 21739 && useWith == 23804) {
			if (c.getItems().playerHasItem(21739) && c.getItems().playerHasItem(23804)) {
				c.getItems().deleteItem(21739, 1);
				c.getItems().deleteItem(23804, 1);
				c.getItems().addItem(21752, 1);
			}
		}
		if (itemUsed == 22969 && useWith == 22971 || useWith == 22973 || itemUsed == 22971 && useWith == 22969 || useWith == 22973
				|| itemUsed == 22973 && useWith == 22969 || useWith == 22971) {
			if (c.getItems().playerHasItem(22969) && c.getItems().playerHasItem(22971) &&
					c.getItems().playerHasItem(22973)) {
				c.getItems().deleteItem(22969, 1);
				c.getItems().deleteItem(22971, 1);
				c.getItems().deleteItem(22973, 1);
				c.getItems().addItem(22975, 1);
				c.getDH().sendStatement("@blu@You have combined all 3 items and created the @red@Brimstone Ring.");
			} else {
				c.sendMessage("You need all the parts such as, heart, fang and eye to make the brimstone ring.");
			}
			return;
		}

		if (itemUsed == 22006 || useWith == 1540) {
			if (!c.getItems().playerHasItem(22006)) {
				c.sendMessage("You do not have the required item.");
				return;
			} else {
				c.getItems().deleteItem(22006, 1);
				c.getItems().deleteItem(1540, 1);
				c.getItems().addItem(22002, 1);
			}
		}

		if (itemUsed == 1775 || useWith == 1775) {
			if (!c.getItems().playerHasItem(1785)) {
				c.sendMessage("In order to do this you must have a glassblowing pipe.");
				return;
			}
			GlassBlowing.makeGlass(c, itemUsed, useWith);
		}
		if (itemUsed == 21043 && useWith == 6914 || (itemUsed == 6914 && useWith == 21043)) {
			if (c.getItems().playerHasItem(21043) &&
					c.getItems().playerHasItem(6914)) {
				c.getItems().deleteItem(itemUsed, 1);
				c.getItems().deleteItem(useWith, 1);
				c.getItems().addItem(21006, 1);
				c.getDH().sendItemStatement("You sucessfully make the kodai wand", 21006);
			}
		}
		if (itemUsed == 22477 && useWith == 12954 || itemUsed == 12954 && useWith == 22477) {
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().deleteItem(useWith, 1);
			c.getItems().addItem(22322, 1);
			c.getDH().sendItemStatement("You sucessfully make the avernic defender", 22322);
		}
		if (itemUsed == 2425 && useWith == 10499) {
			c.getItems().deleteItem(2425, 1);
			c.getItems().deleteItem(10499, 1);
			c.getItems().addItem(22109, 1);
			c.getDH().sendItemStatement("You sucessfully make the Ava's Assembler", 22109);
		}
		if (itemUsed == 21006 && useWith == 1540) {
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().deleteItem(useWith, 1);
			c.getItems().addItem(22002, 1);
			c.getDH().sendItemStatement("You sucessfully make the Dragonfire Ward", 22002);
		}

		if (itemUsed == Items.GRANITE_DUST && useWith == Items.CANNONBALL) {
			if (c.getPA().getLevelForXP(c.playerXP[Skill.SMITHING.getId()]) < 50) {
				c.sendMessage("You need a smithing level of 50 to do this.");
				return;
			}
			int graniteAmount;
			if (c.getItems().getItemAmount(itemUsed) < c.getItems().getItemAmount(useWith)) {
				graniteAmount = c.getItems().getItemAmount(itemUsed);
			} else {
				graniteAmount = c.getItems().getItemAmount(useWith);
			}
			c.getItems().deleteItem(itemUsed, c.getItems().getItemAmount(graniteAmount));
			c.getItems().deleteItem(useWith, c.getItems().getItemAmount(graniteAmount));
			c.getItems().addItem(Items.GRANITE_CANNONBALL, c.getItems().getItemAmount(graniteAmount));
			c.getDH().sendItemStatement("You apply a thick coating of granite dust to your cannonballs.", Items.GRANITE_CANNONBALL);
		}


		if (itemUsed == 23804 && useWith == 6737) {
			c.getItems().deleteItem(useWith, 1);
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().addItem(11773, 1);
		}
		if (itemUsed == 23804 && useWith == 12601) {
			c.getItems().deleteItem(useWith, 1);
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().addItem(13202, 1);
		}
		if (itemUsed == 23804 && useWith == 12603) {
			c.getItems().deleteItem(useWith, 1);
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().addItem(12691, 1);
		}
		if (itemUsed == 23804 && useWith == 12605) {
			c.getItems().deleteItem(useWith, 1);
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().addItem(12692, 1);
		}
		if (itemUsed == 23804 && useWith == 6731) {
			c.getItems().deleteItem(useWith, 1);
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().addItem(11770, 1);
		}
		if (itemUsed == 23804 && useWith == 6733) {
			c.getItems().deleteItem(useWith, 1);
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().addItem(11771, 1);
		}
		if (itemUsed == 23804 && useWith == 6735) {
			c.getItems().deleteItem(useWith, 1);
			c.getItems().deleteItem(itemUsed, 1);
			c.getItems().addItem(11772, 1);
		}
		if (itemUsed == 21295 && useWith == 13280 || itemUsed == 13280 && useWith == 21295) {
			if (!c.getItems().playerHasItem(13281)) {
				c.sendMessage("You also need the max hood to get the full set.");
				return;
			}
				c.getItems().deleteItem(itemUsed, 1);
				c.getItems().deleteItem(useWith, 1);
				c.getItems().addItem(21285, 1);
				c.getItems().addItem(21282, 1);
				c.getDH().sendItemStatement("You sucessfully make the Infernal Max Cape", 21285);
		}

		if (itemUsed == 3155 && useWith == 3157) {
			c.getItems().deleteItem(3155, 1);
			c.getItems().deleteItem(3157, 1);
			c.getItems().addItem(3159, 1);
		}
		if (c.getFletching().fletchBolt(itemUsed, useWith)) {
			return;
		}
		if (itemUsed == 23804 && (useWith == 19550)) {
			c.sendMessage("You must use a ring of suffering on another ring of suffering to imbue.");
			return;
		}
		if (itemUsed == 23804 && !(useWith == 6737 || useWith == 6731 || useWith == 6733 || useWith == 12601 || useWith == 12603 || useWith == 12605)) {
			c.sendMessage("This can only be used on dagganoth rings and wildy rings.");
			return;
		}
		if (c.getFletching().fletchBolt(useWith, itemUsed)) {
			return;
		}
		if (itemUsed == 1743 && useWith == 1733/* || itemUsed == 1733 || useWith == 1743*/) {
			if (!c.getItems().playerHasItem(1734)) {
				c.sendMessage("You need some thread!");
				return;
			}
			if (c.playerLevel[12] >= 28) {
					c.startAnimation(1249);
					c.getItems().deleteItem(1734, c.getItems().getInventoryItemSlot(1734), 1);
					c.getItems().deleteItem2(1743, 1);
					c.getItems().addItem(1131, 1);
					c.getPA().addSkillXPMultiplied(35, 12, true);
					//c.sendMessage("Crafting hardleather body.");
				} else {
					c.sendMessage("You need 28 crafting to do this.");
				}
		}

		if ((itemUsed == 946 && useWith == 19669) ||(itemUsed == 19669 && useWith == 946)) {
			if (!c.getItems().playerHasItem(19669)) {
				c.sendMessage("You ran out of logs!");
				return;
			}
			if (c.playerLevel[9] >= 90) {
				Achievements.increase(c, AchievementType.FLETCH, 1);
				c.startAnimation(1248);
				c.getItems().deleteItem2(19669, 1);
				c.getItems().addItem(52, 105);
				c.getPA().addSkillXPMultiplied(35, 9, true);
			} else {
				c.sendMessage("You need 90 Fletching to do this.");
			}
		}
		if (useWith >= 13579 && useWith <= 13678 || useWith >= 21061 && useWith <= 21076) {
			if (!c.getItems().isNoted(useWith)) {
				if (itemUsed == 3188) {
					if (!c.getItems().playerHasItem(useWith)) {
						return;
					}
					c.getItems().deleteItem2(useWith, 1);
					if (ItemDef.forId(useWith).getName().contains("hood")) {
						c.getItems().addItem(11850, 1);
					} else if (ItemDef.forId(useWith).getName().contains("cape")) {
						c.getItems().addItem(11852, 1);
					} else if (ItemDef.forId(useWith).getName().contains("top")) {
						c.getItems().addItem(11854, 1);
					} else if (ItemDef.forId(useWith).getName().contains("legs")) {
						c.getItems().addItem(11856, 1);
					} else if (ItemDef.forId(useWith).getName().contains("gloves")) {
						c.getItems().addItem(11858, 1);
					} else if (ItemDef.forId(useWith).getName().contains("boots")) {
						c.getItems().addItem(11860, 1);
					}
					c.sendMessage("You reverted your graceful piece.");
				}
			}
		}
		
		switch (useWith) {
		case 3016:
		case 12640:
			if (itemUsed == 12640 || itemUsed == 3016) {
				if (c.playerLevel[Skill.HERBLORE.getId()] < 77) {
					c.sendMessage("You need a herblore level of 77 to make Stamina potion.");
					return;
				}
				if (!c.getItems().playerHasItem(12640, 4) && !c.getItems().playerHasItem(3016)) {
					c.sendMessage("You must have 4 amylase crystals and a Super energy potion to do this.");
					return;
				}
				c.getItems().deleteItem(3016, 1);
				c.getItems().deleteItem(12640, 3);
				c.getItems().addItem(12625, 1);
				c.getPA().addSkillXPMultiplied(152, Skill.HERBLORE.getId(), true);
				c.sendMessage("You combine all of the ingredients and make a Stamina potion.");
				Achievements.increase(c, AchievementType.HERB, 1);
			}
			break;
		//case 12791:
			
				//c.getRunePouch().addItemToRunePouch(itemUsed, c.getItems().getItemAmount(itemUsed));
			
			//break;
			
		case 590:
			Firemaking.lightFire(c, itemUsed, "tinderbox");
			break;
		
		case 12773:
		case 12774:
			if (itemUsed == 3188) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().addItem(4151, 1);
				c.sendMessage("You cleaned the whip.");
			}
			break;
		

			/**
			 * Light ballista
			 */
		case 19586:
			if (itemUsed == 19592) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().addItem(19595, 1);
				c.sendMessage("You combined the two items and got an incomplete ballista.");
			}
			break;
			
			/**
			 * Heavy Ballista
			 */
		case 19589:
			if (itemUsed == 19592) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().addItem(19598, 1);
				c.sendMessage("You combined the two items and got an incomplete ballista.");
			}
			break;
			
			/**
			 * Both heavy and light ballista
			 */
		case 19601:
			if (itemUsed == 19598) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().addItem(19607, 1);
				c.sendMessage("You combined the two items and got an unstrung ballista.");
			}
			if (itemUsed == 19595) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().addItem(19604, 1);
				c.sendMessage("You combined the two items and got an unstrung ballista.");
			}
			break;
		case 19610:
			if (itemUsed == 19607) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().addItem(19481, 1);
				c.sendMessage("You combined the two items and got a heavy ballista.");
			}
			if (itemUsed == 19604) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().addItem(19478, 1);
				c.sendMessage("You combined the two items and got a light ballista.");
			}
			break;
		
		case LootingBag.LOOTING_BAG:
		case LootingBag.LOOTING_BAG_OPEN:
			c.getLootingBag().useItemOnBag(itemUsed);
			break;
		
			
		case 13226:
			c.getHerbSack().addItemToHerbSack(itemUsed, c.getItems().getItemAmount(itemUsed));
			break;
			
		case 12020:
			c.getGemBag().addItemToGemBag(itemUsed, c.getItems().getItemAmount(itemUsed));
			break;
		}
		switch (itemUsed) {

		case 985:
		case 987:
		CrystalChest.makeKey(c);
			break;
		
		case 590:
			Firemaking.lightFire(c, useWith, "tinderbox");
			break;
			
		case 12792:
			RecolourGraceful.ITEM_TO_RECOLOUR = useWith;
			c.getDH().sendDialogues(55, -1);
			break;
		}
		if (itemUsed == 20718 && (useWith == 20716 || useWith == 20714)) { //tome of fire
			TomeOfFire.store(c);
            return;
        }
		if (itemUsed == 1042 && useWith == 12337 || useWith == 1042 && itemUsed == 12337) {
			c.getItems().deleteItem2(1042, 1);
			c.getItems().deleteItem2(12337, 1);
			c.getItems().addItem(12399, 1);
			c.sendMessage("You combine the spectacles and the hat to make a partyhat and specs.");
			return;
		}
		if (itemUsed == 3150 && useWith == 3157 || itemUsed == 3157 && useWith == 3150) {
			if (c.getItems().playerHasItem(3150) && c.getItems().playerHasItem(3157)) {
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().addItem(3159, 1);
			}
		}

		if (itemUsed == 23824 && useWith == 21183) { //slaughter charges
			int slaughterChargeAmount = c.getItems().getItemAmount(23824);
			c.getItems().deleteItem(23824, slaughterChargeAmount);
			c.increaseSlaughterCharge(slaughterChargeAmount);
			int stored = c.slaughterCharge;
			c.sendMessage("You now have " +stored+ " charges in the Bracelet of slaughter.");
			return;
		}

		if (itemUsed == 2399 && useWith == 8167) { //foe m box unlock
			if (c.getItems().playerHasItem(2399) && c.getItems().playerHasItem(8167)) {
				c.getItems().deleteItem2(2399, 1);
				new FoeMysteryBox(c).quickOpen();
			}
			return;
		}

		if (itemUsed == 21820 && useWith == 21817) { //bracelet of ether
			int etheramount = c.getItems().getItemAmount(21820);
			int maxEtherToAdd = 16000 - c.braceletEtherCount;
			if ((c.braceletEtherCount + etheramount) >= 16000) {
				etheramount = maxEtherToAdd;
			}
			c.braceletIncrease(etheramount);
            c.getItems().deleteItem(itemUsed, etheramount);
            c.sendMessage("You now have "+c.braceletEtherCount +" ether in your bracelet");
			c.getItems().addItem(21816, 1);
            c.getItems().deleteItem(21817, 1);
            return;
        }
		if (itemUsed == 21820 && useWith == 21816) { //bracelet of ether charged
			int etheramount = c.getItems().getItemAmount(21820);
			int maxEtherToAdd = 16000 - c.braceletEtherCount;
			if ((c.braceletEtherCount + etheramount) >= 16000) {
				etheramount = maxEtherToAdd;
			}
			c.braceletIncrease(etheramount);
			c.getItems().deleteItem(21820, etheramount);
            c.sendMessage("You now have "+c.braceletEtherCount +" ether in your bracelet");
            return;
        }
		if (itemUsed == 12929 || useWith == 12929) {
			if (useWith == 13200 || itemUsed == 13200 || useWith == 13201 || itemUsed == 13201) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().deleteItem2(itemUsed, 1);
				int mutagen = useWith == 13200 || itemUsed == 13200 ? 13196 : 13198;
				c.getItems().addItem(mutagen, 1);
			}
		}
		if (itemUsed == 12932 && useWith == 11791 || itemUsed == 11791 && useWith == 12932) {
			if (c.playerLevel[Skill.CRAFTING.getId()] < 59) {
				c.sendMessage("You need 59 crafting to do this.");
				return;
			}
			if (!c.getItems().playerHasItem(1755)) {
				c.sendMessage("You need a chisel to do this.");
				return;
			}
			c.getItems().deleteItem2(itemUsed, 1);
			c.getItems().deleteItem2(useWith, 1);
			c.getItems().addItem(12902, 1);
			c.sendMessage("You attach the magic fang to the trident and create an uncharged toxic staff of the dead.");
			return;
		}
		if (itemUsed == 12932 && useWith == 11907 || itemUsed == 11907 && useWith == 12932) {
			if (c.playerLevel[Skill.CRAFTING.getId()] < 59) {
				c.sendMessage("You need 59 crafting to do this.");
				return;
			}
			if (!c.getItems().playerHasItem(1755)) {
				c.sendMessage("You need a chisel to do this.");
				return;
			}
			if (c.getTridentCharge() > 0) {
				c.sendMessage("You cannot do this whilst your trident has charge.");
				return;
			}
			c.getItems().deleteItem2(itemUsed, 1);
			c.getItems().deleteItem2(useWith, 1);
			c.getItems().addItem(12899, 1);
			c.sendMessage("You attach the magic fang to the trident and create a trident of the swamp.");
			return;
		}
		if (itemUsed == 21347 && useWith == 1755 || itemUsed == 1755 && useWith == 21347) {
			c.getItems().handleAmethyst();
			return;
		}

		if (itemUsed == 554 || itemUsed == 560 || itemUsed == 562) {
			if (useWith == 11907)
				c.getDH().sendDialogues(52, -1);
		}
		if (itemUsed == 554 || itemUsed == 560 || itemUsed == 562 || itemUsed == 12934) {
			if (useWith == 12899)
				c.getDH().sendDialogues(53, -1);
			
		}

		if (itemUsed == 12927 && useWith == 1755 || itemUsed == 1755 && useWith == 12927) {
			int visage = itemUsed == 12927 ? itemUsed : useWith;
			if (c.playerLevel[Skill.CRAFTING.getId()] < 52) {
				c.sendMessage("You need a crafting level of 52 to do this.");
				return;
			}
			c.getItems().deleteItem2(visage, 1);
			c.getItems().addItem(12929, 1);
			c.sendMessage("You craft the serpentine visage into a serpentine helm (empty).");
			c.sendMessage("Charge the helm with 11,000 scales.");
			return;
		}
		if (itemUsed == 1755 && useWith == 13173 || itemUsed == 13173 && useWith == 1755) {
			if (6 < c.getItems().freeSlots()) {
				c.sendMessage("You need at least 6 inventory spaces to do this.");
				return;
			}
			c.getItems().deleteItem(13173, 1);
			c.getItems().deleteItem(1755, 1);
			c.getItems().addItem(1038, 1);
			c.getItems().addItem(1040, 1);
			c.getItems().addItem(1042, 1);
			c.getItems().addItem(1044, 1);
			c.getItems().addItem(1046, 1);
			c.getItems().addItem(1048, 1);
			c.sendMessage("You have just unboxed the partyhat set.");
			c.sendMessage("Your chisel has just broken on such a heavy package.");
			return;
		}

		if (itemUsed == 12934 && useWith == 12902 || itemUsed == 12902 && useWith == 12934) {
			if (!c.getItems().playerHasItem(12902)) {
				c.sendMessage("You need the uncharged toxic staff of the dead to do this.");
				return;
			}
			if (c.getToxicStaffOfTheDeadCharge() > 0) {
				c.sendMessage("You must uncharge your current toxic staff of the dead to re-charge.");
				return;
			}
			int amount = c.getItems().getItemAmount(12934);
			if (amount > 11000) {
				amount = 11000;
				c.sendMessage("The staff only required 11,000 zulrah scales to fully charge.");
			}
			c.getItems().deleteItem2(12934, amount);
			c.getItems().deleteItem2(12902, 1);
			c.getItems().addItem(12904, 1);
			c.setToxicStaffOfTheDeadCharge(amount);
			c.sendMessage("You charge the toxic staff of the dead for " + amount + " zulrah scales.");
			return;
		}

		if (itemUsed == 12929 || itemUsed == 13196 || itemUsed == 13198 || useWith == 12929 || useWith == 13196 || useWith == 13198) {
			int helm = itemUsed == 12929 || itemUsed == 13196 || itemUsed == 13198 ? itemUsed : useWith;
			if (useWith == 12934 || itemUsed == 12934) {
				if (c.getSerpentineHelmCharge() > 0) {
					c.sendMessage("You must uncharge your current helm to re-charge.");
					return;
				}
				int amount = c.getItems().getItemAmount(12934);
				if (amount > 11000) {
					amount = 11000;
					c.sendMessage("The helm only required 11,000 zulrah scales to fully charge.");
				}
				c.getItems().deleteItem2(12934, amount);
				c.getItems().deleteItem2(helm, 1);
				c.getItems().addItem(helm == 12929 ? 12931 : helm == 13196 ? 13197 : 13199, 1);
				c.setSerpentineHelmCharge(amount);
				c.sendMessage("You charge the " + ItemDef.forId(helm).getName() + " helm for " + amount + " zulrah scales.");
				return;
			}
		}
		if (itemUsed == 12924 || useWith == 12924) {
			int ammo = itemUsed == 12924 ? useWith : itemUsed;
			ItemDef definition = ItemDef.forId(ammo);
			int amount = c.getItems().getItemAmount(ammo);
			if (ammo == 12934) {
				c.sendMessage("Select a dart to store and have the equivellent amount of scales.");
				return;
			}
			int[] darts = { 806, 807, 808, 809, 810, 811, 812, 813, 814, 815, 816, 817, 5628, 5629, 5630, 5632, 5633, 5634, 5635, 5636, 5637, 5639, 5640, 5641, 11230, 11231, 11233,
					11234 };
			if (definition == null || Misc.linearSearch(darts, ammo) == -1) {
				c.sendMessage("That item cannot be equipped with the blowpipe.");
				return;
			}
			if (c.getToxicBlowpipeAmmoAmount() > 0) {
				c.sendMessage("The blowpipe already has ammo, you need to unload it first.");
				return;
			}
			if (amount < 100) {
				c.sendMessage("You need 100 of this item to store it in the pipe.");
				return;
			}
			if (!c.getItems().playerHasItem(12934, amount)) {
				c.sendMessage("You need at least " + amount + " scales in combination with the " + definition.getName() + " to charge this.");
				return;
			}
			if (!c.getItems().playerHasItem(12924)) {
				c.sendMessage("You need a toxic blowpipe (empty) to do this.");
				return;
			}
			if (amount > 16383) {
				c.sendMessage("The blowpipe can only store 16,383 charges at any given time.");
				amount = 16383;
			}
			c.getItems().deleteItem2(12924, 1);
			c.getItems().addItem(12926, 1);
			c.getItems().deleteItem2(ammo, amount);
			c.getItems().deleteItem2(12934, amount);
			c.setToxicBlowpipeCharge(amount);
			c.setToxicBlowpipeAmmo(ammo);
			c.setToxicBlowpipeAmmoAmount(amount);
			c.sendMessage("You store " + amount + " " + definition.getName() + " into the blowpipe and charge it with scales.");
			return;
		}
		if (itemUsed == 12922 && useWith == 1755 || itemUsed == 1755 && useWith == 12922) {
			if (c.playerLevel[Skill.FLETCHING.getId()] >= 53) {
				c.getItems().deleteItem2(12922, 1);
				c.getItems().addItem(12924, 1);
				c.getPA().addSkillXP(10000, Skill.FLETCHING.getId(), true);
				c.sendMessage("You fletch the fang into a toxic blowpipe.");
			} else {
				c.sendMessage("You need a fletching level of 53 to do this.");
			}
			return;
		}
		if (itemUsed == 233 && useWith == 22124) {
			if (c.getItems().playerHasItem(22124)) {
				c.getItems().deleteItem2(22124, 1);
				c.getItems().addItem(21975, 1);
			}
		}
		//Start of Rock Golems
		if (itemUsed == 438 && useWith == 13321) {
			c.getItems().deleteItem2(13321, 1);
			c.getItems().deleteItem2(438, 1);
			c.getItems().addItem(21187, 1);
			c.sendMessage("Use a cleaning cloth to reset your pet's color.");
		}
		if (itemUsed == 436 && useWith == 13321) {
			c.getItems().deleteItem2(13321, 1);
			c.getItems().deleteItem2(436, 1);
			c.getItems().addItem(21188, 1);
			c.sendMessage("Use a cleaning cloth to reset your pet's color.");
		}
		if (itemUsed == 440 && useWith == 13321) {
			c.getItems().deleteItem2(13321, 1);
			c.getItems().deleteItem2(440, 1);
			c.getItems().addItem(21189, 1);
			c.sendMessage("Use a cleaning cloth to reset your pet's color.");
		}
		if (itemUsed == 453 && useWith == 13321) {
			c.getItems().deleteItem2(13321, 1);
			c.getItems().deleteItem2(453, 1);
			c.getItems().addItem(21192, 1);
			c.sendMessage("Use a cleaning cloth to reset your pet's color.");
		}
		if (itemUsed == 444 && useWith == 13321) {
			c.getItems().deleteItem2(13321, 1);
			c.getItems().deleteItem2(444, 1);
			c.getItems().addItem(21193, 1);
			c.sendMessage("Use a cleaning cloth to reset your pet's color.");
		}
		if (itemUsed == 447 && useWith == 13321) {
			c.getItems().deleteItem2(13321, 1);
			c.getItems().deleteItem2(447, 1);
			c.getItems().addItem(21194, 1);
			c.sendMessage("Use a cleaning cloth to reset your pet's color.");
		}
		if (itemUsed == 449 && useWith == 13321) {
			c.getItems().deleteItem2(13321, 1);
			c.getItems().deleteItem2(449, 1);
			c.getItems().addItem(21196, 1);
			c.sendMessage("Use a cleaning cloth to reset your pet's color.");
		}
		if (itemUsed == 451 && useWith == 13321) {
			c.getItems().deleteItem2(13321, 1);
			c.getItems().deleteItem2(451, 1);
			c.getItems().addItem(21197, 1);
			c.sendMessage("Use a cleaning cloth to reset your pet's color.");
		}
		//Cleaning
		if (itemUsed == 3188 && useWith == 21187) {
			c.getItems().deleteItem2(21187, 1);
			c.getItems().addItem(13321, 1);
		}
		if (itemUsed == 3188 && useWith == 21188) {
			c.getItems().deleteItem2(21188, 1);
			c.getItems().addItem(13321, 1);
		}
		if (itemUsed == 3188 && useWith == 21189) {
			c.getItems().deleteItem2(21189, 1);
			c.getItems().addItem(13321, 1);
		}
		if (itemUsed == 3188 && useWith == 21192) {
			c.getItems().deleteItem2(21192, 1);
			c.getItems().addItem(13321, 1);
		}
		if (itemUsed == 3188 && useWith == 21193) {
			c.getItems().deleteItem2(21193, 1);
			c.getItems().addItem(13321, 1);
		}
		if (itemUsed == 3188 && useWith == 21194) {
			c.getItems().deleteItem2(21194, 1);
			c.getItems().addItem(13321, 1);
		}
		if (itemUsed == 3188 && useWith == 21196) {
			c.getItems().deleteItem2(21196, 1);
			c.getItems().addItem(13321, 1);
		}
		if (itemUsed == 3188 && useWith == 21197) {
			c.getItems().deleteItem2(21197, 1);
			c.getItems().addItem(13321, 1);
		}
		//End
		if (itemUsed == 53 || useWith == 53) {
			int arrow = itemUsed == 53 ? useWith : itemUsed;
			c.getFletching().fletchArrow(arrow);
		}
		if (itemUsed == 19584 || useWith == 19584) {
			int javelin = itemUsed == 19584 ? useWith : itemUsed;
			c.getFletching().fletchJavelin(javelin);
		}
		if (itemUsed == 52 && useWith == 314 || itemUsed == 314 && useWith == 52) {
			c.getFletching().fletchHeadlessArrows();
		}
		if (itemUsed == 1777 || useWith == 1777) {
			int unstrung = itemUsed == 1777 ? useWith : itemUsed;
			c.getFletching().fletchUnstrung(unstrung);
		}
		if (itemUsed == 9438 || useWith == 9438) {
			int unstrung = itemUsed == 9438 ? useWith : itemUsed;
			c.getFletching().fletchUnstrungCross(unstrung);
		}
		if (itemUsed == 314 || useWith == 314) {
			int item = itemUsed == 314 ? useWith : itemUsed;
			c.getFletching().fletchUnfinishedBolt(item);
			c.getFletching().fletchDart(item);
		}
		if (itemUsed == 1733 || useWith == 1733) {
			LeatherMaking.craftLeatherDialogue(c, itemUsed, useWith);
		}
		if (itemUsed == 1391 || useWith == 1391) {
			BattlestaveMaking.craftBattlestaveDialogue(c, itemUsed, useWith);
		}
		if (itemUsed == 1759 || useWith == 1759) {
			JewelryMaking.stringAmulet(c, itemUsed, useWith);
		}
		if (itemUsed == 1755 || useWith == 1755) {
			c.getFletching().fletchGem(useWith, itemUsed);
			GemCutting.cut(c, useWith, itemUsed);
		}

		if (BryophytaStaff.attach(c, itemUsed, useWith))
			return;

		if (useWith == 946 || itemUsed == 946) {
			c.getFletching().combine(useWith, itemUsed);
		}
		if (itemUsed == 12526 && useWith == 6585 || itemUsed == 6585 && useWith == 12526) {
			c.getDH().sendDialogues(580, -1);
		}
		if (itemUsed == 20062 && useWith == 19553 || itemUsed == 19553 && useWith == 20062) {
			c.getDH().sendDialogues(580, -1);
		}
		if (itemUsed == 22246 && useWith == 19547 || itemUsed == 19547 && useWith == 22246) {
			c.getDH().sendDialogues(580, -1);
		}

		if (itemUsed == 20065 && useWith == 12002 || itemUsed == 12002 && useWith == 20065) {
			c.getDH().sendDialogues(580, -1);
		}
		if (itemUsed == 11235 || useWith == 11235) {
			if (itemUsed == 11235 && useWith == 12757 || useWith == 11235 && itemUsed == 12757) {
				c.getDH().sendDialogues(566, 315);
			} else if (itemUsed == 11235 && useWith == 12759 || useWith == 11235 && itemUsed == 12759) {
				c.getDH().sendDialogues(569, 315);
			} else if (itemUsed == 11235 && useWith == 12761 || useWith == 11235 && itemUsed == 12761) {
				c.getDH().sendDialogues(572, 315);
			} else if (itemUsed == 11235 && useWith == 12763 || useWith == 11235 && itemUsed == 12763) {
				c.getDH().sendDialogues(575, 315);
			}
		}
		if (itemUsed == 4081 && useWith == 4081) {
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().addItem(10588, 1);
				c.getDH().sendStatement("You have combined 2 salve amulets to create the (e) version..");
				c.nextChat = -1;
			}
		if (itemUsed == 12804 && useWith == 11838 || itemUsed == 11838 && useWith == 12804) {
			// c.getDH().sendDialogues(550, -1);
		}
		if (itemUsed == 12802 || useWith == 12802) {
			if (itemUsed == 12802 && useWith == 11924 || itemUsed == 11924 && useWith == 12802) {
				c.getDH().sendDialogues(561, 315);
			} else if (itemUsed == 12802 && useWith == 11926 || itemUsed == 11926 && useWith == 12802) {
				c.getDH().sendDialogues(558, 315);
			}
		}
		if (itemUsed == 3224 && useWith == 12766 || useWith == 12766 && itemUsed == 3224) {
			if (c.getItems().playerHasItem(12766) && c.getItems().playerHasItem(3224)) {
				c.getItems().deleteItem2(12766, 1);
				c.getItems().addItem(11235, 1);
				c.getDH().sendStatement("You have cleaned your dark bow.");
				c.nextChat = -1;
			}
		}
		if (itemUsed == 3224 && useWith == 12767 || useWith == 12767 && itemUsed == 3224) {
			if (c.getItems().playerHasItem(12767) && c.getItems().playerHasItem(3224)) {
				c.getItems().deleteItem2(12767, 1);
				c.getItems().addItem(11235, 1);
				c.getDH().sendStatement("You have cleaned your dark bow.");
				c.nextChat = -1;
			}
		}
		if (itemUsed == 3224 && useWith == 12768 || useWith == 12768 && itemUsed == 3224) {
			if (c.getItems().playerHasItem(12768) && c.getItems().playerHasItem(3224)) {
				c.getItems().deleteItem2(12768, 1);
				c.getItems().addItem(11235, 1);
				c.getDH().sendStatement("You have cleaned your dark bow.");
				c.nextChat = -1;
			}
		}
		if (itemUsed == 3224 && useWith == 12773 || useWith == 12773 && itemUsed == 3224) {
			if (c.getItems().playerHasItem(12773) && c.getItems().playerHasItem(3224)) {
				c.getItems().deleteItem2(12773, 1);
				c.getItems().addItem(4151, 1);
				c.getDH().sendStatement("You have cleaned your whip.");
				c.nextChat = -1;
			}
		}
		if (itemUsed == 3224 && useWith == 12774 || useWith == 12774 && itemUsed == 3224) {
			if (c.getItems().playerHasItem(12774) && c.getItems().playerHasItem(3224)) {
				c.getItems().deleteItem2(12774, 1);
				c.getItems().addItem(4151, 1);
				c.getDH().sendStatement("You have cleaned your whip.");
				c.nextChat = -1;
			}
		}
		if (itemUsed == 4153 && useWith == 12849 || itemUsed == 12849 && useWith == 4153) {
			c.getDH().sendDialogues(563, 315);
		}
		if (itemUsed == 12786 && useWith == 861 || useWith == 12786 && itemUsed == 861) {
			if (c.getItems().playerHasItem(12786) && c.getItems().playerHasItem(861)) {
				c.getItems().deleteItem2(12786, 1);
				c.getItems().deleteItem2(861, 1);
				c.getItems().addItem(12788, 1);
				c.getDH().sendStatement("You have imbued your Magic Shortbow.");
				c.nextChat = -1;
			}
		}
		if (itemUsed == 21257 && useWith == 4170 || useWith == 21257 && itemUsed == 4170) {
			if (c.getItems().playerHasItem(21257) && c.getItems().playerHasItem(4170)) {
				c.getItems().deleteItem2(21257, 1);
				c.getItems().deleteItem2(4170, 1);
				c.getItems().addItem(21255, 1);
				c.getDH().sendStatement("You have enchanted your Slayer's Staff.");
				c.nextChat = -1;
			}
		}

		if (itemUsed == 11889 && useWith == 22966 || useWith == 22966 && itemUsed == 11889) { //dragon huner lance
			if (c.getItems().playerHasItem(11889) && c.getItems().playerHasItem(22966)) {
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().addItem(22978, 1);
				c.getDH().sendStatement("You have created your Dragon Hunter Lance.");
				c.nextChat = -1;
			}
	}
		if((itemUsed == 24511 && useWith == 24422)) {//harmonised staff
			if (c.getItems().playerHasItem(24511, 1)&& c.getItems().playerHasItem(24422, 1)) {
				c.getItems().deleteItem(24511, 1);
				c.getItems().deleteItem(24422, 1);
				c.getItems().addItem(24423, 1);
				c.sendMessage("You successfully add the orb to the staff!");
			} else {
				c.sendMessage("You do not have all the stuff to create this.");
				return;
			}
		}
		if((itemUsed == 24514 && useWith == 24422)) {//volatile staff
			if (c.getItems().playerHasItem(24514, 1)&& c.getItems().playerHasItem(24422, 1)) {
				c.getItems().deleteItem(24514, 1);
				c.getItems().deleteItem(24422, 1);
				c.getItems().addItem(24424, 1);
				c.sendMessage("You successfully add the orb to the staff!");
			} else {
				c.sendMessage("You do not have all the stuff to create this.");
				return;
			}
		}
		if((itemUsed == 24517 && useWith == 24422)) {//eldritch staff
			if (c.getItems().playerHasItem(24517, 1)&& c.getItems().playerHasItem(24422, 1)) {
				c.getItems().deleteItem(24517, 1);
				c.getItems().deleteItem(24422, 1);
				c.getItems().addItem(24425, 1);
				c.sendMessage("You successfully add the orb to the staff!");
			} else {
				c.sendMessage("You do not have all the stuff to create this.");
				return;
			}
		}


		if((itemUsed == 24268 && useWith == 10828)) {//faceguard
			if (c.getItems().playerHasItem(24268, 1)&& c.getItems().playerHasItem(10828, 1)) {
				c.getItems().deleteItem(24268, 1);
				c.getItems().deleteItem(10828, 1);
				c.getItems().addItem(24271, 1);
				c.sendMessage("You successfully make the Neitiznot faceguard!");
			} else {
				c.sendMessage("You do not have all the stuff to create this.");
				return;
			}
		}

		if((itemUsed == 19550 && useWith == 19550)) {//ros
			if (c.getItems().playerHasItem(19550)) {
				c.getItems().deleteItem(19550, 1);
				c.getItems().deleteItem(19550, 1);
				c.getItems().addItem(19710, 1);
				c.sendMessage("You successfully created ring of suffering (i).");
			} else {
				c.sendMessage("You do not have all the stuff to create this.");
				return;
			}
		}
		if((itemUsed == 13116 && useWith == 22111) || (itemUsed == 22111 && useWith == 13116)){
			if(c.getItems().playerHasItem(13116) && c.getItems().playerHasItem(22111) && c.getItems().playerHasItem(22988)) {
				c.getItems().deleteItem2(itemUsed, 1);
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().deleteItem2(22988, 1);
				c.getItems().addItemUnderAnyCircumstance(22986, 1);
				c.sendMessage("You have succesfully created the Dragonbone necklace.");
			}
		}
		if((itemUsed == 4155 && useWith == 8901) ||(itemUsed == 4551 && useWith == 8901)){
			if(c.playerLevel[12] < 55) {
				c.sendMessage("You must have a crafting level of at least 55 to create the slayer helmet.");
				return;
			}
			if(!c.getSlayer().getUnlocks().contains(SlayerUnlock.MALEVOLENT_MASQUERADE)) {
				c.sendMessage("You must learn how to create a slayer helmet before you can make one.");
				return;
			}
			if(c.getItems().playerHasItem(4551) && c.getItems().playerHasItem(4166) && c.getItems().playerHasItem(4168) && c.getItems().playerHasItem(4164) && c.getItems().playerHasItem(8901) &&c.getItems().playerHasItem(4155)){
				c.getItems().deleteItem2(4551, 1);
				c.getItems().deleteItem2(4166, 1);
				c.getItems().deleteItem2(4168, 1);
				c.getItems().deleteItem2(4164, 1);
				c.getItems().deleteItem2(8901, 1);
				c.getItems().deleteItem2(4155, 1);
				c.getItems().addItemUnderAnyCircumstance(11864,1);
			}
		}
		if (PotionDecanting.get().isPotion(gameItemUsed) && PotionDecanting.get().isPotion(gameItemUsedWith)) {
			if (PotionDecanting.get().matches(gameItemUsed, gameItemUsedWith)) {
				PotionDecanting.get().mix(c, gameItemUsed, gameItemUsedWith);
			} else {
				c.sendMessage("You cannot combine two potions of different types.");
			}
			return;
		}
		if (PoisonedWeapon.poisonWeapon(c, itemUsed, useWith)) {
			return;
		}
		if (Crushable.crushIngredient(c, itemUsed, useWith)) {
			return;
		}
		if (itemUsed == 227 || useWith == 227) {
			GameItem item = new GameItem(itemUsed);
			if (c.getHerblore().makeUnfinishedPotion(c, item))
				return;
		}
		c.getHerblore().mix(useWith);

		if (itemUsed == 269 || useWith == 12907) {
			if (c.getLevelForXP(c.playerXP[Player.playerHerblore]) < 90) {
				c.sendMessage("You need a Herblore level of " + 90 + " to make this potion.");
				return;
			}
			if (c.getItems().playerHasItem(269) && c.getItems().playerHasItem(12907)) {
				c.getItems().deleteItem(269, c.getItems().getInventoryItemSlot(269), 1);
				c.getItems().deleteItem2(12907, 1);
				c.getItems().addItem(12915, 1);
				c.getPA().addSkillXPMultiplied(125, Skill.HERBLORE.getId(), true);
				c.sendMessage("You put the " + ItemDef.forId(269).getName() + " into the Anti-venom and create a " + ItemDef.forId(12915).getName() + ".");
			} else {
				c.sendMessage("You have run out of supplies to do this.");
				return;
			}

		}
		if (itemUsed >= 11818 && itemUsed <= 11822 && useWith >= 11818 && useWith <= 11822) {
			if (c.getItems().hasAllShards()) {
				c.getItems().makeBlade();
			} else {
				c.sendMessage("@blu@You need to have all the shards to combine them into a blade.");
			}
		}
		if (itemUsed == 21043 && itemUsed == 6914 || useWith == 21043 && useWith == 6914) {
			if (c.getItems().hasAllKodai()) {
				c.getItems().makeKodai();
			} else {
				c.sendMessage("@blu@You need to have a Kodai insignia and a master wand to create a Kodai wand.");
			}
		}
		if (itemUsed >= 19679 && itemUsed <= 19683 && useWith >= 19679 && useWith <= 19683) {
			if (c.getItems().hasAllPieces()) {
				c.getItems().makeTotem();
			} else {
				c.sendMessage("@blu@You need to have all the pieces to make them into a dark totem.");
			}
		}
		if (itemUsed == 2368 && useWith == 2366 || itemUsed == 2366 && useWith == 2368) {
			c.getItems().deleteItem(2368, c.getItems().getInventoryItemSlot(2368), 1);
			c.getItems().deleteItem(2366, c.getItems().getInventoryItemSlot(2366), 1);
			c.getItems().addItem(1187, 1);
			c.getDH().sendStatement("You combine the two shield halves to create a full shield.");
			if (Boundary.isIn(c, Boundary.ARDOUGNE_BOUNDARY)) {
				c.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.DRAGON_SQUARE);
			}
		}

		if (c.getItems().isHilt(itemUsed) || c.getItems().isHilt(useWith)) {
			int hilt = c.getItems().isHilt(itemUsed) ? itemUsed : useWith;
			int blade = c.getItems().isHilt(itemUsed) ? useWith : itemUsed;
			if (blade == 11798) {
				Godswords.makeGodsword(c, hilt);
			}
		}

		switch (itemUsed) {
		/*
		 * case 1511: case 1521: case 1519: case 1517: case 1515: case 1513: case 590: c.getFiremaking().checkLogType(itemUsed, useWith); break;
		 */

		default:
			if (c.debugMessage)
				c.sendMessage("Player used Item id: " + itemUsed + " with Item id: " + useWith);
			break;
		}
	}

	/**
	 * Using items on NPCs.
	 */
	public static void handleItemOnNPC(Player player, NPC npc, int itemId, int slot) {
		player.npcClickIndex = npc.getIndex();
		int npcId = npc.getNpcId();

		if (PlatinumTokens.itemOnNpc(player, npc, itemId, slot))
			return;
		if (HerbloreDecantCleanUnfNpc.useItemOnNpc(player, npc, itemId))
			return;
		if (npcId == 1603) {
			MageArenaII.handleItemOnNPC(player, npc, itemId);
			return;
		}

		if (npcId == BryophytaNPC.GROWTHLING) {
			if (npc.isDead())
				return;
			if (npc.getHealth().getCurrentHealth() == 1 && itemId == 1351) {
				npc.startAnimation(new Animation(3262));
				npc.setDead(true);
			}
			return;
		}

		if (npcId == 954) {
			if (itemId >= 4215 && itemId <= 4223) {
				Degrade.repairCrystalBow(player, itemId);
			} else if (itemId == 4207) {
				player.getDH().sendDialogues(66, 954);
			}
			return;
		}
		if (npcId == 1504) {//lamp on hunter for hunter xp
			if (itemId == 2528) {
				if (player.getItems().playerHasItem(2528, 1)) {
					if (!player.getMode().isOsrs() && !player.getMode().is5x()) {
						player.getPA().addSkillXP(125000, 21, true);

					} else {
						player.getPA().addSkillXP(12500, 21, true);
						player.sendMessage("As a restricted game mode you receive less xp.");
					}

					player.getItems().deleteItem(2528, 1);
					player.sendMessage("The lamp mysteriously vanishes...");

					player.getPA().closeAllWindows();
				}
			}

			return;
		}
		switch (npcId) {
			case 8583:
				switch (itemId) {
					case 23783://lowers Hespori def
						Hespori.useToxicGem(player);
						break;
					case 9699://damages Hespori
						player.sendMessage("Directly attack Hespori to use your runes!");
						break;
				}
				break;
			case 8208:
				PetCollector.exchangePetForGp(player, itemId);
				break;
	case 905:
		PlayerAssistant.decantResource(player, itemId);
		break;
		case 5906:
			switch (itemId) {
			case 11144:
				player.getItems().deleteItem(11144, 1);
				player.getItems().addItem(12002, 1);
				break;
			}
			break;
		
		case 7303:
			MasterClue.exchangeClue(player);
			break;
			
		/*case 7439: //Plain rock golem
			PetHandler.recolor(player, player.npcType, itemId);
			break;*/
		case 3894:
				Packs.openSuperSet(player,13066);
			break;

		case 5730:
			PlayerAssistant.decantHerbs(player, itemId);
			break;
		case 1159:
			if ( itemId != 527 && itemId != 533 && itemId != 535 && itemId != 537 &&
					itemId != 6730 && itemId != 531 && itemId != 2860 && itemId != 3180 &&
					itemId != 3153 && itemId != 6813 && itemId != 4833 && itemId != 11944 &&
					itemId != 22125 && itemId != 22787 ) {
				player.sendMessage("You cannot un-note this item here.");
				return;
			}
			final int boneAmount = player.getItems().getItemAmount(itemId);

			unNoteItems(player, itemId, boneAmount);
			break;
		case 814:
		case 2914:
			switch (itemId) {
			case 1755:
				Packs.openSuperSet(player,13066);
				break;
			case 11824:
				player.getDH().sendDialogues(11824, -1);
				break;
				
			case 11889:
				player.getDH().sendDialogues(11889, -1);
				break;
			}
			break;

		default:
			if (player.debugMessage)
				player.sendMessage("Player used Item id: " + itemId + " with Npc id: " + npcId + " With Slot : " + slot);
			break;
		}

	}

}
