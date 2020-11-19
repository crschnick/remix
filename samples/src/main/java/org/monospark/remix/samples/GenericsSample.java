package org.monospark.remix.samples;

import org.monospark.remix.Records;

public class GenericsSample {

    public static void main(String[] args) {
        record GenericRecord<T1,T2>(T1 v1, T2 v2) {}

        GenericRecord<Object,String> a = Records.create(GenericRecord.class, new Object(), "");
        GenericRecord<Object,String> b = Records.builder(GenericRecord.class).build();
    }
}
