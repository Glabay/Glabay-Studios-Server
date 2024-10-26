package io.xeros.content.itemskeptondeath;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class DeathItemStack {

    private Stack<GameItem> valuedItemStack;

    public void create(Player player) {
        List<GameItem> allPlayerItems = player.getItems().getHeldItems();
        allPlayerItems.sort(GameItem::comparePrice);
        valuedItemStack = new Stack<>();
        valuedItemStack.addAll(Lists.reverse(allPlayerItems));
    }

    /**
     * Pop items off the stack. Stackable items will be popped one at a time, i.e. not the whole stack at once.
     * @param amount The amount of items to pop.
     * @return A list of the popped items.
     */
    public List<GameItem> pop(int amount) {
        List<GameItem> keptItems = new ArrayList<>();
        for (int i = 0; i < amount && !valuedItemStack.isEmpty(); i++) {
            GameItem item = valuedItemStack.pop();
            if (item.amount() > 1)
                valuedItemStack.push(new GameItem(item.id(), item.amount() - 1));
            keptItems.add(new GameItem(item.id(), 1));
        }

        return keptItems;
    }

    public List<GameItem> popRemaining() {
        List<GameItem> items = new ArrayList<>();
        while (!valuedItemStack.isEmpty())
            items.add(valuedItemStack.pop());
        return items;
    }

    public Stack<GameItem> getValuedItemStack() {
        return valuedItemStack;
    }
}
