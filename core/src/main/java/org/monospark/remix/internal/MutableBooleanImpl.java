package org.monospark.remix.internal;

import org.monospark.remix.MutableBoolean;

public final class MutableBooleanImpl extends WrappedBooleanImpl implements MutableBoolean {

    MutableBooleanImpl(RecordParameter param, boolean value) {
        super(param, value);
    }

    public void set(boolean value) {
        super.value = value;

    }
}