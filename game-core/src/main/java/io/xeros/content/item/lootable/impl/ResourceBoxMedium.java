package io.xeros.content.item.lootable.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import io.xeros.content.item.lootable.ItemLootable;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.definition.Items;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.GameItemVariableAmount;

public class ResourceBoxMedium extends ItemLootable {

    public static final int BOX_ITEM = 30_001;

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return Map.of(LootRarity.COMMON, Lists.newArrayList(
                new GameItemVariableAmount(3050, 2, 5), //grimy toadflax
                new GameItemVariableAmount(212, 2, 5), //grimy avantoe
                new GameItemVariableAmount(214, 2, 5), //grimy kwuarm
                new GameItemVariableAmount(3052, 2, 5),  //grimy snapdragon
                new GameItemVariableAmount(216, 2, 5),  //grimy cadantine
//                new GameItemVariableAmount(3003, 2, 5), //toadflax potion (unf)
//                new GameItemVariableAmount(104, 2, 5), //avantoe potion (unf)
//                new GameItemVariableAmount(106, 2, 5), //kwuarm potion (unf)
//                new GameItemVariableAmount(3005, 2, 5),  //snapdragon potion (unf)
//                new GameItemVariableAmount(108, 2, 5),  //cadantine potion (unf)
                new GameItemVariableAmount(454, 5, 7), //coal
                new GameItemVariableAmount(448, 5, 7), //mithril ore
                new GameItemVariableAmount(450, 5, 7), //adamantite ore
                new GameItemVariableAmount(452, 5, 7),  //runite ore
                new GameItemVariableAmount(2360, 1, 5),  //mithril bar
                new GameItemVariableAmount(2362, 1, 5), //adamantite bar
                new GameItemVariableAmount(2364, 1, 5), //runite bar
                new GameItemVariableAmount(1518, 5, 7), //maple logs
                new GameItemVariableAmount(1516, 5, 7),  //yew logs
                new GameItemVariableAmount(1514, 5, 7),  //magic logs
                new GameItemVariableAmount(1620, 2, 5), //uncut ruby
                new GameItemVariableAmount(1618, 2, 5), //uncut diamond
                new GameItemVariableAmount(378, 5, 7), //raw lobster
                new GameItemVariableAmount(372, 5, 7),  //raw swordfish
                new GameItemVariableAmount(7945, 5, 7),  //raw monkfish
                new GameItemVariableAmount(3143, 5, 7), //raw karamwban

                new GameItemVariableAmount(Items.LIMPWURT_ROOT_NOTED, 3, 6),
                new GameItemVariableAmount(Items.RED_SPIDERS_EGGS_NOTED, 5, 10),
                new GameItemVariableAmount(Items.MORT_MYRE_FUNGUS_NOTED, 5, 10),
                new GameItemVariableAmount(Items.CRUSHED_NEST_NOTED, 1, 2)
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
