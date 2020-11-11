package org.monospark.remix.internal;

import org.monospark.remix.WrappedInt;

public final class WrappedIntImpl extends Wrapper implements WrappedInt {

    private int value;

    WrappedIntImpl(RecordParameter recordParameter, int value) {
        super(recordParameter);
        this.value = value;
    }

    public int get() {
        return value;
    }
}
