package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.*;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class makeovermage extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { MAKE_OVER_MAGE, MAKE_OVER_MAGE_2, MAKE_OVER_MAGE_3 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        if (player.getItems().isWearingItems()) {
            player.sendMessage("You must remove your equipment before changing your appearance.");
            player.canChangeAppearance = false;
        }
        else {
            player.getPA().showInterface(3559);
            player.canChangeAppearance = true;
        }
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        if (player.getItems().isWearingItems()) {
            player.sendMessage("You must remove your equipment before changing your appearance.");
            player.canChangeAppearance = false;
        } else {
            player.getPA().showInterface(3559);
            player.canChangeAppearance = true;
        }
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        if (player.getItems().isWearingItems()) {
            player.sendMessage("You must remove your equipment before changing your appearance.");
            player.canChangeAppearance = false;
        } else {
            player.getPA().showInterface(3559);
            player.canChangeAppearance = true;
        }
        return true;
    }
}
