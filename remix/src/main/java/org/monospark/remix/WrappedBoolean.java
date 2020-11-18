package org.monospark.remix;

import org.monospark.remix.internal.WrappedBooleanImpl;

public sealed interface WrappedBoolean extends Wrapped<Boolean>permits MutableBoolean, WrappedBooleanImpl {

    boolean getBoolean();
}
