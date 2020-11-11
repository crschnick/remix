package org.monospark.remix;

import org.monospark.remix.internal.MutableBooleanImpl;
import org.monospark.remix.internal.MutableImpl;
import org.monospark.remix.internal.RecordParameter;
import org.monospark.remix.internal.WrappedImpl;

public sealed interface Mutable<T> extends Wrapped<T> permits MutableImpl {

    public void set(T value);
}
