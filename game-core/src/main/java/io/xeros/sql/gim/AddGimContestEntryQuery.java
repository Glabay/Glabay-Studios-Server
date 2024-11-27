package io.xeros.sql.gim;

import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.model.entity.player.mode.group.contest.ContestType;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AddGimContestEntryQuery implements SqlQuery<Object> {

    private static final GimContestTotalTable TOTALS = new GimContestTotalTable();
    private static final GimContestPlayerTable PLAYERS = new GimContestPlayerTable();

    private final GroupIronmanGroup group;
    private final String playerName;
    private final int collectionLogEntries;
    private final int petsInBank;
    private final int earnedExchangePoints;
    private final int tobCompletions;

    /**
     * @param group
     * @param collectionLogEntries Entries total for the group.
     * @param petsInBank Pets total for the group.
     * @param earnedExchangePoints Exchange points for the player.
     * @param tobCompletions TOB completions for the player.
     */
    public AddGimContestEntryQuery(GroupIronmanGroup group, String playerName, int collectionLogEntries, int petsInBank, int earnedExchangePoints, int tobCompletions) {
        this.group = group;
        this.playerName = playerName;
        this.collectionLogEntries = collectionLogEntries;
        this.petsInBank = petsInBank;
        this.earnedExchangePoints = earnedExchangePoints;
        this.tobCompletions = tobCompletions;
    }

    @Override
    public Object execute(DatabaseManager context, Connection connection) throws SQLException {
        // Pets and collection logs are done on a group basis, so we skip them here.
        updatePlayer(connection, ContestType.EARNED_EXCHANGE_POINTS, earnedExchangePoints);
        updatePlayer(connection, ContestType.TOB_COMPLETIONS, tobCompletions);
        updateTotals(connection);
        return null;
    }

    private void updateTotals(Connection connection) throws SQLException {
        Map<ContestType, Integer> values = new HashMap<>();

        // These two are done on a group basis, not calculated per player
        values.put(ContestType.COLLECTION_LOG, collectionLogEntries);
        values.put(ContestType.PETS_IN_BANK, petsInBank);

        PreparedStatement playerValues = connection.prepareStatement("SELECT * FROM gim_contest_players WHERE group_name = ?");
        playerValues.setString(1, group.getName());
        ResultSet rs = playerValues.executeQuery();

        while (rs.next()) {
            ContestType type = ContestType.forInt(rs.getInt("type"));
            int value = rs.getInt("value");
            values.put(type, values.getOrDefault(type, 0) + value);
        }

        for (ContestType type : ContestType.values())
            insertTotal(connection, type, values);
    }

    private void updatePlayer(Connection connection, ContestType type, int value) throws SQLException {
        PreparedStatement delete = connection.prepareStatement("DELETE FROM gim_contest_players WHERE username = ? AND type = ?");
        delete.setString(1, playerName.toLowerCase());
        delete.setInt(2, type.getIntValue());
        delete.execute();

        PreparedStatement insert = connection.prepareStatement("INSERT INTO gim_contest_players (group_name, username, type, value) VALUES(?, ?, ?, ?)");
        insert.setString(1, group.getName());
        insert.setString(2, playerName.toLowerCase());
        insert.setInt(3, type.getIntValue());
        insert.setInt(4, value);
        insert.execute();
    }

    private void insertTotal(Connection connection, ContestType type, Map<ContestType, Integer> values) throws SQLException {
        PreparedStatement delete = connection.prepareStatement("DELETE FROM gim_contest_totals WHERE group_name = ? AND type = ?");
        delete.setString(1, group.getName());
        delete.setInt(2, type.getIntValue());
        delete.execute();

        PreparedStatement insert = connection.prepareStatement("INSERT INTO gim_contest_totals (group_name, type, value) VALUES(?, ?, ?)");
        insert.setString(1, group.getName());
        insert.setInt(2, type.getIntValue());
        insert.setInt(3, values.get(type));
        insert.execute();
    }
}
