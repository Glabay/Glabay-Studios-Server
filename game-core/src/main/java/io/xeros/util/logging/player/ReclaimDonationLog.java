package io.xeros.util.logging.player;

import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.PlayerLog;

import java.util.Set;

public class ReclaimDonationLog extends PlayerLog {

    private final String oldAccount;
    private final int dollars;

    public ReclaimDonationLog(Player player, String oldAccount, int dollars) {
        super(player);
        this.oldAccount = oldAccount;
        this.dollars = dollars;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("reclaim");
    }

    @Override
    public String getLoggedMessage() {
        return String.format("Reclaimed from old account %s and got $%d", oldAccount, dollars);
    }
}
