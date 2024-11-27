package io.xeros.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides a {@link Connection}
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public interface ConnectionProvider {

    /**
     * Gets the {@link Connection}
     * @return the {@link Connection}
     */
    Connection getConnection() throws SQLException;

}
