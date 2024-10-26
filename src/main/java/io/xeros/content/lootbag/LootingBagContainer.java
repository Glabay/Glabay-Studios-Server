package io.xeros.content.lootbag;

import java.util.ArrayList;
import java.util.List;

import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ContainerUpdate;

public class LootingBagContainer {

    private final Player player;
    public final List<LootingBagItem> items = new ArrayList<>();

    public LootingBagContainer(Player player) {
        this.player = player;
    }

    /**
     * Handles withdrawal from the lootingbag
     *
     * @param id     The id of the item being withdrawn
     * @param amount The amount of the item being withdrawn
     */
    public boolean withdraw(int id, int amount) {
        int index = findIndexInLootBag(id);
        int amountToAdd = 0;
        if (items.size() <= 0) {
            return false;
        }
        if (index == -1) {
            return false;
        }
        LootingBagItem item = items.get(index);
        if (item == null) {
            return false;
        }
        if (item.id() <= 0 || item.amount() <= 0 || player.getItems().freeSlots() <= 0) {
            return false;
        }
        if (!player.getPosition().inBank() || player.getPosition().inWild()) {
            player.sendMessage("You must be at home to do this.");
            return false;
        }
        if (player.getItems().getItemCount(id, false) + amount >= Integer.MAX_VALUE || player.getItems().getItemCount(id, false) + amount <= 0) {
            return false;
        }
        if ((items.get(items.indexOf(item)).amount()) > amount) {
            amountToAdd = amount;
            items.get(items.indexOf(item)).incrementAmount(-amount);
        } else {
            amountToAdd = item.amount();
            items.remove(index);
        }
        player.getItems().addItem(item.id(), amountToAdd);
        return true;
    }

    /**
     * Handles depositing of items into the looting bag
     *
     * @param id     The id of the item being deposited
     * @param amount The amount of the item being deposited
     */
    public boolean deposit(int id, int amount) {
        return deposit(id, amount, true);
    }

    /**
     * Handles depositing of items into the looting bag
     *
     * @param id     The id of the item being deposited
     * @param amount The amount of the item being deposited
     * @param fromInventory if the item is being deposited from the player's inventory
     */
    public boolean deposit(int id, int amount, boolean fromInventory) {
        if (!allowedInBag(id)) {
            return false;
        }
        if (amount >= Integer.MAX_VALUE && fromInventory) {
            amount = player.getItems().getItemCount(id, false);
        }
        if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe()) {
            return false;
        }
        int bagSpotsLeft = 28 - items.size();
        boolean stackable = player.getItems().isStackable(id);
        boolean bagContainsItem = containsItem(id);
        if (amount > bagSpotsLeft) {
            if (!(stackable && bagContainsItem) && !stackable) {
                amount = bagSpotsLeft;
            }
        }
        if (fromInventory && !player.getItems().playerHasItem(id)) {
            return false;
        }
        if (!player.getPosition().inWild()) {
            player.sendMessage("You can only do this in the wilderness.");
            return false;
        }
        if (items.size() >= 28 && !(stackable && bagContainsItem)) {
            player.sendMessage("The bag cannot hold anymore items.");
            return false;
        }
        if (countItems(id) + amount >= Integer.MAX_VALUE || countItems(id) + amount <= 0) {
            return false;
        }

        if (fromInventory) {
            List<Integer> amounts = player.getItems().deleteItemAndReturnAmount(id, amount);

            int count = 0;
            for (int amt : amounts) {
                if (!addItemToList(id, amt)) {
                    break;
                }
                count++;
                if (count >= amount) {
                    break;
                }
            }
        } else {
            addItemToList(id, amount);
        }

        player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);

        return true;
    }

    /**
     * Item allowed check
     * @param item
     * @return
     */
    public boolean allowedInBag(int item) {
        switch (item) {
            case LootingBag.LOOTING_BAG:
            case LootingBag.LOOTING_BAG_OPEN:
            case 11942:
                player.sendMessage("You may be surprised to learn that bagception is not permitted.");
                return false;
            default:
                if (ItemDef.forId(item) == null) {
                    player.sendMessage("This item has no definition and cannot be put into the bag. (please report this, code: " + item + ")");
                    return false;
                } else if (!ItemDef.forId(item).isTradable()) {
                    player.sendMessage("This item is deemed untradable and cannot be put into the bag.");
                    return false;
                }
                return true;
        }
    }

    public int countItems(int id) {
        int count = 0;
        for (LootingBagItem item : items) {
            if (item.id() == id) {
                count += item.amount();
            }
        }
        return count;
    }

    public boolean addItemToList(int id, int amount) {
        for (LootingBagItem item : items) {
            if (item.id() == id) {
                if (item.amount() + amount >= Integer.MAX_VALUE) {
                    return false;
                }
                if (player.getItems().isStackable(id)) {
                    item.incrementAmount(amount);
                    return true;
                }
            }
        }
        items.add(new LootingBagItem(id, amount));
        return true;
    }

    private String getShortAmount(int amount) {
        if (amount <= 1) {
            return "";
        }
        String amountToString = "" + amount;
        if (amount > 1000000000) {
            amountToString = "@gre@" + (amount / 1000000000) + "B";
        } else if (amount > 1000000) {
            amountToString = "@gre@" + (amount / 1000000) + "M";
        } else if (amount > 1000) {
            amountToString = "@whi@" + (amount / 1000) + "K";
        }
        return amountToString;
    }

    public void removeMultipleItemsFromBag(int id, int amount) {
        if (amount >= Integer.MAX_VALUE) {
            amount = countItems(id);
        }
        int count = 0;
        while (containsItem(id)) {
            if (!withdraw(id, amount)) {
                return;
            }
            if (player.getItems().isStackable(id)) {
                count += amount;
            } else {
                count++;
            }
            if (count >= amount) {
                return;
            }
        }
    }

    public boolean containsItem(int id) {
        for (LootingBagItem item : items) {
            if (item.id() == id) {
                return true;
            }
        }
        return false;
    }

    public int findIndexInLootBag(int id) {
        for (LootingBagItem item : items) {
            if (item.id() == id) {
                return items.indexOf(item);
            }
        }
        return -1;
    }

}
