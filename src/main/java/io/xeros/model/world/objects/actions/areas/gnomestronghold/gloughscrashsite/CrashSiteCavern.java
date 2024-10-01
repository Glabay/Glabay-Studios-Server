package io.xeros.model.world.objects.actions.areas.gnomestronghold.gloughscrashsite;

import io.xeros.content.leaderboards.LeaderboardInterface;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class CrashSiteCavern extends WorldObjectAction {

    @Override
    protected Integer[] getIds() {
        return new Integer[]{ 28686 };
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