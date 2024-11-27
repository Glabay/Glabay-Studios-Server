package io.xeros.content.itemskeptondeath.modifiers;

import io.xeros.content.itemskeptondeath.DeathItemModifier;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.lootbag.LootingBagItem;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LootingBagDeathItem implements DeathItemModifier {
    @Override
    public Set<Integer> getItemIds() {
        return Set.of(LootingBag.LOOTING_BAG, LootingBag.LOOTING_BAG_OPEN);
    }

    @Override
    public void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems) {
        Iterator<LootingBagItem> iterator = player.getLootingBag().getLootingBagContainer().items.iterator();
        while (iterator.hasNext()) {
            LootingBagItem item = iterator.next();
            iterator.remove();
            if (item == null || item.id() <= 0 || item.amount() <= 0)
                continue;
            lostItems.add(item);
        }
    }
}
