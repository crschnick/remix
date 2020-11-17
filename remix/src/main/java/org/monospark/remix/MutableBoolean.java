package org.monospark.remix;

import org.monospark.remix.internal.MutableBooleanImpl;

public sealed interface MutableBoolean extends WrappedBoolean permits MutableBooleanImpl {

    void set(boolean value);
}
