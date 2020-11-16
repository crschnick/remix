package org.monospark.remix;

import org.monospark.remix.internal.RecordOperationsImpl;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A builder for an operation container.
 **/
public sealed interface RecordOperations<R extends Record>permits RecordOperationsImpl {

    /**
     * Adds a new operation to a record component.
     * Overrides any previously added operation set for the same component.
     **/
    <T> RecordOperations<R> add(LambdaSupport.WrappedFunction<R,T> component, UnaryOperator<T> op);

    <T> RecordOperations<R> notNull(LambdaSupport.WrappedFunction<R,T> component);

    <T> RecordOperations<R> check(LambdaSupport.WrappedFunction<R,T> component, Predicate<T> toCheck);

    <T> LambdaSupport.WrappedFunction<R,T> all();
}
