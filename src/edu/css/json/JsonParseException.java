package edu.css.json;

/**
 * Catalin Dumitru
 * Date: 4/27/13
 * Time: 3:15 PM
 */
public class JsonParseException extends RuntimeException {
    public JsonParseException() {
    }

    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonParseException(Throwable cause) {
        super(cause);
    }

    public JsonParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
