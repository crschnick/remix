package org.monospark.remix.internal;

import org.monospark.remix.MutableInt;
import org.monospark.remix.WrappedInt;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Objects;

public sealed class WrappedIntImpl extends Wrapper implements WrappedInt permits MutableIntImpl {

    protected int value;

    WrappedIntImpl(RecordParameter recordParameter, int value) {
        super(recordParameter);
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrappedIntImpl that = (WrappedIntImpl) o;
        return value == that.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int getInt() {
        var ops = getRecordParameter().getGetOperation();
        if (ops != null) {
            return (int) getRecordParameter().getGetOperation().apply(value);
        }
        return value;
    }

    @Override
    public Integer get() {
        return getInt();
    }
}
