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
        setDefaultValues();
    }

    private void setDefaultValues() {
        for (RecordParameter p : cacheData.getParameters()) {
            if (p.getDefaultValue() != null) {
                mapping.put(p, p.getType().wrap(p, p.getType().defaultValue(p, p.getDefaultValue())));
            }
        }
    }

    public R build() {
        Object[] args = new Object[cacheData.getParameters().size()];
        int i = 0;
        for (RecordParameter p : cacheData.getParameters()) {
            if (!mapping.containsKey(p)) {
                throw new RecordBlankException("Missing value for record component " + p.getComponent().getName());
            }
            args[i] = mapping.get(p);
            i++;
        }
        return Records.createRaw(recordClass, args);
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
        mapping.put(param, value);
        return this;
    }

    @Override
    public <T> RecordBuilder<R> set(WrappedFunction<R, T> component, Supplier<T> value) {
        RecordParameter param = ((WrappedImpl<R>) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        mapping.put(param, value);
        return this;
    }

    @Override
    public RecordBuilder<R> set(WrappedBooleanFunction<R> component, BooleanSupplier value) {
        RecordParameter param = ((WrappedBooleanImpl) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        mapping.put(param, value);
        return this;
    }

    @SafeVarargs
    @Override
    public final <CT, C extends Collection<CT>, T extends CT> RecordBuilder<R> add(Function<R, C> component, Supplier<T>... value) {
        RecordParameter param = ((WrappedBooleanImpl) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        Arrays.stream(value).forEach(v -> collectionMapping.get(param).add(v));
        return this;
    }

    @Override
    public <CT, C extends Collection<CT>, T extends CT> RecordBuilder<R> add(WrappedFunction<R, C> component, Supplier<T>... value) {
        RecordParameter param = ((WrappedBooleanImpl) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        Arrays.stream(value).forEach(v -> collectionMapping.get(param).add(v));
        return this;
    }
}
