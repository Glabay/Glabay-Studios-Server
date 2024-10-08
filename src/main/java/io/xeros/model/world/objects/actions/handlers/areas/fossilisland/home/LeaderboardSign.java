package io.xeros.model.world.objects.actions.handlers.areas.fossilisland.home;

import io.xeros.content.leaderboards.LeaderboardInterface;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.model.world.objects.actions.WorldObjectAction;

import static io.xeros.model.Objects.NOTICE_BOARD_31846;
import static io.xeros.model.Objects.SCOREBOARD_29064;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class LeaderboardSign extends WorldObjectAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[]{ SCOREBOARD_29064, NOTICE_BOARD_31846 };
    }

    @Override
    public Boolean handleActionOne(Player player, GlobalObject object) {
        return openLeaderboard(player, object);
    }

    private Boolean openLeaderboard(Player player, GlobalObject object) {
        LeaderboardInterface.openInterface(player);
        return true;
    }
}
