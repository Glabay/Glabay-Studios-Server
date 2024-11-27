package io.xeros.content.bosses.nightmare.party;

import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.party.PartyFormAreaController;
import io.xeros.content.party.PlayerParty;
import io.xeros.model.entity.player.Boundary;

import java.util.Set;

public class NightmarePartyFormAreaController extends PartyFormAreaController {

    @Override
    public String getKey() {
        return NightmareParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(NightmareConstants.LOBBY_BOUNDARY);
    }

    @Override
    public PlayerParty createParty() {
        return new NightmareParty();
    }
}
