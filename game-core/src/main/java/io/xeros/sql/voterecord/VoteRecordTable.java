package io.xeros.sql.voterecord;

import io.xeros.sql.DatabaseTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringJoiner;

/**
 * @author Chris | 8/14/21
 */
public class VoteRecordTable implements DatabaseTable {
    public static final String IP_ADDRESS_COLUMN = "ip_address";
    public static final String MAC_ADDRESS_COLUMN = "mac_address";
    public static final String UUID_COLUMN = "uuid";
    public static final String DATE_CLAIMED_COLUMN = "date_claimed";

    @Override
    public String getName() {
        return "vote_record";
    }

    @Override
    public void createTable(Connection connection) throws SQLException {
        StringJoiner primaryKeys = new StringJoiner(",");
        primaryKeys.add(IP_ADDRESS_COLUMN);
        primaryKeys.add(MAC_ADDRESS_COLUMN);
        primaryKeys.add(UUID_COLUMN);

        StringJoiner columns = new StringJoiner(",");
        columns.add(IP_ADDRESS_COLUMN + " VARCHAR(255)");
        columns.add(MAC_ADDRESS_COLUMN + " VARCHAR(255)");
        columns.add(UUID_COLUMN + " VARCHAR(255)");
        columns.add(DATE_CLAIMED_COLUMN + " TIMESTAMP");
        columns.add("PRIMARY KEY(" + primaryKeys + ")");

        connection.createStatement().execute(
                "CREATE TABLE " + getName()
                        + " (" + columns + ")");
    }
}
