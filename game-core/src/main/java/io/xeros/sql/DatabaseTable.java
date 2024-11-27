package io.xeros.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A database table.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public interface DatabaseTable {

    /**
     * The database name (always uppercase)
     * @return the database name
     */
    String getName();

    void createTable(Connection connection)  throws SQLException;

}
