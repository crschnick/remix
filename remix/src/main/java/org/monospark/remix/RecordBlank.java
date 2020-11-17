package org.monospark.remix;

import org.monospark.remix.internal.RecordBlankImpl;
import org.monospark.remix.internal.RecordParameter;

import java.util.Map;
import java.util.function.Supplier;

public sealed abstract class RecordBlank<R extends Record> permits RecordBlankImpl {

    protected abstract RecordBuilder<R> builder();

    protected abstract <T> Supplier<T> getValue(RecordParameter p);
}
