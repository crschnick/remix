package org.monospark.remix.internal;

import org.monospark.remix.MutableInt;

public final class MutableIntImpl extends WrappedIntImpl implements MutableInt {

    MutableIntImpl(RecordParameter param, int value) {
        super(param, value);
    }

    public void set(int value) {
        var ops = getRecordParameter().getSetOperation();
        if (ops != null) {
            ops.apply(value);
        }

        super.value = value;
    }


    @Override
    public String toString() {
        return "MutableInt{" + value + '}';
    }
}