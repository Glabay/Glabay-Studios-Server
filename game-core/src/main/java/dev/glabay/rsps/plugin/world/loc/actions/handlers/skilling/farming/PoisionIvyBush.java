package dev.glabay.rsps.plugin.world.loc.actions.handlers.skilling.farming;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * @author Skryllz | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Skryllz
 * @social Github: <a href="https://github.com/Skryllzz">Skryllzz</a>
 * @since 10/1/2024
 */
public class PoisionIvyBush extends WorldObjectAction {
    @Override
    public Integer[] getIds() {
        return new Integer[] { 7674 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (player.getItems().freeSlots() < 1) {
            player.sendMessage("You need at least one free slot to pick these berries.");
        }
            player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICK_POSION_BERRY);
            player.getItems().addItem(Items.POISON_IVY_BERRIES, 1);
            return true;
    }

}
