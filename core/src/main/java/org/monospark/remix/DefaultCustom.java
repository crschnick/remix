package org.monospark.remix;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultCustom {

    public Class<? extends Factory> value();

    public static interface Factory {
        Object create();
    }
}