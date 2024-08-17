package io.xeros.net.packets;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.combat.CombatItems;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.effects.damageeffect.impl.DragonfireShieldEffect;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.items.Degrade;
import io.xeros.content.skills.crafting.BryophytaStaff;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import org.apache.commons.lang3.text.WordUtils;

public class OperateItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		int slot = c.getInStream().readSignedWordA(); // the row of the action
		int itemId = c.getInStream().readUnsignedWord(); //the item's id

		if(c.debugMessage) {
			c.sendMessage("Operate Item - itemId: " + itemId + " slot: " + slot);
		}

		ItemDef def = ItemDef.forId(itemId);
			Optional<Degrade.DegradableItem> d = Degrade.DegradableItem.forId(itemId);
			if (d.isPresent()) {
				Degrade.checkPercentage(c, itemId);
				return;
			}

			if (CombatItems.SLAYER_HELMETS_REGULAR.contains(itemId) || CombatItems.SLAYER_HELMETS_IMBUED.contains(itemId)) {
				switch (slot) {
					case 1:
						if (!c.getSlayer().getTask().isPresent()) {
							c.sendMessage("You do not have a task!");
							return;
						}
						c.sendMessage("I currently have @blu@" + c.getSlayer().getTaskAmount() + " " + c.getSlayer().getTask().get().getPrimaryName() + "@bla@ to kill.");
						c.getPA().closeAllWindows();
						break;
					case 2:
						if (Server.getMultiplayerSessionListener().inAnySession(c)) {
							return;
						}
//				c.getDH().sendDialogues(12000, -1);
						c.getPA().resetQuestInterface();
						int[] frames = { 8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159, 8160, 8161, 8162, 8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173,
								8174, 8175, 8176, 8177, 8178, 8179, 8180, 8181, 8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193, 8194 };
						c.getPA().sendFrame126("@dre@Kill Tracker for @blu@" + c.getDisplayName() + "", 8144);
						c.getPA().sendFrame126("", 8145);
						c.getPA().sendFrame126("@blu@Total kills@bla@ - " + c.getNpcDeathTracker().getTotal() + "", 8147);
						c.getPA().sendFrame126("", 8148);
						int frameIndex = 0;
						for (Map.Entry<String, Integer> entry : c.getNpcDeathTracker().getTracker().entrySet()) {
							if (entry == null) {
								continue;
							}
							if (frameIndex > frames.length - 1) {
								break;
							}
							if (entry.getValue() > 0) {
								c.getPA().sendFrame126("@blu@" + WordUtils.capitalize(entry.getKey().toLowerCase()) + ": @red@" + entry.getValue(), frames[frameIndex]);
								frameIndex++;
							}
						}
						c.getPA().openQuestInterface();
						break;
				}

				return;
			}

		if (BryophytaStaff.handleItemOption(c, itemId, 2))
			return;

			switch (itemId) {
			case Items.SANGUINESTI_STAFF:
				SanguinestiStaff.checkChargesRemaining(c);
				break;
			case 2550:
				c.sendMessage("You have @red@"+ (40 - c.recoilHits) +"@bla@ recoil charges left.");
				break;
			case 21183:
				c.sendMessage("Your bracelet of slaughter has @red@"+ c.slaughterCharge +"@bla@ charges left.");
				break;
			case 9948:
			case 9949:
					c.getPA().spellTeleport(2592, 4321, 0, false);
			case 12904:
				c.sendMessage("The toxic staff of the dead has " + c.getToxicStaffOfTheDeadCharge() + " charges remaining.");
				break;

				case 13660:
					if(c.wildLevel > Configuration.NO_TELEPORT_WILD_LEVEL) {
						c.sendMessage("You can't teleport above " + Configuration.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
						return;
					}
					c.getTeleportInterface().openInterface();
					return;
				case Items.TOME_OF_FIRE_EMPTY:
				case Items.TOME_OF_FIRE:
					int pages = c.getTomeOfFire().getPages();
					int charges = c.getTomeOfFire().getCharges();
					c.sendMessage("You currently have "+ pages +" pages and " + charges + " charges left in your tome of fire.");
					break;

			case 13199:
			case 13197:
				c.sendMessage("The " + def.getName() + " has " + c.getSerpentineHelmCharge() + " charges remaining.");
				break;
			case 19675:
				c.sendMessage("Your Arclight has "+ c.getArcLightCharge() +" charges remaining.");
			break;
			case 11907:
			case 12899:
				int charge = itemId == 11907 ? c.getTridentCharge() : c.getToxicTridentCharge();
				c.sendMessage("The " + def.getName() + " has " + charge + " charges remaining.");
				break;
			case 12926:
				c.getCombatItems().checkBlowpipeShotsRemaining();
				break;
			case 12931:
				def = ItemDef.forId(itemId);
				if (def == null) {
					return;
				}
				c.sendMessage("The " + def.getName() + " has " + c.getSerpentineHelmCharge() + " charge remaining.");
				break;

			case 13125:
			case 13126:
			case 13127:
				if (c.getRunEnergy() < 100) {
					if (c.getRechargeItems().useItem(itemId)) {
						c.getRechargeItems().replenishRun(50);
					}
				} else {
					c.sendMessage("You already have full run energy.");
					return;
				}
				break;

			case 13128:
				if (c.getRunEnergy() < 100) {
					if (c.getRechargeItems().useItem(itemId)) {
						c.getRechargeItems().replenishRun(100);
					}
				} else {
					c.sendMessage("You already have full run energy.");
					return;
				}
				break;

			case 13117:
				if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
					if (c.getRechargeItems().useItem(itemId)) {
						c.getRechargeItems().replenishPrayer(4);
					}
				} else {
					c.sendMessage("You already have full prayer points.");
					return;
				}
				break;
			case 13118:
				if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
					if (c.getRechargeItems().useItem(itemId)) {
						c.getRechargeItems().replenishPrayer(2);
					}
				} else {
					c.sendMessage("You already have full prayer points.");
					return;
				}
				break;
			case 13119:
			case 13120:
				if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
					if (c.getRechargeItems().useItem(itemId)) {
						c.getRechargeItems().replenishPrayer(1);
					}
				} else {
					c.sendMessage("You already have full prayer points.");
					return;
				}
				break;
			case 13111:
				if (c.getRechargeItems().useItem(itemId)) {
					c.getPA().spellTeleport(3236, 3946, 0, false);
				}
				break;
			case 10507:
				if (c.getItems().isWearingItem(10507)) {
					if (System.currentTimeMillis() - c.lastPerformedEmote < 2500)
						return;
					c.startAnimation(6382);
					c.gfx0(263);
					c.lastPerformedEmote = System.currentTimeMillis();
				}
				break;

			case 20243:
					if (System.currentTimeMillis() - c.lastPerformedEmote < 2500)
						return;
					c.startAnimation(7268);
					c.lastPerformedEmote = System.currentTimeMillis();
				break;
			case 2572:
			case 12785:
				if (c.collectCoins == true) {
					c.collectCoins = false;
					c.sendMessage("@blu@You have now @red@Disabled@blu@ your coin collecting.");
			} else if (c.collectCoins == false) {
					c.collectCoins = true;
					c.sendMessage("@blu@You have now @gre@Enabled@blu@ your coin collecting.");
			}
			break;
			case 4212:
			case 4214:
			case 4215:
			case 4216:
			case 4217:
			case 4218:
			case 4219:
			case 4220:
			case 4221:
			case 4222:
			case 4223:
				c.sendMessage("You currently have " + (250 - c.crystalBowArrowCount) + " charges left before degradation to " + (c.playerEquipment[3] == 4223 ? "Crystal seed" : ItemAssistant.getItemName(c.playerEquipment[3] + 1)));
				break;

			case 4202:
			case 9786:
			case 9787:
				PlayerAssistant.ringOfCharosTeleport(c);
				break;

			case 11283:
			case 11284:
				if (Boundary.isIn(c, Boundary.ZULRAH) || Boundary.isIn(c, Boundary.CERBERUS_BOSSROOMS)) {
					return;
				}
				DragonfireShieldEffect dfsEffect = new DragonfireShieldEffect();
				if (c.npcAttackingIndex <= 0 && c.playerAttackingIndex <= 0) {
					return;
				}
				if (c.getHealth().getCurrentHealth() <= 0 || c.isDead) {
					return;
				}
				if (dfsEffect.isExecutable(c)) {
					Damage damage = new Damage(Misc.random(25));
					if (c.playerAttackingIndex > 0) {
						Player target = PlayerHandler.players[c.playerAttackingIndex];
						if (Objects.isNull(target)) {
							return;
						}
						c.attackTimer = 7;
						dfsEffect.execute(c, target, damage);
						c.setLastDragonfireShieldAttack(System.currentTimeMillis());
					} else if (c.npcAttackingIndex > 0) {
						NPC target = NPCHandler.npcs[c.npcAttackingIndex];
						if (Objects.isNull(target)) {
							return;
						}
						c.attackTimer = 7;
						dfsEffect.execute(c, target, damage);
						c.setLastDragonfireShieldAttack(System.currentTimeMillis());
					}
				}
				break;

				/**
				 * Max capes
				 */
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
				c.getDH().sendDialogues(76, 1);
				break;

				/**
				 * Crafting cape
				 */
			case 9780:
			case 9781:
				c.getPA().startTeleport(2936, 3283, 0, "modern", false);
				break;
				/**
				 * Bracelet of etherium 
				 */
			case 21816:
				switch (slot) {
				case 1:
					c.sendMessage("@blu@You have @red@"+c.braceletEtherCount +"@blu@ charges in your bracelet");
					break;
				case 2:
					if (c.absorption == false) {
						c.absorption = true;
						c.sendMessage("@blu@You have @red@Activated @blu@absorption for your bracelet.");
						return;
					} else if (c.absorption == true) {
						c.absorption = false;
						c.sendMessage("@blu@You have @red@Deactivated @blu@absorption for your bracelet.");
					}
					break;
				}
				break;
				
				
				/**
				 * Magic skillcape
				 */
			case 9762:
			case 9763:
				if (Boundary.isIn(c, Boundary.EDGEVILLE_PERIMETER)) {
					if (c.playerMagicBook == 0) {
						c.playerMagicBook = 1;
						c.setSidebarInterface(6, 838);
						c.autocasting = false;
						c.sendMessage("An ancient wisdomin fills your mind.");
						c.getPA().resetAutocast();
					} else if (c.playerMagicBook == 1) {
						c.sendMessage("You switch to the lunar spellbook.");
						c.setSidebarInterface(6, 29999);
						c.playerMagicBook = 2;
						c.autocasting = false;
						c.autocastId = -1;
						c.getPA().resetAutocast();
					} else if (c.playerMagicBook == 2) {
						c.setSidebarInterface(6, 938);
						c.playerMagicBook = 0;
						c.autocasting = false;
						c.sendMessage("You feel a drain on your memory.");
						c.autocastId = -1;
						c.getPA().resetAutocast();
					}
				} else {
					c.sendMessage("You need to be in Edgeville to use this.");
				}
					break;
				case 13136:
					switch (slot) {
						case 1:
							c.getPA().spellTeleport(3484, 9510, 2, false);
							break;
						case 2:
							c.getPA().spellTeleport(3426, 2927, 0, false);
							break;
					}
					break;
				case 1704:
					c.sendMessage("@red@You currently have no charges in your glory.");
					break;
				case 1712:
				case 1710:
				case 1708:
				case 1706:
				case 19707:
					if (c.isTeleblocked()) {
						c.sendMessage("You cannot use your glory as you are teleblocked.");
						return;
					}
					if (c.wildLevel > 30) {
						c.sendMessage("You can't teleport above level 30 in the wilderness.");
						c.getPA().closeAllWindows();
						return;
					}
					switch (slot) {
					case 1:
						if (System.currentTimeMillis() - c.potDelay < 4000) {
						c.sendMessage("@blu@Please wait a few seconds before doing another action.");
						c.getPA().closeAllWindows();
						return;
						}

					 if (c.playerEquipment[Player.playerAmulet] == 1712) { // new
							c.getItems().equipItem(1710, 1, 2);
							c.setAppearanceUpdateRequired(true);
						c.getPA().startTeleport(3088, 3493, 0, "glory",false);
						c.sendMessage("@red@You now have 3 charges left in your glory.");
				
						} else if (c.playerEquipment[Player.playerAmulet] == 1710) { // new
							c.getItems().equipItem(1708, 1, 2);
							c.setAppearanceUpdateRequired(true);
						c.getPA().startTeleport(3088, 3493, 0, "glory",false);
						c.sendMessage("@red@You now have 2 charges left in your glory.");
					
					} else if (c.playerEquipment[Player.playerAmulet] == 1708) { // new
						c.getItems().equipItem(1706, 1, 2);
						c.setAppearanceUpdateRequired(true);
					c.getPA().startTeleport(3088, 3493, 0, "glory",false);
					c.sendMessage("@red@You now have 1 charges left in your glory.");
					
					} else if (c.playerEquipment[Player.playerAmulet] == 1706) { // new
						c.getItems().equipItem(1704, 1, 2);
						c.setAppearanceUpdateRequired(true);
					c.getPA().startTeleport(3088, 3493, 0, "glory",false);
					c.sendMessage("@red@You now have 0 charges left in your glory.");
					 } else if (c.playerEquipment[Player.playerAmulet] == 19707) { // new
						 c.getPA().startTeleport(3088, 3493, 0, "glory",false);
					 }
						break;
					case 2:
						if (System.currentTimeMillis() - c.potDelay < 2500) {
							c.sendMessage("@blu@Please wait a few seconds before doing another action.");
							c.getPA().closeAllWindows();
							return;
							}
						 if (c.playerEquipment[Player.playerAmulet] == 1712) { // new
								c.getItems().equipItem(1710, 1, 2);
								c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(2925, 3173, 0, "glory",false);
							c.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.TELEPORT_TO_KARAMJA);
							c.sendMessage("@red@You now have 3 charges left in your glory.");
					
							} else if (c.playerEquipment[Player.playerAmulet] == 1710) { // new
								c.getItems().equipItem(1708, 1, 2);
								c.setAppearanceUpdateRequired(true);
								c.getPA().startTeleport(2925, 3173, 0, "glory",false);
								c.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.TELEPORT_TO_KARAMJA);
							c.sendMessage("@red@You now have 2 charges left in your glory.");
						
						} else if (c.playerEquipment[Player.playerAmulet] == 1708) { // new
							c.getItems().equipItem(1706, 1, 2);
							c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(2925, 3173, 0, "glory",false);
							c.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.TELEPORT_TO_KARAMJA);
						c.sendMessage("@red@You now have 1 charges left in your glory.");
						
						} else if (c.playerEquipment[Player.playerAmulet] == 1706) { // new
							c.getItems().equipItem(1704, 1, 2);
							c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(2925, 3173, 0, "glory",false);
							c.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.TELEPORT_TO_KARAMJA);
						c.sendMessage("@red@You now have 0 charges left in your glory.");
						 } else if (c.playerEquipment[Player.playerAmulet] == 19707) { // new
							 c.getPA().startTeleport(2925, 3173, 0, "glory",false);
						 }
						break;
					case 3:
						if (System.currentTimeMillis() - c.potDelay < 2500) {
							c.sendMessage("@blu@Please wait a few seconds before doing another action.");
							c.getPA().closeAllWindows();
							return;
							}
						 if (c.playerEquipment[Player.playerAmulet] == 1712) { // new
							c.getItems().equipItem(1710, 1, 2);
							c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(3079, 3250, 0, "glory",false);
							c.sendMessage("@red@You now have 3 charges left in your glory.");
					
							} else if (c.playerEquipment[Player.playerAmulet] == 1710) { // new
								c.getItems().equipItem(1708, 1, 2);
								c.setAppearanceUpdateRequired(true);
								c.getPA().startTeleport(3079, 3250, 0, "glory",false);
							c.sendMessage("@red@You now have 2 charges left in your glory.");
						
						} else if (c.playerEquipment[Player.playerAmulet] == 1708) { // new
							c.getItems().equipItem(1706, 1, 2);
							c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(3079, 3250, 0, "glory",false);
						c.sendMessage("@red@You now have 1 charges left in your glory.");
						
						} else if (c.playerEquipment[Player.playerAmulet] == 1706) { // new
							c.getItems().equipItem(1704, 1, 2);
							c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(3079, 3250, 0, "glory",false);
						c.sendMessage("@red@You now have 0 charges left in your glory.");
						 } else if (c.playerEquipment[Player.playerAmulet] == 19707) { // new
							 c.getPA().startTeleport(3079, 3250, 0, "glory",false);
						 }
						break;
					case 4:
						if (System.currentTimeMillis() - c.potDelay < 2500) {
							c.sendMessage("@blu@Please wait a few seconds before doing another action.");
							c.getPA().closeAllWindows();
							return;
							}
						 if (c.playerEquipment[Player.playerAmulet] == 1712) { // new
								c.getItems().equipItem(1710, 1, 2);
								c.setAppearanceUpdateRequired(true);
								c.getPA().startTeleport(3293, 3176, 0, "glory",false);
							c.sendMessage("@red@You now have 3 charges left in your glory.");
					
							} else if (c.playerEquipment[Player.playerAmulet] == 1710) { // new
								c.getItems().equipItem(1708, 1, 2);
								c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(3293, 3176, 0, "glory",false);
							c.sendMessage("@red@You now have 2 charges left in your glory.");
						
						} else if (c.playerEquipment[Player.playerAmulet] == 1708) { // new
							c.getItems().equipItem(1706, 1, 2);
							c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(3293, 3176, 0, "glory",false);
						c.sendMessage("@red@You now have 1 charges left in your glory.");
						
						} else if (c.playerEquipment[Player.playerAmulet] == 1706) { // new
							c.getItems().equipItem(1704, 1, 2);
							c.setAppearanceUpdateRequired(true);
							c.getPA().startTeleport(3293, 3176, 0, "glory",false);
						c.sendMessage("@red@You now have 0 charges left in your glory.");
						 } else if (c.playerEquipment[Player.playerAmulet] == 19707) { // new
							 c.getPA().startTeleport(3293, 3176, 0, "glory",false);
						 }
						break;
					}
					c.potDelay = System.currentTimeMillis();
					break;

				case 2552:
				case 2554:
				case 2556:
				case 2558:
				case 2560:
				case 2562:
				case 2564:
				case 2566:
					switch (slot) {
						case 1:
							c.getPA().spellTeleport(3370, 3157, 0, false);
							break;
						case 2:
							c.getPA().spellTeleport(2441, 3088, 0, false);
							break;
						case 3:
							c.getPA().spellTeleport(3304, 3130, 0, false);
							break;
					}
					break;

				default:
					c.sendMessage("Nothing interesting happens..");
					break;
			}
		}
	}
