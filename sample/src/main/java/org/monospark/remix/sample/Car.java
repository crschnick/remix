package org.monospark.remix.sample;

import org.monospark.remix.*;

@Remix(Car.Remix.class)
public record Car(Wrapped<String> manufacturer, Wrapped<String> model, WrappedInt price,
                         MutableBoolean available) {
    public static class Remix extends RecordRemix<Car> {
        @Override
        public void blank(RecordBuilder<Car> builder) {
            builder.set(Car::manufacturer, () -> "RemixCars");
        }

        @Override
        public void assign(RecordOperations<Car> operations) {
            operations.notNull(Car::manufacturer)
                    .notNull(Car::model)
                    .check(Car::price, p -> p > 0);
        }
    }
}
