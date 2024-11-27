package io.xeros.model.entity.npc.interactions.impl.ardougne;

import io.xeros.content.achievement_diary.impl.ArdougneDiaryEntry;
import io.xeros.content.skills.thieving.Thieving;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Npcs.HERO;

/**
 * @author Zeighe | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Zeighe
 * @since 2024-10-01
 */
public class Hero extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { HERO };
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getThieving().steal(Thieving.Pickpocket.HERO, npc);
        if (Boundary.isIn(player, Boundary.ARDOUGNE)) {
            player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PICKPOCKET_ARD);
            player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PICKPOCKET_HERO);
        }
        return true;
    }
}