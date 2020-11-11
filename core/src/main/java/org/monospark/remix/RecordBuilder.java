package org.monospark.remix;

import org.monospark.remix.internal.*;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public sealed interface RecordBuilder<R extends Record> permits RecordBuilderImpl {

    @FunctionalInterface
    interface WrappedFunction<R extends Record, T> {
        Wrapped<T> apply(R r);
    }

    @FunctionalInterface
    interface WrappedIntFunction<R extends Record> {
        WrappedInt apply(R r);
    }

    @FunctionalInterface
    interface WrappedBooleanFunction<R extends Record> {
        WrappedBoolean apply(R r);
    }

    public R build();

    <T> RecordBuilder<R> set(Function<R, T> component, T value);
    RecordBuilder<R> set(ToIntFunction<R> component, int value);
    RecordBuilder<R> set(Function<R,Boolean> component, boolean value);


    <T> RecordBuilder<R> set(WrappedFunction<R, T> component, T value);
    RecordBuilder<R> set(WrappedBooleanFunction<R> component, boolean value);
    RecordBuilder<R> set(WrappedIntFunction<R> component, int value);

    <CT,C extends Collection<CT>, T extends CT> RecordBuilder<R> add(Function<R, CT> variable, T... value);

    <CT,C extends Collection<CT>, T extends CT> RecordBuilder<R> add(WrappedFunction<R,C> variable, T... value);
}
