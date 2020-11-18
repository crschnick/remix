package org.monospark.remix.samples;

import org.monospark.remix.*;

import java.util.function.Predicate;

@Remix
public record Color(WrappedInt red, WrappedInt green, WrappedInt blue) {
    private static void createRemix(RecordRemix<Color> r) {
        r.blank(b -> b
                .set(Color::red).to(() -> 0)
                .set(Color::green).to(() -> 0)
                .set(Color::blue).to(() -> 0));
        Predicate<Integer> range = v -> v >= 0 && v <= Short.MAX_VALUE;
        r.assign(o -> o.check(o.all(), range));
    }
}
