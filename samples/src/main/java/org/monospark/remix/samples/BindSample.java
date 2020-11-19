package org.monospark.remix.samples;

import org.monospark.remix.Records;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BindSample {

    public static void main(String[] args) {
        List<Car> cars = new ArrayList<>();
        Car c1 = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .set(Car::model).to(() -> "The Budget car")
                .set(Car::price).to(() -> 10000)
                .set(Car::available).to(() -> true)
                .build();
        cars.add(c1);

        Function<Car,String> nameFunc = Records.bind(Car::manufacturer).and(Car::model)
                .toFunction((s1, s2) -> String.join(" ", s1, s2));

        Map<Car,String> names = cars.stream().collect(Collectors.toMap(c -> c, nameFunc));
    }
}
