package org.monospark.remix.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BooleanResolver {

    static <R extends Record> RecordParameter resolveParameter(List<RecordParameter> parameters,
                                                           List<R> instances,
                                                           Function<R,Boolean> methodRef) {
        var c = parameters.get(0).getComponent().getDeclaringRecord();
        for (int i = 0; i < parameters.size(); i++) {
            if (c.equals(boolean.class)) {
                if (matches(parameters.get(i), instances, methodRef)) {
                    return parameters.get(i);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static <R extends Record> boolean matches(RecordParameter parameter, List<R> instances, Function<R,Boolean> methodRef) {
        for (R r : instances) {
            try {
                if (methodRef.apply(r) != parameter.getComponent().getAccessor().invoke(r)) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    static <R extends Record> List<R> createInstances(List<RecordParameter> parameters) {
        long booleans = parameters.stream().filter(p -> p.getComponent().getType().equals(boolean.class)).count();
        int neededInstances = (int) Math.ceil(Math.log(booleans) / Math.log(2));
        List<R> list = new ArrayList<>(neededInstances);
        for (int i = 0; i < neededInstances; i++) {
            list.set(i, createValue(parameters, i));
        }
        return list;
    }

    private static <R extends Record> R createValue(List<RecordParameter> parameters, int value) {
        Object[] values = new Object[parameters.size()];
        int booleans = 0;
        var c = parameters.get(0).getComponent().getDeclaringRecord();
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i).getComponent().getDeclaringRecord().equals(boolean.class)) {
                values[i] = (value & (1 << booleans)) == 1;
                booleans++;
            } else {
                values[i] = DefaultValueHelper.createDefaultValue(c);
            }
        }
        try {
            return (R) c.getDeclaredConstructors()[0].newInstance(values);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
