package org.monospark.remix.sample;

import org.monospark.remix.Wrapped;
import org.monospark.remix.defaults.*;

import static org.monospark.remix.defaults.Defaults.*;
import static org.monospark.remix.defaults.Groups.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DefaultSample {

    public static record BigRecord(@DefaultByte(1)
                                   byte byteVar,
                                   @DefaultShort(2)
                                   short shortVar,
                                   @DefaultInt(3)
                                   int intVar,
                                   @DefaultLong(4)
                                   long longVar,
                                   @DefaultFloat(5.0f)
                                   float floatVar,
                                   @DefaultDouble(6.0)
                                   double doubleVar,
                                   @DefaultChar('a')
                                   char charVar,
                                   @DefaultBoolean(false)
                                   boolean booleanVar,
                                   @DefaultString("default string")
                                   String stringVar,
                                   @DefaultClass(Object.class)
                                   Class<?> clazz,
                                   @DefaultInt(value = {1,2})
                                   int[] intArrayVar,
                                   @Default(value = Now.class)
                                   Instant[] instantVar,
                                   @Default(Now.class)
                                   LocalDateTime dateTimeVar,
                                   @Default(value = {Null.class, Null.class}, group = ArrayList.class)
                                   List<Object> listVar) {

    }
}
