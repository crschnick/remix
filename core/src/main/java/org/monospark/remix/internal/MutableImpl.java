package org.monospark.remix.internal;

import org.monospark.remix.Mutable;

public final class MutableImpl<T> extends WrappedImpl<T> implements Mutable<T> {

    public MutableImpl(RecordParameter param, T value) {
        super(param, value);
    }

    @Override
    public void set(T value) {
        super.value = value;
    }
}
