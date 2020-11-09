package org.monospark.remix;

import org.monospark.remix.internal.RecordCache;
import org.monospark.remix.internal.RecordParameter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RecordBuilder<R extends Record> {

    private R object;
    private Map<RecordParameter, Object> mapping = new HashMap<>();

    RecordBuilder(Class<R> recordClass) {
        object = (R) RecordCache.getOrAdd(recordClass).getRecordInstance();
    }

    public R build() {
        return object;
    }

    public <T> RecordBuilder<R> set(Function<R, Wrapped<T>> variable, T value) {
        mapping.put(variable.apply(object).getRecordParameter(), value);
        return this;
    }
}
