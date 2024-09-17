package io.xeros.model.entity.npc.interactions;

import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.inter.*;
import io.xeros.model.entity.player.Player;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-09-17
 */
public abstract class NpcOptionAction   extends NpcAction
                                        implements  NpcActionI,
                                                    NpcActionOne,
                                                    NpcActionTwo,
                                                    NpcActionThree,
                                                    NpcActionFour {
    protected abstract Integer[] getIds();

    public boolean performedAction(Player player, NPC npc, int npcActionId) {
        if (!isEnabled()) {
            logger.info("{} has just tried to use a disabled NPC: {}", player.getDisplayName(), npcId);
            player.sendMessage("This objects is currently disabled.");
            return false;
        }
        return switch (npcActionId) {
            case 1 -> handleActionOne(player, npc);
            case 2 -> handleActionTwo(player, npc);
            case 3 -> handleActionThree(player, npc);
            case 4 -> handleActionFour(player, npc);
            default -> preformedDefaultAction(player, npc);
        };
    }

    private Boolean preformedDefaultAction(Player player, int npcId) {
        var npcDef = NpcDef.forId(npcId);
        if (npcDef == null) {
            logger.info("{} has just tried to use an invalid NPC: {}", player.getDisplayName(), npcId);
            player.sendMessage("Nothing interesting happens.");
            return false;
        }
        // Safe-Check that if the NPC is not attack-able, this is a "Talk-To" option
        if (npcDef.getCombatLevel() == 0) {
            player.getDialogueBuilder().npc(npcId, "Hello there stranger, rather nice day ain't it.");
            return true;
        }
        else
            throw new IllegalArgumentException(String.format("Invalid NPC Action [NPC Id: %d]", npcId));
    }
}
