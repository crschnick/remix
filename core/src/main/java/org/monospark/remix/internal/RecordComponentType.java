package org.monospark.remix.internal;

import org.monospark.remix.Default;
import org.monospark.remix.Mutable;
import org.monospark.remix.Wrapped;
import org.monospark.remix.WrappedInt;

public abstract class RecordComponentType {

    static RecordComponentType create(Class<?> type) {
        if (type.equals(Wrapped.class)) {
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

    public abstract String getImplicitDefaultType();

    public abstract Object createDefaultValue(RecordParameter param, String defaultType);

    public abstract Object wrap(RecordParameter param, Object obj);

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
        public String getImplicitDefaultType() {
            return Default.NULL;
        }

        @Override
        public Object createDefaultValue(RecordParameter param, String defaultType) {
            return DefaultImpl.createDefaultValue(clazz);
        }

        @Override
        public Object wrap(RecordParameter param, Object obj) {
            return obj;
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
        public String getImplicitDefaultType() {
            return Default.ZERO;
        }


        @Override
        public Object createDefaultValue(RecordParameter param, String defaultType) {
            int val;
            switch (defaultType) {
                case Default.ZERO:
                    val = 0;
                    break;
                case Default.ONE:
                    val = 1;
                    break;
                case Default.MINUS_ONE:
                    val = -1;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return new WrappedInt(param, val);
        }

        @Override
        public Object wrap(RecordParameter param, Object obj) {
            return new WrappedInt(param, (int) obj);
        }

        @Override
        public Class<?> getValueType() {
            return Integer.class;
        }
    }

    public static final class WrappedObjectType extends RecordComponentType {

        @Override
        boolean isMutable() {
            return false;
        }

        @Override
        boolean isWrapped() {
            return true;
        }

        @Override
        public String getImplicitDefaultType() {
            return Default.NULL;
        }


        @Override
        public Object createDefaultValue(RecordParameter param, String defaultType) {
            return null;
        }

        @Override
        public Object wrap(RecordParameter param, Object obj) {
            return new Wrapped<>(param, obj);
        }

        @Override
        public Class<?> getValueType() {
            return Object.class;
        }
    }

    public static final class MutableType extends RecordComponentType {

        @Override
        boolean isMutable() {
            return true;
        }

        @Override
        boolean isWrapped() {
            return true;
        }

        @Override
        public String getImplicitDefaultType() {
            return Default.NULL;
        }


        @Override
        public Object createDefaultValue(RecordParameter param, String defaultType) {
            return null;
        }

        @Override
        public Object wrap(RecordParameter param, Object obj) {
            return new Mutable<>(param, obj);
        }

        @Override
        public Class<?> getValueType() {
            return Object.class;
        }
    }
}
