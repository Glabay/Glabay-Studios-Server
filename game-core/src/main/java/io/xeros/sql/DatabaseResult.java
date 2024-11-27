package io.xeros.sql;

/**
 * Encapsulates a {@link SqlQuery} result and any exceptions thrown during it's execution.
 * @param <T> The type returned by the {@link SqlQuery}.
 */
public class DatabaseResult<T> {

    private final T result;
    private final Exception exception;

    public DatabaseResult(T result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    public boolean hasErrorOccurred() {
        return exception != null;
    }

    public T getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }
}
