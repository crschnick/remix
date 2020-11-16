package org.monospark.remix.sample;

import org.monospark.remix.Records;
import org.monospark.remix.Wrapped;

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
            List<Object> listVar,
            Wrapped<String> wString) {

    }

    public static void main(String[] args) {
        BigRecord r = null;
        Records.get(r::booleanVar);
        Records.get(r::shortVar);
        Records.get(r::intVar);
        Records.get(r::stringVar);
        Records.get(r::wString);
    }

}
