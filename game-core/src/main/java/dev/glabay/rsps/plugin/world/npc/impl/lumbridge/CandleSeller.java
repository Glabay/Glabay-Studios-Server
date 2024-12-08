package dev.glabay.rsps.plugin.world.npc.impl.lumbridge;

import dev.glabay.rsps.plugin.world.npc.NpcOptionAction;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import static io.xeros.model.definition.Items.CANDLE;
import static io.xeros.model.definition.Items.COINS;
import static io.xeros.model.definition.Npcs.CANDLE_SELLER;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/2/2024
 */
public class CandleSeller extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { CANDLE_SELLER };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        if (player.getItems().playerHasItem(COINS, 30)) {
            player.getItems().deleteItem(COINS, 30);
            player.getItems().addItem(CANDLE, 1);
            player.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.BUY_CANDLE);
        }
        else {
            player.sendMessage("You need 30 coins to purchase a candle.");
            return false;
        }
        return true;
    }
}
