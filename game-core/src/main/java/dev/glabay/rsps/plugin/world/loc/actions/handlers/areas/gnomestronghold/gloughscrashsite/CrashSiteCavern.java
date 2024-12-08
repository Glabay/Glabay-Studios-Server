package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.gnomestronghold.gloughscrashsite;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.model.definition.Objects.CAVERN_ENTRANCE_28686;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class CrashSiteCavern extends WorldObjectAction {

    @Override
    public Integer[] getIds() {
        return new Integer[]{ CAVERN_ENTRANCE_28686 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return EnterCrashSiteCavern(player, object);
    }

    private Boolean EnterCrashSiteCavern(Player player, GlobalObject object) {
        player.objectDistance = 3;
        AgilityHandler.delayEmote(player, "CRAWL", 3808, 9744, 1, 2);
        return true;
    }
}