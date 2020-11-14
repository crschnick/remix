package org.monospark.remix;

import org.monospark.remix.internal.WrappedIntImpl;

public sealed interface WrappedInt extends Wrapped<Integer>permits WrappedIntImpl {

    int getInt();
}
