package org.monospark.remix.internal;

import org.monospark.remix.LambdaSupport;
import org.monospark.remix.RecordBinder;
import org.monospark.remix.Wrapped;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class RecordBinderImpl<R extends Record, T1> implements RecordBinder<R,T1> {

    protected Function<R, T1> f1;

    public RecordBinderImpl(Function<R, T1> func) {
        this.f1 = func;
    }

    public RecordBinderImpl(LambdaSupport.WrappedFunction<R, T1> wrappedFunc) {
        f1 = (r) -> wrappedFunc.apply(r).get();
    }

    public Consumer<R> to(Consumer<T1> consumer) {
        return (r) -> consumer.accept((T1) f1.apply(r));
    }

    @Override
    public <F> Function<R, F> toFunction(Function<T1, F> function) {
        return r -> function.apply(f1.apply(r));
    }

    @Override
    public <T2> RecordBinderTwo<R, T1, T2> and(Function<R, T2> component) {
        return new RecordBinderTwoImpl<>(f1, component);
    }

    @Override
    public <T2> RecordBinderTwo<R, T1, T2> and(LambdaSupport.WrappedFunction<R, T2> component) {
        return new RecordBinderTwoImpl<>(f1, (r) -> component.apply(r).get());
    }

    public static class RecordBinderTwoImpl<R extends Record, T1, T2> implements RecordBinderTwo<R, T1, T2> {

        protected Function<R, T1> f1;
        protected Function<R, T2> f2;

        RecordBinderTwoImpl(Function<R, T1> f1, Function<R, T2> f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        @Override
        public Consumer<R> to(BiConsumer<T1, T2> consumer) {
            return (r) -> consumer.accept((T1) f1.apply(r), f2.apply(r));
        }

        @Override
        public <F> Function<R, F> toFunction(BiFunction<T1, T2, F> function) {
            return r -> function.apply(f1.apply(r), f2.apply(r));
        }

        @Override
        public <T3> RecordBinderThree<R, T1, T2, T3> and(Function<R, T3> component) {
            return new RecordBinderThreeImpl<>(f1, f2, component);
        }

        @Override
        public <T3> RecordBinderThree<R, T1, T2, T3> and(LambdaSupport.WrappedFunction<R, T3> component) {
            return new RecordBinderThreeImpl<>(f1, f2, r -> component.apply(r).get());
        }
    }

    public static class RecordBinderThreeImpl<R extends Record, T1, T2, T3> implements RecordBinderThree<R, T1, T2, T3> {

        protected Function<R, T1> f1;
        protected Function<R, T2> f2;
        protected Function<R, T3> f3;

        RecordBinderThreeImpl(Function<R, T1> f1, Function<R, T2> f2, Function<R, T3> f3) {
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
        }

        @Override
        public Consumer<R> to(TriConsumer<T1, T2, T3> consumer) {
            return (r) -> consumer.accept((T1) f1.apply(r), f2.apply(r), f3.apply(r));
        }

        @Override
        public <F> Function<R, F> toFunction(TriFunction<T1, T2, T3, F> function) {
            return r -> function.apply(f1.apply(r), f2.apply(r), f3.apply(r));
        }

        @Override
        public <T4> RecordBinderFour<R, T1, T2, T3, T4> and(Function<R, T4> component) {
            return new RecordBinderFourImpl<>(f1, f2, f3, component);
        }

        @Override
        public <T4> RecordBinderFour<R, T1, T2, T3, T4> and(LambdaSupport.WrappedFunction<R, T4> component) {
            return new RecordBinderFourImpl<>(f1, f2, f3, r -> component.apply(r).get());
        }
    }

    public static class RecordBinderFourImpl<R extends Record, T1, T2, T3, T4> implements RecordBinderFour<R, T1, T2, T3, T4> {

        protected Function<R, T1> f1;
        protected Function<R, T2> f2;
        protected Function<R, T3> f3;
        protected Function<R, T4> f4;

        RecordBinderFourImpl(Function<R, T1> f1, Function<R, T2> f2, Function<R, T3> f3, Function<R, T4> f4) {
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
            this.f4 = f4;
        }

        @Override
        public Consumer<R> to(QuadConsumer<T1, T2, T3, T4> consumer) {
            return (r) -> consumer.accept((T1) f1.apply(r), f2.apply(r), f3.apply(r), f4.apply(r));
        }

        @Override
        public <F> Function<R, F> toFunction(QuadFunction<T1, T2, T3, T4, F> function) {
            return r -> function.apply(f1.apply(r), f2.apply(r), f3.apply(r), f4.apply(r));
        }
    }
}