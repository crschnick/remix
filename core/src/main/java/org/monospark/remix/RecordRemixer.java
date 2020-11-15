package org.monospark.remix;

public interface RecordRemixer<R extends Record> {

    void create(RecordRemix<R> r);
}
