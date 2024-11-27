package io.xeros.sql.wogw;

import io.xeros.content.wogw.WogwContribution;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GetRecentContributionsSqlQuery implements SqlQuery<List<WogwContribution>> {

    private final int maximumResults;

    public GetRecentContributionsSqlQuery(int maximumResults) {
        this.maximumResults = maximumResults;
    }

    @Override
    public List<WogwContribution> execute(DatabaseManager context, Connection connection) throws Exception {
        Statement statement = connection.createStatement();
        statement.setMaxRows(maximumResults);
        ResultSet rs = statement.executeQuery("SELECT wogw_recent_contributions.total, display_names.display_name FROM wogw_recent_contributions" +
                " INNER JOIN display_names ON display_names.login_name = wogw_recent_contributions.login_name" +
                " ORDER BY date DESC"
        );

        List<WogwContribution> list = new ArrayList<>();
        while (rs.next()) {
            String displayName = rs.getString("display_name");
            long contribution = rs.getLong("total");
            list.add(new WogwContribution(displayName, contribution));
        }

        return list;
    }
}
