package org.monospark.remix.internal;

import org.monospark.remix.RecordBlank;
import org.monospark.remix.RecordBuilder;
import org.monospark.remix.RecordOperations;
import org.monospark.remix.RecordRemix;

import java.util.function.Consumer;

public final class RecordRemixImpl<T extends Record> implements RecordRemix<T> {

    private Class<T> clazz;
    private RecordBlank<T> blank;
    private Consumer<RecordBuilder<T>> blankBuilder;
    private RecordOperationsImpl<T> getOperations;
    private RecordOperationsImpl<T> assignOperations;
    private RecordOperationsImpl<T> setOperations;
    private RecordOperationsImpl<T> copyOperations;

    RecordRemixImpl(Class<T> clazz) {
        this.clazz = clazz;
        getOperations = new RecordOperationsImpl<>(clazz);
        assignOperations = new RecordOperationsImpl<>(clazz);
    }

    @Override
    public void blank(Consumer<RecordBuilder<T>> builder) {
        blankBuilder = builder;
    }

    @Override
    public void copy(Consumer<RecordOperations<T>> operations) {
        this.copyOperations = new RecordOperationsImpl<>(clazz);
        operations.accept(copyOperations);
    }

    @Override
    public void get(Consumer<RecordOperations<T>> operations) {
        operations.accept(getOperations);
    }

    @Override
    public void assign(Consumer<RecordOperations<T>> operations) {
        operations.accept(assignOperations);
    }

    @Override
    public void set(Consumer<RecordOperations<T>> operations) {
        this.setOperations = new RecordOperationsImpl<>(clazz);
        operations.accept(setOperations);
    }

    public RecordBlank<T> getBlank() {
        if (blank == null) {
            var b = new RecordBuilderImpl<T>(clazz);
            blankBuilder.accept(b);
            blank = b.blank();
        }
        return blank;
    }

    public RecordOperationsImpl<T> getGetOperations() {
        return getOperations;
    }

    public RecordOperationsImpl<T> getAssignOperations() {
        return assignOperations;
    }

    public RecordOperationsImpl<T> getSetOperations() {
        return setOperations != null ? setOperations : assignOperations;
    }

    public RecordOperationsImpl<T> getCopyOperations() {
        return copyOperations != null ? copyOperations : getOperations;
    }
}
