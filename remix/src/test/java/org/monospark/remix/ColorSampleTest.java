package org.monospark.remix;

import org.junit.Ignore;
import org.junit.Test;
import org.monospark.remix.samples.Color;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ColorSampleTest {

    @Ignore
    public record OtherColor(int red, int green, int blue) {}

    @Test
    public void testStructuralCopy() {
        Color c = Records.create(Color.class, 500, 2032, 2034);
        OtherColor other = Records.structuralCopy(OtherColor.class, c);
        Color fromOther = Records.structuralCopy(Color.class, other);

        assertThat(Records.get(c::red), equalTo(Records.get(other::red)));
        assertThat(Records.get(c::green), equalTo(Records.get(other::green)));
        assertThat(Records.get(c::blue), equalTo(Records.get(other::blue)));
        assertThat(c, equalTo(fromOther));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() {
        var c = Records.create(OtherColor.class, -1, -1, -1);
        Records.structuralCopy(Color.class, c);
    }
}
