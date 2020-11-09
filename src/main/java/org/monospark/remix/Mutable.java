package org.monospark.remix;

import org.monospark.remix.internal.RecordParameter;

public class Mutable<T> extends Wrapped<T> {

    public Mutable(RecordParameter param, T value) {
        super(param, value);
    }

    public void set(T value) {
        super.value = getRecordParameter().applyActions(value);
    }
}
