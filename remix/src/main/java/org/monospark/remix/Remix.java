package org.monospark.remix;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Remix {

    /**
     * The {@link RecordRemixer} class that should be associated to the record class.
     * <p>
     * This annotation only works for record classes.
     **/
    Class<? extends RecordRemixer<? extends Record>> value() default None.class;

    final class None implements RecordRemixer<Record> {
        @Override
        public void create(RecordRemix<Record> r) {
            throw new UnsupportedOperationException();
        }
    }


}
