package io.xeros.sql.eventcalendar.queries;

import io.xeros.content.events.eventcalendar.ChallengeParticipant;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.eventcalendar.tables.EventCalendarParticipantsTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Skips the first day for now, remember to change that later!
 */
public class GetMonthlyCalendarParticipants implements SqlQuery<List<ChallengeParticipant>> {

    private static final EventCalendarParticipantsTable TABLE = new EventCalendarParticipantsTable();

    @Override
    public List<ChallengeParticipant> execute(DatabaseManager context, Connection connection) throws SQLException {
        ResultSet result = connection.createStatement().executeQuery("select * from " + TABLE.getName()
                + " INNER JOIN display_names ON calendar_participants.username = display_names.login_name"
                + " where " + EventCalendarParticipantsTable.ENTRY_DAY + " != 1"
        );
        List<ChallengeParticipant> participants = new ArrayList<>();
        while (result.next()) {
            String displayName = result.getString("display_name");
            String username = result.getString(EventCalendarParticipantsTable.USERNAME);
            String ip = result.getString(EventCalendarParticipantsTable.IP_ADDRESS);
            String mac = result.getString(EventCalendarParticipantsTable.MAC_ADDRESS);
            int day = result.getInt(EventCalendarParticipantsTable.ENTRY_DAY);
            participants.add(new ChallengeParticipant(username, displayName, ip, mac, day));
        }
        return participants;
    }
}
