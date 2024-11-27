package io.xeros.content.trails;


import com.google.common.collect.Lists;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.List;
import java.util.Optional;

public class TreasureTrailsRewardItem {

    public static List<GameItem> toGameItems(List<TreasureTrailsRewardItem> list) {
        List<GameItem> items = Lists.newArrayList();
        for (TreasureTrailsRewardItem reward : list) {
            Optional<GameItem> existing = items.stream().filter(it -> it.id() == reward.getItemId()).findFirst();
            if (existing.isPresent()) {
                GameItem gameItem = existing.get();
                gameItem.setAmount(gameItem.amount() + reward.getRandomAmount());
                continue;
            }

            items.add(reward.toGameItem());
        }

        return items;
    }

    private final int itemId;
    private final int minimumAmount;
    private final int maximumAmount;
    private final RewardRarity rarity;

    public TreasureTrailsRewardItem(int itemId, int minimumAmount, int maximumAmount, RewardRarity rarity) {
        this.itemId = itemId;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.rarity = rarity;
    }

    public TreasureTrailsRewardItem copy() {
        return new TreasureTrailsRewardItem(itemId, minimumAmount, maximumAmount, rarity);
    }

    public GameItem toGameItem() {
        return new GameItem(itemId, getRandomAmount());
    }

    public int getRandomAmount() {
        return Misc.random(getMinimumAmount(), getMaximumAmount());
    }

    public int getItemId() {
        return itemId;
    }

    public int getMinimumAmount() {
        return minimumAmount;
    }

    public int getMaximumAmount() {
        return maximumAmount;
    }

    public RewardRarity getRarity() {
        return rarity;
    }
}
