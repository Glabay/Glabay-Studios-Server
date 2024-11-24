package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.definition.Items;
import io.xeros.model.items.GameItem;

public class HesporiChestItems {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(21046, 2),//15% chest rate tomb
                new GameItem(Items.BURNT_PAGE, 25),
                new GameItem(Items.OVERLOAD_4, 3),
                new GameItem(Items.WILLOW_LOGS_NOTED, 251),
                new GameItem(Items.MAPLE_LOGS_NOTED, 181),
                new GameItem(Items.YEW_LOGS_NOTED, 73),
                new GameItem(Items.MAGIC_LOGS_NOTED, 54),

                new GameItem(Items.YEW_ROOTS_NOTED, 30),
                new GameItem(Items.MAGIC_ROOTS_NOTED, 30),
                new GameItem(Items.CRUSHED_NEST_NOTED, 30),
                new GameItem(Items.WINE_OF_ZAMORAK_NOTED, 30),
                new GameItem(Items.SNAPE_GRASS_NOTED, 40),

                new GameItem(Items.DRAGON_DART_TIP, 59),
                new GameItem(Items.RUNE_DART_TIP, 86),
                new GameItem(Items.ADAMANT_DART_TIP, 91),

                new GameItem(Items.RUNE_ARROWTIPS, 120),
                new GameItem(Items.DRAGON_ARROWTIPS, 80),

                new GameItem(Items.DRAGON_BOLTS_UNF, 80),
                new GameItem(Items.RUNITE_BOLTS_UNF, 100),

                new GameItem(Items.RAW_SHARK_NOTED, 70),
                new GameItem(Items.RAW_LOBSTER_NOTED, 110),

                new GameItem(Items.STEEL_BAR_NOTED, 52),
                new GameItem(Items.MITHRIL_BAR_NOTED, 41),
                new GameItem(Items.ADAMANTITE_BAR_NOTED, 27),
                new GameItem(Items.RUNITE_BAR_NOTED, 21),

                new GameItem(Items.UNCUT_SAPPHIRE_NOTED, 41),
                new GameItem(Items.UNCUT_DIAMOND_NOTED, 21),

                new GameItem(Items.RANARR_POTION_UNF_NOTED, 16),
                new GameItem(Items.DWARF_WEED_POTION_UNF_NOTED, 21),
                new GameItem(Items.HARRALANDER_POTION_UNF_NOTED, 31),


                new GameItem(Items.RANARR_WEED_NOTED, 12),
                new GameItem(Items.HARRALANDER_NOTED, 24),
                new GameItem(Items.IRIT_LEAF_NOTED, 27),

                new GameItem(Items.TORSTOL_SEED, 4),
                new GameItem(Items.RANARR_SEED, 6),
                new GameItem(Items.TOADFLAX_SEED, 5),
                new GameItem(Items.IRIT_SEED, 11)


                ));


        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(Items.TOME_OF_FIRE_EMPTY, 1),
                new GameItem(Items.TOME_OF_FIRE_EMPTY, 1),
                new GameItem(Items.ATTAS_SEED, 1),
                new GameItem(Items.IASOR_SEED, 1),
                new GameItem(Items.KRONOS_SEED, 1),
                new GameItem(Items.GOLPAR_SEED, 1),
                new GameItem(Items.NOXIFER_SEED, 1),
                new GameItem(Items.BUCHU_SEED, 1),
                new GameItem(Items.CELASTRUS_SEED, 1),
                new GameItem(Items.CONSECRATION_SEED, 1),
                new GameItem(Items.KELDA_SEED, 1)
                ));
    }

}
