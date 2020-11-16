package org.monospark.remix.sample;

import org.monospark.remix.*;

import java.io.*;
import java.util.*;

public class BuilderSample {

    void doStuff() {
        record TripleEntry(Mutable<String> stringId, MutableInt intId, Wrapped<Object> value) {}
        Records.remix(TripleEntry.class, r -> r.assign(o -> o
                .notNull(o.all())
                .check(TripleEntry::stringId, s -> s.length() >= 5)
                .check(TripleEntry::intId, i -> i >= 0)));
        List<TripleEntry> list = new ArrayList<>();

        // Do some stuff ...
    }

    public static void main(String[] args) throws IOException {
        Car c1 = Records.builder(Car.class)
                .set(Car::manufacturer, () -> "RemixCars")
                .set(Car::model, () -> "The Budget car")
                .set(Car::price, () -> 10000)
                .set(Car::available, () -> true)
                .build();
        Car copy = Records.copy(c1);
        Records.set(copy::available, false);

        RecordBlank<Car> carBlank = Records.builder(Car.class)
                .set(Car::manufacturer, () -> "RemixCars")
                .blank();

        Car c2 = Records.builder(carBlank)
                .set(Car::model, () -> "The luxurious car")
                .set(Car::price, () -> 60000)
                .set(Car::available, () -> true)
                .build();

//        var cs = Records.blank(CarStatus.class)
//                .set(CarStatus::speed,() -> 50)
//                //.set(CarStatus::gear, 2)
//                .set(CarStatus::b1, () -> false)
//                .set(CarStatus::b2, () -> true)
//                .set(CarStatus::b3, () -> false)
//                .build();

        System.out.println(c1);
        System.out.println(c2);

        List<Car> cars = new ArrayList<>();
        cars.add(c1);
        cars.add(c2);
        CarStorage store = Records.create(CarStorage.class, cars);

        // Doesn't alter the database
        cars.clear();

        List<Car> databaseContent = Records.get(store::cars);
        // Throws an exception, since the returned view is unmodifiable
        databaseContent.clear();

        record OtherColor(int red, int green, int blue) {}
        Color c = Records.create(Color.class, 500, 2032, 2034);
        OtherColor other = Records.structuralCopy(OtherColor.class, c);
        Color fromOther = Records.structuralCopy(Color.class, other);

    }
}
