package org.monospark.remix;

import org.monospark.remix.internal.*;

import java.util.Collection;
import java.util.function.*;

public sealed interface RecordBuilder<R extends Record> permits RecordBuilderImpl {

    @FunctionalInterface
    interface WrappedFunction<R extends Record, T> {
        Wrapped<T> apply(R r);
    }

    @FunctionalInterface
    interface WrappedBooleanFunction<R extends Record> {
        WrappedBoolean apply(R r);
    }

    @FunctionalInterface
    interface WrappedSupplier<W,T extends Wrapped<W>> {
        W supply();
    }

    R build();
    RecordBlank<R> blank();

    <T> RecordBuilder<R> set(Function<R, T> component, Supplier<T> value);
    RecordBuilder<R> set(Function<R,Boolean> component, BooleanSupplier value);

    <T> RecordBuilder<R> set(WrappedFunction<R, T> component, WrappedSupplier<T, Wrapped<T>> value);
    RecordBuilder<R> set(WrappedBooleanFunction<R> component, BooleanSupplier value);
}
