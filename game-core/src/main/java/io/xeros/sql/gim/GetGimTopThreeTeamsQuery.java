package io.xeros.sql.gim;

import io.xeros.model.entity.player.mode.group.contest.ContestType;
import io.xeros.model.entity.player.mode.group.contest.GimContestEntry;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GetGimTopThreeTeamsQuery implements SqlQuery<Map<ContestType, List<GimContestEntry>>> {

    private static final GimContestTotalTable TABLE = new GimContestTotalTable();

    @Override
    public Map<ContestType, List<GimContestEntry>> execute(DatabaseManager context, Connection connection) throws SQLException {
        Map<ContestType, List<GimContestEntry>> map = new HashMap<>();

        for (ContestType type : ContestType.values()) {
            List<GimContestEntry> list = new ArrayList<>();
            ResultSet rs = get(connection, type);
            int rank = 1;
            while (rs.next()) {
                String name = rs.getString(GimContestTotalTable.GROUP_NAME);
                long value = rs.getLong(GimContestTotalTable.VALUE);
                list.add(new GimContestEntry(name, type, value, rank++));
            }

            map.put(type, list);
        }

        return map;
    }

    private ResultSet get(Connection connection, ContestType type) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + GimContestTotalTable.GROUP_NAME + ", " + GimContestTotalTable.VALUE
                + " FROM " + TABLE.getName()
                + " WHERE " + GimContestTotalTable.TYPE + " = ?"
                //+ " LIMIT 3"
                + " ORDER BY " + GimContestTotalTable.VALUE + " DESC");
        preparedStatement.setInt(1, type.getIntValue());
        preparedStatement.setMaxRows(3);
        return preparedStatement.executeQuery();
    }

}
