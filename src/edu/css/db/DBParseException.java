package edu.css.db;

/**
 * Catalin Dumitru
 * Universitatea Alexandru Ioan Cuza
 */
public class DBParseException extends RuntimeException {
    public DBParseException() {
    }

    public DBParseException(String message) {
        super(message);
    }

    public DBParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBParseException(Throwable cause) {
        super(cause);
    }

    public DBParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
