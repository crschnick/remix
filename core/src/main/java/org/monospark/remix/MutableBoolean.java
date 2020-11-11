package org.monospark.remix;

import org.monospark.remix.internal.MutableBooleanImpl;

public sealed interface MutableBoolean permits MutableBooleanImpl {

    void set(boolean value);
}
