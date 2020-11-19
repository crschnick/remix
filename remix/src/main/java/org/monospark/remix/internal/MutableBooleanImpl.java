package org.monospark.remix.internal;

import org.monospark.remix.MutableBoolean;

public final class MutableBooleanImpl extends WrappedBooleanImpl implements MutableBoolean {

    MutableBooleanImpl(RecordParameter param, boolean value) {
        super(param, value);
    }

    public void set(boolean value) {
        var ops = getRecordParameter().getSetOperation();
        if (ops != null) {
            ops.apply(value);
        }

        super.value = value;

    }

    @Override
    public String toString() {
        return "MutableBoolean{" + value + '}';
    }
}