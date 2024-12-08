package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.morytania;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.lock.CompleteLock;
import io.xeros.model.entity.player.lock.Unlocked;
import io.xeros.model.world.objects.GlobalObject;

import java.util.Objects;

import static io.xeros.model.definition.Objects.CAVE_42595;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public class AraxyteSpiderCaveExit extends WorldObjectAction {
    @Override
    public Integer[] getIds() {
        return new Integer[] {
            CAVE_42595
        };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        if (Objects.equals(player.getLock(), new CompleteLock()))
            return false;
        player.playerAssistant.runClientScript(951);
        player.lock(new CompleteLock());
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalTicks() == 2)
                    player.setPosition(new Position(3657, 3406));

                if (container.getTotalTicks() == 4) {
                    player.playerAssistant.runClientScript(948, 0, 0, 0, 255, 50);
                    container.stop();
                }
            }

            @Override
            public void onStopped() {
                player.lock(new Unlocked());
            }
        }, 1);
        return true;

    }
}
