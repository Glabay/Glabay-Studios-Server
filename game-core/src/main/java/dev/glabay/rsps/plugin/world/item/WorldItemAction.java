package dev.glabay.rsps.plugin.world.item;

import dev.glabay.rsps.plugin.world.item.inter.*;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

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
    ItemActionFour,
    ItemOnEntity,
    ItemOnItem,
    ItemOnObject,
    ItemOperation {
    protected abstract Integer[] getIds();

    public boolean performActionItemOperation(Player player, int itemId) {
        if (!isEnabled()) {
            logger.info("{} has just tried to use a disabled Item: {}", player.getDisplayName(), itemId);
            player.sendMessage("This Item is currently disabled.");
            return false;
        }
        return handleItemOperation(player, itemId);
    }

    public boolean performActionOnEntity(Player player, Entity target, int itemId, int slot) {
        if (!isEnabled()) {
            logger.info("{} has just tried to use a disabled Item: {}", player.getDisplayName(), itemId);
            player.sendMessage("This Item is currently disabled.");
            return false;
        }
        return switch (target) {
            case NPC targetNpc -> handleItemOnNpc(player, targetNpc, itemId, slot);
            case Player targetPlayer -> handleItemOnPlayer(player, targetPlayer, itemId, slot);
            case null, default -> false;
        };
    }

    public boolean performActionOnItem(Player player, GameItem itemUsed, GameItem itemUsedOn) {
        if (!isEnabled()) {
            logger.info("{} has just tried to use a disabled Item: {}", player.getDisplayName(), itemUsed.id());
            player.sendMessage("This Item is currently disabled.");
            return false;
        }
        return handleItemOnItem(player, itemUsed, itemUsedOn);
    }

    public boolean performActionOnObject(Player player, WorldObject objectUsedOn, int itemId) {
        if (!isEnabled()) {
            logger.info("{} has just tried to use a disabled Item: {}", player.getDisplayName(), itemId);
            player.sendMessage("This Item is currently disabled.");
            return false;
        }
        return handleItemOnObject(player, objectUsedOn, itemId);
    }

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
