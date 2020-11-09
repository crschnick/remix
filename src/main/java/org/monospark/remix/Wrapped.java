package org.monospark.remix;

import org.monospark.remix.internal.RecordParameter;

public class Wrapped<T> extends Wrapper {

    protected T value;

    public Wrapped(RecordParameter param, T value) {
        super(param);
        this.value = value;
    }

    public T get() {
        return value;
    }
}
