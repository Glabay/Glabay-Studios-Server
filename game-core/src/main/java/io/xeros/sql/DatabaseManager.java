package io.xeros.sql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.xeros.Server;
import io.xeros.ServerConfiguration;
import io.xeros.util.logging.global.SqlLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Manages our databases and dispatching sql statements.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class DatabaseManager {

    private static final long BATCHED_SQL_EXECUTE_DELAY = TimeUnit.SECONDS.toMillis(180);

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("database-manager-%d").build());
    private final Map<DatabaseCredentials, ConnectionProvider> dataSources = new ConcurrentHashMap<>();
    private final LinkedBlockingQueue<BatchedSqlQuery> batched = new LinkedBlockingQueue<>();
    private final boolean enabled;

    public DatabaseManager(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            try {
                DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            } catch (SQLException e) {
                logger.error("Error while registering driver", e);
            }

            queueBatchProcessing();
        }
    }

    private void queueBatchProcessing() {
        Server.getIoExecutorService().scheduleAtFixedRate(() -> {
            HashMap<DatabaseCredentials, List<SqlQuery<?>>> map = new HashMap<>();
            BatchedSqlQuery batchedSqlQuery;
            while ((batchedSqlQuery = batched.poll()) != null) {
                if (!map.containsKey(batchedSqlQuery.getDatabaseCredentials()))
                    map.put(batchedSqlQuery.getDatabaseCredentials(), new ArrayList<>());
                map.get(batchedSqlQuery.getDatabaseCredentials()).add(batchedSqlQuery.getSqlQuery());
            }

            for (Map.Entry<DatabaseCredentials, List<SqlQuery<?>>> batch : map.entrySet()) {
                exec(batch.getKey(), ((context, connection) -> {
                    for (SqlQuery<?> query : batch.getValue())
                        query.execute(context, connection);
                    return null;
                }), null);
            }
        }, 0, BATCHED_SQL_EXECUTE_DELAY, TimeUnit.MILLISECONDS);
    }

    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);
        logger.info("Database manager shutdown.");
    }

    public void batch(SqlQuery<?> query) {
        batch(Server.getConfiguration().getLocalDatabase(), query);
    }

    public void batch(DatabaseCredentials credentials, SqlQuery<?> query) {
        if (shouldNotExecute(credentials, query))
            return;
        batched.add(new BatchedSqlQuery(credentials, query));
    }

    /**
     * Calls {@link DatabaseManager#exec(DatabaseCredentials, SqlQuery, Consumer)} on {@link ServerConfiguration#getLocalDatabase()}.
     */
    public <T> Future<DatabaseResult<T>> exec(SqlQuery<T> query) {
        return exec(Server.getConfiguration().getLocalDatabase(), query, null);
    }

    /**
     * Calls {@link DatabaseManager#exec(DatabaseCredentials, SqlQuery, Consumer)} on {@link ServerConfiguration#getLocalDatabase()}.
     */
    public <T> Future<DatabaseResult<T>> exec(SqlQuery<T> query, Consumer<DatabaseResult<T>> callback) {
        return exec(Server.getConfiguration().getLocalDatabase(), query, callback);
    }

    /**
     * Calls {@link DatabaseManager#exec(DatabaseCredentials, SqlQuery, Consumer)}.
     */
    public <T> Future<DatabaseResult<T>> exec(DatabaseCredentials configuration, SqlQuery<T> query) {
        return exec(configuration, query, null);
    }

    /**
     * A newer version of execute that will return a {@link DatabaseResult}, which will give you access
     * to exception checking and the final result of the query. Using this you can handle exceptions
     * inside the code that calls the database execution.
     */
    public <T> Future<DatabaseResult<T>> exec(DatabaseCredentials configuration, SqlQuery<T> query, Consumer<DatabaseResult<T>> callback) {
        if (shouldNotExecute(configuration, query))
            return null;
        return executorService.submit(() -> {
            try {
                T t = executeImmediate(configuration, query);
                DatabaseResult<T> result = new DatabaseResult<>(t, null);
                if (callback != null)
                    callback.accept(result);
                return result;
            } catch (Exception e) {
                logger.error("An error occurred while trying to execute a query, database= {}, query={}", configuration, query, e);
                return new DatabaseResult<>(null, e);
            }
        });
    }

    public <T> T executeImmediate(SqlQuery<T> query) throws Exception {
        return executeImmediate(Server.getConfiguration().getLocalDatabase(), query);
    }

    /**
     * Immediately executes a {@link SqlQuery} on the given {@link DatabaseCredentials}.
     * Do not use this on the main thread.
     */
    public <T> T executeImmediate(DatabaseCredentials configuration, SqlQuery<T> query) throws Exception {
        if (shouldNotExecute(configuration, query))
            return null;
        Server.getLogging().batchWrite(new SqlLog(query.getClass()));
        try (Connection connection = getConnectionProvider(configuration).getConnection()) {
            return query.execute(this, connection);
        } catch (Exception e) {
            logger.error("An error occurred while trying to execute a query, database= {}, query={}", configuration, query, e);
            throw e;
        }
    }

    private boolean shouldNotExecute(DatabaseCredentials credentials, SqlQuery<?> query) {
        if (!enabled)
            return true;
        if (credentials == null) {
            logger.debug("DatabaseCredentials are null for query: {}", query.getClass());
            return true;
        }

        return false;
    }

    public boolean isTablePresent(DatabaseTable table, Connection connection) throws SQLException {
        return isTablePresent(table.getName(), connection);
    }

    public boolean isTablePresent(String table, Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet res = meta.getTables(null, "APP", table.toUpperCase(), null);
        return res.next();
    }

    private synchronized ConnectionProvider getConnectionProvider(DatabaseCredentials configuration) {
        if (dataSources.containsKey(configuration)) {
            return dataSources.get(configuration);
        } else {
            HikariConnectionProvider provider = new HikariConnectionProvider(configuration);
            dataSources.put(configuration, provider);
            return provider;
        }
    }
}
