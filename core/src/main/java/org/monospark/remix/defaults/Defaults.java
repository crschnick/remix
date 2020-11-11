package org.monospark.remix.defaults;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Defaults {

    private static Map<Class<? extends Factory>, Factory> FACTORY_CACHE = new HashMap<>();

    public static Factory getFactory(Class<? extends Factory> f) {
        Factory cached = FACTORY_CACHE.get(f);
        if (cached == null) {
            try {
                cached = (Factory) f.getDeclaredConstructors()[0].newInstance();
                FACTORY_CACHE.put(f, cached);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return cached;
    }

    @FunctionalInterface
    public static
    interface Factory {
        Object create(Class<?> clazz);
    }

    public static final class Now implements Factory {

        @Override
        public Object create(Class<?> clazz) {
            return null;
        }
    }

    public static final class Null implements Factory {

        @Override
        public Object create(Class<?> clazz) {
            return null;
        }
    }

}
