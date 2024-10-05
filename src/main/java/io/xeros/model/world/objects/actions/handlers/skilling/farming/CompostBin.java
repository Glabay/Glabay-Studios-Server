package io.xeros.model.world.objects.actions.handlers.skilling.farming;

import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import static io.xeros.model.Items.*;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/2/2024
 */
public class CompostBin extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {
        return new Integer[]    { 3840 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) if (player.getItems().playerHasItem(BUCKET)) {
            int amount = player.getItems().getItemAmount(BUCKET);
            player.getItems().deleteItem2(BUCKET, amount);
            player.getItems().addItem(COMPOST, amount);
            player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.COMPOST_BUCKET, true, amount);
            return true;
        }
        if (player.getItems().playerHasItem(BUCKET)) {
            int amount = player.getItems().getItemAmount(BUCKET);
            player.getItems().deleteItem2(BUCKET, amount);
            player.getItems().addItem(COMPOST, amount);
        }
        return true;
    }

}