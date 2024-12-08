package dev.glabay.rsps.plugin.world.loc.actions.handlers.areas.fossilisland.home;

import dev.glabay.rsps.plugin.world.loc.actions.WorldObjectAction;
import io.xeros.content.leaderboards.LeaderboardInterface;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.model.definition.Objects.NOTICE_BOARD_31846;
import static io.xeros.model.definition.Objects.SCOREBOARD_29064;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class LeaderboardSign extends WorldObjectAction {
    @Override
    public Integer[] getIds() {
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
