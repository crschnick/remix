package org.monospark.remix.internal;

import org.monospark.remix.WrappedBoolean;

import java.util.Objects;

public sealed class WrappedBooleanImpl extends Wrapper implements WrappedBoolean permits MutableBooleanImpl {

    protected boolean value;

    public WrappedBooleanImpl(RecordParameter recordParameter, boolean value) {
        super(recordParameter);
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrappedBooleanImpl that = (WrappedBooleanImpl) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }


    @Override
    public String toString() {
        return "WrappedBoolean{" + value + '}';
    }

    @Override
    public boolean getBoolean() {
        return value;
    }

    @Override
    public Boolean get() {
        return value;
    }
}
