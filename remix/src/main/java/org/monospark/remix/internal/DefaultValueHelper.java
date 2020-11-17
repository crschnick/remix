package org.monospark.remix.internal;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class DefaultValueHelper {

    private static final Map<Class<?>, Class<?>> DEFAULT_VALUES = Stream
            .of(boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class)
            .collect(toMap(clazz -> clazz,
                    clazz -> Array.get(Array.newInstance(clazz, 1), 0).getClass()));

    public static Class<?> getBoxedClass(Class<?> primitive) {
        return DEFAULT_VALUES.get(primitive);
    }

    public static boolean isBoxedClass(Class<?> c) {
        return DEFAULT_VALUES.containsValue(c);
    }

    public static Object createDefaultValue(Class<?> clazz) {
        return Array.get(Array.newInstance(clazz, 1), 0);
    }

}
