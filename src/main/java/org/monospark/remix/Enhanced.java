package org.monospark.remix;

import java.util.function.Function;

public interface Enhanced {

    default <R extends Record, T> T get(Function<R, Wrapped<T>> entry) {
        return entry.apply((R) this).get();
    }

}
