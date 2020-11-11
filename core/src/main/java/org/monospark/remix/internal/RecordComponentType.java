package org.monospark.remix.internal;

import org.monospark.remix.Mutable;
import org.monospark.remix.WrappedInt;

public abstract class RecordComponentType<T> {

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

    public Object wrapDefault(RecordParameter param, DefaultAnnotationType defaultValue) {
        return wrap(param, defaultValue, (T) DefaultValueHelper.createDefaultValue(param.getComponent().getType()));
    }

    public abstract Object wrap(RecordParameter param, DefaultAnnotationType defaultValue, T value);

    public abstract Class<? extends T> getValueType();

    public static final class BareType extends RecordComponentType<Object> {

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
        public Object wrap(RecordParameter param, DefaultAnnotationType defaultValue, Object value) {
            return defaultValue != null ? defaultValue.defaultObject(param.getComponent().getType()) : null;
        }

        @Override
        public Class<?> getValueType() {
            return clazz;
        }
    }

    public static final class WrappedIntType extends RecordComponentType<Integer> {

        @Override
        boolean isMutable() {
            return false;
        }

        @Override
        boolean isWrapped() {
            return true;
        }

        @Override
        public Object wrap(RecordParameter param, DefaultAnnotationType defaultValue, Integer value) {
            return new WrappedIntImpl(param, value == 0 ? defaultValue.defaultInt() : 0);
        }

        @Override
        public Class<Integer> getValueType() {
            return Integer.class;
        }
    }

    public static class WrappedObjectType extends RecordComponentType<Object> {

        @Override
        boolean isMutable() {
            return false;
        }

        @Override
        boolean isWrapped() {
            return true;
        }

        @Override
        public Object wrap(RecordParameter param, DefaultAnnotationType defaultValue, Object value) {
            return new WrappedImpl<>(param, value == null ? defaultValue.defaultObject(param.getComponent().getType()) : value);
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
