package org.monospark.remix.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class RecordBuilderCacheData<R extends Record> {

    private List<RecordParameter> parameters;

    private List<R> booleanResolveInstances;

    private R lastNonBooleanInstance;
    private Map<RecordParameter, Object> cachedParameters;

    RecordBuilderCacheData(List<RecordParameter> parameters) {
        this.parameters = parameters;
        this.cachedParameters = new HashMap<>();
        this.lastNonBooleanInstance = createDefaultValue();
        this.booleanResolveInstances = BooleanResolver.createInstances(parameters);
    }

    RecordParameter resolveBoolean(Function<R,Boolean> methodRef, Object value) {
        return BooleanResolver.resolveParameter(parameters, booleanResolveInstances, methodRef);
    }

    RecordParameter resolve(Function<R,?> methodRef, Object value) {
        for (RecordParameter p : parameters) {
            if (checkAndCacheParameter(methodRef, p, value)) {
                return p;
            }
        }
        throw new IllegalArgumentException();
    }

    private boolean checkAndCacheParameter(Function<R,?> methodRef, RecordParameter param, Object value) {
        var query = methodRef.apply(lastNonBooleanInstance);
        if (query != null && query == cachedParameters.get(param)) {
            return true;
        }

        cachedParameters.put(param, value);
        R obj = createDefaultValue();
        query = methodRef.apply(obj);
        if (query != null ) {
            lastNonBooleanInstance = obj;
            return true;
        } else {
            cachedParameters.remove(param);
            return false;
        }
    }

    private R createDefaultValue() {
        Object[] values = new Object[parameters.size()];
        var c = parameters.get(0).getComponent().getDeclaringRecord();
        for (int i = 0; i < parameters.size(); i++) {
            values[i] = cachedParameters.getOrDefault(parameters.get(i), DefaultValueHelper.createDefaultValue(c));
        }
        try {
            return (R) c.getDeclaredConstructors()[0].newInstance(values);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
