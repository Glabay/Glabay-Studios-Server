package io.xeros.model.items;

import java.util.Arrays;

import io.xeros.Server;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.bank.BankItem;
import io.xeros.model.items.bank.BankTab;

/**
 * Static class for inventory management, trying to have a class that's actually useful instead of the
 * random shit in ItemAssistant where you can't know when to + 1 + 2 whatthefuckever the item ids.
 * This class always deal with the real item id.
 */
public class Inventory {

    private final Player player;

    public Inventory(Player player) {
        this.player = player;
    }

    public boolean containsAll(ImmutableItem...items) {
        return Arrays.stream(items).allMatch(item -> player.getItems().playerHasItem(item.getId(), item.getAmount()));
    }

    public boolean hasRoomInInventory(ImmutableItem item) {
        boolean stackable = ItemDef.forId(item.getId()).isStackable();

        // If stackable first check for the item and verify the amount will be valid after adding
        if (stackable) {
            for (int i = 0; i < player.playerItems.length; i++) {
                if (player.playerItems[i] - 1 == item.getId()) {
                    return (long) item.getAmount() + (long) player.playerItemsN[i] <= Integer.MAX_VALUE;
                }
            }

            return freeInventorySlots() > 0;
        }

        return freeInventorySlots() >= item.getAmount();
    }

    public boolean addToInventory(ImmutableItem item) {
        if (hasRoomInInventory(item)) {
            boolean stackable = ItemDef.forId(item.getId()).isStackable();
            if (stackable) {
                for (int i = 0; i < player.playerItems.length; i++) {
                    if (player.playerItems[i] - 1 == item.getId()) {
                        player.playerItemsN[i] += item.getAmount();
                        player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
                        return true;
                    }
                }

                int slot = freeInventorySlot();
                player.playerItems[slot] = item.getId() + 1;
                player.playerItemsN[slot] = item.getAmount();
            } else {
                for (int k = 0; k < item.getAmount(); k++) {
                    int slot = freeInventorySlot();
                    player.playerItems[slot] = item.getId() + 1;
                    player.playerItemsN[slot] = 1;
                }
            }

            player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
            return true;
        }

        return false;
    }

    public int freeInventorySlot() {
        for (int i = 0; i < player.playerItems.length; i++) {
            if (player.playerItems[i] <= 0) {
                return i;
            }
        }
        return -1;
    }


    public int freeInventorySlots() {
        return (int) Arrays.stream(player.playerItems).filter(item -> item <= 0).count();
    }

    public boolean addToBank(ImmutableItem item) {
        ItemDef def = ItemDef.forId(item.getId());
        if (def.isNoted()) {
            item = new ImmutableItem(def.getNoteId(), item.getAmount());
        }

        BankItem bankItem = new BankItem(item.getId() + 1, item.getAmount());
        for (BankTab tab : player.getBank().getBankTab()) {
            BankItem foundItem = tab.getItem(bankItem);
            if (foundItem != null) {
                if ((long) item.getAmount() + (long) foundItem.amount() > Integer.MAX_VALUE) {
                    return false;
                } else {
                    foundItem.setAmount(foundItem.amount() + item.getAmount());
                    return true;
                }
            }
        }

        for (BankTab tab : player.getBank().getBankTab()) {
            if (tab.freeSlots() > 0) {
                tab.getItems().add(bankItem);
                return true;
            }
        }

        return false;
    }

    public void addOrDrop(ImmutableItem item) {
        addAnywhere(item, false);
    }

    public void addAnywhere(ImmutableItem item) {
        addAnywhere(item, true);
    }

    public void addAnywhere(ImmutableItem item, boolean bank) {
        String name = ItemDef.forId(item.getId()).getName();
        if (hasRoomInInventory(item)) {
            if (!addToInventory(item)) {
                throw new IllegalStateException(Arrays.toString(player.playerItems) + "," + Arrays.toString(player.playerItems) + "," + item);
            }
        } else if (bank && addToBank(item)) {
            if (item.getAmount() > 1) {
                player.sendMessage(name + " x" + item.getAmount() + " was added to your bank.");
            } else {
                player.sendMessage(name + " was added to your bank.");
            }
        } else {
            player.sendMessage("@red@" + name + " was dropped at your feet.");
            Server.itemHandler.createGroundItem(player, item.getId(), player.getX(), player.getY(), player.heightLevel, item.getAmount());
        }
    }
}
