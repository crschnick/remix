package org.monospark.remix;

import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;
import org.monospark.remix.sample.BuilderSample;

import java.io.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
public class SerializationTest {

    @Ignore
    public static record Car(String manufacturer, Wrapped<String> model, WrappedInt price,
                             MutableBoolean available) implements Serializable {
    }

    @Test
    public void testNormalSerialization() throws IOException, ClassNotFoundException {
        Car c = Records.builder(Car.class)
                .set(Car::manufacturer, () -> "RemixCars")
                .set(Car::model, () -> "The Budget car")
                .set(Car::price, () -> 10000)
                .set(Car::available, () -> true)
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

    @Ignore
    public static record CarSerializableRecord(String manufacturer, Wrapped<String> model, WrappedInt price,
                             MutableBoolean available) implements SerializableRecord {
        @Override
        public Object writeReplace() {
            return Records.serialized(this);
        }
    }

    @Test
    public void testRecordSerialization() throws IOException, ClassNotFoundException {
        CarSerializableRecord c = Records.builder(CarSerializableRecord.class)
                .set(CarSerializableRecord::manufacturer, () -> "RemixCars")
                .set(CarSerializableRecord::model, () -> "The Budget car")
                .set(CarSerializableRecord::price, () -> 10000)
                .set(CarSerializableRecord::available, () -> true)
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
}
