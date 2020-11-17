package org.monospark.remix;

import org.monospark.remix.internal.RecordBuilderImpl;

import java.util.Collection;
import java.util.function.*;

public sealed interface RecordBuilder<R extends Record> permits RecordBuilderImpl {

    /**
     * Creates a new instance of the record class using the previously set record component values.
     *
     * @throws RecordBuilderException if any parameter does not have a set value or
     * any error occurs when calling the canonical record constructor
     */
    R build();

    /**
     * Creates a new blank for the record class using the previously set record component values.
     */
    RecordBlank<R> blank();

    interface ComponentContext<R extends Record,T> {

        /**
         * Supplies the value of a given record component.
         *
         * @param value a supplier that supplies the value whenever a new instance is built
         * @throws NullPointerException if the argument is null
         */
        RecordBuilder<R> to(Supplier<T> value);
    }

    /**
     * Sets the value for a given record component.
     *
     * @param component the record component
     * @throws NullPointerException if the argument is null
     * @throws RecordResolveException if the specified record component can not be resolved.
     * This happens when the given function is not a record component accessor reference
     * of the record class of this builder.
     */
    <T> ComponentContext<R,T> set(Function<R, T> component);

    /**
     * Wrapper overload of {@link #set(Function)}
     */
    <T> ComponentContext<R,T> set(LambdaSupport.WrappedFunction<R,T> component);
}
