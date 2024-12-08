package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.mountkaruulm.karuulmdungeon;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.model.definition.Objects.CAVE_EXIT_34514;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class KaruulmDungeon extends WorldObjectAction {

    @Override
    public Integer[] getIds() {
        return new Integer[]{ CAVE_EXIT_34514 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return ExitDungeon(player, object);
    }

    private Boolean ExitDungeon(Player player, GlobalObject object) {
        player.objectDistance = 3;
        AgilityHandler.delayEmote(player, "CRAWL", 1311, 3806, 0, 2);
        return true;
    }
}
