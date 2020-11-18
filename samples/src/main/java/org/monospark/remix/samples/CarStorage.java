package org.monospark.remix.samples;

import org.monospark.remix.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Remix
public record CarStorage(Wrapped<List<Car>> cars, WrappedInt capacity) {

    private static void createRemix(RecordRemix<CarStorage> r) {
        // Return an unmodifiable list view when calling the component getter
        // to prevent tampering with the storage from the outside
        r.get(o -> o.add(CarStorage::cars, Collections::unmodifiableList));

        // Check for null and make a defensive copy of the list when constructing an instance.
        r.assign(o -> o
                .check(CarStorage::capacity, c -> c > 0)
                .notNull(CarStorage::cars)
                .check(CarStorage::cars, c -> c.stream().noneMatch(Objects::isNull))
                .add(CarStorage::cars, ArrayList::new)
        );
        r.copy(o -> o.add(CarStorage::cars, e -> e.stream()
                .map(Records::copy)
                .collect(Collectors.toCollection(ArrayList::new))));
    }

    public void addCar(Car car) {
        if (car == null || Records.get(this::cars).size() == Records.get(this::capacity)) {
            return;
        }

        cars.get().add(Objects.requireNonNull(car));
    }
}
