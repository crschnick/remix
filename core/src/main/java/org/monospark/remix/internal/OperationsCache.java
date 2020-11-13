package org.monospark.remix.internal;

import org.monospark.remix.RecordOperations;
import org.monospark.remix.Wrapped;
import org.monospark.remix.WrappedInt;
import org.monospark.remix.WrappedPrimitive;

import java.util.*;
import java.util.function.*;

public final class OperationsCache<R extends Record> implements RecordOperations<R> {

    private record OperatorEntry<R extends Record,T>(Function<R,Wrapped<T>> reference, UnaryOperator<T> operator) {}

    private Class<R> recordClass;
    private List<OperatorEntry<R,?>> operatorEntries;
    private Map<RecordParameter, UnaryOperator<?>> parameterOperators;

    OperationsCache(Class<R> recordClass) {
        this.recordClass = recordClass;
        this.operatorEntries = new ArrayList<>();
        this.parameterOperators = new HashMap<>();
    }

    public <T> UnaryOperator<?> getOperator(RecordParameter param) {
        if (!parameterOperators.containsKey(param)) {
            RecordCacheData<R> d = RecordCache.getOrAdd(recordClass);
            List<OperatorEntry<R,T>> matchingEntries = new ArrayList<>();
            for (OperatorEntry<R,?> e : operatorEntries) {
                if (d.getResolverCache().resolveWrapped(e.reference).equals(param)) {
                    matchingEntries.add((OperatorEntry<R, T>) e);
                }
            }
            parameterOperators.put(param, v -> {
                T obj = (T) v;
                for (var e : matchingEntries) {
                    e.operator.apply(obj);
                }
                return obj;
            });
        }
        return parameterOperators.get(param);
    }

    @Override
    public <T> RecordOperations<R> add(WrappedPrimitiveFunction<R, T> component, UnaryOperator<T> op) {
        return add(component.toFunction(), op);
    }

    @Override
    public <T> RecordOperations<R> add(Function<R, Wrapped<T>> component, UnaryOperator<T> op) {
        this.operatorEntries.add(new OperatorEntry<R,T>(component, op));
        return this;
    }

    @Override
    public <T> RecordOperations<R> notNull(Function<R, Wrapped<T>> component) {
        return add(component, Objects::requireNonNull);
    }

    @Override
    public <T> RecordOperations<R> check(WrappedPrimitiveFunction<R, T> component, Function<T, Boolean> toCheck) {
        return check(component.toFunction(), toCheck);
    }

    @Override
    public <T> RecordOperations<R> check(Function<R, Wrapped<T>> component, Function<T, Boolean> toCheck) {
        return add(component, (T v) -> {
            if (!toCheck.apply(v)) {
                throw new IllegalArgumentException("Condition not met");
            } else {
                return v;
            }
        });
    }
}
