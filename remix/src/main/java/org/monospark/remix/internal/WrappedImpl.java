package org.monospark.remix.internal;

import org.monospark.remix.Wrapped;

import java.util.Objects;

public sealed class WrappedImpl<T> extends Wrapper implements Wrapped<T>permits MutableImpl {


    protected T value;


    public WrappedImpl(RecordParameter param, T value) {
        super(param);
        this.value = value;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrappedImpl<?> wrapped = (WrappedImpl<?>) o;
        return Objects.equals(value, wrapped.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public T get() {
        var ops = getRecordParameter().getGetOperation();
        if (ops != null) {
            return (T) getRecordParameter().getGetOperation().apply(value);
        }
        return value;
    }
}
