package org.monospark.remix.internal;

import org.monospark.remix.*;

import java.util.*;
import java.util.function.*;

public final class RecordBuilderImpl<R extends Record> implements RecordBuilder<R> {

    private Class<R> recordClass;
    private RecordCacheData<R> cacheData;
    private Map<RecordParameter, Supplier<?>> mapping;

    RecordBuilderImpl(Class<R> recordClass, Map<RecordParameter, Supplier<?>> mapping) {
        RecordCacheData<R> d = RecordCache.getOrAdd(recordClass);
        this.cacheData = d;
        this.recordClass = recordClass;
        this.mapping = Map.copyOf(mapping);
    }

    public RecordBuilderImpl(Class<R> recordClass) {
        RecordCacheData<R> d = RecordCache.getOrAdd(recordClass);
        this.cacheData = d;
        this.recordClass = recordClass;
        this.mapping = new HashMap<>();
    }

    public R build() {
        Object[] args = new Object[cacheData.getParameters().size()];
        int i = 0;
        for (RecordParameter p : cacheData.getParameters()) {
            if (!mapping.containsKey(p)) {
                throw new RecordBlankException("Missing value for record component " + p.getComponent().getName());
            }
            args[i] = mapping.get(p).get();
            i++;
        }
        return Records.create(recordClass, args);
    }

    @Override
    public RecordBlank<R> blank() {
        return new RecordBlankImpl<>(recordClass, mapping);
    }

    @Override
    public <T> RecordBuilder<R> set(Function<R, T> component, Supplier<T> value) {
        RecordParameter param = cacheData.getResolverCache().resolve(component, value);
        mapping.put(param, value);
        return this;
    }

    @Override
    public RecordBuilder<R> set(Function<R, Boolean> component, BooleanSupplier value) {
        RecordParameter param = cacheData.getResolverCache().resolveBoolean(component);
        mapping.put(param, (Supplier<?>) value);
        return this;
    }

    @Override
    public <T> RecordBuilder<R> set(RecordOperations.WrappedPrimitiveFunction<R, T> component, WrappedSupplier<T, Wrapped<T>> value) {
        RecordParameter param = ((Wrapper) component.get(cacheData.getRecordInstance())).getRecordParameter();
        mapping.put(param, value::supply);
        return this;
    }


    @Override
    public <T> RecordBuilder<R> set(WrappedFunction<R, T> component, WrappedSupplier<T, Wrapped<T>> value) {
        RecordParameter param = ((Wrapper) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        mapping.put(param, value::supply);
        return this;
    }

    @Override
    public RecordBuilder<R> set(WrappedBooleanFunction<R> component, BooleanSupplier value) {
        RecordParameter param = ((WrappedBooleanImpl) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        mapping.put(param, value::getAsBoolean);
        return this;
    }
}
