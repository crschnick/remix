package org.monospark.remix;

import org.junit.jupiter.api.Test;
import org.monospark.remix.samples.BibliographyStore;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BibliographySampleTest {

    @Test
    public void testNull() {
        assertThrows(NullPointerException.class, () -> {
            var entry = Records.create(BibliographyStore.Entry.class,
                    List.of("James Gosling"),
                    "The Java Programming Language",
                    null);
        });
    }

    @Test
    public void testCopy() {
        var entry = Records.create(BibliographyStore.Entry.class,
                List.of("James Gosling"),
                "The Java Programming Language",
                UUID.randomUUID());
        var store = Records.create(BibliographyStore.class, List.of(entry));

        var storeCopy = Records.copy(store);
        var entryInStoreCopy = Records.get(storeCopy::entries).get(0);

        // This should not change the entry title in the original store because we are working on a copy!
        Records.set(entryInStoreCopy::authors, List.of("Ken Arnold", "James Gosling"));

        assertEquals(store.entries().get().get(0).authors().get().size(), 1);
        assertEquals(storeCopy.entries().get().get(0).authors().get().size(), 2);
    }
}
