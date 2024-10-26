package io.xeros.sql.leaderboard;

import io.xeros.content.leaderboards.LeaderboardReward;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.content.leaderboards.RewardEntry;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LeaderboardRewardAdd implements SqlQuery<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardRewardAdd.class);

    private final List<RewardEntry> entries;

    public LeaderboardRewardAdd(List<RewardEntry> entries) {
        this.entries = entries;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        if (LeaderboardUtils.rewards.isEmpty()) {
            return false;
        }
        PreparedStatement insert = connection.prepareStatement("INSERT INTO leaderboards_collection VALUES(?, ?, ?, 0)");
        for (RewardEntry entry: entries) {
            for (LeaderboardReward reward : LeaderboardUtils.rewards) {
                if (entry.getPeriod().equals(reward.getPeriod())
                        && entry.getPlace() == reward.getPlace()
                        && entry.getEntry().getType() == reward.getType()) {
                    insert.setString(1, entry.getEntry().getLoginName());
                    insert.setInt(2, reward.getItem().id());
                    insert.setInt(3, reward.getItem().amount());
                    insert.addBatch();
                    logger.debug("Adding reward {} for {}", reward, entry);
                }
            }
        }
        insert.executeBatch();
        return true;
    }
}
