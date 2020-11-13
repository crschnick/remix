package org.monospark.remix;

import org.monospark.remix.internal.OperationsCache;
import org.monospark.remix.internal.WrappedImpl;

import java.util.function.*;

/**
 * A builder for an operation container.
 **/
public sealed interface RecordOperations<R extends Record> permits OperationsCache {

    /**
     * Adds a new operation to a record component.
     * Overrides any previously added operation set for the same component.
     **/
    <T> RecordOperations<R> add(WrappedPrimitiveFunction<R,T> component, UnaryOperator<T> op);
    <T> RecordOperations<R> add(Function<R, Wrapped<T>> component, UnaryOperator<T> op);

    <T> RecordOperations<R> notNull(Function<R, Wrapped<T>> component);

    @FunctionalInterface
    interface WrappedPrimitiveFunction<R extends Record, T> {
        WrappedPrimitive<T> get(R value);

        default Function<R,Wrapped<T>> toFunction() {
            return r -> {
                WrappedPrimitive<T> w = get(r);
                return w != null ? w.convert() : null;
            };
        }
    }

    <T> RecordOperations<R> check(WrappedPrimitiveFunction<R,T> component, Function<T,Boolean> toCheck);
    <T> RecordOperations<R> check(Function<R, Wrapped<T>> component, Function<T,Boolean> toCheck);
}
