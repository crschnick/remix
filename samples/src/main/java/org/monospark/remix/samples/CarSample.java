package org.monospark.remix.samples;

import org.monospark.remix.RecordBlank;
import org.monospark.remix.Records;

import java.util.ArrayList;
import java.util.List;

public class CarSample {

    public static void main(String[] args) {
        Car c1 = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .set(Car::model).to(() -> "The Budget car")
                .set(Car::price).to(() -> 10000)
                .set(Car::available).to(() -> true)
                .build();

        RecordBlank<Car> carBlank = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .blank();

        Car c2 = Records.builder(carBlank)
                .set(Car::model).to(() -> "The luxurious car")
                .set(Car::price).to(() -> 60000)
                .set(Car::available).to(() -> true)
                .build();


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
    }
}
