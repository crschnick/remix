package org.monospark.remix.internal;

import org.monospark.remix.RecordRemixer;

import java.util.HashMap;
import java.util.Map;

public class RecordCache {

    private static final Map<Class<?>, RecordCacheData<?>> DATA = new HashMap<>();


    public static <R extends Record, T extends R> RecordCacheData<T> getOrAdd(Class<R> recordClass) {
        if (DATA.containsKey(recordClass)) {
            return (RecordCacheData<T>) DATA.get(recordClass);
        } else {
            RecordCacheData<T> r = RecordCacheData.fromRecordClass(recordClass, null);
            DATA.put(recordClass, r);
            return r;
        }
    }

    public static <T extends R, R extends Record> void register(Class<R> recordClass, RecordRemixer<T> rm) {
        RecordCacheData<T> r = RecordCacheData.fromRecordClass(recordClass, rm);
        DATA.put(recordClass, r);
    }
}
