package org.monospark.remix;

public class RecordResolveException extends RuntimeException {

    public RecordResolveException() {
    }

    public RecordResolveException(String message) {
        super(message);
    }

    public RecordResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordResolveException(Throwable cause) {
        super(cause);
    }

    public RecordResolveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
