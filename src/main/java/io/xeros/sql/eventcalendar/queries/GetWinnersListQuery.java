package io.xeros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.content.events.eventcalendar.ChallengeWinner;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.eventcalendar.tables.EventCalendarWinnersTable;

public class GetWinnersListQuery implements SqlQuery<List<ChallengeWinner>> {

    private static final EventCalendarWinnersTable TABLE = new EventCalendarWinnersTable();

    @Override
    public List<ChallengeWinner> execute(DatabaseManager context, Connection connection) throws SQLException {
        List<ChallengeWinner> list = Lists.newArrayList();
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + TABLE.getName()
            + " INNER JOIN display_names on display_names.login_name = calendar_winners.username"
        );
        while (rs.next()) {
            String displayName = rs.getString("display_name");
            String loginName = rs.getString(EventCalendarWinnersTable.USERNAME);
            int day = rs.getInt(EventCalendarWinnersTable.DAY);
            list.add(new ChallengeWinner(loginName, displayName, day));
        }
        return list;
    }
}
