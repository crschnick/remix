package org.monospark.remix.internal;

import org.monospark.remix.MutableBoolean;
import org.monospark.remix.WrappedBoolean;
import org.monospark.remix.internal.RecordParameter;
import org.monospark.remix.internal.WrappedBooleanImpl;

public final class MutableBooleanImpl extends WrappedBooleanImpl implements MutableBoolean {

    MutableBooleanImpl(RecordParameter param, boolean value) {
        super(param, value);
    }

    public void set(boolean value) {
            super.value = value;

    }
}