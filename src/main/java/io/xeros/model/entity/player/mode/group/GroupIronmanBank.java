package io.xeros.model.entity.player.mode.group;

import io.xeros.Server;
import io.xeros.model.ContainerAction;
import io.xeros.model.SlottedItem;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.inventory.Inventory;
import io.xeros.model.items.inventory.InventoryListener;

import java.util.List;
import java.util.stream.Stream;

public class GroupIronmanBank {

    public static final int BANK_SIZE = 1_000;

    public static boolean processContainerAction(Player player, ContainerAction action) {
        int amount = typeToAmount(action);
        GroupIronmanBank bank = getBank(player);
        if (bank == null)
            return false;

        if (action.getInterfaceId() == INTERFACE_ITEM_CONTAINER_ID) {
            bank.withdraw(player, action.getItemId(), action.getSlotId(), amount);
            return true;
        } else if (action.getInterfaceId() == INVENTORY_INTERFACE_CONTAINER_ID) {
            bank.deposit(player, new SlottedItem(action.getItemId(), amount, action.getSlotId()), false);
            return true;
        }

        return false;
    }

    public static boolean swap(Player player, int interfaceId, boolean insert, int fromSlot, int toSlot) {
        GroupIronmanBank bank = getBank(player);
        if (bank == null)
            return false;

        if (interfaceId == INTERFACE_ITEM_CONTAINER_ID) {
            bank.getInventory().swap(insert, fromSlot, toSlot);
            return true;
        }

        return false;
    }

    public static void bankAll(Player player, boolean equipment) {
        GroupIronmanBank bank = getBank(player);
        if (bank == null)
            return;

        List<SlottedItem> items = equipment ? player.getItems().getEquipmentItems() : player.getItems().getInventoryItems();
        for (SlottedItem item : items) {
            bank.deposit(player, item, equipment);
        }
    }

    private static GroupIronmanBank getBank(Player player) {
        if (player.getOpenInterface() != INTERFACE_ID) {
            return null;
        }

        GroupIronmanGroup group = GroupIronmanRepository.getGroupForOnline(player).orElse(null);
        if (group == null)
            return null;
        return group.getBank();
    }

    private static int typeToAmount(ContainerAction action) {
        switch (action.getType()) {
            case ACTION_1: return 1;
            case ACTION_2: return 5;
            case ACTION_3: return 10;
            case ACTION_4: return Integer.MAX_VALUE;
            case X: return action.getItemAmount();
            default: return -1;
        }
    }


    public static final int OBJECT_ID = 43488; //32572 was the old ID

    public static final int INTERFACE_ID = 48_670;
    public static final int INTERFACE_ITEM_CONTAINER_ID = 48_675;

    public static final int INVENTORY_INTERFACE_ID = 5063;
    public static final int INVENTORY_INTERFACE_CONTAINER_ID = 5064;

    private static final int CONTAINER_CURR_SIZE_COMPONENT = 48_673;
    private static final int CONTAINER_MAX_SIZE_COMPONENT = 48_674;

    private final Inventory inventory = new Inventory(BANK_SIZE, Inventory.StackMode.STACK_ALWAYS);
    private final GroupIronmanGroup groupIronman;

    public GroupIronmanBank(GroupIronmanGroup groupIronman) {
        this.groupIronman = groupIronman;
        inventory.addListener(new GroupIronmanBankListener());
    }

    public void open(Player player) {
        updateContainer(player);
        player.getPA().sendInterfaceSet(INTERFACE_ID, INVENTORY_INTERFACE_ID);
        player.getPA().sendString(48_672, "Group Bank of " + groupIronman.getName());
        player.sendMessage("Use ::gstats to track your teams progress!");
    }

    private void updateContainer(Player player) {
        player.getItems().sendItemContainer(INTERFACE_ITEM_CONTAINER_ID, inventory.buildList());
        updateInventoryContainer(player);
        updateBankSize(player);
    }

    private void updateContainerSlot(Player player, int slot, GameItem gameItem) {
        player.getPA().itemOnInterface(gameItem, INTERFACE_ITEM_CONTAINER_ID, slot);
        updateInventoryContainer(player);
        updateBankSize(player);
    }

