package io.xeros.net.packets.itemoptions;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.bosspoints.JarsToPoints;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.displayname.ChangeDisplayName;
import io.xeros.content.items.PvpWeapons;
import io.xeros.content.items.RottenPotato;
import io.xeros.content.items.item_combinations.Godswords;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.skills.crafting.BryophytaStaff;
import io.xeros.content.skills.hunter.impling.Impling;
import io.xeros.content.skills.runecrafting.Pouches;
import io.xeros.content.teleportation.TeleportTablets;
import io.xeros.model.Items;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.net.packets.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

/**
 * Item Click 2 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class ItemOptionTwo implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().isLocked())
			return;
		player.interruptActions();
		int itemId = player.getInStream().readUnsignedWord();

		if (player.debugMessage)
			player.sendMessage(String.format("ItemClick[item=%d, option=%d, interface=%d, slot=%d]", itemId, 2, -1, -1));

		if (player.getLock().cannotClickItem(player, itemId))
			return;
		if (!player.getItems().playerHasItem(itemId, 1))
			return;
		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}

		var itemActionManager = Server.getItemOptionActionManager();
		if (itemActionManager.findHandlerById(itemId).isPresent()) {
			var npcAction = itemActionManager.findHandlerById(itemId).get();
			if (npcAction.performedAction(player, itemId, -1, -1, 2))
				return;
			logger.error("Unhandled Item Action 2: {} ", npcAction.getClass().getSimpleName());
		}
		if (itemId == Items.ROTTEN_POTATO && RottenPotato.getInstance().forPlayer(player).peel())
			return;
		if (LootingBag.isLootingBag(itemId)) {
			player.getLootingBag().openDepositMode();
			return;
		}
		if (Misc.isInDuelSession(player)) return;

		if (JarsToPoints.open(player, itemId)) {
			return;
		}

		if (BryophytaStaff.handleItemOption(player, itemId, 2))
			return;

		if (SanguinestiStaff.clickItem(player, itemId, 2)) {
			return;
		}

		if (ChangeDisplayName.clickChangeNameItem(player, itemId))
			return;

		TeleportTablets.operate(player, itemId);
		ItemDef def = ItemDef.forId(itemId);
		switch (itemId) {
			case 12885:
			case 13277:
			case 19701:
			case 13245:
			case 12007:
			case 22106:
			case 12936:
			case 24495:
			player.getDH().sendDialogues(361, Npcs.BOSS_POINT_SHOP);
			break;
		case 21183:
			player.sendMessage("Your bracelet of slaughter has @red@"+ player.slaughterCharge +"@bla@ charges left.");
			break;
		case 20714:
			int pages = player.getTomeOfFire().getPages();
			int charges = player.getTomeOfFire().getCharges();
			player.sendMessage("You currently have "+ pages +" pages and " + charges + " charges left in your tome of fire.");
			break;
		case 21817:
			 player.getItems().deleteItem(21817, 1);
			 player.getItems().addItem(21820, 250);
			 player.getItems().addItem(995, 50000);
			break;
		case 21816:
		 if (player.getItems().freeSlots() < 2) {
			 player.sendMessage("You need at least two free slots to use this command.");
             return;
         }
		if (player.braceletEtherCount <= 0) {
			player.getItems().deleteItem(21816, 1);
			player.getItems().addItem(21817, 1);
			return;
		}
		player.getItems().addItem(21820, player.braceletEtherCount);
		player.braceletDecrease(player.braceletEtherCount);
		player.sendMessage("@blu@You have removed @red@"+player.braceletEtherCount +"@blu@ ether into your inventory");
		if (player.braceletEtherCount <= 0) {
			player.getItems().deleteItem(21816, 1);
			player.getItems().addItem(21817, 1);
			return;
		}
		break;
		case 7509:
            if (player.getPosition().inDuelArena() || Boundary.isIn(player, Boundary.DUEL_ARENA)) {
            	player.sendMessage("You cannot do this here.");
                return;
            }
            if (player.getHealth().getStatus().isPoisoned() || player.getHealth().getStatus().isVenomed()) {
            	player.sendMessage("You are effected by venom or poison, you should cure this first.");
                return;
            }
            if (player.getHealth().getCurrentHealth() <= 10 && player.getHealth().getCurrentHealth() > 1 ) {
                player.appendDamage(1, Hitmark.HIT);
                player.forcedChat("URGHHHHH!");
                return;
            }
            if (player.getHealth().getCurrentHealth() <= 1) {
            	player.sendMessage("I better not do that.");
                return;
            }
            player.forcedChat("URGHHHHH!");
            player.startAnimation(829);
			player.getPA().sendSound(1018);
            int currentHealth = player.getHealth().getCurrentHealth() / 10;
            player.appendDamage(currentHealth, Hitmark.HIT);
            break;
		case 9762:
			if (!Boundary.isIn(player, Boundary.EDGEVILLE_PERIMETER)) {
				player.sendMessage("This cape can only be operated within the edgeville perimeter.");
				return;
			}
			if (player.getPosition().inWild()) {
				return;
			}
				if (player.playerMagicBook == 0) {
					player.playerMagicBook = 1;
					player.setSidebarInterface(6, 838);
					player.autocasting = false;
					player.sendMessage("An ancient wisdomin fills your mind.");
					player.getPA().resetAutocast();
				} else if (player.playerMagicBook == 1) {
					player.sendMessage("You switch to the lunar spellbook.");
					player.setSidebarInterface(6, 29999);
					player.playerMagicBook = 2;
					player.autocasting = false;
					player.autocastId = -1;
					player.getPA().resetAutocast();
				} else if (player.playerMagicBook == 2) {
					player.setSidebarInterface(6, 938);
					player.playerMagicBook = 0;
					player.autocasting = false;
					player.sendMessage("You feel a drain on your memory.");
					player.autocastId = -1;
					player.getPA().resetAutocast();
				}
				break;
		/*case 10556:
        	player.sendMessage("@red@Attacker Icon gives 10% on max melee hit.");
        	break;
        case 10557:
        	player.sendMessage("@red@Collector Icon gives 7% chance boost on pets.");
        	break;
        case 10558:
        	player.sendMessage("@red@Defender Icon will reduce protect prayers by 5%.");
        	break;
        case 10559:
        	player.sendMessage("@red@Collector Icon shares the same effects as guthans.");
        	break;*/
		case 10832:
			player.getCoinBagSmall().openall();
			break;
		case 10833:
			player.getCoinBagMedium().openall();
			break;
		case 10834:
			player.getCoinBagLarge().openall();
			break;
		case 10835:
			player.getCoinBagBuldging().openall();
			break;
		case 9780:
			player.getPA().spellTeleport(3810, 3550, 0, false);
			player.sendMessage("You have teleported to the Crafting Shop.");
			break;

		case Items.VIGGORAS_CHAINMACE: // Checking charges for pvp weapons
		case Items.THAMMARONS_SCEPTRE:
		case Items.CRAWS_BOW:
			PvpWeapons.handleItemOption(player, itemId, 2);
			break;

		case 6199:
		case 6828:
		case 13346:
			//player.getPA().sendFrame126("https://www.sovark.com/topic/23-mystery-box-drop-tables/", 12000);
			break;
		case 21347:
			player.boltTips = false;
			player.arrowTips = true;
			player.javelinHeads = false;
			player.sendMessage("Your Amethyst method is now Arrowtips!");
			break;

			case 13660:
				if(player.wildLevel > Configuration.NO_TELEPORT_WILD_LEVEL) {
					player.sendMessage("You can't teleport above " + Configuration.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
					return;
				}
				player.getTeleportInterface().openInterface();
				return;
		case 11238:
		case 11240:
		case 11242:
		case 11244:
		case 11246:
		case 11248:
		case 11250:
		case 11252:
		case 11254:
		case 11256:
		case 19732:
			Impling.getReward(player, itemId);
			break;
		case 20164: //Spade
		case 20243:
				if (System.currentTimeMillis() - player.lastPerformedEmote < 2500)
					return;			
				player.startAnimation(7268);
				player.lastPerformedEmote = System.currentTimeMillis();
			break;
		
		case 13136:
			if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
				player.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
				return;
			}
			if (Server.getMultiplayerSessionListener().inAnySession(player)) {
				player.sendMessage("You cannot do that right now.");
				return;
			}
			if (player.wildLevel > Configuration.NO_TELEPORT_WILD_LEVEL) {
				player.sendMessage("You can't teleport above level " + Configuration.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
				return;
			}
			player.getPA().spellTeleport(3426, 2927, 0, false);
			break;
		
		case 13117:
			if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
				if (player.getRechargeItems().useItem(itemId)) {
					player.getRechargeItems().replenishPrayer(4);
				}
			} else {
				player.sendMessage("You already have full prayer points.");
				return;
			}
			break;
		case 13118:
			if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
				if (player.getRechargeItems().useItem(itemId)) {
					player.getRechargeItems().replenishPrayer(2);
				}
			} else {
				player.sendMessage("You already have full prayer points.");
				return;
			}
			break;
		case 13119:
		case 13120:
			if (player.playerLevel[5] < player.getPA().getLevelForXP(player.playerXP[5])) {
				if (player.getRechargeItems().useItem(itemId)) {
					player.getRechargeItems().replenishPrayer(1);
				}
			} else {
				player.sendMessage("You already have full prayer points.");
				return;
			}
			break;
			
		case 13111:
			if (player.getRechargeItems().useItem(itemId)) {
				player.getPA().spellTeleport(3236, 3946, 0, false);
			}
			break;

			case Items.COMPLETIONIST_CAPE:
			case 13280:
			case 13329:
			case 13337:
			case 21898:
			case 13331:
			case 13333:
			case 13335:
			case 20760:
			case 21285:
			case 21776:
			case 21778:
			case 21780:
			case 21782:
			case 21784:
			case 21786:
			player.getDH().sendDialogues(76, 1);
			break;

			case 11802:
			case 11804:
			case 11806:
			case 11808:
				Godswords.dismantle(player,itemId);
				break;

		case 13226:
			player.getHerbSack().withdrawAll();
			break;
			
		case 12020:
			player.getGemBag().check();
			break;
			
		case 5509:
			Pouches.check(player, 0);
			break;
		case 5510:
			Pouches.check(player, 1);
			break;
		case 5512:
			Pouches.check(player, 2);
			break;
		case 12904:
			player.sendMessage("The toxic staff of the dead has " + player.getToxicStaffOfTheDeadCharge() + " charges remaining.");
			break;
		case 13199:
		case 13197:
			player.sendMessage("The " + def.getName() + " has " + player.getSerpentineHelmCharge() + " charges remaining.");
			break;
		case 11907:
		case 12899:
			int charge = itemId == 11907 ? player.getTridentCharge() : player.getToxicTridentCharge();
			player.sendMessage("The " + def.getName() + " has " + charge + " charges remaining.");
			break;
		case 12926:
			player.getCombatItems().checkBlowpipeShotsRemaining();
			break;

		case 12931:
			def = ItemDef.forId(itemId);
			if (def == null) {
				return;
			}
			player.sendMessage("The " + def.getName() + " has " + player.getSerpentineHelmCharge() + " charge remaining.");
			break;
		case 8901:
			player.getPA().assembleSlayerHelmet();
			break;
		case 19675:
			//if (player.getArcLightCharge() >= 1000) {
			//	player.getItems().addItem(19677, 3);
			//	player.getDH().sendStatement("You lost some of the ancient shards in the process!");
			//} else {
			//	player.getDH().sendStatement("Your Arclight's charge was too low to refund shards.");
			//}
		//	player.getItems().deleteItem(19675, 1);
		//	player.getItems().addItem(6746, 1);
		//	player.setArcLightCharge(0);
			player.getDH().sendItemStatement("Your arc light has "+player.getArcLightCharge()+ " charges.",19675);
			break;
		case 11283:
		case 11285:
		case 11284:
			player.sendMessage("Your dragonfire shield currently has " + player.getDragonfireShieldCharge() + " charges.");
			break;
		case 4155:
			player.sendMessage("You currently have <col=a30027>" + Misc.insertCommas(player.getSlayer().getPoints()) + " </col>slayer points.");
			break;
		default:
			if (player.getRights().isOrInherits(Right.OWNER)) {
				Misc.println("[DEBUG] Item Option #2-> Item id: " + itemId);
			}
			break;
		}

	}

}
