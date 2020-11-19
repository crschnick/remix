package org.monospark.remix.internal;

import org.monospark.remix.Mutable;

public final class MutableImpl<T> extends WrappedImpl<T> implements Mutable<T> {

    public MutableImpl(RecordParameter param, T value) {
        super(param, value);
    }

    @Override
    public void set(T value) {
        var ops = getRecordParameter().getSetOperation();
        if (ops != null) {
            ops.apply(value);
        }

        super.value = value;
    }

    @Override
    public String toString() {
        return "Mutable{" + value + '}';
    }
}
