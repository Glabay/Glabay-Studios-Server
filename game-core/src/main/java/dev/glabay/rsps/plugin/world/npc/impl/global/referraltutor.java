package dev.glabay.rsps.plugin.world.npc.impl.global;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.referral.EnterReferralDialogue;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.PILES;

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
        return new Integer[] { PILES };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.start(new EnterReferralDialogue(player));
        return true;
    }
}
