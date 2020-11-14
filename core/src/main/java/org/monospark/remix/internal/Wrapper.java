package org.monospark.remix.internal;

import java.io.Serializable;
import java.util.Objects;

public abstract class Wrapper implements Serializable {

    protected RecordParameter recordParameter;

    Wrapper(RecordParameter recordParameter) {
        Objects.requireNonNull(recordParameter);
        this.recordParameter = recordParameter;
    }

    RecordParameter getRecordParameter() {
        return recordParameter;
    }
}
