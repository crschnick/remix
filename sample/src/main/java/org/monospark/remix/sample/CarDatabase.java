package org.monospark.remix.sample;

import org.monospark.remix.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Remix(CarDatabase.Remixer.class)
public record CarDatabase(Wrapped<List<Car>> cars) {

    static class Remixer implements RecordRemixer<CarDatabase> {
        @Override
        public void create(RecordRemix<CarDatabase> r) {
            // The default value for the car list should be an empty array list
            r.blank(b -> {
                b.set(CarDatabase::cars, () -> new ArrayList<>());
            });

            // Return an unmodifiable list view to prevent tampering with the database from outside this instance
            r.get(o -> o.add(CarDatabase::cars, Collections::unmodifiableList));

            // Check for null and make a defensive copy of the list when constructing an instance.
            r.assign(o -> o
                    .notNull(CarDatabase::cars)
                    .check(CarDatabase::cars, c -> !c.contains(null))
                    .add(CarDatabase::cars, ArrayList::new)
            );

            r.copy(o -> o.);
        }
    }
}