    private void updateInventoryContainer(Player player) {
        player.getItems().sendInventoryInterface(INVENTORY_INTERFACE_CONTAINER_ID);
    }

    private void updateBankSize(Player player) {
        player.getPA().runClientScript(3, INTERFACE_ITEM_CONTAINER_ID, CONTAINER_CURR_SIZE_COMPONENT, CONTAINER_MAX_SIZE_COMPONENT);
    }

    public void deposit(final Player player, final SlottedItem item, boolean equipment) {
        var itemId = item.id();
        var slot = item.slot();
        var amountInContainer = equipment ? player.playerEquipmentN[item.slot()] : player.getItems().getItemAmount(item.id());
        var amount = Math.min(amountInContainer, item.amount());
        if (amount <= 0)
            return;
        if (!player.getItems().isTradable(itemId)) {
            return;
        }

        // Convert to unnoted if noted
        int itemIdToAdd = itemId;
        ItemDef itemDef = ItemDef.forId(itemId);
        if (itemDef.isNoted())
            itemIdToAdd = Server.itemHandler.getCounterpart(itemId);

        GameItem removed = inventory.addAndReturnAmountAdded(new GameItem(itemIdToAdd, amount));
        int remainingAmount = amount - removed.amount();

        // Update source container to only remove successfully deposited item amount
        if (equipment) {
            player.getItems().equipItem(-1, 0, slot);
            if (remainingAmount > 0) {
                player.getItems().equipItem(itemId, remainingAmount, slot);
            }
        } else {
            if (itemDef.isStackable()) {
                player.getItems().deleteItem(itemId, removed.amount());
            } else {
                for (int i = 0; i < removed.amount(); i++) {
                    player.getItems().deleteItem(itemId, 1);
                }
            }
        }

        // Send container update
        if (equipment) {
            player.getItems().addContainerUpdate(ContainerUpdate.EQUIPMENT);
            player.getPA().resetAutocast();
        } else {
            player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
            updateInventoryContainer(player);
        }

        if (removed.amount() != amount) {
            player.sendMessage("There was not enough space in the bank to deposit all those items.");
        }
    }

    public void withdraw(Player player, int itemId, int itemSlot, int amount) {
        boolean withdrawNote = player.takeAsNote;
        amount = Math.min(inventory.getAmount(itemId), amount);
        if (amount <= 0)
            return;

        int itemIdToAdd = itemId;
        if (withdrawNote) {
            if (!ItemDef.forId(itemId).isNoted() && Server.itemHandler.getCounterpart(itemId) > 0) {
                itemIdToAdd = Server.itemHandler.getCounterpart(itemId);
            } else player.sendMessage("You can't withdraw this item as a note!");
        }

        if (!player.getItems().hasRoomInInventory(itemIdToAdd, amount)) {
            ItemDef def = ItemDef.forId(itemIdToAdd); // I hate myself for writing this :]
            if (!def.isStackable()) {
                amount = player.getItems().freeSlots();
                if (amount <= 0 || !player.getItems().hasRoomInInventory(itemIdToAdd, amount)) {
                    player.sendMessage("You don't have enough space in your inventory.");
                    return;
                }
            } else {
                player.sendMessage("You don't have enough space in your inventory.");
                return;
            }
        }

        GameItem item = new GameItem(itemId, amount);
        int removed = inventory.remove(item);
        player.getItems().addItem(itemIdToAdd, removed);
        inventory.shift();
        updateInventoryContainer(player);
        groupIronman.addWithdrawItemLog(player, item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    private Stream<Player> getGroupMembersWithOpenBank() {
        return groupIronman.getOnline().stream().filter(it -> it.isInterfaceOpen(INTERFACE_ID));
    }

    private class GroupIronmanBankListener implements InventoryListener {
        @Override
        public void capacityExceeded(Inventory inventory) { }

        @Override
        public void itemsUpdated(Inventory inventory) {
            getGroupMembersWithOpenBank().forEach(GroupIronmanBank.this::updateContainer);
        }

        @Override
        public void itemUpdated(Inventory inventory, int slot, GameItem item) {
            getGroupMembersWithOpenBank().forEach(it -> GroupIronmanBank.this.updateContainerSlot(it, slot, item));
        }
    }
}
