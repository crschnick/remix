package org.monospark.remix.internal;

import org.monospark.remix.RecordOperations;
import org.monospark.remix.Wrapped;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class RecordOperationsImpl<R extends Record> implements RecordOperations<R> {

    private Class<R> recordClass;
    private List<OperatorEntry<R, ?>> operatorEntries;
    private Map<RecordParameter, UnaryOperator<?>> parameterOperators;

    RecordOperationsImpl(Class<R> recordClass) {
        this.recordClass = recordClass;
        this.operatorEntries = new ArrayList<>();
        this.parameterOperators = new HashMap<>();
    }

    public <T> UnaryOperator<T> getOperator(RecordParameter param) {
        if (!parameterOperators.containsKey(param)) {
            RecordCacheData<R> d = RecordCache.getOrAdd(recordClass);
            List<OperatorEntry<R, T>> matchingEntries = new ArrayList<>();
            for (OperatorEntry<R, ?> e : operatorEntries) {
                if (d.getResolverCache().resolveWrapped(e.reference).equals(param)) {
                    matchingEntries.add((OperatorEntry<R, T>) e);
                }
            }
            if (matchingEntries.size() > 0) {
                parameterOperators.put(param, v -> {
                    T obj = (T) v;
                    for (var e : matchingEntries) {
                        e.operator.apply(obj);
                    }
                    return obj;
                });
            } else {
                parameterOperators.put(param, null);
            }
        }
        return (UnaryOperator<T>) parameterOperators.get(param);
    }

    @Override
    public <T> RecordOperations<R> add(Function<R, Wrapped<T>> component, UnaryOperator<T> op) {
        this.operatorEntries.add(new OperatorEntry<R, T>(component, op));
        return this;
    }

    @Override
    public <T> RecordOperations<R> notNull(Function<R, Wrapped<T>> component) {
        return add(component, Objects::requireNonNull);
    }

    @Override
    public <T> RecordOperations<R> check(Function<R, Wrapped<T>> component, Predicate<T> toCheck) {
        return add(component, (T v) -> {
            if (!toCheck.test(v)) {
                throw new IllegalArgumentException("Condition not met");
            } else {
                return v;
            }
        });
    }

    private record OperatorEntry<R extends Record, T>(Function<R, Wrapped<T>> reference, UnaryOperator<T> operator) {
    }
}
