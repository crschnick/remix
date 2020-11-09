package org.monospark.remix;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {

    public Class<? extends Default.Factory> value();

    @FunctionalInterface
    public static interface Factory {
        Object create(Class<?> clazz);
    }
}
