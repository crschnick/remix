package org.monospark.remix.speedtest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class Sample {

    @Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestI {}

    static record Test(@TestI int a) {
    }

    public static void main(String[] args) {
        ToIntFunction<Test> f = Test::a;
        ToIntFunction<Test> g = Test::a;
        System.out.println(f);
        System.out.println(g);
        System.out.println(Test.class.getRecordComponents()[0].getAnnotations()[0] == Test.class.getDeclaredConstructors()[0].getParameters()[0].getAnnotations()[0]);
        System.out.println(Test.class.getDeclaredConstructors()[0].getParameters()[0].getAnnotations()[0].hashCode());
    }
}
