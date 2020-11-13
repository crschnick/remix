package org.monospark.remix.internal;

import org.monospark.remix.Mutable;
import org.monospark.remix.WrappedInt;

public abstract class RecordComponentType {

    static RecordComponentType create(Class<?> type) {
        if (type.equals(WrappedImpl.class)) {
            return new WrappedObjectType();
        } else if (type.equals(WrappedInt.class)) {
            return new WrappedIntType();
        } else if (type.equals(Mutable.class)) {
            return new MutableType();
        }
        return new BareType(type);
    }


    abstract boolean isMutable();

    abstract boolean isWrapped();

    public abstract Object wrap(RecordParameter param, Object value);

    public abstract Object defaultValue(RecordParameter param, DefaultAnnotationType defaultValue);

    public abstract Class<?> getValueType();

    public static final class BareType extends RecordComponentType {

        private Class<?> clazz;

        public BareType(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        boolean isMutable() {
            return false;
        }

        @Override
        boolean isWrapped() {
            return false;
        }

        @Override
        public Object wrap(RecordParameter param, Object value) {
            return value;
        }

        @Override
        public Object defaultValue(RecordParameter param, DefaultAnnotationType defaultValue) {
            return defaultValue.defaultObject(param.getComponent().getType());
        }

        @Override
        public Class<?> getValueType() {
            return clazz;
        }
    }

    public static final class WrappedIntType extends RecordComponentType {

        @Override
        boolean isMutable() {
            return false;
        }

        @Override
        boolean isWrapped() {
            return true;
        }

        @Override
        public Object wrap(RecordParameter param, Object value) {
            return new WrappedIntImpl(param, (int) value);
        }

        @Override
        public Object defaultValue(RecordParameter param, DefaultAnnotationType defaultValue) {
            return defaultValue.defaultInt();
        }

        @Override
        public Class<Integer> getValueType() {
            return Integer.class;
        }
    }

    public static class WrappedObjectType extends RecordComponentType {

        @Override
        boolean isMutable() {
            return false;
        }

        @Override
        boolean isWrapped() {
            return true;
        }

        @Override
        public Object wrap(RecordParameter param, Object value) {
            return new WrappedImpl<>(param, value);
        }

        @Override
        public Object defaultValue(RecordParameter param, DefaultAnnotationType defaultValue) {
            return defaultValue.defaultObject(param.getComponent().getType());
        }

        @Override
        public Class<?> getValueType() {
            return Object.class;
        }
    }

    public static final class MutableType extends WrappedObjectType {

        @Override
        boolean isMutable() {
            return true;
        }
    }
}
