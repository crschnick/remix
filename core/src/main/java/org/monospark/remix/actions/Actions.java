package org.monospark.remix.actions;

import org.monospark.remix.Mutable;
import org.monospark.remix.Records;
import org.monospark.remix.Wrapped;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Actions {

    private static final Map<Class<? extends Action>, Action> CACHE = new HashMap<>();

    public static Action getActionInstance(Class<? extends Action> f) {
        Action cached = CACHE.get(f);
        if (cached == null) {
            try {
                cached = (Action) f.getDeclaredConstructors()[0].newInstance();
                CACHE.put(f, cached);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return cached;
    }

    public static <T> T executeActions(Object o, List<Action> actions) {
        Object current = o;
        for (Action a : actions) {
            current = a.apply(o);
        }
        return (T) current;
    }

    /**
     * Verifies that the argument is not null.
     *
     * Throws a {@code NullPointerException} if the argument is null.
     **/
    public static final class NotNull implements AssignAction {

        @Override
        public Object apply(Object obj) {
            Objects.requireNonNull(obj);
            return obj;
        }
    }

    public static final class Unmodifiable implements AssignAction {

        @Override
        public Object apply(Object obj) {
            if (obj instanceof NavigableSet<?> nset) {
                return Collections.unmodifiableNavigableSet(nset);
            }
            else if (obj instanceof SortedSet<?> set) {
                return Collections.unmodifiableSortedSet(set);
            }
            else if (obj instanceof Set<?> set) {
                return Collections.unmodifiableSet(set);
            }
            else if (obj instanceof SortedMap<?,?> map) {
                return Collections.unmodifiableSortedMap(map);
            }
            else if (obj instanceof Map<?,?> map) {
                return Collections.unmodifiableMap(map);
            }
            else if (obj instanceof List<?> list) {
                return Collections.unmodifiableList(list);
            }
            else if (obj instanceof Collection<?> c) {
                return Collections.unmodifiableCollection(c);
            }
            else {
                throw new IllegalArgumentException("");
            }
        }
    }

    public static final class Copy implements AssignAction {

        @Override
        public Object apply(Object obj) {
            try {
                return obj.getClass().getConstructor(obj.getClass()).newInstance(obj);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException(obj.getClass().getName() + " can not be copied");
            }
        }
    }

    public static final class Sort implements AssignAction {

        @Override
        public Object apply(Object obj) {
            Collections.sort((List<? extends Comparable>) obj);
            return obj;
        }
    }
}
