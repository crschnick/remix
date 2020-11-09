package org.monospark.remix;

import org.monospark.remix.internal.RecordParameter;

public class WrappedBoolean extends Wrapper {

    protected boolean value;

    public WrappedBoolean(RecordParameter recordParameter, boolean value) {
        super(recordParameter);
        this.value = value;
    }

    public boolean get() {
        return value;
    }
}
