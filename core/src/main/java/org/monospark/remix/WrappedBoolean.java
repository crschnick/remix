package org.monospark.remix;

import org.monospark.remix.internal.WrappedBooleanImpl;

public sealed interface WrappedBoolean permits WrappedBooleanImpl {

    boolean get();
}
