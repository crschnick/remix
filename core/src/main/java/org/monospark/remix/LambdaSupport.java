package org.monospark.remix;

import java.util.function.Function;
import java.util.function.Supplier;

public class LambdaSupport {

    @FunctionalInterface
    public interface WrappedBooleanFunction<T> extends Function<T,WrappedBoolean> {}

    @FunctionalInterface
    public interface WrappedIntFunction<T> extends Function<T,WrappedInt> {}

    @FunctionalInterface
    public interface WrappedFunction<T,W> extends Function<T, Wrapped<W>> {
    }



    @FunctionalInterface
    public interface MutableFunction<T,W> extends Function<T,Wrapped<W>> {}
    


    @FunctionalInterface
    public interface WrappedBooleanSupplier extends Supplier<WrappedBoolean> { }

    @FunctionalInterface
    public interface WrappedIntSupplier extends Supplier<WrappedInt> { }

    @FunctionalInterface
    public interface WrappedSupplier<T> extends Supplier<Wrapped<T>> {}



    @FunctionalInterface
    public interface MutableBooleanSupplier extends Supplier<MutableBoolean> { }

    @FunctionalInterface
    public interface MutableIntSupplier extends Supplier<MutableInt> { }

    @FunctionalInterface
    public interface MutableSupplier<T> extends Supplier<Mutable<T>> {}
}
