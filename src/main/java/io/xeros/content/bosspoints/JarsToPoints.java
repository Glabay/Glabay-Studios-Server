package io.xeros.content.bosspoints;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;

import java.util.Arrays;
import java.util.OptionalInt;

public class JarsToPoints {

    public static final int BOSS_POINTS = 500;
    public static final int FOE_POINTS = 2_500;

    public static final int[] JARS = {
            Items.JAR_OF_CHEMICALS,
            Items.JAR_OF_DARKNESS,
            Items.JAR_OF_DECAY,
            Items.JAR_OF_DIRT,
            Items.JAR_OF_DREAMS,
            Items.JAR_OF_EYES,
            Items.JAR_OF_MIASMA,
            Items.JAR_OF_SAND,
            Items.JAR_OF_SOULS,
            Items.JAR_OF_STONE,
            Items.JAR_OF_SWAMP,
    };

    public static boolean open(Player c, int item) {
        OptionalInt jar = Arrays.stream(JARS).filter(it -> it == item).findFirst();
        if (jar.isPresent()) {
            int id = jar.getAsInt();

            c.start(new DialogueBuilder(c)
                .itemStatement(id, "Would you like to convert this jar to " + BOSS_POINTS + " Boss points?", "You can also burn it in the Fire of Exchange.")
                .option(
                        new DialogueOption("Yes, trade jar for " + BOSS_POINTS + " Boss points.", plr -> {
                            if (plr.getItems().playerHasItem(id)) {
                                ItemDef def = ItemDef.forId(id);
                                c.getItems().deleteItem(def.getId(), 1);
                                c.bossPoints += BOSS_POINTS;
                                c.getQuestTab().updateInformationTab();
                                c.start(new DialogueBuilder(c).itemStatement(id, "You receive @blu@" + BOSS_POINTS + " @bla@boss points."));
                            }
                        }),
                        new DialogueOption("No, keep the jar.", plr -> plr.getPA().closeAllWindows())
                )
            );

            return true;
        }

        return false;
    }

}
