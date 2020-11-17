package org.monospark.remix;

public final class RemixException extends RuntimeException {

    public RemixException() {
    }

    public RemixException(String message) {
        super(message);
    }

    public RemixException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemixException(Throwable cause) {
        super(cause);
    }
}
