package io.xeros.content.item.lootable.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import io.xeros.content.item.lootable.ItemLootable;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.definition.Items;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.GameItemVariableAmount;

public class ResourceBoxSmall extends ItemLootable {

    public static final int BOX_ITEM = 30_000;

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return Map.of(LootRarity.COMMON, Lists.newArrayList(
                new GameItemVariableAmount(200, 1, 3), //grimy guam
                new GameItemVariableAmount(202, 1, 3), //grimy marrentill
                new GameItemVariableAmount(204, 1, 3), //grimy tarromin
                new GameItemVariableAmount(206, 1, 3),  //grimy harralander
                new GameItemVariableAmount(208, 1, 3),  //grimy ranarr weed

//                new GameItemVariableAmount(92, 1, 3), //guam potion (unf)
//                new GameItemVariableAmount(94, 1, 3), //marrentill potion (unf)
//                new GameItemVariableAmount(96, 1, 3), //tarromin potion (unf)
//                new GameItemVariableAmount(98, 1, 3),  //harralander potion (unf)
//                new GameItemVariableAmount(100, 1, 3),  //ranarr potion (unf)

                new GameItemVariableAmount(441, 1, 5), //iron ore
                new GameItemVariableAmount(454, 1, 5), //coal
                new GameItemVariableAmount(448, 1, 5), //mithril ore
                new GameItemVariableAmount(2350, 1, 2),  //bronze bar
                new GameItemVariableAmount(2352, 1, 2),  //iron bar
                new GameItemVariableAmount(2354, 1, 2), //steel bar
                new GameItemVariableAmount(2360, 1, 2), //mithril bar
                new GameItemVariableAmount(1512, 1, 5), //logs
                new GameItemVariableAmount(1522, 1, 5),  //oak logs
                new GameItemVariableAmount(1520, 1, 5),  //willow logs
                new GameItemVariableAmount(1518, 1, 5), //maple logs
                new GameItemVariableAmount(1624, 1, 3), //uncut sapphire
                new GameItemVariableAmount(1622, 1, 3), //uncut emerald
                new GameItemVariableAmount(360, 1, 3),  //raw tuna
                new GameItemVariableAmount(378, 1, 3),  //raw lobster
                new GameItemVariableAmount(364, 1, 3), //raw bass
                new GameItemVariableAmount(372, 1, 3), //raw swordfish

                new GameItemVariableAmount(Items.LIMPWURT_ROOT_NOTED, 3, 6),
                new GameItemVariableAmount(Items.RED_SPIDERS_EGGS_NOTED, 5, 10)

        ));
    }

    @Override
    public int getLootableItem() {
        return BOX_ITEM;
    }

    @Override
    public int getRollCount() {
        return 3;
    }
}
