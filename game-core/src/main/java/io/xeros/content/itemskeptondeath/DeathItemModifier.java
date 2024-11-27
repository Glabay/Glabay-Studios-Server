package io.xeros.content.itemskeptondeath;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.List;
import java.util.Set;

/**
 * When a player dies with the possibility of losing items {@link DeathItemModifier#modify(Player, GameItem, boolean, List, List)}
 * is called on all items that are going to be kept and lost. {@link DeathItemModifier#getItemIds()} defines which items will
 * {@link DeathItemModifier#modify(Player, GameItem, boolean, List, List)} call.
 *
 * You can use this to drop items in a looting bag or herb sack, un-imbue items and drop the un-imbued version,
 * or anything else required when a player dies in a situation where they drop items.
 */
public interface DeathItemModifier {

    /**
     * A set of item ids that will be used to check if {@link DeathItemModifier#modify(Player, GameItem, boolean, List, List)}
     * will be called.
     */
    Set<Integer> getItemIds();

    /**
     * Modify the item dropped.
     *
     * This method is called when an item defined in {@link DeathItemModifier#getItemIds()}} is in a player's carried
     * or equipped items when they die.
     *
     *
     * @param gameItem The {@link GameItem} that is going to be dropped.
     * @param kept True if the item is going to be kept by the {@param player}.
     * @param keptItems List of kept items.
     * @param lostItems List of lost items.
     */
    void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems);

}
