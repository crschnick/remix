package org.monospark.remix.internal;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class DefaultValueHelper {

    private static final Map<Class<?>, Object> DEFAULT_VALUES = Stream
            .of(boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class)
            .collect(toMap(clazz -> Array.get(Array.newInstance(clazz, 1), 0).getClass(),
                    clazz -> Array.get(Array.newInstance(clazz, 1), 0)));

    static Object createDefaultValue(Class<?> clazz) {
        return Array.get(Array.newInstance(clazz, 1), 0);
    }

}
