package org.monospark.remix.internal;

import org.monospark.remix.RecordRemix;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class RecordRemixCache {

    private static final Map<Class<? extends RecordRemix<? extends Record>>, RecordRemix<? extends Record>> CACHE = new HashMap<>();

    public static <R extends Record, T extends RecordRemix<R>> RecordRemix<R> getRecordRemix(Class<T> r) {
        if (!CACHE.containsKey(r)) {
            try {
                var c = r.getConstructors();
                Constructor<T> con = (Constructor<T>) r.getDeclaredConstructors()[0];
                con.setAccessible(true);
                CACHE.put(r, con.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (RecordRemix<R>) CACHE.get(r);
    }
}
