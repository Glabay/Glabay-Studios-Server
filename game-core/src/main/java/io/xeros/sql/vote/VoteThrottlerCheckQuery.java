package io.xeros.sql.vote;

import io.xeros.model.entity.player.Player;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.DatabaseTable;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.voterecord.VoteRecordTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * @author Chris | 8/14/21
 */
public class VoteThrottlerCheckQuery implements SqlQuery<Boolean> {

    public static String select(Player player, int siteId) {
        var emptyMac = player.getMacAddress().equals("");
        var emptyUUID = player.getUUID().equals("");

        var query = "SELECT * FROM vote_record WHERE ";

        if (siteId != -1)
            query += "site_id = " + siteId + " AND";

        query += " (";

        var conditions = new StringJoiner(" OR ");
        conditions.add(VoteRecordTable.IP_ADDRESS_COLUMN + "='" + player.getIpAddress() + "'");
        if (!emptyMac)
            conditions.add(VoteRecordTable.MAC_ADDRESS_COLUMN + "='" + player.getMacAddress() + "'");
        if (!emptyUUID)
            conditions.add(VoteRecordTable.UUID_COLUMN + "='" + player.getUUID() + "'");

        query += conditions.toString();
        query += ")";
        return query;
    }

    private static final DatabaseTable TABLE = new VoteRecordTable();
    private final Player player;
    private final int siteId;

    public VoteThrottlerCheckQuery(final Player player, int siteId) {
        this.player = player;
        this.siteId = siteId;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws Exception {
        var statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchDirection(ResultSet.FETCH_REVERSE);
        var rs = statement.executeQuery(select(player, siteId));

        while (rs.next()) {
            final var timestamp = rs.getTimestamp(VoteRecordTable.DATE_CLAIMED_COLUMN);
            final long millisDiff = Instant.now().toEpochMilli() - timestamp.toInstant().toEpochMilli();
            if (TimeUnit.MILLISECONDS.toHours(millisDiff) < 12) {
                return true;
            }
        }

        return false;
    }
}
