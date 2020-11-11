package org.monospark.remix;

import org.monospark.remix.internal.*;

import java.util.Collection;
import java.util.function.Function;

public interface RecordBuilder<R extends Record> {

    public R build();

    public <T> RecordBuilder<R> set(Function<R, Wrapped<T>> variable, T value);

    public <T> RecordBuilder<R> add(Function<R, Wrapped<? extends Collection<T>>> variable, T... value);
}
