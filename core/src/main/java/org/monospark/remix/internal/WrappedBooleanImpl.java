package org.monospark.remix.internal;

import org.monospark.remix.MutableBoolean;
import org.monospark.remix.Wrapped;
import org.monospark.remix.WrappedBoolean;
import org.monospark.remix.internal.RecordParameter;

public sealed class WrappedBooleanImpl extends Wrapper implements WrappedBoolean permits MutableBooleanImpl {

    protected boolean value;

    public WrappedBooleanImpl(RecordParameter recordParameter, boolean value) {
        super(recordParameter);
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    @Override
    public Wrapped<Boolean> convert() {
        return new WrappedImpl<>(getRecordParameter(), value);
    }
}
