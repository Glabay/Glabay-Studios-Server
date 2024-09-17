package io.xeros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.xeros.content.events.eventcalendar.ChallengeParticipant;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import io.xeros.util.Misc;

public class SelectWinnerQuery implements SqlQuery<ChallengeParticipant> {

    private final int day;

    public SelectWinnerQuery(int day) {
        this.day = day;
    }

    @Override
    public ChallengeParticipant execute(DatabaseManager context, Connection connection) throws SQLException {
        List<ChallengeParticipant> currentParticipants = new GetParticipantsListQuery(day).execute(context, connection);

        if (currentParticipants.isEmpty()) {
            return null;
        } else {
            ChallengeParticipant winner = currentParticipants.get(Misc.trueRand(currentParticipants.size()));
            new AddWinnerQuery(winner).execute(context, connection);
            return winner;
        }
    }
}
