package org.monospark.remix.actions;

import org.monospark.remix.Wrapped;
import org.monospark.remix.Records;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that one or more actions should be performed when calling the accessor of
 * the associated record component.
 *
 * Requires the record component to be wrapped using either {@link Wrapped} or one of its primitive equivalents.
 **/
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {

    /**
     * The actions that should be performed in order of definition.
     */
    Class<? extends GetAction>[] value();
}
