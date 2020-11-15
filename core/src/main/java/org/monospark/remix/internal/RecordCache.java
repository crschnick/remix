package org.monospark.remix.internal;

import org.monospark.remix.RecordRemixer;

import java.util.HashMap;
import java.util.Map;

public class RecordCache {

    private static final Map<Class<?>, RecordCacheData<?>> DATA = new HashMap<>();


    public static <T extends Record> RecordCacheData<T> getOrAdd(Class<T> recordClass) {
        return getOrAdd(recordClass, null);
    }

    public static <T extends Record> RecordCacheData<T> getOrAdd(Class<T> recordClass, RecordRemixer<T> rm) {
        if (DATA.containsKey(recordClass)) {
            return (RecordCacheData<T>) DATA.get(recordClass);
        } else {
            RecordCacheData<T> r = RecordCacheData.fromRecordClass(recordClass, rm);
            DATA.put(recordClass, r);
            return r;
        }
    }
}
