package org.monospark.remix.internal;

import org.monospark.remix.RecordRemix;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class RemixCache {

    private static final Map<Class<? extends RecordRemix<? extends Record>>,RecordRemix<? extends Record>> CACHE = new HashMap<>();

    public static <R extends Record, T extends RecordRemix<R>> RecordRemix<R> getRecordRemix(Class<T> r) {
        if (!CACHE.containsKey(r)) {
            try {
                CACHE.put(r, (RecordRemix<? extends Record>) r.getDeclaredConstructors()[0].newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return (RecordRemix<R>) CACHE.get(r);
    }
}
