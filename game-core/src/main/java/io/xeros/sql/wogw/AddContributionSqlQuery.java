package io.xeros.sql.wogw;

import io.xeros.model.entity.player.Player;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AddContributionSqlQuery implements SqlQuery<Object> {

    private final String loginName;
    private final int contribution;
    private final LocalDateTime date = LocalDateTime.now();

    public AddContributionSqlQuery(Player player, int contribution) {
        this.loginName = player.getLoginNameLower();
        this.contribution = contribution;
    }

    @Override
    public Object execute(DatabaseManager context, Connection connection) throws Exception {
        PreparedStatement insertRecent = connection.prepareStatement("INSERT INTO wogw_recent_contributions VALUES(?, ?, ?)");
        insertRecent.setString(1, loginName);
        insertRecent.setLong(2, contribution);
        insertRecent.setTimestamp(3, Timestamp.valueOf(date));
        insertRecent.execute();

        PreparedStatement select = connection.prepareStatement("SELECT * FROM wogw_total_contributions WHERE login_name = ? FOR UPDATE",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        select.setString(1, loginName);
        ResultSet rs = select.executeQuery();

        if (rs.next()) {
            long total = rs.getLong("total");
            rs.updateLong("total", total + contribution);
            rs.updateRow();
        } else {
            PreparedStatement insert = connection.prepareStatement("INSERT INTO wogw_total_contributions VALUES(?, ?)");
            insert.setString(1, loginName);
            insert.setLong(2, contribution);
            insert.execute();
        }

        return null;
    }
}
