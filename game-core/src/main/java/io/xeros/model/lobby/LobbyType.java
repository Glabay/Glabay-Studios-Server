package io.xeros.model.lobby;

import java.util.stream.Stream;
import io.xeros.model.lobby.impl.ChambersOfXericLobby;
import io.xeros.model.lobby.impl.TrialsOfXericLobby;

public enum LobbyType {
    CHAMBERS_OF_XERIC(ChambersOfXericLobby.class), TRIALS_OF_XERIC(TrialsOfXericLobby.class);

    LobbyType(Class<? extends Lobby> lobbyClass) {
        this.lobbyClass = lobbyClass;
    }

    private final Class<? extends Lobby> lobbyClass;

    public static Stream<LobbyType> stream() {
        // TODO Auto-generated method stub
        return Stream.of(values());
    }

    public Class<? extends Lobby> getLobbyClass() {
        return this.lobbyClass;
    }
}
