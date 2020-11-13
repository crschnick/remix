package org.monospark.remix;

import org.monospark.remix.internal.WrappedIntImpl;

public sealed interface WrappedInt extends WrappedPrimitive<Integer> permits WrappedIntImpl {

    int get();
}
