package org.monospark.remix.internal;

import org.monospark.remix.RecordBlank;
import org.monospark.remix.RecordBuilder;

import java.util.Map;
import java.util.function.Supplier;

public final class RecordBlankImpl<R extends Record> extends RecordBlank<R> {

    private Class<R> recordClass;
    private Map<RecordParameter, Supplier<?>> mapping;

    RecordBlankImpl(Class<R> recordClass, Map<RecordParameter, Supplier<?>> mapping) {
        this.recordClass = recordClass;
        this.mapping = Map.copyOf(mapping);
    }

    @Override
    protected RecordBuilder<R> builder() {
        return new RecordBuilderImpl<>(recordClass, mapping);
    }

    @Override
    protected <T> Supplier<T> getValue(RecordParameter p) {
        return (Supplier<T>) mapping.get(p);
    }
}
