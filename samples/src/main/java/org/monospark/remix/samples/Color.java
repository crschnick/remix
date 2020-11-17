package org.monospark.remix.samples;

import org.monospark.remix.*;

import java.util.function.Predicate;

@Remix(Color.Remixer.class)
public record Color(WrappedInt red, WrappedInt green, WrappedInt blue) {
    public static class Remixer implements RecordRemixer<Color> {
        @Override
        public void create(RecordRemix<Color> r) {
            r.blank(b -> b
                    .set(Color::red).to(() -> 0)
                    .set(Color::green).to(() -> 0)
                    .set(Color::blue).to(() -> 0));
            Predicate<Integer> range = v -> v >= 0 && v <= Short.MAX_VALUE;
            r.assign(o -> o.check(o.all(), range));
        }
    }
}
