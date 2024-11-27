package io.xeros.sql;

public class BatchedSqlQuery {

    private final DatabaseCredentials databaseCredentials;
    private final SqlQuery<?> sqlQuery;

    public BatchedSqlQuery(DatabaseCredentials databaseCredentials, SqlQuery<?> sqlQuery) {
        this.databaseCredentials = databaseCredentials;
        this.sqlQuery = sqlQuery;
    }

    public DatabaseCredentials getDatabaseCredentials() {
        return databaseCredentials;
    }

    public SqlQuery<?> getSqlQuery() {
        return sqlQuery;
    }
}
