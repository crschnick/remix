package org.monospark.remix;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ResolverTest {

    @Ignore
    public static record TestRecord(String s1, String s2, Mutable<String> s3, boolean b4, boolean b5, Boolean b6) {
    }

    @Test
    public void testNullAndDefaultResolvement() {
        String same = "test";
        var t = Records.builder(TestRecord.class)
                .set(TestRecord::s1).to(() -> null)
                .set(TestRecord::s3).to(() -> same)
                .set(TestRecord::b6).to(() -> null)
                .build();

        assertThat(Records.get(t::s1), equalTo(null));
        assertThat(Records.get(t::s2), equalTo(null));
        assertThat(Records.get(t::s3), equalTo(same));
        assertThat(Records.get(t::b4), equalTo(false));
        assertThat(Records.get(t::b5), equalTo(false));
        assertThat(Records.get(t::b6), equalTo(null));

        Records.set(t::s3, "test2");
        assertThat(Records.get(t::s3), equalTo("test2"));
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

        assertThat(Records.get(t::s1), equalTo(same));
        assertThat(Records.get(t::s2), equalTo(same));
        assertThat(Records.get(t::s3), equalTo(same));
        assertThat(Records.get(t::b4), equalTo(false));
        assertThat(Records.get(t::b5), equalTo(false));
        assertThat(Records.get(t::b6), equalTo(true));

        Records.set(t::s3, "test2");
        assertThat(Records.get(t::s3), equalTo("test2"));
    }
}
