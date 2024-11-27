package io.xeros.sql.displayname;

import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetDisplayNameSqlQuery implements SqlQuery<Boolean> {

    private final String loginName;
    private final String newDisplayName;

    public SetDisplayNameSqlQuery(String loginName, String newDisplayName) {
        this.loginName = loginName;
        this.newDisplayName = newDisplayName;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        boolean available = new CheckDisplayNameNotTakenSqlQuery(newDisplayName).execute(context, connection);
        if (!available) {
            return false;
        }

        String current = new GetDisplayNameSqlQuery(loginName).execute(context, connection);

        if (current == null) {
            PreparedStatement insert = connection.prepareStatement("INSERT INTO display_names VALUES(?, ?, ?)");
            insert.setString(1, loginName.toLowerCase());
            insert.setString(2, newDisplayName);
            insert.setString(3, newDisplayName.toLowerCase());
            insert.execute();
            return true;
        } else {
            PreparedStatement update = connection.prepareStatement("UPDATE display_names SET display_name = ?, display_name_lower = ? WHERE login_name = ?");
            update.setString(1, newDisplayName);
            update.setString(2, newDisplayName.toLowerCase());
            update.setString(3, loginName.toLowerCase());
            update.execute();
            return true;
        }
    }
}
