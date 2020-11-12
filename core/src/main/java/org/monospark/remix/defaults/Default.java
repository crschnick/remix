package org.monospark.remix.defaults;

import org.monospark.remix.RecordBuilder;
import org.monospark.remix.Records;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation signals that some value should be used as a default value for the annotated record component
 * when creating an instance using either a {@link RecordBuilder} or {@link Records#create(Class, Object...)}.
 *
 * Default annotations also support creating groups of multiple default elements such as arrays or collections of values.
 * If multiple default values are given, it is required to define the {@link #group()} property,
 * which defines the group factory that should be used for group creation.
 *
 * Default value types and group types are passed to this annotation using class literals.
 * For example
 * <blockquote>
 *     {@code @Default(Defaults.Null.class)}
 * </blockquote>
 * specifies {@code null} as a default element.
 *
 * Furthermore
 * <blockquote>
 *     {@code @Default({Defaults.Null.class, Defaults.Null.class}, group = ArrayList.class)}
 * </blockquote>
 * specifies an {@code ArrayList} with two null elements as a default value.
 *
 * There also exist several similar default annotations for primitives, strings and classes.
 * To create custom default value factories, create a subclass of {@link Defaults.Factory}.
 *
 * @see Defaults
 * @see Groups
 **/
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {

    /**
     * The default value factories that should be used to generate the one or more default values.
     **/
    Class<? extends Defaults.Factory>[] value();

    /**
     * The group factory that bundles multiple default values into a group if needed.
     * This property is used if multiple values are specified and defaults to a normal array creation.
     **/
    Class<?> group() default Groups.Array.class;

}
