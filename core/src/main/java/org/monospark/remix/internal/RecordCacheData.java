package org.monospark.remix.internal;

import org.monospark.remix.actions.Actions;
import org.monospark.remix.defaults.Default;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class RecordCacheData<T extends Record> {

    private static final List<Class<?>> ANNOTATIONS = List.of(Default.class);

    private static boolean hasRemixAnnotations(Class<? extends Record> recordClass) {
        return Arrays.stream(recordClass.getRecordComponents())
                .anyMatch(c -> Arrays.stream(c.getAnnotations())
                        .anyMatch(a -> ANNOTATIONS.contains(a.annotationType())));
    }

    private static boolean checkOnlyGeneratedConstructor(Class<? extends Record> recordClass) {
        if (recordClass.getDeclaredConstructors().length > 1) {
            throw new IllegalArgumentException();
        }

        return Arrays.stream(recordClass.getDeclaredConstructors()[0].getParameters())
                .noneMatch(p -> Arrays.stream(p.getAnnotations())
                        .anyMatch(a -> ANNOTATIONS.contains(a.annotationType())));
    }

    static <T extends Record> RecordCacheData<T> fromRecordClass(Class<T> recordClass) {
        if (!hasRemixAnnotations(recordClass)) {
            return null;
        }
        checkOnlyGeneratedConstructor(recordClass);

        return new RecordCacheData<T>(
                (Constructor<T>) recordClass.getDeclaredConstructors()[0],
                RecordParameter.fromRecordComponents(recordClass));
    }

    private T recordInstance;
    private Constructor<T> constructor;
    private List<RecordParameter> parameters;

    private RecordCacheData(Constructor<T> constructor, List<RecordParameter> parameters) {
        this.recordInstance = defaultRecordInstance(constructor, parameters);
        this.constructor = constructor;
        this.parameters = parameters;
    }

    private static Object create(Constructor<?> constructor, List<RecordParameter> parameters, Object... args) {
        if (args.length > parameters.size()) {
            throw new IllegalArgumentException("Too many arguments");
        }

        Object[] newArgs = new Object[parameters.size()];
        for (int i = 0; i < args.length; i++) {
            boolean compatible = (args[i] == null && !parameters.get(i).getType().getValueType().isPrimitive()) ||
                    (parameters.get(i).getType().getValueType().isAssignableFrom(args[i].getClass()));
            if (!compatible) {
                throw new IllegalArgumentException("Incompatible types " +
                        parameters.get(i).getType().getValueType().getName() + " and " + args[i].getClass().getName());
            }
            Object afterAction = Actions.executeActions(args[i], parameters.get(i).getConstructActions());
            newArgs[i] = parameters.get(i).getType().wrap(parameters.get(i), parameters.get(i).getDefaultValue(), afterAction);
        }

        for (int i = args.length; i < parameters.size(); i++) {
            var d = parameters.get(i).getDefaultValue();
            if (d == null) {
                throw new IllegalArgumentException("Missing default value for parameter "
                        + parameters.get(i).getComponent().getName());
            }
            newArgs[i] = parameters.get(i).getType().wrap(
                    parameters.get(i),
                    parameters.get(i).getDefaultValue(),
                    DefaultValueHelper.createDefaultValue(parameters.get(i).getComponent().getType()));
        }

        try {
            return constructor.newInstance(newArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends Record> T defaultRecordInstance(Constructor<T> constructor, List<RecordParameter> parameters) {
        Object[] args = new Object[parameters.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = parameters.get(i).getType().wrap(
                    parameters.get(i),
                    parameters.get(i).getDefaultValue(),
                    DefaultValueHelper.createDefaultValue(parameters.get(i).getComponent().getType()));
        }
        T obj = null;
        try {
            obj = constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public Object create(Object... args) {
        Object o = create(constructor, parameters, args);
        return o;
    }

    public T getRecordInstance() {
        return recordInstance;
    }
}
