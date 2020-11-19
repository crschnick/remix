package org.monospark.remix;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface RecordBinder<R extends Record,T1>  {

    Consumer<R> to(Consumer<T1> consumer);

    <F> Function<R,F> toFunction(Function<T1, F> function);

    <T2> RecordBinderTwo<R,T1,T2> and(Function<R,T2> component);

    <T2> RecordBinderTwo<R,T1,T2> and(LambdaSupport.WrappedFunction<R,T2> component);

    interface RecordBinderTwo<R extends Record,T1,T2> {

        Consumer<R> to(BiConsumer<T1,T2> consumer);

        <F> Function<R,F> toFunction(BiFunction<T1, T2, F> function);

       <T3> RecordBinderThree<R,T1,T2,T3> and(Function<R,T3> component);

       <T3> RecordBinderThree<R,T1,T2,T3> and(LambdaSupport.WrappedFunction<R,T3> component);
    }

    interface RecordBinderThree<R extends Record,T1,T2,T3> {

        @FunctionalInterface
        interface TriConsumer<T1,T2,T3> {
            void accept(T1 t1, T2 t2, T3 t3);
        }

        @FunctionalInterface
        interface TriFunction<T1,T2,T3,R> {
            R apply(T1 t1, T2 t2, T3 t3);
        }

        Consumer<R> to(TriConsumer<T1,T2,T3> consumer);

        <F> Function<R,F> toFunction(TriFunction<T1, T2, T3, F> function);

        <T4> RecordBinderFour<R,T1,T2,T3,T4> and(Function<R,T4> component);

        <T4> RecordBinderFour<R,T1,T2,T3,T4> and(LambdaSupport.WrappedFunction<R,T4> component);
    }

    interface RecordBinderFour<R extends Record,T1,T2,T3,T4> {

        @FunctionalInterface
        interface QuadConsumer<T1,T2,T3,T4> {
            void accept(T1 t1, T2 t2, T3 t3, T4 t4);
        }

        @FunctionalInterface
        interface QuadFunction<T1,T2,T3,T4,R> {
            R apply(T1 t1, T2 t2, T3 t3, T4 t4);
        }

        Consumer<R> to(QuadConsumer<T1,T2,T3,T4> consumer);

        <F> Function<R,F> toFunction(QuadFunction<T1,T2,T3,T4,F> function);
    }
}
