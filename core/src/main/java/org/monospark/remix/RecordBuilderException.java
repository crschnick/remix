package org.monospark.remix;

public final class RecordBuilderException extends RuntimeException {

    public RecordBuilderException() {
    }

    public RecordBuilderException(String message) {
        super(message);
    }

    public RecordBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordBuilderException(Throwable cause) {
        super(cause);
    }
}
