package io.xeros.model.entity.player;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import io.xeros.Configuration;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.items.Degrade;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.minigames.bounty_hunter.BountyHunterEmblem;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.slayer.*;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;

public class DialogueHandler {

	private final Player c;

	public DialogueHandler(Player client) {
		this.c = client;
	}

	public String tree = "";

	private static final int[] UNSIRED_REWARDS = { 7979, 13265, 4151, 13274, 13275, 13276, 13277 };

	public void sendDialogues(int dialogue, int npcId) {
		c.talkingNpc = npcId;
		c.lastDialogueNewSystem = false;
		if (dialogue == 0 && npcId == -1 && c.getCurrentCombination().isPresent()) {
			c.getCurrentCombination().get().sendCombineConfirmation(c);
			return;
		}
		switch (dialogue) {
			case -500:
				sendOption2("Enter Instance", "Close");
				c.dialogueAction = -500;
				break;
			case 2000:
				sendOption2("Fight The Mimic", "Come back later.");
				c.dialogueAction = 2001;
				break;
			case 2004:
				sendOption2("Yes, Let's go.", "No thanks.");
				c.dialogueAction = 2002;
				break;
		case LootingBag.ITEM_ON_ITEM_DIALOGUE_ID:
			sendOption5("Deposit 1", "Deposit 5", "Deposit 10", "Deposit X", "Deposit All"); //TODO
			c.dialogueAction = LootingBag.ITEM_ON_ITEM_DIALOGUE_ID;
			break;
		case LootingBag.OPTIONS_DIALOGUE_ID:
			sendOption5("Always ask", "Always 1", "Always 10", "Always All", "Always X");
			c.dialogueAction = LootingBag.OPTIONS_DIALOGUE_ID;
			break;
		case 792:
			sendNpcChat ("Are you brave enough to enter?");
			c.nextChat = 793;
			break;
		case 793:
			sendOption2("Yes, I am ready.", "No, I'm scared.");
			c.dialogueAction = 793;
			break;
			case 130135:
				c.currentExchangeItemAmount = c.getItems().getItemAmount(c.currentExchangeItem);
				if (c.getItems().playerHasItem(c.currentExchangeItem)) {
					if (c.currentExchangeItemAmount > 1 && c.currentExchangeItem != 22316) {
							sendOption3("Yes, destroy @red@all " + c.currentExchangeItemAmount + "@bla@ of my " + ItemDef.forId(c.currentExchangeItem).getName() + ".", "Yes, destroy 1 of my " + ItemDef.forId(c.currentExchangeItem).getName() + ".", "No, keep my item safe.");
					} else {
							sendOption2("Yes, destroy my " + ItemDef.forId(c.currentExchangeItem).getName() + ".", "No, keep my item safe.");
					}
					c.dialogueAction = 130135;
				}

				break;
		case 380:
			c.talkingNpc = 8761;
			sendNpcChat ("This cave leads to the Crystalline Hunllef boss.", "@red@[WARNING] You cannot teleport away!", "You will lose your key upon entering.");
			c.nextChat = 369;
			break;
		case 369:
			sendNpcChat ("You must be prepared to fight to the death.", "There is @red@no escape!");
			c.nextChat = 381;
			break;
		case 381:
			sendOption2("Yes, I am ready to fight. @red@[No Escape]", "No, I'm scared.");
			c.dialogueAction = 381;
			break;
		case 265:
			sendOption3("Add pages", "Remove Pages", "None");
			c.dialogueAction = 265;
			break;
		case 362:
			sendOption2("Yes", "No");
			c.dialogueAction = 362;
			break;
		case 345:
			sendNpcChat ("Are you sure you want to use this scroll?");
			c.nextChat = 346;
			break;
		case 346:
			sendOption2("Yes", "No");
			c.dialogueAction = 346;
			break;
		case 347:
			sendNpcChat ("Are you sure you want to use this scroll?");
			c.nextChat = 348;
			break;
		case 348:
			sendOption2("Yes", "No");
			c.dialogueAction = 348;
			break;
		case 750:
			sendPlayerChat1("I need an assignment.");
			if (c.getSlayer().getTask().isPresent()) {
				c.getDH().sendDialogues(3305, 8623);
			} else {
				c.getSlayer().createNewTask(8623, false);
			}
			break;
		case 179:
			sendPlayerChat1("I need an assignment.");
			if (c.getSlayer().getTask().isPresent()) {
				c.getDH().sendDialogues(3305, 6797);
			} else {
				c.nextChat = 180;
			}
		break;
		case 780:
			sendNpcChat ("My attractor cost 1 million coins to purchase.");
			c.nextChat = -1;
			break;
		case 781:
			sendNpcChat ("Thank you for purchasing my attractor.");
			c.nextChat = -1;
			break;
		case 782:
			sendNpcChat ("Would you like to purchase an ava's attractor?");
			c.nextChat = 783;
			break;
		case 783:
			sendOption2("Yes", "No");
			c.dialogueAction = 783;
			break;
		case 983:
			sendOption2("Your Info", "Outlast Info");
			c.dialogueAction = 983;
			break;
		case 459:
			sendOption2("Jatizsot", "Neitiznot");
			c.dialogueAction = 459;
			break;
		case 765:
			sendOption5("Showcase 1", "Showcase 2", "", "", "");
			c.dialogueAction = 765;
			break;
		case 858:
			sendNpcChat("Are you sure you want to destroy this item?");
			c.nextChat = 859;
			break;
		case 333:
			sendNpcChat("Are you sure you want to dismantle this item.");
			c.nextChat = 334;
			break;
		case 334:
			sendNpcChat("Avernic Defender hilt will be lost in the process.");
			c.nextChat = 335;
			break;
		case 335:
			sendOption2("Yes, I don't mind losing my avernic hilt.", "No");
			c.dialogueAction = 335;
			break;
		case 859:
			sendOption2("Yes", "No");
			c.dialogueAction = 859;
			break;
		case 577:
			sendNpcChat ("Would you like to claim your vote reward?");
			c.nextChat = 977;
			break;
		case 977:
			sendOption2("Yes", "No");
			c.dialogueAction = 977;
			break;
		case 943:
			sendOption2("Nieve", "Krystilia");
			c.dialogueAction = 943;
			break;
		case 688:
			sendNpcChat ("Are you sure you want to redeem this scroll?");
			c.nextChat = 689;
			break;
		case 689:
			sendOption2("Yes", "No");
			c.dialogueAction = 689;
			break;
		case 999:
			sendOption5("Walk Chaos", "Stable", "Frimb", "Fpk Merk", "More Options");
			c.dialogueAction = 999;
			break;
		case 1000:
			sendOption5("Vexia", "I Pk Max Jr", "Chopper Rsps", "Didyscape", "More Options");
			c.dialogueAction = 1000;
			break;
		case 1001:
			sendOption5("Fewb", "Artz", "Vihtic", "Less Options", "More Options");
			c.dialogueAction = 1001;
			break;
		case 1002:
			sendOption5("Sprad", "Less Options", "Exit", "", "");
			c.dialogueAction = 1002;
			break;
		case 180:
			sendNpcChat1("What kind of task would you like?", c.talkingNpc, "Nieve");
			c.nextChat = 181;
		break;
		case 181:
			sendOptions("I want an @blu@easy @bla@task", "I want a @blu@medium @bla@task",
						"I want a @blu@hard @bla@task", "I want a @blu@boss @bla@task");
			c.dialogueAction = 181;
		break;
		case 182: 
			sendPlayerChat1("I want an easy task");
			SlayerMaster.get(401);
			c.nextChat = 186;
		break;
		case 183:
			sendPlayerChat1("I want a medium task");
			SlayerMaster.get(402);
			c.nextChat = 187;
		break;
		case 184:
			sendPlayerChat1("I want a hard task");
			SlayerMaster.get(405);
			c.nextChat = 188;
		break;
		case 185:
			sendPlayerChat1("I want a boss task");
			SlayerMaster.get(6797);
			c.nextChat = 189;
		break;
		case 186://Easy
			c.getSlayer().createNewTask(Slayer.EASY_TASK_NPC_ID, false);
		break;
		case 187://medium
			c.getSlayer().createNewTask(Slayer.MEDIUM_TASK_NPC_ID, false);
		break;
		case 188://Hard
			c.getSlayer().createNewTask(Slayer.HARD_TASK_NPC_ID, false);
		break;
		case 189://Boss
			c.getSlayer().createNewTask(Slayer.BOSS_TASK_NPC_ID, false);
		break;
		case 190:
			sendNpcChat2("You're currently assigned to kill "+c.getSlayer().getTask().get().getPrimaryName()+"; Only",
						""+c.getSlayer().getTaskAmount()+" more to go.", c.talkingNpc, "Nieve");
			c.nextChat = -1;
		break;
		case 191://medium
			sendNpcChat2("You need a combat level of", "at least 50 to get a medium task.", c.talkingNpc, "Nieve");
			c.nextChat = -1;
			break;
		case 192://hard
			sendNpcChat2("You need a combat level of", "at least 80 to get a hard task.", c.talkingNpc, "Nieve");
			c.nextChat = -1;
			break;
		case 193://boss
			sendNpcChat2("You need a combat level of", "at least 100 to get a boss task.", c.talkingNpc, "Nieve");
			c.nextChat = -1;
			break;
		case 300:
			sendNpcChat ("To use the Trading Post click on our booth!");
			c.nextChat = -1;
			break;

		/*
		 * Raids historian
		 */
		case 850:
			sendNpcChat("How can I assist you?");
			c.nextChat = 851;
			break;

		case 851:
			sendOption5("Teleport me to the raids dungeon", "Can I see the raids shop?", "What is this place?",
					"Is this safe?", "I don't need any help");
			c.dialogueAction = 851;
			break;
		// What is this?
		case 852:
			sendNpcChat("I am here to show and bring you to the deep dungeons",
					"called raids, whereas you can put your strength to the test", "and combat mighty foes for rewards",
					"you've always been looking for!");
			c.nextChat = 851;
			break;
		// Is this safe?
		case 853:
			sendNpcChat("@red@NO!", "Raids dungeons are not safe, be aware and join only",
					"if you are willing to risk what you bring!");
			c.nextChat = 851;
			break;
		// Teleport me to
		case 854:
			sendNpcChat("Which foe would you like to go to?");
			c.nextChat = 855;
			break;
		// Who are you?
		case 856:
			sendNpcChat("I am Olmlet, fear me.");
			c.nextChat = 851;
			break;
		case 855:
			sendOption5("Tekton", "Skeletal Mystics", "", "", "");
			c.dialogueAction = 855;
			break;
		case 4487:
			sendOption5("Godwars(@gre@FREE)", "Bandos Entrance(@red@150k)", "Saradomin Entrance(@red@150k)", "Zamorak Entrance(@red@150k)", "Armadyl Entrance(@red@150k)");
			c.dialogueAction = 4487;
			break;
		/*
		 * Doomsayer
		 */
		case 800:
			sendNpcChat("How can I assist you?");
			c.nextChat = 801;
			break;
		case 801:
			sendOption5("What is this?", "I would like to "
					+ (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe() ? "go back to reality." : "go to pk district.") + "",
					"How do I transfer my blood money?", "I would like to view my safe-box", "Nevermind..");
			c.dialogueAction = 801;
			break;
		// What is this?
		case 802:
			sendNpcChat("I am in charge of the PK-District, whereas I invite you",
					"to challenge players individually or in team", "you are able to earn blood money while",
					"being within the pk-district.");
			c.nextChat = 803;
			break;
		case 803:
			sendNpcChat("You are then able to use your earned blood money", "in my shop for various items.",
					"While entering the pk-district, your stats will be set",
					"to 99. Don't freak out, it will be back to normal if you leave.");
			c.nextChat = 804;
			break;
		case 804:
			sendNpcChat("You can not bring any items into the district",
					"And to transfer back your blood money to reality, simply", "deposit them into your safe-box!");
			c.nextChat = 801;
			break;
		// Deposit/withdraw
		case 806:
			sendNpcChat("To transfer your blood money from district to reality",
					"or vice versa, Simply deposit your blood money into",
					"your safe-box and withdraw them where you want them.");
			c.nextChat = 801;
			break;
		case 812:
			sendNpcChat("You cannot be wearing or holding any items whatsoever.");
			c.nextChat = -1;
			break;
		case 813:
			sendOption2("Yes I am aware, and I want to leave", "No, let me store my valuables in my safe-box");
			c.dialogueAction = 813;
			break;
		// Safe box
		case 818:
			sendNpcChat("Using quick-sets will replace your inventory and worn items", "You have been warned!!",
					"@red@Deposit your valuable items/blood money.");
			c.nextChat = 819;
			break;
		case 819:
			sendOption2("Main Sets @blu@40+ Defence", "Pure Sets @blu@1 Defence");
			c.dialogueAction = 819;
			break;
		case 820:
			sendOption5("Melee Set", "Ranged Tank Set", "Magic Set", "", "Next");
			c.dialogueAction = 820;
			break;
		case 821:
			sendOption5("Melee Set", "Ranged Set", "Magic Set", "", "Next");
			c.dialogueAction = 821;
			break;

		case 193193:
			sendOption5("Tunnel 1", "Tunnel 2", "Tunnel 3", "Tunnel 4", "Tunnel 5 @red@Danger");
			c.dialogueAction = 193193;
			break;

		case 784:
			sendOption2("Donate Item To The Well", "Check My Item Price For The Well");
			c.dialogueAction = 784;
			break;

		case 785:
			sendPlayerChat2("Hmm.. It says here my item donations",
					"will only count towards @red@Double XP@bla@. &$#@.");
			c.nextChat = 786;
			break;

		case 786:
			sendOption2("Yes, donate my item to the well.", "No thanks, I'll keep it.");
			c.dialogueAction = 786;
			break;

		/*
		 * Death/killer | Halloween order minigame
		 */
		case 720:
			sendPlayerChat1("Hello..? Where am I?");
			c.nextChat = 721;
			break;
		case 721:
			sendNpcChat("Why hello there " + c.getDisplayName() + ", welcome to your worst nightmare!",
					"I am here to offer you some pieces of clothing", "In exchange for a piece of your brain!");
			c.nextChat = 722;
			break;
		case 722:
			sendNpcChat("Jokes aside (MWUHAHAH)", "I have gathered some items from slain players and placed",
					"them on the ground here.", "I want you to guess, in which order I placed them down!");
			c.nextChat = 723;
			break;
		case 723:
			sendNpcChat("You must select the order by clicking on each item",
					"When you have done so, come back to me and I will correct it",
					"for you, if you get them all right", "There will be a reward waiting for you!");
			c.nextChat = 724;
			break;
		
		case 725:
			sendNpcChat("Hey! Leave my items be without my permission!");
			c.nextChat = -1;
			break;
		case 726:
			sendNpcChat("You have already chosen that once... MOVE ON!");
			c.nextChat = -1;
			break;
		case 727:
			sendNpcChat("You got them all right, I am NOT impressed..", "But here, take this..");
			c.nextChat = -1;
			break;
		case 728:
			sendNpcChat("HAHAHAH, you did NOT get them all right!",
					"Check your chatbox for a list of your chosen order", "Your order will remain the same until",
					"you actually manage to get them all right!");
			c.nextChat = -1;
			break;
		case 729:
			sendNpcChat("You have not chosen everything on the ground!", "Do NOT come back before you have!");
			c.nextChat = -1;
			break;
		case 730:
			sendNpcChat("You have chosen all the items, now come let me correct it!");
			c.nextChat = -1;
			break;

		case 702:
			sendNpcChat("Hello, do you want me to create your dragonfire shield?");
			c.nextChat = 703;
			break;
		case 703:
			sendOption2("Yes, please", "No thank you..");
			c.dialogueAction = 703;
			break;

		case 70300:
			sendOption2("Teleport to Catacombs of Kourend", "Nevermind");
			c.dialogueAction = 70300;
			break;

		case 704:
			sendNpcChat("Alright, that will be 500K coins and 5 vote points thank you.",
					"You must have the visage and a antifire shield as well", "of course.");
			c.nextChat = 705;
			break;

		case 705:
			sendOption2("Okey, here you go", "I don't have that kind of wealth!");
			c.dialogueAction = 705;
			break;

		case 706:
			if (c.getItems().playerHasItem(11286) && c.getItems().playerHasItem(1540)
					&& c.getItems().playerHasItem(995, 5_000_000)) {
				c.getItems().deleteItem(11286, 1);
				c.getItems().deleteItem(1540, 1);
				c.getItems().deleteItem(995, 500_000);
				c.getItems().addItem(11283, 1);
				c.votePoints -= 5;
				c.getQuestTab().updateInformationTab();
				sendItemStatement("Oziach successfully bound your dragonfire shield.", 11283);
			} else {
				sendNpcChat("Come back with a shield, visage and 5M Gold!");
			}
			c.nextChat = -1;
			break;

		case 700:
			sendItemStatement("You place the Unsired into the Font of Consumption...", 13273);
			c.nextChat = 701;
			break;
		case 701:
			int randomReward = Misc.random(UNSIRED_REWARDS.length - 1);
			int rights = c.getRights().getPrimary().getValue() - 1;
			int reward = UNSIRED_REWARDS[randomReward];
			if (c.getItems().playerHasItem(13273)) {
				/*
				 * Random chance on pet
				 */
				if (Misc.random(100) == 15) {
					if (c.getItems().getItemCount(13262, false) > 0 || c.petSummonId == 13262) {
						return;
					}
					c.getItems().addItemUnderAnyCircumstance(13262, 1);
					PlayerHandler.executeGlobalMessage("[@red@PET@bla@] @cr20@<col=255> <img=" + rights + ">"
							+ c.getDisplayName() + "</col> received a pet from <col=255>Abyssal Sire.</col>.");
					c.getCollectionLog().handleDrop(c, 5, 13262, 1);
				}
				switch (reward) {
				case 13274:
					if (c.getItems().getItemCount(13274, false) > 0) {
						if (c.getItems().getItemCount(13275, false) < 1) {
							reward = 13275;
						} else if (c.getItems().getItemCount(13276, false) < 1) {
							reward = 13276;
						}
					}
					break;
				case 13275:
					if (c.getItems().getItemCount(13275, false) > 0) {
						if (c.getItems().getItemCount(13276, false) < 1) {
							reward = 13276;
						} else if (c.getItems().getItemCount(13274, false) < 1) {
							reward = 13274;
						}
					}
					break;
				case 13276:
					if (c.getItems().getItemCount(13276, false) > 0) {
						if (c.getItems().getItemCount(13274, false) < 1) {
							reward = 13274;
						} else if (c.getItems().getItemCount(13275, false) < 1) {
							reward = 13275;
						}
					}
					break;
				}
				c.startAnimation(827);
				c.getPA().createPlayersStillGfx(1294, 3039, 4774, 0, 0);
				c.getItems().deleteItem(13273, 1);
				c.getItems().addItem(reward, 1);
				PlayerHandler.executeGlobalMessage("@red@" + c.getDisplayName() + " received a drop: "
						+ ItemAssistant.getItemName(reward) + "@bla@.");
				// PlayerHandler.executeGlobalMessage("@red@[Lootations]@cr19@
				// <col=255><img="+rights+">" + Misc.formatPlayerName(c.playerName) + "</col>
				// received @blu@1@bla@x @blu@" + Item.getItemName(reward) + ".");
				sendItemStatement("The Font consumed the Unsired and returns you a \\n reward.",
						UNSIRED_REWARDS[randomReward]);
			}
			c.nextChat = -1;
			break;

		case 64:
			sendOption2("Buy a kitten for 10,000,000gp", "Do nothing..");
			c.dialogueAction = 64;
			break;

		case 65:
			switch (tree) {
			case "village":
				sendOption2("Tree Gnome Stronghold", "Grand Exchange");
				break;

			case "stronghold":
				sendOption2("Tree Gnome Village", "Grand Exchange");
				break;

			case "grand_exchange":
				sendOption2("Tree Gnome Village", "Tree Gnome Stronghold");
				break;
			}
			c.dialogueAction = 65;
			break;

		case 66:
			sendOption2("Create Crystal Bow - Crystal", "Create Crystal Halberd - Crystal + 10M Coins");
			c.dialogueAction = 66;
			break;
		case 832:
			sendNpcChat("Come back when you have a crystal seed to give me.");
			c.dialogueAction = 832;
			break;
		case 67:
			sendNpcChat("Nice banner you got there, " + c.getDisplayName() + "!",
					"Since you do have one I will offer to take your crystal", "And a price of 10M coins to create",
					"A Crystal Halberd, just for you!");
			c.nextChat = 66;
			break;

		case 55:
			sendOption5("Purple", "Blue", "Gold", "Red", "Next"); // Green
			c.dialogueAction = 55;
			break;

		case 61:
			sendOption5("Green", "White", "Dark Blue", "", "Previous"); // Green
			c.dialogueAction = 56;
			break;

		case 68:
			sendOption2("View drop-rates in 1/kills form", "View drop-rates in percent form");
			c.dialogueAction = 68;
			break;

		case 105:
			sendPlayerChat1("Hello.");
			c.nextChat = 106;
			break;

		case 106:
			sendNpcChat("Who goes there?");
			c.nextChat = 107;
			break;

		case 107:
			sendNpcChat("This is no place for a human. You need to leave.");
			c.nextChat = -1;
			break;

		case 110:
			c.nextChat = -1;

			sendNpcChat(Boundary.getPlayersInBoundary(Boundary.CERBERUS_ROOM_WEST) + " "
					+ (Boundary.getPlayersInBoundary(Boundary.CERBERUS_ROOM_WEST) == 1 ? "adventurer is" : "adventurers are")
					+ " inside the cave.");
			// if (c.absX < 1296) { //West
			// sendNpcChat(Boundary.entitiesInArea(Boundary.BOSS_ROOM_WEST) +" " +
			// (Boundary.entitiesInArea(Boundary.BOSS_ROOM_WEST) == 1 ? "adventurer is" :
			// "adventurers are") + " inside the cave.");
			// } else if (c.absX > 1325) { //East
			// sendNpcChat(Boundary.entitiesInArea(Boundary.BOSS_ROOM_EAST) +" " +
			// (Boundary.entitiesInArea(Boundary.BOSS_ROOM_EAST) == 1 ? "adventurer is" :
			// "adventurers are") + " inside the cave.");
			// } else if (c.absX > 1265) { //North
			// sendNpcChat(Boundary.entitiesInArea(Boundary.BOSS_ROOM_NORTH) +" " +
			// (Boundary.entitiesInArea(Boundary.BOSS_ROOM_NORTH) == 1 ? "adventurer is" :
			// "adventurers are") + " inside the cave.");
			// }
			break;

		/*
		 * Firecape attempts
		 */
		case 70:
			sendPlayerChat1("I have a firecape here.");
			c.nextChat = 71;
			break;

		case 71:
			sendOption3("Yes, sell it for 8,000 TokKul.", "No, keep it.", "Bargain for TzRek-Jad");
			c.dialogueAction = 71;
			break;

		case 72:
			sendOption2("Attempt Pet?", "Yes, I know I won't get my cape back..", "No, I like my cape!.");
			c.dialogueAction = 72;
			break;

		case 73:
			sendNpcChat("You not lucky. Maybe next time, JalYt.");
			c.nextChat = -1;
			break;

		case 74:
			sendNpcChat("You lucky. Better train him good else TzTok-Jad find.", "you, JalYt.");
			c.nextChat = -1;
			break;

		/*
		 * Remove bigger boss tasks
		 */
		case 75:
			sendOption2("Yes, i'd like to remove extended boss tasks.", "Nevermind..");
			c.dialogueAction = 75;
			break;

		/*
		 * Max cape operate options
		 */
		case 76:
			sendOption5("Switch Spellbook.", "Teleports", "Toggles", "", "Nevermind..");
			c.dialogueAction = 76;
			break;
		case 77:
			sendOption5("Toggle ROL Effect", "Toggle Fishing Effect", "Toggle Mining Effect",
					"Toggle Woodcutting Effect", "Nevermind..");
			c.dialogueAction = 77;
			break;
		case 78:
			sendOption5("Crafting Guild", "Slayer Task", "Puro Puro", "", "Nevermind..");
			c.dialogueAction = 78;
			break;

		case 11930:
			sendNpcChat2("The current exchange rates are:", "@red@OSRS Gold: @blu@1m = $1.00", 3189,
					"GP Donation Manager");
			c.nextChat = -1;
			break;
		/*
		 * RFD
		 */
		case 62:
			sendNpcChat3(c.getDisplayName() + " I must warn you, inside that dimension you will",
					"have no assistance from the gods, and your prayers will", "not work at all!", 4847, "Gypsy");
			c.nextChat = 59;
			break;

		case 58:
			sendNpcChat3(c.getDisplayName() + " I must warn you, inside that dimension you will",
					"have no assistance from the gods, and your prayers will", "not work at all!", 4847, "Gypsy");
			c.nextChat = 59;
			break;

		case 59:
			sendNpcChat4("Also be aware that he will use his powers to draw upon",
					"the might of foes you have not fought before, and",
					"that dimensional instability means that your lost items",
					"will be irretrievable should you die...", 4847, "Gypsy");
			c.nextChat = 60;
			break;

		case 60:
			sendNpcChat2("Take care, I suspect the culinaromancer will not give", "up so easily!", 4847, "Gypsy");
			c.nextChat = -1;
			c.rfdChat = 1;
			break;

		case 1:
			sendNpcChat2("Come back when you have finished all your achievements.", "", 6071, "Achievement Handler");
			c.nextChat = -1;
			break;
		case 2:
			sendNpcChat2("This cape costs 99.000gp.", "", 6071, "Achievement Handler");
			c.nextChat = -1;
			break;
		case 4:
			sendItemStatement("Twiggy hands you the achievement cape and hood.", 13069);
			c.nextChat = 7;
			break;
		case 7:
			sendNpcChat2("There you go Ethopian, good job.", "", 6071, "Achievement Handler");
			c.nextChat = -1;
			break;

		case 52: // Trident
			sendOption3("Add 10 Charges", "Add 100 Charges", "Add 1000 Charges");
			c.dialogueAction = 55;
			break;
		case 53: // Trident
			sendOption3("Add 10 Charges", "Add 100 Charges", "Add 1000 Charges");
			c.dialogueAction = 56;
			break;

		case 40:
			sendOption2("Spin", "Bow String", "Crossbow String");
			c.dialogueAction = 40;
			break;

		case 10:
			sendOption2("Bones to Bananas", "Bones To Peaches");
			c.dialogueAction = 10;
			break;

		case 63:
			sendOption5("Resource Area @bla@(@red@Wilderness@bla@) 60K Coins.", "KBD Lair @bla@25K Coins.",
					"Godwars Dungeon + 10 KC @bla@80K Coins.", "Corporeal Beast Lair @bla@100K Coins", "");
			c.dialogueAction = 61;
			break;

		// case 64:
		// sendOption5("Resource Area @bla@(@red@Wilderness@bla@) 60K Coins.", "KBD Lair
		// @bla@25K Coins.", "Godwars Dungeon + 10 KC @bla@80K Coins.", "", "Previous
		// Page");
		// c.dialogueAction = 61;
		// break;

		case 80:
			sendOption2("Upgrade to Elite for 200 points per piece.", "Cancel");
			c.dialogueAction = 80;
			break;

		case 1427:
			sendNpcChat4("I am Ak-Haranu and I'm in charge of Prestiging.",
					"Prestiging lets players set the skill back to", "level one in order to gain prestige points.",
					"You can then buy perks with the points.", 2989, "Ak-Haranu");
			c.nextChat = 1428;
			break;
		case 1428:// Prestige
			sendOption3("Open Prestige Manager", "Open the Prestige Store", "Cancel");
			c.dialogueAction = 1428;
			break;
		case 2001:
			sendOption4("Pest Control", "Duel Arena", "Fight Caves", "@blu@Next");
			c.teleAction = 200;
			break;
		case 2002:
			sendOption5("Barrows", "Warriors Guild", "Mage Arena @red@(52 Wilderness)", "Lighthouse", "@blu@Next");
			c.dialogueAction = 2002;
			c.nextChat = -1;
			break;
		case 2003:
			sendOption5("Recipe For Disaster", "", "", "", "@blu@Back");
			c.dialogueAction = 2003;
			c.nextChat = -1;
			break;
		case 947:
			sendOption2("Supply Shop", "Weapon Shop");
			c.dialogueAction = 947;
			c.nextChat = -1;
			break;
		case 10955:
			sendNpcChat("I can get you a Crystalline task but",
					"it will cost you 1m.");
			c.nextChat = 10956;
			break;
		case 10956:
			sendOption2("I would like a Crystalline task for 1m please.","Nevermind");
			c.dialogueAction = 10956;
			break;
		case 3299:
			sendNpcChat("Before I assign you anything, I want to make",
					"something clear. My tasks have to be done in the",
					"Wilderness. Only kills inside the Wilderness will count.");
			c.nextChat = 33900;
			break;
		case 33900:
			sendOption2("I would like a wilderness task.","Nevermind");
			c.dialogueAction = 33900;
			break;
		case 3300:
			if (c.getSlayer().getTask().isPresent()) {
				if (c.getSlayer().getTask().get().getLevel() > c.playerLevel[Skill.SLAYER.getId()]) {
					sendNpcChat("You have been assigned a task you cannot complete.",
							"What an inconvenience, i'll get to the bottom of this.",
							"In the meanwhile, i've reset your task.",
							"Talk to me or one of the others to get a new task.");
					c.getSlayer().setTask(Optional.empty());
					c.getSlayer().setTaskAmount(0);
					c.nextChat = -1;
					return;
				}
			}
			sendNpcChat("What do you want?");
			c.nextChat = 3301;
			break;
		case 11155:
			sendNpcChat("I can get you a Hydra task but",
					"it will cost you 15m.");
			c.nextChat = 11156;
			break;//hydra npc
		case 11156:
			sendOption2("I would like a Hydra task for 15m please.","Nevermind");
			c.dialogueAction = 11156;
			break;//hydra npc
		case 11955:
			sendNpcChat("I can get you a kraken task but",
					"it will cost you 5m.");
			c.nextChat = 11966;
			break;
		case 11966:
			sendOption2("I would like a Kraken task for 5m please.","Nevermind");
			c.dialogueAction = 11966;
			break;
		case 149:
			sendOption2("Claim rewards", "Open shop");
			c.dialogueAction = 149;
			c.nextChat = -1;
			break;
		case 3301:
			sendOption4("I'd like to see the slayer interface.", "I am in need of a slayer assignment.",
					"Could you tell me where I can find my current task?", "Cancel my task for 1.5m gp.");
			c.dialogueAction = 3301;
			break;
		case 3325:
			c.getDH().sendOption5("KBD Entrance @red@(DEEP WILD)", "Chaos Elemental @red@(DEEP WILD)", "Godwars",
					"Barrelchest @red@(Lvl 20+ Wild)", "@blu@Next");
			c.teleAction = 3;
			c.dialogueAction = -1;
			break;
		case 3324:
			sendOption5("Slayer Tower", "Edgeville Dungeon", "Taverly Dungeon", "Brimhaven Dungeon", "@blu@Next"); // Rellekka
																													// Slayer
																													// Dungeon
																													// //
			c.teleAction = 2;
			c.dialogueAction = -1;
			break;
		case 3333:
			sendOption5("Rellekka Slayer Dungeon", "Stronghold Slayer Cave", "Mithril Dragons", "Lletya", "@blu@Close");
			c.teleAction = 17;
			c.dialogueAction = -1;
			break;
		case 3302:
			sendPlayerChat1("I'd like to see the slayer interface.");
			c.nextChat = 3303;
			break;

		case 3303:
			SlayerRewardsInterface.open(c, SlayerRewardsInterfaceData.Tab.TASK);
			c.nextChat = 0;
			break;

		case 3304:
			sendPlayerChat1("I need an assignment.");
			c.nextChat = 3305;
			break;

		case 3305:
			Optional<Task> task = c.getSlayer().getTask();
			Optional<SlayerMaster> master = SlayerMaster.get(c.talkingNpc);
			Optional<SlayerMaster> myMaster = SlayerMaster.get(c.getSlayer().getMaster());

			if (task.isPresent() && master.isPresent() && myMaster.isPresent()) {
				if (c.getSlayer().getMaster() != master.get().getId()) {
					if (myMaster.get().getLevel() < master.get().getLevel()) {
						sendNpcChat("You have an easier task from a different master.",
								"If you cannot complete their task, you cannot start",
								"one of mine. You must finish theirs first.");
						c.nextChat = -1;
						return;
					}
					sendNpcChat("I can give you an easier task but this will reset your",
							"consecutive tasks completed if you have an active task.",
							"Are you sure this is what you want to do?");
					c.nextChat = 3308;
				} else {
					if (c.talkingNpc == 401) {
						sendNpcChat(
								"You currently have " + c.getSlayer().getTaskAmount() + " "
										+ c.getSlayer().getTask().get().getPrimaryName() + " to kill.",
								"You cannot get an easier task. You must finish this.");
						c.nextChat = -1;
					} else if (c.talkingNpc == 402) {
						sendNpcChat(
								"You currently have " + c.getSlayer().getTaskAmount() + " "
										+ c.getSlayer().getTask().get().getPrimaryName() + " to kill.",
								"Talk to Turael for an easier task. If you",
								"obtain an easier task your consecutive tasks will be reset.");
						c.nextChat = -1;
					} else if (c.talkingNpc == 8761) {
						sendNpcChat(
								"You currently have " + c.getSlayer().getTaskAmount() + " "
										+ c.getSlayer().getTask().get().getPrimaryName() + " to kill.",
								"Talk to Nieve for an easier task. If you",
								"obtain an easier task your consecutive tasks will be reset.");
						c.nextChat = -1;
					} else if (c.talkingNpc == 405) {
						sendNpcChat(
								"You currently have " + c.getSlayer().getTaskAmount() + " "
										+ c.getSlayer().getTask().get().getPrimaryName() + " to kill.",
								"Talk to Turael for an easier task. If you",
								"obtain an easier task your consecutive tasks will be reset.");
						c.nextChat = -1;
					} else if (c.talkingNpc == 6797) {
						sendNpcChat(
								"You currently have " + c.getSlayer().getTaskAmount() + " "
										+ c.getSlayer().getTask().get().getPrimaryName() + " to kill.",
								"Would you like an easier task? This will",
								"reset your consecutive task streak.");
						c.nextChat = 3306;
					} else if (c.talkingNpc == 7663) {

					}else if (c.talkingNpc == 8623) {
						sendNpcChat(
								"You currently have " + c.getSlayer().getTaskAmount() + " "
										+ c.getSlayer().getTask().get().getPrimaryName() + " to kill.",
								"Would you like an easier task? This will",
								"reset your consecutive task streak.");
						c.nextChat = 3306;
					}
				}
			} else {
				c.getSlayer().createNewTask(c.talkingNpc, false);
			}
			break;
		case 3306:
			sendOptions("Yes, I want an easier task.", "Nevermind");
			c.dialogueAction = 3306;
		break;

		// Start Zeah Guards and Throw-aways
		case 55864:
			sendNpcChat2("Welcome to the Shayzien House!", "Home of the best soldiers in the entire WORLD!",
					c.talkingNpc, "Shayzien Guard");
			c.nextChat = 0;
			break;
		case 55865:
			sendNpcChat2("You don't seem like a warrior to me...", "You'd be better off at the Hosidius House, HAHA!",
					c.talkingNpc, "Shayzien Guard");
			c.nextChat = 0;
			break;
		case 55866:
			sendNpcChat2("I hear the Piscarilius House has some", "interesting fish to be caught.", c.talkingNpc,
					"Shayzien Guard");
			c.nextChat = 0;
			break;

		case 5998:
			sendNpcChat3("Only <img=4>@red@Donators @bla@can access the Donator Zone.",
					"Interested in becoming an  <img=4>@red@Donator@bla@?",
					"Go to the bottom of your quest tab and click the <img=19>@yel@Store.", c.talkingNpc,
					"<img=4>Donation Informative<img=4>");
			c.nextChat = 0;
			break;

		case 5999:
			sendNpcChat3("Welcome to the <img=4>@red@Donator Zone.", "If you want to get inside,",
					"All you need to do is type ::dz .", c.talkingNpc, "<img=4>Donation Informative<img=4>");
			c.nextChat = 0;
			break;

		case 55867:
			sendNpcChat2("I've heard rumors about wilderness skilling.",
					"I haven't ventured far enough north to find it.", c.talkingNpc, "Shayzien Guard");
			c.nextChat = 0;
			break;

		case 55868:
			sendNpcChat2("I found some adamant ore in the mine here.", "I sold is for a pint of ale!", c.talkingNpc,
					"Old Sailor");
			c.nextChat = 0;
			break;
		case 55869:
			sendNpcChat2("I hate working here... One day,", "I'll travel to Lands End and become a crafting master!",
					c.talkingNpc, "Port Worker");
			c.nextChat = 0;
			break;
		case 55870:
			sendNpcChat2("If you're looking to catch Karambwans,", "You've come to the right place!", c.talkingNpc,
					"Port Officer");
			c.nextChat = 0;
			break;
		case 55871:
			sendNpcChat2("We were sailing next to CrabClaw Isle..", "When out of nowhere, a Kraken attacked!",
					c.talkingNpc, "Port Officer");
			c.nextChat = 0;
			break;
		case 55872:
			sendNpcChat2("I ain't never going back to that old farm!", "I'm *burp* happy right here!", c.talkingNpc,
					"Drunk Farmer");
			c.nextChat = 0;
			break;
		case 55873:
			sendNpcChat2("Hello, my name is Banky.", "I was hired by  to be a travelling banker!", c.talkingNpc,
					"Banky The Banker");
			c.nextChat = 0;
			break;
		case 55875:
			sendNpcChat2("Welcome to my chapel.", "Feel free to train your prayer skill here.", c.talkingNpc,
					"High Priest");
			c.nextChat = 0;
			break;
		case 55876:
			sendNpcChat2("Why hello my pretty!", "I will have a quest for you soon young one.", c.talkingNpc, "Witch");
			c.nextChat = 0;
			break;
		case 55877:
			sendNpcChat2("Those lizards are no joke!", "I wish I had some better armor...", c.talkingNpc,
					"Injured Guard");
			c.nextChat = 0;
			break;
		case 55874:
			sendOption5("Gnome Stronghold", "Xeric's Shrine", "Hosidius House", "Graveyard of Heros",
					"Piscarilius Docks");
			c.dialogueAction = 141314;
			c.nextChat = 0;
			break;

		// Zeah End guard and Throw Aways

		case 3308:
			sendOption2("Yes, I would like an easier task.", "No, I want to keep hunting on my current task.");
			c.dialogueAction = 3308;
			break;

		case 3309:
			sendNpcChat("Sorry, but your current task is already easy.", "Please come back when you've finished it.");
			c.nextChat = 0;
			break;

		case 3310:
			task = c.getSlayer().getTask();
			if (task.isPresent()) {
				task.ifPresent(t -> {
					String lines = WordUtils.wrap(Arrays.toString(t.getLocations()), 400);
					String[] split = lines.split("\\n");
					if (split.length > 4) {
						split = ArrayUtils.subarray(split, 0, 4);
					}
					sendNpcChat(split);
					c.nextChat = 3311;
				});
			} else {
				sendNpcChat("You need a task to check the location.");
				c.nextChat = -1;
			}
			break;

		case 3311:
			sendPlayerChat1("Great, thank you!");
			c.nextChat = 0;
			break;

		case 1390:
			sendNpcChat2("Hello Brave Warrior", "What would you like to do?", c.talkingNpc, "Combat Instructor");
			c.nextChat = 1391;
			break;

		case 1391:
			sendOption2("I'd like to reset a Combat skill", "I'd like to configure my dropvalue");
			c.dialogueAction = 1391;
			break;

		case 1392:
			sendOption2("Configure Dropvalue", "Reset Dropvalue");
			c.dialogueAction = 1392;
			break;

		case 1393:
			sendOption5("Set Value to 100K", "Set Value to 500K", "Set Value to 1M", "Set Value Manually",
					"What is this?");
			c.dialogueAction = 1393;
			break;

		case 1394:
			sendNpcChat("By configuring your dropvalue you are able", "to set it at any amount and when enabled",
					"any drop you receive which is worth that amount",
					"or more will be noticed by a graphic on the ground.");
			c.nextChat = 1393;
			break;

		case 1400:
			sendNpcChat1("I can reset a combat stat of your choice for 5M coins", c.talkingNpc, "Combat Instructor");
			c.nextChat = 1401;
			break;
		case 1401:
			sendOption5("Reset Attack", "Reset Strength", "Reset Defence", "Next",
					"I do not want to reset a stat right now");
			c.dialogueAction = 1401;
			break;
		case 1404:
			sendOption5("Reset Prayer", "Reset Range", "Reset Magic", "Reset Hitpoints", "Back");
			c.dialogueAction = 1404;
			break;

		case 14400:
			sendNpcChat("Hello there, " + c.getDisplayName() + ".", "I can take you to the agility courses or",
					"sell you some clothing.");
			c.nextChat = 14401;
			break;
		case 14401:
			sendOption4("Gnome Course", "Barbarian Course", "Wilderness Course @red@(DEEP WILD)", "Rooftop Agility Courses");
			c.dialogueAction = 14400;
			c.nextChat = 0;
			break;
		case 14410:
			sendOption5("Draynor Rooftop Course (Lvl 10)", "Al Kharid Rooftop Course (Lvl 20)", "Varrock Rooftop Course (Lvl 30)", "Canafis Rooftop Course (Lvl 40)", "Next");
			c.dialogueAction = 14410;
			c.nextChat = 0;
			break;
		case 14411:
			sendOption5("Seers Rooftop Course (Lvl 60)", "Ardougne Rooftop Course (Lvl 90)", "", "", "Back");
			c.dialogueAction = 14411;
			c.nextChat = 0;
			break;
		case 14412:
			sendOption5("Rellekka Rooftop Course (Lvl 80)", "Ardougne Rooftop Course (Lvl 90)", "", "", "Back");
			c.dialogueAction = 14412;
			c.nextChat = 0;
			break;
		case 14000:
			sendNpcChat1("What is it, Human?", c.talkingNpc, "Dagganoth Rex Jr");
			c.nextChat = 14001;
			break;
		case 14001:
			sendPlayerChat1("Wow you're feisty aren't you! I like my pets feisty");
			c.nextChat = 14002;
			break;
		case 14002:
			sendNpcChat2("And I like my humans crispy.", "You'll regret keeping me prisoner.", c.talkingNpc,
					"Dagganoth Rex Jr");
			c.nextChat = 0;
			break;
		case 14003:
			sendPlayerChat1("Hey, what does Prime say to Rex when Rex dies?");
			c.nextChat = 14004;
			break;
		case 14004:
			sendNpcChat1("I don't know, tell me.", c.talkingNpc, "Dagganoth Supreme Jr");
			c.nextChat = 14005;
			break;
		case 14005:
			sendPlayerChat1("Rext kid");
			c.nextChat = 0;
			break;
		case 14006:
			sendNpcChat2("ARE YOU NOT ENTERTAINED?", "IS THIS NOT WHY YOU ARE HERE?", c.talkingNpc,
					"Dagganoth Prime Jr");
			c.nextChat = 14007;
			break;
		case 14007:
			sendPlayerChat1("Yes, you are quite entertaining! Who's a good Prime!");
			c.nextChat = 14008;
			break;
		case 14008:
			sendNpcChat1("I'll have you know the ladies call me the Gladiator.", c.talkingNpc, "Dagganoth Prime Jr");
			c.nextChat = 0;
			break;
		case 14009:
			sendNpcChat2("...and then she mentioned something called a BBC?", "Oh, err, hello human.", c.talkingNpc,
					"King Black Dragon Jr");
			c.nextChat = 14010;
			break;
		case 14010:
			sendPlayerChat2("Oh, well okay then.", "I'll let you get back to your conversation.");
			c.nextChat = 0;
			break;
		case 14011:
			sendNpcChat1("Hey kid, you diggin' the helmet?", c.talkingNpc, "Barrelchest Jr");
			c.nextChat = 14012;
			break;
		case 14012:
			sendPlayerChat3("Yeah! I've never seen one before.", "I guess it's one small step for NPC's,",
					"one giant leap for !");
			c.nextChat = 14013;
			break;
		case 14013:
			sendNpcChat1("Precisely. Talk to you later, Neil! Err..." + c.getDisplayName() + "", c.talkingNpc,
					"Barrelchest Jr");
			c.nextChat = 0;
			break;
		case 14014:
			sendNpcChat3("I have failed my master on Gielinor.", "The Allspark has been...", "Human! what do you want!",
					c.talkingNpc, "General Graardor Jr");
			c.nextChat = 14015;
			break;
		case 14015:
			sendPlayerChat1("I'll do what you say, all right?! Just don't hurt me...");
			c.nextChat = 14016;
			break;
		case 14016:
			sendNpcChat2("PUNY but smart Human!", "You know to fear Graardor, leader of the Jogres.", c.talkingNpc,
					"General Graardor Jr");
			c.nextChat = 0;
			break;
		case 12001:
			sendOption2("Change task for 20 PKP (Chance of same)", "I'll do my assigned task.");
			c.dialogueAction = 12001;
			c.nextChat = 0;
			break;
		case 4000:
			sendOption2("Yes, read this scroll. I understand I cannot change my mind.", "No, don't read it.");
			c.dialogueAction = 4000;
			c.nextChat = 0;
			break;
		case 4001:
			sendOption2("Yes, read this scroll. I understand I cannot change my mind.", "No, don't read it.");
			c.dialogueAction = 4001;
			c.nextChat = 0;
			break;
		case 4002:
			sendOption2("Yes, read this scroll. I understand I cannot change my mind.", "No, don't read it.");
			c.dialogueAction = 4002;
			c.nextChat = 0;
			break;
		case 4003:
			sendOption2("Yes, read this scroll. I understand I cannot change my mind.", "No, don't read it.");
			c.dialogueAction = 4003;
			c.nextChat = 0;
			break;
		case 4004:
			sendOption2("Yes, read this scroll. I understand I cannot change my mind.", "No, don't read it.");
			c.dialogueAction = 4004;
			c.nextChat = 0;
			break;
		case 4005:
			sendOption2("Yes, use this scroll on the selected player. @red@NO REFUNDS@bla@.", "No, don't use it.");
			c.dialogueAction = 4005;
			c.nextChat = 0;
			break;
		case 4006:
			sendOption2("Yes, use this scroll on the selected player. @red@NO REFUNDS@bla@.", "No, don't use it.");
			c.dialogueAction = 4006;
			c.nextChat = 0;
			break;
		case 4007:
			sendOption2("Yes, use this scroll on the selected player. @red@NO REFUNDS@bla@.", "No, don't use it.");
			c.dialogueAction = 4007;
			c.nextChat = 0;
			break;
		case 4008:
			sendOption2("Yes, use this scroll on the selected player. @red@NO REFUNDS@bla@.", "No, don't use it.");
			c.dialogueAction = 4008;
			c.nextChat = 0;
			break;
		case 4009:
			sendOption2("Yes, use this scroll on the selected player. @red@NO REFUNDS@bla@.", "No, don't use it.");
			c.dialogueAction = 4009;
			c.nextChat = 0;
			break;
		case 4010:
			sendOption2("Yes, use this scroll on the selected player. @red@NO REFUNDS@bla@.", "No, don't use it.");
			c.dialogueAction = 4010;
			c.nextChat = 0;
			break;
		case 4011:
			sendOption2("Yes, use this scroll on the selected player. @red@NO REFUNDS@bla@.", "No, don't use it.");
			c.dialogueAction = 4011;
			c.nextChat = 0;
			break;
		case 2244:
			sendNpcChat1("Do you want change your spellbooks?", c.talkingNpc, "High Priest");
			c.nextChat = 2245;
			break;
			case 450:
				sendOption2("Empty inventory.", "No I made a mistake.");
				c.dialogueAction = 450;
				break;
		case 2299:
			c.getDH().sendOption4("Main", "Zerker", "Pure", "I'll set my stats myself.");
			c.dialogueAction = 2299;
			c.nextChat = 0;
			break;

		case 2400:
			sendOption5("Imbue Berserker Ring - @red@250 PKP", "Imbue Archer Ring - @red@250 PKP",
					"Imbue Seers Ring - @red@250 PKP", "Imbue Warrior Ring - @red@250 PKP", "More");
			c.dialogueAction = 114;
			c.teleAction = -1;
			c.nextChat = 0;
			break;
		case 2401:
			c.getDH().sendNpcChat2("Hello. I can take one of your regular rings", "and imbue it for PK points (PKP).",
					c.talkingNpc, "Amik Varze");
			c.nextChat = 2400;
			break;

		case 2402:
			sendOption5("Imbue Treasonous Ring @red@250 PKP", "Imbue Tyrannical Ring @red@250 PKP", "", "Previous",
					"Close");
			c.dialogueAction = 135;
			break;
		case 9994:
			sendPlayerChat1("Enter a minimum amount:");
			c.nextChat = 9995;
			break;
		case 9995:
			c.getPA().sendEnterAmount(0);
			c.settingMin = true;
			break;
		case 9998:
			c.settingMin = false;
			c.settingMax = true;
			sendPlayerChat1("Minimum bet amount set to: " + c.diceMin);
			c.nextChat = 9996;
			break;
		case 9996:
			sendPlayerChat1("Enter a maximum amount:");
			c.nextChat = 9997;
			break;
		case 9997:
			c.getPA().sendEnterAmount(0);
			c.nextChat = 9999;
			break;
		case 9999:
			c.settingMax = true;
			c.settingMin = false;
			sendPlayerChat1("Maximum bet amount set to: " + c.diceMax);
			c.nextChat = -1;
			break;
		case 11000:
			Player o = PlayerHandler.players[c.otherDiceId];
			sendPlayerChat1("Enter a number to bet between " + o.diceMin + " and " + o.diceMax);
			c.nextChat = 11001;
			break;
		case 11001:
			c.getPA().sendEnterAmount(0);
			c.nextChat = -1;
			break;
		// case 11002:
		// o = PlayerHandler.players[c.otherDiceId];
		// sendPlayerChat1("Bet amount with " + o.playerName + ": "
		// + o.betAmount);
		// o.sendMessage("Rolling dice...");
		// Dicing.rollDice(c);
		// // c.nextChat = -1;
		// break;
		case 11003:
			o = PlayerHandler.players[c.otherDiceId];
			sendPlayerChat1("Enter the amount you want to bet with " + o.getDisplayName());
			c.settingMax = false;
			c.settingMin = false;
			c.settingBet = true;
			c.nextChat = 11001;
			break;

		case 1023:
			sendOption2("Yes", "No");
			c.dialogueAction = 110;
			break;
		case 2245:
			c.getDH().sendOption3("Teleport me to Lunar Island, for lunars spellbook!",
					"Teleport me to Desert Pyramid, for ancients spellbook!", "No thanks, i will stay here.");
			c.dialogueAction = 2245;
			c.nextChat = 0;
			break;
		case 69:
			c.getDH().sendNpcChat1("Hello! Do you want to choose your clothes?", c.talkingNpc, "Thessalia");
			c.sendMessage("@red@You must right-click Thessalia to change your clothes.");
			c.nextChat = 0;
			break;
		case 6969:
			c.getDH().sendNpcChat2("I'm not working right now sir.",
					"If you wan't me to work, talk to Ardi give me a job.", c.talkingNpc, "Unemployed");
			c.sendMessage("This NPC do not have an action, if you have any suggestion for this NPC, post on forums.");
			c.nextChat = 0;
			break;
		/* LOGIN 1st TIME */
		case 769:
			c.getDH().sendNpcChat2("Welcome to Nothing!", "You must select your starter package.", c.talkingNpc,
					"Guide");
			c.nextChat = 770;
			break;
		case 770:
			sendStatement("Remember we're on beta, we will have a reset before official release.");
			c.nextChat = 771;
			break;
		case 771:
			c.getDH().sendOption3("Master (levels & items)", "Zerker (levels & items)", "Pure (levels & items)");
			c.dialogueAction = 771;
			break;
		/* END LOGIN */
		case 691:
			c.getDH().sendNpcChat2("Welcome to 2007remake.", "Please, read what i've to tell you...", c.talkingNpc,
					"Mod Ardi");
			c.nextChat = 692;
			// c.loggedIn = 1;
			break;
		case 692:
			sendNpcChat4("2007remake's on pre-alpha state.", "Then you can spawn items, and set your levels.",
					"But remember, it's just for pre-alpha sir...", "When we do the official release...", c.talkingNpc,
					"Mod Ardi");
			c.nextChat = 693;
			break;
		case 693:
			sendNpcChat4("We will have economy reset and,", "this commands will be removed too...",
					"Please, report glitches, and post suggestions",
					"on forums, for i can code, and we get 100% ready!", c.talkingNpc, "Mod Ardi");
			c.sendMessage("@red@You're online in 2007remake pre-alpha.");
			c.sendMessage("@red@Pre-alpha's to find glitches, and post suggestions in forums...");
			c.sendMessage("@red@Then our developer 'Mod Ardi' can code it, and we get official release in less time.");
			c.sendMessage("@red@Thanks for your attention sir.");
			c.nextChat = 0;
			break;
		/* AL KHARID */
		case 1022:
			c.getDH().sendPlayerChat1("Can I come through this gate?");
			c.nextChat = 1023;
			break;

		/*
		 * case 1023: c.getDH().sendNpcChat1(
		 * "You must pay a toll of 10 gold coins to pass.", c.talkingNpc,
		 * "Border Guard"); c.nextChat = 1024; break;
		 */
		case 1024:
			c.getDH().sendOption3("Okay, I'll pay.", "Who does my money go to?", "No thanks, I'll walk around.");
			c.dialogueAction = 502;
			break;
		case 1025:
			c.getDH().sendPlayerChat1("Okay, I'll pay.");
			c.nextChat = 1026;
			break;
		case 1026:
			c.getDH().sendPlayerChat1("Who does my money go to?");
			c.nextChat = 1027;
			break;
		case 1027:
			c.getDH().sendNpcChat2("The money goes to the city of Al-Kharid.", "Will you pay the toll?", c.talkingNpc,
					"Border Guard");
			c.nextChat = 1028;
			break;
		case 1028:
			c.getDH().sendOption2("Okay, I'll pay.", "No thanks, I'll walk around.");
			c.dialogueAction = 508;
			break;
		case 1029:
			c.getDH().sendPlayerChat1("No thanks, I'll walk around.");
			c.nextChat = 0;
			break;

		case 22:
			sendOption2("Pick the flowers", "Leave the flowers");
			c.nextChat = 0;
			c.dialogueAction = 22;
			break;
		/* Bank Settings **/
		case 1013:
			c.getDH().sendNpcChat1("Good day. How may I help you?", c.talkingNpc, "Banker");
			c.nextChat = 1014;
			break;
		case 1014:// bank open done, this place done, settings done, to do
					// delete pin
			c.getDH().sendOption3("I'd like to access my bank account, please.",
					"I'd like to check my my P I N settings.", "What is this place?");
			c.dialogueAction = 251;
			break;
		/* What is this place? **/
		case 1015:
			c.getDH().sendPlayerChat1("What is this place?");
			c.nextChat = 1016;
			break;
		case 1016:
			c.getDH().sendNpcChat2("This is the bank of 2007remake.", "We have many branches in many towns.",
					c.talkingNpc, "Banker");
			c.nextChat = 0;
			break;
		/*
		 * Note on P I N. In order to check your "Pin Settings. You must have enter your
		 * Bank Pin first
		 */
		/* I don't know option for Bank Pin **/
		case 1017:
			c.getDH().sendStartInfo("Since you don't know your P I N, it will be deleted in @red@3 days@bla@. If you",
					"wish to cancel this change, you may do so by entering your P I N",
					"correctly next time you attempt to use your bank.", "", "", false);
			c.nextChat = 0;
			break;
		case 0:
			c.talkingNpc = -1;
			c.getPA().removeAllWindows();
			c.nextChat = 0;
			break;
		case 3:
			sendNpcChat4("Hello!", "My name is Duradel and I am a master of the slayer skill.",
					"I can assign you a slayer task suitable to your combat level.", "Would you like a slayer task?",
					c.talkingNpc, "Duradel");
			c.nextChat = 4;
			break;
		case 5:
			sendNpcChat4("Hello adventurer...", "My name is Kolodion, the master of this mage bank.",
					"Would you like to play a minigame in order ",
					"to earn points towards receiving magic related prizes?", c.talkingNpc, "Kolodion");
			c.nextChat = 6;
			break;
		case 6:
			sendNpcChat4("The way the game works is as follows...", "You will be teleported to the wilderness,",
					"You must kill mages to receive points,", "redeem points with the chamber guardian.", c.talkingNpc,
					"Kolodion");
			c.nextChat = 15;
			break;
		case 11:
			sendNpcChat4("Hello!", "My name is Duradel and I am a master of the slayer skill.",
					"I can assign you a slayer task suitable to your combat level.", "Would you like a slayer task?",
					c.talkingNpc, "Duradel");
			c.nextChat = 12;
			break;
		case 12:
			sendOption2("Yes I would like a slayer task.", "No I would not like a slayer task.");
			c.dialogueAction = 5;
			c.nextChat = 0;
			break;
		case 13:
			sendNpcChat4("Hello!", "My name is Duradel and I am a master of the slayer skill.",
					"I see I have already assigned you a task to complete.",
					"Would you like me to give you an easier task?", c.talkingNpc, "Duradel");
			c.nextChat = 14;
			break;
		case 14:
			sendOption2("Yes I would like an easier task.", "No I would like to keep my task.");
			c.dialogueAction = 6;
			c.nextChat = 0;
			break;
		case 15:
			sendOption2("Yes I would like to play", "No, sounds too dangerous for me.");
			c.dialogueAction = 7;
			break;
		case 16:
			sendOption2("I would like to reset my barrows brothers.", "I would like to fix all my barrows");
			c.dialogueAction = 8;
			break;
		case 17:
			sendOption5("Air", "Mind", "Water", "Earth", "More");
			c.dialogueAction = 10;
			c.dialogueId = 17;
			c.teleAction = -1;
			break;
		case 18:
			sendOption5("Fire", "Body", "Cosmic", "Astral", "More");
			c.dialogueAction = 11;
			c.dialogueId = 18;
			c.teleAction = -1;
			break;
		case 19:
			sendOption5("Nature", "Law", "Death", "Blood", "More");
			c.dialogueAction = 12;
			c.dialogueId = 19;
			c.teleAction = -1;
			break;
		case 20:
			sendNpcChat4("Haha, hello", "My name is Wizard Distentor! I am the master of clue scroll reading.",
					"I can read the magic signs of a clue scroll",
					"You got to pay me 100K for reading the clue though!", c.talkingNpc, "Wizard Distentor");
			c.nextChat = 21;
			break;

		case 23000:
			c.getDH().sendStartInfo("As you collect your reward, you notice an awful smell.",
					"You look below the remaining debris to the bottom of the",
					"chest. You see a trapdoor. You open it and it leads to a ladder", "that goes down a long ways.",
					"Continue?");
			break;
		case 24000:
			c.getDH().sendStatement("Would you like to continue?");
			c.nextChat = 2500;
			break;
		case 2500:
			c.dialogueAction = 25;
			c.getDH().sendOption2("Yes, I'm not afraid of anything!", "No way, the smell itself turns me away.");
			break;
		case 2600:
			c.getDH().sendStatement("This is a very dangerous minigame, are you sure?");
			c.nextChat = 2700;
			break;
		case 2700:
			c.dialogueAction = 27;
			sendOption2("Yes, I'm a brave warrior!", "Maybe I shouldn't, I could lose my items!");
			break;
		case 2800:
			c.getDH().sendStatement("Congratulations, " + c.getDisplayName()
					+ ". You've completed the barrows challenge & your reward has been delivered.");
			c.nextChat = 0;
			break;
		case 2900:
			sendStatement("You've found a hidden tunnel, do you want to enter?");
			c.nextChat = 3000;
			c.dialogueAction = 29;
			break;
		case 3000:
			sendOption2("Yeah, I'm fearless!", "No way, that looks scary!");
			c.nextChat = 0;
			break;
		case 21:
			sendOption2("Yes I would like to pay 100K", "I don't think so sir");
			c.dialogueAction = 50;
			break;
		case 206:
			sendOption2("Stop viewing", "");
			c.dialogueAction = 206;
			break;
		case 23:
			sendNpcChat4("Greetings, Adventure", "I'm the legendary Vesta seller",
					"With 120 noted Lime Stones, and 20 Million GP", "I'll be selling you the Vesta's Spear",
					c.talkingNpc, "Legends Guard");
			c.nextChat = 24;
			break;
		case 54:
			sendOption2("Buy Vesta's Spear", "I can't afford that");
			c.dialogueAction = 51;
			break;
		case 56:
			sendStatement("Hello " + c.getDisplayName() + ", you currently have " + c.pkp + " PK points.");
			break;

		case 57:
			c.getPA().sendFrame126("Teleport to shops?", 2460);
			c.getPA().sendFrame126("Yes.", 2461);
			c.getPA().sendFrame126("No.", 2462);
			c.getPA().sendChatboxInterface(2459);
			c.dialogueAction = 27;
			break;

		/*
		 * Recipe for disaster - Sir Amik Varze
		 */

		case 25:
			sendOption2("Yes", "No");
			c.rfdOption = true;
			c.nextChat = 0;
			break;
		case 26:
			sendPlayerChat1("Yes");
			c.nextChat = 28;
			break;
		case 27:
			sendPlayerChat1("No");
			c.nextChat = 29;
			break;

		case 29:
			c.getPA().removeAllWindows();
			c.nextChat = 0;
			break;
		case 30:
			sendNpcChat4("Congratulations!", "You have defeated all Recipe for Disaster bosses",
					"and have now gained access to the Culinaromancer's chest", "and the Culinaromancer's item store.",
					c.talkingNpc, "Sir Amik Varze");
			c.nextChat = 0;
			PlayerSave.saveGame(c);
			break;
		case 31:
			sendNpcChat4("", "You have been defeated!", "You made it to round " + c.roundNpc, "", c.talkingNpc,
					"Sir Amik Varze");
			c.roundNpc = 0;
			c.nextChat = 0;
			break;

		//

		case 144:
			sendNpcChat1("Nice day, isn't it?", c.talkingNpc, "");
			c.nextChat = 0;
			break;

		case 145:
			sendNpcChat1("Hello, I am the Blood Money exchange handler", c.talkingNpc, "Blood Money Exchange");
			c.nextChat = 146;
			break;

		case 146:
			sendNpcChat3("Right click me and use the 'Exchange' option", "to turn your Blood Money into points",
					"to use in my shop!", c.talkingNpc, "Blood Money Exchange");
			c.nextChat = 0;
			break;

		case 500:
			sendOption2("Vote Crystal", "PKP tickets");
			c.dialogueAction = 201;
			c.nextChat = 0;
			break;
		case 501:
			sendStatement("Would you like to exchange all of the PK point tickets", "in your inventory for PK points?");
			c.nextChat = 502;
			break;

		case 502:
			sendOption2("Yes", "No");
			c.dialogueAction = 200;
			break;
		case 503:
			sendStatement("Would you like to exchange all of the vote tickets", "in your inventory for vote points?");
			c.nextChat = 504;
			break;
		case 504:
			sendOption2("Yes", "No");
			c.dialogueAction = 202;
			break;


		case 837:
			sendNpcChat("Where would you like to travel on my magical carpet?");
			c.nextChat = 838;
			break;
		case 838:
			sendOption5("Pollnivneach", "Bedabin Camp", "Al Kharid", "Bandit Camp", "Nardah");
			c.dialogueAction = 838;
			break;
		case 506:
			sendPlayerChat1("I...");
			c.nextChat = 507;
			break;

		case 507:
			sendNpcChat1("Speak up human before I lose my patience.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 508;
			break;

		case 508:
			sendPlayerChat2("Okay okay, what are you doing here in Edgeville?", "You weren't here a few days ago.");
			c.nextChat = 509;
			break;

		case 509:
			sendNpcChat1("No...I wasn't. I'm seeking a few lost posessions.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 510;
			break;

		case 510:
			sendPlayerChat1("What did you lose?");
			c.nextChat = 511;
			break;

		case 511:
			sendNpcChat3("I lost a large amount of @or2@pumpkins@bla@.",
					"I was travelling through the wilderness and they were stolen.",
					"It's probably that gang of ghosts again.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 512;
			break;

		case 512:
			sendNpcChat2("If you help me find those pumpkins, I'll let you live.", "Do we have a deal?", c.talkingNpc,
					"Grim Reaper");
			c.nextChat = 513;
			break;

		case 513:
			sendOption3("Yes", "Do I really have a choice?", "*whimpering* N...o");
			c.dialogueAction = 513;
			break;

		case 514:
			sendNpcChat4("Every Halloween I travel through the wilderness",
					"carrying a very important bag of pumpkins.", "I need to take these pumpkins to the other world",
					"by 12:00, otherwise there will be no halloween.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 518;
			break;

		case 515:
			sendNpcChat2("Haha, you're clever human.", "You're lucky you know your place.", c.talkingNpc,
					"Grim Reaper");
			c.nextChat = 514;
			break;

		case 516:
			sendNpcChat1("Do I smell fear on you human?.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 517;
			break;

		case 517:
			sendPlayerChat1("Please don't hurt me.");
			c.nextChat = -1;
			break;

		case 518:
			sendPlayerChat1("I don't understand, why do you need them so badly?");
			c.nextChat = 519;
			break;

		case 519:
			sendNpcChat3("You fool, these are no ordinary pumpkins.",
					"They are the root of my power, without them I cannot",
					"venture to this world. Thus, no more halloween.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 520;
			break;

		case 520:
			sendPlayerChat3("No more halloween, that would be no more candy.",
					"That would also mean no more grim reaper.",
					"The world would be better off without you, I can't help.");
			c.nextChat = 521;
			break;

		case 521:
			sendNpcChat2("YOU WILL HELP ME HUMAN", "I WILL DESTROY THIS WORLD IF YOU DON'T!", c.talkingNpc,
					"Grim Reaper");
			c.nextChat = 522;
			break;

		case 522:
			sendStatement("You whisper:", "Whoah that power...hes not bluffing.", "I will have to help him.");
			c.nextChat = 523;
			break;



		case 524:
			sendNpcChat3("You will need to recover @red@50@bla@ pumpkins..",
					"You can find these pumpkins in a chest, in the wilderness.",
					"The chest is guarded by four ghosts.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 525;
			break;


		case 528:
			if (!c.getItems().playerHasItem(611) && !c.getItems().bankContains(611)) {
				if (c.getItems().freeSlots() < 1) {
					sendNpcChat2("You lost the locator, but need an open space in your",
							"inventory, come back when you have an open space.", c.talkingNpc, "Grim Reaper");
					c.nextChat = -1;
					return;
				}
				sendNpcChat3("You are fortunate, I found the crystal.",
						"Do not lose this crystal again, it's important.",
						"Come talk to me when you have made some progress.", c.talkingNpc, "Grim Reaper");
				c.getItems().addItem(611, 1);
				c.nextChat = -1;
			} else {
				sendNpcChat2("You must bring me back the pumpkins noted.",
						"Lets see how much progress you have made...", c.talkingNpc, "Grim Reaper");
				c.nextChat = 529;
			}
			break;

		case 529:
			int total = c.getItems().getItemAmount(1960);
			if (total < 50) {
				if (total == 0) {
					sendNpcChat3("You have not found any pumpkins.",
							"You need to look harder and search using the locator.", "Hurry, there isn't much time.",
							c.talkingNpc, "Grim Reaper");
				} else if (total > 0 && total < 10) {
					sendNpcChat3("You have found " + total + ", that's a good start.",
							"However, you need to search harder.", "Hurry, there isn't much time.", c.talkingNpc,
							"Grim Reaper");
				} else {
					sendNpcChat2("You're nearly finished, find the rest and", "talk to me as soon as you can.",
							c.talkingNpc, "Grim Reaper");
				}
				c.nextChat = -1;
			} else {
				sendNpcChat2("Great job you found the 50 pumpkins.", "Let me take them off your hands...", c.talkingNpc,
						"Grim Reaper");
				c.nextChat = 530;
			}
			break;

		case 530:
			sendPlayerChat2("Do you know what I had to go through to get these?", "What do I get in return?!");
			c.nextChat = 531;
			break;

		case 531:
			sendNpcChat2("Greedy human, isn't your life enough?", "Well, if you insist peasant...", c.talkingNpc,
					"Grim Reaper");
			c.nextChat = 532;
			break;

		

		case 533:
			sendNpcChat3("You came through for me human, halloween will", "continue to exist because of you.",
					"I hope you enjoy the reward I gave you.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 534;
			break;

		case 534:
			sendNpcChat1("Is there anything else I can do for you?", c.talkingNpc, "Grim Reaper");
			c.nextChat = 535;
			break;

		case 535:
			sendOption3("What else can I do?", "I lost my locator, can I get another?", "Nothing");
			c.dialogueAction = 535;
			break;

	

		case 537:
			if (c.getItems().bankContains(611)) {
				sendNpcChat3("Hmm...It seems your bank has the locator in it.",
						"You may have helped me but I will still punish you.",
						"I will provide you with one if you don't have one.", c.talkingNpc, "Grim Reaper");
				c.nextChat = -1;
				return;
			}
			if (c.getItems().playerHasItem(611)) {
				sendNpcChat3("Hmm...It seems your inventory has the locator in it.",
						"You may have helped me but I will still punish you.",
						"I will provide you with one if you don't have one.", c.talkingNpc, "Grim Reaper");
				c.nextChat = -1;
				return;
			}
			if (c.getItems().freeSlots() < 1) {
				sendNpcChat2("I can give you a new locator but you need a free", "inventory slot.", c.talkingNpc,
						"Grim Reaper");
				c.nextChat = -1;
				return;
			}
			c.getItems().addItem(611, 1);
			sendNpcChat2("I have given you a new locator.", "Click it to get a hint where the chest is.", c.talkingNpc,
					"Grim Reaper");
			c.nextChat = -1;
			break;

		case 538:
			sendNpcChat2("Welcome player, you can retrieve lost items in my shop.",
					"Unfortunately, these are mostly holiday items.", c.talkingNpc, "Diango");
			c.nextChat = -1;
			break;

		case 550:
			sendOption2("Speak about emblems", "How many players are in the wilderness?");
			c.dialogueAction = 550;
			break;

		case 551:
			sendNpcChat3(
					"There are currently " + Boundary.getPlayersInBoundary(Boundary.WILDERNESS) + " players in wilderness.",
					"There are currently " + Boundary.getPlayersInBoundary(Boundary.WILDERNESS_UNDERGROUND)
							+ " players in underground wilderness.",
					"There are currently " + Boundary.getPlayersInBoundary(Boundary.WILDERNESS_GOD_WARS_BOUNDARY)
							+ " players in godwars wilderness.",
					c.talkingNpc, "Emblem Trader");
			c.nextChat = 0;
			break;

		case 539:
			sendPlayerChat1("Hey..?");
			c.nextChat = 540;
			break;

		case 540:
			sendNpcChat1("Hello, wanderer.", c.talkingNpc, "Emblem Trader");
			for (BountyHunterEmblem e : BountyHunterEmblem.EMBLEMS) {
				if (c.getItems().playerHasItem(e.getItemId())) {
					c.nextChat = 553;
					break;
				}
			}
			if (c.nextChat != 553)
				c.nextChat = 541;
			sendNpcChat1("Hello, wanderer.", c.talkingNpc, "Emblem Trader");
			break;

		case 541:
			sendNpcChat2("Don't suppose you've come across any strange....", "emblems along your journey?",
					c.talkingNpc, "Emblem Trader");
			c.nextChat = 542;
			break;

		case 542:
			sendPlayerChat1("Not that I've seen.");
			c.nextChat = 543;
			break;

		case 543:
			sendNpcChat2("If you do, please do let me know. I'll reward you", "handsomely.", c.talkingNpc,
					"Emblem Trader");
			c.nextChat = 544;
			break;

		case 544:
			sendOption3("What rewards have you got?", "Can I have a PK skull please.", "That's nice.");
			c.dialogueAction = 100;
			break;

		case 545:
			if (!c.isSkulled) {
				sendOption2("@red@Obtain a Skull?", "Give me a PK skull.", "Cancel");
				c.dialogueAction = 101;
			} else if (c.skullTimer > 0 && c.skullTimer < Configuration.SKULL_TIMER) {
				sendOption2("@red@Extend duration?", "Yes", "No");
				c.dialogueAction = 102;
			} else {
				sendNpcChat1("You are already skulled, and the duration is extended.", c.talkingNpc, "Emblem Trader");
				c.nextChat = -1;
			}
			break;

		case 546:
			sendStatement("You are now skulled.");
			c.isSkulled = true;
			c.skullTimer = Configuration.SKULL_TIMER;
			c.headIconPk = 0;
			c.getPA().requestUpdates();
			c.nextChat = -1;
			break;

		case 547:
			sendStatement("Your PK skull will now last for the full 20 minutes.");
			c.isSkulled = true;
			c.skullTimer = Configuration.EXTENDED_SKULL_TIMER;
			c.headIconPk = 0;
			c.getPA().requestUpdates();
			c.nextChat = -1;
			break;

		case 548:
			if (c.getBH().isStatisticsVisible()) {
				sendOption2("Disable Streaks?", "Yes", "No");
			} else {
				sendOption2("Enable Streaks?", "Yes", "No");
			}
			c.dialogueAction = 104;
			break;

		case 549:
			if (c.getBH().isStatisticsVisible()) {
				c.getBH().setStatisticsVisible(false);
				sendNpcChat1("The statistics interface has been disabled.", c.talkingNpc, "Emblem Trader");
			} else {
				c.getBH().setStatisticsVisible(true);
				sendNpcChat1("The statistics interface has been enabled.", c.talkingNpc, "Emblem Trader");
			}
			c.getBH().updateTargetUI();
			c.nextChat = -1;
			break;

		case 553:
			sendNpcChat2("I see you have something valuable on your person.", "Certain.... ancient emblems, you see.",
					c.talkingNpc, "Emblem Trader");
			c.nextChat = 554;
			break;

		case 554:
			sendNpcChat2("I'll happily take those off of your hands for a handsome", "fee.", c.talkingNpc,
					"Emblem Trader");
			c.nextChat = 555;
			break;

		case 555:
			sendStatement("All of your emblems are worth a total of "
					+ Misc.insertCommas(Integer.toString(c.getBH().getNetworthForEmblems())), "PKP Points.");
			c.nextChat = 556;
			break;

		case 556:
			sendOption2("Sell all Emblems?", "Yes", "No");
			c.dialogueAction = 106;
			break;

		case 557:
			sendNpcChat2("Thank you. My master in the north will be very", "pleased.", c.talkingNpc, "Emblem Trader");
			c.nextChat = -1;
			break;

		case 578:
			if (c.getBH().isSpellAccessible()) {
				sendStatement("This spell looks very familiar, I must already know", "the spell.");
			} else {
				sendOption2("Learn teleport?", "Yes", "No");
				c.dialogueAction = 114;
			}
			c.nextChat = -1;
			break;

		case 579:
			if (!c.getItems().playerHasItem(12846)) {
				c.getPA().removeAllWindows();
				return;
			}
			if (c.getBH().isSpellAccessible()) {
				sendDialogues(578, -1);
				return;
			}
			sendStatement("You start to read the scroll...",
					"You now have access to the @blu@Teleport to Bounty Target spell.",
					"This spell requires @blu@3 law runes@bla@ and @blu@85 magic.");
			c.getBH().setSpellAccessible(true);
			c.getItems().deleteItem2(12846, 1);
			break;

		// Eric Hunter
		case 88393:
			sendNpcChat2("I've been looking for Red Chinchompas...", "I hear they are to the North West of here.",
					c.talkingNpc, "Hunting Expert");
			c.nextChat = -1;
			break;
		case 88394:
			sendNpcChat2("Birds surround this area, you should start with those.",
					"After that, you can hunt larger animals.", c.talkingNpc, "Hunting Expert");
			c.nextChat = -1;
			break;

		/*
		 * Christmas
		 */
		case 477:
			sendNpcChat("Thank you so much!, here is a special gift for you help.");
			c.nextChat = -1;
			break;
		case 478:
			sendNpcChat("This is not all of santa's presents lost.");
			c.nextChat = -1;
			break;
		case 480:
			sendNpcChat("Merry christmas eve!, have you retrieved the stolen items?");
			c.nextChat = 481;
			break;
		case 481:
			sendOption2("Yes, I have.", "No, I haven't.");
			c.dialogueAction = 481;
			break;
		case 580:
			sendNpcChat("Merry christmas eve!");
			c.nextChat = 581;
			break;

		case 581:
			sendPlayerChat3("Merry christmas eve to you too.", "If you don't mind me asking, what are",
					"you doing here in edgeville?");
			c.nextChat = 582;
			break;

		case 582:
			sendNpcChat("Strange, another player asked the same thing.",
					"The reason I am here in edgeville is is because",
					"I am looking for people with good hearts to help me.", "Do you have a good heart?");
			c.nextChat = 583;
			break;

		case 583:
			sendOption2("Yes, I do.", "No, I don't.");
			c.dialogueAction = 1004;
			break;

		case 584:
			sendNpcChat("Ho Ho Ho, aren't you a little rascal.", "I hope you like smithing because you will",
					"be getting tons of coal this year.");
			c.nextChat = -1;
			break;

		case 585:
			sendNpcChat("Ho Ho Ho, I see your name on the nice list.", "Would you like to help me?");
			c.nextChat = 586;
			break;

		case 3454:
			sendNpcChat2("The Industrial Revolution and its consequences have", "been a disaster for the human race.", 9400, "Theodore Kaczynski");
			break;
		case 586:
			sendOption2("Yes", "No");
			c.dialogueAction = 116;
			break;

		case 587:
			sendNpcChat("Alright then, come back if you do.", "Happy holidays.");
			c.nextChat = -1;
			break;

		case 588:
			sendNpcChat("Thank you, let me explain my dilemma...");
			c.nextChat = 589;
			break;

		case 589:
			sendNpcChat("Each and every year presents are made by the elves",
					"and then distributed to all the children on christmas.",
					"Presents are made at @blu@2 @bla@workstations. These workstations", "are in the north pole.");
			c.nextChat = 590;
			break;

		case 590:
			sendNpcChat("Normally we travel in our sleigh.", "However due to us @blu@crashing",
					"caused by the mass amount of players stomping in edgeville", "we can no longer travel with it.");
			c.nextChat = 591;
			break;

		case 591:
			sendNpcChat("The only choice we have is to go by foot and go",
					"from station-to-station which is very tedious for our little elves.");
			c.nextChat = 592;
			break;

		case 592:
			sendNpcChat("I need you to go from station-to-station to further",
					"the development of each toy. Let me explain the process.",
					"Each toy has 2 stages of development. The first is",
					"the development stage, this happens at @blu@Station #1@bla@.");
			c.nextChat = 593;
			break;

		case 593:
			sendNpcChat("The second is the finalization stage.", "This happens at @blu@Station #2@bla@.");
			c.nextChat = 594;
			break;

		case 594:
			sendNpcChat("You must bring each toy to station 1, and station 2.",
					"You cannot take a toy to station 2, then station 1.",
					"If you do, the toy may break and you will have to restart.");
			c.nextChat = 595;
			break;

		case 595:
			sendNpcChat("Each station will have a unique engineer.",
					"You must give the toy to the engineer at that station.",
					"Once the engineer at station 2 has finished the", "development, speak to me.");
			c.nextChat = 596;
			break;

		

		case 597:
			sendOption2("Yes, please repeat that.", "No, I understand what I have to do.");
			c.dialogueAction = 117;
			break;




		case 600:
			sendOption4("I have a fully developed toy.", "I don't know what to do.", "Where are the stations?",
					"Nevermind");
			c.dialogueAction = 118;
			break;

		case 601:
			sendNpcChat("Let me reiterate for you.");
			c.nextChat = 589;
			break;

		case 602:
			sendNpcChat("The first station is at the top of the ice-path.", "Right-click on me to go there",
					"Note: It is very cold and slippery, you might take damage.");
			c.nextChat = 603;
			break;

		case 603:
			sendNpcChat("The second station is next the mountain.", "directly south-west of the ice-path.");
			c.nextChat = 600;
			break;

	

		case 605:
			sendPlayerChat1("Err...alright.");
			c.nextChat = -1;
			break;

		case 606:
			sendNpcChat("The toy has been further developed and is now", "ready to be completed. Go to the second",
					"station and talk to the other engineer.");
			c.dialogueAction = -1;
			c.nextChat = -1;
			break;

		case 607:
			sendNpcChat("The toy has finished being developed.", "Talk to santa in edgeville.");
			c.dialogueAction = -1;
			c.nextChat = -1;
			break;

		

		case 610:
			sendNpcChat("I cannot thank you enough for the help " + c.getDisplayName() + ".",
					"Would you like to fight the anti-santa? You must",
					"wear the reindeer hat to successfully attack him.", "He is in deep wilderness, it is dangerous.");
			c.nextChat = 611;
			break;

		case 611:
			sendOption2("Yes, I'm ready.", "No.");
			c.dialogueAction = 119;
			break;

		case 612:
			sendOption2("Use christmas cracker, I may get a partyhat.", "No, I want to keep my partyhat.");
			c.dialogueAction = 120;
			break;

		// End of Christmas

		case 613:
			sendOption5("How much have I contributed this week?", "Who are the top 5 contributors this week?",
					"Who won last week?", "When will a winner be selected?", "Close.");
			c.dialogueAction = 121;
			break;

		case 615:
			sendStatement("You have not contributed anything so far this week.", "The gods are not very pleased.");
			c.nextChat = 613;
			break;



		case 619:
			sendNpcChat2("Hello player, is there anything I can help", "you with?", c.talkingNpc, "Perdu");
			c.nextChat = 620;
			break;

		case 620:
			sendOptions("I want to repair an item.", "I want to claim back an item that has degraded.",
					"I don't see the item I want to claim on the list.", "Nothing.");
			c.dialogueAction = 122;
			break;

		case 621:
			sendNpcChat("You can repair a damaged item by using it on me.",
					"Each item has a repair cost. Most repairs range from", "250,000k to 500,000k");
			c.nextChat = 622;
			break;

		case 622:
			sendNpcChat("If an item degrades and disappears, you can claim it.",
					"It costs 2x as much to claim it then it does to repair.");
			c.nextChat = 620;
			break;

		case 623:
			Degrade.DegradableItem[] degradeList = Degrade.getClaimedItems(c);
			if (degradeList.length == 0) {
				sendNpcChat("You don't have any fully degraded items to claim.");
				c.nextChat = 620;
				return;
			}
			String item1 = "";
			String item2 = "";
			String item3 = "";
			String item4 = "";

			if (Degrade.DegradableItem.forId(degradeList[0].getItemId()).isPresent())
				item1 = ItemAssistant.getItemName(degradeList[0].getItemId()) + " @red@"
						+ (Degrade.DegradableItem.forId(degradeList[0].getItemId()).get().getCost()) + " GP";

			if (degradeList.length > 1 && Degrade.DegradableItem.forId(degradeList[1].getItemId()).isPresent()) {
				item2 = ItemAssistant.getItemName(degradeList[1].getItemId()) + " @red@"
						+ (Degrade.DegradableItem.forId(degradeList[1].getItemId()).get().getCost()) + " GP";
			}
			if (degradeList.length > 2 && Degrade.DegradableItem.forId(degradeList[2].getItemId()).isPresent()) {
				item3 = ItemAssistant.getItemName(degradeList[2].getItemId()) + " @red@"
						+ (Degrade.DegradableItem.forId(degradeList[2].getItemId()).get().getCost()) + " GP";
			}
			if (degradeList.length > 3 && Degrade.DegradableItem.forId(degradeList[3].getItemId()).isPresent()) {
				item4 = ItemAssistant.getItemName(degradeList[3].getItemId()) + " @red@"
						+ (Degrade.DegradableItem.forId(degradeList[3].getItemId()).get().getCost()) + " GP";
			}
			sendOptions(item1, item2, item3, item4, "Close");
			c.dialogueAction = 123;
			break;

		case 624:
			sendNpcChat("The list only shows up to 4 claimable items at a time.",
					"You can free up space by claiming items.", "It is good practice to never have more than three",
					"claimable items at any given time to avoid this.");
			break;

		case 625:
			sendStatement("Would you like to return to Zulrah's shrine?");
			c.nextChat = 626;
			break;

		case 626:
			sendOptions("Yes, return.", "No.");
			c.dialogueAction = 124;
			break;

		case 627:
			sendOption2("Yes", "No");
			c.dialogueAction = 125;
			break;

		case 628:
			sendNpcChat("Hello, my name is Sigmund.", "I am looking for items I can merchant to other players.",
					"If you have any please trade me.");
			c.nextChat = -1;
			break;

		case 629:
			sendNpcChat("Hello, what do you want?");
			c.nextChat = 630;
			break;

		case 630:
			sendOptions("I want to go to the Abyss", "I want to go to the Essence Mine", "I'm in need of a pouch");
			c.dialogueAction = 126;
			c.nextChat = -1;
			break;

		case 631:
			sendStatement("Would you like to enter the resource area for 50,000?");
			c.nextChat = 632;
			break;

		case 632:
			sendOptions("Yes", "No");
			c.dialogueAction = 127;
			break;

		case 63100:
			sendStatement("Would you like to enter the resource area for 50,000?");
			c.nextChat = 63200;
			break;

		case 63200:
			sendOptions("Yes", "No");
			c.dialogueAction = 12700;
			break;

		case 633:
			sendOption5("10 Waves - Fire Cape", "20 Waves - Fire Cape", "63 Waves - Fire Cape", "How does it work?",
					"What are my rankings?");
			c.dialogueAction = 128;
			break;

		case 634:
			this.sendStatement("The minigame is fairly straightforward.",
					"Choose how many waves of npcs you want to fight against.",
					"After you defeat all of the npcs in the current wave",
					"you will move onto the next. Once you reach the final",
					"wave you will fight Tz-tok jad, a level 702 beast.");
			c.nextChat = 635;
			break;

		case 635:
			this.sendStatement("The more waves you defeat in total, the better your reward.",
					"You may logout at anytime within the minigame.",
					"You are unable to teleport from within the minigame.");
			c.nextChat = 0;
			break;

		case 636:
			this.sendStatement("You have completed...", "Level 1 (10 Waves) " + c.waveInfo[0] + " times",
					"Level 2 (20 Waves) " + c.waveInfo[1] + " times", "Level 3 (63 Waves) " + c.waveInfo[2] + " times");
			c.nextChat = 0;
			break;

		case 637:
			sendNpcChat("Hello " + c.getDisplayName() + ".", "Are you looking to fight Zulrah?");
			c.nextChat = 638;
			break;

		case 638:
			sendOptions("Yes, take me there.", "No, I was hoping to get my items back.",
					"No, I was wondering who holds the current record.", "Nevermind");
			c.dialogueAction = 129;
			break;

		case 639:
			sendNpcChat("You don't have any items that are lost.");
			c.nextChat = -1;
			break;

		case 640:
			sendNpcChat("So you want to claim your lost items.", "It will cost a total of 500,000GP for all the items.",
					"Do you want them back?");
			c.nextChat = 641;
			break;

		case 641:
			sendOptions("Yes", "No");
			c.dialogueAction = 130;
			break;

		case 642:
			sendNpcChat("You need to retain all of your lost items before you", "can go in again.");
			c.nextChat = -1;
			break;

		case 643:
			sendNpcChat("There is no current record!");
			c.nextChat = -1;
			break;
		case 645:
			sendNpcChat("You've made it through!", "You have the option to play as an <col=" + Right.IRONMAN + "><img=12></img>Iron Man</col>, <col=" + Right.GROUP_IRONMAN + "><img=27></img>GIM</col>",
                    "<col=" + Right.ULTIMATE_IRONMAN + "><img=13></img>Ultimate Iron Man</col>, <col=" + Right.HC_IRONMAN + "><img=9></img>Hardcore Iron Man</col>, or neither.", "Choose from the following interface.");
            c.nextChat = 646;
			break;


		case 2647:
			sendOption3("Fast Xp Rates","@red@[WARNING] 5x XP Rates but +5% Drop Rate Boost[SLOW]",
					"@red@[WARNING] 1x XP Rates but +10% Drop Rate Boost[SLOW]");
			c.dialogueAction = 2647;
		case 9649:
			c.getPA().showInterface(3559);
			c.canChangeAppearance = true;
			
			break;
		case 650:
			if (c.getMode().hasAccessToIronmanNpc()) {
				sendNpcChat("Hello " + c.getDisplayName() + ". How can I help you?");
				c.nextChat = 651;
			} else {
				sendNpcChat("Hello there " + c.getDisplayName() + ".",
						"You're not enrolled as an iron man or ultimate iron man.", "I cannot assist you in anyway.");
				c.nextChat = -1;
			}
			break;

		case 651:
			sendOptions("What restrictions do I have?", "I would like to revert my account",
					"I am missing armour, do you have any?", "", "Close");
			c.dialogueAction = 131;
			break;

		case 653:
			sendOptions("Yes, revert me to regular mode. [NO-REVERSE-BACKS]", "No.");
			c.dialogueAction = 132;
			break;

		case 654:
			sendOptions("Yes, cancel the request to revert to regular mode.",
					"No, keep the request, I want to revert.");
			c.dialogueAction = 133;
			break;

		case 655:
			c.getRights().remove(Right.OSRS);
			c.getRights().remove(Right.IRONMAN);
			c.getRights().remove(Right.ULTIMATE_IRONMAN);
			c.getRights().remove(Right.HC_IRONMAN);
			c.setMode(Mode.forType(ModeType.STANDARD));
			sendNpcChat("Your mode has been reverted to regular upon request.");
			c.nextChat = -1;
			if (c.totalLevel > 750) {
				PlayerHandler.executeGlobalMessage("[@blu@REVERT@bla@]@red@ " + c.getDisplayName() + " @bla@has just reverted to a normie account!");
				}
			break;

		case 656:
			c.setRevertModeDelay(0L);
			sendNpcChat("Your request to have your mode reverted has been cancelled.");
			c.nextChat = -1;
			break;

		case 657:
			sendStatement("This scroll will reset your defence to level 1.",
					"This action cannot be undone. You cannot be wearing any items.",
					"The scroll is only good for one usage.",
					"Are you sure you want to reset your defence to level 1?");
			c.nextChat = 658;
			break;

		case 658:
			sendOptions("Yes, reset defence to level 1.", "No");
			c.dialogueAction = 134;
			break;

		case 659:
			sendNpcChat("Iron man accounts cannot trade, stake or pickup items",
					"that do not belong to them. They do not gain experience",
					"in pvp combat. They must deal 99% of the damage dealt",
					"to an npc to get the drop. They do not get drops from pvp.");
			c.nextChat = 660;
			break;

		case 660:
			sendNpcChat("Ultimate Iron Man accounts have all the restrictions of",
					"regular Iron man accounts with the exception of a few",
					"other limitations. They cannot use the banking feature",
					"and when they die they lose all items except 'untradables'.");
			c.nextChat = -1;
			break;

		case 662:
			sendNpcChat("You need at least 250 coins to fill a bucket", "of compost.");
			c.nextChat = -1;
			break;

		case 663:
			sendNpcChat("You need an empty bucket to fill it with compost.");
			c.nextChat = -1;
			break;

		case 664:
			if (c.getArenaPoints() <= 0) {
				sendNpcChat("Hello, how can I help you?");
			} else {
				sendNpcChat("Hello, you're doing pretty good against those mages.",
						"You've accumulated " + c.getArenaPoints() + " arena points.",
						"Is there anything else I can help with?");
			}
			c.nextChat = 665;
			break;

		case 665:
			sendOptions("Teleport to Mage Arena", "Open store", "How does it work?", "Nothing");
			c.dialogueAction = 136;
			break;

		case 666:
			sendNpcChat("You must enter the arena and kill as many", "opposing mage's as you can. Killing a mage",
					"will provide you with 1 arena point.", "You can only engage in magic vs magic combat.");
			c.nextChat = 665;
			break;

		case 667:
			sendNpcChat("After many hours spent, you have learned how to", "successfully create a slayer helmet.");
			c.nextChat = -1;
			break;

		case 668:
			sendNpcChat("After many hours spent, you have learned how to",
					"successfully create a slayer helmet (imbued).");
			c.nextChat = -1;
			break;

		case 669:
			sendNpcChat("How can I help you?");
			c.nextChat = 670;
			break;

		case 670:
			sendOptions("Open bank", "Set bank pin.", "Nevermind.");
			c.dialogueAction = 137;
			break;

		case 671:
			sendNpcChat("Welcome! Would you like to view the", "");
			c.nextChat = 674;
			break;

		case 672:
			sendOptions("Yes, show me the tutorial.", "No thanks, I know what I'm doing.");
			c.dialogueAction = 673;
			break;

		case 673:
			sendNpcChat("Welcome to " + Configuration.SERVER_NAME + "!", "Here is our home area!",
					"Also, don't forget to join our ::discord!");
			c.nextChat = 674;
			break;

		case 674:
			c.getPA().movePlayer(3080, 3509, 0);
			sendNpcChat("Here you can find all the shops needed", "when you first start out! You can buy combat gear,",
					"foods and pots, or show off your fashion skills!");
			c.nextChat = 675;
			break;
		case 675:
			c.getPA().movePlayer(3075, 3504, 0);
			sendNpcChat("This is the @red@Fire of Exchange@bla@.", "Use items on it to earn exchange points",
					"which can be used to buy Mystery Boxes!");
			c.nextChat = 676;
			break;
		case 676:
			c.getPA().movePlayer(3079, 3493, 0);
			sendNpcChat("Slayer Tasks and Boss points shop is found here.", "If you would like to do Wilderness Slayer",
					"head directly north-east and talk to Krystillia!");
			c.nextChat = 677;
			break;

		case 677:
			c.getPA().movePlayer(3107, 3498, 0);
			sendNpcChat("This is the Outlast Portal.", "Anybody can join, any level, or game mode!",
					"Use the Quest Tab to see when the next", "event will happen!");
			c.nextChat = 678;
			break;

		case 678:
			c.getPA().movePlayer(3099, 3505, 0);
			sendNpcChat("Here is the vote chest!", "After voting 10 times you get a @yel@vote key@bla@!",
					"Check out '@red@::chestrewards@bla@' to see what you can get!");
			c.nextChat = 680;
			break;
		case 680:
			c.getPA().movePlayer(3083, 3496, 0);
			sendNpcChat("Here is the corrupt chest!", "There are 2 Wildy bosses that can be killed for keys!",
					"Check out '@red@::chestrewards@bla@' to see what you can get!");
			c.nextChat = 681;
			break;
		case 681:
			c.getPA().movePlayer(3084, 3476, 0);
			sendNpcChat("If you decide to be a restricted game mode", "you can use the shops here!");
			c.nextChat = 679;
			break;
		case 679:
			c.getPA().movePlayer(3083, 3488, 0);
			sendNpcChat("Finally, change your character style here!", "And don't forget you can select any teleport",
					"in your mage book to open the Teleport Menu!");
			c.nextChat = 645;
			break;

		case 11824:
			c.getDH().sendOption2("Yes, I want to create a Zamorakian hasta for 10m.", "Never mind");
			c.dialogueAction = 11824;
			break;

		case 11889:
			c.getDH().sendOption2("Yes, I want to create a Zamorakian spear for 5m.", "Never mind");
			c.dialogueAction = 11889;
			break;

		/*
		 * Wise old man
		 */
		case 710:
			sendNpcChat("How can I help you?");
			c.nextChat = 711;
			break;

		case 711:
			c.getDH().sendOption5("How do I teleport?", "Are there any rules I must follow?",
					"How do I find my slayer tasks?", "What do I do with my pkp and vote tickets?", "Nothing..");
			c.dialogueAction = 711;
			break;

		case 712:
			sendNpcChat("To teleport through the world simply clicking a teleport",
					"icon in your spellbook. Depending on the", "icon you click, that catergory of teleports",
					"will be shown in the interface.");
			c.nextChat = 710;
			break;

		case 713:
			sendNpcChat("Yes, you must follow rules of this world.", "Make sure you are aware of them! You can",
					"find and read them by typing ::rules");
			c.nextChat = 710;
			break;

		case 714:
			sendNpcChat("To simply get a clue of where your current slayer task",
					"is, speak to a slayer master and they", "will be kind enough to tell you!");
			c.nextChat = 710;
			break;

		case 715:
			sendNpcChat("If you have been lucky enough to get your hands on",
					"one of those precious tickets, you can exchange",
					"them into points by using the Exchange Handler right", "inside the bank of edgeville!");
			c.nextChat = 710;
			break;
		/*
		 * Teleports Reworked by Ryan Makes it easier to add new ones. Better
		 * organization
		 */
		case 400:
			sendOption5("Chickens", "Cows", "Rock Crabs", "Yaks", "@blu@Next Page");
			c.teleAction = 15;
			break;
		case 399:
			sendOption5("Crash Island", "Bob's Island", "Slayer Tower", "", "");
			c.teleAction = 99;
			break;
		case 401:
			sendOption5("@cr22@West Dragons", "@cr22@East Dragons", "@cr22@Dwarf Cave", "@cr22@Lava Dragons",
					"@cr22@Mage Bank");
			c.teleAction = 1;
			break;

		case 402:
			sendOption5("Raids", "Pest Control", "Fight Caves", "Warriors Guild", "@blu@Next Page");
			c.teleAction = 200;
			break;

		case 403:
			sendOption5("Duel Arena", "Shayzien Assault", "Barrows", "", "");
			c.teleAction = 201;
			break;

		case 404:
			sendOption5("@cr22@King Black Dragon", "@cr22@Chaos Elemental", "God Wars Dungeon", "Zulrah",
					"@blu@Next Page");
			c.teleAction = 3;
			break;

		case 405:
			sendOption5("Dagannoth Kings", "Barrelchest", "@cr22@Callisto", "Lizardman Shaman", "@blu@Next Page");
			c.teleAction = 33;
			break;

		case 406:
			sendOption5("@cr22@Vet'ion", "@cr22@Venenatis", "@cr22@Scorpia", "@cr22@Crazy archaeologist",
					"@cr22@Chaos Fanatic");
			c.teleAction = 3434;
			break;

		// case 407: //More Bosses

		case 407:
			sendOption5("Lands End", "Skillers Cove", "Woodcutting Guild", "Farming Patches", "");
			c.teleAction = 1337;
			break;

		// case 408: //More Skilling

		case 409:
			c.getDH().sendOption5("Brimhaven Dungeon", "Taverly Dungeon", "Relleka Dungeon", "Stronghold Slayer Cave",
					"Asgarnian Ice Dungeon");
			c.teleAction = 2;
			break;

		case 7286:
			c.getDH().sendOption2("@red@Fight Skotizo?", "Yes, I understand I lose my totem and I might die.",
					"I need to prepare some more.");
			c.dialogueAction = 7286;
			break;

		case 121312: // city
			c.getDH().sendOption5("Varrock", "Falador", "Lumbridge", "Karamja", "@blu@Next Page");
			c.teleAction = 147258;
			break;

		case 121313: // city
			c.getDH().sendOption5("Ardougne", "Camelot", "Mortania", "Catherby", "Relleka");
			c.teleAction = 258369;
			break;

		}
	}

	public void sendStatement(String line1, String line2) {
		c.getPA().sendString(line1, 360);
		c.getPA().sendString(line2, 361);
		c.getPA().sendChatboxInterface(359);
	}

	private void sendStatement(String line1, String line2, String line3) {
		c.getPA().sendString(line1, 364);
		c.getPA().sendString(line2, 365);
		c.getPA().sendString(line3, 366);
		c.getPA().sendChatboxInterface(363);
	}

	private void sendStatement(String line1, String line2, String line3, String line4) {
		c.getPA().sendString(line1, 369);
		c.getPA().sendString(line2, 370);
		c.getPA().sendString(line3, 371);
		c.getPA().sendString(line4, 372);
		c.getPA().sendChatboxInterface(368);
	}

	public void sendStatement(String line1, String line2, String line3, String line4, String line5) {
		c.getPA().sendString(line1, 375);
		c.getPA().sendString(line2, 376);
		c.getPA().sendString(line3, 377);
		c.getPA().sendString(line4, 378);
		c.getPA().sendString(line5, 379);
		c.getPA().sendChatboxInterface(374);
	}

	/*
	 * Information Box
	 */

	private void sendStartInfo(String text, String text1, String text2, String text3, String title) {
		c.getPA().sendFrame126(title, 6180);
		c.getPA().sendFrame126(text, 6181);
		c.getPA().sendFrame126(text1, 6182);
		c.getPA().sendFrame126(text2, 6183);
		c.getPA().sendFrame126(text3, 6184);
		c.getPA().sendChatboxInterface(6179);
	}

	/*
	 * Options
	 */

	public void sendOption(String s) {
		c.getPA().sendFrame126("Select an Option", 2470);
		c.getPA().sendFrame126(s, 2471);
		c.getPA().sendFrame126("Click here to continue", 2473);
		c.getPA().sendChatboxInterface(13758);
	}

	public void sendOption2(String s, String s1) {
		c.dialogueOptions = 2;
		c.getPA().sendFrame126("Select an Option", 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().sendChatboxInterface(2459);
	}

	public void sendOption2(String title, String s, String s1) {
		c.dialogueOptions = 2;
		c.getPA().sendFrame126(title, 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().sendChatboxInterface(2459);
	}

	private void sendOption3(String s, String s1, String s2) {
		c.dialogueOptions = 3;
		c.getPA().sendFrame126("Select an Option", 2470);
		c.getPA().sendFrame126(s, 2471);
		c.getPA().sendFrame126(s1, 2472);
		c.getPA().sendFrame126(s2, 2473);
		c.getPA().sendChatboxInterface(2469);
	}

	public void sendOption4(String s, String s1, String s2, String s3) {
		c.dialogueOptions = 4;
		c.getPA().sendFrame126("Select an Option", 2481);
		c.getPA().sendFrame126(s, 2482);
		c.getPA().sendFrame126(s1, 2483);
		c.getPA().sendFrame126(s2, 2484);
		c.getPA().sendFrame126(s3, 2485);
		c.getPA().sendChatboxInterface(2480);
	}

	public void sendOption5(String s, String s1, String s2, String s3, String s4) {
		c.dialogueOptions = 5;
		c.getPA().sendFrame126("Select an Option", 2493);
		c.getPA().sendFrame126(s, 2494);
		c.getPA().sendFrame126(s1, 2495);
		c.getPA().sendFrame126(s2, 2496);
		c.getPA().sendFrame126(s3, 2497);
		c.getPA().sendFrame126(s4, 2498);
		c.getPA().sendChatboxInterface(2492);
	}

	private void sendStartInfo(String text, String text1, String text2, String text3, String title, boolean send) {
		c.getPA().sendFrame126(title, 6180);
		c.getPA().sendFrame126(text, 6181);
		c.getPA().sendFrame126(text1, 6182);
		c.getPA().sendFrame126(text2, 6183);
		c.getPA().sendFrame126(text3, 6184);
		c.getPA().sendChatboxInterface(6179);
	}

	/*
	 * Statements
	 */

	public void sendStatement(String s) { // 1 line click here to continue chat
											// box interface
		c.getPA().sendFrame126(s, 357);
		c.getPA().sendFrame126("Click here to continue", 358);
		c.getPA().sendChatboxInterface(356);
	}

	public void sendItemStatement(String text, int item) {
		c.getPA().sendFrame126(text, 308);
		c.getPA().sendFrame246(307, 200, item);
		c.getPA().sendChatboxInterface(306);
	}

	/*
	 * Npc Chatting
	 */


	public void sendNpcChat1(String s, int ChatNpc, String name) {
		//c.getPA().sendFrame200(4883, 591);
		c.getPA().sendFrame126(name, 4884);
		c.getPA().sendFrame126(s, 4885);
		c.getPA().sendNpcHeadOnInterface(ChatNpc, 4883);
		sendNpcDialogueAnimation(4885, DialogueExpression.SPEAKING_CALMLY);
		c.getPA().sendChatboxInterface(4882);
	}

	private void sendOptions(String... options) {
		if (options.length < 2) {
			return;
		}
		if (Arrays.stream(options).anyMatch(Objects::isNull)) {
			return;
		}
		switch (options.length) {
		case 2:
			sendOption2(options[0], options[1]);
			c.dialogueOptions = 2;
			break;
		case 3:
			sendOption3(options[0], options[1], options[2]);
			c.dialogueOptions = 3;
			break;
		case 4:
			sendOption4(options[0], options[1], options[2], options[3]);
			c.dialogueOptions = 4;
			break;
		case 5:
			sendOption5(options[0], options[1], options[2], options[3], options[4]);
			c.dialogueOptions = 5;
			break;
		}
	}

	public void sendNpcChat(String... messages) {
		String name = WordUtils.capitalize(NpcDef.forId(c.talkingNpc).getName());

		switch (messages.length) {
		case 1:
			sendNpcChat1(messages[0], c.talkingNpc, name);
			break;

		case 2:
			sendNpcChat2(messages[0], messages[1], c.talkingNpc, name);
			break;

		case 3:
			sendNpcChat3(messages[0], messages[1], messages[2], c.talkingNpc, name);
			break;

		case 4:
			sendNpcChat4(messages[0], messages[1], messages[2], messages[3], c.talkingNpc, name);
			break;
		}
	}

	public void sendStatement(String... messages) {
		switch (messages.length) {
		case 1:
			sendStatement(messages[0]);
			break;

		case 2:
			sendStatement(messages[0], messages[1]);
			break;

		case 3:
			sendStatement(messages[0], messages[1], messages[2]);
			break;

		case 4:
			sendStatement(messages[0], messages[1], messages[2], messages[3]);
			break;

		case 5:
			sendStatement(messages[0], messages[1], messages[2], messages[3], messages[4]);
			break;
		}
	}

	public void sendNpcChat2(String s, String s1, int ChatNpc, String name) {
		//c.getPA().sendFrame200(4888, 591);
		c.getPA().sendFrame126(name, 4889);
		c.getPA().sendFrame126(s, 4890);
		c.getPA().sendFrame126(s1, 4891);
		c.getPA().sendNpcHeadOnInterface(ChatNpc, 4888);
		sendNpcDialogueAnimation(4890, DialogueExpression.SPEAKING_CALMLY);
		c.getPA().sendChatboxInterface(4887);
	}

	public void sendNpcChat3(String s, String s1, String s2, int ChatNpc, String name) {
		//c.getPA().sendFrame200(4894, 591);
		c.getPA().sendFrame126(name, 4895);
		c.getPA().sendFrame126(s, 4896);
		c.getPA().sendFrame126(s1, 4897);
		c.getPA().sendFrame126(s2, 4898);
		c.getPA().sendNpcHeadOnInterface(ChatNpc, 4894);
		sendNpcDialogueAnimation(4896, DialogueExpression.SPEAKING_CALMLY);
		c.getPA().sendChatboxInterface(4893);
	}

	public void sendNpcChat4(String s, String s1, String s2, String s3, int ChatNpc, String name) {
		//c.getPA().sendFrame200(4901, 591);
		c.getPA().sendFrame126(name, 4902);
		c.getPA().sendFrame126(s, 4903);
		c.getPA().sendFrame126(s1, 4904);
		c.getPA().sendFrame126(s2, 4905);
		c.getPA().sendFrame126(s3, 4906);
		c.getPA().sendNpcHeadOnInterface(ChatNpc, 4901);
		sendNpcDialogueAnimation(48903, DialogueExpression.SPEAKING_CALMLY);
		c.getPA().sendChatboxInterface(4900);
	}

	/*
	 * Player Chating Back
	 */

	private void sendPlayerChat1(String s) {
		//c.getPA().sendFrame200(969, 591);
		c.getPA().sendFrame126(c.getDisplayName(), 970);
		c.getPA().sendFrame126(s, 971);
		c.getPA().sendPlayerHeadOnInterface(969);
		c.getPA().sendChatboxInterface(968);
	}

	private void sendPlayerChat2(String s, String s1) {
		//c.getPA().sendFrame200(974, 591);
		c.getPA().sendFrame126(c.getDisplayName(), 975);
		c.getPA().sendFrame126(s, 976);
		c.getPA().sendFrame126(s1, 977);
		c.getPA().sendPlayerHeadOnInterface(974);
		c.getPA().sendChatboxInterface(973);
	}

	private void sendPlayerChat3(String s, String s1, String s2) {
		//c.getPA().sendFrame200(980, 591);
		c.getPA().sendFrame126(c.getDisplayName(), 981);
		c.getPA().sendFrame126(s, 982);
		c.getPA().sendFrame126(s1, 983);
		c.getPA().sendFrame126(s2, 984);
		c.getPA().sendPlayerHeadOnInterface(980);
		c.getPA().sendChatboxInterface(979);
	}

	public void sendPlayerChat4(String s, String s1, String s2, String s3) {
		//c.getPA().sendFrame200(987, 591);
		c.getPA().sendFrame126(c.getDisplayName(), 988);
		c.getPA().sendFrame126(s, 989);
		c.getPA().sendFrame126(s1, 990);
		c.getPA().sendFrame126(s2, 991);
		c.getPA().sendFrame126(s3, 992);
		c.getPA().sendPlayerHeadOnInterface(987);
		c.getPA().sendChatboxInterface(986);
	}

	public void sendNpcDialogueAnimation(int interfaceId, DialogueExpression expression) {
//		int headChildId = interfaceId - 2;
//		c.getPA().sendInterfaceAnimation(headChildId, expression.getAnimation());
	}
}
