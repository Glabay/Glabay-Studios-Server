package io.xeros.content.events.eventcalendar;

import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.sql.DatabaseTable;
import io.xeros.sql.eventcalendar.queries.AddToBlacklistQuery;
import io.xeros.sql.eventcalendar.queries.RemoveFromBlacklistQuery;
import io.xeros.sql.eventcalendar.tables.EventCalendarParticipantsTable;
import io.xeros.sql.eventcalendar.tables.EventCalendarWinnersTable;

public class EventCalendarHelper {

    public static void blacklistCommand(Player c, String playerCommand, boolean blacklist) {
        String[] split = playerCommand.split(" ", 2);
        if (split.length < 2) {
            c.sendMessage("Invalid command format: [::calblacklist username with spaces]");
        } else {
            String username = split[1];
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(username);
            if (optionalPlayer.isPresent()) {
                Player other = optionalPlayer.get();
                final ChallengeParticipant participant = new ChallengeParticipant(other, EventCalendar.getDateProvider());
                Server.getDatabaseManager().exec((context, connection) -> {
                    if (blacklist) {
                        if (new AddToBlacklistQuery(participant).execute(context, connection) != null) {
                            synchronized (c) {
                                c.sendMessage("Successfully blacklisted player: " + username);
                            }
                            synchronized (other) {
                                other.sendMessage("You've been prevented from competing in the rest of the " + EventCalendar.EVENT_NAME + ".");
                            }
                        } else {
                            synchronized (c) {
                                c.sendMessage("An unexpected error occurred while attempting to blacklist player: " + username);
                            }
                        }
                    } else {
                        if (new RemoveFromBlacklistQuery(participant).execute(context, connection) != null) {
                            synchronized (c) {
                                c.sendMessage("Successfully unblacklisted player: " + username);
                            }
                            synchronized (other) {
                                other.sendMessage("You are now allowed to compete in the " + EventCalendar.EVENT_NAME + ".");
                            }
                        } else {
                            synchronized (c) {
                                c.sendMessage("An unexpected error occurred while attempting to unblacklist player: " + username);
                            }
                        }
                    }
                    return null;
                });
            } else {
                c.sendMessage("Player cannot be found: " + username);
            }
        }
    }

    public static boolean doTestingCommand(Player c, String command) throws ExecutionException, InterruptedException {
        if (!c.isManagement()) {
            return false;
        }

        String[] split = command.split(" ");
        switch (split[0]) {
            case "calprogress":
                c.sendMessage("Selected winner for day " + EventCalendar.getDateProvider().getDay());
                EventCalendar.setDateProvider(new DateProviderEventStarted(EventCalendar.getDateProvider().getDay() + 1));
                if (Server.isDebug()) {
                    tickCalendarProgress(c, -1);
                    c.sendMessage("Debug mode, progressing calendar.");
                }
                c.sendMessage("Set calendar to day " + EventCalendar.getDateProvider().getDay());
                c.getEventCalendar().openCalendar();
                return true;
            case "calreset":
                if (Server.isDebug()) {
                    resetTables();
                    resetCalendarProgress(c);
                    c.sendMessage("Reset calendar tables and local player progress");
                }
                return true;
            case "caltick":
                int ticks = -1;
                if (split.length == 2)
                    ticks = Integer.parseInt(split[1]);
                tickCalendarProgress(c, ticks);
                c.getEventCalendar().openCalendar();
                return true;
            case "caltestall":
                testAllDays(c);
                return true;
            case "calstart":
                setCalendarDayTestOnly(c, 1);
                return true;
        }
        return false;
    }

    private static void resetCalendarProgress(Player c) {
        for (EventCalendarDay day : EventCalendarDay.values()) {
            c.getEventCalendar().set(new EventChallengeKey(day, day.getChallenge()), 0);
        }
    }

    private static void resetTables() throws ExecutionException, InterruptedException {
        Server.getDatabaseManager().exec((context, connection) -> {
            Statement statement = connection.createStatement();
            final DatabaseTable PARTICIPANTS_TABLE = new EventCalendarParticipantsTable();
            final DatabaseTable WINNERS_TABLE = new EventCalendarWinnersTable();
            if (context.isTablePresent(PARTICIPANTS_TABLE, connection)) {
                statement.executeUpdate("DELETE FROM " + PARTICIPANTS_TABLE.getName());
            }
            if (context.isTablePresent(WINNERS_TABLE, connection)) {
                statement.executeUpdate("DELETE FROM " + WINNERS_TABLE.getName());
            }
            return null;
        }).get();
    }

    private static void testAllDays(Player c) throws ExecutionException, InterruptedException {
        resetTables();
        resetCalendarProgress(c);

        for (EventCalendarDay day : EventCalendarDay.values()) {
            setCalendarDayTestOnly(c, day.getDay());
            Future<?> tickFuture = tickCalendarProgress(c, -1);
            if (tickFuture != null) {
                tickFuture.get();
            }
        }
        setCalendarDayTestOnly(c, 999);
    }

    private static Future<?> tickCalendarProgress(Player c, int ticks) {
        EventChallenge challenge = EventCalendar.getChallenge();
        if (challenge == null) {
            c.sendMessage("No challenge for today, use ::calday day_number");
        } else {
            ticks = ticks == -1 ? challenge.getTicks() : ticks;
            c.getEventCalendar().progress(challenge, ticks);
        }
        return null;
    }

    private static void setCalendarDayTestOnly(Player c, int day) {
        c.sendMessage("Setting event calendar day to " + day);
        if ((day - 1) >= EventCalendarDay.values().length) {
            EventCalendar.setDateProvider(new DateProviderEventEnded());
            c.sendMessage("You set a day after the event ended, event will now end.");
        } else {
            EventCalendar.setDateProvider(new DateProviderEventStarted(day));
        }
    }

}
