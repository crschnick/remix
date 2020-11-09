package org.monospark.remix.internal;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class DefaultImpl {

    public static final String NULL = "null";
    public static final String ZERO = "zero";
    private static final Map<Class<?>, Object> DEFAULT_VALUES = Stream
            .of(boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class)
            .collect(toMap(clazz -> Array.get(Array.newInstance(clazz, 1), 0).getClass(),
                    clazz -> Array.get(Array.newInstance(clazz, 1), 0)));

    public static Object createValue(String valueType) {
        return switch (valueType) {
            case NULL -> null;
            case ZERO -> 0;
            default -> throw new IllegalArgumentException("Invalid value type: " + valueType);
        };
    }

    static Object createDefaultValue(Class<?> clazz) {
        return Array.get(Array.newInstance(clazz, 1), 0);
    }

}
