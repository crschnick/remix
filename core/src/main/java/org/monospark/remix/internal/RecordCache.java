package org.monospark.remix.internal;

import java.util.HashMap;
import java.util.Map;

public class RecordCache {

    private static final Map<Class<?>, RecordCacheData> DATA = new HashMap<>();

    public static <T extends Record> RecordCacheData getOrAdd(Class<T> recordClass) {
        RecordCacheData r = DATA.get(recordClass);
        if (r != null) {
            return r;
        } else {
            r = RecordCacheData.fromRecordClass(recordClass);
            DATA.put(recordClass, r);
            return r;
        }
    }
}
