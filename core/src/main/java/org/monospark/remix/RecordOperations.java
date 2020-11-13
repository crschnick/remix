package org.monospark.remix;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

/**
 * A builder for an operation container.
 **/
public interface RecordOperations<R> {

    /**
     * Adds a new operation to a record component.
     * Overrides any previously added operation set for the same component.
     **/
    <T> RecordOperations<R> add(Function<R, Wrapped<T>> component, UnaryOperator<T> op);

    <T> RecordOperations<R> notNull(Function<R, Wrapped<T>> component);

    <T> RecordOperations<R> check(Function<R, WrappedInt> component, IntFunction<Boolean> toCheck);
    <T> RecordOperations<R> check(Function<R, Wrapped<T>> component, Function<T,Boolean> toCheck);
}