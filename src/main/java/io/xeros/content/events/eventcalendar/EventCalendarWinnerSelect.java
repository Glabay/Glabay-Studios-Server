package io.xeros.content.events.eventcalendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import io.xeros.Server;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.sql.eventcalendar.queries.GetParticipantsListQuery;
import io.xeros.sql.eventcalendar.queries.GetWinnersListQuery;
import io.xeros.sql.eventcalendar.queries.SelectWinnerQuery;
import io.xeros.util.Misc;

public class EventCalendarWinnerSelect {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(EventCalendarWinnerSelect.class.getName());
    private static final EventCalendarWinnerSelect INSTANCE = new EventCalendarWinnerSelect();

    public static EventCalendarWinnerSelect getInstance() {
        return INSTANCE;
    }

    public EventCalendarWinnerSelect() {
    }

    public void init() {
        int cycles = Server.isDebug() ? 2 : Misc.toCycles(1, TimeUnit.HOURS) + 1;
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                selectWinners();
            }
        }, cycles);
    }

    private void selectWinners() {
        final LocalDate time = EventCalendar.getDateProvider().getLocalDate();
        Server.getDatabaseManager().exec((context, connection) -> {
            List<ChallengeWinner> winnerList = new GetWinnersListQuery().execute(context, connection);
            for (EventCalendarDay day : EventCalendarDay.values()) {
                List<ChallengeParticipant> participantList = new GetParticipantsListQuery(day.getDay()).execute(context, connection);
                if (time.getDayOfMonth() != day.getDay() && !participantList.isEmpty() && winnerList.stream().noneMatch(winner -> winner.getDay() == day.getDay())) {
                    ChallengeParticipant challengeParticipant = new SelectWinnerQuery(day.getDay()).execute(context, connection);
                    if (challengeParticipant != null) {
                        log.info("Winner selected " + challengeParticipant + " for " + day);
                        PlayerHandler.addQueuedAction(() -> {
                            String dayString = Misc.formatPlayerName(EventCalendar.MONTH.toString().toLowerCase()) + " " + day.getDay();
                            PlayerHandler.executeGlobalMessage(EventCalendar.MESSAGE_COLOUR + Misc.formatPlayerName(challengeParticipant.getUsername()) + " has been selected as " + dayString + "\'s " + EventCalendar.EVENT_NAME + " winner!");
                            Optional<Player> playerOptional = PlayerHandler.getOptionalPlayerByLoginName(challengeParticipant.getUsername());
                            if (playerOptional.isPresent()) {
                                Player player = playerOptional.get();
                                player.sendMessage(EventCalendar.MESSAGE_COLOUR + "Congratulations, you\'ve been selected as " + dayString + "\'s event winner!");
                                player.sendMessage(EventCalendar.MESSAGE_COLOUR + "You can post a message on the discord whenever your ready to collect your reward.");
                            }
                        });
                    } else {
                        log.severe("Could not select event calendar winner for day " + day);
                    }
                }
            }
            return null;
        });
    }
}
