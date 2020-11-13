package org.monospark.remix.internal;

import org.monospark.remix.Wrapped;
import org.monospark.remix.WrappedInt;

import java.util.Objects;

public final class WrappedIntImpl extends Wrapper implements WrappedInt {

    private int value;

    WrappedIntImpl(RecordParameter recordParameter, int value) {
        super(recordParameter);
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public int get() {
        return value;
    }

    @Override
    public Wrapped<Integer> convert() {
        return new WrappedImpl<>(getRecordParameter(), value);
    }
}
