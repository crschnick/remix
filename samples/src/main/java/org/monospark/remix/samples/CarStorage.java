package org.monospark.remix.samples;

import org.monospark.remix.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Remix(CarStorage.Remixer.class)
public record CarStorage(Wrapped<List<Car>> cars) {

    static class Remixer implements RecordRemixer<CarStorage> {
        @Override
        public void create(RecordRemix<CarStorage> r) {
            // The default value for the car list should be an empty array list
            r.blank(b -> {
                b.set(CarStorage::cars).to(ArrayList::new);
            });

            // Return an unmodifiable list view to prevent tampering with the database from outside this instance
            r.get(o -> o.add(CarStorage::cars, Collections::unmodifiableList));

            // Check for null and make a defensive copy of the list when constructing an instance.
            r.assign(o -> o
                    .notNull(CarStorage::cars)
                    .check(CarStorage::cars, c -> !c.contains(null))
                    .add(CarStorage::cars, ArrayList::new)
            );
        }
    }

    public void addCar(Car car) {
        cars.get().add(Objects.requireNonNull(car));
    }
}
