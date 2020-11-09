package org.monospark.remix;

import org.monospark.remix.internal.RecordParameter;

public class MutableBoolean extends WrappedBoolean {

    MutableBoolean(RecordParameter param, boolean value) {
        super(param, value);
    }

    public void set(boolean value) {

        super.value = getRecordParameter().applyActions(value);
    }
}