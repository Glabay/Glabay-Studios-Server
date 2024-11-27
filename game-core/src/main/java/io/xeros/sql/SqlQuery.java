package io.xeros.sql;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlQuery<T> {

    T execute(DatabaseManager context, Connection connection) throws Exception;

}
