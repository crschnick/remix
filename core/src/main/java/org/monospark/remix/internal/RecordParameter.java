package org.monospark.remix.internal;

import org.monospark.remix.RemixException;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class RecordParameter implements Serializable {

    private RecordComponent component;
    private RecordComponentType type;

    public RecordParameter(RecordComponent component, RecordComponentType type) {
        this.component = component;
        this.type = type;
    }

    @Serial
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(component.getDeclaringRecord());
        out.writeInt(Arrays.asList(getRecord().getRecordComponents()).stream()
                .map(RecordComponent::getName).collect(Collectors.toList())
                .indexOf(component.getName()));
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        Class<? extends Record> c = (Class<? extends Record>) in.readObject();
        int index = in.readInt();
        this.component = c.getRecordComponents()[index];
        this.type = RecordComponentType.get(c.getRecordComponents()[index].getType());
    }

    public static List<RecordParameter> fromRecordComponents(Class<?> recordClass) {
        var params = Arrays.stream(recordClass.getRecordComponents())
                .map(comp -> {
                    RecordComponent component = comp;
                    RecordComponentType type = RecordComponentType.get(comp.getType());
                    return new RecordParameter(component, type);
                })
                .collect(Collectors.toList());
        return params;
    }

    public Object wrap(Object value) {
        return getType().wrap(this, value);
    }

    public Object getValue(Object instance) {
        try {
            return getComponent().getAccessor().invoke(instance);
        } catch (Exception e) {
            throw new RemixException(e);
        }
    }

    public Object unwrap(Object value) {
        return getType().unwrap(value);
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

    public <T> UnaryOperator<T> getGetOperation() {
        return RecordCache.getOrAdd(this.getRecord()).getGetOperations().getOperator(this);
    }
}
