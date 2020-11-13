package org.monospark.remix.internal;

import org.monospark.remix.actions.Action;
import org.monospark.remix.actions.Actions;
import org.monospark.remix.actions.Assign;
import org.monospark.remix.actions.Get;
import org.monospark.remix.defaults.Default;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class RecordParameter {

    private RecordComponent component;
    private RecordComponentType type;

    public RecordParameter(RecordComponent component, RecordComponentType type) {
        this.component = component;
        this.type = type;
    }

    public static List<RecordParameter> fromRecordComponents(Class<?> recordClass) {
        var params = Arrays.stream(recordClass.getRecordComponents())
                .map(comp -> {
                    RecordComponent component = comp;
                    RecordComponentType type = RecordComponentType.create(comp.getType());
                    return new RecordParameter(component, type);
                })
                .collect(Collectors.toList());
        return params;
    }

    public Object wrap(Object value) {
        return getType().wrap(this, value);
    }

    public Object defaultValue() {
        return getType().defaultValue(this);
    }

    public <T extends Record> Class<T> getRecord() {
        return (Class<T>) component.getDeclaringRecord();
    }

    public boolean isMutable() {
        return type.isMutable();
    }

    public RecordComponent getComponent() {
        return component;
    }

    public RecordComponentType getType() {
        return type;
    }
}
