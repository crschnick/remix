package org.monospark.remix;

public final class RecordBlankException extends RuntimeException {

    public RecordBlankException() {
    }

    public RecordBlankException(String message) {
        super(message);
    }

    public RecordBlankException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordBlankException(Throwable cause) {
        super(cause);
    }
}
