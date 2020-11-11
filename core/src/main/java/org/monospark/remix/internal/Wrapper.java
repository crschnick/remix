package org.monospark.remix.internal;

public class Wrapper {

    private RecordParameter recordParameter;

    Wrapper(RecordParameter recordParameter) {
        this.recordParameter = recordParameter;
    }

    RecordParameter getRecordParameter() {
        return recordParameter;
    }
}
