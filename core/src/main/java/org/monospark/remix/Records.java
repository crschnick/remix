package org.monospark.remix;

import org.monospark.remix.internal.*;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class Records {

    private Records() {
    }


    private static Object createInternal(Constructor<?> constructor, RecordCacheData<?> data, Object... args) {
        var parameters = data.getParameters();
        if (args.length > parameters.size()) {
            throw new IllegalArgumentException("Too many arguments. Required for record "
                    + constructor.getDeclaringClass().getName() + ": " + constructor.getParameters().length
                    + ", given: " + args.length);
        }

        Object[] newArgs = new Object[parameters.size()];
        for (int i = 0; i < data.getParameters().size(); i++) {
            var p = parameters.get(i);
            Object arg;
            if (i > args.length) {
                if (data.getRemix().getBlank().getValue(p) == null) {
                    throw new IllegalArgumentException("Missing value for record component "
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
                            parameters.get(i).getType().getValueType().getName() + " and "
                            + (args[i] == null ? "null" : args[i].getClass().getName()));
                }
                arg = args[i];
            }
            var op = data.getRemix().getAssignOperations().getOperator(p);
            Object afterAction = op != null ? op.apply(arg) : arg;
            newArgs[i] = p.wrap(afterAction);
        }

        try {
            return constructor.newInstance(newArgs);
        } catch (Exception e) {
            throw new RemixException("Could not create new record instance of "
                    + constructor.getDeclaringClass().getName(), e);
        }
    }

    private static void verifyRecord(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Record class must be not null");
        if (!clazz.isRecord()) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not a record");
        }
    }

    private static void verifyArgs(Object[] args) {
        Objects.requireNonNull(args, "Argument array must be not null");
    }


    public static <R extends Record> RecordBuilder<R> builder(Class<R> clazz) {
        verifyRecord(clazz);

        return new RecordBuilderImpl<>(clazz);
    }

    public static <R extends Record> RecordBuilder<R> builder(RecordBlank<R> blank) {
        return blank.builder();
    }

    public static <R extends Record> R create(Class<R> clazz, Object... args) {
        verifyRecord(clazz);
        verifyArgs(args);

        var r = RecordCache.getOrAdd(clazz);
        return (R) createInternal(r.getConstructor(), r, args);
    }


    public static <R extends Record> R createRaw(Class<R> clazz, Object... args) {
        verifyRecord(clazz);
        verifyArgs(args);

        try {
            return RecordCache.getOrAdd(clazz).getConstructor().newInstance(args);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <R extends Record> R fromArray(Class<R> clazz, Object[] values) {
        verifyRecord(clazz);
        verifyArgs(values);

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
        Objects.requireNonNull(src, "Source record must be not null");
        verifyRecord(src.getClass());

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
        verifyRecord(src);
        Objects.requireNonNull(rm, "Remixer must be not null");

        RecordCache.register(src, rm);
    }

    public static <R extends Record> SerializedRecord serialized(R obj) {
        Objects.requireNonNull(obj, "Record instance must be not null");
        Class<?> clazz = obj.getClass();
        verifyRecord(clazz);

        return new SerializedRecord(clazz.getName(), Records.toArray(obj));
    }

    private static <R extends Record> Object[] copyValues(R src) {
        Objects.requireNonNull(src, "Record instance must be not null");
        verifyRecord(src.getClass());

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
        Objects.requireNonNull(src, "Record instance must be not null");
        verifyRecord(src.getClass());

        return fromArray((Class<R>) src.getClass(), copyValues(src));
    }

    public static <D extends Record, S extends Record> D structuralCopy(Class<D> destClass, S src) {
        Objects.requireNonNull(src, "Record instance must be not null");
        verifyRecord(src.getClass());
        verifyRecord(destClass);

        return fromArray(destClass, copyValues(src));
    }



    public static <T> boolean get(BooleanSupplier component) {
        Objects.requireNonNull(component, "Record component must be not null");
        return component.getAsBoolean();
    }
    public static <T> int get(IntSupplier component) {
        Objects.requireNonNull(component, "Record component must be not null");
        return component.getAsInt();
    }
    public static <T> T get(Supplier<T> component) {
        Objects.requireNonNull(component, "Record component must be not null");
        return component.get();
    }



    public static boolean get(LambdaSupport.WrappedBooleanSupplier component) {
        Objects.requireNonNull(component, "Record component must be not null");
        return component.get().get();
    }
    public static int get(LambdaSupport.WrappedIntSupplier component) {
        Objects.requireNonNull(component, "Record component must be not null");
        return component.get().get();
    }
    public static <T> T get(LambdaSupport.WrappedSupplier<T> component) {
        Objects.requireNonNull(component, "Record component must be not null");
        return component.get().get();
    }



    public static void set(LambdaSupport.MutableBooleanSupplier component, boolean value) {
        Objects.requireNonNull(component, "Record component must be not null");
        component.get().set(value);
    }
    public static void set(LambdaSupport.MutableIntSupplier component, int value) {
        Objects.requireNonNull(component, "Record component must be not null");
        component.get().set(value);
    }
    public static <T> void set(LambdaSupport.MutableSupplier<T> component, T value) {
        Objects.requireNonNull(component, "Record component must be not null");
        component.get().set(value);
    }
}
