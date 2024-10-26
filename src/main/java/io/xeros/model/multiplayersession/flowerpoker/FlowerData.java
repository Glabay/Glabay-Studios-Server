package io.xeros.model.multiplayersession.flowerpoker;

import com.google.common.base.Preconditions;
import io.xeros.annotate.Init;
import io.xeros.util.Misc;

import java.util.Arrays;

// https://oldschool.runescape.wiki/w/Mithril_seeds
public enum FlowerData {
    RED(2981, 14.08),
    BLUE(2982, 15.3),
    YELLOW(2983, 14.65),
    PURPLE(2984, 14.85),
    ORANGE(2985, 15.38), // Change from 15.39 to 15.38 because all flowers total 100.1 otherwise
    MIXED(2986, 14.66),
    ASSORTED(2980, 10.78),
    BLACK(2988, 0.2),
    WHITE(2987, 0.1),
    ;

    public final int objectId;

    public final double chance;

    FlowerData(int objectId, double chance) {
        this.objectId = objectId;
        this.chance = chance;
    }

    @Init
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static void check() {
        double sum = Arrays.stream(FlowerData.values()).mapToDouble(it -> it.chance).sum();
        Preconditions.checkState(sum == 100.0, "Flowers sum doesn't equal 100, sum=", sum);

        sum = Arrays.stream(FlowerData.values()).mapToDouble(it -> it.chance * 100).sum();
        Preconditions.checkState(sum == 10_000.0, "Flowers x 100 sum doesn't equal 10,000, sum=", sum);
    }

    public static FlowerData getRandomFlower() {
        return getRandomFlower(Misc.trueRand(10_000));
    }

    /**
     * Roll should be an integer between 0 inclusive and 10,000 exclusive.
     */
    public static FlowerData getRandomFlower(int roll) {
        Preconditions.checkArgument(roll >= 0 && roll < 10_000, "Roll doesn't meet criteria.");
        int floor = 0;
        for (FlowerData flower : FlowerData.values()) {
            int chance = (int) (flower.chance * 100.0);
            if (roll >= floor && roll < chance + floor)
                return flower;
            floor += chance;
        }

        throw new IllegalStateException();
    }
}