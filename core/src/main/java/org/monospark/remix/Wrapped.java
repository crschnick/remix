package org.monospark.remix;

import org.monospark.remix.internal.WrappedImpl;

public sealed interface Wrapped<T> permits Mutable, WrappedImpl {

    T get();
}
