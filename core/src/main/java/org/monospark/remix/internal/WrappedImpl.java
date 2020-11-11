package org.monospark.remix.internal;

import org.monospark.remix.Mutable;
import org.monospark.remix.Wrapped;
import org.monospark.remix.internal.RecordParameter;

public sealed class WrappedImpl<T> extends Wrapper implements Wrapped<T> permits MutableImpl {

    protected T value;

    public WrappedImpl(RecordParameter param, T value) {
        super(param);
        this.value = value;
    }

    public T get() {
        return value;
    }
}
