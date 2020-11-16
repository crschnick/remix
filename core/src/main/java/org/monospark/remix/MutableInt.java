package org.monospark.remix;

import org.monospark.remix.internal.MutableIntImpl;

public sealed interface MutableInt extends WrappedInt permits MutableIntImpl {

    public void set(int value);
}
