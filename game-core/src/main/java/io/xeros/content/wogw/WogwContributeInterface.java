package io.xeros.content.wogw;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.sql.wogw.GetRecentContributionsSqlQuery;
import io.xeros.sql.wogw.GetTopContributionsSqlQuery;
import io.xeros.util.Misc;

public class WogwContributeInterface {

    public static final int INTERFACE_ID = 22931;
    public static final int WOGW_BONUS_BUTTON_SELECTION = 1373;
    private static final int CONTRIBUTE_BUTTON = 22940;
    private static final int TOP_CONTRIBUTOR_TEXT = 22935;
    private static final int RECENT_CONTRIBUTORS_CONTAINER = 22937;

    private final Player player;
    private WogwInterfaceButton selectedButton = WogwInterfaceButton.EXPERIENCE_BOOST;

    public WogwContributeInterface(Player player) {
        this.player = player;
    }

    public void open() {
        if (player.hitDatabaseRateLimit(true))
            return;

        Server.getDatabaseManager().exec((context, connection) -> {
            List<WogwContribution> top = new GetTopContributionsSqlQuery(1).execute(context, connection);
            List<WogwContribution> recent = new GetRecentContributionsSqlQuery(4).execute(context, connection);

            player.addQueuedAction(plr -> {
                updateConfig();
                player.sendMessage("@cr10@ You may also use your FoE Point Certificates on the WoGW!");
                player.getPA().sendString(TOP_CONTRIBUTOR_TEXT, top != null && !top.isEmpty() ? top.get(0).toString() : "N/A");

                if (!recent.isEmpty()) {
                    player.getPA().sendStringContainer(RECENT_CONTRIBUTORS_CONTAINER, recent.stream().map(WogwContribution::toString).collect(Collectors.toList()));
                } else {
                    player.getPA().sendStringContainer(RECENT_CONTRIBUTORS_CONTAINER, Lists.newArrayList("N/A"));
                }

                for (WogwInterfaceButton button : WogwInterfaceButton.values()) {
                    player.getPA().sendString(button.getCoinsTextId(), Misc.getCoinColour(button.getCurrentCoins()) + Misc.formatCoins(button.getCurrentCoins()) + "</col>/" + Misc.getCoinColour(button.getCoinsRequired()) + Misc.formatCoins(button.getCoinsRequired()));
                }

                player.getPA().showInterface(INTERFACE_ID);
            });

            return null;
        });
    }

    private void updateConfig() {
        player.getPA().sendConfig(WOGW_BONUS_BUTTON_SELECTION, selectedButton.ordinal());
    }

    public boolean clickButton(int buttonId) {
        if (buttonId == CONTRIBUTE_BUTTON) {
            player.getPA().sendEnterAmount("Donate to " + selectedButton.toString(), (p, a) -> Wogw.donate(player, a, -1, -1));
            return true;
        }

        Optional<WogwInterfaceButton> button = Arrays.stream(WogwInterfaceButton.values()).filter(it -> it.getButtonId() == buttonId).findAny();
        if (button.isPresent()) {
            selectedButton = button.get();
            updateConfig();
            return true;
        }
        return false;
    }

    public WogwInterfaceButton getSelectedButton() {
        return selectedButton;
    }
}
