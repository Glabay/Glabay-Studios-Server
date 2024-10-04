package io.xeros.model.entity.npc.interactions.impl.alkharid;

import io.xeros.content.dialogue.DialogueBuilder;
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
public class RugMerchant extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { RUG_MERCHANT, RUG_MERCHANT_2, RUG_MERCHANT_3, RUG_MERCHANT_4, RUG_MERCHANT_5 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(837, 17);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(838, 17);
        return true;
    }
}
