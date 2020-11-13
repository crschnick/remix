package org.monospark.remix.internal;

import org.monospark.remix.actions.Assign;
import org.monospark.remix.actions.Get;
import org.monospark.remix.defaults.*;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RemixAnnotations {

    static final List<Class<? extends Annotation>> DEFAULT_ANNOTATIONS =
            List.of(Default.class, DefaultBoolean.class, DefaultClass.class, DefaultChar.class,
                    DefaultDouble.class, DefaultFloat.class, DefaultLong.class, DefaultInt.class, DefaultString.class,
                    DefaultByte.class, DefaultShort.class);

    static final List<Class<? extends Annotation>> ACTION_ANNOTATIONS =
            List.of(Assign.class, Get.class);

    static final List<Class<? extends Annotation>> ALL_ANNOTATIONS;
    static {
        ALL_ANNOTATIONS = Stream.concat(DEFAULT_ANNOTATIONS.stream(), ACTION_ANNOTATIONS.stream())
                .collect(Collectors.toList());
    }

}
