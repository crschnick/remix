package org.monospark.remix;

import java.util.function.Consumer;

public interface RecordRemix<R extends Record> {

    /**
     * Defines a global blank record that will be used as a basis
     * when creating a new builder using {@link Records#builder(Class)}.
     **/
    public void blank(Consumer<RecordBuilder<R>> builder);

    /**
     * Defines the operations that should be performed when copying a record instance using
     * {@link Records#copy(Record)} and {@link Records#structuralCopy(Class, Record)}
     *
     * If the copy operations should be different,
     * override this method to explicitly specify copy operations.
     *
     * Otherwise, the {@link #get(Consumer)} and {@link #assign(Consumer)} are used to determine the copy operations.
     **/
    public void copy(Consumer<RecordOperations<R>> operations);

    /**
     * Adding a {@code get} operation to a record component specifies that an
     * operation should be performed when calling the accessor of the associated record component.
     *
     * Requires the record component to be wrapped using either {@link Wrapped},
     * {@link Mutable} or one of its primitive equivalents.
     **/
    public void get(Consumer<RecordOperations<R>> operations);

    /**
     * Adding a {@code assign} operation to a record component specifies that an
     * operation should be performed when assigning a value to the associated record component.
     *
     * Assignment is defined as the initial value assignment in the constructor and the assignment using
     * {@link Records#set(LambdaSupport.MutableSupplier, Object)} if
     * the record component is made mutable by using {@link Mutable}.
     *
     * Requires the record component to be wrapped using either {@link Wrapped}, {@link Mutable}
     * or one of its primitive equivalents.
     **/
    public void assign(Consumer<RecordOperations<R>> operations);

    /**
     * Adding a {@code set} operation to a record component specifies that an
     * operation should be performed when setting the value of the associated record
     * component using {@link Records#set(LambdaSupport.MutableSupplier, Object)}
     *
     * If the actions performed for the constructor assignment and setter assignment should be different,
     * override this method to explicitly specify setter operations.
     *
     * Otherwise, {@link #assign(Consumer)} is used to determine the setter operations.
     **/
    public void set(Consumer<RecordOperations<R>> operations);
}
