package org.monospark.remix;

import org.monospark.remix.internal.RecordBuilderImpl;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * If any component is not set, the value of a given record component is set to its default value.
 * This is either null for objects or the primitive default value for primitives.
 */
public sealed interface RecordBuilder<R extends Record>permits RecordBuilderImpl {

    /**
     * Creates a new instance of the record class using the previously set record component values.
     *
     * @throws RecordBuilderException if any parameter does not have a set value or
     *                                any error occurs when calling the canonical record constructor
     */
    R build();

    /**
     * Creates a new blank for the record class using the previously set record component values.
     */
    RecordBlank<R> blank();

    /**
     * Sets the value for a given record component.
     *
     * @param component the record component
     * @throws NullPointerException if the argument is null
     */
    <T> ComponentContext<R, T> set(Function<R, T> component);

    /**
     * Wrapper overload of {@link #set(Function)}
     */
    <T> ComponentContext<R, T> set(LambdaSupport.WrappedFunction<R, T> component);

    interface ComponentContext<R extends Record, T> {

        /**
         * Supplies the value of a given record component.
         *
         * @param value a supplier that supplies the value whenever a new instance is built
         * @throws NullPointerException   if the argument is null
         * @throws RecordResolveException if the given record component can not be resolved.
         *                                This happens when the given function is not a record component accessor reference
         *                                of the record class of this builder.
         */
        RecordBuilder<R> to(Supplier<T> value);
    }
}
