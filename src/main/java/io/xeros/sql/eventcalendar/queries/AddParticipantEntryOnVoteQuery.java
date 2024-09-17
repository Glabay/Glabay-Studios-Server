package io.xeros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.xeros.content.events.eventcalendar.ChallengeParticipant;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.DatabaseTable;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.eventcalendar.tables.EventCalendarParticipantsTable;

public class AddParticipantEntryOnVoteQuery implements SqlQuery<Boolean> {

    private static final DatabaseTable TABLE = new EventCalendarParticipantsTable();

    private final ChallengeParticipant participant;

    public AddParticipantEntryOnVoteQuery(ChallengeParticipant participant) {
        this.participant = participant;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + TABLE.getName() + " WHERE "
                + EventCalendarParticipantsTable.USERNAME + "='" + participant.getUsername() + "'"
                + " AND " + EventCalendarParticipantsTable.ENTRY_DAY + "=" + participant.getEntryDay());
        int count = 0;
        while (rs.next()) {
            count++;
        }

        if (count == 1) {
            new AddParticipantQuery(participant, 1).execute(context, connection);
            return true;
        } else {
            return false;
        }
    }

}
