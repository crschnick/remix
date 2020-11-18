package org.monospark.remix;

/**
 * A {@code RecordRemixer} is a factory for a {@link RecordRemix}
 * that can be linked to a record class using either the {@link Remix} annotation or
 * {@link Records#remix(Class, RecordRemixer)}.
 * <p>
 * Any subclass must have a default constructor to instantiate it via reflection.
 **/
@FunctionalInterface
public interface RecordRemixer<R extends Record> {

    /**
     * Creates the {@link RecordRemix} that specifies the record behaviour.
     * <p>
     * This method is only called once.
     **/
    void create(RecordRemix<R> r);
}
