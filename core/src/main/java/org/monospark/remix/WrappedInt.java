package org.monospark.remix;

import org.monospark.remix.internal.WrappedIntImpl;

public sealed interface WrappedInt permits WrappedIntImpl {

    int get();
}
