package org.monospark.remix.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class RecordCacheData {

    private Object recordInstance;
    private Constructor<?> constructor;
    private List<RecordParameter> parameters;

    private RecordCacheData(Constructor<?> constructor, List<RecordParameter> parameters) {
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
            Object afterAction = ActionImpl.executeActions(args[i], parameters.get(i).getActions());
            newArgs[i] = parameters.get(i).getType().wrap(parameters.get(i), afterAction);
        }

        for (int i = args.length; i < parameters.size(); i++) {
            var d = parameters.get(i).getDefaultValueType();
            if (d.isEmpty()) {
                throw new IllegalArgumentException("Too many arguments");
            }
            newArgs[i] = parameters.get(i).getType().createDefaultValue(parameters.get(i),
                    parameters.get(i).getDefaultValueType().orElse(parameters.get(i).getType().getImplicitDefaultType()));
        }

        try {
            return constructor.newInstance(newArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static boolean hasComponentInfo(Class<? extends Record> recordClass) {
        return Arrays.stream(recordClass.getRecordComponents())
                .anyMatch(c -> RecordParameter.getDefaultValueType(c).isPresent() || RecordParameter.getActions(c).size() > 0);
    }

    static RecordCacheData fromRecordClass(Class<? extends Record> recordClass) {
        boolean hasComponentInfo = hasComponentInfo(recordClass);
        if (recordClass.getDeclaredConstructors().length > 1 && hasComponentInfo) {
            throw new IllegalArgumentException();
        }

        if (recordClass.getDeclaredConstructors().length == 1 && !hasComponentInfo) {
            throw new IllegalArgumentException();
        }

        return new RecordCacheData(
                recordClass.getDeclaredConstructors()[0],
                RecordParameter.fromRecordComponents(recordClass));
    }

    static Object defaultRecordInstance(Constructor<?> constructor, List<RecordParameter> parameters) {
        Object[] args = new Object[parameters.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = parameters.get(i).getType().createDefaultValue(parameters.get(i),
                    parameters.get(i).getDefaultValueType().orElse(parameters.get(i).getType().getImplicitDefaultType()));
        }
        Object obj = null;
        try {
            obj = constructor.newInstance(args);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public Object create(Object... args) {
        Object o = create(constructor, parameters, args);
        if (o != null) {
            return o;
        }

        throw new RuntimeException();
    }

    public Object getRecordInstance() {
        return recordInstance;
    }
}
