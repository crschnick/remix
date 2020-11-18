package org.monospark.remix;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResolverTest {

    @Test
    public void testNullAndDefaultResolvement() {
        String same = "test";
        var t = Records.builder(TestRecord.class)
                .set(TestRecord::s1).to(() -> null)
                .set(TestRecord::s3).to(() -> same)
                .set(TestRecord::b6).to(() -> null)
                .build();

        assertEquals(Records.get(t::s1), null);
        assertEquals(Records.get(t::s2), null);
        assertEquals(Records.get(t::s3), same);
        assertEquals(Records.get(t::b4), false);
        assertEquals(Records.get(t::b5), false);
        assertEquals(Records.get(t::b6), null);

        Records.set(t::s3, "test2");
        assertEquals(Records.get(t::s3), "test2");
    }

    @Test
    public void testSameResolvement() {
        String same = "test";
        var t = Records.builder(TestRecord.class)
                .set(TestRecord::s1).to(() -> same)
                .set(TestRecord::s2).to(() -> same)
                .set(TestRecord::s3).to(() -> same)
                .set(TestRecord::b4).to(() -> false)
                .set(TestRecord::b5).to(() -> false)
                .set(TestRecord::b6).to(() -> true)
                .build();

        assertEquals(Records.get(t::s1), same);
        assertEquals(Records.get(t::s2), same);
        assertEquals(Records.get(t::s3), same);
        assertEquals(Records.get(t::b4), false);
        assertEquals(Records.get(t::b5), false);
        assertEquals(Records.get(t::b6), true);

        Records.set(t::s3, "test2");
        assertEquals(Records.get(t::s3), "test2");
    }

    @Disabled
    public static record TestRecord(String s1, String s2, Mutable<String> s3, boolean b4, boolean b5, Boolean b6) {
    }
}
