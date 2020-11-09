package org.monospark.remix;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SetAction {

    public static final String DEFENSIVE_COPY = "defensive-copy";
    public static final String NOT_NULL = "not-null";
    public static final String NOT_EMPTY_STRING = "not-empty-string";
    public static final String GREATER_ZERO = "check-not-null";
    public static final String GREATER_OR_EQUAL_ZERO = "check-not-null";
    public static final String SIZE_GREATER_ZERO = "not-empty-string";
    public static final String UNMODIFIABLE_LIST = "not-empty-string";


    public String[] value();
}
