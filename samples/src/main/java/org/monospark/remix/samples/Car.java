package org.monospark.remix.samples;

import org.monospark.remix.*;

@Remix
public record Car(Wrapped<String> manufacturer, Wrapped<String> model, WrappedInt price,
                         MutableBoolean available) {
    private static void createRemix(RecordRemix<Car> r) {
        r.blank(b -> b.set(Car::manufacturer).to(() -> "RemixCars"));
        r.assign(o -> o
                .notNull(o.all())
                .check(Car::price, p -> p > 0)
        );
    }
}
