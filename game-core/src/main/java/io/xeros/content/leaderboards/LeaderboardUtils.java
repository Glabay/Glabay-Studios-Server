package io.xeros.content.leaderboards;

import com.fasterxml.jackson.core.type.TypeReference;
import io.xeros.Server;
import io.xeros.annotate.Init;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.sql.leaderboard.LeaderboardAdd;
import io.xeros.sql.leaderboard.LeaderboardGetAllUnclaimedRewards;
import io.xeros.util.JsonUtil;
import io.xeros.util.logging.player.LeaderboardRewardCollected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderboardUtils {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardUtils.class);
    public static final Map<LeaderboardType, List<LeaderboardEntry>> leaderboards = new ConcurrentHashMap<>();
    public static final Map<LeaderboardType, List<LeaderboardEntry>> daily = new ConcurrentHashMap<>();
    public static final Map<LeaderboardType, List<LeaderboardEntry>> weekly = new ConcurrentHashMap<>();

    public static final List<LeaderboardReward> rewards = new ArrayList<>();

    @Init
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static void loadLeaderboardRewards() throws IOException {
        rewards.addAll(JsonUtil.fromYaml(Server.getDataDirectory() + "/cfg/leaderboard_rewards.yaml", new TypeReference<List<LeaderboardReward>>() {}));
    }

    public static void checkRewards(Player player) {
        Server.getDatabaseManager().exec((context, connection) -> {
            List<GameItem> entries = new LeaderboardGetAllUnclaimedRewards(player).execute(context, connection);

            if (!entries.isEmpty()) {
                logger.debug("Giving {} rewards {}", player, entries);
                player.addQueuedAction(plr -> {
                    for (GameItem item : entries) {
                        if (!player.getInventory().addToBank(new ImmutableItem(item))) {
                            player.getCollectionBox().add(player, item);
                        }
                        player.sendMessage("<clan=6> You've received {} for placing in the leaderboards!", item.getFormattedString());
                        Server.getLogging().write(new LeaderboardRewardCollected(player, item));
                    }
                });
            }

            return null;
        });
    }

    public static void addCount(LeaderboardType type, Player player, int amount) {
        Server.getDatabaseManager().batch(new LeaderboardAdd(new LeaderboardEntry(type, player.getLoginName(), amount, LocalDateTime.now())));
    }
}
