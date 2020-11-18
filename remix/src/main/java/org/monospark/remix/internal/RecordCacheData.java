package org.monospark.remix.internal;

import org.monospark.remix.RecordRemix;
import org.monospark.remix.RecordRemixer;
import org.monospark.remix.Remix;
import org.monospark.remix.RemixException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class RecordCacheData<T extends Record> {

    private RecordRemixImpl<T> remix;
    private Constructor<T> constructor;
    private List<RecordParameter> parameters;
    private RecordResolver<T> resolverCache;

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
                var value = (Class<T>) recordClass.getAnnotation(Remix.class).value();
                if (!value.equals(Remix.None.class)) {
                    RecordRemixCache.getOrAddRecordRemixer(value).create(remix);
                } else {
                    try {
                        Method m = recordClass.getDeclaredMethod("createRemix", RecordRemix.class);
                        m.setAccessible(true);
                        m.invoke(null, remix);
                    } catch (NoSuchMethodException e) {
                        throw new RemixException("Record class is annotated but missing createRemix method", e);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RemixException("Exception while calling createRemix method", e);
                    }
                }
            }
        }

        var cons = (Constructor<R>) recordClass.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        return new RecordCacheData<R>(remix, cons,
                RecordParameter.fromRecordComponents(recordClass));
    }

    public RecordResolver<T> getResolverCache() {
        if (resolverCache == null) {
            this.resolverCache = new RecordResolver<T>(constructor, parameters);
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
