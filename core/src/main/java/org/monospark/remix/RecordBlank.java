package org.monospark.remix;

import org.monospark.remix.internal.RecordBlankImpl;

public sealed abstract class RecordBlank<R extends Record> permits RecordBlankImpl {

    protected abstract RecordBuilder<R> builder();

}
