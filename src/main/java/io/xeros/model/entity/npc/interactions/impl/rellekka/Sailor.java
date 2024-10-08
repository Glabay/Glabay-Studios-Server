package io.xeros.model.entity.npc.interactions.impl.rellekka;

import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.skills.agility.AgilityHandler;
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
public class Sailor extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { SAILOR };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendNpcChat1("Right click on me and i will take you on-board.", SAILOR, "Sailor");
        return true;
    }
    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        AgilityHandler.delayFade(player, "NONE", 2674, 3274, 0, "The sailor brings you onto the ship.",
                "and you end up in ardougne.", 3);
        player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.SAIL_TO_ARDOUGNE);
        return true;
    }
}
