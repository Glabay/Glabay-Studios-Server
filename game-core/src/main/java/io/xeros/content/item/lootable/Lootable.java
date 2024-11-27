package io.xeros.content.item.lootable;

import java.util.List;
import java.util.Map;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

public interface Lootable {

    Map<LootRarity, List<GameItem>> getLoot();

    void roll(Player player);

}
