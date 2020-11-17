package org.monospark.remix;

import java.io.*;

public interface SerializableRecord extends Serializable {

    @Serial
    Object writeReplace();
}
