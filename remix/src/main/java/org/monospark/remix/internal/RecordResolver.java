package org.monospark.remix.internal;

import org.monospark.remix.LambdaSupport;
import org.monospark.remix.RecordResolveException;
import org.monospark.remix.Records;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RecordResolver<R extends Record> {

    private List<RecordParameter> parameters;
    private R primitiveInstance;
    private Map<RecordParameter, Object> primitiveValueMap;
    private R wrapperInstance;
    private List<R> booleanResolveInstances;
    private R learnedObjectsInstance;
    private Map<RecordParameter, Object> cachedParameters;

    RecordResolver(Constructor<R> constructor, List<RecordParameter> parameters) {
        this.wrapperInstance = defaultRecordInstance(constructor, parameters);
        this.parameters = parameters;
        this.cachedParameters = new HashMap<>();
        this.booleanResolveInstances = BooleanResolver.createInstances(parameters);
        this.primitiveValueMap = new HashMap<>();
        this.primitiveInstance = createPrimitiveInstance();
        this.learnedObjectsInstance = createLearnedObjectsInstance();
    }

    <T> RecordParameter resolveWrapped(LambdaSupport.WrappedFunction<R, T> methodRef) {
        Object wrapperValue = methodRef.apply(wrapperInstance);
        return ((Wrapper) wrapperValue).getRecordParameter();
    }

    <T> ParameterValue<T> resolve(Function<R, T> methodRef, Supplier<T> valueSupplier) {
        T value = valueSupplier.get();

        // If the value is null, we check whether we already know a parameter instance
        if (value == null) {
            var query = methodRef.apply(learnedObjectsInstance);

            // If we don't, then nothing changes
            if (query == null) {
                return null;
            }

            // If we do, then we explicitly set the value to null for the component
            else {
                for (RecordParameter p : parameters) {
                    var cached = cachedParameters.get(p);
                    if (cached != null && cached == query) {
                        return new ParameterValue<>(p, null);
                    }
                }
            }
        }

        // Check for boolean
        else if (value.getClass().equals(Boolean.class)) {
            return new ParameterValue<>(resolveBoolean((Function<R, Boolean>) methodRef), value);
        } else {
            boolean boxed = methodRef.apply(primitiveInstance) != null;
            if (boxed) {
                for (RecordParameter p : parameters) {
                    if (checkPrimitiveParameter(methodRef, p)) {
                        return new ParameterValue<>(p, value);
                    }
                }
            } else {
                for (RecordParameter p : parameters) {
                    if (checkAndCacheParameter(methodRef, p, value)) {
                        return new ParameterValue<>(p, value);
                    }
                }
            }
        }

        throw new RecordResolveException("Could not resolve record component for input value "
                + valueSupplier.get().toString() + ", therefore the method reference does not belong to the associated record class");
    }

    private R defaultRecordInstance(Constructor<R> constructor, List<RecordParameter> parameters) {
        Object[] args = new Object[parameters.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = parameters.get(i).defaultValue();
        }
        R obj = Records.createRaw(constructor.getDeclaringClass(), args);
        return obj;
    }

    private RecordParameter resolveBoolean(Function<R, Boolean> methodRef) {
        return BooleanResolver.resolveParameter(parameters, booleanResolveInstances, methodRef);
    }

    private boolean checkPrimitiveParameter(Function<R, ?> methodRef, RecordParameter param) {
        var query = methodRef.apply(primitiveInstance);
        return query.equals(primitiveValueMap.get(param));
    }

    private <T> boolean checkAndCacheParameter(Function<R, ?> methodRef, RecordParameter param, T value) {
        var query = methodRef.apply(learnedObjectsInstance);
        var def = DefaultValueHelper.createDefaultValue(param.getComponent().getType());
        if (query != def && query == cachedParameters.get(param)) {
            return true;
        }

        cachedParameters.put(param, param.wrap(value));
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
            var type = p.getType().getValueType();
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                values[i] = false;
            } else if (type.isPrimitive() || DefaultValueHelper.isBoxedClass(type)) {
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

    public static record ParameterValue<T>(RecordParameter parameter, T value) {
    }
}
