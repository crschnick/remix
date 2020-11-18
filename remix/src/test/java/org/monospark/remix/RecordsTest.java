package org.monospark.remix;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monospark.remix.samples.Car;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordsTest {

    @Disabled
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

        assertEquals(Records.get(c1::manufacturer), "RemixCars");
    }

    @Test
    public void testCopy() {
        assertEquals(createCar(), Records.copy(createCar()));
    }

    @Test
    public void testStructuralCopy() {
        var car = createCar();
        var otherCar = Records.structuralCopy(OtherCar.class, createCar());
        assertEquals(Records.get(car::manufacturer), otherCar.manufacturer);
        assertEquals(car.model(), otherCar.model);
        assertEquals(Records.get(car::price), otherCar.price);
        assertEquals(Records.get(car::available), otherCar.available);
    }

    @Disabled
    public static record OtherCar(String manufacturer, Wrapped<String> model, int price,
                                  boolean available) {
    }
}
