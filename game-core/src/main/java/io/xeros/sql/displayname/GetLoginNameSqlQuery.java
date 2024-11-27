package io.xeros.sql.displayname;

import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetLoginNameSqlQuery implements SqlQuery<String> {

    private final String displayName;

    public GetLoginNameSqlQuery(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String execute(DatabaseManager context, Connection connection) throws SQLException {
        PreparedStatement select = connection.prepareStatement("SELECT login_name FROM display_names WHERE display_name_lower = ?");
        select.setString(1, displayName.toLowerCase());
        ResultSet rs = select.executeQuery();
        if (!rs.next())
            return null;
        return rs.getString("login_name");
    }

}
