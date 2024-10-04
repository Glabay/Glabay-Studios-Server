package io.xeros.model.entity.npc.interactions.impl.morulrek;

import io.xeros.content.minigames.inferno.Inferno;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.TZHAAR_KET_KEH;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class TzhaarKetKeh extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { TZHAAR_KET_KEH };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        Inferno.startInferno(player, Inferno.getDefaultWave());
        return true;
    }
    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        Inferno.gamble(player);
        return true;
    }

}
