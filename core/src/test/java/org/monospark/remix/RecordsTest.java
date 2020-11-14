package org.monospark.remix;

import org.junit.Ignore;
import org.junit.Test;
import org.monospark.remix.sample.BuilderSample;
import org.monospark.remix.sample.Car;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

public class RecordsTest {

    @Ignore
    public static record OtherCar(String manufacturer, Wrapped<String> model, int price,
                             boolean available) {
    }

    public Car createCar() {
        Car c = Records.builder(Car.class)
                .set(Car::manufacturer, () -> "RemixCars")
                .set(Car::model, () -> "The Budget car")
                .set(Car::price, () -> 10000)
                .set(Car::available, () -> true)
                .build();
        return c;
    }
    
    @Test
    public void testGet() {
        Car c1 = Records.builder(Car.class)
                .set(Car::manufacturer, () -> "RemixCars")
                .set(Car::model, () -> "The Budget car")
                .set(Car::price, () -> 10000)
                .set(Car::available, () -> true)
                .build();

        assertThat(Records.get(Car::manufacturer, c1), equalTo( "RemixCars"));
    }

    @Test
    public void testCopy() {
        assertThat(createCar(), equalTo( Records.copy(createCar())));
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
}
