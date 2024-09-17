package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class HesporiChest implements Lootable {

    private static final int KEY = Hespori.KEY;
    private static final int ANIMATION = 881;

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                //UPDATE HESPORICHESTITEMS TOO
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
                // UPDATE HESPORICHESTITEMS TOO
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

    private static GameItem randomChestRewardsCommon() {

        List<GameItem> itemList = HesporiChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    private static GameItem randomChestRewardsRare() {
        List<GameItem> itemList = HesporiChestItems.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
        int random = Misc.random(1000);
        int rareChance = 950;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 920;
            c.getItems().deleteItem(21046, 1);
            c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        if (random < rareChance) {
            if (c.getItems().playerHasItem(KEY)) {
                c.getItems().deleteItem(KEY, 1);
                c.getItems().addItem(995, 500_000 + Misc.random(1_000_000));
                c.startAnimation(ANIMATION);
                GameItem reward = randomChestRewardsCommon();
                GameItem reward2 = randomChestRewardsCommon();
                c.getItems().addItem(reward.getId(), reward.getAmount() * 1);
                c.getItems().addItem(reward2.getId(), reward2.getAmount() * 1);
                c.sendMessage("@blu@You received common items out of the chest.");

            } else {
                c.sendMessage("@blu@The chest is locked, it won't budge!");
            }
        } else if (random >= rareChance) {
            if (c.getItems().playerHasItem(KEY)) {
                c.getItems().deleteItem(KEY, 1);
                c.getItems().addItem(995, 500_000 + Misc.random(1_000_000));
                c.startAnimation(ANIMATION);
                GameItem reward = randomChestRewardsRare();
                c.getItems().addItem(reward.getId(), reward.getAmount() * 1);
                if (reward.getId() != Items.TOME_OF_FIRE_EMPTY) {
                    ItemDef def = ItemDef.forId(reward.getId());
                    PlayerHandler.executeGlobalMessage("@bla@[<col=3d7018>HESPORI@bla@] The Hespori chest rewarded a <col=47831c>rare seed!");
                }
            } else {
                c.sendMessage("@blu@The chest is locked, it won't budge!");
            }
        }
    }
}
