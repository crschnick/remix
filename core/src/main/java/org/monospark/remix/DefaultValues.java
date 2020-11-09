package org.monospark.remix;

public class DefaultValues {

    private static final class NowFactory implements Default.Factory {

        @Override
        public Object create(Class<?> clazz) {
            return null;
        }
    }
    public static final Class<? extends Default.Factory> NOW = NowFactory.class;

    private static final class NullFactory implements Default.Factory {

        @Override
        public Object create(Class<?> clazz) {
            return null;
        }
    }
    public static final Class<? extends Default.Factory> NULL = NullFactory.class;

}
