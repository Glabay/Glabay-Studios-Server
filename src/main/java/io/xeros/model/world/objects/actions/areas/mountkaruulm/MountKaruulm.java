package io.xeros.model.world.objects.actions.areas.mountkaruulm;

import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import static io.xeros.model.Objects.ELEVATOR;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class MountKaruulm extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {
        return new Integer[]{ ELEVATOR };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return EnterDungeon(player, object);
    }

    private Boolean EnterDungeon(Player player, GlobalObject object) {
        player.objectDistance = 3;
        AgilityHandler.delayEmote(player, "CRAWL", 1312, 10188, 0, 2);
        return true;
    }
}
