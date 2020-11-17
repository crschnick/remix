package org.monospark.remix;

import org.junit.Ignore;
import org.junit.Test;
import org.monospark.remix.samples.Car;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RecordsTest {

    public Car createCar() {
        Car c = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .set(Car::model).to(() -> "The Budget car")
                .set(Car::price).to(() -> 10000)
                .set(Car::available).to(() -> true)
                .build();
        return c;
    }

    @Test
    public void testGet() {
        Car c1 = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .set(Car::model).to(() -> "The Budget car")
                .set(Car::price).to(() -> 10000)
                .set(Car::available).to(() -> true)
                .build();

        assertThat(Records.get(c1::manufacturer), equalTo("RemixCars"));
    }

    @Test
    public void testCopy() {
        assertThat(createCar(), equalTo(Records.copy(createCar())));
    }

    @Test
    public void testStructuralCopy() {
        var car = createCar();
        var otherCar = Records.structuralCopy(OtherCar.class, createCar());
        assertThat(Records.get(car::manufacturer), equalTo(otherCar.manufacturer));
        assertThat(car.model(), equalTo(otherCar.model));
        assertThat(Records.get(car::price), equalTo(otherCar.price));
        assertThat(Records.get(car::available), equalTo(otherCar.available));
    }

    @Ignore
    public static record OtherCar(String manufacturer, Wrapped<String> model, int price,
                                  boolean available) {
    }
}
