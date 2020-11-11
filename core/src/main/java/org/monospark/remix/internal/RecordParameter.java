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
import java.util.stream.Collectors;

public class RecordParameter {

    private RecordComponent component;
    private RecordComponentType<?> type;
    private DefaultAnnotationType defaultValue;
    private List<Action> constructActions;
    private List<Action> setActions;
    private List<Action> getActions;

    public RecordParameter(RecordComponent component, RecordComponentType type, DefaultAnnotationType defaultValue, List<Action> constructActions, List<Action> setActions, List<Action> getActions) {
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

    static DefaultAnnotationType getDefaultValueType(AnnotatedElement p) {
        return Optional.ofNullable(p.getAnnotation(Default.class))
                .map(DefaultAnnotationType::new)
                .orElse(null);
    }

    private static void validate(RecordParameter param) {
        if (!param.getType().isWrapped()) {
            if (param.isMutable()) {
                throw new IllegalArgumentException();
            }
            if (param.getConstructActions().size() > 0 || param.getSetActions().size() > 0 ||param.getGetActions().size() > 0) {
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
                    List<Action> constructActions = mapConstructActions(comp);
                    List<Action> setActions = mapSetActions(comp);
                    List<Action> getActions = mapGetActions(comp);
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
        return Actions.executeActions(value, getActions);
    }

    public <T> T applySetActions(T value) {
        return Actions.executeActions(value, setActions);
    }

    public <T> T applyConstructActions(T value) {
        return Actions.executeActions(value, constructActions);
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

    public List<Action> getConstructActions() {
        return constructActions;
    }

    public List<Action> getSetActions() {
        return setActions;
    }

    public List<Action> getGetActions() {
        return getActions;
    }
}
