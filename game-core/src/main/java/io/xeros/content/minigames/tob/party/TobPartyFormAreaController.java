package io.xeros.content.minigames.tob.party;

import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.content.party.PartyFormAreaController;
import io.xeros.content.party.PlayerParty;
import io.xeros.model.entity.player.Boundary;

import java.util.Set;

public class TobPartyFormAreaController extends PartyFormAreaController {

    @Override
    public String getKey() {
        return TobParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(TobConstants.TOB_LOBBY);
    }

    @Override
    public PlayerParty createParty() {
        return new TobParty();
    }
}
