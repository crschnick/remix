package org.monospark.remix;

import org.monospark.remix.internal.WrappedBooleanImpl;

public sealed interface WrappedBoolean extends WrappedPrimitive<Boolean> permits MutableBoolean, WrappedBooleanImpl {

    boolean get();
}
