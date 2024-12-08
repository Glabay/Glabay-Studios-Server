package dev.glabay.rsps.plugin.world.npc.impl.rellekka;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.SAILOR_3936_DIALOGUE;
import static io.xeros.model.definition.Npcs.SAILOR_3936;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class Sailor2 extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] {  SAILOR_3936 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendNpcChat1("Right click on me and i will take you on-board.", SAILOR_3936, "Sailor");
        return true;
    }
    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(SAILOR_3936_DIALOGUE, SAILOR_3936);
        return true;
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        AgilityHandler.delayFade(player, "NONE", 2310, 3782, 0, "You board the boat...", "And end up in Neitiznot", 3);
        player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.TRAVEL_NEITIZNOT);
        return true;
    }
}
