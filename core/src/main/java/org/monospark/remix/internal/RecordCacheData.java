package org.monospark.remix.internal;

import org.monospark.remix.*;

import java.lang.reflect.Constructor;
import java.util.List;

public class RecordCacheData<T extends Record> {

    private RecordRemixImpl<T> remix;
    private Constructor<T> constructor;
    private List<RecordParameter> parameters;
    private RecordResolverData<T> resolverCache;

    public RecordCacheData(RecordRemixImpl<T> remix, Constructor<T> constructor, List<RecordParameter> parameters) {
        this.remix = remix;
        this.constructor = constructor;
        this.parameters = parameters;
    }

    private static boolean hasRemixAnnotation(Class<? extends Record> recordClass) {
        return recordClass.getAnnotationsByType(Remix.class).length > 0;
    }

    static <R extends Record, T extends RecordRemixer<R>> RecordCacheData<R> fromRecordClass(
            Class<R> recordClass, RecordRemixer<R> remixer) {
        RecordRemixImpl<R> remix = new RecordRemixImpl<>(recordClass);
        if (hasRemixAnnotation(recordClass) || remixer != null) {
            if (recordClass.getDeclaredConstructors().length > 1) {
                throw new RemixException("More than one constructors declared");
            }
            if (remixer != null) {
                remixer.create(remix);
            } else {
                Class<T> clazz = (Class<T>) recordClass.getAnnotation(Remix.class).value();
                RecordRemixCache.getOrAddRecordRemixer(clazz).create(remix);
            }
        }

        var cons = (Constructor<R>) recordClass.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        return new RecordCacheData<R>(remix, cons,
                RecordParameter.fromRecordComponents(recordClass));
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

    public RecordRemixImpl<T> getRemix() {
        return remix;
    }
}
