package io.xeros.model.entity.player.mode.group;

import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import org.apache.commons.collections4.ListUtils;

import java.util.stream.Collectors;

/**
 * @author Chris | 6/29/21
 */
public class GroupIronmanGroupStats {
    private static final int INTERFACE_ID = 50635;
    private static final int STARTING_LINE_ID = 50666;
    private final GroupIronmanGroup group;

    public GroupIronmanGroupStats(final GroupIronmanGroup group) {
        this.group = group;
    }

    /**
     * Opens and sets the GIM group statistics interface for the player specified.
     */
    public void display(final Player observingPlayer) {
        long totalXp = 0, totalGp = 0;
        int totalLevel = 0, totalVotePoints = 0, totalSlayerPoints = 0, totalExchangePoints = 0, totalPkp = 0, totalBossPoints = 0;
        var onlinePlayers = group.getOnline();
        var offlinePlayers = group.getOfflineMembers().stream().map(this::loadDummy).collect(Collectors.toList());
        var groupPlayers = ListUtils.union(onlinePlayers, offlinePlayers);
        for (Player groupPlayer : groupPlayers) {
            if (groupPlayer != null) {
                totalLevel += groupPlayer.totalLevel;
                totalXp += groupPlayer.getTotalXp();
                totalGp += groupPlayer.getItems().getItemCount(Items.COINS, false) + group.getBank().getInventory().getAmount(Items.COINS);
                totalExchangePoints += groupPlayer.exchangePoints;
                totalBossPoints += groupPlayer.bossPoints;
                totalSlayerPoints += groupPlayer.getSlayer().getPoints();
                totalPkp += groupPlayer.pkp;
                totalVotePoints += groupPlayer.votePoints;
            }
        }
        var observingPA = observingPlayer.getPA();
        observingPA.showInterface(INTERFACE_ID);
        var startId = STARTING_LINE_ID;
        observingPA.sendFrame126(totalLevel, startId);
        observingPA.sendFrame126(Misc.formatCoins(totalXp), startId += 2);
        observingPA.sendFrame126(totalGp, startId += 2);
        observingPA.sendFrame126(totalExchangePoints, startId += 2);
        observingPA.sendFrame126(totalBossPoints, startId += 2);
        observingPA.sendFrame126(totalSlayerPoints, startId += 2);
        observingPA.sendFrame126(totalPkp, startId += 2);
        observingPA.sendFrame126(totalVotePoints, startId + 2);
    }

    private Player loadDummy(final String username) {
        var player = new Player(null);
        player.setLoginName(username);
        player.playerPass = "null";
        player.setMacAddress("null");
        player.setIpAddress("127.0.0.1");
        return player;
    }
}
