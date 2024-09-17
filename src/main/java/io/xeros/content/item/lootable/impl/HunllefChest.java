package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class HunllefChest implements Lootable {

    private static final int KEY = 23776;
    private static final int ANIMATION = 881;

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(

                new GameItem(21046, 2),//15% chest rate tomb
                new GameItem(995, 240000), //coins
                new GameItem(2996, 35),//pkp tickets
                new GameItem(537, 20 + Misc.random(10)),//dragon bones
                new GameItem(1306, 3),//dragon longsword
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 300),// magic logs
                new GameItem(1632, 80),//uncut dragonstone
                new GameItem(11230, 20 + Misc.random(30)),//darts
                new GameItem(1080, 6),//rune platelegs
                new GameItem(1128, 6),//rune platebody
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(4151, 1),//whip
                new GameItem(23804, 1),//imbuedifier

                new GameItem(21046, 2),//15% chest rate tomb
                new GameItem(995, 240000), //coins
                new GameItem(2996, 35),//pkp tickets
                new GameItem(537, 20 + Misc.random(10)),//dragon bones
                new GameItem(1306, 3),//dragon longsword
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 300),// magic logs
                new GameItem(1632, 80),//uncut dragonstone
                new GameItem(11230, 20 + Misc.random(30)),//darts
                new GameItem(1080, 6),//rune platelegs
                new GameItem(1128, 6),//rune platebody
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(4151, 1),//whip
                new GameItem(23804, 1),//imbuedifier

                new GameItem(21547, 1),//small foe bone
                new GameItem(21547, 1),//small foe bone
                new GameItem(21547, 1),//small foe bone
                new GameItem(21547, 1),//small foe bone
                new GameItem(21549, 1),//medium foe bone
                new GameItem(21549, 1),//medium foe bone
                new GameItem(21549, 1),//medium foe bone
                new GameItem(21551, 1),//large foe bone
                new GameItem(21551, 1),//large foe bone
                new GameItem(21553, 1)//rare foe bone
                ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(23975, 1), //crystal body-----------------------------2
                new GameItem(23971, 1), //crystal helm
                new GameItem(23979, 1), //crystal legs
                new GameItem(23757, 1), //younglleff pet 	 RARES//////////////////////////////////
                new GameItem(23995, 1), //blade of saeldor
                new GameItem(23848, 1), //corrupt legs
                new GameItem(23842, 1), //corrupt helm
                new GameItem(23845, 1), //corrupt body
                new GameItem(23848, 1), //corrupt legs
                new GameItem(23842, 1), //corrupt helm
                new GameItem(23845, 1), //corrupt body  	 RARES//////////////////////////////////
                new GameItem(23975, 1), //crystal body-----------------------------1
                new GameItem(23971, 1), //crystal helm
                new GameItem(23979, 1), //crystal legs
                new GameItem(23975, 1), //crystal body-----------------------------2
                new GameItem(23971, 1), //crystal helm
                new GameItem(23979, 1), //crystal legs
                new GameItem(23975, 1), //crystal body-----------------------------3
                new GameItem(23971, 1), //crystal helm
                new GameItem(23979, 1), //crystal legs
                new GameItem(23975, 1), //crystal body-----------------------------4
                new GameItem(23971, 1), //crystal helm
                new GameItem(23979, 1), //crystal legs
                new GameItem(23975, 1), //crystal body-----------------------------5
                new GameItem(23971, 1), //crystal helm
                new GameItem(23979, 1), //crystal legs
                new GameItem(23975, 1), //crystal body-----------------------------6
                new GameItem(23971, 1), //crystal helm
                new GameItem(23979, 1) //crystal legs
        ));
    }

    private static GameItem commonChestRewards() {

        List<GameItem> itemList = randomNumber() < 850 ? items.get(LootRarity.COMMON) : items.get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);

    }

    private static GameItem rareChestRewards(int rareChance) {

        List<GameItem> itemList = randomNumber() >= rareChance ? items.get(LootRarity.RARE) : items.get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);

    }

    public final static int randomNumber() {
        final int random = Misc.random(1000);
        return random;
    }

    public static void rolledCommon(Player c) {
        int crystalshardbonus = Misc.random(29) + 10;
        if (randomNumber() < 750) { //not a rare
            if (c.getItems().playerHasItem(KEY)) {
                c.getItems().deleteItem(KEY, 1);
                c.startAnimation(ANIMATION);
                GameItem commonreward = commonChestRewards();
                GameItem commonreward2 = commonChestRewards();
                GameItem commonreward3 = commonChestRewards();
                c.getItems().addItem(commonreward.getId(), commonreward.getAmount() * 1);
                c.getItems().addItem(commonreward2.getId(), commonreward2.getAmount() * 1);
                c.getItems().addItem(commonreward3.getId(), commonreward3.getAmount()* 1);
                c.getItems().deleteItem(21046, 1);
                c.getItems().addItem(23877, crystalshardbonus);
            } else if (!(c.getItems().playerHasItem(KEY))) {
                c.sendMessage("@blu@The chest is locked, it won't budge!");
            }
        }
    }
    public static void rolledRare(Player c, int rareChance) {
        int crystalshardbonus = Misc.random(29) + 10;
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem rarereward = rareChestRewards(rareChance);
            if (rarereward.getId() == 23757 && c.getItems().getItemCount(23757, false) == 0) {
                c.getCollectionLog().handleDrop(c, 5, 23757, 1);
            }
            c.getItems().addItem(rarereward.getId(), rarereward.getAmount() * 1);
            if (c.getItems().playerHasItem(21046)) {
                c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
                c.getItems().deleteItem(21046, 1);
                c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            }
            c.getItems().addItem(23877, crystalshardbonus);
            NPCDeath.announce(c, rarereward, Npcs.CRYSTALLINE_HUNLLEF);
            //PlayerHandler.executeGlobalMessage("@red@[Hunllef] @pur@" + c.playerName + " has just received a rare item from Hunllef's chest.");
        } else if (!(c.getItems().playerHasItem(KEY))) {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
        final int random = randomNumber();
        int rareChance = 850;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 830;
        }
        if (random < rareChance) {
            rolledCommon(c);
        } else if (random >= rareChance) {
            rolledRare(c, rareChance);
        }
    }
}
