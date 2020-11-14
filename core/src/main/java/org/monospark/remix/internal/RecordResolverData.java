package org.monospark.remix.internal;

import org.monospark.remix.RecordBuilderException;
import org.monospark.remix.Records;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RecordResolverData<R extends Record> {

    private List<RecordParameter> parameters;

    private R primitiveInstance;
    private Map<RecordParameter, Object> primitiveValueMap;

    private R wrapperInstance;

    private List<R> booleanResolveInstances;
    private R learnedObjectsInstance;

    private Map<RecordParameter, Object> cachedParameters;

    RecordResolverData(R defaultInstance, List<RecordParameter> parameters) {
        this.wrapperInstance = defaultInstance;
        this.parameters = parameters;
        this.cachedParameters = new HashMap<>();
        this.booleanResolveInstances = BooleanResolver.createInstances(parameters);
        this.primitiveValueMap = new HashMap<>();
        this.primitiveInstance = createPrimitiveInstance();
        this.learnedObjectsInstance = createLearnedObjectsInstance();
    }

    RecordParameter resolveBoolean(Function<R, Boolean> methodRef) {
        return BooleanResolver.resolveParameter(parameters, booleanResolveInstances, methodRef);
    }

    <T> RecordParameter resolveWrapped(Function<R, ?> methodRef) {
        Object wrapperValue = methodRef.apply(wrapperInstance);
        return ((Wrapper) wrapperValue).getRecordParameter();
    }

    <T> RecordParameter resolve(Function<R, ?> methodRef, Supplier<T> valueSupplier) {
        boolean boxed = methodRef.apply(primitiveInstance) != null;
        for (RecordParameter p : parameters) {
            if ((boxed && checkPrimitiveParameter(methodRef, p)) || (!boxed && checkAndCacheParameter(methodRef, p, valueSupplier))) {
                return p;
            }
        }
        throw new RecordBuilderException("Could not resolve record component for input value "
                + valueSupplier.get().toString() + ", therefore the method reference does not belong to the associated record class");
    }

    private boolean checkPrimitiveParameter(Function<R, ?> methodRef, RecordParameter param) {
        var query = methodRef.apply(primitiveInstance);
        return query.equals(primitiveValueMap.get(param));
    }

    private <T> boolean checkAndCacheParameter(Function<R, ?> methodRef, RecordParameter param, Supplier<T> valueSupplier) {
        var query = methodRef.apply(learnedObjectsInstance);
        var def = DefaultValueHelper.createDefaultValue(param.getComponent().getType());
        if (query != def && query == cachedParameters.get(param)) {
            return true;
        }

        cachedParameters.put(param, param.wrap(valueSupplier.get()));
        R obj = createLearnedObjectsInstance();
        query = methodRef.apply(obj);
        if (query != def && query == cachedParameters.get(param)) {
            learnedObjectsInstance = obj;
            return true;
        } else {
            cachedParameters.remove(param);
            return false;
        }
    }

    private R createPrimitiveInstance() {
        Object[] values = new Object[parameters.size()];
        Class<R> c = (Class<R>) parameters.get(0).getComponent().getDeclaringRecord();

        for (int i = 0; i < parameters.size(); i++) {
            RecordParameter p = parameters.get(i);
            if (p.getComponent().getType().equals(boolean.class) || p.getComponent().getType().equals(Boolean.class)) {
                values[i] = false;
            } else if (p.getComponent().getType().isPrimitive() || DefaultValueHelper.isBoxedClass(p.getComponent().getType())) {
                values[i] = i;
                primitiveValueMap.put(p, i);
            } else {
                values[i] = null;
            }
        }
        return (R) Records.createRaw(c, values);
    }

    private R createLearnedObjectsInstance() {
        Object[] values = new Object[parameters.size()];
        Class<R> c = (Class<R>) parameters.get(0).getComponent().getDeclaringRecord();

        for (int i = 0; i < parameters.size(); i++) {
            RecordParameter p = parameters.get(i);
            if (cachedParameters.containsKey(p)) {
                values[i] = cachedParameters.get(p);
                continue;
            }

            if (p.getComponent().getType().equals(boolean.class)) {
                values[i] = false;
            } else if (p.getComponent().getType().isPrimitive()) {
                values[i] = 0;
            } else {
                values[i] = p.defaultValue();
            }
        }
        return (R) Records.createRaw(c, values);
    }
}
