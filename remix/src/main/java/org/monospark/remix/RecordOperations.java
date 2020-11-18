package org.monospark.remix;

import org.monospark.remix.internal.RecordOperationsImpl;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A builder for an operation containers that maps operations to record components.
 * <p>
 * Note that all operations added to this builder are performed lazily,
 * i.e. they will only be validated when needed. An {@code RecordResolveException}
 * will only be thrown when the operations are performed and
 * the specified record component can not be resolved.
 **/
public sealed interface RecordOperations<R extends Record>permits RecordOperationsImpl {

    /**
     * Adds a new operation to a record component.
     *
     * @param component the record component
     * @param operation the operation that should be performed
     * @throws NullPointerException if any argument is null
     **/
    <T> RecordOperations<R> add(LambdaSupport.WrappedFunction<R, T> component, UnaryOperator<T> operation);

    /**
     * Adds a new operation to a record component that verifies that the input is not null.
     *
     * @param component the record component
     * @throws NullPointerException if {@code component} is null
     **/
    <T> RecordOperations<R> notNull(LambdaSupport.WrappedFunction<R, T> component);

    /**
     * Adds a new operation to a record component that verifies if a given predicate is true.
     *
     * @param component the record component
     * @throws NullPointerException if {@code component} is null
     **/
    <T> RecordOperations<R> check(LambdaSupport.WrappedFunction<R, T> component, Predicate<T> toCheck);

    /**
     * Returns a component identifier that indicates that an
     * operation should be performed on all record components.
     **/
    <T> LambdaSupport.WrappedFunction<R, T> all();
}
