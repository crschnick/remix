package org.monospark.remix.sample;

import org.monospark.remix.*;

import java.util.function.Predicate;

@Remix(Color.Remix.class)
public record Color(WrappedInt red, WrappedInt green, WrappedInt blue) {
    public static class Remix extends RecordRemix<Color> {
        @Override
        public void blank(RecordBuilder<Color> builder) {
            builder.set(Color::red, () -> 0);
            builder.set(Color::green, () -> 0);
            builder.set(Color::blue, () -> 0);
        }

        @Override
        public void assign(RecordOperations<Color> ops) {
            Predicate<Integer> range = v -> v >= 0 && v <= Short.MAX_VALUE;
            ops.check(Color::red, range);
            ops.check(Color::green, range);
            ops.check(Color::blue, range);
        }
    }
}
