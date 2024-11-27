package io.xeros.util.logging.player;

import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.PlayerLog;

import java.util.Set;
import java.util.stream.Collectors;

public class CompletedTobLog extends PlayerLog {

    private final InstancedArea instance;

    public CompletedTobLog(Player player, InstancedArea instance) {
        super(player);
        this.instance = instance;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("completed_tob");
    }

    @Override
    public String getLoggedMessage() {
        String players = "";
        if (instance != null) {
            players = instance.getPlayers().stream().map(Player::getLoginNameLower).collect(Collectors.joining(", "));
        }
        return "Completed tob with " + players;
    }
}
