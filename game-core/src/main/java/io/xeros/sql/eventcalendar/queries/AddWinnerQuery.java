package io.xeros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import io.xeros.content.events.eventcalendar.ChallengeParticipant;
import io.xeros.content.events.eventcalendar.ChallengeWinner;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.eventcalendar.tables.EventCalendarWinnersTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddWinnerQuery implements SqlQuery<Object> {

    private static final Logger logger = LoggerFactory.getLogger(AddWinnerQuery.class);

    public static void addWinner(DatabaseManager databaseManager, String winner, int day) throws ExecutionException, InterruptedException {
        List<ChallengeWinner> winners = databaseManager.exec(new GetWinnersListQuery()).get().getResult();
        Optional<ChallengeWinner> presentWinner = winners.stream().filter(it -> it.getDay() == day).findFirst();
        if (presentWinner.isPresent()) {
            logger.error("Winner already exists for day " + day + ", name: " + presentWinner.get().getUsername());
        } else {
            databaseManager.exec(new AddWinnerQuery(new ChallengeParticipant(winner, "manual_entry_no_address", "manual_entry_no_address", day))).get();
            logger.info("Added winner " + winner + " for day " + day);
        }
    }

    private static final EventCalendarWinnersTable TABLE = new EventCalendarWinnersTable();
    private final ChallengeParticipant participant;

    public AddWinnerQuery(ChallengeParticipant participant) {
        this.participant = participant;
    }

    @Override
    public Object execute(DatabaseManager context, Connection connection) throws SQLException {
        connection.createStatement().execute("INSERT INTO " + TABLE.getName() + " VALUES (" + "'" + participant.getUsername() + "'," + participant.getEntryDay() + ")");
        return null;
    }
}
