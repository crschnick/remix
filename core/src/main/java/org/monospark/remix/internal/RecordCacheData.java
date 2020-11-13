package org.monospark.remix.internal;

import org.monospark.remix.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class RecordCacheData<T extends Record> {

    private static boolean hasRemixAnnotations(Class<? extends Record> recordClass) {
        return Arrays.stream(recordClass.getRecordComponents())
                .anyMatch(c -> Arrays.stream(c.getAnnotations())
                        .anyMatch(a -> RemixAnnotations.ALL_ANNOTATIONS.contains(a.annotationType())));
    }

    private static boolean hasOnlyGeneratedConstructor(Class<? extends Record> recordClass) {
        if (recordClass.getDeclaredConstructors().length > 1) {
            return false;
        }

        return Arrays.stream(recordClass.getDeclaredConstructors()[0].getParameters())
                .anyMatch(p -> Arrays.stream(p.getAnnotations())
                        .anyMatch(a -> RemixAnnotations.ALL_ANNOTATIONS.contains(a.annotationType())));
    }

    private static <R extends Record> boolean hasOnlyGeneratedConstructor(Class<R> recordClass) throws Exception {
        if (recordClass.getAnnotation(Remix.class) != null) {
            return;
        }


        RecordResolverData<R> resolverCache = new RecordResolverData<R>(parameters);

        Class<? extends RecordRemix<R>> clazz = (Class<? extends RecordRemix<R>>) recordClass.getAnnotation(Remix.class).value();
        RecordRemix<R> b = (RecordRemix<R>) clazz.getDeclaredConstructors()[0].newInstance();
        RecordBuilder<R> blank = new RecordBuilderImpl<>();
        b.blank(blank);
    }

    static <T extends Record> RecordCacheData<T> fromRecordClass(Class<T> recordClass) {
        if (hasRemixAnnotations(recordClass)) {
            if (!hasOnlyGeneratedConstructor(recordClass)) {
                throw new RemixException("");
            }
        }

        return new RecordCacheData<T>(
                (Constructor<T>) recordClass.getDeclaredConstructors()[0],
                RecordParameter.fromRecordComponents(recordClass));
    }

    private T recordInstance;
    private RecordBuilder blank;
    private Constructor<T> constructor;
    private List<RecordParameter> parameters;
    private RecordResolverData<T> resolverCache;
    private OperationsCache<T> opCache;

    private RecordCacheData(Constructor<T> constructor, List<RecordParameter> parameters) {
        this.constructor = constructor;
        this.parameters = parameters;
        this.resolverCache = new RecordResolverData<T>(parameters);
        this.opCache = new OperationsCache<>(constructor.getDeclaringClass());
    }

    static <T extends Record> T defaultRecordInstance(Constructor<T> constructor, List<RecordParameter> parameters) {
        Object[] args = new Object[parameters.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = parameters.get(i).getType().wrap(
                    parameters.get(i),
                    DefaultValueHelper.createDefaultValue(parameters.get(i).getComponent().getType()));
        }
        T obj = Records.create(constructor.getDeclaringClass(), args);
        return obj;
    }

    public T getRecordInstance() {
        if (this.recordInstance == null) {
            this.recordInstance = defaultRecordInstance(constructor, parameters);
        }
        return recordInstance;
    }

    public RecordBuilder getBlank() {
        return blank;
    }

    public OperationsCache<T> getOperationsCache() {
        return opCache;
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
}
