package io.xeros.model.items.interactions;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.interactions.inter.*;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-04
 */
public abstract class WorldItemAction extends ItemAction
                                      implements ItemActionI,
                                                 ItemActionOne,
                                                 ItemActionTwo,
                                                 ItemActionThree,
                                                 ItemActionFour {
    protected abstract Integer[] getIds();

    public boolean performedAction(Player player, int itemId, int slotId, int interfaceId, int itemActionId) {
        if (!isEnabled()) {
            logger.info("{} has just tried to use a disabled Item: {}", player.getDisplayName(), itemId);
            player.sendMessage("This Item is currently disabled.");
            return false;
        }

        return switch (itemActionId) {
            case 1 -> handleActionOne(player, itemId, slotId, interfaceId);
            case 2 -> handleActionTwo(player, itemId, slotId, interfaceId);
            case 3 -> handleActionThree(player, itemId, slotId, interfaceId);
            case 4 -> handleActionFour(player, itemId, slotId, interfaceId);
            default -> throw new IllegalArgumentException(
                String.format(
                    "Invalid action Id: %d [ItemId: %d](Slot: %d)",
                    itemActionId,
                    itemId,
                    slotId)
            );
        };
    }
}
