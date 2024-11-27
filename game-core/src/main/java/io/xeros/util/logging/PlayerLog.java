package io.xeros.util.logging;

import java.util.HashSet;
import java.util.Set;

import io.xeros.model.entity.player.Player;

public abstract class PlayerLog extends Log {

    protected final String username;

    public abstract Set<String> getLogFileNames();

    public PlayerLog(Player player) {
        this.username = player.getLoginName().toLowerCase();
    }

    @Override
    public String getDirectory() {
        return "player_logs/" + username + "/";
    }

    @Override
    public Set<String> getFileNames() {
        Set<String> stringSet = new HashSet<>();
        stringSet.add("all");
        stringSet.addAll(getLogFileNames());
        return stringSet;
    }

    public String getUsername() {
        return username;
    }
}
