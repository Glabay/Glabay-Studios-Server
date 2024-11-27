package io.xeros.content.bosses.nightmare;

import io.xeros.content.bosses.nightmare.party.NightmareParty;
import io.xeros.model.entity.player.Player;

public class NightmareActionHandler {

    public static boolean clickNpc(Player player, int npcId, int option) {
        if (NightmareStatus.isStatusNpc(npcId)) {
            if (player.inParty(NightmareParty.TYPE)) {
                player.getParty().openStartActivityDialogue(player, "Nightmare", NightmareConstants.LOBBY_BOUNDARY::in, list -> {
                    NightmareInstance nightmare = new NightmareInstance(false);
                    nightmare.countdown();
                    list.forEach(nightmare::add);
                });
                return true;
            }

            switch (option) {
                case 1:
                    NightmareInstanceManager.getSingleton().join(player);
                    return true;
                case 2:
                    // Status
                    return true;
            }
        }

        return false;
    }

}
