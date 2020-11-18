package org.monospark.remix;

import org.junit.jupiter.api.Test;
import org.monospark.remix.samples.DefaultSample;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultSampleTest {

    @Test
    public void testDefaultValues() {
        DefaultSample.BigRecord r = Records.builder(DefaultSample.BigRecord.class).build();

        assertEquals(r.booleanVar(),false);
        assertEquals(r.byteVar(), (byte) 0);
        assertEquals(r.shortVar(), (short) 0);
        assertEquals(r.intVar(), 0);
    }
}
