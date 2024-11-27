package io.xeros.model.world.objects.actions;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.actions.inter.*;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-16
 */
public abstract class WorldObjectAction extends ObjectAction
                                        implements  ObjectActionI,
                                                    ObjectActionOne,
                                                    ObjectActionTwo,
                                                    ObjectActionThree,
                                                    ObjectActionFour,
                                                    ObjectActionFive {
    protected abstract Integer[] getIds();

    public boolean performedAction(Player player, int objectId, int objectX, int objectY, int objectZ, int objectActionId) {
        if (!isEnabled()) {
            logger.info("{} has just tried to use a disabled object: {}", player.getDisplayName(), objectId);
            player.sendMessage("This objects is currently disabled.");
            return false;
        }
        var objectClicked = Server.getGlobalObjects().get(objectId, objectX, objectY, objectZ);
        return switch (objectActionId) {
            case 1 -> handleActionOne(player, objectClicked);
            case 2 -> handleActionTwo(player, objectClicked);
            case 3 -> handleActionThree(player, objectClicked);
            case 4 -> handleActionFour(player, objectClicked);
            case 5 -> handleActionFive(player, objectClicked);
            default -> throw new IllegalArgumentException(
                String.format(
                    "Invalid action Id: %d [ObjectId: %d](X: %d, Y: %d)",
                    objectActionId,
                    objectId,
                    objectX,
                    objectY)
            );
        };
    }
}
