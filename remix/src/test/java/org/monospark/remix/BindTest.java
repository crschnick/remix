package org.monospark.remix;

import org.junit.jupiter.api.Test;
import org.monospark.remix.samples.Car;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BindTest {

    @Test
    public void testBind() {
        Car c1 = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .set(Car::model).to(() -> "The Budget car")
                .set(Car::price).to(() -> 10000)
                .set(Car::available).to(() -> true)
                .build();

        var consumer = Records.bind(Car::manufacturer).and(Car::model).to((man, mod) -> {
            assertEquals(man, c1.manufacturer().get());
            assertEquals(mod, c1.model().get());
        });
        consumer.accept(c1);

        var consumer2 = Records.bind(Car::price).and(Car::available).and(Car::manufacturer).to((pr, av, man) -> {
            assertEquals(man, c1.manufacturer().get());
            assertEquals(pr, c1.price().getInt());
            assertEquals(av, c1.available().getBoolean());
        });
        consumer2.accept(c1);
    }
}
