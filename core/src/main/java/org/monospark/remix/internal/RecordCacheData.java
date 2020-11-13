package org.monospark.remix.internal;

import org.monospark.remix.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class RecordCacheData<T extends Record> {

    private static boolean hasRemixAnnotation(Class<? extends Record> recordClass) {
        return recordClass.getAnnotationsByType(Remix.class).length > 0;
    }

    private static boolean hasOnlyGeneratedConstructor(Class<? extends Record> recordClass) {
        if (recordClass.getDeclaredConstructors().length > 1) {
            return false;
        }

        return Arrays.stream(recordClass.getDeclaredConstructors()[0].getParameters())
                .anyMatch(p -> Arrays.stream(p.getAnnotations())
                        .anyMatch(a -> RemixAnnotations.ALL_ANNOTATIONS.contains(a.annotationType())));
    }

    private static <R extends Record, T extends RecordRemix<R>> RecordBlank<R> createBlank(Class<R> recordClass) {
        Class<T> clazz = (Class<T>) recordClass.getAnnotation(Remix.class).value();
        RecordRemix<R> remix = RemixCache.getRecordRemix((Class<T>) recordClass.getAnnotation(Remix.class).value());
        RecordBuilder<R> blank = new RecordBuilderImpl<R>(recordClass);
        remix.blank(blank);
        return blank.blank();
    }

    static <R extends Record, T extends RecordRemix<R>> RecordCacheData<R> fromRecordClass(Class<R> recordClass) {
        Class<T> clazz = (Class<T>) recordClass.getAnnotation(Remix.class).value();
        RecordRemix<R> remix = RemixCache.getRecordRemix(clazz);

        var get = new OperationsCache<>(recordClass);
        var set = new OperationsCache<>(recordClass);
        var assign = new OperationsCache<>(recordClass);


        if (hasRemixAnnotation(recordClass)) {
            if (recordClass.getDeclaredConstructors().length > 1) {
                throw new RemixException("More than one constructors declared");
            }

            remix.get(get);
            remix.set(set);
            remix.assign(assign);
        }

        return new RecordCacheData<R>(remix,  (Constructor<R>) recordClass.getDeclaredConstructors()[0],
                RecordParameter.fromRecordComponents(recordClass), get, assign, set);
    }

    private RecordRemix<T> remix;
    private T recordInstance;
    private RecordBlank<T> blank;
    private Constructor<T> constructor;
    private List<RecordParameter> parameters;
    private RecordResolverData<T> resolverCache;
    private OperationsCache<T> getOperations;
    private OperationsCache<T> assignOperations;
    private OperationsCache<T> setOperations;

    public RecordCacheData(RecordRemix<T> remix, Constructor<T> constructor,
                           List<RecordParameter> parameters, OperationsCache<T> getOperations,
                           OperationsCache<T> assignOperations, OperationsCache<T> setOperations) {
        this.remix = remix;
        this.constructor = constructor;
        this.parameters = parameters;
        this.recordInstance = defaultRecordInstance(constructor, parameters);
        this.resolverCache = new RecordResolverData<T>(recordInstance, parameters);
        this.getOperations = getOperations;
        this.assignOperations = assignOperations;
        this.setOperations = setOperations;
    }

    static <T extends Record> T defaultRecordInstance(Constructor<T> constructor, List<RecordParameter> parameters) {
        Object[] args = new Object[parameters.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = parameters.get(i).defaultValue();
        }
        T obj = Records.createRaw(constructor.getDeclaringClass(), args);
        return obj;
    }

    public T getRecordInstance() {
        return recordInstance;
    }

    public RecordBlank<T> getBlank() {
        if (blank == null) {
            RecordBuilder<T> b = new RecordBuilderImpl<T>(constructor.getDeclaringClass());
            this.remix.blank(b);
            this.blank = b.blank();
        }
        return blank;
    }

    public RecordResolverData<T> getResolverCache() {
        return resolverCache;
    }

    public List<RecordParameter> getParameters() {
        return parameters;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public OperationsCache<T> getGetOperations() {
        return getOperations;
    }

    public OperationsCache<T> getAssignOperations() {
        return assignOperations;
    }

    public OperationsCache<T> getSetOperations() {
        return setOperations;
    }
}
