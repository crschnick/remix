package org.monospark.remix.samples;

import org.monospark.remix.Mutable;
import org.monospark.remix.MutableInt;
import org.monospark.remix.Records;
import org.monospark.remix.Wrapped;

import java.util.ArrayList;
import java.util.List;

public class MiscSample {

    void doStuff() {
        record TripleEntry(Mutable<String> stringId, MutableInt intId, Wrapped<Object> value) {}
        Records.remix(TripleEntry.class, r -> r.assign(o -> o
                .notNull(o.all())
                .check(TripleEntry::stringId, s -> s.length() >= 5)
                .check(TripleEntry::intId, i -> i >= 0)));
        List<TripleEntry> list = new ArrayList<>();

        // Do some stuff ...
    }
}
