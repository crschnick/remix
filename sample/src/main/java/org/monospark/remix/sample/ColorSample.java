package org.monospark.remix.sample;

import org.monospark.remix.Records;

public class ColorSample {

    public static void main(String[] args) {
        record OtherColor(int red, int green, int blue) {}
        Color c = Records.create(Color.class, 500, 2032, 2034);
        OtherColor other = Records.structuralCopy(OtherColor.class, c);
        Color fromOther = Records.structuralCopy(Color.class, other);
    }
}
