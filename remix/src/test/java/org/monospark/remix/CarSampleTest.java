package org.monospark.remix;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monospark.remix.samples.Car;
import org.monospark.remix.samples.CarStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CarSampleTest {

    @Disabled
    private List<Car> createCars() {
        Car c1 = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .set(Car::model).to(() -> "The Budget car")
                .set(Car::price).to(() -> 10000)
                .set(Car::available).to(() -> true)
                .build();

        Car c2 = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .set(Car::model).to(() -> "The luxurious car")
                .set(Car::price).to(() -> 60000)
                .set(Car::available).to(() -> true)
                .build();

        List<Car> cars = new ArrayList<>();
        cars.add(c1);
        cars.add(c2);
        return cars;
    }

    @Test
    public void testCarStorage() {
        var cars = createCars();
        CarStorage store = Records.create(CarStorage.class, cars, 100);

        cars.clear();
        assertEquals(store.cars().get().size(), 2);
    }

    @Test
    public void testCarStorageUnmodifiable() {
        var cars = createCars();
        CarStorage store = Records.create(CarStorage.class, cars, 100);

        List<Car> databaseContent = Records.get(store::cars);
        assertThrows(UnsupportedOperationException.class, () -> {
            databaseContent.clear();
        });
    }
}
