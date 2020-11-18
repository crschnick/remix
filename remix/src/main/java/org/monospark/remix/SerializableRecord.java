package org.monospark.remix;

import java.io.Serial;
import java.io.Serializable;

public interface SerializableRecord extends Serializable {

    @Serial
    Object writeReplace();
}
