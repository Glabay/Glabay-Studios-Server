package io.xeros.sql.displayname;

import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckDisplayNameNotTakenSqlQuery implements SqlQuery<Boolean> {

    private final String newDisplayName;

    public CheckDisplayNameNotTakenSqlQuery(String newDisplayName) {
        this.newDisplayName = newDisplayName;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        var select = connection.prepareStatement("SELECT display_name FROM display_names WHERE display_name_lower = ?");
            select.setString(1, newDisplayName.toLowerCase());
        ResultSet rs = select.executeQuery();
        return !rs.next(); // Return true if the display name is not taken
    }
}
