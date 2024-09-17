package io.xeros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.SQLException;

import io.xeros.content.events.eventcalendar.ChallengeParticipant;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.DatabaseTable;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.eventcalendar.tables.EventCalendarBlacklistTable;

public class AddToBlacklistQuery implements SqlQuery<Boolean> {

    private static final DatabaseTable TABLE = new EventCalendarBlacklistTable();

    private final ChallengeParticipant participant;

    public AddToBlacklistQuery(ChallengeParticipant participant) {
        this.participant = participant;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        connection.createStatement().execute("insert into " + TABLE.getName()+ " values("
               + "'" + participant.getIpAddress() + "', '" + participant.getMacAddress() + "')");
        return true;
    }

}
