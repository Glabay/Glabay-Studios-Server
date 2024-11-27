package io.xeros.model.controller;

import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.Set;

public class DefaultController implements Controller {

    @Override
    public String getKey() {
        return ControllerRepository.DEFAULT_CONTROLLER_KEY;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return null;
    }

    @Override
    public void added(Player player) { }

    @Override
    public void removed(Player player) { }

    @Override
    public boolean onPlayerOption(Player player, Player clicked, String option) {
        return false;
    }

    @Override
    public boolean canMagicTeleport(Player player) {
        return true;
    }

    @Override
    public void onLogin(Player player) { }

    @Override
    public void onLogout(Player player) { }
}
