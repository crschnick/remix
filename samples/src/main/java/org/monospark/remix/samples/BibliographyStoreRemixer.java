package org.monospark.remix.samples;

import org.monospark.remix.RecordRemix;
import org.monospark.remix.RecordRemixer;
import org.monospark.remix.Records;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class BibliographyStoreRemixer implements RecordRemixer<BibliographyStore> {
    @Override
    public void create(RecordRemix<BibliographyStore> r) {
        // The default value should be an empty array list
        r.blank(b -> {
            b.set(BibliographyStore::entries).to(ArrayList::new);
        });

        // Return an unmodifiable list view to prevent tampering from outside
        r.get(o -> o.add(BibliographyStore::entries, Collections::unmodifiableList));

        // Check for null and make a defensive copy of the list when constructing an instance.
        r.assign(o -> o
                .notNull(BibliographyStore::entries)
                .check(BibliographyStore::entries, c -> c.stream().noneMatch(Objects::isNull))
                .add(BibliographyStore::entries, ArrayList::new)
        );

        // Perform a deep copy. Otherwise, operations working on the copied bibliography entries
        // will change the entries of this one as well!
        r.copy(o -> o
                .add(BibliographyStore::entries, e -> e.stream()
                        .map(Records::copy)
                        .collect(Collectors.toCollection(ArrayList::new))));
    }
}
