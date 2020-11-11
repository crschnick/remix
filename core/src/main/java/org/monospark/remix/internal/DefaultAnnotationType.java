package org.monospark.remix.internal;

import org.monospark.remix.defaults.Default;
import org.monospark.remix.defaults.DefaultBoolean;
import org.monospark.remix.defaults.DefaultInt;
import org.monospark.remix.defaults.Defaults;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class DefaultAnnotationType {

    private Annotation annotation;

    public DefaultAnnotationType(Annotation annotation) {
        this.annotation = annotation;
    }

    public Object defaultObject(Class<?> clazz) {
        return Defaults.getFactory(((Default) annotation).value()).create(clazz);
    }

    public int defaultInt() {
        return ((DefaultInt) annotation).value();
    }

    public boolean defaultBoolean() {
        return ((DefaultBoolean) annotation).value();
    }
}
