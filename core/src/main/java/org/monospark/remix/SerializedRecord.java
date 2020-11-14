package org.monospark.remix;

import org.monospark.remix.internal.WrappedImpl;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;

public class SerializedRecord implements Serializable {

    private String className;
    private Object[] values;

    public SerializedRecord(String className, Object[] values) {
        this.className = className;
        this.values = values;
    }

    @Serial
    private Object readResolve() {
        try {
            Class<? extends Record> c = (Class<? extends Record>) Class.forName(className);
            return Records.fromArray(c, values);
        } catch (ClassNotFoundException e) {
            throw new RemixException(e);
        }
    }
}
