package org.monospark.remix;

import org.monospark.remix.internal.*;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.function.*;

public final class Records {

    private Records() {
    }


    private static Object createInternal(Constructor<?> constructor, RecordCacheData<?> data, Object... args) {
        var parameters = data.getParameters();
        if (args.length != parameters.size()) {
            throw new IllegalArgumentException("Invalid amount of arguments. Required for record "
                    + constructor.getDeclaringClass().getName() + ": " + constructor.getParameters().length
                    + ", given: " + args.length);
        }

        Object[] newArgs = new Object[parameters.size()];
        for (int i = 0; i < data.getParameters().size(); i++) {
            var p = parameters.get(i);
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

            Object arg = args[i];
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


    public static <R extends Record, T extends R> RecordBuilder<T> builder(Class<R> clazz) {
        verifyRecord(clazz);

        return new RecordBuilderImpl<T>((Class<T>) clazz);
    }

    public static <R extends Record> RecordBuilder<R> builder(RecordBlank<R> blank) {
        Objects.requireNonNull(blank, "Blank can not be null");

        return blank.builder();
    }

    /**
     * Creates a new record instance using the given arguments.
     *
     * This method validates all arguments, automatically wraps arguments if needed
     * and performs operations specified by {@link RecordRemix#assign(Consumer)}.
     *
     * @param clazz the record class
     * @param args the constructor arguments
     * @return a new instance of the record class
     * @throws NullPointerException if {@code clazz} or {@code args} is null
     * @throws IllegalArgumentException if {@code clazz} is not a record class
     * @throws RemixException if an instance could not be constructed for any reason
     */
    public static <T extends R, R extends Record> T create(Class<R> clazz, Object... args) {
        verifyRecord(clazz);
        verifyArgs(args);

        var r = RecordCache.getOrAdd(clazz);
        return (T) createInternal(r.getConstructor(), r, args);
    }

    /**
     * Creates a new record instance using the given arguments.
     *
     * This method does not perform validation and does not perform operations
     * specified by {@link RecordRemix#assign(Consumer)}.
     * However, it automatically wraps arguments if needed.
     *
     * @param clazz the record class
     * @param args the constructor arguments
     * @return a new instance of the record class
     * @throws NullPointerException if {@code clazz} or {@code args} is null
     * @throws IllegalArgumentException if {@code clazz} is not a record class
     * @throws RemixException if an instance could not be constructed for any reason
     */
    public static <T extends R, R extends Record> T createRaw(Class<T> clazz, Object... args) {
        verifyRecord(clazz);
        verifyArgs(args);

        var r = RecordCache.getOrAdd(clazz);
        var parameters = r.getParameters();
        Object[] newArgs = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            newArgs[i] = parameters.get(i).wrap(args[i]);
        }

        try {
            return RecordCache.getOrAdd(clazz).getConstructor().newInstance(newArgs);
        } catch (Exception e) {
            throw new RemixException("Could not create new record instance of "
                    + clazz.getName(), e);
        }
    }

    public static <T extends R, R extends Record> T fromArray(Class<R> clazz, Object[] values) {
        verifyRecord(clazz);
        verifyArgs(values);

        var r = RecordCache.getOrAdd(clazz);
        int i = 0;
        Object[] newArgs = new Object[values.length];
        for (var p : r.getParameters()) {
            newArgs[i] = values[i];
            i++;
        }
        return create(clazz, newArgs);
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


    public static <T extends R, R extends Record> void remix(Class<R> src, RecordRemixer<T> rm) {
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

    public static <T extends D, D extends Record, S extends Record> T structuralCopy(Class<D> destClass, S src) {
        Objects.requireNonNull(src, "Record instance must be not null");
        verifyRecord(src.getClass());
        verifyRecord(destClass);

        return fromArray(destClass, copyValues(src));
    }


    public static boolean get(BooleanSupplier component) {
        Objects.requireNonNull(component, "Record component must be not null");
        return component.getAsBoolean();
    }

    public static int get(IntSupplier component) {
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

    public static <R extends Record,T1> RecordBinder<R, T1> bind(Function<R, T1> t1) {
        Objects.requireNonNull(t1, "Record component must be not null");

       return new RecordBinderImpl<R,T1>(t1);
    }

    public static <R extends Record,T1> RecordBinder<R, T1> bind(LambdaSupport.WrappedFunction<R, T1> t1) {
        Objects.requireNonNull(t1, "Record component must be not null");

        return new RecordBinderImpl<R,T1>(t1);
    }
}
