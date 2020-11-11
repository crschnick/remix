package org.monospark.remix.internal;

import org.monospark.remix.RecordBuilder;
import org.monospark.remix.Wrapped;

import java.util.Collection;
import java.util.function.Function;

public class BareBuilder<R extends Record> implements RecordBuilder<R> {
    @Override
    public R build() {
        return null;
    }

    @Override
    public <T> RecordBuilder<R> set(Function<R, Wrapped<T>> variable, T value) {
        return null;
    }

    @Override
    public <T> RecordBuilder<R> add(Function<R, Wrapped<? extends Collection<T>>> variable, T... value) {
        return null;
    }
}
