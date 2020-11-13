package org.monospark.remix.internal;

import org.monospark.remix.RecordOperations;
import org.monospark.remix.Wrapped;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class OperationsCache<R extends Record> implements RecordOperations<R> {

    private Class<R> recordClass;
    private Map<Function<R,?>, UnaryOperator<?>> operators;
    private Map<RecordParameter, UnaryOperator<?>> parameterOperators;

    OperationsCache(Class<R> recordClass) {
        this.recordClass = recordClass;
        this.operators = new HashMap<>();
        this.parameterOperators = new HashMap<>();
    }

    public <T> UnaryOperator<?> getOperator(RecordParameter param, Supplier<T> value) {
        if (!parameterOperators.containsKey(param)) {
            RecordCacheData<R> d = RecordCache.getOrAdd(recordClass);
            for (var e : operators.entrySet()) {
                if (d.getResolverCache().resolve(e.getKey(), value).equals(param)) {
                    var op = operators.get(param);
                    parameterOperators.put(param, operators.get(param));
                    return op;
                }
            }
        }
        return parameterOperators.get(param);
    }

    @Override
    public <T> RecordOperations<R> add(Function<R, Wrapped<T>> component, UnaryOperator<T> op) {
        if (!operators.containsKey(component)) {
            operators.put(component, op);
        } else {
            UnaryOperator<T> oldOp = (UnaryOperator<T>) operators.get(component);
            operators.put(component, v -> {
                var next = oldOp.apply((T) v);
                return op.apply(next);
            });
        }
        return this;
    }
}
