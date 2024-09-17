package io.xeros.content.events.eventcalendar;

import io.xeros.Server;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.eventcalendar.queries.GetMonthlyCalendarParticipants;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class EventChallengeMonthlyReward {

    private static final LocalDate CUTOFF = LocalDate.of(2021, Month.JULY, 28);

    public static boolean getsReward(Player player, DatabaseManager context, Connection connection) throws SQLException {
        List<ChallengeParticipant> participants = new GetMonthlyCalendarParticipants().execute(context, connection);
        long daysParticipated = participants.stream().filter(it -> it.getUsername().equalsIgnoreCase(player.getLoginName()))
                .map(ChallengeParticipant::getEntryDay).distinct().count();
        return daysParticipated > 26;
    }

    public static void onLogin(Player player) {
        if (player.isReceivedCalendarCosmeticJune2021() || LocalDate.now().isAfter(CUTOFF)) {
            return;
        }

        Server.getDatabaseManager().exec((context, connection) -> {
            boolean getsReward = getsReward(player, context, connection);

            player.addQueuedAction(plr -> {
                player.setReceivedCalendarCosmeticJune2021(true);
                if (getsReward) {
                    player.sendMessage("Thanks for participating in the Calendar Event!");
                    player.getItems().addItemUnderAnyCircumstance(Items.TWISTED_HAT_T3, 1);
                    player.getItems().addItemUnderAnyCircumstance(Items.TWISTED_COAT_T3, 1);
                    player.getItems().addItemUnderAnyCircumstance(Items.TWISTED_TROUSERS_T3, 1);
                    player.getItems().addItemUnderAnyCircumstance(Items.TWISTED_BOOTS_T3, 1);
                    player.getItems().addItemUnderAnyCircumstance(Items.TWISTED_BANNER, 1);
                }
            });
            return null;
        });
    }

}
