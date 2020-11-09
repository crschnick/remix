package org.monospark.remix.internal;

import java.util.*;
import java.util.function.Function;

import static org.monospark.remix.Action.NOT_NULL;
import static org.monospark.remix.Action.SIZE_GREATER_ZERO;

public class ActionImpl {

    private static final Map<String, Function<Object, Object>> ACTIONS = new HashMap<>();

    static {
        ACTIONS.put(NOT_NULL, o -> {
            Objects.requireNonNull(o);
            return o;
        });
        ACTIONS.put(SIZE_GREATER_ZERO, o -> {
            if (o instanceof Collection<?> c && c.size() == 0) {
                throw new IllegalArgumentException("Size must be greater than zero");
            }
            return o;
        });
    }

    static <T> T executeActions(Object o, List<String> actions) {
        Object current = o;
        for (String a : actions) {
            current = ACTIONS.get(a).apply(current);
        }
        return (T) current;
    }
}
