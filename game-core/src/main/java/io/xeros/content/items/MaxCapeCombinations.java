package io.xeros.content.items;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum MaxCapeCombinations {
    ASSEMBLER(Items.AVAS_ASSEMBLER, Items.ASSEMBLER_MAX_CAPE, Items.ASSEMBLER_MAX_HOOD),
    ACCUMULATOR(Items.AVAS_ACCUMULATOR, Items.ACCUMULATOR_MAX_CAPE, Items.ACCUMULATOR_MAX_HOOD),
    ARDOUGNE(Items.ARDOUGNE_CLOAK_4, Items.ARDOUGNE_MAX_CAPE, Items.ARDOUGNE_MAX_HOOD),

    INFERNAL(Items.INFERNAL_CAPE, Items.INFERNAL_MAX_CAPE, Items.INFERNAL_MAX_HOOD),
    FIRE(Items.FIRE_CAPE, Items.FIRE_MAX_CAPE, Items.FIRE_MAX_HOOD),

    SARADOMIN(Items.SARADOMIN_CAPE, Items.SARADOMIN_MAX_CAPE, Items.SARADOMIN_MAX_HOOD),
    ZAMORAK(Items.ZAMORAK_CAPE, Items.ZAMORAK_MAX_CAPE, Items.ZAMORAK_MAX_HOOD),
    GUTHIX(Items.GUTHIX_CAPE, Items.GUTHIX_MAX_CAPE, Items.GUTHIX_MAX_HOOD),


    SARADOMIN_I(Items.IMBUED_SARADOMIN_CAPE, Items.IMBUED_SARADOMIN_MAX_CAPE, Items.IMBUED_SARADOMIN_MAX_HOOD),
    ZAMORAK_I(Items.IMBUED_ZAMORAK_CAPE, Items.IMBUED_ZAMORAK_MAX_CAPE, Items.IMBUED_ZAMORAK_MAX_HOOD),
    GUTHIX_I(Items.IMBUED_GUTHIX_CAPE, Items.IMBUED_GUTHIX_MAX_CAPE, Items.IMBUED_GUTHIX_MAX_HOOD),

    ;

    private final int combinedWithItemId;
    private final int newCapeItemId;
    private final int newHoodItemId;

    MaxCapeCombinations(int combinedWithItemId, int newCapeItemId, int newHoodItemId) {
        this.combinedWithItemId = combinedWithItemId;
        this.newCapeItemId = newCapeItemId;
        this.newHoodItemId = newHoodItemId;
    }

    public int getCombinedWithItemId() {
        return combinedWithItemId;
    }

    public int getNewCapeItemId() {
        return newCapeItemId;
    }

    public int getNewHoodItemId() {
        return newHoodItemId;
    }

    public static List<Integer> getMaxCapeIds() {
        List<Integer> capes = Arrays.stream(MaxCapeCombinations.values()).map(MaxCapeCombinations::getNewCapeItemId).collect(Collectors.toList());
        capes.add(Items.MAX_CAPE);
        return capes;
    }

    public static List<Integer> getMaxHoodIds() {
        List<Integer> capes = Arrays.stream(MaxCapeCombinations.values()).map(MaxCapeCombinations::getNewHoodItemId).collect(Collectors.toList());
        capes.add(Items.MAX_HOOD);
        return capes;
    }

    public static MaxCapeCombinations forUsedItems(int item1, int item2) {
        int itemSearch = item1 == Items.MAX_CAPE || item1 == Items.MAX_HOOD ? item2
            : item2 == Items.MAX_CAPE || item2 == Items.MAX_HOOD ? item1 : -1;
        if (itemSearch == -1)
            return null;
        return Arrays.stream(MaxCapeCombinations.values()).filter(it -> it.getCombinedWithItemId() == itemSearch).findFirst().orElse(null);
    }

    public static boolean mix(Player player, int item1, int item2) {
        MaxCapeCombinations combination = forUsedItems(item1, item2);
        if (combination == null)
            return false;

        String name = ItemDef.forId(combination.getNewCapeItemId()).getName();

        Consumer<Player> make = plr -> {
            plr.getPA().closeAllWindows();
            if (!plr.getItems().playerHasItem(Items.MAX_CAPE) || !plr.getItems().playerHasItem(combination.getCombinedWithItemId()))
                return;
            if (!plr.getItems().playerHasItem(Items.MAX_HOOD)) {
                player.sendMessage("You need a Max hood to do this.");
                return;
            }

            plr.getItems().deleteItem(Items.MAX_HOOD, 1);
            plr.getItems().deleteItem(Items.MAX_CAPE, 1);
            plr.getItems().deleteItem(combination.getCombinedWithItemId(), 1);
            plr.getItems().addItem(combination.getNewHoodItemId(), 1);
            plr.getItems().addItem(combination.getNewCapeItemId(), 1);
            plr.sendMessage("You've created the {}.", name);
        };

        new DialogueBuilder(player).option(
                new DialogueOption("Combine this to create the " + name + ".", make),
                DialogueOption.nevermind()
        ).send();

        return true;
    }
}
