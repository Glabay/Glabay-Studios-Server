package io.xeros.content.item.lootable;

import java.util.Arrays;
import java.util.Optional;

import com.google.common.base.Preconditions;

/**
 * Represents the rarity of a certain list of items
 */
public enum MysteryBoxRarity {
    COMMON(LootRarity.COMMON, "<col=336600>"),
    UNCOMMON(LootRarity.UNCOMMON, "<col=ffff00>"),
    RARE(LootRarity.RARE, "<col=B80000>");

    private final LootRarity lootRarity;
    private final String color;

    MysteryBoxRarity(LootRarity lootRarity, String color) {
        this.lootRarity = lootRarity;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public static MysteryBoxRarity forId(int id) {
        Optional<MysteryBoxRarity> rarity = Arrays.stream(values()).filter(r -> r.ordinal() == id).findFirst();
        Preconditions.checkState(rarity.isPresent(), "No rarity: " + id);
        return rarity.get();
    }

    public LootRarity getLootRarity() {
        return lootRarity;
    }
}
