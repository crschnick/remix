package org.monospark.remix.internal;

import org.monospark.remix.*;

import java.lang.reflect.Constructor;
import java.util.List;

public class RecordCacheData<T extends Record> {

    private RecordRemix<T> remix;
    private T recordInstance;
    private RecordBlank<T> blank;
    private Constructor<T> constructor;
    private List<RecordParameter> parameters;
    private RecordResolverData<T> resolverCache;
    private RecordOperationsImpl<T> getOperations;
    private RecordOperationsImpl<T> assignOperations;
    private RecordOperationsImpl<T> setOperations;
    private RecordOperationsImpl<T> copyOperations;
    public RecordCacheData(RecordRemix<T> remix, Constructor<T> constructor,
                           List<RecordParameter> parameters, RecordOperationsImpl<T> getOperations,
                           RecordOperationsImpl<T> assignOperations, RecordOperationsImpl<T> setOperations,
                           RecordOperationsImpl<T> copyOperations) {
        this.remix = remix;
        this.constructor = constructor;
        this.parameters = parameters;
        this.getOperations = getOperations;
        this.assignOperations = assignOperations;
        this.setOperations = setOperations;
        this.copyOperations = copyOperations;
    }

    private static boolean hasRemixAnnotation(Class<? extends Record> recordClass) {
        return recordClass.getAnnotationsByType(Remix.class).length > 0;
    }

    private static <R extends Record, T extends RecordRemix<R>> RecordBlank<R> createBlank(Class<R> recordClass) {
        Class<T> clazz = (Class<T>) recordClass.getAnnotation(Remix.class).value();
        RecordRemix<R> remix = RecordRemixCache.getRecordRemix((Class<T>) recordClass.getAnnotation(Remix.class).value());
        RecordBuilder<R> blank = new RecordBuilderImpl<R>(recordClass);
        remix.blank(blank);
        return blank.blank();
    }

    static <R extends Record, T extends RecordRemix<R>> RecordCacheData<R> fromRecordClass(Class<R> recordClass) {
        var get = new RecordOperationsImpl<>(recordClass);
        var set = new RecordOperationsImpl<>(recordClass);
        var assign = new RecordOperationsImpl<>(recordClass);
        var copy = new RecordOperationsImpl<>(recordClass);

        RecordRemix<R> remix = null;
        if (hasRemixAnnotation(recordClass)) {
            if (recordClass.getDeclaredConstructors().length > 1) {
                throw new RemixException("More than one constructors declared");
            }
            Class<T> clazz = (Class<T>) recordClass.getAnnotation(Remix.class).value();
            remix = RecordRemixCache.getRecordRemix(clazz);


            remix.get(get);
            remix.set(set);
            remix.assign(assign);
            remix.copy(copy);
        }

        var cons = (Constructor<R>) recordClass.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        return new RecordCacheData<R>(remix, cons,
                RecordParameter.fromRecordComponents(recordClass), get, assign, set, copy);
    }

    public RecordBlank<T> getBlank() {
        if (blank == null) {
            RecordBuilder<T> b = new RecordBuilderImpl<T>(constructor.getDeclaringClass());
            if (remix != null) this.remix.blank(b);
            this.blank = b.blank();
        }
        return blank;
    }

    public RecordResolverData<T> getResolverCache() {
        if (resolverCache == null) {
            this.resolverCache = new RecordResolverData<T>(constructor, parameters);
        }
        return resolverCache;
    }

    public List<RecordParameter> getParameters() {
        return parameters;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public RecordOperationsImpl<T> getGetOperations() {
        return getOperations;
    }

    public RecordOperationsImpl<T> getAssignOperations() {
        return assignOperations;
    }

    public RecordOperationsImpl<T> getSetOperations() {
        return setOperations;
    }

    public RecordOperationsImpl<T> getCopyOperations() {
        return copyOperations;
    }
}
