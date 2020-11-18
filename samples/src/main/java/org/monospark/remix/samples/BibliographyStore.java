package org.monospark.remix.samples;

import org.monospark.remix.*;

import java.util.*;
import java.util.stream.Collectors;

@Remix(BibliographyStoreRemixer.class)
public record BibliographyStore(Wrapped<List<Entry>> entries) {

    @Remix
    public record Entry(Mutable<List<String>> authors,
                  Mutable<String> title,
                  Wrapped<UUID> id) {
        private static void createRemix(RecordRemix<Entry> r) {
            r.get(o -> o.add(BibliographyStore.Entry::authors, Collections::unmodifiableList));
            r.assign(o -> o
                    .notNull(o.all())
                    .check(BibliographyStore.Entry::authors, c -> c.stream().noneMatch(Objects::isNull))
                    .add(BibliographyStore.Entry::authors, ArrayList::new)
            );
        }
    }
}