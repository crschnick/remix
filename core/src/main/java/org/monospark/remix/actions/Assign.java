package org.monospark.remix.actions;

import org.monospark.remix.Mutable;
import org.monospark.remix.Records;
import org.monospark.remix.Wrapped;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that one or more actions should be performed when assigning a value to the associated record component.
 * Assignment is defined as the initial value assignment in the constructor and the assignment using
 * {@link Records#set(Mutable, Object)} if the record component is made mutable by using {@link Mutable}.
 *
 * Requires the record component to be wrapped using either {@link Wrapped}, {@link Mutable}
 * or one of its primitive equivalents.
 *
 * If the actions performed for the constructor assignment and setter assignment should be different,
 * explicitly specifiy the {@link #set()} property, which overrides {@link #value()} for setter assignments if not left empty.
 **/
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Assign {

    /**
     * The actions that should be performed in order of definition.
     */
    Class<? extends AssignAction>[] value();

    /**
     * The actions that should be performed for setter assignment in order of definition.
     */
    Class<? extends AssignAction>[] set() default {};
}
