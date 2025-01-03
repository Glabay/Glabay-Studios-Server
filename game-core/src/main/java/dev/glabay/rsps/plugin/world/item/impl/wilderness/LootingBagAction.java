package dev.glabay.rsps.plugin.world.item.impl.wilderness;

import dev.glabay.rsps.plugin.world.item.WorldItemAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.LOOTING_BAG_OPTIONS_DIALOGUE_ID;
import static io.xeros.model.definition.Items.LOOTING_BAG;
import static io.xeros.model.definition.Items.LOOTING_BAG_OPEN;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-04
 */
public class LootingBagAction extends WorldItemAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { LOOTING_BAG, LOOTING_BAG_OPEN };
    }

    @Override
    public Boolean handleActionOne(Player player, int itemId, int slotId, int interfaceId) {
        player.getLootingBag().toggleOpen();
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, int itemId, int slotId, int interfaceId) {
        player.getLootingBag().openDepositMode();
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, int itemId, int slotId, int interfaceId) {
        player.getDH().sendDialogues(LOOTING_BAG_OPTIONS_DIALOGUE_ID, -1);
        return true;
    }
}
