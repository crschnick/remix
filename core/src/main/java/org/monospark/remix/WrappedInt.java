package org.monospark.remix;

import org.monospark.remix.internal.RecordParameter;

public final class WrappedInt extends Wrapper {

    private int value;

    public WrappedInt(RecordParameter recordParameter, int value) {
        super(recordParameter);
        this.value = value;
    }

    public int get() {
        return value;
    }
}
