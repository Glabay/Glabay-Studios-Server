package io.xeros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.content.events.eventcalendar.ChallengeParticipant;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.eventcalendar.tables.EventCalendarParticipantsTable;

public class GetParticipantsListQuery implements SqlQuery<List<ChallengeParticipant>> {

    private static final EventCalendarParticipantsTable TABLE = new EventCalendarParticipantsTable();

    private final int day;

    public GetParticipantsListQuery(int day) {
        this.day = day;
    }

    @Override
    public List<ChallengeParticipant> execute(DatabaseManager context, Connection connection) throws SQLException {
        List<ChallengeParticipant> list = Lists.newArrayList();
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + TABLE.getName()
                + " INNER JOIN display_names ON display_names.login_name = calendar_participants.username"
                + " WHERE " + EventCalendarParticipantsTable.ENTRY_DAY + "=" + day
        );
        while (rs.next()) {
            String displayName = rs.getString("display_name");
            String loginName = rs.getString(EventCalendarParticipantsTable.USERNAME);
            String macAddress = rs.getString(EventCalendarParticipantsTable.MAC_ADDRESS);
            String ipAddress = rs.getString(EventCalendarParticipantsTable.IP_ADDRESS);
            int entryDay = rs.getInt(EventCalendarParticipantsTable.ENTRY_DAY);
            list.add(new ChallengeParticipant(loginName, displayName, ipAddress, macAddress, entryDay));
        }
        list.sort((p1, p2) -> Collator.getInstance().compare(p1.getUsername(), p2.getUsername()));
        return list;
    }
}
