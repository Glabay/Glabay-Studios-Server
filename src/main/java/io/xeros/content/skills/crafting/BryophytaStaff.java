package io.xeros.content.skills.crafting;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.skills.Skill;
import io.xeros.model.Graphic;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.net.packets.ItemOnItem;
import io.xeros.util.Misc;

import java.util.function.Consumer;

public class BryophytaStaff {

    public static final int UNCHARGED_STAFF = 22368, CHARGED_STAFF = 22370;

    public static boolean attach(Player player, int itemUsedId, int itemUsedWithId) {
        if (!(itemUsedId == Items.BATTLESTAFF && itemUsedWithId == Items.BRYOPHYTAS_ESSENCE)
                && !(itemUsedWithId == Items.BATTLESTAFF && itemUsedId == Items.BRYOPHYTAS_ESSENCE))
            return false;

        player.getPA().stopSkilling();

        if (player.getLevel(Skill.CRAFTING) < 62) {
            player.sendMessage("You need a crafting level of 62 to create {}.", ItemDef.forId(Items.BRYOPHYTAS_STAFF).getName());
            return true;
        }

        if (player.getItems().playerHasItem(Items.BATTLESTAFF) && player.getItems().playerHasItem(Items.BRYOPHYTAS_ESSENCE)) {
            player.getItems().deleteItem(Items.BRYOPHYTAS_ESSENCE, 1);
            player.getItems().deleteItem(Items.BATTLESTAFF, 1);
            player.getItems().addItem(UNCHARGED_STAFF, 1);
            player.startAnimation(7981);
            player.startGraphic(new Graphic(264, 20, Graphic.GraphicHeight.HIGH));
            player.getPA().addSkillXPMultiplied(1, Skill.CRAFTING.getId(), true);
        }

        return true;
    }

    public static boolean handleItemOption(Player player, int itemId, int option) {
        if (option == 2) {
            if (itemId == UNCHARGED_STAFF) {
                player.sendMessage("Your {} is not charged, use 1,000 Nature runes to charge it.", ItemDef.forId(CHARGED_STAFF).getName());
                return true;
            }

            if (itemId == CHARGED_STAFF) {
                player.sendMessage("Your {} has {} charges remaining.", ItemDef.forId(itemId).getName(), Misc.insertCommas(player.bryophytaStaffCharges));
                return true;
            }
        }

        if (option == 3 && itemId == CHARGED_STAFF) {
            uncharge(player);
            return true;
        }

        return false;
    }

    public static boolean handleItemOnItem(Player player, int item1, int item2) {
        if (ItemOnItem.is(Items.NATURE_RUNE, CHARGED_STAFF, item1, item2)) {
            if (player.bryophytaStaffCharges == 0) {
                charge(player, CHARGED_STAFF);
            } else {
                player.sendMessage("Your charge has already been charged, you must unload it or use all charges.");
            }
            return true;
        }

        if (ItemOnItem.is(Items.NATURE_RUNE, UNCHARGED_STAFF, item1, item2)) {
            long staffCount = player.getItems().getTotalCount(CHARGED_STAFF);

            if (staffCount == 0 && player.bryophytaStaffCharges != 0) {
                int slot = player.getItems().getInventoryItemSlot(UNCHARGED_STAFF);
                if (slot == -1)
                    return true;
                player.getItems().setInventoryItemSlot(slot, CHARGED_STAFF);
                return true;
            }

            if (staffCount != 0) {
                player.sendMessage("You already have a charged staff, you must deplete all charges.");
                return true;
            }

            charge(player, UNCHARGED_STAFF);
            return true;
        }

        return false;
    }

    private static void charge(Player player, int staffId) {
        if (player.bryophytaStaffCharges != 0) {
            player.sendMessage("The staff is already charged.");
            return;
        }

        int runes = player.getItems().getInventoryCount(Items.NATURE_RUNE);

        if (runes == 0) {
            player.sendMessage("You don't have any nature runes.");
            return;
        }

        if (runes < 1_000) {
            player.sendMessage("You need 1,000 Nature runes to charge the staff.");
            return;
        }

        int staffSlot = player.getItems().getInventoryItemSlot(staffId);
        if (staffSlot == -1)
            return;

        player.getItems().deleteItem(Items.NATURE_RUNE, 1_000);
        player.bryophytaStaffCharges = 1_000;
        player.sendMessage("You charge your {}.", ItemDef.forId(CHARGED_STAFF).getName());
        if (staffId != CHARGED_STAFF)
            player.getItems().setInventoryItemSlot(staffSlot, CHARGED_STAFF);
    }

    private static void uncharge(Player player) {
        Consumer<Player> uncharge = plr -> {
            plr.getPA().closeAllWindows();
            if (plr.getItems().getInventoryCount(CHARGED_STAFF) == 0)
                return;

            int slot = plr.getItems().getInventoryItemSlot(CHARGED_STAFF);
            plr.getItems().setInventoryItemSlot(slot, UNCHARGED_STAFF, 1);
            plr.bryophytaStaffCharges = 0;
            player.sendMessage("You uncharge your {}.", ItemDef.forId(CHARGED_STAFF).getName());
        };

        new DialogueBuilder(player).statement("This will unload your staff but consume all the Nature runes.", "Are you sure?")
                .option(
                        new DialogueOption("Yes, uncharge the staff and consume the Nature runes.", uncharge),
                        new DialogueOption("Never mind", plr -> plr.getPA().closeAllWindows())
                ).send();
    }

    public static void depleteIfUsed(Player player, int runeUsed) {
        if (runeUsed != Items.NATURE_RUNE || !isWearingStaffWithCharge(player))
            return;
        if (Math.random() < 0.15) // 15% chance to not deplete runes on use
            return;
        player.bryophytaStaffCharges--;
        if (player.bryophytaStaffCharges <= 0) {
            player.bryophytaStaffCharges = 0;
            if (player.getItems().isWearingItem(CHARGED_STAFF)) {
                player.getItems().setEquipment(UNCHARGED_STAFF, 1, 3, true);
                player.sendMessage("<col=ff0000>Your Bryophyta's Staff has run out of charges!");
            }
        }
    }

    public static boolean isWearingStaffWithCharge(Player player) {
        return player.getItems().isWearingItem(CHARGED_STAFF, Player.playerWeapon) && player.bryophytaStaffCharges > 0;
    }
}
