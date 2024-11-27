package io.xeros.util.logging.player;

import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.PlayerLog;

import java.util.Set;

public class ChangeDisplayNameLog extends PlayerLog {

    private final String oldDisplayName;
    private final String newDisplayName;

    public ChangeDisplayNameLog(Player player, String oldDisplayName, String newDisplayName) {
        super(player);
        this.oldDisplayName = oldDisplayName;
        this.newDisplayName = newDisplayName;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("name_change");
    }

    @Override
    public String getLoggedMessage() {
        return String.format("Changed name from '%s' to '%s'.", oldDisplayName, newDisplayName);
    }
}
