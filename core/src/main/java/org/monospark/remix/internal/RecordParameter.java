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
    private DefaultAnnotationType defaultValue;
    private UnaryOperator<Object> constructActions;
    private UnaryOperator<Object> setActions;
    private UnaryOperator<Object> getActions;

    public RecordParameter(RecordComponent component, RecordComponentType type, DefaultAnnotationType defaultValue,
                           UnaryOperator<Object> constructActions, UnaryOperator<Object> setActions, UnaryOperator<Object> getActions) {
        this.component = component;
        this.type = type;
        this.defaultValue = defaultValue;
        this.constructActions = constructActions;
        this.setActions = setActions;
        this.getActions = getActions;
    }

    private static <T> List<Action> mapConstructActions(AnnotatedElement p) {
        return Optional.ofNullable(p.getAnnotation(Assign.class))
                .map(a -> Arrays.stream(a.value())
                        .map(Actions::getActionInstance)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    private static List<Action> mapSetActions(AnnotatedElement p) {
        return Optional.ofNullable(p.getAnnotation(Assign.class))
                .map(a -> a.set().length > 0 ? a.set() : a.value())
                .map(a -> Arrays.stream(a)
                        .map(Actions::getActionInstance)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    private static List<Action> mapGetActions(AnnotatedElement p) {
        return Optional.ofNullable(p.getAnnotation(Get.class))
                .map(a -> Arrays.stream(a.value())
                        .map(Actions::getActionInstance)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    static DefaultAnnotationType getDefaultValueType(RecordComponent c) {
        return DefaultAnnotationType.fromRecordComponent(c);
    }

    private void validate() {
        if (!getType().isWrapped()) {
            if (isMutable()) {
                throw new IllegalArgumentException();
            }
            if (constructActions != null || setActions != null || getActions != null) {
                throw new IllegalArgumentException();
            }
        }
    }

    public static List<RecordParameter> fromRecordComponents(Class<?> recordClass) {
        var params = Arrays.stream(recordClass.getRecordComponents())
                .map(comp -> {
                    RecordComponent component = comp;
                    RecordComponentType type = RecordComponentType.create(comp.getType());
                    DefaultAnnotationType defaultValue = getDefaultValueType(comp);
                    UnaryOperator<Object> constructActions = mapConstructActions(comp);
                    UnaryOperator<Object> setActions = mapSetActions(comp);
                    UnaryOperator<Object> getActions = mapGetActions(comp);
                    return new RecordParameter(component, type, defaultValue, constructActions, setActions, getActions);
                })
                .collect(Collectors.toList());
        params.forEach(RecordParameter::validate);
        return params;
    }

    static <T> T get(Object wrapper) {
        return (T) ((Wrapper) wrapper).getRecordParameter();
    }

    public <T> T applyGetActions(T value) {
        return (T) getActions.apply(value);
    }

    public <T> T applySetActions(T value) {
        return (T) setActions.apply(value);
    }

    public <T> T applyConstructActions(T value) {
        return (T) constructActions.apply(value);
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

    public DefaultAnnotationType getDefaultValue() {
        return defaultValue;
    }
}
