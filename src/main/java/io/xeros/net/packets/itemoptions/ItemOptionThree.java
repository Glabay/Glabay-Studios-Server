package io.xeros.net.packets.itemoptions;

import java.util.Optional;
import java.util.function.Consumer;

import io.xeros.Server;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.dialogue.impl.AmethystChiselDialogue;
import io.xeros.content.item.lootable.impl.NormalMysteryBox;
import io.xeros.content.item.lootable.impl.SuperMysteryBox;
import io.xeros.content.item.lootable.impl.UltraMysteryBox;
import io.xeros.content.items.Degrade;
import io.xeros.content.items.Degrade.DegradableItem;
import io.xeros.content.items.PvpWeapons;
import io.xeros.content.items.RottenPotato;
import io.xeros.content.items.TomeOfFire;
import io.xeros.content.items.pouch.RunePouch;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.skills.crafting.BryophytaStaff;
import io.xeros.content.teleportation.TeleportTablets;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.net.packets.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

/**
 * Item Click 3 Or Alternative Item Option 1
 *
 * @author Ryan / Lmctruck30
 * <p>
 * Proper Streams
 */

public class ItemOptionThree implements PacketType {


    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        if (c.getMovementState().isLocked())
            return;
        c.interruptActions();
        int itemId11 = c.getInStream().readSignedWordBigEndianA();
        int itemId1 = c.getInStream().readSignedWordA();
        int itemId = c.getInStream().readUnsignedWord();

        if (c.debugMessage) {
            c.sendMessage(String.format("ItemClick[item=%d, option=%d, interface=%d, slot=%d]", itemId, 3, -1, -1));
        }

        if (c.getLock().cannotClickItem(c, itemId))
            return;
        if (!c.getItems().playerHasItem(itemId, 1)) {
            return;
        }
        if (c.getInterfaceEvent().isActive()) {
            c.sendMessage("Please finish what you're doing.");
            return;
        }
        if (c.getBankPin().requiresUnlock()) {
            c.getBankPin().open(2);
            return;
        }
        if (itemId == Items.ROTTEN_POTATO && RottenPotato.getInstance().forPlayer(c).mash())
            return;

        if (RunePouch.isRunePouch(itemId)) {
            c.getRunePouch().emptyBagToInventory();
            return;
        }
        TeleportTablets.operate(c, itemId);
        if (Misc.isInDuelSession(c)) return;
        Optional<DegradableItem> d = DegradableItem.forId(itemId);
        if (d.isPresent()) {
            Degrade.checkPercentage(c, itemId);
            return;
        }
        if (SanguinestiStaff.clickItem(c, itemId, 3)) {
            return;
        }

