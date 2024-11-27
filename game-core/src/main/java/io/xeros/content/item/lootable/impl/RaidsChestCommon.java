package io.xeros.content.item.lootable.impl;

import java.util.List;
import java.util.Map;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class RaidsChestCommon implements Lootable {

    private static final int KEY = Raids.COMMON_KEY;
    private static final int ANIMATION = 881;



    private static GameItem randomChestRewards() {
        List<GameItem> itemList = RaidsChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return RaidsChestItems.getItems();
    }

    @Override
    public void roll(Player c) {
        int twistedhornsroll = Misc.random(120);
        if (twistedhornsroll == 1) {
            c.getItems().addItem(24466, 1);
            PlayerHandler.executeGlobalMessage("@bla@[@blu@RAIDS@bla@] "+ c.getDisplayName() +"@pur@ has just received twisted horns.");
        }
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward =  randomChestRewards();
            GameItem reward2 = randomChestRewards();
            GameItem reward3 = randomChestRewards();
            c.getItems().addItem(reward.id(), reward.amount() * 1); //potentially gives the loot 3 times.
            c.getItems().addItem(reward2.id(), reward2.amount() * 1); //potentially gives the loot 3 times.
            c.getItems().addItem(reward3.id(), reward3.amount()* 1); //potentially gives the loot 3 times.
            c.sendMessage("@blu@You received a common item out of the storage unit.");
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
