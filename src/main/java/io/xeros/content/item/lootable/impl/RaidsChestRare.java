package io.xeros.content.item.lootable.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class RaidsChestRare implements Lootable {

    private static final int KEY = Raids.RARE_KEY;
    private static final int ANIMATION = 881;

    public static void main(String[] args) throws Exception {
        ItemDef.load();
        HashMap<GameItem, Integer> map = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            GameItem gameItem = randomChestRewards();
            int amount = map.getOrDefault(gameItem, 0);
            map.put(gameItem, amount + 1);
        }

        map.forEach((gameItem, amount) -> {
            String dropChance = String.format("%.2f", ((double) amount / 10000) * 100);
            String itemName = ItemDef.forId(gameItem.id()).getName();
            System.out.println("Rolled a " + itemName + " " + amount + " times. " + dropChance);
        });

    }

    private static GameItem randomChestRewards() {
        List<GameItem> itemList = RaidsChestItems.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = RaidsChestItems.getItems().get(LootRarity.RARE);
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
        return RaidsChestItems.getItems();
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward =  randomChestRewards();
            c.getCollectionLog().handleDrop(c, 7554, reward.id(), reward.amount());
            if (reward.id() == 20851 && c.getItems().getItemCount(20851, false) == 0) {
                c.getCollectionLog().handleDrop(c, 5, 20851, 1);
            }
            c.getItems().addItem(reward.id(), reward.amount() * 1); //potentially gives the loot 3 times.
            c.sendMessage("@blu@You have received a rare item out of the storage unit.");
            NPCDeath.announceKc(c, reward, c.raidCount);
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
