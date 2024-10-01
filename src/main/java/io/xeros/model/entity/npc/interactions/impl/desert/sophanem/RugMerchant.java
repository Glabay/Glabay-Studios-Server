package io.xeros.model.entity.npc.interactions.impl.desert.sophanem;

import io.xeros.content.achievement_diary.impl.DesertDiaryEntry;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Npcs.RUG_MERCHANT;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-01
 */
public class RugMerchant extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { RUG_MERCHANT };
    }

    @Override
    public Boolean handleActionThree(Player player, NPC npc) {
        player.getDiaryManager().getDesertDiary()
            .progress(DesertDiaryEntry.TRAVEL_NARDAH);
        player.startAnimation(2262);
        AgilityHandler.delayFade(player,
            "NONE",
            3402, 2916, 0,
            "You step on the carpet and take off...",
            "at last you end up in nardah.",
            3);
        return true;
    }

    @Override
    public Boolean handleActionFour(Player player, NPC npc) {
        player.startAnimation(2262);
        AgilityHandler.delayFade(player,
            "NONE", 3285, 2815, 0,
            "You step on the carpet and take off...",
            "at last you end up in sophanem.",
            3);
        return true;
    }
}
