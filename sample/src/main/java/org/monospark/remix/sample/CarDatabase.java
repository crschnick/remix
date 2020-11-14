package org.monospark.remix.sample;

import org.monospark.remix.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Remix(CarDatabase.Remix.class)
public record CarDatabase(Wrapped<List<Car>> cars) implements Serializable {

    static class Remix extends RecordRemix<CarDatabase> {
        @Override
        public void blank(RecordBuilder<CarDatabase> builder) {
            // The default value for the car list should be an empty array list
            builder.set(CarDatabase::cars, () -> new ArrayList<>());
        }

        @Override
        public void get(RecordOperations<CarDatabase> ops) {
            // Return an unmodifiable list view to prevent tampering with the database from outside this instance
            ops.add(CarDatabase::cars, Collections::unmodifiableList);
        }

        @Override
        public void assign(RecordOperations<CarDatabase> ops) {
            // Check for null and make a defensive copy of the list when constructing an instance.
            ops.notNull(CarDatabase::cars)
                    .check(CarDatabase::cars, c -> !c.contains(null))
                    .add(CarDatabase::cars, c -> {
                        return new ArrayList<>(c);
                    });
        }
    }
}
