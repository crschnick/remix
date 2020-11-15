package org.monospark.remix;

import org.monospark.remix.internal.*;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Records {

    private Records() {
    }


    private static Object createInternal(Constructor<?> constructor, RecordCacheData<?> data, Object... args) {
        var parameters = data.getParameters();
        if (args.length > parameters.size()) {
            throw new IllegalArgumentException("Too many arguments");
        }

        Object[] newArgs = new Object[parameters.size()];
        for (int i = 0; i < data.getParameters().size(); i++) {
            var p = parameters.get(i);
            Object arg;
            if (i > args.length) {
                if (data.getRemix().getBlank().getValue(p) == null) {
                    throw new IllegalArgumentException("Missing value for component "
                            + parameters.get(i).getComponent().getName());
                }
                arg = data.getRemix().getBlank().getValue(p).get();
            } else {
                boolean nullCompat = args[i] == null && !p.getType().getValueType().isPrimitive();
                boolean classCompat = args[i] != null && p.getType().getValueType().isAssignableFrom(args[i].getClass());
                boolean boxedCompat = args[i] != null && p.getType().getValueType().isPrimitive()
                        && DefaultValueHelper.getBoxedClass(p.getType().getValueType()).equals(args[i].getClass());
                boolean compatible = nullCompat || classCompat || boxedCompat;
                if (!compatible) {
                    throw new IllegalArgumentException("Incompatible types " +
                            parameters.get(i).getType().getValueType().getName() + " and " + (args[i] == null ? "null" : args[i].getClass().getName()));
                }
                arg = args[i];
            }
            try {
                var op = data.getRemix().getAssignOperations().getOperator(p);
                Object afterAction = op != null ? op.apply(arg) : arg;
                newArgs[i] = p.wrap(afterAction);
            } catch (Exception e) {
                int a = 0;
            }
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

    public static <R extends Record> R create(Class<R> clazz, Object... args) {
        var r = RecordCache.getOrAdd(clazz);
        return (R) createInternal(r.getConstructor(), r, args);
    }


    public static <R extends Record> R createRaw(Class<R> clazz, Object... args) {
        try {
            return RecordCache.getOrAdd(clazz).getConstructor().newInstance(args);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <R extends Record> R fromArray(Class<R> clazz, Object[] values) {
        var r = RecordCache.getOrAdd(clazz);
        int i = 0;
        Object[] newArgs = new Object[values.length];
        for (var p : r.getParameters()) {
            newArgs[i] = p.wrap(values[i]);
            i++;
        }
        return createRaw(clazz, newArgs);
    }

    public static <R extends Record> Object[] toArray(R src) {
        var r = RecordCache.getOrAdd(src.getClass());
        Object[] out = new Object[r.getParameters().size()];
        int i = 0;
        for (var p : r.getParameters()) {
            out[i] = p.unwrap(p.getValue(src));
            i++;
        }
        return out;
    }


    public static <R extends Record> void remix(Class<R> src, RecordRemixer<R> rm) {
        RecordCache.getOrAdd(src, rm);
    }

    public static <R extends Record> SerializedRecord serialized(R obj) {
        Class<R> clazz = (Class<R>) obj.getClass();
        if (!clazz.isRecord()) {
            throw new RemixException("Class " + clazz.getName() + " is not a record");
        }
        return new SerializedRecord(clazz.getName(), Records.toArray(obj));
    }

    private static <R extends Record> Object[] copyValues(R src) {
        var r = RecordCache.getOrAdd(src.getClass());
        Object[] args = toArray(src);
        int i = 0;
        for (var p : r.getParameters()) {
            if (r.getRemix().getCopyOperations().getOperator(p) != null) {
                args[i] = r.getRemix().getCopyOperations().getOperator(p).apply(args[i]);
            }
            i++;
        }
        return args;
    }
    public static <R extends Record> R copy(R src) {
        return fromArray((Class<R>) src.getClass(), copyValues(src));
    }

    public static <D extends Record, S extends Record> D structuralCopy(Class<D> destClass, S src) {
        return fromArray(destClass, copyValues(src));
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
