package io.xeros.content.item.lootable.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.model.definition.Items;
import io.xeros.model.definition.Npcs;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;

public class TheatreOfBloodChest implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(Items.DEATH_RUNE, 600),
                new GameItem(Items.BLOOD_RUNE, 600),
                new GameItem(Items.COAL_NOTED, 600),
                new GameItem(Items.GOLD_ORE_NOTED, 360),
                new GameItem(Items.ADAMANTITE_ORE_NOTED, 150),
                new GameItem(Items.RUNITE_ORE_NOTED, 72),
                new GameItem(Items.GRIMY_CADANTINE_NOTED, 60),
                new GameItem(Items.GRIMY_AVANTOE_NOTED, 48),
                new GameItem(Items.GRIMY_TOADFLAX_NOTED, 44),
                new GameItem(Items.GRIMY_KWUARM_NOTED, 43),
                new GameItem(Items.GRIMY_IRIT_LEAF_NOTED, 40),
                new GameItem(Items.GRIMY_RANARR_WEED_NOTED, 36),
                new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 32),
                new GameItem(Items.GRIMY_LANTADYME_NOTED, 31),
                new GameItem(Items.GRIMY_DWARF_WEED_NOTED, 28),
                new GameItem(Items.GRIMY_TORSTOL_NOTED, 24),
                new GameItem(Items.BATTLESTAFF_NOTED, 18),
                new GameItem(Items.RUNE_BATTLEAXE_NOTED, 4),
                new GameItem(Items.RUNE_PLATEBODY_NOTED, 4),
                new GameItem(Items.RUNE_CHAINBODY_NOTED, 4),
                new GameItem(995, 7_500_000)
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(Items.JUSTICIAR_FACEGUARD),
                new GameItem(Items.JUSTICIAR_FACEGUARD),
                new GameItem(Items.JUSTICIAR_FACEGUARD),

                new GameItem(Items.JUSTICIAR_CHESTGUARD),
                new GameItem(Items.JUSTICIAR_CHESTGUARD),
                new GameItem(Items.JUSTICIAR_CHESTGUARD),

                new GameItem(Items.JUSTICIAR_LEGGUARDS),
                new GameItem(Items.JUSTICIAR_LEGGUARDS),
                new GameItem(Items.JUSTICIAR_LEGGUARDS),

                new GameItem(Items.GHRAZI_RAPIER),
                new GameItem(Items.GHRAZI_RAPIER),
                new GameItem(Items.GHRAZI_RAPIER),

                new GameItem(Items.SANGUINESTI_STAFF),
                new GameItem(Items.SANGUINESTI_STAFF),
                new GameItem(Items.SANGUINESTI_STAFF),

                new GameItem(Items.AVERNIC_DEFENDER_HILT),
                new GameItem(Items.AVERNIC_DEFENDER_HILT),
                new GameItem(Items.AVERNIC_DEFENDER_HILT),
                new GameItem(Items.AVERNIC_DEFENDER_HILT),
                new GameItem(Items.AVERNIC_DEFENDER_HILT),
                new GameItem(Items.AVERNIC_DEFENDER_HILT),
                new GameItem(Items.AVERNIC_DEFENDER_HILT),
                new GameItem(Items.AVERNIC_DEFENDER_HILT),

                new GameItem(Items.SCYTHE_OF_VITUR)

        ));
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.id() == f.id()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    public static List<GameItem> getRandomItems(boolean rollRares, int size) {
        List<GameItem> rewards = Lists.newArrayList();
        int rareChance = 9;
        if (Hespori.activeKronosSeed) {
            rareChance = 5;
        }

        if (rollRares && Misc.trueRand(rareChance) == 0) {
            rewards.add(Misc.getRandomItem(items.get(LootRarity.RARE)));
        } else {
            for (int count = 0; count < 3; count++) {
                rewards.add(Misc.getRandomItem(items.get(LootRarity.COMMON)));
            }
        }
        return rewards;
    }

    public static boolean containsRare(List<GameItem> itemList) {
        return items.get(LootRarity.RARE).stream().anyMatch(rareItem -> itemList.stream().anyMatch(itemReward -> rareItem.id() == itemReward.id()));
    }

    /**
     * Reward items that are generated when the treasure room is initialised.
     */
    public static void rewardItems(Player player, List<GameItem> rewards) {
        BossPoints.addManualPoints(player, "theatre of blood");

        PetHandler.roll(player, PetHandler.Pets.LIL_ZIK);
        player.getEventCalendar().progress(EventChallenge.COMPLETE_TOB);
        LeaderboardUtils.addCount(LeaderboardType.TOB, player, 1);
        Achievements.increase(player, AchievementType.TOB, 1);
        if (Hespori.activeKronosSeed == true) {
            player.sendMessage("@red@The @gre@Kronos seed@red@ doubles your chances!" );
        }
        player.getItems().addItem(995, 500_000 + Misc.random(1_000_000));
        List<GameItem> rareItemList = items.get(LootRarity.RARE);
        for (GameItem reward : rewards) {
            if (rareItemList.stream().anyMatch(rareItem -> reward.id() == rareItem.id())) {
                if (!player.getDisplayName().equalsIgnoreCase("thimble") && !player.getDisplayName().equalsIgnoreCase("top hat")) {
                    PlayerHandler.executeGlobalMessage("@pur@" + player.getDisplayNameFormatted() + " received a drop: "
                            + ItemDef.forId(reward.id()).getName() + " x " + reward.amount() + " from Theatre of Blood.");
                }
                player.getCollectionLog().handleDrop(player, Npcs.THE_MAIDEN_OF_SUGADINTI, rewards.get(0).id(), 1);
            }
        }

        for (GameItem item : rewards) {
            player.getInventory().addAnywhere(new ImmutableItem(item.id(), item.amount()));
        }

        player.getTobContainer().displayRewardInterface(rewards);
    }

    /**
     * To be removed but kept for now.
     */
    @Override
    public void roll(Player player) {}
}
