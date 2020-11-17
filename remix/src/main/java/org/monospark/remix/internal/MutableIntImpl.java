package org.monospark.remix.internal;

import org.monospark.remix.MutableBoolean;
import org.monospark.remix.MutableInt;

public final class MutableIntImpl extends WrappedIntImpl implements MutableInt {

    MutableIntImpl(RecordParameter param, int value) {
        super(param, value);
    }

    public void set(int value) {
        super.value = value;

    }
}