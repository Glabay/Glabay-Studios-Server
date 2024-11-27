package io.xeros.content.bosses.nightmare;

import java.util.List;

import io.xeros.content.bosses.nightmare.totem.Totem;
import io.xeros.model.entity.player.Player;

public class NightmareInterface {

    public static final int NIGHTMARE_HEALTH_INTERFACE_ID = 47302;
    public static final int NIGHTMARE_HEALTH_STATUS = 1360;
    public static final int NIGHTMARE_HEALTH_AMOUNT = 1361;
    public static final int NIGHTMARE_MAX_HEALTH_AMOUNT = 1362;
    public static final int NIGHTMARE_TOTEM1_HEALTH = 1363;
    public static final int NIGHTMARE_TOTEM2_HEALTH = 1364;
    public static final int NIGHTMARE_TOTEM3_HEALTH = 1365;
    public static final int NIGHTMARE_TOTEM4_HEALTH = 1366;
    public static final int NIGHTMARE_TOTEM_MAX_HEALTH = 1368;

    public void update(Player player, Nightmare nightmare) {
        if (!nightmare.isAlive()) {
            player.getPA().walkableInterface(-1);
        } else {
            player.getPA().walkableInterface(NIGHTMARE_HEALTH_INTERFACE_ID);
            player.getPA().sendConfig(NIGHTMARE_HEALTH_AMOUNT, nightmare.getHealth().getCurrentHealth());
            player.getPA().sendConfig(NIGHTMARE_MAX_HEALTH_AMOUNT, nightmare.getHealth().getMaximumHealth());
            player.getPA().sendConfig(NIGHTMARE_HEALTH_STATUS, nightmare.isOnHealth() ? 1 : 0);
            if (nightmare.isOnHealth()) {
                List<Totem> totems = nightmare.getTotems().getTotems();
                player.getPA().sendConfig(NIGHTMARE_TOTEM_MAX_HEALTH, totems.get(0).getHealth().getMaximumHealth());
                player.getPA().sendConfig(NIGHTMARE_TOTEM1_HEALTH, totems.get(0).getHealth().getCurrentHealth());
                player.getPA().sendConfig(NIGHTMARE_TOTEM2_HEALTH, totems.get(1).getHealth().getCurrentHealth());
                player.getPA().sendConfig(NIGHTMARE_TOTEM3_HEALTH, totems.get(3).getHealth().getCurrentHealth());
                player.getPA().sendConfig(NIGHTMARE_TOTEM4_HEALTH, totems.get(2).getHealth().getCurrentHealth());
            }
        }
    }

}
