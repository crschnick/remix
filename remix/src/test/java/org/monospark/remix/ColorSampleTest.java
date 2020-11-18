package org.monospark.remix;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monospark.remix.samples.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ColorSampleTest {

    @Test
    public void testStructuralCopy() {
        Color c = Records.create(Color.class, 500, 2032, 2034);
        OtherColor other = Records.structuralCopy(OtherColor.class, c);
        Color fromOther = Records.structuralCopy(Color.class, other);

        assertEquals(Records.get(c::red), Records.get(other::red));
        assertEquals(Records.get(c::green), Records.get(other::green));
        assertEquals(Records.get(c::blue), Records.get(other::blue));
        assertEquals(c, fromOther);
    }

    @Test
    public void testInvalid() {
        var c = Records.create(OtherColor.class, -1, -1, -1);
        assertThrows(IllegalArgumentException.class, () -> {
            Records.structuralCopy(Color.class, c);
        });
    }

    @Disabled
    public record OtherColor(int red, int green, int blue) {
    }
}
