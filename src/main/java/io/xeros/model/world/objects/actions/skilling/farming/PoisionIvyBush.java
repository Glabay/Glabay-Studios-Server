package io.xeros.model.world.objects.actions.skilling.farming;

import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/1/2024
 */
public class PoisionIvyBush extends WorldObjectAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { 7674 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (player.getItems().freeSlots() < 1) {
            player.sendMessage("You need at least one free slot to pick these berries.");
        }
            player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICK_POSION_BERRY);
            player.getItems().addItem(6018, 1);
            return true;
    }

}
