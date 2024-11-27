package io.xeros.model.lobby;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import com.google.common.collect.Lists;

public class LobbyManager {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LobbyManager.class.getName());

    public static void initializeLobbies() {
        Stream.of(LobbyType.values()).forEach(lobbyType -> {
            try {
                Lobby lobby = lobbyType.getLobbyClass().newInstance();
                lobby.startTimer();
                lobbies.add(lobby);
            } catch (InstantiationException e) {
                e.printStackTrace(System.err);
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.err);
            }
        });
        log.info("Initialized " + lobbies.size() + " lobbies.");
    }

    public static Optional<Lobby> get(LobbyType lobbyType) {
        return lobbies.stream().filter(lobby -> lobby.getClass() == lobbyType.getLobbyClass()).findFirst();
    }

    private static final List<Lobby> lobbies = Lists.newArrayList();
}
