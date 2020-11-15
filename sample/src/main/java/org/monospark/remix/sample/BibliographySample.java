package org.monospark.remix.sample;

import org.monospark.remix.*;

import java.util.*;
import java.util.stream.Collectors;

public class BibliographySample {

    public static void main(String[] args) {
        var entry = Records.create(BibliographyStore.Entry.class,
                List.of("James Gosling"),
                "The Java Programming Language",
                UUID.randomUUID());
        var store = Records.create(BibliographyStore.class, List.of(entry));

        var storeCopy = Records.copy(store);
        var entryInStoreCopy = Records.get(storeCopy::entries).get(0);

        // This should not change the entry title in the original store because we are working on a copy!
        Records.set(entryInStoreCopy::authors, List.of("Ken Arnold", "James Gosling"));
    }
}
