package org.monospark.remix;

import org.monospark.remix.internal.RecordParameter;

public abstract class Wrapper {

    private RecordParameter recordParameter;

    public Wrapper(RecordParameter recordParameter) {
        this.recordParameter = recordParameter;
    }

    RecordParameter getRecordParameter() {
        return recordParameter;
    }
}
