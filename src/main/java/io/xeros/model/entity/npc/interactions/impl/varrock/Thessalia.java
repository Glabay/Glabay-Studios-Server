package io.xeros.model.entity.npc.interactions.impl.varrock;

import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.Dialogues.THESSALIA_DIALOGUE;
import static io.xeros.model.Npcs.THESSALIA;
import static io.xeros.model.Shops.FANCY_SHOP;

/**
 * @author Zeighe | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Zeighe
 * @since 2024-10-01
 */
public class Thessalia extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { THESSALIA };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        player.getDH().sendDialogues(THESSALIA_DIALOGUE, THESSALIA);
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        if (Boundary.isIn(player, Boundary.VARROCK_BOUNDARY)) {
            player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.DRESS_FOR_SUCESS);
        }
        player.getShops().openShop(FANCY_SHOP);
        return true;
    }
}
