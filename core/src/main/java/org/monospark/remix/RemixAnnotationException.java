package org.monospark.remix;

public final class RemixAnnotationException extends RuntimeException {

    public RemixAnnotationException() {
    }

    public RemixAnnotationException(String message) {
        super(message);
    }

    public RemixAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemixAnnotationException(Throwable cause) {
        super(cause);
    }
}
