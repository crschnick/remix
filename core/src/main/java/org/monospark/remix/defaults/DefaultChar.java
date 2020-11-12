package org.monospark.remix.defaults;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation signals that character values should be used as default values for the annotated record component.
 *
 * @see Default
 **/
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultChar {

    /**
     * The characters that should be used as default values.
     **/
    char[] value();

    /**
     * The group factory that bundles multiple characters into a group if needed.
     * This property is used if multiple values are specified and defaults to a normal array creation.
     **/
    Class<?> group() default Groups.Array.class;
}