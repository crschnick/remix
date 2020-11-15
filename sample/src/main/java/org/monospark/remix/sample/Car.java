package org.monospark.remix.sample;

import org.monospark.remix.*;

@Remix(Car.Remixer.class)
public record Car(Wrapped<String> manufacturer, Wrapped<String> model, WrappedInt price,
                         MutableBoolean available) {
    static class Remixer implements RecordRemixer<Car> {
        @Override
        public void create(RecordRemix<Car> r) {
            r.blank(b -> b.set(Car::manufacturer, () -> "RemixCars"));
            r.assign(o -> {
                o.notNull(Car::manufacturer)
                        .notNull(Car::model)
                        .check(Car::price, p -> p > 0);
            });
        }
    }
}
