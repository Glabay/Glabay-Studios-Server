package io.xeros.content.party;

import io.xeros.model.controller.DefaultController;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.Set;

public abstract class PartyFormAreaController extends DefaultController {

    public static PartyFormAreaController get(Player player) {
        if (player.getController() instanceof PartyFormAreaController) {
            return (PartyFormAreaController) player.getController();
        }

        return null;
    }

    @Override
    public abstract String getKey();

    @Override
    public abstract Set<Boundary> getBoundaries();

    public abstract PlayerParty createParty();

    @Override
    public void added(Player player) {
        PartyInterface.open(player);
        player.getPA().showOption(3, 0, "Invite");
    }

    @Override
    public void removed(Player player) {
        PartyInterface.close(player);
        player.getPA().showOption(3, 0, "null");
    }

    @Override
    public boolean onPlayerOption(Player player, Player clicked, String option) {
        if (option.equals("Invite")) {
            if (player.getParty() == null) {
                player.sendMessage("You are not in a party.");
                return true;
            }

            if (clicked.getController() != this) {
                player.sendMessage("That player isn't in the designated party area.");
                return true;
            }

            player.getParty().invite(player, clicked);
            return true;
        }

        return false;
    }
}
