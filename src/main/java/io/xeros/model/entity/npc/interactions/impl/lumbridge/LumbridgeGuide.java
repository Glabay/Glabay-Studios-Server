package io.xeros.model.entity.npc.interactions.impl.lumbridge;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.LUMBRIDGE_GUIDE_DIALOGUE;
import static io.xeros.model.Npcs.*;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class LumbridgeGuide extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { LUMBRIDGE_GUIDE, LUMBRIDGE_GUIDE_1179, LUMBRIDGE_GUIDE_1181, LUMBRIDGE_GUIDE_3393 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        if (!player.pkDistrict) {
            player.sendMessage("You cannot do this right now.");
            return false;
        }
        player.getDH().sendDialogues(LUMBRIDGE_GUIDE_DIALOGUE, LUMBRIDGE_GUIDE);
        return true;
    }
}
