package org.monospark.remix.internal;

import org.monospark.remix.RecordBuilder;
import org.monospark.remix.Wrapped;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WrappedBuilder<R extends Record> implements RecordBuilder<R> {

    private R object;
    private Map<RecordParameter, Object> mapping = new HashMap<>();

    public WrappedBuilder(Class<R> recordClass) {
        RecordCacheData<R> d = RecordCache.getOrAdd(recordClass);
        if (d == null) {
            throw new IllegalArgumentException("Builders are not supported for " + recordClass.getName());
        }
        object = (R) RecordCache.getOrAdd(recordClass).getRecordInstance();
    }

    public R build() {
        return object;
    }

    public <T> WrappedBuilder<R> add(Function<R, Wrapped<? extends Collection<T>>> variable, T... value) {
        if (!mapping.containsKey(RecordParameter.get(variable.apply(object)))) {
            throw new IllegalArgumentException();
        }

        var c = ((Collection<T>) mapping.get(RecordParameter.get(variable.apply(object))));
        Arrays.stream(value).forEach(c::add);
        return this;
    }

    public <T> WrappedBuilder<R> set(Function<R, Wrapped<T>> variable, T value) {
        mapping.put(RecordParameter.get(variable.apply(object)), value);
        return this;
    }
}
