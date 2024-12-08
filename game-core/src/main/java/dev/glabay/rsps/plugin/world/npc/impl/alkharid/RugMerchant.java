package dev.glabay.rsps.plugin.world.npc.impl.alkharid;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.achievement_diary.impl.DesertDiaryEntry;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Dialogues.RUG_MERCHANT_DIALOGUE;
import static io.xeros.model.definition.Dialogues.RUG_MERCHANT_DIALOGUE_2;
import static io.xeros.model.definition.Npcs.*;

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
        return new Integer[] { RUG_MERCHANT, RUG_MERCHANT_18, RUG_MERCHANT_19, RUG_MERCHANT_20, RUG_MERCHANT_22 };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(RUG_MERCHANT_DIALOGUE, RUG_MERCHANT);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        player.getDH().sendDialogues(RUG_MERCHANT_DIALOGUE_2, RUG_MERCHANT);
        return true;
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
