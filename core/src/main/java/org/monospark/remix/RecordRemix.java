package org.monospark.remix;

import java.util.function.Consumer;

public interface RecordRemix<R extends Record> {

    /**
     * Defines a global blank record that will be used as a starting point when calling {@link Records#blank(Class)}.
     **/
    public void blank(Consumer<RecordBuilder<R>> builder);

    /**
     * Defines the operations that should be performed when copying a record instance using
     * {@link Records#copy(Record)} and {@link Records#structuralCopy(Class, Record)}
     **/
    public void copy(Consumer<RecordOperations<R>> operations);

    /**
     * By adding an operation to a record component, you specify that an action should be performed when calling the accessor of
     * the associated record component.
     * <p>
     * Requires the record component to be wrapped using either {@link Wrapped} or one of its primitive equivalents.
     **/
    public void get(Consumer<RecordOperations<R>> operations);

    /**
     * Defines the operations that should be performed when assigning a value to the associated record component.
     * Assignment is defined as the initial value assignment in the constructor and the assignment using
     * {@link Records#set(Mutable, Object)} if the record component is made mutable by using {@link Mutable}.
     * <p>
     * Requires the record component to be wrapped using either {@link Wrapped}, {@link Mutable}
     * or one of its primitive equivalents.
     **/
    public void assign(Consumer<RecordOperations<R>> operations);

    /**
     * If the actions performed for the constructor assignment and setter assignment should be different,
     * override this method to explicitly specify setter operations.
     * <p>
     * Otherwise, {@link #assign(RecordOperations)} is used to determine the setter operations.
     **/
    public void set(Consumer<RecordOperations<R>> operations);
}
