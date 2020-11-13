package org.monospark.remix.internal;

public class Wrapper {

    protected RecordParameter recordParameter;

    Wrapper(RecordParameter recordParameter) {
        this.recordParameter = recordParameter;
    }

    RecordParameter getRecordParameter() {
        return recordParameter;
    }
}
