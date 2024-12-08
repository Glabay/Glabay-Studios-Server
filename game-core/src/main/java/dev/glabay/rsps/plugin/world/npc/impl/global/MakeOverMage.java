package dev.glabay.rsps.plugin.world.npc.impl.global;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Interfaces.MAKEOVER_MAGE_INTERFACE;
import static io.xeros.model.definition.Npcs.*;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class MakeOverMage extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { MAKEOVER_MAGE, MAKEOVER_MAGE_1307, MAKEOVER_MAGE_8487 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        if (player.getItems().isWearingItems()) {
            player.sendMessage("You must remove your equipment before changing your appearance.");
            player.canChangeAppearance = false;
        }
        else {
            player.getPA().showInterface(MAKEOVER_MAGE_INTERFACE);
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
            player.getPA().showInterface(MAKEOVER_MAGE_INTERFACE);
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
            player.getPA().showInterface(MAKEOVER_MAGE_INTERFACE);
            player.canChangeAppearance = true;
        }
        return true;
    }
}
