package org.monospark.remix;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SerializationTest {

    @Test
    public void testNormalSerialization() throws IOException, ClassNotFoundException {
        Car c = Records.builder(Car.class)
                .set(Car::manufacturer).to(() -> "RemixCars")
                .set(Car::model).to(() -> "The Budget car")
                .set(Car::price).to(() -> 10000)
                .set(Car::available).to(() -> true)
                .build();

        var out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(c);
        oos.flush();
        byte[] b = out.toByteArray();

        ByteArrayInputStream in = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(in);
        Car s = (Car) ois.readObject();

        assertThat(s, is(equalTo(c)));
    }

    @Test
    public void testRecordSerialization() throws IOException, ClassNotFoundException {
        CarSerializableRecord c = Records.builder(CarSerializableRecord.class)
                .set(CarSerializableRecord::manufacturer).to(() -> "RemixCars")
                .set(CarSerializableRecord::model).to(() -> "The Budget car")
                .set(CarSerializableRecord::price).to(() -> 10000)
                .set(CarSerializableRecord::available).to(() -> true)
                .build();

        var out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(c);
        oos.flush();
        byte[] b = out.toByteArray();

        ByteArrayInputStream in = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(in);
        CarSerializableRecord s = (CarSerializableRecord) ois.readObject();

        assertThat(s, is(equalTo(c)));
    }

    @Ignore
    public static record Car(String manufacturer, Wrapped<String> model, WrappedInt price,
                             MutableBoolean available) implements Serializable {
    }

    @Ignore
    public static record CarSerializableRecord(String manufacturer, Wrapped<String> model, WrappedInt price,
                                               MutableBoolean available) implements SerializableRecord {
        @Override
        public Object writeReplace() {
            return Records.serialized(this);
        }
    }
}
