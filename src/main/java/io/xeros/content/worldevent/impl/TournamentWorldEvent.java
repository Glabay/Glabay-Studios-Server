package io.xeros.content.worldevent.impl;

import java.util.List;

import io.xeros.content.commands.Command;
import io.xeros.content.tournaments.TourneyManager;
import io.xeros.content.worldevent.WorldEvent;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.broadcasts.Broadcast;

public class TournamentWorldEvent implements WorldEvent {

    private final TourneyManager tourney = TourneyManager.getSingleton();

    @Override
    public void init() {
        tourney.openLobby();
    }

    @Override
    public void dispose() {
        tourney.endGame();
    }

    @Override
    public boolean isEventCompleted() {
        return !tourney.isLobbyOpen() && !tourney.isArenaActive();
    }

    @Override
    public String getCurrentStatus() {
        return tourney.getTimeLeft();
    }

    @Override
    public String getEventName() {
        return "Outlast";
    }

    @Override
    public String getStartDescription() {
        return "starts";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return null;
    }

    @Override
    public void announce(List<Player> players) {
        new Broadcast("An Outlast " + tourney.getTournamentType() + " tournament will begin soon, type ::outlast or click HERE to join!").addTeleport(new Position(3080, 3510, 0)).copyMessageToChatbox().submit();
    }
}
