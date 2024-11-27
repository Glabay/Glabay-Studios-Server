package io.xeros.util.logging.player;

import java.util.Set;

import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.PlayerLog;

public class ClickButtonLog extends PlayerLog {

    private final int buttonId;
    private final boolean newButton;

    public ClickButtonLog(Player player, int buttonId, boolean newButton) {
        super(player);
        this.buttonId = buttonId;
        this.newButton = newButton;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("buttons_clicked");
    }

    @Override
    public String getLoggedMessage() {
        return "Clicked: " + buttonId + ", new: " + newButton;
    }
}
