package io.xeros.sql.displayname;

import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetDisplayNameSqlQuery implements SqlQuery<String> {

    private final String loginName;

    public GetDisplayNameSqlQuery(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String execute(DatabaseManager context, Connection connection) throws SQLException {
        PreparedStatement select = connection.prepareStatement("SELECT display_name FROM display_names WHERE login_name = ?");
        select.setString(1, loginName.toLowerCase());
        ResultSet rs = select.executeQuery();
        if (!rs.next())
            return null;
        return rs.getString("display_name");
    }

}
