package org.monospark.remix.sample;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class DefaultSample {

    public static record BigRecord(
            byte byteVar,
            short shortVar,
            int intVar,
            long longVar,
            float floatVar,
            double doubleVar,
            char charVar,
            boolean booleanVar,
            String stringVar,
            Class<?> clazz,
            int[] intArrayVar,
            Instant[] instantVar,
            LocalDateTime dateTimeVar,
            List<Object> listVar) {

    }


}
