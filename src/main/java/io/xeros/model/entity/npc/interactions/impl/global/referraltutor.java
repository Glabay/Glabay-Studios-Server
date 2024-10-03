package io.xeros.model.entity.npc.interactions.impl.global;

import io.xeros.content.referral.EnterReferralDialogue;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.REFERRAL_TUTOR;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class referraltutor extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { REFERRAL_TUTOR };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.start(new EnterReferralDialogue(player));
        return true;
    }
}
