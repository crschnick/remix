package org.monospark.remix.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class BooleanResolver {

    static <R extends Record> RecordParameter resolveParameter(List<RecordParameter> parameters,
                                                           List<R> instances,
                                                           Function<R,Boolean> methodRef) {
        var c = parameters.get(0).getComponent().getDeclaringRecord();
        for (int i = 0; i < parameters.size(); i++) {
            var type = parameters.get(i).getComponent().getType();
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                if (matches(parameters.get(i), instances, methodRef)) {
                    return parameters.get(i);
                }
            }
        }
        throw new IllegalArgumentException("Could not resolve boolean record component, therefore" +
                "the method reference does not belong to the associated record class.");
    }

    private static <R extends Record> boolean matches(RecordParameter parameter, List<R> instances, Function<R,Boolean> methodRef) {
        for (R r : instances) {
            try {
                var query = methodRef.apply(r);
                var shouldBe = parameter.getComponent().getAccessor().invoke(r);
                if (!query.equals(shouldBe)) {
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
        long booleans = parameters.stream()
                .filter(p -> p.getComponent().getType().equals(boolean.class)
                        || p.getComponent().getType().equals(Boolean.class))
                .count();
        int neededInstances = (int) booleans;
        List<R> list = new ArrayList<>(neededInstances);
        for (int i = 0; i < neededInstances; i++) {
            list.add(createValue(parameters, i));
        }
        return list;
    }

    private static <R extends Record> R createValue(List<RecordParameter> parameters, int value) {
        Object[] values = new Object[parameters.size()];
        int booleans = 0;
        var c = parameters.get(0).getComponent().getDeclaringRecord();
        for (int i = 0; i < parameters.size(); i++) {
            Class<?> type = parameters.get(i).getComponent().getType();
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                values[i] = booleans == value;
                booleans++;
            } else {
                values[i] = DefaultValueHelper.createDefaultValue(parameters.get(i).getComponent().getType());
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
