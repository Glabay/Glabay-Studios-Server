package io.xeros.model.entity.npc.interactions.impl.zulandra;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.ZUL_ARETH_DIALOGUE;
import static io.xeros.model.Npcs.ZULARETH;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class ZulAreth extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { ZULARETH };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(ZUL_ARETH_DIALOGUE, ZULARETH);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        if (player.getZulrahEvent().isActive()) {
            player.getDH().sendStatement("It seems that a zulrah instance for you is already created.",
                    "If you think this is wrong then please re-log.");
            player.nextChat = -1;
            return false;
        }
        player.getZulrahEvent().initialize();
        return true;
    }
}
