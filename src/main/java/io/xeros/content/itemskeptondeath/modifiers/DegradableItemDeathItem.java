package io.xeros.content.itemskeptondeath.modifiers;

import io.xeros.content.items.Degrade;
import io.xeros.content.itemskeptondeath.DeathItemModifier;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.*;

public class DegradableItemDeathItem implements DeathItemModifier {

    private static final Set<Integer> ALL;

    static {
        ALL = new HashSet<>();
        Arrays.stream(Degrade.DegradableItem.values()).forEach(it -> ALL.add(it.getItemId()));
    }

    @Override
    public Set<Integer> getItemIds() {
        return ALL;
    }

    @Override
    public void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems) {
        if (kept)
            return;

        Degrade.DegradableItem degrade = Degrade.DegradableItem.forId(gameItem.id()).orElse(null);
        if (degrade == null)
            return;

        lostItems.remove(gameItem);
        Degrade.reset(player, degrade);

        if (degrade.getBrokenId() > 0 && ItemDef.forId(degrade.getBrokenId()).isTradable()) {
            lostItems.add(new GameItem(degrade.getBrokenId()));
        } else if (degrade.getCost() > 0) {
            lostItems.add(new GameItem(Items.COINS, degrade.getCost()));
        }
    }
}
