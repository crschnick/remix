package org.monospark.remix;

import org.monospark.remix.internal.DefaultValueHelper;
import org.monospark.remix.internal.RecordCache;
import org.monospark.remix.internal.RecordBuilderImpl;
import org.monospark.remix.internal.RecordParameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Records {

    private Records() {}


    private static Object createInternal(Constructor<?> constructor, List<RecordParameter> parameters, Object... args) {
        if (args.length > parameters.size()) {
            throw new IllegalArgumentException("Too many arguments");
        }

        Object[] newArgs = new Object[parameters.size()];
        for (int i = 0; i < args.length; i++) {
            var p = parameters.get(i);
            boolean nullCompat = args[i] == null && !p.getType().getValueType().isPrimitive();
            boolean classCompat = args[i] != null && p.getType().getValueType().isAssignableFrom(args[i].getClass());
            boolean boxedCompat = args[i] != null && p.getType().getValueType().isPrimitive()
                    && DefaultValueHelper.getBoxedClass(p.getType().getValueType()).equals(args[i].getClass());
            boolean compatible = nullCompat || classCompat ||boxedCompat;
            if (!compatible) {
                throw new IllegalArgumentException("Incompatible types " +
                        parameters.get(i).getType().getValueType().getName() + " and " + (args[i] == null ? "null" : args[i].getClass().getName()));
            }
            Object afterAction = parameters.get(i).getConstructActions().apply(args[i]);
            newArgs[i] = p.getType().wrap(parameters.get(i), afterAction);
        }

        for (int i = args.length; i < parameters.size(); i++) {
            var d = parameters.get(i).getDefaultValue();
            if (d == null) {
                throw new IllegalArgumentException("Missing default value for parameter "
                        + parameters.get(i).getComponent().getName());
            }
            newArgs[i] = parameters.get(i).getType().wrap(
                    parameters.get(i),
                    parameters.get(i).getType().defaultValue(parameters.get(i), parameters.get(i).getDefaultValue()));
        }

        try {
            return constructor.newInstance(newArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <R extends Record> RecordBuilder<R> builder(Class<R> clazz) {
        return new RecordBuilderImpl<>(clazz);
    }

    public static <R extends Record> RecordBuilder<R> builder(RecordBlank<R> blank) {
        return blank.builder();
    }

    public static <R extends Record> RecordBuilder<R> builderWith(R object) {
        return null;
    }


    public static <R extends Record> R create(Class<R> clazz, Object... args) {
        var r = RecordCache.getOrAdd(clazz);
        return (R) createInternal(r.getConstructor(), r.getParameters(), args);
    }


    public static <R extends Record> R createRaw(Class<R> clazz, Object... args) {
        try {
            return (R) clazz.getDeclaredConstructors()[0].newInstance(args);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <R extends Record> R copy(R src) {
        return null;
    }

    public static <D extends Record, S extends Record> D structuralCopy(Class<D> destClass, S src) {
        return null;
    }

    public static <T> T get(Wrapped<T> entry) {
        return entry.get();
    }

    public static <T> T get(Supplier<Wrapped<List<T>>> entry, int index) {
        return entry.get().get().get(index);
    }

    public static <T> T get(Supplier<Wrapped<T>> entry) {
        return entry.get().get();
    }

    public static <R extends Record, T> T get(Function<R, Wrapped<T>> entry, R instance) {
        return entry.apply(instance).get();
    }

    public static <T> void set(Mutable<T> entry, T value) {
        entry.set(value);
    }

    public static <T> void set(Supplier<Mutable<T>> entry, T value) {
        entry.get().set(value);
    }

    public static void set(MutableBoolean entry, boolean value) {
        entry.set(value);
    }

    public static void set(Supplier<MutableBoolean> entry, boolean value) {
        entry.get().set(value);
    }
}
