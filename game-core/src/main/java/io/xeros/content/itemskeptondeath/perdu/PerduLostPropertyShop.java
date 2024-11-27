package io.xeros.content.itemskeptondeath.perdu;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.model.ContainerAction;
import io.xeros.model.ContainerActionType;
import io.xeros.model.definition.Items;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.inventory.Inventory;
import io.xeros.util.Misc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PerduLostPropertyShop {

    private static final int INTERFACE_ID = 22_992;
    private static final int SCROLLABLE = 22_994;
    private static final int ITEM_CONTAINER = 22_995;
    private static final int PRICE_CONTAINER = 22_996;

    public static boolean handleContainerAction(Player player, ContainerAction action) {
        if (action.getInterfaceId() != ITEM_CONTAINER)
            return false;
        if (action.getType() == ContainerActionType.ACTION_1)
            player.getPerduLostPropertyShop().buy(player, action.getSlotId());
        return true;
    }

    private final Inventory inventory = new Inventory(128, Inventory.StackMode.STACK_STACKABLE_ITEMS);

    public void open(Player player) {
        if (inventory.freeSlots() == inventory.capacity()) {
            new DialogueBuilder(player).npc(Npcs.PERDU, DialogueExpression.SAD, "You don't have any items to reclaim.").send();
            return;
        }
        updateContainer(player);
        player.getPA().resetScrollBar(SCROLLABLE);
        player.getPA().showInterface(INTERFACE_ID);
    }

    private void updateContainer(Player player) {
        List<GameItem> container = inventory.buildList();
        player.getItems().sendItemContainer(ITEM_CONTAINER, container);
        player.getPA().sendStringContainer(PRICE_CONTAINER, container.stream().map(it -> Misc.insertCommas(it.getDef().getShopValue())).collect(Collectors.toList()));
    }

    public void add(Player player, GameItem gameItem) {
        Optional<GameItem> added = inventory.add(gameItem);
        if (added.isPresent()) {
            player.sendMessage("@dre@Perdu couldn't store your lost item because his shop is full.");
        } else
            player.sendMessage("@dre@Your {} was lost and must be bought back from Perdu.", gameItem.getDef().getName());
    }

    public void buy(Player player, int slot) {
        if (slot >= inventory.capacity() || slot < 0 || inventory.get(slot) == null)
            return;
        GameItem gameItem = inventory.get(slot);

        int cost = gameItem.getDef().getShopValue() * gameItem.amount();
        if (cost > 0) {
            if (!player.getItems().playerHasItem(Items.COINS, cost)) {
                player.sendMessage("You don't have enough coins.");
                return;
            }

            player.getItems().deleteItem(Items.COINS, cost);
        }

        inventory.remove(gameItem);
        inventory.shift();
        player.getItems().addItemUnderAnyCircumstance(gameItem.id(), gameItem.amount());
        updateContainer(player);

        String description = (gameItem.amount() > 1 ? "x" + gameItem.amount() + " " : "") + gameItem.getDef().getName();
        player.sendMessage("@dre@You buy back your {} for {} coins.", description, Misc.insertCommas(cost));
        updateContainer(player);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
