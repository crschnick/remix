package org.monospark.remix;

import org.junit.Ignore;
import org.junit.Test;
import org.monospark.remix.samples.Car;
import org.monospark.remix.samples.CarStorage;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CarSampleTest {

    @Ignore
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
        CarStorage store = Records.create(CarStorage.class, cars);

        cars.clear();
        assertThat(store.cars().get().size(), equalTo(2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCarStorageUnmodifiable() {
        var cars = createCars();
        CarStorage store = Records.create(CarStorage.class, cars);

        List<Car> databaseContent = Records.get(store::cars);
        databaseContent.clear();
    }
}
