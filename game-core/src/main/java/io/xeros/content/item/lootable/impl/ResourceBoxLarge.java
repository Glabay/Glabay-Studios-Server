package io.xeros.content.item.lootable.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import io.xeros.content.item.lootable.ItemLootable;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.definition.Items;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.GameItemVariableAmount;

public class ResourceBoxLarge extends ItemLootable {

    public static final int BOX_ITEM = 30_002;

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return Map.of(LootRarity.COMMON, Lists.newArrayList(
                new GameItemVariableAmount(3050, 5, 7), //grimy lantadyme
                new GameItemVariableAmount(212, 5, 7), //grimy dwarf weed
                new GameItemVariableAmount(214, 5, 7), //grimy torstol
//                new GameItemVariableAmount(3052, 5, 7),  //lantadyme potion (unf)
//                new GameItemVariableAmount(216, 5, 7),  //dwarf weed potion (unf)
//                new GameItemVariableAmount(3003, 5, 7), //torstol potion (unf)
                new GameItemVariableAmount(454, 7, 10), //coal
                new GameItemVariableAmount(448, 7, 10), //mithril ore
                new GameItemVariableAmount(450, 7, 10), //adamantite ore
                new GameItemVariableAmount(452, 7, 10),  //runite ore
                new GameItemVariableAmount(2360,2, 5),  //mithril bar
                new GameItemVariableAmount(2362,2, 5), //adamantite bar
                new GameItemVariableAmount(2364,2, 5), //runite bar
                new GameItemVariableAmount(1516, 7, 10),  //yew logs
                new GameItemVariableAmount(1514, 7, 10),  //magic logs
                new GameItemVariableAmount(19670, 7, 10),  //redwood logs
                new GameItemVariableAmount(1620, 5, 7), //uncut ruby
                new GameItemVariableAmount(1618, 5, 7), //uncut diamond
                new GameItemVariableAmount(1632, 5, 7), //uncut dragonstone
                new GameItemVariableAmount(7945, 7, 10),  //raw monkfish
                new GameItemVariableAmount(3143, 7, 10), //raw karamwban
                new GameItemVariableAmount(384, 7, 10),  //raw shark
                new GameItemVariableAmount(390, 7, 10), //raw manta ray

                new GameItemVariableAmount(Items.LIMPWURT_ROOT_NOTED, 5, 10),
                new GameItemVariableAmount(Items.RED_SPIDERS_EGGS_NOTED, 10, 15),
                new GameItemVariableAmount(Items.MORT_MYRE_FUNGUS_NOTED, 10, 15),
                new GameItemVariableAmount(Items.CRUSHED_NEST_NOTED, 2, 4)
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
