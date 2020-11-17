package org.monospark.remix;

import org.monospark.remix.internal.MutableImpl;

public sealed interface Mutable<T> extends Wrapped<T>permits MutableImpl {

    public void set(T value);
}
