package org.monospark.remix.internal;

import org.monospark.remix.Action;
import org.monospark.remix.Default;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecordParameter {

    private RecordComponent component;
    private Optional<String> defaultValueType;
    private List<String> actions;
    private RecordComponentType type;

    public RecordParameter(RecordComponent component, Optional<String> defaultValueType,
                           List<String> actions, RecordComponentType type) {
        this.component = component;
        this.defaultValueType = defaultValueType;
        this.actions = actions;
        this.type = type;
    }

    static List<String> getActions(AnnotatedElement p) {
        return Optional.ofNullable(p.getAnnotation(Action.class))
                .map(a -> Arrays.asList(a.value()))
                .orElse(List.of());
    }

    static Optional<String> getDefaultValueType(AnnotatedElement p) {
        return Arrays.stream(p.getAnnotations())
                .filter(a -> a.annotationType().equals(Default.class))
                .map(a -> ((Default) a).value())
                .findFirst();
    }

    private static void validate(RecordParameter param) {
        if (!param.getType().isWrapped()) {
            if (param.isMutable()) {
                throw new IllegalArgumentException();
            }
            if (param.getActions().size() > 0) {
                throw new IllegalArgumentException();
            }
        }
    }

    public static List<RecordParameter> fromRecordComponents(Class<?> recordClass) {
        var params = Arrays.stream(recordClass.getRecordComponents())
                .map(comp -> {
                    RecordComponent component = comp;
                    Optional<String> defaultValueType = getDefaultValueType(comp);
                    List<String> actions = getActions(comp);
                    RecordComponentType type = RecordComponentType.create(comp.getType());
                    return new RecordParameter(component, defaultValueType, actions, type);
                })
                .collect(Collectors.toList());
        params.forEach(RecordParameter::validate);
        return params;
    }

    public <T> T applyActions(T value) {
        return ActionImpl.executeActions(value, actions);
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

    public Optional<String> getDefaultValueType() {
        return defaultValueType;
    }

    public List<String> getActions() {
        return actions;
    }

    public RecordComponentType getType() {
        return type;
    }
}
