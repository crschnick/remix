package org.monospark.remix.defaults;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A class containing a collection of group factory implementations to be used with
 * {@link @Default} annotations.
 **/
public final class Groups {

    private Groups() {}

    @FunctionalInterface
    public interface Factory {
        Object group(Object array);
    }

    private static Map<Class<? extends Factory>, Factory> FACTORY_CACHE = new HashMap<>();

    /**
     * Gets a group factory from the cache or creates one.
     **/
    public static Groups.Factory getFactory(Class<?> f) {
        if (!Factory.class.isAssignableFrom(f)) {
            return null;
        }

        Factory cached = FACTORY_CACHE.get(f);
        if (cached == null) {
            try {
                cached = (Factory) f.getDeclaredConstructors()[0].newInstance();
                FACTORY_CACHE.put((Class<? extends Factory>) f, cached);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return cached;
    }

    public static final class Array implements Factory {

        @Override
        public Object group(Object args) {
            return args;
        }
    }
}