        if (BryophytaStaff.handleItemOption(c, itemId, 3))
            return;
        switch (itemId) {
            case LootingBag.LOOTING_BAG:
            case LootingBag.LOOTING_BAG_OPEN:
                c.getDH().sendDialogues(LootingBag.OPTIONS_DIALOGUE_ID, 0);
                break;

            case 21183:
                if (c.getItems().freeSlots() < 1) {
                    c.sendMessage("@blu@You need at least 1 free slot to do this.");
                    return;
                }
                c.getItems().addItem(23824, c.slaughterCharge);
                c.sendMessage("You remove @red@" + c.slaughterCharge + " @bla@charges from your bracelet of slaughter.");
                c.slaughterCharge = 0;
                break;
            case 20714:
                c.getDH().sendDialogues(265, 2897);
                break;
            case 24271:
                if (!c.getItems().playerHasItem(24271)) {
                    c.sendMessage("@blu@You do not have the item to do this.");
                    return;
                }
                if (c.getItems().freeSlots() < 2) {
                    c.sendMessage("You need at least two free slots to dismantle this item.");
                    return;
                }
                c.sendMessage("@blu@You have dismantled your helmet.");
                c.getItems().deleteItem(24271, 1);
                c.getItems().addItem(24268, 1);
                c.getItems().addItem(10828, 1);
                break;
            case 20716:
                TomeOfFire.store(c);
                break;
            case 24423:
                if (c.getItems().freeSlots() < 2) {
                    c.sendMessage("You need at least two free slots to use this command.");
                    return;
                }
                if (!(c.getItems().playerHasItem(24423, 1))) {
                    return;
                }
                c.getItems().deleteItem(24423, 1);
                c.getItems().addItem(24422, 1);
                c.getItems().addItem(24511, 1);
                break;
            case 24424:
                if (c.getItems().freeSlots() < 1) {
                    c.sendMessage("You need at least one free slots to use this command.");
                    return;
                }
                if (!(c.getItems().playerHasItem(24424, 1))) {
                    return;
                }
                c.getItems().deleteItem(24424, 1);
                c.getItems().addItem(24422, 1);
                c.getItems().addItem(24514, 1);
                break;
            case 24425:
                if (c.getItems().freeSlots() < 1) {
                    c.sendMessage("You need at least one free slots to use this command.");
                    return;
                }
                if (!(c.getItems().playerHasItem(24425, 1))) {
                    return;
                }
                c.getItems().deleteItem(24425, 1);
                c.getItems().addItem(24422, 1);
                c.getItems().addItem(24517, 1);
                break;
            case Items.VIGGORAS_CHAINMACE: // Uncharging pvp weapons
            case Items.THAMMARONS_SCEPTRE:
            case Items.CRAWS_BOW:
                PvpWeapons.handleItemOption(c, itemId, 3);
                break;
            case 22322:
                c.getDH().sendDialogues(333, 7456);
                break;
            case 1704:
                c.sendMessage("@red@You currently have no charges in your glory.");
                break;
            case 12932:
            case 12922:
            case 12929:
            case 12927:
            case 12924:
                String name = ItemDef.forId(itemId).getName();
                Consumer<Player> dismantle = plr -> {
                    plr.getPA().closeAllWindows();
                    if (!plr.getItems().playerHasItem(itemId))
                        return;
                    plr.getItems().deleteItem(itemId, 1);
                    plr.getItems().addItemUnderAnyCircumstance(Items.ZULRAHS_SCALES, 20_000);
                    plr.sendMessage("You dismantle the {} and receive 20,000 scales.", name);
                };

                new DialogueBuilder(c)
                        .itemStatement(itemId, "Are you sure you want to dismantle your " + name + "?", "You will receive 20,000 Zulrah scales.")
                        .option(new DialogueOption("Yes, dismantle it.", dismantle), DialogueOption.nevermind()).send();
                break;
            case 13346:
                new UltraMysteryBox(c).quickOpen();
                break;
            case 6199:
                new NormalMysteryBox(c).quickOpen();
                break;
            case 6828:
                new SuperMysteryBox(c).quickOpen();
                break;

            case 21347:
                c.start(new AmethystChiselDialogue(c));
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

            case 13226:
                c.getHerbSack().check();
                break;

            case 12020:
                c.getGemBag().withdrawAll();
                break;

            case 12902: //Toxic staff dismantle
                if (!c.getItems().playerHasItem(12902))
                    return;
                if (c.getItems().freeSlots() < 2)
                    return;

                c.getItems().deleteItem(12902, 1);
                c.getItems().addItem(12932, 1);
                c.getItems().addItem(11791, 1);
                c.sendMessage("You dismantle your toxic staff of the dead.");
                break;

            case 12900: //Toxic trident dismantle
                if (!c.getItems().playerHasItem(12900))
                    return;
                if (c.getItems().freeSlots() < 2)
                    return;

                c.getItems().deleteItem(12900, 1);
                c.getItems().addItem(12932, 1);
                c.getItems().addItem(11907, 1);
                c.sendMessage("You dismantle your toxic trident.");
                break;

            case 11283:
                if (c.getDragonfireShieldCharge() == 0) {
                    c.sendMessage("Your dragonfire shield has no charge.");
                    return;
                }
                c.setDragonfireShieldCharge(0);
                c.sendMessage("Your dragonfire shield has been emptied.");
                break;
            case 13196:
            case 13198:
                if (c.getItems().freeSlots() < 2) {
                    c.sendMessage("You need at least 2 free slots for this.");
                    return;
                }
                c.getItems().deleteItem2(itemId, 1);
                c.getItems().addItem(12929, 1);
                c.getItems().addItem(itemId == 13196 ? 13200 : 13201, 1);
                c.sendMessage("You revoke the mutagen from the helmet.");
                break;
            case 11907:
            case 12899:
                int charge = itemId == 11907 ? c.getTridentCharge() : c.getToxicTridentCharge();
                if (charge <= 0) {
                    if (itemId == 12899) {
                        if (c.getToxicTridentCharge() == 0) {
                            if (c.getItems().freeSlots() > 1) {
                                c.getItems().deleteItem(12899, 1);
                                c.getItems().addItem(12932, 1);
                                c.getItems().addItem(11907, 1);
                                c.sendMessage("You dismantle your Trident of the swamp.");
                                return;
                            } else {
                                c.sendMessage("You need at least 2 inventory spaces to dismantle the trident.");
                                return;
                            }
                        }
                    } else {
                        c.sendMessage("Your trident currently has no charge.");
                        return;
                    }
                }

                if (c.getItems().freeSlots() < 3) {
                    c.sendMessage("You need at least 3 free slots for this.");
                    return;
                }
                c.getItems().addItem(554, 5 * charge);
                c.getItems().addItem(560, 1 * charge);
                c.getItems().addItem(562, 1 * charge);

                if (itemId == 12899) {
                    c.getItems().addItem(12934, 1 * charge);
                }

                if (itemId == 11907) {
                    c.setTridentCharge(0);
                } else {
                    c.setToxicTridentCharge(0);
                }
                c.sendMessage("You revoke " + charge + " charges from the trident.");
                break;

            case 12926:
                if (c.getToxicBlowpipeAmmo() == 0 || c.getToxicBlowpipeAmmoAmount() == 0) {
                    c.sendMessage("You have no ammo in the pipe.");
                    return;
                }
                if (c.getItems().freeSlots() < 2) {
                    c.sendMessage("You need at least 2 free slots for this.");
                    return;
                }
                if (c.getItems().addItem(c.getToxicBlowpipeAmmo(), c.getToxicBlowpipeAmmoAmount())) {
                    c.setToxicBlowpipeAmmoAmount(0);
                    c.sendMessage("You unload the pipe.");
                }
                break;
            case 2552:
            case 2554:
            case 2556:
            case 2558:
            case 2560:
            case 2562:
            case 2564:
            case 2566:
                //c.getPA().ROD();
                c.getPA().spellTeleport(3304, 3130, 0, false);
                break;
            case 11968:
            case 11970:
            case 11105:
            case 11107:
            case 11109:
            case 11111:
                c.getPA().handleSkills();
                c.isOperate = true;
                c.operateEquipmentItemId = itemId;
                break;
            case 1712:
            case 1710:
            case 1708:
            case 1706:
            case 19707:
                c.getPA().handleGlory();
                c.operateEquipmentItemId = itemId;
                c.isOperate = true;
                break;

            case 24444:
            case 19639:
            case 19641:
            case 19643:
            case 19645:
            case 19647:
            case 19649:
            case 11864:
            case 11865:
                c.getSlayer().revertHelmet(itemId);
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
                if (Server.getMultiplayerSessionListener().inAnySession(c)) {
                    return;
                }

                c.getPA().openQuestInterface("@dre@Max Cape Features",
                        "While wielding the cape you will:",
                        "Have a chance of saving ammo.",
                        "Deplete run energy slower.",
                        "Get more herbs & faster growth time.",
                        "Have less chance of burning food",
                        "Have 20% of saving a bar while smithing.",
                        "Have 20% of saving a herb while mixing potions.",
                        "Regenerate 2 hitpoints instead of 1 at a time.",
                        "Get 5+ restore per prayer/super restore sip.",
                        "Get double seeds while thieving the master farmer.",
                        "Be able to operate for additional options."
                );
                break;

            default:
                if (c.getRights().isOrInherits(Right.OWNER)) {
                    Misc.println("[DEBUG] Item Option #3-> Item id: " + itemId);
                }
                break;
        }

    }

}
