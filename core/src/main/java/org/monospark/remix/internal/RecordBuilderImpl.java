package org.monospark.remix.internal;

import org.monospark.remix.RecordBuilder;
import org.monospark.remix.Wrapped;
import org.monospark.remix.WrappedBoolean;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class RecordBuilderImpl<R extends Record> implements RecordBuilder<R> {

    private Class<R> recordClass;
    private RecordCacheData<R> cacheData;
    private Map<RecordParameter, Object> mapping;

    public RecordBuilderImpl(Class<R> recordClass) {
        RecordCacheData<R> d = RecordCache.getOrAdd(recordClass);
        this.cacheData = d;
        this.recordClass = recordClass;
        this.mapping = new HashMap<>();
    }

    private void setDefaultValues() {
        for (RecordParameter p : cacheData.getParameters()) {
            if (p.getDefaultValue() != null) {
                mapping.put(p, p.getType().wrapDefault(p, p.getDefaultValue()));
            }
        }
    }

    public R build() {
        return object;
    }


    public <T> RecordBuilderImpl<R> set(Function<R, T> component, T value) {
        RecordParameter param = cacheData.getBuilderCache().resolve(component, value);
        mapping.put(param, value);
        return this;
    }

    @Override
    public RecordBuilder<R> set(ToIntFunction<R> component, int value) {
        RecordParameter param = cacheData.getBuilderCache().resolve(component::applyAsInt, value);
        mapping.put(param, value);
        return this;
    }

    @Override
    public RecordBuilder<R> set(Function<R, Boolean> component, boolean value) {
        RecordParameter param = cacheData.getBuilderCache().resolveBoolean(component, value);
        mapping.put(param, value);
        return this;
    }

    @Override
    public <T> RecordBuilder<R> set(WrappedFunction<R, T> component, T value) {
        RecordParameter param = ((WrappedImpl<R>) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        mapping.put(param, value);
        return this;
    }

    @Override
    public RecordBuilder<R> set(WrappedBooleanFunction<R> component, boolean value) {
        RecordParameter param = ((WrappedBooleanImpl) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        mapping.put(param, value);
        return this;
    }

    @Override
    public RecordBuilder<R> set(WrappedIntFunction<R> component, int value) {
        RecordParameter param = ((WrappedIntImpl) component.apply(cacheData.getRecordInstance())).getRecordParameter();
        mapping.put(param, value);
        return this;
    }

    @Override
    public <CT, C extends Collection<CT>, T extends CT> RecordBuilder<R> add(Function<R, CT> variable, T... value) {
        return null;
    }

    @Override
    public <CT, C extends Collection<CT>, T extends CT> RecordBuilder<R> add(WrappedFunction<R, C> variable, T... value) {
        if (!mapping.containsKey(RecordParameter.get(variable.apply(object)))) {
            throw new IllegalArgumentException();
        }

        var c = ((Collection<T>) mapping.get(RecordParameter.get(variable.apply(object))));
        Arrays.stream(value).forEach(c::add);
        return this;
    }



}
